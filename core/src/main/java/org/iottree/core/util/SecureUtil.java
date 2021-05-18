package org.iottree.core.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * use PBKDF2 to encrypt and check password
 * 
 * @author jason.zhu
 *
 */
public class SecureUtil
{
	public static final String PBKDF2 = "PBKDF2WithHmacSHA1";//"PBKDF2WithHmacSHA256";

	public static final int SALT_BYTE_SIZE = 32 / 2;
	public static final int HASH_BIT_SIZE = 128 * 4;
	public static final int ITERATE_NUM = 1000; // iterations number


	/**
	 * encrypt psw
	 * @param password
	 * @param salt
	 * @return
	 */
	public static String encryptPsw(String password, String salt)
			throws Exception
	{
		KeySpec spec = new PBEKeySpec(password.toCharArray(), Convert.hexStr2ByteArray(salt), ITERATE_NUM, HASH_BIT_SIZE);
		SecretKeyFactory f = SecretKeyFactory.getInstance(PBKDF2);
		return Convert.byteArray2HexStr(f.generateSecret(spec).getEncoded());
	}

	/**
	 * check inputed password,
	 * @param chked_psw
	 * @param enc_psw
	 * @param salt
	 * @return
	 */
	public static boolean checkPsw(String chked_psw, String enc_psw, String salt)
			throws Exception
	{
		String enc_p = encryptPsw(chked_psw, salt);
		return enc_p.equals(enc_psw);
	}

	
	public static String generateSalt() throws NoSuchAlgorithmException
	{
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[SALT_BYTE_SIZE];
		random.nextBytes(salt);

		return Convert.byteArray2HexStr(salt);
	}
}
