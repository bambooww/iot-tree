package org.iottree.core.msgnet.nodes;

import org.iottree.core.filter.SubFilteredTree;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 在一个路径下，根据一定条件进行标签内容的过滤，形成一颗子树
 * 
 * @author jason.zhu
 */
public class NM_TagFilter extends MNNodeMid implements ILang
{
	boolean bFlatOut = false;
	
	@Override
	public String getColor()
	{
		return "#a1cbde";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf02c";
	}

	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "tag_filter";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_filter");
	}
	
	private SubFilteredTree sft = null;
	
	private  SubFilteredTree getFilterTree()
	{
		if(sft!=null)
			return sft ;
		
		sft = new SubFilteredTree(this.getBelongTo().getPrj());
		return sft ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = getFilterTree().toDefJO();
		jo.put("_b_flat_out", this.bFlatOut) ;
		return jo ;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		getFilterTree().fromDefJO(jo) ;
		this.bFlatOut = jo.optBoolean("_b_flat_out",false) ;
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		MNMsg outm = null ;
		if(this.bFlatOut)
		{
			JSONArray jo = getFilterTree().RT_getFilteredJArrFlat() ;
			//System.out.println(jo.toString(2)) ;
			outm = new MNMsg().asPayload(jo) ;
		}
		else
		{
			JSONObject jo = getFilterTree().RT_getFilteredJO() ;
			//System.out.println(jo.toString(2)) ;
			outm = new MNMsg().asPayload(jo) ;
		}
		return RTOut.createOutAll(outm) ;
	}
}
