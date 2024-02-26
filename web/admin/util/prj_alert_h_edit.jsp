<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.alert.*,
				org.iottree.core.store.*,
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
String name="" ;
String title = "" ;
String desc = "" ;
boolean benable = true ;
boolean b_trigger_en = true ;
boolean b_release_en = true ;
String release_color="" ;
String trigger_color="";
int lvl = 0 ;

boolean b_inner_record = true;
int inner_record_days = 100 ;
boolean b_outer_record = false;
String outer_record_sor = "" ;
int outer_record_days = 100 ;


if(Convert.isNotNullEmpty(id))
{
	AlertHandler ah = amgr.getHandlerById(id);
	if(ah==null)
	{
		out.println("no AlertHandler found");
		return ;
	}
	
	name = ah.getName() ;
	title = ah.getTitle() ;
	benable = ah.isEnable() ;
	b_trigger_en = ah.isTriggerEn() ;
	b_release_en = ah.isReleaseEn() ;
	trigger_color = ah.getTriggerColor() ;
	release_color = ah.getReleaseColor() ;
	lvl = ah.getLevel() ;
	b_inner_record = ah.isInnerRecord() ;
	inner_record_days = ah.getInnerRecordDays() ;
	b_outer_record = ah.isOuterRecord() ;
	outer_record_sor = ah.getOuterRecordSor() ;
	outer_record_days = ah.getOuterRecordDays() ;
}
else
{
	id = "" ;
}
String chked = benable?"checked='checked'":"" ;
String trigger_chked = b_trigger_en?"checked='checked'":"" ;
String release_chked = b_release_en?"checked='checked'":"" ;
String b_inner_record_chked =  b_inner_record?"checked='checked'":"" ;
String b_outer_record_chked =  b_outer_record?"checked='checked'":"" ;
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
    <label class="layui-form-label">Name</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="name" id="name" value="<%=name %>" class="layui-input"/>
    </div>
    <div class="layui-form-mid">Enable</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item" >
  <label class="layui-form-label">Title</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="title" id="title" value="<%=title %>" class="layui-input"/>
    </div>
    <label class="layui-form-mid">Level</label>
     <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="lvl" name="lvl"  lay-filter="lvl" class="layui-input"  value="<%=lvl%>"/>
    </div>
    
  </div>
  
  <div class="layui-form-item" >
    <label class="layui-form-label">Trigger Color:</label>
    <div class="layui-input-inline" style="width:150px;">
      <input type="text" name="trigger_color" id="trigger_color" value="<%=trigger_color %>" class="layui-input"/>
    </div>
    <div class="layui-form-mid">Release Color:</div>
    <div class="layui-input-inline" style="width:150px;">
      <input type="text" name="release_color" id="release_color" value="<%=release_color %>" class="layui-input"/>
    </div>
  </div>
  
   <div class="layui-form-item">
    <label class="layui-form-label">Record</label>
    <div class="layui-form-mid">Inner:</div>
	  <div class="layui-input-inline" style="width: 50px;">
	    <input type="checkbox" id="b_inner_record" name="b_inner_record" <%=b_inner_record_chked%> lay-skin="switch"  lay-filter="b_inner_record" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Keep Days:</div>
    <div class="layui-input-inline" style="width:80px;">
      <input type="text" name="inner_record_days" id="inner_record_days" value="<%=inner_record_days %>" class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid">Outer:</div>
	  <div class="layui-input-inline" style="width: 50px;">
	    <input type="checkbox" id="b_outer_record" name="b_outer_record" <%=b_outer_record_chked%> lay-skin="switch"  lay-filter="b_outer_record" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Source:</div>
    <div class="layui-input-inline" style="width:120px;">
      <select  id="outer_record_sor"  name="outer_record_sor"  class="layui-input" placeholder="" lay-filter="outer_record_sor">
      <option value="" >---</option>
<%
for(Source sor:StoreManager.listSources())
{
	String n = sor.getName() ;
	//String seled = tp.equals(StoreOut.TPS[i])?"selected":"" ;
%><option value="<%=n%>"><%=sor.getSorTpTitle() %>-<%=sor.getTitle() %></option>
<%
}
%>
      </select>
    </div>
	  <div class="layui-form-mid">Keep Days:</div>
    <div class="layui-input-inline" style="width:80px;">
      <input type="text" name="outer_record_days" id="outer_record_days" value="<%=outer_record_days %>" class="layui-input"/>
    </div>
    
  </div>

</form>
</body>
<script type="text/javascript">
var path="<%=prj_path%>";
var id = "<%=id%>" ;

var outer_record_sor = "<%=outer_record_sor%>" ;
layui.use('form', function(){
	var form = layui.form;
	$("#outer_record_sor").val(outer_record_sor) ;
	form.render();
});
	

	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let n =  $('#name').val();
	if(!n)
	{
		cb(false,"No name input") ;
		return ;
	}
	let tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = "";
	}
	
	let desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	let ben = $("#enable").prop("checked") ;
	let b_trigger_en = true;//$("#trigger_en").prop("checked") ;
	let b_release_en = true;//$("#release_en").prop("checked") ;
	let trigger_c = $("#trigger_color").val() ;
	let release_c = $("#release_color").val() ;
	let lvl = parseInt($("#lvl").val()) ;
	
	let b_inner_record = $("#b_inner_record").prop("checked") ;
	let inner_record_days = parseInt($("#inner_record_days").val()) ;
	let b_outer_record = $("#b_outer_record").prop("checked") ;
	let outer_record_days = parseInt($("#outer_record_days").val()) ;
	let outer_record_sor = $("#outer_record_sor").val() ;
	cb(true,{id:id,n:n,trigger_en:b_trigger_en,release_en:b_release_en,t:tt,en:ben,trigger_c:trigger_c,release_c:release_c,lvl:lvl
		,b_inner_record:b_inner_record,inner_record_days:inner_record_days,b_outer_record:b_outer_record,outer_record_days:outer_record_days,outer_record_sor:outer_record_sor});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}


</script>
</html>