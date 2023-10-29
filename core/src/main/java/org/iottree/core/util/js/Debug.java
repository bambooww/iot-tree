package org.iottree.core.util.js;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.graalvm.polyglot.HostAccess;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsDef;

@JsDef(name="debug",title="Debug",desc="System Debug",icon="icon_debug")
public class Debug extends JSObMap
{
	PrintWriter pw = null;
	

	public void setOutPipe(PrintWriter pw)
	{
		this.pw = pw ;
	}
	
	@HostAccess.Export
	public void print(String txt)
	{
		if(pw!=null)
		{
			pw.print(txt);
			return ;
		}
		System.out.print(txt);
	}
	
	@HostAccess.Export
	public void println(String txt)
	{
		if(pw!=null)
		{
			pw.println(txt);
			return ;
		}
		System.out.println(txt);
	}
	
}