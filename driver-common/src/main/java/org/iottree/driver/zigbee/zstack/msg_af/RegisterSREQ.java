package org.iottree.driver.zigbee.zstack.msg_af;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.LatencyReq;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command enables the tester to register an application’s endpoint description.
 * 
 * AF层必须注册一个 Endpoint。否则，AF_DATA_REQUEST，AF_INCOMING_MSG，ZCL收发会出异常
 * 
 * @author jason.zhu
 */
public class RegisterSREQ extends MsgData
{
	public RegisterSREQ()
	{
		super(0x24,0x00) ;
	}
	
	/**
	 * 
	 * @param endpoint  zigbee-herdsman=01
	 * @param app_prof_id profileId 2 0x0104=Home Automation Profile
	 * @param app_dev_id DeviceId 2  DeviceID=0x0005(zigbee-herdsman),make znp accept endpoint=01
	 * @param app_dev_ver DeviceVersion 1
	 * @param latency_req
	 * @param app_in_cluster_ids e.g
	 *   InClusters:
			0x0000 Basic
			0x0006 OnOff
			0x0008 LevelControl
	 * @param app_out_cluster_ids
	 *   OutClusters:
			0x0019 OTA
	 */
	public RegisterSREQ(int endpoint,int app_prof_id,int app_dev_id,int app_dev_ver,
			int latency_req,List<Integer> app_in_cluster_ids,List<Integer> app_out_cluster_ids)
	{
		super(0x24,0x00) ;
		
		int app_in_clusters_num = app_in_cluster_ids!=null?app_in_cluster_ids.size():0 ;
		int app_out_num = app_out_cluster_ids!=null?app_out_cluster_ids.size():0 ;
		if(app_in_clusters_num>16)
			throw new IllegalArgumentException("app_in_clusters_num must in [0,16]");
		if(app_out_num>16)
			throw new IllegalArgumentException("app_out_clusters_num must in [0,16]");
		
		this.data = new byte[9+app_in_clusters_num*2+app_out_num*2] ;
		this.data[0] = (byte)endpoint ;
		DataUtil.setUInt16(app_prof_id, this.data, 1);
		DataUtil.setUInt16(app_dev_id, this.data, 3);
		this.data[5] = (byte)app_dev_ver ;
		this.data[6] = (byte)latency_req ;
		
		
		this.data[7] = (byte)app_in_clusters_num ;
		int sidx = 8 ;
		for(int i = 0 ; i < app_in_clusters_num ; i ++)
		{
			DataUtil.setUInt16(app_in_cluster_ids.get(i), this.data, sidx) ;
			sidx += 2 ;
		}
		
		this.data[sidx] = (byte)app_out_num ;
		sidx ++ ;
		for(int i = 0 ; i < app_out_num ; i ++)
		{
			DataUtil.setUInt16(app_out_cluster_ids.get(i), this.data, sidx) ;
			sidx += 2 ;
		}
	}
	
	@Override
	public String getTpName()
	{
		return "AF_REGISTER";
	}
	
	public int getEndpoint()
	{
		return this.data[0] & 0xFF ;
	}
	
	public int getAppProfId()
	{
		return DataUtil.getUInt16(this.data, 1) ;
	}
	
	public int getAppDevId()
	{
		return DataUtil.getUInt16(this.data, 3) ;
	}
	
	public int getAppDevVer()
	{
		return this.data[5] & 0xFF ;
	}
	
	public LatencyReq getLatencyReq()
	{
		return new LatencyReq(this.data[6] & 0xFF) ;
	}
	
	public int getAppInClusterNum()
	{
		return this.data[7] & 0xFF ;
	}
	
	public List<Integer> getAppInClusters()
	{
		int in_num = getAppInClusterNum() ;
		ArrayList<Integer> rets = new ArrayList<>() ;
		for(int i = 0 ; i < in_num ; i ++)
		{
			int cid = DataUtil.getUInt16(this.data, 8+i*2) ;
			rets.add(cid) ;
		}
		return rets ;
	}
	
	public int getAppOutClusterNum()
	{
		int in_num = getAppInClusterNum() ;
		return this.data[8+in_num*2] & 0xFF ;
	}
	
	public List<Integer> getAppOutClusters()
	{
		int in_num = getAppInClusterNum() ;
		int out_num = getAppOutClusterNum() ;
		ArrayList<Integer> rets = new ArrayList<>() ;
		for(int i = 0 ; i < out_num ; i ++)
		{
			int cid = DataUtil.getUInt16(this.data, 9+in_num*2+i*2) ;
			rets.add(cid) ;
		}
		return rets ;
	}

	@Override
	public int getDataLenMin()
	{
		return 9;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " endpoint="+DataUtil.transHex(this.getEndpoint(),2)+" profileid="+DataUtil.transHex(this.getAppProfId())+" devid="+DataUtil.transHex(this.getAppDevId())+" dev_ver="+this.getAppDevVer()+" latencyreq="+this.getLatencyReq()
			+"\r\n    cluster in=["+Convert.combineWith(getAppInClusters(), ',')+"] out=["+Convert.combineWith(getAppOutClusters(), ',')+"]";
	}
}
