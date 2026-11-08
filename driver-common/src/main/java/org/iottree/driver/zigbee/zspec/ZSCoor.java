package org.iottree.driver.zigbee.zspec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.models.M_Coor;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.iottree.driver.zigbee.zcl.cmd_frm.ReportAttr;
import org.iottree.driver.zigbee.zcl.util.AttrReportConfig;
import org.iottree.driver.zigbee.zspec.event.DeviceJoinedPld;
import org.iottree.driver.zigbee.zspec.event.IncomeMsgDefEvt;
import org.iottree.driver.zigbee.zspec.types.BroadcastAddr;
import org.iottree.driver.zigbee.zspec.types.InterviewState;
import org.iottree.driver.zigbee.zspec.types.NetworkParam;
import org.iottree.driver.zigbee.zspec.types.ZSDeviceType;
import org.iottree.driver.zigbee.zspec.types.ZSStartResult;
import org.iottree.driver.zigbee.zspec.util.Capabilities;
import org.iottree.driver.zigbee.zspec.util.DeviceState;
import org.iottree.driver.zigbee.zspec.util.EndDeviceCap;
import org.iottree.driver.zigbee.zspec.util.PowerSrc;
import org.iottree.driver.zigbee.zspec.util.RunRes;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zspec.util.StartupStatus;
import org.iottree.driver.zigbee.zspec.zdo.EndDeviceAnnounceResp;
import org.iottree.driver.zigbee.zstack.msg_sys.VersionSRSP;
import org.iottree.driver.zigbee.zstack.msg_util.GetDeviceInfoSRSP;
import org.iottree.driver.zigbee.zstack.msg_zdo.NwkAddrRspAREQ_CB;
import org.json.JSONArray;
//import org.iottree.driver.zigbee.zstack.ZStackEndDevice;
//import org.iottree.driver.zigbee.zstack.msg_af.IncomingMsgAREQ_CB;
//import org.iottree.driver.zigbee.zstack.msg_zdo.EndDeviceAnnceIndSREQ_CB;
//import org.iottree.driver.zigbee.zstack.msg_zdo.NwkAddrRspAREQ_CB;
//import org.iottree.driver.zigbee.zstack.msg_zdo.TCDevIndAREQ_CB;
import org.json.JSONObject;

public abstract class ZSCoor
{
	private transient boolean bNewCoor = false;

	protected File storeDirBase = null;

	private ZSDriver zsDrv;

	protected Capabilities cap = null;

	protected StartupStatus startupST = null;

	protected DeviceState deviceST = null; // make share 0x09:Started as ZigBee

	// Coordinator
	protected String ieee_addr = null;

//	protected int pan_id = -1;
//
//	protected String ext_pan_id = null;
//
//	protected String nwkKey = null;
//
//	protected List<Integer> ch_list = null;
	protected ZSSettings settings ;

	protected LinkedHashMap<String, ZSDevice> ieee2device = new LinkedHashMap<>();

	protected HashMap<String, DevNetRelation> ieee2net_relat = new HashMap<>();
	
	private ZSController controller = null ;
	
	private ILogger log ;

	public ZSCoor(ZSDriver drv)
	{
		this.zsDrv = drv;

		try
		{
			loadFromStore();
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
		}
		this.settings = ZSSettings.acquireSettings(this) ;
		this.controller = new ZSController(this,this.settings) ;
	}

	public ZSDriver getDriver()
	{
		return this.zsDrv;
	}
	
	protected ILogger getLog()
	{
		log = LoggerManager.getLogger("Coor_"+this.zsDrv.getBelongToCh().getName()) ;
		log.setCurrentLogLevel(ILogger.LOG_LEVEL_DEBUG);
		return log ;
	}
	
	public final ZSSettings getSettings()
	{
		return this.settings ;
	}
	

	public final ZSController getController()
	{
		return this.controller ;
	}
	
	public abstract String getCoorName();

