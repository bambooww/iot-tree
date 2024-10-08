package org.iottree.driver.s7.ppi;

import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.DevAddr.ChkRes;
import org.iottree.core.UAVal.ValTP;
import org.iottree.driver.s7.eth.S7ValTp;

public class PPIAddr extends DevAddr implements Comparable<PPIAddr>
{
	public static class AddrPt
	{
		PPIMemTp memTp ;
		
		int offsetBytes = 0 ;
		
		int inBit = -1 ;//-1 is null
		
		PPIMemValTp memValTp = null ;
		
		public PPIMemTp getMemTp()
		{
			return memTp; 
		}

		
		public int getInBit()
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
		
		
		public String toString()
		{
			if(inBit>0 && memTp.hasBit())
				return memTp.name()+offsetBytes+"."+inBit;
			
			String ret = memTp.name() ;
			//if(memTp.hasBit())
			//	ret +=memValTp.name() ;
			ret += offsetBytes ;
			return ret;
		}
	}
	
	
	AddrPt addrPt = null ;
	
	/**
	 * read or write bytes number
	 */
	int bytesNum ;
	
	
	public PPIAddr()
	{}

	public PPIAddr(String addr,ValTP vtp) throws Exception
	{
		super(addr,vtp) ;
		
		StringBuilder failedr = new StringBuilder() ;
		this.addrPt = parseAddrPt(addr, failedr) ;
		if(this.addrPt==null)
			throw new Exception(failedr.toString()) ;
		if(this.addrPt.memValTp!=null)
		{
			this.bytesNum = this.addrPt.memValTp.getByteNum();
		}
		else
		{
			this.bytesNum = vtp.getValByteLen() ;
		}
	}
	
	private PPIAddr(String addr,AddrPt apt,ValTP vtp)
	{
		super(addr,vtp) ;
		this.addrPt = apt ;
		if(this.addrPt.memValTp!=null)
		{
			this.bytesNum = this.addrPt.memValTp.getByteNum();
		}
		else
		{
			this.bytesNum = vtp.getValByteLen() ;
		}
	}
	
	public PPIAddr(PPIMemTp mtp,int byteoffsets,int inbit,ValTP vtp)
	{
		super(mtp.name()+"B"+byteoffsets+((inbit>=0)?("."+inbit):""),vtp);
		this.addrPt = new AddrPt() ;
		this.addrPt.memTp = mtp ;
		//this.memValTp = PPIMemValTp.transFromValTp(vtp);
		this.addrPt.offsetBytes = byteoffsets;
		this.addrPt.inBit = inbit;
		
		if(this.addrPt.memValTp!=null)
		{
			this.bytesNum = this.addrPt.memValTp.getByteNum();
		}
		else
		{
			this.bytesNum = vtp.getValByteLen() ;
		}
	}
	
	public static PPIAddr parsePPIAddr(String addr,ValTP vtp,StringBuilder failedr)
	{
		AddrPt apt = parseAddrPt(addr,failedr) ;
		if(apt==null)
			return null ;
		
		if(vtp==null && apt.getMemTp()!=null)
			vtp = apt.getMemTp().getFitValTPs()[0] ;
		
		if(!checkFit(addr,apt,vtp,failedr))
			return null ;
		
		return new PPIAddr(addr,apt,vtp) ;
	}
	
	@Override
	public DevAddr parseAddr(UADev dev,String str, ValTP vtp, StringBuilder failedr)
	{
		return parsePPIAddr(str,vtp,failedr) ;
	}
	
	@Override
	public ChkRes checkAddr(UADev dev,String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;
		AddrPt apt = parseAddrPt(addr,failedr) ;
		if(apt==null)
			return new ChkRes(-1,null,null,failedr.toString()) ;
		
		return checkFit(addr,apt,vtp) ;
	}
	
	
	private static boolean checkFit(String addr,AddrPt apt,ValTP vtp,StringBuilder failedr)
	{
		ChkRes cr = checkFit(addr,apt,vtp);
		if(cr==null||cr.isChkOk())
			return true ;
		failedr.append(cr.getChkPrompt()) ;
		return false;
	}
	
	private static ChkRes checkFit(String addr,AddrPt apt,ValTP vtp)
	{
		if(apt.isBitAddr())
		{
			if(vtp!=ValTP.vt_bool)
			{
				return new ChkRes(0,addr,ValTP.vt_bool,"PPI Addr ["+addr+"] must use bool value type");
			}
		}
		
		if(apt.memTp==PPIMemTp.T)
		{
			if(vtp!=ValTP.vt_uint32)
			{
				return new ChkRes(0,addr,ValTP.vt_uint32,"PPI Addr ["+addr+"] must use uint32");
			}
		}
		
		if(apt.memTp==PPIMemTp.C)
		{
			if(vtp!=ValTP.vt_uint16)
			{
				return new ChkRes(0,addr,ValTP.vt_uint32,"PPI Addr ["+addr+"] must use uint16");
			}
		}
		
		if(apt.memValTp==null)
		{
			
		}
		
		return CHK_RES_OK;
	}

