package org.iottree.core.station;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.station.StationLocSaver.Item;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.queue.QueTickThread;
import org.iottree.core.util.queue.QueueThread;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * for station local manager
 * @author zzj
 *
 */
public class StationLocal
{
	static ILogger log = LoggerManager.getLogger(StationLocal.class) ;
	
	static StationLocal instance = null ;
	
	public static StationLocal getInstance()
	{
		if(instance!=null)
		{
			if(!instance.bValid)
				return null ;
			return instance ;
		}
		
		synchronized(StationLocal.class)
		{
			if(instance!=null)
			{
				if(!instance.bValid)
					return null ;
				return instance ;
			}
			
			instance = new StationLocal() ;
			if(!instance.bValid)
				return null ;
			return instance ;
		}
		
	}
	
	static class PrjSynPm
	{
		String prjName = null ;
		
		boolean dataSynEn = false;
		
		long dataSynIntv = 10000 ;
		
		boolean failedKeep = false;
		
		long keepMaxLen = 3153600 ;
		
		private transient long lastSynDT = -1 ;
		
		private transient UAPrj prj ;
		
		public PrjSynPm(String prjname,boolean data_syn_en,long data_syn_intv,boolean failed_keep,long keep_max_len)
		{
			this.prjName = prjname ;
			this.dataSynEn = data_syn_en ;
			this.dataSynIntv = data_syn_intv ;
			this.failedKeep = failed_keep ;
			this.keepMaxLen = keep_max_len ;
			this.prj = UAManager.getInstance().getPrjByName(prjname) ;
		}
		
		public boolean setPm(boolean data_syn_en,long data_syn_intv,boolean failed_keep,long keep_max_len)
		{
			if(this.dataSynEn==data_syn_en && data_syn_intv==this.dataSynIntv
					&& this.failedKeep == failed_keep && this.keepMaxLen==keep_max_len)
				return false;
			this.dataSynEn = data_syn_en ;
			this.dataSynIntv = data_syn_intv ;
			this.failedKeep = failed_keep ;
			this.keepMaxLen = keep_max_len ;
			return true ;
		}
		
		boolean isPrjRunning()
		{
			if(this.prj==null)
				return false;
			return this.prj.RT_isRunning() ;
		}
		
		UAPrj getPrj()
		{
			return this.prj ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("prjname", this.prjName) ;
			jo.put("data_syn_en", this.dataSynEn) ;
			jo.putOpt("data_syn_intv",this.dataSynIntv) ;
			jo.putOpt("failed_keep",this.failedKeep) ;
			jo.putOpt("keep_max_len", this.keepMaxLen) ;
			return jo ;
		}
		
		public static PrjSynPm fromJO(JSONObject jo)
		{
			String n = jo.optString("prjname") ;
			if(Convert.isNullOrEmpty(n))
				return null;
			boolean en = jo.optBoolean("data_syn_en",false) ;
			long intv = jo.optLong("data_syn_intv",10000) ;
			boolean  failed_keep = jo.optBoolean("failed_keep",false) ;
			long keep_max_len = jo.optLong("keep_max_len", 3153600) ;
			return new PrjSynPm(n,en,intv,failed_keep,keep_max_len) ;
		}
	}
	
	class WebSockClient extends WebSocketClient
	{

		public WebSockClient(URI serveruri)
		{
			super(serveruri);
			//

		}

		@Override
		public void onOpen(ServerHandshake handshakedata)
		{

		}

		@Override
		public void onMessage(String message)
		{
			
		}
		
