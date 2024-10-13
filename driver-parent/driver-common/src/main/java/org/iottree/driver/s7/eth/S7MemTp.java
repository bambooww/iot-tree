package org.iottree.driver.s7.eth;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;

public enum S7MemTp
{
	I(0x81,true,true,Tp2ValTP.VTP_NOR,S7ValTp.B), //Inputs
	Q(0x82,true,true,Tp2ValTP.VTP_NOR,S7ValTp.B),  //Outputs
	M(0x83,true,true,Tp2ValTP.VTP_NOR,S7ValTp.B),  //Flag Memory
	DB(0x84,true,true,Tp2ValTP.VTP_NOR,S7ValTp.W),  //PPI=V(0x84,true,true,PPITp2ValTP.VTP_NOR);
	
	//
	C(0x1C,false,true,Tp2ValTP.VTP_W_S,S7ValTp.W),  //counter
	T(0x1D,false,true,Tp2ValTP.VTP_W_S,S7ValTp.D),  //timer
	
	//add after 2024
	V(0x84,true,true,Tp2ValTP.VTP_NOR,S7ValTp.B), // Variable same as DB1 ,s7-200 will use it
	AI(0x6,false,false,Tp2ValTP.VTP_W_S,S7ValTp.W), //Analog Inputs
	AQ(0x7,false,true,Tp2ValTP.VTP_W_S,S7ValTp.W); //Analog outputs
	//DI(0x85,true,true,Tp2ValTP.VTP_NOR); //Discrete Inputs
	

	private final int val ;
	private final boolean canBit ;
	private final boolean bWrite;
	private final ValTP[] valTPs;
	private final S7ValTp defaultVT ;
	
	S7MemTp(int v,boolean hasbit,boolean bwrite,ValTP[] vtps,S7ValTp def_svt)
	{
		val = v ;
		canBit = hasbit ;
		bWrite = bwrite;
		valTPs = vtps;
		defaultVT = def_svt ;
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
	
	public S7ValTp getDefaultS7ValTp()
	{
		return defaultVT;
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
		case "V":
			return V;
		case "AI":
			return AI;
		case "AQ":
			return AQ;
//		case "DI":
//			return DI ;
		default:
			return null ;
		}
	}
	
	public static S7MemTp parseStrHead(String ss)
	{
		if(Convert.isNullOrEmpty(ss))
			return null;
		S7MemTp mt = valOf(ss.substring(0,1)) ;
		if(mt!=null)
			return mt ;
		if(ss.length()<=1)
			return null ;
		return valOf(ss.substring(0,2)) ;
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
		case 0x87:
			return V ;
		case 0x6:
			return AI ;
		case 0x7:
			return AQ;
//		case 0x85:
//			return DI;
		default:
			throw new IllegalArgumentException("invalid S7 Mem Tp val="+v) ;
		}
	}
}

class Tp2ValTP
{
	static final ValTP[] VTP_NOR = new ValTP[] {ValTP.vt_bool,ValTP.vt_byte,ValTP.vt_int16,ValTP.vt_uint16,ValTP.vt_int32,ValTP.vt_uint32,ValTP.vt_int64} ;
	
	static final ValTP[] VTP_DW_L = new ValTP[] {ValTP.vt_uint32,ValTP.vt_int64} ;
	
	static final ValTP[] VTP_W_S = new ValTP[] {ValTP.vt_uint16,ValTP.vt_int16} ;
	
}
