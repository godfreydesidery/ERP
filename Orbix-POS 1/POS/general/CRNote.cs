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
    class CRNote
    {
        public double getCreditNoteNo(string cRNo)
        {
            double no = 0d;
            try
            {

                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.get_("credit_notes/get_crnoteno?code=" + cRNo);
                    json = JObject.Parse(response.ToString());
                }
                catch (Exception)
                {
                    return 0;
                }
                no = JsonConvert.DeserializeObject<double>(json.ToString());


                
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }

            return no;
        }
    }
}
