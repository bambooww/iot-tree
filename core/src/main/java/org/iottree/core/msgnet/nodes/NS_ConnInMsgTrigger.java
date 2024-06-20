package org.iottree.core.msgnet.nodes;

import java.util.HashMap;
import java.util.Map;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.UAManager;
import org.iottree.core.UATag;
import org.iottree.core.conn.ConnPtMSG;
import org.iottree.core.conn.ConnPtMSG.BindHandler;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class NS_ConnInMsgTrigger   extends MNNodeStart 
{
	String connPtMsgId = null ;
	
	boolean bRawMsg = true;
	
	HashMap<String,String> tag2var = new HashMap<>() ;
	
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

	@Override
	public String getTP()
	{
		return "conn_in_msg_trigger";
	}

	@Override
	public String getTPTitle()
	{
		return g("conn_in_msg_trigger");
	}

	@Override
	public String getColor()
	{
		return "#007cb7";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#dddddd" ;
	}

	@Override
	public String getIcon()
	{
		return "\\uf0c1";
	}
	
	private ConnPtMSG getConnPt()
	{
		if(Convert.isNullOrEmpty(connPtMsgId))
		{
			return null ;
		}
		for(ConnProvider cp:this.getBelongTo().getPrj().getConnProviders())
		{
			ConnPt cpt = cp.getConnById(this.connPtMsgId) ;
			if(cpt!=null)
			{
				if(cpt instanceof ConnPtMSG)
					return (ConnPtMSG)cpt ;
				else
					return null ;
			}
		}
		return null ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		ConnPtMSG cpt = this.getConnPt() ;
		if(cpt==null)
		{
			failedr.append("no conn id set or no conn msg with id="+this.connPtMsgId+" found") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("conn_pt_id",this.connPtMsgId) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.connPtMsgId = jo.optString("conn_pt_id","") ;
	}
	
	public boolean RT_fireByConnInMsg(ConnPtMSG cpt_msg,String json_xml_str,Map<UATag,Object> tag2obj)
	{
		if(!cpt_msg.getId().equals(this.connPtMsgId))
			return false;
		
		BindHandler bh = cpt_msg.getBindHandler() ;
		
		if(bRawMsg)
		{
			MNMsg msg = new MNMsg();
			msg.asPayload(json_xml_str) ;
			RT_sendMsgOut(RTOut.createOutAll(msg));
			return true ;
		}
		
		JSONObject jo = new JSONObject() ;
		for(Map.Entry<UATag, Object> tag2o:tag2obj.entrySet())
		{
			String tagp = tag2o.getKey().getNodeCxtPathInPrj() ;
			String varn = tag2var.get(tagp) ;
			Object ov = tag2o.getValue() ;
			if(Convert.isNullOrEmpty(varn))
				varn = tagp ;
			jo.put(varn,ov) ;
		}
		
		MNMsg msg = new MNMsg();
		msg.asPayload(jo) ;
		RT_sendMsgOut(RTOut.createOutAll(msg));
		return true ;
	}
}
