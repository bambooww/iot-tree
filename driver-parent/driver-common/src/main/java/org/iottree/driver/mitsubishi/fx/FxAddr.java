package org.iottree.driver.mitsubishi.fx;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;

/**
 * PLC inner address defintion
 * 
 *  D:   PLC-Address*2+1000H;   数据寄存器D bit16
    T:   PLC-Address+00C0H; //timer  bit16
    C:   PLC-Address*2+01C0H; //count  bit16
    S:   PLC-Address*3;  状态继电器
    M:   PLC-Address*2+0100H;   辅助继电器
    Y:   PLC-Address+00A0H; out
    X:   PLC-Address+0080H; input(只能读不能写，输入寄存器必须由外部信号驱动)
    
         PLC-Address元件是指最低位开始后的第N个元件的位置。
         
 * @author jason.zhu
 *
 */
public class FxAddr extends DevAddr implements Comparable<FxAddr>
{
	public static final int TP_S_START = 0x00 ; //S 为状态元件
	public static final int TP_X_START = 0x80 ; //128
	public static final int TP_Y_START = 0xA0 ; //160
	
	public static final int TP_TC_START = 0xC0 ; // timer contacts
	public static final int TP_TCOIL_START = 0x2C0 ; // timer coil
	
	public static final int TP_TV_START = 0x800;// time value
	public static final int TP_TR_START = 0x4C0 ; //Time reset
	
	public static final int TP_CC_START = 0x1C0 ; // counter contaces ;
	public static final int TP_CCOIL_START = 0x3C0 ; //counter coil
	public static final int TP_CR_START = 0x5C0 ;  // Counter Reset
	
	public static final int TP_CV16_START = 0xA00 ; //counter value
	public static final int TP_CV32_START = 0xC00 ;//3072;
	
	public static final int TP_MC_START = 0x100 ; // M Contanct Auxiliary Relays
	public static final int TP_MS_START = 0x1E0 ; // M specila Special Aux. Relays
	public static final int TP_PM_START = 0x300 ;//768
	
	
	public static final int TP_OC_START = 0x3C0 ;//960
	public static final int TP_RC_START = 0x5C0 ;//1472 
	public static final int TP_OT_START = 0x2C0; //704
	public static final int TP_RT_START = 0x4C0;//1216
	
	public static final int TP_PY_START = 0x2A0; //672
	public static final int TP_D_SPEC_START = 0x0E00 ; //3584
	public static final int TP_D_START = 0x1000 ; //4096
	
	
	
	private static List<String> splitPrefixNum(String str,StringBuilder failedr)
	{
		if(Convert.isNullOrTrimEmpty(str))
		{
			failedr.append("addr cannot be empty") ;
			return null ;
		}
		str = str.toUpperCase().trim() ;
		int n = str.length() ;
		char c = str.charAt(0) ;
		int i =0 ;
		if(!(c>='A'&&c<='Z'))
		{
			failedr.append("addr no prefix") ;
			return null ;
		}
		
		String prefix =null ;
		for(i = 1 ; i <n ; i ++)
		{
			c = str.charAt(i) ;
			if(!(c>='A'&&c<='Z'))
			{
				prefix = str.substring(0,i) ;
				String num = str.substring(i) ;
				if(Convert.isNullOrEmpty(num))
				{
					failedr.append("addr no number") ;
					return null ;
				}
				
				return Arrays.asList(prefix,num) ;
			}
		}
		failedr.append("addr no number") ;
		return null ;
	}
	
	String prefix = null ;
	
	int addrNum = -1 ;
	
	int digitNum = 3 ;
	
	boolean bOct = false;
	
	boolean bValBit = false;
	
	boolean bWritable = false;
	
	FxModel fxModel ;
	
	transient FxAddrDef addrDef = null ;
	
	transient FxAddrSeg addrSeg = null ;
	
	public FxAddr()
	{}
	
	FxAddr(String addr_str,ValTP vtp,FxModel fx_m,String prefix,int addr_num,boolean b_valbit,int digit_num,boolean b_oct)
	{
		super(addr_str,vtp) ;
		this.fxModel = fx_m ;
		this.prefix = prefix ;
		this.addrNum = addr_num ;
		this.bValBit = b_valbit ;
		this.digitNum = digit_num ;
		this.bOct = b_oct ;
	}
	
	FxAddr asDef(FxAddrDef addr_def,FxAddrSeg seg)
	{
		this.addrDef = addr_def ;
		this.addrSeg = seg ;
		this.bWritable = this.addrSeg.isWritable() ;
		return this ;
	}
	
	public String getPrefix()
	{
		return prefix ;
	}
	
	public int getAddrNum()
	{
		return addrNum ;
	}
	
	public boolean isValBit()
	{
		return this.bValBit ;
	}
	
	public int getDigitNum()
	{
		return this.digitNum ;
	}
	
	public boolean isOctal()
	{
		return bOct ;
	}
	
	public int getBytesInBase()
	{
		//FxAddrDef def = fxModel.getAddrDef(this.prefix) ;
		return this.addrSeg.calBytesInBase(this.addrNum) ;
	}
	
	public int getInBits()
	{
		return this.addrNum%8 ;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append(this.prefix) ;
		String nstr = null ;
		if(bOct)
			nstr = Integer.toOctalString(addrNum) ;
		else
			nstr = Integer.toString(addrNum) ;
			
		int dn = digitNum - nstr.length() ;
		if(dn>0)
		{
			for(int i = 0 ; i < dn ; i ++)
				sb.append('0') ;
		}
		sb.append(nstr) ;
		return sb.toString() ;
	}
	
	@Override
	public DevAddr parseAddr(UADev dev,String str, ValTP vtp, StringBuilder failedr)
	{
		if(dev==null)
			throw new IllegalArgumentException("no UADev") ;
		FxModel fx_m = (FxModel)dev.getDrvDevModel() ;
		if(fx_m==null)
			throw new IllegalArgumentException("no FxModel") ;
		List<String> ss = splitPrefixNum(str,failedr) ;
		if(ss==null)
			return null ;
		String prefix = ss.get(0) ;
		return fx_m.transAddr(prefix, ss.get(1), vtp, failedr) ;
	}
	
	@Override
	public ChkRes checkAddr(UADev dev,String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;

		List<String> ss = splitPrefixNum(addr, failedr) ;
		if(ss==null)
			return new ChkRes(-1,addr,vtp,"Invalid FxAddr="+addr);
		FxModel fxm = (FxModel)dev.getDrvDevModel() ;
		
		FxAddrDef addrdef = fxm.getAddrDef(ss.get(0)) ;
		if(addrdef==null)
		{
			return new ChkRes(-1,addr,vtp,"Invalid FxAddr no address def found");
		}
		FxAddrSeg seg = addrdef.findSeg(vtp,ss.get(1)) ;
		if(seg==null)
		{
			return new ChkRes(-1,addr,vtp,"Invalid FxAddr seg ["+ss.get(0)+"] not match "+ss.get(1));
		}
		
		//if(vtp!=ValTP.vt_bool)
		return CHK_RES_OK;
	}

	@Override
	public boolean isSupportGuessAddr()
	{
		return false;
	}

	@Override
	public DevAddr guessAddr(UADev dev,String str)
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
		return true;
	}

	@Override
	public boolean canWrite()
	{
		return bWritable;
	}

	@Override
	public int compareTo(FxAddr o)
	{
		return this.addrNum-o.addrNum;
	}

}
