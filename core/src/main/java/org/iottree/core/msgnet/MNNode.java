package org.iottree.core.msgnet;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public abstract class MNNode extends MNBase
{
	private MNModule TP_relatedM = null ;
	
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
		super();
		//this.title = this.getNodeTPTitle();
	}
	
	void TP_setRelatedModule(MNModule m)
	{
		this.TP_relatedM = m ;
		this.cat = m.cat ;
	}
	
	public MNModule TP_getRelatedModule()
	{
		return this.TP_relatedM ;
	}
	
	protected final String getOwnerTP()
	{
		if(this.TP_relatedM==null)
			return null ;
		else
			return this.TP_relatedM.getTP() ;
	}
	
	final MNNode createNewIns(MNNet net) throws Exception
	{
		MNNode new_n = (MNNode)this.getClass().getConstructor().newInstance() ;
		new_n.TP_relatedM = this.TP_relatedM ;
		new_n.belongTo = net;
		new_n.cat = this.cat ;
		//if(relatedM!=null)
		//	relatedM.nodeIdSet.add(new_n.id) ;
		return new_n ;
	}
	
	public MNModule getOwnRelatedModule()
	{
		for(MNModule m:this.belongTo.getModuleMapAll().values())
		{
			if(m.nodeIdSet.contains(this.getId()))
				return m ;
		}
		return null ;
	}
	
	public abstract JSONTemp getInJT();
	
	public abstract boolean supportInConn() ;
	
	public boolean isEnd()
	{
		return false;
	}
	
	public abstract JSONTemp getOutJT();
	

	public abstract int getOutNum() ;
	
	public final MNConnOut getConnOut()
	{
		return this.connOut ;
	}
	
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
	
	
	
	
	
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO();//new JSONObject() ;
//		jo.put("id", this.id) ;
//		String tt = this.title ;
//		if(Convert.isNullOrEmpty(tt))
//			tt = this.getNodeTPTitle() ;
//		jo.putOpt("title", tt) ;
//		jo.putOpt("desc", desc);
//		jo.put("_tp", getNodeTP()) ;
////		jo.put("uid", this.getUID());
//		jo.put("x", this.x) ;
//		jo.put("y", this.y) ;
		
		jo.put("in", this.supportInConn()) ;
		jo.put("out_num", getOutNum()) ;
		
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

//		JSONObject pmjo = this.getParamJO() ;
//		jo.putOpt("pm_jo", pmjo) ;
//		//jo.put("pm_need", this.needParam()) ;
//		StringBuilder sb = new StringBuilder() ;
//		boolean br = this.isParamReady(sb);
//		jo.put("pm_ready", br) ;
//		if(!br)
//			jo.put("pm_err", sb.toString()) ;
//		else
//			jo.put("pm_err", "") ;
		
		return jo;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		super.fromJO(jo) ;
		
		MNConnOut cos = new MNConnOut(this);
		if(cos.fromJArr(this, jo.optJSONArray("out_conns")))
			this.connOut = cos ;
		
		return true;
	}
	
	protected boolean fromJOBasic(JSONObject jo,StringBuilder failedr)
	{
		super.fromJOBasic(jo, failedr) ;
		
		
		return true ;
	}
	
	
	
	// runtime 
	
	
	
	protected abstract RTOut RT_onMsgIn(MNConn in_conn,MNMsg msg) ;
	
	final private void RT_onMsgInWithTrans(MNConn in_conn,MNMsg msg)
	{
		RTOut out = RT_onMsgIn(in_conn,msg) ;
		msg = in_conn.RT_transMsg(msg) ;
		if(msg==null)
			return ; // stopped by conn
		
		this.RT_sendMsgOut(out,msg);
	}

	protected final void RT_sendMsgOut(RTOut out,MNMsg msg)
	{
		if(out==null||!out.hasOut)
			return ;
	
		int outn = this.getOutNum() ;
		if(outn<=0)
			return ;
		MNConnOut co = getConnOut() ;
		if(co==null)
			return ;
		
		for(int i = 0 ; i < outn ; i ++)
		{
			if(out.outIdxs!=null && !out.outIdxs.contains(i))
				continue ;
			
			List<MNConn> conns = co.getConns(i) ;
			if(conns==null)
				continue ;
			for(MNConn conn:conns)
			{
				MNNode ton = conn.getToNode() ;
				if(ton==null)
					return ;
				ton.RT_onMsgInWithTrans(conn, msg);
			}
		}
	}
}
