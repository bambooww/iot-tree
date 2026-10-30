package org.iottree.driver.zigbee.zstack.msg_af;

import java.util.HashMap;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.iottree.driver.zigbee.zcl.ZCLManager;
import org.iottree.driver.zigbee.zcl.cluster_cmd.GreenPowerCmd;
import org.iottree.driver.zigbee.zspec.util.TransmitOptions;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This extended form of the AF_DATA_REQUEST must be used to send an inter-pan message
(note that the target code must be compiled with the INTER_PAN flag defined.) This extended
data request must also be used when making a request with a huge data byte count which is
defined to be a size that would cause the RPC request to exceed the maximum allowed size:
Where sizeof(AF_DATA_REQUEST_EXT) counts everything but the data bytes and now stands at
20. When making an AF_DATA_REQUEST_EXT with a huge data byte count, the request shall
not contain any data bytes. The huge data buffer is sent over separately as a sequence of one or
more AF_DATA_STORE requests. Note that the outgoing huge message is timed-out in 15
seconds; thus all AF_DATA_STORE requests must be completed within 15 seconds of an
AF_DATA_REQUEST_EXT with a huge data byte count. And any AF_DATA_REQUEST_EXT
with a huge data byte count must be completed (or timed-out) before another will be started. The
default timeout can be changed by defining the following to other values:

 */
public class DataRequestExtSREQ extends MsgData
{
	public DataRequestExtSREQ()
	{
		super(0x24,0x02) ;
	}
	
	/**
	 * 
	 * @param dst_addr_mode 3=8bytes addr,2=2bytes
	 * @param dst_addr LSB to MSB for the long or short address of the destination device (upper 6 bytes
					are don’t care when short address.)
	 * @param dst_endpoint Endpoint of the destination device (but don’t care if the DstPanId is non-zero,
					which indicates an inter-pan message.)  242-代表zigbee2mqtt定义
	 * @param dst_pan_id PanId of the destination device: 0x0000=Intra-Pan; otherwise, Inter-Pan.
	 * @param src_endpoint Endpoint of the source device.
	 * @param cluster_id Specifies the cluster ID
	 * @param trans_id Specifies the transaction sequence number of the message.
	 * @param options Transmit options bit mask according to the following defines from AF.h: bit 1:
					sets ‘Wildcard Profile ID’; bit 4: turns on/off ‘APS ACK’; bit 5 sets ‘discover
					route’; bit 6 sets ‘APS security’; bit 7 sets ‘skip routing’. (This doesn’t apply for
					an inter-pan message.)
	 * @param radius Specifies the number of hops allowed delivering the message (reference
					DEF_NWK_RADIUS.) ,半径如30=最大30跳
					
			Len  Length of the data. If a large data length causes the MT command to exceed
					MT_RPC_DATA_MAX, then zero bytes of the data shall be sent with this
					request and the data shall be transferred in as many MT_AF_DATA_STORE
					requests as necessary
	 * @param last_data 0-128 bytes data
	 */
	private DataRequestExtSREQ(int dst_addr_mode,byte[] dst_addr,
			int dst_endpoint,int dst_pan_id,
			int src_endpoint,int cluster_id,int trans_id,int options,int radius,byte[] last_data)
	{
		super(0x24,0x02) ;
		
		int ldlen = last_data!=null?last_data.length:0 ;
		this.data = new byte[20+ldlen] ;
		
		this.data[0] = (byte)dst_addr_mode ;
		System.arraycopy(dst_addr, 0, this.data, 1, 8); //1-8
		this.data[9] = (byte)dst_endpoint;
		DataUtil.setUInt16(dst_pan_id, this.data, 10) ; //10-11
		
		this.data[12] = (byte)src_endpoint ;
		DataUtil.setUInt16(cluster_id, this.data, 13) ; //13-14
		this.data[15] = (byte)trans_id ;
		this.data[16] = (byte)options ;
		this.data[17] = (byte)radius ;
		DataUtil.setUInt16(ldlen, this.data, 18) ; //18-19
		if(ldlen>0)
			System.arraycopy(last_data, 0, this.data, 20, ldlen);
	}
	
	private static byte[] transAddr2byte8(int addr2byte)
	{
		byte[] ret = new byte[8] ;
		DataUtil.setUInt16(addr2byte, ret, 0);
		return ret ;
	}
	
	private static byte[] transAddr2byte8(String addr)
	{
		byte[] ret = new byte[8] ;
		DataUtil.transStr2IEEEAddr(addr, ret, 0) ;
		return ret ;
	}
	
	public DataRequestExtSREQ(int addr2byte,
			int dst_endpoint,int dst_pan_id,
			int src_endpoint,int cluster_id,int trans_id,int options,int radius,byte[] last_data)
	{
		this(2,transAddr2byte8(addr2byte),dst_endpoint,dst_pan_id,
				src_endpoint,cluster_id,trans_id,options,radius,last_data);
	}
	
	public DataRequestExtSREQ(String addr8,
			int dst_endpoint,int dst_pan_id,
			int src_endpoint,int cluster_id,int trans_id,int options,int radius,byte[] last_data)
	{
		this(3,transAddr2byte8(addr8),dst_endpoint,dst_pan_id,
				src_endpoint,cluster_id,trans_id,options,radius,last_data);
	}
	
