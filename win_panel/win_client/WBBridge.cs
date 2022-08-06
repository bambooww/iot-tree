using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace wclient
{
    [ComVisible(true)]
    public class WBBridge
    {
        public string get_loc_lic(string k)
        {
            return k + "--";
        }
    }
}
