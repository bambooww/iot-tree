package org.iottree.driver.zigbee.zstack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.iottree.driver.zigbee.zcl.cmd_frm.ReadAttr;
import org.iottree.driver.zigbee.zcl.cmd_frm.ReadAttrRsp;
import org.iottree.driver.zigbee.zcl.cmd_frm.ReportAttr;
import org.iottree.driver.zigbee.zcl.util.AttrReportConfig;
import org.iottree.driver.zigbee.zcl.util.DataVal;
import org.iottree.driver.zigbee.zspec.DevNetRelation;
import org.iottree.driver.zigbee.zspec.ZDOCluster;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZSController;
import org.iottree.driver.zigbee.zspec.ZSCoor;
import org.iottree.driver.zigbee.zspec.ZSDevice;
import org.iottree.driver.zigbee.zspec.ZSEndPoint;
import org.iottree.driver.zigbee.zspec.event.PermitJoinEvt;
import org.iottree.driver.zigbee.zspec.types.BroadcastAddr;
import org.iottree.driver.zigbee.zspec.types.NetworkParam;
import org.iottree.driver.zigbee.zspec.types.ZSStartResult;
import org.iottree.driver.zigbee.zspec.util.DeviceState;
import org.iottree.driver.zigbee.zspec.util.EndDeviceCap;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.msg_af.*;
import org.iottree.driver.zigbee.zstack.msg_zdo.*;
import org.iottree.driver.zigbee.zstack.msg_sys.*;
import org.iottree.driver.zigbee.zstack.msg_util.GetDeviceInfoSREQ;
import org.iottree.driver.zigbee.zstack.msg_util.GetDeviceInfoSRSP;
import org.iottree.driver.zigbee.zstack.req_rsp.*;
import org.iottree.driver.zigbee.zstack.util.NVItemIDS;
import org.iottree.driver.zigbee.zstack.util.ZnpVersion;
import org.json.JSONArray;
import org.json.JSONObject;

public class ZStackCoor extends ZSCoor
{
	private ILogger log ;
	
	ZStackManager zsm = null ;
	
	
	
	VersionSRSP version = null ;
	
	
	
	/**
	 * coor's network addr info
	 */
	NwkAddrRspAREQ_CB nwk_addr_inf = null ;
	
	
	
	GetDeviceInfoSRSP deviceInfo = null ;
	
	
	
	
	public ZStackCoor(ZStackManager zsm)
	{
		super(zsm.getDriver()) ;
		this.zsm = zsm ;
		log = LoggerManager.getLogger("ZStackCoor_"+zsm.getBelongToCh().getName()) ;
		log.setCurrentLogLevel(ILogger.LOG_LEVEL_DEBUG);
	}
	
	public ILogger getLogger()
	{
		return this.log ;
	}
	
	public ZStackManager getZStackManager()
	{
		return this.zsm ;
	}
	
	@Override
	public String getCoorName()
	{
		return "zstack";
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.putOpt("ieee_addr", this.ieee_addr)
			//.putOpt("pan_id", this.pan_id).putOpt("ext_pan_id", this.ext_pan_id).putOpt("nwk_key", this.nwkKey)
			;
		if(cap!=null)
		{
			ret.put("capabilities_v", this.cap.getCapVal()) ;
			ret.put("capabilities", this.cap.toJO()) ;
		}
		if(this.version!=null)
			ret.put("version",this.version.toRecvJO()) ;
		if(this.startupST!=null)
			ret.put("startup_st", this.startupST.toJO()) ;
		if(this.deviceST!=null)
			ret.put("device_st", this.deviceST.toString()) ;
		if(this.nwk_addr_inf!=null)
			ret.put("nwk_addr_info", this.nwk_addr_inf.toRecvJO()) ;
		if(this.deviceInfo!=null)
			ret.put("device_info", this.deviceInfo.toRecvJO()) ;
		
		
		JSONArray end_devs = new JSONArray() ;
		ret.put("end_devices", end_devs) ;
		for(ZSDevice ed:this.ieee2device.values())
		{
			end_devs.put(ed.toJO()) ;
		}
		
		JSONArray net_relations = new JSONArray() ;
		ret.put("net_relations", net_relations) ;
		for(DevNetRelation nr:this.ieee2net_relat.values())
		{
			net_relations.put(nr.toJO()) ;
		}
		
		return ret ;
	}
	
