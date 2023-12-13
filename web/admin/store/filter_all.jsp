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
UAPrj prj  = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
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
String prj_path = prj.getNodePath() ;
StoreHandler handler = stmgr.getHandlerById(hid) ;
if(handler==null)
{
	out.print("StoreHandler is not found") ;
	return ;
}

StoreHandler.TF_All tf = (StoreHandler.TF_All)handler.getFilter() ;
String substr = tf.getSubTp() ;
%>
<html>
<head>
<title></title>
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
for(int i = 0 ; i < StoreOut.TPS.length ; i ++)
{
	String seled = tp.equals(StoreOut.TPS[i])?"selected":"" ;
%>       <option <%=seled %> value="<%=StoreOut.TPS[i]%>"><%=StoreOut.TP_TITLES[i] %></option>
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
    <label class="layui-form-label">Name</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="name" id="name" value="<%=name %>" class="layui-input"/>
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Title</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" name="title" id="title" value="<%=title %>" class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item" style="display:none;" id="js_c">
    <label class="layui-form-label">fsdd:</label>
    <div class="layui-input-inline" style="text-align: left;color:green;width:400px;">
      
    </div>
    <div class="layui-form-mid" style="padding: 0!important;top:15px;"> 
        <button type="button" class="layui-btn layui-btn-primary" onclick="on_js_edit()" title="open js editor">...</button>
    </div>
  </div>
     

</form>
</body>
<script type="text/javascript">
var path="<%=prj_path%>";
var hid = "<%=hid%>" ;
var id = "<%=id%>" ;

layui.use('form', function(){
	  var form = layui.form;
	  form.on('select(_tp)', function (data) {
			　update_ui()
			});
	  
	  form.render();
	  
	  
	});

	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let tp = $("#_tp").val() ;
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
	cb(true,{id:id,tp:tp,n:n,t:tt,en:ben});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}


</script>
</html>