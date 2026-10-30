package org.iottree.driver.zigbee.zspec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zcl.ZCLAttr;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLCmd;
import org.iottree.driver.zigbee.zcl.ZCLParam;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLStatus;
import org.iottree.driver.zigbee.zcl.util.DataVal;
import org.iottree.driver.zigbee.zcl.util.TagDef;
import org.iottree.driver.zigbee.zspec.event.ZclPayload;
import org.iottree.driver.zigbee.zspec.loc.LocEDFacModel;
import org.iottree.driver.zigbee.zspec.loc.LocManager;
import org.iottree.driver.zigbee.zspec.types.InterviewState;
import org.iottree.driver.zigbee.zspec.types.ZSDeviceType;
import org.iottree.driver.zigbee.zspec.util.EndDeviceCap;
import org.iottree.driver.zigbee.zspec.util.PowerSrc;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.json.JSONArray;
import org.json.JSONObject;

public  class ZSDevice implements Comparable<ZSDevice>
{
	protected String ieee_addr ; // unique id
	
	protected ZSDeviceType dev_tp = ZSDeviceType.Unknown ;
	
	protected String title = null ; // set by user
	
	protected ShortAddr short_addr ; //设备重新入网可能会有变化
	
	protected PowerSrc powerSrc = null ;
	
	protected long findDT = -1 ;
	
	protected long updateDT = -1 ;
	
	protected InterviewState interview_st = null;
	
	//通信链路质量
	protected transient int linkQual = -1 ;
	protected transient long linkDT = -1 ;
	
	protected int facCode = -1 ; //生产厂商代码
	protected String facName = null ; // 生产厂商
	protected String facModel =  null ;//生产型号
	
	protected String gp_security_key = null ;
	
	protected EndDeviceCap endDevCap = null ;
	
	protected HashMap<Integer,ZSEndPoint> endpoints = new HashMap<>() ;
	
	private transient ZSCoor belongTo ;
	
	public ZSDevice(ZSCoor belongto,String ieeeaddr)
	{
		this.belongTo = belongto ;
		this.ieee_addr = ieeeaddr ;
	}
	
	public ZSDevice asBasic(ZSDeviceType dtp,String title,int addr,PowerSrc pow_src,
			int fac_code,String fac_n,String fac_model,InterviewState intv_st,String gp_security_key)
	{
		this.dev_tp = dtp ;
		this.title = title ;
		this.short_addr = new ShortAddr(addr) ;
		this.powerSrc = pow_src ;
		this.facCode = fac_code ;
		this.facName = fac_n ;
		this.facModel = fac_model;
		this.interview_st = intv_st ;
		this.gp_security_key = gp_security_key ;
		return this ;
	}

	public long getFindDT()
	{
		return this.findDT ;
	}
	
	public ZSCoor getBelongTo()
	{
		return this.belongTo ;
	}
	
	public UADev getRelatedUADev()
	{
		if(Convert.isNullOrEmpty(this.ieee_addr))
			return null ;
		UACh ch = this.belongTo.getDriver().getBelongToCh() ;
		if(ch==null)
			return null ;
		UANode n = ch.getSubNodeByOuterId(this.ieee_addr) ;
		if(n==null || !(n instanceof UADev))
			return null ;
		return (UADev)n ;
	}
	

	public List<ZSAttrVal> getDisplayAttrVals()
	{
		ArrayList<ZSAttrVal> rets = new ArrayList<>() ;
		for(ZSEndPoint ep:this.listEndPoints())
		{
			List<Integer> ids = ep.getInClusterIds() ;
			if(ids==null)
				continue ;
			for(Integer id:ids)
			{
				ZSCluster zsc = ep.getInCluster(id) ;
				if(zsc==null)
					continue ;
				ZCLCluster c = zsc.getClusterDef() ;
				if(c==null)
					continue ;
				for(ZCLAttr attrdef : c.getIdAttributes().values())
				{
					TagDef tagd = attrdef.getTagDef() ;
					if(tagd==null)
						continue ;
					if(!tagd.getDis())
						continue ;
					int attrid = attrdef.getID() ;
					ZSAttrVal zav = zsc.getAttrVal(attrid) ;
					if(zav==null)
						continue ;
					rets.add(zav) ;
				}
			}
		}
		return rets ;
	}
	
	
	
