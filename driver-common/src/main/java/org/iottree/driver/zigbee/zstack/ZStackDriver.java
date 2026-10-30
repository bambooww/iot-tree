package org.iottree.driver.zigbee.zstack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.ZCLCmd;
import org.iottree.driver.zigbee.zcl.ZCLParam;
import org.iottree.driver.zigbee.zspec.ZSAddr;
import org.iottree.driver.zigbee.zspec.ZSCoor;
import org.iottree.driver.zigbee.zspec.ZSDevice;
import org.iottree.driver.zigbee.zspec.ZSDriver;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestSREQ;
import org.json.JSONArray;
import org.json.JSONObject;

public class ZStackDriver extends ZSDriver
{
	protected static ILogger log = LoggerManager.getLogger(ZStackDriver.class);

	long readTimeout = 3000 ;
	
	private long cmdInterval = 10 ;
	
	private ZStackManager zsMgr ;
	
	public ZStackDriver()
	{
		super() ;
		zsMgr = new ZStackManager(this) ;
		log.setCurrentLogLevel(ILogger.LOG_LEVEL_DEBUG);
	}
	
	public ZStackManager getZStackManager()
	{
		return this.zsMgr ;
	}
	
	@Override
	public ZSCoor getZSCoor()
	{
		return this.zsMgr.getCoor() ;
	}

	@Override
	public DevDriver copyMe()
	{
		return new ZStackDriver();
	}

	@Override
	public String getName()
	{
		return "zigbee_zstack";
	}

	@Override
	public String getTitle()
	{
		return "Zigbee ZStack(ZBDongle-P)";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class;
	}
	
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return true;
	}

	@Override
	public boolean supportDevFinder()
	{
		return true;
	}
	
	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
//		gp = new PropGroup("timing",lan);//"Timing");
//
//		gp.addPropItem(new PropItem("req_to",lan,PValTP.vt_int,false,null,null,1000)); //"Request Timeout(millisecond)",""
//		gp.addPropItem(new PropItem("failed_tryn",lan,PValTP.vt_int,false,null,null,3)); //"Fail after successive times",""
//		gp.addPropItem(new PropItem("recv_to",lan,PValTP.vt_int,false,null,null,200)); //"Receive response timeout(millisecond)",""
//		gp.addPropItem(new PropItem("inter_req",lan,PValTP.vt_int,false,null,null,0)); //"Inter-request millisecond",""
//		pgs.add(gp) ;
//
//		gp = new PropGroup("block_size",lan);//"Block Sizes");
//		gp.addPropItem(new PropItem("blk_bytes",lan,PValTP.vt_int,false,null,null,64)); //"Output Coils",""
		//gp.addPropItem(new PropItem("reg",lan,PValTP.vt_int,false,null,null,80)); //"Internal Registers",""
//		pgs.add(gp) ;
		
		return pgs;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
//		gp = new PropGroup("mceth_comm",lan);//"Timing");
//		gp.addPropItem(new PropItem("comm_fmt",lan,PValTP.vt_str,false,new String[] {"bin","ascii"} ,new Object[] {"bin","ascii"},"bin")); //"Request Timeout(millisecond)",""
		
//		pgs.add(gp) ;
		
		return pgs;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		return null;
	}
	
	private static transient ZSAddr ADDR = new ZSAddr() ; 

	@Override
	public DevAddr getSupportAddr()
	{
		return ADDR;
	}
	
	/**
	 * tag由驱动生产，地址不需要解析
	 */
	@Override
	public boolean ignoreParseAddr()
	{
		return true;
	}
	
