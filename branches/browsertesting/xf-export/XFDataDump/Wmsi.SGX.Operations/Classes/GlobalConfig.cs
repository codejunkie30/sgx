using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Wmsi.SGX.Operations.Classes
{
    public static class GlobalConfig
    {
        public static string SGXDomain
        {
            get {
                return ConfigurationManager.AppSettings["SGXDomain"];
            }
        }

        public static string TickerURL
        {
            get
            {
                return ConfigurationManager.AppSettings["tickerURL"];
            }
        }

        public static string XtraTickerURL
        {
            get
            {
                return ConfigurationManager.AppSettings["xtraTickerURL"];
            }
        }

        public static string SGXValueVolumeURL
        {
            get
            {
                return ConfigurationManager.AppSettings["SGX_ValueVolume_URL"];
            }
        }

        public static string SmtpHost
        {
            get
            {
                return ConfigurationManager.AppSettings["SmtpHost"];
            }
        }
        public static int SmtpPort
        {
            get
            {
                return int.Parse(ConfigurationManager.AppSettings["SmtpPort"]);
            }
        }
        public static string EmailTo
        {
            get
            {
                return ConfigurationManager.AppSettings["EmailTo"];
            }
        }
        public static string EmailFrom
        {
            get
            {
                return ConfigurationManager.AppSettings["EmailFrom"];
            }
        }
        public static string SmtpUserName
        {
            get
            {
                return ConfigurationManager.AppSettings["SmtpUserName"];
            }
        }
        public static string SmtpPassword
        {
            get
            {
                return ConfigurationManager.AppSettings["SmtpPassword"];
            }
        }
        public static string AdjustmentFactorFileName
        {
            get
            {
                return ConfigurationManager.AppSettings["AdjustmentFactorFileName"];
            }
        }
    }
}
