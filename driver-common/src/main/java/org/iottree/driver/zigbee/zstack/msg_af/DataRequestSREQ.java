package org.iottree.driver.zigbee.zstack.msg_af;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.iottree.driver.zigbee.zcl.ZCLManager;
import org.iottree.driver.zigbee.zcl.cmd_frm.ConfigReport;
import org.iottree.driver.zigbee.zcl.util.AttrReportConfig;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zspec.util.TransmitOptions;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command is used by the tester to build and send a message through AF layer.
 * 
 * @author jason.zhu
 *
 */
public class DataRequestSREQ extends MsgData
{
	public DataRequestSREQ()
	{
		super(0x24,0x01) ;
	}

	public DataRequestSREQ(int dst_addr,int dst_endpoint,
			int src_endpoint,int cluster_id,int trans_id,int options,int radius,byte[] last_data)
	{
		super(0x24,0x01) ;
		
		int ldlen = last_data!=null?last_data.length:0 ;
		this.data = new byte[10+ldlen] ;
		DataUtil.setUInt16(dst_addr, this.data, 0) ; //0-1
		this.data[2] = (byte)dst_endpoint ;
		this.data[3] = (byte)src_endpoint ;
		DataUtil.setUInt16(cluster_id, this.data, 4) ; //4-5
		this.data[6] = (byte)trans_id ;
		this.data[7] = (byte)options ;
		this.data[8] = (byte)radius ;
		this.data[9] = (byte)ldlen ;
		if(ldlen>0)
			System.arraycopy(last_data, 0, this.data, 10, ldlen);
	}
	
	/**
	 * 创建cluster指令
	 * @param dst_addr
	 * @param endpoint
	 * @param cluster_id
	 * @param cmd_id
	 * @param trans_id
	 * @return
	 */
	public static DataRequestSREQ createClusterCmdReq(int dst_addr,int endpoint,int cluster_id,int cmd_id,Object[] pm_vals,boolean dir_s2c,int trans_id)
	{
		ZCLFrmClusterCmd cc = new ZCLFrmClusterCmd(cmd_id,dir_s2c) ;
		cc.asCmdParams(cluster_id, pm_vals) ;
		cc.asSeqNum(trans_id) ;
		
		byte[] zcl_bs = cc.packFrame() ;
		DataRequestSREQ r = new DataRequestSREQ(dst_addr,endpoint,endpoint,cluster_id,
				trans_id,0,30,zcl_bs) ;
		return r ;
	}
	
	
	public static DataRequestSREQ createConfigAttrReportReq(int dst_addr,int endpoint,int cluster_id,boolean dir_s2c,int trans_id,
			List<AttrReportConfig> attr_reports)
	{
		ConfigReport cr = new ConfigReport() ;
		cr.setAttrReportConfigs(attr_reports);
		
		byte[] zcl_bs = cr.packFrame() ;
		DataRequestSREQ r = new DataRequestSREQ(dst_addr,endpoint,endpoint,cluster_id,
				trans_id,0,30,zcl_bs) ;
		return r ;
	}
	
	@Override
	public String getTpName()
	{
		return "AF_DATA_REQUEST";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 10;
	}
	
	public ShortAddr getDstAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 0)) ;
	}
	
	public int getDstEndpoint()
	{
		return this.data[2] & 0xFF ;
	}
	
	public int getSrcEndpoint()
	{
		return this.data[3] & 0xFF ;
	}
	
	public int getClusterId()
	{
		return DataUtil.getUInt16(this.data, 4) ; //4-5
	}
	
	public int getTransId()
	{
		return this.data[6] & 0xFF ;
	}
	
	/**
	 * Transmit options bit mask according to the following defines
		from AF.h: bit 1: sets ‘Wildcard Profile ID’; bit 4: turns on/off
		‘APS ACK’; bit 5 sets ‘discover route’; bit 6 sets ‘APS
		security’; bit 7 sets ‘skip routing’.

	 * @return
	 */
	public TransmitOptions getOptions()
	{
		return new TransmitOptions(this.data[7] & 0xFF) ;
	}
	
	/**
	 * Specifies the number of hops allowed delivering the message (see AF_DEFAULT_RADIUS.)
	 * @return
	 */
	public int getRadius()
	{
		return this.data[8] & 0xFF ;
	}
	
	public int getLastDataLen()
	{
		return this.data[9] & 0xFF ;
	}
	
	public byte[] getLastData()
	{
		int len = this.getLastDataLen() ;
		if(len>128) return null ;
		byte[] r = new byte[len] ;
		System.arraycopy(this.data, 10, r, 0, len);
		return r ;
	}
	
	/**
	 * unpack zcl frame in payload
	 * @return
	 */
	public ZCLFrame getZCLFrame(StringBuilder failedr)
	{
		byte[] bs = this.getLastData() ;
		if(bs==null||bs.length<=0)
			return null ;
		
		return ZCLManager.unpackedFrame(bs, failedr) ;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		ret += " dst_addr="+getDstAddr()+" dst_ep="+this.getDstEndpoint()
		+" src_ep="+this.getSrcEndpoint()+" cluster_id="+this.getClusterId()+" trans_id="+this.getTransId()
		+" options="+getOptions()+" radius="+this.getRadius() +" dlen="+this.getLastDataLen();
		
		StringBuilder failedr = new StringBuilder() ;
		ZCLFrame zf = this.getZCLFrame(failedr) ;
		if(zf!=null)
			ret += "\r\n    ZCL="+zf.toJO().toString();
		else
			ret += "\r\n    ZCL=??? - "+failedr.toString();
		return ret ;
	}
}
