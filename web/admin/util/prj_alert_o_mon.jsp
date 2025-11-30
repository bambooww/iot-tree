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

AlertManager amgr = AlertManager.getInstancePrjN(rep.getName()) ;
String prj_path = rep.getNodePath() ;
String id = request.getParameter("id") ;
String _tp = AlertOutUI.TP ;
String title = "" ;
String desc = "" ;
boolean benable = true ;
String js_code = "" ;

if(Convert.isNotNullEmpty(id))
{
	AlertOut ao = amgr.getOutById(id) ;
	if(ao==null)
	{
		out.println("no AlertOut found");
		return ;
	}
	
	_tp = ao.getOutTp() ;
	title = ao.getTitle() ;
	benable = ao.isEnable() ;
	if(ao instanceof AlertOutJS)
	{
		js_code = ((AlertOutJS)ao).getJsCode() ;
	}
}
else
{
	id = "" ;
}
String chked = "" ;
if(benable)
	chked = "checked='checked'" ;
String ai_cn = AlertItem.class.getCanonicalName() ;
%>
<html>
<head>
<title>Alert Out editor</title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(600,400);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label">Type</label>
    <div class0="layui-input-block" class="layui-input-inline">
      <select  id="_tp"  name="_tp"  class="layui-input" placeholder="" lay-filter="_tp">
<%
for(int i = 0 ; i < AlertOut.TPS.length ; i ++)
{
	String seled = _tp.equals(AlertOut.TPS[i])?"selected":"" ;
%>       <option <%=seled %> value="<%=AlertOut.TPS[i]%>"><%=AlertOut.TP_TITLES[i] %></option>
<%
}
%>
      </select>
    </div>
    <div class="layui-form-mid">Enable</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Title</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="title" id="title" value="<%=title %>" class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item" style="display:none;" id="js_c">
    <label class="layui-form-label">OnAlertSend:</label>
    <div class="layui-input-inline" style="text-align: left;color:green;width:400px;">
      ($uid,$alert)=&gt;{
      <textarea id="js" name="js" placeholder="" class="layui-textarea" rows="6" ondblclick="on_js_edit()" title="double click to open js editor"><%=js_code %></textarea>
      }
    </div>
    <div class="layui-form-mid" style="padding: 0!important;top:15px;"> 
        <button type="button" class="layui-btn layui-btn-primary" onclick="on_js_edit()" title="open js editor">...</button>
    </div>
  </div>
     

</form>
</body>
<script type="text/javascript">
var path="<%=prj_path%>";
var id = "<%=id%>" ;
var ai_cn = "<%=ai_cn%>";

layui.use('form', function(){
	  var form = layui.form;
	  form.on('select(_tp)', function (data) {
			　update_ui()
			});
	  
	  form.render();
	  
	  
	});
function update_ui()
{
	let bjs = $("#_tp").val() == "js" ;
	$("#js_c").css("display",bjs?"":"none") ;
}

update_ui();
	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let tp = $("#_tp").val() ;
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = "";
	}
	
	var desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	let js = $("#js").val() ;
	
	var ben = $("#enable").prop("checked") ;
	cb(true,{id:id,_tp:tp,t:tt,en:ben,js:js});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}


function on_js_edit()
{
	//if(!path)
	//	return ;
	let txt = $("#js").val() ;
	dlg.open("../ua_cxt/cxt_script.jsp?dlg=true&opener_txt_id=js&path="+path,
			{title:"Edit JS",w:'600px',h:'400px',pm_objs:{$alert:ai_cn}},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#js").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

</script>
</html>