	public void setFindDT(long t)
	{
		this.findDT = t ;
	}
	
	public long getUpdateDT()
	{
		return this.updateDT ;
	}
	
	public void setUpdateDT(long t)
	{
		this.updateDT = t ;
	}
	
	public void setLinkQual(int lq)
	{
		this.linkQual = lq ;
		this.linkDT = System.currentTimeMillis() ;
	}
	
	public int getLinkQual()
	{
		return this.linkQual ;
	}
	
	public long getLinkDT()
	{
		return this.linkDT ;
	}
	
	public int getFacCode()
	{
		return this.facCode;
	}
	
	public String getFacName()
	{
		if(this.facName==null)
			return "" ;
		return this.facName ;
	}
	
	public String getFacModel()
	{
		if(this.facModel==null)
			return "" ;
		return this.facModel ;
	}
	
	
	/**
	 * 设备唯一地址
	 * 
	 * @return
	 */
	public String getIEEEAddr()
	{
		return this.ieee_addr ;
	}
	
	public ShortAddr getShortAddr()
	{
		return this.short_addr ;
	}
	
	public void setShortAddr(ShortAddr sa)
	{
		this.short_addr = sa ;
	}
	
	public EndDeviceCap getDeviceCap()
	{
		return this.endDevCap ;
	}
	
	public boolean updateBy(ShortAddr addr,EndDeviceCap cap)
	{
		this.short_addr = addr ;
		this.endDevCap = cap ;
		this.updateDT = System.currentTimeMillis() ;
		return true;
	}
	
	public boolean updateFacCode(int fc)
	{
		if(fc==this.facCode)
			return false;
		
			this.facCode = fc ;
		
		this.updateDT = System.currentTimeMillis() ;
		return true ;
	}

	public List<ZSEndPoint> listEndPoints()
	{
		ArrayList<ZSEndPoint> rets = new ArrayList<>() ;
		rets.addAll(this.endpoints.values()) ;
		return rets ;
	}
	
	public ZSEndPoint getEndPoint(int epid)
	{
		return this.endpoints.get(epid) ;
	}
	

	public List<ZSAttrVal> getTagDefAttrs()
	{
		ArrayList<ZSAttrVal> rets = new ArrayList<>() ;
		for(ZSEndPoint ep : this.listEndPoints())
		{
			for(int cid:ep.getInClusterIds())
			{
				ZSCluster zsc = ep.getInCluster(cid);
				if(zsc==null)
				{
					continue ;
				}
				for(ZSAttrVal av:zsc.listAttrVals())
				{
					TagDef td = av.getTagDef() ;
					if(td==null)
						continue ;
					rets.add(av) ;
				}
			}
		}
		return rets ;
	}
	
	public boolean hasTagDefAttrs()
	{
		return getTagDefAttrs().size()>0 ;
	}
	

	private int setupEndDevTags(JSONArray ret)
	{
		// main power 供电不缺的end device，添加设备状态信息
		PowerSrc ps = this.getPowerSrc() ;
		if(ps!=null&& ps.isNoLimit())
		{
			TagDef td = new TagDef("ed_no_rsp_n","ED No Rsp Num","int32",false,"0",null) ;
			ret.put(td.toTagJO()) ;
			td = new TagDef("ed_no_rsp_sec","ED No Rsp Seconds","int32",false,"0",null) ;
			ret.put(td.toTagJO()) ;
			td = new TagDef("ed_lost","ED Lost or Not","bool",false,"false",null) ;
			ret.put(td.toTagJO()) ;
			return 3 ;
		}
		else
		{
			return 0 ;
		}
	}
	

