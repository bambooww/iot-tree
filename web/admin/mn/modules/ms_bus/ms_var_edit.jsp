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
	int fc = Convert.parseToInt32(request.getParameter("fc"),-1) ;
	if(fc<=0)
	{
		out.print("no fc input") ;
		return ;
	}
	boolean bBool = false;
	switch(fc)
	{
	case 1:
	case 2:
		bBool=  true ;
	case 3:
	case 4:
	}
	
	
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
    <label class="layui-form-label"><w:g>name</w:g>:</label>
    <div class="layui-input-inline" style="width:46%;">
      <input type="text" id="name" name="name" value=""  class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Value Type:</label>
    
<%
if(bBool)
{
%><div class="layui-form-mid">bool</div>
<%
}
else
{
%>
<div class="layui-input-inline" style="width:200px;">
      <select id="valtp">
<%
for(UAVal.ValTP vtp:SlaveVar.VAL_TPS)
{
	String tpstr = vtp.getStr() ;
%><option value="<%=tpstr%>"><%=tpstr%></option>
<%
}
%>
</select>
    </div>
<%
}
%>
  </div>
  
 </form>

</body>

<script type="text/javascript">
var b_bool = <%=bBool%>;

var form ;
var vvar = dlg.get_opener_opt("vv") ;
//console.log(vvar) ;

layui.use('form', function(){
	  form = layui.form;
	  //update_seg();
	  if(vvar)
	  {
		  $("#name").val(vvar.name||"") ;
		  $("#valtp").val(vvar.valtp) ;
	  }
	  form.render();
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

function get_edit_var(cb)
{
	let n = $("#name").val() ;
	if(!n)
	{
		cb(false,"<w:g>pls,input,name</w:g>")
		return ;
	}
	let tp = $("#valtp").val() ;
	if(b_bool)
		tp="bool" ;
	cb(true,{name:n,valtp:tp}) ;
}

dlg.resize_to(400,500) ;

</script>

</html>                                                                                                                                                                                                                            