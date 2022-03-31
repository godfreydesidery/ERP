using Newtonsoft.Json;
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

namespace POS
{
    public partial class FormPettyCash : Form
    {
        public FormPettyCash()
        {
            InitializeComponent();
        }
        
        private void button28_Click(object sender, EventArgs e)
        {
            this.Visible = false;
        }

        private void txtAmount_TextChanged(object sender, EventArgs e)
        {
            try
            {
                double amount = Convert.ToDouble(LCurrency.getValue(txtAmount.Text));
                if (amount <= 0)
                {
                    txtAmount.Text = "";
                    txtDescription.Text = "";
                    btnOK.Enabled = false;
                }
                else
                {
                    btnOK.Enabled = true;
                }
            }
            catch (Exception)
            {
                txtAmount.Text = "";
                txtDescription.Text = "";
                btnOK.Enabled = false;
            }
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            txtAmount.Text = "";
            txtDescription.Text = "";
        }
        
        private double getCurrentFunds()
        {
            double available = 0;
            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.get_("tills/get_by_till_no?till_no=" + Till.TILLNO);
                json = JObject.Parse(response.ToString());
                Till till_ = JsonConvert.DeserializeObject<Till>(json.ToString());
                available = till_.cash + till_.floatBalance;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }           
            return available;
        }
        
        private void btnOK_Click(object sender, EventArgs e)
        {
            double amount = Convert.ToDouble(LCurrency.getValue(txtAmount.Text));
            string details = txtDescription.Text;
            if(details == "")
            {
                MessageBox.Show("Please enter petty cash details", "Error: Required information missing", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            if (getCurrentFunds() < amount)
            {
                MessageBox.Show("Could not complete operation. Insufficient funds available.", "Error: Insufficient Funds", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            DialogResult res = MessageBox.Show("Petty Cash amount: " + LCurrency.displayValue(txtAmount.Text) + " Confirm?", "Confirm Petty Cash", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (res == DialogResult.Yes)
            {
                PettyCash pettyCash = new PettyCash();
                pettyCash.amount = Convert.ToDouble(amount);
                pettyCash.details = details;
                pettyCash.till.no = Till.TILLNO;
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.post(pettyCash, "tills/petty_cash?no=" + Till.TILLNO);
                    MessageBox.Show("Petty Cash registered successifully");
                    Dispose();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.ToString());
                    return;
                }
            }          
        }
    }
}
