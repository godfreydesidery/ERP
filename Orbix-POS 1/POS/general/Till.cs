using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class Till
    {
        public static string TILLNO = "";

        // Till basic information
        public string id { get; set; }
        public string no { get; set; }
        public string name { get; set; }
        public string computerName { get; set; }
        public bool active { get; set; }

        // Till postiotion
        public double cash { get; set; }
        public double voucher { get; set; }
        public double deposit { get; set; }
        public double loyalty { get; set; }
        public double crCard { get; set; }
        public double cheque { get; set; }
        public double cap { get; set; }
        public double invoice { get; set; }
        public double crNote { get; set; }
        public double mobile { get; set; }
        public double other { get; set; }
        // Till float balance
        public double floatBalance { get; set; }

        public static bool tillTotalRegister(string tillNo, double cash, double voucher, double cheque, double deposit, double loyalty, double CRCard, double CAP, double invoice, double CRNote, double mobile)
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
    }
}
