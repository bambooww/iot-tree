package org.iottree.driver.gb.szy;

import org.iottree.core.util.Convert;
import org.junit.Test;

public class SZYTest
{
	IRecvCallback cb = new IRecvCallback() {
		
		@Override
		public void onRecvFrame(SZYFrame f)
		{
			byte[] bs0 = f.packTo();
			System.out.println(">>"+Convert.byteArray2HexStr(bs0)) ;
			System.out.println(">>>"+f.getUserData()) ;
		}
	};
	
	@Test
    public void testFrame() throws Exception
    {
		SZYListener lis = new SZYListener() ;
		
		String hex = "68 1A 68 B3 14 04 31 59 1B C0 84 05 00 00 00 00 00 00 00 00 20 20 50 00 00 00 16 14 00 2D 16" ;
		byte[] bs = Convert.hexStr2ByteArray(hex) ;
		lis.onRecvedData(bs, cb);
    }
}
