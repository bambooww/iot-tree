package org.iottree.core.node;

import org.iottree.core.node.NodeMsg.MsgTp;

public abstract class PrjNodeAdapter
{
	
	
	public void SW_callerOnResp(String shareprjid,byte[] cont) throws Exception
	{}
	
	public void SW_sharerOnReq(String callerprjid,byte[] cont) throws Exception
	{}
	
	public void SW_callerOnPush(byte[] cont) throws Exception
	{}
}
