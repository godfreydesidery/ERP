using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Windows.Forms;
using System.Diagnostics;

namespace POS.general
{
    class User
    {
        public static string USER_ID = "";
        public static string USERNAME = "";
        public static string FIRST_NAME = "";
        public static string SECOND_NAME = "";
        public static string LAST_NAME = "";
        public static string PASSWORD = "";
        public static string ALIAS = "";
        public static string LOGIN_TIME = "";
        public static string ROLE_ = "";
        public static string accessToken = "";

        public string username;
        public string password;
        public string alias;
        public string access_token = "";

        public static int authenticate(string username, string password)
        {
            //0-Login success
            //1-Login failed due to invalid credentials
            //2-Login failed due to other problems
            User.accessToken = "";
            int auth = 0;
            Object response = new Object();
            JObject json = new JObject();
            try
            {
                response = Web.login(username, password);         
                json = JObject.Parse(response.ToString());
            }
            catch(NullReferenceException)
            {
                MessageBox.Show("Could not log in, invalid username and password", "Error: Login Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return 2;
            }
            catch(Exception)
            {
                MessageBox.Show("Could not log in", "Error: Login Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return 2;
            }
            User user = JsonConvert.DeserializeObject<User>(json.ToString());
            if(user.access_token == null)
            {
                User.accessToken = "";
                MessageBox.Show("Could not log in, invalid username and password", "Error: Login Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                auth = 1;
            }else if(user.access_token == "")
            {
                User.accessToken = "";
                MessageBox.Show("Could not log in, invalid username and password", "Error: Login Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                auth = 1;
            }
            else
            {
                //Create a user session
                auth = 0;
                User.accessToken = user.access_token;
                Object res = new Object();
                JObject jobj = new JObject();
                try
                {
                    var user_ = new JObject();                  
                    res = Web.get_("users/load_user?username="+username);
                    jobj = JObject.Parse(res.ToString());
                    user = JsonConvert.DeserializeObject<User>(jobj.ToString());
                    User.ALIAS = user.alias;
                }
                catch (Exception)
                {
                    User.accessToken = "";
                    MessageBox.Show("Could not log in");
                    return 2;
                }
            }
            return auth;
        }

        public static bool authorize(string priveledge)
        {
            bool response = false;
            try
            {
                response =(bool) Web.get_("authorize?user_id=" + User.USER_ID + "&priveledge=" + priveledge);
                if (response == true)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            catch (Exception)
            {
                response = false;
            }
            return true;
        }
    }
}
