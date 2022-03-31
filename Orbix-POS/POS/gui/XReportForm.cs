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
    public partial class XReportForm : Form
    {
        double totalAmount = 0d;
        public XReportForm()
        {
            InitializeComponent();
        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            this.Visible = false;
        }

        private void btnPrint_Click(object sender, EventArgs e)
        {

        }

        private void XReportForm_Load(object sender, EventArgs e)
        {
            string tillno = Till.TILLNO;
            string currentdate = general.Day.bussinessDate;


            var response = new object();
            var json = new JObject();
            try
            {
                response = Web.get_("reports/get_xreport");
                json = JObject.Parse(response.ToString());
            }
            catch (Exception)
            {
                return;
            }
        }
    }
}
