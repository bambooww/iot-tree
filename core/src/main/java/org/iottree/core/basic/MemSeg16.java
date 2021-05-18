package org.iottree.core.basic;

import java.util.Arrays;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

public class MemSeg16  extends MemSeg
{
	short[] buf = null;
	
	public MemSeg16(long idx,int len)
	{
		super(idx,len) ;
		this.buf = new short[len] ;
		Arrays.fill(buf, (short)0);
	}
	
	public int getBitLen()
	{
		return 16 ;
	}
	
	public short[] getBuf()
	{
		return buf ;
	}
	
	public void setValBlock(long idx,byte[] bsdata,int bsoffset,int bslen)
	{
		int offset = (int)(idx-this.idx) ;
		for(int i = 0 ; i < bslen ; i ++)
		{
			int oldv = 0xFFFF & (int)(buf[offset+i/2]) ;
			int tmpv = buf[bsoffset+i] & 0xFF ;
			if(i%2==0)
			{
				tmpv <<= 8 ;
				buf[bsoffset+i/2]  = (short)((oldv & 0x00FF) | tmpv);
			}
			else
			{
				buf[bsoffset+i/2]  = (short)((oldv & 0xFF00) | tmpv);
			}
		}
	}
	
	public void setValBool(long idx,int bitpos,boolean v)
	{
		int offset = (int)(idx-this.idx) ;
		if(bitpos>=16)
			throw new IllegalArgumentException("bit pos 0-15 in bit16") ;
		if(v)
			buf[offset] |= (1<<bitpos) ;
		else
			buf[offset] &= ~(1<<bitpos) ;
	}
	
	public boolean getValBool(long idx,int bitpos)
	{
		int offset = (int)(idx-this.idx) ;
		if(bitpos>=16)
			throw new IllegalArgumentException("bit pos 0-15 in bit16") ;
		return (((int)buf[offset]) & (1<<bitpos)) > 0 ;
	}
	
	public void setValNumber(UAVal.ValTP tp,long idx,Number v)
	{
		int offset = (int)(idx-this.idx) ;
		switch(tp)
		{
		case vt_byte:
			buf[offset] = v.byteValue() ;
			break ;
		case vt_char://u8
			buf[offset] = (byte)(((short)v.byteValue())&0xff) ;
			break ;
		case vt_int16:
		case vt_uint16:
			buf[offset]=v.shortValue();
			break ;
		case vt_int32:
		case vt_uint32:
			if(len<2)
				throw new IllegalArgumentException("int32 must 2 short len") ;
			int vi = v.intValue() ;
			buf[offset+1] = (short) (vi & 0xFFFF);
			vi = vi >>> 16;
			buf[offset] = (short) (vi & 0xFFFF);
			break ;
		case vt_int64:
		case vt_uint64:
		case vt_date://millis second
			if(len<4)
				throw new IllegalArgumentException("int64 must 4 short len") ;
			long vl = v.longValue() ;
			buf[offset+3] = (short) (vl & 0xFFFF);
			vl = vl >>> 16;
			buf[offset+2] = (short) (vl & 0xFFFF);
			vl = vl >>> 16;
			buf[offset+1] = (short) (vl & 0xFFFF);
			vl = vl >>> 16;
			buf[offset] = (short) (vl & 0xFFFF);
			break ;
		case vt_float:
			if(len<2)
				throw new IllegalArgumentException("float must 2 short len") ;
			int vfi = Float.floatToIntBits(v.floatValue());
			buf[offset+1] = (short) (vfi & 0xFFFF);
			vfi = vfi >>> 16;
			buf[offset] = (short) (vfi & 0xFFFF);
			break ;
		case vt_double:
			if(len<4)
				throw new IllegalArgumentException("double must 4 short len") ;
			vl = Double.doubleToLongBits(v.doubleValue());
			buf[offset+3] = (short) (vl & 0xFFFF);
			vl = vl >>> 16;
			buf[offset+2] = (short) (vl & 0xFFFF);
			vl = vl >>> 16;
			buf[offset+1] = (short) (vl & 0xFFFF);
			vl = vl >>> 16;
			buf[offset] = (short) (vl & 0xFFFF);
			break ;
		case vt_str:
		//case vt_date:
		default:
			throw new IllegalArgumentException("not support vt="+tp.getStr());
		}
	}
	
	
	public Number getValNumber(UAVal.ValTP tp,long idx)
	{
		int offset = (int)(idx-this.idx) ;
		switch(tp)
		{
		case vt_byte:
		case vt_char:
			return (byte)(buf[offset]&0xFF);
		case vt_int16:
		case vt_uint16:
			short shortv = buf[offset];
			if(tp==ValTP.vt_int16)
				return shortv ;
			else
				return UnsignedShort.fromShortBits(shortv);
		case vt_int32:
		case vt_uint32:
			if(len<2)
				throw new IllegalArgumentException("int32 must 2 short len") ;
			int i = 0;
			i = (buf[offset] & 0xFFFF);
			i = (i << 16) | (buf[offset+1] & 0xFFFF);
			if(tp==ValTP.vt_int32)
				return i ;
			else
				return  UnsignedInteger.fromIntBits(i);
		case vt_int64:
		case vt_uint64:
		case vt_date:
			if(len<4)
				throw new IllegalArgumentException("int64 must 4 short len") ;
			long l = 0;
			l = (buf[offset] & 0xFFFF);
			l = (l << 16) | (buf[offset+1] & 0xFFFF);
			l = (l << 16) | (buf[offset+2] & 0xFFFF);
			l = (l << 16) | (buf[offset+3] & 0xFFFF);
			if(tp==ValTP.vt_int64||tp==ValTP.vt_date)
				return l;
			else
				return UnsignedLong.fromLongBits(l);
		case vt_float:
			if(len<2)
				throw new IllegalArgumentException("float must 2 short len") ;
			i = 0;
			i = (buf[offset] & 0xFFFF);
			i = (i << 16) | (buf[offset+1] & 0xFFFF);
			return Float.intBitsToFloat(i);
		case vt_double:
			if(len<4)
				throw new IllegalArgumentException("double must 4 short len") ;
			l = 0;
			l = (buf[offset] & 0xFFFF);
			l = (l << 16) | (buf[offset+1] & 0xFFFF);
			l = (l << 16) | (buf[offset+2] & 0xFFFF);
			l = (l << 16) | (buf[offset+3] & 0xFFFF);
			return Double.longBitsToDouble(l);
		case vt_str:
		
		default:
			throw new IllegalArgumentException("not support vt="+tp.getStr());
		}
	}
}
