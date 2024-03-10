package org.iottree.core.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONArray;
import org.json.JSONObject;

@data_class
public abstract class StoreHandler extends JSObMap //implements IJsProp
{
	static ILogger log = LoggerManager.getLogger(StoreHandler.class) ;
	
	public static abstract class TagsFilter
	{
		public List<UATag> filterTags(UAPrj prj)
		{
			ArrayList<UATag> rets = new ArrayList<>() ;
			for(UATag tag:prj.listTagsAll())
			{
				if(checkFit(tag))
					rets.add(tag) ;
			}
			return rets ;
		}
		
		public abstract String getTp() ;
		
		public abstract String getTpTitle() ;
		
		public abstract boolean checkFit(UATag tag) ;
		
		public abstract String toStr() ;
		
		abstract boolean fromSubStr(String str,StringBuilder faieldr) ;
		
	}
	
	
	public static class TF_All extends TagsFilter
	{
		String subTp = null ;
		
		
		@Override
		public String getTp()
		{
			return "all";
		}
		
		public String getTpTitle()
		{
			return "All" ;
		}
		
		public void setSubTp(String tp)
		{
			this.subTp = tp ;
		}
		
		public String getSubTp()
		{
			if(this.subTp==null)
				return "" ;
			
			return subTp ;
		}

		@Override
		public boolean checkFit(UATag tag)
		{
			if(Convert.isNullOrEmpty(subTp))
				return true ;
			switch(subTp)
			{
			case "nor":
				if(tag.isSysTag()) return false;
				if(tag.isMidExpress()) return false;
				return true ;
			case "sys":
				return tag.isSysTag() ;
			case "mid":
				return tag.isMidExpress() ;
			case "not_sys":
				return !tag.isSysTag() ;
			}
			return true;
		}
		
		public String toStr()
		{
			return "all-"+this.getSubTp() ;
		}
		
		boolean fromSubStr(String str,StringBuilder faildr)
		{
//			if(!str.startsWith("all-"))
//			{
//				faildr.append("invalid Filter All str="+str) ;
//				return false;
//			}
			this.subTp = str;// str.substring(4) ;
			return true ;
		}
	}
	
	public static class TF_Prefix extends TagsFilter
	{
		List<String> prefixs = new ArrayList<>() ;
		
		@Override
		public String getTp()
		{
			return "prefix";
		}
		
		public String getTpTitle()
		{
			return "Prefix" ;
		}
		
		public void setPrefixs(List<String> prefixs)
		{
			this.prefixs.addAll(prefixs);
		}
		
		public List<String> getPrefixs()
		{
			return this.prefixs ;
		}

		@Override
		public boolean checkFit(UATag tag)
		{
			if(prefixs==null||prefixs.size()<=0)
				return false;
			UAPrj prj = tag.getBelongToPrj() ;
			String np = tag.getNodeCxtPathTitleIn(prj) ;
			for(String p:prefixs)
			{
				if(np.startsWith(p))
					return true ;
			}
			return false;
		}
		
		public String toStr()
		{
			String str = Convert.combineStrWith(this.prefixs, ',') ;
			if(str==null)
				str = "" ;
			return "prefix-"+str ;
		}
		
		boolean fromSubStr(String str,StringBuilder faildr)
		{
//			if(!str.startsWith("prefix-"))
//			{
//				faildr.append("invalid Filter All str="+str) ;
//				return false;
//			}
//			String ss = str.substring(7) ;
			if(Convert.isNotNullEmpty(str))
			{
				this.prefixs = Convert.splitStrWith(str, ",") ;
			}
			return true ;
		}
	}

	public final static TagsFilter[] TFS = new TagsFilter[] {new TF_All(),new TF_Prefix()} ;
	
