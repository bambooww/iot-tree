package org.iottree.ext.ai.edge;

import java.time.Duration;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IOTTE_Gesture_NM extends MNNodeMid //implements IMNRunner
{
	String cameraId = null ;
	
	long checkIntV = 100 ;
	
	long connTO = 3000 ; //ms
	long readTO = 5000 ; //ms
	
	
	@Override
	public String getTP()
	{
		return "iottree_edge_gest";
	}

	@Override
	public String getTPTitle()
	{
		return g("iottree_edge_gest");
	}

	@Override
	public String getColor()
	{
		return "#5B9CD7";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "\\uf256";
	}
	
	@Override
	public int getOutNum()
	{
		return 2;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.cameraId))
		{
			failedr.append("no camera id set in edge") ;
			return false;
		}

		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.put("camera_id", this.cameraId) ;
		ret.putOpt("check_intv",this.checkIntV) ;
		return ret;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.cameraId = jo.optString("camera_id") ;
		this.checkIntV = jo.optLong("check_intv",100) ;
		if(this.checkIntV<=0)
			this.checkIntV = 100 ;
	}

	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return "detail";
		if(idx==1)
			return "simple";
		return "" ;
	}
	
	@Override
	public String getOutColor(int idx)
	{
		return "yellow";
	}
	

	private transient OkHttpClient httpClient = null ; 
	
	private synchronized OkHttpClient getHttpClient()
	{
		if(httpClient!=null)
			return httpClient ;
		
		return httpClient = new OkHttpClient.Builder()
				//.callTimeout(Duration.ofMinutes(3))
				.connectTimeout(Duration.ofSeconds(connTO))
				.readTimeout(Duration.ofMillis(readTO))
				.build() ;
	}
	

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		if(Convert.isNullOrEmpty(this.cameraId))
			return null;
		//check ai edge
		IOTTreeEdge_M m = (IOTTreeEdge_M)this.getOwnRelatedModule() ;
		String edge_u = m.getEdgeUrl();
		if(Convert.isNullOrEmpty(edge_u))
			return null;
		
		String url = edge_u+"/camera/trigger_process_result?process=gesture&camera_id="+this.cameraId;
		Request request = new Request.Builder()
				.url(url) //.post(req_body)
				.build();
		try (Response response = this.getHttpClient().newCall(request).execute())
		{
			if (!response.isSuccessful())
				throw new RuntimeException("Unexpected code " + response);
			String responseBody = response.body().string();
			//System.out.println("resp="+responseBody);
			JSONObject tmpjo = new JSONObject(responseBody) ;
			if(tmpjo.isEmpty())
				return null;
			//System.out.println(tmpjo) ;
			boolean bsucc = tmpjo.optBoolean("success",false) ;
			if(!bsucc)
				return null;
			JSONObject resjo = transResult(tmpjo) ;
			MNMsg outmsg = new MNMsg().asPayloadJO(tmpjo) ;
			
			if(resjo==null)
			{
				return RTOut.createOutIdx().asIdxMsg(0,outmsg);
			}
			return RTOut.createOutIdx().asIdxMsg(0,outmsg)
					.asIdxMsg(1, new MNMsg().asPayloadJO(resjo));
		}
	}

	private JSONObject transResult(JSONObject jo)
	{
		if(jo==null)
			return null ;
		JSONObject ress = jo.optJSONObject("results") ;
		if(ress==null)
			return null ;
		JSONObject ret = new JSONObject() ;
		for(String k : ress.keySet())
		{
			Object v = ress.get(k) ;
			if(v==null || !(v instanceof JSONObject))
				continue;
			JSONObject jov = (JSONObject)v ;
			String hand_label = jov.optString("hand_label") ;
			if(Convert.isNullOrEmpty(hand_label))
				continue ;
			String hand_cat = jov.optString("hand_category") ;
			if(Convert.isNullOrEmpty(hand_cat) || "None".equals(hand_cat))
				continue;
			ret.put(hand_label,hand_cat) ;
		}
		
		if(ret.isEmpty())
			return null;
		return ret ;
	}

//	private boolean bRun = false;
//	
//	private Thread procTh = null ;
//
//	private Runnable runner = new Runnable()
//	{
//		public void run()
//		{
//			try
//			{
//				
//				while(bRun)
//				{
//					UTIL_sleep(checkIntV) ;
//					
//					//RT_runPID();
//					RT_runInLoop();
//				}
//			}
//			finally
//			{
//				synchronized(this)
//				{
//					procTh = null ;
//					bRun = false;
//				}
//			}
//		}
//	};
//	
//	synchronized private void RT_runInLoop()
//	{
//		if(Convert.isNullOrEmpty(this.cameraId))
//			return ;
//		//check ai edge
//		IOTTreeEdge_M m = (IOTTreeEdge_M)this.getOwnRelatedModule() ;
//		String edge_u = m.getEdgeUrl();
//		if(Convert.isNullOrEmpty(edge_u))
//			return ;
//		
//		String url = edge_u+"/camera/result?camera_id="+this.cameraId;
//		Request request = new Request.Builder()
//				.url(url) //.post(req_body)
//				.build();
//		try (Response response = this.getHttpClient().newCall(request).execute())
//		{
//			if (!response.isSuccessful())
//				throw new RuntimeException("Unexpected code " + response);
//			String responseBody = response.body().string();
//			JSONObject tmpjo = new JSONObject(responseBody) ;
//			//System.out.println(tmpjo) ;
//			boolean bsucc = tmpjo.optBoolean("success",false) ;
//			if(!bsucc)
//				return ;
//			
//			MNMsg msg = new MNMsg().asPayloadJO(tmpjo) ;
//			RTOut rto  = RTOut.createOutIdx().asIdxMsg(0, msg) ;
//			this.RT_sendMsgOut(rto);
//		}
//		catch(Exception ee)
//		{
//			if(IOTTreeEdge_M.LOG.isDebugEnabled())
//				IOTTreeEdge_M.LOG.debug("IOTTE_Gesture_NS.RT_runInLoop",ee);
//			else
//				IOTTreeEdge_M.LOG.error(ee.getMessage());
//		}
//	}
//	
//
//	@Override
//	public synchronized boolean RT_start(StringBuilder failedr)
//	{
//		if (bRun)
//			return true;
//
//		bRun = true;
//		procTh = new Thread(runner);
//		procTh.start();
//		return true;
//	}
//
//	@Override
//	public synchronized void RT_stop()
//	{
//		Thread th = procTh;
//		if (th != null)
//			th.interrupt();
//		bRun = false;
//		procTh = null;
//	}
//
//	@Override
//	public boolean RT_isRunning()
//	{
//		return bRun;
//	}
//
//	@Override
//	public boolean RT_isSuspendedInRun(StringBuilder reson)
//	{
//		return false;
//	}
//	
//	/**
//	 * false will not support runner
//	 * @return
//	 */
//	public boolean RT_runnerEnabled()
//	{
//		return true ;
//	}
//	
//	/**
//	 * true will not support manual trigger to start
//	 * @return
//	 */
//	public boolean RT_runnerStartInner()
//	{
//		return false;
//	}

}
