package org.iottree.ext.grpc;

public class ClientItem
{
	public String clientId ;
	
	public String remoteAddr ;
	
	public ClientItem(String clientid,String addr)
	{
		this.clientId = clientid ;
		this.remoteAddr = addr ;
	}
}
