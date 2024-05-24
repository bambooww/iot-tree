package org.iottree.core.msgnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class MNModule extends MNBase
{
	
	HashSet<String> nodeIdSet = new HashSet<>() ;
	
	private HashMap<String,MNNode> supTp2Node = null ;
	
	public MNModule()
	{
		super() ;
	}
	
	protected final String getOwnerTP()
	{
		return null ;
	}
	
	final MNModule createNewIns(MNNet net) throws Exception
	{
		MNModule new_n = (MNModule)this.getClass().getConstructor().newInstance() ;
		new_n.belongTo = net;
		new_n.cat = this.cat ;
		return new_n ;
	}
	
	protected abstract List<MNNode> getSupportedNodes() ;
	
	
	private HashMap<String,MNNode> getSupTp2Node()
	{
		if(supTp2Node!=null)
			return supTp2Node;
		HashMap<String,MNNode> tp2n = new HashMap<>() ;
		List<MNNode> ns = this.getSupportedNodes() ;
		if(ns!=null)
		{
			for(MNNode n:ns)
			{
				n.TP_setRelatedModule(this);
				tp2n.put(n.getTP(), n) ;
			}
		}
		supTp2Node = tp2n ;
		return tp2n ;
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
}
