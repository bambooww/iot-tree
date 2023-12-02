package org.iottree.core.util.queue;

public enum HandleResult
{
	Succ,//处理成功-对象会从队列中删除
	Failed_Retry_Later,//对象处理失败,需要以后再处理,可能需要把内容放到队列后面,并等待一定的时间再处理
	Handler_Invalid,//处理器本身不正常-这要求队列不做任何改变
}
