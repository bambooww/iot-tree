package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNManager;

public abstract class ConnPtMsg extends ConnPt
{
	public ConnPtMsg()
	{}
	
	public ConnPtMsg(ConnProvider cp,String name,String title,String desc)
	{
		super(cp,name,title,desc) ;
	}
	
	/**
	 * top level recved msg
	 * @param topic
	 * @param msgob
	 */
	protected void RT_onMsgRecved(String topic,Object msgob)
	{
		UAPrj prj = this.getConnProvider().getBelongTo() ;
		MNManager mnmgr = MNManager.getInstance(prj) ;
		if(mnmgr==null)
			return ;
		mnmgr.RT_onConnPtMsgRecved(this,topic,msgob);
	}
	
	public boolean RT_supportSendMsgOut()
	{
		return false;
	}
	
	public boolean RT_sendMsgOut(String topic,byte[] msg,StringBuilder failedr) throws Exception
	{
		failedr.append("no impl") ;
		return false;
	}
}
