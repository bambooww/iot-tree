package org.iottree.driver.s7.eth;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

public class S7Util
{
	public static boolean getBit(byte[] bs, int idx, int bit_in_byte)
	{
		if (bit_in_byte < 0)
			bit_in_byte = 0;
		if (bit_in_byte > 7)
			bit_in_byte = 7;

		int v = bs[idx] & 0x0FF;
		return (v & (1 << bit_in_byte)) != 0;
	}

	public static int getUInt16(byte[] bs, int idx)
	{
		int hi = (bs[idx] & 0x00FF);
		int lo = (bs[idx + 1] & 0x00FF);
		return (hi << 8) + lo;
	}

	public static short getInt16(byte[] bs, int idx)
	{
		int hi = (bs[idx]);
		int lo = (bs[idx + 1] & 0x00FF);
		return (short) ((hi << 8) + lo);
	}

	public static long getUint32(byte[] bs, int idx)
	{
		long v;
		v = (long) (bs[idx] & 0x0FF);
		v <<= 8;
		v += (long) (bs[idx + 1] & 0x0FF);
		v <<= 8;
		v += (long) (bs[idx + 2] & 0x0FF);
		v <<= 8;
		v += (long) (bs[idx + 3] & 0x0FF);
		return v;
	}

	// 32 bit signed value
	public static int getInt32(byte[] bs, int idx)
	{
		int v;
		v = bs[idx];
		v <<= 8;
		v += (bs[idx + 1] & 0x0FF);
		v <<= 8;
		v += (bs[idx + 2] & 0x0FF);
		v <<= 8;
		v += (bs[idx + 3] & 0x0FF);
		return v;
	}

	// 32 bit floating point
	public static float getFloatAt(byte[] bs, int idx)
	{
		int iv = getInt32(bs, idx);
		return Float.intBitsToFloat(iv);
	}

	public static String getStr(byte[] bs, int idx, int len)
	{
		try
		{
			return new String(bs,idx,len, "UTF-8");
		}
		catch ( UnsupportedEncodingException ex)
		{
			return "";
		}
	}

	public static String getPrintableStr(byte[] bs, int idx, int len)
	{
		byte[] tmpbs = new byte[len];
		System.arraycopy(bs, idx, tmpbs, 0, len);
		for (int c = 0; c < len; c++)
		{
			if ((tmpbs[c] < 31) || (tmpbs[c] > 126))
				tmpbs[c] = 46; // '.'
		}
		try
		{
			return new String(tmpbs, "UTF-8");
		}
		catch ( UnsupportedEncodingException ex)
		{
			return "";
		}
	}

	public static Date getDate(byte[] bs, int idx)
	{
		Calendar cal = Calendar.getInstance();

		int y = transBCDtoByte(bs[idx]);
		if (y < 90)
			y += 2000;
		else
			y += 1900;

		int m = transBCDtoByte(bs[idx + 1]) - 1;
		int d = transBCDtoByte(bs[idx + 2]);
		int h = transBCDtoByte(bs[idx + 3]);
		int min = transBCDtoByte(bs[idx + 4]);
		int s = transBCDtoByte(bs[idx + 5]);

		cal.set(y, m, d, h, min, s);

		return cal.getTime();
	}

	public static void setBit(byte[] bs, int idx, int bit_in_byte, boolean v)
	{
		if (bit_in_byte < 0)
			bit_in_byte = 0;
		if (bit_in_byte > 7)
			bit_in_byte = 7;

		if (v)
			bs[idx] = (byte) (bs[idx] | (1<<bit_in_byte));
		else
			bs[idx] = (byte) (bs[idx] & ~ (1<<bit_in_byte));
	}

	public static void setUInt16(byte[] bs, int idx, int v)
	{
		v = v & 0x0FFFF;
		bs[idx] = (byte) (v >> 8);
		bs[idx + 1] = (byte) (v & 0x00FF);
	}

	public static void setInt16(byte[] bs, int idx, int v)
	{
		bs[idx] = (byte) (v >> 8);
		bs[idx + 1] = (byte) (v & 0x00FF);
	}

	public static void setUint32(byte[] bs, int idx, long v)
	{
		v &= 0x0FFFFFFFF;
		bs[idx + 3] = (byte) (v & 0xFF);
		bs[idx + 2] = (byte) ((v >> 8) & 0xFF);
		bs[idx + 1] = (byte) ((v >> 16) & 0xFF);
		bs[idx] = (byte) ((v >> 24) & 0xFF);
	}

	public static void setInt32(byte[] bs, int idx, int v)
	{
		bs[idx + 3] = (byte) (v & 0xFF);
		bs[idx + 2] = (byte) ((v >> 8) & 0xFF);
		bs[idx + 1] = (byte) ((v >> 16) & 0xFF);
		bs[idx] = (byte) ((v >> 24) & 0xFF);
	}

	public static void setFloat(byte[] bs, int idx, float v)
	{
		int iv = Float.floatToIntBits(v);
		setInt32(bs, idx, iv);
	}

	public static void setDate(byte[] bs, int idx, Date v)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(v);

		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		int wk = cal.get(Calendar.DAY_OF_WEEK);

		if (y > 1999)
			y -= 2000;

		bs[idx] = transByteToBCD(y);
		bs[idx + 1] = transByteToBCD(m);
		bs[idx + 2] = transByteToBCD(d);
		bs[idx + 3] = transByteToBCD(h);
		bs[idx + 4] = transByteToBCD(min);
		bs[idx + 5] = transByteToBCD(s);
		bs[idx + 6] = 0;
		bs[idx + 7] = transByteToBCD(wk);
	}

	public static int transBCDtoByte(byte B)
	{
		return ((B >> 4) * 10) + (B & 0x0F);
	}

	public static byte transByteToBCD(int v)
	{
		return (byte) (((v / 10) << 4) | (v % 10));
	}
}
