<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.node.*,
				org.iottree.core.router.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
	
	String prjid = request.getParameter("prjid") ;
	String id = request.getParameter("id") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}

RouterManager rmgr = RouterManager.getInstance(prj) ;
RouterInnCollator ric = null ;
if(Convert.isNotNullEmpty(id))
{
	ric = rmgr.getInnerCollatorById(id) ;
	if(ric==null)
	{
		out.print("no RouterInnCollator found with id="+id) ;
		return ;
	}
}
else
{
	ric = new RICRunTime(rmgr) ;
}

boolean benable = ric.isEnable();
String chk_en = "" ;
if(benable)
	chk_en = "checked" ;

String name =ric.getName() ;
String title = ric.getTitle() ;
String desc = ric.getDesc() ;
long out_intv = ric.getOutIntervalMS() ;

JSONObject js_ob = ric.toJO() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(700,500);
</script>
<style>
.layui-form-label
{
	width:120px;
}
.conf
{
	position: relative;
	width:90%;
	height:30px;
	border:1px solid ;
}

.conf .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:7px;
}
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>name</w:g>:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off"  class="layui-input" <%=Convert.isNotNullEmpty(name)?"readonly":"" %>>
    </div>
    <div class="layui-form-mid"><w:g>title</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><w:g>enable</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="en" name="en" <%=chk_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><w:g>out,interval</w:g>:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="number" id="out_intv" name="out_intv" value="<%=out_intv%>"  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid">(MS)</div>
	  <div class="layui-input-inline" style="width: 150px;">
	  </div>
	  
 </div>
 
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>desc</w:g></label>
    <div class="layui-input-inline" style="width:350px;">
      <textarea  style="width:100%;height:50px;" id="desc" class="layui-input" ><%=desc %></textarea>
    </div>
  </div>
</form>
</body>
<script type="text/javascript">

var prjid="<%=prjid%>";
var prj_path = "<%=prj.getNodePath()%>" ;


layui.use('form', function(){
	  var form = layui.form;
	  
	  
	  form.render();
	});

	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function do_submit(cb)
{
	let n = $("#name").val() ;
	if(!n)
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	
	var ben = $("#en").prop("checked") ;
	let t =  $("#title").val() ;
	if(!t) t = "" ;
	let d = $("#desc").val() ;
	let out_intv = get_input_val("out_intv",30000,true) ;
	let pm={n:n,t:t,d:d,out_intv:out_intv,en:ben} ;
	cb(true,pm) ;
}

</script>
</html>