	public static DataRequestExtSREQ createClusterCmdReq(int dst_addr,int endpoint,int cluster_id,int cmd_id,boolean dir_s2c,int trans_id)
	{
		return createClusterCmdReq( dst_addr, endpoint, cluster_id, cmd_id,null, dir_s2c, trans_id);
	}
	
	public static DataRequestExtSREQ createClusterCmdReq(int dst_addr,int endpoint,int cluster_id,int cmd_id,HashMap<String,Object> pm_name2val,boolean dir_s2c,int trans_id)
	{
		ZCLFrmClusterCmd cc = new ZCLFrmClusterCmd(cmd_id,dir_s2c) ;
		//cc.asClusterID(cluster_id) ;
		cc.asCmdParams(cluster_id, pm_name2val) ;
		cc.asSeqNum(trans_id) ;
		
		byte[] zcl_bs = cc.packFrame() ;
		DataRequestExtSREQ r = new DataRequestExtSREQ(dst_addr,endpoint,0,endpoint,cluster_id,
				trans_id,0,30,zcl_bs) ;
		return r ;
	}
	
	public static DataRequestExtSREQ createClusterGreenPowerCmd(int dst_addr,GreenPowerCmd gp_c,int trans_id)
	{
		int endpoint = 242 ;
		int cluster_id = 0x0021 ;//33
		gp_c.asSeqNum(trans_id) ;
		
		byte[] zcl_bs = gp_c.packFrame() ;
		DataRequestExtSREQ r = new DataRequestExtSREQ(dst_addr,endpoint,0,endpoint,cluster_id,
				trans_id,0,30,zcl_bs) ;
		return r ;
	}
	
	@Override
	public String getTpName()
	{
		return "AF_DATA_REQUEST_EXT";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 20;
	}
	
	public int getDstAddrMode()
	{
		return this.data[0] & 0xFF ;
	}
	
	public String getDstAddrModeT()
	{
		int m = getDstAddrMode();
		switch(m)
		{
		case 2: return "short";
		case 3: return "long";
		default: return ""+m ;
		}
	}
	
	public int getDstAddrShort()
	{
		return DataUtil.getUInt16(this.data, 1) ;
	}
	
	public String getDstAddrLong()
	{
		return DataUtil.transIEEEAddr(this.data, 1) ; //1-8
	}
	
	public String getDstAddrStr()
	{
		int m = getDstAddrMode();
		switch(m)
		{
		case 2: return DataUtil.transHexAddr(this.getDstAddrShort()) ;
		case 3: return getDstAddrLong();
		default: return "" ;
		}
	}
	
	public int getDstEndpoint()
	{
		return this.data[9] & 0xFF ;
	}
	
	public int getDstPanId()
	{
		return DataUtil.getUInt16(this.data, 10) ; //10-11
	}
	
	public int getSrcEndpoint()
	{
		return this.data[12] & 0xFF ;
	}
	
	public int getClusterId()
	{
		return DataUtil.getUInt16(this.data, 13) ; //13-14
	}
	
	public ZCLCluster getClusterDef()
	{
		return ZCLClusterManager.getInstance().getCluserByID(this.getClusterId()) ;
	}
	
	public int getTransId()
	{
		return this.data[15] & 0xFF ;
	}
	
	/**
	 * Transmit options bit mask according to the following defines from AF.h: bit 1:
		sets ‘Wildcard Profile ID’; bit 4: turns on/off ‘APS ACK’; bit 5 sets ‘discover
		route’; bit 6 sets ‘APS security’; bit 7 sets ‘skip routing’. (This doesn’t apply for
		an inter-pan message.)
	 * @return
	 */
	public TransmitOptions getOptions()
	{
		return new TransmitOptions(this.data[16] & 0xFF) ;
	}
	
	public int getRadius()
	{
		return this.data[17] & 0xFF ;
	}
	
	public int getLastDataLen()
	{
		return DataUtil.getUInt16(this.data, 18) ; //18-19
	}
	
	public byte[] getLastData()
	{
		int len = this.getLastDataLen() ;
		if(len>128) return null ;
		byte[] r = new byte[len] ;
		System.arraycopy(this.data, 20, r, 0, len);
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
		ZCLCluster cdef = getClusterDef() ;
		String cluster_n = "" ;
		if(cdef!=null)
			cluster_n = cdef.getName() ;
		ret += " dst_addr_mode="+getDstAddrModeT()+" dst_addr="+getDstAddrStr()+" dst_ep="+this.getDstEndpoint()+" dst_panid="+DataUtil.transHexAddr(this.getDstPanId())
			+" src_ep="+this.getSrcEndpoint()+" cluster_id="+this.getClusterId();
		if(Convert.isNotNullEmpty(cluster_n))
			ret += " cluster_n="+cluster_n ;
		ret += " trans_id="+this.getTransId()
			+" options="+getOptions()+" radius="+this.getRadius() +" dlen="+this.getLastDataLen()+" dd="+Convert.byteArray2HexStr(getLastData(), "-");
		
		StringBuilder failedr = new StringBuilder() ;
		ZCLFrame zf = this.getZCLFrame(failedr) ;
		if(zf!=null)
			ret += "\r\n    ZCL="+zf.toJO().toString();
		else
			ret += "\r\n    ZCL=??? - "+failedr.toString();
		return ret ;
	}

}
