﻿using System;
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

        internal static void Error(String msg, Exception e) { LogIt(msg + Environment.NewLine + e.StackTrace, "Error"); }

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
             SqlConnection conn = null;

            try
            {

                // tmp directory to store files
                string tmpDir = ConfigurationManager.AppSettings["tmpDir"].ToString();

                // open (and keep open connection) need for temporary table
                LOG.Info("Opening Database Connection");
                conn = new SqlConnection(ConfigurationManager.ConnectionStrings["xf.target"].ConnectionString);
                conn.Open();

                // table to hold file mappings
                LOG.Info("Creating Ticker Table");
                using (SqlCommand command = new SqlCommand(Properties.Resources.createTickerTable, conn)) command.ExecuteNonQuery();
                LOG.Info("Finished Creating Ticker Table");

                // SGX ticker file - write to DB
                LOG.Info("Loading SGX Listings");
                DataTable tickerData = getTickerData(new string[] { "name", "tickerSymbol", "exchangeSymbol", "isin", "short_name" }, ConfigurationManager.AppSettings["tickerURL"].ToString(), "SGX");
                writeToDB(tickerData, conn);
                LOG.Info("Finished Loading SGX Listings");

                // ASEAN ticker file - write to DB
                LOG.Info("Loading ASEAN Listings");
                tickerData = getTickerData(new string[] { "exchangeSymbol", "tickerSymbol", "name" }, ConfigurationManager.AppSettings["xtraTickerURL"].ToString(), "ASEAN");
                writeToDB(tickerData, conn);
                LOG.Info("Finished Loading ASEAN Listings");

                // match S&P companies to lists provided
                LOG.Info("Updating Ticker Table");
                using (SqlCommand command = new SqlCommand(Properties.Resources.updateTmpTickerTable, conn)) command.ExecuteNonQuery();
                LOG.Info("Finished Updating Ticker Table");

                // export list of good companies
                tickerData = new DataTable();
                using (SqlCommand command = new SqlCommand(Properties.Resources.exportTickerTable, conn)) tickerData.Load(command.ExecuteReader());
                writeToFile(tickerData.CreateDataReader(), tmpDir + ConfigurationManager.AppSettings["companiesFileName"].ToString());

                // export list of bad companies
                tickerData = new DataTable();
                using (SqlCommand command = new SqlCommand(Properties.Resources.exportNFCompanies, conn)) tickerData.Load(command.ExecuteReader());
                writeToFile(tickerData.CreateDataReader(), tmpDir + ConfigurationManager.AppSettings["notFoundFileName"].ToString());

                // export list of unique currencies
                tickerData = new DataTable();
                using (SqlCommand command = new SqlCommand(Properties.Resources.exportUniqueCurrency, conn)) tickerData.Load(command.ExecuteReader());
                writeToFile(tickerData.CreateDataReader(), tmpDir + ConfigurationManager.AppSettings["currenciesFileName"].ToString());

                // now execute loader specific sql scripts (need a while to do so)
                executeQueries(tmpDir, conn);

                // close connection (removes tmp table)
                conn.Close();

                // now let's move the files over to an FTP server
                pushFiles(tmpDir);

                // archive files
                archiveFiles(tmpDir);


            }
            catch (Exception e)
            {
                LOG.Error("Running Program", e);
            }
            finally
            {
                if (conn != null) conn.Close();
                LOG.Info("Closing Database Connection");
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
            LOG.Info("Finished " + type + " Retrieving Ticker Data");

            byte[] bytes = Encoding.UTF8.GetBytes(response);
            MemoryStream stream = new MemoryStream(bytes);

            DataTable table = new DataTable();
            table.TableName = ConfigurationManager.AppSettings["companiesTableName"];
            foreach (string field in columns) table.Columns.Add(field, typeof(string));

            LOG.Debug("Parsing " + type + " Ticker Data");
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
                    command.CommandTimeout = 1800;
                    IDataReader reader = command.ExecuteReader();
                    LOG.Info("Finished executing script " + file);
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
         * write dataset to delimeted file
         */
        public static void writeToFile(this IDataReader dataReader, string fileOutputPath, bool firstRowIsColumnHeader = true, string seperator = ",")
            {

            LOG.Info("Exporting data to " + fileOutputPath);

            StreamWriter sw = new StreamWriter(fileOutputPath, false);
            int icolcount = dataReader.FieldCount;

            if (firstRowIsColumnHeader) {
                for (int index = 0; index < icolcount; index++) {
                    sw.Write(dataReader.GetName(index));
                    if (index < icolcount - 1) sw.Write(seperator);
                }
                sw.Write(sw.NewLine);
            }

            while (dataReader.Read()) {
                for (int index = 0; index < icolcount; index++) {
                    if (!dataReader.IsDBNull(index)) sw.Write(FormatValueCSV(dataReader.GetValue(index), dataReader.GetFieldType(index)));
                    if (index < icolcount - 1) sw.Write(seperator);
                }
                sw.Write(sw.NewLine);
            }
            sw.Close();

            LOG.Info("Finished Exporting data to " + fileOutputPath);
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
    }

}
