package org.iottree.ext.av;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.nodes.NM_Exec;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

/**
 * voice recognizer
 * 
 * @author jason.zhu
 *
 */
public class VoReco_NM extends MNNodeMid
{
	static ILogger log = LoggerManager.getLogger(VoReco_NM.class);

	static Lan lan = Lan.getLangInPk(VoReco_NM.class);

	static final int SAMPLE_RATE = 16000; // Vosk

	public static class ModelItem
	{
		public String name;

		public String title;

		private String dirName;

		private ModelItem(String n, String t, String dir)
		{
			this.name = n;
			this.title = t;
			if (Convert.isNullOrEmpty(this.title))
				this.title = n;
			this.dirName = dir;
		}

		public String getFullPath()
		{
			return Config.getDataDirBase() + "/vosk/models/" + dirName + "/";
		}

		public static ModelItem fromJO(JSONObject jo)
		{
			String n = jo.optString("n");
			String t = jo.optString("t");
			String dir = jo.optString("dir");
			if (Convert.isNullOrEmpty(n) || Convert.isNullOrEmpty(dir))
				return null;
			return new ModelItem(n, t, dir);
		}
	}

	private static LinkedHashMap<String, ModelItem> name2model = null;

	public static LinkedHashMap<String, ModelItem> listModelItems()
	{
		if (name2model != null)
			return name2model;

		LinkedHashMap<String, ModelItem> ret = new LinkedHashMap<>();
		try
		{
			File listf = new File(Config.getDataDirBase() + "/vosk/models/list.json");
			if (!listf.exists())
				return name2model = ret;
			JSONObject jo = Convert.readFileJO(listf);
			JSONArray jarr = jo.optJSONArray("models");
			for (int i = 0; i < jarr.length(); i++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i);
				ModelItem m = ModelItem.fromJO(tmpjo);
				if (m == null)
					continue;
				ret.put(m.name, m);
			}
			return name2model = ret;
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
			return name2model = ret;
		}
	}

	private static LinkedHashMap<String, Model> name2md = new LinkedHashMap<>();

	public static Model getOrLoadModel(String lang_model) throws IOException
	{
		Model m = name2md.get(lang_model);
		if (m != null)
			return m;

		ModelItem mi = listModelItems().get(lang_model);
		LibVosk.setLogLevel(LogLevel.WARNINGS); // 设置日志级别
		//long st = System.currentTimeMillis();
		String fullp = mi.getFullPath() ;
		//System.out.println("fp="+fullp) ;
		m = new Model(fullp);
		name2md.put(lang_model, m);
		return m;
	}

	/**
	 * key def id and related words
	 * 
	 * @author jason.zhu
	 *
	 */
	public static class KeyDefItem
	{
		public String key;

		public List<String> words;

		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject();
			ret.put("k", key);
			ret.putOpt("v", Convert.combineWith(words, ','));
			return ret;
		}

		public static KeyDefItem fromJO(JSONObject jo)
		{
			String k = jo.optString("k");
			if (Convert.isNullOrEmpty(k))
				return null;
			String v = jo.optString("v");
			KeyDefItem ret = new KeyDefItem();
			ret.key = k;
			ret.words = Convert.splitStrWith(v, ",，|");
			return ret;
		}
	}
	
	/**
	 * multi word with space to combined for one obj (related to one key)
	 * @author jason.zhu
	 *
	 */
	public static class Words implements Comparable<Words>
	{
		List<String> words = null ;
		
		String key ;
		
		String wordsStr; 
		
		private Words(List<String> ss,String key,String wds_str)
		{
			this.words = ss;
			this.key = key ;
			this.wordsStr = wds_str ;
		}
		/**
		 * 
		 * @param in_wd_list input words list (reco result)
		 * @return
		 */
		public boolean checkMatch(List<String> in_wd_list,int offset)
		{
			int leftn = in_wd_list.size()-offset ;
			int ws ;
			if(leftn<(ws=words.size()))
				return false;
			for(int i = 0 ; i < ws ; i ++)
			{
				String w = words.get(i) ;
				String in_w = in_wd_list.get(offset+i) ;
				if(!w.equals(in_w))
					return false;
			}
			return true ;
		}
		
		public static Words parseFromStr(String str,String key)
		{
			List<String> ss = Convert.splitStrWith(str, " ") ;
			if(ss==null||ss.size()<=1)
				return null ;
			return new Words(ss,key,str) ;
		}
		
		@Override
		public int compareTo(Words o)
		{
			return o.words.size() - this.words.size() ; //desc order
		}
	}

	String langModel = "cn";

	private transient ModelItem modelItem = null;
	private transient Recognizer recognizer = null;

	// ArrayList<String> gramWords = null ;

	boolean bEnLimit = false;

	LinkedHashMap<String, KeyDefItem> key2def = null;

	private transient HashMap<String, String> word2key = null;
	private transient ArrayList<Words> wordsKeyList = null;

	private void calcWord2Key(HashMap<String, String> wd2k,ArrayList<Words> wdsk)
	{
		if (key2def == null)
			return ;
		for (KeyDefItem kdi : key2def.values())
		{
			if (kdi.words == null)
				continue;
			for (String wd : kdi.words)
			{
				Words wds = Words.parseFromStr(wd,kdi.key) ;
				if(wds!=null)
					wdsk.add(wds);
				else
					wd2k.put(wd,kdi.key) ;
			}
		}
		
		Collections.sort(wdsk); //desc order words,size big first
	}
	
	@Override
	public int getOutNum()
	{
		return 2;
	}

	@Override
	public String getTP()
	{
		return "vo_reco";
	}

	@Override
	public String getTPTitle()
	{
		return g("vo_reco");
	}

	@Override
	public String getColor()
	{
		return "#11caff";
	}

	@Override
	public String getIcon()
	{
		return "\\uf130";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (Convert.isNullOrEmpty(this.langModel))
		{
			failedr.append("no language model set");
			return false;
		}

		modelItem = listModelItems().get(this.langModel);
		if (modelItem == null)
		{
			failedr.append("no model found with name=" + this.langModel);
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("lang_m", this.langModel);
		jo.put("en_limit", this.bEnLimit);

		if (key2def != null && key2def.size() > 0)
		{
			JSONArray jarr = new JSONArray();
			for (KeyDefItem kd : key2def.values())
			{
				jarr.put(kd.toJO());
			}
			jo.put("limit_kvs", jarr);
		}
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.langModel = jo.optString("lang_m");
		this.bEnLimit = jo.optBoolean("en_limit", false);
		JSONArray jarr = jo.optJSONArray("limit_kvs");
		int n = 0;
		LinkedHashMap<String, KeyDefItem> id2kv = new LinkedHashMap<>();
		if (jarr != null && (n = jarr.length()) > 0)
		{
			for (int i = 0; i < n; i++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i);
				KeyDefItem kd = KeyDefItem.fromJO(tmpjo);
				if (kd == null)
					continue;
				id2kv.put(kd.key, kd);
			}
		}
		this.key2def = id2kv;

		this.word2key = new HashMap<>() ;
		this.wordsKeyList = new ArrayList<>() ;
		
		calcWord2Key(this.word2key,this.wordsKeyList);
		//
		// ArrayList<String> ss = new ArrayList<>() ;
		// ss.add("打开");
		// ss.add("关闭");
		// ss.add("空调");
		// ss.add("电灯");
		// ss.add("阀门");
		// ss.add("开始");
		// ss.add("浇水");
		//
		// this.gramWords = ss ;
		//
	}

	@Override
	public String getPmTitle()
	{
		if (Convert.isNullOrEmpty(this.langModel))
			return null;
		modelItem = listModelItems().get(this.langModel);
		if (modelItem == null)
			return null;
		return modelItem.title;
	}

	private JSONArray getGramWords()
	{
		HashMap<String,String> wd2k = new HashMap<>() ;
		ArrayList<Words> wdsk_list = new ArrayList<>() ;
		
		calcWord2Key(wd2k,wdsk_list);
		
		JSONArray jarr = new JSONArray(wd2k.keySet());
		for(Words wds:wdsk_list)
			jarr.putAll(wds.words) ;
		return jarr;
	}

	private static final String LOG_SEG = "Ignoring word missing in vocabulary:";

	public static List<String> checkErrorWords(String lang_model, List<String> words,StringBuilder failedr) throws IOException
	{
		ModelItem mi = listModelItems().get(lang_model);
		if(mi==null)
		{
			failedr.append("no model found with name="+lang_model) ;
			return null ;
		}
		
		ArrayList<String> rets = new ArrayList<>();
		if (words == null || words.size() <= 0)
			return rets;

		String classpath = System.getProperty("java.class.path") ;
		String cmd = "\""+Config.getJavaExePath()+"\" -Dfile.encoding=UTF-8 -cp \""+classpath+"\" org.iottree.ext.vo.VoRecoChker " ;
		cmd += " \""+ mi.getFullPath() +"\"";
		File work_dir = new File(Config.getDataDirBase()+"/vosk/checker/") ;
		File words_f = new File(work_dir,"words.txt") ;
		
		try
		{
			if(!work_dir.exists())
				work_dir.mkdirs() ;
			
			Convert.writeFileTxt(words_f, Convert.combineWith(words, ','));
			RTOut rto = NM_Exec.RT_runCmd(cmd, work_dir, "UTF-8", 10000) ;
			MNMsg msg = rto.getOutMsg(0) ;
			if(msg==null)
			{
				failedr.append("no check out result found") ;
				return null ;
			}
			String pld = msg.getPayloadStr() ;
			String[] lines = pld.split("\n");

			for (String ln : lines)
			{
				ln = ln.trim();
				int k = ln.indexOf(LOG_SEG);
				if (k < 0)
					continue;
				String ends = ln.substring(k + LOG_SEG.length()).trim();
				k = ends.indexOf('\'') ;
				if(k<0)
					continue ;
				int ke = ends.indexOf('\'', k+1) ;
				if(ke<0)
					continue ;
				ends = ends.substring(k+1,ke) ;
				rets.add(ends);
			}
			return rets;
		}
		catch(Exception ee)
		{
			if(log.isDebugEnabled())
				log.debug(ee);
			failedr.append(ee.getMessage()) ;
			return null ;
		}
	}
	
