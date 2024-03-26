<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.json.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.store.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;
	String prjid = request.getParameter("prjid") ;
UAPrj prj  = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found");
	return ;
}

StoreManager amgr = StoreManager.getInstance(prjid) ;
String prj_path = prj.getNodePath() ;
String id = request.getParameter("id") ;
if(id==null)
	id=  "" ;
String name = "" ;
String title = "" ;
String desc = "" ;
boolean benable = true ;
boolean filter_all=true ;
String filter_prefix="" ;
boolean sel_all = false;
long scan_intv = 60000 ;
HashSet<String> sel_tagids = null ;
JSONArray sel_tag_jarr = new JSONArray() ;

if(Convert.isNotNullEmpty(id))
{
	StoreHandlerRT ah = (StoreHandlerRT)amgr.getHandlerById(id);
	if(ah==null)
	{
		out.println("no StoreHandler found");
		return ;
	}
	
	name = ah.getName() ;
	title = ah.getTitle() ;
	scan_intv = ah.getScanIntV() ;
	benable = ah.isEnable() ;
	filter_all = ah.isFilterAll() ;
	filter_prefix = ah.getFilterPrefixStr();
	sel_all = ah.isSelectAll() ;
	sel_tagids = ah.getSelectTagIds() ;
	if(sel_tagids!=null)
		sel_tag_jarr.put(sel_tagids);
}

String chked = benable?"checked='checked'":"" ;
//String trigger_chked = b_trigger_en?"checked='checked'":"" ;
//String release_chked = b_release_en?"checked='checked'":"" ;
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
    <label class="layui-form-label"><w:g>name</w:g></label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="name" id="name" value="<%=name %>" class="layui-input"/>
    </div>
    <div class="layui-form-mid"><w:g>enable</w:g></div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item" >
  <label class="layui-form-label"><w:g>title</w:g></label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="title" id="title" value="<%=title %>" class="layui-input"/>
    </div>
   <div class="layui-form-mid"><w:g>scan,intv</w:g></div>
	  <div class="layui-input-inline" style="width: 100px;">
	    <input type="number" id="scan_intv" name="scan_intv" value="<%=scan_intv%>" class="layui-input" title="<w:g>ms</w:g>"/>
	  </div>
  </div>
	<div class="layui-form-item" >
	  <label class="layui-form-label"><w:g>filter</w:g></label>
	    <div class="layui-input-inline" style="width:150px;">
	      <select id="filter_all"  lay-filter="filter_all" >
	    	<option value="true"><w:g>user_all_tags</w:g></option>
	    	<option value="false"><w:g>filter_by_prefix</w:g></option>
	    </select>
	    </div>
	    <label class="layui-form-mid"><w:g>selector</w:g></label>
	     <div class="layui-input-inline" style="width:150px;">
	      <select id="sel_all"  lay-filter="sel_all" >
	      	<option value="false"><w:g>chk_the_box</w:g></option>
	    	<option value="true"><w:g>sel_all</w:g></option>
	    </select>
	    </div>
	  </div>
    <div class="layui-form-item" id="filter_prefix_c" style="display:none">
	  <label class="layui-form-label"><w:g>filter_p</w:g></label>
	    <div class="layui-input-inline" style="width:300px;">
	      <textarea id="filter_prefixs"  style="width:300px;height:100px;"><%=filter_prefix %></textarea>
	    </div>
	  </div>
	 
</form>
</body>
<script type="text/javascript">
var path="<%=prj_path%>";
var id = "<%=id%>" ;
var filter_all = <%=filter_all%>;
var sel_all = <%=sel_all%>;
var sel_tagids = <%=sel_tag_jarr%>;


var form = null;
layui.use('form', function(){
	form = layui.form;
	$("#filter_all").val(""+filter_all);
	$("#sel_all").val(""+sel_all);
	
	form.on("select(filter_all)",function(){
		update_ui();
	}) ;
	
	update_ui();
	form.render();
});
	
function update_ui()
{
	let v = $("#filter_all").val() ;
	if(v=='false')
		$("#filter_prefix_c").css("display","") ;
	else
		$("#filter_prefix_c").css("display","none") ;
	form.render();
}
	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let n =  $('#name').val();
	if(!n)
	{
		cb(false,"<w:g>pls,input,name</w:g>") ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = "";
	}
	
	let f_all = $("#filter_all").val() !='false';
	let f_prefix = $("#filter_prefixs").val() ;
	let s_all = $("#sel_all").val() == 'true';
	
	var desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	let ben = $("#enable").prop("checked") ;
	let scan_intv = parseInt($("#scan_intv").val()) ;
	if(isNaN(scan_intv))
	{
		cb(false,"<w:g>scan,intv,invalid</w:g>");
		return;
	}
	
	cb(true,{_tp:"rt",id:id,n:n,t:tt,en:ben,filter_all:f_all,filter_prefixs:f_prefix,sel_all:s_all,scan_intv:scan_intv});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}


</script>
</html>