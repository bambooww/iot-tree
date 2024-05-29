package org.iottree.core.msgnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RTOut
{
	//boolean hasOut ;
	
	//List<Integer> outIdxs ; // null = all outs
	
	private MNMsg outAllMsg = null ;
	
	private HashMap<Integer,MNMsg> outIdx2Msg = null ;
	
	private RTOut(MNMsg outall_msg)
	{
		//this.hasOut = has_out ;
		//this.outIdxs = null ; // all if has out
		this.outAllMsg = outall_msg ;
	}
	
	private boolean isOutAll()
	{
		return this.outAllMsg!=null ;
	}
	
	
	
	public RTOut asIdxMsg(int idx,MNMsg m)
	{
		this.outIdx2Msg.put(idx, m) ;
		return this ;
	}
	
//	public MNMsg getOutAllMsg()
//	{
//		return this.outAllMsg;
//	}
//	
//	public List<Integer> listOutIdxs()
//	{
//		ArrayList<Integer> rets = new ArrayList<>() ;
//		rets.addAll(this.outIdx2Msg.keySet()) ;
//		return rets ;
//	}
//
//	public Map<Integer,MNMsg>  getOutIdx2Msg()
//	{
//		return outIdx2Msg ;
//	}
	
	// for using
	
	public boolean hasOutIdx(int i)
	{
		if(this.isOutAll())
			return true ;
		return this.outIdx2Msg.containsKey(i) ;
	}
	
	public MNMsg getOutMsg(int idx)
	{
		if(this.isOutAll())
			return this.outAllMsg ;
		
		return this.outIdx2Msg.get(idx) ;
	}
	
	public static final RTOut createOutAll(MNMsg m)
	{
		return new RTOut(m) ;
	}
	
	public static final RTOut createOutIdx()
	{
		RTOut rto = new RTOut(null) ;
		rto.outIdx2Msg = new HashMap<>() ;
		return rto ;
	}
	
//	public static final RTOut ALL = new RTOut(true) ;
//	
//	public static final RTOut NONE = new RTOut(false) ;
}
