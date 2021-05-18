<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.system.*,
				java.net.*"%><%
	String op = request.getParameter("op");
	
	File dirf = new File(Config.getDataDirBase()+"/iott/rep/") ;
	if(!dirf.exists())
		dirf.mkdirs();
	if("load_all".equalsIgnoreCase(op))
	{
		File[] fs = dirf.listFiles(new FileFilter(){
	 public boolean accept(File f)
	 {
		 if(f.isDirectory())
			 return false;
		 String fn =  f.getName();
		 return fn.startsWith("rep_")&&fn.endsWith(".txt");
	 }
		});
		out.print("[");
		boolean bfirst = true ;
		for(File tmpf:fs)
		{
	String txt= Convert.readFileTxt(tmpf, "UTF-8") ;
	if(Convert.isNullOrEmpty(txt))
		continue ;
	if(bfirst)bfirst=false;
	else out.print(",");
	out.print(txt);
		}
		out.print("]");
		return ;
	}
	if("save".equalsIgnoreCase(op))
	{
		String id = request.getParameter("id");
		if(Convert.isNullOrEmpty(id))
		{
	out.print("no id input") ;
	return ;
		}
		
		File f = new File(Config.getDataDirBase()+"/iott/rep/r_"+id+".txt") ;
		if(!f.getParentFile().exists())
	f.getParentFile().mkdirs();
		String txt = request.getParameter("txt") ;
		try(FileOutputStream fos = new FileOutputStream(f))
		{
	fos.write(txt.getBytes("UTF-8"));
		}
		out.print("save ok");
		return ;
	}
	if("load".equalsIgnoreCase(op))
	{
		String id = request.getParameter("id");
		if(Convert.isNullOrEmpty(id))
		{
	out.print("no id input") ;
	return ;
		}
		File f = new File(Config.getDataDirBase()+"/iott/rep/r_"+id+".txt") ;
		if(!f.exists())
	f =  new File(Config.getDataDirBase()+"/iott/rep/template.txt") ;
		
		String txt= Convert.readFileTxt(f, "UTF-8") ;
		out.print(txt) ;
		return ;
	}
	if("temp".equalsIgnoreCase(op))
	{
		File f =  new File(Config.getDataDirBase()+"/iott/rep/template.txt") ;
		
		String txt= Convert.readFileTxt(f, "UTF-8") ;
		out.print(txt) ;
		return;
	}
%>
err:unknow op