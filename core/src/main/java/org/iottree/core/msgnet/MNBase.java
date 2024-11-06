package org.iottree.core.msgnet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.UAServer;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsEnv;
import org.iottree.core.cxt.JsMethod;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.cxt.JsSub;
import org.iottree.core.cxt.JsSubOb;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.plugin.PlugJsApi;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * a base may be db ,mqtt client which has one or more node related
 * @author jason.zhu
 *
 */
public abstract class MNBase extends MNCxtPk implements ILang
{
	String id = IdCreator.newSeqId() ;
	
	String title = "" ;
	
	String desc = "";
	
	MNNet belongTo = null ;
	
	MNCat cat = null ;
	
	float x = 0 ;
	float y = 0 ;
	
	boolean bEnable = true ;
	
	boolean bShowRT = false;
	
	/**
	 * 如果节点实现接口IMNNodeRes,此变量才会起作用
	 */
	String resName = null ;
//	private String nodeTp = null ;
//	
//	private String nodeTpT = null ;


	public MNBase()
	{
	}
	
	void setCat(MNCat cat)
	{
		this.cat = cat ;
	}
	
	public MNCat getCat()
	{
		return this.cat ;
	}
	
	public String TP_getParamUrl()
	{
		return this.cat.getParamUrl(this) ;
	}
	
	public String TP_getDocUrl()
	{
		return this.cat.getDocUrl(this) ;
	}

