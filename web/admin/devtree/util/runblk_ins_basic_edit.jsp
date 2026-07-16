<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,org.iottree.core.devtree.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(550,350);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>name</wbt:g>:</label>
    <div class="layui-input-inline"  style="width:150px;">
      <input type="text" id="ins_name" name="ins_name" value=""  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>title</wbt:g></div>
    <div class="layui-input-inline"  style="width:200px;">
      <input type="text" id="ins_title" name="ins_title" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>

  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>enable</wbt:g>:</label>
    <div class="layui-input-inline"  style="width:30px;">
      <input type="checkbox" id="en" name="en" checked lay-skin="primary" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>mode</wbt:g></div>
    <div class="layui-input-inline"  style="width:130px;">
      <select id="m" name="m" >
<%
	for(DTRunBlkIns.Mode m:DTRunBlkIns.Mode.values())
	{
		int mv = m.getInt();
%><option value="<%=mv%>"><%=m.getTitle() %></option>
<%
	}
%>
      </select>
    </div>
    <div class="layui-form-mid"><wbt:g>min,intv</wbt:g>:</div>
    <div class="layui-input-inline"  style="width:70px;">
    	<input type="number" id="min_intv" name="min_intv" value=""  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">ms</div>
  </div>
  <div class="layui-form-item" id="cont_desc">
    <label class="layui-form-label"><wbt:g>desc</wbt:g>:</label>
    <div class="layui-input-inline"  style="width:300px;">
      <input type="text" id="desc" name="desc" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var input = dlg.get_opener_opt("input") ;
var b_title_need=true;
if(input)
{
	$("#ins_name").val(input.ins_name).attr("readonly","readonly");
	$("#ins_title").val(input.ins_title||"");
	$("#ins_desc").val(input.ins_desc||"");
	$("#m").val(input.m||0) ;
	$("#min_intv").val(input.min_intv||"") ;
	$("#en").prop("checked",input.en) ;
}

var form ;
layui.use('form', function(){
	  form = layui.form;
	  form.render() ;
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
	let n = $('#ins_name').val();
	if(!n)
	{
		cb(false,"<wbt:g>pls,input,name</wbt:g>") ;return ;
	}
	let tt = $('#ins_title').val();
	if(!tt && b_title_need)
	{
		cb(false,"<wbt:g>pls,input,title</wbt:g>") ;return ;
	}
	let desc = $('#desc').val();

	let ret = {ins_name:n,ins_title:tt,ins_desc:desc} ;
	ret.en = $("#en").prop("checked") ;
	ret.mode = get_input_val("mode",0,true) ;
	ret.min_intv = get_input_val("min_intv",-1,true) ;
	cb(true,ret);
}

</script>
</html>                                                                                                                                                                                                                            