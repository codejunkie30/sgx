using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Wmsi.SGX.Operations.Classes;

namespace Wmsi.SGX.Operations
{
    public static class SGX_AdjustmentFactorSpreadSheetMerge
    {
        public static void Execute()
        {
            //1 Load CSV file from SGX with volume adjustments  
            //2 Load Adjustment File from sgx
            //3 Merge
            //4 Spit out new spreadsheet.
            //Download from here: 

            var folderPath = @"E:\Company Documents\SGX\SGX_AdjVWAP_Files\";
            var spFileData = File.ReadAllLines(@folderPath + "S_P_ValVol_03_May_2016_QA_Post.csv");
            //var spFileData = Utilities.DownloadTextFile(GlobalConfig.SGXValueVolumeURL);

            /*
             * Format:
             * Exchange - A
             * Ticker - B
             * Date - C - DD/MM/YYYY  DateTime dt=DateTime.ParseExact("24/01/2013", "d/M/yyyy", CultureInfo.InvariantCulture);
             * Value - D
             * Volume - E
             * Currency - F
             */
            var tickerAdjustedList = new List<Classes.TickerAdjusted>();
            var spFileDataWOHeader = spFileData.Skip(1).ToList();
            foreach (var row in spFileDataWOHeader)
            {
                //Console.WriteLine(row);
                var columns = Utilities.SplitCSV(row);
                var tickerAdjusted = new Classes.TickerAdjusted()
                {
                    //CompanyStockFileId = companyStockFile.CompanyStockFileId,
                    Exchange = columns[0], //Exchange
                    Ticker = (columns[1] ?? string.Empty).Trim().ToUpper().Replace("\"", ""), //Ticker
                    Date = DateTime.ParseExact(columns[2].Replace("\"", ""), "d/M/yyyy", CultureInfo.InvariantCulture), //Date
                    Value = columns[3], //Value
                    Volume = columns[4], //Volume
                    Currency = columns[5] //Currency
                };
                tickerAdjustedList.Add(tickerAdjusted);
            }

            var adjFileData = Utilities.DownloadFTPTextFile("adjustment-factor.csv");
            //var adjFileData = File.ReadAllLines(@folderPath + "adjustment-factor.csv");
            /*
             * Format:
             * tickerStock - A - 0
             * exchangeSymbol - B - 1
             * WMISApi - C - 2
             * MaxVolume - D - 3
             * Blank - E - 4
             * PricingDate - F - 5 - MM/DD/YYYY
             */
             var adjFileDataWOHeader = adjFileData.Skip(1).ToList();
            foreach (var row in adjFileDataWOHeader)
            {
                //Console.WriteLine(row);
                var columns = Utilities.SplitCSV(row);
                //Map Values to local variables
                var _tickerStock = (columns[0] ?? string.Empty).Trim().ToUpper().Replace("\"", "");
                var _exchangeSymbol = columns[1];
                var _WMISApi = columns[2];
                var _MaxVolume = columns[3];
                var _PricingDate = DateTime.Parse(columns[5].Replace("\"", ""));

                //Finds single value
                //var item = tickerAdjustedList.Find(s => s.Ticker == _tickerStock && s.Date.Value.ToShortDateString() == _PricingDate.ToShortDateString());
                //Finds multiple values
                var items = tickerAdjustedList.FindAll(s => s.Ticker == _tickerStock && s.Date.Value.ToShortDateString() == _PricingDate.ToShortDateString()).ToList();
                foreach (var item in items)
                {
                    if (item != null)
                    {
                        item.AdjFactor = _WMISApi;
                        item.AdjVolume = _MaxVolume;
                    }
                }
            }

            //adj factor file only provides last 6 months
            var filteredList1 = tickerAdjustedList.OrderBy(p => p.Exchange)
                .ThenBy(p => p.Ticker)
                .ThenBy(p => p.Date)
                .ToList();

            string fileName = string.Format(@"SGX-ADJVWAP-RESEARCH_ALLTICKERS_{0}.csv", DateTime.Now.ToString("yyyyMMdd_HH_mm_ss"));
            WriteToFile(folderPath + fileName, filteredList1);

            //string[] tickers = { "BJE", "BRD", "BJV", "BKB", "580" }; //Get Tickers from Server
            string[] tickers = { "S68" , "BJE" };
            foreach (var ticker in tickers)
            {

                fileName = string.Format(@"SGX-ADJVWAP-RESEARCH_{0}_{1}.csv", ticker, DateTime.Now.ToString("yyyyMMdd_HH_mm_ss"));
                //Not filtering?
                var filteredList = tickerAdjustedList.Where(l => l.Ticker.ToUpper().Trim() == ticker).ToList();
                WriteToFile(folderPath + fileName, filteredList);
            }
        }

        public static void WriteToFile(string filePathAndName, List<Classes.TickerAdjusted> tickerAdjustedList)
        {
            var csv = new StringBuilder();
            //Write Header
            /*
                //Exchange
                //Ticker
                //Date
                //Value
                //Volume
                //Currency
                //AdjFactor
                //AdjVolume
                //Volume * AdjFactor
             */
            csv.AppendLine(string.Format("{0},{1},{2},{3},{4},{5},{6},{7},{8}",
                    "Exchange", //0
                    "Ticker", //1
                    "Date", //2
                    "Value", //3
                    "Volume", //4
                    "Currency", //5
                    "AdjFactor", //6
                    "AdjVolume", //7
                    "Volume * AdjFactor")); //8


            foreach (var item in tickerAdjustedList)
            {
                decimal volPlusAdjFactor = 0m;
                if (item.Volume != null && item.AdjFactor != null)
                {
                    var dVolume = decimal.Parse(item.Volume.Replace("\"", ""));
                    var dAdjFactor = decimal.Parse(item.AdjFactor.Replace("\"", ""));
                    volPlusAdjFactor = dVolume * dAdjFactor;
                }

                csv.AppendLine(string.Format("{0},{1},{2},{3},{4},{5},{6},{7},{8}",
                    item.Exchange, //0
                    item.Ticker, //1
                    item.Date.Value.ToShortDateString(), //2
                    item.Value, //3
                    item.Volume, //4
                    item.Currency, //5
                    item.AdjFactor, //6
                    item.AdjVolume, //7
                    volPlusAdjFactor.ToString())); //8
            }


            var csvFileData = csv.ToString();
            File.WriteAllText(filePathAndName, csvFileData);
        }
    }
}
