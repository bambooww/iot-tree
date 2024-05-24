package org.iottree.core.msgnet;

import java.util.List;

public class RTOut
{
	boolean hasOut ;
	
	List<Integer> outIdxs ; // null = all outs
	
	private RTOut(boolean has_out)
	{
		this.hasOut = has_out ;
		this.outIdxs = null ; // all if has out
	}
	
	public RTOut(List<Integer> outidxs)
	{
		this.hasOut = true ;
		this.outIdxs = outidxs ;
	}
	
	public boolean isHasOut()
	{
		return this.hasOut ;
	}
	
	public List<Integer> getOutIdxs()
	{
		return outIdxs ;
	}
	
	public static final RTOut ALL = new RTOut(true) ;
	
	public static final RTOut NONE = new RTOut(false) ;
}