	public String getId()
	{
		return this.id ;
	}
	
//	public String getUID()
//	{
//		return belongTo.getUid()+"."+this.id ;
//	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public void setTitle(String t)
	{
		this.title = t ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
	
	
	public MNNet getBelongTo()
	{
		return this.belongTo ;
	}
	
	
	public float getX()
	{
		return x ;
	}
	public float getY()
	{
		return  y;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public boolean isShowRT()
	{
		return this.bShowRT ;
	}
	
	public boolean isRunner()
	{
		return this instanceof IMNRunner;
	}
	
	public String getTPFull()
	{
		String ownn = this.getOwnerTP() ;
		if(Convert.isNullOrEmpty(ownn))
			return this.cat.getName()+"."+this.getTP() ;
		else
			return this.cat.getName()+"."+ownn+"."+this.getTP() ;
	}
	
	protected abstract String getOwnerTP() ;

	public abstract String getTP() ;
	
	
	
	public abstract String getTPTitle();
	
	public String getTPDesc()
	{
		return g(getTP(),"desc","") ;
	}
	
	abstract MNBase createNewIns(MNNet net) throws Exception ;
//	{
//		if(Convert.isNotNullEmpty(this.nodeTpT))
//			return this.nodeTpT ;
//		
//		return g_def(this.nodeTp,this.nodeTp) ;
//	}
	
//	void setNodeTP(String tp,String tpt)
//	{
//		this.nodeTp = tp ;
//		this.nodeTpT = tpt ;
//	}


	
	public abstract String getColor() ;
	
	public abstract String getIcon() ;
	
	public String getTitleColor()
	{
		return null ;
	}
	
	protected boolean supportCxtVars()
	{
		return false ;
	}
	
	public String getPmTitle()
	{
		return null ;
	}
	/**
	 * 判断节点参数是否完备，只有完备之后的节点才可以运行
	 * @return
	 */
	public abstract boolean isParamReady(StringBuilder failedr);
	
	//to be override
	public abstract JSONObject getParamJO();
	
//	//to be override
//	final void setParamJO(JSONObject jo)
//	{
//		setParamJO(jo,System.currentTimeMillis()) ;
//	}
	
	protected abstract void setParamJO(JSONObject jo);
	
	/**
	 * Node impl IMNNodeRes ,it will be used
	 * @return
	 */
	public String getMNResName()
	{
		return this.resName ;
	}
	
	final void setDetailJO(JSONObject jo)
	{
		JSONObject pm_jo = jo.getJSONObject("pm_jo");
		setParamJO(pm_jo);
		this.bEnable = jo.optBoolean("enable",true) ;
		this.title = jo.optString("title","") ;
		this.desc = jo.optString("desc","") ;
		this.resName = jo.optString("res_name") ;
		//other may be icon color etc
	}
	
	public void renderOut(Writer w)
	{
		JSONObject jo = toJO() ;
		jo.write(w) ; 
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id) ;
		String tt = this.getTitle() ;
		if(Convert.isNullOrEmpty(tt))
			tt = this.getTPTitle() ;
		jo.putOpt("title", tt) ;
		jo.putOpt("desc", desc);
		jo.put("_tp", getTPFull()) ;
		jo.put("tpt", getTPTitle()) ;
//		jo.put("uid", this.getUID());
		jo.put("x", this.x) ;
		jo.put("y", this.y) ;
		jo.put("enable", this.bEnable) ;
		jo.put("show_rt", this.bShowRT) ;
		jo.put("color", this.getColor()) ;
		jo.putOpt("tcolor", this.getTitleColor()) ;
		jo.put("icon", this.getIcon()) ;

		if(this instanceof IMNRunner)
		{
			IMNRunner rr = (IMNRunner)this;
			jo.put("runner", isRunner()) ;
			jo.put("runner_en", rr.RT_runnerEnabled()) ;
			jo.put("runner_in", rr.RT_runnerStartInner()) ;
			
		}
		return jo ;
	}

	public JSONObject toJO()
	{
		
		JSONObject jo = this.toListJO() ;
		
		JSONObject cxtdefjo = this.CXT_getDefJO();
		jo.putOpt("cxt_def", cxtdefjo) ;
		
		JSONObject pmjo = this.getParamJO() ;
		jo.putOpt("pm_jo", pmjo) ;
		//jo.put("pm_need", this.needParam()) ;
		StringBuilder sb = new StringBuilder() ;
		boolean br = this.isParamReady(sb);
		jo.put("pm_ready", br) ;
		jo.putOpt("pm_title", this.getPmTitle()) ;
		if(!br)
			jo.put("pm_err", sb.toString()) ;
		else
			jo.put("pm_err", "") ;
		
		jo.putOpt("res_name", resName) ;
		return jo;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.id = jo.getString("id") ;
		this.title = jo.optString("title") ;
		this.desc = jo.optString("desc") ;
		this.x = jo.optFloat("x",0) ;
		this.y = jo.optFloat("y",0) ;
		this.bEnable = jo.optBoolean("enable",true) ;
		this.bShowRT = jo.optBoolean("show_rt",false) ;
		
		this.resName = jo.optString("res_name") ;
		
		JSONObject pmjo = jo.optJSONObject("pm_jo") ;
		if(pmjo!=null)
		{
			//long updt = this.belongTo.updateDT ;
			this.setParamJO(pmjo);
		}
		
		JSONObject cxt_def = jo.optJSONObject("cxt_def") ;
		if(cxt_def!=null)
		{
			this.CXT_setDefJO(cxt_def) ;
		}
		
		return true;
	}
	
	protected boolean fromJOBasic(JSONObject jo,StringBuilder failedr)
	{
		this.x = jo.optFloat("x", this.x) ;
		this.y = jo.optFloat("y", this.y) ;
		this.bShowRT = jo.optBoolean("show_rt",false) ;
		//this.title = jo.optString("title") ;
		//this.desc = jo.optString("desc") ;
		//this.bStart = jo.optBoolean("b_start",false) ;
		return true ;
	}
	
	
	// -- RT
	
	public void RT_clean()
	{
		this.RT_CXT_clean();
	}
	
	protected RTDebug RT_DEBUG_INF = new RTDebug(this,"inf","rgba(0,0,255,0.3)") ;
	protected RTDebug RT_DEBUG_WARN = new RTDebug(this,"warn","rgba(255,255,0,0.3)") ;
	protected RTDebug RT_DEBUG_ERR = new RTDebug(this,"err","rgba(255,0,0,0.3)") ;
	
//	HashMap<String,RTDebugPrompt> RT_tp2pptInf = new HashMap<>() ;
//	HashMap<String,RTDebugPrompt> RT_tp2pptWarn = new HashMap<>() ;
//	HashMap<String,RTDebugPrompt> RT_tp2pptErr = new HashMap<>() ;
//	
//	public final boolean RT_hasPromptWarn()
//	{
//		return RT_tp2pptWarn.size()>0 ;
//	}
//	
//	public final boolean RT_hasPromptErr()
//	{
//		return RT_tp2pptErr.size()>0 ;
//	}
	
	public RTDebug RT_DEBUG_getByLvl(String lvl)
	{
		switch(lvl)
		{
		case "err":
			return RT_DEBUG_ERR;
		case "warn":
			return RT_DEBUG_WARN ;
		case "inf":
			return RT_DEBUG_INF ;
		default:
			return null ;
		}
	}
	
	public static class DivBlk
	{
		String blk ;
		
		String div ;
		
		public DivBlk(String blk,String div)
		{
			this.blk = blk ;
			this.div = div ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("blk",this.blk) ;
			jo.put("div", div) ;
			return jo ;
		}
	}
	
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		if(isRunner())
		{
			IMNRunner rnr = (IMNRunner)this ;
			if(rnr.RT_isRunning())
			{
				StringBuilder ssb = new StringBuilder() ;
				if(rnr.RT_isSuspendedInRun(ssb))
				{
					StringBuilder divsb = new StringBuilder() ;
					divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:#dd7924\">Suspended:"+ssb.toString()+"</span>");
					if(!rnr.RT_runnerStartInner())
						divsb.append("<button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',false)\">stop</button>");
					divsb.append("</div>") ;
					
					divblks.add(new DivBlk("rt_run",divsb.toString())) ;
				}
				else
				{
					StringBuilder divsb = new StringBuilder() ;
					divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:green\">Running</span>");
					if(!rnr.RT_runnerStartInner())
						divsb.append("<button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',false)\">stop</button>");
					divsb.append("</div>") ;
					
					divblks.add(new DivBlk("rt_run",divsb.toString())) ;
				}
			}
			else
			{
				StringBuilder divsb = new StringBuilder() ;
				divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:green\">Stopped</span>");
				if(!rnr.RT_runnerStartInner())
					divsb.append("<button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',true)\">start</button>");
				divsb.append("</div>") ;
				
				divblks.add(new DivBlk("rt_run",divsb.toString())) ;
			}
		}
		
		RT_DEBUG_ERR.renderDiv(divblks);
		
		RT_DEBUG_WARN.renderDiv(divblks);
		
		RT_DEBUG_INF.renderDiv(divblks);

		if(supportCxtVars())
			CXT_renderVarsDiv(divblks) ;
		
	}


	/**
	 * override to impl div fired event
	 * @param evtn
	 */
	public void RT_onRenderDivEvent(String evtn,StringBuilder retmsg)
	{}
	
	public JSONObject RT_toJO(boolean out_rt_div)
	{
		JSONObject jo = new JSONObject() ;
		if(isRunner())
		{
			IMNRunner rnr = (IMNRunner)this ;
			jo.put("runner", true) ;
			jo.put("b_running",rnr.RT_isRunning()) ;
			StringBuilder rsb = new StringBuilder() ;
			boolean bsusp = rnr.RT_isSuspendedInRun(rsb) ;
			jo.put("suspended", bsusp) ;
			if(bsusp)
				jo.put("suspend_reson", rsb.toString()) ;
		}
		if(out_rt_div)
		{
			ArrayList<DivBlk> divblks = new ArrayList<>() ;
			RT_renderDiv(divblks);
			JSONArray tmpjar = new JSONArray() ;
			for(DivBlk db : divblks)
				tmpjar.put(db.toJO()) ;
			jo.put("divs", tmpjar) ;
		}
		
		List<String> ss = this.RT_DEBUG_WARN.getPromptTitles() ;
		//jo.put("has_warn", warns.size()>0) ;
		jo.put("warns", new JSONArray(ss)) ;
		ss = this.RT_DEBUG_ERR.getPromptTitles() ;
		jo.put("errs",  new JSONArray(ss)) ;
		return jo ;
	}

	
	// JS
	
	public static List<JsProp> JS_getSysPropsCxt()
	{
		ArrayList<JsProp> jps = new ArrayList<>() ;
		jps.add(new JsProp("$sys",UAContext.sys,null,true,"system","System support func")) ;
		jps.add(new JsProp("$util",UAContext.util,null,true,"util","System util func")) ;
		jps.add(new JsProp("$debug",UAContext.debug,null,true,"system","System debug func")) ;
		HashMap<String,PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
		if(gvar2obj!=null)
		{
			for(Map.Entry<String, PlugJsApi> n2o:gvar2obj.entrySet())
			{
				String k = n2o.getKey();
				PlugJsApi jsapi= n2o.getValue() ;
				jps.add(new JsProp("$$"+k,jsapi,null,true,"plugin_"+k,jsapi.getDesc())) ;
			}
		}
		return jps;
	}
	
	private List<JsProp> globalPropsCxt = null ;
	
	protected List<JsProp> JS_getGlobalPropsCxt()
	{
		if(globalPropsCxt!=null)
			return globalPropsCxt ;
		
		ArrayList<JsProp> jps = new ArrayList<>() ;
		jps.addAll(JS_getSysPropsCxt()) ;
		
		MNNet net = this.getBelongTo() ;
		
		jps.add(new JsProp("flow",net,null,true,"Flow/Net","Flow obj in context").asTpTitle("Flow")) ;
		jps.add(new JsProp("node",this,null,true,this.getTitle(),this.getDesc()).asTpTitle("Node")) ;
		//jps.add(new JsProp("topic",null,String.class,false,"In Msg Topic","")) ;
		//jps.add(new JsProp("heads",null,Map.class,true,"In Msg Heads","")) ;
		//jps.add(new JsProp("payload",null,Object.class,true,"In Msg Payload","")) ;
		globalPropsCxt = jps;
		return jps ;
	}
	
	private transient List<JsSub> js_cxt_root_subs = null ;
	
	public final List<JsSub> JS_CXT_get_root_subs()
	{
		if(js_cxt_root_subs==null)
		{
			List<JsSub> rets = new ArrayList<>() ;
			List<JsProp> jps = JS_getGlobalPropsCxt();
			rets.addAll(jps) ;
			
			List<JsSub> subs = this.JS_get_subs() ;
			if(subs!=null)
			{
				for(JsSub sub:subs)
				{
					if(sub instanceof JsMethod || !sub.hasSub())
						continue ;// root has no method
					if(sub instanceof JsProp && ((JsProp)sub).isSysTag())
						continue ;
					rets.add(sub);
				}
			}
			js_cxt_root_subs = rets ;
		}
		
		JsEnv env = JsEnv.getInThLoc() ;
		if(env==null)
			return js_cxt_root_subs ;
		
		ArrayList<JsSub> rets = new ArrayList<>(js_cxt_root_subs);
		List<JsProp> jps = env.JS_get_props() ;
		if(jps!=null)
			rets.addAll(jps) ;
		return rets ;
	}
	
	public final JsSub JS_CXT_get_root_sub(String name)
	{
		for(JsSub jp: JS_CXT_get_root_subs())
		{
			if(jp.getName().equals(name))
				return jp ;
		}
		return null ;
	}
	
	public final JsSubOb JS_CXT_get_sub_by_id(String sub_id)
	{
		if(Convert.isNullOrEmpty(sub_id))
			return null ;
		
		List<String> ss = Convert.splitStrWith(sub_id, ".") ;
		String rootn = ss.get(0) ;
		int n = ss.size() ;
		JsSub jp1 = JS_CXT_get_root_sub(rootn) ;
		if(jp1==null)
			return null ;
		Object pv = this.JS_get(rootn) ;
		if(n==1)
			return new JsSubOb(jp1,pv) ;

		JsSub jss = null ;
		for(int i = 1 ; i < n ; i ++)
		{
			if(pv==null || !(pv instanceof JSObMap))
				return null ;
			
			JSObMap pv_ob = (JSObMap)pv ; 
			
			String name = ss.get(i) ;
			jss = pv_ob.JS_get_sub(name) ;
			if(jss==null)
				return null ;
			pv = pv_ob.JS_get(name) ;
		}
		return new JsSubOb(jss,pv) ;
	}
	
	public Object JS_get(String key)
	{
		Object r = super.JS_get(key);
		if (r != null)
			return r;
		
		if(key.startsWith("$$"))
		{//JsApi
			String plug_n = key.substring(2) ;
			HashMap<String,PlugJsApi> gvar2obj = PlugManager.getInstance().getJsApiAll();
			if(gvar2obj==null)
				return null ;
			return gvar2obj.get(plug_n) ;
		}
		
		if(key.startsWith("$"))
		{
			switch(key)
			{
//			case "flow":
//				return this.getBelongTo() ;
//			case "node":
//				return this ;
			case "$sys":
				return UAContext.sys;
			case "$debug":
				return UAContext.debug;
			case "$util":
				return UAContext.util;
			}
			
			//env
			JsEnv env = JsEnv.getInThLoc();
			if(env!=null)
			{
				return env.JS_get(key) ;
			}
		}
		
		switch(key)
		{
		case "flow":
			return this.getBelongTo() ;
		case "node":
			return this ;
		}
		
		return null ;
	}
	
	
	protected void UTIL_sleep(long t)
	{
		try
		{
			Thread.sleep(t);
		}catch(Exception e) {}
	}
	
}
