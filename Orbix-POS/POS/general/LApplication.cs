using Newtonsoft.Json.Linq;
using POS;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Xml;

namespace POS.general
{
    class LApplication
    {     
        private static object getRemoteXMLFile(string path)
        {           
            var document = new System.Xml.XmlDocument();
            // Create a WebRequest to the remote site
            System.Net.HttpWebRequest request = (System.Net.HttpWebRequest) System.Net.WebRequest.Create(path);

            // NB! Use the following line ONLY if the website is protected
            // request.Credentials = New System.Net.NetworkCredential("username", "password")

            // Call the remote site, and parse the data in a response object
            System.Net.HttpWebResponse response = (System.Net.HttpWebResponse)request.GetResponse();

            // Check if the response is OK (status code 200)
            if (response.StatusCode == System.Net.HttpStatusCode.OK)
            {
                // Parse the contents from the response to a stream object
                Stream stream = response.GetResponseStream();
                // Create a reader for the stream object
                var reader = new StreamReader(stream);
                // Read from the stream object using the reader, put the contents in a string
                string contents = reader.ReadToEnd();
                // Create a new, empty XML document

                // Load the contents into the XML document
                document.LoadXml(contents);
            }

            // Now you have a XmlDocument object that contains the XML from the remote site, you can
            // use the objects and methods in the System.Xml namespace to read the document

            else
            {
                // If the call to the remote site fails, you'll have to handle this. There can be many reasons, ie. the 
                // remote site does not respond (code 404) or your username and password were incorrect (code 401)
                // 
                // See the codes in the System.Net.HttpStatusCode enumerator 

                throw new Exception("Could not retrieve document from the URL, response code: " + response.StatusCode);
            }
            return document;
        }
        public static string localAppDataDir = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments)+"Orbix-POS";

        public Boolean loadSettings()
        {
            string computerName = "";
            try
            {
                computerName = System.Environment.MachineName;
            }
            catch (Exception)
            {
            }

            bool loaded = false;
            string path = localAppDataDir + @"\localSettings.xml";
            XmlReader document;
            try
            {

                if (File.Exists(path))
                {

                    document = new XmlTextReader(path);
                    while (document.Read())
                    {
                        var type = document.NodeType;
                        if (type == XmlNodeType.Element)
                        {
                            if (document.Name == "Address")
                            {
                                Env.SERVER_ADDRESS = document.ReadInnerXml().ToString();
                            }
                        }
                    }
                    document.Dispose();
                }
                else
                {
                    DialogResult res = MessageBox.Show("Could not load settings. Settings configurations not found. Configure System?", "Settings not found", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
                    if (res == DialogResult.Yes)
                    {
                        FormSetUp frmSetup = new FormSetUp();
                        frmSetup.ShowDialog();
                    }
                    Application.Exit();
                }
            }
            catch (Exception)
            {
                DialogResult res = MessageBox.Show("Could not load settings. Settings configurations not found. Configure System?", "Settings not found", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
                if (res == DialogResult.Yes)
                {
                    FormSetUp frmSetup = new FormSetUp();
                    frmSetup.ShowDialog();
                }
                MessageBox.Show("Could not load System. Application will close", "Error: Load failed", MessageBoxButtons.OK, MessageBoxIcon.Error);
                Application.Exit();
                Environment.Exit(0);
            }
            //Now load till information          
            try
            {
                var response = new object();
                var json = new JObject();
                response = Web.get_("tills/get_by_computer_name?computer_name=" + System.Environment.MachineName);
                json = JObject.Parse(response.ToString());
                Till.TILLNO = (string)json.SelectToken("no");
                //PointOfSale.operatorName = reader.GetString("operator_code");
                //PointOfSale.operatorPassword = reader.GetString("operator_password");
                //PointOfSale.port = reader.GetString("port");
                //PointOfSale.fiscalPrinterEnabled = reader.GetString("status");
            }
            catch (Exception)
            {
                MessageBox.Show("Could not load Till. Application will close", "Error: Loading till failed", MessageBoxButtons.OK, MessageBoxIcon.Error);
                Application.Exit();
            }
            // load day information
            try
            {
                Day.bussinessDate = Day.getCurrentDay().ToString("yyyy-MM-dd"); // settings.SelectSingleNode("Settings/Day/Date").InnerText
            }
            catch (Exception)
            {
                MessageBox.Show("Could not load Day Information. Day not set. Application will close", "Error: Day error");
                Application.Exit();
                loaded = false;
            }

            loaded = true;
            if (Company.loadCompanyDetails() == true)
            {
                //Company loaded
            }
            else
            {
                MessageBox.Show("Could not load company information. Application will close", "Error: Loading company information");
                loaded = false;
                Application.Exit();
            }
            return loaded;
        }
    }
}
