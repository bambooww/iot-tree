package org.iottree.core.plugin;

import java.io.File;

/**
 * The plug-in class that inherits this abstract class can obtain the plug-in running environment. 
 * Such as the file directory where the plug-in is located. In this way, when the plug-in is implemented,
 *  it can put some files it needs into its plug-in directory, 
 *  which can easily obtain the resources and other contents it provides.
 * 
 * @author jason.zhu
 *
 */
public abstract class AbstractPlugin
{
	File plugDirF = null ;
	
	void initPlugin(PlugDir pd)
	{
		//plugDirF = pd;
	}
	
	public File getPlugDir()
	{
		return plugDirF ;
	}
}
