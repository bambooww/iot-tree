package org.iottree.driver.gb.szy;

import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;

/**
 * 地址为 终端5字节地址Hex.FL.0
 * @author jason.zhu
 *
 */
public class SZYAddr extends DevAddr implements Comparable<SZYAddr>
{
	byte[] terminal = null ;

	SZYFrame.FC fc = null;
	
	int idx =-1;
	
	@Override
	public DevAddr parseAddr(UADev dev, String str, ValTP vtp, StringBuilder failedr)
	{
		return parseAddrStr(str) ;
	}
	
	static SZYAddr parseAddrStr(String str)
	{
		if(Convert.isNullOrEmpty(str))
			return null ;
		List<String> ss = Convert.splitStrWith(str, ".") ;
		int n = ss.size() ;
		if(n==3)
		{
			SZYAddr ret = new SZYAddr() ;
			
			ret.terminal = Convert.hexStr2ByteArray(ss.get(0)) ;
			String mk = ss.get(1).toUpperCase() ;
			ret.fc = SZYFrame.FC.fromMk(mk) ;
			if(ret.fc==null)
				return null ;
			ret.idx = Convert.parseToInt32(ss.get(2),-1) ;
			if(ret.idx<0)
				return null ;
			return ret ; 
		}
		
		//String 
		return null;
	}
	
	public String toString()
	{
		if(this.terminal!=null)
			return "" ;
		
		StringBuilder sb = new StringBuilder() ;
		sb.append( Convert.byteArray2HexStr(this.terminal)) ;
		if(fc!=null)
			sb.append(".").append(this.fc.getMark()) ;
		if(this.idx>=0)
			sb.append(".").append(this.idx) ;
		return sb.toString() ;
	}
	
	@Override
	public String toCheckAdjStr()
	{
		String str = this.getAddr();
		SZYAddr addr = parseAddrStr(str) ;
		if(addr==null)
			return null ;
		return toString() ;
	}

	@Override
	public boolean isSupportGuessAddr()
	{
		return false;
	}

	@Override
	public DevAddr guessAddr(UADev dev, String str, ValTP vtp)
	{
		return null;
	}

	@Override
	public List<String> listAddrHelpers()
	{
		return null;
	}

	@Override
	public ValTP[] getSupportValTPs()
	{
		return null;
	}

	@Override
	public boolean canRead()
	{
		return false;
	}

	@Override
	public boolean canWrite()
	{
		return false;
	}

	@Override
	public int compareTo(SZYAddr o)
	{
		return 0;
	}
}
