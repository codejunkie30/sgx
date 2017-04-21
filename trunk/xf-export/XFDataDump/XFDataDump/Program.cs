using System;
using System.Configuration;
using System.Net;
using System.Data.SqlClient;
using System.Data;
using System.IO;
using System.Text;
using Microsoft.VisualBasic.FileIO;
using Renci.SshNet;
using ICSharpCode.SharpZipLib.Zip;
//using System.Collections.Generic;
using System.Linq;


namespace XFDataDump
{

    /**
     * hack logger to reduce overhead
     */
    static class LOG
    {

        internal static void Debug(string msg) { LogIt(msg, 0); }

        internal static void Info(string msg) { LogIt(msg, 1); }

        internal static void Error(String msg, Exception e) { LogIt(msg + Environment.NewLine + e.StackTrace, 2); }

        internal static void LogIt(string msg, int cat)
        {
            int logLevel = Convert.ToInt32(ConfigurationManager.AppSettings["logLevel"].ToString());
            if (cat < logLevel) return;
            using (StreamWriter w = File.AppendText(Program.getPath("sgxLog")))
            {
                w.WriteLine("{0} {1} {2}", DateTime.Now.ToString("MM\\/dd\\/yyyy HH:mm:ss"), cat, msg);
                Console.WriteLine("{0} {1} {2}", DateTime.Now.ToString("MM\\/dd\\/yyyy HH:mm"), cat, msg);
            }
        }

    }

    /**
     * program to load remote tickers and then build an export based on those files
     */
    static class Program
    {

        internal static DateTime ID = DateTime.Now;

        internal static string TEMP_DIR = getPath("tmpDir");

        internal static string PREPROCESS_DIR = getPath("sqlPreprocess");

        internal static bool m_RemoveDuplicateTicker = getRemoveDuplicateProperty();

        internal static void ToDisk(DataTable dt, string fileName)
        {
            StringBuilder sb = new StringBuilder();
            foreach (DataRow row in dt.Rows)
            {
                var fields = row.ItemArray.Select(field =>
                  string.Concat("\"", field.ToString().Replace("\"", "\"\""), "\""));
                sb.AppendLine(string.Join(",", fields));
            }
            File.WriteAllText(fileName, sb.ToString());

        }

