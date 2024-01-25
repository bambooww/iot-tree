package org.iottree.core.util.xmldata;

import java.io.IOException;
import java.io.InputStream;

import org.iottree.core.basic.ByteOrder;

public class DataUtil
{
	public final static byte[] intToBytes(int i)
	{
		return intToBytes(i,ByteOrder.LittleEndian);
	}
	
	public final static byte[] intToBytes(int i,ByteOrder bo)
	{
		// int is 32bits, 4Bytes
		byte[] bytes = new byte[4];

		intToBytes(i, bytes,0,bo);

		return bytes;
	}
	
	public final static void intToBytes(int i,byte[] bytes,int offset,ByteOrder bo)
	{
		// int is 32bits, 4Bytes
		//byte[] bytes = new byte[4];
		if(bo==ByteOrder.ModbusWord)
		{// b3 b2 b1 b0 - b1 b0 b3 b2
			bytes[offset+1] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+0] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+3] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+2] = (byte) (i & 0xFF);
		}
		else if(bo==ByteOrder.BigEndian)
		{
			bytes[offset] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+1] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+2] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+3] = (byte) (i & 0xFF);
		}
		else
		{//little
			bytes[offset+3] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+2] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+1] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset] = (byte) (i & 0xFF);
		}
		//return bytes;
	}
	
	public final static short bytesToShort(byte[] bytes)
	{
		return bytesToShort(bytes,0,ByteOrder.LittleEndian);
	}
	
	public final static short bytesToShort(byte[] bytes,int offset)
	{
		return bytesToShort(bytes,offset,ByteOrder.LittleEndian) ;
	}

	public final static short bytesToShort(byte[] bytes,int offset,ByteOrder bo)
	{
		if (bytes == null || bytes.length < 2+offset)
			throw new IllegalArgumentException("byte array size must be "+(2+offset));

		int i = 0;
		
		if(bo==ByteOrder.ModbusWord)
		{ // b0 b1 b2 b3  - b2 b3 b0 b1 (based word)
			i = ((bytes[offset] & 0xFF) << 8) | (bytes[offset+1] & 0xFF);
		}
		else if(bo==ByteOrder.BigEndian)
		{
			i = ((bytes[offset+1] & 0xFF) << 8) | (bytes[offset] & 0xFF);
		}
		else
		{//little endian
			i = ((bytes[offset] & 0xFF) << 8) | (bytes[offset+1] & 0xFF);
		}
		
		
		//i = (short) (bytes[offset] & 0xFF);
		//i = (short) ((i << 8) | (bytes[offset+1] & 0xFF));

		return (short)i;
	}

	public final static byte[] shortToBytes(short i)
	{
		// int is 32bits, 4Bytes
		byte[] bytes = new byte[2];
		shortToBytes(i, bytes,0) ;
		return bytes;
	}
	
	public final static void shortToBytes(short i,byte[] bytes,int offset)
	{
		 shortToBytes(i,bytes,offset,null) ;
	}
	
	public final static void shortToBytes(short i,byte[] bytes,int offset,ByteOrder bo)
	{
		if(bo==ByteOrder.BigEndian)
		{
			bytes[offset] = (byte) (i & 0xFF);
			i = (short) (i >>> 8);
			bytes[offset+1] = (byte) (i & 0xFF);
		}
		else
		{//ByteOrder.ModbusWord default
			bytes[offset+1] = (byte) (i & 0xFF);
			i = (short) (i >>> 8);
			bytes[offset] = (byte) (i & 0xFF);
		}
	}
	
	public final static int bytesToInt(byte[] bytes,ByteOrder bo)
	{
		return bytesToInt(bytes,0,bo);
	}

	public final static int bytesToInt(byte[] bytes,int offset,ByteOrder bo)
	{
		if (bytes == null || bytes.length < 4+offset)
			throw new IllegalArgumentException("byte array size must be "+(4+offset));

		int i = 0;
		if(bo==ByteOrder.ModbusWord)
		{ // b0 b1 b2 b3  - b2 b3 b0 b1 (based word)
			i = ((bytes[offset+2] & 0xFF) << 8) | (bytes[offset+3] & 0xFF);
			i = (i << 8) | (bytes[offset+0] & 0xFF);
			i = (i << 8) | (bytes[offset+1] & 0xFF);
		}
		else if(bo==ByteOrder.BigEndian)
		{
			i = ((bytes[offset+3] & 0xFF) << 8) | (bytes[offset+2] & 0xFF);
			i = (i << 8) | (bytes[offset+1] & 0xFF);
			i = (i << 8) | (bytes[offset] & 0xFF);
		}
		else
		{//little endian
			i = ((bytes[offset] & 0xFF) << 8) | (bytes[offset+1] & 0xFF);
			i = (i << 8) | (bytes[offset+2] & 0xFF);
			i = (i << 8) | (bytes[offset+3] & 0xFF);
		}

		return i;
	}
	
	public final static byte[] longToBytes(long i)
	{
		return longToBytes( i,ByteOrder.LittleEndian);
	}
	
	public final static byte[] longToBytes(long i,ByteOrder bo)
	{
		byte[] bytes = new byte[8];
		longToBytes(i,bytes,0,bo);
		return bytes;
	}

	public final static void longToBytes(long i,byte[] bytes,int offset,ByteOrder bo)
	{
		// long is 64bits, 8Bytes
		if(bo==ByteOrder.ModbusWord)
		{// b7 b6 b5 b4 b3 b2 b1 b0 - b1 b0 b3 b2 b5 b4 b7 b6
			bytes[offset+1] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+0] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+3] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+2] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+5] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+4] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+7] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+6] = (byte) (i & 0xFF);
		}
		else if(bo==ByteOrder.BigEndian)
		{
			bytes[offset] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+1] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+2] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+3] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+4] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+5] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+6] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+7] = (byte) (i & 0xFF);
		}
		else
		{//litter
			bytes[offset+7] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+6] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+5] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+4] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+3] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+2] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset+1] = (byte) (i & 0xFF);
			i = i >>> 8;
			bytes[offset] = (byte) (i & 0xFF);
		}
		
	}

	public final static long bytesToLong(byte[] bytes,ByteOrder bo)
	{
		return bytesToLong(bytes,0,bo);
	}
	
	public final static long bytesToLong(byte[] bytes,int offset,ByteOrder bo)
	{
		if (bytes == null || bytes.length < 8+offset)
			throw new IllegalArgumentException("byte array size must be "+(8+offset));

		long i = 0;

		if(bo==ByteOrder.ModbusWord)
		{// b0 b1 b2 b3 b4 b5 b6 b7  - b6 b7 b4 b5 b2 b3 b0 b1
			i = ((bytes[offset+6] & 0xFF) << 8) | (bytes[offset+7] & 0xFF);
			i = (i << 8) | (bytes[offset+4] & 0xFF);
			i = (i << 8) | (bytes[offset+5] & 0xFF);
			i = (i << 8) | (bytes[offset+2] & 0xFF);
			i = (i << 8) | (bytes[offset+3] & 0xFF);
			i = (i << 8) | (bytes[offset+0] & 0xFF);
			i = (i << 8) | (bytes[offset+1] & 0xFF);
		}
		else if(bo==ByteOrder.BigEndian)
		{
			i = ((bytes[offset] & 0xFF+7) << 8) | (bytes[offset+6] & 0xFF);
			i = (i << 8) | (bytes[offset+5] & 0xFF);
			i = (i << 8) | (bytes[offset+4] & 0xFF);
			i = (i << 8) | (bytes[offset+3] & 0xFF);
			i = (i << 8) | (bytes[offset+2] & 0xFF);
			i = (i << 8) | (bytes[offset+1] & 0xFF);
			i = (i << 8) | (bytes[offset] & 0xFF);
		}
		else
		{//litte
			i = ((bytes[offset] & 0xFF) << 8) | (bytes[offset+1] & 0xFF);
			i = (i << 8) | (bytes[offset+2] & 0xFF);
			i = (i << 8) | (bytes[offset+3] & 0xFF);
			i = (i << 8) | (bytes[offset+4] & 0xFF);
			i = (i << 8) | (bytes[offset+5] & 0xFF);
			i = (i << 8) | (bytes[offset+6] & 0xFF);
			i = (i << 8) | (bytes[offset+7] & 0xFF);
		}
		// i = (i << 8) | (bytes [3] & 0xFF) ;
		return i;
	}

	public final static byte[] floatToBytes(float f)
	{
		return intToBytes(Float.floatToIntBits(f),ByteOrder.LittleEndian);
	}
	
	public final static void floatToBytes(float f,byte[] bs,int offset)
	{
		intToBytes(Float.floatToIntBits(f),bs,offset,ByteOrder.LittleEndian);
	}

	public final static float bytesToFloat(byte[] bytes)
	{
		return bytesToFloat(bytes,ByteOrder.LittleEndian);
	}
	
	public final static float bytesToFloat(byte[] bytes,ByteOrder bo)
	{
		return Float.intBitsToFloat(bytesToInt(bytes,bo));
	}
	
	public final static float bytesToFloat(byte[] bytes,int offset)
	{
		return bytesToFloat(bytes,offset,ByteOrder.LittleEndian) ;
	}
	
	public final static float bytesToFloat(byte[] bytes,int offset,ByteOrder bo)
	{
		if(bo==null)
			bo = ByteOrder.LittleEndian;
		return Float.intBitsToFloat(bytesToInt(bytes,offset,bo));
	}

	public final static byte[] doubleToBytes(double f)
	{
		return longToBytes(Double.doubleToLongBits(f),ByteOrder.LittleEndian);
	}
	
	public final static void doubleToBytes(double f,byte[] bs,int offset)
	{
		doubleToBytes(f,bs,offset,ByteOrder.LittleEndian);
		
	}
	
	public final static void doubleToBytes(double f,byte[] bs,int offset,ByteOrder bo)
	{
		longToBytes(Double.doubleToLongBits(f),bs,offset,bo);
	}

	public final static double bytesToDouble(byte[] bytes)
	{
		return Double.longBitsToDouble(bytesToLong(bytes,ByteOrder.LittleEndian));
	}
	
	public final static double bytesToDouble(byte[] bytes,int offset)
	{
		return bytesToDouble( bytes,offset,ByteOrder.LittleEndian);
	}
	
	public final static double bytesToDouble(byte[] bytes,int offset,ByteOrder bo)
	{
		return Double.longBitsToDouble(bytesToLong(bytes,offset,bo));
	}

	public final static byte booleanToByte(boolean b)
	{
		if (b)
			return (byte) 1;
		else
			return (byte) 0;
	}

	public final static boolean byteToBoolean(byte b)
	{
		return b != 0;
	}

	public final static byte[] readBytes(InputStream in, int size)
			throws IOException
	{
		if (size <= 0)
			return new byte[0];

		byte[] buffer = new byte[size];

		int count = 0;
		int ret = 0;
		while (true)
		{
			ret = in.read(buffer, count, size - count);
			if (ret == -1)
				throw new IOException("No more bytes! [" + count + " < " + size
						+ "]");
			count += ret;
			if (count == size)
				break;

		}
		if (count != size)
			throw new IOException("Must be " + size + " bytes! [" + count + "]");

		return buffer;
	}

	/**
	 * read boolean value from inputStream.
	 */
	public static boolean readBoolean(InputStream in) throws IOException
	{
		return byteToBoolean(readByte(in));
	}

	/**
	 * read long value from inputStream.
	 */
	public static long readLong(InputStream in) throws IOException
	{

		return bytesToLong(readBytes(in, 8),ByteOrder.LittleEndian);
	}

	/**
	 * read byte value from inputStream.
	 */
	public static byte readByte(InputStream in) throws IOException
	{
		int ret = in.read();
		if (ret < 0)
			throw new IOException("Must be 1bytes! [" + ret + "]");

		return (byte) ret;
	}

	/**
	 * read short value from inputStream.
	 */
	public static short readShort(InputStream in) throws IOException
	{
		return bytesToShort(readBytes(in, 2));
	}

	/**
	 * read integer value from inputStream.
	 */
	public static int readInt(InputStream in) throws IOException
	{
		return bytesToInt(readBytes(in, 4),ByteOrder.LittleEndian);
	}

	/**
	 * read float value from inputStream.
	 */
	public static float readFloat(InputStream in) throws IOException
	{
		return bytesToFloat(readBytes(in, 4));
	}

	/**
	 * read double value from inputStream.
	 */
	public static double readDouble(InputStream in) throws IOException
	{

		return bytesToDouble(readBytes(in, 8));
	}

	public static byte[] readByteArray(InputStream in, int size)
			throws IOException
	{

		return readBytes(in, size);
	}

	public static short[] readShortArray(InputStream in, int size)
			throws IOException
	{
		short[] shorts = new short[size];

		for (int i = 0; i < size; i++)
		{
			shorts[i] = readShort(in);
		}

		return shorts;
	}

	public static int[] readIntArray(InputStream in, int size)
			throws IOException
	{
		int[] ints = new int[size];

		for (int i = 0; i < size; i++)
		{
			ints[i] = readInt(in);
		}

		return ints;
	}

	public static long[] readLongArray(InputStream in, int size)
			throws IOException
	{
		long[] longs = new long[size];

		for (int i = 0; i < size; i++)
		{
			longs[i] = readLong(in);
		}

		return longs;
	}

	public static float[] readFloatArray(InputStream in, int size)
			throws IOException
	{
		float[] floats = new float[size];

		for (int i = 0; i < size; i++)
		{
			floats[i] = readFloat(in);
		}

		return floats;
	}

	public static double[] readDoubleArray(InputStream in, int size)
			throws IOException
	{
		double[] doubles = new double[size];

		for (int i = 0; i < size; i++)
		{
			doubles[i] = readDouble(in);
		}

		return doubles;
	}
}
