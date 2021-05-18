package org.iottree.core;

public interface IOC
{
	/**
	 * by create,and never be changed
	 * @return
	 */
	public String getId() ;
	
	/**
	 * support dyn data output
	 * @return
	 */
	public String getName();
	
	//public void OC_setBaseVal(String name,String title);
	
	public String getTitle();
}