	public static TagsFilter parseFilter(String str,StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(str))
			return null ;
		int k = str.indexOf('-') ;
		if(k<=0)
			return null ;
		String tp = str.substring(0,k) ;
		String ss = str.substring(k+1) ;
		TagsFilter tf = null ;
		switch(tp)
		{
		case "all":
			tf = new TF_All();
			break ;
		case "prefix":
			tf = new TF_Prefix() ;
			break ;
		}
		if(tf==null)
			return null ;
		if(tf.fromSubStr(ss, failedr))
			return tf ;
		return null ;
	}
	
	// Store Handler
	
	static StoreHandler newInsByTp(String tp)
	{
		StoreHandler ao = null ;
		switch(tp)
		{
		case StoreHandlerRT.TP:
			ao = new StoreHandlerRT() ;
			break ;
		case StoreHandlerCond.TP:
			ao = new StoreHandlerCond() ;
			break ;
		case StoreHandlerInd.TP:
			ao = new StoreHandlerInd() ;
			break ;
//		case AlertOutUI.TP:
//			ao = new AlertOutUI() ;
//			break ;
		}
		return ao ;
	}
	@data_val
	String id = null ;
	
	@data_val(param_name = "en")
	boolean bEnable = true ;
	
	@data_val(param_name = "n")
	private String name = "" ;
	
	@data_val(param_name = "t")
	private String title = "" ;
	
	@data_val(param_name = "d")
	private String desc="" ;
	
	@data_val(param_name = "scan_intv")
	private long scanIntV = 60000;
	
//	TagsFilter tagsFilter = null ;
	
	/**
	 * select all or check the box
	 */
	@data_val(param_name = "sel_all")
	private boolean selectedAll = false ;
	
	private HashSet<String> selectedTags = null ;
	
	private boolean ignoreSys = false;
	
	
	
//	@data_val(param_name = "sel_tagids")
//	private String get_SelTagIds()
//	{
//		return Convert.combineStrWith(this.selectedTags,',') ; 
//	}
//	@data_val(param_name = "sel_tagids")
//	private void set_SelTagIds(String idstr)
//	{
//		if(Convert.isNullOrEmpty(idstr))
//		{
//			this.selectedTags = null ;
//			return ;
//		}
//		
//		List<String> ss = Convert.splitStrWith(idstr, ",") ;
//		this.selectedTags = new HashSet<>(ss) ;
//	}
	/**
	 *
	 */
	LinkedHashMap<String,StoreOut> id2out = new LinkedHashMap<>() ;
	
	transient UAPrj prj = null ;
	
	transient Thread th = null;
	
	public StoreHandler()
	{
		this.id = CompressUUID.createNewId() ;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return this.name ;
	}

	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public String getTitle()
	{
		return title;
	}

	public String getDesc()
	{
		return desc;
	}
	
	public long getScanIntV()
	{
		if(this.scanIntV<=0)
			return 60000;
		
		return scanIntV;
	}
	
	public abstract String getTp() ;
	
	public abstract String getTpTitle() ;
	
	public abstract boolean checkFilterFit(UATag tag) ;
	
	public abstract List<StoreOut> getSupportedOuts();
	
	public boolean isSelectAll()
	{
		return this.selectedAll ;
	}
	
	//private transient List<UATag> selTags = null ;
	
	public HashSet<String> getSelectTagIds()
	{
		return this.selectedTags ;
	}
	
	public void setSelectTags(Collection<String> tag_nps)
	{
		HashSet<String> ss = new HashSet<>() ;
		if(tag_nps!=null)
			ss.addAll(tag_nps) ;
		this.selectedTags =ss;
		
		selTags = null ;
	}
	
	
	public LinkedHashMap<String,StoreOut> getId2Out()
	{
		return this.id2out ;
	}
	
	public List<StoreOut> listOuts()
	{
		ArrayList<StoreOut> rets =new ArrayList<>(this.id2out.size()) ;
		rets.addAll(this.id2out.values()) ;
		return rets ;
	}
	
	public StoreOut getOutById(String id)
	{
		return this.id2out.get(id) ;
	}
	
	public StoreOut getOutByName(String n)
	{
		for(StoreOut so:this.id2out.values())
		{
			if(so.getName().equals(n))
				return so ;
		}
		return null ;
	}
	
	public void setOut(StoreOut so)
	{
		String n = so.getName() ;
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(n, true, sb))
			throw new IllegalArgumentException(sb.toString()) ;
		
		if(Convert.isNullOrEmpty(so.id))
			so.id = CompressUUID.createNewId();
		
		StoreOut old = this.getOutByName(so.getName()) ;
		if(old!=null && !old.getId().equals(so.getId()))
			 throw new IllegalArgumentException("Out with name="+n+" in handler is already existed!") ;
		so.belongTo = this ;
		this.id2out.put(so.id, so) ;
	}
	
	public StoreOut delOutById(String id)
	{
		StoreOut so = this.getOutById(id) ;
		if(so==null)
			return so ;
		return this.id2out.remove(id);//so.getName()) ;
	}
	
