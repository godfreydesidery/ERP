using Newtonsoft.Json.Linq;
using POS.general;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS.gui
{
    public partial class FormAllow : Form
    {
        public static bool allowed = false;

        public FormAllow()
        {
            InitializeComponent();
        }

        private void FormAllow_Load(object sender, EventArgs e)
        {
            allowed = false;
            txtPassword.Focus();
        }

        private void checkAllowed(string uname, string pword)
        {
            // verify managers password

            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.get_("credit_notes/get_crnoteno?code=");
                json = JObject.Parse(response.ToString());
            }
            catch (Exception)
            {
                return;
            }           
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            allowed = false;
            this.Dispose();
        }

        private void txtUsername_KeyDown(object sender, KeyEventArgs e)
        {
            if(e.KeyCode == Keys.Down || e.KeyCode == Keys.Up)
            {
                txtPassword.Focus();
            }
        }

        private void txtPassword_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Down || e.KeyCode == Keys.Up)
            {
                txtUsername.Focus();
            }
        }
    }
}
