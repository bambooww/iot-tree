package org.iottree.core.util;

public interface IConsole
{
	public String getConsoleName() ;
	
	public String getConsoleTitle() ;
	
	public int onConsoleCmds(String[] cmds) throws Exception;
}
