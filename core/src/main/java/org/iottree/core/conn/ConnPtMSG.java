package org.iottree.core.conn;

import java.util.HashMap;
import java.util.List;


/**
 * based on msg connections,
 * 
 * @author jason.zhu
 *
 */
public abstract class ConnPtMSG  extends ConnPtBinder
{
	public abstract List<String> getMsgTopics() ;
	
	protected abstract void onRecvedMsg(String topic,byte[] bs) throws Exception;
	
	public abstract boolean sendMsg(String topic,byte[] bs) throws Exception ;
}
