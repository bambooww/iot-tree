package org.iottree.driver.s7.eth;

import org.iottree.core.UAVal.ValTP;

public enum S7MemTp
{
	I(0x81,true,true,Tp2ValTP.VTP_NOR), //Inputs
	Q(0x82,true,true,Tp2ValTP.VTP_NOR),  //Outputs
	M(0x83,true,true,Tp2ValTP.VTP_NOR),  //Flag Memory
	DB(0x84,true,true,Tp2ValTP.VTP_NOR),  //PPI=V(0x84,true,true,PPITp2ValTP.VTP_NOR);
	//
	C(0x1C,false,true,Tp2ValTP.VTP_W_S),  //counter
	T(0x1D,false,true,Tp2ValTP.VTP_W_S);  //timer
	
	

	private final int val ;
	private final boolean canBit ;
	private final boolean bWrite;
	private final ValTP[] valTPs;
	
	S7MemTp(int v,boolean hasbit,boolean bwrite,ValTP[] vtps)
	{
		val = v ;
		canBit = hasbit ;
		bWrite = bwrite;
		valTPs = vtps;
	}
	
	public int getVal()
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
	
	public static S7MemTp valOf(String ss)
	{
		switch(ss)
		{
		
		case "C":
			return C ;
		case "DB":
			return DB ;
		case "T":
			return T ;
		case "I":
			return I;
		case "Q":
			return Q ;
		case "M":
			return M;
		default:
			return null ;
		}
	}
	
	public static S7MemTp valOf(short v)
	{
		switch(v)
		{
		case 0x81:
			return I;
		case 0x82:
			return Q;
		case 0x83:
			return M;
		case 0x84:
			return DB;
		case 0x1C:
			return C;
		case 0x1D:
			return T;
		default:
			throw new IllegalArgumentException("invalid PPI Mem Tp val="+v) ;
		}
	}
}

class Tp2ValTP
{
	static final ValTP[] VTP_NOR = new ValTP[] {ValTP.vt_bool,ValTP.vt_byte,ValTP.vt_int16,ValTP.vt_uint16,ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_int64} ;
	
	static final ValTP[] VTP_DW_L = new ValTP[] {ValTP.vt_uint32,ValTP.vt_int64} ;
	
	static final ValTP[] VTP_W_S = new ValTP[] {ValTP.vt_uint16,ValTP.vt_int16} ;
	
}