	@Override
	public int compareTo(PPIAddr o)
	{
		return this.addrPt.offsetBytes-o.addrPt.offsetBytes ;
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
		PPIAddr ppiaddr = parsePPIAddr(str,vtp,failedr) ;
		if(ppiaddr==null)
			return null ;
		return ppiaddr;
	}

	@Override
	public List<String> listAddrHelpers()
	{
		return null;
	}

	@Override
	public ValTP[] getSupportValTPs()
	{
		return addrPt.memTp.getFitValTPs();
	}

	@Override
	public boolean canRead()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		if(this.addrPt==null)
			return false;
		return this.addrPt.memTp.canWrite();
	}
	
	public int getOffsetBytes()
	{
		return this.addrPt.getOffsetBytes() ;
	}
	
	public int getInBits()
	{
		return this.addrPt.getInBit() ;
	}
	
	
	public int getOffsetBits()
	{
		return this.addrPt.getOffsetBits() ;
	}
	
	public boolean isBitAddr()
	{
		return this.addrPt.isBitAddr();
	}
	
	public int getRegEnd()
	{
		return addrPt.offsetBytes + this.getValTP().getValByteLen() ;
	}
	
	public PPIMemTp getMemTp()
	{
		return addrPt.memTp ;
	}
	
	
//	public PPIMemValTp getMemValTp()
//	{
//		return memValTp ;
//	}
	
	public int getBytesNum()
	{
		return bytesNum ;
	}
	
	public PPIMemValTp getFitMemValTp()
	{
		if(this.isBitAddr())
			return PPIMemValTp.BIT;
		switch(this.bytesNum)
		{
		case 1:
			return PPIMemValTp.B ;
		case 2:
			return PPIMemValTp.W ;
		case 4:
			return PPIMemValTp.D ;
		default:
			return null ;
		}
	}
	
	public String toString()
	{
		return addrPt+" "+this.valTP;
	}
	
	@Override
	public String toCheckAdjStr()
	{
		return addrPt.toString();
	}
	
	/**
	 * parse from string
	 * @param addr
	 * @return
	 */
	public static AddrPt parseAddrPt(String addr,StringBuilder failedr)
	{
		addr = addr.toUpperCase();
		int k = 0 ;
		for(; k < addr.length() ; k ++)
		{
			int c = addr.charAt(k) ;
			if(c>='0' && c<='9')
				break ;
		}
		
		String pstr = addr.substring(0,k) ;
		String sstr = addr.substring(k) ;
		
		char vtc = pstr.charAt(k-1) ;
		PPIMemValTp vt = PPIMemValTp.valOf(vtc) ;
		if(vt!=null)
			pstr = pstr.substring(0,k-1) ;
		
		PPIMemTp mtp = PPIMemTp.valOf(pstr) ;
		if(mtp==null)
		{
			failedr.append("unknown mem tp:"+pstr);
			return null ;
		}
		
		if(vt==null)
		{// use default valtp
			switch(mtp)
			{
			case S:
			case SM:
			case I:
			case Q:
			case M:
			case V:
				vt = PPIMemValTp.B;
				break;
			case AI:
			case AQ:
			case C:
			case T:
				vt = PPIMemValTp.W;
				break ;
			case HC:
				vt = PPIMemValTp.D;
				break ;
			default:
				vt = PPIMemValTp.B;
				break;
			}
		}
		
		AddrPt ret = new AddrPt() ;
		ret.memTp = mtp ;
		//ret.memValTp = vt ;
		//ret.valTP = vtp ;
		k = sstr.indexOf('.') ;
		try
		{
			if(k>0)
			{//bit 
				ret.offsetBytes = Integer.parseInt(sstr.substring(0,k)) ;
				ret.inBit = Integer.parseInt(sstr.substring(k+1)) ;
				//ret.memValTp = PPIMemValTp.BIT;
			}
			else
			{
				ret.offsetBytes = Integer.parseInt(sstr) ;
				if(mtp==PPIMemTp.T)
					ret.offsetBytes *= 4 ;
				else if(mtp==PPIMemTp.C)
					ret.offsetBytes *= 2 ;
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
