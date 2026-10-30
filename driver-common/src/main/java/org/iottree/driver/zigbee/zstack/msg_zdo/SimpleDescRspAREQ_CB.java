package org.iottree.driver.zigbee.zstack.msg_zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * This callback message is in response to the ZDO Simple Descriptor Request

 * @author jason.zhu
 */
public class SimpleDescRspAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0x84 ;
	
	public SimpleDescRspAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_SIMPLE_DESC_RSP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 14;
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
	
	public ShortAddr getSrcAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 0)) ; //0-1
	}

	public boolean getStatus()
	{//Status is either Success (0) or Failure (1).
		return this.data[2]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[2] & 0xFF;
	}
	
	/**
	 * Specifies Device’s short address that this response describes.
	 * @return
	 */
	public ShortAddr getNwkAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 3)) ; //3-4
	}
	
	/**
	 * Specifies the length of the simple descriptor
	 * @return
	 */
	public int getLen()
	{
		return this.data[5] & 0xFF ;
	}
	
	/**
	 * Specifies Endpoint of the device
	 * @return
	 */
	public int getEndpoint()
	{
		return this.data[6] & 0xFF ;
	}
	
	/**
	 * The profile Id for this endpoint.
	 * @return
	 */
	public int getProfileId()
	{
		return DataUtil.getUInt16(this.data, 7) ; //7-8
	}
	
	/**
	 * The Device Description Id for this endpoint.
	 * @return
	 */
	public int getDeviceId()
	{
		return DataUtil.getUInt16(this.data, 9) ; //9-10
	}
	
	/**
	 * 0 – Version 1.00
	 * 0x01-0x0F – Reserved.
	 * @return
	 */
	public int getDeviceVer()
	{
		return this.data[11] & 0xFF ;
	}
	
	public int getInClusterNum()
	{
		return this.data[12] & 0xFF ;
	}
	
	public List<Integer> getInClusterList()
	{
		int n = getInClusterNum() ;
		ArrayList<Integer> rets = new ArrayList<>() ;
		if(n<=0)
			return rets ;
		for(int i = 0 ; i < n ; i ++)
		{
			rets.add(DataUtil.getUInt16(this.data, 13+i*2)) ;
		}
		return rets ;
	}
	
	public int getOutClusterNum()
	{
		int in_n = getInClusterNum() ;
		int sidx = 13+in_n*2 ;
		return this.data[sidx] & 0xFF ;
	}
	
	public List<Integer> getOutClusterList()
	{
		int n = getOutClusterNum() ;
		int in_n = getInClusterNum() ;
		int sidx = 14+in_n*2 ;
		ArrayList<Integer> rets = new ArrayList<>() ;
		if(n<=0)
			return rets ;
		for(int i = 0 ; i < n ; i ++)
		{
			rets.add(DataUtil.getUInt16(this.data, sidx+i*2)) ;
		}
		return rets ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().putOpt("src_addr", this.getSrcAddr()).put("status",this.getStatusVal()).put("status_ok", this.getStatus())
				.putOpt("nwk_addr", this.getNwkAddr()).put("len", this.getLen()).put("endpoint", this.getEndpoint())
				.put("profileid", this.getProfileId()).put("deviceid", this.getDeviceId()).put("dev_ver", this.getDeviceVer())
				.put("in_cluster_n", this.getInClusterNum()).putOpt("in_clusters", this.getInClusterList())
				.put("out_cluster_n", this.getOutClusterNum()).putOpt("out_clusters", this.getOutClusterList());
	}

//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " status="+this.getStatus()+" src_addr="+this.getSrcAddr()+" nwk_addr="+this.getNwkAddr()+" len="+this.getLen() +" ep="+this.getEndpoint()
//			+" profile_id="+this.getProfileId()+" device_id="+this.getDeviceId()+" dev_ver="+this.getDeviceVer()
//			+"\r\n\t in_clusters="+Convert.combineWith(this.getInClusterList(), ',')
//			+" out_clusters="+Convert.combineWith(this.getOutClusterList(), ',') ;
//	}

}
