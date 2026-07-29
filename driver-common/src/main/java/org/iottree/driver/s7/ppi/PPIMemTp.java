package org.iottree.driver.s7.ppi;

import org.iottree.core.UAVal.ValTP;

public enum PPIMemTp
{
	
	
	S(0x04,true,true,PPITp2ValTP.VTP_NOR),
	SM(0x05,true,true,PPITp2ValTP.VTP_NOR),
	AI(0x06,false,false,PPITp2ValTP.VTP_W_S),
	AQ(0x07,false,true,PPITp2ValTP.VTP_W_S), //
	C(0x1E,false,true,PPITp2ValTP.VTP_W_S),  //counter
	HC(0x20,false,false,PPITp2ValTP.VTP_DW_L), //high speed counter
	T(0x1F,false,true,PPITp2ValTP.VTP_DW_L), //timer
	I(0x81,true,true,PPITp2ValTP.VTP_NOR),
	Q(0x82,true,true,PPITp2ValTP.VTP_NOR),
	M(0x83,true,true,PPITp2ValTP.VTP_NOR),
	V(0x84,true,true,PPITp2ValTP.VTP_NOR);
	
	
	private final short val ;
	private final boolean canBit ;
	private final boolean bWrite;
	private final ValTP[] valTPs; 
	
	PPIMemTp(int v,boolean hasbit,boolean bwrite,ValTP[] vtps)
	{
		val = (short)v ;
		canBit = hasbit ;
		bWrite = bwrite;
		valTPs = vtps;
	}
	
	public short getVal()
	{
		return val ;
	}
	
	public boolean hasBit()
	{
		return canBit;
	}
	
	public boolean canWrite()
	{
		return bWrite;
	}
	
	public ValTP[] getFitValTPs()
	{
		return valTPs;
	}
	
	public static PPIMemTp valOf(String ss)
	{
		switch(ss)
		{
		case "S":
			return S;
		case "SM":
			return SM ;
		case "AI":
			return AI ;
		case "AQ":
			return AQ ;
		case "C":
			return C ;
		case "HC":
			return HC ;
		case "T":
			return T ;
		case "I":
			return I;
		case "Q":
			return Q ;
		case "M":
			return M;
		case "V":
			return V ;
		default:
			return null ;
		}
	}
	
	public static PPIMemTp valOf(short v)
	{
		switch(v)
		{
		case 0x04:
			return S ;
		case 0x05:
			return SM;
		case 0x06:
			return AI;
		case 0x07:
			return AQ;
		case 0x1E:
			return C;
		case 0x20:
			return HC;
		case 0x1F:
			return T;
		case 0x81:
			return I;
		case 0x82:
			return Q;
		case 0x83:
			return M;
		case 0x84:
			return V;
		default:
			throw new IllegalArgumentException("invalid PPI Mem Tp val="+v) ;
		}
	}
}

class PPITp2ValTP
{
	static final ValTP[] VTP_NOR = new ValTP[] {ValTP.vt_bool,ValTP.vt_byte,ValTP.vt_int16,ValTP.vt_uint16,ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_int64} ;
	
	static final ValTP[] VTP_DW_L = new ValTP[] {ValTP.vt_uint32,ValTP.vt_int64} ;
	
	static final ValTP[] VTP_W_S = new ValTP[] {ValTP.vt_uint16,ValTP.vt_int16} ;
	
}