	public Capabilities getCapabilities()
	{
		return this.cap;
	}

	public int getPanIDSetting()
	{
		return this.settings.getPanId();
	}
	
	protected void setPanIDSetting(int panid) throws IOException
	{
		this.settings.setPanId(panid);
	}

	public String getExtPanID()
	{
		return this.settings.getExtPanId();
	}

	public String getNwkKey()
	{
		return this.settings.getNetworkKey();
	}
	
	public int getRadioChannel()
	{
		return this.settings.getChannel() ;
	}

	public String getIEEEAddr()
	{
		return this.ieee_addr;
	}
	
	

	public abstract boolean RT_init(StringBuilder failedr) throws Exception;

	/**
	 * 根据EndDevice运行情况，调整Coor reset 或 restart
	 * 
	 * @return
	 */
	private RunRes RT_checkEndDeviceLoop()
	{
		int syn_ed_num = 0;
		int lost_num = 0;
		for (ZSDevice ed : this.listDevices())
		{
			if (!ed.hasTagDefAttrs())
				continue;

			syn_ed_num++;
			boolean b_lost = ed.getNoRespLost();
			if (b_lost)
				lost_num++;
		}
		if (syn_ed_num <= 0)
			return RunRes.ok;
		if (lost_num == syn_ed_num)
			return RunRes.reset_disconn;
		return RunRes.ok;
	}

	public boolean RT_interview(StringBuilder failedr) throws Exception
	{
		// coor interview
		for (ZSDevice ed : this.listDevices())
		{// end point interview
			ed.RT_init_tag_default_vals();
		}

		boolean b_dirty = false;

		for (ZSDevice ed : this.listDevices())
		{// end point interview
			int res = RT_interviewEndDev(ed, failedr);
			if (res < 0)
			{
				if (getLog().isWarnEnabled())
					getLog().warn("RT_interviewEndDev " + ed.getIEEEAddr() + " err:" + failedr.toString());
				continue;
			}
			if (res > 0)
				b_dirty = true;
		}

		if (this.bNewCoor || b_dirty)
			this.save();
		return true;
	}
	
	/**
	 * 用来做备份
	 * @return
	 */
	public M_Coor toModelCoor()
	{
		M_Coor ret = new M_Coor() ;
		
		return ret ;
	}
	
	protected void saveBackup() throws IOException
	{
		M_Coor mc = toModelCoor() ;
		File coor_f = calCoorFileBackup() ;
		JSONObject jo = mc.toJO();
		Convert.writeFileJO(coor_f, jo);
	}
	
	protected M_Coor loadBackup() throws IOException
	{
		File coor_f = calCoorFileBackup() ;
		JSONObject jo = Convert.readFileJO(coor_f) ;
		if(jo==null)
			return null ;
		return M_Coor.fromJO(jo);
	}
	

	protected void save() throws IOException
	{
		File coor_f = this.calCoorFile();
		JSONObject jo = this.toJO();
		Convert.writeFileJO(coor_f, jo);
	}

	private File calCoorFile()
	{
		File dir = this.getStoreDirBase();
		return new File(dir, this.getCoorName() + "_coor.json");
	}
	
	private File calCoorFileBackup()
	{
		File dir = this.getStoreDirBase();
		return new File(dir, this.getCoorName() + "_coor.backup.json");
	}

	public File getStoreDirBase()
	{
		if (this.storeDirBase != null)
			return this.storeDirBase;

		UACh ch = this.getDriver().getBelongToCh();
		UAPrj prj = ch.getBelongToPrj();
		File prjdir = UAManager.getPrjFileSubDir(prj.getId());
		File ch_dir = new File(prjdir, ch.getName() + "/");
		if (!ch_dir.exists())
			ch_dir.mkdirs();
		return this.storeDirBase = ch_dir;
	}

	// private File calEndDevicesFile()
	// {
	// File dir = this.zsm.getStoreDirBase() ;
	// return new File(dir,"zstack_end_devs.json") ;
	// }

