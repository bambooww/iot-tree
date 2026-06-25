package org.iottree.core.devtree;

import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * Device Tree Node for DevPart
 * 
 * it's leaf node in Tree
 * 
 * @author jason.zhu
 *
 */
public class DTNodePart extends DTNode
{
	public static final String TP = "p" ;
	
	String partId = null ;
	
	/**
	 * for load
	 * @param parent
	 */
	DTNodePart(DTNodeGrp parent)
	{
		super(parent) ;
	}
	
	/**
	 * for new
	 * @param parent
	 * @param title
	 * @param desc
	 */
	public DTNodePart(DTNodeGrp parent,String title,String desc)
	{
		super(parent,title,desc) ;
	}
	
	public DTNodeGrp getParentNode()
	{
		return (DTNodeGrp)this.parent ;
	}
	
	@Override
	protected String getNodeTp()
	{
		return TP;
	}


	@Override
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject ret = super.toJO(b_show_detail).putOpt("part_id", this.partId);
		return ret;
	}
	
	@Override
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		this.partId = jo.optString("part_id") ;
		return true ;
	}

	@Override
	public JSONObject renderToTree(DTTreeRenderCtrl tr_ctrl)
	{
		return null;
	}

}
