package org.iottree.driver.s7.ppi;

import org.iottree.core.UAVal.ValTP;

public enum PPIMemValTp
{
	BIT(0x01),
	B(0x02),
	W(0x04),
	D(0x06);
	
	private final short val ;
	
	PPIMemValTp(int v)
	{
		val = (short)v ;
	}
	
	public short getVal()
	{
		return val ;
	}
	
	public int getByteNum()
	{
		switch(val)
		{
		case 0x02:
			return 1 ;
		case 0x04:
			return 2 ;
		case 0x06:
			return 4 ;
		default:
			return 1;
		}
	}
	
	public int getBitNum()
	{
		switch(val)
		{
		case 0x01:
			return 1 ;
		case 0x02:
			return 8 ;
		case 0x04:
			return 0x10 ;
		case 0x06:
			return 0x20 ;
		default:
			return 8;
		}
	}
	
	public static PPIMemValTp valOf(char c)
	{
		switch(c)
		{
		case 'B':
			return B ;
		case 'W':
			return W ;
		case 'D':
			return D ;
		default:
			return null;
		}
	}
	
	public static PPIMemValTp valOf(short v)
	{
		switch(v)
		{
		//case 0x01:
		//	return BIT ;
		case 0x02:
			return B;
		case 0x04:
			return W;
		case 0x06:
			return D;
		default:
			return null;
			//throw new IllegalArgumentException("invalid PPI Val Tp val="+v) ;
		}
	}
	
	public static PPIMemValTp transFromValTp(ValTP vtp)
	{
		switch(vtp)
		{
		case vt_bool:
			return PPIMemValTp.BIT;
		case vt_byte:
		case vt_char:
			return PPIMemValTp.B;
		case vt_int16:
		case vt_uint16:
			return PPIMemValTp.W;
		case vt_int32:
		case vt_uint32:
		case vt_float:
			return PPIMemValTp.D;
		case vt_int64:
		case vt_double:
		case vt_str:
		case vt_date:
		case vt_uint64:
		default:
			return null ;
		}
	}
	
	
	
}
