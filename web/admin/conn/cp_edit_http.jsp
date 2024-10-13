<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.conn.mqtt.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
ConnProHTTP cp = null ;
if(Convert.isNullOrEmpty(cpid))
{
	cp = new ConnProHTTP() ;
	cpid = cp.getId() ;
}
else
{
	cp = (ConnProHTTP)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
	if(cp==null)
	{
		out.print("no ConnProvider found") ;
		return ;
	}
}

String name = cp.getName() ;
String title= cp.getTitle() ;
String chked = "" ;
if(cp.isEnable())
	chked = "checked='checked'" ;
String desc = cp.getDesc();
String cp_tp = cp.getProviderType() ;
//MqttEndPoint mep = cp.getMqttEP() ;

%>
<html>
<head>
<title>tcp client cp editor</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off"  class="layui-input">
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
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
      
 </form>
</body>
<script type="text/javascript">
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name,#title,#desc,#local_ip,#local_port").on("input",function(e){
		  setDirty(true);
		  });
	  form.on('select(local_ip)', function(data){   
		  setDirty(true);
	 });
	  form.on('switch(enable)', function(obj){
		  setDirty(true);
		  });
	  
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cp_tp%>" ;


function isDirty()
{
	return bdirty;
}
function setDirty(b)
{
	if(!(b===false))
		b = true ;
	bdirty= b;
	dlg.btn_set_enable(1,b);
}


	
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
		cb(false,'Please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt=n;//
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	
	cb(true,{id:cp_id,name:n,title:tt,desc:desc,enable:ben});
}

function str2lns(str)
{
	var arr = str.split('\n');
	var res = [];
	arr.forEach(function (item)
	{
		var ln = item.replace(/(^\s*)|(\s*$)/g, "").replace(/\s+/g, " ")
		if(ln=='')
			return ;
	    res.push(ln);
	})

	return res ;
}
</script>
</html>