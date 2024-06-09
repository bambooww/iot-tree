package org.iottree.core.msgnet.nodes;

import org.iottree.core.filter.SubFilteredTree;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

/**
 * 在一个路径下，根据一定条件进行标签内容的过滤，形成一颗子树
 * 
 * @author jason.zhu
 */
public class NM_TagFilter extends MNNodeMid implements ILang
{
	
	
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
		return getFilterTree().toDefJO();
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		getFilterTree().fromDefJO(jo) ;
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		JSONObject jo = getFilterTree().RT_getFilteredJO() ;
		//System.out.println(jo.toString(2)) ;
		MNMsg outm = new MNMsg().asPayload(jo) ;
		return RTOut.createOutAll(outm) ;
	}
}
