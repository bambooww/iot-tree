using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace iottree
{
    class CmdHelper
    {
        public static string runCmd(string cmd,string arguments)
        {
            ProcessStartInfo info = new ProcessStartInfo();
            info.FileName = cmd;
            info.CreateNoWindow = true;
            info.Arguments = arguments;
            info.UseShellExecute = false;
            info.RedirectStandardInput = true;
            info.RedirectStandardOutput = true;
            info.RedirectStandardError = true;
            info.Verb = "runas";
            info.WindowStyle = ProcessWindowStyle.Minimized;
            try
            {
                Process pro = Process.Start(info);
                //pro.StandardInput.WriteLine("net " + arguments);
                //pro.StandardInput.AutoFlush = true;
                //pro.StandardInput.WriteLine("exit");
                string ret = pro.StandardOutput.ReadToEnd();
                ret += pro.StandardError.ReadToEnd();

                pro.WaitForExit();
                return ret;
            }
            catch (Exception e)
            {
                return e.Message;
            }
            
        }

        public static Process runCmdNewWin(string wk_dir,string cmd, string arguments)
        {
            ProcessStartInfo info = new ProcessStartInfo();
            info.FileName = cmd;
            info.WindowStyle = ProcessWindowStyle.Normal;
            info.Arguments = arguments;
            info.UseShellExecute = true;
            //info.RedirectStandardInput = true;
            //info.RedirectStandardOutput = true;
            //info.RedirectStandardError = true;
            info.Verb = "runas";
            info.WorkingDirectory = wk_dir;
            
            try
            {
                return Process.Start(info);
                
            }
            catch (Exception e)
            {
                return null;
            }

        }


        public static string runCmdNetList()
        {
            return runCmd("net.exe", "start");
        }

        public static bool runCmdNetCheckRun(string service_list_name)
        {
            string tmps = runCmd("net.exe", "start");
            StringReader sr = new StringReader(tmps);
            string ln;
            do
            {
                ln = sr.ReadLine();
                if (ln == null)
                    break;
                ln = ln.Trim();
                if (service_list_name == ln)
                    return true;
            }
            while (ln != null);
            return false;
        }

        

        public static string runCmdNetStart(string servicename)
        {
            return runCmd("net.exe", "start "+ servicename);
        }

        public static string runCmdNetStop(string servicename)
        {
            return runCmd("net.exe", "stop " + servicename);
        }

        public static string runNetCmd(string arguments)
        {
            ProcessStartInfo info = new ProcessStartInfo();
            info.FileName = "cmd.exe";
            info.CreateNoWindow = true;
            info.Arguments = arguments;
            info.UseShellExecute = false;
            info.RedirectStandardInput = true;
            info.RedirectStandardOutput = true;
            info.RedirectStandardError = true;
            info.WindowStyle = ProcessWindowStyle.Minimized;
            Process pro = Process.Start(info);
            pro.StandardInput.WriteLine("net " + arguments);
            pro.StandardInput.AutoFlush = true;
            pro.StandardInput.WriteLine("exit");
            string ret = pro.StandardOutput.ReadToEnd();
            
            pro.WaitForExit();
            return ret;
        }
       
    }
}
