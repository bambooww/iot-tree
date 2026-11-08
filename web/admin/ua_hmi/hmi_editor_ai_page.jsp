<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.res.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "tabid","path"))
		return ;
	//String op = request.getParameter("op");
	String tabid = request.getParameter("tabid");
	String path = request.getParameter("path");
	//String id = request.getParameter("id");
	//UARep rep = UAManager.getInstance().getRepById(repid);
	//if(rep==null)
	//{
	//	out.print("no rep found!");
	//	return;
	//}
	UAHmi uahmi = (UAHmi)UAUtil.findNodeByPath(path);//.findHmiById(id) ;
	if(uahmi==null)
	{
		out.print("no hmi node found") ;
		return;
	}
	if(uahmi.getRefBranchNode()!=null)
	{
		response.sendRedirect(path);
		return ;
	}
	
	String bkcolor = uahmi.getBkColor() ;
	if(Convert.isNullOrEmpty(bkcolor))
		bkcolor = "#1e1e1e" ; 
	UANodeOCTagsCxt node = uahmi.getBelongTo() ;
	String cxtnodeid = node.getId() ;
	UANode topn = node.getTopNode() ;
	String res_ref_id="" ;
	String reslibid = "" ;
	String resid = "" ;
	if(topn instanceof DevDef)
	{
		resid = topn.getId();
	}
	if(topn instanceof IResNode)
	{
		res_ref_id = reslibid = ((IResNode)topn).getResLibId();
		//.getResNodeUID() ;
	}
	
	
	boolean bprj = topn instanceof UAPrj ;
	UAPrj prj = null;
	UADev owner_dev = null;
	DevDef owner_def = null ;
	if(bprj)
	{
		prj = (UAPrj)topn;
		owner_dev = uahmi.getOwnerUADev() ;
		if(owner_dev!=null)
			owner_def = owner_dev.getDevDef() ; 
	}
	
	if(owner_def!=null)
	{// use UADev as top res_ref_id
		res_ref_id = prj.getResLibId() ;
		reslibid = owner_def.getResLibId() ;
		resid = owner_def.getId();
	}
	
	if(owner_dev!=null)
	{
		res_ref_id =reslibid= prj.getResLibId() ;
		//reslibid = owner_dev.getId();
		resid = owner_dev.getId();
	}
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>hmi context editor</title>
<jsp:include page="../head.jsp">
 <jsp:param value="true" name="simple"/>
</jsp:include>
<link rel="stylesheet" href="/_js/xterm/xterm.css" />
<script src="/_js/xterm/xterm.js"></script>
<style>

</style>
</head>
<body class="layout-body">
<div id="terminal"></div>
      <script>
        var term = new Terminal();
        term.open(document.getElementById('terminal'));
        term.write('Hello from \x1B[1;3;31mxterm.js\x1B[0m $ ')
      </script>
</body>
</html>