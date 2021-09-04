package org.iottree.core.node;

public class NodeMsg
{
	public static enum MsgTp
	{
		req(1),
		resp(2),
		push(3),
		w(4);
		
		private final int val ;
		
		MsgTp(int v)
		{
			val = v ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
	}
	
	
	MsgTp msgTp = null ;
	
	String sorId = null ;
	
	String tarId = null ;
	
	byte[] content = null ;
	
	public MsgTp getMsgTp()
	{
		return msgTp ;
	}
	
	public String getSorId()
	{
		return sorId ;
	}
	
	public String getTarId()
	{
		return tarId ;
	}
	
	public byte[] getContent()
	{
		return content ;
	}
	
	public String toString()
	{
		return sorId+"-"+(tarId==null?"":tarId)+" "+msgTp+" "+(content!=null?content.length:0);
	}
}
