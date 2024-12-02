package org.iottree.core.msgnet.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class ValPack
{	
	Object payload ;
	
	boolean bzip = false;
	
	public ValPack(Object pld,boolean b_zip)
	{
		this.payload = pld ;
		this.bzip = b_zip ;
	}
	
	public Object getPayload()
	{
		return this.payload ;
	}
	
	public boolean isZip()
	{
		return bzip ;
	}
	
	public String getPayloadTP()
	{
		UAVal.ValTP vtp = UAVal.checkValTPByObj(this.payload) ;
		if(vtp!=null)
			return vtp.getStr() ;
		
		if(this.payload instanceof JSONObject)
			return "jo" ;

		if(this.payload instanceof JSONArray)
			return "jarr" ;

		if(this.payload instanceof byte[])
			return "bs" ;
		return null ;
	}
	
	public String toHeadStr()
	{
		return getPayloadTP()+"|"+bzip;
	}
	
	public byte[] pkOut() //throws UnsupportedEncodingException
	{
		String pld_tp = null ;
		byte[] pld_bs = null ;
		
		try
		{
		UAVal.ValTP vtp = UAVal.checkValTPByObj(this.payload) ;
		if(vtp!=null)
		{
			pld_tp = vtp.getStr() ;
			if(this.payload!=null)
				pld_bs = this.payload.toString().getBytes("utf-8") ;
		}
		else if(this.payload instanceof JSONObject)
		{
			pld_tp = "jo" ;
			pld_bs = this.payload.toString().getBytes("utf-8") ;
		}
		else if(this.payload instanceof JSONArray)
		{
			pld_tp = "jarr" ;
			pld_bs = this.payload.toString().getBytes("utf-8") ;
		}
		else if(this.payload instanceof byte[])
		{
			pld_tp="bs" ;
			pld_bs = (byte[])this.payload ;
		}
		else
			throw new RuntimeException("unsupport payload type="+payload.getClass().getCanonicalName()) ;
		
		byte[] hd = (pld_tp+"|"+(bzip?"1":"0")+"\n").getBytes("utf-8") ;
		if(pld_bs==null)
			return hd ;
		if(bzip)
			pld_bs = zip(pld_bs) ;
		byte[] ret = new byte[hd.length+pld_bs.length] ;
		System.arraycopy(hd, 0, ret, 0, hd.length);
		System.arraycopy(pld_bs, 0, ret, hd.length, pld_bs.length);
		return ret ;
		}
		catch(Exception ee)
		{
			throw new RuntimeException(ee) ;
		}
	}
	
	public static ValPack parseFrom(byte[] bs) throws Exception
	{
		if(bs==null||bs.length<3)
			return null ;
		
		String hd = null ;
		int pld_idx=-1,pld_len=-1 ;
		for(int i = 0 ; i < bs.length ; i ++)
		{
			int c = bs[i] ;
			if(c=='\n')
			{
				hd = new String(bs,0,i) ;
				pld_idx = i +1 ;
				pld_len = bs.length - pld_idx ;
				break ;
			}
		}
		
		if(hd==null)
			return null ;
		List<String> ss = Convert.splitStrWith(hd, "|") ;
		if(ss.size()!=2)
			return null ;
		String vtp = ss.get(0) ;
		
		boolean bzip = "1".equals(ss.get(1)) ;
		byte[] pld_bs = null ;
		if(bzip)
			pld_bs = unzip(bs,pld_idx,pld_len) ;
		else
		{
			pld_bs = new byte[pld_len] ;
			System.arraycopy(bs, pld_idx, pld_bs, 0, pld_len);
		}
		
		Object payload = null ;
		UAVal.ValTP tp = UAVal.getValTp(vtp) ;
		if(tp!=null)
		{
			String pldstr = new String(pld_bs,"utf-8") ;
			payload = UAVal.transStr2ObjVal(tp, pldstr) ;
		}
		else if(vtp.equals("jo"))
		{
			String pldstr = new String(pld_bs,"utf-8") ;
			payload = new JSONObject(pldstr) ;
		}
		else if(vtp.equals("jarr"))
		{
			String pldstr = new String(pld_bs,"utf-8") ;
			payload = new JSONArray(pldstr) ;
		}
		else if(vtp.equals("bs"))
		{
			payload = pld_bs ;
		}
		else
			return null ;
		
		return new ValPack(payload,bzip) ;
	}
	
	static byte[] zip(byte[] input) throws UnsupportedEncodingException
	{
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		
		deflater.setInput(input);
		
		deflater.finish();

		final byte[] bytes = new byte[256];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);

		while (!deflater.finished())
		{
		
			int length = deflater.deflate(bytes);
			outputStream.write(bytes, 0, length);
		}
		deflater.end();
		return outputStream.toByteArray();
	}

	/**
	 * 
	 * @throws DataFormatException
	 */
	public static byte[] unzip(byte[] ziped,int offset,int len) throws Exception
	{

		Inflater inflater = new Inflater();
		inflater.setInput(ziped,offset,len);
		final byte[] bytes = new byte[256];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
		try
		{
			while (!inflater.finished())
			{
				int length = inflater.inflate(bytes);
				outputStream.write(bytes, 0, length);
			}
		}
		finally
		{
			inflater.end();
		}

		return outputStream.toByteArray();
	}
}
