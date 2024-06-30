package org.iottree.ext.msg_net;

import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class MqttIn_NS extends MNNodeStart
{
	String recvId = null;
	
	@Override
	public String getTP()
	{
		return "mqtt_in";
	}

	@Override
	public String getTPTitle()
	{
		return "MQTT Receiver";
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
		jo.putOpt("recv_id", this.recvId);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.recvId = jo.optString("recv_id");
	}
}
