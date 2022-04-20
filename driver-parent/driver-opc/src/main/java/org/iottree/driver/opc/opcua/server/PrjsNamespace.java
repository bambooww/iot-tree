package org.iottree.driver.opc.opcua.server;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.dtd.DataTypeDictionaryManager;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilters;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UAManager;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UANodeOCTagsGCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UATagG;
import org.iottree.core.UAVal;
import org.iottree.driver.opc.opcua.UaHelper;

public class PrjsNamespace extends ManagedNamespaceWithLifecycle
{
	public static final String NAMESPACE_URI = "urn:iottree:server";
	
	private final DataTypeDictionaryManager dictionaryManager;

    private final SubscriptionModel subscriptionModel;
    
    //private UAPrj uaPrj = null ;
	
	public PrjsNamespace(OpcUaServer server)
	{
		super(server, NAMESPACE_URI);
		
		//uaPrj = prj ;

        subscriptionModel = new SubscriptionModel(server, this);
        dictionaryManager = new DataTypeDictionaryManager(getNodeContext(), NAMESPACE_URI);

        getLifecycleManager().addLifecycle(dictionaryManager);
        getLifecycleManager().addLifecycle(subscriptionModel);

        getLifecycleManager().addStartupTask(this::createAndAddNodes);

        getLifecycleManager().addLifecycle(new Lifecycle() {
            @Override
            public void startup() {
//                startBogusEventNotifier();
            }

            @Override
            public void shutdown() {
//                try {
//                    keepPostingEvents = false;
//                    eventThread.interrupt();
//                    eventThread.join();
//                } catch (InterruptedException ignored) {
//                    // ignored
//                }
            }
        });
    }
	
	private void createAndAddNodes() {
		
		//prjNamespaces = new ArrayList<>() ;
		for(UAPrj p:UAManager.getInstance().listPrjs())
		{
			addPrjNodes(p);
		}
        
    }
	
	private void addPrjNodes(UAPrj prj)
	{
		NodeId nodeid = newNodeId(prj.getName());

        UaFolderNode pnode = new UaFolderNode(
            getNodeContext(),
            nodeid,
            newQualifiedName(prj.getName()),
            LocalizedText.english(prj.getTitle())
        );

        getNodeManager().addNode(pnode);

        // Make sure our new folder shows up under the server's Objects folder.
        pnode.addReference(new Reference(
            pnode.getNodeId(),
            Identifiers.Organizes,
            Identifiers.ObjectsFolder.expanded(),
            false
        ));
        
        //
     // Add the rest of the nodes
		for(UACh ch:prj.getChs())
        {
        	addChNodes(ch,pnode);
        }
		
		addTagsNodes(prj,pnode);
	}
	
	private void addTagGNodes(UANodeOCTagsGCxt tagscxt,UaFolderNode pnode)
	{
		List<UATagG> taggs = tagscxt.getSubTagGs();
		if(taggs==null)
			return ;
		
		for(UATagG tagg:taggs)
		{
			NodeId tggnid = newNodeId(tagg.getNodePathCxt());

	        UaFolderNode tggn = new UaFolderNode(
	            getNodeContext(),
	            tggnid,
	            newQualifiedName(tagg.getName()),
	            LocalizedText.english(tagg.getTitle())
	        );

	        getNodeManager().addNode(tggn);
	        pnode.addOrganizes(tggn);
	        
	        addTagGNodes(tagg,tggn);
	        addTagsNodes(tagg,tggn);
		}
		
		
	}
	
	private void addTagsNodes(UANodeOCTagsCxt tagscxt,UaFolderNode pnode)
	{
		
		List<UATag> tags = tagscxt.listTags();
		String ppath = tagscxt.getNodePathCxt("/");
		//System.out.println("addTagsNodes   "+ppath) ;
		for(UATag tag:tags)
		{
			 String name = tag.getName() ;
			 //System.out.println("before add tag=="+ppath+"/"+name) ;
	            NodeId tpid = UaHelper.transValTp2UaTp(tag.getValTp()) ;
	            if(tpid==null)
	            	continue ;
	           // System.out.println("add tag=="+ppath+"/"+name) ;
	            UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
	                .setNodeId(newNodeId(ppath +"/"+ name))
	                .setAccessLevel(AccessLevel.READ_WRITE)
	                .setUserAccessLevel(AccessLevel.READ_WRITE)
	                .setBrowseName(newQualifiedName(name))
	                .setDisplayName(LocalizedText.english(name))
	                .setDataType(tpid)
	                .setTypeDefinition(Identifiers.BaseDataVariableType)
	                .build();

//	            Variant variant = new Variant(ulong(64L))
//	            node.setValue(new DataValue(variant));

	            node.getFilterChain().addLast(new AttributeLoggingFilter(AttributeId.Value::equals));
	            node.getFilterChain().addLast(
	                    new AttributeLoggingFilter(),
	                    AttributeFilters.getValue(
	                        ctx ->{
	                        	UAVal uav =  tag.RT_getVal() ;
	                        	if(uav.isValid())
	                        	{
		                        	Variant v = new Variant(uav.getObjVal()) ;
		                            return new DataValue(v) ;
	                        	}
	                        	else
	                        	{
	                        		 return new DataValue(StatusCode.BAD) ;
	                        	}
	                        }
	                    )
	                );
	            getNodeManager().addNode(node);
	            pnode.addOrganizes(node);
		}
	}

	private void addChNodes(UACh ch,UaFolderNode prjn)
	{
		NodeId chnid = newNodeId(ch.getNodePathCxt());

        UaFolderNode chn = new UaFolderNode(
            getNodeContext(),
            chnid,
            newQualifiedName(ch.getName()),
            LocalizedText.english(ch.getTitle())
        );

        getNodeManager().addNode(chn);
        prjn.addOrganizes(chn);
        
        List<UADev> devs = ch.getDevs() ;
        if(devs!=null)
        {
        	for(UADev dev:devs)
        	{
        		NodeId devnid = newNodeId(dev.getNodePathCxt());

    	        UaFolderNode devn = new UaFolderNode(
    	            getNodeContext(),
    	            devnid,
    	            newQualifiedName(dev.getName()),
    	            LocalizedText.english(dev.getTitle())
    	        );

    	        getNodeManager().addNode(devn);
    	        chn.addOrganizes(devn);
    	        
    	        addTagGNodes(dev,devn);
    	        addTagsNodes(dev,devn);
        	}
        }
        
        addTagGNodes(ch,chn) ;

        addTagsNodes(ch,chn);
	}

	@Override
    public void onDataItemsCreated(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
        subscriptionModel.onMonitoringModeChanged(monitoredItems);
    }

}
