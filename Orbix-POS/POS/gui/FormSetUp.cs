using POS.general;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net.NetworkInformation;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Xml;

namespace POS
{
    public partial class FormSetUp : Form
    {
        public FormSetUp()
        {
            InitializeComponent();
        }

        private void SetupForm_Load(object sender, EventArgs e)
        {
            txtAddress.Text = "";
            txtAddress.Focus();
        }

        private void btnOK_Click(object sender, EventArgs e)
        {
            if(txtAddress.Text == "")
            {
                MessageBox.Show("Please enter server address");
            }else
            {
                if (true)
                {
                    string localSettings = LApplication.localAppDataDir + @"\localSettings.xml";
                    // Create the file if it does not exist
                    try
                    {
                        File.Create(localSettings).Dispose();
                        // Write To the xml file
                        var settings = new XmlWriterSettings();
                        settings.Indent = true;
                        XmlWriter writer = XmlWriter.Create(localSettings, settings);
                        writer.WriteStartElement("Settings");
                        writer.WriteStartElement("Server");
                        writer.WriteStartElement("Address");
                        writer.WriteString(txtAddress.Text);
                        writer.WriteEndElement();
                        writer.WriteEndElement();
                        writer.WriteStartElement("Printer");
                        writer.WriteStartElement("Fiscal_printer");
                        writer.WriteStartElement("Enabled_fiscal");
                        writer.WriteString("True");
                        writer.WriteEndElement();
                        writer.WriteEndElement();
                        writer.WriteStartElement("POS_printer");
                        writer.WriteStartElement("Enabled_POS");
                        writer.WriteString("True");
                        writer.WriteEndElement();
                        writer.WriteEndElement();
                        writer.WriteEndElement();
                        writer.WriteEndElement();
                        writer.Close();
                        // writer.Dispose()
                        Application.Exit();
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show(ex.Message);
                        Application.Exit();
                    }
                }
            }
        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            //this.Visible = false;
            Application.Exit();
        }

        private bool ping(string address)
        {
            bool available = false;
            try
            {
                Ping myPing = new Ping();
                PingReply reply = myPing.Send(address, 1000);
                if (reply != null)
                {                  
                    available = true;
                }else
                {
                    available = false;
                }           
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }

            return available;
        }

        private void btnTestConnection_Click(object sender, EventArgs e)
        {
            if (ping("http://"+txtAddress.Text.ToString()) == true)
            {
                MessageBox.Show("Connected", "Success", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
            else
            {
                MessageBox.Show("Not Connected", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
    }
}
