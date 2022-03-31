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
    public partial class FormFiscalPrinter : Form
    {
        public FormFiscalPrinter()
        {
            InitializeComponent();
        }

        private void btnSave_Click(object sender, EventArgs e)
        {
            if (save(txtPrinterName.Text, txtOperatorCode.Text, txtOperatorPassword.Text) == true)
            {
            }
            else
            {
                MessageBox.Show("Operation Failed", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
        private bool save(string name, string operatorCode, string operatorPassword)
        {
            bool saved = false;
            // save printer information

            return saved;
        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            this.Visible = false;
        }
    }
}
