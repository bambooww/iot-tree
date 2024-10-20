package org.iottree.core.station;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.iottree.core.UACh;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UANodeOCTagsGCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
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
	public void RT_onRecvedInPlatform(PlatformWSServer.SessionItem si, PStation ps) throws Exception
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

		String p_prjname = ps.getId()+"_"+prjname ;
		UAPrj platform_prj = UAManager.getInstance().getPrjByName(p_prjname);
		if (platform_prj == null)
			return;

		byte[] bs = this.getCmdData();
		String jostr = unzip(bs);
		JSONObject rt_jo = new JSONObject(jostr);// this.getCmdDataJO() ;

		// platform_prj.RT_platform_RTSet(rt_jo) ;
		if (rt_jo != null && !bhis)
			updateCxtDyn(platform_prj, rt_jo);
		
		//如何发送的Platform后端进行后续的使用？
		PlatformManager.getInstance().onRecvedRTData(ps,prjname,key,rt_jo,bhis) ;
	}
	
	/**
	 * 专门支持批量处理历史数据的函数，主要是写数据库批处理，以提升性能
	 * @param rds
	 * @param ps
	 * @throws Exception 
	 */
	public static void onRecvedMultiHisInPlatform(List<PSCmdPrjRtData> rds,PStation ps) throws Exception
	{
		HashMap<String,HashMap<String,JSONObject>> prj_k2jo = new HashMap<>() ;
		//分表处理
		for(PSCmdPrjRtData rd:rds)
		{
			String prjname = rd.getParamByIdx(0);
			String key = rd.getParamByIdx(1) ;
			if (Convert.isNullOrEmpty(prjname) || Convert.isNullOrEmpty(key))
				return;
			String p_prjname = ps.getId()+"_"+prjname ;
			UAPrj platform_prj = UAManager.getInstance().getPrjByName(p_prjname);
			if (platform_prj == null)
				return;

			byte[] bs = rd.getCmdData();
			
			try
			{
				String jostr = unzip(bs);
				JSONObject rt_jo = new JSONObject(jostr);// this.getCmdDataJO() ;
				
				HashMap<String,JSONObject> k2jo = prj_k2jo.get(prjname) ;
				if(k2jo==null)
				{
					k2jo = new HashMap<>() ;
					prj_k2jo.put(prjname,k2jo) ;
				}
				k2jo.put(key,rt_jo) ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		for(Map.Entry<String, HashMap<String,JSONObject>> prj2kjo:prj_k2jo.entrySet())
		{
			String prjname = prj2kjo.getKey() ;
			HashMap<String,JSONObject> k2jo = prj2kjo.getValue() ;
			PlatformManager.getInstance().onRecvedHisRTDatas(ps, prjname, k2jo);
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

	// private void onNodeSharePush(String jsonstr) throws Exception
	// {
	// try
	// {
	// if(log.isDebugEnabled())
	// log.debug("onNodeSharePush="+jsonstr);
	// UACh ch = this.getJoinedCh();
	// if(ch==null)
	// return ;
	//
	// if(Convert.isNullOrEmpty(jsonstr))
	// return ;
	// JSONObject jo = new JSONObject(jsonstr);
	// shareWritable = jo.optBoolean("share_writable", false) ;
	// shareDT = jo.optLong("share_dt", -1) ;
	// //if(log.isDebugEnabled())
	// // log.debug("onNodeSharePush before updateChCxtDyn");
	// updateChCxtDyn(ch,jo);
	// }
	// finally
	// {
	// lastPushDT = System.currentTimeMillis();
	// }
	// }

	private static final int MAX_NUM = 5;

	/**
	 * record tag update info for later timeout checking
	 * 
	 * @author jason.zhu
	 *
	 */
	private static class Tag2Up
	{
		UATag tag;

		LinkedList<UAVal> prevVals = new LinkedList<>();

		public Tag2Up(UATag tag, UAVal val)
		{
			this.tag = tag;
			this.prevVals.addLast(val);
		}

		public void putVal(UAVal v)
		{
			prevVals.addLast(v);

			if (prevVals.size() > MAX_NUM)
				prevVals.removeFirst();
		}

		public UAVal getLastVal()
		{
			return prevVals.getLast();
		}
	}

	private transient HashMap<String, Tag2Up> tagp2upMap = new HashMap<>();

	private void setToBuf(UATag tag, UAVal val)
	{
		String tagp = tag.getNodeCxtPathInPrj();
		Tag2Up t2u = tagp2upMap.get(tagp);
		if (t2u != null)
		{
			t2u.putVal(val);
			return;
		}

		t2u = new Tag2Up(tag, val);
		tagp2upMap.put(tagp, t2u);
		return;
	}

	private void setTagErrInBuf(long dt)
	{
		for (Tag2Up t2u : tagp2upMap.values())
		{
			UAVal lastv = t2u.getLastVal();
			if (!lastv.isValid())
				continue; //
			UATag tag = t2u.tag;

			UAVal uav = new UAVal(false, null, dt, dt);
			tag.RT_setUAVal(uav);

			setToBuf(tag, uav);
		}
	}

	private void updateCxtDyn(UANodeOCTagsCxt p, JSONObject curcxt)
	{
		JSONArray jos = curcxt.optJSONArray("tags");
		if (jos != null)
		{
			for (int i = 0, n = jos.length(); i < n; i++)
			{
				JSONObject tg = jos.getJSONObject(i);
				String name = tg.getString("n");
				UATag tag = p.getTagByName(name);
				if (tag == null) // || tag.isSysTag())
					continue;
				// var tagp =p+n ;
				boolean bvalid = tg.optBoolean("valid", false);
				long dt = tg.optLong("dt", -1);
				long chgdt = tg.optLong("chgdt", -1);

				Object ov = tg.opt("v");
				String strv = "";
				if (ov != null && ov != JSONObject.NULL)
					strv = "" + ov;
				// set to cxt
				ov = UAVal.transStr2ObjVal(tag.getValTp(), strv);
				UAVal uav = new UAVal(bvalid, ov, dt, chgdt);
				// tag.RT_setValStr(strv, true);
				tag.RT_setUAVal(uav);

				setToBuf(tag, uav);
			}
		}

		JSONArray subs = curcxt.optJSONArray("subs");
		if (subs != null)
		{
			for (int i = 0, n = subs.length(); i < n; i++)
			{
				JSONObject sub = subs.getJSONObject(i);

				String subn = sub.getString("n");

				UANode uan = p.getSubNodeByName(subn);
				if (uan == null)
					continue;
				if (!(uan instanceof UANodeOCTagsCxt))
					continue;

				updateCxtDyn((UANodeOCTagsCxt) uan, sub);
			}
		}
	}
}
