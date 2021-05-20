<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "repid","chid"))
		return ;
	String repid = request.getParameter("repid") ;
	String chid = request.getParameter("chid") ;
	UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
	if(rep==null)
	{
		out.print("no rep found") ;
		return ;
	}
	
	UACh ch = rep.getChById(chid) ;
	if(ch==null)
	{
		out.print("no ch found") ;
		return ;
	}
	
	List<DevDriver> drvs = ch.getSupportedDrivers() ;
	if(drvs==null||drvs.size()<=0)
	{
		out.print("no drivers supported") ;
		return ;
	}
%>
<html>
<head>
<title>Driver Selector in Channel</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(400,520);
</script>
</head>
<body>
<form class="layui-form" action="">
  <table id="view_colorval"  class="layui-table" lay-filter="dl_list"  lay-size="sm" lay-even="true" style="width:95%" border="1">
		   <thead style="background-color: #cccccc">
		     <tr>
		      <td></td>
			  <td>Name</td>
			  <td>Title</td>
			  <td>Description</td>
			</tr>
		  </thead>
		  <tbody id="client_list">
 <%
 	for(DevDriver drv:drvs)
 	{
 		String n = drv.getName() ;
 		String t = drv.getTitle() ;
%>
<tr onclick="sel_drv('<%=n%>')">
			<td><input id="sel_<%=n %>" type="radio" name="sel_name" value="<%=n %>" title=""></td>
			  <td><%=n %></td>
			  <td><%=t %></td>
			  <td><%=drv.getDesc() %></td>
			 
			</tr>
<%
 	}
 %>
		  </tbody>
		  
		</table>
 </form>
</body>
<script type="text/javascript">

var form = null ;
var sel_name = null ;
layui.use('form', function(){
	  form = layui.form;
	  
	  form.render() ;
});

function sel_drv(n)
{
	sel_name = n ;
	$("#sel_"+n).attr('checked','true');
	form.render();
}
	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	if(sel_name==null||sel_name=='')
	{
		cb(false,'please select driver') ;
		return ;
	}
	
	cb(true,{name:sel_name});
}

</script>
</html>                                                                                                                                                                                                                            