	public PowerSrc getPowerSrc()
	{
		if(this.powerSrc==PowerSrc.unknown)
		{
			LocEDFacModel fm = LocManager.getInstance().getFacModel(this.getFacName(),this.getFacModel()) ;
			if(fm!=null)
			{
				PowerSrc ps = fm.getPowerSrcDef() ;
				if(ps!=null)
					this.powerSrc = ps ;
			}
		}
		return this.powerSrc ;
	}
	
	public String getPowerIco()
	{
		PowerSrc ps = getPowerSrc() ;
		if(ps==null)
			return "<i class=\"fa-regular fa-circle-question\" style='color:red'></i>";
		switch(ps)
		{
		case mains1:
		case mains3:
			return "<i class=\"fa-solid fa-plug\" style='color:#43a72d'></i>" ;
		case battery:
			//<i class="fa-solid fa-battery-empty"></i>
			//<i class="fa-solid fa-battery-quarter"></i>
			//<i class="fa-solid fa-battery-half"></i>
			//<i class="fa-solid fa-battery-three-quarters"></i>
			return "<i class='fa-solid fa-battery-full' style='color:#43a72d'></i>" ;
		case dc:
			return "<i class=\"fa-solid fa-car-battery\" style='color:#43a72d'></i>" ;
		case em_mains_const:
			return "<i class=\"fa-solid fa-plug\" style='color:yellow'></i>" ;
		case em_mains_switch:
			return "<i class=\"fa-solid fa-plug\" style='color:red'></i>" ;
		default:
			return "<i class=\"fa-regular fa-circle-question\" style='color:red'></i>";
		}
	}
	
	public String getTitle()
	{
		if(title==null)
			return "" ;
		return this.title ;
	}
	
	public void setTitle(String t)
	{
		this.title = t ;
	}

	//for syn tags in UADev
	public int setupTagsJArr(JSONArray ret)
	{
		int r = 0 ;
		r += setupEndDevTags(ret) ;
		
		for(ZSEndPoint zep:this.endpoints.values())
		{
			r += zep.setupTagsJArr(ret) ;
		}
		return r;
	}
	
	/**
	 * EndDevice没有任何反映计数
	 */
	private transient int noRespErrNum = 0 ;
	
	/**
	 * 第一次出现没应答错误时间
	 */
	private transient long noRespFirstDT = -1 ;
	
	public int getNoRespErrNum()
	{
		return noRespErrNum ;
	}
	
	public int getNoRespSec()
	{
		return (int)((System.currentTimeMillis()-this.noRespFirstDT)/1000) ;
	}
	
	public boolean getNoRespLost()
	{
		return this.noRespErrNum>=3 ;
	}
	

	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject().putOpt("ieee_addr", this.ieee_addr).putOpt("title",this.title)
				.put("find_dt", this.findDT)
				.put("update_dt", this.updateDT);
		if(this.short_addr!=null)
			ret.putOpt("short_addr",short_addr.getAddrVal());
		if(this.endDevCap!=null)
		{
			ret.put("cap_v", this.endDevCap.getCapVal()) ;
			ret.put("cap", this.endDevCap.toJO()) ;
		}
		
		ret.put("link_qual", this.linkQual) ;
		ret.put("link_dt", this.linkDT) ;
		
		ret.put("fac_code",this.facCode) ;
		ret.putOpt("fac_name", this.facName) ;
		ret.putOpt("fac_model", this.facModel) ;
		if(this.powerSrc!=null)
			ret.put("power_src", this.powerSrc.getValInt()) ;
		
		if(this.endpoints!=null)
		{
			JSONArray jarr = new JSONArray() ;
			ret.put("endpoints",jarr) ;
			for(ZSEndPoint ep:this.endpoints.values())
			{
				jarr.put(ep.toJO()) ;
			}
		}
		
