package org.iottree.core.basic;

import java.io.*;

public interface IConnEndPoint extends Closeable
{
	public InputStream getInputStream() ;
	
	public OutputStream getOutputStream() ;
	
}
