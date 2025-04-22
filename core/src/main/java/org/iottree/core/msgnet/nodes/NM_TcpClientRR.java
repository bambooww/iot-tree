package org.iottree.core.msgnet.nodes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.station.StationLocSaver;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

/**
 * Tcp Client Request and Response
 * 
 * @author jason.zhu
 *
 */
public class NM_TcpClientRR extends MNNodeMid implements IMNRunner 
{
	static ILogger log = LoggerManager.getLogger(NM_TcpClientRR.class) ;
	
	static Lan lan = Lan.getLangInPk(NM_TcpClientRR.class) ;
	
	public static enum ReqTP
	{
		fixed_req(0),
		pm_req(1),
		no_req(2);
		
		public final int val ;
		
		ReqTP(int v)
		{
			val = v ;
		}
		
		public int getVal()
		{
			return this.val ;
		}
		
		public String getTitle()
		{
			return lan.g("tc_rr_req_"+this.name()) ;
		}
		
		public static ReqTP fromVal(int v)
		{
			switch(v)
			{
			case 1:
				return pm_req ;
			case 2:
				return no_req;
			default:
				return fixed_req ;
			}
		}
	}
	
//	public static enum ReqSendTP
//	{
//		trigger_in(0),
//		interval(1),
//		once(2);
//		
//		public final int val ;
//		
//		ReqSendTP(int v)
//		{
//			val = v ;
//		}
//		
//		public int getVal()
//		{
//			return this.val ;
//		}
//		
//		public String getTitle()
//		{
//			return lan.g("tc_rr_req_sd_"+this.name()) ;
//		}
//		
//		public static ReqSendTP fromVal(int v)
//		{
//			switch(v)
//			{
//			case 1:
//				return interval ;
//			case 2:
//				return once;
//			default:
//				return trigger_in ;
//			}
//		}
//	}
	
	public static enum RespPkTP
	{
		fixed_len(0),
		fixed_len_se(2),
		start_end(1);
		
		public final int val ;
		
		RespPkTP(int v)
		{
			val = v ;
		}
		
		public int getVal()
		{
			return this.val ;
		}
		
		public String getTitle()
		{
			return lan.g("tc_rr_resppk_"+this.name()) ;
		}
		
		public static RespPkTP fromVal(int v)
		{
			switch(v)
			{
			case 1:
				return start_end ;
			case 2:
				return fixed_len_se;
			default:
				return fixed_len ;
			}
		}
	}
	
	public static enum RespErrTP
	{
		break_link(0),
		keep_link(1);
		
		public final int val ;
		
		RespErrTP(int v)
		{
			val = v ;
		}
		
		public int getVal()
		{
			return this.val ;
		}
		
		public String getTitle()
		{
			return lan.g("tc_rr_resperr_"+this.name()) ;
		}
		
		public static RespErrTP fromVal(int v)
		{
			switch(v)
			{
			case 1:
				return keep_link ;
			default:
				return break_link ;
			}
		}
	}
	
	public static enum LinkEndTP
	{
		recv_one_break(0),
		recv_end_mark(1), //break when find end
		recv_num_break(2),
		keep_link(3); //
		
		public final int val ;
		
		LinkEndTP(int v)
		{
			val = v ;
		}
		
		public int getVal()
		{
			return this.val ;
		}
		
		public String getTitle()
		{
			return lan.g("tc_rr_linkend_"+this.name()) ;
		}
		
		public static LinkEndTP fromVal(int v)
		{
			switch(v)
			{
			case 1:
				return recv_end_mark ;
			case 2:
				return recv_num_break;
			case 3:
				return keep_link;
			default:
				return recv_one_break ;
			}
		}
	}
	
	String server = null ;
	
	int port = 12345 ;
	
	int connTimeoutMS = 3000;

	private transient Socket sock = null;

	private transient InputStream inputS = null;

	private transient OutputStream outputS = null;
	
