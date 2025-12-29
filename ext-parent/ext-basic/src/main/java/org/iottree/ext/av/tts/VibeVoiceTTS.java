package org.iottree.ext.av.tts;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class VibeVoiceTTS extends MNNodeMid
{
	static ILogger log = LoggerManager.getLogger(VibeVoiceTTS.class);

	private transient long lastChk = -1;

	WebSockClient wsClient = null;

	String host = "localhost";

	int port = 3000;

	float cfg = 1.5f ;
	
	String voice = "en-Carter_man" ;
	
	int steps = 5 ;
//	/**
//	 * play voice in node
//	 */
//	boolean bNodePlayVoice = false;

	@Override
	public int getOutNum()
	{
		return 2;
	}

	@Override
	public String getTP()
	{
		return "tts_vibe_voice";
	}

	@Override
	public String getTPTitle()
	{
		return "TTS-Vibe Voice";
	}

	@Override
	public String getColor()
	{
		return "#11caff";
	}

	@Override
	public String getIcon()
	{
		return "\\uf027";
	}
	
	public String getHost()
	{
		if(this.host==null)
			return "" ;
		return this.host ;
	}
	
	public int getPort()
	{
		return this.port ;
	}
	
	public float getCfg()
	{
		return this.cfg ;
	}
	
	public int getSteps()
	{
		return this.steps ;
	}
	
	public String getVoice()
	{
		if(this.voice==null)
			return "" ;
		return this.voice ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.host) || this.port<=0)
		{
			failedr.append("no valid host:port set") ;
			return false;
		}
		if(Convert.isNullOrEmpty(voice))
		{
			failedr.append("no speaker set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject ret = new JSONObject();
		ret.putOpt("host",host) ;
		ret.put("port",port) ;
		ret.put("cfg",cfg) ;
		ret.put("steps",steps) ;
		ret.putOpt("voice",this.voice) ;
		return ret;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.host = jo.optString("host") ;
		this.port =  jo.optInt("port", 3000) ;
		this.cfg = jo.optFloat("cfg",1.5f) ;
		this.steps = jo.optInt("steps", 5) ;
		this.voice = jo.optString("voice") ;
	}

	public boolean isConnReady()
	{
		if (this.wsClient == null)
			return false;
		return wsClient.getReadyState() == ReadyState.OPEN;
	}

	public String getConnErrInfo()
	{
		return null;
	}

	synchronized void disconnect() // throws IOException
	{
		WebSockClient ss = wsClient;
		if (ss == null)
			return;

		try
		{
			ss.close();
		}
		catch ( Exception e)
		{
		}
		finally
		{
			wsClient = null;
		}
	}

	private String getUrlWS(String txt) throws UnsupportedEncodingException
	{
		if (Convert.isNullOrEmpty(this.host)||Convert.isNullOrEmpty(txt))
			return null;
		return "ws://" + this.host + ":" + this.port+"/stream?text="
			+URLEncoder.encode(txt, "UTF-8")+"&cfg="+this.cfg+"&steps="+this.steps+"&voice="+this.voice;
	}
	
	private String getUrlConfig()
	{
		if (Convert.isNotNullEmpty(this.host))
			return null;
		return "http://" + this.host + ":" + this.port+"/config";
	}

	public static JSONObject RT_readConfig(String host,int port) throws IOException
	{
		String uuu = "http://" + host + ":" + port+"/config"; //this.getUrlConfig();
		if (Convert.isNullOrEmpty(uuu))
			return null;
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		HttpGet git = new HttpGet(uuu);
		// String result = "";
		try (CloseableHttpClient chc = httpClientBuilder.build())
		{
			HttpResponse resp = chc.execute(git);
			InputStream respIs = resp.getEntity().getContent();
			byte[] bs = IOUtils.toByteArray(respIs);

			// result = new String(rbs, this.getEncod());
			// return result;
			String jstr = new String(bs, "UTF-8");
			return new JSONObject(jstr);
		}
		// Convert.
	}

	private boolean connectToWS(String txt) throws Exception
	{
		String uuu = this.getUrlWS(txt);
		if (Convert.isNullOrEmpty(uuu))
			return false;
		
		WebSockClient ss = wsClient;
		if (ss != null)
		{
			this.disconnect();
		}

		if (this.wsClient == null)
		{
			this.wsClient = new WebSockClient(new URI(uuu));
		}

		this.wsClient.connect();
		return true ;
	}
	
	

//	public void RT_checkConn()
//	{
//		if (System.currentTimeMillis() - lastChk < 5000)
//			return;
//
//		try
//		{
//			connectToWS();
//		}
//		catch ( Exception e)
//		{
//			if (log.isDebugEnabled())
//				log.debug("connect to websocket", e);
//		}
//		finally
//		{
//			lastChk = System.currentTimeMillis();
//		}
//	}

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
			onRecvedMsgTxt(message);
		}

		public void onMessage(ByteBuffer bytes)
		{
			try
			{
				VibeVoiceTTS.this.onRecvedMsg(bytes.array());
			}
			catch ( Exception e)
			{
				e.printStackTrace();
				RT_DEBUG_ERR.fire("websocket", "onMessage",e);
			}
		}

		@Override
		public void onClose(int code, String reason, boolean remote)
		{

		}

		@Override
		public void onError(Exception ex)
		{
			RT_DEBUG_ERR.fire("websocket", "onError",ex);
		}

//		private boolean checkAndConn() throws Exception
//		{
//			switch (this.getReadyState())
//			{
//			case NOT_YET_CONNECTED:
//				if (isClosed())
//					return reconnectBlocking();
//				else
//					return connectBlocking();
//			case OPEN:
//
//				return true;
//
//			case CLOSED:
//				return reconnectBlocking();
//			default:
//				// Thread.sleep(5000);
//				return false;
//			}
//		}
	}
	

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		String txt = msg.getPayloadStr() ;
		if(Convert.isNullOrEmpty(txt))
			return null ;
		if(!this.connectToWS(txt))
			RT_DEBUG_ERR.fire("conn","connect to VibeVoice Host err");
		else
			RT_DEBUG_ERR.clear("conn");
		return null;
	}
	
	@Override
	public String getPmTitle()
	{
		if(Convert.isNullOrEmpty(this.host))
			return "" ;
		return this.host+":"+this.port;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "audio bytes 24000";
		case 1:
			return "message txt";
		default:
			return null ;
		}
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		switch(idx)
		{
		case 0:
			return "blue";
		case 1:
			return "#17c6a3";
		default:
			return null ;
		}
	}
	
	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}


	public boolean sendMsg(String topic, byte[] bs) throws Exception
	{
		if (this.wsClient == null)
			return false;
		this.wsClient.send(ByteBuffer.wrap(bs));
		return true;
	}

	private void onRecvedMsgTxt(String msg)
	{
		MNMsg m = new MNMsg().asPayload(msg);
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(1, m));
	}

	private void onRecvedMsg(byte[] bs) throws Exception
	{
		MNMsg msg = new MNMsg().asBytesArray(bs);
		RTOut rto = RTOut.createOutIdx().asIdxMsg(0, msg);
		this.RT_sendMsgOut(rto);
	}

	static private void connToVibeVoiceWS(String host, int port)
	{

	}

	
}
