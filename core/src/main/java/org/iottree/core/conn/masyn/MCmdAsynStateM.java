package org.iottree.core.conn.masyn;

/**
 * asyn handler state machine
 * 
 * 
 * 注：如果要接收处理ack消息，则继承该类的类，应该实现接口IAckListener
 * 
 * @author Jason Zhu
 *
 */
public abstract class MCmdAsynStateM
{
	static public enum StateRes
	{
		TryLater,//need retry
		Ok,//ok state
		Stop//need to break conn
	}
	
	//protected MCmdAsynEndPoint relatedEP = null ;
	
	long lastPulseMS = -1 ;

	public MCmdAsynStateM() //(MCmdAsynEndPoint ep)
	{
//		relatedEP = ep ;
		//
	}
	
//	public MCmdAsynEndPoint getRelatedEndPoint()
//	{
//		return relatedEP ;
//	}
	
	public abstract void onMCmdAsynRecved(MCmdAsyn mca) ;
	
	public abstract void onMCmdAsynBroken() ;
	
	/**
	 * 
	 * @return
	 */
	public abstract boolean checkStateMOk();
	
	public String getRecvFileBase()
	{
		return null ;
	}
	/**
	 * get state machine check interval
	 * @return
	 */
	public long getPulseIntervalMS()
	{
		return 500 ;
	}
	
	void pulseWithInterval()
	{
		long ct = System.currentTimeMillis() ;
		if(ct-lastPulseMS<getPulseIntervalMS())
			return ;
		StateRes sr = onPulseStateMachine();
//		if(sr==StateRes.Stop)
//		{
//			this.relatedEP.stop() ;
//			this.relatedEP.close();
//		}
		lastPulseMS = ct ;
	}
	/**
	 * loop called by endpoint,
	 * @return
	 */
	public abstract StateRes onPulseStateMachine() ;
}