	ReqTP reqTP = ReqTP.fixed_req;
	
	String reqHex = null ;
	
	//ReqSendTP reqSendTP = ReqSendTP.trigger_in ;
	/*
	 * 
	 */
	HashMap<String,String> reqPm2Hex = null ;
	
	RespPkTP respPkTP = RespPkTP.fixed_len ;
	
	int respPkFixedLen = -1 ;
	
	String respPkStartHex = null ; //pack
	
	String respPkEndHex = null ; //pack
	
	LinkEndTP linkEndTP = LinkEndTP.recv_one_break ; 
	
	RespErrTP respErrTP = RespErrTP.break_link ;
	
	
	static final int OUT_IDX_REQ_OK = 0 ;
	static final int OUT_IDX_RECV_PK = 1 ;
	static final int OUT_IDX_RECV_END = 2 ;
	static final int OUT_IDX_RECV_ERR = 3 ;
	
	@Override
	public int getOutNum()
	{
		return 4;
	}

	
	@Override
	public String getTP()
	{
		return "tcpc_rr";
	}

	@Override
	public String getTPTitle()
	{
		return g("tcpc_rr");
	}

	@Override
	public String getColor()
	{
		return "#e8e7af";
	}

	@Override
	public String getIcon()
	{
		return "\\uf362";
	}
	
