<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%>
<%//通信节点下挂载的设备
if(!Convert.checkReqEmpty(request, out, "path"))
	return;

String path=request.getParameter("path");

UANode n = UAUtil.findNodeByPath(path) ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}
if(!(n instanceof UANodeOCTags))
{
	out.print("not node oc tags") ;
	return ;
}
UANodeOCTags ntags = (UANodeOCTags)n ;
List<UATag> tags = ntags.listTagsAll() ;


String parent_p = ntags.getNodePathName() ;
if(Convert.isNotNullEmpty(parent_p))
	parent_p +="." ;

out.print("succ={dt:\'"+""+"\',vals:[") ;
boolean bfirst = true ;
for(UATag tg : tags)
{
	String pathn = tg.getNodePathName();
	pathn = pathn.substring(parent_p.length()) ;
	
	if(!bfirst)
		out.print(",") ;
	else
		bfirst = false ;

	UAVal val = tg.RT_getVal() ;
	boolean bvalid = false;
	String vstr = "" ;
	String dt = "" ;
	String dt_chg="" ;
	if(val!=null)
	{
		bvalid = val.isValid() ;
		vstr = ""+val.getObjVal() ;
		
		dt = Convert.toFullYMDHMS(new Date(val.getValDT())) ;
		dt_chg = Convert.toFullYMDHMS(new Date(val.getValChgDT())) ;
	}
	out.print("{path:\'");
	out.print(pathn) ;
	
	out.print("\',valid:"+bvalid+",v:\'"+vstr+"\',dt:\'"+dt+"\',chgdt:\'"+dt_chg+"\'}") ;
}

out.print("]}") ;
%>