		public void onMessage(ByteBuffer bytes)
		{
			try
			{
				RT_onRecvedMsg(bytes.array());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onClose(int code, String reason, boolean remote)
		{

		}

		@Override
		public void onError(Exception ex)
		{

		}

		private int checkAndConn() throws Exception
		{
			switch(this.getReadyState())
			{
			case NOT_YET_CONNECTED:
				if(isClosed())
					reconnectBlocking();
				else
					connectBlocking();
				return 2;
			case OPEN:
				return 1;
			
			case CLOSED:
				reconnectBlocking() ;
				return 2;
			default:
				return 0;
			}
		}
	}
	
	String id = null ;
	
	
	/**
	 * local station's parent address
	 */
	String platformHost = null ;
	
	int platformPort = 9090 ;
	
	String key = null ;
	
//	boolean rt_data_failed_keep = false ;
//	long rt_data_keep_len = 3153600 ;
	
	private boolean bValid = false;
	
	private List<PrjSynPm> prjSynPMs = null ;
	
	private StationLocal()
	{
		bValid = loadConfig() ;
		prjSynPMs = loadPrjSynPMs() ;
	}
	
	private boolean loadConfig()
	{
		File fstat = Config.getConfFile("station.json") ;
		if(!fstat.exists())
			return false ;
		try
		{
			JSONObject jo = Convert.readFileJO(fstat) ;
			this.id = jo.getString("id") ;
			this.platformHost = jo.getString("platform_host") ;
			this.platformPort = jo.optInt("platform_port",9090) ;
			this.key = jo.getString("key") ;
			this.bValid = jo.optBoolean("valid",false) ;
//			this.rt_data_failed_keep = jo.optBoolean("rt_data_failed_keep",false) ;
//			this.rt_data_keep_len = jo.optLong("rt_data_keep_len", 3153600) ;
			return true ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return false;
		}
	}
	
	private List<PrjSynPm> loadPrjSynPMs()
	{
		ArrayList<PrjSynPm> pms = new ArrayList<>() ;
		
		File fstat = Config.getConfFile("station_syn_pms.json") ;
		if(!fstat.exists())
			return pms ;
		try
		{
			String txt =Convert.readFileTxt(fstat) ;
			JSONArray jarr = new JSONArray(txt) ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject jo = jarr.getJSONObject(i) ;
				PrjSynPm psp = PrjSynPm.fromJO(jo) ;
				if(psp==null)
					continue ;
				pms.add(psp) ;
			}
			return pms ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return pms ;
		}
	}
	
	private void savePrjSynPMs() throws IOException
	{
		JSONArray jarr = new JSONArray() ;
		for(PrjSynPm pm: prjSynPMs)
		{
			JSONObject tmpjo = pm.toJO() ;
			jarr.put(tmpjo) ;
		}
		File fstat = Config.getConfFile("station_syn_pms.json") ;
		if(!fstat.getParentFile().exists())
			fstat.getParentFile().mkdirs() ;
		Convert.writeFileTxt(fstat, jarr.toString());
	}
	
	public boolean isStationValid()
	{
		return this.bValid ;
	}
	
	public String getStationId()
	{
		return id ;
	}
	
	public String getPlatfromHost()
	{
		return this.platformHost ;
	}
	
	public int getPlatfromPort()
	{
		return this.platformPort ;
	}
	
	public String getStationKey()
	{
		return this.key ;
	}
	
	public List<PrjSynPm> getPrjSynPMs()
	{
		return prjSynPMs ;
	}
	
	public PrjSynPm getPrjSynPM(String prjname)
	{
		if(this.prjSynPMs==null)
			return null ;
		for(PrjSynPm synpm:this.prjSynPMs)
		{
			if(prjname.equals(synpm.prjName))
				return synpm ;
		}
		return null ;
	}
	
	public void setPrjSynPM(String prjname,boolean datasyn_en,long syn_intv,boolean failed_keep,long keep_max_len) throws IOException
	{
		PrjSynPm psp = getPrjSynPM(prjname) ;
		if(psp==null)
		{
			psp = new PrjSynPm(prjname,datasyn_en,syn_intv, failed_keep,keep_max_len) ;
			getPrjSynPMs().add(psp) ;
		}
		else
		{
			if(!psp.setPm(datasyn_en, syn_intv, failed_keep,keep_max_len))
				return ;
		}
		
		savePrjSynPMs() ;
	}
	// rt
	
	static class QItem
	{
		PrjSynPm prjpm ;
		
		String key ;
		
		PSCmdPrjRtData rtd ;
		
		public QItem(PrjSynPm prjpm,String key, PSCmdPrjRtData rtd)
		{
			this.prjpm = prjpm ;
			this.key = key ;
			this.rtd = rtd ;
		}
	}
	
	WebSockClient wsClient =null;
	
	private Thread th = null ;
	
	QueTickThread.Handler<QItem> qttH = new QueTickThread.Handler<QItem>() {

		/**
		 * 发送或存储，或测试链接
		 */
		@Override
		public void onQueObj(QItem qi)
		{
			//send or save
			RT_chkSendOrSave(qi) ;
			// check on tick
			this.onTick();
		}

		
		@Override
		public void onTick()
		{
			RT_checkConn() ;
			
			RT_checkStationST();
			
			RT_checkHisReSend() ;
		}};
	//private Thread rtDataTh = null ;
	/**
	 * 使用队列保证数据采集精度
	 */
	private QueTickThread<QItem> queTh =new QueTickThread<>(qttH,200) ;
	
