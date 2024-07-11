package org.iottree.core.util.encrypt;

public class DES
{
	private static byte[] EMPTY_KEY = new byte[]{0,0,0,0,0,0,0,0} ;
	private static byte[] keyToBit64(String key)
	{
		if(key==null||"".equals(key))
			return EMPTY_KEY ;
		
		byte[] rets = new byte[8] ;
		System.arraycopy(EMPTY_KEY, 0, rets, 0, 8) ;
		byte[] tmps = key.getBytes() ;
		for(int i = 0 ; i <8 && i < tmps.length ; i ++)
		{
			rets[i] = tmps[i] ;
		}
		return rets ;
	}
	
	public static String decode (String d,String key)
	{
		if(d==null)
			return null ;
		if("".equals(d))
			return "" ;
		
		
		int len = d.length () / 2 ;
		if(len%8>0)
			throw new IllegalArgumentException("illegal encrypted str!") ;

		byte [] buffer = new byte [len] ;

		for (int i = 0 ; i < buffer.length ; i ++)
		{
			int b = charToNumber (d.charAt (i * 2)) ;
			b = b << 4 ;
			b |= charToNumber (d.charAt (i * 2 + 1)) ;
			buffer [i] = (byte) b ;
		//	System.out.print (Integer.toHexString(b).toUpperCase()) ;
		}

		byte [] out = new byte [len] ;

		int fn = len / 8 ;
		
		DesUtil des = new DesUtil () ;
		byte[] b_key = keyToBit64(key) ;
		byte[] tmpb_in = new byte[8],tmpb_out = new byte[8] ;
		for(int i = 0 ; i < fn ; i ++)
		{
			System.arraycopy(buffer, i*8, tmpb_in, 0, 8) ;
			des.DES (tmpb_in , tmpb_out , b_key, false) ;
			System.arraycopy(tmpb_out, 0, out, i*8, 8) ;
		}

		int l = out.length ;
		for(int i = 1 ; i <= 8 ; i ++)
		{
			if(out[l-i]!=0)
			{
				l = l-i+1 ;
				break ;
			}
		}
		try
		{
			return new String (out,0,l,"UTF-8") ;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage()) ;
		}
	}
	
	
	public static String decode_no_hex (String d,String key)
	{
		if(d==null)
			return null ;
		if("".equals(d))
			return "" ;
		
		
		int len = d.length ();
		if(len%8>0)
			throw new IllegalArgumentException("illegal encrypted str!") ;

		byte [] buffer = d.getBytes() ;

		byte [] out = new byte [len] ;

		int fn = len / 8 ;
		
		DesUtil des = new DesUtil () ;
		byte[] b_key = keyToBit64(key) ;
		byte[] tmpb_in = new byte[8],tmpb_out = new byte[8] ;
		for(int i = 0 ; i < fn ; i ++)
		{
			System.arraycopy(buffer, i*8, tmpb_in, 0, 8) ;
			des.DES (tmpb_in , tmpb_out , b_key, false) ;
			System.arraycopy(tmpb_out, 0, out, i*8, 8) ;
		}

		int l = out.length ;
		for(int i = 1 ; i <= 8 ; i ++)
		{
			if(out[l-i]!=0)
			{
				l = l-i+1 ;
				break ;
			}
		}
		try
		{
			return new String (out,0,l,"UTF-8") ;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage()) ;
		}
	}

	public static String encode (String d,String key)
	{
		if(d==null)
			return null ;
		if("".equals(d))
			return "" ;
		
		byte[] dd = null;
		try
		{
			dd =  d.getBytes("UTF-8") ;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage()) ;
		}
			//分解成8字节单独加密
			char a ='\0';
			int dlen = dd.length ;
			int fn = dlen / 8 ;
			int ln = dlen % 8 ;//最后几位
			int b8len = fn + (ln>0?1:0) ;
			DesUtil des = new DesUtil () ;
			
			byte [] output = new byte [b8len*8] ;
			byte[] tmpb_in = new byte[8],tmpb_out = new byte[8] ;
			byte[] b_key = keyToBit64(key) ;
			for(int i = 0 ; i < fn ; i ++)
			{
				System.arraycopy(dd, i*8, tmpb_in, 0, 8) ;
				des.DES (tmpb_in , tmpb_out , b_key , true) ;
				System.arraycopy(tmpb_out, 0, output, i*8, 8) ;
			}
			
			if(ln>0)
			{//last bytes
				System.arraycopy(EMPTY_KEY, 0, tmpb_in, 0, 8) ;
				System.arraycopy(dd, fn*8, tmpb_in, 0, ln) ;
				des.DES (tmpb_in , tmpb_out , b_key , true) ;
				System.arraycopy(tmpb_out, 0, output, fn*8, 8) ;
			}
			
			
	
			StringBuffer buf = new StringBuffer (output.length * 2) ;
	
			for (int i = 0 ; i < output.length ; i ++)
			{
		        buf.append (numberToHexString (output [i])) ;
			}
	
			return buf.toString () ;
		
	}
	
	public static String encode_no_hex (String d,String key)
	{
		if(d==null)
			return null ;
		if("".equals(d))
			return "" ;
		
		byte[] dd = null;
		try
		{
			dd =  d.getBytes("UTF-8") ;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage()) ;
		}
			//分解成8字节单独加密
			char a ='\0';
			int dlen = dd.length ;
			int fn = dlen / 8 ;
			int ln = dlen % 8 ;//最后几位
			int b8len = fn + (ln>0?1:0) ;
			DesUtil des = new DesUtil () ;
			
			byte [] output = new byte [b8len*8] ;
			byte[] tmpb_in = new byte[8],tmpb_out = new byte[8] ;
			byte[] b_key = keyToBit64(key) ;
			for(int i = 0 ; i < fn ; i ++)
			{
				System.arraycopy(dd, i*8, tmpb_in, 0, 8) ;
				des.DES (tmpb_in , tmpb_out , b_key , true) ;
				System.arraycopy(tmpb_out, 0, output, i*8, 8) ;
			}
			
			if(ln>0)
			{//last bytes
				System.arraycopy(EMPTY_KEY, 0, tmpb_in, 0, 8) ;
				System.arraycopy(dd, fn*8, tmpb_in, 0, ln) ;
				des.DES (tmpb_in , tmpb_out , b_key , true) ;
				System.arraycopy(tmpb_out, 0, output, fn*8, 8) ;
			}

			return new String(output) ;
	}
	

	public static String numberToHexString (int n)
	{
		char [] str = new char [2] ;

		str [0] = bit4ToHexChar ((n >> 4)&0x0F) ;
		str [1] = bit4ToHexChar (n&0x0F) ;

		return new String (str) ;
	}

	public static char bit4ToHexChar (int n)
	{
		if (n >= 0 && n <= 9)
			return (char) ('0' + n) ;
		else
		if (n >= 10 && n <= 15)
			return (char) ('A' + n - 10) ;
		else
			throw new NumberFormatException ("bit4 MUST between (0 , 15): [" + n + "]") ;

	}
	
	public static int charToNumber (char c)
	{
		if (c >= '0' && c <= '9')
			return c - '0' ;
		else
		if (c >= 'A' && c <= 'F')
			return c - 'A' + 10 ;
		else
			throw new NumberFormatException ("Hex String must be 0-9 A-F. [" +
				c + "]") ;

	}
	
    static String toHex(byte b)
    {
        int i = (int)b ;
        i = 0x000000FF&i ;
        if (i>=16)
        	return Integer.toHexString(i).toUpperCase() ;
        else
        	return "0" + Integer.toHexString(i).toUpperCase() ;
    }