        static void Main()
        {
            SqlConnection conn = null;

            try
            {
                // open (and keep open connection) need for temporary table
                LOG.Debug("Opening Database Connection");
                conn = new SqlConnection(ConfigurationManager.ConnectionStrings["xf.target"].ConnectionString);
                conn.Open();

                // table to hold file mappings
                LOG.Info("Creating Ticker Table");
                using (SqlCommand command = new SqlCommand(Properties.Resources.createTickerTable, conn)) command.ExecuteNonQuery();
                LOG.Info("Finished Creating Ticker Table");

                // SGX ticker file - write to DB
                DataTable tickerData = getTickerData(new string[] { "name", "tickerSymbol", "exchangeSymbol", "isin", "short_name" }, ConfigurationManager.AppSettings["tickerURL"].ToString(), "SGX");
                writeToDB(tickerData, conn);


                //Assembla ticket #935, add switch to disable this
                // ASEAN ticker file - write to DB
                var aseanTickerData = getTickerData(new string[] { "exchangeSymbol", "tickerSymbol", "name" }, ConfigurationManager.AppSettings["xtraTickerURL"].ToString(), "ASEAN");

                if (m_RemoveDuplicateTicker)
                {
                    //Get Array from SGX Tickers
                    var sgxTickerArray = tickerData.AsEnumerable().Select(row => row["tickerSymbol"].ToString()).ToArray();
                    //ToDisk(aseanTickerData, "c:\\asean_test_before.csv"); //For debugging

                    //Filter ASEAN Ticker Data
                    aseanTickerData = aseanTickerData.AsEnumerable()
                                                     .Where(r => !sgxTickerArray.Contains(r.Field<string>("tickerSymbol")))
                                                     .CopyToDataTable();
                    aseanTickerData.TableName = ConfigurationManager.AppSettings["companiesTableName"];
                    //ToDisk(aseanTickerData, "c:\\asean_test_after.csv"); //For debugging
                }
                writeToDB(aseanTickerData, conn);

                // match S&P companies to lists provided
                LOG.Info("Updating Ticker Table");
                string filePath = Path.Combine(PREPROCESS_DIR, "updateTmpTickerTable.sql");
                string sql = File.ReadAllText(filePath);
                using (SqlCommand command = new SqlCommand(sql, conn))
                { 
                    command.ExecuteNonQuery();
                }
                LOG.Info("Finished Updating Ticker Table");

                // export list of good companies
                tickerData = new DataTable();
                using (SqlCommand command = new SqlCommand(Properties.Resources.exportTickerTable, conn)) tickerData.Load(command.ExecuteReader());
                writeToFile(tickerData.CreateDataReader(), TEMP_DIR + ConfigurationManager.AppSettings["companiesFileName"].ToString());

                // export list of bad companies
                tickerData = new DataTable();
                using (SqlCommand command = new SqlCommand(Properties.Resources.exportNFCompanies, conn)) tickerData.Load(command.ExecuteReader());
                writeToFile(tickerData.CreateDataReader(), TEMP_DIR + ConfigurationManager.AppSettings["notFoundFileName"].ToString());

                // export list of unique currencies
                tickerData = new DataTable();
                using (SqlCommand command = new SqlCommand(Properties.Resources.exportUniqueCurrency, conn)) tickerData.Load(command.ExecuteReader());
                writeToFile(tickerData.CreateDataReader(), TEMP_DIR + ConfigurationManager.AppSettings["currenciesFileName"].ToString());

                // now execute loader specific sql scripts (need a while to do so)
                executeQueries(TEMP_DIR, conn);

                // now let's move the files over to an FTP server
                pushFiles(TEMP_DIR);

            }
            catch (Exception e)
            {
                LOG.Error("Running Program", e);
            }
            finally
            {

                // close connection
                if (conn != null) conn.Close();
                LOG.Debug("Closing Database Connection");

                // let's tell everyone we're done
                LOG.Info("Finished Data Processing, cleaning up");

                // archive files
                archiveFiles(TEMP_DIR);

                // remove temporary directory
                Directory.Delete(TEMP_DIR, true);

            }

        }

        /**
         * retrieve ticker file and convert to data table
         */
        static DataTable getTickerData(string[] columns, string tickerURL, string type)
        {

            // start by retrieving file
            LOG.Info("Retrieving " + type + " Ticker Data");
            WebClient client = new WebClient();
            string response = client.DownloadString(tickerURL);
            client.Dispose();
            LOG.Debug("Finished " + type + " Retrieving Ticker Data");

            byte[] bytes = Encoding.UTF8.GetBytes(response);
            MemoryStream stream = new MemoryStream(bytes);

            DataTable table = new DataTable();
            table.TableName = ConfigurationManager.AppSettings["companiesTableName"];
            foreach (string field in columns) table.Columns.Add(field, typeof(string));

            LOG.Info("Parsing " + type + " Ticker Data");
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
                    if (values.Length < columns.Length) continue;
                    DataRow row = table.NewRow();
                    for (int i = 0; i < columns.Length; i++) row[columns[i]] = values[i].Replace(" MAINBOARD", "");
                    table.Rows.Add(row);
                }
            }
            LOG.Debug("Finished " + type + " Parsing Ticker Data");

