package org.iottree.core.conn.masyn;

/**
 * 支持ack的信息进行回调接口
 * 此接口应该有继承MCmdAsynStateM的类实现，这样系统能够自动产生回调
 * @author zzj
 *
 */
public interface IAckListener
{
	/**
	 * 
	 * @param ackid 被ack信息内部自带的ackid
	 * @param ack_cmd 被ack信息的原始命令
	 */
	public void onAckRecved(String ackid,String ack_cmd) ;
	
	/**
	 * 在ack机制下，要考虑ack自身发送失败的情况，那么有可能接收端收到正常的信息
	 * 但发送端没有收到ack的情况，导致发送端重新发送相同的信息
	 * 在这种情况下，接收端应该能够判断避免消息重复。但还是会及时返回ack给发送端
	 * @param ackid
	 * @param ack_cmd
	 * @return
	 */
//	public boolean onChkAlreadyRecved(String ackid,String ack_cmd) ;
}
