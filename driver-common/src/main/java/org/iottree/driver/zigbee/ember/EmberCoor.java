package org.iottree.driver.zigbee.ember;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.AdapterOptions;
import org.iottree.driver.zigbee.DataBuffer;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EmberConsts;
import org.iottree.driver.zigbee.ember.ezsp.*;
import org.iottree.driver.zigbee.ember.req_rsp.GetValueRR;
import org.iottree.driver.zigbee.ember.req_rsp.SetCommonRR;
import org.iottree.driver.zigbee.ember.req_rsp.SetFacCodeRR;
import org.iottree.driver.zigbee.ember.req_rsp.SetPolicyRR;
import org.iottree.driver.zigbee.ember.req_rsp.*;
import org.iottree.driver.zigbee.ember.util.EZSPWaiterZCL;
import org.iottree.driver.zigbee.ember.util.EZSPWaiterZDO;
import org.iottree.driver.zigbee.ember.util.EzspError;
import org.iottree.driver.zigbee.ember.util.NwkCache;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.iottree.driver.zigbee.models.M_Coor;
import org.iottree.driver.zigbee.models.M_Device;
import org.iottree.driver.zigbee.models.M_NwkKeyInf;
import org.iottree.driver.zigbee.models.M_NwkOptions;
import org.iottree.driver.zigbee.ember.types.*;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.iottree.driver.zigbee.zcl.cmd_frm.DefaultRsp;
import org.iottree.driver.zigbee.zcl.defs.ManufacturerCode;
import org.iottree.driver.zigbee.zcl.util.AttrReportConfig;
import org.iottree.driver.zigbee.zspec.ZDOCluster;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOConsts;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.iottree.driver.zigbee.zspec.ZDOUtil;
import org.iottree.driver.zigbee.zspec.ZSCoor;
import org.iottree.driver.zigbee.zspec.ZSDevice;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;
import org.iottree.driver.zigbee.zspec.types.BroadcastAddr;
import org.iottree.driver.zigbee.zspec.types.NetworkParam;
import org.iottree.driver.zigbee.zspec.types.ZSStartResult;
import org.iottree.driver.zigbee.zspec.util.RunRes;
import org.iottree.driver.zigbee.zspec.util.ZSpecUtil;
import org.iottree.driver.zigbee.zspec.zdo.PermitJoiningReq;
import org.json.JSONObject;

public class EmberCoor extends ZSCoor
{
	public static final long WATCHDOG_COUNTERS_FEED_INTERVAL = 3600000; // every hour...
	
	/** Default behavior is to disable app key requests */
	private static final boolean ALLOW_APP_KEY_REQUESTS = false;
	
	/**
	 * Application generated ZDO messages use sequence numbers 0-127, and the stack
	 * uses sequence numbers 128-255.  This simplifies life by eliminating the need
	 * for coordination between the two entities, and allows both to send ZDO
	 * messages with non-conflicting sequence numbers.
	 */
	private static final int APPLICATION_ZDO_SEQUENCE_MASK = 0x7f;
	
	private static final int DEFAULT_APS_OPTIONS = EmberApsOption.RETRY.getIntVal() | EmberApsOption.ENABLE_ROUTE_DISCOVERY.getIntVal()
			| EmberApsOption.ENABLE_ADDRESS_DISCOVERY.getIntVal();
	
	private static final int DEFAULT_NETWORK_REQUEST_TIMEOUT = 10000; // nothing on the network to bother requests, should be much faster than this
	
	private static final int  ZDO_REQUEST_RADIUS = 0xff;
	
	/** Time for a request to get a callback response. ASH is 2400*6 for ACK timeout. */
	private static final long DEFAULT_REQUEST_TIMEOUT = 15000; // msec
	
	private static final int QUEUE_MAX_SEND_ATTEMPTS = 3;
	private static final int  QUEUE_BUSY_DEFER_MSEC = 500;
	private static final int  QUEUE_NETWORK_DOWN_DEFER_MSEC = 1500;
	
	int ncpVer = -1 ;
	
	
	
	String ncpStackVer = null ;
	
	String ezspVer = null ;
	
	NwkCache nwkCache = new NwkCache() ;
	
	AdapterOptions adapterOpt = new AdapterOptions() ;
	
	private StackConfig stackConfig = null ;
	
	private ArrayList<Integer> multicastTable = new ArrayList<>();
	
	EmberManager ebm = null ;
	
	EZSP  ezsp = null ;
	
	private boolean hasZdoMessageOverhead = true;
	
	/** Sequence number used for ZDO requests. static uint8_t  */
    private transient int zdoRequestSequence = 0;


