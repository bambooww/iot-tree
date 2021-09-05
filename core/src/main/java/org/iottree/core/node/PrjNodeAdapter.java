package org.iottree.core.node;


public abstract class PrjNodeAdapter
{
	
	
	public void SW_callerOnResp(String shareprjid,byte[] cont) throws Exception
	{}
	
	public void SW_sharerOnReq(String callerprjid,byte[] cont) throws Exception
	{}
	
	
	public void SW_sharerOnWrite(String callerprjid,byte[] cont) throws Exception
	{}
	
	
	public void SW_callerOnPush(byte[] cont) throws Exception
	{}
}
