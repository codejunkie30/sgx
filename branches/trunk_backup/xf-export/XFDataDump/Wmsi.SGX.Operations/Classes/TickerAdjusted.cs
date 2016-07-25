using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Wmsi.SGX.Operations.Classes
{
    public class TickerAdjusted
    {
        public string Exchange { get; set; }
        public string Ticker { get; set; }
        public DateTime? Date { get; set; }
        public string Value { get; set; }
        public string Volume { get; set; }
        public string Currency { get; set; }
        public string AdjFactor { get; set; }
        public string AdjVolume { get; set; }
    }
}
