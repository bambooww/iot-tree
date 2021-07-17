<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "drv"))
	return;
	String drv = request.getParameter("drv") ;
	DevDriver ddrv = DevManager.getInstance().getDriver(drv) ;
	if(ddrv==null)
	{
		out.print("no driver found") ;
		return ;
	}
	//boolean hide_drv = "true".equals(request.getParameter("hide_drv")) ;
%><html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
}

select option
{
font-size: 12px;
}

.oc-toolbar .toolbarbtn
{
width:40px;height:40px;margin: 5px;
font-size: 13px;
background-color: #eeeeee
}

.rmenu_item:hover {
	background-color: #373737;
}

.pitem
{
	width:80px;
}
</style>
<body marginwidth="0" marginheight="0">
<table width='100%' height='99%'>
 <tr>
   <td width="70%">
     <iframe id="dev_lister" src="../dev/dev_lib_lister.jsp?hide_drv=true&drv=<%=drv%>" width="100%" height="100%" style="border:0px"></iframe>
   </td>
   <td width="30%" valign="top">
     Device Name: <input id="dev_name" type="text"/>
<%--

List<PropGroup> pgchs = ddrv.getPropGroupsForCh() ;
if(pgchs!=null)
{
%>
	<p>Prop for channel:</p>
<%
	for(PropGroup pg:pgchs)
	{
%>
 <br/> <%=pg.getTitle() %>
<%
		for(PropItem pi:pg.getPropItems())
		{
		%>
		<br/>  <%=pi.getTitle() %><input id="pgch_<%=pi.getName() %>" pi_tp="ch"  pi_name="<%=pi.getName() %>" class="pitem" />
		<%
		}
	}
}
--%>
<%
List<PropGroup> pgdevs = ddrv.getPropGroupsForDevInCh();
if(pgdevs!=null)
{
%>
	<p>Prop for device under channel:</p>
<%
	for(PropGroup pg:pgdevs)
	{
%><%=pg.getTitle() %>
<%
		for(PropItem pi:pg.getPropItems())
		{
			String defv = pi.getDefaultVal()==null?"":pi.getDefaultVal()+"" ;
			
		%>
<br/>  <%=pi.getTitle() %><input id="pgdev_<%=pi.getName() %>"  pi_tp="dev" pg_name="<%=pg.getName() %>" pi_name="<%=pi.getName() %>" value="<%=defv %>" class="pitem"/>
		<%
		}
	}
}
%>
   </td>
 </tr>
</table>
</body>
<script>

function on_devdef_selected(d)
{
	console.log(d) ;
}

function get_selected()
{
	let devname = $("#dev_name").val() ;
	if(devname==null||devname=="")
	{
		dlg.msg("please input device name") ;
		return null;
	}
	let r = $("#dev_lister")[0].contentWindow.get_selected() ;
	if(r==null)
	{
		dlg.msg("please select device");
		return null;
	}
	r.dev_name = devname ;
	r.ch_props=[];
	r.dev_props=[];
	$(".pitem").each(function(){
		var pg = $(this).attr("pg_name");
		var pn = $(this).attr("pi_name");
		var tp = $(this).attr("pi_tp");
		var pv = $(this).val() ;
		if("dev"==tp)
			r.dev_props.push({pg:pg, pn:pn,pv:pv}) ;
		//else if("ch"==tp)
		//	r.ch_props.push({pn:pn,pv:pv}) ;
	});
	return r ;
}




$("#dev_lister")[0].contentWindow.on_devdef_selected = on_devdef_selected ;

</script>
</html>