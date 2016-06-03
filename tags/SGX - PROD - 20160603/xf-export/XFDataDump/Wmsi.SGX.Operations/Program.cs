using System;
using Wmsi.Utilities.ExceptionManagement;

namespace Wmsi.SGX.Operations
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                //throw new Exception("test");
           //     SGX_AdjustmentFactorSpreadSheetMerge.Execute();
                SGX_DataLoadTester.Execute();
                //SGX_DataLoadTester.CrossCheck();
            }
            catch (Exception ex)
            {
                ExceptionManager.Publish(ex);
            }
        }
    }
}
