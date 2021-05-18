<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "repid","id"))
		return ;
	//String op = request.getParameter("op");
	String repid = request.getParameter("repid");
	String id = request.getParameter("id");
	UARep rep = UAManager.getInstance().getRepById(repid);
	if(rep==null)
	{
		out.print("no rep found!");
		return;
	}
	UANode n = rep.findNodeById(id) ;
	if(n==null)
	{
		out.print("no node found") ;
	}

	String node_path = n.getNodePath() ;
	if(!(n instanceof UANodeOCTags))
	{
		out.print("not node oc tags") ;
		return ;
	}
	UANodeOCTags ntags = (UANodeOCTags)n ;
	List<UAHmi> hmis = null ;
	if(n instanceof UANodeOCTagsCxt)
	{
		UANodeOCTagsCxt ntcxt = (UANodeOCTagsCxt)n ;
		hmis = ntcxt.getHmis() ;
	}
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title></title>
  <link rel="stylesheet" href="/_js/layui/css/layui.css">
</head>
<body class="layui-layout-body">
<form class="layui-form" action="">
   <div class="layui-inline" class0="layui-form-item">
              <label class="layui-form-label">node resources</label>
              
	         	<div class="layui-form-mid">-</div>
	         	
    <div class="layui-input-inline" style="width: 100px;">
	            <select name="dlp_cat" lay-verify="required" lay-filter="dlp_cat">
        <option value="">---</option>

      </select>
         </div>
   </div>
</form>
<table class="layui-table">
  <colgroup>
    <col width="150">
    <col width="200">
    <col>
  </colgroup>
  <thead>
    <tr>
      <th>name</th>
      <th>title</th>
      <th>operation</th>
    </tr> 
  </thead>
  <tbody>
   <tr>
      <td>control panel</td>
      <td>control panel system default</td>
      <td><a href="/iottree<%=node_path%>/_panel" target="_blank">view</a></td>
    </tr>
    <tr>
      <td>context</td>
      <td>context json format</td>
      <td><a href="/iottree<%=node_path%>/_json" target="_blank">view</a></td>
    </tr>
<%
if(hmis!=null)
{
	for(UAHmi hmi:hmis)
	{
%>
    <tr>
      <td><%=hmi.getName() %></td>
      <td><%=hmi.getTitle() %></td>
      <td><a href="/iottree<%=node_path%>/_hmi/<%=hmi.getName()%>" target="_blank">view</a></td>
    </tr>
<%
	}
}
%>
  </tbody>
</table>
</body>
<script>

var table ;
var table_cur_page = 1 ;

layui.use(['form','table'], function()
{
	var form = layui.form ;
  table = layui.table;

});
</script>
</html>