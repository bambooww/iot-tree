package org.iottree.core.cxt;

import java.util.*;

public class UARtSystem
{
	public int getYear()
	{
		return Calendar.getInstance().get(Calendar.YEAR) ;
	}
	
	public int getMonth()
	{
		return Calendar.getInstance().get(Calendar.MONTH)+1 ;
	}
	
	public int getDay()
	{
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ;
	}
	
	public Date getDateTime()
	{
		return new Date() ;
	}
}
