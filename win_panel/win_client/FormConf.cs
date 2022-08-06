using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace wclient
{
    public partial class FormConf : Form
    {
        public FormConf()
        {
            InitializeComponent();
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            string hurl = tbHostUrl.Text;
            if(string.IsNullOrEmpty(hurl))
            {
                MessageBox.Show("Server URL cannot empty");
                return;
            }

            string oldu = Conf.getHostUrl();
            if (!hurl.Equals(oldu))
            {
                Conf.setHostUrl(hurl);
            }
            this.Close();
        }

        private void FormConf_Load(object sender, EventArgs e)
        {
            string hu = Conf.getHostUrl();
            this.tbHostUrl.Text = hu;
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
