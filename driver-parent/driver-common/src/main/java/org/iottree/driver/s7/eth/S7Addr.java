package org.iottree.driver.s7.eth;

import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.UAVal.ValTP;

/**
 * bit         addr :   I0.0   M0.0  DB1.DBX0.0
 * byte       addr :   IB0    MB0    DB1.DBB0
 * word     addr:    IW0   MW0  DB1.DBW0
 * dword   addr:    ID0    MD0   DB1.DBD0
 * symbol  addr:    START STOP
 * 
 * 
 * @author jason.zhu
 *
 */
public class S7Addr extends DevAddr implements Comparable<S7Addr>
{
	int dbNum = -1 ;//<=0 is normal address syntax
	
	S7MemTp memTp ;
	
	int offsetBytes = 0 ;
	
	int inBit = -1 ;//-1 is null
	
	S7ValTp memValTp = null ;
	
	/**
	 * read or write bytes number
	 */
	int bytesNum ;
	
	
	public S7Addr(String addr,ValTP vtp)
	{
		super(addr,vtp) ;
		
		if(vtp!=null)
			this.bytesNum = vtp.getValByteLen() ;
	}

//	public S7Addr(String addr,ValTP vtp) throws Exception
//	{
//		super(addr,vtp) ;
//		
//		StringBuilder failedr = new StringBuilder() ;
//		this.addrPt = parseAddrPt(addr, failedr) ;
//		if(this.addrPt==null)
//			throw new Exception(failedr.toString()) ;
//		if(this.addrPt.memValTp!=null)
//		{
//			this.bytesNum = this.addrPt.memValTp.getByteNum();
//		}
//		else
//		{
//			this.bytesNum = vtp.getValByteLen() ;
//		}
//	}
//	
//	private S7Addr(String addr,AddrPt apt,ValTP vtp)
//	{
//		super(addr,vtp) ;
//		this.addrPt = apt ;
//		if(this.addrPt.memValTp!=null)
//		{
//			this.bytesNum = this.addrPt.memValTp.getByteNum();
//		}
//		else
//		{
//			this.bytesNum = vtp.getValByteLen() ;
//		}
//	}
//	
//	public S7Addr(S7MemTp mtp,int byteoffsets,int inbit,ValTP vtp)
//	{
//		super(mtp.name()+"B"+byteoffsets+((inbit>=0)?("."+inbit):""),vtp);
//		this.addrPt = new AddrPt() ;
//		this.addrPt.memTp = mtp ;
//		this.addrPt.offsetBytes = byteoffsets;
//		this.addrPt.inBit = inbit;
//		
//		if(this.addrPt.memValTp!=null)
//		{
//			this.bytesNum = this.addrPt.memValTp.getByteNum();
//		}
//		else
//		{
//			this.bytesNum = vtp.getValByteLen() ;
//		}
//	}
	
	public static S7Addr parseS7Addr(String addr,ValTP vtp,StringBuilder failedr)
	{
		S7Addr apt = parseAddrPt(addr,failedr) ;
		if(apt==null)
			return null ;
		
		if(vtp==null)
		{
			vtp = apt.getValTP() ;
			if(vtp==null)
				vtp = apt.getFitMemValTp().getValTP() ;
		}
		
		if(!checkFit(addr,apt,vtp,failedr))
			return null ;
		
		apt.valTP = vtp ;
		apt.bytesNum = vtp.getValByteLen();
		return apt;//new S7Addr(addr,apt,vtp) ;
	}
	
	@Override
	public DevAddr parseAddr(UADev dev,String str, ValTP vtp, StringBuilder failedr)
	{
		return parseS7Addr(str,vtp,failedr) ;
	}
	
	public S7MemTp getMemTp()
	{
		return memTp;
	}
	
	public S7ValTp getMemValTp()
	{
		return this.memValTp ;
	}
	
	public boolean isDBMem()
	{
		return dbNum>0;
	}
	
	public int getDBNum()
	{
		return this.dbNum ;
	}
	
	public String getAreaKey()
	{
		if(memTp==S7MemTp.DB)
			return "DB"+dbNum ;
		return memTp.name() ;
	}

	
	public int getInBits()
	{
		return inBit ;
	}
	

	public int getOffsetBytes()
	{
		return offsetBytes ;
	}
	
	public int getOffsetBits()
	{
		return this.offsetBytes*8 + (this.inBit>=0?this.inBit:0) ;
	}
	
	public boolean isBitAddr()
	{
		return inBit>=0 ;
	}
	
	
	
	@Override
	public ChkRes checkAddr(UADev dev,String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;
		S7Addr apt = parseAddrPt(addr,failedr) ;
		if(apt==null)
			return new ChkRes(-1,null,null,failedr.toString()) ;
		
		return checkFit(addr,apt,vtp) ;
	}
	
	
	private static boolean checkFit(String addr,S7Addr apt,ValTP vtp,StringBuilder failedr)
	{
		ChkRes cr = checkFit(addr,apt,vtp);
		if(cr==null||cr.isChkOk())
			return true ;
		failedr.append(cr.getChkPrompt()) ;
		return false;
	}
	
