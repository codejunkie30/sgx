using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Wmsi.SGX.Operations.Classes
{
    public partial class CompanyStock
    {
        #region Properties
        public int CompanyStockId { get; set; }
        public int CompanyStockFileId { get; set; }
        public string CompanyName { get; set; }
        public string StockCode { get; set; }
        public string Market { get; set; }
        public string ISINCode { get; set; }
        public string HPShortName { get; set; }
        #endregion
    }
}
