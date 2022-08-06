using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace wclient
{
    public class Conf
    {
        static object locker = new object();

        static Dictionary<string,string> confD = null;

        private static Dictionary<string, string> getOrLoadConf()
        {
            if (confD != null)
                return confD;

            lock(locker)
            {
                if (confD != null)
                    return confD;

                confD = new Dictionary<string, string>();
                StreamReader sr = null;
                try
                {
                    sr = new StreamReader(".\\conf.txt", Encoding.Default);
                    String line;
                    while ((line = sr.ReadLine()) != null)
                    {
                        line = line.Trim();
                        if (line.StartsWith("#"))
                            continue;

                        int k = line.IndexOf("=");
                        string n = line;
                        string v = "";
                        if(k>0)
                        {
                            n = line.Substring(0, k);
                            v = line.Substring(k + 1);
                        }
                        confD.Add(n, v);
                    }
                }
                catch (Exception)
                {
                    
                }
                finally
                {
                    if(sr!=null)
                        sr.Close();
                }

                return confD;
                
            }
        }

        private static void saveConf()
        {
            Dictionary<string, string> d = getOrLoadConf();
            string wtxt = "";
            foreach(string k in d.Keys)
            {
                string v = d[k];
                wtxt += k + "=" + v + "\r\n";
            }
            File.WriteAllText(".\\conf.txt", wtxt);
        }

        public static string getHostUrl()
        {
            Dictionary<string, string> d = getOrLoadConf();
            string r=null;
            d.TryGetValue("host_url", out r);
            if (string.IsNullOrEmpty(r))
                return "http://localhost:9090";
            return r;
        }

        public static void setHostUrl(string u)
        {
            Dictionary<string, string> d = getOrLoadConf();
            d["host_url"] = u;
            saveConf();
        }
    }
}
