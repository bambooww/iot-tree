package org.iottree.core.station;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class PSCmdPrjRtData extends PSCmd
{
	public final static String CMD = "prj_rtdata";

	private String key = null ;
	
	private UAPrj prj = null ;
	
	@Override
	public String getCmd()
	{
		return CMD;
	}

	public PSCmdPrjRtData asStationLocalPrj(String key,UAPrj prj) throws IOException
	{
		this.key = key ;
		this.prj = prj ;
		
		this.asParams(Arrays.asList(prj.getName(),key));

		String rtjson = prj.JS_get_rt_json(false);
		byte[] bs = zipString(rtjson);
		this.asCmdData(bs);// (rtjson.getBytes("utf-8")) ;
		return this;
	}
	
	public PSCmdPrjRtData asHisData(boolean b_his)
	{
		this.asParams(Arrays.asList(prj.getName(),key,"his"));
		return this ;
	}
	
	public boolean isHis()
	{
		return "his".equals(this.getParamByIdx(2)) ;
	}
	
	//private static HashMap<String,Integer> prj2hiscc = new HashMap<>() ;

	@Override
	public void RT_onRecvedInPlatform(PlatInsWSServer.SessionItem si, PStation ps) throws Exception
	{
		String prjname = this.getParamByIdx(0);
		String key = this.getParamByIdx(1) ;
		if (Convert.isNullOrEmpty(prjname) || Convert.isNullOrEmpty(key))
			return;
		
		boolean bhis = "his".equals(this.getParamByIdx(2)) ;
		
//		if(bhis)
//		{
//			Integer cc = prj2hiscc.get(prjname) ;
//			int c =0;
//			if(cc!=null)
//				c = cc ;
//			c ++ ;
//			prj2hiscc.put(prjname,c) ;
//			System.out.println("recved his prj="+prjname+" cc="+c) ;
//		}

		//String p_prjname = ps.getId()+"_"+prjname ;
		UAPrj platform_prj = ps.getRelatedPrjByRStationPrjN(prjname); //UAManager.getInstance().getPrjByName(p_prjname);
		if (platform_prj == null)
			return;

		byte[] bs = this.getCmdData();
		String jostr = unzip(bs);
		JSONObject rt_jo = new JSONObject(jostr);// this.getCmdDataJO() ;

		// platform_prj.RT_platform_RTSet(rt_jo) ;
		
		
		PlatInsManager.getInstance().onRecvedRTData(ps,platform_prj,key,bs,rt_jo,bhis) ;
	}
	
	/**
	 * 专门支持批量处理历史数据的函数，主要是写数据库批处理，以提升性能
	 * 输入数据通过队列获取
	 * @param rds
	 * @param ps
	 * @throws Exception 
	 */
	public static void onRecvedMultiHisInPlatform(List<PSCmdPrjRtData> rds,PStation ps) throws Exception
	{
		HashMap<UAPrj,HashMap<String,byte[]>> prj_k2jo = new HashMap<>() ;
		//分表处理
		for(PSCmdPrjRtData rd:rds)
		{
			String prjname = rd.getParamByIdx(0);
			String key = rd.getParamByIdx(1) ;
			if (Convert.isNullOrEmpty(prjname) || Convert.isNullOrEmpty(key))
				return;
			
			UAPrj platform_prj = ps.getRelatedPrjByRStationPrjN(prjname);
			
//			String p_prjname = ps.getId()+"_"+prjname ;
//			UAPrj platform_prj = UAManager.getInstance().getPrjByName(p_prjname);
			if (platform_prj == null)
				return;

			byte[] bs = rd.getCmdData();
			
			try
			{
				//String jostr = unzip(bs);
				//JSONObject rt_jo = new JSONObject(jostr);// this.getCmdDataJO() ;
				
				HashMap<String,byte[]> k2jo = prj_k2jo.get(platform_prj) ;
				if(k2jo==null)
				{
					k2jo = new HashMap<>() ;
					prj_k2jo.put(platform_prj,k2jo) ;
				}
				k2jo.put(key,bs) ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		for(Map.Entry<UAPrj, HashMap<String,byte[]>> prj2kjo:prj_k2jo.entrySet())
		{
			UAPrj prj = prj2kjo.getKey() ;
			HashMap<String,byte[]> k2jo = prj2kjo.getValue() ;
			PlatInsManager.getInstance().onRecvedHisRTDatas(ps, prj, k2jo);
		}
	}

	static byte[] zipString(String unzipString) throws UnsupportedEncodingException
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
		deflater.setInput(unzipString.getBytes("UTF-8"));
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
	public static String unzip(byte[] ziped) throws Exception
	{

		Inflater inflater = new Inflater();
		inflater.setInput(ziped);
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

		return new String(outputStream.toByteArray(), "UTF-8");
	}


//	private static final int MAX_NUM = 5;
//
//	/**
//	 * record tag update info for later timeout checking
//	 * 
//	 * @author jason.zhu
//	 *
//	 */
//	private static class Tag2Up
//	{
//		UATag tag;
//
//		LinkedList<UAVal> prevVals = new LinkedList<>();
//
//		public Tag2Up(UATag tag, UAVal val)
//		{
//			this.tag = tag;
//			this.prevVals.addLast(val);
//		}
//
//		public void putVal(UAVal v)
//		{
//			prevVals.addLast(v);
//
//			if (prevVals.size() > MAX_NUM)
//				prevVals.removeFirst();
//		}
//
//		public UAVal getLastVal()
//		{
//			return prevVals.getLast();
//		}
//	}
//
//	private transient HashMap<String, Tag2Up> tagp2upMap = new HashMap<>();
//
//	private void setToBuf(UATag tag, UAVal val)
//	{
//		String tagp = tag.getNodeCxtPathInPrj();
//		Tag2Up t2u = tagp2upMap.get(tagp);
//		if (t2u != null)
//		{
//			t2u.putVal(val);
//			return;
//		}
//
//		t2u = new Tag2Up(tag, val);
//		tagp2upMap.put(tagp, t2u);
//		return;
//	}
//
//	private void setTagErrInBuf(long dt)
//	{
//		for (Tag2Up t2u : tagp2upMap.values())
//		{
//			UAVal lastv = t2u.getLastVal();
//			if (!lastv.isValid())
//				continue; //
//			UATag tag = t2u.tag;
//
//			UAVal uav = new UAVal(false, null, dt, dt);
//			tag.RT_setUAVal(uav);
//
//			setToBuf(tag, uav);
//		}
//	}

	
}
