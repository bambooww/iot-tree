package org.iottree.ext.msg_net;

import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class KafkaIn_NS extends MNNodeStart
{
	String topic = null ;
	
	@Override
	public String getTP()
	{
		return "kafka_in";
	}

	@Override
	public String getTPTitle()
	{
		return "Kafka In";
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
		return "\\uf0ec";
	}
	
	public String getTopic()
	{
		return this.topic ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.topic))
		{
			failedr.append("no topic set") ;
			return false ;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("topic", this.topic) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.topic = jo.optString("topic",null) ;
	}


	// rt
	
	/**
	 * will called by Kafka_M
	 * @param topic
	 * @param msg
	 */
	void RT_onTopicMsgRecv(String topic,String msg)
	{
		MNMsg m = new MNMsg().asPayload(msg).asTopic(topic) ;
		RT_sendMsgOut(RTOut.createOutAll(m));
	}
}