	public void fromJO(JSONObject jo)
	{
//		this.pan_id = jo.optInt("pan_id",-1) ;
//		this.ext_pan_id = jo.optString("ext_pan_id") ;
//		this.nwkKey = jo.optString("nwk_key") ;
		this.ieee_addr = jo.optString("ieee_addr") ;
		JSONObject tmpjo = jo.optJSONObject("version") ;
		if(tmpjo!=null)
		{
			VersionSRSP v = new VersionSRSP() ;
			if(v.fromRecvJO(tmpjo))
				this.version = v ;
		}
		
		tmpjo = jo.optJSONObject("nwk_addr_info") ;
		if(tmpjo!=null)
		{
			NwkAddrRspAREQ_CB v = new NwkAddrRspAREQ_CB() ;
			if(v.fromRecvJO(tmpjo))
				this.nwk_addr_inf = v ;
		}
		
		tmpjo = jo.optJSONObject("device_info") ;
		if(tmpjo!=null)
		{
			GetDeviceInfoSRSP v = new GetDeviceInfoSRSP() ;
			if(v.fromRecvJO(tmpjo))
				this.deviceInfo = v ;
		}
		
		JSONArray tmpjarr = jo.optJSONArray("end_devices") ;
		if(tmpjarr!=null)
		{
			int n = tmpjarr.length() ;
			for(int i = 0 ; i <  n ; i ++)
			{
				JSONObject oo = tmpjarr.getJSONObject(i) ;
				ZStackEndDevice ed = ZStackEndDevice.fromeJO(this, oo) ;
				if(ed==null)
					continue ;
				this.ieee2device.put(ed.getIEEEAddr(),ed) ;
			}
		}
		
		tmpjarr = jo.optJSONArray("net_relations") ;
		if(tmpjarr!=null)
		{
			int n = tmpjarr.length() ;
			for(int i = 0 ; i <  n ; i ++)
			{
				JSONObject oo = tmpjarr.getJSONObject(i) ;
				DevNetRelation nr = new DevNetRelation();
				if(nr.fromJO(oo))
					this.ieee2net_relat.put(nr.getIEEEAddr(),nr) ;
			}
		}
	}
	
	
	
	
//	public synchronized List<ZStackEndDevice> listEndDevicesZStack()
//	{
//		ArrayList<ZStackEndDevice> rets = new ArrayList<>() ;
//		rets.addAll(this.ieee2EndDevice.values()) ;
//		Collections.sort(rets);
//		return rets ;
//	}
	
	/**
	 * 被ZStackDriver中的线程调用，可以产生一些主任务的消费对象
	 * 
	 * 独立获得的回调消息
	 * @param m_recv
	 */
	void RT_onCoorIndCallback(MsgRecv m_recv) throws Exception
	{
		if(getLogger().isTraceEnabled())
			getLogger().trace("RT_onCoorIndCallback <<"+m_recv);
				
		if(m_recv instanceof EndDeviceAnnceIndSREQ_CB)
		{
			EndDeviceAnnceIndSREQ_CB cbmsg = (EndDeviceAnnceIndSREQ_CB)m_recv ;
			//System.out.println(">>annce ed>>"+cbmsg) ;
			ZSDevice ed = MGR_onDeviceAnnce(cbmsg) ;
			putQueue(QN_ANNCE_EP,ed) ;
			return ;
		}
		
		if(m_recv instanceof TCDevIndAREQ_CB)
		{
			TCDevIndAREQ_CB cbmsg = (TCDevIndAREQ_CB)m_recv ;
			MGR_onTCDevInd(cbmsg) ;
			return ;
		}
		
		if(m_recv instanceof IncomingMsgAREQ_CB)
		{// 在 TCDevIndAREQ_CB之后会出现
			//System.out.println("$IncomingMsgAREQ_CB$>>"+m_recv) ;
			IncomingMsgAREQ_CB income_msg = (IncomingMsgAREQ_CB)m_recv ;
			MGR_onIncomingMsg(income_msg) ;
			this.RT_fireEvent(ZSController.INCOME_MSG, income_msg);
			return ;
		}
		
		if(m_recv instanceof SrcRtgIndAREQ_CB)
		{// 
			SrcRtgIndAREQ_CB rtg = (SrcRtgIndAREQ_CB)m_recv ;
			int relay_count = rtg.getRelayCount() ;
			int dst_addr = rtg.getDstAddr().getAddrVal() ;
			List<ShortAddr> relay_list = rtg.getRelayList() ;
			return ;
		}
		
		if(m_recv instanceof ResetIndAREQ_CB)
		{
			ResetIndAREQ_CB resetind = (ResetIndAREQ_CB)m_recv ;
			if(getLogger().isWarnEnabled())
				getLogger().warn(" FOUND:"+resetind);
			zsm.RT_reset(false);
			return ;
		}
		
		if(m_recv instanceof DataConfirmAREQ_CB)
		{
			return ;
		}
		//System.out.println("$$>>"+m_recv) ;
		if(this.getLogger().isDebugEnabled())
			this.getLogger().debug("RT_onCoorIndCallback unknown <<"+m_recv);
	}
	
	boolean RT_ping(StringBuilder failedr) throws Exception
	{
		Ping_CMD rr = new Ping_CMD() ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return false ;
		PingSRSP rsp = rr.getPingRsp() ;
		if(rsp==null)
		{
			failedr.append("no ping rsp") ;
			return false ;
		}
		this.cap = rsp.getCapability() ;
		return true ;
	}
	
	boolean RT_init_before_start(StringBuilder failedr) throws Exception
	{
		String old_ieee = this.ieee_addr ;
		String cur_ieee = readCoorIEEEAddr(failedr) ;
		if(Convert.isNullOrEmpty(cur_ieee))
			return false;
		if(Convert.isNotNullEmpty(old_ieee))
		{
			if(!old_ieee.equals(cur_ieee))
			{
				// TODO 以后要考虑协调器被更换能够自动适配的情况
			}
		}
		this.ieee_addr = cur_ieee ;
		
		this.version = readVersion(failedr) ;
		if(this.version==null)
			return false;
		
		int pan_id = this.readPanId(failedr) ;
		if(pan_id<0)
			return false;
		String ext_pan_id = this.readExtPanId(failedr) ;
		if(Convert.isNullOrEmpty(this.getExtPanID()))
			return false ;
		String nwkKey = readNwkKey(failedr) ;
		if(Convert.isNullOrEmpty(this.getNwkKey()))
			return false;

		registerEndpoint(1,0x0104,5,failedr) ;
		registerEndpoint(3,0x0104,5,failedr) ; //genIdentify
		registerEndpoint(4,0x0104,5,failedr) ;
		registerEndpoint(5,0x0104,5,failedr) ;
		registerEndpoint(6,0x0104,5,failedr) ;//genOnOff
		registerEndpoint(7,0x0104,5,failedr) ;
		registerEndpoint(8,0x0104,5,failedr) ;
		registerEndpoint(9,0x0104,5,failedr) ;
		registerEndpoint(242,0x0104,5,failedr) ;
		
		return true ;
	}
	
