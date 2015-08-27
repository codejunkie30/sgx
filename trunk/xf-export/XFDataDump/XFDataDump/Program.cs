using System;
using System.Configuration;
using System.Net;
using System.Data.SqlClient;
using System.Data;
using System.IO;
using System.Text;
using Microsoft.VisualBasic.FileIO;


namespace XFDataDump
{
    static class Program
    {
        /// <summary>
        /// 
        /// </summary>
        static void Main()
        {

            // open (and keep open connection) need for temporary table
            SqlConnection conn = new SqlConnection(ConfigurationManager.ConnectionStrings["xf.target"].ConnectionString);
            conn.Open();

            // tmp directory to store files
            string tmpDir = ConfigurationManager.AppSettings["tmpDir"].ToString();

            // SGX ticker file - write to DB and lookup S&P data
            DataTable tickerData = getTickerData();
            using (SqlCommand command = new SqlCommand(Properties.Resources.createTickerTable, conn)) command.ExecuteNonQuery();
            writeToDB(tickerData, conn);
            using (SqlCommand command = new SqlCommand(Properties.Resources.updateTmpTickerTable, conn)) command.ExecuteNonQuery();

            // export list of good companies
            tickerData = new DataTable();
            using (SqlCommand command = new SqlCommand(Properties.Resources.exportTickerTable, conn)) tickerData.Load(command.ExecuteReader());
            writeToFile(tickerData, tmpDir + ConfigurationManager.AppSettings["companiesFileName"].ToString());

            // export list of bad companies
            tickerData = new DataTable();
            using (SqlCommand command = new SqlCommand(Properties.Resources.exportNFCompanies, conn)) tickerData.Load(command.ExecuteReader());
            writeToFile(tickerData, tmpDir + ConfigurationManager.AppSettings["notFoundFileName"].ToString());

            // export list of unique currencies
            tickerData = new DataTable();
            using (SqlCommand command = new SqlCommand(Properties.Resources.exportUniqueCurrency, conn)) tickerData.Load(command.ExecuteReader());
            writeToFile(tickerData, tmpDir + ConfigurationManager.AppSettings["currenciesFileName"].ToString());

            // now execute loader specific sql scripts (need a while to do so)
            executeQueries(tmpDir, conn);
            
            // close connection (removes tmp table)
            conn.Close();

        }

        static void executeQueries(string tmpDir, SqlConnection conn)
        {

            string baseDir = ConfigurationManager.AppSettings["sqlDir"].ToString();
            string[] files = Directory.GetFiles(baseDir, "*.sql");

            foreach (string file in files) 
            {
                DataTable results = new DataTable();
                string query = File.ReadAllText(file);
                string name = Path.GetFileName(file).Replace(".sql", ".csv");
                using (SqlCommand command = new SqlCommand(query, conn))
                {
                    command.CommandTimeout = 1800;
                    results.Load(command.ExecuteReader());
                }
                writeToFile(results, tmpDir + name);
            }

        }

        /**
         * retrieve ticker file and convert to data table
         */
        static DataTable getTickerData()
        {

            // get the ticker URL
            var tickerURL = ConfigurationManager.AppSettings["tickerURL"].ToString();

            // start by retrieving file
            WebClient client = new WebClient();
            string response = client.DownloadString(tickerURL);
            client.Dispose();

            byte[] bytes = Encoding.UTF8.GetBytes(response);
            MemoryStream stream = new MemoryStream(bytes);

            DataTable table = new DataTable();
            table.TableName = ConfigurationManager.AppSettings["companiesTableName"];
            string[] fields = new string[] { "name", "tickerSymbol", "exchangeSymbol", "isin", "short_name" };
            foreach (string field in fields) table.Columns.Add(field, typeof(string));

            using (TextFieldParser parser = new TextFieldParser(stream))
            {
                // set delimeters
                parser.TextFieldType = FieldType.Delimited;
                parser.SetDelimiters(new string[] { ",", ":" });

                // ignore first line
                parser.ReadFields();

                // loop through rest
                while (!parser.EndOfData)
                {
                    string[] values = parser.ReadFields();
                    if (values.Length < 5) continue;
                    DataRow row = table.NewRow();
                    for (int i = 0; i < fields.Length; i++) row[fields[i]] = values[i].Replace(" MAINBOARD", "");
                    table.Rows.Add(row);
                }
            }

            return table;
        }

        /**
         * write dataset to delimeted file
         */
        static void writeToFile(DataTable dataSource, string fileOutputPath, bool firstRowIsColumnHeader = false, string seperator = ",")
        {
            var sw = new StreamWriter(fileOutputPath, false);

            int icolcount = dataSource.Columns.Count;

            if (!firstRowIsColumnHeader)
            {
                for (int i = 0; i < icolcount; i++)
                {
                    sw.Write(dataSource.Columns[i]);
                    if (i < icolcount - 1)
                        sw.Write(seperator);
                }

                sw.Write(sw.NewLine);
            }

            foreach (DataRow drow in dataSource.Rows)
            {
                for (int i = 0; i < icolcount; i++)
                {
                    if (!Convert.IsDBNull(drow[i])) sw.Write(FormatValueCSV(drow[i]));
                    if (i < icolcount - 1) sw.Write(seperator);
                }
                sw.Write(sw.NewLine);
            }
            sw.Close();
        }

        /**
         * format value for CSV
         */
        public static string FormatValueCSV(object value)
        {
            if (Object.ReferenceEquals(value, null)) return "";
            Type valueType = value.GetType();
            if (valueType == typeof(DateTime)) return value.ToString();
            if (valueType.IsPrimitive && valueType != typeof(string)) return value.ToString();
            return String.Format("\"{0}\"", value.ToString().Replace("\"", "\"\""));
        }

        /**
         *  write datatable data to the database
         */
        static void writeToDB(DataTable source, SqlConnection conn)
        {
            using (var bulkCopy = new SqlBulkCopy(conn))
            {
                foreach (DataColumn col in source.Columns) bulkCopy.ColumnMappings.Add(col.ColumnName, col.ColumnName);
                bulkCopy.BulkCopyTimeout = 600;
                bulkCopy.DestinationTableName = source.TableName;
                bulkCopy.WriteToServer(source);
            }

        }
    }
}
