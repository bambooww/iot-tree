package org.iottree.ext.ai.dev;

import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.ext.ai.LLMMsg;
import org.iottree.ext.ai.LLMReq;
import org.iottree.ext.ai.LLMMsg.Role;
import org.iottree.ext.ai.mn.VLLM_M;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * support DevCtrl_M to create chat request .
 * 1,system msg
 * 2,user msg (input by in)
 * 
 * and it will create LLMReq obj out, which can be accept by nodes like:
 * 	  LLMOllamaChat_NM ,VLLMChat_NM etc
 * 
 * @author jason.zhu
 *
 */
public class DevCtrlRequest_NM extends MNNodeMid
{
	
	@Override
	public String getTP()
	{
		return "devctrl_req";
	}

	@Override
	public String getTPTitle()
	{
		return "Request";
	}

	@Override
	public String getColor()
	{
		return "#af40a0";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0a0";
	}
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{

		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		
		
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}
	
	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		return "LLM Req" ;
	}
	
	@Override
	public String getOutColor(int idx)
	{
		return "#4185d2";
	}
	
	

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		
		String pldstr = msg.getPayloadStr() ;
		if(Convert.isNullOrEmpty(pldstr))
			return null;
		
		DevCtrl_M owner = (DevCtrl_M)this.getOwnRelatedModule() ;
		LLMReq req = new LLMReq() ;
		req.addMessage(Role.system, owner.getSystemMsg()) ;
		req.addMessageUser(pldstr) ; 
		
		req.asStructuredOutputJsonSchema(DevCtrl_M.getOrLoadSchemaCmd()) ;
		
		return RTOut.createOutIdx().asIdxMsg(0, new MNMsg().asExtObj(req)) ;
	}

}
