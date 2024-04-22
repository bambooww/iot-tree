package org.iottree.core.router;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.filter.SubFilteredTree;
import org.json.JSONObject;

public class RICFilterTags extends RouterInnCollator
{
	public static final String TP="filter_tags";
	
	private SubFilteredTree subFilterTree = null ;
	
	public RICFilterTags(RouterManager rm)
	{
		super(rm);
	}
	
	@Override
	public String getTp()
	{
		return TP;
	}
	
	protected RouterInnCollator newInstance(RouterManager rm)
	{
		return new RICFilterTags(rm) ;
	}
	
	public SubFilteredTree getSubFilteredTree()
	{
		return this.subFilterTree ;
	}

	public OutStyle getOutStyle()
	{//interval only
		return OutStyle.interval  ;
	}
	
	JoinOut filterOut = new JoinOut(this,"filter_out") ;
	
	private List<JoinOut> jouts = Arrays.asList(filterOut
			) ;
	
	@Override
	public List<JoinIn> getJoinInList()
	{
		return null ;
	}
	
	@Override
	public List<JoinOut> getJoinOutList()
	{
//		if(rtOutTags==null||rtOutTags.size()<=0)
//			return null ;
		return jouts ;
	}
	
	/**
	 * override by sub
	 */
	@Override
	protected void RT_runInIntvLoop()
	{
		JSONObject datajo = this.getSubFilteredTree().RT_getFilteredJO() ;
		if(datajo==null)
		{
			this.RT_fireErr("no out data git",null) ;
			return ;
		}
		
		RT_sendToJoinOut(filterOut,datajo) ;
	}
	
	@Override
	public boolean DEBUG_triggerOutData(StringBuilder failedr)
	{
		JSONObject datajo = this.getSubFilteredTree().RT_getFilteredJO() ;
		if(datajo==null)
		{
			this.RT_fireErr("no out data git",null) ;
			failedr.append("no out data git") ;
			return false;
		}
		
		RT_sendToJoinOut(filterOut,datajo) ;
		return true;
	}
	
	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji,String recved_txt)
	{
		
	}
	
	
	
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		
		if(subFilterTree!=null)
		{
			JSONObject tmpjo = subFilterTree.toDefJO() ;
			jo.put("sub_f_t", tmpjo) ;
		}
		
		
		return jo ;
	}
	

	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		if(!super.fromJO(jo,failedr))
			return false;
		
		JSONObject tmpjo = jo.optJSONObject("sub_f_t");
		if(tmpjo!=null)
		{
			SubFilteredTree sft = new SubFilteredTree(this.belongPrj) ;
			if(!sft.fromDefJO(tmpjo))
				return false;
			subFilterTree = sft ;
		}
		
		return true ;
	}

}
