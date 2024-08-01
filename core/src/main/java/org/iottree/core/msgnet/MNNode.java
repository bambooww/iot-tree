package org.iottree.core.msgnet;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.msgnet.MNBase.DivBlk;
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
	private long lastInMsgCost = -1 ;
	
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
	
	protected MNNode createNewIns(MNNet net) throws Exception
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

	public abstract boolean supportInConn() ;
	
	public boolean isEnd()
	{
		return false;
	}
	
	public JSONTemp getInJT()
	{
		return null;
	}

	public JSONTemp getOutJT()
	{
		return null;
	}
	

	public abstract int getOutNum() ;
	
	public String getOutColor(int idx)
	{
		return null ;
	}
	
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
	public final boolean checkHasPath(MNNode to_n)
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

	
	public final List<MNConn> getInConns()
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
	
	
	
	public JSONObject toListJO()
	{
		JSONObject jo = super.toListJO() ;
		jo.put("in", this.supportInConn()) ;
		int outn = getOutNum() ;
		jo.put("out_num", outn) ;
		if(outn>0)
		{
			//JSONArray jarr = new JSONArray() ;
			JSONArray jarr_c = new JSONArray() ;
			for(int i = 0 ; i < outn ; i ++)
			{
				String tt  = this.getOutColor(i) ;
				if(Convert.isNullOrEmpty(tt))
					tt = "" ;
				jarr_c.put(tt) ;
			}
			//jo.put("out_tts", jarr) ;
			jo.put("out_cs", jarr_c) ; //out color s
		}
		return jo;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO();//new JSONObject() ;

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
	
	private MNMsg lastMsgIn = null ;
	
	private HashMap<Integer,MNMsg> lastMsgOutMap = null ;
	
	public final MNMsg RT_getLastMsgIn()
	{
		return this.lastMsgIn ;
	}
	
	private synchronized void RT_setLastMsgOut(int idx,MNMsg m)
	{
		if(lastMsgOutMap==null)
			lastMsgOutMap = new HashMap<>() ;
		lastMsgOutMap.put(idx, m) ;
	}
	
	public final MNMsg RT_getLastMsgOut(int idx)
	{
		if(this.lastMsgOutMap==null)
			return null ;
		return this.lastMsgOutMap.get(idx) ;
	}
	
	public synchronized void RT_clean()
	{
		super.RT_clean(); 
		lastMsgIn = null ;
		lastMsgOutMap = null ;
	}
	
	protected abstract RTOut RT_onMsgIn(MNConn in_conn,MNMsg msg) throws Exception;
	
	final private void RT_onMsgInWithTrans(MNConn in_conn,MNMsg msg)
	{
		msg = in_conn.RT_transMsg(msg) ;
		if(msg==null)
			return ; // stopped by conn
		
		this.lastMsgIn = msg ;  //record
		
		//StringBuilder failedr = new StringBuilder() ;
		RTOut out = null ;
		long st = System.currentTimeMillis() ;
		try
		{
			out = RT_onMsgIn(in_conn,msg) ;
			lastInMsgCost = System.currentTimeMillis() - st ;
			if(out==null)
				return ; //may be processed later,
		}
		catch(Throwable ee)
		{
			this.RT_DEBUG_ERR.fire("msg_in", ee.getMessage(),ee);
			return ;
		}
		
		this.RT_DEBUG_ERR.clear("msg_in");
		if(out!=null)
			this.RT_sendMsgOut(out);
	}

	protected final void RT_sendMsgOut(RTOut out) //,MNMsg msg)
	{
		if(out==null) //||!out.hasOut)
			return ;
	
		int outn = this.getOutNum() ;
		if(outn<=0)
			return ;
		
		MNConnOut co = getConnOut() ;

		for(int i = 0 ; i < outn ; i ++)
		{
			MNMsg msg = out.getOutMsg(i) ;
			if(msg==null)
				continue ;
			
			RT_setLastMsgOut(i,msg);
			
			if(co==null)
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
	
	
	
	
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		super.RT_renderDiv(divblks);
		MNMsg msg = null ;
		if(this.supportInConn())
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div class=\"rt_blk\">Msg In [cost="+lastInMsgCost+"ms]") ;
			
			if((msg=this.RT_getLastMsgIn())!=null)
			{
				divsb.append(Convert.calcDateGapToNow(msg.getMsgDT())+ " <button onclick=\"debug_in_out_msg(\'"+this.getId()+"\',-1)\">View</button>") ;
			}
			divsb.append("</div>") ;
			divblks.add(new DivBlk("in_msg",divsb.toString())) ;
		}
		
		if(this.getOutNum()>0)
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div class='rt_blk'>Msg Out") ;
			for(int i = 0 ; i < this.getOutNum() ; i ++)
			{
				if((msg=this.RT_getLastMsgOut(i))!=null)
					divsb.append("<div class='rt_sub'>"+Convert.calcDateGapToNow(msg.getMsgDT())+"<button onclick=\"debug_in_out_msg(\'"+this.getId()+"\',"+i+")\">Out "+(i+1)+"</button></div>") ;
			}
			divsb.append("</div>") ;
			divblks.add(new DivBlk("out_msgs",divsb.toString())) ;
		}
	}
	
	public JSONObject RT_toJO(boolean out_rt_div)
	{
		JSONObject jo = super.RT_toJO(out_rt_div) ;
		jo.put("msg_in_id",lastMsgIn!=null?lastMsgIn.getMsgId():"") ;
		
		int outn = this.getOutNum() ;
		if(outn>0)
		{
			ArrayList<String> msg_outs = new ArrayList<>(outn) ;
			
			JSONArray jarr = new JSONArray() ;
			JSONArray jarr_c = new JSONArray() ;
			
			for(int i = 0 ; i < outn ; i ++)
			{
				MNMsg m = RT_getLastMsgOut(i);
				msg_outs.add(m!=null?m.getMsgId():"") ;
				
				String tt = this.RT_getOutTitle(i) ;
				if(Convert.isNullOrEmpty(tt))
					tt = "" ;
				jarr.put(tt) ;
				
				tt = this.RT_getOutColor(i) ;
				if(Convert.isNullOrEmpty(tt))
					tt = "" ;
				jarr_c.put(tt) ;
			}
			
			jo.put("msg_out_ids", msg_outs) ;
			jo.put("rt_out_tts", jarr) ;
			jo.put("rt_out_cs", jarr_c) ; //out color s
		}
		
		if(this.supportInConn())
		{
			String intt = this.RT_getInTitle() ;
			jo.putOpt("rt_in_tt",intt) ;
		}
		
		return jo ;
	}
	
	public String RT_getInTitle()
	{
		return null;
	}

	public String RT_getOutTitle(int idx)
	{
		return null ;
	}
	
	public String RT_getOutColor(int idx)
	{
		return null ;
	}
	
}
