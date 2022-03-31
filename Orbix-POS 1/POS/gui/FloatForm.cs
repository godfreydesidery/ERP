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
    public partial class FloatForm : Form
    {
        double currentFloat = 0d;
        double addFloat = 0d;
        double newFloat = 0d;

        public FloatForm()
        {
            InitializeComponent();
        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            Visible = false;
        }

        private void FloatForm_Load(object sender, EventArgs e)
        {
            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.get_("tills/get_by_till_no?till_no=" + Till.TILLNO);
                json = JObject.Parse(response.ToString());
                Till till_ = JsonConvert.DeserializeObject<Till>(json.ToString());
                currentFloat = till_.floatBalance;
                txtCurrentFloat.Text = LCurrency.displayValue(currentFloat.ToString());
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }
        }

        private void txtAdd_Enter(object sender, EventArgs e)
        {
            txtDeduct.Text = "";
        }

        private void txtAdd_TextChanged(object sender, EventArgs e)
        {
            try
            {
                double amount = Convert.ToDouble(LCurrency.getValue(txtAdd.Text));
                if (amount <= 0)
                {
                    txtAdd.Text = "";
                    txtNewFloat.Text = "";
                }
                else
                {
                    newFloat = currentFloat + Convert.ToDouble(amount);
                    txtNewFloat.Text = LCurrency.displayValue(newFloat.ToString());
                }
            }
            catch (Exception)
            {
                txtAdd.Text = "";
                txtNewFloat.Text = "";
            }
                   
        }

        private void txtNewFloat_TextChanged(object sender, EventArgs e)
        {
            if (txtNewFloat.Text == "")
            {
                btnOK.Enabled = false;
            }
            else
            {
                btnOK.Enabled = true;
            }
        }
        private void btnOK_Click(object sender, EventArgs e)
        {
            double add = Convert.ToDouble(LCurrency.getValue(txtAdd.Text));
            double deduct = Convert.ToDouble(LCurrency.getValue(txtDeduct.Text));
            if (add > 0 && deduct > 0)
            {
                MessageBox.Show("Both values must not be more than zero", "Error: Invalid Entry", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            if (add == 0 && deduct == 0)
            {
                MessageBox.Show("Both values must not be zero", "Error: Invalid Entry", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            if (add < 0 || deduct < 0)
            {
                MessageBox.Show("Neither value should be less than zero", "Error: Invalid Entry", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            if (deduct > 0)
            {
                if(getCurrentFloat() < deduct)
                {
                    MessageBox.Show("Could not process. Insufficient float balance", "Error: Insufficient Balance", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
            }
            DialogResult res;
            if(add > 0)
            {
                deduct = 0;
                res = MessageBox.Show("Add float : " + LCurrency.displayValue(txtAdd.Text) + " Confirm?", "Confirm Float addition", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            }else
            {
                add = 0;
                res = MessageBox.Show("Deduct float : " + LCurrency.displayValue(txtDeduct.Text) + " Confirm?", "Confirm Float deduction", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            }
            if (res == DialogResult.Yes)
            {
                Float float_ = new Float();
                float_.addition = add;
                float_.deduction = deduct;
                float_.till.no = Till.TILLNO;
                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.post(float_, "tills/float?no=" + Till.TILLNO);
                    MessageBox.Show("Float registered successifully");
                    Dispose();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.ToString());
                    return;
                }
            }           
        }
        private double getCurrentFloat()
        {
            double available = 0;
            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.get_("tills/get_by_till_no?till_no=" + Till.TILLNO);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.ToString());
            }
            json = JObject.Parse(response.ToString());
            Till till_ = JsonConvert.DeserializeObject<Till>(json.ToString());
            available = till_.floatBalance;
            return available;
        }
        private void btnCancel_Click(object sender, EventArgs e)
        {
            txtAdd.Text = "";
        }
        private void txtDeduct_Enter(object sender, EventArgs e)
        {
            txtAdd.Text = "";
        }
        private void txtDeduct_TextChanged(object sender, EventArgs e)
        {

            try
            {
                double amount = Convert.ToDouble(LCurrency.getValue(txtDeduct.Text));
                if (amount <= 0 || amount > currentFloat)
                {
                    txtDeduct.Text = "";
                    txtNewFloat.Text = "";
                }
                else
                {
                    newFloat = currentFloat - Convert.ToDouble(amount);
                    txtNewFloat.Text = LCurrency.displayValue(newFloat.ToString());
                }
            }
            catch (Exception)
            {
                txtDeduct.Text = "";
                txtNewFloat.Text = "";
            }            
        }
    }
}
