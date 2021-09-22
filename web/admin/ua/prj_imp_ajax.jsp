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
if(!Convert.checkReqEmpty(request, out, "tmpfn"))
	return;
String tmpfn = request.getParameter("tmpfn") ;
boolean bdemo = "true".equals(request.getParameter("demo")) ;
File tmpf = null;
if(bdemo)
{
	tmpf = new File(Config.getDataDirBase()+"/demo/",tmpfn) ;
}
else
{
	tmpf = new File(Config.getDataTmpDir(),tmpfn) ;
}
if(!tmpf.exists())
{
	out.print("no upload file found") ;
	return ;
}

HashMap<String,String> pms = new HashMap<>() ;
for(Enumeration<String> en = request.getParameterNames();en.hasMoreElements();)
{
	String pn = en.nextElement() ;
	if(!pn.startsWith("id_"))
		continue ;
	String id = pn.substring(3) ;
	String v = request.getParameter(pn) ;
	pms.put(id, v) ;
}

List<IdName> idnames = UAManager.getInstance().parsePrjZipFile(tmpf) ;
if(idnames==null||idnames.size()<=0)
{
	out.print("no project found") ;
	return ;
}

for(IdName idn:idnames)
{
	String pv = pms.get(idn.getId()) ;
	if(pv==null)
		continue ;
	String newid =  CompressUUID.createNewId();
	if(pv.startsWith("rename_"))
	{
		String newn = pv.substring(7) ;
		String newtt = pms.get(idn.getId()+"_title");
		UAPrj oldp = UAManager.getInstance().getPrjByName(newn) ;
		if(oldp!=null)
		{
			out.print("project with name ["+newn+"] already existed") ;
			return ;
		}
		UAManager.getInstance().importPrjZipFile(tmpf, idn.getId(), newid, newn,newtt);
	}
	else if(pv.equals("replace"))
	{
		UAPrj oldp = UAManager.getInstance().getPrjById(idn.getId()) ;
		if(oldp==null)
		{
			out.print("project with id="+idn.getId()+" is no existed,so it cannot be replaced") ;
			return ;
		}
		//do replace
		UAManager.getInstance().importPrjZipFile(tmpf, idn.getId(), newid, null,null);
	}
	else
	{//new
		UAManager.getInstance().importPrjZipFile(tmpf, idn.getId(), newid, null,null);
	}
}
%>succ