	public EmberCoor(EmberDriver drv,EmberManager ebm)
	{
		super(drv) ;
		this.ebm = ebm ;
		this.ezsp = new EZSP(ebm) ;
		try
		{
			//loadFromStore() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	public EmberManager getManager()
	{
		return this.ebm ;
	}
	
	public EZSP getEZSP()
	{
		return this.ezsp ;
	}
	
	@Override
	public String getCoorName()
	{
		return "ember";
	}
	
	public int getNCPVerion()
	{
		return this.ncpVer ;
	}
	
	public StackConfig getStackConfig()
	{
		if(stackConfig != null)
			return this.stackConfig ;
		// my be do load
		this.stackConfig = StackConfig.DEFAULT_STACK_CONFIG ;
		return this.stackConfig ;
	}

	private synchronized NwkParams getOrLoadNwkParam()
	{
		if(nwkCache.params!=null)
			return nwkCache.params; 
		try
		{
			NwkGetParamsRsp rsp = NwkRR.getNwkParams(ebm) ;
			if(rsp==null)
				return null ;
			if(rsp.getStatus()!=SLStatus.OK)
				return null ;
			nwkCache.params = rsp.getNwkParams() ;
			if(this.getPanIDSetting()!=nwkCache.params.panId)
				this.setPanIDSetting(nwkCache.params.panId);
			return nwkCache.params ;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null ;
		}
	}
	
	NwkCache getNwkCache()
	{
		return this.nwkCache ;
	}
	
	public synchronized void clearNwkCache()
	{
        this.nwkCache.params = null; //initNetworkCache();
    }
	
	public String emberGetIEEEAddr()
	{
        if (Convert.isNullOrEmpty(this.nwkCache.ieee_addr))
			try
			{
				this.nwkCache.ieee_addr = NwkRR.getEui64(ebm);
			}
			catch ( Exception e)
			{
				e.printStackTrace();
				return null ;
			}

        return this.nwkCache.ieee_addr;
    }
	
	public int emberGetPanId()
	{
		NwkParams np =  getOrLoadNwkParam() ;
		if(np==null)
			throw new RuntimeException("no nwk param get or load") ;
		return np.panId ;
    }
	
	public String emberGetExtPanId()
	{
		NwkParams np =  getOrLoadNwkParam() ;
		if(np==null)
			throw new RuntimeException("no nwk param get or load") ;
		return np.extPanId ;
    }

	@Override
	public boolean RT_init(StringBuilder failedr) throws Exception
	{
		return false;
	}
	
	boolean RT_init_before_start(StringBuilder failedr) throws Exception
	{
		final int TRUST_CENTER_ADDRESS_CACHE_SIZE = 2;
		
		this.ncpStackVer = GetValueRR.ezspGetVersion(ebm,failedr) ;
		if(Convert.isNullOrEmpty(this.ncpStackVer))
			return false;
		
		/** The address cache needs to be initialized and used with the source routing code for the trust center to operate properly. */
		ConfRR.setConfigVal(ebm, EzspConfigId.TRUST_CENTER_ADDRESS_CACHE_SIZE, TRUST_CENTER_ADDRESS_CACHE_SIZE) ;
		/** MAC indirect timeout should be 7.68 secs (STACK_PROFILE_ZIGBEE_PRO) */
		ConfRR.setConfigVal(ebm, EzspConfigId.INDIRECT_TRANSMISSION_TIMEOUT, 7680);
        /** Max hops should be 2 * nwkMaxDepth, where nwkMaxDepth is 15 (STACK_PROFILE_ZIGBEE_PRO) */
		ConfRR.setConfigVal(ebm, EzspConfigId.MAX_HOPS, 30);
		ConfRR.setConfigVal(ebm, EzspConfigId.SUPPORTED_NETWORKS, 1);
        
		//int n = ConfRR.RT_updateConfig(ebm) ;

		SetPolicyRR.setPolicyVal(ebm,EzspPolicyId.BINDING_MODIFICATION_POLICY,
	            EzspDecisionId.CHECK_BINDING_MODIFICATIONS_ARE_VALID_ENDPOINT_CLUSTERS.getIntVal()) ;
		// return message tag only in ezspMessageSentHandler()
		SetPolicyRR.setPolicyVal(ebm,EzspPolicyId.MESSAGE_CONTENTS_IN_CALLBACK_POLICY, EzspDecisionId.MESSAGE_TAG_ONLY_IN_CALLBACK.getIntVal());
		//n = SetPolicyRR.RT_updatePolices(ebm) ;
		StackConfig stack_config = getStackConfig() ;
		byte[] vs = new byte[2] ;
		DataUtil.setUInt16(stack_config.TRANSIENT_DEVICE_TIMEOUT, vs, 0);
		SetValueRR.setValue(ebm, EzspValueId.TRANSIENT_DEVICE_TIMEOUT, vs) ;

		final int DEFAULT_MANUFACTURER_CODE = ManufacturerCode.SILICON_LABORATORIES;
		SetFacCodeRR.setFacCode(ebm, DEFAULT_MANUFACTURER_CODE);
		
		// network security init
		ConfRR.setConfigVal(ebm, EzspConfigId.STACK_PROFILE, EmberConsts.STACK_PROFILE_ZIGBEE_PRO);
		ConfRR.setConfigVal(ebm, EzspConfigId.SECURITY_LEVEL, EmberConsts.SECURITY_LEVEL_Z3);
        // common configs
		ConfRR.setConfigVal(ebm, EzspConfigId.MAX_END_DEVICE_CHILDREN, stack_config.MAX_END_DEVICE_CHILDREN);
		ConfRR.setConfigVal(ebm, EzspConfigId.END_DEVICE_POLL_TIMEOUT, stack_config.END_DEVICE_POLL_TIMEOUT);
		ConfRR.setConfigVal(ebm, EzspConfigId.TRANSIENT_KEY_TIMEOUT_S, stack_config.TRANSIENT_KEY_TIMEOUT_S);
        // XXX: temp-fix: forces a side-effect in the firmware that prevents broadcast issues in environments with unusual interferences
		SetValueRR.setValue(ebm, EzspValueId.CCA_THRESHOLD, new byte[] {0});

        if (stack_config.CCA_MODE!=null) {
            // validated in `loadStackConfig`
            SetCommonRR.setRadioIeee802154CcaMode(ebm, stack_config.CCA_MODE);
        }
        registerFixedEndpoints() ;
        
        this.MGR_initTrustCenter() ; // 初始化或装载网络
        
        this.MGR_initNCPConcentrator();
        
     // populate network cache info
        //const [status, , parameters] = await this.ezsp.ezspGetNetworkParameters();
        NwkGetParamsRsp prsp = NwkRR.getNwkParams(ebm) ; 
        SLStatus status = prsp.getStatus() ;
        NwkParams params = prsp.getNwkParams() ;

        if (status != SLStatus.OK) {
            throw new Exception("Failed to get network parameters with status="+status);
        }

        if (this.adapterOpt.tansmit_power>0 && params.radio_tx_power != this.adapterOpt.tansmit_power)
        {
        	SLStatus rp_status = NwkRR.setRadioPower(ebm, this.adapterOpt.tansmit_power);

            if (rp_status != SLStatus.OK) {
                // soft-fail, don't prevent start
                log.error("Failed to set transmit power to "+this.adapterOpt.tansmit_power+" status="+rp_status);
            }
        }

        this.nwkCache.params = params;
        this.nwkCache.ieee_addr = NwkRR.getEui64(ebm);

        log.debug("[INIT] Network Ready! "+this.nwkCache);

        //this.watchdogCountersHandle = setInterval(this.watchdogCounters.bind(this), WATCHDOG_COUNTERS_FEED_INTERVAL);

        //return result;
        
		return true;
	}
	
	/**
     * Register fixed endpoints and set any related multicast entries that need to be.
     */
    private void registerFixedEndpoints() throws Exception
    {
        for (EndpointInfo ep : EndpointInfo.FIXED_ENDPOINTS)
        {
        	EzspEndpointFlag epf = GetExtValueRR.getEndpointFlags(ebm,ep.endpoint);
            // endpoint not already registered
            if (epf ==null)
            {
                // check to see if ezspAddEndpoint needs to be called
                // if ezspInit is called without NCP reset, ezspAddEndpoint is not necessary and will return an error
            	SLStatus sl_st = SetCommonRR.addEndpoint(ebm,ep.endpoint,ep.profileId,ep.deviceId,ep.deviceVersion,
                    ep.inClusterList,ep.outClusterList
                );

                if (sl_st!=null&& sl_st.isOk())
                {
                	if(log.isDebugEnabled())
                		log.debug("Registered endpoint ["+ep.endpoint+"]");
                } else {
                    throw new Exception("Failed to register endpoint ["+ep.endpoint+"] with status="+sl_st);
                }
            } else {
            	if(log.isDebugEnabled())
            		log.debug("Endpoint ["+ep.endpoint+"] already registered.");
            }

            for (Integer multicastId : ep.multicastIds)
            {
            	MulticastTableEntryGetRsp old_mte = SetCommonRR.getMulticastTableEntry(ebm,ep.networkIndex) ;
            	if(old_mte!=null&&old_mte.getStatus()==SLStatus.OK)
            	{
            		MulticastTableEntry old_te = old_mte.getMulticastTableEntry() ;
            		if(old_te!=null)
            			continue ;
            	}
            	MulticastTableEntry multicastEntry  = new MulticastTableEntry(multicastId,ep.endpoint,ep.networkIndex);
                SLStatus status = SetCommonRR.setMulticastTableEntry(ebm,this.multicastTable.size(), multicastEntry);
                if (status!=SLStatus.OK)
                    throw new Exception("Failed to register group "+multicastId+" in multicast table with status="+status);

                if(log.isDebugEnabled())
            		log.debug("Registered multicast table entry "+this.multicastTable.size()+" "+multicastEntry);
                this.multicastTable.add(multicastEntry.multicastId);
            }
        }
    }
	
	boolean RT_startup(StringBuilder failedr) throws Exception
	{
		return true;
	}
	
	boolean RT_init_after_start(StringBuilder failedr) throws Exception
	{
		return true;
	}
	
	public boolean RT_interview(StringBuilder failedr) throws Exception
	{
		return true;
	}
	
	@Override
	public RunRes RT_onCoorNorRunning() throws Exception
	{
		return RunRes.ok ;
	}

	@Override
	public JSONObject toJO()
	{
		JSONObject ret = super.toJO() ;
		
		return ret ;
	}

	@Override
	public void fromJO(JSONObject jo)
	{
		
	}
	

	@Override
	public int MGR_setPermitJoin(boolean permit_or_not, StringBuilder failedr) throws Exception
	{
		int sec = permit_or_not?0xFE:0x00 ;
		SLStatus slst = NwkRR.permitJoining(ebm, sec) ;
		if(slst == SLStatus.OK)
			return sec ;
		return -1 ;
	}

	
	/**
    *
    * @returns True if the network needed to be formed.
    */
   private String MGR_initTrustCenter() throws Exception
   {
       // init TC policies
       {
    	   SLStatus status = SetPolicyRR.setPolicyVal(ebm,EzspPolicyId.TC_KEY_REQUEST_POLICY, EzspDecisionId.ALLOW_TC_KEY_REQUESTS_AND_SEND_CURRENT_KEY.getIntVal());

           if (status!= SLStatus.OK) {
               throw new Exception("[INIT TC] Failed to set EzspPolicyId TC_KEY_REQUEST_POLICY to ALLOW_TC_KEY_REQUESTS_AND_SEND_CURRENT_KEY with status="+status);
           }

           /* v8 ignore next */
           EzspDecisionId appKeyRequestsPolicy = ALLOW_APP_KEY_REQUESTS ? EzspDecisionId.ALLOW_APP_KEY_REQUESTS : EzspDecisionId.DENY_APP_KEY_REQUESTS;
           status = SetPolicyRR.setPolicyVal(ebm,EzspPolicyId.APP_KEY_REQUEST_POLICY, appKeyRequestsPolicy.getIntVal());

           if (status != SLStatus.OK) {
        	   throw new Exception("[INIT TC] Failed to set EzspPolicyId APP_KEY_REQUEST_POLICY to ${EzspDecisionId[appKeyRequestsPolicy]} with status="+status);
           }
           
           status = SetPolicyRR.setJoinPolicy(ebm,EmberJoinDecision.USE_PRECONFIGURED_KEY);

           if (status != SLStatus.OK) {
        	   throw new Exception("[INIT TC] Failed to set join policy to USE_PRECONFIGURED_KEY with status="+status);
           }
       }
       
       // 对比coor装载的之前保存数据
       
       int nwk_bitm =  NwkInitBitmask.PARENT_INFO_IN_TOKEN.getIntVal() | NwkInitBitmask.END_DEVICE_REJOIN_ON_REBOOT.getIntVal() ;
       SLStatus initStatus =  NwkRR.nwkInit(ebm,nwk_bitm);

       if(log.isDebugEnabled())
    	   log.debug("[INIT TC] Network init status="+initStatus);
       
       if (initStatus != SLStatus.OK && initStatus != SLStatus.NOT_JOINED)
           throw new Exception("[INIT TC] Failed network init request with status="+initStatus);
       
       NwkInitAction action = NwkInitAction.DONE ;
       if(initStatus==SLStatus.OK)
       {
    	   waitForStackStatusEvt(SLStatus.NETWORK_UP,"[INIT TC] Network init") ;
           
           //const [npStatus, nodeType, netParams] = await this.ezsp.ezspGetNetworkParameters();
           NwkGetParamsRsp ngp = NwkRR.getNwkParams(ebm) ;
           SLStatus np_status = ngp.getStatus() ;
           NwkParams nwk_params = ngp.getNwkParams() ;
           if(log.isDebugEnabled())
        	   log.debug("[INIT TC] Current adapter network: nodeType="+ngp.getEmberNodeType()+" params="+ngp.getNwkParams().toJO());

           if (np_status == SLStatus.OK && ngp.getEmberNodeType() == EmberNodeType.COORDINATOR &&
                   this.settings.getPanId() == nwk_params.panId && nwk_params.getExtPanIdStr().equals(this.settings.getExtPanId()))
           {
                   // config matches adapter so far, no error, we can check the network key
                   SecManContext context =  SecManContext.initContext();
                   context.coreKeyType = SecManKeyType.NETWORK;
                   context.keyIndex = 0;
                   SecManExportKeyRsp exp_rsp = SecRR.exportKey(ebm,context);
                   SLStatus nk_status = exp_rsp.getSLStatus() ;
                   String nwk_key = exp_rsp.getSecManKey() ;
                   if (nk_status != SLStatus.OK) {
                       throw new Exception("[INIT TC] Failed to export Network Key with status="+nk_status);
                   }

                   // config doesn't match adapter anymore
                   if (!nwk_key.equals(this.settings.getNetworkKey())) {
                       action = NwkInitAction.LEAVE;
                   }
               } else {
                   // config doesn't match adapter
                   action = NwkInitAction.LEAVE;
               }
           
           if (action == NwkInitAction.LEAVE)
           {
               log.info("[INIT TC] Adapter network does not match config. Leaving network...");
               SLStatus leave_st = NwkRR.nwkLeave(ebm) ;
               if (leave_st != SLStatus.OK) {
                   throw new Exception("[INIT TC] Failed leave network request with status="+leave_st);
               }

               waitForStackStatusEvt(SLStatus.NETWORK_DOWN, "[INIT TC] Leave network") ;

               EmberManager.sleep(200);// settle down

               action = NwkInitAction.LEFT;
           }
       }
       

       M_Coor backup = this.loadBackup() ;

       if (initStatus == SLStatus.NOT_JOINED || action == NwkInitAction.LEFT) {
           // no network
           if (backup != null)
           {
               if (
            		   backup.checkPanId(this.settings.getPanId()) &&  //this.settings.getPanId() == backup.nwk_opts.panId &&
                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
            		   backup.checkExtPanId(this.settings.getExtPanId()) && //   this.se.chkExtPanID(backup.nwk_opts.extPanId) &&
                   this.settings.includeCh(backup.radio_ch) &&
                   backup.checkNwkKey(this.settings.getNetworkKey()) //this.settings.chkNetworkKey(backup.nwk_opts.networkKey)
               ) {
                   // config matches backup
                   action = NwkInitAction.FORM_BACKUP;
               } else {
                   // config doesn't match backup
                   log.info("[INIT TC] Config does not match backup.");
                   action = NwkInitAction.FORM_CONFIG;
               }
           } else {
               // no backup
               log.info("[INIT TC] No valid backup found.");
               action = NwkInitAction.FORM_CONFIG;
           }
       }

       //---- from here on, we assume everything is in place for whatever decision was taken above

       String result = "resumed";

       switch (action) {
           case FORM_BACKUP: {
               log.info("[INIT TC] Forming from backup.");
               // `backup` valid in this `action` path (not detected by TS)
               /* v8 ignore start */
               List<LinkKeyData> keyList = new ArrayList<>();

               // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
               if(backup!=null)
               {
	               for (M_Device device : backup.devices) {
	                   // link_key is optional in the open coordinator backup format, skip devices without one
	                   if (Convert.isNullOrEmpty(device.link_key))
	                       continue;
	                   
	                   LinkKeyData lk= new LinkKeyData() ;
	                   lk.dev_ieee_addr =  device.ieee_addr;
	                    lk.sec_key = device.link_key;
	                    lk.out_frm_cc= device.tx_counter;
	                    lk.in_frm_cc= device.rx_counter ;
	                   
	                   keyList.add(lk);
	               }
               }
               /* v8 ignore stop */

               // before forming
               this.MGR_importLinkKeys(keyList);

               /*
                boolean fromBackup , String networkKey,int networkKeySequenceNumber,
            	        int networkKeyFrameCounter,int panId,String extendedPanId,int radioChannel,
            	        String tcLinkKey,int nwkUpdateId)
                */
               if(backup!=null)
               {
	               this.MGR_formNetwork(
	                   true /*from backup*/,
	                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
	                   backup.nwk_opts.networkKey,
	                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
	                   (int)backup.nwk_key_inf.seq_num,
	                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
	                   (int)backup.nwk_key_inf.frame_counter,
	                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
	                   backup.nwk_opts.panId,
	                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
	                   backup.nwk_opts.extPanId,
	                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
	                   backup.radio_ch,
	                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
	                   backup.ezsp_tc_linkkey, // valid from getStoredBackup
	                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
	                   backup.nwk_updateid
	               );
               }

               result = "restored";
               break;
           }
           case FORM_CONFIG: {
               log.info("[INIT TC] Forming from config.");
               MGR_formNetwork(
                   false /*from config*/,
                   this.settings.getNetworkKey(),
                   0,
                   0,
                   this.settings.getPanId(),
                   // biome-ignore lint/style/noNonNullAssertion: ignored using `--suppress`
                   this.settings.getExtPanId(),
                   this.settings.getChannel(),//this.settings.getChannelList().get(0),
                   DataUtil.randomBytesHex(EZSPConsts.EMBER_ENCRYPTION_KEY_SIZE), // rnd TC link key
                   0
               );

               result = "reset";
               break;
           }
           case DONE: {
               log.info("[INIT TC] Adapter network matches config.");
               break;
           }
           default:
        	   break ;
       }

       // can't let frame counter wrap to zero (uint32_t), will force a broadcast after init if getting too close
       if (backup != null && backup.nwk_key_inf.frame_counter > 0xfeeeeeee) {
           // XXX: while this remains a pretty low occurrence in most (small) networks,
           //      currently Z2M won't support the key update because of one-way config...
           //      need to investigate handling this properly

           // logger.warning(`[INIT TC] Network key frame counter is reaching its limit. Scheduling broadcast to update network key. `
           //     + `This may result in some devices (especially battery-powered) temporarily losing connection.`, NS);
           // // XXX: no idea here on the proper timer value, but this will block the network for several seconds on exec
           // //      (probably have to take the behavior of sleepy-end devices into account to improve chances of reaching everyone right away?)
           // setTimeout(async () => {
           //     this.requestQueue.enqueue(async (): Promise<SLStatus> => {
           //         await this.broadcastNetworkKeyUpdate();

           //         return SLStatus.OK;
           //     }, logger.error, true);// no reject just log error if any, will retry next start, & prioritize so we know it'll run when expected
           // }, 300000);
           log.warn("[INIT TC] Network key frame counter is reaching its limit. A new network key will have to be instaured soon.");
       }

       
       return result;
   }
   
   private void MGR_initNCPConcentrator() throws Exception
   {
       SLStatus status = NwkRR.setConcentrator(ebm,true,
           this.stackConfig.CONCENTRATOR_RAM_TYPE == "low" ? EmberConsts.EMBER_LOW_RAM_CONCENTRATOR : EmberConsts.EMBER_HIGH_RAM_CONCENTRATOR,
           this.stackConfig.CONCENTRATOR_MIN_TIME,
           this.stackConfig.CONCENTRATOR_MAX_TIME,
           this.stackConfig.CONCENTRATOR_ROUTE_ERROR_THRESHOLD,
           this.stackConfig.CONCENTRATOR_DELIVERY_FAILURE_THRESHOLD,
           this.stackConfig.CONCENTRATOR_MAX_HOPS
       );

       if (status != SLStatus.OK) {
           throw new Exception("[CONCENTRATOR] Failed to set concentrator with status="+status);
       }

       long remainTilMTORR = MsgRR.setSrcRouteDiscMode(ebm,EmberSrcRouteDiscMode.RESCHEDULE);

       log.info("[CONCENTRATOR] Started source route discovery. "+remainTilMTORR+"ms until next broadcast.");
   }
   
   /**
    * 根据WATCHDOG_COUNTERS_FEED_INTERVAL，定时运行
    */
   private void MGR_watchdogCounters()
   {
        List<Integer> ncpCounters = null;
		try
		{
			ncpCounters = UtilRR.readAndClearCounters(ebm);
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}

           log.info("[NCP COUNTERS] "+Convert.combineWith(ncpCounters, ','));

           List<Integer> ash_cc = ebm.slideWin.readAndClearCounters() ;
           //const ashCounters = this.ezsp.ash.readAndClearCounters();

           log.info("[ASH COUNTERS] "+Convert.combineWith(ash_cc, ','));
   }
   
   /**
    * Export link keys for backup.
    *
    * @return List of keys data with AES hashed keys
    */
   public List<LinkKeyData> exportLinkKeys() throws Exception
   {
	   Integer key_table_sz = ConfRR.getConfigVal(ebm, EzspConfigId.KEY_TABLE_SIZE) ;
	      //const [confStatus, keyTableSize] = await this.ezsp.ezspGetConfigurationValue();

	      if (key_table_sz==null) {
	          throw new Exception("Failed to retrieve key table size from NCP ");
	      }

	      SecManContext context ;
       String plaintextKey;
       SecManAPSKeyMeta apsKeyMeta;
       SLStatus status;
       
       List<LinkKeyData> keyList = new ArrayList<>() ;

       for (int i = 0; i < key_table_sz; i++)
       {
    	   SecManExportLinkKeyByIdxRsp rsp = SecRR.exportLinkKeyByIndex(ebm,i);
    	   status = rsp.getSLStatus() ;
           log.debug("Export link key at index "+i+", status="+status);
           context = rsp.getContext() ;
           plaintextKey = rsp.getSecManKey() ;
           apsKeyMeta = rsp.getKeyData() ;
           // only include key if we could retrieve one at index and hash it properly
           if (status == SLStatus.OK) {
               // Rather than give the real link key, the backup contains a hashed version of the key.
               // This is done to prevent a compromise of the backup data from compromising the current link keys.
               // This is per the Smart Energy spec.
        	   // TODO aes128MmoHash
//               const hashedKey = ZSpec.Utils.aes128MmoHash(plaintextKey.contents);
//
//               keyList.push({
//                   deviceEui64: context.eui64,
//                   key: {contents: hashedKey},
//                   outgoingFrameCounter: apsKeyMeta.outgoingFrameCounter,
//                   incomingFrameCounter: apsKeyMeta.incomingFrameCounter,
//               });
        	   keyList.add(new LinkKeyData(context.eui64,plaintextKey,
        			   apsKeyMeta.outgoingFrameCounter,apsKeyMeta.incomingFrameCounter)) ;
           }
       }

       log.info(" Retrieved "+keyList.size()+" link keys");

       return keyList;
   }
   
   /**
    * Import link keys from backup.
   *
   * @param backupData
   */
   private void MGR_importLinkKeys(List<LinkKeyData> linkks) throws Exception
   {
      if (linkks==null || linkks.size()<=0)
          return;

      Integer key_table_sz = ConfRR.getConfigVal(ebm, EzspConfigId.KEY_TABLE_SIZE) ;
      //const [confStatus, keyTableSize] = await this.ezsp.ezspGetConfigurationValue();

      if (key_table_sz==null) {
          throw new Exception("Failed to retrieve key table size from NCP ");
      }

      if (linkks.size() > key_table_sz) {
          throw new Exception("Current key table of ${keyTableSize} is too small to import backup of "+linkks.size());
      }

      NwkStateRsp nwk_rsp = NwkRR.ezspNwkStatus(ebm) ;
      NwkStatus nwk_status = null ;
      if(nwk_rsp!=null)
    	  nwk_status = nwk_rsp.getStatus() ;
      
      if (nwk_status != NwkStatus.NO_NETWORK) {
          throw new Exception("Cannot import TC data while network is up, networkStatus="+nwk_status);
      }

      SLStatus status;

      for (int i = 0; i < key_table_sz; i++)
      {
    	  LinkKeyData lk = linkks.get(i) ;
          // erase any key index not present in backup but available on the NCP
          status = (i >= linkks.size())? SecRR.eraseKeyTableEntry(ebm, i)
                  : SecRR.importLinkKey(ebm,i, lk.dev_ieee_addr, lk.sec_key);

          if (status != SLStatus.OK) {
        	  String ee = i >= linkks.size() ? "erase" : "set" ;
              throw new Exception("Failed to ["+ee+"] key table entry at index ${i} with status="+status);
          }
      }

      log.info("Imported "+linkks.size()+" keys.");
  }

   private void MGR_formNetwork(
		   boolean fromBackup , String networkKey,int networkKeySequenceNumber,
	        int networkKeyFrameCounter,int panId,String extendedPanId,int radioChannel,
	        String tcLinkKey,int nwkUpdateId) throws Exception
   {
	   EmberInitSecState state  = new EmberInitSecState();
	   state.bitmask = EmberInitSecBitmask.TRUST_CENTER_GLOBAL_LINK_KEY.getIntVal() |
	           	EmberInitSecBitmask.HAVE_PRECONFIGURED_KEY.getIntVal() |
	           	EmberInitSecBitmask.HAVE_NETWORK_KEY.getIntVal() |
	           	EmberInitSecBitmask.TRUST_CENTER_USES_HASHED_LINK_KEY.getIntVal() |
	           	EmberInitSecBitmask.REQUIRE_ENCRYPTED_KEY.getIntVal() ;
	            	
	   state.preconfiguredKey= tcLinkKey;
	   state.networkKey = networkKey;
	   state.networkKeySequenceNumber = networkKeySequenceNumber;
	   state.preconfiguredTrustCenterEui64 = null; //: ZSpec.BLANK_EUI64,

	        if (fromBackup) {
	            state.bitmask |= EmberInitSecBitmask.NO_FRAME_COUNTER_RESET.getIntVal();

	            SLStatus status = NwkRR.ezspSetNWKFrameCounter(ebm,networkKeyFrameCounter);

	            if (status!=SLStatus.OK) {
	                throw new Exception("[INIT FORM] Failed to set NWK frame counter with status="+status);
	            }
	        }
	        
	        SLStatus status = SecRR.setInitSecState(ebm, state);

	        if (status != SLStatus.OK)
	            throw new Exception("[INIT FORM] Failed to set initial security state with status="+status);

	        int extended = EmberExtSecBitmask.JOINER_GLOBAL_LINK_KEY.getIntVal() | EmberExtSecBitmask.NWK_LEAVE_REQUEST_NOT_ALLOWED.getIntVal();
	        status = SecRR.setEmberExtSecBitmask(ebm, extended);

	        if (status != SLStatus.OK) {
	            throw new Exception("[INIT FORM] Failed to set extended security bitmask to "+extended+" with status="+status);
	        }

	        if (!fromBackup)
	        {
	            status = SecRR.clearKeyTable(ebm); //.ezspClearKeyTable();

	            if (status != SLStatus.OK) {
	                log.error("[INIT FORM] Failed to clear key table with status="+status);
	            }
	        }

	        NwkParams netParams = new NwkParams();
	        netParams.panId = panId ;
	        netParams.extPanId =  extendedPanId ;
	        netParams.radio_tx_power = this.adapterOpt.tansmit_power>0?this.adapterOpt.tansmit_power: 5 ;
	        netParams.radio_ch = radioChannel ;
	        netParams.join_method = EmberJoinMethod.MAC_ASSOCIATION ;
	        netParams.nwk_short_addr = ZSpecConsts.COORDINATOR_ADDRESS;
	        netParams.nwk_updateid =     nwkUpdateId ;
	        netParams.channels = ZSpecConsts.ALL_802_15_4_CHANNELS_MASK ;

	        log.info("[INIT FORM] Forming new network with: "+netParams.toJO());

	        status = NwkRR.formNetwork(ebm,netParams);

	        if (status != SLStatus.OK) {
	            throw new Exception("[INIT FORM] Failed form network request with status="+status);
	        }

	        waitForStackStatusEvt(SLStatus.NETWORK_UP,"[INIT FORM] Form network") ;

	        status = SetValueRR.startWritingStackTokens(ebm);

	        log.debug("[INIT FORM] Start writing stack tokens status="+status);
	        log.info("[INIT FORM] New network formed!");
	    }
   
   private EZSPRsp waitForStackStatusEvt(SLStatus wait_st,String inf)
   {
	   EZSPWaiterEvt wait_evt = new EZSPWaiterEvt(this.ebm,EzspFrameID.STACK_STATUS_HANDLER,DEFAULT_NETWORK_REQUEST_TIMEOUT) {
       	public boolean ezspStackStatusHandler(SLStatus status)
       	{
       		super.ezspStackStatusHandler(status) ;
               if(status==wait_st)
               {
               	if(log.isDebugEnabled())
               		log.debug(inf+"wait STACK_STATUS_HANDLER - "+wait_st+" ok");
                   return true ;// wait ok
               }
               return true ;
       	}
       } ;
       return ebm.RT_synWaitFor(wait_evt) ;
   }
   
// queued
   public M_Coor MGR_saveCxt(String[] _ieeeAddressesInDatabase) throws Exception
   {
           // grab fresh version here, bypass cache
	   NwkGetParamsRsp ngp = NwkRR.getNwkParams(ebm) ;
       SLStatus net_status = ngp.getStatus() ;
       NwkParams nwk_params = ngp.getNwkParams() ;

       if (net_status != SLStatus.OK) {
           throw new Exception("[SAVE_CXT] Failed to get network parameters with status="+net_status);
       }

       // update cache
       this.nwkCache.params = nwk_params;
       this.nwkCache.ieee_addr = NwkRR.getEui64(ebm);
       
       GetNwkKeyInfRsp nk_rsp = SecRR.getNetworkKeyInfo(ebm) ;
       SLStatus net_key_st = nk_rsp.getStatus() ;
       SecManNwkKeyInf net_key_inf = nk_rsp.getNwkKeyInf() ;

       if (net_key_st != SLStatus.OK) {
           throw new Exception("[SAVE_CXT] Failed to get network keys info with status="+net_key_st);
       }

       if (!net_key_inf.networkKeySet) {
           throw new Exception("[SAVE_CXT] No network key set.");
       }

       /* v8 ignore next */
       List<LinkKeyData> keyList = ALLOW_APP_KEY_REQUESTS ? this.exportLinkKeys() : Arrays.asList();

       SecManContext context  = SecManContext.initContext();
       context.coreKeyType = SecManKeyType.TC_LINK;
       SecManExportKeyRsp exp_key_rsp = SecRR.exportKey(ebm,context) ;
       SLStatus tclk_status= exp_key_rsp.getSLStatus() ;
       String tc_linkkey = exp_key_rsp.getSecManKey() ;

       if (tclk_status != SLStatus.OK) {
           throw new Exception("Failed to export TC Link Key with status="+tclk_status);
       }

       context =  SecManContext.initContext(); // make sure it's back to zeroes
       context.coreKeyType = SecManKeyType.NETWORK;
       context.keyIndex = 0;
       //const [nkStatus, networkKey] = await this.ezsp.ezspExportKey(context);
       SecManExportKeyRsp rsp0 = SecRR.exportKey(ebm,context) ;
       SLStatus nk_status = rsp0.getSLStatus() ;
       String network_key = rsp0.getSecManKey() ;

       if (nk_status != SLStatus.OK) {
           throw new Exception("Failed to export Network Key with status="+nk_status);
       }

       M_Coor mcoor = new M_Coor() ;
       mcoor.radio_ch = nwk_params.radio_ch ;
       mcoor.nwk_updateid = nwk_params.nwk_updateid ;
       mcoor.coor_ieee_addr = nwkCache.ieee_addr ;
       mcoor.security_lvl = EmberConsts.SECURITY_LEVEL_Z3;
       
       mcoor.ezsp_ver = this.ezspVer;
       mcoor.ezsp_tc_linkkey = tc_linkkey ;
       
       mcoor.nwk_opts = new M_NwkOptions() ;
       mcoor.nwk_opts.extPanId = nwk_params.extPanId ;
       mcoor.nwk_opts.panId = nwk_params.panId ;
       mcoor.nwk_opts.extPanId = nwk_params.extPanId ;
       mcoor.nwk_opts.networkKey = network_key ;
       mcoor.nwk_opts.networkKeyDistribute = false;
       mcoor.nwk_opts.channelList = nwk_params.listChannels() ;
       
       for(LinkKeyData keyl:keyList)
       {
    	   M_Device dev = new M_Device() ;
    	   dev.ieee_addr = keyl.dev_ieee_addr ;
    	   dev.link_key = keyl.sec_key ;
    	   dev.rx_counter = keyl.in_frm_cc ;
    	   dev.tx_counter = keyl.out_frm_cc ;
    	   mcoor.devices.add(dev) ;
       }
       
       mcoor.nwk_key_inf = new M_NwkKeyInf() ;
       mcoor.nwk_key_inf.seq_num = net_key_inf.networkKeySequenceNumber ;
       mcoor.nwk_key_inf.frame_counter = net_key_inf.networkKeyFrameCounter ;
       
       
       return mcoor ;
   }

/**
 * coor 主动发起读取cluster定义好的(带__tag定义)的所有属性值
 * @param endpoint
 * @param clusterid
 * @return -100=read err in driver
 * @throws Exception 
 */
//public int RT_readAttrs(int endpoint,int clusterid,StringBuilder failedr) throws Exception
//{
//	ZSEndPoint ep = this.getEndPoint(endpoint) ;
//	if(ep==null)
//	{
//		failedr.append("no end point found") ;
//		return -1;
//	}
//	ZSCluster zsc = ep.getInCluster(clusterid);
//	if(zsc==null)
//	{
//		failedr.append("no in cluster found") ;
//		return -1;
//	}
//	ZCLCluster cdef = zsc.getClusterDef() ;
//	if(cdef==null)
//	{
//		failedr.append("no in cluster def found") ;
//		return 0; //err
//	}
//	List<Integer> attr_ids = cdef.listTagDefAttrIds() ;
//	if(attr_ids.size()<=0)
//	{
//		failedr.append("no tag def attr found") ;
//		return 0; // not err
//	}
//	IncomingMsgAREQ_CB inc_msg = this.readReadAttr(this.short_addr.getAddrVal(), endpoint, clusterid, attr_ids, failedr);
//	if(inc_msg==null)
//		return -100;
//	ZCLFrame zclf = inc_msg.getZCLFrame(failedr) ;
//	if(zclf==null || !(zclf instanceof ReadAttrRsp))
//		return -1 ;
//	
//	ReadAttrRsp attr_rsp = (ReadAttrRsp)zclf ;
//	HashMap<Integer,DataVal> attrid2dv = attr_rsp.getReadAttrId2DataVal() ;
//	ArrayList<ZSAttrVal> avs = new ArrayList<>() ;
//	for(Map.Entry<Integer,DataVal> id2dv:attrid2dv.entrySet())
//	{
//		ZSAttrVal zav = zsc.RT_onUpdateAttrVal(id2dv.getKey(),id2dv.getValue()) ;
//		if(zav!=null)
//			avs.add(zav) ;
//	}
//	
//	if(avs==null||avs.size()>0)
//		RT_onUpdateAttrVals(avs) ;
//	
//	return attrid2dv.size() ;
//}
	
	@Override
	protected int RT_interviewEndDev(ZSDevice ed, StringBuilder failedr) throws Exception
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean MGR_setEndDeviceTitle(String ieeeaddr, String tt) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean MGR_setEndDeviceConfigReportAttr(String ieeeaddr, int endpoint, int cluster_id,
			List<AttrReportConfig> attr_rcs, StringBuilder failedr) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean MGR_synEndDeviceToUADev(String ieeeaddr, StringBuilder failedr) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean MGR_putQueueEndDevice(String ieee, StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int MGR_delEndDevice(String ieee, boolean force_del, boolean rejoin, boolean del_uadev, StringBuilder failedr)
			throws Exception
	{
		// TODO Auto-generated method stub
		return 0;
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
		return NwkRR.getEui64(this.ebm) ;
	}
	
	@Override
	public String getCoordinatorVersion()
	{
		return "EmberZNet";
	}
	
	@Override
	public void reset(boolean hard_or_soft)
	{
		
	}
	
	@Override
	public NetworkParam getNetworkParameters() throws Exception
	{
		int channel = NwkRR.getRadioChannel(ebm);
		NwkGetParamsRsp rrsp = NwkRR.getNwkParams(ebm) ; //await this.emberGetPanId();
		if(rrsp==null)
			return null ;
		SLStatus st = rrsp.getStatus() ;
		if(st!=SLStatus.OK)
			return null ;
		NwkParams nps = rrsp.getNwkParams() ;
		int panid = nps.panId;
		String extpanid = nps.extPanId ;
	    return new NetworkParam(panid,extpanid,channel,-1) ;
	}
	
	@Override
	public void addInstallCode(String ieee_addr, String key, boolean hashed) throws Exception
	{
		if(!hashed)
		{
			byte[] bs = Convert.hexStr2ByteArray(key) ;
			bs = ZSpecUtil.aes128MmoHash(bs) ;
			key = Convert.byteArray2HexStr(bs) ;
		}
		SLStatus  st = SecRR.importTransientKey(ebm,ieee_addr,key,SecManFlag.NONE) ;
		if(st!=SLStatus.OK)
			throw new Exception("addInstallCode failed for "+ieee_addr+" with status="+st) ;
		if(log.isDebugEnabled())
			log.debug("addInstallCode succ for "+ieee_addr);
	}
	
	/**
	 * ZDO
	 * Get the next device request sequence number.
	 *
	 * Requests have sequence numbers so that they can be matched up with the
	 * responses. To avoid complexities, the library uses numbers with the high
	 * bit clear and the stack uses numbers with the high bit set.
	 *
	 * @return uint8_t The next device request sequence number
	 */
	private int nextZDORequestSequence()
	{
	    this.zdoRequestSequence = ++this.zdoRequestSequence & APPLICATION_ZDO_SEQUENCE_MASK;
	    return this.zdoRequestSequence;
	}
	
	@Override
	public ZDOResp sendZdo(String ieee_address, int nwk_addr, ZDOCluster cluster, boolean disableResponse)
		throws Exception
	{
        // `authentication`: TC significance always 1 (zb specs)
        byte[] payload = cluster.packToReq(this.hasZdoMessageOverhead);
        
//		ZCLCluster cluster = clusterobj.getZDOClusterId() ;//ZCLClusterManager.getInstance().getCluserByID(clusterId) ;
//		if(cluster==null)
//			throw new Exception("no cluster found with id="+clusterId) ;
		
	        String clusterName = cluster.getClusterName() ;
	        int messageTag = this.nextZDORequestSequence();
	        payload[0] = (byte)messageTag;
	        ApsFrame apsf = new ApsFrame() ;
	        apsf. profileId = ZDOConsts.ZDO_PROFILE_ID;
	        apsf.clusterId = cluster.getClusterId();
	        apsf.src_ep =  ZDOConsts.ZDO_ENDPOINT;
	        apsf.dst_ep = ZDOConsts.ZDO_ENDPOINT;
	         apsf.options = DEFAULT_APS_OPTIONS ;
	         apsf.groupId = 0;
	         apsf.sequence = 0; // set by stack
	         
	         SLStatus status=  null ;
	        int apsSequence;
	
	        if (ZSpecUtil.isBroadcastAddress(nwk_addr))
	        {
	            log.debug("[ZDO "+clusterName+" BROADCAST to="+nwk_addr+" messageTag="+messageTag+" payload=");
	
	            //ebm, alias_addr, dest_addr, nwk_seq, aps_frm, radius, msg_tag, msg_cont)
	            SendRsp rsp = MsgRR.ezspSendBroadcast(ebm,
	                ZSpecConsts.NULL_NODE_ID, // alias
	                nwk_addr, 0, // nwkSequence
	                apsf,ZDO_REQUEST_RADIUS,messageTag,payload);
	
	            status = rsp.getStatus() ;
	            int aps_seq = rsp.getSeq() ;
	            apsf.sequence = aps_seq;
	
	            log.debug("[SENT ZDO BROADCAST messageTag=${messageTag} apsSequence=${apsSequence} status=${SLStatus[status]}]");
	
	            if (status != SLStatus.OK) {
	                throw new Exception("[ZDO ${clusterName} BROADCAST to=${networkAddress} messageTag=${messageTag}] Failed to send request with status=${SLStatus[status]}.");
	            }
	        } else {
	            log.debug("[ZDO ${clusterName} UNICAST to=${ieeeAddress}:${networkAddress} messageTag=${messageTag} payload=");
	            SendRsp rsp = MsgRR.ezspSendUnicast(ebm,OutgoingMsgType.DIRECT,
	                    nwk_addr,apsf,messageTag,payload);
	            status = rsp.getStatus() ;
	            int aps_seq = rsp.getSeq() ;
	            apsf.sequence = aps_seq;
	
	            log.debug("[SENT ZDO UNICAST messageTag="+messageTag+" apsSequence="+aps_seq+" status="+status);
	
	            if (status != SLStatus.OK) {
	                throw new Exception("[ZDO "+clusterName+" UNICAST to=${ieeeAddress}:${networkAddress} messageTag=${messageTag}] Failed to send request with status="+status);
	            }
	        }
	
	        if (!disableResponse) {
	        	ZDOClusterId resp_cid = ZDOUtil.getRespClusterId(cluster.getZDOClusterId());
	            if (resp_cid!=null)
	            {
	            	EZSPWaiterZDO w_zdo = new EZSPWaiterZDO(resp_cid,this.hasZdoMessageOverhead,DEFAULT_REQUEST_TIMEOUT) ;
	            	EZSPRsp w_rsp = ebm.RT_synWaitFor(w_zdo) ;
	            	if(w_rsp!=null)
	            	{
	            		return w_zdo.getZDOResp() ;
	            	}
	            	//return w_zdo ;
	            }
	        }
	        return null ;
	}
	
	/**
	 * This function sets an EZSP policy.
	 * @param policyId
	 * @param decisionId Can be bitop
	 * @returns
	 */
	private SLStatus emberSetEzspPolicy(EzspPolicyId policyId ,int decision_id) throws Exception
	{
	    SLStatus status = SetPolicyRR.setPolicyVal(ebm, policyId,  decision_id) ;
	    if(log.isDebugEnabled())
	    	log.debug("[EzspPolicyId] SET '"+policyId+"' TO '"+decision_id+"' with status="+status);
	    return status;
	}
	
	/**
	 * Set the trust center policy bitmask using decision.
	 * @param decision
	 * @returns
	 */
	private SLStatus emberSetJoinPolicy(EmberJoinDecision decision) throws Exception
	{
	    int policy = EzspDecisionBitmask.DEFAULT_CONFIGURATION.getIntVal();
	
	    switch (decision) {
	        case USE_PRECONFIGURED_KEY: {
	            policy = EzspDecisionBitmask.ALLOW_JOINS.getIntVal() | EzspDecisionBitmask.ALLOW_UNSECURED_REJOINS.getIntVal();
	            break;
	        }
	        case ALLOW_REJOINS_ONLY: {
	            policy = EzspDecisionBitmask.ALLOW_UNSECURED_REJOINS.getIntVal();
	            break;
	        }
	        /*case EmberJoinDecision.SEND_KEY_IN_THE_CLEAR: {
	            policy = EzspDecisionBitmask.ALLOW_JOINS | EzspDecisionBitmask.ALLOW_UNSECURED_REJOINS | EzspDecisionBitmask.SEND_KEY_IN_CLEAR;
	            break;
	        }*/
	        default:
	        	break ;
	    }
	
	    return this.emberSetEzspPolicy(EzspPolicyId.TRUST_CENTER_POLICY, policy);
	}
	
	private void permitPreJoining(int seconds) throws Exception
	{
		if (seconds>0)
		{
	        String plaintextKey = EmberConsts.ZIGBEE_PROFILE_INTEROPERABILITY_LINK_KEY;
	        SLStatus impKeyStatus = SecRR.importTransientKey(ebm, ZSpecConsts.BLANK_EUI64_STR, plaintextKey,SecManFlag.NONE);
	
	        if (impKeyStatus != SLStatus.OK) {
	            throw new Exception("[ZDO] Failed import transient key with status="+impKeyStatus);
	        }
	
	        SLStatus setJPstatus = this.emberSetJoinPolicy(EmberJoinDecision.USE_PRECONFIGURED_KEY);
	
	        if (setJPstatus != SLStatus.OK) {
	            throw new Exception("[ZDO] Failed set join policy with status="+setJPstatus);
	        }
	    } else {
	        SecRR.clearTransientLinkKeys(ebm);
	
	        SLStatus setJPstatus = this.emberSetJoinPolicy(EmberJoinDecision.ALLOW_REJOINS_ONLY);
	
	        if (setJPstatus != SLStatus.OK) {
	            throw new Exception("[ZDO] Failed set join policy with status="+setJPstatus);
	        }
	    }
	}
	
	@Override
	public void permitJoin(int seconds, int nwk_addr) throws Exception
	{
		ZDOClusterId clusterId = ZDOClusterId.PERMIT_JOINING_REQUEST;
	    
	    if (nwk_addr>0)
	    {
	        // specific device that is not `Coordinator`
	    	this.permitPreJoining(seconds) ; 
	
	    	PermitJoiningReq pjr = new PermitJoiningReq(seconds,1,null) ;
	        // `authentication`: TC significance always 1 (zb specs)
	       // byte[] zdoPayload = pjr.packToReq(this.hasZdoMessageOverhead);
	
	        //const result = await this.sendZdo(ZSpec.BLANK_EUI64, networkAddress, clusterId, zdoPayload, false);
	        ZDOResp zdo_rsp = this.sendZdo(ZSpecConsts.BLANK_EUI64_STR, nwk_addr, pjr, false) ;
	        ZDOStatus zdo_status = null;
	        if(zdo_rsp!=null)
	        	zdo_status = zdo_rsp.getStatus() ;
	        /* v8 ignore start */
	        if (zdo_status!=ZDOStatus.SUCCESS) {
	            throw new Exception("permitJoint sendZDO respont error status="+zdo_status);
	        }
	        /* v8 ignore stop */
	    } else {
	        // coordinator-only (0), or all
	    	this.permitPreJoining(seconds) ; 
	
	    	SLStatus status = NwkRR.permitJoining(ebm, seconds);
	        if (status != SLStatus.OK) {
	            throw new Exception("[ZDO] Failed coordinator permit joining request with status="+status);
	        }
	
	        if(log.isDebugEnabled())
	        	log.debug("Permit joining on coordinator for "+seconds+" sec.");
	
	        // broadcast permit joining ZDO
	        if (nwk_addr<0) {
	            // `authentication`: TC significance always 1 (zb specs)
	        	PermitJoiningReq pjr = new PermitJoiningReq(seconds,1,null) ;
	        	this.sendZdo(ZSpecConsts.BLANK_EUI64_STR, BroadcastAddr.DEFAULT.val, pjr, true);
	        }
	    }
	}
	
	@Override
	public ZCLFrame sendZclFrameToEndpoint(String ieeeAddr, int networkAddress, int endpoint, ZCLFrmClusterCmd zclFrame, int timeout,
			boolean disableResponse, boolean disableRecovery, Integer sourceEndpoint, Integer profileId) throws Exception
	{
		int resp_cmdid = zclFrame.getCmdIDResp() ;
        Integer commandResponseId = null;

        if (resp_cmdid>=0 && disableResponse == false) {
            commandResponseId = resp_cmdid;
        }
        else if (!zclFrame.isDisableDefaultResp())
        {
            commandResponseId = DefaultRsp.CMD_ID; //Zcl.Foundation.defaultRsp.ID;
        }

        ApsFrame apsf = new ApsFrame();
        if (profileId != null) {
        	apsf.profileId = profileId;
        } else {
        	EndpointInfo matched = null;
            if (sourceEndpoint!=null)
            	matched = EndpointInfo.findEndpointInfo(sourceEndpoint) ;
            EndpointInfo chosen = (matched != null) ? matched : EndpointInfo.FIXED_ENDPOINTS.get(0);
            apsf.profileId = chosen.profileId;
        }
        
        apsf.clusterId=zclFrame.getClusterID();
        apsf.src_ep= sourceEndpoint>0?sourceEndpoint : EndpointInfo.FIXED_ENDPOINTS.get(0).endpoint;
        apsf.dst_ep = endpoint;
        apsf.options= DEFAULT_APS_OPTIONS;
        apsf.groupId= 0;
        apsf.sequence= 0; // set by stack

        // don't RETRY if no response expected
        if (commandResponseId == null)
        {
        	apsf.options &= ~EmberApsOption.RETRY.getIntVal();
        }

        // Zigbee Direct cluster, enable APS layer encryption
        ZCLCluster zcl_c = ZCLClusterManager.getInstance().getClusterByName("zigbeeDirectConfiguration") ;
        if (apsf.clusterId == zcl_c.getID()) //Zcl.Clusters.zigbeeDirectConfiguration.ID)
        {
        	apsf.options |= EmberApsOption.ENCRYPTION.getIntVal();
        }

        byte[] data = zclFrame.packFrame();

        //return await this.queue.execute<ZclPayload | undefined>(async () => {
        //    this.checkInterpanLock();

            log.debug("~~~> [ZCL to=${ieeeAddr}:${networkAddress} apsFrame=${JSON.stringify(apsFrame)} header=${JSON.stringify(zclFrame.header)}]");

            for (int i = 1; i <= QUEUE_MAX_SEND_ATTEMPTS; i++)
            {
            	SLStatus status = SLStatus.FAIL;

                try
                {
                   SendRsp send_rsp = this.ezsp.send(
                        OutgoingMsgType.DIRECT,
                        networkAddress,
                        apsf,
                        data,
                        0, // alias
                        0 // alias seq
                    );
                   if(send_rsp!=null)
                	   status = send_rsp.getStatus() ;
                }
                catch (Exception error)
                {
                    if (error instanceof EzspError) {
                        switch (((EzspError) error).getCode())
                        {
                            case NO_TX_SPACE: {
                                status = SLStatus.BUSY;
                                break;
                            }
                            case NOT_CONNECTED: {
                                status = SLStatus.NETWORK_DOWN;
                                break;
                            }
                        }
                    }
                }

                // `else if` order matters
                if (status == SLStatus.OK) {
                    break;
                }

                if (disableRecovery || i == QUEUE_MAX_SEND_ATTEMPTS) {
                    throw new Exception("~x~> [ZCL to=${ieeeAddr}:${networkAddress} apsFrame=${JSON.stringify(apsFrame)}] Failed to send request with status=${SLStatus[status]}.");
                }

                if (status == SLStatus.ZIGBEE_MAX_MESSAGE_LIMIT_REACHED || status == SLStatus.BUSY) {
                    EmberManager.sleep(QUEUE_BUSY_DEFER_MSEC);
                } else if (status == SLStatus.NETWORK_DOWN) {
                	EmberManager.sleep(QUEUE_NETWORK_DOWN_DEFER_MSEC);
                } else {
                    throw new Exception("~x~> [ZCL to=${ieeeAddr}:${networkAddress} apsFrame=${JSON.stringify(apsFrame)}] Failed to send request with status=${SLStatus[status]}.");
                }

                log.debug("~x~> [ZCL to=${ieeeAddr}:${networkAddress}] Failed to send request attempt ${i}/${QUEUE_MAX_SEND_ATTEMPTS} with status=${SLStatus[status]}.");
            }

            if (commandResponseId !=null)
            {
	            	EZSPWaiterZCL w_zcl = new EZSPWaiterZCL(networkAddress,apsf,commandResponseId,timeout) ;
	            	EZSPRsp w_rsp = ebm.RT_synWaitFor(w_zcl) ;
	            	if(w_rsp!=null)
	            	{
	            		return w_zcl.getRspZCLFrame() ;//.getZDOResp() ;
	            	}
	            	//return w_zdo ;
	            
//                // NOTE: aps sequence number will have been set by send function
//                const result = await this.oneWaitress.startWaitingFor<ZclPayload>(
//                    {
//                        target: networkAddress,
//                        apsFrame,
//                        zclSequence: zclFrame.header.transactionSequenceNumber,
//                        commandIdentifier: commandResponseId,
//                    },
//                    timeout,
//                );

               
            }
        // }, networkAddress);
            return null ;
	}
	
	@Override
	public void sendZclFrameToGroup(int groupID, ZCLFrmClusterCmd zclf, Integer sourceEndpoint, Integer profileId) throws Exception
	{
		ApsFrame apsf = new ApsFrame();
	        if (profileId != null) {
	        	apsf.profileId = profileId;
	        } else {
	        	EndpointInfo matched = null;
	            if (sourceEndpoint!=null)
	            	matched = EndpointInfo.findEndpointInfo(sourceEndpoint) ;
	            EndpointInfo chosen = (matched != null) ? matched : EndpointInfo.FIXED_ENDPOINTS.get(0);
	            apsf.profileId = chosen.profileId;
	        }
	        apsf.clusterId=zclf.getClusterID();
	        apsf.src_ep= sourceEndpoint>0?sourceEndpoint : EndpointInfo.FIXED_ENDPOINTS.get(0).endpoint;
	        apsf.dst_ep = 0xff;
	        apsf.options= DEFAULT_APS_OPTIONS;
	        apsf.groupId= groupID;
	        apsf.sequence= 0; // set by stack
	        
	        byte[] data = zclf.packFrame() ;


	           log.debug("~~~> [ZCL GROUP apsFrame=${JSON.stringify(apsFrame)} header=${JSON.stringify(zclFrame.header)}]");
	           SendRsp send_rsp = this.ezsp.send(
	                OutgoingMsgType.MULTICAST,
	                groupID, // not used with MULTICAST
	                apsf,
	                data,
	                0, // alias
	                0 // alias seq
	            );
	           
	           SLStatus status = send_rsp.getStatus() ;

	            if (status != SLStatus.OK) {
	                throw new Exception("~x~> [ZCL GROUP groupId="+groupID+" Failed to send with status="+status);
	            }

	            // NOTE: since ezspMessageSentHandler could take a while here, we don't block, it'll just be logged if the delivery failed
	            EmberManager.sleep(QUEUE_BUSY_DEFER_MSEC);
	}
	
	@Override
	public void sendZclFrameToAll(int endpoint, ZCLFrmClusterCmd zclf, Integer src_endpoint, BroadcastAddr destination,
			Integer profileId) throws Exception
	{
		ApsFrame apsf  = new ApsFrame();
		
		 if (profileId != null) {
	        	apsf.profileId = profileId;
	        } else {
	        	EndpointInfo matched = null;
	            if (src_endpoint!=null)
	            	matched = EndpointInfo.findEndpointInfo(src_endpoint) ;
	            EndpointInfo chosen = (matched != null) ? matched : EndpointInfo.FIXED_ENDPOINTS.get(0);
	            apsf.profileId = chosen.profileId;
	        }
	        apsf.clusterId=zclf.getClusterID();
	        apsf.src_ep= src_endpoint;
	        apsf.dst_ep = endpoint;
	        apsf.options= DEFAULT_APS_OPTIONS;
	        apsf.groupId= destination.val;
	        apsf.sequence= 0; // set by stack
	        

	            byte[] data = zclf.packFrame() ;


	            log.debug("~~~> [ZCL BROADCAST apsFrame=${JSON.stringify(apsFrame)} header=${JSON.stringify(zclFrame.header)}]");
	            SendRsp send_rsp = this.ezsp.send(
	                OutgoingMsgType.BROADCAST,
	                destination.val,
	                apsf,
	                data,
	                0, // alias
	                0 // alias seq
	            );
	            SLStatus status = send_rsp.getStatus() ;
	            if (status != SLStatus.OK) {
	                throw new Exception("~x~> [ZCL BROADCAST destination="+destination+"] Failed to send with status="+status);
	            }

	            // NOTE: since ezspMessageSentHandler could take a while here, we don't block, it'll just be logged if the delivery failed
	            EmberManager.sleep(QUEUE_BUSY_DEFER_MSEC);
	}
	
	@Override
	public void setChannelInterPAN(int channel) throws Exception
	{
        SLStatus status = ezsp.setLogicalAndRadioChannel(channel);

        if (status != SLStatus.OK)
        {
            throw new Exception("Failed to set InterPAN channel to '"+channel+"' with status="+status);
        }
	}
	
	@Override
	public void sendZclFrameInterPANToIeeeAddr(ZCLFrmClusterCmd zclFrame, String ieee_addr) throws Exception
	{
		EZSPBuffer buf = EZSPBuffer.alloc(EmberConsts.MAXIMUM_INTERPAN_LENGTH);

        // cache-enabled getters
        int sourcePanId = this.emberGetPanId();
        String sourceEui64 = this.emberGetIEEEAddr() ;

        buf.writeUInt16(EmberConsts.LONG_DEST_FRAME_CONTROL | EmberConsts.MAC_ACK_REQUIRED); // macFrameControl
        buf.writeUInt8(0); // sequence Skip Sequence number, stack sets the sequence number.
        buf.writeUInt16(ZSpecConsts.INVALID_PAN_ID); // destPanId
        buf.writeIEEEAddr(ieee_addr); // destAddress (longAddress)
        buf.writeUInt16(sourcePanId); // sourcePanId
        buf.writeIEEEAddr(sourceEui64); // sourceAddress
        buf.writeUInt16(EmberConsts.STUB_NWK_FRAME_CONTROL); // nwkFrameControl
        buf.writeUInt8(EmberInterpanMsgTp.UNICAST.getIntVal() | EmberConsts.INTERPAN_APS_FRAME_TYPE); // apsFrameControl
        buf.writeUInt16(zclFrame.getClusterID());
        buf.writeUInt16(ZSpecConsts.TOUCHLINK_PROFILE_ID);
        
        byte[] zclf_bs = zclFrame.packFrame() ;
        byte[] msg_bs = DataBuffer.concat(buf,zclf_bs) ;
        
        log.debug("~~~> [ZCL TOUCHLINK to="+ieee_addr+" header=${JSON.stringify(zclFrame.header)}]");
        SLStatus status = this.ezsp.sendRawMessage(
            msg_bs,//Buffer.concat([msgBuffalo.getWritten(), zclFrame.toBuffer()]),
            EmberTransmitPriority.NORMAL,
            true
        );

        if (status != SLStatus.OK) {
            throw new Exception("~x~> [ZCL TOUCHLINK to="+ieee_addr+"] Failed to send with status="+status);
        }
	}
	
	@Override
	public ZCLFrame sendZclFrameInterPANBroadcast(ZCLFrmClusterCmd zclFrame, int timeout)
	{
		return null;
	}
	
	@Override
	public ZCLFrame sendZclFrameInterPANBroadcast(ZCLFrmClusterCmd zclf, int timeout, boolean disableResponse)
		throws Exception
	{
		int resp_cmdid = zclf.getCmdIDResp() ;

        if (!disableResponse && resp_cmdid<0) {
            throw new Exception("Command '"+zclf.getCmdName()+" has no response, cannot wait for response.");
        }

        int endpoint = EndpointInfo.FIXED_ENDPOINTS.get(0).endpoint;
        // just for waitress
        ApsFrame apsf = new ApsFrame();
        apsf.profileId = ZSpecConsts.TOUCHLINK_PROFILE_ID;
        apsf.clusterId = zclf.getClusterID();
        apsf.src_ep = endpoint; // arbitrary since not sent over-the-air
        apsf.dst_ep = endpoint;
        apsf.options = EmberApsOption.NONE.getIntVal();
        apsf.groupId = BroadcastAddr.SLEEPY.val;
        apsf.sequence = 0; // set by stack

        //return await this.queue.execute<ZclPayload | undefined>(async () => {
        EZSPBuffer buf = EZSPBuffer.alloc(EmberConsts.MAXIMUM_INTERPAN_LENGTH) ;

        // cache-enabled getters
        int sourcePanId = this.emberGetPanId();
        String sourceEui64 = this.emberGetIEEEAddr();

        buf.writeUInt16(EmberConsts.SHORT_DEST_FRAME_CONTROL); // macFrameControl
        buf.writeUInt8(0); // sequence Skip Sequence number, stack sets the sequence number.
        buf.writeUInt16(ZSpecConsts.INVALID_PAN_ID); // destPanId
        buf.writeUInt16(apsf.groupId); // destAddress (longAddress)
        buf.writeUInt16(sourcePanId); // sourcePanId
        buf.writeIEEEAddr(sourceEui64); // sourceAddress
        buf.writeUInt16(EmberConsts.STUB_NWK_FRAME_CONTROL); // nwkFrameControl
        buf.writeUInt8(EmberInterpanMsgTp.BROADCAST.getIntVal() | EmberConsts.INTERPAN_APS_FRAME_TYPE); // apsFrameControl
        buf.writeUInt16(apsf.clusterId);
        buf.writeUInt16(apsf.profileId);

        byte[] zclf_bs = zclf.packFrame() ;
        byte[] data = DataBuffer.concat(buf,zclf_bs) ;
        
         log.debug("~~~> [ZCL TOUCHLINK BROADCAST header=${JSON.stringify(zclFrame.header)}]");
         SLStatus status = this.ezsp.sendRawMessage(data, EmberTransmitPriority.NORMAL, true);

            if (status != SLStatus.OK) {
                throw new Exception("~x~> [ZCL TOUCHLINK BROADCAST] Failed to send with status="+status);
            }

            // NOTE: can use ezspRawTransmitCompleteHandler if needed here

            if (!disableResponse && resp_cmdid>=0)
            {
            	int seq_n = zclf.getTransSeqNum() ;
            	EZSPWaiterZCL w_zcl = new EZSPWaiterZCL(-1,apsf,resp_cmdid,timeout)
            			.asZclSequence(seq_n);
            	EZSPRsp w_rsp = ebm.RT_synWaitFor(w_zcl) ;
            	if(w_rsp!=null)
            	{
            		return w_zcl.getRspZCLFrame() ;//.getZDOResp() ;
            	}
            	
//                const result = await this.oneWaitress.startWaitingFor<ZclPayload>(
//                    {
//                        target: undefined,
//                        apsFrame: apsFrame,
//                        zclSequence: zclFrame.header.transactionSequenceNumber,
//                        commandIdentifier: command.response,
//                    },
//                    timeout,
//                );
            }
        
		return null;
	}
	
	@Override
	public void restoreChannelInterPAN() throws Exception
	{
		int ch = this.settings.getChannelList().get(0) ;
		SLStatus status = this.ezsp.setLogicalAndRadioChannel(ch);

        if (status != SLStatus.OK) {
            throw new Exception("Failed to restore InterPAN channel to '"+this.settings.getChannelList().get(0)+"${this.networkOptions.channelList[0]}' with status="+status);
        }
        // let adapter settle down
        EmberManager.sleep(QUEUE_NETWORK_DOWN_DEFER_MSEC);
	}
}
