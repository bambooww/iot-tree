package org.iottree.core.conn;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscriptionManager;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.FolderTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaDataTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableTypeNode;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.Node;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.iottree.core.Config;
import org.iottree.core.ConnPt;
import org.iottree.core.UACh;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.conn.ConnPtBinder.BindItem;
import org.iottree.core.conn.opcua.KeyStoreLoader;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

public class ConnPtOPCUA extends ConnPtBinder
{
	public static enum SecurityPolicy
	{
		basic256sha256, basic256, basic128rsa15, none;

		public String getTitle()
		{
			switch (this)
			{
			case basic256sha256:
				return "Basic256Sha256";
			case basic256:
				return "Basic256 (Deprecated)";
			case basic128rsa15:
				return "Basic128Rsa15 (Deprecated)";
			default:
				return "None";
			}
		}
	}

	/**
	 * SecurityPolicy != none ,it will be used
	 * 
	 * @author jason.zhu
	 *
	 */
	public static enum MessageMode
	{
		sign, sign_encrypt;

		public String getTitle()
		{
			switch (this)
			{
			case sign:
				return "Sign";
			case sign_encrypt:
				return "Sign and Encrypt";
			default:
				return "";
			}
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

	SecurityPolicy securityPolicy = SecurityPolicy.none;

	MessageMode msgMode = null;

	int reqTimeout = 5000;

	String idUser = null;

	String idPsw = null;

	long updateIntMs = 3000;

	private transient OpcUaClient uaClient = null;

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
		if (securityPolicy != null)
			xd.setParamValue("opc_sp", securityPolicy.name());
		if (msgMode != null)
			xd.setParamValue("opc_mm", msgMode.name());
		xd.setParamValue("opc_req_to", this.reqTimeout);
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
		if (Convert.isNotNullEmpty(opc_sp))
			securityPolicy = SecurityPolicy.valueOf(opc_sp);
		if (securityPolicy == null)
			securityPolicy = SecurityPolicy.none;

		String opc_mm = xd.getParamValueStr("opc_mm", null);
		if (Convert.isNotNullEmpty(opc_mm))
			msgMode = MessageMode.valueOf(opc_mm);

		this.reqTimeout = xd.getParamValueInt32("opc_req_to", 5000);
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
		if (Convert.isNotNullEmpty(opc_sp))
			securityPolicy = SecurityPolicy.valueOf(opc_sp);
		if (securityPolicy == null)
			securityPolicy = SecurityPolicy.none;

		String opc_mm = optJSONString(jo, "opc_mm", null);
		if (Convert.isNotNullEmpty(opc_mm))
			msgMode = MessageMode.valueOf(opc_mm);
		this.reqTimeout = optJSONInt(jo, "opc_req_to", 5000);
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

	public SecurityPolicy getOpcSP()
	{
		return this.securityPolicy;
	}

	public MessageMode getOpcMsgMode()
	{
		return this.msgMode;
	}

	public int getOpcReqTimeout()
	{
		return reqTimeout;
	}

	public long getUpdateIntMs()
	{
		return this.updateIntMs;
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

	Predicate<EndpointDescription> endpointFilter()
	{
		return e -> true;
	}

	private UsernameProvider getIdPro()
	{
		UsernameProvider idp = null;
		if (Convert.isNotNullEmpty(this.idUser))
			idp = new UsernameProvider(this.idUser, idPsw);
		return idp;
	}

	UaSubscriptionManager.SubscriptionListener subLis = new UaSubscriptionManager.SubscriptionListener() {

		public void onSubscriptionTransferFailed(UaSubscription subscription, StatusCode statusCode)
		{
			System.out.println("onSubscriptionTransferFailed");
		}
	};

	private static List<UaNode> browserNodes(OpcUaClient client) throws Exception
	{
		client.connect().get();

		ArrayList<UaNode> rets = new ArrayList<>();
		// start browsing at root folder
		browseNode(rets, "", client, Identifiers.RootFolder);

		return rets;
	}

	private static void browseNode(List<UaNode> uanodes, String indent, OpcUaClient client, NodeId browseRoot)
	{
		try
		{
			List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(browseRoot);

			for (UaNode node : nodes)
			{
				// logger.info("{} Node={}", indent,
				// node.getBrowseName().getName());
				uanodes.add(node);
				// recursively browse to children
				browseNode(uanodes, indent + "  ", client, node.getNodeId());
			}
		}
		catch ( UaException e)
		{
			// logger.error("Browsing nodeId={} failed: {}", browseRoot,
			// e.getMessage(), e);
		}
	}

	public static void browseNodesOut(Writer w, OpcUaClient client) throws Exception
	{
		// client.getAddressSpace().
		UaNode uan = client.getAddressSpace().getNode(Identifiers.RootFolder);
		if (uan == null)
			return;
		browseNodesOut(w, "", client, uan);
	}

	public static void browseNodesOut(Writer w, String prefix, OpcUaClient client, UaNode curnode) throws Exception
	{

		String p = prefix + curnode.getBrowseName().getName();
		w.write(p + "   " + curnode.getNodeId().toParseableString() + "<br/>");
		List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(curnode);

		String subp = p + ".";
		for (UaNode node : nodes)
		{
			String bn = node.getBrowseName().getName();
			if (bn.startsWith("_"))
				continue;
			// logger.info("{} Node={}", indent, );
			// uanodes.add(node);
			// recursively browse to children
			browseNodesOut(w, subp, client, node);
		}
	}

	public static void writeSubNodesJSON(Writer w, OpcUaClient client, String curnodeid) throws Exception
	{
		NodeId nid = NodeId.parse(curnodeid);
		if (nid == null)
			return;
		UaNode nd = client.getAddressSpace().getNode(nid);
		// String p = prefix+curnode.getBrowseName().getName();
		// w.write(p+" "+curnode.getNodeId().toParseableString()+"<br/>");

	}

	public static List<EndpointDescription> getEndpointsByUri(String uri)
			throws InterruptedException, ExecutionException
	{
		return DiscoveryClient.getEndpoints(uri).get();
	}

	public static List<EndpointDescription> getEndpointsByPort(int port) throws Exception
	{
		return getEndpointsByUri("opc.tcp://localhost:" + port);
	}

	public void writeUaNodeTreeJson(Writer w, String nodeid) throws Exception
	{
		if (uaClient == null)
		{
			throw new Exception("no UaClient connected");
		}
		writeUaNodeTreeJson(w, uaClient, nodeid);
	}

	public void clearBindBeSelectedCache()
	{

	}

	@Override
	public List<BindItem> getBindBeSelectedItems() throws Exception
	{
		if (uaClient == null)
		{
			throw new Exception("no UaClient connected");
		}

		// find /Root/Objects nodes
		UaNode root = findUaNodeByPath(uaClient, new String[] { "Objects" });
		if (root == null)
			throw new Exception("no /Root/Objects/ node found");
		ArrayList<BindItem> rets = new ArrayList<>();

		String rootname = this.getOpcEndPointURI();

		List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(root);
		if (nodes != null && nodes.size() > 0)
		{
			for (UaNode node : nodes)
			{
				String bn = node.getBrowseName().getName();
				if (bn.startsWith("_"))
					continue;

				listBindBeSelectedItems(rets, uaClient, "", node);
			}
		}
		return rets;
	}

	private void listBindBeSelectedItems(List<BindItem> bis, OpcUaClient client, String ppath, UaNode n)
			throws Exception
	{
		NodeId nid = n.getNodeId();
		String bn = n.getBrowseName().getName();
		if (n instanceof UaVariableNode)
		{
			UaVariableNode uvn = (UaVariableNode) n;

			DataValue dataval = uvn.getValue();
			Object valob = dataval.getValue().getValue();
			if (valob != null && valob.getClass().isArray())
			{
				Object[] objectArray = (Object[]) valob;
				valob = Arrays.deepToString(objectArray);
			}
			String val_dt = Convert.toFullYMDHMS(dataval.getSourceTime().getJavaDate());

			NodeId dt = uvn.getDataType();
			UaVariableTypeNode vtn = uvn.getTypeDefinition();
			UInteger[] arrdim = vtn.getArrayDimensions();
			UaDataTypeNode tpnode = (UaDataTypeNode) uaClient.getAddressSpace().getNode(dt);
			String datatp = tpnode.getBrowseName().getName();

			String arr_dim = "";
			if (arrdim != null && arrdim.length > 0)
			{
				arr_dim = Convert.combineWith(arrdim, ',');
			}

			BindItem bi = new BindItem(ppath + "/" + bn, datatp);
			bi.setVal(valob);
			bis.add(bi);
			return;
		}

		List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(n);
		if (nodes != null && nodes.size() > 0)
		{
			if (Convert.isNullOrEmpty(ppath))
				ppath = bn;
			else
				ppath += "/" + bn;
			for (UaNode node : nodes)
			{
				String bn0 = node.getBrowseName().getName();
				if (bn0.startsWith("_"))
					continue;

				listBindBeSelectedItems(bis, client, ppath, node);
			}

		}

	}

	@Override
	public boolean supportBindBeSelectTree()
	{
		return true;
	}

	@Override
	public void writeBindBeSelectTreeRoot(Writer w) throws Exception
	{
		if (uaClient == null)
		{
			throw new Exception("no UaClient connected");
		}

		// find /Root/Objects nodes
		UaNode root = findUaNodeByPath(uaClient, new String[] { "Objects" });
		if (root == null)
			throw new Exception("no /Root/Objects/ node found");

		w.write("{\"id\":\"" + UUID.randomUUID().toString() + "\"");
		w.write(",\"nc\":0");
		w.write(",\"icon\": \"fa fa-sitemap fa-lg\"");

		w.write(",\"text\":\"" + this.getOpcEndPointURI() + "\"");
		w.write(",\"state\": {\"opened\": true}");

		List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(root);
		if (nodes != null && nodes.size() > 0)
		{
			w.write(",\"children\":[");
			//
			boolean bfirst = true;
			for (UaNode node : nodes)
			{
				String bn = node.getBrowseName().getName();
				if (bn.startsWith("_"))
					continue;

				if (bfirst)
					bfirst = false;
				else
					w.write(',');

				writeBindBeSelectTreeNode(w, node);
			}
			w.write("]");
		}
		w.write("}");
	}

	@Override
	public void writeBindBeSelectTreeSub(Writer w, String pnode_id) throws Exception
	{
		NodeId pnid = null;
		if (Convert.isNullOrEmpty(pnode_id))
			pnid = Identifiers.RootFolder;
		else
			pnid = NodeId.parse(pnode_id);
		if (pnid == null)
			return;
		if (uaClient == null)
		{
			throw new Exception("no UaClient connected");
		}
		UaNode pn = uaClient.getAddressSpace().getNode(pnid);
		if (pn == null)
			return;
		List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(pn);
		w.write("[");
		if (nodes != null && nodes.size() > 0)
		{

			//
			boolean bfirst = true;
			for (UaNode node : nodes)
			{
				String bn = node.getBrowseName().getName();
				if (bn.startsWith("_"))
					continue;

				if (bfirst)
					bfirst = false;
				else
					w.write(',');

				writeBindBeSelectTreeNode(w, node);
			}

		}

		w.write("]");
	}

	private void writeBindBeSelectTreeNode(Writer w, UaNode n) throws Exception
	{

		// boolean bvar = n instanceof UaVariableNode;
		NodeId nid = n.getNodeId();
		w.write("{\"id\":\"" + nid.toParseableString() + "\"");
		w.write(",\"nc\":" + n.getNodeClass().getValue());
		if (n instanceof UaVariableNode)
		{
			w.write(",\"tp\": \"tag\"");
			w.write(",\"icon\": \"fa fa-tag fa-lg\"");

			// if("BooleanArray".contentEquals(node.getBrowseName().getName()))
			// {
			// System.out.println("1");
			// }
			UaVariableNode uvn = (UaVariableNode) n;
			DataValue dataval = uvn.getValue();
			Object valob = dataval.getValue().getValue();
			if (valob != null && valob.getClass().isArray())
			{
				Object[] objectArray = (Object[]) valob;
				valob = Arrays.deepToString(objectArray);
			}
			String serverdt = Convert.toFullYMDHMS(dataval.getSourceTime().getJavaDate());
			w.write(",\"val\":\"" + valob + "\"");
			w.write(",\"val_dt\":\"" + serverdt + "\"");

			NodeId dt = uvn.getDataType();
			UaVariableTypeNode vtn = uvn.getTypeDefinition();
			UInteger[] arrdim = vtn.getArrayDimensions();
			UaDataTypeNode tpnode = (UaDataTypeNode) uaClient.getAddressSpace().getNode(dt);
			String vt = tpnode.getBrowseName().getName();
			w.write(",\"vt\":\"" + vt + "\"");
			if (arrdim != null && arrdim.length > 0)
			{
				w.write(",\"arr_dim\":\"" + Convert.combineWith(arrdim, ',') + "\"");
			}
			w.write(",\"text\":\"" + n.getDisplayName().getText() + ":" + vt + "\"}");
		}
		else
		{
			w.write(",\"tp\": \"tagg\"");
			w.write(",\"icon\": \"fa fa-folder fa-lg\"");
			w.write(",\"children\": true ");
			w.write(",\"text\":\"" + n.getDisplayName().getText() + "\"}");
		}

	}

	@Override
	public void writeBindBeSelectedTreeJson(Writer w, boolean list_tags_only, boolean force_refresh) throws Exception
	{
		writeUaNodeTreeJson(w, true, force_refresh);
	}

	public void writeUaNodeTreeJson(Writer w, boolean b_var, boolean force_refresh) throws Exception
	{
		if (uaClient == null)
		{
			throw new Exception("no UaClient connected");
		}

		// find /Root/Objects nodes
		UaNode root = findUaNodeByPath(uaClient, new String[] { "Objects" });
		if (root == null)
			throw new Exception("no /Root/Objects/ node found");

		// UaObjectNode uaou= new UaObjectNode(
		// uaClient,
		// getNodeId(),
		// getBrowseName(),
		// getDisplayName(),
		// getDescription(),
		// getWriteMask(),
		// getUserWriteMask(),
		// getEventNotifier()
		// );

		// NodeId nid = n.getNodeId() ;

		w.write("{\"id\":\"" + UUID.randomUUID().toString() + "\"");
		w.write(",\"nc\":0");
		w.write(",\"icon\": \"fa fa-sitemap fa-lg\"");

		w.write(",\"text\":\"" + this.getOpcEndPointURI() + "\"");
		w.write(",\"state\": {\"opened\": true}");

		List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(root);
		if (nodes != null && nodes.size() > 0)
		{
			w.write(",\"children\":[");
			//
			boolean bfirst = true;
			for (UaNode node : nodes)
			{
				String bn = node.getBrowseName().getName();
				if (bn.startsWith("_"))
					continue;

				if (!b_var && node instanceof UaVariableNode)
					continue;

				if (bfirst)
					bfirst = false;
				else
					w.write(',');

				writeUaNodeTreeJson(w, uaClient, node, b_var);
			}
			w.write("]");
		}
		w.write("}");

		// writeUaNodeTreeJson(w,uaClient,root);
	}

	public static UaNode findUaNodeByPath(OpcUaClient client, String[] path) throws UaException
	{
		NodeId nid = Identifiers.RootFolder;

		UaNode pn = client.getAddressSpace().getNode(nid);
		if (pn == null)
			return null;
		for (String p : path)
		{
			List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(pn);
			if (nodes == null || nodes.size() <= 0)
				return null;
			boolean bgit = false;
			for (UaNode node : nodes)
			{
				String bn = node.getBrowseName().getName();
				if (p.equals(bn))
				{
					pn = node;
					bgit = true;
					break;
				}
			}
			if (!bgit)
				return null;
		}

		return pn;
	}

	public static void writeUaNodeTreeJson(Writer w, OpcUaClient client, String nodeid) throws Exception
	{
		NodeId nid = null;
		if (Convert.isNullOrEmpty(nodeid))
			nid = Identifiers.RootFolder;
		else
			nid = NodeId.parse(nodeid);
		if (nid == null)
			return;
		UaNode nd = client.getAddressSpace().getNode(nid);
		if (nd == null)
			return;
		writeUaNodeTreeJson(w, client, nd, false);
	}

	/**
	 * complate to jstree
	 * 
	 * @param w
	 * @param n
	 * @throws IOException
	 */
	public static void writeUaNodeTreeJson(Writer w, OpcUaClient client, UaNode n, boolean b_var) throws Exception
	{
		NodeId nid = n.getNodeId();

		boolean bvar = n instanceof UaVariableNode;

		w.write("{\"id\":\"" + nid.toParseableString() + "\"");
		w.write(",\"nc\":" + n.getNodeClass().getValue());
		if (bvar)
		{
			w.write(",\"tp\": \"tag\"");
			w.write(",\"icon\": \"fa fa-tag fa-lg\"");
		}
		else
		{
			w.write(",\"tp\": \"tagg\"");
			w.write(",\"icon\": \"fa fa-folder fa-lg\"");
		}
		w.write(",\"text\":\"" + n.getDisplayName().getText() + "\"");
		// w.write(",\"state\": {\"opened\": true}");
		if (!bvar)
		{
			List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(n);
			if (nodes != null && nodes.size() > 0)
			{
				w.write(",\"children\":[");
				//
				boolean bfirst = true;
				for (UaNode node : nodes)
				{
					String bn = node.getBrowseName().getName();
					if (bn.startsWith("_"))
						continue;

					if (!b_var && node instanceof UaVariableNode)
						continue;

					if (bfirst)
						bfirst = false;
					else
						w.write(',');

					writeUaNodeTreeJson(w, client, node, b_var);
				}
				w.write("]");
			}
		}
		w.write("}");
	}

	public void writeSubUaVarNodeJson(Writer w, String nodeid) throws Exception
	{
		if (uaClient == null)
		{
			throw new Exception("no UaClient connected");
		}

		NodeId nid = NodeId.parse(nodeid);
		if (nid == null)
			throw new Exception("no NodeId parsed");

		List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(nid);
		w.write("[");
		//
		if (nodes != null && nodes.size() > 0)
		{
			boolean bfirst = true;
			for (UaNode node : nodes)
			{
				String bn = node.getBrowseName().getName();
				if (bn.startsWith("_"))
					continue;

				if (bfirst)
					bfirst = false;
				else
					w.write(',');

				w.write("{\"id\":\"" + node.getNodeId().toParseableString() + "\"");
				w.write(",\"nc\":" + node.getNodeClass().getValue());
				w.write(",\"name\":\"" + node.getBrowseName().getName() + "\"");

				// node.

				if (node instanceof UaVariableNode)
				{
					// if("BooleanArray".contentEquals(node.getBrowseName().getName()))
					// {
					// System.out.println("1");
					// }
					UaVariableNode uvn = (UaVariableNode) node;
					DataValue dataval = uvn.getValue();
					Object valob = dataval.getValue().getValue();
					if (valob != null && valob.getClass().isArray())
					{
						Object[] objectArray = (Object[]) valob;
						valob = Arrays.deepToString(objectArray);
					}
					String serverdt = Convert.toFullYMDHMS(dataval.getSourceTime().getJavaDate());
					w.write(",\"val\":\"" + valob + "\"");
					w.write(",\"val_dt\":\"" + serverdt + "\"");

					NodeId dt = uvn.getDataType();
					UaVariableTypeNode vtn = uvn.getTypeDefinition();
					UInteger[] arrdim = vtn.getArrayDimensions();
					UaDataTypeNode tpnode = (UaDataTypeNode) uaClient.getAddressSpace().getNode(dt);
					w.write(",\"vt\":\"" + tpnode.getBrowseName().getName() + "\"");
					if (arrdim != null && arrdim.length > 0)
					{
						w.write(",\"arr_dim\":\"" + Convert.combineWith(arrdim, ',') + "\"");
					}
					w.write(",\"tp\":\"var\"");
				}
				else if (node instanceof FolderTypeNode)
				{
					FolderTypeNode ftn = (FolderTypeNode) node;
					// ftn.
					w.write(",\"tp\":\"folder\"");
				}
				else
				{
					w.write(",\"tp\":\"\"");
				}
				w.write("}");
			}

		}
		w.write("]");
	}

	@Override
	protected List<String> transBindIdToConnLeafPath(String bindid)
	{
		// this.get
		return null;
	}

	// private static void browseNode(List<ReferenceDescription> rds,String
	// indent, OpcUaClient client, NodeId browseRoot)
	// throws Exception
	// {
	// BrowseDescription browse = new BrowseDescription(browseRoot,
	// BrowseDirection.Forward, Identifiers.References,
	// true, uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
	// uint(BrowseResultMask.All.getValue()));
	//
	// BrowseResult browseResult = client.browse(browse).get();
	//
	// List<ReferenceDescription> references =
	// toList(browseResult.getReferences());
	// rds.addAll(references);
	// for (ReferenceDescription rd : references)
	// {
	// QualifiedName qn = rd.getBrowseName();
	// String tn = rd.getTypeDefinition().getType().name();
	// //logger.info("{} Node={}", indent, qn.getName()+" "+tn);
	//
	// // recursively browse to children
	// rd.getNodeId().toNodeId(client.getNamespaceTable()).ifPresent(nodeId -> {
	// try
	// {
	// browseNode(rds,indent + " ", client, nodeId);
	// }
	// catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	// });
	// }
	// }

	private static void createSubscription(OpcUaClient client) throws InterruptedException, ExecutionException
	{
		// create connection
		client.connect().get();

		// interval 1000ms
		UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();

		// sub var
		NodeId nodeId = new NodeId(3, "\"test_value\"");
		ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);

		MonitoringParameters parameters = new MonitoringParameters(uint(1), 1000.0, // sampling
																					// interval
				null, // filter, null means use default
				uint(10), // queue size
				true // discard oldest
		);

		MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting,
				parameters);

		List<MonitoredItemCreateRequest> requests = new ArrayList<>();
		requests.add(request);

		// create and callback
		List<UaMonitoredItem> items = subscription
				.createMonitoredItems(TimestampsToReturn.Both, requests, (item, id) -> {
					item.setValueConsumer((item0, value) -> {
						System.out.println("nodeid :" + item0.getReadValueId().getNodeId());
						System.out.println("value :" + value.getValue().getValue());
					});
				}).get();
	}