	private static ChkRes checkFit(String addr,S7Addr apt,ValTP vtp)
	{
		if(apt.isBitAddr())
		{
			if(vtp!=ValTP.vt_bool)
			{
				return new ChkRes(0,addr,ValTP.vt_bool,"S7 Addr ["+addr+"] must use bool value type");
			}
			return CHK_RES_OK;
		}
		
		if(apt.memTp==S7MemTp.T)
		{
			if(vtp!=ValTP.vt_uint32)
			{
				return new ChkRes(0,addr,ValTP.vt_uint32,"S7 Addr ["+addr+"] must use uint32");
			}
		}
		
		if(apt.memTp==S7MemTp.C)
		{
			if(vtp!=ValTP.vt_uint16)
			{
				return new ChkRes(0,addr,ValTP.vt_uint16,"S7 Addr ["+addr+"] must use uint16");
			}
		}

		S7ValTp s7vt = apt.getMemValTp();
		switch(s7vt)
		{
		case X:
			if(vtp!=ValTP.vt_bool)
				return new ChkRes(0,addr,ValTP.vt_bool,"S7 Addr ["+addr+"] must use bool value type");
			break;
		case B:
			if(vtp!=ValTP.vt_byte && vtp!= ValTP.vt_uint8)
				return new ChkRes(0,addr,ValTP.vt_uint8,"S7 Addr ["+addr+"] may use vt_uint8 value type");
			break;
		case W:
			if(vtp!=ValTP.vt_int16 && vtp!= ValTP.vt_uint16)
				return new ChkRes(0,addr,ValTP.vt_uint16,"S7 Addr ["+addr+"] may use vt_uint16 value type");
			break;
		case D:
			if(vtp!=ValTP.vt_int32 && vtp!= ValTP.vt_uint32)
				return new ChkRes(0,addr,ValTP.vt_uint32,"S7 Addr ["+addr+"] may use vt_uint32 value type");
			break;
		default:
			break;
		}
		
		return CHK_RES_OK;
	}

	@Override
	public int compareTo(S7Addr o)
	{
		return this.offsetBytes-o.offsetBytes ;
	}

	

	@Override
	public boolean isSupportGuessAddr()
	{
		return true;
	}

	@Override
	public DevAddr guessAddr(UADev dev,String str,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;
		S7Addr addr = parseS7Addr(str,vtp,failedr) ;
		if(addr==null)
			return null ;
		return addr;
	}

	@Override
	public List<String> listAddrHelpers()
	{
		return null;
	}

	@Override
	public ValTP[] getSupportValTPs()
	{
		return memTp.getFitValTPs();
	}

	@Override
	public boolean canRead()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		return memTp.canWrite();
	}
	
	
	public int getRegEnd()
	{
		return offsetBytes + this.getValTP().getValByteLen() ;
	}
	
//	public S7MemTp getMemTp()
//	{
//		return addrPt.memTp ;
//	}
	