		return ret ;
	}
	
	public JSONObject RT_toJO()
	{
		JSONObject ret = new JSONObject().putOpt("ieee_addr", this.ieee_addr)
				.put("update_dt", this.updateDT)
				.put("link_qual", this.linkQual)
				.put("no_resp_num",this.getNoRespErrNum())
				.put("no_resp_sec", this.getNoRespSec())
				.put("no_resp_lost", this.getNoRespLost());
		List<ZSAttrVal> avs = this.getDisplayAttrVals() ;
		JSONArray jarr = new JSONArray() ;
		ret.put("attr_vals",jarr) ;
		if(avs!=null)
		{
			for(ZSAttrVal av:avs)
			{
				jarr.put(av.RT_toJO()) ;
			}
		}
		return ret ;
	}
	
	private transient long lastRun = System.currentTimeMillis();
	/**
	 * 被coor RT_onCoorNorRunning定时调用，更新对应的属性值
	 * 
	 * 如果发现EndDevice出问题，需要自动降级运行。而Coor需要统一调用（自动reset等）
	 * 
	 * @throws Exception
	 */
	void RT_runInLoop()
	{
		if(System.currentTimeMillis()-lastRun<5000*(noRespErrNum+1))
			return ; //自动降级
		
		try
		{
			// RT_runAttrs();
			
			RT_updateEDTags() ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			lastRun = System.currentTimeMillis();
		}
	}
	
	private void RT_updateEDTags()
	{
		UADev dev = this.getRelatedUADev() ;
		if(dev==null)
			return ;
		UATag tag = dev.getTagByName("ed_no_rsp_n") ;
		if(tag!=null)
			tag.RT_setValRaw(this.noRespErrNum) ;
		tag = dev.getTagByName("ed_no_rsp_sec") ;
		if(tag!=null)
		{
			if(this.noRespFirstDT<=0)
				tag.RT_setValRaw(-1) ;
			else
				tag.RT_setValRaw(this.getNoRespSec()) ;
		}
		 tag = dev.getTagByName("ed_lost") ;
		if(tag!=null)
			tag.RT_setValRaw(this.getNoRespLost()) ;
	}
