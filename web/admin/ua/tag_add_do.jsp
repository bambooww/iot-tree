<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
if(!Convert.checkReqEmpty(request, out, "repid","pid","name"))
	return;
String repid = request.getParameter("repid") ;
String pid = request.getParameter("pid");
String name=request.getParameter("name");
String title = request.getParameter("title");
String desc = request.getParameter("desc");
String addr = request.getParameter("addr");
boolean bmid = "true".equalsIgnoreCase(request.getParameter("mid")) ;

int vt = Convert.parseToInt32(request.getParameter("vt"),1);
UAVal.ValTP dt = UAVal.getValTp(vt) ;
long srate = Convert.parseToInt64(request.getParameter("srate"),100);
boolean canw = "true".equalsIgnoreCase(request.getParameter("canw"));

float x = Convert.parseToFloat(request.getParameter("x"), 0.0f);
float y = Convert.parseToFloat(request.getParameter("y"), 0.0f);

StringBuilder errsb = new StringBuilder() ;
UAManager uam = UAManager.getInstance();
UARep dc = uam.getRepById(repid) ;
if(dc==null)
{
	out.print("no rep found with id="+repid) ;
	return ;
}
UANode n = dc.findNodeById(pid);
if(n==null)
{
	out.print("no node with id="+pid) ;
	return ;
}

UATag tag = null;
try
{
	if(n instanceof UANodeOCTags)
	{
		UANodeOCTags nt = (UANodeOCTags)n;
		tag = nt.addTag(bmid,name, title, desc,addr,dt,canw,srate) ;
	}
	else if(n instanceof UATagG)
	{
		UATagG tgg = (UATagG)n;
		tag = tgg.addTag(bmid,name, title, desc,addr,dt,canw,srate) ;
	}
	else if(n instanceof UATagList)
	{
		UATagList tgl = (UATagList)n;
		UANode pn = tgl.getParentNode() ;
		if(pn instanceof UANodeOCTags)
			tag = ((UANodeOCTags)pn).addTag(bmid,name, title, desc,addr,dt,canw,srate) ;
		else if(pn instanceof UATagG)
			tag = ((UATagG)pn).addTag(bmid,name, title, desc,addr,dt,canw,srate) ;
	}
	else if(n instanceof UATag)
	{
		UATagList tl = (UATagList)n.getParentNode();
		UANode pn = tl.getParentNode() ;
		if(pn instanceof UADev)
			tag = ((UADev)pn).addTag(bmid,name, title, desc,addr,dt,canw,srate) ;
		else if(pn instanceof UATagG)
			tag = ((UATagG)pn).addTag(bmid,name, title, desc,addr,dt,canw,srate) ;
	}
}
catch(Exception e)
{
	out.print(e.getMessage());
	return ;
}

if(tag==null)
{
	out.print(errsb.toString()) ;
	return ;
}
else
{
	out.print("succ="+tag.getId()) ;
	return ;
}
%>