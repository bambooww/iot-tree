package org.iottree.driver.zigbee;

import java.security.SecureRandom;
import java.util.List;

import org.iottree.core.basic.ByteOrder;
import org.iottree.core.util.Convert;

public class DataUtil
{
	/**
	 * 2 bytes
	 * 
	 * @param bs
	 * @param offset
	 * @return
	 */
	public final static int getUInt16(byte[] bs, int offset)
	{
		return ((bs[offset + 1] & 0xFF) << 8) + (bs[offset] & 0xFF);
	}
	
	public final static short getInt16(byte[] bs, int offset)
	{
		short r = (short)(bs[offset + 1]<<8) ;
		r |=  (short)(bs[offset] & 0xFF) ;
		//return (( & 0xFF) << 8) + (bs[offset] & 0xFF);
		return r ;
	}

	public final static void setUInt16(int val, byte[] bs, int offset)
	{
		bs[offset] = (byte) (val & 0xFF);
		bs[offset + 1] = (byte) ((val >> 8) & 0xFF);
	}

	/**
	 * 4 bytes
	 * 
	 * @param bs
	 * @param offset
	 * @return
	 */
	public final static long getUInt32(byte[] bs, int offset)
	{
		long r = (bs[offset + 3] & 0xFF) << 24;
		r += (bs[offset + 2] & 0xFF) << 16;
		r += (bs[offset + 1] & 0xFF) << 8;
		r += bs[offset] & 0xFF;
		return r;
	}
	
	public final static int getInt32(byte[] bs,int offset)
	{
		long r = getUInt32(bs, offset) ;
		return (int)r ;
	}

	public final static void setUInt32(long val, byte[] bs, int offset)
	{
		bs[offset] = (byte) (val & 0xFF);
		bs[offset + 1] = (byte) ((val >> 8) & 0xFF);
		bs[offset + 2] = (byte) ((val >> 16) & 0xFF);
		bs[offset + 3] = (byte) ((val >> 24) & 0xFF);
	}

	public final static String transIEEEAddr(byte[] bs, int offset)
	{
		// return Convert.byteArray2HexStr(bs, offset, 8) ;
		return transHexMSB(bs, offset, 8);
	}

	public final static int transStr2IEEEAddr(String hex_addr, byte[] bs, int offset)
	{
		// return Convert.hexStr2ByteArray(hex_addr,bs,offset);
		return transHexStr2BytesMSB(hex_addr, bs, offset);
	}
	
	public final static String transSecManKey(byte[] bs, int offset)
	{
		return Convert.byteArray2HexStr(bs, offset, 16) ;
	}
	
	public final static int transStr2SecManKey(String hex_addr,byte[] bs,int offset)
	{
		byte[] bb = Convert.hexStr2ByteArray(hex_addr) ;
		if(bb.length!=16)
			throw new IllegalArgumentException("not 16 bytes len") ;
		System.arraycopy(bb, 0, bs, offset, 16);
		return 16 ;
	}
	

	public final static String transPanId(byte[] bs, int offset)
	{// MSB
		return transHexMSB(bs, offset, 8);
	}

	public final static int transStr2PanId(String hexstr, byte[] ret, int offset)
	{ // MSB
		return transHexStr2BytesMSB(hexstr, ret, offset);
	}

