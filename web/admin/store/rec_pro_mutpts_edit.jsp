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
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
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
RecProL1MutPts.MutTP muttp = RecProL1MutPts.MutTP.up_to_vertex ;
RecProL1MutPts.SlopeTP slopetp = RecProL1MutPts.SlopeTP.fixed ;
StoreManager storem = StoreManager.getInstance(prjid) ;
RecManager recm = RecManager.getInstance(prjid) ;
RecProL1MutPts proDV = null ;
if(Convert.isNotNullEmpty(id))
{
	RecPro pro = recm.getRecProById(id) ;
	if(pro==null || !(pro instanceof RecProL1MutPts))
	{
		out.print("no RecProL1MutPts found") ;
		return ;
	}
	proDV = (RecProL1MutPts)pro ;
	name = proDV.getName() ;
	title = proDV.getTitle() ;
	if(!proDV.isEnable())
		chked = "" ;
	sor_name = proDV.getSorName() ;
	muttp = proDV.getMutTP() ;
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
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
 </div>
  <div class="layui-form-item">
  <label class="layui-form-label"><w:g>data,sor</w:g>:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <select id="sor_name"  lay-filter="sor_name" >
	    	<option value=""> <w:g>inner_s</w:g> </option>
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
    
    <div class="layui-form-mid"></div>
	  <div class="layui-input-inline" style="width: 150px;">
	    
	  </div>
	 </div>
	 
	 <div class="layui-form-item">
  <label class="layui-form-label"><w:g>mut_tp</w:g>:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <select id="muttp"  lay-filter="muttp" >
<%

	for(RecProL1MutPts.MutTP w:RecProL1MutPts.MutTP.values())
	{
		
		int v = w.getVal() ;
		String t = w.getTitle() ;
%><option value="<%=v %>" ><%=t %></option>
<%
	}
%>
	    </select>
    </div>
    
    <div class="layui-form-mid"><w:g>slope_tp</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="slopetp"  lay-filter="slopetp" >
<%
for(RecProL1MutPts.SlopeTP w:RecProL1MutPts.SlopeTP.values())
{
	
	int v = w.getVal() ;
	String t = w.getTitle() ;
%><option value="<%=v %>" ><%=t %></option>
<%
}
%>
	    </select>
	  </div>
	 </div>
	 
      <div class="layui-form-item">
    <label class="layui-form-label"><w:g>desc</w:g>:</label>
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
	  $("#muttp").val("<%=muttp.getVal()%>") ;
	  $("#slopetp").val("<%=slopetp.getVal()%>") ;
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
		cb(false,'<w:g>pls,input,name</w:g>') ;
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
	let muttp = get_input_val('muttp',0,true);
	let slopetp = get_input_val('slopetp',0,true);
	
	cb(true,{id:id,tp:tp,n:n,t:tt,en:ben,desc:desc,sor:sor_name,
		muttp:muttp,slopetp:slopetp});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>