	private static void readValue(OpcUaClient client) throws InterruptedException, ExecutionException
	{
		client.connect().get();

		NodeId nodeid = new NodeId(3, "\"test_value\"");

		DataValue value = client.readValue(0.0, TimestampsToReturn.Both, nodeid).get();

		System.out.println((Integer) value.getValue().getValue());
	}

	private static boolean writeValue(OpcUaClient client, int value) throws InterruptedException, ExecutionException
	{
		// 创建连接
		client.connect().get();

		// 创建变量节点
		NodeId nodeId = new NodeId(3, "\"test_value\"");

		// 创建Variant对象和DataValue对象
		Variant v = new Variant(value);
		DataValue dataValue = new DataValue(v, null, null);

		StatusCode statusCode = client.writeValue(nodeId, dataValue).get();

		return statusCode.isGood();
	}

	private boolean connect() // throws UaException
	{
		if (uaClient != null)
			return true;
		try
		{
			String dir = Config.getDataTmpDir()+"/pcua/security/" ;
			File dirf=  new File(dir) ;
			if(!dirf.exists())
				dirf.mkdirs() ;
			
			Path sec_p = Paths.get(dir);
			// EndpointDescription[] endpointDescription =
			// UaTcpStackClient.getEndpoints(EndPointUrl).get();
			KeyStoreLoader loader = new KeyStoreLoader().load(sec_p);
			UsernameProvider unp = getIdPro();
			
			uaClient = OpcUaClient.create(this.getOpcEndPointURI(),
					endpoints -> endpoints.stream().filter(endpointFilter()).findFirst(), configBuilder -> {
						configBuilder.setApplicationName(LocalizedText.english(this.getOpcAppNameDef()))
								.setApplicationUri("urn:iottree:conn:opc_client");
						//configBuilder.setCertificate(loader.getClientCertificate()).setKeyPair(loader.getClientKeyPair());
						if (unp != null)
							configBuilder.setIdentityProvider(unp);
						configBuilder.setRequestTimeout(uint(reqTimeout));
						return configBuilder.build();
					});

			// uaClient.getStackClient().getNamespaceTable()
			uaClient.getSubscriptionManager().addSubscriptionListener(subLis);

			uaClient.connect();

			// ArrayList<NodeId> nids = new ArrayList<>() ;
			// nids.addAll(tag2nodeid.values());
			// uaClient.registerNodes(nids) ;

		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

	public List<UaNode> opcBrowseNodes() throws Exception
	{
		if (uaClient == null)
			return null;
		return browserNodes(uaClient);
	}

	public void opcBrowseNodeOut(Writer w) throws Exception
	{
		if (uaClient == null)
			return;
		browseNodesOut(w, uaClient);
	}

	private transient HashMap<String, NodeId> tag2nodeid = null;

	@Override
	protected void RT_connInit() throws Exception
	{
		super.RT_connInit();

		HashMap<String, NodeId> t2n = new HashMap<>();

		Map<String, String> bindm = this.getBindMap();
		for (Map.Entry<String, String> t2p : bindm.entrySet())
		{
			String tagp = t2p.getKey();
			String bindp = t2p.getValue();
			int k = bindp.lastIndexOf(":");
			if (k > 0)
				bindp = bindp.substring(0, k);
			k = tagp.lastIndexOf(":");
			if (k > 0)
				tagp = tagp.substring(0, k);
			NodeId pnid = NodeId.parse(bindp);
			if (pnid == null)
				continue;
			t2n.put(tagp, pnid);
		}

		tag2nodeid = t2n;

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

			for (Map.Entry<String, NodeId> tag2n : tag2nodeid.entrySet())
			{
				NodeId nodeid = tag2n.getValue();
				UShort us = nodeid.getNamespaceIndex();
				Object id = nodeid.getIdentifier();

				DataValue v = uaClient.readValue(0.0, TimestampsToReturn.Both, nodeid).get();

				updateTagVal(ch, tag2n.getKey(), v);
			}
		}
		catch ( Exception e)
		{
			System.out.println("read data err=" + e.getMessage());
		}
		finally
		{
			lastReadData = System.currentTimeMillis();
		}
	}

	private void updateTagVal(UACh ch, String tagpath, DataValue v) throws Exception
	{
		int k = tagpath.indexOf(':');
		if (k > 0)
			tagpath = tagpath.substring(0, k);
		UANode tmpn = ch.getDescendantNodeByPath(tagpath);
		if (tmpn == null || !(tmpn instanceof UATag))
			return;
		StatusCode sc = v.getStatusCode();
		// sc.
		Object objv = v.getValue().getValue();
		if (objv == null)
			return;

		UATag tag = (UATag) tmpn;
		// itemval.getValue().
		if (sc.isGood())
		{
			long chgdt = v.getServerTime().getJavaTime();
			// itemval.
			// UAVal uav =
			// UAVal.createByStrVal(tag.getValTp(),itemval.getLastValueStr(),chgdt,chgdt);
			// tag.RT_setUAVal(uav);
			tag.RT_setValRawStr(objv.toString(), true, chgdt);
			// tag.RT_setValStr(itemval.getLastValueStr());
		}
		else
			tag.RT_setValErr("", null);
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
		return uaClient != null;
	}

	public String getConnErrInfo()
	{
		if (uaClient == null)
			return "no connection";
		else
			return null;
	}

	synchronized void disconnect() // throws IOException
	{
		if (uaClient == null)
			return;

		try
		{
			uaClient.disconnect().get();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			uaClient = null;
		}
	}

	public void RT_writeValByBind(String tagpath, String strv)
	{
		if (uaClient == null)
			return;
		int k = tagpath.indexOf(':');
		if (k > 0)
			tagpath = tagpath.substring(0, k);
		NodeId nid = tag2nodeid.get(tagpath);
		if (nid == null)
			return;

		Variant v = new Variant(Integer.parseInt(strv));
		DataValue dataValue = new DataValue(v, null, null);

		try
		{
			StatusCode statusCode = uaClient.writeValue(nid, dataValue).get();
			boolean r = statusCode.isGood();
			System.out.println("w result=" + r);
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}

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
}
