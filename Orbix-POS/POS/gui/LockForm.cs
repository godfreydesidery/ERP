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

namespace POS
{
    public partial class LockForm : Form
    {
        public LockForm()
        {
            InitializeComponent();
        }

        private void btnUnlock_Click(object sender, EventArgs e)
        {
            unlock();
        }

        private void btnExit_Click(object sender, EventArgs e)
        {
            DialogResult res = MessageBox.Show("Are you sure you want to Exit the Application?", "Confirmation", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (res == DialogResult.Yes)
            {
                System.Windows.Forms.Application.Exit();
            }      
        }

        private object unlock()
        {
            string username = User.USERNAME;
            string password = User.PASSWORD;
            if (txtUsername.Text == username)// & Hash.check(txtPassword.Text, password) == true)
            {
                this.Dispose();
            }
            else
            {
                MessageBox.Show("Unlock failed. Wrong username and password", "Error: Unlock fail", MessageBoxButtons.OK, MessageBoxIcon.Error);
                txtUsername.Text = "";
                txtPassword.Text = "";
                txtUsername.Focus();
            }

            return null;
        }

        private void txtUsername_Enter(object sender, EventArgs e)
        {
            txtPassword.Text = "";
        }

        private void txtUsername_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                txtPassword.Text = "";
                txtPassword.Focus();
            }

            if (e.KeyCode == Keys.Down | e.KeyCode == Keys.Up)
            {
                txtPassword.Text = "";
                txtPassword.Focus();
            }
        }

        private void txtPassword_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                unlock();
            }

            if (e.KeyCode == Keys.Down | e.KeyCode == Keys.Up)
            {
                txtPassword.Text = "";
                txtUsername.Focus();
            }
        }
    }
}
