package org.iottree.ext.msg_net;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class MqttOut_NE extends MNNodeEnd
{
	String sendId = null;
	
	@Override
	public String getTP()
	{
		return "mqtt_out";
	}

	@Override
	public String getTPTitle()
	{
		return "MQTT Sender";
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
	public String getColor()
	{
		return "#debed7";
	}

	@Override
	public String getIcon()
	{
		return "PK_bridge";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("send_id", this.sendId);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.sendId = jo.optString("send_id");
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		if(Convert.isNullOrEmpty(this.sendId))
			return null ;
		Mqtt_M mm = (Mqtt_M)this.getOwnRelatedModule();
		Mqtt_M.SendConf sc = mm.getSendConfById(this.sendId) ;
		if(sc==null)
			return null ;
		
		try
		{
			mm.publish(sc.topic, msg.getPayloadStr());
			RT_DEBUG_ERR.clear("mqtt_send");
		}
		catch(Exception e)
		{
			RT_DEBUG_ERR.fire("mqtt_send", e.getMessage(),e);
		}
		return null;
	}
}
