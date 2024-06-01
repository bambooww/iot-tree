package org.iottree.core.msgnet;

import org.iottree.core.util.Lan;

/**
 * 实现此接口的节点，内部有自己的线程或其他异步任务。节点要支持启动，停止等内容
 * 
 * @author jason.zhu
 */
public interface IMNRunner
{

	//public boolean RT_init(StringBuilder failedr);
	
	public boolean RT_start(StringBuilder failedr) ;
	
	public default boolean RT_start_main(StringBuilder failedr)
	{
		if(this instanceof MNBase)
		{
			if(!((MNBase)this).isEnable())
			{
				Lan lan = Lan.getLangInPk(MNBase.class) ;
				failedr.append(lan.g("not_enabled")) ;
				return false;
			}
		}
		
		return RT_start(failedr) ;
	}
	
	public void RT_stop() ;
	
	public boolean RT_isRunning();
	
	public boolean RT_isSuspendedInRun(StringBuilder reson) ;
}