	public boolean isConnReady()
	{
		if(this.wsClient==null)
			return false;
		return wsClient.getReadyState()==ReadyState.OPEN ;
	}
	
	protected void RT_onRecvedMsg(byte[] bs) throws Exception
	{
		if(log.isTraceEnabled())
		{
			log.trace(" StationLocal RT_onRecvedMsg bs len="+bs.length);
		}
		
		PSCmd psc = PSCmd.parseFrom(bs) ;
		if(psc==null)
			return ;
		
		if(log.isTraceEnabled())
		{
			log.trace(" StationLocal RT_onRecvedMsg "+psc);
		}
		psc.RT_onRecvedInStationLocal(this);
	}
	

	void disconnect() // throws IOException
	{
		WebSockClient ss = wsClient;
		if(ss==null)
			return ;
		
		try
		{
			ss.close();
		}
		catch(Exception e) {}
		finally
		{
			wsClient = null ;
		}
	}

	private int connectToWS() throws Exception
	{
		if(this.wsClient==null)
		{
			String url = "ws://"+this.platformHost+":"+this.platformPort+"/_ws/station/"+id ;
			System.out.println(" station local to platform ->"+url) ;
			this.wsClient = new WebSockClient(new URI(url)) ;
		}
		
		return this.wsClient.checkAndConn() ;
	}
	

	private transient long lastChk = -1 ;
	
	private transient long lastReConnDT = -1 ;
	
	private void RT_checkConn()
	{
		if(System.currentTimeMillis()-lastChk<5000)
			return ;
		
		try
		{
			int res = connectToWS();
			if(res==2)
				lastReConnDT = System.currentTimeMillis() ;
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("connect to websocket",e);
		}
		finally
		{
			lastChk = System.currentTimeMillis() ;
		}
	}
	
	private transient PSCmdStationST _lastStationST = null ;
	
	private transient long lastChkST = -1 ;
	
	private transient int notSendST_CC = 0 ;
	
	private void RT_checkStationST()
	{
		if(System.currentTimeMillis()-lastChkST<5000)
			return ;
		
		try
		{
			if(wsClient.getReadyState()==ReadyState.OPEN)
			{
				if(notSendST_CC>=12 || _lastStationST==null || _lastStationST.chkChg(this) || System.currentTimeMillis()-lastReConnDT<10000)
				{
					PSCmdStationST cmd_st = new PSCmdStationST() ;
					cmd_st.asStationLocal(this) ;
					
					StringBuilder failedr = new StringBuilder() ;
					RT_sendCmd(cmd_st,failedr) ;
					_lastStationST = cmd_st ;
					notSendST_CC = 0 ;
				}
				else
				{
					notSendST_CC ++ ;
				}
			}
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("RT_checkStationST "+e.getMessage(),e);
		}
		finally
		{
			lastChkST = System.currentTimeMillis() ;
		}
	}
	
	private void RT_chkSendOrSave(QItem qi)
	{
		boolean bconnok = false;
		if(wsClient!=null)
			bconnok = wsClient.getReadyState()==ReadyState.OPEN ;
		
		PrjSynPm prjsyn = qi.prjpm ;
		//PSCmdPrjRtData rtd = qi.rtd ;
		try
		{
			if(bconnok)
			{
				byte[] bs = qi.rtd.packTo() ;
				wsClient.send(bs);
				
				if(log.isTraceEnabled())
				{
					long dt =  IdCreator.extractTimeInMillInSeqId(qi.key) ;
					String dtstr = Convert.toFullYMDHMS(new Date(dt)) ;
					log.trace(" ["+this.id+"] send "+prjsyn.prjName+" RTData:["+dtstr+"] key="+qi.key+" size="+bs.length+" qlen="+queTh.getQueLen());
				}
				return ;
			}
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("RT_chkSendRTData "+e.getMessage(),e);
		}
		finally
		{
			prjsyn.lastSynDT = System.currentTimeMillis() ;
		}
		
		if(!prjsyn.failedKeep)
			return ;
		//saver
		StationLocSaver locsaver = StationLocSaver.getSaver(prjsyn.prjName) ;
		if(locsaver==null)
			return ;
		
		qi.rtd.asHisData(true) ;
		locsaver.RT_putItemBuffered(qi.key, qi.rtd.packTo(),prjsyn.keepMaxLen);
		if(log.isTraceEnabled())
		{
			long dt =  IdCreator.extractTimeInMillInSeqId(qi.key) ;
			String dtstr = Convert.toFullYMDHMS(new Date(dt)) ;
			log.trace(" ["+this.id+"] save local ["+dtstr+"]  "+prjsyn.prjName+" loc_num="+locsaver.RT_getSavedBufferedNum()+" qlen="+queTh.getQueLen());
		}
	}
	
