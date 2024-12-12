package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.DevDriver;
import org.iottree.core.DevDriverMsgOnly;
import org.iottree.core.UACh;
import org.iottree.core.UAPrj;
import org.iottree.core.cxt.IJsProp;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.msgnet.MNManager;

public abstract class ConnPtMsg extends ConnPt implements IJsProp
{
	public ConnPtMsg()
	{}

	public ConnPtMsg(ConnProvider cp,String name,String title,String desc)
	{
		super(cp,name,title,desc) ;
	}
	
	private JsProp jsP = null;
	
	@Override
	public JsProp toJsProp()
	{
		if(jsP!=null)
			return jsP ;
		jsP = new JsProp("$connpt",this,null,true,"ConnPtMsg","Channel relateed ConnPtMsg") ;
		return jsP;
	}
	
	@Override
	public void constructSubForCxtHelper()
	{
		
	}
	
	private transient boolean drvMsgOnlyGotten = false;
	private transient DevDriverMsgOnly drvMsgOnly = null ;
	
	protected final DevDriverMsgOnly getDriverMsgOnly()
	{
		if(drvMsgOnlyGotten)
			return drvMsgOnly ;
		
		try
		{
			UACh ch = this.getJoinedCh() ;
			if(ch==null)
				return null;
			DevDriver dd = ch.getDriver() ;
			if(dd==null)
				return null;
			
			if(dd instanceof DevDriverMsgOnly)
			{
				drvMsgOnly = (DevDriverMsgOnly)dd ;
			}
			return drvMsgOnly ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null ;
		}
		finally
		{
			drvMsgOnlyGotten = true ;
		}
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
	
	@JsDef
	public boolean RT_supportSendMsgOut()
	{
		return false;
	}
	
	
	public boolean RT_sendMsgOut(String topic,byte[] msg,StringBuilder failedr) throws Exception
	{
		failedr.append("no impl") ;
		return false;
	}
	
	@JsDef(method_params_title = "topic,msg bytes")
	public Object rt_send_msg_bytes(String topic,byte[] msg) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!RT_sendMsgOut(topic,msg,failedr))
			return failedr.toString() ;
		return true ;
	}
	
	@JsDef(method_params_title = "topic,msg string")
	public Object rt_send_msg_str(String topic,String msg) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!RT_sendMsgOut(topic,msg.getBytes("UTF-8"),failedr))
			return failedr.toString() ;
		return true ;
	}
}
