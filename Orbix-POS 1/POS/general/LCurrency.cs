using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class LCurrency
    {
        public static string displayValue(string value)
        {
            double am = 0;
            if (string.IsNullOrEmpty(value))
            {
                am = 0;
            }else
            {
                am = Convert.ToDouble(value);
            }
            return @String.Format("{0:N}", am);
        }

        public static object getValue(string value)
        {
            if (string.IsNullOrEmpty(value))
            {
                value = "0";
            }
            return value.Replace(",", "");
        }

    }
}
