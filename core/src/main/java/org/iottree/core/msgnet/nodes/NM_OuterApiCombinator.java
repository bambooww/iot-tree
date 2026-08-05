package org.iottree.core.msgnet.nodes;

import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_OuterApiCombinator extends MNNodeMid // implements ILang
{
	String uri_path = null ; //prj_n.net_n.node_n or prj_n.net_n or null is whole server
	
	List<String> sub_api_uids = null ;
	
	String req_txt = "" ;
	
	@Override
	public String getColor()
	{
		return "#e6d970";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf079";
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
		return 1;
	}
	
//	@Override
	public String getTP()
	{
		return "outerapi_comb";
	}

	@Override
	public String getTPTitle()
	{
		return g("outerapi_comb");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(sub_api_uids==null || sub_api_uids.size()<=0)
		{
			failedr.append("no outer api set") ;
			return false;
		}
		String s = this.req_txt.trim() ;
		if(Convert.isNotNullTrimEmpty(s))
		{
			try
			{
				if(s.startsWith("["))
				{
					new JSONArray(s) ;
				}
				else
				{
					failedr.append("request JSONArray format") ;
					return false;
				}
			}
			catch(Exception ee)
			{
				failedr.append("request JSONArray format: "+ee.getMessage()) ;
				return false;
			}
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("uri_path", this.uri_path) ;
		jo.putOpt("sub_api_uids", Convert.combineStrWith(this.sub_api_uids, ",")) ;
		jo.putOpt("req_txt", req_txt) ;
		return jo;
	}
	
	private transient JSONArray reqJArrDef = null ;

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.uri_path = jo.optString("uri_path");
		this.sub_api_uids = Convert.splitStrWith(jo.optString("sub_api_uids"), ",");
		this.req_txt = jo.optString("req_txt","") ;
		if(Convert.isNotNullEmpty(this.req_txt))
		{
			try
			{
				reqJArrDef = new JSONArray(this.req_txt) ;
			}
			catch(Exception ee)
			{
				
			}
		}
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		JSONArray req_jarr = msg.getPayloadJArr(reqJArrDef);
		if(req_jarr==null)
			return null ;

		StringBuilder failedr = new StringBuilder() ;
		JSONArray ret_jarr = OuterApi.RT_callInPath(uri_path, req_jarr, failedr);
		if(ret_jarr==null)
			return null ;
		return RTOut.createOutIdx().asIdxMsg(0, new MNMsg().asPayload(ret_jarr)) ;
	}
	
}
