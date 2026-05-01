package org.iottree.core.cli;

import java.util.concurrent.Callable;

import org.iottree.core.UAPrj;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "tag_add", description = "Tag Add command")
class CmdTag implements Callable<Integer>
{
	private final UAPrj prj;
	
	@CommandLine.Option(names = { "-p", "--path" }, description = "container path like /xx/yy")
	private int path;
	
	@CommandLine.Option(names = { "-n", "--name" }, description = "tag name")
	private int name;
	
	public CmdTag(UAPrj prj)
	{
		this.prj = prj ;
	}
	
	public UAPrj getPrj()
	{
		return this.prj ;
	}

	@Override
	public Integer call()
	{
		
		return 0;
	}
}