//	private static byte[] generateTone(double frequency, int durationMs) {
//        int sampleRate = 16000;
//        int samples = (int)(sampleRate * durationMs / 1000.0);
//        byte[] output = new byte[samples * 2]; // 16-bit = 2 bytes
//        
//        for (int i = 0; i < samples; i++) {
//            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
//            short value = (short)(Short.MAX_VALUE * Math.sin(angle));
//            
//            // 小端序
//            output[2*i] = (byte)(value & 0xFF);
//            output[2*i+1] = (byte)((value >> 8) & 0xFF);
//        }
//        return output;
//    }
//	
//	public static List<String> checkErrorWords0(String lang_model, List<String> words) throws IOException
//	{
//		ArrayList<String> rets = new ArrayList<>();
//		if (words == null || words.size() <= 0)
//			return rets;
//
//		Model model = getOrLoadModel(lang_model);
//		if (model == null)
//			return rets;
//
//		// 创建零长度音频（静音）
//		byte[] tone = generateTone(1000, 500); // 1000Hz, 500ms //new byte[3200]; // 200ms 16kHz 16位单声道
//
//		Recognizer reco = null;
//		try
//		{
//			reco = new Recognizer(model, SAMPLE_RATE);
//			reco.setMaxAlternatives(3);
//			//reco.
//			for (String wd : words)
//			{
//				reco.reset();
//				reco.setGrammar("[\"" + wd + "\"]");
//				boolean b_git = false;
//				if (reco.acceptWaveForm(tone, tone.length))
//				{
//					String result = reco.getResult();
//					if (Convert.isNotNullEmpty(result))
//					{
//						JSONObject jo = new JSONObject(result);
//						String txt = jo.optString("text");
//						b_git = Convert.isNotNullEmpty(txt) ;
//					}
//				}
//				if(!b_git)
//					rets.add(wd) ;
//			}
//			return rets;
//		}
//		finally
//		{
//			if (reco != null)
//				reco.close();
//		}
//
//	}
	// private String getCmdGram()
	// {
	// // 控制命令语法
	// String gram = "#BNF+EM V2.1;\n" +
	// "grammar commands;\n" +
	// "<action> = 打开 | 关闭 | pause | resume | open | close | increase |
	// decrease;\n" +
	// "<object> = light | fan | music | volume | window | door;\n" +
	// "<command> = [please] <action> <object>;\n" +
	// "public <command>;";
	// }

	@Override
	protected void RT_onBeforeNetRun()
	{
		try
		{

			// if(gramWords!=null&&gramWords.size()>0)
			// {
			// JSONArray jarr = new JSONArray(gramWords) ;
			// recognizer.setGrammar(jarr.toString());
			// }
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
		}
	}

	private synchronized Recognizer acqReco() throws Exception
	{
		if (recognizer != null)
			return recognizer;

		Model model = getOrLoadModel(this.langModel);
		recognizer = new Recognizer(model, SAMPLE_RATE);
		// long et2 = System.currentTimeMillis() ;
		// System.out.println(" load reco cost="+(et2-et1)) ;

		if (this.bEnLimit)
		{
			JSONArray jarr = getGramWords();
			// System.out.println("---1--");
			if (jarr != null)
			{
				recognizer.setGrammar(jarr.toString());
				recognizer.setWords(true);
			}
			// System.out.println("---2--");
		}

		return recognizer;
	}

	@Override
	protected synchronized void RT_onAfterNetStop()
	{
		if (recognizer != null)
		{
			recognizer.close();
			recognizer = null;
		}
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		byte[] vo_bs = msg.getBytesArray();
		if (vo_bs == null)
			return null;

		Recognizer reco = acqReco();

		String result = null;
		synchronized (this)
		{
			if (reco == null)
				return null;

			//audio data in PCM 16-bit mono format
			if (reco.acceptWaveForm(vo_bs, vo_bs.length))
			{
				// full result
				result = reco.getResult();
			}
			else
			{
				// partial result
				// String partial =
				// recognizer.getPartialResult();
				// if (!partial.contains("\"partial\" : \"\""))
				// {
				// System.out.println("" + partial);
				// }
			}
		}

		
		if (Convert.isNullOrEmpty(result))
			return null;

		//
		
		JSONObject jo = new JSONObject(result);
		String txt = jo.optString("text");
		if (Convert.isNullOrEmpty(txt))
			return null;
		
		if(log.isDebugEnabled())
			log.debug("识别结果: " + result);
		
		MNMsg mtxt = new MNMsg().asPayload(txt);
		RTOut rto = RTOut.createOutIdx().asIdxMsg(0, mtxt);
		if(this.bEnLimit)
		{//[{"key":"k1","word":"xxxx"},{"key":"2","word":"yyy"}]
			JSONArray jarr = jo.optJSONArray("result") ; //
			ArrayList<String> inp_wds = new ArrayList<>() ; //extract all reco words
			if(jarr!=null)
			{
				int nn = jarr.length() ;
				for(int i = 0 ; i < nn ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					String wd = tmpjo.optString("word") ;
					if(Convert.isNullOrEmpty(wd))
						continue ;
					inp_wds.add(wd) ;
				}
			}
			
			JSONArray kws = new JSONArray() ;
			int nn = inp_wds.size() ;
			for(int offset = 0 ; offset < nn ; )
			{
				//match words first
				boolean words_m = false;
				for(Words wdsk:this.wordsKeyList)
				{
					if(wdsk.checkMatch(inp_wds, offset))
					{
						offset += wdsk.words.size() ; //offset chg
						JSONObject jo0 = new JSONObject() ;
						jo0.put("key",wdsk.key) ;
						jo0.put("word",wdsk.wordsStr) ;
						kws.put(jo0) ;
						words_m = true;
						break ;
					}
				}
				if(words_m)
					continue ;
				
				String wd = inp_wds.get(offset) ;
				if(Convert.isNullOrEmpty(wd))
				{
					offset ++ ;
					continue ;
				}
				
				String k = this.word2key.get(wd) ;
				if(Convert.isNullOrEmpty(k))
				{
					offset ++ ;
					continue ;
				}
				
				JSONObject jo0 = new JSONObject() ;
				jo0.put("key",k) ;
				jo0.put("word",wd) ;
				kws.put(jo0) ;
				offset ++ ;
			}
			
			if(kws.length()>0)
				rto.asIdxMsg(1, new MNMsg().asPayloadJO(kws)) ;
		}
		
		return rto ;
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		switch (idx)
		{
		case 0:
			return "xx xxx" ;
		case 1:
			return "[{\"key\":\"k1\",\"word\":\"xxxx\"},{\"key\":\"2\",\"word\":\"yyy\"}]";
		}
		return null;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		switch (idx)
		{
		case 1:
			return "blue";
		}
		return null ;
	}

}
