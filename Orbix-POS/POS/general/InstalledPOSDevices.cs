using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class InstalledPOSDevices
    {
        public static string fiscalPort = "COM";
        public static string fiscalOperatorPassword = "1234";
        public static string fiscalOperatorCode = "1234";
        public static string fiscalDrawer = "";
        public static string posLogicName = "";

        public static object setFiscalPrinter()
        {
            bool _set = false;
            return _set;
        }

        public static object getAvailablePosPrinter()
        {
            bool _set = false;
            return _set;
        }
    }
}
