using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS.general
{
    class Error
    {
        public static object databaseConnection()
        {
            MessageBox.Show("Could not connect to Database", "Error: Database connection", MessageBoxButtons.OK, MessageBoxIcon.Error);
            return null;
        }
    }
}