//	public JSONObject toListJO()
//	{
//		JSONObject jo = new JSONObject() ;
//		jo.put("id", this.id);
//		jo.put("n", this.name);
//		jo.putOpt("t", this.title);
//		jo.putOpt("d", this.desc);
//		jo.put("en", this.bEnable) ;
//		jo.put("tp", this.getTp()) ;
//		jo.put("tpt", this.getTpTitle()) ;
//		return jo ;
//	}
	
	public JSONObject toJO() //throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		jo.put("_tp", this.getTp());
		jo.put("tp", this.getTp()) ;
		jo.put("tpt", this.getTpTitle()) ;
		JSONArray jarr = new JSONArray() ;
		for(StoreOut ao:this.id2out.values())
		{
			jarr.put(ao.toJO()) ;
		}
		jo.put("outs", jarr) ;
		
		jarr = new JSONArray(this.selectedTags);
		jo.put("sel_tags", jarr) ;
		
		return jo ;
	}
	
	public void fromJO(JSONObject jo,boolean include_sel_tagid,boolean include_out) throws Exception
	{
		DataTranserJSON.injectJSONToObj(this, jo);
		if(include_out)
		{
			JSONArray jarr = jo.optJSONArray("outs") ;
			if(jarr!=null)
			{
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					String tp = tmpjo.getString("tp");
					StoreOut so = StoreOut.newInsByTp(tp) ;
					if(so==null)
						continue ;
					DataTranserJSON.injectJSONToObj(so, tmpjo) ;
					so.belongTo = this ;
					StringBuilder failedr = new StringBuilder() ;
					if(!so.checkValid(failedr))
						throw new Exception(failedr.toString()) ;
					this.id2out.put(so.getId(), so) ;
				}
			}
		}
		
		if(include_sel_tagid)
		{
			JSONArray jarr = jo.optJSONArray("sel_tags") ;
			HashSet<String> hs = new HashSet<>() ;
			if(jarr!=null)
			{
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					String tmps = jarr.getString(i) ;
					hs.add(tmps) ;
				}
			}
			this.selectedTags = hs ;
			
			
		}
		
		clearCache();
	}
	
	JSONObject RT_toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id);
		jo.putOpt("n", this.name) ;
		jo.putOpt("en", this.bEnable) ;
		jo.put("last_scan", rt_last_scan);
		jo.put("run", this.RT_isRunning()) ;
		
		JSONArray jarr = new JSONArray() ;
		jo.put("outs", jarr) ;
		for(StoreOut so:this.listOuts())
		{
			JSONObject tmpjo = so.RT_toJO() ;
			if(tmpjo==null)
				continue ;
			jarr.put(tmpjo) ;
		}
		return jo ;
	}
	
	@Override
	public Object JS_get(String  key)
	{
		Object ob = super.JS_get(key) ;
		if(ob!=null)
			return ob ;
		
		switch(key)
		{
		case "id":
			return this.id ;
		case "name":
			return this.name ;
		case "title":
			return this.title ;
		
		}
		return null;
	}
	
	@Override
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props() ;
		ss.add(new JsProp("id",null,String.class,false,"Handler ID","")) ;
		ss.add(new JsProp("name",null,String.class,false,"Handler Name","Handler unique name in project")) ;
		ss.add(new JsProp("title",null,String.class,false,"Handler Title","")) ;
		
		return ss ;
	}
	
	private transient List<UATag> fitTags = null ;
	private transient List<UATag> selTags = null ;
	
	public synchronized List<UATag> listFitTags()
	{
		if(this.fitTags!=null)
			return this.fitTags ;
		
		ArrayList<UATag> tags = new ArrayList<>() ;
		for(UATag tag:this.prj.listTagsAll())
		{
			if(this.checkFilterFit(tag))
				tags.add(tag) ;
		}
		this.fitTags = tags ;
		return this.fitTags ;
	}
	
	
	public synchronized List<UATag> listSelectedTags()
	{
		if(this.selectedAll)
			return listFitTags() ;
		
		if(this.selTags!=null)
			return this.selTags ;
		
		HashSet<String> tagids = this.getSelectTagIds() ;
		ArrayList<UATag> tags = new ArrayList<>() ;
		if(tagids==null)
		{
			this.selTags = tags ;
			return this.selTags ;
		}
		
		for(UATag tag:listFitTags())
		{
			String np = tag.getNodeCxtPathIn(this.prj) ;
			if(tagids.contains(np))
				tags.add(tag) ;
		}
		this.selTags = tags ;
		return this.selTags ;
	}
	
	private synchronized void clearCache()
	{
		fitTags = null ;
		selTags = null;
	}
	
	public synchronized void RT_start()
	{
		if(!this.bEnable)
			return ;
		
		if(th!=null)
			return;
		
		th = new Thread(runner);
		th.start();
	}
	
	public synchronized void RT_stop()
	{
		Thread t = th ;
		if(t==null)
			return;
		
		t.interrupt();
		th = null ;
	}
	
	public boolean RT_isRunning()
	{
		return th!=null ;
	}
	
	private transient long rt_last_scan = -1;
	
	protected void RT_init()
	{
		for(StoreOut so:this.listOuts())
		{
			if(!so.isEnable())
				continue ;
			
			StringBuilder failedr = new StringBuilder() ;
			try
			{
				so.rtInitOk = so.RT_init(failedr) ;
				if(!so.rtInitOk)
				{
					so.rtErrorInfo = failedr.toString() ;
					System.out.println("StoreHandler ["+this.getName()+"] out["+so.getName()+"] init failed:"+so.rtErrorInfo);
				}
				else
				{
					System.out.println("StoreHandler ["+this.getName()+"] out["+so.getName()+"] init ok");
				}
			}
			catch(Exception ee)
			{
				so.rtInitOk= false;
				so.rtErrorInfo = ee.getMessage();
				if(log.isErrorEnabled())
					log.error("StoreHandler ["+this.name+"] RT_init error", ee);
			}
		}
	}
	
	protected void RT_runInLoop()
	{
		for(StoreOut so:this.listOuts())
		{
			if(!so.bEnable)
				continue ;
			
			if(!so.rtInitOk)
				continue ;
			
			try
			{
				so.RT_runInLoop() ;
				so.rtRunOk = true ;
			}
			catch(Exception ee)
			{
				so.rtRunOk = false ;
				so.rtErrorInfo = ee.getMessage();
				if(log.isDebugEnabled())
					log.debug("StoreHandler ["+this.name+"] RT_runInLoop error", ee);
			}
		}
	}
	
	private Runnable runner = new Runnable() {

		@Override
		public void run()
		{
			try
			{
				RT_init();
				
				while(th!=null)
				{
					try
					{
						Thread.sleep(StoreHandler.this.scanIntV);
					}
					catch(Exception e) {}
					
					RT_runInLoop();
					
					rt_last_scan = System.currentTimeMillis() ;
				}
			}
			finally
			{
				th = null ;
			}
		}};
}