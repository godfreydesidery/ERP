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
    class Receipt
    {
        public string id { get; set; }
        public string no { get; set; }
        public DateTime issueDate { get; set; }
        public string status { get; set; }
        public string notes { get; set; }
        public DateTime printed { get; set; }
        public DateTime rePrinted { get; set; }
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
        public Cart cart { get; set; } = new Cart();
        public Till till { get; set; } = new Till();
        public User createdUser { get; set; } = new User();
        public User reprintedUser { get; set; } = new User();
        public List<ReceiptDetail> receiptDetails { get; set; } = new List<ReceiptDetail>();


        public static Receipt CURRENT_RECEIPT = null;

        public int makeReceipt(string tillNo, string date_)
        {
            int number = 0;

            // get the maximum receipt number for that particular date
            // and increment it by 1
            // to provide a starting point
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
                    return 0;
                }
                return JsonConvert.DeserializeObject<int>(json.ToString());
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }

            return number;
        }
    }
}
