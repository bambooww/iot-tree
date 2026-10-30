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
import org.iottree.driver.zigbee.ember.util.EZSPWaiterZDO;
import org.iottree.driver.zigbee.models.M_Coor;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.iottree.driver.zigbee.zcl.util.AttrReportConfig;
import org.iottree.driver.zigbee.zspec.types.BroadcastAddr;
import org.iottree.driver.zigbee.zspec.types.InterviewState;
import org.iottree.driver.zigbee.zspec.types.NetworkParam;
import org.iottree.driver.zigbee.zspec.types.ZSDeviceType;
import org.iottree.driver.zigbee.zspec.types.ZSStartResult;
import org.iottree.driver.zigbee.zspec.util.Capabilities;
import org.iottree.driver.zigbee.zspec.util.DeviceState;
import org.iottree.driver.zigbee.zspec.util.PowerSrc;
import org.iottree.driver.zigbee.zspec.util.RunRes;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zspec.util.StartupStatus;
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
	
	protected ILogger log ;

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
		
		log = LoggerManager.getLogger("Coor_"+drv.getBelongToCh().getName()) ;
		log.setCurrentLogLevel(ILogger.LOG_LEVEL_DEBUG);
	}

	public ZSDriver getDriver()
	{
		return this.zsDrv;
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
				if (log.isWarnEnabled())
					log.warn("RT_interviewEndDev " + ed.getIEEEAddr() + " err:" + failedr.toString());
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
		return this.ieee2device.get(ieee_addr.toLowerCase());
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
				if (this.log.isWarnEnabled())
					this.log.warn("RT_interviewEndDev err:" + failedr);
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
			ret.putOpt("pan_id", this.getPanIDSetting()).putOpt("ext_pan_id", this.getExtPanID()).putOpt("nwk_key", this.getNwkKey())
				.putOpt("ieee_addr", this.ieee_addr) ;
			return ret ;
	}

	public abstract void fromJO(JSONObject jo);

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

	public abstract boolean MGR_setEndDeviceTitle(String ieeeaddr, String tt) throws Exception;

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
	 * syn UADev (include sub tags) under coor channel
	 * 
	 * @param ieeeaddr
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public abstract boolean MGR_synEndDeviceToUADev(String ieeeaddr, StringBuilder failedr) throws Exception;

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
