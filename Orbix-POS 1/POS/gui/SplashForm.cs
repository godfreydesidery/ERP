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
    public partial class SplashForm : Form
    {
        public SplashForm()
        {
            InitializeComponent();           
        }

        private void SplashForm_Load(object sender, EventArgs e)
        {        
            BeginInvoke(new MethodInvoker(delegate
            {
                
                Hide();
                LoginForm form = new LoginForm();
                form.Show();
            }));
        }
    }
}
