package org.iottree.core.ext;

public abstract class AbstractPlugin
{
	public abstract String getPlugName() ;
	
	public String getPlugDesc() {return null ;}
	
	/**
	 * 
	 */
	public abstract void onInitPlugin() ;
	
	/**
	 * 
	 */
	public abstract void onCallPlugin() ;
}
