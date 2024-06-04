<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","netid","nodeid"))
			return ;
	String prjid = request.getParameter("prjid");
	String netid = request.getParameter("netid") ;
	String nodeid = request.getParameter("nodeid") ;
	
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
%><!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>3D Pipeline</title>
    <style>
        canvas {
            border: 1px solid black;
        }
		img
		{width:50px;height:50px;border:1px solid;
		}
    </style>
</head>
<body style="background-color:#555555">
<%
	File dir = new File(Config.getWebappBase()+"/_iottree/res/") ;
	for(File f:dir.listFiles())
	{
		if(f.isDirectory())
			continue ;
		if(!f.getName().endsWith(".svg"))
			continue ;
%><img src="/_iottree/res/<%=f.getName() %>"  title="<%=f.getName() %>"/>
<%
	}
%>

</body>
</html>                                                                                                                                                                                                                            