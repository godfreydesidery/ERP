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
    public partial class ZReportForm : Form
    {
        

        public ZReportForm()
        {
            InitializeComponent();
        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            this.Visible = false;
        }

        private void ZReportForm_Load(object sender, EventArgs e)
        {
            double cash = 0d;
            double CRCards = 0d;
            double giftVouchers = 0d;
            double cheque = 0d;
            double CRNotes = 0d;
            double newFloat = 0d;
            string tillno = Till.TILLNO;
            string currentDate = general.Day.bussinessDate;


            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.get_("reports/get_zreport");
                json = JObject.Parse(response.ToString());
            }
            catch (Exception)
            {
                return;
            }           
        }
    }
}
