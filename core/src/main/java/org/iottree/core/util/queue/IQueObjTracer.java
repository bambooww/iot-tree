package org.iottree.core.util.queue;

/**
 * 放入队列中的内容，如果实现此接口，则可以被外界进行状态跟踪
 * 比如，是否在队列中，是否在处理中等
 * @author zzj
 *
 */
public interface IQueObjTracer
{
	/**
	 * 队列中
	 */
	public static final int TRACE_IN_QUE = 0 ;
	/**
	 * 处理中
	 */
	public static final int TRACE_IN_PRO = 1 ;
	/**
	 * 处理成功
	 */
	public static final int TRACE_DEQUE_SUCC = 2 ;
	/**
	 * 处理错误
	 */
	public static final int TRACE_DEQUE_ERROR = 3 ;
	
	public void onQueObjTrace(int st) ; 
}