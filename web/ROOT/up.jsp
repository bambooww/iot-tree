<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.res.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%	
	InputStream inputs = request.getInputStream() ;
	byte[] buf = new byte[1024] ;
	int rlen ;
	ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
	while((rlen=inputs.read(buf))>=0)
	{
		if(rlen<=0)
			continue ;
		baos.write(buf, 0, rlen) ;
	}
	String str = new String(baos.toByteArray(),"UTF-8");
	System.out.println("up recved len="+str.length());
%>ok