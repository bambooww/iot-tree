package org.iottree.ext.msg_net;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.json.JSONObject;

public class Kafka_M extends MNModule
{
	static ArrayList<MNNode> supNodes = new ArrayList<>() ;
	static
	{
		supNodes.add(new KafkaIn_NS()) ;
		supNodes.add(new KafkaOut_NE()) ;
	}
	
	@Override
	public String getTP()
	{
		return "kafka";
	}

	@Override
	public String getTPTitle()
	{
		return "Kafka";
	}
	
	@Override
	public String getColor()
	{
		return "#debed7";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0ec";
	}
	
	protected List<MNNode> getSupportedNodes() 
	{
		return supNodes;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		
	}
}
