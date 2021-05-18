<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				org.json.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				java.net.*"%><%!public static String loadUnitJsonById(String id) throws Exception
			{
					File f = new File(Config.getDataDirBase()+"/ua/unit/u_"+id+".txt") ;
					if(!f.exists())
					{
						return null;
					}
					String txt= Convert.readFileTxt(f, "UTF-8") ;
					f = new File(Config.getDataDirBase()+"/ua/unit/u_"+id+".ext") ;
					if(f.exists())
					{
						JSONObject jobj=new JSONObject(txt);
						String exttxt = Convert.readFileTxt(f, "UTF-8") ;
						JSONObject extobj = new JSONObject(exttxt);
						jobj.put("_ext", extobj);
						txt = jobj.toString();
					}
					return txt;
			}%><%
	String op = request.getParameter("op");
	
	File dirf = new File(Config.getDataDirBase()+"/ua/unit/") ;
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
		 return fn.startsWith("u_")&&fn.endsWith(".txt");
	 }
		});
		out.print("[");
		boolean bfirst = true ;
		for(File tmpf:fs)
		{
	String tmpfn = tmpf.getName();
	String id = tmpfn.substring(2,tmpfn.length()-4);
	String txt = loadUnitJsonById(id);
	//String txt= Convert.readFileTxt(tmpf, "UTF-8") ;
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
		String name = request.getParameter("name");
		String title = request.getParameter("title");
		
		File f = new File(Config.getDataDirBase()+"/ua/unit/u_"+id+".txt") ;
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
		String txt = loadUnitJsonById(id);
		if(txt==null)
	txt = "{}" ;
		out.print(txt) ;
		return ;
	}
%>
err:unknow op