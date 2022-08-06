using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace wclient
{
    public partial class FormClient : Form
    {
        private Size WindowMaximumSize;

        public FormClient()
        {
            InitializeComponent();
        }

        private void FormClient_LoadAsync(object sender, EventArgs e)
        {
            WindowMaximumSize = this.MaximumSize;
            //this.TopMost = true;
            this.panelTop.Height = 1;
            webView.NavigationCompleted += WebView_NavigationCompleted;


            initWebView();

            string hostu = Conf.getHostUrl();

            this.webView.Source = new System.Uri(hostu, System.UriKind.Absolute);
        }


        private async void initWebView()
        {
            await webView.EnsureCoreWebView2Async();
            //await webView.CoreWebView2.AddScriptToExecuteOnDocumentCreatedAsync("alert('hello world')");
            webView.CoreWebView2.AddHostObjectToScript("iottree_client", new WBBridge());
        }

        private void WebView_NavigationCompleted(object sender, Microsoft.Web.WebView2.Core.CoreWebView2NavigationCompletedEventArgs e)
        {
            webView.CoreWebView2.NewWindowRequested += CoreWebView2_NewWindowRequested;
            webView.CoreWebView2.NavigationCompleted += CoreWebView2_NavigationCompleted;

            //webView.CoreWebView2.doc
            //webView.CoreWebView2.AddScriptToExecuteOnDocumentCreatedAsync("alert('hello world')");
        }

        private void CoreWebView2_NavigationCompleted(object sender, Microsoft.Web.WebView2.Core.CoreWebView2NavigationCompletedEventArgs e)
        {
            //this.webView.CoreWebView2.
        }

        private void CoreWebView2_NewWindowRequested(object sender, Microsoft.Web.WebView2.Core.CoreWebView2NewWindowRequestedEventArgs e)
        {
            var deferral = e.GetDeferral();
            //e.NewWindow = (CoreWebView2)sender;
            e.NewWindow = this.webView.CoreWebView2;
            e.NewWindow.Navigate(e.Uri);
            deferral.Complete();
        }



        private void FormClient_KeyPress(object sender, KeyPressEventArgs e)
        {
            char kc = e.KeyChar;
            
        }


        private void FormClient_SizeChanged(object sender, EventArgs e)
        {
            //m_rect.X = this.Bounds.Width - 95;
           // m_rect.Y = 4;
            //m_rect.Width = m_rect.Height = 16;
        }

        private void pbClose_Click(object sender, EventArgs e)
        {
            DialogResult dr = MessageBox.Show("You will close IOT-Tree Client", "Confirm", MessageBoxButtons.OKCancel);
            if (dr == DialogResult.OK)
                this.Close();
        }

        private void pbRefresh_Click(object sender, EventArgs e)
        {
            string hostu = Conf.getHostUrl();
            Uri u = this.webView.Source;
            string oldu = null;
            if (u != null)
                oldu = u.AbsoluteUri;
            if (hostu.Equals(oldu))
            {
                //this.webView.Refresh
                this.webView.Reload();
            }
            else
            this.webView.Source = new System.Uri(hostu, System.UriKind.Absolute);
        }

        private const int TOP_TICK_N = 50;

        private int showTopPanelTick = 0;

        private void panelTop_MouseEnter(object sender, EventArgs e)
        {
            this.panelTop.Height = 30;
            showTopPanelTick = TOP_TICK_N;
            timer1.Enabled = true;
        }

        private void panelTop_MouseLeave(object sender, EventArgs e)
        {
            //this.panelTop.Height = 1;
        }

        private void panelTop_MouseMove(object sender, MouseEventArgs e)
        {
            showTopPanelTick = TOP_TICK_N;
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            showTopPanelTick--;
            if(showTopPanelTick<=0)
            {
                timer1.Enabled = false;
                this.panelTop.Height = 1;
            }
        }

        private void pbSetup_Click(object sender, EventArgs e)
        {
            FormConf fc = new FormConf();
            fc.ShowDialog(this);
        }
    }
}
