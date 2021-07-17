<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				 java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.basic.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.util.xmldata.*,
				 org.apache.commons.fileupload.*,
org.apache.commons.fileupload.servlet.*,
org.apache.commons.fileupload.disk.*"%><%!
		
%><%
if(!Convert.checkReqEmpty(request, out, "tmpfn","catid","catname","drvname"))
	return;
	String tmpfn = request.getParameter("tmpfn");
	File tmpf = new File(Config.getDataTmpDir(),tmpfn) ;
	if(!tmpf.exists())
	{
		out.print("no upload file found") ;
		return ;
	}
	
	DevManager devmgr = DevManager.getInstance() ;
	HashMap<String,String> pms = devmgr.parseDevCatZipFileMeta(tmpf) ;
	if(pms==null)
	{
		out.print("no invalid Device Definition file") ;
		return ;
	}
	
	String catid = pms.get("catid");
	String catname = pms.get("catname");
	String drvname = pms.get("drvname") ;
	
	DevDriver dd = devmgr.getDriver(drvname) ;
	
	DevManager.getInstance().importDevCatZipFile(tmpf, catid, catname, drvname) ;

%>succ