	boolean RT_startup(StringBuilder failedr) throws Exception
	{
		GetDeviceInfoSRSP dev_inf = readDeviceInfo(failedr) ;
		if(dev_inf==null)
			return false;

		DeviceState dev_st = dev_inf.getDeviceState() ;
		if(dev_st!=null && dev_st.isZigbeeCoorStarted())
		{
			this.getLogger().debug("adapter is already running in coordinator mode");
			return true ;
		}
		
		Startup_CMD rr = new Startup_CMD() ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return false ;
		this.startupST = rr.getStartupStatus() ;
		this.deviceST = rr.getDeviceState() ;
		if(this.startupST==null || this.deviceST==null)
		{
			failedr.append("no start rsp or status return or device state") ;
			return false ;
		}
		if(!this.deviceST.isZigbeeCoorStarted())
		{
			failedr.append("no started as ZigBee Coordinator") ;
			return false;
		}
		return true ;
	}
	
	boolean RT_init_after_start(StringBuilder failedr) throws Exception
	{
		nwk_addr_inf = readNwkAddrRsp(this.ieee_addr,false,0,failedr) ;
		if(nwk_addr_inf==null)
			return false;
		//System.out.println("nwk_cb="+nwk_cb) ;
		
		List<Integer> ch_list = readChannelList(failedr) ;
		if(ch_list==null)
			return false;
		//System.out.println(" channels="+Convert.combineWith(ch_list, ',')) ;
		
		deviceInfo = readDeviceInfo(failedr) ;
		if(deviceInfo==null)
			return false ;
		
		List<Integer> active_eps = readDevActiveEPs(0x0000,failedr); 
		System.out.println(" active_eps="+Convert.combineWith(active_eps, ',')) ;
		return true;
	}
	
	
	/**
	 * init 
	 */
	public boolean RT_init(StringBuilder failedr) throws Exception
	{
		String ieee_addr = readCoorIEEEAddr(failedr) ;
		if(Convert.isNullOrEmpty(ieee_addr))
			return false;
		System.out.println("ieee_addr="+ieee_addr) ;
		
		int pan_id = readPanId(failedr) ;
		if(pan_id<0)
			return false;
		System.out.println("pan_id="+pan_id) ;
		
		List<Integer> ch_list = readChannelList(failedr) ;
		if(ch_list==null)
			return false;
		System.out.println(" channels="+Convert.combineWith(ch_list, ',')) ;
		
//		NwkAddrRspAREQ_CB nwk_addr_rsp = readNwkAddrRsp(ieee_addr,false,0,failedr) ;
//		if(nwk_addr_rsp==null)
//			return false ;
//		System.out.println(" nwk_addr_rsp="+nwk_addr_rsp) ;
		read_NodeInf(ieee_addr,failedr) ;
		
		ieee_addr = "00124b0030db7dd6" ;
		read_NodeInf(ieee_addr,failedr) ;
		/*
		SimpleDescRspAREQ_CB sdr = readSimpleDesc(0x0000,1,failedr) ;
		System.out.println(" ep 1="+sdr) ;
		sdr = readSimpleDesc(0x0000,2,failedr) ;
		System.out.println(" ep 2="+sdr) ;
		sdr = readSimpleDesc(0x0000,242,failedr) ;
		System.out.println(" ep 242="+sdr) ;
		*/
//		registerEndpoint(1,0x0104,5,failedr) ;
//		registerEndpoint(3,0x0104,5,failedr) ;
//		registerEndpoint(4,0x0104,5,failedr) ;
//		registerEndpoint(5,0x0104,5,failedr) ;
//		registerEndpoint(6,0x0104,5,failedr) ;
		
		//dst_ep == src_ep,并且本机先得注册  //,4,5,6
		pan_id = 0x0000;
		int node_short_addr = 0xFBA8;
		int end_point = 1 ;
		int cluster_id = 0x0000 ;
		List<Integer> attr_ids = Arrays.asList(3,4,5);
		IncomingMsgAREQ_CB inc_msg = readReadAttr(node_short_addr,end_point,cluster_id,
				attr_ids,failedr) ;
		if(inc_msg==null)
			System.out.println("     > IncomingMsgAREQ_CB err="+failedr);
		else
			System.out.println("     > IncomingMsgAREQ_CB="+inc_msg) ;
		
		return true ;
	}
	
	
	//
	
	
	
