package org.iottree.driver.zigbee.ember.util;

public class EZSPCallbackType
{
	private static String[] OPTS = new String[] {
			"not_callback", // 0 This response is not a callback.
			"syn_callback", //1 =This response is a synchronous callback. It was sent in response to a callback command.
			"asyn_callback", //2 =(UART interface only) This response is an asynchronous callback. It was not sent in response to a callback command.
			"Reserved", //3
	};
	
	private int v ;
	
	public EZSPCallbackType(int v)
	{
		if(v<0||v>3)
			throw new IllegalArgumentException("v must in [0-3]") ;
		this.v = v ;
	}

	public int getVal()
	{
		return this.v ;
	}
	
	public String getTitle()
	{
		return OPTS[this.v] ;
	}
	
	public String toString()
	{
		return v+"="+this.getTitle() ;
	}
}
