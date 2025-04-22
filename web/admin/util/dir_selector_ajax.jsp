<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.util.*
	"%><%!
	
	static boolean hasSubDir(File dir)
	{
		File[] subfs = dir.listFiles() ;
		if(subfs==null||subfs.length<=0)
			return false;
		for(File subf:subfs)
		{
			if(subf.isDirectory())
				return true ;
		}
		return false;
	}
	
	public static JSONObject rendAsRoot(File dir) //throws Exception
	{
		JSONObject jo = new JSONObject() ;
		String id = dir.getAbsolutePath().replaceAll("\\\\", "/") ;
		jo.put("id",id);// this.getId()) ;
		jo.put("nc", 0) ;
		jo.put("root", true) ;
		
		String icon =  "<i class=\"fa fa-folder\" /></i>";
		
		jo.put("icon", "fa fa fa-folder") ;
		String txt = "<span style='font-weight: bold;'>"+dir.getAbsolutePath()+"</span>" ;
		
		jo.put("text",icon+txt) ;
		jo.put("state", new JSONObject().put("opened", true)) ;
		JSONArray jarr = renderSubDir(dir) ;
		if(jarr==null)
			jarr = new JSONArray() ;
		jo.put("children", jarr) ;
		return jo ;
	}
	
	public static JSONArray renderSubDir(File dir) //throws Exception
	{
		JSONArray jarr = new JSONArray() ;
		File[] subfs = dir.listFiles() ; 
		if(subfs!=null)
		{
			for(File dn:subfs)
			{
				if(!dn.isDirectory())
					continue ;
				JSONObject jo = renderToTree(dn);//,null) ;
				if(jo==null)
					continue ;
				jarr.put(jo) ;
			}
		}
		return jarr ;
	}
	
	public static JSONObject renderToTree(File dir) //throws Exception
	{
		JSONObject jo = new JSONObject() ;
		String id = dir.getAbsolutePath().replaceAll("\\\\", "/") ;
		jo.put("id",id);
		String icon =  "<i class=\"fa fa-folder\" /></i>";
		
		jo.put("icon", "fa fa fa-folder") ;
		
		if(hasSubDir(dir))
			jo.put("children",true) ;
			
		jo.put("text","<div >"+icon+dir.getName()+"</div>");
		return jo ;
	}
%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;
String op = request.getParameter("op");
String root = request.getParameter("root") ;
String tree_nid = request.getParameter("tree_nid") ;
switch(op)
{
case "treen":
	if(!Convert.checkReqEmpty(request, out,"root"))
		return ;
	if(Convert.isNotNullEmpty(tree_nid))
	{
		File cur_dir = new File(tree_nid) ;
		JSONArray jarr = renderSubDir(cur_dir) ;
		if(jarr==null)
			return ;
		jarr.write(out) ;
	}
	else
	{
		File root_dir = new File(root) ;
		JSONObject jo = rendAsRoot(root_dir) ;
		if(jo==null)
			return ;
		jo.write(out) ;
	}
	return ;
default:
	break ;
}
%>