	public boolean read_NodeInf(String ieee_addr,StringBuilder failedr) throws Exception
	{
		System.out.println(" --------node info   -------"+ieee_addr) ;
		NwkAddrRspAREQ_CB nwk_addr_rsp = readNwkAddrRsp(ieee_addr,true,0,failedr) ;
		if(nwk_addr_rsp==null)
			return false ;
		System.out.println(ieee_addr+">>> nwk_addr_rsp="+nwk_addr_rsp) ;
		ShortAddr nwk_addr = nwk_addr_rsp.getNwkAddr() ;
		
		NodeDescRspAREQ_CB node_desc = readNodeDesc(nwk_addr.getAddrVal(),failedr) ;
		if(node_desc==null)
			System.out.println("     > node_desc err="+failedr);
		else
			System.out.println("     > node_desc="+node_desc) ;
		
		PowerDescRspAREQ_CB pow_desc = readPowerDesc(nwk_addr.getAddrVal(),failedr) ;
		if(pow_desc==null)
			System.out.println("     > pow_desc err="+failedr);
		else
			System.out.println("     > pow_desc="+pow_desc) ;
				
		List<Integer> eps = readDevActiveEPs(nwk_addr.getAddrVal(),failedr) ; // 0x0000 local coor
		if(eps==null)
			System.out.println(failedr) ;
		System.out.println(" eps="+Convert.combineWith(eps, ',')) ;
		
		if(eps!=null&& eps.size()>0)
		{// read end device id,in out cluster
			for(int ep:eps)
			{
				SimpleDescRspAREQ_CB sim_desc = readSimpleDesc(nwk_addr.getAddrVal(),ep,failedr) ;
				if(sim_desc==null)
					System.out.println("     # simple_desc err="+failedr);
				else
					System.out.println("     # simple_desc="+sim_desc) ;
			}
		}
		
		return true;
	}
	
	public String readCoorIEEEAddr(StringBuilder failedr) throws Exception
	{
		ReadNV_IEEEAddr rr = new ReadNV_IEEEAddr() ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		return rr.getIEEEAddr() ;
	}
	
	public int readPanId(StringBuilder failedr) throws Exception
	{
		ReadNV_PANID rr = new ReadNV_PANID() ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return -1 ;
		return rr.getPanID() ;
	}
	
	public String readExtPanId(StringBuilder failedr) throws Exception
	{
		ReadNV_EXT_PAN_ID rr = new ReadNV_EXT_PAN_ID() ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		return rr.getExtPanID() ;
	}
	
	public String readNwkKey(StringBuilder failedr) throws Exception
	{
		ReadNVNwkKey rr = new ReadNVNwkKey() ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		return rr.getNetworkKey() ;
	}
	
	
	public List<Integer> readChannelList(StringBuilder failedr) throws Exception
	{
		ArrayList<Integer> rets = new ArrayList<>() ;
		ReadNV_Common rr = new ReadNV_Common(NVItemIDS.ZCD_NV_CHANLIST) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		byte[] bs = rr.getRespNVData() ;
		long v = DataUtil.getUInt32(bs, 0) ;
		for(int i = 1 ; i < 32 ; i ++)
		{
			if( (v & (1<<i))>0)
				rets.add(i) ;
		}
		return rets ;
	}
	
	public VersionSRSP readVersion(StringBuilder failedr) throws Exception
	{
		Version_CMD rr = new Version_CMD() ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		return rr.getVersionRsp();
	}
	
	public GetDeviceInfoSRSP readDeviceInfo(StringBuilder failedr) throws Exception
	{
		GetDeviceInfoSREQ req = new GetDeviceInfoSREQ() ;
		ReqRsp_CMD<GetDeviceInfoSREQ,GetDeviceInfoSRSP> rr = new ReqRsp_CMD<GetDeviceInfoSREQ,GetDeviceInfoSRSP>(req,GetDeviceInfoSRSP.class) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		
		return rr.getRsp();
	}
	
	public List<Integer> readDevActiveEPs(int dst_addr,StringBuilder failedr) throws Exception
	{
		ActiveEP_CMD rr = new ActiveEP_CMD(dst_addr) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		boolean b_status = rr.getStatus() ;
		if(!b_status)
		{
			failedr.append("ActiveEPSREQ status false") ;
			return null ;
		}
		ActiveEPRspAREQ_CB rsp = rr.getActiveEPRsp() ;
		if(rsp==null)
			return null ;
		return rsp.getActiveEPList() ;
	}
	
	public NwkAddrRspAREQ_CB readNwkAddrRsp(String ieee_addr,boolean que_assoc_dev,int start_idx,StringBuilder failedr)
		throws Exception
	{
		NwkAddr_CMD rr = new NwkAddr_CMD(ieee_addr,que_assoc_dev,start_idx) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		boolean b_status = rr.getStatus() ;
		if(!b_status)
		{
			failedr.append("NwkAddrSREQ status false") ;
			return null ;
		}
		return rr.getNwkAddrRsp();
	}
	