	public byte[] getReqBS(MNMsg msg)
	{
		switch(reqTP)
		{
		case fixed_req:
			return Convert.hexStr2ByteArray(this.reqHex) ;
		case pm_req:
			throw new RuntimeException("no impl") ; //my using msg input var
		default:
			return null ;
		}
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.server) || this.port<=0)
		{
			failedr.append("no invalid server host/ip port set") ;
			return false;
		}
		if(reqTP!=ReqTP.no_req)
		{
			if(Convert.isNullOrEmpty(this.reqHex))
			{
				failedr.append("No request hex string set");
				return false;
			}
		}
		
		if(reqTP!=ReqTP.fixed_req)
		{
			failedr.append("not impl");
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("server", this.server);
		jo.put("port",this.port) ;
		jo.put("req_tp",this.reqTP.getVal()) ;
		jo.putOpt("req_hex", reqHex) ;
		if(reqPm2Hex!=null)
		{
			jo.put("pm2hex",reqPm2Hex) ;
		}
		//jo.put("req_send_tp",this.reqSendTP.getVal()) ;
		
		jo.put("resp_pk_tp",this.respPkTP.getVal()) ;
		jo.put("resp_pk_fixedlen",this.respPkFixedLen) ;
		jo.putOpt("resp_pk_start_hex",this.respPkStartHex) ;
		jo.putOpt("resp_pk_end_hex",this.respPkEndHex) ;
		
		jo.put("resp_err_tp",this.respErrTP.getVal()) ;
		jo.put("link_end_tp",this.linkEndTP.getVal()) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.server = jo.optString("server") ;
		this.port = jo.optInt("port",12345) ;
		this.reqTP = ReqTP.fromVal(jo.optInt("req_tp",0)) ;
		this.reqHex = jo.optString("req_hex") ;
		JSONObject tmpjo = jo.optJSONObject("pm2hex") ;
		if(tmpjo!=null)
		{
			this.reqPm2Hex = new HashMap<>() ;
			for(String k:tmpjo.keySet())
			{
				String v = tmpjo.getString(k) ;
				this.reqPm2Hex.put(k,v) ;
			}
		}
		//this.reqSendTP = ReqSendTP.fromVal(jo.optInt("req_send_tp",0)) ;
		this.respPkTP = RespPkTP.fromVal(jo.optInt("resp_pk_tp",0)) ;
		this.respPkFixedLen = jo.optInt("resp_pk_fixedlen",-1) ;
		this.respPkStartHex = jo.optString("resp_pk_start_hex") ;
		this.respPkEndHex = jo.optString("resp_pk_end_hex") ;
		
		this.respErrTP = RespErrTP.fromVal(jo.optInt("resp_err_tp",0)) ;
		
		this.linkEndTP = LinkEndTP.fromVal(jo.optInt("link_end_tp",0)) ;
	}

	/**
	 * trigger send req when msg in ;
	 */
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		byte[] bs = this.RT_do_send_req(msg) ;
		if(bs!=null)
		{
			return RTOut.createOutIdx().asIdxMsg(OUT_IDX_REQ_OK, new MNMsg().asBytesArray(bs)) ;
		}
		return null;
	}

	@Override
	protected void RT_onBeforeNetRun()
	{
		
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case OUT_IDX_REQ_OK:
			return "request send out";
		case OUT_IDX_RECV_PK:
			return "received pack";
		case OUT_IDX_RECV_END:
			return "received end" ;
		case OUT_IDX_RECV_ERR:
			return "received err";
		default:
			return null ;
		}
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		if(idx==OUT_IDX_RECV_ERR)
		{
			return "red" ;
		}
			
		return null ;
	}

	private boolean bRun = false;
	
	private Thread procTh = null ;
	
	private OutputStream chkConnOutput()
	{
		Socket sck = this.sock ;
		if(sck==null || sck.isClosed() || this.outputS==null)
			return null ;
		return this.outputS ;
	}
	
	private synchronized int connect() //throws Exception
	{
		if(Convert.isNullOrEmpty(this.server) || this.port<=0)
			return -1;
		
		if (sock != null)
		{
			if (sock.isClosed())
			{
				try
				{
					disconnect();
				}
				catch ( Exception e)
				{
				}
				return -1 ;
			}

			return 0; //connect
		}
		
		
		if(log.isTraceEnabled())
			log.trace(" NM_TcpClientRR try connect to "+server+":"+port) ;
		
		try
		{

			sock = new Socket(server, port);
			
			//set recv timeout,it will make read waiting throw timeout
			//sock.setSoTimeout(connTimeoutMS);
			
			sock.setTcpNoDelay(true);
			sock.setKeepAlive(true);
			inputS = sock.getInputStream();
			outputS = sock.getOutputStream();
			return 1; //new conn
		}
		catch ( Exception ee)
		{
			if(log.isDebugEnabled())
			{
				log.debug(" ConnPtTcpClient will disconnect by connect err:"+ee.getMessage()) ;
				//ee.printStackTrace(); 
			}
			disconnect();
			//throw ee ;
			return -1 ;
		}
	}

	void disconnect() // throws IOException
	{
		if (sock == null)
			return;

		synchronized (this)
		{
			//System.out.println("ConnPtTcpClient disconnect [" + this.getName());
			try
			{
				try
				{
					if (inputS != null)
						inputS.close();
				}
				catch ( Exception e)
				{
				}

				try
				{
					if (outputS != null)
						outputS.close();
				}
				catch ( Exception e)
				{
				}

				try
				{
					if (sock != null)
						sock.close();
				}
				catch ( Exception e)
				{
				}

			}
			finally
			{
				inputS = null;
				outputS = null;
				sock = null;
			}
		}
	}

	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				while(bRun)
				{
					
//					if(!connect())
//					{
//						UTIL_sleep(1000) ;
//						continue ;
//					}
					int ret ;
					try
					{
						ret = RT_do_recv_resp() ;
					}
					catch(Exception ee)
					{
						ret = RT_on_recv_err(ee.getMessage(),ee) ;
					}
					
					if(ret<0)
						break ;
				}
			}
			finally
			{
				synchronized(this)
				{
					procTh = null ;
					bRun = false;
				}
				
				disconnect();
			}
		}
	};
	
	
	
	private int RT_do_recv_resp()  throws IOException
	{
		OutputStream outputs = chkConnOutput() ;
		InputStream inputs = this.inputS ;
		if(inputs==null)
			return -1;
		
		switch(this.respPkTP)
		{
		case fixed_len:
			return RT_do_recv_fixedlen(inputs);
		case start_end:
		case fixed_len_se:
		default:
			return -1 ;
		}
	}
	
	private transient byte[] RT_curRecvPk = null ;
	private transient int RT_curRecvLen = -1 ;
	
	private int RT_do_recv_fixedlen(InputStream inputs) throws IOException
	{
		if(RT_curRecvPk==null||RT_curRecvPk.length!=this.respPkFixedLen)
		{
			RT_curRecvLen = 0 ;
			RT_curRecvPk = new byte[this.respPkFixedLen] ;
		}
		
		int rlen = inputs.read(RT_curRecvPk, RT_curRecvLen, this.respPkFixedLen-RT_curRecvLen);
		if(rlen<0)
		{//conn broken
			this.disconnect();
			RT_on_recv_end() ;
			return -1;
		}
		RT_curRecvLen += rlen ;
		if(RT_curRecvLen==this.respPkFixedLen)
		{
			RT_on_recv_pk(RT_curRecvPk) ;
			RT_curRecvLen = 0 ;
			return 1 ;
		}
		
		return 0 ;
	}
	
	private int RT_on_recv_err(String err,Exception ee)
	{
		MNMsg msg = new MNMsg().asPayload(err);
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(OUT_IDX_RECV_ERR, msg));
		
		switch(this.respErrTP)
		{
		case break_link:
			this.disconnect();
			return -1 ;
		case keep_link:
			return 0 ;
		}
		return -1 ;//close 
	}
	
	private void RT_on_recv_end()
	{
		MNMsg msg = new MNMsg();
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(OUT_IDX_RECV_END, msg));
	}
	
	private void RT_on_recv_pk(byte[] pk)
	{
		MNMsg msg = new MNMsg().asBytesArray(pk) ;
		
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(OUT_IDX_RECV_PK, msg));
	}
	
	public byte[] RT_do_send_req(MNMsg msg) throws Exception
	{
		byte[] bs = getReqBS(msg) ;
		if(bs==null)
			return null;
		int c_res = connect() ;
		if(c_res<0)
		{
			RT_DEBUG_WARN.fire("tcpc_rr", "Connect to "+this.server+":"+this.port+" error");
			return null;
		}
		RT_DEBUG_WARN.clear("tcpc_rr");
		if(c_res>0)
		{//new conn
			this.RT_start(null) ; //start recv
		}
		RT_curRecvLen = 0 ;
		this.outputS.write(bs);
//		switch(this.reqTP)
//		{
//		case fixed_req:
//			
//			return true;
//		case pm_req:
//			return true;
//		default: //no req
//			return false;
//		}
		return bs ;
	}

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if (bRun)
			return true;

		bRun = true;
		procTh = new Thread(runner);
		procTh.start();
		return true;
	}
	
	
	@Override
	public synchronized void RT_stop()
	{
		Thread th = procTh;
		if (th != null)
			th.interrupt();
		bRun = false;
		procTh = null;
	}

	@Override
	public boolean RT_isRunning()
	{
		return bRun;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return true;
	}
	

	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;
		divsb.append("<div class='rt_blk'>") ;
//		if(lastSendDT>0)
//			divsb.append("&nbsp;Last send ").append(Convert.calcDateGapToNow(lastSendDT));
//		if(lastSaveDT>0)
//			divsb.append("&nbsp;Last save").append(Convert.calcDateGapToNow(lastSaveDT));
//		StationLocSaver locsaver = this.getSaver() ;
//		if(locsaver!=null)
//			divsb.append(" loc saved num="+locsaver.RT_getSavedBufferedNum()) ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk("tcpc_rr",divsb.toString())) ;
		
		super.RT_renderDiv(divblks);
	}
}
