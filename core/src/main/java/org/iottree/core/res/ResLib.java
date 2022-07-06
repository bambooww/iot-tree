package org.iottree.core.res;

import java.io.File;

/**
 * for local res store and manager
 * @author jason.zhu
 *
 */
public class ResLib extends ResDir
{
	String name = null ;
	
	File resDir = null ;
	
	File norDir = null ;
	
	public ResLib(String n,File nordir,File res_dir)
	{
		super(res_dir) ;
		
		this.name = n ;
		this.norDir = nordir ;
		this.resDir = res_dir ;
	}
	
	public String getLibName()
	{
		return this.name ;
	}
	
	
	public File getNorDir()
	{
		return this.norDir;
	}
	
	/**
	 * for new res item id
	 * @return
	 */
	public int getNextResId()
	{
		return 1 ;
	}
	
	public File getResDir()
	{
		return this.resDir ;
	}
}
