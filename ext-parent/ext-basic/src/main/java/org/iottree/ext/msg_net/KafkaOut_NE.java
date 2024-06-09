package org.iottree.ext.msg_net;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class KafkaOut_NE extends MNNodeEnd
{
	String topic = null ;
	
	@Override
	public String getTP()
	{
		return "kafka_out";
	}

	@Override
	public String getTPTitle()
	{
		return "Kafka Out";
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

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		Kafka_M km = (Kafka_M)this.getOwnRelatedModule();
		if(km==null)
		{
			// may has some warn
			return null ;
		}

		
		km.RT_send(this.topic,msg.getPayloadStr()) ;
		this.RT_DEBUG_INF.fire("msg_in","topic="+this.topic+" out ",msg.getPayloadStr());
		return null;
	}

}
