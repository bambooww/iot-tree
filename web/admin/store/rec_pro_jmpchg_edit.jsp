<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.store.*,
				org.iottree.core.store.record.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.store.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request,  out, "prjid"))
	return ;

String prjid = request.getParameter("prjid") ;
String id = request.getParameter("id") ;

if(id==null)
	id="" ;
String tp = RecProL1DValue.TP;
String name="" ;
String title="" ;
String chked = "checked" ;
String sor_name="" ;
String desc="" ;
RecProL1JmpChg.JmpTP jmptp = RecProL1JmpChg.JmpTP.off_to_on ;
StoreManager storem = StoreManager.getInstance(prjid) ;
RecManager recm = RecManager.getInstance(prjid) ;
RecProL1JmpChg proDV = null ;
if(Convert.isNotNullEmpty(id))
{
	RecPro pro = recm.getRecProById(id) ;
	if(pro==null || !(pro instanceof RecProL1JmpChg))
	{
		out.print("no RecProL1DValue found") ;
		return ;
	}
	proDV = (RecProL1JmpChg)pro ;
	name = proDV.getName() ;
	title = proDV.getTitle() ;
	if(!proDV.isEnable())
		chked = "" ;
	sor_name = proDV.getSorName() ;
	jmptp = proDV.getJmpTP();
	desc = "";//st.getDesc() ;
}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(700,600);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off"  class="layui-input" <%=Convert.isNotNullEmpty(name)?"readonly":"" %>>
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
 </div>
  <div class="layui-form-item">
  <label class="layui-form-label">Jump Type:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <select id="jmptp"  lay-filter="jmptp" >
<%

	for(RecProL1JmpChg.JmpTP w:RecProL1JmpChg.JmpTP.values())
	{
		
		int v = w.getVal() ;
		String t = w.getTitle() ;
%><option value="<%=v %>" ><%=t %></option>
<%
	}
%>
	    </select>
    </div>
    
    <div class="layui-form-mid">Data Source:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="sor_name"  lay-filter="sor_name" >
	    	<option value=""> Inner </option>
<%
List<Source> sors = storem.listSources() ;
	for(Source sor:sors)
	{
		if(!(sor instanceof SourceJDBC))
			continue ;
		
		String sorn = sor.getName() ;
		String sort = sor.getTitle() ;
%><option value="<%=sorn %>" ><%=sort %></option>
<%
	}
%>
	    </select>
	  </div>
	 </div>
      <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block" style="width: 450px;">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var id = "<%=id%>" ;
var tp = "<%=tp%>" ;

layui.use('form', function(){
	  var form = layui.form;
	  form.on("select(sor_name)",function(obj){
		  //let dbport = $("#db_port").val() ;
		  if(!id)
		  {
			  let pdef = $("#drv_name").find("option:selected").attr("jdbc_port_def");
			  $("#db_port").val(pdef) ;
			  form.render();
		  }
		  
		  update_ui();
	  });
	  
	  $("#sor_name").val("<%=sor_name%>") ;
	  $("#jmptp").val("<%=jmptp.getVal()%>") ;
	  form.render();
});

function update_ui()
{
	
}

update_ui();
	
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
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = n;
	}
	
	var desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;

	var ben = $("#enable").prop("checked") ;
	
	let sor_name = $('#sor_name').val();
	if(!sor_name)
	{
		//cb(false,"Data source cannot be null") ;
		//return ;
		sor_name="" ;
	}
	let jmptp = get_input_val('jmptp',0,true);

	cb(true,{id:id,tp:tp,n:n,t:tt,en:ben,desc:desc,sor:sor_name,
		jmptp:jmptp});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>