package org.iottree.core.node;

import java.util.List;

import org.iottree.core.node.NodeMsg.MsgTp;
import org.iottree.core.util.Convert;

public class MqttUtil
{
	/**
	 * _n/sor_tar/tp
	 * @param nm
	 * @return
	 */
	public static String calMqttTopic(NodeMsg nm)
	{
		String topic="_n/"+nm.getSorId()+"/" ;
		
		String tarid = nm.getTarId() ;
		if(Convert.isNotNullEmpty(tarid))
			topic += tarid ;
		else
			topic += "_";
		topic += "/"+nm.msgTp.toString();
		return topic ;
	}
	
	
	
	public static NodeMsg calNodeMsg(String mqtt_topic,byte[] msg)
	{
		List<String> tps = Convert.splitStrWith(mqtt_topic, "/") ;
		if(tps.size()!=4)
			return null ;
		if(!"_n".equals(tps.get(0)))
			return null ;
		
		String sor = tps.get(1);
		String tar = tps.get(2) ;
		if("_".equals(tar))
			tar = null ;
		MsgTp mt = MsgTp.valueOf(tps.get(3));
		if(mt==null)
			return null ;
		
		NodeMsg r = new NodeMsg() ;
		r.msgTp = mt ;
		r.sorId = sor;
		r.tarId = tar;
		r.content = msg ;
		return r;
	}
}
