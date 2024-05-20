package org.iottree.core.msgnet;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.iottree.core.util.IdCreator;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public abstract class MNNode
{
	String id = null ;
	
	String title = null ;
	
	String desc = null;
	
	MNNet belongTo = null ;
	
	float x = 0 ;
	float y = 0 ;
	
	protected transient String _nodeState = null ;
	
	protected transient Exception _nodeErr = null ;
	//boolean bStart = false;
	
	
	MNConnOut connOut = null ;
//	/**
//	 * 节点参数，实现节点可能会根据参数的不同，有不同的输出
//	 */
//	JSONObject paramJO = null ;
	
	public MNNode() //(RNDef def)
	{
		this.id = IdCreator.newSeqId() ;
		this.title = this.getNodeTPTitle();
	}
	

	public abstract String getNodeTP() ;
	
	
	public abstract String getNodeTPTitle() ;
	

	
	public String getId()
	{
		return this.id ;
	}
	
//	public String getUID()
//	{
//		return belongTo.getUid()+"."+this.id ;
//	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
	public MNNet getBelongTo()
	{
		return this.belongTo ;
	}
	
	
	public float getX()
	{
		return x ;
	}
	public float getY()
	{
		return  y;
	}

	public abstract String getColor() ;
	
	public abstract String getIcon() ;
	
	public abstract JSONTemp getInJT();
	
	public abstract boolean supportInConn() ;
	
	public boolean isEnd()
	{
		return false;
	}
	
	public abstract JSONTemp getOutJT();
	

	public abstract int getOutNum() ;
	
	public final MNConn getOutConn(int idx,String to_nid)
	{
		if(connOut==null)
			return null ;
		return connOut.getConn(idx, to_nid) ;
	}
	
	public final List<MNConn> getOutConns(int idx)
	{
		if(connOut==null)
			return null ;
		return connOut.getConns(idx) ;
	}
	
	public final List<MNConn> getOutConnsByToNodeId(String to_nid)
	{
		if(connOut==null)
			return null ;
		return connOut.getConnsByToNodeId(to_nid) ;
	}
	
	public final boolean hasOutConnNode(String to_nid)
	{
		if(connOut==null)
			return false;
		return connOut.hasConnToNode(to_nid) ;
	}
	
	public final List<MNNode> getOutConnNodes()
	{
		int n = this.getOutNum() ;
		if(connOut==null || n<=0)
			return null ;
		ArrayList<MNNode> rets = new ArrayList<>() ;
		for(int i = 0 ; i < n ; i ++)
		{
			List<MNConn> cs = getOutConns(i) ;
			if(cs==null||cs.size()<=0)
				continue ;
			for(MNConn c:cs)
			{
				MNNode ton = c.getToNode() ;
				if(ton==null)
					continue ;
				rets.add(ton) ;
			}
		}
		return rets ;
	}
	
	/**
	 * 
	 * @param from_n
	 * @param to_n
	 * @return
	 */
	public boolean checkHasPath(MNNode to_n)
	{
		if(this==to_n)
			throw new IllegalArgumentException("same node") ;
		List<MNNode> nextns = this.getOutConnNodes() ;
		if(nextns==null||nextns.size()<=0)
			return false;
		for(MNNode nn:nextns)
		{
			if(nn==to_n)
				return true ;
		}
		for(MNNode nn:nextns)
		{
			if(nn.checkHasPath(to_n))
				return true ;
		}
		return false;
	}
	
	public final MNConn setOutConn(int idx,String to_nid) throws MNException
	{
		int outn = this.getOutNum() ;
		if(outn<=0)
			throw new IllegalArgumentException("node has no out") ;
		if(idx>=outn)
			throw new IllegalArgumentException("idx is out of boundry") ;
		if(connOut==null)
			connOut = new MNConnOut(this) ;
		return connOut.setConn(idx, to_nid) ;
	}
	
	public final MNConn unsetOutConn(int idx,String to_nid)
	{
		if(connOut==null)
			return null ;
		return connOut.unsetConn(idx, to_nid) ;
	}

	
	public List<MNConn> getInConns()
	{
		if(!this.supportInConn())
			return null ;
		
		ArrayList<MNConn> rets = new ArrayList<>() ;
		for(MNNode n:this.belongTo.id2node.values())
		{
			if(n==this)
				continue ;
			List<MNConn> ccs = n.getOutConnsByToNodeId(this.getId()) ;
			if(ccs==null||ccs.size()<=0)
				continue ;
			rets.addAll(ccs) ;
		}
		return rets ;
	}
	
	/**
	 * 
	 * @return
	 */
	final boolean cleanOutConns()
	{
		if(this.connOut==null)
			return false;
		return connOut.cleanConns() ;
	}
	
	/**
	 * 节点是否需要设置参数才能运行，如果需要则需要定制UI展示设置界面
	 * 并且根据输入的参数和debug上下文，进行界面展示
	 * @return
	 */
	public abstract boolean needParam();
	
	/**
	 * 判断节点参数是否完备，只有完备之后的节点才可以运行
	 * @return
	 */
	public abstract boolean isParamReady();
	
	//to be override
	public abstract JSONObject getParamJO();
		
	//to be override
	public final void setParamJO(JSONObject jo)
	{
		setParamJO(jo,System.currentTimeMillis()) ;
	}
	
	protected abstract void setParamJO(JSONObject jo,long up_dt);
	
	
	public void renderOut(Writer w)
	{
		JSONObject jo = toJO() ;
		jo.write(w) ; 
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id) ;
		jo.putOpt("title", this.title) ;
		jo.putOpt("desc", desc);
		jo.put("_tp", getNodeTP()) ;
//		jo.put("uid", this.getUID());
		jo.put("x", this.x) ;
		jo.put("y", this.y) ;
		jo.put("in", this.supportInConn()) ;
		jo.put("out_num", getOutNum()) ;
		jo.put("color", this.getColor()) ;
		jo.put("icon", this.getIcon()) ;
//		jo.put("w", this.w) ;
//		jo.put("h", this.h) ;
		
		jo.putOpt("_node_st",_nodeState) ;
		if(_nodeErr!=null)
		{
			jo.putOpt("_node_err",_nodeErr.getMessage()) ;
		}
		
		
		
		
		if(connOut!=null)
		{
			JSONArray jarr = connOut.toJArr() ;
			jo.putOpt("out_conns", jarr) ;
		}
//		if(!(this instanceof MNNodeInput) && this.isInputMulti())
//			jo.put("input_multi", true) ;
		
		JSONObject pmjo = this.getParamJO() ;
		jo.putOpt("pm_jo", pmjo) ;
		jo.put("pm_need", this.needParam()) ;
		jo.put("pm_ready", this.isParamReady()) ;
		
		return jo;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.id = jo.getString("id") ;
		this.title = jo.optString("title") ;
		this.desc = jo.optString("desc") ;
		this.x = jo.optFloat("x",0) ;
		this.y = jo.optFloat("y",0) ;
//		this.w = jo.optFloat("w",100) ;
//		this.h = jo.optFloat("h",100) ;
		//this.bStart = jo.optBoolean("b_start",false) ;
		
		MNConnOut cos = new MNConnOut(this);
		if(cos.fromJArr(this, jo.optJSONArray("out_conns")))
			this.connOut = cos ;
		
		JSONObject pmjo = jo.optJSONObject("pm_jo") ;
		long updt = this.belongTo.updateDT ;
		this.setParamJO(pmjo,updt);
		
		return true;
	}
	
	protected boolean fromJOBasic(JSONObject jo,StringBuilder failedr)
	{
		this.x = jo.optFloat("x", this.x) ;
		this.y = jo.optFloat("y", this.y) ;
		//this.title = jo.optString("title") ;
		//this.desc = jo.optString("desc") ;
		//this.bStart = jo.optBoolean("b_start",false) ;
		return true ;
	}
	
}
