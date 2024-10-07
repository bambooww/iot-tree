package org.iottree.core.util;

public class Env
{
	static final String OS = System.getProperty("os.name").toLowerCase();
	static final String arch = System.getProperty("os.arch").toLowerCase();
	
	public static boolean isOSWin()
	{
		return OS.contains("win") ;
	}
	
	public static boolean isJVM_Win32()
	{
		if(!isOSWin())
			return false ;
		
		return arch.equals("x86") ;
	}
}
