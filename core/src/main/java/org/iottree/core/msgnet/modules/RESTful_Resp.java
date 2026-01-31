package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;

public class RESTful_Resp  extends MNNodeEnd
{
	public static enum OutTP
	{
		json,
		str,
		bytes;
		
		public String getContentType()
		{
			if(this==json)
				return "application/json; charset=utf-8";
			else if(this==str)
				return "text/plain; charset=utf-8";
			else if(this==bytes)
				return "application/octet-stream";
			return null ;
		}
	}
	
	String apiName = null ;
	
	String respSample = null ;
	
	OutTP outTp = OutTP.json ;
	
	@Override
	public String getTP()
	{
		return "restful_resp";
	}

	@Override
	public String getTPTitle()
	{
		return g("restful_resp");
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
	
	public String getRespSample()
	{
		if(this.respSample==null)
			return "" ;
		return this.respSample ;
	}
	
	public OutTP getOutTP()
	{
		return this.outTp ;
	}
	
	String getContentType()
	{
		if(this.outTp==null)
			return null;
		
		return this.outTp.getContentType() ;
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

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("api_n",this.apiName) ;
		jo.put("sample", this.respSample) ;
		if(this.outTp!=null)
			jo.put("out_tp", this.outTp.name());
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.apiName = jo.optString("api_n") ;
		this.respSample = jo.optString("sample") ;
		String out_tp = jo.optString("out_tp","json") ;
		this.outTp = OutTP.valueOf(out_tp) ;
	}
	
	private transient MNMsg _curMsg = null ; 

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		this._curMsg = msg ;
		return null;
	}

	MNMsg RT_getCurInMsg()
	{
		return this._curMsg ;
	}
	
	void RT_clear()
	{
		this._curMsg = null ;
	}
}