	void loadFromStore() throws IOException
	{
		File coor_f = this.calCoorFile();
		if (!coor_f.exists())
		{
			bNewCoor = true;
			return;
		}
		JSONObject jo = Convert.readFileJO(coor_f);
		if (jo == null)
		{
			bNewCoor = true;
			return;
		}

		this.fromJO(jo);
	}

	public synchronized List<ZSDevice> listDevices()
	{
		ArrayList<ZSDevice> rets = new ArrayList<>();
		rets.addAll(this.ieee2device.values());
		Collections.sort(rets);
		return rets;
	}

	public ZSDevice getDeviceByIEEEAddr(String ieee_addr)
	{
		return this.ieee2device.get(ieee_addr.toUpperCase());
	}

	public ZSDevice getDeviceByShortAddr(int addr)
	{
		for (ZSDevice ed : this.ieee2device.values())
		{
			if (addr == ed.getShortAddr().getAddrVal())
				return ed;
		}
		return null;
	}

	public synchronized void setDevice(ZSDevice ed)
	{
		this.ieee2device.put(ed.ieee_addr, ed);
	}

	public LinkedHashMap<String, ZSDevice> getDevicesMap()
	{
		return this.ieee2device;
	}

	protected ZSDevice delDevice(String ieee_addr, boolean del_uadev) throws Exception
	{
		ZSDevice ed = null;

		synchronized (this)
		{
			ed = this.ieee2device.remove(ieee_addr);
		}

		if (ed == null)
			return null;

		this.save();

		if (del_uadev)
		{
			UADev d = ed.getRelatedUADev();
			if (d != null)
			{
				d.delFromParent();
			}
		}
		return ed;
	}
	
	/**
	 * 创建新的设备
	 * @param ieee_addr
	 * @param dtp
	 * @param network_addr
	 * @param fac_code
	 * @param fac_name
	 * @param power_src
	 * @param fac_model
	 * @param intv_st
	 * @param gp_security_key
	 * @return
	 * @throws Exception
	 */
	public ZSDevice createNew(String ieee_addr,
	        ZSDeviceType dtp,int network_addr, int fac_code,String fac_name,
	        PowerSrc power_src,String fac_model,InterviewState intv_st,
	        String gp_security_key) throws Exception
	{
	        if (this.getDeviceByIEEEAddr(ieee_addr)!=null)
	            throw new Exception("Device with IEEE address "+ieee_addr+" already exists");

	        ZSDevice device = new ZSDevice(this,ieee_addr) ;
	        device.asBasic(dtp, ieee_addr, network_addr, power_src,
	    			fac_code, fac_name,fac_model,intv_st,gp_security_key) ;
	        
	        this.ieee2device.put(ieee_addr, device);
	        this.save();
	        return device;
	    }


	private static class QueItem
	{
		String name;
		Object obj;

		QueItem(String n, Object o)
		{
			this.name = n;
			this.obj = o;
		}
	}

	protected static final String QN_ANNCE_EP = "annce_ep";

	private LinkedList<QueItem> queue = new LinkedList<>();

	protected synchronized void putQueue(String name, Object o)
	{
		this.queue.addLast(new QueItem(name, o));
	}

	protected synchronized QueItem getQueue()
	{
		if (this.queue.isEmpty())
			return null;
		return this.queue.removeFirst();
	}

	// call by ZStackManager running in state=RT_ST_NORMAL_RUNNING
	public RunRes RT_onCoorNorRunning() throws Exception
	{
		RT_runEndDeviceLoop();

		RunRes rres = RT_checkEndDeviceLoop();
		if (rres == RunRes.reset)
			return rres;

		QueItem qi = this.getQueue();
		if (qi == null)
			return RunRes.ok;

		switch (qi.name)
		{
		case QN_ANNCE_EP:
			ZSDevice new_ed = (ZSDevice) qi.obj;
			if (new_ed == null)
				return RunRes.ok;
			StringBuilder failedr = new StringBuilder();
			int res = this.RT_interviewEndDev(new_ed, failedr);
			if (res < 0)
			{
				if (this.getLog().isWarnEnabled())
					this.getLog().warn("RT_interviewEndDev err:" + failedr);
			}
			else if (res > 0)
			{
				this.MGR_synEndDeviceToUADev(new_ed.getIEEEAddr(), failedr);
				this.save();
			}

			return RunRes.ok;
		}

		return RunRes.ok;
	}

