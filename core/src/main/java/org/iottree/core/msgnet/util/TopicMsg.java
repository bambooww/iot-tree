package org.iottree.core.msgnet.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * for transmit pk
 * 
 * @author jason.zhu
 *
 */
public class TopicMsg
{
	String topic ;
	
	Object payload ;
	
	boolean bzip = false;
	
	public TopicMsg(String topic,Object pld,boolean b_zip)
	{
		if(topic.indexOf("|")>=0)
			throw new IllegalArgumentException("topic cannot has char |") ;
		this.topic = topic ;
		this.payload = pld ;
		this.bzip = b_zip ;
	}
	
	public String getTopic()
	{
		return this.topic ;
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
		return this.topic+"|" + getPayloadTP()+"|"+bzip;
	}
	
	public byte[] pkOut() throws UnsupportedEncodingException
	{
		String pld_tp = null ;
		byte[] pld_bs = null ;
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
		
		byte[] hd = (this.topic+"|" + pld_tp+"|"+bzip+"\n").getBytes("utf-8") ;
		if(pld_bs==null)
			return hd ;
		if(bzip)
			pld_bs = zip(pld_bs) ;
		byte[] ret = new byte[hd.length+pld_bs.length] ;
		System.arraycopy(hd, 0, ret, 0, hd.length);
		System.arraycopy(pld_bs, 0, ret, hd.length, pld_bs.length);
		return ret ;
	}
	
	public static TopicMsg parseFrom(byte[] bs) throws Exception
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
		if(ss.size()!=3)
			return null ;
		String topic = ss.get(0) ;
		String vtp = ss.get(1) ;
		
		boolean bzip = "true".equals(ss.get(2)) ;
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
		
		return new TopicMsg(topic,payload,bzip) ;
	}
	
	static byte[] zip(byte[] input) throws UnsupportedEncodingException
	{
		/**
		 * https://www.yiibai.com/javazip/javazip_deflater.html#article-start 0
		 * ~ 9 压缩等级 低到高 public static final int BEST_COMPRESSION = 9; 最佳压缩的压缩级别。
		 * public static final int BEST_SPEED = 1; 压缩级别最快的压缩。 public static
		 * final int DEFAULT_COMPRESSION = -1; 默认压缩级别。 public static final int
		 * DEFAULT_STRATEGY = 0; 默认压缩策略。 public static final int DEFLATED = 8;
		 * 压缩算法的压缩方法(目前唯一支持的压缩方法)。 public static final int FILTERED = 1;
		 * 压缩策略最适用于大部分数值较小且数据分布随机分布的数据。 public static final int FULL_FLUSH = 3;
		 * 压缩刷新模式，用于清除所有待处理的输出并重置拆卸器。 public static final int HUFFMAN_ONLY = 2;
		 * 仅用于霍夫曼编码的压缩策略。 public static final int NO_COMPRESSION = 0; 不压缩的压缩级别。
		 * public static final int NO_FLUSH = 0; 用于实现最佳压缩结果的压缩刷新模式。 public
		 * static final int SYNC_FLUSH = 2; 用于清除所有未决输出的压缩刷新模式; 可能会降低某些压缩算法的压缩率。
		 */

		// 使用指定的压缩级别创建一个新的压缩器。
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		// 设置压缩输入数据。
		deflater.setInput(input);
		// 当被调用时，表示压缩应该以输入缓冲区的当前内容结束。
		deflater.finish();

		final byte[] bytes = new byte[256];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);

		while (!deflater.finished())
		{
			// 压缩输入数据并用压缩数据填充指定的缓冲区。
			int length = deflater.deflate(bytes);
			outputStream.write(bytes, 0, length);
		}
		// 关闭压缩器并丢弃任何未处理的输入。
		deflater.end();
		return outputStream.toByteArray();
	}

	/**
	 * 解压缩
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
			// finished() 如果已到达压缩数据流的末尾，则返回true。
			while (!inflater.finished())
			{
				// 将字节解压缩到指定的缓冲区中。
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
