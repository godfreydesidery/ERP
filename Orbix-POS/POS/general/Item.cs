using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using POS.general;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS.general
{
    class Item
    {
        // Install-Package Microsoft.VisualBasic

        
        public string getShortDescription(string code)
        {
            string descr = "";

            var list = new List<string>();
            try
            {
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("products/get_shortdescr?code=" + code);
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    return null;
                }
                descr = JsonConvert.DeserializeObject<string>(json.ToString());
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.StackTrace.ToString());
            }
            return descr.ToString();
        }

        public static string getCostPrice(string code)
        {
            double price = 0d;
            var list = new List<string>();
            try
            {
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("products/get_cost_price?code="+code);
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    return null;
                }
                price = JsonConvert.DeserializeObject<double>(json.ToString());

            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.StackTrace.ToString());
            }
            return price.ToString();
        }

        public List<string> getItems(string descr)
        {
            var list = new List<string>();
            try
            {
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("products/get_product_descriptions");
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    return null;
                }
                list = JsonConvert.DeserializeObject<List<string>>(json.ToString());
                
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.StackTrace.ToString());
            }

            return list;
        }

        public List<string> getItemDescriptions()
        {
            var list = new List<string>();
            try
            {
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("products/get_product_descriptions");
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    return null;
                }
                list = JsonConvert.DeserializeObject<List<string>>(json.ToString());

            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.StackTrace.ToString());
            }

            return list;
        }
        
    }
}