	public IEEEAddrRspAREQ_CB readIEEEAddrByShortAddr(int short_addr,StringBuilder failedr) throws Exception
	{
		IEEEAddrSREQ req = new IEEEAddrSREQ() ;
		ReqRspFlw_CMD<IEEEAddrSREQ,IEEEAddrSRSP,IEEEAddrRspAREQ_CB> rr =
				new ReqRspFlw_CMD<>(req,IEEEAddrSRSP.class,IEEEAddrRspAREQ_CB.class) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		return rr.getFlw();
	}
	/**
	 * 获得设备节点本身信息，如厂家信息等
	 * @param dst_addr
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public NodeDescRspAREQ_CB readNodeDesc(int dst_addr,StringBuilder failedr) throws Exception
	{
		NodeDesc_CMD rr = new NodeDesc_CMD(dst_addr,dst_addr) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		boolean b_status = rr.getStatus() ;
		if(!b_status)
		{
			failedr.append("NodeDescSREQ status false") ;
			return null ;
		}
		return rr.getNodeDescRsp();
	}
	
	/**
	 * 获得节点电源信息
	 * @param dst_addr
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public PowerDescRspAREQ_CB readPowerDesc(int dst_addr,StringBuilder failedr) throws Exception
	{
		PowerDesc_CMD rr = new PowerDesc_CMD(dst_addr,dst_addr) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		boolean b_status = rr.getStatus() ;
		if(!b_status)
		{
			failedr.append("NodeDescSREQ status false") ;
			return null ;
		}
		return rr.getPowerDescRsp();
	}
	
	/**
	 * 获得设备下面某个endpoint的具体信息
	 * @param dst_addr
	 * @param endpoint
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public SimpleDescRspAREQ_CB readSimpleDesc(int dst_addr,int endpoint,StringBuilder failedr) throws Exception
	{
		SimpleDesc_CMD rr = new SimpleDesc_CMD(dst_addr,dst_addr,endpoint) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		boolean b_status = rr.getStatus() ;
		if(!b_status)
		{
			failedr.append("SimpleDescSREQ status false") ;
			return null ;
		}
		return rr.getSimpleDescRsp();
	}
	
	public boolean registerEndpoint(int endpoint,int profile_id,int device_id,StringBuilder failedr) throws Exception
	{
		RegEndpoint_CMD rr = new RegEndpoint_CMD(endpoint,profile_id,device_id) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return false ;
		return rr.getStatus() ;
	}
	
	public IncomingMsgAREQ_CB readReadAttr(int dst_addr,int dst_ep,int cluster_id,
			List<Integer> attr_ids,StringBuilder failedr)
		throws Exception
	{
		ReadAttr read_attr = new ReadAttr(attr_ids) ;
		DataReq_CMD rr = new DataReq_CMD(zsm,dst_addr,dst_ep,dst_ep,cluster_id,read_attr) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		boolean b_status = rr.getStatus() ;
		if(!b_status)
		{
			failedr.append("DataRequestExtSREQ status false") ;
			return null ;
		}
		return rr.getIncomingMsg();
	}
	
	public IncomingMsgAREQ_CB readReadAttr(String dst_ieeeaddr,int dst_ep,int dst_pan_id,int src_ep,int cluster_id,
			List<Integer> attr_ids,StringBuilder failedr)
		throws Exception
	{
		ReadAttr read_attr = new ReadAttr(attr_ids) ;
		DataReqExt_CMD rr = new DataReqExt_CMD(zsm,dst_ieeeaddr,dst_ep,dst_pan_id,src_ep,cluster_id,read_attr) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		boolean b_status = rr.getStatus() ;
		if(!b_status)
		{
			failedr.append("DataRequestExtSREQ status false") ;
			return null ;
		}
		return rr.getIncomingMsg();
	}
	
	public boolean writeWriteAttr()
	{
		//WriteAttr read_attr = new WriteAttr(attr_ids) ;
		return true;
	}
	
	/**
	 * 读取end device的厂家，型号，供电方式等信息
	 * @return attr_id2strval
	 */
	public HashMap<Integer,DataVal> readEndDevBasic(int short_addr,StringBuilder failedr) throws Exception
	{
		
		//int pan_id = 0x0000;
		int end_point = 1 ;
		int cluster_id = 0x0000 ;
		List<Integer> attr_ids = Arrays.asList(4,5,7); //4-manufacturerName 5=modelId 7-powerSource
		
		IncomingMsgAREQ_CB inc_msg = readReadAttr(short_addr,end_point,cluster_id,
				attr_ids,failedr) ;
		if(inc_msg==null)
			return null ;
		ZCLFrame zclf = inc_msg.getZCLFrame(failedr) ;
		if(zclf==null || !(zclf instanceof ReadAttrRsp))
			return null ;
		ReadAttrRsp attr_rsp = (ReadAttrRsp)zclf ;
		return attr_rsp.getReadAttrId2DataVal() ;
	}
	
	
	/**
	 * 发送cluster 指令到end device
	 * @param dst_addr
	 * @param endpoint
	 * @param cluster_id
	 * @param cmd_id
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public boolean sendClusterCmdToEndDevice(int dst_addr,int endpoint,int cluster_id,int cmd_id,StringBuilder failedr)
			throws Exception
	{
		return sendClusterCmdToEndDevice(dst_addr,endpoint,cluster_id,cmd_id,null,failedr) ;
	}
	
	public boolean sendClusterCmdToEndDevice(int dst_addr,int endpoint,int cluster_id,int cmd_id,Object[] pm_vals,StringBuilder failedr)
		throws Exception
	{
		DataReqConfirm_CMD rr = DataReqConfirm_CMD.createClusterCmdReqRsp(this.zsm, dst_addr,
				endpoint, cluster_id, cmd_id,pm_vals,false); //dir_s2c=false  client -> server
		System.out.println("cmd req="+Convert.byteArray2HexStr(rr.getSReqMsg().pack(),"-")) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return false ;
		return rr.getConfirmStatus();
	}
	
	/**
	 * 对cluster 下面的attribute (report=true),设置report时间参数和最小变化值参数
	 * 
	 * @param dst_addr
	 * @param endpoint
	 * @param cluster_id
	 * @param attr_reports
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public boolean sendConfigReportToEndDevice(int dst_addr,int endpoint,int cluster_id,List<AttrReportConfig> attr_reports,StringBuilder failedr)
			throws Exception
	{
		DataReqConfirm_CMD rr = DataReqConfirm_CMD.createConfigAttrReport(this.zsm,dst_addr,endpoint,cluster_id,false,
				attr_reports); //dir_s2c=false  client -> server
		System.out.println("cmd req="+Convert.byteArray2HexStr(rr.getSReqMsg().pack(),"-")) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return false ;
		return rr.getConfirmStatus();
	}
	
	protected int RT_interviewEndDev(ZSDevice ed,StringBuilder failedr) throws Exception
	{
		boolean b_dirty = false;
		String ieee = ed.getIEEEAddr() ;
		int dst_addr = ed.getShortAddr().getAddrVal() ;
		//check dst addr be changed or not
		NwkAddrRspAREQ_CB nwkaddr_rsp = this.readNwkAddrRsp(ieee,false,0, failedr) ;
		if(nwkaddr_rsp!=null && nwkaddr_rsp.getStatus())
		{
			int rsp_addr = nwkaddr_rsp.getNwkAddr().getAddrVal() ;
			if(dst_addr!=rsp_addr)
			{
				this.MGR_onEndDeviceNwkAddr(ieee, nwkaddr_rsp) ;
			}
		}
		
		if(Convert.isNullOrEmpty(ed.getFacName()))
		{
			HashMap<Integer,DataVal> attr2dv = readEndDevBasic(dst_addr,failedr);
			if(attr2dv==null)
			{
				failedr.append(" @ readEndDevBasic") ;
				if(this.getLogger().isWarnEnabled())
					this.getLogger().warn("RT_interviewEndDev "+ieee+" err:"+failedr.toString());
				//return -1;
			}
			else
			{
				int up_n =ed.MGR_updateByClusterBasic(attr2dv);
				if(up_n>0)
					b_dirty = true;
			}
		}
		
		if(ed.getFacCode()<0)
		{//get fac code
			NodeDescRspAREQ_CB dev_nd = readNodeDesc(dst_addr,failedr) ;
			if(dev_nd==null)
			{
				failedr.append(" @ readNodeDesc") ;
				if(this.getLogger().isWarnEnabled())
					this.getLogger().warn("RT_interviewEndDev "+ieee+" err:"+failedr.toString());
				//return -1 ;
			}
			else
			{
				boolean bup =ed.updateFacCode(dev_nd.getManufacturerCode());
				if(bup)
					b_dirty = true ;
			}
		}
		
		List<ZSEndPoint> eps = ed.listEndPoints() ;
		if(eps==null|| eps.size()<=0)
		{
			List<Integer> active_eps = readDevActiveEPs(dst_addr,failedr);
			if(active_eps==null)
			{
				failedr.append(" @ readDevActiveEPs") ;
				if(this.getLogger().isWarnEnabled())
					this.getLogger().warn("RT_interviewEndDev "+ieee+" err:"+failedr.toString());
				//return -1 ;
			}
			else
			{
				if(ed.MGR_updateActiveEPs(active_eps))
					b_dirty = true ;
				
				for(Integer epid:active_eps)
				{
					ZSEndPoint ep = ed.getEndPoint(epid) ;
					SimpleDescRspAREQ_CB sdr = readSimpleDesc(dst_addr,epid, failedr) ;
					if(sdr==null)
						continue ;
					if(ep.MGR_updateBy(sdr))
						b_dirty = true ;
				}
			}
		}
		
		return b_dirty?1:0 ;
	}
	
	/**
	 * 启动允许新设备加入网络
	 * 
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public int MGR_setPermitJoin(boolean permit_or_not,StringBuilder failedr) throws Exception
	{
		PermitJoinCMD rr = new PermitJoinCMD(permit_or_not) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return -1 ;
		boolean b_status = rr.isRspStatusOk() ;
		if(!b_status)
		{
			failedr.append("MgmtPermitJoinSREQ status false") ;
			return -1 ;
		}
		int ret = rr.getReqPermitDuration() ;
		if(getLogger().isDebugEnabled())
			getLogger().debug("MgmtPermitJoinSREQ PermitDuration = "+ret);
		
		DataReqExtConfirm_CMD dr_rr = DataReqExtConfirm_CMD.createGreenPowerPermitOrNot(this.zsm,permit_or_not) ;
		System.out.println("GP>>"+Convert.byteArray2HexStr(dr_rr.getSReqMsg().pack(), "-")) ;
		//DataReqExtConfirm_CMD dr_rr = DataReqExtConfirm_CMD.createClusterGreenPowerProxy(this.zsm) ;
		if(!zsm.RT_synReqRsp(dr_rr,failedr))
		{
			if(getLogger().isWarnEnabled())
				getLogger().warn("GreenPower Proxy Mode:"+failedr);
			return ret ;
		}
		
		if(getLogger().isDebugEnabled())
			getLogger().debug("GreenPower Proxy Mode Confirm = "+dr_rr.getConfirmStatus());
		
		this.RT_fireEvent(ZSController.PERMIT_JOIN, new PermitJoinEvt(rr.getReqPermitDuration()));
		return ret ;
	}
	
	/**
	 * 设备网络结构的自动获取
	 * 
	 * @param tcdd
	 * @throws IOException 
	 */
	protected void MGR_onTCDevInd(TCDevIndAREQ_CB tcdd) throws IOException
	{
		String ieee = tcdd.getSrcIEEEAddr() ;
		int src_addr = tcdd.getSrcNwkAddr() ;
		int p_addr = tcdd.getParentNwkAddr() ;
		DevNetRelation nr = new DevNetRelation(ieee,src_addr,p_addr) ;
		DevNetRelation old_nr = this.ieee2net_relat.get(ieee) ;
		if(old_nr!=null && old_nr.equals(nr))
			return ;
		this.ieee2net_relat.put(ieee, nr);
		this.save();
	}
	
