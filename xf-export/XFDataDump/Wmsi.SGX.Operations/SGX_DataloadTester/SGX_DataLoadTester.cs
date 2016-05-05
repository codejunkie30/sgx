//using Microsoft.VisualBasic.FileIO;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Net.Mail;
using System.Runtime.Serialization.Json;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Wmsi.SGX.Operations.Classes;

namespace Wmsi.SGX.Operations
{
    public static class SGX_DataLoadTester
    {

        public static void CrossCheck()
        {
            List<string> rows = null;
            using (var client = new HttpClient())
            {
                string url = GlobalConfig.TickerURL;
                var gaResponse = client.GetAsync(url);
                var response = gaResponse.Result.Content.ReadAsStringAsync();
                rows = response.Result.Split(new[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries)
                                .Skip(1)
                                .ToList();
            }

            var companies = new List<CompanyStock>();
            foreach (var row in rows)
            {
                //Console.WriteLine(row);
                var columns = Utilities.SplitCSV(row);
                var companyStock = new CompanyStock()
                {
                    CompanyName = columns[0],
                    StockCode = (columns[1].Split(':')[0] ?? string.Empty).Replace("\"", "").ToUpper(),
                    Market = columns[1].Split(':')[1],
                    ISINCode = columns[2],
                    HPShortName = columns[3],

                };
                companies.Add(companyStock);
            }

            var stockCodesSGX = companies.Select(c => c.StockCode).ToList();

            rows = null;
            using (var client = new HttpClient())
            {
                string url = GlobalConfig.XtraTickerURL;
                var gaResponse = client.GetAsync(url);
                var response = gaResponse.Result.Content.ReadAsStringAsync();
                rows = response.Result.Split(new[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries)
                                .Skip(1)
                                .ToList();
            }

            companies = new List<CompanyStock>();
            foreach (var row in rows)
            {
                //Console.WriteLine(row);
                var columns = Utilities.SplitCSV(row);
                var companyStock = new CompanyStock()
                {
                    CompanyName = columns[2],
                    StockCode = (columns[1] ?? string.Empty).Replace("\"", "").ToUpper(),
                    Market = columns[0]
                };
                companies.Add(companyStock);
            }

            foreach (var item in companies)
            {
                if (stockCodesSGX.Contains(item.StockCode))
                {
                    Console.WriteLine(item.StockCode);
                }
            }   
        }

        public static void Execute()
        {
            using (var client = new HttpClient())
            {
                
                var gaResponse = client.GetAsync(GlobalConfig.TickerURL);
                var response = gaResponse.Result.Content.ReadAsStringAsync();
                string result = response.Result;
                var rows = result.Split(new[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries)
                                .Skip(1)
                                .ToList();

                var companies = new List<CompanyStock>();
                foreach (var row in rows)
                {
                    //Console.WriteLine(row);
                    var columns = Utilities.SplitCSV(row);
                    var companyStock = new CompanyStock()
                    {
                        CompanyName = columns[0],
                        StockCode = (columns[1].Split(':')[0] ?? string.Empty).Replace("\"", ""),
                        Market = columns[1].Split(':')[1],
                        ISINCode = columns[2],
                        HPShortName = columns[3],

                    };
                    companies.Add(companyStock);
                }
                var stockCodes = companies.Select(c => c.StockCode).ToList();
                CreateStockCodeSheet(stockCodes);
            }
        }

        private static void CreateStockCodeSheet(List<String> stockCodes)
        {
            //GlobalProxySelection.Select = new WebProxy("127.0.0.1", 8888);
            //string[] tickers = { "O5RU", "5TG", "533" }; //Get Tickers from Server
            string requestUrl = string.Format(@"https://{0}/sgx/company", GlobalConfig.SGXDomain);

            //before your loop
            var csv = new StringBuilder();
            //Write Header
            csv.AppendLine(string.Format("{0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11}",
                    "tickerCode", //0
                    "CompanyName", //1
                    "vwapCurrency", //2
                    "vwapAsOfDate", //3
                    "volWeightedAvgPrice", //4
                    "adjustedVolWeightedAvgPrice", //5
                    "PreviousCloseDate", //6
                    "Pricing.LastPrice", //7
                    "Pricing.LastTradeDateTime", //8
                    "Pricing.CurrentDateTime", //9
                    "Pricing.PreviousTradeDateTime", //10
                    "Float Percentage")); //11

            foreach (var ticker in stockCodes)
            {
                HttpWebRequest request = WebRequest.Create(requestUrl) as HttpWebRequest;
                //request.Accept = "application/json";
                request.Method = "Post";
                request.ContentType = "application/json";
                var stock = new TickerComponent();
                stock.id = ticker;
                string json = JsonConvert.SerializeObject(stock);

                using (var streamWriter = new StreamWriter(request.GetRequestStream()))
                {
                    streamWriter.Write(json);
                    streamWriter.Flush();
                    streamWriter.Close();
                }

                var httpResponse = (HttpWebResponse)request.GetResponse();
                string jsonResult = string.Empty;
                using (var streamReader = new StreamReader(httpResponse.GetResponseStream()))
                {
                    jsonResult = streamReader.ReadToEnd();
                }

                Classes.CompanyResponse t1 = null;
                try
                {
                    t1 = JsonConvert.DeserializeObject<Classes.CompanyResponse>(jsonResult);
                }
                catch
                {
                    csv.AppendLine(string.Format("{0},{1},{2}",
                              ticker, //0
                              "Couldn't parse JSON response when requesting using StockCode", //1
                              jsonResult //2
                              )); 
                    continue; //next item
                }

                var newLine = string.Empty;
                if (t1.company != null)
                {
                    if (t1.company.companyInfo != null)
                    {
                        //Get Current Price

                        var currentPrice = GetCurrentPrice(ticker);

                        string lastPrice = string.Empty;
                        string lastTradeTimestamp = string.Empty;
                        string currentTradeTimestamp = string.Empty;
                        string previousTradeTimestamp = string.Empty;

                        if (currentPrice != null && currentPrice.price != null)
                        {
                            lastPrice = (currentPrice.price.lastPrice.HasValue) ? currentPrice.price.lastPrice.Value.ToString() : string.Empty;
                            lastTradeTimestamp = (currentPrice.price.lastTradeTimestamp.HasValue) ? Utilities.FromUnixTime(currentPrice.price.lastTradeTimestamp.Value).ToShortDateString() : "Date is null";
                            currentTradeTimestamp = (currentPrice.price.currentDate.HasValue) ? Utilities.FromUnixTime(currentPrice.price.currentDate.Value).ToShortDateString() : "Date is null";
                            previousTradeTimestamp = (currentPrice.price.previousDate.HasValue) ? Utilities.FromUnixTime(currentPrice.price.previousDate.Value).ToShortDateString() : "Date is null";
                        }
                        newLine = string.Format("{0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11}",
                            t1.company.companyInfo.tickerCode, //0
                            t1.company.companyInfo.companyName, //1
                            t1.company.companyInfo.vwapCurrency, //2
                            (t1.company.companyInfo.vwapAsOfDate.HasValue) ? Utilities.FromUnixTime(t1.company.companyInfo.vwapAsOfDate.Value).ToShortDateString() : "Date is null", //3
                            t1.company.companyInfo.volWeightedAvgPrice, //4
                            t1.company.companyInfo.adjustedVolWeightedAvgPrice, //5
                            (t1.company.companyInfo.previousCloseDate.HasValue) ? Utilities.FromUnixTime(t1.company.companyInfo.previousCloseDate.Value).ToShortDateString() : "Date is null", //6
                            lastPrice, //7
                            lastTradeTimestamp, //8
                            currentTradeTimestamp, //9
                            previousTradeTimestamp, //10
                            t1.company.companyInfo.floatPercentage); //11
                    }
                    else
                    {
                        newLine = string.Format("{0},{1}",
                              ticker, //0
                              "No Company.CompanyInfo Data Found"); //1
                    }
                }
                else
                {
                    newLine = string.Format("{0},{1}",
                            ticker, //0
                            "No Company Data Found"); //1
                }
                csv.AppendLine(newLine);
            }//Loop

            //after your loop write to disk
            string fileName = string.Format(@"SGX-DataPoints_{0}.csv", DateTime.Now.ToString("yyyyMMdd_HH_mm_ss"));
            string csvFileData = csv.ToString();
            var attachment = Attachment.CreateAttachmentFromString(csv.ToString(), fileName); //Mail attachment
            //string filePath = string.Format(@"C:\{0}", fileName);
            //File.WriteAllText(filePath, csvFileData);

            var email = new System.Net.Mail.MailMessage(GlobalConfig.EmailFrom, GlobalConfig.EmailTo, string.Format("SGX data load data points from {0}", GlobalConfig.SGXDomain), string.Format("SGX data from {0}", GlobalConfig.SGXDomain));
            email.Priority = (stockCodes.Count() <= 700) ? MailPriority.High : MailPriority.Normal;
            
            email.Attachments.Add(attachment);
            SmtpClient SMTPServer = new SmtpClient(GlobalConfig.SmtpHost, GlobalConfig.SmtpPort);

            if(!string.IsNullOrWhiteSpace(GlobalConfig.SmtpUserName) && !string.IsNullOrWhiteSpace(GlobalConfig.SmtpPassword))
            { 
                SMTPServer.Credentials = new System.Net.NetworkCredential(GlobalConfig.SmtpUserName, GlobalConfig.SmtpPassword);
            }

            SMTPServer.Send(email);
        }

        private static Classes.PriceResponse GetCurrentPrice(string ticker)
        {
            //GlobalProxySelection.Select = new WebProxy("127.0.0.1", 8888);
            //string[] tickers = { "O5RU", "5TG", "533" }; //Get Tickers from Server
            string requestUrl = string.Format(@"https://{0}/sgx/price", GlobalConfig.SGXDomain);

            HttpWebRequest request = WebRequest.Create(requestUrl) as HttpWebRequest;
            //request.Accept = "application/json";
            request.Method = "Post";
            request.ContentType = "application/json";
            var stock = new TickerComponent();
            stock.id = ticker;
            string json = JsonConvert.SerializeObject(stock);

            using (var streamWriter = new StreamWriter(request.GetRequestStream()))
            {
                streamWriter.Write(json);
                streamWriter.Flush();
                streamWriter.Close();
            }

            var httpResponse = (HttpWebResponse)request.GetResponse();
            string jsonResult = string.Empty;
            using (var streamReader = new StreamReader(httpResponse.GetResponseStream()))
            {
                jsonResult = streamReader.ReadToEnd();
            }

            try
            {
                return JsonConvert.DeserializeObject<Classes.PriceResponse>(jsonResult);
            }
            catch
            {
                return null; //Couldn't deserialize
            }
        }
    }
}