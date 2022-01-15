using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace win_panel
{
    public partial class FormMain : Form
    {
        public FormMain()
        {
            InitializeComponent();
        }


        private void FormMain_Load(object sender, EventArgs e)
        {
            //this.ShowInTaskbar = false;

        }

        private void btnStart_Click(object sender, EventArgs e)
        {

        }

        private void btnStop_Click(object sender, EventArgs e)
        {

        }

        private void btnRegService_Click(object sender, EventArgs e)
        {

        }

        private void btnUnregService_Click(object sender, EventArgs e)
        {

        }

        private void notifyIcon_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            if (this.Visible)
            {
                this.Hide();
            }
            else
            {
                restoreWin();
            }
                
            //restoreWin();
        }

        public void restoreWin()
        {
            this.ShowInTaskbar = true;
            this.Show();
            this.WindowState = FormWindowState.Normal;
        }

        public void hideWin()
        {
            this.Hide();
            this.notifyIcon.Visible = true;
        }

        private void FormMain_SizeChanged(object sender, EventArgs e)
        {
            if (this.WindowState == FormWindowState.Minimized)
            {
                this.Hide();
                this.notifyIcon.Visible = true;
            }
            
        }

        private void menuExit_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void openPanelMenuItem_Click(object sender, EventArgs e)
        {
            restoreWin();
        }

        private void FormMain_FormClosed(object sender, FormClosedEventArgs e)
        {
            
        }

        private void FormMain_FormClosing(object sender, FormClosingEventArgs e)
        {
            switch (e.CloseReason)
            {
                case CloseReason.ApplicationExitCall:
                    e.Cancel = false;
                    break;
                case CloseReason.FormOwnerClosing:
                    e.Cancel = true;
                    hideWin();
                    break;
                case CloseReason.MdiFormClosing:
                    e.Cancel = true;
                    hideWin();
                    break;
                case CloseReason.None:
                    break;
                case CloseReason.TaskManagerClosing:
                    e.Cancel = false;
                    break;
                case CloseReason.UserClosing:
                    e.Cancel = true;
                    hideWin();
                    break;
                case CloseReason.WindowsShutDown:
                    e.Cancel = false;
                    break;
                default:
                    break;
            }
        }
    }
}
