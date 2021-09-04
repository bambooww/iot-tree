package org.iottree.core.util;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class DesJava
{
	private final static String DES = "DES";

	/**
	 * 加密
	 * 
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回加密后的数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception
	{
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(src);
	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回解密后的原始数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception
	{
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		return cipher.doFinal(src);
	}
	
	
//	 DES算法要求有一个可信任的随机数源
	SecureRandom sr = new SecureRandom();
	// 从原始密匙数据创建一个DESKeySpec对象
	DESKeySpec dks = null ;
	// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
	// 一个SecretKey对象
	SecretKeyFactory keyFactory = null;//SecretKeyFactory.getInstance(DES);
	SecretKey securekey = null;//keyFactory.generateSecret(dks);
	// Cipher对象实际完成解密操作
	Cipher enCipher = null,deCipher=null;//Cipher.getInstance(DES);
	
	public DesJava(byte[] key)
		throws Exception
	{
		//System.out.println("key=="+new String(key)+" len="+key.length);
		byte[] kk = new byte[8] ;
		for(int i = 0 ;i<8;i++)
			kk[i] = 0 ;
		for(int i = 0 ; i < 8 && i<key.length ;i++)
		{
			kk[i] = key[i] ;
		}
		dks = new DESKeySpec(kk);
		
		keyFactory = SecretKeyFactory.getInstance(DES);
		
		securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		deCipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		deCipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		
		enCipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		enCipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
	}
	
	public byte[] encrypt(byte[] src,int offset,int len) throws IllegalBlockSizeException, BadPaddingException
	{
		return enCipher.doFinal(src,offset,len) ;
	}
	
	public byte[] encrypt(byte[] src) throws IllegalBlockSizeException, BadPaddingException
	{
		return enCipher.doFinal(src) ;
	}
	
	public byte[] decrypt(byte[] src,int offset,int len) throws IllegalBlockSizeException, BadPaddingException
	{
		return deCipher.doFinal(src,offset,len) ;
	}

	
	public byte[] decrypt(byte[] src) throws IllegalBlockSizeException, BadPaddingException
	{
		return deCipher.doFinal(src) ;
	}
	
	public static void main(String[] args)
		throws Throwable
	{
		String data = "12345678";//>?:jklmn" ;
		String key = "Th345678" ;
		byte[] sorbs = data.getBytes();
		byte[] dd = encrypt(sorbs, key.getBytes()) ;
		String encdata = Convert.byteArray2HexStr(dd).toUpperCase();
		String decdata = new String(decrypt(dd,key.getBytes()));
		
		System.out.println("key="+key) ;
		System.out.println("sorlen="+sorbs.length+" enc="+encdata+" ddlen="+dd.length) ;
		System.out.println("dec="+decdata) ;
		
		key="1234567890" ;
		DesJava dj = new DesJava(key.getBytes()) ;
		
		data="12345678901234567890123" ;
		
		dd = dj.encrypt(data.getBytes()) ;
		encdata = Convert.byteArray2HexStr(dd).toUpperCase();
		decdata = new String(dj.decrypt(dd));
		
		System.out.println("\ndlen="+data.getBytes().length+"  data="+data);
		System.out.println("key="+key) ;
		System.out.println("enc="+encdata+" ddlen="+dd.length) ;
		System.out.println("dec ="+decdata) ;
	}
}