package org.iottree.core.util.js;

import org.graalvm.polyglot.HostAccess;

public class Debug
{
	@HostAccess.Export
	public void print(String txt)
	{
		System.out.print(txt);
	}
	
	@HostAccess.Export
	public void println(String txt)
	{
		
		System.out.println(txt);
	}
}