using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS.general
{
    class Key
    {
        public static object place(string _key)
        {
            return null;
        }

        public static object delete()
        {
            return null;
        }

        public static object enter()
        {
            return null;
        }

        public static object sendUp()
        {
            try
            {
                System.Windows.Forms.SendKeys.Send("{up}");
            }
            catch (Exception)
            {
            }

            return null;
        }

        public static object sendDown()
        {
            try
            {
                SendKeys.Send("{down}");
            }
            catch (Exception)
            {
            }
            return null;
        }

        public static object sendLeft()
        {
            try
            {
                SendKeys.Send("{left}");
            }
            catch (Exception)
            {
            }

            return null;
        }

        public static object sendRight()
        {
            try
            {
                SendKeys.Send("{right}");
            }
            catch (Exception)
            {
            }

            return null;
        }
    }
}
