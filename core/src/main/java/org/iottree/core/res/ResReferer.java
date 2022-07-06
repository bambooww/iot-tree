package org.iottree.core.res;

import java.io.File;

public class ResReferer //extends ResDir
{
	String refLibName= null ;
	
	/**
	 * local copied local dir
	 */
	File refLocDir = null ;
	
	public ResReferer(String libn,File rldir)
	{
		this.refLibName = libn ;
		this.refLocDir = rldir ;
	}
	
	public String getRefLibName()
	{
		return this.refLibName ;
	}
	
	public File getRefLocDir()
	{
		return refLocDir ;
	}

	
	protected File getResDir()
	{
		return refLocDir;
	}
	
	
}
