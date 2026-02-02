package org.iottree.core.msgnet.modules;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.*;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class RESTful_M extends MNModule
{
	public static final String TP = "restful_m";
	
	String module_name = null ;
	
	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return g("restful_m");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "PK_rest_api";
	}
	
	public String getModuleName()
	{
		if(this.module_name==null)
			return "" ;
		return this.module_name ;
	}
	
	public String getAccessPath()
	{
		MNNet net = this.getBelongTo() ;
		UAPrj prj = net.getBelongTo().getBelongToPrj() ;
		return "/"+prj.getName()+"/_mn_"+TP+"/"+net.getName()+"/"+this.module_name+"/" ;
	}
	
	@Override
	public String getPmTitle()
	{
		if(Convert.isNullOrEmpty(this.module_name))
			return "";
		return this.module_name ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.module_name))
		{
			failedr.append( "no module name set");
			return false;
		}
		if(!Convert.checkVarName(module_name, true, failedr))
			return false;
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("module_name", this.module_name) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		String m_name = jo.optString("module_name") ;
		//check module in net
		MNNet net = this.getBelongTo() ;
		List<RESTful_M> mms = net.findItemByTpMark(RESTful_M.class, null) ;
		for(RESTful_M m:mms)
		{
			String nnn = m.getModuleName() ;
			if(Convert.isNullOrEmpty(nnn) || !nnn.equals(m_name) || m==this)
				continue ;
			throw new RuntimeException("module name="+m_name+" is already existed") ;
		}
		this.module_name = m_name;
	}
	
	public RESTful_Req getReqNode(String api_name)
	{
		List<RESTful_Req> reqs = this.listRelatedNodes(RESTful_Req.class) ;
		if(reqs==null)
			return null ;
		
		for(RESTful_Req req:reqs)
		{
			if(api_name.equals(req.getApiName()))
				return req;
		}
		return null ;
	}
	
	public RESTful_Resp getRespNode(String api_name)
	{
		List<RESTful_Resp> reqs = this.listRelatedNodes(RESTful_Resp.class) ;
		if(reqs==null)
			return null ;
		
		for(RESTful_Resp req:reqs)
		{
			if(api_name.equals(req.getApiName()))
				return req;
		}
		return null ;
	}
	

	public List<String> getNoRespApiNames()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		List<RESTful_Req> reqs = this.listRelatedNodes(RESTful_Req.class) ;
		if(reqs==null)
			return rets ;
		
		for(RESTful_Req req:reqs)
		{
			String apin = req.getApiName() ;
			if(Convert.isNullOrEmpty(apin))
				continue ;
			RESTful_Resp resp = getRespNode(apin) ;
			if(resp==null)
				rets.add(apin) ;
		}
		return rets;
	}
	
	
//	public byte[] RT_onReqInput(String api_name,HttpServletRequest request,byte[] req_bs) throws UnsupportedEncodingException
//	{
//		RESTful_Req req = getReqNode(api_name) ;
//		if(req==null)
//			return null ;
//		RESTful_Resp resp = this.getRespNode(api_name) ;
//		if(resp!=null)
//			resp.RT_clear() ;
//		
//		req.RT_onReq(request,req_bs) ;
//		//
//		
//		if(resp==null)
//			return null ;
//		MNMsg cur_msg = resp.RT_getCurInMsg() ;
//		if(cur_msg==null)
//			return null ;
//		Object pld = cur_msg.getPayload() ;
//		if(pld==null)
//			return null ;
//		return pld.toString().getBytes("UTF-8");
//	}
	
	private boolean RT_checkRight(HttpServletRequest request)
	{
		
		return true ;
	}
	
	public boolean RT_onReqResp(String api_name,
			HttpServletRequest request,HttpServletResponse response,byte[] req_bs) throws IOException
	{
		RESTful_Req req = getReqNode(api_name) ;
		if(req==null)
			return false ;
		
		if(!this.RT_checkRight(request))
			return false;
		
		RESTful_Resp resp = this.getRespNode(api_name) ;
		if(resp!=null)
			resp.RT_clear() ;
		
		req.RT_onReq(request,req_bs) ;
		if(resp==null)
			return false ;
		MNMsg cur_msg = resp.RT_getCurInMsg() ;
		if(cur_msg==null)
			return false ;
		Object pld = cur_msg.getPayload() ;
		if(pld==null)
			return false ;
		byte[] respbs = pld.toString().getBytes("UTF-8");
		if(respbs==null)
			return false;
		
		String conttp = resp.getContentType() ;
		if(Convert.isNotNullEmpty(conttp))
			response.setContentType(conttp);
		response.getOutputStream().write(respbs);
		return true;
	}
}
