package org.iottree.core.msgnet;

/**
 * 实现此接口的节点，内部有自己的线程或其他异步任务。节点要支持启动，停止等内容
 * 
 * @author jason.zhu
 */
public interface IMNRunner
{

	public boolean RT_init(StringBuilder failedr);
	
	public boolean RT_start(StringBuilder failedr) ;
	
	public void RT_stop() ;
	
	public boolean RT_isRunning();
}
