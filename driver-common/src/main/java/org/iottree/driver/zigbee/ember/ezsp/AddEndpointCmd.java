package org.iottree.driver.zigbee.ember.ezsp;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Configures endpoint information on the NCP. The NCP does not remember these settings 
 * after a reset. Endpoints can be added by the Host after the NCP has reset. 
 * Once the status of the stack changes to SL_STATUS_NETWORK_UP, 
 * endpoints can no longer be added and this command will respond with SL_ZIGBEE_EZSP_ERROR_INVALID_CALL.
 * 
 * @author jason.zhu
 */
public class AddEndpointCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0002 ;
	public static final String FRAME_NAME = "addEndpoint" ;
	
	/**
	 * uint8_t endpoint  The application endpoint to be added.
	 */
	int endpoint ;
	
	/**
	 * uint16_t profileId  The endpoint's application profile.
	 */
	int profileId ;
	
	/**
	 * uint16_t deviceId  The endpoint's device ID within the application profile.
	 */
	int deviceId ;
	
	/**
	 * uint8_t appFlags The device version and flags indicating description availability.
	 */
	int appFlags ;
	
	List<Integer> in_cluster_list ;
	
	List<Integer> out_cluster_list ;
	
	public AddEndpointCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public AddEndpointCmd(EmberManager emgr,int endpoint,int profileid,
			int deviceid,int app_flags,List<Integer> in_clusters,List<Integer> out_clusters)
	{
		super(emgr) ;
		this.endpoint = endpoint ;
		this.profileId = profileid;
		this.deviceId = deviceid;
		this.appFlags = app_flags;
		this.in_cluster_list = in_clusters;
		this.out_cluster_list = out_clusters;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME ;
	}

	
	public int getEndPoint()
	{
		return this.endpoint ;
	}
	
	public int getProfileId()
	{
		return this.profileId ;
	}
	
	public int getDeviceId()
	{
		return this.deviceId ;
	}
	
	public int getAppFlags()
	{
		return this.appFlags ;
	}
	
	public List<Integer> getInClusterList()
	{
		return this.in_cluster_list ;
	}
	
	public List<Integer> getOutClusterList()
	{
		return this.out_cluster_list ;
	}
	
	@Override
	protected int getParamsLen()
	{
		int in_c = 0 ;
		if(this.in_cluster_list!=null)
			in_c = this.in_cluster_list.size() ;
		int out_c = 0 ;
		if(this.out_cluster_list!=null)
			out_c = this.out_cluster_list.size() ;
		return 8+in_c*2+out_c*2;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		bs[offset] = (byte)this.endpoint ;
		DataUtil.setUInt16(this.profileId, bs, offset+1); // 1-2
		DataUtil.setUInt16(this.deviceId, bs, offset+3); // 3-4
		bs[offset+5] = (byte)this.appFlags ;
		int in_c = 0 ;
		if(this.in_cluster_list!=null)
			in_c = this.in_cluster_list.size() ;
		bs[offset+6] = (byte)in_c ;
		int out_c = 0 ;
		if(this.out_cluster_list!=null)
			out_c = this.out_cluster_list.size() ;
		bs[offset+7] = (byte)out_c ;
		if(in_c>0)
		{
			for(int i = 0 ; i < in_c ; i ++)
			{
				DataUtil.setUInt16(in_cluster_list.get(i), bs, offset+8+i*2);
			}
		}
		if(out_c>0)
		{
			int idx = offset+8+in_c*2 ;
			for(int i = 0 ; i < out_c ; i ++)
			{
				DataUtil.setUInt16(out_cluster_list.get(i), bs, idx+i*2);
			}
		}
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		this.endpoint = bs[offset] & 0xFF ;
		this.profileId = DataUtil.getUInt16(bs, offset+1) ;//1-2
		this.deviceId = DataUtil.getUInt16(bs, offset+3) ;//3-4
		this.appFlags = bs[offset+5] & 0xFF ;
		int in_c = bs[offset+6] & 0xFF ;
		int out_c = bs[offset+7] & 0xFF ;
		if(in_c>0)
		{
			this.in_cluster_list = new ArrayList<>(in_c) ;
			for(int i = 0 ; i < in_c ; i ++)
			{
				this.in_cluster_list.add(DataUtil.getUInt16(bs, offset+8+i*2)) ;
			}
		}
		if(out_c>0)
		{
			this.out_cluster_list = new ArrayList<>(out_c) ;
			int sidx = offset+8+in_c*2 ;
			for(int i = 0 ; i < out_c ; i ++)
			{
				this.out_cluster_list.add(DataUtil.getUInt16(bs, sidx+i*2)) ;
			}
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("epid",this.endpoint).put("profileid", this.profileId).put("deviceid", this.deviceId)
				.put("app_flags",this.appFlags).putOpt("in_clusters", Convert.combineWith(this.in_cluster_list, ','))
				.putOpt("out_clusters", Convert.combineWith(this.out_cluster_list, ','));
	}

}
