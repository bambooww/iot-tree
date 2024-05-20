package org.iottree.core.msgnet;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * out connection
 * 
 * @author jason.zhu
 *
 */
public class MNConn
{
	String to_nid ;
	MNNode to = null ;
	
	MNConnOut belongTo = null ; //same as from
	
	MNNode from = null ;
	
	int outIdx = 0 ;
	
	boolean bJsEn = false;
	
	String jsTxt = null ;
	
	public MNConn(MNConnOut belongto,int idx,String to_nid)
	{
		this.from = belongto.belongTo ;
		if(idx<0 || idx>=from.getOutNum())
			throw new IllegalArgumentException("invalid out idx") ;
		
		this.belongTo = belongto ;
		this.outIdx = idx ;
		this.to_nid = to_nid ;
	}
	
	MNConn(MNConnOut belongto,int idx,MNNode to)
	{
		this.from = belongto.belongTo ;
		if(idx<0 || idx>=from.getOutNum())
			throw new IllegalArgumentException("invalid out idx") ;
		
		this.belongTo = belongto ;
		this.outIdx = idx ;
		this.to_nid = to.getId() ;
		this.to = to ;
	}
	
	public MNNode getFromBelongToNode()
	{
		return this.from ;
	}
	
	public int getOutIdx()
	{
		return outIdx;
	}
	
	public String getToNodeId()
	{
		return this.to_nid ;
	}
	
	public MNNode getToNode()
	{
		if(to!=null)
			return to ;

		to = from.belongTo.getNodeById(to_nid) ;
		return to ;
	}
	
	public String getUid()
	{
		return calConnId(from.getId(),this.outIdx,to_nid) ;
	}
	
	public boolean isJsEn()
	{
		return bJsEn ;
	}
	
	public String getJsTxt()
	{
		return this.jsTxt ;
	}
	
	public static String calConnId(String from_nid,int from_outidx,String to_nid)
	{
		return from_nid+"_"+from_outidx+"-"+to_nid ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("idx", this.outIdx) ;
		jo.put("tid", to_nid) ;
		jo.put("js_en", this.bJsEn) ;
		jo.putOpt("js", this.jsTxt) ;
		return jo ;
	}
	
	
	static MNConn createFromJO(MNConnOut connout,JSONObject jo)
	{
		int idx = jo.optInt("idx",-1) ;
		if(idx<0)
			return null ;
		int n = connout.belongTo.getOutNum() ;
		if(idx>=n)
			return null ;
		
		String tid = jo.optString("tid") ;
		if(Convert.isNullOrEmpty(tid))
			return null;
		
		MNConn ret = new MNConn(connout,idx,tid) ;
		ret.bJsEn = jo.optBoolean("js_en",false) ;
		ret.jsTxt = jo.optString("js") ;
		return ret ;
	}
//	public void fromJOBasic(JSONObject jo)
//	{
//		this.bJsEn = jo.optBoolean("js_en",false) ;
//		this.jsTxt = jo.optString("js") ;
//	}
}
