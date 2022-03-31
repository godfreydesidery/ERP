using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using POS.general;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS
{
    public partial class FormPayPoint : Form
    {
        private static RawPrinterHelper prn = new RawPrinterHelper();

        public static string posPrinterLogicName = "";
        public static string posCashDrawerLogicName = "";
        public static string posLineDisplayLogicName = "";
        public static bool posPrinterEnabled = false;

        /// <summary>
        /// 'fiscal printer settings
        /// </summary>
        public static string strLogicalName = InstalledPOSDevices.posLogicName;  // get the available fiscal printer logical name
        public static string fiscalPrinterDeviceName = "";
        public static string operatorName = "";
        public static string operatorPassword = "";
        public static string port = "";
        public static string drawer = "";
        public static string fiscalPrinterEnabled = "";



        public static string no = "";
        public static double cash = 0d;
        public static double voucher = 0d;
        public static double deposit = 0d;
        public static double loyalty = 0d;
        public static double CRCard = 0d;
        public static double CAP = 0d;
        public static double invoice = 0d;
        public static double CRNote = 0d;
        public static double mobile = 0d;
        public static double cheque = 0d;
        public static double other = 0d;

        public static double total = 0d;
        public static string cashReceived = "";
        public static string balance = "";

        string MODE = "";
        public static bool paid = false;
        public FormPayPoint()
        {
            InitializeComponent();
        }
        private void button28_Click(object sender, EventArgs e)
        {
            this.Visible = false;
        }
        private void button1_Click(object sender, EventArgs e)
        {
            Receipt.CURRENT_RECEIPT = null;
            double amount =Convert.ToDouble(LCurrency.getValue(txtTotal.Text));
            if (Convert.ToDouble(LCurrency.getValue(txtBalance.Text)) >= 0)
            {
                DialogResult confirm = MessageBox.Show("Total amount payable is " + LCurrency.displayValue(amount.ToString()) + ". Confirm payment?", "Confirm Payment: " + LCurrency.displayValue(amount.ToString()), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
                if (confirm == DialogResult.Yes)
                {                   
                    cashReceived = LCurrency.displayValue(txtAmountReceived.Text);
                    balance = LCurrency.displayValue(txtBalance.Text);
                    // commit payment
                    cash = Convert.ToDouble(LCurrency.getValue(txtCash.Text)) - Convert.ToDouble(LCurrency.getValue(txtBalance.Text));
                    voucher = Convert.ToDouble(LCurrency.getValue(txtVoucher.Text));
                    deposit = Convert.ToDouble(LCurrency.getValue(txtDeposit.Text));
                    loyalty = Convert.ToDouble(LCurrency.getValue(txtLoyalty.Text));
                    CRCard = Convert.ToDouble(LCurrency.getValue(txtCRCard.Text));
                    CAP = Convert.ToDouble(LCurrency.getValue(txtCAP.Text));
                    invoice = Convert.ToDouble(LCurrency.getValue(txtInvoice.Text));
                    CRNote = Convert.ToDouble(LCurrency.getValue(txtCRNote.Text));
                    mobile = Convert.ToDouble(LCurrency.getValue(txtMobile.Text));
                    cheque = Convert.ToDouble(LCurrency.getValue(txtCheque.Text));
                    
                    Receipt.CURRENT_RECEIPT = pay(cash, voucher, deposit, loyalty, CRCard, CAP, invoice, CRNote, mobile);
                    if(Receipt.CURRENT_RECEIPT != null)
                    {
                        this.Dispose();
                    }                
                }                
            }
            else
            {
                MessageBox.Show("Could not process payment. Insufficient funds", "Error: Insufficient funds", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
        private Receipt pay(double cash, double voucher, double deposit, double loyalty, double crCard, double cap, double invoice, double crNote, double mobile)
        {
            bool continue_ = true;
            try
            {
                prn.OpenPrint(posPrinterLogicName);
            }
            catch (Exception)
            {

            }

            if (prn.PrinterIsOpen == false & posPrinterEnabled == true)
            {
                DialogResult res = MessageBox.Show("Could Not connect to POS printer. Continue operation without printing POS receipt?", "Error: POS Printer not available", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
                if (res == DialogResult.Yes)
                {
                    continue_ = true;
                }
                else
                {
                    continue_ = false;
                }
            }

            if(continue_ == true)
            {
                Receipt receipt = new Receipt();
                Payment payment = new Payment();
                payment.cash = cash;
                payment.voucher = voucher;
                payment.deposit = deposit;
                payment.loyalty = loyalty;
                payment.crCard = crCard;
                payment.cap = cap;
                payment.invoice = invoice;
                payment.crNote = crNote;
                payment.mobile = mobile;

                var response = new object();
                var json = new JObject();
                try
                {
                    response = Web.post(payment, "carts/pay?till_no="+Till.TILLNO+"&cart_no="+Cart.NO);
                    json = JObject.Parse(response.ToString());
                    return JsonConvert.DeserializeObject<Receipt>(json.ToString());              
                }
                catch (Exception)
                {
                    return null;
                }
            }            
            return null;
        }               
        private object calculateTotal()
        {
            double totalAmount = Convert.ToDouble(LCurrency.getValue(total.ToString()));
            double totalReceived = 0d;
            double balance;
            double cash;
            double voucher;
            double cheque;
            double deposit;
            double loyalty;
            double CRCard;
            double CAP;
            double invoice;
            double CRNote;
            double mobile;
            cash =Convert.ToDouble(LCurrency.getValue(txtCash.Text));
            voucher = Convert.ToDouble(LCurrency.getValue(txtVoucher.Text));
            cheque = Convert.ToDouble(LCurrency.getValue(txtCheque.Text));
            deposit = Convert.ToDouble(LCurrency.getValue(txtDeposit.Text));
            loyalty = Convert.ToDouble(LCurrency.getValue(txtLoyalty.Text));
            CRCard = Convert.ToDouble(LCurrency.getValue(txtCRCard.Text));
            CAP = Convert.ToDouble(LCurrency.getValue(txtCAP.Text));
            invoice = Convert.ToDouble(LCurrency.getValue(txtInvoice.Text));
            CRNote = Convert.ToDouble(LCurrency.getValue(txtCRNote.Text));
            mobile = Convert.ToDouble(LCurrency.getValue(txtMobile.Text));

            totalReceived = cash + voucher + cheque + deposit + loyalty + CRCard + CAP + invoice + CRNote + mobile;
            txtAmountReceived.Text =(string) LCurrency.displayValue(totalReceived.ToString());
            balance = totalReceived - totalAmount;
            txtBalance.Text = (string)LCurrency.displayValue(balance.ToString());
            return null;
        }
        private void txtCash_KeyDown(object sender, KeyEventArgs e)
        {

        }
        private void txtCash_KeyUp(object sender, KeyEventArgs e)
        {
            // use the plus equal key
            if (e.KeyCode == Keys.Oemplus)
            {
                clearAll();
                txtCash.Text =LCurrency.getValue(txtTotal.Text).ToString();
            }
            if ((bool)validateInput(txtCash.Text) == false)
            {
                txtCash.Text = "";
            }
            calculateTotal();
        }
        private void txtCash_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            clearAll();
            txtCash.Text = (string)LCurrency.getValue(txtTotal.Text);
        }
        private void txtCash_TextChanged(object sender, EventArgs e)
        {
            //if ((bool) validateInput(txtCash.Text) == false)
           // {
                //txtCash.Text = "";
            //}
            //calculateTotal();
        }
        private object clearAll()
        {
            // clears all the fields
            txtCash.Text = "";
            txtVoucher.Text = "";
            txtDeposit.Text = "";
            txtLoyalty.Text = "";
            txtCheque.Text = "";
            txtCRCard.Text = "";
            txtCAP.Text = "";
            txtInvoice.Text = "";
            txtCRNote.Text = "";
            txtMobile.Text = "";
            return null;
        }
        private object validateInput(string input)
        {
            double number = 0d;
            bool valid = false;
            if (double.TryParse(input, out number))
            {
                // parsedNumber is a valid number!
                valid = true;
            }
            
            return valid;
        }
        private void button2_Click(object sender, EventArgs e)
        {
            clearAll();
        }
        private void txtBalance_TextChanged(object sender, EventArgs e)
        {
            if (double.Parse(txtBalance.Text.Replace(",","")) < 0)
            {
                txtValidBalance.Text = "Insufficient tender amount!";
            }
            else
            {
                txtValidBalance.Text = "";
            }
            if(txtBalance.Text == "" || Convert.ToDouble( LCurrency.getValue(txtBalance.Text)) < 0)
            {
                btnPay.Enabled = false;
            }else
            {
                btnPay.Enabled = true;
            }
        }
        private void FormPayPoint_Load(object sender, EventArgs e)
        {
            {
                
            }

        }
        protected virtual object place(string key)
        {
            try
            {
                if (MODE == "CASH")
                {
                    txtCash.Focus();
                    txtCash.SelectionStart = txtCash.Text.Length;
                    txtCash.SelectionLength = 0;
                    SendKeys.Send(key);
                }
            }
            catch (Exception)
            {
            }

            return null;
        }
        private void button21_Click(object sender, EventArgs e)
        {
            place("1");
        }
        private void button19_Click(object sender, EventArgs e)
        {
            place("2");
        }
        private void button3_Click(object sender, EventArgs e)
        {
            place("3");
        }
        private void button4_Click(object sender, EventArgs e)
        {
            place("4");
        }
        private void button5_Click(object sender, EventArgs e)
        {
            place("5");
        }
        private void button6_Click(object sender, EventArgs e)
        {
            place("6");
        }
        private void button7_Click(object sender, EventArgs e)
        {
            place("7");
        }
        private void button8_Click(object sender, EventArgs e)
        {
            place("8");
        }
        private void button9_Click(object sender, EventArgs e)
        {
            place("9");
        }
        private void button11_Click(object sender, EventArgs e)
        {
            place("0");
        }
        private void button15_Click(object sender, EventArgs e)
        {
            place(".");
        }
        private void button13_Click(object sender, EventArgs e)
        {
            place("00");
        }
        private void button12_Click(object sender, EventArgs e)
        {
            place("{BACKSPACE}");
        }
        private void button10_Click(object sender, EventArgs e)
        {
            place("{BACKSPACE}");
        }
        private void button20_Click(object sender, EventArgs e)
        {
            place("{UP}");
        }
        private void button14_Click(object sender, EventArgs e)
        {
            place("{DOWN}");
        }
        private void button18_Click(object sender, EventArgs e)
        {
            place("{LEFT}");
        }
        private void button16_Click(object sender, EventArgs e)
        {
            place("{RIGHT}");
        }
        private void panel1_Paint(object sender, PaintEventArgs e)
        {

        }
        private void FormPayPoint_Shown(object sender, EventArgs e)
        {
            paid = false;

            clearAll();
            txtTotal.Text = LCurrency.displayValue(total.ToString());

            txtCash.Text = "";
            txtVoucher.Text = "";
            txtDeposit.Text = "";
            txtLoyalty.Text = "";
            txtCheque.Text = "";
            txtCRCard.Text = "";
            txtCAP.Text = "";
            txtInvoice.Text = "";
            txtCRNote.Text = "";
            txtMobile.Text = "";
            //totalReceived = cash + voucher + cheque + deposit + loyalty + CRCard + CAP + invoice + CRNote + mobile;
            //txtAmountReceived.Text = (string)LCurrency.displayValue(totalReceived.ToString());
            //balance = totalReceived - totalAmount;
            //txtBalance.Text = (string)LCurrency.displayValue(balance.ToString());

            txtCash.Focus();
        }
    }
}