	/**
	 * 当新的EndDevice 发现
	 * @param endd
	 * @throws IOException 
	 */
	protected ZSDevice MGR_onDeviceAnnce(EndDeviceAnnceIndSREQ_CB endd) throws IOException
	{
		String ieee_addr = endd.getIEEEAddr() ;
		ZSDevice old_ed = (ZStackEndDevice)getDeviceByIEEEAddr(ieee_addr) ;
		ZSDevice new_ed = null;
		if(old_ed==null)
		{//new add
			new_ed = new ZSDevice(this,ieee_addr) ;
			new_ed.setFindDT(System.currentTimeMillis()) ;
			UADev related_d = new_ed.getRelatedUADev() ;
			if(related_d!=null)
				new_ed.setTitle(related_d.getTitle()) ;
		}
		else
		{//check change or not
			if(!endd.MGR_checkChanged(old_ed))
				return null;
			new_ed = old_ed ;
		}
		new_ed.updateBy(new ShortAddr(endd.getNwkAddr()) ,endd.getDeviceCapabilites());
		new_ed.setUpdateDT(System.currentTimeMillis()) ;
		setDevice(new_ed) ;

		this.save();
		return new_ed ;
	}
	
	protected  ZStackEndDevice MGR_onEndDeviceNwkAddr(String ieee_addr,NwkAddrRspAREQ_CB nwkaddr_rsp)
			throws IOException
	{
		ZStackEndDevice ed = (ZStackEndDevice)getDeviceByIEEEAddr(ieee_addr) ;
		if(ed==null)
			return null ;
		int rsp_addr = nwkaddr_rsp.getNwkAddr().getAddrVal() ;
		int dst_addr = ed.getShortAddr().getAddrVal() ;
		if(rsp_addr!=dst_addr)
		{
			ed.setShortAddr(new ShortAddr(rsp_addr)) ;
			this.save();
		}
		return ed ;
	}
	
