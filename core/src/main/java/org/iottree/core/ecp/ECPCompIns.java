package org.iottree.core.ecp;

import org.json.JSONObject;

public class ECPCompIns extends ECPNode
{
	public static final String TP = "comp_ins" ;
	
	private String ref_comp_id="" ;
	
	public ECPCompIns(ECPNet net, String id)
	{
		super(net, id,null);
	}

	@Override
	public String getNodeTP()
	{
		return TP;
	}

	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.putOpt("ref_comp_id", ref_comp_id) ;
		return jo ;
	}
	
	public void updateStructByJO(JSONObject jo)
	{
		super.updateStructByJO(jo);
		this.ref_comp_id = jo.optString("ref_comp_id") ;
	}
}
