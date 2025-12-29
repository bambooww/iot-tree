package org.iottree.core.msgnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class MNModule extends MNBase
{
	
	HashSet<String> nodeIdSet = new HashSet<>() ;
	
	
	
	private LinkedHashMap<String,MNNode> supTp2Node = new LinkedHashMap<>() ;
	
	public MNModule()
	{
		super() ;
	}
	
	protected final String getOwnerTP()
	{
		return null ;
	}
	
	@Override
	final MNModule createNewIns(MNNet net) throws Exception
	{
		MNModule new_n = (MNModule)this.getClass().getConstructor().newInstance() ;
		new_n.belongTo = net;
		new_n.cat = this.cat ;
		new_n.supTp2Node = this.supTp2Node ;
		return new_n ;
	}
	
	//protected abstract List<MNNode> getSupportedNodes() ;
	final void registerSubTpNode(MNNode subn)
	{
		subn.TP_setRelatedModule(this);
		supTp2Node.put(subn.getTP(), subn) ;
	}
	
	
	private HashMap<String,MNNode> getSupTp2Node()
	{
		return supTp2Node;
		
//		if(supTp2Node!=null)
//			return supTp2Node;
//		HashMap<String,MNNode> tp2n = new HashMap<>() ;
//		List<MNNode> ns = this.getSupportedNodes() ;
//		if(ns!=null)
//		{
//			for(MNNode n:ns)
//			{
//				n.TP_setRelatedModule(this);
//				tp2n.put(n.getTP(), n) ;
//			}
//		}
//		supTp2Node = tp2n ;
//		return tp2n ;
	}
	
	public final List<MNNode> listSupportedNodes()
	{
		HashMap<String,MNNode> t2n = getSupTp2Node() ;
		ArrayList<MNNode> rets  =new ArrayList<>(t2n.size()) ;
		rets.addAll(t2n.values()) ;
		return rets ;
	}
	
	public final MNNode getSupportedNodeByTP(String tp)
	{
		return getSupTp2Node().get(tp) ;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO();
		jo.putOpt("node_ids", this.nodeIdSet) ;
		return jo ;
	}
	
	public Set<String> getRelatedNodeIdSet()
	{
		return this.nodeIdSet ;
	}
	
	public List<MNNode> getRelatedNodes()
	{
		if(this.nodeIdSet==null||this.nodeIdSet.size()<=0)
			return null ;
		ArrayList<MNNode> rets = new ArrayList<>(this.nodeIdSet.size()) ;
		for(String nid:this.nodeIdSet)
		{
			MNNode n = this.belongTo.getNodeById(nid) ;
			if(n==null)
				continue ;
			rets.add(n) ;
		}
		return rets ;
	}
	
	public List<MNNode> getRelatedNodes(Class<? extends MNNode> c)
	{
		ArrayList<MNNode> rets = new ArrayList<>(this.nodeIdSet.size()) ;
//		for(MNNode n:this.belongTo.id2node.values())
//		{
//			if(n.getOwnRelatedModule()!=this)
//				continue ;
//			if(!c.isInstance(n))
//				continue ;
//			rets.add(n) ;
//		}
//		return rets ;
		if(this.nodeIdSet==null||this.nodeIdSet.size()<=0)
			return null ;
		
		for(String nid:this.nodeIdSet)
		{
			MNNode n = this.belongTo.getNodeById(nid) ;
			if(n==null)
				continue ;
			if(!c.isInstance(n))
				continue ;
			rets.add(n) ;
		}
		return rets ;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MNNode> List<T> listRelatedNodes(Class<T> c)
	{
		ArrayList<T> rets = new ArrayList<>(this.nodeIdSet.size()) ;
		if(this.nodeIdSet==null||this.nodeIdSet.size()<=0)
			return null ;
		
		for(String nid:this.nodeIdSet)
		{
			MNNode n = this.belongTo.getNodeById(nid) ;
			if(n==null)
				continue ;
			if(!c.isInstance(n))
				continue ;
			rets.add((T)n) ;
		}
		return rets ;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MNNode> T getRelatedNodeFirst(Class<T> c)
	{
		if(this.nodeIdSet==null||this.nodeIdSet.size()<=0)
			return null ;
		for(String nid:this.nodeIdSet)
		{
			MNNode n = this.belongTo.getNodeById(nid) ;
			if(n==null)
				continue ;
			if(!c.isInstance(n))
				continue ;
			return (T)n;
		}
		return null ;
	}
	
	//for module,it can be impl to auto add related nodes
	public void checkAfterSetParam()
	{
		
	}
	
	@Override
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		JSONArray jarr = jo.optJSONArray("node_ids") ;
		
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i++)
				this.nodeIdSet.add(jarr.getString(i)) ;
		}
		return true;
	}
	
//	protected void RT_renderDiv(StringBuilder divsb)
//	{
//		super.RT_renderDiv(divsb);
//		
//	}
	
	public JSONObject RT_toJO(boolean out_rt_div)
	{
		JSONObject jo = super.RT_toJO(out_rt_div) ;
		
		return jo ;
	}
	
	protected void RT_sendMsgByRelatedNode(MNNode n,RTOut rtout)
	{
		n.RT_sendMsgOut(rtout);
	}
}