	private void RT_checkHisReSend()
	{
		boolean bconnok = false;
		if(wsClient!=null)
			bconnok = wsClient.getReadyState()==ReadyState.OPEN ;
		
		if(!bconnok)
			return ;
		
		for(PrjSynPm prjsyn:this.getPrjSynPMs())
		{
			if(!prjsyn.dataSynEn || !prjsyn.failedKeep)
				continue ;
			StationLocSaver locsaver = StationLocSaver.getSaver(prjsyn.prjName) ;
			if(locsaver==null)
				continue ;
			
			if(locsaver.RT_getSavedBufferedNum()<=0)
				continue ;

			try
			{
				locsaver.RT_flushBuffered(prjsyn.keepMaxLen) ;
								
				List<Item> his_items = locsaver.getLastItems(3) ;
				if(his_items.size()>0)
				{
					for(Item item:his_items)
					{
						if(log.isTraceEnabled())
						{
							log.trace(" Station ["+this.id+"] send Prj His RTData key="+item.getKey());
						}
						
						wsClient.send(item.msg);
					}
					locsaver.deleteBatchByItems(his_items) ;
					
					if(log.isTraceEnabled())
					{
						log.trace(" Station ["+this.id+"] send Prj His RTData:"+prjsyn.prjName+" num="+his_items.size()+" local saved left num="+locsaver.RT_getSavedBufferedNum());
					}
				}
			}
			catch(Exception e)
			{
				if(log.isDebugEnabled())
					log.debug("RT_checkHisReSend "+e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 尽可能在准确的时间间隔内，获取数据
	 */
	private void RT_collRTData()
	{
		for(PrjSynPm prjsyn:this.getPrjSynPMs())
		{
			if(!prjsyn.dataSynEn)
				continue ;
			
//			UAPrj prj = UAManager.getInstance().getPrjByName(prjsyn.prjName) ;
//			if(prj==null)
//				continue;
			
			if(!prjsyn.isPrjRunning())
				continue ; ;
			
			if(System.currentTimeMillis() - prjsyn.lastSynDT < prjsyn.dataSynIntv)
				continue ;
			
			//byte[] bs = null;
			String keyid = IdCreator.newSeqId() ;//用来唯一标识数据key
			prjsyn.lastSynDT = System.currentTimeMillis() ;
			try
			{
				PSCmdPrjRtData cmd_rd = new PSCmdPrjRtData() ;
				cmd_rd.asStationLocalPrj(keyid,prjsyn.getPrj());
				//bs = cmd_st.packTo() ;
				
				QItem qi = new QItem(prjsyn,keyid,cmd_rd) ;
				//直接加入队列
				queTh.enqueue(qi);
			}
			catch(Exception eee)
			{
				eee.printStackTrace();
				return ;
			}
			finally
			{
				
			}
		}
	}


	/**
	 * run collection rt data
	 */
	private void RT_run()
	{
		while(th!=null)
		{
			try
			{
				Thread.sleep(10);
			}
			catch(Exception ee)
			{}

			RT_collRTData();
		}
	}
	
	private Runnable runner = new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						RT_run();
					}
					finally
					{
						th = null ;
						disconnect();
					}
				}
			} ;
	
		
	public synchronized void RT_start()
	{
		if(th!=null)
			return ;
		
		th = new Thread(runner) ;
		th.start();
		queTh.RT_start();
	}
	
	public synchronized void RT_stop()
	{
		Thread t = th ;
		if(t==null)
			return ;
		t.interrupt(); 
		th = null ;
		queTh.RT_stop();
		disconnect();
	}
	
	public boolean isRunning()
	{
		return th!=null ;
	}
	
	public boolean RT_sendCmd(PSCmd cmd,StringBuilder failedr)
	{
		if(wsClient==null)
		{
			failedr.append("no connected to platform") ;
			return false;
		}
		if(!wsClient.isOpen())
		{
			failedr.append("connection is not open") ;
			return false;
		}
		
		if(log.isTraceEnabled())
			log.trace("RT_sendCmd "+cmd.toString()) ;
		
		wsClient.send(cmd.packTo());
		return true;
	}
}
