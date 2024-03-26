package org.iottree.core.store.record;

import org.iottree.core.util.Lan;

public enum RecValStyle
{
	successive_normal(0,"succ_nor"),  // 
	successive_accumulation(1,"succ_acc"),
	discrete(2,"discrete"); // bool enum etc
	
	private final int val ;
	private final String mark ;
	
	RecValStyle(int v,String m)
	{
		val = v ;
		this.mark = m ;
	}
	
	public int getVal()
	{
		return val ;
	}
	
	public String getTitle()
	{
		Lan lan = Lan.getLangInPk(RecValStyle.class) ;
		return lan.g("val_sty_"+this.mark) ;
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
