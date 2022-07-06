<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				 java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.basic.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.comp.*,
				 org.iottree.core.util.xmldata.*,
				 org.apache.commons.fileupload.*,
org.apache.commons.fileupload.servlet.*,
org.apache.commons.fileupload.disk.*"%><%!
		
%><%
if(!Convert.checkReqEmpty(request, out, "tmpfn","libtitle","op"))
	return;
String libid = request.getParameter("libid") ;
String libtitle = request.getParameter("libtitle") ;
String op = request.getParameter("op") ;
	String tmpfn = request.getParameter("tmpfn");
	File tmpf = new File(Config.getDataTmpDir(),tmpfn) ;
	if(!tmpf.exists())
	{
		out.print("no upload file found") ;
		return ;
	}
	
	switch(op)
	{
	case "ignore":
		tmpf.delete();
		return ;
	case "new":
		libid = null;
		break ;
	case "replace":
		if(Convert.isNullOrEmpty(libid))
		{
			out.print("no libid input") ;
			return ;
		}
		break ;
	default:
		out.print("unknown op") ;
		return ;
	}
	
	CompManager devmgr = CompManager.getInstance() ;
	HashMap<String,String> pms = devmgr.parseCompLibZipFileMeta(tmpf) ;
	if(pms==null)
	{
		out.print("no invalid Device Library file") ;
		return ;
	}
	
	String libid0 = pms.get("libid");
	String libtitle0 = pms.get("libtitle");
	String tp = pms.get("tp") ;
	if(!"complib".equals(tp))
	{
		out.println("invalid Library file") ;
		return ;
	}
	
	if(CompManager.getInstance().importCompLibZipFile(tmpf, libid, libtitle))
		out.print("succ") ;
	else
		out.print("import failed") ;
%>