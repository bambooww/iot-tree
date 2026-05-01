package org.iottree.core.cli;

import org.iottree.core.*;
import junit.framework.TestCase;

public class CliTest extends TestCase
{
	public void test1() throws Exception
	{
		UAPrj prj = UAManager.getInstance().getPrjByName("xx");
		CLIManager clkmgr=  CLIManager.getInstance(prj) ;
		String doc = clkmgr.getAllCommandDocs() ;
		System.out.println(doc) ;

		CmdResult cr = clkmgr.executeInternal("sensor -r 12345") ;
		System.out.println(cr) ;
	}

}