	private int RT_runEndDeviceLoop()
	{
		for (ZSDevice ed : this.listDevices())
		{
			ed.RT_runInLoop();
		}
		return 1;
	}

	/**
	 * 单个End Device数据同步 1，启动时调用 2，新设备加入时
	 * 
	 * @param ed
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	protected abstract int RT_interviewEndDev(ZSDevice ed, StringBuilder failedr) throws Exception;

	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.putOpt("ieee_addr", this.ieee_addr) ; 
			//.putOpt("pan_id", this.getPanIDSetting()).putOpt("ext_pan_id", this.getExtPanID()).putOpt("nwk_key", this.getNwkKey())
		
		if(cap!=null)
		{
			ret.put("capabilities_v", this.cap.getCapVal()) ;
			ret.put("capabilities", this.cap.toJO()) ;
		}
		
		
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
		this.ieee_addr = jo.optString("ieee_addr","").toUpperCase() ;
		
		
		JSONArray tmpjarr = jo.optJSONArray("end_devices") ;
		if(tmpjarr!=null)
		{
			int n = tmpjarr.length() ;
			for(int i = 0 ; i <  n ; i ++)
			{
				JSONObject oo = tmpjarr.getJSONObject(i) ;
				ZSDevice ed = ZSDevice.fromeJO(this, oo) ;
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

	//call by sub class
	public final void RT_fireEvent(String evt_n,ZSController.IEvent evt_v)
	{
		this.controller.RT_onEventCB(evt_n, evt_v);
	}
	
	/**
	 * 启动允许新设备加入网络
	 * 
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public abstract int MGR_setPermitJoin(boolean permit_or_not, StringBuilder failedr) throws Exception;

//	/**
//	 * 设备网络结构的自动获取
//	 * 
//	 * @param tcdd
//	 * @throws IOException
//	 */
//	protected abstract void MGR_onTCDevInd(TCDevIndAREQ_CB tcdd) throws IOException;
//
//	/**
//	 * 当新的EndDevice 发现
//	 * 
//	 * @param endd
//	 * @throws IOException
//	 */
//	protected abstract ZStackEndDevice MGR_onEndDeviceAnnce(EndDeviceAnnceIndSREQ_CB endd) throws IOException;
//
//	protected abstract ZStackEndDevice MGR_onEndDeviceNwkAddr(String ieee_addr, NwkAddrRspAREQ_CB nwkaddr_rsp)
//			throws IOException;
//
//	protected abstract void MGR_onIncomingMsg(IncomingMsgAREQ_CB in_msg) throws IOException;

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
	public abstract boolean MGR_setEndDeviceConfigReportAttr(String ieeeaddr, int endpoint, int cluster_id,
			List<AttrReportConfig> attr_rcs, StringBuilder failedr) throws Exception;

	

	/**
	 * 触发EndDevice新发现运行过程
	 * 
	 * @param ieee
	 * @param failedr
	 * @return
	 */
	public abstract boolean MGR_putQueueEndDevice(String ieee, StringBuilder failedr);

	public abstract int MGR_delEndDevice(String ieee, boolean force_del, boolean rejoin, boolean del_uadev,
			StringBuilder failedr) throws Exception;


