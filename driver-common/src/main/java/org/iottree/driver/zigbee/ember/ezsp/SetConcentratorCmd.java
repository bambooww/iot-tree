package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Enable/disable concentrator support. 集中器
 * @author jason.zhu
 *
 */
public class SetConcentratorCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0010 ;
	public static final String FRAME_NAME = "setConcentrator" ;
	
	/**
	 * If this bool is true the concentrator support is enabled. 
	 * Otherwise is disabled. If this bool is false all the other arguments are ignored
	 */
	boolean on ;
	
	/**
	 * Must be either SL_ZIGBEE_HIGH_RAM_CONCENTRATOR or SL_ZIGBEE_LOW_RAM_CONCENTRATOR. The former is used 
	 * when the caller has enough memory to store source routes for the whole network. 
	 * In that case, remote nodes stop sending route records once the concentrator has successfully 
	 * received one. The latter is used when the concentrator has insufficient RAM 
	 * to store all outbound source routes. In that case, route records are sent to the concentrator 
	 * prior to every inbound APS unicast.
	 * SL_ZIGBEE_LOW_RAM_CONCENTRATOR = 0xFFF8
	 * SL_ZIGBEE_HIGH_RAM_CONCENTRATOR = 0xFFF9
	 * 
	 * uint16_t concentratorType
	 */
	int concentrator_tp = 0xFFF9 ;
	
	/**
	 * uint16_t minTime
	 * The minimum amount of time that must pass between MTORR broadcasts.
	 */
	int min_time ;
	
	/**
	 * uint16_t maxTime
	 * The maximum amount of time that can pass between MTORR broadcasts.
	 */
	int max_time ;
	
	/**
	 * uint8_t routeErrorThreshold
	 * The number of route errors that will trigger a re-broadcast of the MTORR.
	 */
	int route_err_thre ;
	
	/**
	 * uint8_t deliveryFailureThreshold
	 * The number of APS delivery failures that will trigger a re-broadcast of the MTORR.
	 */
	int delivery_fail_thre ;
	
	/**
	 * uint8_t maxHops
	 * The maximum number of hops that the MTORR broadcast will be allowed to have. 
	 * A value of 0 will be converted to the SL_ZIGBEE_MAX_HOPS value set by the stack.
	 */
	int max_hops ;
	
	public SetConcentratorCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SetConcentratorCmd(EmberManager emgr,boolean on,int concentrator_tp,int min_time,int max_time,
			int router_err_thre,int delivery_fail_thre,int max_hops)
	{
		super(emgr) ;
		this.on = on ;
		this.concentrator_tp = concentrator_tp ;
		this.min_time = min_time ;
		this.max_time = max_time ;
		this.route_err_thre = router_err_thre ;
		this.delivery_fail_thre = delivery_fail_thre;
		this.max_hops = max_hops ;
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

	
	public boolean getOnOrNot()
	{
		return this.on ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 10;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		bs[offset] = on?(byte)1:0 ;
		DataUtil.setUInt16(this.concentrator_tp, bs, offset+1); //1-2
		DataUtil.setUInt16(this.min_time, bs, offset+3); //3-4
		DataUtil.setUInt16(this.max_time, bs, offset+5); //5-6
		bs[offset+7] = (byte)route_err_thre;
		bs[offset+8] = (byte)delivery_fail_thre;
		bs[offset+9] = (byte)max_hops;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		this.on = bs[offset] !=0 ;
		this.concentrator_tp = DataUtil.getUInt16(bs, offset+1) ; //1-2
		this.min_time = DataUtil.getUInt16(bs, offset+3) ; //3-4
		this.max_time = DataUtil.getUInt16(bs, offset+5) ; //5-6
		this.route_err_thre = bs[offset+7] & 0xFF ;
		this.delivery_fail_thre = bs[offset+8] & 0xFF ;
		this.max_hops = bs[offset+9] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("on",this.on).put("concentrator_tp",this.concentrator_tp).put("min_time", this.min_time)
				.put("max_time", this.max_time).put("router_err_thre", this.route_err_thre)
				.put("delivery_fail_thre", this.delivery_fail_thre).put("max_hops", this.max_hops);
	}
}
