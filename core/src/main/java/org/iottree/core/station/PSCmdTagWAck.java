package org.iottree.core.station;

import org.iottree.core.station.PlatInsWSServer.SessionItem;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class PSCmdTagWAck extends PSCmd
{
	public final static String CMD = "tag_w_ack" ;

	@Override
	public String getCmd()
	{
		return CMD;
	}
	
	public PSCmdTagWAck asAckWriteTag(String prjname,String tagpath,String strv,boolean bwok,String err)
	{
		JSONObject jo = new JSONObject() ;
		jo.put("prjn", prjname) ;
		jo.put("tagp", tagpath) ;
		jo.put("strv", strv) ;
		jo.put("wok", bwok) ;
		jo.putOpt("err", err) ;
		this.asCmdDataJO(jo) ;
		return this ;
	}
	
	
	@Override
	public void RT_onRecvedInPlatform(SessionItem si, PStation ps) throws Exception
	{
		JSONObject jo = this.getCmdDataJO() ;
		if(jo==null)
			return ;
		String prjn = jo.optString("prjn") ;
		String tagp = jo.optString("tagp") ;
		String strv = jo.optString("strv") ;
		if(Convert.isNullOrEmpty(prjn) || Convert.isNullOrEmpty(tagp) || Convert.isNullOrEmpty(strv))
			return ;
	}
	
}
