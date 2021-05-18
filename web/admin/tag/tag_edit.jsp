<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%!
	public static String html_str(Object o)
	{
		if(o==null)
			return "" ;
		return ""+o ;
	}
	 %><%
	boolean bmid = "true".equalsIgnoreCase(request.getParameter("mid")) ;
	String path = request.getParameter("path") ;
	String id = request.getParameter("id") ;
	UATag tag = null ;
	String name= "" ;
	String title = "" ;
	String addr = "" ;
	UAVal.ValTP valtp = null ;
	long srate = 200;
	boolean canw = false;
	String desc = "" ;
	if(Convert.isNotNullEmpty(path) && Convert.isNotNullEmpty(id))
	{
		UANodeOCTags n = (UANodeOCTags)UAUtil.findNodeByPath(path);
		if(n==null)
		{
			out.print("no node with path="+path) ;
			return ;
		}
 		tag = n.getTagById(id) ;
 		name = tag.getName() ;
 		title = tag.getTitle() ;
 		desc = tag.getDesc() ;
 		addr = tag.getAddress() ;
 		valtp = tag.getValTp() ;
 		srate = tag.getScanRate() ;
 		canw = tag.isCanWrite();
	}
%>
<html>
<head>
<title>Tag Editor </title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(400,600);
</script>
<style type="text/css">
.layui-form-label {
    padding: 5px 15px;
}
.layui-input, .layui-select, .layui-textarea {
    height: 30px;
    line-height: 20px\9;
}
.layui-form-select dl dd, .layui-form-select dl dt {
    line-height: 30px;
}
</style>
</head>
<body>
<form class="layui-form" action="">
	<input type="hidden" id="id" name="name" value="<%=html_str(id)%>">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-block">
      <input type="text" id="name" name="name"  lay-verify="required" placeholder="Pls input name" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
    <div class="layui-input-block">
      <input type="text" id="title" name="title"  lay-verify="required" placeholder="Pls input name" autocomplete="off" class="layui-input">
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label"><%=(bmid?"Express":"Address") %>:</label>
    <div class="layui-input-block">
      <input type="text"  id="addr"  name="addr"  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <input type="text"  id="desc"  name="desc"  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
  
  <div class="layui-form-item">
    <label class="layui-form-label">Data type</label>
    <div class="layui-input-block">
      <select  id="vt"  name="vt" lay-verify="required" class="layui-input">
<%
for(UAVal.ValTP vt:UAVal.ValTP.values())
{
	 %><option value="<%=vt.getInt()%>"><%=vt.getStr() %></option><%
}
%>
      </select>
    </div>
    </div>
        <div class="layui-form-item">
    <label class="layui-form-label">Scan rate:</label>
    <div class="layui-input-block">
      <input type="text" id="srate" name="srate" required  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
  
    <div class="layui-form-item">
    <label class="layui-form-label">Client access</label>
    <div class="layui-input-block">
      <select id="canw"  name="canw" lay-verify="required" class="layui-input">
        <option value="false">Read Only</option>
        <option value="true">Read/Write</option>
      </select>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">

var bmid = <%=bmid%>;

var name= "<%=html_str(name) %>" ;
var title = "<%=html_str(title)%>" ;
var desc = "<%=html_str(desc)%>";
var addr = "<%=html_str(addr)%>" ;
var vt = "<%=html_str(valtp)%>" ;
var srate = "<%=srate%>";
var canw = "<%=canw%>"

$("#name").val(name) ;
$("#title").val(title) ;
$("#addr").val(addr) ;
$("#desc").val(desc) ;
$("#vt").val(vt) ;
$("#srate").val(srate) ;
$("#canw").val(canw) ;

layui.use('form', function(){
	  var form = layui.form;
	  
	  form.render();
});
	
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
	var id=$("#id").val() ;
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'请输入名称') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'请输入标题') ;
		return ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var canw = get_input_val("canw",null)=="true";
	cb(true,{id:id,name:n,title:tt,desc:desc,mid:bmid,
		addr:get_input_val("addr",""),
		vt:get_input_val("vt",""),
		srate:get_input_val("srate",100,true),
		canw:canw
		});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>