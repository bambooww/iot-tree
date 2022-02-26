package org.iottree.core.conn;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.iottree.core.ConnPt;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

public class ConnPtOPCUA extends ConnPtBinder
{
	// String appName = null;
	// tcp http https ws wss
	String opcProtocal = "tcp";

	String opcHost = "";

	int opcPort = 49320;
	
	// "" or /xxx
	String opcPath = "" ;

	int reqTimeout = 5000;

	String idUser = null;

	String idPsw = null;

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
		xd.setParamValue("opc_proto", this.opcProtocal);
		xd.setParamValue("opc_host", this.opcHost);
		xd.setParamValue("opc_port", this.opcPort);
		xd.setParamValue("opc_path", this.opcPath);
		xd.setParamValue("opc_req_to", this.reqTimeout);
		xd.setParamValue("opc_user", this.idUser);
		xd.setParamValue("opc_psw", this.idPsw);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);

		// this.appName = xd.getParamValueStr("opc_app_name",
		// "iottree_opc_client_"+this.getName());
		this.opcProtocal = xd.getParamValueStr("opc_proto", "tcp");
		this.opcHost = xd.getParamValueStr("opc_host", "");
		this.opcPort = xd.getParamValueInt32("opc_port", 49320);
		this.opcPath = xd.getParamValueStr("opc_path", "");
		this.reqTimeout = xd.getParamValueInt32("opc_req_to", 5000);
		this.idUser = xd.getParamValueStr("opc_user", "");
		this.idPsw = xd.getParamValueStr("opc_psw", "");
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
	
	

	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);

		// this.appName =optJSONString(jo,"opc_app_name",getOpcAppNameDef()) ;
		this.opcProtocal = optJSONString(jo, "opc_proto", "tcp");
		this.opcHost = optJSONString(jo, "opc_host", "");
		this.opcPort = optJSONInt(jo, "opc_port", 49320);
		this.opcPath = optJSONString(jo, "opc_path", "");
		this.reqTimeout = optJSONInt(jo, "opc_req_to", 5000);
		this.idUser = optJSONString(jo, "opc_user", "");
		this.idPsw = optJSONString(jo, "opc_psw", "");
	}
	
	public List<String> transBindIdToPath(String bindid)
	{
		return null ;
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
	
	public String getOpcProtocal()
	{
		if(Convert.isNullOrEmpty(this.opcProtocal))
			return "tcp";
		return this.opcProtocal;
	}

	public String getOpcHost()
	{
		return this.opcHost;
	}

	public int getOpcPort()
	{
		return this.opcPort;
	}

	public String getOpcPortStr()
	{
		if (this.opcPort <= 0)
			return "";
		return "" + this.opcPort;
	}
	
	public String getOpcPath()
	{
		return opcPath ;
	}

	public String getOpcEndPointURI()
	{
		return "opc."+this.getOpcProtocal()+"://" + this.opcHost + ":" + this.opcPort+this.opcPath;
	}

	public int getOpcReqTimeout()
	{
		return reqTimeout;
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
			// 恢复订阅失败 需要重新订阅
			System.out.println("onSubscriptionTransferFailed");
		}
	};

	private static List<UaNode> browserNodes(OpcUaClient client) throws Exception
	{
		client.connect().get();
		
		ArrayList<UaNode> rets = new ArrayList<>() ;
		// start browsing at root folder
		browseNode(rets,"", client, Identifiers.RootFolder);
		
		return rets;
	}
	

    private static void browseNode(List<UaNode> uanodes,String indent, OpcUaClient client, NodeId browseRoot) {
        try {
            List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(browseRoot);

            for (UaNode node : nodes) {
               // logger.info("{} Node={}", indent, node.getBrowseName().getName());
            	uanodes.add(node);
                // recursively browse to children
                browseNode(uanodes,indent + "  ", client, node.getNodeId());
            }
        } catch (UaException e) {
            //logger.error("Browsing nodeId={} failed: {}", browseRoot, e.getMessage(), e);
        }
    }
    
    public static void browseNodesOut(Writer w,OpcUaClient client) throws Exception
    {
    	//client.getAddressSpace().
    	UaNode uan = client.getAddressSpace().getNode(Identifiers.RootFolder);
    	if(uan==null)
    		return ;
    	browseNodesOut(w,"", client,uan);
    }
    
    public static void browseNodesOut(Writer w,String prefix, OpcUaClient client, UaNode curnode) throws Exception
    {
    	
        	String p = prefix+curnode.getBrowseName().getName();
        	w.write(p+"   "+curnode.getNodeId().toParseableString()+"<br/>");
            List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(curnode);

            String subp = p +".";
            for (UaNode node : nodes)
            {
            	String bn = node.getBrowseName().getName();
            	if(bn.startsWith("_"))
            		continue;
               // logger.info("{} Node={}", indent, );
            	//uanodes.add(node);
                // recursively browse to children
            	browseNodesOut(w,subp, client, node);
            }
    }
    
    public static void writeSubNodesJSON(Writer w, OpcUaClient client, String curnodeid) throws Exception
    {
    	NodeId nid = NodeId.parse(curnodeid) ;
    	if(nid==null)
    		return ;
    	UaNode nd = client.getAddressSpace().getNode(nid);
    	//String p = prefix+curnode.getBrowseName().getName();
    	//w.write(p+"   "+curnode.getNodeId().toParseableString()+"<br/>");
        
    }
    
    public void writeUaNodeTreeJson(Writer w,String nodeid) throws Exception
    {
    	if(uaClient==null)
    	{
    		throw new Exception("no UaClient connected");
    	}
    	writeUaNodeTreeJson(w,uaClient,nodeid);
    }
    
    public  void writeBindBeSelectedTreeJson(Writer w) throws Exception
    {
    	writeUaNodeTreeJson(w,true) ;
    }
    
    public void writeUaNodeTreeJson(Writer w,boolean b_var) throws Exception
    {
    	if(uaClient==null)
    	{
    		throw new Exception("no UaClient connected");
    	}
    	
    	//find /Root/Objects nodes
    	UaNode root =  findUaNodeByPath(uaClient,new String[] {"Objects"}) ;
    	if(root==null)
    		throw new Exception("no /Root/Objects/ node found");
    		
//    	UaObjectNode uaou= new UaObjectNode(
//    			uaClient,
//                getNodeId(),
//                getBrowseName(),
//                getDisplayName(),
//                getDescription(),
//                getWriteMask(),
//                getUserWriteMask(),
//                getEventNotifier()
//        );
    	
    	//NodeId nid = n.getNodeId() ;
    	
    	w.write("{\"id\":\""+UUID.randomUUID().toString()+"\"");
    	w.write(",\"nc\":0");
    	w.write(",\"icon\": \"fa fa-sitemap fa-lg\"");
    	
    	w.write(",\"text\":\""+this.getOpcEndPointURI()+"\"");
    	w.write(",\"state\": {\"opened\": true}");
    	
    	List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(root);
    	if(nodes!=null&&nodes.size()>0)
    	{
	        w.write(",\"children\":[");
	        //
	        boolean bfirst = true;
	        for (UaNode node : nodes)
	        {
	        	String bn = node.getBrowseName().getName();
	        	if(bn.startsWith("_"))
	        		continue;
	        	
	        	if(!b_var&&node instanceof UaVariableNode)
	        		continue;
	        	
	        	if(bfirst) bfirst=false;
	        	else w.write(',');
	        	
	        	writeUaNodeTreeJson(w,uaClient,node,b_var);
	        }
	        w.write("]");
    	}
        w.write("}");
        
    	//writeUaNodeTreeJson(w,uaClient,root);
    }
    
    public static UaNode findUaNodeByPath(OpcUaClient client,String[] path) throws UaException
    {
    	NodeId nid = Identifiers.RootFolder;
    	
    	UaNode pn = client.getAddressSpace().getNode(nid);
    	if(pn==null)
    		return null ;
    	for(String p:path)
    	{
    		List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(pn);
        	if(nodes==null||nodes.size()<=0)
        		return null ;
        	boolean bgit = false;
	        for (UaNode node : nodes)
	        {
	        	String bn = node.getBrowseName().getName();
	        	if(p.equals(bn))
	        	{
	        		pn = node ;
	        		bgit = true;
	        		break ;
	        	}
	        }
	        if(!bgit)
	        	return null ;
    	}
    	
    	return pn ;
    }
    
    public static void writeUaNodeTreeJson(Writer w,OpcUaClient client,String nodeid) throws Exception
    {
    	NodeId nid = null;
    	if(Convert.isNullOrEmpty(nodeid))
    		nid = Identifiers.RootFolder;
    	else
    		nid = NodeId.parse(nodeid) ;
    	if(nid==null)
    		return ;
    	UaNode nd = client.getAddressSpace().getNode(nid);
    	if(nd==null)
    		return ;
    	writeUaNodeTreeJson(w,client,nd,false);
    }
    /**
     * complate to jstree
     * @param w
     * @param n
     * @throws IOException
     */
    public static void writeUaNodeTreeJson(Writer w,OpcUaClient client,UaNode n,boolean b_var) throws Exception
    {
    	NodeId nid = n.getNodeId() ;
    	
    	boolean bvar = n instanceof UaVariableNode;
    	
    	w.write("{\"id\":\""+nid.toParseableString()+"\"");
    	w.write(",\"nc\":"+n.getNodeClass().getValue());
    	if(bvar)
    		w.write(",\"icon\": \"fa fa-tag fa-lg\"");
    	else
    		w.write(",\"icon\": \"fa fa-folder fa-lg\"");
    	w.write(",\"text\":\""+n.getDisplayName().getText()+"\"");
    	//w.write(",\"state\": {\"opened\": true}");
    	if(!bvar)
    	{
	    	List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(n);
	    	if(nodes!=null&&nodes.size()>0)
	    	{
		        w.write(",\"children\":[");
		        //
		        boolean bfirst = true;
		        for (UaNode node : nodes)
		        {
		        	String bn = node.getBrowseName().getName();
		        	if(bn.startsWith("_"))
		        		continue;
		        	
		        	if(!b_var && node instanceof UaVariableNode)
		        		continue;
		        	
		        	if(bfirst) bfirst=false;
		        	else w.write(',');
		        	
		        	writeUaNodeTreeJson(w,client,node,b_var);
		        }
		        w.write("]");
	    	}
    	}
        w.write("}");
    }
    
    public void writeSubUaVarNodeJson(Writer w,String nodeid) throws Exception
    {
    	if(uaClient==null)
    	{
    		throw new Exception("no UaClient connected");
    	}
    	
    	NodeId nid = NodeId.parse(nodeid);
    	if(nid==null)
    		throw new Exception("no NodeId parsed");
    	
    	List<? extends UaNode> nodes = uaClient.getAddressSpace().browseNodes(nid);
    	w.write("[");
        //
    	if(nodes!=null&&nodes.size()>0)
    	{
	        
	        boolean bfirst = true;
	        for (UaNode node : nodes)
	        {
	        	String bn = node.getBrowseName().getName();
	        	if(bn.startsWith("_"))
	        		continue;

	        	if(bfirst) bfirst=false;
	        	else w.write(',');

	        	w.write("{\"id\":\""+node.getNodeId().toParseableString()+"\"");
	        	w.write(",\"nc\":"+node.getNodeClass().getValue());
	        	w.write(",\"name\":\""+node.getBrowseName().getName()+"\"");
	        	
	        	//node.

	        	if(node instanceof UaVariableNode)
	        	{
//	        		if("BooleanArray".contentEquals(node.getBrowseName().getName()))
//	        		{
//	        			System.out.println("1");
//	        		}
	        		UaVariableNode uvn = (UaVariableNode)node;
	        		DataValue dataval = uvn.getValue();
	        		Object valob = dataval.getValue().getValue();
	        		if (valob != null && valob.getClass().isArray())
	        		{
	                    Object[] objectArray = (Object[])valob;
	                    valob = Arrays.deepToString(objectArray);
	        		}
	        		String serverdt = Convert.toFullYMDHMS(dataval.getSourceTime().getJavaDate());
	        		w.write(",\"val\":\""+valob+"\"");
	        		w.write(",\"val_dt\":\""+serverdt+"\"");
	        		
	        		NodeId dt = uvn.getDataType();
	        		UaVariableTypeNode vtn = uvn.getTypeDefinition();
	        		UInteger[] arrdim = vtn.getArrayDimensions();
	        		UaDataTypeNode tpnode = (UaDataTypeNode)uaClient.getAddressSpace().getNode(dt);
	        		w.write(",\"datatp\":\""+tpnode.getBrowseName().getName()+"\"");
	        		if(arrdim!=null&&arrdim.length>0)
	        		{
	        			w.write(",\"arr_dim\":\""+Convert.combineWith(arrdim, ',')+"\"");
	        		}
	        		w.write(",\"tp\":\"var\"");
	        	}
	        	else if(node instanceof FolderTypeNode)
	        	{
	        		FolderTypeNode ftn = (FolderTypeNode)node;
	        		//ftn.
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
	
//	private static void browseNode(List<ReferenceDescription> rds,String indent, OpcUaClient client, NodeId browseRoot)
//		throws Exception
//	{
//		BrowseDescription browse = new BrowseDescription(browseRoot, BrowseDirection.Forward, Identifiers.References,
//				true, uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
//				uint(BrowseResultMask.All.getValue()));
//
//		BrowseResult browseResult = client.browse(browse).get();
//
//		List<ReferenceDescription> references = toList(browseResult.getReferences());
//		rds.addAll(references);
//		for (ReferenceDescription rd : references)
//		{
//			QualifiedName qn = rd.getBrowseName();
//			String tn = rd.getTypeDefinition().getType().name();
//			//logger.info("{} Node={}", indent, qn.getName()+" "+tn);
//
//			// recursively browse to children
//			rd.getNodeId().toNodeId(client.getNamespaceTable()).ifPresent(nodeId -> {
//				try
//				{
//					browseNode(rds,indent + "  ", client, nodeId);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			});
//		}
//	}


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
			//EndpointDescription[] endpointDescription =
			//UaTcpStackClient.getEndpoints(EndPointUrl).get();
			// KeyStoreLoader loader = new
			// KeyStoreLoader().load(securityTempDir);
			uaClient = OpcUaClient.create(this.getOpcEndPointURI(),
					endpoints -> endpoints.stream().filter(endpointFilter()).findFirst(),
					configBuilder -> configBuilder.setApplicationName(LocalizedText.english(this.getOpcAppNameDef()))
							.setApplicationUri("urn:iottree:conn:opc_client")
							// .setCertificate(loader.getClientCertificate()).setKeyPair(loader.getClientKeyPair())
							.setIdentityProvider(getIdPro()).setRequestTimeout(uint(reqTimeout)).build());

			//uaClient.getStackClient().getNamespaceTable()
			uaClient.getSubscriptionManager().addSubscriptionListener(subLis);
			
			uaClient.connect();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	
	public List<UaNode> opcBrowseNodes() throws Exception
	{
		if(uaClient==null)
			return null;
		return browserNodes(uaClient);
	}
	
	public void opcBrowseNodeOut(Writer w) throws Exception
	{
		if(uaClient==null)
			return;
		browseNodesOut(w,uaClient);
	}

	private long lastChk = -1;

	void checkConn()
	{
		if (System.currentTimeMillis() - lastChk < 5000)
			return;

		try
		{
			connect();
		} finally
		{
			lastChk = System.currentTimeMillis();
		}
	}

	@Override
	public boolean isConnReady()
	{
		return uaClient != null;
	}
	
	public String getConnErrInfo()
	{
		if(uaClient==null)
			return "no connection" ;
		else
			return null ;
	}

	synchronized void disconnect() // throws IOException
	{
		if (uaClient == null)
			return;

		try
		{
			uaClient.disconnect().get();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			uaClient = null;
		}
	}

	public void RT_writeValByBind(String tagpath,String strv)
	{
		//TODO 
	}
}
