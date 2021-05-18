package org.iottree.core.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.ConnProvider;

public class ConnPtCOM extends ConnPtStream
{
	public static String TP = "com" ;
	
	public ConnPtCOM()
	{}
	
	public ConnPtCOM(ConnProvider cp, String name, String title,String desc)
	{
		super(cp, name, title,desc);
	}

	public String getConnType()
	{
		return "com" ;
	}
	
	@Override
	public boolean isClosed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnReady()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected InputStream getInputStreamInner()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected OutputStream getOutputStreamInner()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStaticTxt()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

}