	protected  void MGR_onIncomingMsg(IncomingMsgAREQ_CB in_msg) throws IOException
	{
		 int src_addr = in_msg.getSrcAddr();
		 ZStackEndDevice ed = (ZStackEndDevice)getDeviceByShortAddr(src_addr) ;
		 if(ed==null)
		 {
			 if(log.isWarnEnabled())
				 log.warn("no EndDevice found with src_addr"+src_addr+" in IncomeMsg");
			 return ;
		 }
		 
		 ed.setLinkQual(in_msg.getLinkQuality());
		 
		 int src_ep = in_msg.getSrcEndpoint() ;
		 int cluster_id = in_msg.getClusterId() ;
		 ZCLCluster c = in_msg.getCluster() ;
		 
		 StringBuilder failedr  = new StringBuilder() ;
		 ZCLFrame f = in_msg.getZCLFrame(failedr) ;
		 if(f==null)
		 {
			 //
			 System.err.println(failedr) ;
			 return ;
		 }
		 
		 if(f instanceof ReportAttr)
		 {
			 ReportAttr ra = (ReportAttr)f ;
			 boolean b_dirty = ed.RT_onReportAttr(src_ep,cluster_id,ra,failedr) ;
			 if(b_dirty)
				 this.save();
			 else
			 {
				 if(log.isWarnEnabled())
					 log.warn("MGR_onIncomingMsg RT_onReportAttr err:"+failedr.toString()+"\r\n\t"+in_msg);
			 }
		 }
	}
	
	@Override
	public boolean MGR_setEndDeviceTitle(String ieeeaddr,String tt) throws Exception
	{
		ZStackEndDevice ed = (ZStackEndDevice)this.getDeviceByIEEEAddr(ieeeaddr) ;
		if(ed==null)
			return false;
		ed.MGR_setTitle(tt);
		this.save();
		StringBuilder failedr = new StringBuilder() ;
		if(!this.MGR_synEndDeviceToUADev(ieeeaddr, failedr))
		{
			if(log.isDebugEnabled())
				log.debug("MGR_setEndDeviceTitle - MGR_synEndDeviceToUADev err:"+failedr);
		}
		
		return true ;
	}
	
