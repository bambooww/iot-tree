package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.ValEvent;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.nodes.NS_TagEvtTrigger.MsgOutSty;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class NS_TagChgTrigger extends MNNodeStart
{
	static ILogger log = LoggerManager.getLogger(NS_TagChgTrigger.class) ;
	
	// String tagId = null ;
	public static enum ChgTP
	{
		all_chg(0), all_valid_chg(1),up_down(2), up(3), down(4);

		final int val;

		ChgTP(int v)
		{
			this.val = v;
		}

		public int getIntVal()
		{
			return val;
		}

		public String getTitle()
		{
			switch (this.val)
			{
			case 0:
				return "All Change"; // include invalid
			case 1:
				return "All Valid Change"; // change compare to last (last is
											// invalid)
			case 2:
				return "Up and Down"; // change compare to last
			case 3:
				return "Up"; // bigger than last
			case 4:
				return "Down"; // small than last
			default:
				return null;
			}
		}

		/**
		 * current val is valid,and
		 * 
		 * @param last_vi
		 * @param cur_valid
		 * @param curval
		 * @return
		 */
//		public boolean checkChgTrigger(ValItem last_vi, boolean cur_valid, Object curval)
//		{
//			if (!cur_valid)
//			{
//				return this == all_chg;
//			}
//
//			Object lastv = null;
//			if (last_vi != null && last_vi.bValid)
//				lastv = last_vi.val;
//			if (lastv == null)
//			{
//				switch (this)
//				{
//				case all_chg:
//				case all_valid_chg:
//					return true;
//				default:
//					return false;
//				}
//			}
//
//			int comp_res = compareCurLastUpDown(curval, lastv);
//			// valid = true
//			switch (this)
//			{
//			case up:
//				return comp_res > 0;
//			case down:
//				return comp_res < 0;
//			case up_down:
//				return comp_res != 0;
//			default:
//				return false;
//			}
//		}
		
		public boolean checkChgTrigger(UAVal last_v, boolean cur_valid, Object curval)
		{
			if (!cur_valid)
			{
				return this == all_chg;
			}

			Object lastv = null;
			if (last_v != null && last_v.isValid())
				lastv = last_v.getObjVal();
			if (lastv == null)
			{
				if(curval!=null)
				{
					switch (this)
					{
					case all_chg:
					case all_valid_chg:
						return true;
					default:
						return false;
					}
				}
				return false;
			}

			int comp_res = compareCurLastUpDown(curval, lastv);
			// valid = true
			switch (this)
			{
			case all_chg:
			case all_valid_chg:
				return comp_res != 0;
			case up:
				return comp_res > 0;
			case down:
				return comp_res < 0;
			case up_down:
				return comp_res != 0;
			default:
				return false;
			}
		}
		
		

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static int compareCurLastUpDown(Object curval, Object lastv)
		{
			if (curval instanceof Boolean)
			{
				Boolean cur_b = (Boolean) curval;
				Boolean last_b = (Boolean) lastv;
				return cur_b.compareTo(last_b);
			}

			if (curval instanceof Number && lastv instanceof Number)
			{
				Number cur_n = (Number) curval;
				Number last_n = (Number) lastv;
				if (cur_n instanceof Double || last_n instanceof Double)
				{
					return Double.compare(cur_n.doubleValue(), last_n.doubleValue());
				}
				else if (cur_n instanceof Float || last_n instanceof Float)
				{
					return Float.compare(cur_n.floatValue(), last_n.floatValue());
				}
				else if (cur_n instanceof Long || last_n instanceof Long)
				{
					return Long.compare(cur_n.longValue(), last_n.longValue());
				}
				else if (cur_n instanceof Integer || last_n instanceof Integer)
				{
					return Integer.compare(cur_n.intValue(), last_n.intValue());
				}
				else
				{
					// other
					return Double.compare(cur_n.doubleValue(), last_n.doubleValue());
				}
			}
			
			if(curval instanceof Comparable) // || lastv instanceof Comparable)
			{
				return ((Comparable)curval).compareTo(lastv) ;
			}
			
			return 0 ;
		}

		public static ChgTP fromIntVal(int v)
		{
			switch (v)
			{
			case 0:
				return all_chg;
			case 1:
				return all_valid_chg;
			case 2:
				return up_down;
			case 3:
				return up;
			case 4:
				return down;
			default:
				return null;
			}
		}
	}

	boolean ignoreInvalid = true;

	/**
	 * Tag value is not changed ,only updated date
	 */
	private boolean ignoreUpdateOnly = false;

	ArrayList<String> tagPaths = null;

	ChgTP chgTP = ChgTP.all_chg;
	
	boolean enableDelay = false;
	
	long delayMS = -1 ;
	
	boolean enableLog = false;

	transient private HashSet<String> tagIdSet = null;

	// transient private List<UATag> tags = null ;

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "tag_chg";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_chg");
	}

	@Override
	public String getColor()
	{
		return "#a1cbde";
	}

	@Override
	public String getIcon()
	{
		return "PK_trigger";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (tagPaths == null || tagPaths.size() <= 0)
		{
			failedr.append("no Tags set");
			return false;
		}

		HashSet<String> idset = getTagIdSet();
		if (idset == null || idset.size() <= 0)
		{
			failedr.append("no Tag found");
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.put("ignore_invalid", ignoreInvalid);
		jo.put("ignore_update", ignoreUpdateOnly);
		jo.putOpt("tag_paths", this.tagPaths);
		if (chgTP != null)
			jo.put("chg_tp", chgTP.val);
		jo.put("bdelay", this.enableDelay) ;
		jo.put("delay_ms",this.delayMS) ;
		jo.put("blog", this.enableLog) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("tag_paths");
		if (jarr != null)
		{
			ArrayList<String> subts = new ArrayList<>();
			int n = jarr.length();
			for (int i = 0; i < n; i++)
				subts.add(jarr.getString(i));
			this.tagPaths = subts;
		}
		ignoreInvalid = jo.optBoolean("ignore_invalid", true);
		this.ignoreUpdateOnly = jo.optBoolean("ignore_update", false);
		int chg_tp = jo.optInt("chg_tp", 0);
		this.chgTP = ChgTP.fromIntVal(chg_tp);
		if (this.chgTP == null)
			this.chgTP = ChgTP.up_down;
		this.enableDelay = jo.optBoolean("bdelay",false) ;
		this.delayMS = jo.optLong("delay_ms", -1) ;
		this.enableLog = jo.optBoolean("blog",false) ;
		synchronized (this)
		{
			tagIdSet = null;
			// tags = null;
		}

		clearCache();
	}

//	private UAPrj getPrj()
//	{
//		IMNContainer mnc = this.getBelongTo().getBelongTo().getBelongTo();
//		if (mnc == null || !(mnc instanceof UAPrj))
//			return null;
//
//		return (UAPrj) mnc;
//	}

	public boolean isIgnoreInvalid()
	{
		return this.ignoreInvalid;
	}

	public ArrayList<String> getTagPaths()
	{
		return this.tagPaths;
	}

	public boolean checkFitTag(UATag tag)
	{
		if (this.tagPaths == null)
			return false;
		String np = tag.getNodeCxtPathInPrj();
		return this.tagPaths.contains(np);
	}

	// public synchronized List<UATag> getMonTags()
	// {
	//
	// }

	private synchronized HashSet<String> getTagIdSet()
	{
		if (tagIdSet != null)
			return tagIdSet;

		if (tagPaths == null || tagPaths.size() <= 0)
			return null;

		UAPrj prj = this.getPrj();
		if (prj == null)
			return null;

		// ArrayList<UATag> tags = new ArrayList<>(tagPaths.size()) ;
		HashSet<String> rets = new HashSet<>();
		for (String tagp : tagPaths)
		{
			UATag tag = prj.getTagByPath(tagp);
			if (tag == null)
				continue;
			rets.add(tag.getId());
		}
		tagIdSet = rets;
		return rets;
	}

	public boolean RT_fireDtUpValNotChg(UATag tag)
	{
		if (ignoreUpdateOnly)
			return false;

		HashSet<String> idset = getTagIdSet();
		if (idset == null)
			return false;
		if (!idset.contains(tag.getId()))
			return false;

		UAVal v = tag.RT_getVal();
		if (!v.isValid())
		{
			if (this.ignoreInvalid)
				return false;
		}

		sendTagOut(tag, v);
		return true;
	}

//	private static class ValItem
//	{
//		private boolean bValid = false;
//
//		private Object val = null;
//
//		ValItem(boolean valid, Object val)
//		{
//			this.bValid = valid;
//			this.val = val;
//		}
//	}

	//private transient Hashtable<String, ValItem> lastTagId2ValItem = new Hashtable<>();

	private void clearCache()
	{
		//lastTagId2ValItem = new Hashtable<>();
	}

	public boolean RT_fireValChg(UATag tag,UAVal lastv,boolean cur_valid,Object curval)
	{
		HashSet<String> idset = getTagIdSet() ;
		if(idset==null)
			return false;
		String tagid = tag.getId() ;
		if(!idset.contains(tagid))
			return false;
		
		try
		{
			UAVal v = tag.RT_getVal() ;
			if(!cur_valid)
			{
				if(this.ignoreInvalid)
					return false;
			}
			
			//ValItem last_vi = lastTagId2ValItem.get(tagid) ;
			if(!chgTP.checkChgTrigger(lastv,cur_valid,curval))
				return false;
			
			sendTagOut(tag,v) ;
			return true ;
		}
		finally
		{
			//lastTagId2ValItem.put(tagid,new ValItem(cur_valid,curval)) ;
		}
	}
	
	private transient boolean delaySending = false;
	
	private transient long delayST = -1;
	
	//private transient ScheduledExecutorService executor = null ;
	
	private void sendTagOut(UATag tag,UAVal v)
	{
		if(!enableDelay || delayMS<=0)
		{
			if(enableLog)
				log.warn("sendTagOutDo nor at="+System.currentTimeMillis());
			sendTagOutDo(tag, v);
			return ;
		}
		
		//
		synchronized(this)
		{
			if(delaySending)
			{
				if(System.currentTimeMillis()-this.delayST<this.delayMS*2)
					return ; //ignore
				//timeout do send again
			}
			
			delaySending = true;
			delayST = System.currentTimeMillis() ;
			if(enableLog)
				log.warn("sendTagOut delay at="+System.currentTimeMillis());
			DELAY_EXE.schedule(() -> {
				try
				{
					if(enableLog)
						log.warn("sendTagOutDo after "+delayMS+" at="+System.currentTimeMillis());
					sendTagOutDo(tag, v);
				}
				finally
				{
					delaySending = false;
				}
			}, delayMS, TimeUnit.MILLISECONDS);
		}
	}
	
	private void sendTagOutDo(UATag tag, UAVal v)
	{
		if (v == null)
			return;

		boolean valid = v.isValid();
		MNMsg msg = new MNMsg();
		JSONObject jo = new JSONObject();
		jo.put("tag_id", tag.getId());
		jo.put("tag_path", tag.getNodeCxtPathInPrj());
		jo.putOpt("tag_title", tag.getTitle());
		jo.put("updt", v.getValDT());
		jo.put("chgdt", v.getValChgDT());
		UAVal.ValTP vt = tag.getValTp();
		if (vt != null)
			jo.put("vt", vt.getStr());
		jo.put("valid", valid);
		if (valid)
			jo.putOpt("tag_val", v.getObjVal());
		else
			jo.putOpt("tag_err", v.getErr());
		msg.asPayload(jo);
		RT_sendMsgOut(RTOut.createOutAll(msg));
	}
	
	public static final ScheduledExecutorService DELAY_EXE = 
	        Executors.newScheduledThreadPool(5);
	
	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			DELAY_EXE.shutdown();
		    try {
		        // wait to normal shut down
		        if (!DELAY_EXE.awaitTermination(60, TimeUnit.SECONDS)) {
		        	DELAY_EXE.shutdownNow();
		        }
		    } catch (InterruptedException e) {
		    	DELAY_EXE.shutdownNow();
		    }
		}));
	}
}