//    public static String  doDes(String input ,String key,boolean encrypt)
//    {
//        if (key==null||key.length()!=8)
//        {
//            //System.out.println ("key len!=8") ;
//            //return null ;
//        }
//        
//        if (encrypt)
//            return makeSerial(input,key,true) ;
//        else
//            return makeSerial(input,key,false) ;
//    }
//	
//	private static String toHexStr (byte[] bs)
//	{
//		StringBuffer tmpsb = new StringBuffer () ;
//        for (int i = 0 ; i < bs.length; i ++)
//            tmpsb.append (toHex(bs[i])) ;
//        return tmpsb.toString() ;
//	}
//	
//	public static String makeSerial (String source, String key,boolean flag)
//    {
//    	//byte[] source = sor.getBytes() ;
//        String s1 ;
//        byte[] key1 = new byte[8] ;
//        byte[] a1 = new byte[16] ;
//        byte[] b1 = new byte[8] ;
//        byte[] b2 = new byte[8] ;
//        byte[] c1 = new byte[8] ;
//        byte[] c2 = new byte[8] ;
//        
//        int i ;
//        
//        if (source.length()>16)
//        {
//            s1 = source.substring (0,16) ;
//        }
//        else
//        {
//        	s1 = addSpac (source,16,1) ;
//        }
//        
//        //System.out.println("Input:" + s1);
//        a1 = s1.getBytes () ;
//        //System.out.println("Key:" + key);
//        
//        for (i = 0 ; i < 8 ; i ++)
//        {
//        	b1[i] = a1[i] ;
//        	c1[i] = a1[i+8] ;
//        	if (i==0)
//        	{
//        		key1[i] = 0 ;
//        	}
//        	else
//        	{
//        		key1[i] = (byte)key.charAt(i-1) ;
//        	}
//        }
//      
//		b2 = lbDes(b1,key1,flag);
//		
//		c2 = lbDes(c1, b2,flag);
//	
//		b2 = lbDes(b1, c2,flag);
//		
//		String res = "" ;
//        for (i = 0 ; i < 8 ; i ++)
//            res = res + toHex(b2[i]) + toHex(c2[i]);
//        return res ;
//    }
//
//    private static byte[] lbDes (byte[] source,byte[] key,boolean encrypted)
//    {
//        DesUtil du = new DesUtil () ;
//        byte[] output = new byte[8] ;
//        du.DES(source/*64bits*/,output/*64bits*/,key,encrypted) ;
//        return output ;
//    }
//    
//    private static String addSpac (String ch,int len,int flag)
//    {
//    	int j,n ;
//    	String spac = "" ;
//    	String result = ch ;
//    	n = ch.length() ;
//    	for (j = 1 ; j <= (len-n) ; j ++)
//    	{
//    		spac += ' ' ;
//    	}
//    	
//    	if (flag==0)
//    	{
//    		result = spac + result ;
//    	}
//    	else
//    	if (flag==1)
//    	{
//    		result = result + spac ;
//    	}
//    	return result ;
//    }
    
    public static void main (String[] args)
    {
        //////System.out.println( makeSerial (args[0],"BPHOENIX") );
    	
        long s = System.currentTimeMillis() ;
        //"1111222  23 中文阿说的发生发的 000 $% "
        String kk = "Th345678";//"wRQL<&a";
        System.out.println("key="+kk);
        String res = DES.encode("12345678",kk) ;
        long e = System.currentTimeMillis() ;
        System.out.println("old="+res);
        System.out.println("encode cost="+(e-s));
        res = DES.decode(res,kk) ;
        long e1= System.currentTimeMillis() ;
        System.out.println("decode res=["+res +"] cost="+(e1-e));
    }
    
}