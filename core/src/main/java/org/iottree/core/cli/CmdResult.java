package org.iottree.core.cli;

import org.iottree.core.util.*;

public class CmdResult
{
	int exitCode;
	String output;
	String error;

	public CmdResult(int exitCode, String output, String error)
	{
		this.exitCode = exitCode;
		this.output = output;
		this.error = error;
	}

	public boolean isSuccess()
	{
		return exitCode == 0;
	}
	
	@Override
	public String toString()
	{
		String ret = "["+exitCode+"]" + output;
		if(Convert.isNotNullEmpty(error))
			ret +=" err:"+error;
		return ret ;
	}
}
