<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				java.net.*"%><%
	String op = request.getParameter("op");
	
	File dirf = new File(Config.getDataDirBase()+"/ua/ui/") ;
	if(!dirf.exists())
		dirf.mkdirs();
	if("save".equalsIgnoreCase(op))
	{//save temp
		String id = request.getParameter("id");
		if(Convert.isNullOrEmpty(id))
		{
	out.print("no id input") ;
	return ;
		}
		
		File f = new File(Config.getDataDirBase()+"/ua/ui/temp_"+id+".txt") ;
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
		File f = new File(Config.getDataDirBase()+"/ua/ui/temp_"+id+".txt") ;
		if(!f.exists())
	f =  new File(Config.getDataDirBase()+"/ua/ui/template.txt") ;
		
		String txt= Convert.readFileTxt(f, "UTF-8") ;
		out.print(txt) ;
		return ;
	}
%>
err:unknow op