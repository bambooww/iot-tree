package org.iottree.ext.msg_net;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.json.JSONObject;

public class Mqtt_M extends MNModule
{
	static ArrayList<MNNode> supNodes = new ArrayList<>() ;
	static
	{
		supNodes.add(new MqttIn_NS()) ;
		supNodes.add(new MqttOut_NE()) ;
	}
	
	@Override
	protected List<MNNode> getSupportedNodes()
	{
		return supNodes;
	}

	@Override
	public String getTP()
	{
		return "mqtt";
	}

	@Override
	public String getTPTitle()
	{
		return "MQTT";
	}

	@Override
	public String getColor()
	{
		return "#debed7";
	}

	@Override
	public String getIcon()
	{
		return "\\uf1eb-270,\\uf1eb-90";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		// TODO Auto-generated method stub
		
	}

}
