package org.iottree.core.util;

import java.util.ArrayList;
import java.util.List;

public class BSOutputBuf implements IBSOutput
{
	ArrayList<byte[]> bss = new ArrayList<>() ;
	
	@Override
	public void write(byte[] bs)
	{
		bss.add(bs) ;
	}
	
	
	public List<byte[]> getBufferedBSS()
	{
		return bss ;
	}
	
	public String toHexStr(boolean fix_len2)
	{
		StringBuilder sb = new StringBuilder() ;
		for(byte[] bs:bss)
		{
			sb.append(byteArray2HexStr(bs,0,bs.length,"",fix_len2)) ;
		}
		return sb.toString() ;
	}
	
	public static String byteArray2HexStr(byte[] bs, int offset, int len, String delim,boolean fix_len2)
	{
		if (bs == null)
			return null;

		if (bs.length == 0 || len <= 0)
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++)
		{
			if(i>0 && delim != null)
				sb.append(delim);
			int tmpi = 255;
			tmpi = tmpi & bs[i + offset];
			String s = Integer.toHexString(tmpi);
			if(fix_len2)
			{
				if (s.length() == 1)
					s = "0" + s;
			}
			sb.append(s);
		}
		return sb.toString().toUpperCase();
	}
}
