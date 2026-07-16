package org.iottree.core.devtree;

import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * DevPart Type
 * 
 * it's for abstraction a set of Device Part
 * 
 * it's used by DTDevPart
 * 
 * @author jason.zhu
 *
 */
public class DTDevPartTP extends DTNodeRoot implements Comparable<DTDevPartTP>
{
	public static final String TP = "p" ;
	
	DTDevPartLib owner ;
	
	String parttpId = null ;
	
	long modifyDT  = -1 ;
	/**
	 * for load
	 * @param parent
	 */
	DTDevPartTP(DTDevPartLib owner)
	{
		super() ;
		this.owner = owner;
	}
	
	/**
	 * for new
	 * @param parent
	 * @param title
	 * @param desc
	 */
	public DTDevPartTP(DTDevPartLib owner,String title,String desc)
	{
		super(title,desc) ;
		this.owner = owner;
		this.nodeId = this.parttpId = CompressUUID.createNewId();
	}
	
	public static DTDevPartTP createByCopy(DTDevPartLib owner,DTNode cp_nd)
	{
		DTDevPartTP ret = new DTDevPartTP(owner) ;
		ret.nodeId = ret.parttpId = CompressUUID.createNewId();
		ret.copyLocalFromOth(cp_nd, true,true);
		ret.copySubDeepFromOth(ret, cp_nd, true,true, true, true);
		return ret ;
	}
	
//	public DTDevPartTP(DTNode cp_nd)
//	{
//		super(null,cp_nd,true,false,true) ;
//		this.parttpId = CompressUUID.createNewId();
//	}
	
	public String getPartTpUID()
	{
		return this.owner.getLibId()+"."+this.parttpId ;
	}
	
	public String getPartTpId()
	{
		return this.parttpId ;
	}
	
	@Override
	public String getRootId()
	{
		return this.parttpId ;
	}
	
	@Override
	public DTNode getParentNode()
	{
		return null;
	}
	
	@Override
	protected String getNodeTp()
	{
		return TP;
	}

	public List<DTDevPart> listParts()
	{
		return null ;
	}
	
	public DTDevPart getPartById(String part_id)
	{
		return null ;
	}

	@Override
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject ret = super.toJO(b_show_detail).putOpt("parttp_id", this.parttpId);
		return ret;
	}
	
	@Override
	public boolean fromJO(JSONObject jo)
	{
		this.parttpId = jo.optString("parttp_id") ;
		if(Convert.isNullOrEmpty(parttpId))
			return false;
		if(!super.fromJO(jo))
			return false;
		
		return true ;
	}

	@Override
	public JSONObject renderToTree(DTTreeRenderCtrl tr_ctrl)
	{
		return null;
	}

	@Override
	public int compareTo(DTDevPartTP o)
	{
		long d = o.modifyDT-o.modifyDT;
		if(d==0)
			return 0 ;
		return d>0?1:-1 ;
	}

}
