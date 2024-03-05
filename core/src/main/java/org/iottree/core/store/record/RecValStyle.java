package org.iottree.core.store.record;

public enum RecValStyle
{
	successive_normal(0),  // 
	successive_accumulation(1),
	discrete(2); // bool enum etc
	
	private final int val ;
	
	RecValStyle(int v)
	{
		val = v ;
	}
	
	public int getVal()
	{
		return val ;
	}
	
	public String getTitle()
	{
		switch(val)
		{
		case 0:
			return "Successive Normal" ;
		case 1:
			return "Successive Accumulation";
		case 2:
			return "Discrete (bool/enum)";
		default:
			return null ;
		}
	}
	
	public static RecValStyle valOfInt(int i)
	{
		switch(i)
		{
		case 0:
			return successive_normal ;
		case 1:
			return successive_accumulation;
		case 2:
			return discrete;
		default:
			return null ;
		}
	}
}
