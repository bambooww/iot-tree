package org.iottree.core.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlugAuthUser
{
	String id,regName,fullName ;
	
	public PlugAuthUser(String id,String regname,String fulln)
	{
		this.id = id ;
		this.regName = regname ;
		this.fullName = fulln ;
	}
	
	
	public static PlugAuthUser createFromObj(Object ob) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(ob==null)
			return null;
		String regn=null,id=null,fulln=null;
		try
		{
			Method m = ob.getClass().getDeclaredMethod("getRegName");
			regn = (String)m.invoke(ob);
		}
		catch(NoSuchMethodException nsme) {
			return null;
		}
		
		try
		{
			Method m = ob.getClass().getDeclaredMethod("getId");
			id = (String)m.invoke(ob);
		}
		catch(NoSuchMethodException nsme) {}
		
		try
		{
			Method m = ob.getClass().getDeclaredMethod("getFullName");
			fulln = (String)m.invoke(ob);
		}
		catch(NoSuchMethodException nsme) {}
		
		return new PlugAuthUser(id,regn,fulln);
	}
	/**
	 * unique id  (option method)
	 * 
	 *  
	 * @return
	 */
	public String getId()
	{
		return this.id ;
	}
	
	/**
	 * get registion name (unique name for login)  (must have method)
	 * @return
	 */
	public String getRegName()
	{
		return this.regName ;
	}
	
	/**
	 * get display name (option method)
	 * @return
	 */
	public String getFullName()
	{
		return this.fullName ;
	}
}
