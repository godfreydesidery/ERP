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
    class Inventory : Product
    {
        public double GL_MIN_INVENTORY = 0d;
        public double GL_MAX_INVENTORY = 0d;
        public double GL_REORDER_LEVEL = 0d;
        public double GL_DEFAULT_REORDER_QTY = 0d;

        public string getInventory(string code)
        {
            string value = "";
            try
            {

                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("products/get_inventory?code=" + code);
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    return null;
                }
                value = JsonConvert.DeserializeObject<string>(json.ToString());
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }

            return value;
        }

        public bool adjustInventory(string code, double value)
        {
            bool success = false;
            string actValue = value.ToString();
            try
            {
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("credit_notes/get_crnoteno?code=" + code);
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
                MessageBox.Show(ex.Message);
            }

            return success;
        }

        public bool addInventory(string itemCode, double minInventory, double maxInventory, double reorderLevel, double defReorderQty)
        {
            bool added = false;
            return added;
        }

        public bool editInventory(string itemCode, double minInventory, double maxInventory, double reorderLevel, double defReorderQty)
        {
            bool edited = false;
            return edited;
        }
    }
}
