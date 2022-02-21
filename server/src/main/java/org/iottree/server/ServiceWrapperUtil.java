package org.iottree.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;

public class ServiceWrapperUtil
{
	public static String readFileTxt(File f, String encod) throws IOException
	{
		byte[] bs = readFileBuf(f);
		if (encod==null||encod.equals(""))
			return new String(bs);
		else
			return new String(bs, encod);
	}

	public static byte[] readFileBuf(File f) throws IOException
	{
		if (f.length() > 10485760)
			throw new RuntimeException("file is too long");

		try (FileInputStream fis = new FileInputStream(f);)
		{
			int size = fis.available();
			byte[] buffer = new byte[size];
			fis.read(buffer);
			return buffer;
		}
	}
	
	private static String getJarCPInDir(String prefix,File dirf)
	{
		String[] fns = dirf.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".jar") ;
			}}) ;
		
		if(fns==null||fns.length<=0)
			return "" ;
		String ret = prefix+fns[0] ;
		for(int i = 1 ; i < fns.length ; i ++)
		{
			ret += ";"+prefix+fns[i] ;
		}
		return ret ;
	}
	
	private static String getClasspathStr()
	{//.\lib\*;.\tomcat\lib\*;.\tomcat\bin\bootstrap.jar;.\tomcat\bin\tomcat-juli.jar
		File libdir = new File("./lib/") ;
		File tlibdir = new File("./tomcat/lib/") ;
		String ret = getJarCPInDir("./lib/",libdir) ;
		ret += getJarCPInDir("./tomcat/lib/",tlibdir) ;
		ret +=";./tomcat/bin/bootstrap.jar;./tomcat/bin/tomcat-juli.jar" ;
		return ret ;
	}
	
	private static void setupWrapperClasspath() throws IOException
	{
		File conf_sor = new File("./wrapper.sor.conf");
		File conff = new File("./wrapper.conf");
		String txt = readFileTxt(conf_sor, "utf-8") ;
		BufferedReader br = new BufferedReader( new StringReader(txt)) ;
		try(FileOutputStream fos = new FileOutputStream(conff);)
		{
			String ln = null;
			while((ln=br.readLine())!=null)
			{
				if(ln.startsWith("wrapper.java.command="))
				{
					File jdkdir = new File("./jdk/") ;
					if(jdkdir.exists())
					{
						ln = "wrapper.java.command=.\\jdk\\bin\\java" ;
					}
				}
				
				if(ln.startsWith("wrapper.java.classpath.2="))
				{
					ln = "wrapper.java.classpath.2="+getClasspathStr() ;
				}
				fos.write(ln.getBytes("UTF-8"));
				fos.write("\r\n".getBytes());
			}
		}
	}
	
	public static void main(String[] args) throws IOException
	{//
		setupWrapperClasspath();
		System.out.println("wrapper util setup ok") ;
	}
}
