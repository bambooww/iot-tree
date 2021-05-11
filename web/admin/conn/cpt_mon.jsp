<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%><%!
final int LN_LEN = 120 ;
%><%
if(!Convert.checkReqEmpty(request, out,"repid", "cpid","connid"))
	return;
String repid = request.getParameter("repid") ;
String cpid = request.getParameter("cpid") ;
String connid = request.getParameter("connid") ;

ConnProvider cp = ConnManager.getInstance().getConnProviderById(repid, cpid) ;
if(cp==null)
{
	out.print("no ConnProvider found") ;
	return ;
}

ConnPt cpt = cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no ConnPt found") ;
	return ;
}
	
List<ConnPt.MonItem> monitems = cpt.getMonitorList() ;
%>
<html>
<head>
<title>通道数据跟踪</title>
<script type="text/javascript" src="/system/ui/dlg.js"></script>
<script type="text/javascript" src="/system/ui/ajax.js"></script>
</head>
<body>
<div>
<span style="color:blue">链接跟踪：<%=cp.getTitle() %>-<%=cpt.getTitle() %></span>
<input type='button' value="stop" onclick="stop_refresh()"/>
</div>
<table width="98%" border="1">
<%
	//int cc = 0 ;
	int s = monitems.size() -1;
	for(int i = s ; i >=0 ; i --)
	{
		ConnPt.MonItem mi = monitems.get(i) ;
		if(mi==null)
			continue ;
		String st = Convert.toFullYMDHMS(new Date(mi.getStartDT())) ;
		
		String dir = "→" ;
		String bcolor="pink";
		if(!mi.isInput())
		{
			dir = "←";
			bcolor = "yellow" ;
		}
		int mdlen = mi.getMonDataLen() ;
		byte[] md = mi.getMonData() ;
		String hexstr = Convert.byteArray2HexStr(md,0,mdlen," ") ;
		String str = new String(md,0,mdlen) ;
%>
	<tr style="width:95%;border: 1;font-size: 10">
		<td  width="20%" valign="top"  style="overflow: auto;">
		<span style="background-color: <%=bcolor %>;">[<%=(i+1) %>] <%=dir %>  <%=st %> 数据长度<%=mdlen %></span><br>
		
		</td>
		<td width="80%">
		
		<%=hexstr %> | <%=str %>
		</td>
		
	</tr>
<%
	} //end of for
%>
</table>
</body>
<script>
var brefresh = true ;

function stop_refresh()
{
	brefresh = false ;
}

function refresh_me()
{
	if(!brefresh)
		return ;
		
	document.location.href = document.location.href;
}

setTimeout("refresh_me()",3000) ;
</script>
</html>