<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "tmpfn"))
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
	if(dd==null)
	{
		out.print("no Driver found") ;
		return ;
	}
	
	DevCat devcat = dd.getDevCatByName(catname) ;
	
%>
<html>
<head>
<title>Device Definition Category importer</title>
<jsp:include page="../head.jsp"></jsp:include>
<style type="text/css">
.imp_item
{
 position:relative;
 width:90%;
 margin:10px;
 
 height:85px;
}
</style>
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
Driver [<%=dd.getTitle() %>] - Device Definition Category [<%=catname %>] will be imported
<%
if(devcat!=null)
{
%>
	Device Definition Category [<%=catname %>] <%=devcat.getTitle() %> is already existed.<br>
	Importer will replace it. 
<%
}
%>
</body>
<script type="text/javascript">
var tmpfn = "<%=tmpfn%>" ;
var catid="<%=catid%>";
var catname="<%=catname%>";
var drvname="<%=drvname%>";

function win_close()
{
	dlg.close(0);
}


function do_submit(cb)
{
	var pms = {tmpfn:tmpfn,catid:catid,catname:catname,drvname:drvname} ;
	
	send_ajax('cat_imp_ajax.jsp',pms,function(bsucc,ret)
	{
		if(!bsucc || ret.indexOf('succ')<0)
		{
			cb(false,ret) ;
			return ;
		}
		cb(true,ret);
	},false);
	//document.getElementById('form1').submit() ;
}

</script>
</html>