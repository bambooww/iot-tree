<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.store.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.store.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%
	String id = request.getParameter("id") ;

if(id==null)
	id="" ;
String name="" ;
String title="" ;
String chked = "checked" ;
String desc="" ;
String url="" ;
String token = "" ;
String org = "" ;
String bucket = "" ;
if(Convert.isNotNullEmpty(id))
{
	SourceInfluxDB st = (SourceInfluxDB)StoreManager.getSourceById(id);//.getSourceById(storeid) ;
	if(st==null)
	{
		out.print("no store found") ;
		return ;
	}
	name = st.getName() ;
	title = st.getTitle() ;
	if(!st.isEnable())
		chked = "" ;
	url = st.getUrl() ;
	token = st.getToken() ;
	org=  st.getOrg();
	bucket = st.getBucket() ;
	desc = st.getDesc() ;
}

if(Convert.isNullOrEmpty(url))
	url = "http://localhost:8086" ;
%>
<html>
<head>
<title>jdbc editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(700,600);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
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
    <label class="layui-form-label">URL:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <input type="text" id="url" name="url" value="<%=url%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
  </div>

  <div class="layui-form-item">
    <label class="layui-form-label">Token:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <input type="text" id="token" name="token" value="<%=token%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    
  </div>
   
   <div class="layui-form-item">
    <label class="layui-form-label">Org:</label>
    <div class="layui-input-inline" style="width: 200px;">
      <input type="text" id="org" name="org" value="<%=org%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Bucket:</div>
	  <div class="layui-input-inline" style="width: 200px;">
	    <input type="text" id="bucket" name="bucket" value="<%=bucket%>"  lay-verify="required" autocomplete="off" class="layui-input">
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
	
	let url=$('#url').val();;
	let token=$('#token').val();;
	let org=$('#org').val();;
	let bucket=$('#bucket').val();;
	
	cb(true,{id:id,_tp:"influxdb",name:n,title:tt,enable:ben,desc:desc,url:url,token:token,org:org,bucket:bucket});
}

</script>
</html>
