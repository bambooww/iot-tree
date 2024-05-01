package org.iottree.ext.roa;

import java.io.IOException;

public class ROAModbusSlave
{

	private void test()
	{
		try
		{
			throw new IOException();
		}
		catch(IOException | IllegalArgumentException e)
		{}
	}
}
