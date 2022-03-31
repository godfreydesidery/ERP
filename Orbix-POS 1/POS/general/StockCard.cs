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
    class StockCard
    {
        public bool qtyIn(string date_, string itemCode, double qty, double balance, string reference)
        {
            bool success = false;
            try
            {
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("credit_notes/get_crnoteno?code=");
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    return false;
                }
                return JsonConvert.DeserializeObject<bool>(json.ToString());


                
            }
            catch (Exception ex)
            {
                success = false;
                MessageBox.Show(ex.Message);
            }

            return success;
        }

        public bool qtyOut(string date_, string itemCode, double qty, double balance, string reference)
        {
            bool success = false;
            try
            {
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("credit_notes/get_crnoteno?code=");
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    return false;
                }
                return JsonConvert.DeserializeObject<bool>(json.ToString());

                
            }
            catch (Exception ex)
            {
                success = false;
                MessageBox.Show(ex.Message);
            }

            return success;
        }
    }
}
