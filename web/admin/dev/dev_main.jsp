<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
boolean bedit = "true".equalsIgnoreCase(request.getParameter("edit")) ;
String tbh = "100%";
if(!bedit)
	tbh = "90%";

String libid = request.getParameter("libid") ;
if(libid==null)
	libid ="" ;

boolean bdlg = "true".equals(request.getParameter("dlg")) ;
boolean bsel_libcat = "true".equals(request.getParameter("sel_libcat")) ;
boolean bsel_dev = "true".equals(request.getParameter("sel_dev")) ;

String sel_libid = request.getParameter("sel_libid") ;
String sel_catid = request.getParameter("sel_catid") ;
String sel_devid = request.getParameter("sel_devid") ;
if(sel_libid==null)
	sel_libid="";
if(sel_catid==null)
	sel_catid="";
if(sel_devid==null)
	sel_devid="" ;
DevLib sel_lib = null ;
DevCat sel_cat = null ;
String sel_lib_tt = "" ;
String sel_cat_tt = "" ;
DevDef sel_dev = null ;

String sel_dev_tt="" ;
if(!bedit)
{
	if(Convert.isNotNullEmpty(sel_libid))
	{
		libid = sel_libid;
		sel_lib = DevManager.getInstance().getDevLibById(sel_libid) ;
		if(sel_lib!=null)
			sel_lib_tt = sel_lib.getTitle();
	}
	if(Convert.isNotNullEmpty(sel_catid))
	{
		sel_cat = sel_lib.getDevCatById(sel_catid) ;
		if(sel_cat!=null)
		{
			sel_cat_tt = sel_cat.getTitle();
			if(Convert.isNotNullEmpty(sel_devid))
			{
				sel_dev = sel_cat.getDevDefById(sel_devid) ;
				if(sel_dev!=null)
					sel_dev_tt = sel_dev.getTitle() ;
			}
		}
	}
}

if(bdlg)
	tbh = "400px";
%>
<html>
<head>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>
table{border:0px solid skyblue;}
</style>
<script type="text/javascript">
var bdlg = <%=bdlg%>
if(bdlg)
	dlg.resize_to(800,600) ;
</script>
<body>
<%
if(!bedit)
{
%><blockquote class="layui-elem-quote " id="selected_info">&nbsp;
 Selected Category:<span id="selected_libcat_tt" style="color:red"><%=sel_lib_tt %> - <%=sel_cat_tt %></span> 
<%
if(bsel_dev)
{
%>
 Selected Device:<span id="selected_dev_tt" style="color:red"><%=sel_dev_tt %></span>
<%
}
%>
 </blockquote>
<%
}

if(bedit || bsel_dev)
{
%>
<table style="width:100%;height:<%=tbh %>;border:0px;">
	<tr >
		<td style="width:45%;height:100%"><iframe name="dev_left" src="dev_left.jsp?edit=<%=bedit %>&libid=<%=libid %>&catid=<%=sel_catid %>" style="width:100%;height:100%;border:0"></iframe></td>
		<td style="width:55%;height:100%"><iframe name="dev_right" src="" style="width:100%;height:100%;border:0"></iframe></td>
	</tr>
</table>
<%
}
else
{
%>
<table style="width:100%;height:<%=tbh %>;border:0px;">
	<tr >
		<td style="width:100%;height:100%"><iframe name="dev_left" src="dev_left.jsp?edit=<%=bedit %>&libid=<%=libid %>" style="width:100%;height:100%;border:0"></iframe></td>
	</tr>
</table>
<%
}
%>
<%--
<frameset rows="*" cols="45%,*" id="frame1">
    <frame name="dev_left" src="dev_left.jsp?edit=<%=bedit %>" frameborder="0">
    <frame name="dev_right" src="" frameborder="0">
</frameset>
 --%>
</body>
<script type="text/javascript">
var sel_libid = "<%=sel_libid%>" ;
var sel_catid = "<%=sel_catid%>" ;
var sel_devid = null ;
var sel_libcat_tt = "<%=sel_lib_tt %> - <%=sel_cat_tt %>" ;
var sel_dev_tt = "" ;
var sel_dev_n = "" ;

var bsel_libcat = <%=bsel_libcat%>;
var bsel_dev = <%=bsel_dev%>;

function on_selected_libcat(libid,catid,tt)
{
	sel_libid = libid ;
	sel_catid = catid ;
	sel_libcat_tt = tt; 
	$("#selected_libcat_tt").html(tt) ;
}

function on_selected_dev(devid,n,tt)
{
	sel_devid = devid ;
	sel_dev_n = n ;
	sel_dev_tt = tt ;
	$("#selected_dev_tt").html(tt) ;
}

function check_selected_ok()
{
	if(bsel_libcat || bsel_dev)
	{
		if(!sel_libid || !sel_catid)
			return false;
	}
	
	if(bsel_dev)
	{
		if(!sel_devid)
			return false;
	}
	
	return true ;
}

function get_selected()
{
	return {libid:sel_libid,catid:sel_catid,devid:sel_devid,libcat_tt:sel_libcat_tt,dev_n:sel_dev_n,dev_tt:sel_dev_tt};
}
</script>
</html>