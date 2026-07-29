package org.iottree.driver.s7.eth;


public enum S7LinkTp
{
	PG(0x01),
	OP(0x02),
	PC(0x03);
	
	private final int val ;
	
	S7LinkTp(int v)
	{
		val =v ;
	}
	
	public int getVal()
	{
		return val ;
	}
}
