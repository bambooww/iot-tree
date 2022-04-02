package org.iottree.core.conn;

import java.util.HashMap;
import java.util.List;

import org.iottree.core.UACh;
import org.iottree.core.UATag;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;


/**
 * based on msg connections,
 * 
 * @author jason.zhu
 *
 */
public abstract class ConnPtMSGTopic  extends ConnPtMSG //ConnPtBinder
{
	public abstract List<String> getMsgTopics() ;
	
	protected abstract void onRecvedMsg(String topic,byte[] bs) throws Exception;
	
	public abstract boolean sendMsg(String topic,byte[] bs) throws Exception ;
	
	public abstract void runOnWrite(UATag tag,Object val) throws Exception;
}
