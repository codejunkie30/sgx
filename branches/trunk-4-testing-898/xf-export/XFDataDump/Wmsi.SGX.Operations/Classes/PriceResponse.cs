using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Wmsi.SGX.Operations.Classes
{

    public class PriceResponse
    {
        public Price price { get; set; }
    }

    public class Price
    {
        public float? lastPrice { get; set; }
        public float? openPrice { get; set; }
        public float? closePrice { get; set; }
        public long? previousDate { get; set; }
        public long? currentDate { get; set; }
        public long? lastTradeTimestamp { get; set; }
        public string tradingCurrency { get; set; }
        public float? change { get; set; }
        public float? percentChange { get; set; }
    }

}
