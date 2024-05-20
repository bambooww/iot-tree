package org.iottree.core.msgnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.util.ILang;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * node out
 * 
 * @author jason.zhu
 *
 */
public class MNConnOut implements ILang
{
	MNNode belongTo = null ;
	
	private HashMap<Integer,ArrayList<MNConn>> idx2conns = new HashMap<>() ;
	
	public MNConnOut(MNNode belongn)
	{
		this.belongTo = belongn ;
	}
	
	public List<MNConn> getConns(int idx)
	{
		int n = belongTo.getOutNum() ;
		if(n==0 || idx>=n)
			return null ;
		if(this.idx2conns==null)
			return null ;
		return idx2conns.get(idx) ;
	}
	
	public MNConn getConn(int idx,String to_nid)
	{
		List<MNConn> conns = getConns(idx) ;
		if(conns==null)
			return null;
		for(MNConn conn:conns)
		{
			if(conn.getToNodeId().equals(to_nid))
				return conn ;
		}
		return null ;
	}
	
	public final boolean hasConnToNode(String to_nid)
	{
		int n = belongTo.getOutNum() ;
		if(n<=0)
			return false;
		for(int i = 0 ; i < n ; i ++)
		{
			MNConn cc = getConn(i,to_nid) ;
			if(cc!=null)
				return true ;
		}
		return false;
	}
	
	public final List<MNConn> getConnsByToNodeId(String to_nid)
	{
		int n = belongTo.getOutNum() ;
		if(n<=0)
			return null;
		ArrayList<MNConn> rets = new ArrayList<>(n) ;
		for(int i = 0 ; i < n ; i ++)
		{
			MNConn cc = getConn(i,to_nid) ;
			if(cc!=null)
				rets.add(cc) ;
		}
		return rets;
	}
	
	public MNConn setConn(int idx,String to_nid) throws MNException
	{
		int n = belongTo.getOutNum() ;
		if(n==0 || idx>=n)
			throw new MNException("node has no output or idx out of out_num") ;
		
		MNConn oldc = getConn(idx,to_nid);
		if(oldc!=null)
			throw new MNException("conn is already existed") ;
		
		MNNode to_n = this.belongTo.belongTo.getNodeById(to_nid) ;
		if(to_n==null)
			throw new MNException("no target node found with id="+to_nid) ;
		if(to_n==this.belongTo)
			throw new MNException("target node cannot be self") ;
		
		if(!to_n.supportInConn())
			throw new MNException("target node has no input") ;
		
		if(to_n instanceof MNNodeStart)
			throw new MNException("cannot connect to start node") ;
		
		if(to_n.checkHasPath(this.belongTo))
			throw new MNException(g("make_loop_err")) ;
		
		ArrayList<MNConn> conns = idx2conns.get(idx) ;
		if(conns==null)
		{
			conns = new ArrayList<>() ;
			idx2conns.put(idx,conns) ;
		}
		MNConn conn = new MNConn(this,idx,to_n) ;
		conns.add(conn) ;
		return conn ;
	}
	
	public MNConn unsetConn(int idx,String to_nid)
	{
		List<MNConn> conns = getConns(idx) ;
		if(conns==null)
			return null;
		for(MNConn conn:conns)
		{
			if(conn.getToNodeId().equals(to_nid))
			{
				conns.remove(conn) ;
				return conn ;
			}
		}
		return null ;
	}
	
	boolean cleanConns()
	{
		int n = belongTo.getOutNum() ;
		if(n<=0)
		{
			if(idx2conns!=null && idx2conns.size()>0)
			{
				idx2conns.clear();
				return true ;
			}
			return false;
		}
		
		MNNet net = this.belongTo.getBelongTo() ;
		boolean ret = false;
		for(int i = 0 ; i < n ; i ++)
		{
			ArrayList<MNConn> conns = this.idx2conns.get(i) ;
			if(conns==null||conns.size()<=0)
				continue ;
			
			for(int j = 0 ; j < conns.size() ; )
			{
				MNConn conn = conns.get(j) ;
				String tid = conn.getToNodeId();
				MNNode tarnode = net.getNodeById(tid) ;
				if(tarnode==null)
				{
					conns.remove(j) ;
					ret = true ;
					continue ;
				}
				
				j ++;
			}
		}
		return ret;
	}
	
	public JSONArray toJArr()
	{
		int outn = belongTo.getOutNum() ;
		if(outn<=0)
			return null ;
		
		JSONArray ret = new JSONArray() ;
		for(int i = 0 ; i < outn ; i ++)
		{
			List<MNConn> conns = idx2conns.get(i) ;
			if(conns==null)
				continue ;
			for(MNConn conn:conns)
			{
				JSONObject tmpjo = conn.toJO() ;
				ret.put(tmpjo) ;
			}
		}
		return ret ;
	}
	
	public boolean fromJArr(MNNode node,JSONArray jarr)
	{
		if(jarr==null)
			return false ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			MNConn conn = MNConn.createFromJO(this, jo) ;
			if(conn==null)
				continue ;
			int idx = conn.getOutIdx() ;
			ArrayList<MNConn> conns = idx2conns.get(idx) ;
			if(conns==null)
			{
				conns = new ArrayList<>() ;
				idx2conns.put(idx,conns) ;
			}
			conns.add(conn) ;
		}
		return true ;
	}
}
