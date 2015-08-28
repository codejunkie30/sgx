using System;
using System.Configuration;
using System.Net;
using System.Data.SqlClient;
using System.Data;
using System.IO;
using System.Text;
using Microsoft.VisualBasic.FileIO;
using Renci.SshNet;

namespace XFDataDump
{

    static class LOG
    {

        internal static void Debug(string msg) { LogIt(msg, "Debug"); }

        internal static void Info(string msg) { LogIt(msg, "Info"); }

        internal static void LogIt(string msg, string cat)
        {
            var log = ConfigurationManager.AppSettings["sgxLog"].ToString();
            using (StreamWriter w = File.AppendText(log))
            {
                w.WriteLine("{0} {1}", DateTime.Now.ToString("MM\\/dd\\/yyyy HH:mm"), msg);
                Console.WriteLine("{0} {1}", DateTime.Now.ToString("MM\\/dd\\/yyyy HH:mm"), msg);
            }        
        }

    }

    static class Program
    {

        /// <summary>
        /// 
        /// </summary>
        static void Main()
        {

            // tmp directory to store files
            string tmpDir = ConfigurationManager.AppSettings["tmpDir"].ToString();

            // open (and keep open connection) need for temporary table
            SqlConnection conn = new SqlConnection(ConfigurationManager.ConnectionStrings["xf.target"].ConnectionString);
            conn.Open();

            // SGX ticker file - write to DB and lookup S&P data
            DataTable tickerData = getTickerData();
            LOG.Info("Creating Ticker Table");
            using (SqlCommand command = new SqlCommand(Properties.Resources.createTickerTable, conn)) command.ExecuteNonQuery();
            writeToDB(tickerData, conn);
            LOG.Info("Finished Creating Ticker Table");
            LOG.Info("Updating Ticker Table");
            using (SqlCommand command = new SqlCommand(Properties.Resources.updateTmpTickerTable, conn)) command.ExecuteNonQuery();
            LOG.Info("Finished Updating Ticker Table");

            // export list of good companies
            LOG.Info("Saving Company List");
            tickerData = new DataTable();
            using (SqlCommand command = new SqlCommand(Properties.Resources.exportTickerTable, conn)) tickerData.Load(command.ExecuteReader());
            writeToFile(tickerData, tmpDir + ConfigurationManager.AppSettings["companiesFileName"].ToString());
            LOG.Info("Finished Saving Company List");

            // export list of bad companies
            LOG.Info("Saving Bad Company List");
            tickerData = new DataTable();
            using (SqlCommand command = new SqlCommand(Properties.Resources.exportNFCompanies, conn)) tickerData.Load(command.ExecuteReader());
            writeToFile(tickerData, tmpDir + ConfigurationManager.AppSettings["notFoundFileName"].ToString());
            LOG.Info("Finished Saving Bad Company List");

            // export list of unique currencies
            LOG.Info("Exporting Currency Info");
            tickerData = new DataTable();
            using (SqlCommand command = new SqlCommand(Properties.Resources.exportUniqueCurrency, conn)) tickerData.Load(command.ExecuteReader());
            writeToFile(tickerData, tmpDir + ConfigurationManager.AppSettings["currenciesFileName"].ToString());
            LOG.Info("Finished Exporting Currency Info");
            
            // now execute loader specific sql scripts (need a while to do so)
            executeQueries(tmpDir, conn);
            
            // close connection (removes tmp table)
            conn.Close();

            // now let's move the files over to an FTP server
            pushFiles(tmpDir);

            // archive files
            archiveFiles(tmpDir);

        }

        /**
         * push all the files in the tmp directory to remote server (FTPS)
         */
        static void pushFiles(string tmpDir)
        {

            string[] files = Directory.GetFiles(tmpDir);
            string url = ConfigurationManager.AppSettings["ftpURL"].ToString();
            string user = ConfigurationManager.AppSettings["ftpUsername"].ToString();
            string pass = ConfigurationManager.AppSettings["ftpPassword"].ToString();

            LOG.Debug("Creating SFTP Client");

            using (SftpClient scp = new SftpClient(url, user, pass))
            {
                LOG.Debug("Trying to connect to " + url);
                scp.Connect();
                LOG.Debug("Connected to " + url);
                foreach (string file in files)
                {
                    string name = Path.GetFileName(file);
                    LOG.Debug("Opening FileStream for " + file);

                    using (var fileStream = new FileStream(file, FileMode.Open))
                    {
                        LOG.Info("Uploading " + file + " " + fileStream.Length + " bytes");
                        scp.BufferSize = 4 * 1024; // bypass Payload error large files
                        scp.UploadFile(fileStream, Path.GetFileName(file));
                    }

                    LOG.Debug("Sent " + file);
                }
                scp.Disconnect();
                LOG.Debug("Disconnected SFTP Client");
            }

        }

        /**
         * archive files
         */
        static void archiveFiles(string tmpdir)
        {
            // TODO archive each run
        }

        /**
         * traverse a directory sql files an execute them, dumping a CSV file of the same name into the tmp directory
         */
        static void executeQueries(string tmpDir, SqlConnection conn)
        {

            string baseDir = ConfigurationManager.AppSettings["sqlDir"].ToString();
            string[] files = Directory.GetFiles(baseDir, "*.sql");

            foreach (string file in files)
            {
                LOG.Info("Executing script " + file);
                DataTable results = new DataTable();
                string query = File.ReadAllText(file);
                string name = Path.GetFileName(file).Replace(".sql", ".csv");
                using (SqlCommand command = new SqlCommand(query, conn))
                {
                    command.CommandTimeout = 1800;
                    results.Load(command.ExecuteReader());
                }
                LOG.Info("Finished executing script " + file);
                writeToFile(results, tmpDir + name);
                LOG.Info("Exported data to " + name);
            }

        }

        /**
         * retrieve ticker file and convert to data table
         */
        static DataTable getTickerData()
        {

            LOG.Info("Retrieving Ticker Data");

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

            LOG.Debug("Parsing Ticker Data");

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

            LOG.Debug("Finished Parsing Ticker Data");

            LOG.Info("Finished Retrieving Ticker Data");

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
