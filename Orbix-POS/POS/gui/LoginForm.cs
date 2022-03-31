using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using POS.general;
using System.IO;

namespace POS
{
    public partial class LoginForm : Form
    {
        public LoginForm()
        {
            InitializeComponent();
        }

        private void button28_Click(object sender, EventArgs e)
        {
            System.Windows.Forms.Application.Exit();
        }

        private void btnLogin_Click(object sender, EventArgs e)
        {
            Cursor = Cursors.WaitCursor;     
            if (User.authenticate(txtUsername.Text, txtPassword.Text) == 0)
            {
                Cursor = Cursors.Default;
                MainForm form = new MainForm();
                form.Show();
                this.Close();             
            }
            else
            {
                Cursor = Cursors.Default;
                MessageBox.Show("Invalid Username and Password", "Error: Login failed", MessageBoxButtons.OK, MessageBoxIcon.Error);
                txtUsername.Focus();
            }
            Cursor = Cursors.Default;
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            Application.Exit();
            Environment.Exit(0);
        }

        private void LoginForm_Load(object sender, EventArgs e)
        {
            global::System.String path = LApplication.localAppDataDir;
            // create a directory

            if ((!Directory.Exists(path)))
            {
                Directory.CreateDirectory(path);
                serverSettings();
                this.Close();
                return;
            }

            try
            {
                var app = new LApplication();
                app.loadSettings();
            }
            catch (global::System.Exception)
            {
                MessageBox.Show("Failed to load application",  "Error: Application", MessageBoxButtons.OK, MessageBoxIcon.Error);
                Application.Exit();
                Environment.Exit(0);
            }
        }
    

        private void txtUsername_KeyDown(object sender, KeyEventArgs e)
        {
            if (((e.KeyCode) == (Keys.Down)))
            {
                txtPassword.Focus();
            }
        }

        private void txtPassword_Enter(object sender, EventArgs e)
        {
            txtPassword.Text = "";
        }

        private void txtPassword_KeyDown(object sender, KeyEventArgs e)
        {
            if (((e.KeyCode) == (Keys.Up)))
            {
                txtUsername.Focus();
            }
        }

        private void serverSettings()
        {
            FormSetUp form = new FormSetUp();
            form.Show();
        }
    }
}