/*
	private void RT_runAttrs() throws Exception
	{
		PowerSrc ps = this.getPowerSrc() ;
		if(ps==null|| !ps.isNoLimit())
		{
			return ;
		}
		
		int r = 0 ;
		// RT_runAttrs
		boolean has_read = false;
		int total_n = 0 ;
		int err_n = 0 ;
		for(ZSEndPoint zsep:this.endpoints.values())
		{
			List<ZSCluster> clusters = zsep.getInClusters() ;
			if(clusters==null||clusters.size()<=0)
				continue ;
			
			r = 1 ;
			total_n += clusters.size() ;
			for(ZSCluster zsc:clusters)
			{
				StringBuilder failedr  = new StringBuilder() ;
				int epid = zsep.getEndPointId() ;
				int res = this.belongTo.RT_readAttrs(epid,zsc.getClusterId(),failedr) ;
				if(res<0)
				{
//					if(log.isDebugEnabled())
//						getLogger().debug(this.ieee_addr+" RT_runAttrs (ep="+epid+" cluster="+zsc.getClusterId()+") err:"+failedr.toString());
					r = -1 ;
					err_n ++ ;
				}
				else if(res>0)
				{
					has_read = true;
				}
				else if(res==0)
				{
					total_n -- ;
				}
			}
		}
		if(err_n>0 && err_n==total_n) //all error
		{
			if(this.noRespFirstDT<0)
				this.noRespFirstDT = System.currentTimeMillis() ;
			noRespErrNum ++ ;
			if(this.noRespErrNum>10)
				this.noRespErrNum = 10 ;
		}
		else
		{
			noRespErrNum = 0 ;
			this.noRespFirstDT = -1 ;
		}
	}
*/
//	/**
//	 * coor 主动发起读取cluster定义好的(带__tag定义)的所有属性值
//	 * @param endpoint
//	 * @param clusterid
//	 * @return -100=read err in driver
//	 * @throws Exception 
//	 */
//	public abstract int RT_readAttrs(int endpoint,int clusterid,StringBuilder failedr) throws Exception;
	

	public void RT_init_tag_default_vals()
	{
		UADev dev = getRelatedUADev() ;
		if(dev!=null)
		{// init related tags
			for(UATag tag:dev.listTagsNorAll())
			{
				UAVal uav = tag.RT_getVal() ;
				if(uav!=null && uav.isValid())
					continue ;
				
				StringBuilder failedr = new StringBuilder() ;
				DevAddr da = tag.getDevAddr(false,failedr) ;
				if(da==null || !(da instanceof ZSAddr))
					continue ;
				ZSAddr zsaddr = (ZSAddr)da;
				if(zsaddr.isAddrCmd())
				{
					ZCLCmd cmd = zsaddr.getCmdDef() ;
					if(cmd==null)
						continue ;
					TagDef tagd = cmd.getTagDef() ;
					String def_v = tagd.getDefaultStr() ;
					if(Convert.isNotNullEmpty(def_v))
						tag.RT_setValRawStr(def_v);
				}
				else if(zsaddr.isAddrAttr())
				{
					ZCLAttr attr = zsaddr.getAttrDef() ;
					if(attr==null)
						continue ;
					TagDef tagd = attr.getTagDef() ;
					String def_v = tagd.getDefaultStr() ;
					if(Convert.isNotNullEmpty(def_v))
						tag.RT_setValRawStr(def_v);
				}
				else if(zsaddr.isAddrCmdPm())
				{
					ZCLParam pm = zsaddr.getCmdPmDef() ;
					if(pm==null)
						continue ;
					TagDef tagd = pm.getTagDef() ;
					String def_v = tagd.getDefaultStr() ;
					if(Convert.isNotNullEmpty(def_v))
						tag.RT_setValRawStr(def_v);
				}
			}
		}
	}
	
	/**
	 * ZCLCluster ID = 0x0000  Name = Basic
| AttrId | 名称               | 类型     |
| ------  | ---------------- | ------ |
| 0x0000 | zclVersion       | uint8  |
| 0x0001 | appVersion       | uint8  |
| 0x0002 | stackVersion     | uint8  |
| 0x0003 | hwVersion        | uint8  |
| 0x0004 | manufacturerName | string |
| 0x0005 | modelId          | string |
| 0x0006 | dateCode         | string |
| 0x0007 | powerSource      | enum8  |
| 0x4000 | swBuildId        | string |
	 */
	public int MGR_updateByClusterBasic(HashMap<Integer,DataVal> attr2val)
	{
		int r = 0 ;
		DataVal dv = attr2val.get(0x0004) ; //
		if(dv!=null)
		{
			String v = dv.toValString() ;
			if(Convert.isNotNullEmpty(v) && !v.equals(this.facName))
			{
				this.facName = v;
				r ++ ;
			}
		}
		dv = attr2val.get(0x0005) ;
		if(dv!=null)
		{
			String v = dv.toValString() ;
			if(Convert.isNotNullEmpty(v) && !v.equals(this.facModel))
			{
				this.facModel = v ;
				r ++ ;
			}
		}
		
		dv = attr2val.get(0x0007) ;
		if(dv!=null)
		{
			Object v = dv.getObjVal() ;
			int pow = -1 ;
			if(v instanceof Number)
				pow = ((Number)v).intValue() ;
			PowerSrc ps = PowerSrc.valOfInt(pow) ;
			if(ps!=null && powerSrc!=ps)
			{
				this.powerSrc = ps ;
				r ++ ;
			}
		}
		
		this.updateDT = System.currentTimeMillis() ;
		return r ;
	}
	

	
	public void MGR_setTitle(String t)
	{
		this.title = t;
	}
	
	@Override
	public int compareTo(ZSDevice o)
	{
		if(this.getFindDT()>o.getFindDT())
			return -1 ;
		else if(this.getFindDT()==o.getFindDT())
			return 0 ;
		else
			return 1 ;
	}
	
	/**
	 * 更新设备支持的endpoints
	 * @param eps
	 * @return
	 */
	public boolean MGR_updateActiveEPs(List<Integer> eps)
	{
		System.out.println("ED="+this.ieee_addr+" eps="+Convert.combineWith(eps, ',')) ;
		if(eps==null||eps.size()<=0)
			return false;
		boolean bdirty = false;
		for(Integer epid:eps)
		{
			ZSEndPoint ep = endpoints.get(epid) ;
			if(ep==null)
			{
				ep = new ZSEndPoint(this,epid) ;
				endpoints.put(epid, ep) ;
				bdirty = true;
			}
		}
		
		this.updateDT = System.currentTimeMillis() ;
		return bdirty ;
	}
	
	public void MGR_removeFromNetwork(boolean b_clear_cache)
	{
//	        if (this._type === "GreenPower") {
//	            const payload = {
//	                options: 0x002550,
//	                srcID: Number(this.ieeeAddr),
//	            };
//	            const frame = Zcl.Frame.create(
//	                Zcl.FrameType.SPECIFIC,
//	                Zcl.Direction.SERVER_TO_CLIENT,
//	                true,
//	                undefined,
//	                zclTransactionSequenceNumber.next(),
//	                "pairing",
//	                33,
//	                payload,
//	                this.customClusters,
//	            );
//
//	            await Entity.adapter.sendZclFrameToAll(242, frame, 242, BroadcastAddress.RX_ON_WHEN_IDLE);
//	        } else {
//	            const clusterId = Zdo.ClusterId.LEAVE_REQUEST;
//	            const zdoPayload = Zdo.Buffalo.buildRequest(
//	                Entity.adapter.hasZdoMessageOverhead,
//	                clusterId,
//	                this.ieeeAddr as Eui64,
//	                Zdo.LeaveRequestFlags.WITHOUT_REJOIN,
//	            );
//	            const response = await Entity.adapter.sendZdo(this.ieeeAddr, this.networkAddress, clusterId, zdoPayload, false);
//
//	            if (!Zdo.Buffalo.checkStatus<Zdo.ClusterId.LEAVE_RESPONSE>(response)) {
//	                throw new Zdo.StatusError(response[0]);
//	            }
//	        }
//
//	        this.removeFromDatabase(clearCache);

	}
	
	public void RT_onZclData(ZclPayload dataPayload,
			ZCLFrame frame,ZSEndPoint endpoint,ZCLStatus defaultResponse)
	{
	        if (this.dev_tp ==ZSDeviceType.GreenPower) {
	            // nothing below applies
	            return;
	        }

	       
//	        const {header, command, cluster} = frame;
//	        let sendDefaultResponse = !dataPayload.wasBroadcast && command.response === undefined;
//	        let defaultResponseStatus = defaultResponse ?? Zcl.Status.SUCCESS;
//
//	        if (frame.isGlobal) {
//	            // Response to read requests from device to coordinator
//	            switch (command.name) {
//	                case "read": {
//	                    // NOTE: `sendDefaultResponse` always false from `command.response === 0x01`
//
//	                    if (this._customReadResponse?.(frame, endpoint)) {
//	                        break;
//	                    }
//
//	                    const response: KeyValue = {};
//
//	                    switch (dataPayload.clusterID) {
//	                        case GEN_TIME_CLUSTER_ID: {
//	                            // relax type to index by attr name, undefined results in non-success attr record
//	                            const timeAttrs = timeService.getTimeClusterAttributes() as Record<string, unknown>;
//
//	                            for (const entry of frame.payload) {
//	                                // TODO: this.manufacturerID or frame.header.manufacturerCode
//	                                const name = Zcl.Utils.getClusterAttribute(cluster, entry.attrId, this.manufacturerID)?.name;
//
//	                                if (name === undefined) {
//	                                    // UNSUPPORTED_ATTRIBUTE
//	                                    response[entry.attrId] = {value: undefined, type: Zcl.DataType.NO_DATA};
//	                                } else {
//	                                    response[name] = timeAttrs[name];
//	                                }
//	                            }
//	                            break;
//	                        }
//	                        // NOTE: can add more clusters here to use defaults from spec as needed
//	                        case GEN_BASIC_CLUSTER_ID: {
//	                            for (const entry of frame.payload) {
//	                                // TODO: this.manufacturerID or frame.header.manufacturerCode
//	                                const attr = Zcl.Utils.getClusterAttribute(cluster, entry.attrId, this.manufacturerID);
//
//	                                if (attr?.default === undefined) {
//	                                    // UNSUPPORTED_ATTRIBUTE
//	                                    response[entry.attrId] = {value: undefined, type: Zcl.DataType.NO_DATA};
//	                                } else {
//	                                    response[attr.name] = attr.default;
//	                                }
//	                            }
//
//	                            break;
//	                        }
//	                        default: {
//	                            for (const entry of frame.payload) {
//	                                // UNSUPPORTED_ATTRIBUTE
//	                                response[entry.attrId] = {value: undefined, type: Zcl.DataType.NO_DATA};
//	                            }
//
//	                            break;
//	                        }
//	                    }
//
//	                    try {
//	                        await endpoint.readResponse(cluster.ID, header.transactionSequenceNumber, response, {
//	                            srcEndpoint: dataPayload.destinationEndpoint,
//	                        });
//	                    } catch (error) {
//	                        logger.error(`Read response to ${this.ieeeAddr} failed (${(error as Error).message})`, NS);
//	                        // XXX: technically, if `readResponse` fails before reaching the network (internal to ZH), we should send a default response
//	                        //      currently not possible due to implementation (no distinction as to "where" it failed)
//	                    }
//
//	                    break;
//	                }
//	                case "defaultRsp": {
//	                    sendDefaultResponse = false; // per spec
//	                    break;
//	                }
//	            }
//	        } else if (header.isSpecific) {
//	            switch (cluster.name) {
//	                case "ssIasZone": {
//	                    if (command.name === "enrollReq") {
//	                        // Respond to enroll requests
//	                        logger.debug(`IAS - '${this.ieeeAddr}' responding to enroll response`, NS);
//
//	                        try {
//	                            await endpoint.command(
//	                                "ssIasZone",
//	                                "enrollRsp",
//	                                {enrollrspcode: 0, zoneid: 23},
//	                                {transactionSequenceNumber: header.transactionSequenceNumber, disableDefaultResponse: true},
//	                            );
//
//	                            sendDefaultResponse = false; // per spec, sending a specific response TODO: no "Effect on receipt" in spec, is this correct?
//	                        } catch (error) {
//	                            logger.error(`Handling of IAS zone enroll for ${this.ieeeAddr} failed (${(error as Error).message})`, NS);
//	                            defaultResponseStatus = Zcl.Status.FAILURE;
//	                        }
//	                    }
//	                    break;
//	                }
//	                case "genPollCtrl": {
//	                    if (command.name === "checkin") {
//	                        let startedFastPolling = false;
//
//	                        // Handle check-in from sleeping end devices
//	                        try {
//	                            if (this.hasPendingRequests() || this._checkinInterval === undefined) {
//	                                logger.debug(`check-in from ${this.ieeeAddr}: accepting fast-poll`, NS);
//	                                await endpoint.command(
//	                                    cluster.name as "genPollCtrl",
//	                                    "checkinRsp",
//	                                    {startFastPolling: 1, fastPollTimeout: 0},
//	                                    {
//	                                        transactionSequenceNumber: header.transactionSequenceNumber,
//	                                        disableDefaultResponse: true,
//	                                        sendPolicy: "immediate",
//	                                    },
//	                                );
//	                                startedFastPolling = true;
//
//	                                // This is a good time to read the checkin interval if we haven't stored it previously
//	                                if (this._checkinInterval === undefined) {
//	                                    const pollPeriod = await endpoint.read("genPollCtrl", ["checkinInterval"], {sendPolicy: "immediate"});
//	                                    this._checkinInterval = pollPeriod.checkinInterval / 4; // convert to seconds
//	                                    this.resetPendingRequestTimeout();
//	                                    logger.debug(
//	                                        `Request Queue (${this.ieeeAddr}): default expiration timeout set to ${this.pendingRequestTimeout}`,
//	                                        NS,
//	                                    );
//	                                }
//
//	                                await Promise.all(this.endpoints.map(async (e) => await e.sendPendingRequests(true)));
//	                            } else {
//	                                logger.debug(`check-in from ${this.ieeeAddr}: declining fast-poll`, NS);
//	                                await endpoint.command(
//	                                    cluster.name as "genPollCtrl",
//	                                    "checkinRsp",
//	                                    {startFastPolling: 0, fastPollTimeout: 0},
//	                                    {
//	                                        transactionSequenceNumber: header.transactionSequenceNumber,
//	                                        disableDefaultResponse: true,
//	                                        sendPolicy: "immediate",
//	                                    },
//	                                );
//	                            }
//
//	                            sendDefaultResponse = false; // per spec, sending a specific response
//	                        } catch (error) {
//	                            logger.error(`Handling of poll check-in from ${this.ieeeAddr} failed (${(error as Error).message})`, NS);
//	                            defaultResponseStatus = Zcl.Status.FAILURE;
//	                        } finally {
//	                            if (startedFastPolling) {
//	                                // We *must* end fast-poll when we're done sending things. Otherwise we cause undue power-drain.
//	                                logger.debug(`check-in from ${this.ieeeAddr}: stopping fast-poll`, NS);
//
//	                                try {
//	                                    await endpoint.command(cluster.name as "genPollCtrl", "fastPollStop", {}, {sendPolicy: "immediate"});
//	                                } catch (error) {
//	                                    logger.error(`Failed to stop fast poll for ${this.ieeeAddr} (${(error as Error).message})`, NS);
//	                                }
//	                            }
//	                        }
//	                    }
//	                    break;
//	                }
//	            }
//	        }
//
//	        // Send a default response if necessary.
//	        /* v8 ignore next */
//	        const disableTuyaDefaultResponse = this.manufacturerName?.startsWith("_TZ") && process.env.DISABLE_TUYA_DEFAULT_RESPONSE;
//	        // Sometimes messages are received twice, prevent responding twice
//	        const alreadyResponded = this._lastDefaultResponseSequenceNumber === header.transactionSequenceNumber;
//
//	        if (
//	            !this._skipDefaultResponse &&
//	            sendDefaultResponse &&
//	            (!header.frameControl.disableDefaultResponse || defaultResponseStatus !== Zcl.Status.SUCCESS) &&
//	            !alreadyResponded &&
//	            !disableTuyaDefaultResponse
//	        ) {
//	            try {
//	                this._lastDefaultResponseSequenceNumber = header.transactionSequenceNumber;
//	                const direction =
//	                    header.frameControl.direction === Zcl.Direction.CLIENT_TO_SERVER
//	                        ? Zcl.Direction.SERVER_TO_CLIENT
//	                        : Zcl.Direction.CLIENT_TO_SERVER;
//
//	                await endpoint.defaultResponse(command.ID, defaultResponseStatus, cluster.ID, header.transactionSequenceNumber, {
//	                    direction,
//	                });
//	            } catch (error) {
//	                logger.debug(`Default response to ${this.ieeeAddr} failed (${error})`, NS);
//	            }
//	        }
	    }
}
