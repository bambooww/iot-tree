<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.pro.modbuss.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<html>
<head>
<title></title>
<jsp:include page="../../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>

<style>

</style>
</head>

<body>

<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>title</w:g>:</label>
    <div class="layui-input-inline" style="width:46%;">
      <input type="text" id="title" name="title" value=""  class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">FC:</label>
    <div class="layui-input-inline" style="width:300px;">
      <select id="fc">
<%
LinkedHashMap<Integer,String>fc2tt = SlaveDevSeg.listFCs() ;
for(Map.Entry<Integer,String> f2t:fc2tt.entrySet())
{
%><option value="<%=f2t.getKey()%>"><%=f2t.getKey()%> <%=f2t.getValue() %></option>
<%
}
%>
</select>
    </div>
   
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Idx:</label>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="reg_idx" name="reg_idx" value=""  class="layui-input">
    </div>
    <label class="layui-form-mid">Num:</label>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="reg_num" name="reg_num" value=""  class="layui-input">
    </div>
  </div>
 </form>

</body>

<script type="text/javascript">
var form ;
var seg = dlg.get_opener_opt("seg") ;
if(!seg)
	seg = {id:dlg.create_new_tmp_id(),fc:1,reg_idx:0,reg_num:10} ;

layui.use('form', function(){
	  form = layui.form;
	  update_seg();
});

function update_seg()
{
	$("#fc").val(seg.fc) ;
	$("#reg_idx").val(seg.reg_idx||0) ;
	$("#reg_num").val(seg.reg_num||10) ;
	$("#title").val(seg.title||"") ;
	//$("#title").val(dev.title) ;
	
	 form.render();
}

function get_edit_seg(cb)
{
	let n = $("#name").val() ;
	//if(!n)
	//{
	//	cb(false,"<w:g>pls,input,name</w:g>");
	//	return false;
	//}
	seg.name = n ;
	seg.fc = get_input_val("fc",1,true) ;
	seg.title = $("#title").val() ;
	seg.reg_idx = get_input_val("reg_idx",0,true) ;
	seg.reg_num = get_input_val("reg_num",10,true) ;
	cb(true,seg) ;
}

dlg.resize_to(600,500) ;

</script>

</html>                                                                                                                                                                                                                            