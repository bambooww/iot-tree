package org.iottree.core.msgnet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import org.iottree.core.UAServer;
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
public abstract class MNBase implements ILang
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
	
	
	/**
	 * 判断节点参数是否完备，只有完备之后的节点才可以运行
	 * @return
	 */
	public abstract boolean isParamReady(StringBuilder failedr);
	
	//to be override
	public abstract JSONObject getParamJO();
	
	//to be override
	final void setParamJO(JSONObject jo)
	{
		setParamJO(jo,System.currentTimeMillis()) ;
	}
	
	protected abstract void setParamJO(JSONObject jo,long up_dt);
	
	final void setDetailJO(JSONObject jo)
	{
		JSONObject pm_jo = jo.getJSONObject("pm_jo");
		setParamJO(pm_jo);
		this.title = jo.optString("title","") ;
		this.desc = jo.optString("desc","") ;
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
		String tt = this.title ;
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
		jo.put("icon", this.getIcon()) ;

		jo.put("runner", isRunner()) ;
		return jo ;
	}

	public JSONObject toJO()
	{
		JSONObject jo = this.toListJO() ;
		
		JSONObject pmjo = this.getParamJO() ;
		jo.putOpt("pm_jo", pmjo) ;
		//jo.put("pm_need", this.needParam()) ;
		StringBuilder sb = new StringBuilder() ;
		boolean br = this.isParamReady(sb);
		jo.put("pm_ready", br) ;
		if(!br)
			jo.put("pm_err", sb.toString()) ;
		else
			jo.put("pm_err", "") ;
		
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
		
		JSONObject pmjo = jo.optJSONObject("pm_jo") ;
		if(pmjo!=null)
		{
			long updt = this.belongTo.updateDT ;
			this.setParamJO(pmjo,updt);
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
	
	RTDebugPrompt RT_pptInf = null ;
	RTDebugPrompt RT_pptWarn = null ;
	RTDebugPrompt RT_pptErr = null ;
	
	public final boolean RT_hasPromptWarn()
	{
		return RT_pptWarn!=null ;
	}
	
	public final boolean RT_hasPromptErr()
	{
		return RT_pptErr!=null ;
	}
	
	protected void RT_renderDiv(StringBuilder divsb)
	{
		if(isRunner())
		{
			IMNRunner rnr = (IMNRunner)this ;
			if(rnr.RT_isRunning())
			{
				StringBuilder ssb = new StringBuilder() ;
				if(rnr.RT_isSuspendedInRun(ssb))
					divsb.append("<div class=\"rt_blk\"><span style=\"color:#dd7924\">Suspended:"+ssb.toString()+"</span><button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',false)\">stop</button></div>") ;
				else
					divsb.append("<div class=\"rt_blk\"><span style=\"color:green\">Running</span><button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',false)\">stop</button></div>") ;
			}
			else
				divsb.append("<div  class=\"rt_blk\"><span style=\"color:green\">Stopped</span><button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',true)\">start</button></div>") ;
		}
		
		if(RT_pptErr!=null)
		{
			divsb.append("<div  class=\"rt_blk\" style='background-color:rgba(255,0,0,0.3)'>[Error] "+RT_pptErr.getDTGapToNow()+" "+RT_pptErr.getMsg()) ;
			if(RT_pptErr.hasDetail())
				divsb.append("<button onclick=\"debug_prompt_detail(\'"+this.getId()+"\','err')\">Detail</button>") ;
			divsb.append("</div>") ;
		}
		
		if(RT_pptWarn!=null)
		{
			divsb.append("<div  class=\"rt_blk\" style='background-color:rgba(255,255,0,0.3)'>[Warn] "+RT_pptWarn.getDTGapToNow()+" "+RT_pptWarn.getMsg()) ;
			if(RT_pptWarn.hasDetail())
				divsb.append("<button onclick=\"debug_prompt_detail(\'"+this.getId()+"\','warn')\">Detail</button>") ;
			divsb.append("</div>") ;
		}
		
		if(RT_pptInf!=null)
		{
			divsb.append("<div  class=\"rt_blk\" style='background-color:rgba(0,0,255,0.3)'>[Info] "+RT_pptInf.getDTGapToNow()+" "+RT_pptInf.getMsg()) ;
			if(RT_pptInf.hasDetail())
				divsb.append("<button onclick=\"debug_prompt_detail(\'"+this.getId()+"\','info')\">Detail</button>") ;
			divsb.append("</div>") ;
		}

		divsb.append("<div class=\"rt_blk\">Vars</div>") ;
	}
	
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
			StringBuilder divsb = new StringBuilder() ;
			RT_renderDiv(divsb);
			jo.put("div", divsb.toString()) ;
		}
		
		jo.put("has_warn", this.RT_hasPromptWarn()) ;
		jo.put("has_err", this.RT_hasPromptErr()) ;
		return jo ;
	}

	protected final void RT_DEBUG_fireErr(String msg)
	{
		RT_DEBUG_fireErr(msg,null,null) ;
	}
	
	protected final void RT_DEBUG_fireErr(String msg,String detail)
	{
		RT_DEBUG_fireErr(msg,detail,null) ;
	}
	
	protected final void RT_DEBUG_fireErr(String msg,Throwable ee)
	{
		RT_DEBUG_fireErr(msg,null,ee) ;
	}
	
	/**
	 * called by overrider,to fire same err inf
	 * if msg and ee are null,it will clear inf
	 * @param msg
	 * @param ee
	 */
	protected final void RT_DEBUG_fireErr(String msg,String content,Throwable ee)
	{
		this.RT_pptErr = new RTDebugPrompt(msg, content, ee) ;
	}
	
	protected final void RT_DEBUG_fireWarn(String msg)
	{
		RT_DEBUG_fireWarn(msg,null,null) ;
	}
	
	protected final void RT_DEBUG_fireWarn(String msg,String detail)
	{
		RT_DEBUG_fireWarn(msg,detail,null) ;
	}
	
	protected final void RT_DEBUG_fireWarn(String msg,Throwable ee)
	{
		RT_DEBUG_fireWarn(msg,null,ee) ;
	}
	
	protected final void RT_DEBUG_fireWarn(String msg,String content,Throwable ee)
	{
		this.RT_pptWarn = new RTDebugPrompt(msg, content, ee) ;
	}
	
	protected final void RT_DEBUG_fireInfo(String msg)
	{
		RT_DEBUG_fireInfo(msg,null) ;
	}
	
	protected final void RT_DEBUG_fireInfo(String msg,String content)
	{
		this.RT_pptInf = new RTDebugPrompt(msg, content, null) ;
	}
	
	public final RTDebugPrompt RT_DEBUG_getPrompt(String lvl)
	{
		switch(lvl)
		{
		case "info":
		case "inf":
			return this.RT_pptInf;
		case "err":
		case "error":
			return this.RT_pptErr ;
		case "warn":
			return this.RT_pptWarn ;
		default:
			return null ;
		}
	}
}
