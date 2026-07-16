<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,org.iottree.core.devtree.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	List<DTDevPartLib> libs = DTDevPartManager.getInstance().listLibs() ;
	
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<style type="text/css">
.layui-form-label {    width: 120px;}

</style>
</head>
<script type="text/javascript">
dlg.resize_to(550,300);
</script>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label" ><wbt:g>parttp,lib</wbt:g>:</label>
    <div class="layui-input-inline" style="width:300px">
<%
if(libs==null||libs.size()<=0)
{
%><div style="color:red"><wbt:g>no_parttp_lib_ppt</wbt:g></div><%
}
else
{
%>
		<select id="sel_parttp_lib">
<%
	for(DTDevPartLib lib:libs)
	{
%><option value="<%=lib.getLibId() %>"><%=lib.getTitle() %></option><%
	}
%>
		</select>
<%
}
%>
    </div>
  </div>
  
  <div class="layui-form-item">
    <label class="layui-form-label" ><wbt:g>title</wbt:g></label>
    <div class="layui-input-inline" style="width:300px">
<input  id="tt"  name="tt" class="layui-input"  style="width:100%;border-color: #e6e6e6" value="" />
    </div>
  </div>
  
 </form>
 
</body>
<script type="text/javascript">
var form = null;

let tt = dlg.get_opener_opt("txt") ;
if(tt)
	$("#tt").val(tt) ;
$("#tt").focus();

layui.use(['form','table'], function(){
	  form = layui.form;
	  form.on('select(lib_ids)', function(obj){
		   refresh_table()
	   });
	  
	  form.render();
});

function get_input(cb)
{
	let t = $("#tt").val() ;
	if(!t) {cb(false,"<wbt:g>pls,input,title</wbt:g>");return}
	let libid = $("#sel_parttp_lib").val() ;
	if(!libid) {cb(false,"<wbt:g>pls,select,parttp,lib</wbt:g>");return}
	cb(true,{libid:libid,title:t}) ;
}

</script>
</html>