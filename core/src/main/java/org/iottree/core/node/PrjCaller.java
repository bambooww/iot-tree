package org.iottree.core.node;

import java.util.HashMap;

import org.iottree.core.UAPrj;
import org.iottree.core.node.NodeMsg.MsgTp;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public abstract class PrjCaller extends PrjNode
{
	private String sharePrjId = null ;
	
	public void withSharePrjId(String share_prjid)
	{
		this.sharePrjId = share_prjid ;
	}
	
	public String getSharePrjId()
	{
		if(this.sharePrjId==null)
			return "" ;
		return sharePrjId ;
	}
	
	public abstract boolean isConnReady();
	
	public abstract void disconnect() ;
	
	public abstract void checkConn() ;
	
	public void callShareTree() throws Exception
	{
		if(Convert.isNullOrEmpty(sharePrjId))
			throw new Exception("no share project") ;
		
		this.sendMsg(sharePrjId, MsgTp.req, null);
	}
	
//	protected void SW_callerOnResp(String shareprjid,byte[] cont) throws Exception
//	{
//		super.SW_callerOnResp(shareprjid,cont);
//		
//		
//	}
	
	protected void SW_callerOnPush(byte[] cont) throws Exception
	{
		super.SW_callerOnPush(cont);
		//System.out.println("SW_callerOnPush "+cont.length) ;
		//String tmps = new String(cont,"UTF-8") ;
		//System.out.println(tmps) ;
	}
	
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData() ;
		if(this.sharePrjId!=null)
			xd.setParamValue("share_prjid", sharePrjId);
		
		return xd ;
	}
	
	public void fromXmlData(XmlData xd)
	{
		super.fromXmlData(xd);
		this.sharePrjId = xd.getParamValueStr("share_prjid") ;
	}
	
	public void fromJSON(JSONObject jo) throws Exception
	{
		super.fromJSON(jo);
		this.sharePrjId = jo.optString("share_prjid");
	}
}
