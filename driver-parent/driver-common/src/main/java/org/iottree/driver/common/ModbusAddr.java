package org.iottree.driver.common;

import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.*;
import org.iottree.core.DevAddr.ChkRes;
import org.iottree.core.UAVal.ValTP;

/**
 * 0xxxxx
 * 1xxxxx
 * 3xxxxx
 * 4xxxxx
 * 
 * @author zzj
 *
 */
public class ModbusAddr extends DevAddr implements Comparable<ModbusAddr>
{
	public static final short COIL_OUTPUT = '0' ; //w r
	
	public static final short COIL_INPUT = '1' ;  // r
	
	public static final short REG_INPUT = '3' ; //r
	
	public static final short REG_HOLD = '4' ; // r w
	/**
	 * 0 - coil output w/r  1 - coil input r
	 * 3  - input reg  r       4- hold reg  r/w
	 */
	short addrTp = -1 ;
	
	int regPos = -1 ;
	int bitPos = -1 ;
	
	//private ValTP valTP = null ;
	
	ModbusAddr()
	{}
	
	public ModbusAddr(String addr,ValTP vtp,char addrtp,int regpos,int bitpos)
	{
		super(addr,vtp);
		this.addrTp = (short)(addrtp&0xFF) ;
		//valTP = vtp ;
		this.regPos = regpos ;
		this.bitPos = bitpos ;
	}
	
	
	@Override
	public DevAddr parseAddr(UADev dev,String str, ValTP vtp,StringBuilder failedr)
	{
		return parseModbusAddr(str, vtp,failedr) ;
	}
	
	@Override
	public ChkRes checkAddr(UADev dev,String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;
		ModbusAddr ma = parseModbusAddr(addr, vtp,failedr) ;
		if(ma==null)
		{
			return new ChkRes(-1,null,null,failedr.toString()) ;
		}
		return CHK_RES_OK ;
	}
	
	static public ModbusAddr parseModbusAddr(String str, ValTP vtp,StringBuilder failedr)
	{
		String addr = str ;
		if(Convert.isNullOrEmpty(str)||str.length()<2)
		{
			failedr.append("invalid address,address must 0xxxxx 1xxxxx 3xxxxx 4xxxxx") ;
			return null ;//guess failed
		}
		char c = str.charAt(0);
		str = str.substring(1);
		switch(c)
		{
		case '0':
		case '1':
			if(vtp==ValTP.vt_none)
				vtp = ValTP.vt_bool ;
			if(vtp!=ValTP.vt_bool)
			{
				failedr.append("invalid address,address must 0xxxxx 1xxxxx 3xxxxx 4xxxxx") ;
				return null ;
			}
			break;
		case '3':
		case '4':
			break;
		default:
			failedr.append("invalid address,address must 0xxxxx 1xxxxx 3xxxxx 4xxxxx") ;
			return null ;
		}
		
		int i = str.indexOf('.') ;
		if(i<0)
		{
			int v = Integer.parseInt(str)-1 ;
			if(v<0)
			{
				failedr.append("invalid address value="+str) ;
				return null ;
			}
			return new ModbusAddr(addr,vtp,c,v,0) ;
//			if(vtp==ValTP.vt_bool)
//			{
//				return new ModbusAddr(addr,vtp,c,v,0) ;
//			}
//			else
//			{
//				return new ModbusAddr(addr,vtp,c,v,0) ;
//			}
		}
		else
		{
			int v = Integer.parseInt(str.substring(0,i)) ;
			int bitv = Integer.parseInt(str.substring(i+1)) ;
			return new ModbusAddr(addr,vtp,c,v,bitv) ;
		}
	}

	@Override
	public DevAddr guessAddr(UADev dev,String str,ValTP vtp)
	{
		if(Convert.isNullOrEmpty(str)||str.length()<2)
			return null ;//guess failed
		char c = str.charAt(0);
		switch(c)
		{
		case '0':
		case '1':
			vtp = ValTP.vt_bool ;
			break;
		case '3':
		case '4':
			if(vtp!=ValTP.vt_uint16)
				vtp = ValTP.vt_int16;
			break;
		default:
			str = "0"+str ;
			vtp = ValTP.vt_bool ;
		}
		
		c = str.charAt(0);
		//String leftstr = formatVal(Integer.parseInt(str.substring(1)),5);
		//leftstr = UAUtil.transAddrNumByGuess(leftstr,5) ;
		
		StringBuilder sb = new StringBuilder() ;
		return parseAddr(dev,str, vtp,sb) ;
	}
	
	public short getAddrTp()
	{
		return this.addrTp ;
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
	public boolean isSupportGuessAddr()
	{
		return true;
	}

	@Override
	public boolean canRead()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		switch(addrTp)
		{
		case '0':
		case '4':
			return true ;
		default:
			return false;
		}
	}

	
	public int getRegPos()
	{
		return regPos;
	}
	
	public int getRegEnd()
	{
		return regPos + this.getValTP().getValByteLen() ;
	}
	

	public int getBitPos()
	{
		return bitPos;
	}


//	public int getBoolBitPos()
//	{
//		if(this.getValTP()==)
//		return getRegPos()*8+getBitPos() ;
//	}
	
	private String formatVal()
	{
		String s = ""+(regPos+1);
		int len = s.length() ;
		if(len>5)
			return null ;
		StringBuilder sb = new StringBuilder() ;
		for(int i = 0 ; i < 5-len ; i ++)
			sb.append('0') ;
		sb.append(s) ;
		return sb.toString() ;
	}
	
	@Override
	public String toCheckAdjStr()
	{
		String str = this.getAddr();
		char c = str.charAt(0);
		String fstr = this.formatVal() ;
		if(fstr==null)
			return null ;
		
		switch(c)
		{
		case '0':
		case '1':
		case '3':
		case '4':
			return c+formatVal() ;
		default:
			return null ;
		}
	}
	
	@Override
	public int compareTo(ModbusAddr o)
	{
		int v = this.regPos-o.regPos ;
		if(v==0)
		{
			return this.bitPos - o.bitPos ;
		}
		return v;
	}

}
