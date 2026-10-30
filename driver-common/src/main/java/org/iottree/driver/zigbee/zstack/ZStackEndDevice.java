package org.iottree.driver.zigbee.zstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.cmd_frm.ReadAttrRsp;
import org.iottree.driver.zigbee.zcl.cmd_frm.ReportAttr;
import org.iottree.driver.zigbee.zcl.util.DataVal;
import org.iottree.driver.zigbee.zcl.util.TagDef;
import org.iottree.driver.zigbee.zspec.ZSAttrVal;
import org.iottree.driver.zigbee.zspec.ZSCluster;
import org.iottree.driver.zigbee.zspec.ZSCoor;
import org.iottree.driver.zigbee.zspec.ZSDevice;
import org.iottree.driver.zigbee.zspec.ZSEndPoint;
import org.iottree.driver.zigbee.zspec.util.EndDeviceCap;
import org.iottree.driver.zigbee.zspec.util.PowerSrc;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.msg_af.IncomingMsgAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.EndDeviceAnnceIndSREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.IEEEAddrRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_zdo.IEEEAddrSREQ;
import org.iottree.driver.zigbee.zstack.msg_zdo.IEEEAddrSRSP;
import org.iottree.driver.zigbee.zstack.msg_zdo.NodeDescRspAREQ_CB;
import org.iottree.driver.zigbee.zstack.req_rsp.ReqRspFlw_CMD;
import org.json.JSONArray;
import org.json.JSONObject;

public class ZStackEndDevice extends ZSDevice 
{
	private static ILogger log = LoggerManager.getLogger(ZStackEndDevice.class) ;
	
	private ZStackCoor owner = null ;
	//private ZStackManager zsm = null ;
	
	
	
	
	
	IEEEAddrRspAREQ_CB ieeeAddrRsp = null ;
	
	ZStackManager zstack_mgr ;
	
	public ZStackEndDevice(ZStackCoor belongto,String ieeeaddr)
	{
		super(belongto,ieeeaddr) ;
		this.zstack_mgr = belongto.getZStackManager() ;
	}
	
	
//	public JSONObject toJODetail()
//	{
//		JSONObject r = this.toJO() ;
//		//增加一些运行时数据
//		r.put("link_qual", this.linkQual) ;
//		r.put("link_dt", this.linkDT) ;
//		return r ;
//	}
	
	public static ZStackEndDevice fromeJO(ZStackCoor coor,JSONObject jo)
	{
		String ieee = jo.optString("ieee_addr") ;
		if(Convert.isNullOrEmpty(ieee))
			return null ;
		ZStackEndDevice r = new ZStackEndDevice(coor,ieee) ;
		r.title = jo.optString("title") ;
		r.findDT = jo.optLong("find_dt",-1) ;
		r.updateDT = jo.optLong("update_dt") ;
		int short_addr = jo.optInt("short_addr",-1) ;
		if(short_addr>=0)
			r.short_addr = new ShortAddr(short_addr) ;
		int cap_v = jo.optInt("cap_v",-1);
		if(cap_v>=0)
			r.endDevCap = new EndDeviceCap(cap_v) ;
		
		r.linkQual = jo.optInt("link_qual", -1) ;
		r.linkDT = jo.optInt("link_dt", -1) ;
		
		r.facCode = jo.optInt("fac_code", -1) ;
		r.facName = jo.optString("fac_name") ;
		r.facModel = jo.optString("fac_model") ;
		int psv = jo.optInt("power_src", -1) ;
		r.powerSrc = PowerSrc.valOfInt(psv) ;
		
		JSONArray jarr = jo.optJSONArray("endpoints") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject ep_jo = jarr.getJSONObject(i) ;
				StringBuilder failedr = new StringBuilder() ;
				ZSEndPoint ep = ZSEndPoint.fromJO(r,ep_jo, failedr) ;
				r.endpoints.put(ep.getEndPointId(),ep) ;
			}
		}
		
		return r ;
	}
	
	// - mgr
	
	
	
	/**
	 * 更新厂家代码等信息
	 * @param nd_rsp
	 * @return
	 */
	public boolean MGR_updateBy(NodeDescRspAREQ_CB nd_rsp)
	{
		boolean b_dirty = false;
		int fac_code = nd_rsp.getManufacturerCode() ;
		if(fac_code!=this.facCode)
		{
			this.facCode = fac_code ;
			b_dirty = true ;
		}
		this.updateDT = System.currentTimeMillis() ;
		return b_dirty ;
	}
	
	
	//
	/**
	 * 被动接收到的Report Attr
	 * @param endpoint
	 * @param clusterid
	 * @param ra
	 * @return
	 */
	public boolean RT_onReportAttr(int endpoint,int clusterid,ReportAttr ra,StringBuilder failedr)
	{
		boolean b_dirty = false;
		ZSEndPoint ep = this.endpoints.get(endpoint) ;
		if(ep==null)
		{
			ep = new ZSEndPoint(this,endpoint) ;
			this.endpoints.put(endpoint, ep) ;
			b_dirty = true ;
		}
		
		ArrayList<ZSAttrVal> avs = new ArrayList<>() ;
		boolean b = ep.RT_onReportAttr(clusterid,ra,avs,failedr) ;
		if(b)
			b_dirty = true ;
		if(avs==null||avs.size()>0)
			RT_onUpdateAttrVals(avs) ;
		
		this.updateDT = System.currentTimeMillis() ;
		
		return b_dirty ;
	}
	
	
	
	
	
	
	
	
	// update to related tags
	private void RT_onUpdateAttrVals(List<ZSAttrVal> zavs)
	{
		UADev dev = getRelatedUADev() ;
		if(dev==null)
			return ;
		for(ZSAttrVal zav:zavs)
		{
			Object objv = zav.getObjVal() ;
			if(objv==null)
				continue ;
			int attr_id = zav.getAttrId() ;
			// DataVal dv = zav.getVal().getDataVal() ;
			String addr = zav.getTagAddr() ;
			String tag_vt = zav.getValTP() ;
			List<UATag> tags = dev.filterTagsAllByAddr(addr) ;
			if(tags==null||tags.size()<=0)
				continue ;
			
			String strv = objv.toString();
			for(UATag tag:tags)
			{
				try
				{
					tag.RT_setValRawStr(strv);
					//tag.RT_setValRaw(objv);
				}
				catch(Exception ee)
				{
					if(log.isWarnEnabled())
						log.warn("attr_id="+attr_id+" attr_tag_vt="+tag_vt+" addr="+addr+" set_strv="+objv+" to_tag="+tag.getNodePathCxt()+"\r\n\t"+ ee.getMessage());
				}
			}
		}
	}
	
	
	// --
	
	

}