//	public String getAreaKey()
//	{
//		return this.addrPt.getAreaKey() ;
//	}
	
	public boolean chkSameArea(S7MemTp mtp,int db_num)
	{
		if(this.memTp!=mtp)
			return false;
		
		if(mtp==S7MemTp.DB)
			return this.dbNum==db_num ;
		
		return true;
	}
	
	public int getBytesNum()
	{
		return bytesNum ;
	}
	
	public S7ValTp getFitMemValTp()
	{
		if(this.isBitAddr())
			return S7ValTp.X;
		switch(this.bytesNum)
		{
		case 1:
			return S7ValTp.B ;
		case 2:
			return S7ValTp.W ;
		case 4:
			return S7ValTp.D ;
		default:
			return null ;
		}
	}
	
	public String toString()
	{
		String ret = memTp.name() ;
		
		if(memTp==S7MemTp.DB)
		{
			ret +=this.dbNum+",";
		}
		
		//if(memTp.getDefaultS7ValTp()!=this.memValTp)
		//	ret += this.memValTp.name() ;
		
		if(inBit>=0 && memTp.hasBit())
		{
			if(memTp==S7MemTp.DB || memTp.getDefaultS7ValTp()!=this.memValTp)
				ret += this.memValTp.name() ;
			ret += offsetBytes+"."+inBit;
		}
		else
		{
			ret += this.memValTp.name() ;
			ret += offsetBytes ;
			
		}
		return ret;
	}
	
	@Override
	public String toCheckAdjStr()
	{
		return toString();
	}
	/**
DB<num>,<S7 data type><address>
DB<num>,<S7 data type><address><.bit>
DB<num>,<S7 data type><address><.string length>*
DB<num>,<S7 data type><address><[row][col]>
	 * 
	 * 
	 * @param addr
	 * @param failedr
	 * @return
	 */
	private static S7Addr parseAddrDB(String addr,StringBuilder failedr)
	{
		int k = addr.indexOf(',');
		if(k<=0)
		{
			failedr.append("DB address may be like DB200,D2 no [,] found") ;
			return null ;
		}
		String fulladdr = addr ;
		String dbstr = addr.substring(0,k) ;
		
		try
		{
			S7MemTp ret_memTp = S7MemTp.DB;
			int ret_dbNum = Integer.parseInt(dbstr.substring(2)) ;
			addr = addr.substring(k+1) ;
			if(addr.startsWith("DB"))
				addr = addr.substring(2) ;
			//addr  <data type><address><.bit>
			k = 0 ;
			int addrlen = addr.length() ;
			for(; k < addrlen ; k ++)
			{
				int c = addr.charAt(k) ;
				if(c>='0' && c<='9')
					break ;
			}
			if(k<=0)
			{
				failedr.append("no data type found in addr"+addr) ;
				return null ;
			}
			String vtstr = addr.substring(0,k) ;
			S7ValTp vtp = S7ValTp.valOf(vtstr) ;
			if(vtp==null)
			{
				failedr.append("unknown data type ["+vtstr+"] found in addr "+addr) ;
				return null ;
			}
			
			S7Addr ret = new S7Addr(fulladdr,vtp.getValTP()) ;
			ret.memTp = ret_memTp ;
			ret.dbNum = ret_dbNum ;
			ret.memValTp = vtp ;
			addr = addr.substring(k) ;
			k=  addr.indexOf('.') ;
			if(k>0)
			{
				ret.offsetBytes = Integer.parseInt(addr.substring(0,k)) ;
				ret.inBit = Integer.parseInt(addr.substring(k+1)) ;
			}
			else
			{
				ret.offsetBytes = Integer.parseInt(addr) ;
			}
			return ret ;
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage()) ;
			return null ;
		}
		
	}
	/**
	 * parse from string
	 * @param addr
	 * @return
	 */
	private static S7Addr parseAddrPt(String addr0,StringBuilder failedr)
	{
		addr0 = addr0.toUpperCase();
		String addr = addr0 ;
		if(addr.startsWith("DB"))
		{//parse DB
			return parseAddrDB(addr,failedr) ;
		}
		
		// parse mem
		S7MemTp mt = S7MemTp.parseStrHead(addr);
//		char fc = addr.charAt(0);
//		switch(fc)
//		{
//		case 'I':
//			mt = S7MemTp.I;
//			break;
//		case 'Q':
//			mt = S7MemTp.Q;
//			break;
//		case 'M':
//			mt = S7MemTp.M;
//			break;
//		case 'C':
//			mt = S7MemTp.C;
//			break;
//		case 'T':
//			mt = S7MemTp.T;
//			break;
//		default:
//			failedr.append("unknown mem tp:"+addr);
//			return null ;
//		}
		if(mt==null)
		{
			failedr.append("unknown mem tp:"+addr);
			return null ;
		}
		
		addr = addr.substring(mt.name().length());
		
		int k = 0 ;
		int addrlen = addr.length() ;
		for(; k < addrlen ; k ++)
		{
			int c = addr.charAt(k) ;
			if(c>='0' && c<='9')
				break ;
		}
		
		S7ValTp vt  = null;
		if(k>0)
		{//like I0
			String pstr = addr.substring(0,k) ;
			vt = S7ValTp.valOf(pstr) ;
			if(vt==null)
			{
				failedr.append("unknown val tp:"+pstr);
				return null ;//
			}
			addr = addr.substring(k) ;
		}
		
		if(vt==null)
		{// use default valtp
//			switch(mt)
//			{
//			case I:
//			case Q:
//			case M:
//				vt = S7ValTp.B;
//				break;
//			case C:
//				vt = S7ValTp.W;
//				break ;
//			case T:
//				vt = S7ValTp.D;
//				break ;
//			default:
//				vt = S7ValTp.B;
//				break;
//			}
			
			vt = mt.getDefaultS7ValTp() ;
		}
		
		S7Addr ret = new S7Addr(addr0,vt.getValTP()) ;
		ret.memTp = mt ;
		ret.memValTp = vt ;
		ret.bytesNum = vt.getByteNum() ;
		try
		{
			k = addr.indexOf('.') ;
			if(k>0)
			{
				ret.offsetBytes = Integer.parseInt(addr.substring(0,k));
				ret.inBit = Integer.parseInt(addr.substring(k+1)) ;
			}
			else
			{
				if(vt==S7ValTp.X)
				{
					failedr.append("bit addr must end with num.num") ;
					return null ;
				}
				ret.offsetBytes = Integer.parseInt(addr);
			}
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage()) ;
			return null ;
		}
		return ret ;
	}

}