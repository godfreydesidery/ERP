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
    class Payment
    {
        public double cash;
        public double voucher;
        public double deposit;
        public double loyalty;
        public double crCard;
        public double cap;
        public double invoice;
        public double crNote;
        public double mobile;       
        public static bool commitPayment(string paymentId)
        {
            bool commited = false;
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
                MessageBox.Show(ex.Message);
            }
            return commited;
        }
    }
}
