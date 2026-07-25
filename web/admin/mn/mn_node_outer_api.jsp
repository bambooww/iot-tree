<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,org.iottree.core.util.jt.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid","itemid"))
			return ;
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
	if(mnm==null)
	{
		out.print("no MsgNet Manager with container_id="+container_id) ;
		return ;
	}
	
	UAPrj prj = mnm.getBelongToPrj() ;
	String prjn = prj.getName() ;

	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	String net_n = net.getName() ;
	MNBase item =net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	
	String tp = item.getTPFull() ;
	String title = item.getTitle() ;
	
	LinkedHashMap<String,MNBase.OuterApi> all_apis = item.listOuterApiAll() ;
	if(all_apis==null||all_apis.size()<=0)
	{
		out.print("no Outer Api found in this node") ;
		return ;
	}
	LinkedHashMap<String,MNBase.OuterApi> use_apis = item.getUsingOuterApis() ;
%><html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style>
.save_btn
{
	position: absolute;
	right:5px;
	top:5px;
	color:#27ba7d;
	
}

.in_title {position: absolute;left:2px;top:50px;border:1px solid #ccc;background-color: #003a36;color:#00ffe2;cursor: pointer;}
.in_title:hover ~ .child  {display: block;}
.layui-form-item .layui-form-checkbox[lay-skin=primary] {
    margin-top:0px;
}
.layui-form-item {
    margin-bottom: 3px;
    margin-top: 3px;
}
</style>
</head>

<body>
<blockquote class="layui-elem-quote">
  <%=title %>
</blockquote>
<table class="layui-table">
 <thead>
 	<tr>
 		<td><w:g>name</w:g></td>
 		<td><w:g>title</w:g></td>
 		<td><w:g>desc</w:g></td>
 	</tr>
 </thead>
 <tbody>
<%
for(MNBase.OuterApi oa:all_apis.values())
{
	String ndname = item.getName() ;
	String url = "<span style='color:red'>node has no name</span>";
	String color = "red" ;
	JSONObject[] inout = item.getOuterApiIOSample(oa.getName()) ;
	if(Convert.isNotNullEmpty(ndname))
	{
		boolean bopen = use_apis.containsKey(oa.getName()) ;
		color = bopen?"green":"red";
		
		url = "/"+prjn+"/_mn_outer_api/"+net_n+"/"+ndname+"/"+oa.getName()+"</span>" ;
	}
%><tr>
 		<td><%=oa.getName() %></td>
 		<td><%=oa.getTitle() %></td>
 		<td><%=oa.getDesc() %>
<%
if(inout!=null)
{
	if(inout[0]!=null)
	{
%><br>In:<%=inout[0]%><%
	}
	if(inout[1]!=null)
	{
%><br>Out:<%=inout[1]%><%
	}
}

%>
 		</td>
 	</tr>
 	<tr>
 		<td></td>
 		<td colspan="3" style="color:<%=color%>;"><span class="pre"></span><%=url %></td>
 	</tr>
<%
}
%>
  </tbody>
</table>
 </body>
<script>
dlg.resize_to(700,500) ;
var container_id="<%=container_id%>";
var netid="<%=netid%>";
var itemid="<%=itemid%>";
let pre = location.protocol+"//"+location.host ;
$(".pre").html(pre);

</script>
</html>