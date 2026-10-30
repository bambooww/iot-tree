package org.iottree.driver.zigbee.zstack.msg_af;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLManager;
import org.iottree.driver.zigbee.zspec.event.IncomeMsgEvt;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * This callback message is in response to incoming data to any of the registered endpoints
 *  on this device. 
 *  
 * @author jason.zhu
 */
public class IncomingMsgAREQ_CB extends MsgRecv implements IncomeMsgEvt
{
	public static final int CMD0 = 0x44 ;
	public static final int CMD1 = 0x81 ;
	
	public IncomingMsgAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "AF_INCOMING_MSG";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 0x11;
	}

	@Override
	protected boolean checkCmd()
	{
		if(this.cmd0!=CMD0)
			return false;
		if(this.cmd1!=CMD1)
			return false;
		return true ;
	}
	
	/**
	 * Specifies the group ID of the device
	 * @return
	 */
	public int getGroupId()
	{
		return ((this.data[1] & 0xFF)<<8)+(this.data[0]&0xFF) ;
	}
	
	/**
	 * Specifies the cluster Id (only the LSB is used in V1.0 networks.)
	 * @return
	 */
	public int getClusterId()
	{
		return ((this.data[3] & 0xFF)<<8)+(this.data[2]&0xFF) ;
	}
	
	public ZCLCluster getCluster()
	{
		return ZCLClusterManager.getInstance().getCluserByID(this.getClusterId()) ;
	}

	public int getSrcAddr()
	{
		return ((this.data[5] & 0xFF)<<8)+(this.data[4]&0xFF) ;
	}
	
	public int getSrcEndpoint()
	{
		return this.data[6] & 0xFF ;
	}
	
	public int getDstEndpoint()
	{
		return this.data[7] & 0xFF ;
	}
	
	public boolean getWasBroadcast()
	{
		return this.data[8] !=0 ;
	}
	
	/**
	 * Indicates the link quality measured during reception
	 * @return
	 */
	public int getLinkQuality()
	{
		return this.data[9] & 0xFF ;
	}
	
	/**
	 * Specifies if the security is used or not
	 * @return
	 */
	public boolean getSecurityUse()
	{
		return this.data[10] !=0 ;
	}
	
	/**
	 * Specifies the timestamp of the message
	 * @return
	 */
	public long getTimeStamp()
	{
		return DataUtil.getUInt32(this.data,11) ; // 11-14
	}
	
	/**
	 * Specifies transaction sequence number of the message
	 * @return
	 */
	public int getTransSeqNumber()
	{
		return this.data[15] & 0xFF ;
	}
	
	public int getMsgDataLen()
	{//0-128
		return this.data[16] & 0xFF ;
	}
	
	public byte[] getMsgData()
	{
		int len = this.getMsgDataLen() ;
		if(len>128)
			return null ;
		byte[] r = new byte[len] ;
		System.arraycopy(this.data, 17, r, 0, len);
		return r ;
	}
	
	/**
	 * unpack zcl frame in payload
	 * @return
	 */
	public ZCLFrame getZCLFrame(StringBuilder failedr)
	{
		byte[] bs = this.getMsgData() ;
		if(bs==null||bs.length<=0)
			return null ;
		
		return ZCLManager.unpackedFrame(bs, failedr) ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		JSONObject r = new JSONObject().put("groupid",this.getGroupId()).put("clusterid", this.getClusterId())
				.putOpt("src_addr", this.getSrcAddr()).putOpt("src_ep", this.getSrcEndpoint())
				.putOpt("dst_ep", this.getDstEndpoint())
				.putOpt("broad",this.getWasBroadcast()).putOpt("link_qual", this.getLinkQuality())
				.putOpt("security", this.getSecurityUse()).putOpt("timestamp", this.getTimeStamp())
				.putOpt("trans_seq", this.getTransSeqNumber()).putOpt("msg_data", Convert.byteArray2HexStr(this.getMsgData(), "-"));
		
		StringBuilder failedr = new StringBuilder() ;
		ZCLFrame zf = this.getZCLFrame(failedr) ;
		if(zf!=null)
			r.putOpt("ZCL",zf.toJO());
		else
			r.putOpt("ZCL", "??? - "+failedr.toString());
		return r;
	}

//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " grpid="+this.getGroupId()+" clusterid="+this.getClusterId()+" src_addr="+this.getSrcAddr()+" src_ep="+this.getSrcEndpoint()
//			+" dst_ep="+this.getDstEndpoint()+" broad="+this.getWasBroadcast()+" link_qual="+this.getLinkQuality()
//			+" security="+this.getSecurityUse()+" ts="+this.getTimeStamp()+" trans_seq="+this.getTransSeqNumber()
//			+"\r\n\t msg_dlen="+this.getMsgDataLen() +"["+Convert.byteArray2HexStr(this.getMsgData(), "-")+"] ZCL="+getZCLFrame();
//	}
}
