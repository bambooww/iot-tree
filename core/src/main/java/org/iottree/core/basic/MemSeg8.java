package org.iottree.core.basic;

import java.util.Arrays;

import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

/**
 * a continue mem segment,which has start pos and len
 * it can extended automatically,and merge with other
 * @author zzj
 *
 */
public class MemSeg8 extends MemSeg
{
	byte[] buf = null;
	
	
	
	public MemSeg8(long idx,int len)
	{
		super(idx,len) ;
		this.buf = new byte[len] ;
		Arrays.fill(buf, (byte)0);
	}
	
	public int getBitLen()
	{
		return 8 ;
	}
	
	public byte[] getBuf()
	{
		return buf ;
	}
	
	public void setValBlock(long idx,byte[] bsdata,int bsoffset,int bslen)
	{
		int offset = (int)(idx-this.idx) ;
		System.arraycopy(bsdata, bsoffset, buf, offset, bslen);
	}
	
	public void setValBool(long idx,int bitpos,boolean v)
	{
		int offset = (int)(idx-this.idx) ;
		if(bitpos>=8)
			throw new IllegalArgumentException("bit pos 0-7 in bit8") ;
		if(v)
			buf[offset] |= (1<<bitpos) ;
		else
			buf[offset] &= ~(1<<bitpos) ;
	}
	
	public boolean getValBool(long idx,int bitpos)
	{
		int offset = (int)(idx-this.idx) ;
		if(bitpos>=8)
			throw new IllegalArgumentException("bit pos 0-7 in bit8") ;
		return (((int)buf[offset]) & (1<<bitpos)) > 0 ;
	}
	
	public void setValNumber(UAVal.ValTP tp,long idx,Number v,ByteOrder bo)
	{
		int sidx = (int)(idx-this.idx) ;
		switch(tp)
		{
		case vt_byte:
		case vt_char:
			buf[sidx] = v.byteValue() ;
			break ;
		case vt_int16:
		case vt_uint16:
			if(len<2)
				throw new IllegalArgumentException("int16 must 2 bytes len") ;
			DataUtil.shortToBytes(v.shortValue(),buf,sidx) ;
			break ;
		case vt_int32:
		case vt_uint32:
			if(len<4)
				throw new IllegalArgumentException("int32 must 4 bytes len") ;
			DataUtil.intToBytes(v.intValue(),buf,sidx,bo) ;
			break ;
		case vt_int64:
		case vt_uint64:
		case vt_date://millis second
			if(len<8)
				throw new IllegalArgumentException("int64 must 8 bytes len") ;
			DataUtil.longToBytes(v.longValue(),buf,sidx,bo) ;
			break ;
		case vt_float:
			if(len<4)
				throw new IllegalArgumentException("float must 4 bytes len") ;
			float vf = v.floatValue() ;
			DataUtil.floatToBytes(vf,buf,sidx) ;
			break ;
		case vt_double:
			if(len<8)
				throw new IllegalArgumentException("double must 8 bytes len") ;
			double vd = v.floatValue() ;
			DataUtil.doubleToBytes(vd,buf,sidx) ;
			break ;
		case vt_str:
		default:
			throw new IllegalArgumentException("not support vt="+tp.getStr());
		}
	}
	
	@Override
	public Number getValNumber(ValTP tp,long idx,ByteOrder bo)
	{
		int sidx = (int)(idx-this.idx) ;
		switch(tp)
		{
		case vt_byte:
		case vt_char:
			return buf[sidx];
		case vt_uint8:
			return UnsignedByte.fromByteBits(buf[sidx]);
		case vt_int16:
		case vt_uint16:
			if(len<2)
				throw new IllegalArgumentException("int16 must 2 bytes len") ;
			short shortv = DataUtil.bytesToShort(buf,sidx,bo) ;
			if(tp==ValTP.vt_int16)
				return shortv ;
			else
				return UnsignedShort.fromShortBits(shortv);
		case vt_int32:
		case vt_uint32:
			if(len<4)
				throw new IllegalArgumentException("int32 must 4 bytes len") ;
			int intv = DataUtil.bytesToInt(buf,sidx,bo) ;
			if(tp==ValTP.vt_int32)
				return intv ;
			else
				return UnsignedInteger.fromIntBits(intv) ;
		case vt_int64:
		case vt_uint64:
		case vt_date:
			if(len<8)
				throw new IllegalArgumentException("int64 must 8 bytes len") ;
			long longv = DataUtil.bytesToLong(buf,sidx,bo) ;
			if(tp==ValTP.vt_int64||tp==ValTP.vt_date)
				return longv ;
			else
				return UnsignedLong.fromLongBits(longv) ;
		case vt_float:
			if(len<4)
				throw new IllegalArgumentException("float must 4 bytes len") ;
			return DataUtil.bytesToFloat(buf,sidx,bo);
		case vt_double:
			if(len<8)
				throw new IllegalArgumentException("double must 8 bytes len") ;
			return DataUtil.bytesToDouble(buf,sidx);
		case vt_str:
		
		default:
			throw new IllegalArgumentException("not support vt="+tp.getStr());
		}
	}
	
}
