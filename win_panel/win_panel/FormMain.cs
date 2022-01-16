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

namespace iottree
{
    public partial class FormMain : Form
    {
        const string SERVICE_NAME = "iottree_server";
        const string SERVICE_NAME_LIST = "IOT Tree Server";

        public FormMain()
        {
            InitializeComponent();
        }


        private void FormMain_Load(object sender, EventArgs e)
        {
            //this.ShowInTaskbar = false;
            timer.Enabled = true;

        }

        private void updateUI()
        {
            bool brun = CmdHelper.runCmdNetCheckRun(SERVICE_NAME_LIST);
            if (brun)
            {
                lbTitle.Text = "Running";
                btnStart.Enabled = false;
                btnStop.Enabled = true;
            }
            else
            {
                lbTitle.Text = "Not Running";
                btnStart.Enabled = true;
                btnStop.Enabled = false;
            }
                
        }

        private void btnStart_Click(object sender, EventArgs e)
        {
            //String tmps = CmdHelper.runNetCmd("start");
            //string tmps = CmdHelper.runCmdNetList();
            //MessageBox.Show(tmps);
            //Console.WriteLine(tmps);
            string ret = CmdHelper.runCmdNetStart(SERVICE_NAME);
            MessageBox.Show(ret);
        }

        private void btnStop_Click(object sender, EventArgs e)
        {
            string ret = CmdHelper.runCmdNetStop(SERVICE_NAME);
            MessageBox.Show(ret);
        }

        private void btnRegService_Click(object sender, EventArgs e)
        {
            string ret = CmdHelper.runCmd("./iot-tree-setup.bat", "install");
            MessageBox.Show(ret);
        }

        private void btnUnregService_Click(object sender, EventArgs e)
        {
            string ret = CmdHelper.runCmd("./iot-tree-setup.bat", "uninstall");
            MessageBox.Show(ret);
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
            this.notifyIcon.Visible = false;
            this.notifyIcon.Dispose();
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

        private void timer_Tick(object sender, EventArgs e)
        {
            updateUI();
        }

        private Process logPro = null;

        private void lklbLog_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            if(logPro!=null)
            {
                if(!logPro.HasExited)
                {
                    logPro.Kill();
                    
                }
                logPro = null;
                return;
            }

            string str = System.Environment.CurrentDirectory;

            logPro = CmdHelper.runCmdNewWin(str+@".\log\", str+@".\log\tt.bat", "");
        }
    }
}
