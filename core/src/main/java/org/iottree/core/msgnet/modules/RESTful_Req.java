package org.iottree.core.msgnet.modules;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNet;

public class RESTful_Req extends MNNodeStart
{
	public static enum InTP
	{
		json_object,
		json_array,
		str,
		bytes
	}
	
	String apiName = null ;
	
	InTP inTP = InTP.json_object;
	
	/**
	 * 
	 */
	String reqSample = null ;
	
	@Override
	public String getTP()
	{
		return "restful_req";
	}

	@Override
	public String getTPTitle()
	{
		return g("restful_req");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "\\uf35a";
	}
	
	@Override
	public String getPmTitle()
	{
		if(Convert.isNullOrEmpty(this.apiName))
			return "[]";
		return "["+this.apiName +"]";
	}
	
	public String getApiName()
	{
		if(this.apiName==null)
			return "" ;
		return this.apiName ;
	}
	
	public String getAccessPath()
	{
		RESTful_M m = (RESTful_M)this.getOwnRelatedModule() ;
		return m.getAccessPath()+this.apiName ;
	}

	public InTP getInTP()
	{
		return this.inTP ;
	}
	
	public String getReqSample()
	{
		if(this.reqSample==null)
			return "" ;
		return this.reqSample ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.apiName))
		{
			failedr.append("no api name set") ;
			return false;
		}
		return true ;
	}

	private void checkApiName(String api_n)
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(api_n, true, failedr))
			throw new RuntimeException("api name err:"+failedr) ;
		RESTful_M m = (RESTful_M)this.getOwnRelatedModule() ;
		if(m==null)
			return ;//first added
		
		RESTful_Req oldreq = m.getReqNode(api_n) ;
		if(oldreq!=null && oldreq!=this)
			throw new RuntimeException("api name="+api_n+" is already existed in module") ;
	}
	

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("api_n",this.apiName) ;
		if(this.inTP!=null)
			jo.put("in_tp", this.inTP.name()) ;
		jo.put("sample", this.reqSample) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		String api_n = jo.optString("api_n") ;
		if(Convert.isNotNullEmpty(api_n))
		{
			checkApiName(api_n) ;
		}
		
		this.apiName = api_n;
		
		String in_tp = jo.optString("in_tp") ;
		if(Convert.isNullOrEmpty(in_tp))
			this.inTP = InTP.json_object ;
		else
			this.inTP = InTP.valueOf(in_tp) ;
		
		this.reqSample = jo.optString("sample") ;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}
	
	void RT_onReq(HttpServletRequest request,byte[] req_bs) throws UnsupportedEncodingException
	{
		
		String method = request.getMethod() ;
		MNMsg msg = null;
		if("GET".equals(method)) // || Convert.isNullOrEmpty(req_txt))
		{
			Map<String,String> pms = Convert.parseFromRequest(request, null) ;
			JSONObject tmpjo = new JSONObject(pms) ;
			msg = new MNMsg().asPayload(tmpjo) ;
		}
		else if("POST".equals(method))
		{
			String req_txt = new String(req_bs,"UTF-8") ;
			switch(this.inTP)
			{
			case json_object:
				JSONObject tmpjo = new JSONObject(req_txt) ;
				msg = new MNMsg().asPayload(tmpjo) ;
				break ;
			case json_array:
				JSONArray tmpjarr = new JSONArray(req_txt) ;
				msg = new MNMsg().asPayload(tmpjarr) ;
				break ;
			case str:
				msg = new MNMsg().asPayload(req_txt) ;
				break ;
			case bytes:
				msg = new MNMsg().asBytesArray(req_bs);//.asPayload(req_txt) ;
				break ;
			default:
				return ;
			}
		}
		
		if(msg==null)
			return ;
		
		RTOut rto = RTOut.createOutIdx().asIdxMsg(0, msg) ;
		this.RT_sendMsgOut(rto);
	}
}
