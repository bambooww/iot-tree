package org.iottree.core.util.web;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Token token = Token(user,psw) ;
 * 
 * Restful 每次请求，http头token属性都必须生成一个新的
 * 
 * String newtoken = token.createNew() ;
 * 
 * @author jason.zhu
 *
 */
public class Token
{
	public static final String KEY_ALGORITHM = "DES";

	public static final String CIPHER_ALGORITHM = "DES/ECB/NoPadding";

	String user;

	String psw;

	SecretKey secretKey;

	Cipher encCipher;

	Cipher decCipher;

	public Token(String user, String psw) throws Exception
	{
		if (user == null || "".equals(user) || psw == null || "".equals(psw))
			throw new IllegalArgumentException("user psw cannot be null or empty");

		this.user = user;
		this.psw = psw;

		byte input[] = psw.getBytes("UTF-8");
		byte[] kbs = "00000000".getBytes();
		System.arraycopy(input, 0, kbs, 0, input.length <= 8 ? input.length : 8);
		DESKeySpec desKey = new DESKeySpec(kbs);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		secretKey = keyFactory.generateSecret(desKey);

		encCipher = Cipher.getInstance(CIPHER_ALGORITHM);
		SecureRandom random = new SecureRandom();
		encCipher.init(Cipher.ENCRYPT_MODE, secretKey, random);

		decCipher = Cipher.getInstance(CIPHER_ALGORITHM);
		decCipher.init(Cipher.DECRYPT_MODE, secretKey);
	}

	public String createNew() throws Exception
	{
		String uuid = UUID.randomUUID().toString();
		//System.out.println("new uuid=" + uuid);
		String dd = uuid;

		byte[] bs = dd.getBytes("UTF-8");
		int blen = 0;
		if ((blen = bs.length % 8) != 0)
		{
			byte[] newbs = new byte[bs.length + 8 - blen];
			System.arraycopy(bs, 0, newbs, 0, bs.length);
			for (int i = bs.length; i < newbs.length; i++)
				newbs[i] = 0;
			bs = newbs;
		}
		bs = encCipher.doFinal(bs);
		return user + "|" + byteArray2HexStr(bs);
	}

	public String[] parseToken(String token) throws Exception
	{
		if (isNullOrEmpty(token))
			return null;
		int k = token.indexOf('|');
		if (k <= 0)
			return null;

		String usr = token.substring(0, k);
		if(!user.equals(usr))
			return null ;
		
		String dd = token.substring(k + 1);

		Token tk = new Token(usr, psw);
		byte[] bs = hexStr2ByteArray(dd);
		bs = tk.decCipher.doFinal(bs);
		int last = bs.length;
		for (; last > 0; last--)
		{
			if (bs[last - 1] != 0)
				break;
		}
		String uuid = new String(bs, 0, last, "UTF-8");
		//System.out.println("token uuid=" + uuid);
		return new String[] { usr, uuid };
	}

	private static String byteArray2HexStr(byte[] bs)
	{
		return byteArray2HexStr(bs, 0, bs.length);
	}

	private static String byteArray2HexStr(byte[] bs, int offset, int len)
	{
		return byteArray2HexStr(bs, offset, len, null);
	}

	private static String byteArray2HexStr(byte[] bs, int offset, int len, String delim)
	{
		if (bs == null)
			return null;

		if (bs.length == 0 || len <= 0)
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++)
		{
			if (i > 0 && delim != null)
				sb.append(delim);
			int tmpi = 255;
			tmpi = tmpi & bs[i + offset];
			String s = Integer.toHexString(tmpi);
			if (s.length() == 1)
				s = "0" + s;
			sb.append(s);
		}
		return sb.toString().toUpperCase();
	}

	private static byte[] hexStr2ByteArray(String hexstr)
	{
		if (hexstr == null)
			return null;

		if (hexstr.equals(""))
			return new byte[0];

		if (hexstr.indexOf(' ') < 0)
		{
			int s = hexstr.length() / 2;
			byte[] ret = new byte[s];
			for (int i = 0; i < s; i++)
			{
				ret[i] = (byte) Short.parseShort(hexstr.substring(i * 2, i * 2 + 2), 16);
			}
			return ret;
		}

		List<String> ss = splitStrWith(hexstr, " ");
		int s = ss.size();
		byte[] ret = new byte[s];
		for (int i = 0; i < s; i++)
		{
			ret[i] = (byte) Short.parseShort(ss.get(i), 16);
		}
		return ret;
	}

	private static List<String> splitStrWith(String str, String delimi)
	{
		if (isNullOrEmpty(str))
			return null;

		ArrayList<String> rets = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(str, delimi);
		while (st.hasMoreTokens())
			rets.add(st.nextToken());

		return rets;
	}

	private static boolean isNullOrEmpty(String s)
	{
		if (s == null)
			return true;

		return s.equals("");
	}

	public static void main(String[] args) throws Exception
	{
		Token tk = new Token("user1", "123456");
		String newtk = tk.createNew();
		System.out.println("new token=" + newtk);
		String[] ret = tk.parseToken(newtk);
		System.out.println("parsed ret=" + ret);
	}
}
