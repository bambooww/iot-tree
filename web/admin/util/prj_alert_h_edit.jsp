<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.alert.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;
	String prjid = request.getParameter("prjid") ;
UAPrj rep  = UAManager.getInstance().getPrjById(prjid) ;
if(rep==null)
{
	out.print("no prj found");
	return ;
}

AlertManager amgr = AlertManager.getInstance(prjid) ;
String prj_path = rep.getNodePath() ;
String id = request.getParameter("id") ;
String title = "" ;
String desc = "" ;
boolean benable = true ;
boolean b_trigger_en = true ;
boolean b_release_en = true ;
String release_color="" ;
String trigger_color="";

if(Convert.isNotNullEmpty(id))
{
	AlertHandler ah = amgr.getHandlerById(id);
	if(ah==null)
	{
		out.println("no AlertHandler found");
		return ;
	}
	
	title = ah.getTitle() ;
	benable = ah.isEnable() ;
	b_trigger_en = ah.isTriggerEn() ;
	b_release_en = ah.isReleaseEn() ;
	trigger_color = ah.getTriggerColor() ;
	release_color = ah.getReleaseColor() ;
}
else
{
	id = "" ;
}
String chked = benable?"checked='checked'":"" ;
String trigger_chked = b_trigger_en?"checked='checked'":"" ;
String release_chked = b_release_en?"checked='checked'":"" ;
%>
<html>
<head>
<title>Alert Out editor</title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(600,500);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label">Title</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="title" id="title" value="<%=title %>" class="layui-input"/>
    </div>
    <div class="layui-form-mid">Enable</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  
  <div class="layui-form-item" >
    <label class="layui-form-label">Triggered:</label>
     <div class="layui-input-inline" style="width:50px;">
      <input type="checkbox" id="trigger_en" name="trigger_en" <%=trigger_chked%> lay-skin="switch"  lay-filter="trigger_en" class="layui-input">
    </div>
    <div class="layui-form-mid">Color:</div>
    <div class="layui-input-inline" style="width:150px;">
      <input type="text" name="trigger_color" id="trigger_color" value="<%=trigger_color %>" class="layui-input"/>
    </div>
  </div>
  
  <div class="layui-form-item" >
    <label class="layui-form-label">Released:</label>
    <div class="layui-input-inline" style="width:50px;">
      <input type="checkbox" id="release_en" name="release_en" <%=release_chked%> lay-skin="switch"  lay-filter="release_en" class="layui-input">
    </div>
    <div class="layui-form-mid">Color:</div>
    <div class="layui-input-inline" style="width:150px;">
      <input type="text" name="release_color" id="release_color" value="<%=release_color %>" class="layui-input"/>
    </div>
  </div>

</form>
</body>
<script type="text/javascript">
var path="<%=prj_path%>";
var id = "<%=id%>" ;

layui.use('form', function(){
	var form = layui.form;
	form.render();
});
	

	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = "";
	}
	
	var desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var ben = $("#enable").prop("checked") ;
	var b_trigger_en = $("#trigger_en").prop("checked") ;
	var b_release_en = $("#release_en").prop("checked") ;
	let trigger_c = $("#trigger_color").val() ;
	let release_c = $("#release_color").val() ;
	cb(true,{id:id,trigger_en:b_trigger_en,release_en:b_release_en,t:tt,en:ben,trigger_c:trigger_c,release_c:release_c});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}


</script>
</html>