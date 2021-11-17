package org.iottree.driver.common.modbus.sniffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * a modbus line or channel
 
 [09:21:13.486]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 45 33 33 3F 45 60 42 41 75 EB 85 3F 80 00 00 00 00 B0 3F A2 57 
[09:21:13.631]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 4C CC CD 3F 4D D2 F2 41 80 00 00 3F 80 00 00 00 03 85 84 93 17 02 04 10 10 00 0A 75 3B 
[09:23:11.428]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 53 99 9A 3F 53 B6 46 41 84 3D 71 3F 80 00 00 00 00 B0 41 5B A5 
[09:23:11.569]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 53 99 9A 3F 54 BC 6A 41 84 3D 71 3F 80 00 00 00 03 85 86 8A 26 02 04 10 10 00 0A 75 3B 
[09:23:14.615]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 56 00 00 3F 56 04 19 41 85 AE 14 3F 80 00 00 00 00 B0 41 E5 67 
[09:23:14.755]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 53 99 9A 3F 54 FD F4 41 84 3D 71 3F 80 00 00 00 03 85 86 1C B8 02 04 10 10 00 0A 75 3B 
[09:23:17.786]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 57 99 9A 3F 58 51 EC 41 86 B8 52 3F 80 00 00 00 00 B0 41 0F AF 
[09:23:17.927]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 53 33 33 3F 54 FD F4 41 84 00 00 3F 80 00 00 00 03 85 86 4A 67 02 04 10 10 00 0A 75 3B 02 04 14 42 57 99 9A 3F 58 51 EC 41 86 B8 52 3F 80 00 00 00 00 B0 41 0F AF 
[09:23:19.020]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 52 CC CD 3F 54 BC 6A 41 84 00 00 3F 80 00 00 00 03 85 86 C1 71 02 04 10 10 00 0A 75 3B 
[09:23:22.066]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 56 66 66 3F 58 10 62 41 86 00 00 3F 80 00 00 00 00 B0 41 78 D7 
[09:23:22.207]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 53 33 33 3F 54 39 58 41 84 00 00 3F 80 00 00 00 03 85 86 1E 0E 02 04 10 10 00 0A 75 3B 
[09:23:25.253]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 54 CC CD 3F 56 C8 B4 41 84 F5 C3 3F 80 00 00 00 00 B0 41 35 92 
[09:23:25.394]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 53 33 33 3F 54 7A E1 41 84 00 00 3F 80 00 00 00 03 85 86 FF 75 02 04 10 10 00 0A 75 3B 
[09:23:28.439]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 53 33 33 3F 55 3F 7D 41 84 00 00 3F 80 00 00 00 00 B0 41 58 4A 
[09:23:28.580]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 52 CC CD 3F 54 7A E1 41 83 AE 14 3F 80 00 00 00 03 85 86 60 FE 02 04 10 10 00 0A 75 3B 
[09:23:31.626]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 51 99 9A 3F 53 F7 CF 41 82 F5 C3 3F 80 00 00 00 00 B0 41 37 AD 
[09:23:31.767]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 52 CC CD 3F 54 39 58 41 83 AE 14 3F 80 00 00 00 03 85 86 81 85 02 04 10 10 00 0A 75 3B 
[09:23:34.813]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 50 CC CD 3F 52 2D 0E 41 82 7A E1 3F 80 00 00 00 00 B0 41 2B 4E 
[09:23:34.969]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 52 66 66 3F 53 B6 46 41 83 70 A4 3F 80 00 00 00 03 85 86 3E A5 02 04 10 10 00 0A 75 3B 
[09:23:38.016]收←◆02 04 10 10 00 0A 75 3B 02 04 14 42 50 CC CD 3F 51 EB 85 41 82 7A E1 3F 80 00 00 00 00 B0 41 4B C3 
[09:23:38.156]收←◆01 04 10 10 00 0A 75 08 01 04 14 42 52 66 66 3F 53 74 BC 41 83 70 A4 3F 80 00 
 * 
 * @author jason.zhu
 *
 */
public class SnifferRTUCh
{
	HashMap<String,SnifferCmd> id2scmd = new HashMap<>() ;
	
	
	private static final int PST_NOR = 0 ;
	
	private static final int PST_REQ = 1 ;
	
	private static final int PST_RESP = 2 ;
	
	private int pst = PST_NOR ;
	
	private SnifferBuffer sniBuf = new SnifferBuffer() ;
	
	public SnifferRTUCh()
	{
		
	}
	
	/**
	 * state machine to parse modbus data
	 * 
	 * @param bs
	 */
	public void onSniffedData(byte[] bs)
	{
		if(bs==null||bs.length<=0)
			return ;
		
		sniBuf.addData(bs);
		//baos.
		switch(pst)
		{
		case PST_NOR:
			if(sniBuf.getBufLen()<8)
				return ;
			byte[] tmpbs = new byte[8] ;
			sniBuf.peekData(tmpbs, 0, 8) ;
			
			break ;
		case PST_REQ:
		case PST_RESP:
		}
	}
	
	
	//private 
}
