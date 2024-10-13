package org.iottree.driver.s7.eth;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;

public enum S7ValTp
{
	X(1,ValTP.vt_bool,1), //bit
	B(2,ValTP.vt_uint8,1), //unsigned byte
	C(3,ValTP.vt_byte,1), //singed byte
	W(4,ValTP.vt_uint16,2),//unsigned word
	D(5,ValTP.vt_uint32,4), //unsigned double word
	DATE(6,ValTP.vt_str,2),// word date "yyyy-mm-dd" with range "1990-01-01" to "2168-12-31"
	DI(7,ValTP.vt_int32,4) ,//signed double word
	DT(8,ValTP.vt_date,8), //Date and time  8 bytes "yyyy-mm-ddThh:mm:ss.hhh"  range "1990-01-01T00:00:00.000" - "2089-12-31T23:59:59.998". readonly
	I(9,ValTP.vt_int16,2), //signed word    I0-I65534  INT0-INT65534
	REAL(10,ValTP.vt_float,4),//IEEE float
	STRING(11,ValTP.vt_str,-1),//str
	T(12,ValTP.vt_str,4),//  "+/-ddD_hhH_mmM_ssS_hhhMS" with range "-24D_20H_31M_23S_648MS" to "24D_20H_31M_23S_647MS.
	TOD(13,ValTP.vt_str,4);//Time_Of_Day "h:m:s.mmm" with range "0:0:0.0" to "23:59:59.999"
	
	private final int val ;
	private final UAVal.ValTP valTP;
	private final int byteNum ;
	
	S7ValTp(int v,UAVal.ValTP vtp,int byte_num)
	{
		val = v ;
		this.valTP = vtp ;
		this.byteNum = byte_num ;
	}
	
	public int getVal()
	{
		return val ;
	}
	
	public ValTP getValTP()
	{
		return this.valTP;
	}
	
	public int getByteNum()
	{
		return byteNum;
	}
	
	
	public static S7ValTp valOf(String nn)
	{
		switch(nn.toUpperCase())
		{
		case "X":
			return X ;
		case "B":
		case "BYTE":
			return B ;
		case "C":
		case "CHAR":
			return C ;
		case "W":
			return W ;
		case "D":
		case "DWORD":
			return D;
		case "DATE":
			return DATE;
		case "DI":
		case "DINT":
			return DI;
		case "DT":
			return DT;
		case "I":
		case "INT":
			return I;
		case "REAL":
			return REAL ;
		case "T":
		case "TIME":
			return T;
		case"TOD":
			return TOD;
		default:
			return null;
		}
	}
	
	public static S7ValTp valOf(int v)
	{
		switch(v)
		{
		case 1:
			return X;
		case 2:
			return B;
		case 3:
			return C;
		case 4:
			return W;
		case 5:
			return D;
		case 6:
			return DATE;
		case 7:
			return DI;
		case 8:
			return DT;
		case 9:
			return I;
		case 10:
			return REAL;
		case 11:
			return STRING;
		case 12:
			return T;
		case 13:
			return TOD;
		default:
			return null;
		}
	}
	
	
}