//	@Override
//	public ValTP[] getLimitValTPs(UADev dev)
//	{
//		return LIMIT_VTPS ;
//	}
	
	public long getReadTimeout()
	{
		return this.readTimeout ;
	}
	
	public long getCmdInterval()
	{
		return this.cmdInterval ;
	}
	
	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
		super.initDriver(failedr) ;
		
		return true ;
	}
	


	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)cp;
		//System.out.println("on conn ready="+cpt) ;
		this.zsMgr.RT_start();
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		//System.out.println("on conn broken="+cp) ;
		this.zsMgr.RT_stop();
	}
	
	protected void afterDriverRun() throws Exception
	{
		//System.out.println("afterDriverRun") ;
		this.zsMgr.RT_stop();
	}
	
	public synchronized boolean RT_sendReq(Msg m,StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null || !cpt.isConnReady())
		{
			if(failedr!=null)
				failedr.append("no connection") ;
			return false;
		}
		
		byte[] bs = m.pack() ;
		// System.out.println("RT_sendReq ->"+Convert.byteArray2HexStr(bs, "-")) ;
		OutputStream outputo = cpt.getOutputStream() ;
		outputo.write(bs);
		outputo.flush();
		return true;
	}
	
	private transient int respST = 0 ;
	private transient int respLeftLen = 0;
	private transient int respReadLen = 0;
	private transient byte[] respBS = new byte[255] ;
	
	@Override
	protected void RT_beforeDriverRunLoop() throws Exception
	{
		clearEndDevDD() ;//make auto reload after this time
		
		//reload data all,and set tags loaded data
		UACh ch = getBelongToCh();
		List<UADev> devs = ch.getDevs() ;
		if(devs!=null)
		{
			for(UADev dev:devs)
			{
				String ieee = dev.getOuterId() ;
				if(Convert.isNullOrEmpty(ieee))
					continue ;
				EndDevDynData ddd = this.getOrLoadEndDevDD(ieee) ;
				ddd.RT_setDataToTags() ;
			}
		}
		return;
	}
	
	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		return true;
	}
	
	@Override
	protected boolean RT_useLoopNoWait()
	{
		return true;
	}
	
	@Override
	protected boolean RT_runInLoopNoWait(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null || !cpt.isConnReady())
		{
			respST = 0 ;
			return true ;
		}
		
		int c = 0 ;
		InputStream inputs = cpt.getInputStream() ;
		if(inputs==null)
		{
			respST = 0 ;
			return true ;
		}
		
		while(inputs.available()>0)
		{
			c=inputs.read() ;
			switch(respST)
			{
			case 0:
				if(c==Msg.SOF)
					respST = 1 ;
				break;
			case 1:
				if(c>250) {respST=0;break;}
				respBS[0] = (byte)Msg.SOF;
				respBS[1] = (byte)c ;
				respLeftLen = c + 3 ;
				respReadLen = 0 ;
				respST = 2 ;
				break ;
			case 2:
				respBS[2+respReadLen] = (byte)c ;
				respReadLen ++ ;
				if(respReadLen>=respLeftLen)
				{
					StringBuilder msg_f = new StringBuilder() ;
					Msg rsp =MsgManager.unpackedMsg(respBS,msg_f) ;
					if(rsp==null)
					{
//						if(log.isDebugEnabled())
//						{
//							log.debug(msg_f.toString()+"\r\n\t"+Convert.byteArray2HexStr(respBS, "-"));
//						}
						zsMgr.RT_onUnknownResp(Convert.byteArray2HexStr(respBS, "-")) ;
					}
					else
					{
						if(rsp instanceof MsgRecv)
							zsMgr.RT_onMsgRecv((MsgRecv)rsp) ;
					}
					respST = 0 ;
				}
				break ;
			default:
				respST = 0 ;
				break;
			}
		}
		
		sleep(1);
		
		return true;
	}
	
	
	@Override
	public boolean RT_writeVal(UACh ch,UADev dev,UATag tag, DevAddr da, Object v)
	{
		if(da==null)
			return false;
		ZSAddr addr = (ZSAddr)da;
		if(!addr.canWrite())
			return false;
		String ieee_addr = dev.getOuterId() ;
		if(Convert.isNullOrEmpty(ieee_addr))
			return false;
		
		StringBuilder failedr = new StringBuilder() ;
		return RT_writeTo(ch,dev,tag,ieee_addr, addr, v,failedr);
	}

	@Override
	public boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags, DevAddr[] da, Object[] v)
	{
		throw new RuntimeException("no impl") ;
	}
	
	
	
	private int transSeqN = 0 ;
	
	private synchronized int getNextSeqN()
	{
		transSeqN ++ ;
		return transSeqN ;
	}
	
	
	private boolean RT_writeTo(UACh ch,UADev dev,UATag tag,String ieee_addr,ZSAddr addr,Object v,StringBuilder failedr)
	{
		ZSDevice ed = (ZSDevice)zsMgr.getCoor().getDeviceByIEEEAddr(ieee_addr) ;
		if(ed==null)
		{
			failedr.append("no end device found with ieee_addr="+ieee_addr) ;
			return false;
		}
		if(addr.isAddrCmd())
		{
			return RT_writeToCmd(ed,addr,v,failedr);
		}
		if(addr.isAddrAttr())
		{
			return RT_writeToAttr(ed,addr,v,failedr) ;
		}
		if(addr.isAddrCmdPm())
		{
			return RT_writeToCmdPM(tag,ed,addr,v,failedr) ;
		}
		
		failedr.append("unknown write addr="+addr) ;
		return false;
	}
	
	
	private boolean RT_writeToCmd(ZSDevice ed,ZSAddr addr,Object v,StringBuilder failedr)
	{
		if(!addr.isAddrCmd() || !addr.isValid())
		{
			failedr.append("not valid cmd address") ;
			return false;
		}
		int dst_addr = ed.getShortAddr().getAddrVal() ;
		int endpoint = addr.getEndPointID() ;
		int cluster_id = addr.getClusterID() ;
		int cmd_id = addr.getCmdID() ;
		
		ZCLCmd cmddef = ZCLClusterManager.getInstance().getCmd(cluster_id, cmd_id) ;
		if(cmddef==null)
		{
			failedr.append("not cmd def found") ;
			return false;
		}
		List<ZCLParam> pms = cmddef.getParemeters() ;
		Object[] pm_vals = null ;
		int n ;
		if(pms!=null&&(n=pms.size())>0)
		{
			UADev dev = ed.getRelatedUADev() ;
			if(dev==null)
			{
				failedr.append("no related UADev found") ;
				return false;
			}
			pm_vals = new Object[n] ;
			for(int i = 0 ; i <  n ; i ++)
			{
				ZSAddr pmaddr = ZSAddr.createCmdPmAddr(addr,i) ;
				String pm_addr = pmaddr.getAddrStr() ;
				List<UATag> tags = dev.filterTagsAllByAddr(pm_addr) ;
				if(tags==null||tags.size()<=0)
				{
					failedr.append("no related tag found with addr="+pm_addr) ;
					return false;
				}
				UATag tag = tags.get(0) ;
				UAVal uav = tag.RT_getVal() ;
				if(!uav.isValid())
				{
					failedr.append("tag ["+tag.getNodeCxtPathInPrj()+"] value is invalid") ;
					return false;
				}
				Object objv = uav.getObjVal() ;
				pm_vals[i] = objv ;
			}
		}
		
		try
		{
			return zsMgr.getCoor().sendClusterCmdToEndDevice(dst_addr,endpoint,cluster_id,cmd_id,pm_vals, failedr) ;
		}
		catch(Exception ee)
		{
			if(log.isWarnEnabled())
				log.warn(ee);
			failedr.append("RT_sendReq err:"+ee.getMessage()) ;
			return false;
		}
	}
	
	private boolean RT_writeToCmdPM(UATag tag,ZSDevice ed,ZSAddr addr,Object v,StringBuilder failedr)
	{
		if(!addr.isValid() || !addr.isAddrCmdPm())
		{
			failedr.append("not valid cmd pm address") ;
			return false;
		}
		
		EndDevDynData eddd = this.getOrLoadEndDevDD(ed.getIEEEAddr()) ;
		try
		{//save to driver device related file
			eddd.RT_updatePmVal(addr, v,true) ;
			tag.RT_setValRaw(v) ;
			return true;
		}
		catch(Exception ee)
		{
			failedr.append("RT_sendReq err:"+ee.getMessage()) ;
			return false;
		}
	}
	
	static class CmdPmVal
	{
		ZSAddr addr ;
		
		long up_dt = -1;
		
		String strv = null;
		
		public CmdPmVal(ZSAddr addr)
		{
			if(!addr.isAddrCmdPm() || !addr.isValid())
				throw new IllegalArgumentException("must be valid and cmd pm addr") ;
//			this.ep = addr.getEndPointID() ;
//			this.cluster = addr.getClusterID() ;
//			this.cmd = addr.getCmdID() ;
//			this.pm = addr.getCmdPmIdx() ;
			this.addr = addr ;
		}
		
		public CmdPmVal() {}
		

		public long getUpDT()
		{
			return this.up_dt ;
		}
		
		public String getStrV()
		{
			return this.strv ;
		}
		
		public ZCLParam getParamDef()
		{
			return ZCLClusterManager.getInstance().getCmdPm(this.addr.getClusterID(), this.addr.getCmdID()
					,this.addr.getCmdPmIdx()) ;
		}
		
		public void RT_updateV(Object v)
		{
			this.strv = v.toString() ;
			this.up_dt = System.currentTimeMillis() ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("addr",this.addr.toString())
					.put("up_dt", this.up_dt).putOpt("strv", this.strv) ;
		}
		
		public static CmdPmVal fromJO(JSONObject jo)
		{
			CmdPmVal r = new CmdPmVal() ;
			String addr = jo.optString("addr") ;
			if(Convert.isNullOrEmpty(addr))
				return null ;
			
			StringBuilder failedr = new StringBuilder() ;
			r.addr = ZSAddr.parseAddr(addr, failedr) ;
			if(r.addr==null)
				return null ;
			if(!r.addr.isValid())
				return null;
			if(!r.addr.isAddrCmdPm())
				return null ;
			
			r.up_dt = jo.optLong("up_dt", -1) ;
			r.strv = jo.optString("strv") ;
			return r ;
		}
	}
	
	private static class EndDevDynData
	{
		ZStackDriver drv ;
		
		String ieee_addr ;
		
		HashMap<String,CmdPmVal> cmdPmVals = new HashMap<>() ;
		
		EndDevDynData(ZStackDriver drv,String ieee_addr)
		{
			this.drv = drv ;
			this.ieee_addr = ieee_addr ;
		}
		
		UADev getUAdev()
		{
			UACh ch = this.drv.getBelongToCh() ;
			if(ch==null)
				return null ;
			return ch.getDevByOuterId(this.ieee_addr) ;
		}
		
		
		void RT_updatePmVal(ZSAddr addr,Object v,boolean bsave) throws IOException
		{
			String k = addr.getAddrStr() ;
			CmdPmVal cpv = cmdPmVals.get(k) ;
			if(cpv==null)
			{
				cpv = new CmdPmVal(addr) ;
				cmdPmVals.put(k,cpv) ;
			}
			cpv.RT_updateV(v);
			if(bsave)
				this.save();
		}
		
		void RT_setDataToTags()
		{
			if(cmdPmVals.size()<=0)
				return ;
			
			UADev dev = getUAdev() ;
			if(dev==null)
				return ;
			//set cmd pm tags
			for(Map.Entry<String, CmdPmVal> k2cpv:this.cmdPmVals.entrySet())
			{
				String addr = k2cpv.getKey() ;
				CmdPmVal cpv = k2cpv.getValue() ;
				List<UATag> tags = dev.filterTagsAllByAddr(addr) ;
				if(tags==null||tags.size()<=0)
					continue ;
				String strv = cpv.getStrV() ;
				if(Convert.isNullOrEmpty(strv))
				{
					ZCLParam pmdef = cpv.getParamDef() ;
					if(pmdef!=null)
						strv = pmdef.getTagDef().getDefaultStr() ;
				}
				if(Convert.isNullOrEmpty(strv))
					continue ;
				for(UATag tag:tags)
				{
					tag.RT_setValRawStr(strv);
				}
			}
		}
		
		JSONObject toJO()
		{
			JSONObject r = new JSONObject() ;
			JSONArray jarr = new JSONArray() ;
			r.put("cmd_pm_vals",jarr) ;
			for(CmdPmVal cpv:this.cmdPmVals.values())
			{
				jarr.put(cpv.toJO()) ;
			}
			return r ;
		}
		
		void fromJO(JSONObject jo)
		{
			JSONArray jarr = jo.optJSONArray("cmd_pm_vals") ;
			if(jarr!=null)
			{
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					CmdPmVal cpv = CmdPmVal.fromJO(tmpjo) ;
					if(cpv==null)
						continue ;
					cmdPmVals.put(cpv.addr.getAddrStr(),cpv) ;
				}
			}
		}
		
		File calFile()
		{
			File dirb = drv.getZSCoor().getStoreDirBase() ;
			return new File(dirb,"ed_"+this.ieee_addr+".json") ;
		}
		
		
		void load()
		{
			File f = calFile() ;
			if(!f.exists())
				return ;
			try
			{
				JSONObject jo = Convert.readFileJO(f) ;
				if(jo==null)
					return ;
				fromJO(jo) ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		void save() throws IOException
		{
			File f = calFile() ;
			Convert.writeFileJO(f, this.toJO());
		}
	}
	
	private HashMap<String,EndDevDynData> ieee2eddd = new HashMap<>() ;
	
	private synchronized void clearEndDevDD()
	{
		ieee2eddd = new HashMap<>() ;
	}
	
	private synchronized EndDevDynData getOrLoadEndDevDD(String ieeeaddr)
	{
		ieeeaddr = ieeeaddr.toLowerCase() ;
		EndDevDynData eddd = ieee2eddd.get(ieeeaddr) ;
		if(eddd!=null)
			return eddd ;
		
		eddd = new EndDevDynData(this,ieeeaddr) ;
		eddd.load();
		ieee2eddd.put(ieeeaddr,eddd); 
		return eddd ;
	}
	
	//private void RT_save
	
	private boolean RT_writeToAttr(ZSDevice ed,ZSAddr addr,Object v,StringBuilder failedr)
	{
		int dst_addr = ed.getShortAddr().getAddrVal() ;
		int endpoint = addr.getEndPointID() ;
		int cluster_id = addr.getClusterID() ;
		int cmd_id = addr.getCmdID() ;
		if(cmd_id<0)
		{
			failedr.append("not cmd address") ;
			return false;
		}
		
		try
		{
			return zsMgr.getCoor().writeWriteAttr();
		}
		catch(Exception ee)
		{
			failedr.append("RT_sendReq err:"+ee.getMessage()) ;
			return false;
		}
	}
}
