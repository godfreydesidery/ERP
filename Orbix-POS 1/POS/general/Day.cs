using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS.general
{
    class Day
    {

        public static string bussinessDate  = "";

        // Install-Package Microsoft.VisualBasic

        public static int zNo = 0;
        public static string systemDate = "";

        public static DateTime getCurrentDay()
        {
            var date_ = DateTime.Parse("0001-01-01");
            try
            {

                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("days/get_bussiness_date");
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    MessageBox.Show("Could not load day information, Application will close", "Error: Loading day", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    Application.Exit();
                }
                date_ = DateTime.Parse(json.SelectToken("bussinessDate").ToString());                
            }
            catch (Exception)
            {
                MessageBox.Show("Could not load day information, Application will close", "Error: Loading day", MessageBoxButtons.OK, MessageBoxIcon.Error);
                Application.Exit();
            }

            return date_;
        }
    }

}