	/**
	 * 当新的EndDevice 发现
	 * @param endd
	 * @throws IOException 
	 */
	protected ZSDevice MGR_onEndDeviceAnnce(EndDeviceAnnounceResp endd) throws IOException
	{
		String ieee_addr = endd.ieee_addr ;
		ZSDevice old_ed = getDeviceByIEEEAddr(ieee_addr) ;
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
		new_ed.updateBy(new ShortAddr(endd.nwk_addr) ,endd.cap);
		new_ed.setUpdateDT(System.currentTimeMillis()) ;
		setDevice(new_ed) ;

		this.save();
		return new_ed ;
	}
	
	public ZSDevice MGR_onDeviceJoined(DeviceJoinedPld devjoined) throws IOException
	{
		ZSDevice old_ed = getDeviceByIEEEAddr(devjoined.ieee_addr) ;
		ZSDevice new_ed = null;
		if(old_ed==null)
		{//new add
			new_ed = new ZSDevice(this,devjoined.ieee_addr) ;
			new_ed.setFindDT(System.currentTimeMillis()) ;
			UADev related_d = new_ed.getRelatedUADev() ;
			if(related_d!=null)
				new_ed.setTitle(related_d.getTitle()) ;
		}
		else
		{//check change or not
			if(old_ed.getShortAddrVal()==devjoined.network_addr)
				return null;
			new_ed = old_ed ;
		}
		new_ed.updateBy(new ShortAddr(devjoined.network_addr) ,null);
		new_ed.setUpdateDT(System.currentTimeMillis()) ;
		setDevice(new_ed) ;

		this.save();
		return new_ed ;
	}
	

	public void MGR_onIncomingMsg(IncomeMsgDefEvt in_msg) throws IOException
	{
		//IncomeMsgDefEvt income_msg_evt = in_msg.toIncomeMsgDefEvt() ;
		
		 int src_addr = in_msg.getSrcAddr();
		 ZSDevice ed = getDeviceByShortAddr(src_addr) ;
		 if(ed==null)
		 {
			 if(getLog().isWarnEnabled())
				 getLog().warn("no EndDevice found with src_addr"+src_addr+" in IncomeMsg");
			 return ;
		 }
		 
		 ed.setLinkQual(in_msg.getLinkQuality());
		 
		 int src_ep = in_msg.getSrcEndpoint() ;
		 int cluster_id = in_msg.getClusterId() ;
		 // ZCLCluster c = in_msg.getCluster() ;
		 
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
				 if(getLog().isWarnEnabled())
					 getLog().warn("MGR_onIncomingMsg RT_onReportAttr err:"+failedr.toString()+"\r\n\t"+in_msg);
			 }
		 }
	}
	
	
	/**
	 * syn UADev (include sub tags) under coor channel
	 * @param ieeeaddr
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public boolean MGR_synEndDeviceToUADev(String ieeeaddr,StringBuilder failedr) throws Exception
	{
		ZSDevice ed = this.getDeviceByIEEEAddr(ieeeaddr) ;
		if(ed==null)
		{
			failedr.append("no EndDevice found") ;
			return false;
		}
		UACh ch = this.zsDrv.getBelongToCh() ;
		String tt = ed.getTitle() ;
		if(Convert.isNullOrEmpty(tt))
			tt = ed.getIEEEAddr();
		JSONArray tags_jarr = new JSONArray() ;
		ed.setupTagsJArr(tags_jarr) ;
		ch.synOuterDevByJO(ieeeaddr, "ed_", tt, tags_jarr, true);
		return true ;
	}
	

	public boolean MGR_setEndDeviceTitle(String ieeeaddr,String tt) throws Exception
	{
		ZSDevice ed = this.getDeviceByIEEEAddr(ieeeaddr) ;
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
	
	
//	/**
//	 * coor 主动发起读取cluster定义好的(带__tag定义)的所有属性值
//	 * @param endpoint
//	 * @param clusterid
//	 * @return -100=read err in driver
//	 * @throws Exception 
//	 */
//	public abstract int RT_readAttrs(int endpoint,int clusterid,StringBuilder failedr) throws Exception ;
	
	
	
	// -------------------------------------------------------
	public abstract ZSStartResult start();

    public abstract void stop() ;

    public abstract String getCoordinatorIEEE()  throws Exception ;

    public abstract String getCoordinatorVersion() ; // : Promise<TsType.CoordinatorVersion>;

    public abstract void reset(boolean hard_or_soft) ;

    //public abstract boolean supportsBackup() ;

    //public abstract backup(ieeeAddressesInDatabase: string[]): Promise<Models.Backup>;

    public abstract NetworkParam getNetworkParameters() throws Exception ;

    public abstract void addInstallCode(String ieee_addr, String key, boolean hashed) throws Exception ;

