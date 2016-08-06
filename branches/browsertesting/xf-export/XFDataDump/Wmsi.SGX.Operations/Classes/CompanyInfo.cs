using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Wmsi.SGX.Operations.Classes
{

    public class CompanyResponse
    {
        public Gtis gtis { get; set; }
        public Dividendhistory dividendHistory { get; set; }
        public Holders holders { get; set; }
        public Company company { get; set; }
        public Keydev[] keyDevs { get; set; }
        public Alphafactors alphaFactors { get; set; }
    }

    public class Gtis
    {
        public object tickerCode { get; set; }
        public Gti[] gtis { get; set; }
    }

    public class Gti
    {
        public int? adjustment { get; set; }
        public int? baseScore { get; set; }
        public string companyName { get; set; }
        public long? date { get; set; }
        public string isin { get; set; }
        public string issue { get; set; }
        public int? rank { get; set; }
        public int? rankChange { get; set; }
        public string ticker { get; set; }
        public int? totalScore { get; set; }
    }

    public class Dividendhistory
    {
        public string tickerCode { get; set; }
        public Dividendvalue[] dividendValues { get; set; }
    }

    public class Dividendvalue
    {
        public long? dividendExDate { get; set; }
        public long? dividendPayDate { get; set; }
        public string dividendType { get; set; }
        public float? dividendPrice { get; set; }
    }

    public class Holders
    {
        public Holder[] holders { get; set; }
        public string tickerCode { get; set; }
    }

    public class Holder
    {
        public string name { get; set; }
        public int? shares { get; set; }
        public float? percent { get; set; }
    }

    public class Company
    {
        public Companyinfo companyInfo { get; set; }
    }

    public class Companyinfo
    {
        public object avgBrokerReq { get; set; }
        public string exchange { get; set; }
        public float? avgTradedVolM3 { get; set; }
        public float? avgVolumeM3 { get; set; }
        public float? basicEpsIncl { get; set; }
        public object beta5Yr { get; set; }
        public string businessDescription { get; set; }
        public float? bvShare { get; set; }
        public float? capitalExpenditures { get; set; }
        public float? cashInvestments { get; set; }
        public float? closePrice { get; set; }
        public string companyAddress { get; set; }
        public string companyName { get; set; }
        public string companyWebsite { get; set; }
        public float? dividendYield { get; set; }
        public float? ebit { get; set; }
        public float? ebitda { get; set; }
        public float? ebitdaMargin { get; set; }
        public object employees { get; set; }
        public float? enterpriseValue { get; set; }
        public float? eps { get; set; }
        public float? evEbitData { get; set; }
        public long? fiscalYearEnd { get; set; }
        public long? filingDate { get; set; }
        public string filingCurrency { get; set; }

        public string floatPercentage { get; set; }
        public float? Percentage { get; set; }
        public int? gtiScore { get; set; }
        public int? gtiRankChange { get; set; }
        public string gvKey { get; set; }
        public float? highPrice { get; set; }
        public string industry { get; set; }
        public string industryGroup { get; set; }
        public float? lowPrice { get; set; }
        public float? marketCap { get; set; }
        public float? netIncome { get; set; }
        public float? netProfitMargin { get; set; }
        public float? openPrice { get; set; }
        public float? peRatio { get; set; }
        public long? previousCloseDate { get; set; }
        public float? previousClosePrice { get; set; }
        public float? priceToBookRatio { get; set; }
        public float? priceVolHistYr { get; set; }
        public float? priceVs52WeekHigh { get; set; }
        public float? priceVs52WeekLow { get; set; }
        public float? returnOnEquity { get; set; }
        public float? sharesOutstanding { get; set; }
        public object sharesSoldShort { get; set; }
        public object targetPrice { get; set; }
        public float? targetPriceNum { get; set; }
        public string tickerCode { get; set; }
        public float? totalAssets { get; set; }
        public float? totalDebt { get; set; }
        public object totalDebtEbitda { get; set; }
        public float? totalDebtEquity { get; set; }
        public float? totalRev1YrAnnGrowth { get; set; }
        public float? totalRev3YrAnnGrowth { get; set; }
        public float? totalRev5YrAnnGrowth { get; set; }
        public float? totalRevenue { get; set; }
        public float? volume { get; set; }
        public float? volWeightedAvgPrice { get; set; }
        public float? adjustedVolWeightedAvgPrice { get; set; }
        public long? vwapAsOfDate { get; set; }
        public string vwapCurrency { get; set; }
        public int? yearFounded { get; set; }
        public float? yearHigh { get; set; }
        public float? yearLow { get; set; }
        public object volatility { get; set; }
    }

    public class Alphafactors
    {
        public int? analystExpectations { get; set; }
        public int? capitalEfficiency { get; set; }
        public long? date { get; set; }
        public int? earningsQuality { get; set; }
        public int? historicalGrowth { get; set; }
        public string id { get; set; }
        public int? priceMomentum { get; set; }
        public int? size { get; set; }
        public int? valuation { get; set; }
        public int? volatility { get; set; }
        public int? companyId { get; set; }
    }

    public class Keydev
    {
        public long? date { get; set; }
        public string headline { get; set; }
        public string situation { get; set; }
        public object time { get; set; }
        public string type { get; set; }
        public string source { get; set; }
    }

}
