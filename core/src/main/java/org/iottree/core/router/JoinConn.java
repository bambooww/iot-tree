package org.iottree.core.router;

import javax.script.ScriptException;

import org.iottree.core.ConnPt.DataTp;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public class JoinConn
{
	RouterManager rmgr = null ;
	
	JoinOut fromJO = null ;
	
	JoinIn toJI = null ;
	
	String fromId = null; 
	
	
	String toId = null ;
	
	boolean bEnJS = false;
	
	String transJS = null ;
	
//	private UACodeItem codeItem = null ;
	
	private UAContext context = null ;
	private String jsFN = null ;
	//private boolean hasJS = false;
	
	JoinConn(RouterManager rm,JoinOut fromjo,JoinIn toji)
	{
		this.rmgr = rm ;
		this.fromJO = fromjo ;
		this.toJI = toji ;
	}
	
	public JoinConn(RouterManager rm,JoinOut fromjo,JoinIn toji,String fromid,String toid)
	{
		this.rmgr = rm ;
		this.fromJO = fromjo ;
		this.toJI = toji ;
		this.fromId = fromid ;
		this.toId = toid ;
	}
	
	public boolean isTransJSEnable()
	{
		return this.bEnJS ;
	}
	
	public synchronized JoinConn asTransJS(String js)
	{
		if(js!=null)
			this.transJS = js.trim() ;
		else
			this.transJS = null ;
		//this.codeItem = null ;
		context = null ;
		jsFN = null ;
		//this.hasJS = !Convert.isNullOrTrimEmpty(js) ;
		return this ;
	}
	
	public JoinOut getFromJO()
	{
		return this.fromJO ;
	}
	
	public String getFromId()
	{
		return fromId ;
	}
	
	public JoinIn getToJI()
	{
		return this.toJI ;
	}
	
	public String getToId()
	{
		return toId ;
	}
	
	public String getKey()
	{
		return fromId+","+toId ;
	}
	
	public static String calKey(String fromid,String toid)
	{
		return fromid+","+toid;
	}
	
	public String getTransJS()
	{
		return transJS ;
	}
	
	private UAContext getCxt() throws ScriptException
	{
		if(context!=null)
			return context;
		
		if(Convert.isNullOrTrimEmpty(this.transJS))
			return null ;
		
		RouterNode rn = this.fromJO.getBelongNode() ;
		if(rn instanceof RouterInnCollator)
		{
			context = ((RouterInnCollator)rn).RT_getContext() ;
		}
		rn = this.toJI.getBelongNode() ;
		if(rn instanceof RouterInnCollator)
		{
			context = ((RouterInnCollator)rn).RT_getContext() ;
		}
		
		if(context==null)
			return null ;
		
		jsFN = "__JoinConn_"+IdCreator.newSeqId() ;
		String tmps ="function "+jsFN + "($input){\r\n" ;
		tmps += transJS ;
		tmps += "\r\n}\r\n";
		tmps += "function "+jsFN + "_w($__input__,$__txt_only__){\r\n" +
				"let $input = $__input__;if(!$__txt_only__)eval(\"$input=\"+$__input__);\r\n"+ 
				"let ret = "+jsFN+"($input);"+
				"if(typeof(ret)==\"string\") return ret;"+
				"return JSON.stringify(ret);\r\n}" ;
		context.scriptEval(tmps);				
		return context ;
	}
	
//	private synchronized UACodeItem getCodeItem() throws ScriptException
//	{
//		if(Convert.isNullOrTrimEmpty(transJS))
//			return null ;
//		
//		this.codeItem = new UACodeItem("","{\r\n"+transJS+"\r\n}") ;
//		this.codeItem.initItem(getCxt(), "$__input__","$__txt_only__") ;
//		return this.codeItem ;
//	}
	
	private long rt_transErrDT = -1;//System.currentTimeMillis() ;
	
	private String rt_transErrInf = null;//"test err" ;
	
	private Exception rt_transErr = null ;
	
	public RouterObj RT_doTrans(RouterObj input)// throws Exception 
	{
		if(!this.bEnJS)
			return input ;
		
//		if(!this.hasJS)
//		{
//			rt_transErrDT = System.currentTimeMillis() ;
//			rt_transErrInf = "no js text";
//			rt_transErr = null ;
//			return null ;
//		}
		try
		{
//			UACodeItem ci = getCodeItem() ;
//			if(ci==null)
//				return input ;
			UAContext cxt = this.getCxt() ;
			if(cxt==null)
				return null ;
			String res = (String)cxt.scriptInvoke(jsFN + "_w",input.getTxt(),input.isTxtOnly());
			if(Convert.isNullOrEmpty(res))
			{
				rt_transErrDT = System.currentTimeMillis() ;
				rt_transErrInf = "js code return null or empty" ;
				return null ;
			}
			
			rt_transErrDT = -1 ;
			rt_transErr = null ;
			return new RouterObj(res) ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			rt_transErrDT = System.currentTimeMillis() ;
			rt_transErrInf = ee.getMessage() ;
			rt_transErr = ee ;
			return null ;
		}
	}
	
	public String RT_getTransInf()
	{
		return this.rt_transErrInf ;
	}
	
	public Exception RT_getTransErr()
	{
		return this.rt_transErr ;
	}
	
	public JSONObject RT_getRunInf()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("key", this.getKey()) ;
		jo.put("rt_err_dt", this.rt_transErrDT) ;
		jo.putOpt("rt_last_err", this.rt_transErrInf) ;
		
		//jo.putOpt(, value)
		return jo ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("fid",this.fromId);
		jo.put("tid",this.toId);
		jo.put("key", this.getKey()) ;
		jo.putOpt("js", transJS) ;
		jo.put("en_js", this.bEnJS) ;
		return jo ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = toJO();
		jo.put("has_js",Convert.isNotNullTrimEmpty(transJS)) ;
		return jo ;
	}
	
	public static JoinConn RIC_fromJO(RouterManager rm,JSONObject jo)
	{
		String fid = jo.getString("fid") ;
		String tid = jo.getString("tid") ;
		
		if(Convert.isNullOrEmpty(fid))
			return null;
		if(Convert.isNullOrEmpty(tid))
			return null;
		
		JoinOut jjo = rm.CONN_RIC_getJoinOutByFromId(fid) ;
		if(jjo==null)
			return null;
		
		JoinIn ji = rm.CONN_ROA_getJoinInByToId(tid) ;
		if(ji==null)
			return null;
		
		JoinConn ret = new JoinConn(rm,jjo,ji,fid,tid) ;
		ret.transJS = jo.optString("js") ;
		ret.bEnJS = jo.optBoolean("en_js",false) ;
		//ret.hasJS = !Convert.isNullOrTrimEmpty(ret.transJS) ;
		return ret ;
	}
	
	public static JoinConn ROA_fromJO(RouterManager rm,JSONObject jo)
	{
		String fid = jo.getString("fid") ;
		String tid = jo.getString("tid") ;
		
		if(Convert.isNullOrEmpty(fid))
			return null;
		if(Convert.isNullOrEmpty(tid))
			return null;
		
		JoinOut jjo = rm.CONN_ROA_getJoinOutByFromId(fid) ;
		if(jjo==null)
			return null;
		
		JoinIn ji = rm.CONN_RIC_getJoinInByToId(tid) ;
		if(ji==null)
			return null;
		
		JoinConn ret = new JoinConn(rm,jjo,ji,fid,tid) ;
		ret.transJS = jo.optString("js") ;
		ret.bEnJS = jo.optBoolean("en_js",false) ;
		//ret.hasJS = !Convert.isNullOrTrimEmpty(ret.transJS) ;
		return ret ;
	}
}