	public final static String transHexMSB(byte[] bs, int offset, int len)
	{// MSB
		if (bs == null)
			return null;

		if (bs.length - offset < len)
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = len - 1; i >= 0; i--)
		{
			int tmpi = 255;
			tmpi = tmpi & bs[i + offset];
			String s = Integer.toHexString(tmpi);
			if (s.length() == 1)
				s = "0" + s;
			sb.append(s);
		}
		return sb.toString().toUpperCase();
	}

	public final static int transHexStr2BytesMSB(String hexstr, byte[] ret, int offset)
	{ // MSB
		if (hexstr == null)
			return 0;

		if (hexstr.equals(""))
			return 0;

		if (hexstr.startsWith("0x") || hexstr.startsWith("0X"))
			hexstr = hexstr.substring(2);

		if (hexstr.indexOf(' ') < 0)
		{
			int s = hexstr.length() / 2;

			for (int i = s - 1; i >= 0; i--)
			{
				ret[offset + s - 1 - i] = (byte) Short.parseShort(hexstr.substring(i * 2, i * 2 + 2), 16);
			}
			return s;
		}

		List<String> ss = Convert.splitStrWith(hexstr, " ");
		int s = ss.size();
		for (int i = s - 1; i >= 0; i--)
		{
			ret[offset + s - 1 - i] = (byte) Short.parseShort(ss.get(i), 16);
		}
		return s;
	}

	public final static String transHexAddr(int v)
	{
		return "0x" + String.format("%04X", v);
	}

	public final static String transHex(int v, int len)
	{
		if (len == 2)
			return "0x" + String.format("%02X", v);
		else if (len == 4)
			return "0x" + String.format("%04X", v);
		else
			return "0x" + String.format("%02X", v);
	}

	public final static String transHex(int v)
	{
		return transHex(v, 4);
	}

	public final static double bytesToDouble(byte[] bytes,int offset)
	{
		return org.iottree.core.util.xmldata.DataUtil.bytesToDouble(bytes,offset);
	}
	
	public final static byte[] doubleToBytes(double f)
	{
		return org.iottree.core.util.xmldata.DataUtil.doubleToBytes(f) ;
	}
	
	public final static void doubleToBytes(double f,byte[] bs,int offset)
	{
		org.iottree.core.util.xmldata.DataUtil.doubleToBytes(f,bs,offset) ;
	}
	
	
	
	public final static void floatToBytes(float f,byte[] bs,int offset)
	{
		org.iottree.core.util.xmldata.DataUtil.floatToBytes(f, bs, offset);
	}
	
	public final static byte[] floatToBytes(float f)
	{
		return org.iottree.core.util.xmldata.DataUtil.floatToBytes(f) ;
	}
	
	public final static float bytesToFloat(byte[] bytes,int offset)
	{
		return org.iottree.core.util.xmldata.DataUtil.bytesToFloat(bytes,offset) ;
	}
	/**
	 * 16bit浮点数
	 * 
	 * @param h
	 * @return
	 */
	public static float halfToFloat(short h)
	{
		int bits = h & 0xFFFF;
		int sign = bits >>> 15;
		int exp = (bits >>> 10) & 0x1F;
		int mant = bits & 0x3FF;

		int fsign = sign << 31;
		if (exp == 0)
		{
			if (mant == 0)
			{
				return Float.intBitsToFloat(fsign); // ±0
			}
			// 非规格化数，先规格化
			while ((mant & 0x400) == 0)
			{
				mant <<= 1;
				exp--;
			}
			exp++;
			mant &= 0x3FF;
			return Float.intBitsToFloat(fsign | ((exp + 112) << 23) | (mant << 13));
		}
		else if (exp == 31)
		{
			if (mant == 0)
			{
				return Float.intBitsToFloat(fsign | 0x7F800000); // ±Inf
			}
			return Float.intBitsToFloat(fsign | 0x7F800000 | (mant << 13)); // NaN
		}
		else
		{
			return Float.intBitsToFloat(fsign | ((exp + 112) << 23) | (mant << 13));
		}
	}

	/**
	 * 将 32 位 IEEE 754 单精度 float 转换为 16 位 IEEE 754 半精度位模式（binary16）。 返回的 short 低
	 * 16 位就是半精度的原始位，可直接写入 2 字节。
	 */
	public static short floatToHalf(float f)
	{
		int bits = Float.floatToRawIntBits(f);
		int sign = (bits >>> 31) & 0x1;
		int exp = (bits >>> 23) & 0xFF;
		int mant = bits & 0x7FFFFF;

		int hsign = sign << 15;

		// 1) NaN 或 Infinity
		if (exp == 0xFF)
		{
			if (mant == 0)
			{
				return (short) (hsign | 0x7C00); // ±Inf
			}
			int nanMant = mant >>> 13;
			if (nanMant == 0)
				nanMant = 1; // 防止 NaN 退化为 Inf
			return (short) (hsign | 0x7C00 | nanMant); // NaN
		}

		// 2) 重新计算指数：float 偏置 127 → half 偏置 15
		int newExp = exp - 112; // = exp - 127 + 15

		// 3) 上溢 → ±Inf
		if (newExp >= 0x1F)
		{
			return (short) (hsign | 0x7C00);
		}

		// 4) 下溢 / 非规格化数（subnormal）
		if (newExp <= 0)
		{
			if (newExp < -10)
			{
				return (short) hsign; // 太小 → ±0
			}
			mant |= 0x800000; // 补上隐藏的 1
			int shift = 14 - newExp; // 需要右移的位数
			int halfMant = mant >>> shift;
			int rem = mant & ((1 << shift) - 1);
			int halfway = 1 << (shift - 1);
			// 四舍五入到偶数
			if (rem > halfway || (rem == halfway && (halfMant & 1) == 1))
			{
				halfMant++;
			}
			return (short) (hsign | halfMant);
		}

		// 5) 规格化数：23 位尾数 → 10 位尾数，四舍五入到偶数
		int halfMant = mant >>> 13;
		int rem = mant & 0x1FFF;
		int halfway = 0x1000;
		if (rem > halfway || (rem == halfway && (halfMant & 1) == 1))
		{
			halfMant++;
			if (halfMant == 0x400)
			{ // 尾数进位
				halfMant = 0;
				newExp++;
				if (newExp >= 0x1F)
				{
					return (short) (hsign | 0x7C00); // 变成 Inf
				}
			}
		}
		return (short) (hsign | (newExp << 10) | halfMant);
	}
	
	/**
	 * get hex random bytes as string
	 * @param bytes_len
	 * @return
	 */
	public static String randomBytesHex(int bytes_len)
	{
		SecureRandom secureRandom = new SecureRandom();
        
        byte[] rets = new byte[bytes_len];
        secureRandom.nextBytes(rets);
        
        return Convert.byteArray2HexStr(rets) ;
	}
}