	/**
	 * 
	 * @param ieeeaddr
	 * @param endpoint
	 * @param cluster_id
	 * @param attr_rcs
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean MGR_setEndDeviceConfigReportAttr(String ieeeaddr,int endpoint,int cluster_id,
			List<AttrReportConfig> attr_rcs,StringBuilder failedr) throws Exception
	{
		ZStackEndDevice ed = (ZStackEndDevice)this.getDeviceByIEEEAddr(ieeeaddr) ;
		if(ed==null)
		{
			failedr.append("no end device found") ;
			return false;
		}
		return sendConfigReportToEndDevice(ed.getShortAddr().getAddrVal(),endpoint, cluster_id,
				attr_rcs,failedr) ;
	}
	/**
	 * syn UADev (include sub tags) under coor channel
	 * @param ieeeaddr
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean MGR_synEndDeviceToUADev(String ieeeaddr,StringBuilder failedr) throws Exception
	{
		ZStackEndDevice ed = (ZStackEndDevice)this.getDeviceByIEEEAddr(ieeeaddr) ;
		if(ed==null)
		{
			failedr.append("no EndDevice found") ;
			return false;
		}
		UACh ch = this.zsm.getBelongToCh() ;
		String tt = ed.getTitle() ;
		if(Convert.isNullOrEmpty(tt))
			tt = ed.getIEEEAddr();
		JSONArray tags_jarr = new JSONArray() ;
		ed.setupTagsJArr(tags_jarr) ;
		ch.synOuterDevByJO(ieeeaddr, "ed_", tt, tags_jarr, true);
		return true ;
	}
	
	/**
	 * 触发EndDevice新发现运行过程
	 * @param ieee
	 * @param failedr
	 * @return
	 */
	@Override
	public boolean MGR_putQueueEndDevice(String ieee,StringBuilder failedr)
	{
		ZStackEndDevice ed = (ZStackEndDevice)this.getDeviceByIEEEAddr(ieee) ;
		if(ed==null)
		{
			failedr.append("no end device found") ;
			return false;
		}
		putQueue(QN_ANNCE_EP,ed) ;
		return true ;
	}
	
	@Override
	public int MGR_delEndDevice(String ieee,boolean force_del,boolean rejoin,boolean del_uadev,StringBuilder failedr) throws Exception
	{
		ZStackEndDevice ed = (ZStackEndDevice)this.getDeviceByIEEEAddr(ieee) ;
		if(ed==null)
		{
			failedr.append("no end device found") ;
			return -1;
		}
		int dst_addr = ed.getShortAddr().getAddrVal() ;
		MgmtLeaveSREQ req = new MgmtLeaveSREQ(dst_addr,ieee,rejoin?MgmtLeaveSREQ.RM_REJOIN:0); //) ;
//		ReqRspFlw2_CMD<MgmtLeaveSREQ,MgmtLeaveSRSP,MgmtLeaveRspAREQ_CB,LeaveIndAREQ> rr = new ReqRspFlw2_CMD<>(
//				req,MgmtLeaveSRSP.class,MgmtLeaveRspAREQ_CB.class,LeaveIndAREQ.class) ;
		ReqRspFlw_CMD<MgmtLeaveSREQ,MgmtLeaveSRSP,MgmtLeaveRspAREQ_CB> rr = new ReqRspFlw_CMD<>(
				req,MgmtLeaveSRSP.class,MgmtLeaveRspAREQ_CB.class) ;
		rr.setDebugOut(true);
		
		if(!zsm.RT_synReqRsp(rr,failedr))
		{
			failedr.append(" @ MGR_delEndDevice");
			if(log.isWarnEnabled())
				log.warn(failedr.toString());
			
			if(force_del)
			{
				delDevice(ieee,del_uadev);
				failedr.append(",but force deleted") ;
				return 0 ;
			}
			return -1 ;
		}
		
		delDevice(ieee,del_uadev);
		
		//return rr.getFlw2();
		return 1; //rr.getFlw();
	}

	@Override
	public ZSStartResult start()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCoordinatorIEEE() throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		GetDeviceInfoSRSP dev_inf = readDeviceInfo(failedr) ;
		if(dev_inf==null)
			return null ;
		return dev_inf.getIEEEAddr();
	}

	@Override
	public String getCoordinatorVersion()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset(boolean hard_or_soft)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public NetworkParam getNetworkParameters() throws Exception
	{
		 StringBuilder failedr = new StringBuilder() ;
		ExtNwkInfoSREQ req = new ExtNwkInfoSREQ() ;
		ReqRsp_CMD<ExtNwkInfoSREQ,ExtNwkInfoSRSP> rr = new ReqRsp_CMD<>(req,ExtNwkInfoSRSP.class) ;
		if(!zsm.RT_synReqRsp(rr,failedr))
			return null ;
		
		ExtNwkInfoSRSP rsp = rr.getRsp();;
		if(rsp==null)
			return null ;
		return rsp.toNwkParam() ;
	}

	@Override
	public void addInstallCode(String ieee_addr, String key, boolean hashed) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ZDOResp sendZdo(String ieee_address, int nwk_addr, ZDOCluster clusterobj, boolean disableResponse)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void permitJoin(int seconds, int nwk_addr) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		MGR_setPermitJoin(seconds>0,failedr) ;
	}

	@Override
	public ZCLFrame sendZclFrameToEndpoint(String ieeeAddr, int networkAddress, int endpoint, ZCLFrmClusterCmd zclFrame,
			int timeout, boolean disableResponse, boolean disableRecovery, Integer sourceEndpoint, Integer profileId)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendZclFrameToGroup(int groupID, ZCLFrmClusterCmd zclf, Integer sourceEndpoint, Integer profileId)
			throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendZclFrameToAll(int endpoint, ZCLFrmClusterCmd zclf, Integer src_endpoint, BroadcastAddr destination,
			Integer profileId) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChannelInterPAN(int channel) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendZclFrameInterPANToIeeeAddr(ZCLFrmClusterCmd zclFrame, String ieee_addr) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ZCLFrame sendZclFrameInterPANBroadcast(ZCLFrmClusterCmd zclFrame, int timeout) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ZCLFrame sendZclFrameInterPANBroadcast(ZCLFrmClusterCmd zclFrame, int timeout, boolean disableResponse)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void restoreChannelInterPAN() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	
}
