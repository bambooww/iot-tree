<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.station.*,
	org.iottree.core.util.logger.*
	"%><%!

%><%
if(!PlatformManager.isInPlatform())
{
	out.println("not platform") ;
	return ;
}

if(!Convert.checkReqEmpty(request, null, "stationid","module","path","subf"))
{
	throw new Exception("invalid input") ;
}
String token= request.getHeader(PSCmdDirSyn.TOKEN) ;
if(Convert.isNullOrEmpty(token))
	throw new Exception("no token") ;

String stationid = request.getParameter("stationid") ;
String module = request.getParameter("module") ;
String path = request.getParameter("path") ;
String subf = request.getParameter("subf") ;
PStation pstat = PlatformManager.getInstance().getStationById(stationid) ;
if(pstat==null)
	throw new Exception("no station found") ;
if(!pstat.RT_synDirCheckDownloadToken(token))
	throw new Exception("token check failed") ;

File plat_f = new File(PSCmdDirSyn.calDir(module, path),subf) ;
if(plat_f==null || !plat_f.exists())
	throw new Exception("no platfrom file module="+module+" path="+path+" subf="+subf) ;

OutputStream outos = response.getOutputStream() ;
try(FileInputStream fis = new FileInputStream(plat_f))
{
	byte[] bs = new byte[4096] ;
	int rlen ;
	while((rlen=fis.read(bs))>=0)
	{
		outos.write(bs, 0, rlen) ;
	}
	outos.flush() ;
}
%>