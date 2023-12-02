package org.iottree.core.util.queue;

/**
 * 放入队列中的内容，如果实现此接口，则可以被外界进行搜索
 * 判断某个id对应的对象是否在队列中。
 * @author zzj
 *
 */
public interface IQueObjId
{
	public String getQueObjId() ;
}
