package org.iottree.core.msgnet.nodes;

import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.graalvm.polyglot.Value;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTDebugPrompt;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.cxt.MNContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_JsFunc extends MNNodeMid implements ILang
{
	int outNum = 1 ;
	
	String onInitJS = "" ;
	
	String onMsgJS = "" ;
	
	String onEndJS = "" ;
	
	@Override
	public String getColor()
	{
		return "#ffcea0";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf09a";
	}

	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return outNum;
	}

//	@Override
	public String getTP()
	{
		return "js_func";
	}

	@Override
	public String getTPTitle()
	{
		return g("js_func");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(!checkJsTxt("init_js",this.onInitJS,failedr)) 
//			return false;
//		if(!checkJsTxt("run_js",this.onMsgJS,failedr)) 
//			return false;
//		if(!checkJsTxt("end_js",this.onEndJS,failedr)) 
//			return false;
		
		String jstr = combineJsTxt().toString() ;
		if(!checkJsTxt("js",jstr,failedr))
			return false;
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.put("out_num", this.outNum) ;
		jo.putOpt("on_msg_js", this.onMsgJS) ;
		jo.putOpt("on_init_js", this.onInitJS) ;
		jo.putOpt("on_end_js", this.onEndJS) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.outNum = jo.optInt("out_num",1) ;
		if(this.outNum<=0)
			this.outNum = 1 ;
		this.onMsgJS = jo.optString("on_msg_js", "") ;
		this.onInitJS = jo.optString("on_init_js", "") ;
		this.onEndJS = jo.optString("on_end_js", "") ;
		
		RT_clearCxt();
	}
	
	//    js cxt
	

	private MNContext context = null ;
	
	
	private MNContext getCxt() throws ScriptException
	{
		return this.getBelongTo().RT_JS_getContext();
//		if(context!=null)
//			return context;
		
//		if(Convert.isNullOrTrimEmpty(this.onMsgJS))
//			return null ;
		
//		if(context==null)
//			return null ;
		
//		jsFN = "__JoinConn_"+IdCreator.newSeqId() ;
//		String tmps ="function "+jsFN + "($input){\r\n" ;
//		tmps += onMsgJS ;
//		tmps += "\r\n}\r\n";
//		tmps += "function "+jsFN + "_w($__input__,$__txt_only__){\r\n" +
//				"let $input = $__input__;if(!$__txt_only__)eval(\"$input=\"+$__input__);\r\n"+ 
//				"let ret = "+jsFN+"($input);"+
//				"if(typeof(ret)==\"string\") return ret;"+
//				"return JSON.stringify(ret);\r\n}" ;
//		context.scriptEval(tmps);				
//		return context ;
	}
	
	private String JS_getJsNameSp()
	{
		return "_n_"+this.getId() ;
	}
	
	/**
	 
	 var kk; (function (kk) {
    var k1 = 123;
   
    function k_f()
    {
    	console.log("k1="+k1);
    	console.log("oo="+oo())
    }
    
    kk.k_f = k_f ;
})(kk || (kk = {}));
	 
	 
	 * @param jstp
	 * @param chk_js
	 * @param failedr
	 * @return
	 */
	private boolean checkJsTxt(String jstp,String chk_js,StringBuilder failedr)// throws ScriptException
	{
		if(Convert.isNullOrTrimEmpty(chk_js))
			return true ;
		String name_sp = JS_getJsNameSp();
		StringBuilder sb = new StringBuilder() ;
		sb.append("var "+name_sp+"; (function ("+name_sp+") {\r\n") ;
			
		sb.append(chk_js) ;
		    
		 sb.append("\r\n})("+name_sp+" || ("+name_sp+" = {}));") ;
		 try
		 {
			 MNContext cxt = this.getBelongTo().RT_JS_getContext();
			 cxt.scriptEval(sb.toString());
			 return true ;
		 }
		 catch(Exception ee)
		 {
			 failedr.append("[").append(jstp).append("]").append(ee.getMessage()).append("\r\n").append(RTDebugPrompt.transToStr(ee)) ;
			 return false ;
		 }
	}
	
	private String getOnMsgInJsFunc()
	{
		String name_sp = JS_getJsNameSp();
		StringBuilder sb = new StringBuilder() ;
		sb.append("\r\nfunction ___on_msg_in(topic,heads,payload,node,flow) {\r\n") ;
		
		if(Convert.isNotNullEmpty(this.onMsgJS))
			sb.append(this.onMsgJS) ;
		
		sb.append("\r\n}\r\n") ;
		sb.append(name_sp).append(".___on_msg_in=___on_msg_in;\r\n") ;
		return sb.toString() ;
	}
	
	private String getOnEndJsFunc()
	{
		String name_sp = JS_getJsNameSp();
		StringBuilder sb = new StringBuilder() ;
		sb.append("\r\nfunction ___on_end(node,flow) {\r\n") ;
		
		if(Convert.isNotNullEmpty(this.onEndJS))
			sb.append(this.onEndJS) ;
		
		sb.append("\r\n}\r\n") ;
		sb.append(name_sp).append(".___on_end=___on_end;\r\n") ;
		return sb.toString() ;
	}
	
	private StringBuilder combineJsTxt()
	{
		String name_sp = JS_getJsNameSp();
		StringBuilder sb = new StringBuilder() ;
		sb.append("var "+name_sp+"; (function ("+name_sp+") {\r\n") ;
		
		
		
		if(Convert.isNotNullEmpty(this.onInitJS))
			sb.append(onInitJS) ;
		
		sb.append(getOnMsgInJsFunc()) ;
		
		sb.append(getOnEndJsFunc()) ;
		
		sb.append("\r\nfunction Msg(topic,heads,payload)\r\n{	this.___m_s_g__=true;this.topic = topic;	this.payload = payload;	this.heads = heads ;}");
		
		sb.append("\r\n})("+name_sp+" || ("+name_sp+" = {}));\r\n") ;
		
		sb.append("function fn_msgin_"+name_sp+"(topic,heads,payload,node,flow){\r\n") ;
		sb.append("\r\n return "+name_sp+".___on_msg_in(topic,heads,payload,node,flow);\r\n");
		sb.append("\r\n}\r\n");
		
		sb.append("function fn_end_"+name_sp+"(node,flow){\r\n") ;
		sb.append("\r\n return "+name_sp+".___on_end(node,flow);\r\n");
		sb.append("\r\n}\r\n");
		
		return sb ;
	}
	
	// --------------
	
	MNContext rtCxt = null ;
	
	private MNContext RT_getCxt() throws ScriptException
	{
		if(rtCxt!=null)
			return rtCxt ;
		
		synchronized(this)
		{
			if(rtCxt!=null)
				return rtCxt ;
			
			MNContext cxt = this.getCxt() ;
			String jstr = combineJsTxt().toString() ;
			cxt.scriptEval(jstr);
			rtCxt = cxt ;
			return cxt ;
		}
	}
	
	private void RT_clearCxt()
	{
		rtCxt = null;
	}
	
	private MNMsg createMsgFromRetJO(JSONObject jo)
	{
		boolean b = jo.optBoolean("___m_s_g__",false) ;
		if(b)
		{// new Msg(topic,heads.payload)
			String topic = jo.optString("topic") ;
			JSONObject heads = jo.optJSONObject("heads") ;
			Map<?,?> hmap = null ;
			if(heads!=null)
				hmap = heads.toMap() ;
			Object pld = jo.opt("payload") ;
			return new MNMsg().asTopic(topic).asHeads(hmap).asPayload(pld) ;
		}
		
		return  new MNMsg().asPayload(jo) ;
	}
	
	private MNMsg createMsgFromRetObj(Object obj)
	{
		if(obj instanceof Map)
		{
			JSONObject tmpjo = new JSONObject((Map<?,?>)obj) ;
			return createMsgFromRetJO(tmpjo) ;
		}
		
		return  new MNMsg().asPayload(obj) ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		MNContext cxt = RT_getCxt() ;
		String name_sp = JS_getJsNameSp();
		String fn = "fn_msgin_"+name_sp ;
		Object pld = msg.getPayload() ;
		Object obj = cxt.scriptInvoke(fn,msg.getTopic(), msg.getHeadsMap(),pld,this,this.getBelongTo()) ;
		if(obj==null)
			return null ;

		if(obj instanceof Map)
		{
			JSONObject tmpjo = new JSONObject((Map<?,?>)obj) ;
			MNMsg outmsg = createMsgFromRetJO(tmpjo) ;
			return RTOut.createOutAll(outmsg) ;
		}
		
		if(obj instanceof List)
		{
			//JSONArray jarr = new JSONArray((List<?>)obj) ;
			List<?> vs = (List<?>)obj ;
			int n = vs.size();
			int outn = this.getOutNum() ;
			RTOut rto = RTOut.createOutIdx() ;
			for(int i = 0 ; i < n && i < outn ; i ++)
			{
				Object tmpo =vs.get(i) ;
				if(tmpo==null)
					continue ;
				MNMsg tmpm = createMsgFromRetObj(tmpo) ;
				rto.asIdxMsg(i, tmpm) ;//new MNMsg().asPayload(tmpo)) ;
			}
			return rto ;
		}
		
		MNMsg outmsg = new MNMsg().asPayload(obj) ;
		return RTOut.createOutAll(outmsg) ;
	}
	
	@JsDef(method_params_title = "(Msg)")
	private boolean sendMsgToAll(Value msg_obj) //Map<String,?>
	{
		if(msg_obj==null)
		{
			this.RT_DEBUG_ERR.fire("send_err", "input Msg is null");
			return  false;
		}
		//String topic,Map<String,Object> heads,Object payload
		//System.out.println("msg_obj="+msg_obj) ;
		Object obj = MNContext.transGraalValueToMapListObj(msg_obj) ;
		if(obj==null  || !(obj instanceof Map))
		{
			this.RT_DEBUG_ERR.fire("send_err", "input is not Msg object");
			return false;
		}
		Map<String,?> mob = (Map<String,?>)obj;
		JSONObject tmpjo = new JSONObject(mob) ;
		MNMsg outmsg = createMsgFromRetJO(tmpjo) ;
		this.RT_sendMsgOut(RTOut.createOutAll(outmsg));
		this.RT_DEBUG_ERR.clear("send_err");
		return true ;
	}
	
	@JsDef(method_params_title = "(int,Msg)")
	private boolean sendMsgToIdx(int out_idx,Value msg_obj) //Map<String,?>
	{
		if(out_idx<0 || out_idx>=this.getOutNum())
		{
			this.RT_DEBUG_ERR.fire("send_err", "invalid out_idx="+out_idx);
			return false;
		}
		if(msg_obj==null)
		{
			this.RT_DEBUG_ERR.fire("send_err", "input Msg is null");
			return  false;
		}
		//String topic,Map<String,Object> heads,Object payload
		//System.out.println("msg_obj="+msg_obj) ;
		Object obj = MNContext.transGraalValueToMapListObj(msg_obj) ;
		if(obj==null  || !(obj instanceof Map))
		{
			this.RT_DEBUG_ERR.fire("send_err", "input is not Msg object");
			return false;
		}
		
		Map<String,?> mob = (Map<String,?>)obj;
		JSONObject tmpjo = new JSONObject(mob) ;
		MNMsg outmsg = createMsgFromRetJO(tmpjo) ;
		RTOut rto = RTOut.createOutIdx().asIdxMsg(out_idx, outmsg) ;
		this.RT_sendMsgOut(rto);
		return true ;
	}
}