            return table;
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
                string outPath = tmpDir + Path.GetFileName(file).Replace(".sql", ".csv");
                using (SqlCommand command = new SqlCommand(query, conn))
                {
                    int commandLineTimeout = 0;
                    if (!int.TryParse(ConfigurationManager.AppSettings["commandLineTimeout"], out commandLineTimeout))
                    {
                        //default to 2 hours
                        commandLineTimeout = 7200;
                    }
                    command.CommandTimeout = commandLineTimeout;
                    IDataReader reader = command.ExecuteReader();
                    LOG.Debug("Finished executing script " + file);
                    writeToFile(reader, outPath);
                    reader.Close();
                }
            }

        }

        /**
         * push all the files in the tmp directory to remote server (FTPS)
         */
        static void pushFiles(string tmpDir)
        {

            string[] files = Directory.GetFiles(tmpDir);
            string url = @ConfigurationManager.AppSettings["ftpURL"].ToString();
            string user = @ConfigurationManager.AppSettings["ftpUsername"].ToString();
            string pass = @ConfigurationManager.AppSettings["ftpPassword"].ToString();
            string directory = @ConfigurationManager.AppSettings["ftpDirectory"];

            LOG.Info("Creating SFTP Client");

            using (SftpClient scp = new SftpClient(url, user, pass))
            {
                LOG.Debug("Trying to connect to " + url);
                scp.Connect();

                if (!String.IsNullOrWhiteSpace(directory))
                {
                    scp.ChangeDirectory(directory);
                }

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
        static void archiveFiles(string tmpDir)
        {

            string zipPath = getPath("archiveFile");
            string[] files = Directory.GetFiles(tmpDir);
            byte[] buffer = new byte[4096];

            LOG.Info("Creating archive " + zipPath);

            using (ZipOutputStream s = new ZipOutputStream(File.Create(zipPath)))
            {
                s.SetLevel(9);
                foreach (string file in files)
                {

                    LOG.Debug("Adding Entry " + file);

                    ZipEntry entry = new ZipEntry(Path.GetFileName(file));
                    s.PutNextEntry(entry);

                    using (FileStream fs = File.OpenRead(file))
                    {
                        int sourceBytes;
                        do
                        {
                            sourceBytes = fs.Read(buffer, 0, buffer.Length);
                            s.Write(buffer, 0, sourceBytes);
                        } while (sourceBytes > 0);
                    }

                    LOG.Debug("Finished Adding Entry " + file);

                }

            }

            LOG.Debug("Finished Creating archive " + zipPath);

        }

        /**
         * write dataset to delimeted file
         */
        public static void writeToFile(this IDataReader dataReader, string fileOutputPath, bool firstRowIsColumnHeader = true, string seperator = ",")
        {

            LOG.Info("Exporting data to " + fileOutputPath);

            StreamWriter sw = new StreamWriter(fileOutputPath, false);
            int icolcount = dataReader.FieldCount;

            if (firstRowIsColumnHeader)
            {
                for (int index = 0; index < icolcount; index++)
                {
                    sw.Write(dataReader.GetName(index));
                    if (index < icolcount - 1) sw.Write(seperator);
                }
                sw.Write(sw.NewLine);
            }

            while (dataReader.Read())
            {
                for (int index = 0; index < icolcount; index++)
                {
                    if (!dataReader.IsDBNull(index)) sw.Write(FormatValueCSV(dataReader.GetValue(index), dataReader.GetFieldType(index)));
                    if (index < icolcount - 1) sw.Write(seperator);
                }
                sw.Write(sw.NewLine);
            }
            sw.Close();

            LOG.Debug("Finished Exporting data to " + fileOutputPath);
        }

        /**
         * format value for CSV
         */
        public static string FormatValueCSV(object value, Type valueType)
        {
            if (Object.ReferenceEquals(value, null)) return "";
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

        /**
         * get a date based directory
         */
        internal static string getPath(string prop)
        {
            var val = ConfigurationManager.AppSettings[prop].ToString();
            val = string.Format(val, ID);
            var dirs = Path.GetDirectoryName(val);
            if (!Directory.Exists(dirs)) Directory.CreateDirectory(dirs);
            return val;
        }

        internal static bool getRemoveDuplicateProperty()
        {
            bool removeDuplicateTickers = false;
            if (ConfigurationManager.AppSettings["RemoveDuplicateTickers"] != null)
            {
                removeDuplicateTickers = bool.Parse(ConfigurationManager.AppSettings["RemoveDuplicateTickers"]);
            }
            return removeDuplicateTickers;
        }

    }

}