//    public abstract waitFor(
//        networkAddress: number,
//        endpoint: number,
//        frameType: Zcl.FrameType,
//        direction: Zcl.Direction,
//        transactionSequenceNumber: number | undefined,
//        clusterId: number,
//        commandId: number,
//        defaultRspCommandId: number | undefined,
//        timeout: number,
//    ): {promise: Promise<AdapterEvents.ZclPayload>; cancel: () => void};

    /**
     * ZDO
     */

    public abstract ZDOResp sendZdo(String ieee_address, int nwk_addr, ZDOCluster clusterobj, boolean disableResponse)
    		throws Exception ;
    
//    public abstract sendZdo<K extends keyof ZdoTypes.RequestToResponseMap>(
//        ieeeAddress: string,
//        networkAddress: number,
//        clusterId: K,
//        payload: Buffer,
//        disableResponse: false,
//    ): Promise<ZdoTypes.RequestToResponseMap[K]>;
//    public abstract sendZdo<K extends keyof ZdoTypes.RequestToResponseMap>(
//        ieeeAddress: string,
//        networkAddress: number,
//        clusterId: K,
//        payload: Buffer,
//        disableResponse: boolean,
//    ): Promise<ZdoTypes.RequestToResponseMap[K] | undefined>;

    public abstract void permitJoin(int seconds, int nwk_addr) throws Exception ;  //networkAddress?: number

    /**
     * ZCL
     */

    //: Promise<AdapterEvents.ZclPayload | undefined>;
    public abstract ZCLFrame sendZclFrameToEndpoint(
        String ieeeAddr,
        int networkAddress,
        int endpoint,
        ZCLFrmClusterCmd zclFrame, //: Zcl.Frame,
        int timeout, //: number,
        boolean disableResponse, //: boolean,
        boolean disableRecovery, //: boolean,
        Integer sourceEndpoint, //?: number,
        Integer profileId //?: number,
    ) throws Exception;

    public abstract void sendZclFrameToGroup(int groupID, ZCLFrmClusterCmd zclf, 
    		Integer sourceEndpoint ,//?: number,
    		Integer profileId) throws Exception ;//?: number);

    public abstract void sendZclFrameToAll(int endpoint, ZCLFrmClusterCmd zclf, Integer src_endpoint, BroadcastAddr destination,
			Integer profileId) throws Exception;

    /**
     * InterPAN
     */

    public abstract void setChannelInterPAN(int channel)  throws Exception;

    public abstract void sendZclFrameInterPANToIeeeAddr(ZCLFrmClusterCmd zclFrame, String ieee_addr) throws Exception;

    // : Promise<AdapterEvents.ZclPayload>;
    public abstract ZCLFrame sendZclFrameInterPANBroadcast(ZCLFrmClusterCmd zclFrame, int timeout) throws Exception ;//, disableResponse: false);
    
    //public abstract void sendZclFrameInterPANBroadcast(ZCLFrame zclFrame,int timeout) ;//, disableResponse: true) ;
    
    //: Promise<AdapterEvents.ZclPayload | undefined>
    public abstract ZCLFrame sendZclFrameInterPANBroadcast(
    		ZCLFrmClusterCmd zclFrame,
        int timeout,
        boolean disableResponse
    ) throws Exception;

    public abstract void restoreChannelInterPAN() throws Exception ;
}
