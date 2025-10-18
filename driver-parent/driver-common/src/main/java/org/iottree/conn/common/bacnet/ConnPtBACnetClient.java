package org.iottree.conn.common.bacnet;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import org.iottree.core.Config;
import org.iottree.core.UACh;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.conn.ConnPtBinder;
import org.iottree.core.conn.ConnPtBinder.BindItem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnPtBACnetClient extends ConnPtBinder
{
	static ILogger log = LoggerManager.getLogger(ConnPtBACnetClient.class) ;
	
	public static final String CLIENT_APP_URI = "urn:iottree:client";
	
	/**
	 * 验证类型
	 * @author jason.zhu
	 *
	 */
	public static enum AuthTP
	{
		anony, //Anonymous
		user_psw;  // User Details
	}
	
	
	public static class EndpointPk
	{
		String endpointUrl ;
		
		
		
		public String getEndpointUrl()
		{
			return this.endpointUrl ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("url", this.endpointUrl) ;
//			jo.put("sp_name", secPolicy.name()) ;
//			jo.put("sm_name", secMode.name()) ;
			return jo ;
		}
	}
	
	public static class ServerPk
	{
		String appName ;
		
		String appUri ;
		
		ArrayList<EndpointPk> endptPks = new ArrayList<>() ;
		
		public ServerPk(String app_n,String app_uri)
		{
			this.appName = app_n ;
			this.appUri = app_uri ;
		}
		
		public String getAppName()
		{
			return this.appName ;
		}
		
		public List<EndpointPk> getEndpointPks()
		{
			return this.endptPks ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject();
			jo.put("app_n", this.appName) ;
			jo.put("app_uri", this.appUri) ;
			JSONArray jarr = new JSONArray() ;
			jo.put("endpoints",jarr) ;
			for(EndpointPk ept:this.endptPks)
			{
				jarr.put(ept.toJO()) ;
			}
			return jo ;
		}
	}

	String endpointUri = "";
	// String appName = null;
	// tcp http https ws wss
	// String opcProtocal = "tcp";
	//
	// String opcHost = "";
	//
	// int opcPort = 49320;
	//
	// // "" or /xxx
	// String opcPath = "" ;


	int reqTimeout = 5000;
	
	AuthTP authTP = AuthTP.anony ;

	String idUser = null;

	String idPsw = null;

	long updateIntMs = 3000;

	@Override
	public String getConnType()
	{
		return "opc_ua";
	}

	@Override
	public String getStaticTxt()
	{
		return null;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		// xd.setParamValue("opc_app_name", this.appName);
		// xd.setParamValue("opc_proto", this.opcProtocal);
		// xd.setParamValue("opc_host", this.opcHost);
		// xd.setParamValue("opc_port", this.opcPort);
		// xd.setParamValue("opc_path", this.opcPath);
		xd.setParamValue("opc_epu", this.endpointUri);

		xd.setParamValue("opc_req_to", this.reqTimeout);
		xd.setParamValue("auth_tp", authTP.name());
		xd.setParamValue("opc_user", this.idUser);
		xd.setParamValue("opc_psw", this.idPsw);
		xd.setParamValue("int_ms", this.updateIntMs);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		// this.appName = xd.getParamValueStr("opc_app_name",
		// "iottree_opc_client_"+this.getName());
		// this.opcProtocal = xd.getParamValueStr("opc_proto", "tcp");
		// this.opcHost = xd.getParamValueStr("opc_host", "");
		// this.opcPort = xd.getParamValueInt32("opc_port", 49320);
		// this.opcPath = xd.getParamValueStr("opc_path", "");
		this.endpointUri = xd.getParamValueStr("opc_epu", "");
		String opc_sp = xd.getParamValueStr("opc_sp", null);
		
		int opc_mm = xd.getParamValueInt32("opc_sec_m", 1);
		//if (Convert.isNotNullEmpty(opc_mm))

		this.reqTimeout = xd.getParamValueInt32("opc_req_to", 5000);
		String authtp = xd.getParamValueStr("auth_tp", "");
		if(Convert.isNullOrEmpty(authtp))
			this.authTP = AuthTP.anony ;
		else
			this.authTP = AuthTP.valueOf(authtp) ;
		this.idUser = xd.getParamValueStr("opc_user", "");
		this.idPsw = xd.getParamValueStr("opc_psw", "");
		this.updateIntMs = xd.getParamValueInt64("int_ms", 3000);
		if (this.updateIntMs <= 0)
			updateIntMs = 3000;
		return r;
	}

	private String optJSONString(JSONObject jo, String name, String defv)
	{
		String r = jo.optString(name);
		if (r == null)
			return defv;
		return r;
	}

	private int optJSONInt(JSONObject jo, String name, int defv)
	{
		Object v = jo.opt(name);
		if (v == null)
			return defv;
		return jo.optInt(name);
	}

	private long optJSONInt64(JSONObject jo, String name, long defv)
	{
		Object v = jo.opt(name);
		if (v == null)
			return defv;
		return jo.optLong(name);
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);

		// this.appName =optJSONString(jo,"opc_app_name",getOpcAppNameDef()) ;
		// this.opcProtocal = optJSONString(jo, "opc_proto", "tcp");
		// this.opcHost = optJSONString(jo, "opc_host", "");
		// this.opcPort = optJSONInt(jo, "opc_port", 49320);
		// this.opcPath = optJSONString(jo, "opc_path", "");
		this.endpointUri = optJSONString(jo, "opc_epu", "");
		String opc_sp = optJSONString(jo, "opc_sp", null);
		
		this.reqTimeout = optJSONInt(jo, "opc_req_to", 5000);
		String authtp = jo.optString("auth_tp", "");
		if(Convert.isNullOrEmpty(authtp))
			this.authTP = AuthTP.anony ;
		else
			this.authTP = AuthTP.valueOf(authtp) ;
		this.idUser = optJSONString(jo, "opc_user", "");
		this.idPsw = optJSONString(jo, "opc_psw", "");
		this.updateIntMs = optJSONInt64(jo, "int_ms", 3000);
	}

	public List<String> transBindIdToPath(String bindid)
	{
		return null;
	}

	private String getOpcAppNameDef()
	{
		return "iottree_opc_client_" + this.getName();
	}

	// public String getOpcAppName()
	// {
	// if(this.appName==null)
	// return getOpcAppNameDef();
	// return appName ;
	// }

	// public String getOpcProtocal()
	// {
	// if(Convert.isNullOrEmpty(this.opcProtocal))
	// return "tcp";
	// return this.opcProtocal;
	// }
	//
	// public String getOpcHost()
	// {
	// return this.opcHost;
	// }
	//
	// public int getOpcPort()
	// {
	// return this.opcPort;
	// }
	//
	// public String getOpcPortStr()
	// {
	// if (this.opcPort <= 0)
	// return "";
	// return "" + this.opcPort;
	// }
	//
	// public String getOpcPath()
	// {
	// return opcPath ;
	// }

	public String getOpcEndPointURI()
	{
		// return "opc."+this.getOpcProtocal()+"://" + this.opcHost + ":" +
		// this.opcPort+this.opcPath;
		return this.endpointUri;
	}


	public int getOpcReqTimeout()
	{
		return reqTimeout;
	}

	public long getUpdateIntMs()
	{
		return this.updateIntMs;
	}
	
	public AuthTP getOpcAuthTP()
	{
		return this.authTP ;
	}

	public String getOpcIdUser()
	{
		if (this.idUser == null)
			return "";
		return this.idUser;
	}

	public String getOpcIdPsw()
	{
		if (this.idPsw == null)
			return "";
		return this.idPsw;
	}


	public void clearBindBeSelectedCache()
	{

	}

	@Override
	public List<BindItem> getBindBeSelectedItems() throws Exception
	{


		
		ArrayList<BindItem> rets = new ArrayList<>();

//		String rootname = this.getOpcEndPointURI();
//
//		List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(root);
//		if (nodes != null && nodes.size() > 0)
//		{
//			for (UaNode node : nodes)
//			{
//				String bn = node.getBrowseName().getName();
//				if (bn.startsWith("_"))
//					continue;
//
//				listBindBeSelectedItems(rets, uaClient, "", node);
//			}
//		}
		return rets;
	}

	@Override
	public boolean supportBindBeSelectTree()
	{
		return true;
	}

	@Override
	public void writeBindBeSelectTreeRoot(Writer w) throws Exception
	{
//		if (uaClient == null)
//		{
//			throw new Exception("no UaClient connected");
//		}
//
//		// find /Root/Objects nodes
//		UaNode root = findUaNodeByPath(uaClient, new String[] { "Objects" });
//		if (root == null)
//			throw new Exception("no /Root/Objects/ node found");
//
//		w.write("{\"id\":\"" + UUID.randomUUID().toString() + "\"");
//		w.write(",\"nc\":0");
//		w.write(",\"icon\": \"fa fa-sitemap fa-lg\"");
//
//		w.write(",\"text\":\"" + this.getOpcEndPointURI() + "\"");
//		w.write(",\"state\": {\"opened\": true}");
//
//		List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(root);
//		if (nodes != null && nodes.size() > 0)
//		{
//			w.write(",\"children\":[");
//			//
//			boolean bfirst = true;
//			for (UaNode node : nodes)
//			{
//				String bn = node.getBrowseName().getName();
//				if (bn.startsWith("_"))
//					continue;
//
//				if (bfirst)
//					bfirst = false;
//				else
//					w.write(',');
//
//				writeBindBeSelectTreeNode(w, node);
//			}
//			w.write("]");
//		}
//		w.write("}");
	}

	@Override
	public void writeBindBeSelectTreeSub(Writer w, String pnode_id) throws Exception
	{
//		NodeId pnid = null;
//		if (Convert.isNullOrEmpty(pnode_id))
//			pnid = NodeIds.RootFolder;
//		else
//			pnid = NodeId.parse(pnode_id);
//		if (pnid == null)
//			return;
//		if (uaClient == null)
//		{
//			throw new Exception("no UaClient connected");
//		}
//		UaNode pn = uaClient.getAddressSpace().getNode(pnid);
//		if (pn == null)
//			return;
//		List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(pn);
//		w.write("[");
//		if (nodes != null && nodes.size() > 0)
//		{
//
//			//
//			boolean bfirst = true;
//			for (UaNode node : nodes)
//			{
//				String bn = node.getBrowseName().getName();
//				if (bn.startsWith("_"))
//					continue;
//
//				if (bfirst)
//					bfirst = false;
//				else
//					w.write(',');
//
//				writeBindBeSelectTreeNode(w, node);
//			}
//
//		}
//
//		w.write("]");
	}

	
	@Override
	public void writeBindBeSelectedTreeJson(Writer w, boolean list_tags_only, boolean force_refresh) throws Exception
	{
		//writeUaNodeTreeJson(w, true, force_refresh);
	}

	@Override
	protected List<String> transBindIdToConnLeafPath(String bindid)
	{
		// this.get
		return null;
	}

	private boolean connect() // throws UaException
	{
		
		return true;
	}

	@Override
	protected void RT_connInit() throws Exception
	{
		super.RT_connInit();

//		HashMap<String, NodeId> t2n = new HashMap<>();
//
//		Map<String, String> bindm = this.getBindMap();
//		for (Map.Entry<String, String> t2p : bindm.entrySet())
//		{
//			String tagp = t2p.getKey();
//			String bindp = t2p.getValue();
//			int k = bindp.lastIndexOf(":");
//			if (k > 0)
//				bindp = bindp.substring(0, k);
//			k = tagp.lastIndexOf(":");
//			if (k > 0)
//				tagp = tagp.substring(0, k);
//			NodeId pnid = null;
//			pnid = NodeId.parse(bindp); //s=id
//			if (pnid == null)
//				continue;
//			t2n.put(tagp, pnid);
//		}
//
//		tag2nodeid = t2n;

	}

	transient private long lastReadData = -1;

	private void readDataInLoop()
	{
		if (System.currentTimeMillis() - lastReadData < updateIntMs)
			return;

		try
		{
			UACh ch = this.getJoinedCh();
			if (ch == null)
				return;

			if (!isConnReady())
				return;

			// uaClient.connect().get();

//			for (Map.Entry<String, NodeId> tag2n : tag2nodeid.entrySet())
//			{
//				NodeId nodeid = tag2n.getValue();
//				UShort us = nodeid.getNamespaceIndex();
//				Object id = nodeid.getIdentifier();
//				//DataValue v = uaClient.readValue(maxAge, timestampsToReturn, nodeId)
//				DataValue v = uaClient.readValue(0.0, TimestampsToReturn.Both, nodeid);//.get()
//				updateTagVal(ch, tag2n.getKey(), v);
//			}
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug(e);
			//System.out.println("read data err=" + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			lastReadData = System.currentTimeMillis();
		}
	}
	
	private long lastChk = -1;

	public void RT_checkConn() 
	{
		if (System.currentTimeMillis() - lastChk < 5000)
			return;

		try
		{
			connect();
		}
		finally
		{
			lastChk = System.currentTimeMillis();
		}
	}

	void checkConn()
	{
		RT_checkConn();
		readDataInLoop();
	}

	@Override
	public boolean isConnReady()
	{
		return false;//uaClient != null;
	}

	public String getConnErrInfo()
	{
		
			return null;
	}

	synchronized void disconnect() // throws IOException
	{
		
	}
	
	public boolean RT_supportSendMsgOut()
	{
		return false;
	}

	@Override
	public boolean RT_sendMsgOut(String topic,byte[] msg,StringBuilder failedr) throws Exception
	{
		return false;
	}

	@Override
	public void RT_writeValByBind(String tagpath, String strv)
	{
		// TODO Auto-generated method stub
		
	}
}
