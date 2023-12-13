<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.store.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "prjid","hid"))
	return ;
	String prjid = request.getParameter("prjid") ;
	String hid = request.getParameter("hid") ;
	String id =  request.getParameter("id") ;
UAPrj rep  = UAManager.getInstance().getPrjById(prjid) ;
if(rep==null)
{
	out.print("no prj found");
	return ;
}

StoreManager stmgr = StoreManager.getInstance(prjid) ;
if(stmgr==null)
{
	out.print("store manager not found") ;
	return ;
}
String prj_path = rep.getNodePath() ;
StoreHandler handler = stmgr.getHandlerById(hid) ;
if(handler==null)
{
	out.print("StoreHandler is not found") ;
	return ;
}

String tp = StoreOutTb.TP ;
String name = "" ;
String title = "" ;
String desc = "" ;
boolean benable = true ;
String sor_n = "" ;
String table = "" ;
String col_tag = "tag" ;
String col_updt = "up_dt" ;
String col_chgdt = "chg_dt" ;
String col_valid = "valid" ;
String col_valtp = "val_tp" ;
String col_valstr = "val_str" ;


if(Convert.isNotNullEmpty(id))
{
	StoreOutTb ao = (StoreOutTb)handler.getOutById(id) ;
	if(ao==null)
	{
		out.println("no StoreOutTb found");
		return ;
	}
	
	//tp = ao.getOutTp() ;
	name = ao.getName() ;
	title = ao.getTitle() ;
	benable = ao.isEnable() ;
	sor_n = ao.getSorName() ;
	table = ao.getTableName() ;
	col_tag = ao.getColTag() ;
	col_updt = ao.getColUpDT();
	col_chgdt = ao.getColChgDT();
	col_valid = ao.getColValid();
	col_valtp = ao.getColValTp() ;
	col_valstr = ao.getColValStr();
}
else
{
	id = "" ;
}
String chked = "" ;
if(benable)
	chked = "checked='checked'" ;
%>
<html>
<head>
<title>Store Out editor</title>
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
 <div class="layui-form-item">
    <label class="layui-form-label">Title</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="title" id="title" value="<%=title %>" class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item" >
    <label class="layui-form-label">Data Source:</label>
    <div class="layui-input-inline" style="text-align: left;color:green;width:200px;">
      <select  id="sor_n"  name="sor_n"  class="layui-input" placeholder="" lay-filter="sor_n">
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
    <div class="layui-form-mid" >Table</div>
    <div class="layui-input-inline">
        <input type="text" name="table" id="table" value="<%=table %>" class="layui-input"/>
    </div>
  </div>
  
  <div class="layui-form-item" >
    <label class="layui-form-label">DB Column:</label>
    <div class="layui-form-mid"  style="width:70px">Tag (PK)</div>
    <div class="layui-input-inline" style="text-align: left;color:green;width:100px;">
      <input type="text" name="col_tag" id="col_tag" value="<%=col_tag %>" class="layui-input"/>
    </div>
     <div class="layui-form-mid" style="width:70px">Valid</div>
    <div class="layui-input-inline" style="text-align: left;color:green;width:100px;">
      <input type="text" name="col_valid" id="col_valid" value="<%=col_valid %>" class="layui-input"/>
    </div>
    
  </div>
  
   <div class="layui-form-item" >
    <label class="layui-form-label"></label>
   <div class="layui-form-mid" style="width:70px">Update DateTime</div>
    <div class="layui-input-inline" style="width:100px">
        <input type="text" name="col_updt" id="col_updt" value="<%=col_updt %>" class="layui-input"/>
    </div>
    <div class="layui-form-mid" style="width:70px">Change DateTime</div>
    <div class="layui-input-inline" style="width:100px">
        <input type="text" name="col_chgdt" id="col_chgdt" value="<%=col_chgdt %>" class="layui-input"/>
    </div>
  </div>
  
  <div class="layui-form-item" >
    <label class="layui-form-label"></label>
     <div class="layui-form-mid" style="width:70px">Value Type</div>
    <div class="layui-input-inline" style="width:100px">
        <input type="text" name="col_valtp" id="col_valtp" value="<%=col_valtp %>" class="layui-input"/>
    </div>
    <div class="layui-form-mid"  style="width:70px">Value Str</div>
    <div class="layui-input-inline" style="text-align: left;color:green;width:100px;">
      <input type="text" name="col_valstr" id="col_valstr" value="<%=col_valstr %>" class="layui-input"/>
    </div>
  </div>
  
</form>
</body>
<script type="text/javascript">
var path="<%=prj_path%>";
var hid = "<%=hid%>" ;
var id = "<%=id%>" ;
var sor_n ="<%=sor_n%>";
var tp = "<%=tp%>";

layui.use('form', function(){
	  var form = layui.form;
	  form.on('select(_tp)', function (data) {
			　update_ui()
			});
	  
	  $("#sor_n").val(sor_n);
	  
	  form.render();
	  
	  
	});

	
function win_close()
{
	dlg.close(0);
}

function get_inp_str(id,tt,chk,cb,out)
{
	let v = $("#"+id).val() ;
	if(chk && !v)
	{
		cb(false,"please input "+tt) ;
		return false;
	}
	out[id]=v ;
	return true ;
}

function do_submit(cb)
{
	//let tp = $("#_tp").val() ;
	let n =  $('#name').val();
	if(!n)
	{
		cb(false,"No name input") ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = "";
	}
	
	var desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var ben = $("#enable").prop("checked") ;
	let sor_n = $("#sor_n").val() ;
	if(!sor_n)
	{
		cb(false,"please select Data Source,or you can create at admin main page first!");
		return;
	}
	let table = $("#table").val() ;
	if(!table)
	{
		cb(false,"please input table name") ;
		return ;
	}
	
	let ret={} ;
	
	let b1 = get_inp_str("col_tag","Coloumn of Tag",true,cb,ret) ;
	let b2 = get_inp_str("col_updt","Coloumn of Update DateTime",true,cb,ret) ;
	let b21 = get_inp_str("col_chgdt","Coloumn of Change DateTime",true,cb,ret) ;
	let b3 = get_inp_str("col_valid","Coloumn of Valid",true,cb,ret) ;
	let b4 = get_inp_str("col_valtp","Coloumn of Value Type",true,cb,ret) ;
	let b5 = get_inp_str("col_valstr","Coloumn of Value Str",true,cb,ret) ;
	
	if(!b1 || !b2 || !b21 || !b3 || !b4 || !b5)
		return ;
	let r = {id:id,tp:tp,n:n,t:tt,en:ben,sor_n:sor_n,table:table} ;
	Object.assign(r, ret);
	cb(true,r);
}


</script>
</html>