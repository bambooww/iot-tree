package org.iottree.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;


public class UpdateUtil
{
	public static final String HASH_TYPE_MD5 = "MD5";
	public static final String HASH_TYPE_SHA1 = "SHA-1";
	public static final String HASH_TYPE_SHA256 = "SHA-256";
	public static final String HASH_TYPE_SHA384 = "SHA-384";
	public static final String HASH_TYPE_SHA512 = "SHA-512";

	/**
	 * 计算文件的Hash值
	 * 
	 * @param file
	 * @return hash：md5
	 * @throws FileNotFoundException
	 */
	public static String getHashByFile(File file) throws Exception
	{
		return getHashByFile(file, HASH_TYPE_MD5);
	}

	/**
	 * 计算文件的Hash值
	 * 
	 * @param file
	 * @param hashType
	 * @return hash：hashType
	 * @throws FileNotFoundException
	 */
	
	public static String getHashByFile(File file, String hashType) throws Exception
	{
		String value = null;

		try (FileInputStream fis = new FileInputStream(file);)
		{
			//MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest digest = MessageDigest.getInstance(hashType);
			byte[] buf = new byte[1024] ;
			int len ;
			while((len=fis.read(buf))>0)
			{
				digest.update(buf,0,len);
			}
			//
			
			BigInteger bigInteger = new BigInteger(1, digest.digest());
			value = bigInteger.toString(16);
			
//			byteBuffer.force();
//			sun.misc.Cleaner cleaner = ((sun.nio.ch.DirectBuffer) byteBuffer).cleaner();
//            if (cleaner != null)
//                cleaner.clean();
		}

		return value;
	}
	
	
//	public static String getHashByFile(File file, String hashType) throws Exception
//	{
//		String value = null;
//
//		try (FileInputStream fis = new FileInputStream(file);
//				FileChannel fc = fis.getChannel();)
//		{
//			MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
//			MessageDigest digest = MessageDigest.getInstance(hashType);
//			digest.update(byteBuffer);
//			
//			BigInteger bigInteger = new BigInteger(1, digest.digest());
//			value = bigInteger.toString(16);
//			
////			byteBuffer.force();
////			sun.misc.Cleaner cleaner = ((sun.nio.ch.DirectBuffer) byteBuffer).cleaner();
////            if (cleaner != null)
////                cleaner.clean();
//		}
//
//		return value;
//	}

	
	public static String calFileSHA1(File file) throws Exception
	{
		return getHashByFile(file, HASH_TYPE_SHA1) ;
	}
	/**
	 * 计算字符串的SHA值，可指定hash算法
	 * 
	 * @param value
	 * @param hashType
	 *            HASH_TYPE_
	 * @return
	 */
	public static String hexSHA1(String value, String hashType)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance(hashType);
			md.update(value.getBytes("utf-8"));
			byte[] digest = md.digest();
			return byteToHexString(digest);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 计算Hex值
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteToHexString(byte[] bytes)
	{
		return String.valueOf(encodeHex(bytes));
	}
	
	
	public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data
     *            a byte[] to convert to Hex characters
     * @param toLowerCase
     *            <code>true</code> converts to lowercase, <code>false</code> to uppercase
     * @return A char[] containing hexadecimal characters in the selected case
     * @since 1.4
     */
    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data
     *            a byte[] to convert to Hex characters
     * @param toDigits
     *            the output alphabet (must contain at least 16 chars)
     * @return A char[] containing the appropriate characters from the alphabet
     *         For best results, this should be either upper- or lower-case hex.
     * @since 1.4
     */
    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    private static final char[] DIGITS_LOWER =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_UPPER =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


	public static void main(String[] args) throws Exception
	{
		File file = new File("D:\\work\\work_java_tomato\\biz_server_output\\lib\\system.jar");
		long st = System.currentTimeMillis() ;
		String ss = getHashByFile(file, HASH_TYPE_SHA1) ;
		long et = System.currentTimeMillis() ;
		System.out.println(" ss="+ss +" cost="+(et-st)) ;
		
		ss = calFileSHA1(file) ;
		System.out.println(" ss="+ss ) ;
	}
}
