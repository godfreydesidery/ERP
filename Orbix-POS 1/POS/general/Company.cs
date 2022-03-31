using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS.general
{
    class Company
    {
        public static object NAME = "";
        public static object CONTACT_NAME = "";
        public static object TIN = "";
        public static object VRN = "";
        public static object BANK_ACC_NAME = "";
        public static object BANK_ACC_ADDRESS = "";
        public static object BANK_POST_CODE = "";
        public static object BANK_NAME = "";
        public static object BANK_ACC_NO = "";
        public static object POST_ADDRESS = "";
        public static object POST_CODE = "";
        public static object PHYSICAL_ADDRESS = "";
        public static object TELEPHONE = "";
        public static object MOBILE = "";
        public static object EMAIL = "";
        public static object WEBSITE = "";
        public static object FAX = "";

        public static bool loadCompanyDetails()
        {
            bool loaded = false;
            var response = new object();
            var json = new JObject();
            try
            {
                
                response = Web.get_("company_profile/get");

                json = JObject.Parse(response.ToString());
                
                NAME = json.SelectToken("companyName").ToString();
                CONTACT_NAME = json.SelectToken("contactName").ToString();
                TIN = json.SelectToken("tin").ToString();
                VRN = json.SelectToken("vrn").ToString();
                BANK_ACC_NAME = json.SelectToken("bankAccountName").ToString();
                BANK_ACC_ADDRESS = json.SelectToken("bankPhysicalAddress").ToString();
                BANK_POST_CODE = json.SelectToken("bankPostCode").ToString();
                BANK_NAME = json.SelectToken("bankName").ToString();
                BANK_ACC_NO = json.SelectToken("bankAccountNo").ToString();
                POST_ADDRESS = json.SelectToken("postAddress").ToString();
                POST_CODE = json.SelectToken("postCode").ToString();
                PHYSICAL_ADDRESS = json.SelectToken("physicalAddress").ToString();
                TELEPHONE = json.SelectToken("telephone").ToString();
                MOBILE = json.SelectToken("mobile").ToString();
                EMAIL = json.SelectToken("email").ToString();
                WEBSITE = json.SelectToken("website").ToString();
                FAX = json.SelectToken("fax").ToString();
                loaded = true;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString(), "");
                return false;
            }
            return loaded;
        }
    }
}
