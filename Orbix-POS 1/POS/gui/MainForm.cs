using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using POS.general;
using POS.gui;
using POS.Properties;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS
{
    public partial class MainForm : Form
    {
        Cart cart = null;

        string barCode = "";
        string code = "";
        string description = "";
        double costPriceVatExcl = 0;
        double costPriceVatIncl = 0;
        double sellingPriceVatExcl = 0;
        double sellingPriceVatIncl = 0;
        double discount = 0;
        double vat = 0;
        double qty = 0;
        double amount = 0;
        bool voided = false;
        string sn = "";

        bool allowVoid = false;
        int seq = 0;

        int voidRow = -1;

        public MainForm()
        {
            InitializeComponent();
        }
        private void frmMain_Load(object sender, EventArgs e)
        {
            lblAlias.Text = POS.general.Day.bussinessDate + " Welcome " + User.ALIAS;            
            if (User.authorize("SELLING"))
            {
                dtgrdProductList.Enabled = true;               
                btnPay.Enabled = true;
            }
            var product = new Product();
            longList = product.getDescriptions();
            cart = loadCart(Till.TILLNO);
            if (cart  == null)
            {
                cart = createCart(Till.TILLNO);
            }
            displayCart(cart);           
        }
        private void button19_Click(object sender, EventArgs e)
        {
            try
            {
                if (dtgrdProductList.RowCount > 0 & isAllVoid() == false)
                {
                    calculateValues(cart);
                    FormPayPoint frmPayPoint = new FormPayPoint();
                    FormPayPoint.total = Convert.ToDouble(LCurrency.getValue(txtGrandTotal.Text)); //String.Format("{0:0.00}", txtGrandTotal.Text);
                    frmPayPoint.ShowDialog();                
                    if (FormPayPoint.paid == true)
                    {
                        Receipt receipt = Receipt.CURRENT_RECEIPT;
                        printReceipt(receipt, FormPayPoint.cash, Convert.ToDouble(LCurrency.getValue(FormPayPoint.balance)));
                   
                        allowVoid = false;
                        if (dtgrdProductList.RowCount == 1)
                        {
                            txtId.Text = "";
                        }            
                    }               
                    cart = loadCart(Till.TILLNO);
                    if (cart == null)
                    {
                        cart = createCart(Till.TILLNO);
                    }
                    displayCart(cart);
                }
            }
            catch (Exception)
            {
                
            }
            txtGrandTotal.Text = "";
            txtTotal.Text = "";
            txtTax.Text = "";
            txtDiscount.Text = "";        
        }
        private void button21_Click(object sender, EventArgs e)
        {
            CashPickUpForm form = new CashPickUpForm();
            form.ShowDialog();
        }
        private void button22_Click(object sender, EventArgs e)
        {
            FormPettyCash form = new FormPettyCash();
            form.ShowDialog();
        }
        private void btnFloat_Click(object sender, EventArgs e)
        {
            FloatForm form = new FloatForm();
            form.ShowDialog();
        }
        private void btnLock_Click(object sender, EventArgs e)
        {
            DialogResult res = MessageBox.Show("Lock System? You will be required to enter username and password to unlock, or login afresh", "Lock System?", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (res == DialogResult.Yes)
            {
                LockForm form = new LockForm();
                form.ShowDialog();
            }           
        }
        private void btnXReport_Click(object sender, EventArgs e)
        {
            DialogResult res = MessageBox.Show("Print X-Report?", "X-Report: Print?", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if(res == DialogResult.Yes)
            {
                //Print X-Report
            }
            //XReportForm form = new XReportForm();
            //form.ShowDialog();
        }
        private void btnZReport_Click(object sender, EventArgs e)
        {

            DialogResult res = MessageBox.Show("Print Z-Report?", "Z-Report: Print?", MessageBoxButtons.YesNo, MessageBoxIcon.Question);
            if (res == DialogResult.Yes)
            {
                //Print X-Report
            }
            //ZReportForm form = new ZReportForm();
            //form.ShowDialog();
        }
        protected virtual object place(string key)
        {
            try
            {
                int row = dtgrdProductList.CurrentCell.RowIndex;
                int col = dtgrdProductList.CurrentCell.ColumnIndex;
                if (dtgrdProductList.CurrentCell.ColumnIndex == 0 | dtgrdProductList.CurrentCell.ColumnIndex == 1 | dtgrdProductList.CurrentCell.ColumnIndex == 5)
                {
                    dtgrdProductList.Select();
                    dtgrdProductList.CurrentCell = dtgrdProductList[col, row];
                    string val = dtgrdProductList[col, row].Value.ToString();                  
                    SendKeys.Send(val + key);
                }
                else
                {
                    dtgrdProductList.Select();
                    dtgrdProductList.CurrentCell = dtgrdProductList[col, row];
                    SendKeys.Send(key);
                }
            }
            catch (Exception)
            {
                try
                {
                    SendKeys.Send(key);
                }catch(Exception ex)
                {
                    MessageBox.Show(ex.ToString());
                }                
            }
            return null;
        }
        private void dtgrdProductList_CellEndEdit(object sender, DataGridViewCellEventArgs e)
        {
            if(dtgrdProductList.CurrentCell.ColumnIndex == 6)
            {
                double qty = 0;
                string sn = "";
                try
                {
                    qty = Convert.ToDouble(dtgrdProductList[6, e.RowIndex].Value);
                    sn = (string)dtgrdProductList[9, e.RowIndex].Value;
                }
                catch (Exception)
                {

                }
                if (Convert.ToDouble(dtgrdProductList[6, e.RowIndex].Value) >= 0 & Convert.ToDouble( dtgrdProductList[6, e.RowIndex].Value) <= 1000 & (string)dtgrdProductList[9, e.RowIndex].Value != "")
                {
                    if (qty > 0)
                    {
                        updateQty(qty, sn);
                    }
                    else
                    {
                        updateQty(1, sn);
                    }               
                }
                else if (Convert.ToDouble(dtgrdProductList[6, e.RowIndex].Value) <= 0 & (string)dtgrdProductList[1, e.RowIndex].Value == "")
                {
                    MessageBox.Show("Invalid quantity value. Quantity value should be between 1 and 1000", "Error: Invalid Entry", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    dtgrdProductList[6, e.RowIndex].Value = 1;
                }
                cart = loadCart(Till.TILLNO);
                calculateValues(cart);
            }
            
        }
        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            if (keyData == Keys.Enter)
            {
                dtgrdProductList.EndEdit();  
                if(dtgrdProductList.CurrentCell.ColumnIndex == 0 || dtgrdProductList.CurrentCell.ColumnIndex == 1)
                {            
                    search();
                }                
                return true;
            }
             //return MyBase.ProcessCmdKey(msg, keyData)
            return false;
        }
        private void search()
        {
            try
            {
                int row = dtgrdProductList.CurrentCell.RowIndex;
                int col = dtgrdProductList.CurrentCell.ColumnIndex;
                if (col == 0 & row == dtgrdProductList.RowCount - 2)
                {
                    string value = "";
                    bool search = true;
                    try
                    {
                        value = dtgrdProductList[col, row].Value.ToString();
                        if (string.IsNullOrEmpty(value) | value == "0")
                        {
                            search = false;
                            return;
                        }
                    }
                    catch (Exception)
                    {
                        search = false;
                    }

                    if (search == true)
                    {
                        bool found = searchByBarcode(value, 1);
                        if (found == true)
                        {
                            // item found
                            if (SaleSequence.multiple == true)
                            {
                                for (int i = 0; i <= 6; i++)
                                {
                                    SendKeys.Send("{right}");
                                }
                            }
                            else
                            {
                                SendKeys.Send("{down}");
                            }
                        }
                        else if (found == false)
                        {
                            MessageBox.Show("Product not found", "Error: Not Found", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        }
                    }
                }
                else if (col == 6 & row == dtgrdProductList.RowCount - 2)
                {
                    for (int i = 0; i <= 6; i++)
                    {
                        // SendKeys.Send("{left}")
                    }
                    // SendKeys.Send("{down}")
                }

                if (col == 1 & row == dtgrdProductList.RowCount - 2)
                {
                    // search item
                    // add item to list
                    string value = "";
                    bool search = true;
                    try
                    {
                        value = dtgrdProductList[col, row].Value.ToString();
                        if (string.IsNullOrEmpty(value) | value == "0")
                        {
                            search = false;
                            return;
                        }
                    }
                    catch (Exception)
                    {
                        search = false;
                    }

                    if (search == true)
                    {
                        bool found = searchByCode(value, 1);
                        if (found == true)
                        {
                            // item found
                            if (SaleSequence.multiple == true)
                            {
                                for (int i = 0; i <= 4; i++)
                                {
                                    SendKeys.Send("{right}");
                                }
                            }
                            else
                            {
                                SendKeys.Send("{down}");
                                SendKeys.Send("{left}");
                            }
                        }
                        else if (found == false)
                        {
                            MessageBox.Show("Product not found", "Error: Not Found", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        }
                    }
                }
                else if (col == 6 & row == dtgrdProductList.RowCount - 2)
                {
                    for (int i = 0; i <= 4; i++)
                    {
                        SendKeys.Send("{left}");
                    }
                    SendKeys.Send("{down}");
                }

                if (col == 2 & row == dtgrdProductList.RowCount - 2)
                {
                    // search item
                    // add item to list
                    string value = "";
                    bool search = true;
                    try
                    {
                        value = dtgrdProductList[col, row].Value.ToString();
                        if (string.IsNullOrEmpty(value))
                        {
                            search = false;
                            return;
                        }
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show(ex.StackTrace);
                        search = false;
                    }
                    if (search == true)
                    {
                        bool found = searchByDescription(value, 1);
                        if (found == true)
                        {
                            // item found
                            if (SaleSequence.multiple == true)
                            {
                                for (int i = 0; i <= 4; i++)
                                {
                                    SendKeys.Send("{right}");
                                }
                            }
                            else
                            {
                                SendKeys.Send("{down}");
                                SendKeys.Send("{left}");
                                SendKeys.Send("{left}");
                            }
                        }
                        else if (found == false)
                        {
                            MessageBox.Show("Product not found", "Error: Not Found", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        }
                    }
                }
                else if (col == 6 & row == dtgrdProductList.RowCount - 2)
                {
                    for (int i = 0; i <= 4; i++)
                    {
                        SendKeys.Send("{left}");
                    }
                    SendKeys.Send("{down}");
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
            dtgrdProductList.Select();
        }

        private bool searchByBarcode(string barcode, int q)
        {
            return search(barcode, "", "", q);
        }

        private bool searchByCode(string code, int q)
        {
            return search("", code, "", q);
        }

        private bool searchByDescription(string description, int q)
        {
            return search("", "", description, q);
        }
        private bool search(string barcode, string code, string description, double q)
        {
            bool found = false;
            var response = new object();
            var json = new JObject();
            int row = 0;
            try
            {
                if (!string.IsNullOrEmpty(barcode))
                {
                    response = Web.get_("products/get_by_barcode?barcode=" + barcode);
                }
                else if (!string.IsNullOrEmpty(code))
                {
                    response = Web.get_("products/get_by_code?code=" + code);
                }
                else
                {
                    response = Web.get_("products/get_by_description?description=" + description);
                }
                json = JObject.Parse(response.ToString());
                Product product = JsonConvert.DeserializeObject<Product>(json.ToString());
                barcode = product.primary;
                code = product.code;
                description = product.description;
                discount = product.discount;
                vat = product.vat;
                qty = q;
                costPriceVatExcl = product.costPriceVatExcl;
                costPriceVatIncl = product.costPriceVatIncl;
                sellingPriceVatExcl = product.sellingPriceVatExcl;
                sellingPriceVatIncl = product.sellingPriceVatIncl;
                amount = (double) qty * sellingPriceVatIncl * (1 - discount / 100);
                sn = "";
                found = true;
                if (string.IsNullOrEmpty(code))
                {
                    found = false;
                    cart = loadCart(Till.TILLNO);
                    displayCart(cart);
                }
                try
                {
                    row = dtgrdProductList.CurrentCell.RowIndex;
                }
                catch (Exception)
                {
                    dtgrdProductList.Rows.Add();
                    row = dtgrdProductList.CurrentCell.RowIndex;
                }
                if (found == true)
                {
                    dtgrdProductList[0, row].Value = barcode;
                    dtgrdProductList[1, row].Value = code;
                    dtgrdProductList[2, row].Value = description;
                    dtgrdProductList[3, row].Value = LCurrency.displayValue(sellingPriceVatIncl.ToString());
                    dtgrdProductList[4, row].Value = LCurrency.displayValue(discount.ToString());
                    dtgrdProductList[6, row].Value = "";
                    dtgrdProductList[6, row].Value = qty;
                    dtgrdProductList[7, row].Value = LCurrency.displayValue(amount.ToString());
                    dtgrdProductList[8, row].Value = false;
                    dtgrdProductList[0, row].ReadOnly = true;
                    dtgrdProductList[1, row].ReadOnly = true;
                    dtgrdProductList[2, row].ReadOnly = true;
                    seq = seq + 1;
                    addToCart(barCode, code, description, costPriceVatExcl, costPriceVatIncl, sellingPriceVatExcl, sellingPriceVatIncl, discount, vat, qty, amount);
                    if (dtgrdProductList.RowCount > 1)
                    {
                        if ((double)dtgrdProductList[6, row - 1].Value > 1)
                        {
                            SaleSequence.multiple = true;
                        }
                        else
                        {
                            SaleSequence.multiple = false;
                        }
                    }
                }
                else
                {
                    cart = loadCart(Till.TILLNO);
                    displayCart(cart);
                }
            }
            catch (Exception)
            {
                //MessageBox.Show(ex.ToString());
                dtgrdProductList[0, row].Value = "";
                dtgrdProductList[1, row].Value = "";
                dtgrdProductList[2, row].Value = "";
                dtgrdProductList[3, row].Value = "";
                dtgrdProductList[4, row].Value = "";
                dtgrdProductList[5, row].Value = "";
                dtgrdProductList[6, row].Value = "";
                dtgrdProductList[7, row].Value = "";
                dtgrdProductList[8, row].Value = false;
                dtgrdProductList[8, row].Value = "";
                dtgrdProductList.EndEdit();
            }
            dtgrdProductList.EndEdit();
            cart = loadCart(Till.TILLNO);
            displayCart(cart);
            return found;
        }
        
        private bool allow = false;
        
        private void dtgrdProductList_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            int row = -1;
            int col = -1;
            try
            {
                row = dtgrdProductList.CurrentRow.Index;
                col = dtgrdProductList.CurrentCell.ColumnIndex;
            }
            catch (Exception)
            {
                row = -1;
            }
            dtgrdProductList.EndEdit();
            if (dtgrdProductList.CurrentCell.ColumnIndex == 8 && dtgrdProductList.CurrentCell.RowIndex != dtgrdProductList.RowCount - 1)
            {
                string sn = dtgrdProductList[9, dtgrdProductList.CurrentCell.RowIndex].Value.ToString();
                cart = loadCart(Till.TILLNO);
                
                if ((bool)dtgrdProductList[8, dtgrdProductList.CurrentCell.RowIndex].Value == false)
                {
                    _void(sn);
                }
                else
                {
                    unvoid(sn);
                }                                         
            }
            cart = loadCart(Till.TILLNO);
            displayCart(cart);
        }

        FormDiscount discountDialog;
        private void dtgrdProductList_CellClick(object sender, DataGridViewCellEventArgs e)
        {


            cellValueChanged();
            int row = -1;
            int col = -1;
            double amount = 0;
            string product = "";
            double unitPrice = 0;
            try
            {
                row = dtgrdProductList.CurrentRow.Index;
                col = dtgrdProductList.CurrentCell.ColumnIndex;
                amount = Convert.ToDouble(LCurrency.getValue(dtgrdProductList[3, row].Value.ToString()));
                product = "(" + dtgrdProductList[1, row].Value.ToString() + ")  " + dtgrdProductList[2, row].Value.ToString();
                unitPrice =Convert.ToDouble( LCurrency.getValue(dtgrdProductList[3, row].Value.ToString()));
            }
            catch (Exception)
            {
                row = -1;
            }
            dtgrdProductList.EndEdit();
            if (dtgrdProductList.CurrentCell.ColumnIndex == 4)
            {
                string sn = (string) dtgrdProductList[9, dtgrdProductList.CurrentCell.RowIndex].Value;
                cart = loadCart(Till.TILLNO);

                discountDialog = new FormDiscount();
                discountDialog.product = product;
                discountDialog.unitPrice = "Unit Price " + unitPrice;
                discountDialog.ShowDialog();
                if (discountDialog.DialogResult == System.Windows.Forms.DialogResult.OK)
                {
                    double discount = FormDiscount.discount;
                    if ((discount) <= amount)
                    {
                        updateDiscount(sn, (discount) / amount * 100);
                    }
                    else
                    {
                        MessageBox.Show("Invalid Discount Amount. Discount should be less than Unit Price", "Error: Invalid Entry", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    }

                    discountDialog.Dispose();
                }
                else
                {
                    discountDialog.Dispose();
                }
                cart = loadCart(Till.TILLNO);
                displayCart(cart);
            }         
        }
        private void updateDiscount(string sn, double discount)
        {
            var detail = new CartDetail();
            detail.id = sn;
            detail.discount = discount;
            Web.post(detail, "carts/update_discount");
        }
        private int RECEIPT_NO0 = 0;

        private bool isAllVoid()
        {
            bool allVoid = true;
            for (int i = 0; i < cart.cartDetails.Count; i++)
            {
                if (cart.cartDetails[i].voided == false)
                {
                    allVoid = false;
                }
            }
            return allVoid;
        }       
        private void printReceipt(Receipt receipt, double tender, double balance)
        {
            int size = -1;
            for (int i = 0; i < receipt.receiptDetails.Count; i++)
            {              
                size = size + 1;
            }
            var code = new string[size + 1];
            var descr = new string[size + 1];
            var qty = new string[size + 1];
            var price = new string[size + 1];
            var tax = new string[size + 1];
            var amount = new string[size + 1];
            string subTotal = txtTotal.Text;
            string totalVat = txtTax.Text;
            string total = txtGrandTotal.Text;
            string discountRatio = txtDiscount.Text;
            int count = 0;
            for (int i = 0; i < receipt.receiptDetails.Count; i++)
            {     
                code[count] = receipt.receiptDetails[i].code;
                descr[count] = receipt.receiptDetails[i].description;
                qty[count] = receipt.receiptDetails[i].qty.ToString();
                price[count] =LCurrency.displayValue(receipt.receiptDetails[i].sellingPriceVatIncl.ToString());
                tax[count] = LCurrency.displayValue(receipt.receiptDetails[i].vat.ToString());
                amount[count] = LCurrency.displayValue(receipt.receiptDetails[i].amount.ToString());
                count = count + 1;
            }
            PointOfSale.printReceipt(Till.TILLNO, receipt.no, general.Day.bussinessDate, Company.TIN.ToString(), Company.VRN.ToString(), code, descr, qty, price, tax, amount, subTotal, totalVat, total, tender.ToString(), balance.ToString());           
        }
        private bool updateQty(double qty, string sn)
        {
            var detail = new CartDetail();
            detail.id = sn;
            detail.qty = qty;
            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.post(detail, "carts/update_qty");
                return true;
            }
            catch (Exception)
            {
                return false;
            }           
        }
        private void MainForm_Shown(object sender, EventArgs e)
        {
            RECEIPT_NO0 = new Receipt().makeReceipt(Till.TILLNO, general.Day.bussinessDate);
        }      
        private List<string> longList = new List<string>();
        private List<string> shortList = new List<string>();
        
        [DllImport("kernel32")]
        static extern bool Wow64DisableWow64FsRedirection(ref long oldvalue);
        [DllImport("kernel32")]
        static extern bool Wow64EnableWow64FsRedirection(ref long oldvalue);
        private string osk = @"C:\Windows\System32\osk.exe";
        private void startOSK()
        {
            /*long old = 0;
            if (Environment.Is64BitOperatingSystem)
            {
                if (Wow64DisableWow64FsRedirection(old))
                {
                    Process.Start(osk);
                    Wow64EnableWow64FsRedirection(old);
                }
            }
            else*/
            Process.Start(osk);
        }
        private Cart loadCart(string tillNo)
        {
            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.get_("carts/load?till_no=" + tillNo);
                json = JObject.Parse(response.ToString());
                Cart.NO = json.SelectToken("no").ToString();
                return JsonConvert.DeserializeObject<Cart>(json.ToString());
            }
            catch (Exception)
            {
                return null;
            }
        }
        private Cart createCart(string tillNo)
        {
            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.get_("carts/create?till_no=" + tillNo);
                json = JObject.Parse(response.ToString());
                Cart.NO = json.SelectToken("no").ToString();
                return JsonConvert.DeserializeObject<Cart>(json.ToString());
            }catch(Exception)
            {
                MessageBox.Show("Could not create work space. Application will close", "Error: Work space creation failed");
                Application.Exit();
                return null;
            }          
        }
        private void displayCart(Cart cart)
        {
            dtgrdProductList.Rows.Clear();
            if (cart.cartDetails != null)
            {
                int i = -1;
                double _total = 0;
                double _vat = 0;
                double _discount = 0;
                double _grandTotal = 0;
                               
                foreach (CartDetail detail in cart.cartDetails)
                {
                    i = i + 1;
                    double price = detail.sellingPriceVatIncl;
                    double discount = detail.discount;
                    double vat = detail.vat;
                    double qty = detail.qty;
                    double amount = price * qty * (1 - discount / 100);

                    if (detail.voided == false)
                    {
                        _total = _total + amount;
                        _vat = _vat + (price * vat / 100) * qty;
                        _discount = _discount + (price * discount/100) * qty;
                    }

                    var dtgrdRow = new DataGridViewRow();
                    DataGridViewCell dtgrdCell;
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = detail.barcode;
                    dtgrdRow.Cells.Add(dtgrdCell);
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = detail.code;
                    dtgrdRow.Cells.Add(dtgrdCell);
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = detail.description;
                    dtgrdRow.Cells.Add(dtgrdCell);
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = LCurrency.displayValue(detail.sellingPriceVatIncl.ToString());
                    dtgrdRow.Cells.Add(dtgrdCell);
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = detail.discount;
                    dtgrdRow.Cells.Add(dtgrdCell);
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = detail.vat;
                    dtgrdRow.Cells.Add(dtgrdCell);
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = detail.qty;
                    dtgrdRow.Cells.Add(dtgrdCell);
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = LCurrency.displayValue(amount.ToString());
                    dtgrdRow.Cells.Add(dtgrdCell);                    
                    dtgrdCell = new DataGridViewCheckBoxCell();
                    if (detail.voided == true)
                    {
                        dtgrdCell.Value = true;
                    }
                    else
                    {
                        dtgrdCell.Value = false;
                    }
                    dtgrdRow.Cells.Add(dtgrdCell);
                    dtgrdCell = new DataGridViewTextBoxCell();
                    dtgrdCell.Value = detail.id;
                    dtgrdRow.Cells.Add(dtgrdCell);                   
                    dtgrdProductList.Rows.Add(dtgrdRow);
                    dtgrdProductList[0, i].ReadOnly = true;
                    dtgrdProductList[1, i].ReadOnly = true;
                    dtgrdProductList[2, i].ReadOnly = true;
                }               
                dtgrdProductList.CurrentCell = dtgrdProductList[0, dtgrdProductList.RowCount - 1];
                _grandTotal = _total;
                txtTotal.Text = (string)LCurrency.displayValue(_grandTotal.ToString());
                txtDiscount.Text = (string)LCurrency.displayValue(_discount.ToString());
                txtTax.Text = (string)LCurrency.displayValue(_vat.ToString());
                txtGrandTotal.Text = (string)LCurrency.displayValue(_grandTotal.ToString());
            }
        }
        private void calculateValues(Cart cart)
        {
            if (cart.cartDetails != null)
            {
                int i = -1;
                double _total = 0;
                double _vat = 0;
                double _discount = 0;
                double _grandTotal = 0;

                foreach (CartDetail detail in cart.cartDetails)
                {
                    i = i + 1;
                    double price = detail.sellingPriceVatIncl;
                    double discount = detail.discount;
                    double qty = detail.qty;
                    double amount = price * qty * (1 - discount / 100);

                    if (detail.voided == false)
                    {
                        _total = _total + amount;
                        dtgrdProductList[7, i].Value = LCurrency.displayValue(amount.ToString());
                        _vat = _vat + (price * vat / 100) * qty;
                        _discount = _discount + (price * discount / 100) * qty;
                    }                   
                }
                _grandTotal = _total;
                txtTotal.Text = (string)LCurrency.displayValue(_grandTotal.ToString());
                txtDiscount.Text = (string)LCurrency.displayValue(_discount.ToString());
                txtTax.Text = (string)LCurrency.displayValue(_vat.ToString());
                txtGrandTotal.Text = (string)LCurrency.displayValue(_grandTotal.ToString());
            }
        }        
        private void addToCart(string barcode, string code, string description, double costPriceVatExcl, double costPriceVatIncl, double sellingPriceVatExcl, double sellingPriceVatIncl, double discount, double vat, double qty, double amount)
        {
            var cart = new Cart();
            cart.no = Cart.NO;
            cart.till.no = Till.TILLNO;
            var cartDetail = new CartDetail();
            cartDetail.cart = cart;
            cartDetail.barcode = barcode;
            cartDetail.code = code;
            cartDetail.description = description;
            cartDetail.costPriceVatExcl = costPriceVatExcl;
            cartDetail.costPriceVatIncl = costPriceVatIncl;
            cartDetail.sellingPriceVatExcl = sellingPriceVatExcl;
            cartDetail.sellingPriceVatIncl = sellingPriceVatIncl;
            cartDetail.discount = discount;
            cartDetail.vat = vat;
            cartDetail.qty = qty;
            cartDetail.amount = amount;
            var response = new object();
            var json = new JObject();
            try
            {
                Web.post(cartDetail, "carts/add_detail");
            }
            catch (Exception)
            {
                MessageBox.Show("Could not add product", "Error: Process failed", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            cart = loadCart(Till.TILLNO);
            displayCart(cart);
        }
        private bool checkVoid(string tillNo, string sn)
        {
            var response = new object();
            var json = new JObject();
            response = Web.get_("carts/check_voided?detail_id=" + sn);
            if ((bool) response == true)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        private void _void(string id)
        {
            var cartDetail = new CartDetail();
            cartDetail.id = id;
            var response = new object();
            var json = new JObject();
            response = Web.post(cartDetail, "carts/void");
        }
        private void unvoid(string id)
        {
            var cartDetail = new CartDetail();
            cartDetail.id = id;
            var response = new object();
            var json = new JObject();
            response = Web.post(cartDetail, "carts/unvoid");
        }

        private int c = -1;
        private int r = -1;
        private void cmbProducts_KeyUp(object sender, KeyEventArgs e)
        {
            if(c != 2)
            {
                return;
            }
            string currentText = cmbProducts.Text;
            shortList.Clear();
            cmbProducts.Items.Clear();
            cmbProducts.Items.Add(currentText);
            cmbProducts.DroppedDown = true;
            foreach(string text in longList)
            {
                string formattedText = text.ToUpper();
                if (formattedText.Contains(cmbProducts.Text.ToUpper()))
                {
                    shortList.Add(text);
                }
            }
            cmbProducts.Items.AddRange(shortList.ToArray());
            cmbProducts.SelectionStart = cmbProducts.Text.Length;
            Cursor.Current = Cursors.Default;
        }
        private void cellValueChanged()
        {
            //Dim CellRectangle1 As Rectangle = DataGridView1.GetCellDisplayRectangle(e.ColumnIndex, e.RowIndex, False)
            



            int rowHeight = dtgrdProductList.Rows[0].Height;
            int x = 252;
            x = dtgrdProductList.Columns[0].Width + dtgrdProductList.Columns[1].Width + dtgrdProductList.RowHeadersWidth;
            int y = 73 + (dtgrdProductList.RowCount - 1) * (rowHeight);
            if(y > dtgrdProductList.Size.Height + 90)
            {
                y = dtgrdProductList.Size.Height + 25;
            }
            cmbProducts.SetBounds(x, y, 300, rowHeight);
            if(dtgrdProductList.CurrentCell.ColumnIndex == 2)
            {
                r = dtgrdProductList.CurrentCell.RowIndex;
                c = 2;               
                shortList.Clear();              
                if (dtgrdProductList[c, r].Value == null && r == (dtgrdProductList.RowCount - 1))
                {
                    cmbProducts.Items.Clear();
                    cmbProducts.Visible = true;
                    cmbProducts.Focus();
                }else
                {
                    cmbProducts.Focus();
                    cmbProducts.Items.Clear();
                    c = -1;
                    r = -1;
                }
            }
            else
            {
                cmbProducts.Focus();
                cmbProducts.Items.Clear();
                c = -1;
                r = -1;
            }           
        }
        private void cmbProducts_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                string value = cmbProducts.Text;
                dtgrdProductList[c, r].Value = value;
                cmbProducts.Visible = false;
                searchByDescription(value, 1);
            }
            catch (Exception)
            {
            }
        }

        private void MainForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            Application.Exit();
        }

        private void btnOne_Click(object sender, EventArgs e)
        {
            place("1");
        }

        private void btnTwo_Click(object sender, EventArgs e)
        {
            place("2");
        }

        private void btnThree_Click(object sender, EventArgs e)
        {
            place("3");
        }

        private void btnFour_Click(object sender, EventArgs e)
        {
            place("4");
        }

        private void btnFive_Click(object sender, EventArgs e)
        {
            place("5");
        }

        private void btnSix_Click(object sender, EventArgs e)
        {
            place("6");
        }

        private void btnSeven_Click(object sender, EventArgs e)
        {
            place("7");
        }

        private void btnEight_Click(object sender, EventArgs e)
        {
            place("8");
        }

        private void btnNine_Click(object sender, EventArgs e)
        {
            place("9");
        }

        private void btnZero_Click(object sender, EventArgs e)
        {
            place("0");
        }

        private void btnPoint_Click(object sender, EventArgs e)
        {
            place(".");
        }

        private void btnUp_Click(object sender, EventArgs e)
        {
            place("{UP}");
        }

        private void btnLeft_Click(object sender, EventArgs e)
        {
            place("{LEFT}");
        }

        private void btnRight_Click(object sender, EventArgs e)
        {
            place("{RIGHT}");
        }

        private void btnDown_Click(object sender, EventArgs e)
        {
            place("{DOWN}");
        }

        private void btnC_Click(object sender, EventArgs e)
        {
            place("{BACKSPACE}");
        }

        private void btnOK_Click(object sender, EventArgs e)
        {
            place("~");
        }

        private void btnDoubleZero_Click(object sender, EventArgs e)
        {
            place("00");
        }
    }
}
