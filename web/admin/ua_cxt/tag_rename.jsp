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
	 if(!Convert.checkReqEmpty(request, out, "id","path"))
			return;
	String path = request.getParameter("path") ;
	String id = request.getParameter("id") ;
	UATag tag = null ;
	String name= "" ;
	String title = "" ;
	String desc = "" ;
	UANodeOCTags n = (UANodeOCTags)UAUtil.findNodeByPath(path);
	if(n==null)
	{
		out.print("no node with path="+path) ;
		return ;
	}
	if(!n.isRefedNode())
	{
		out.print("only refed node can be rename") ;
		return ;
	}
	tag = n.getTagById(id) ;
	name = tag.getName() ;
	title = tag.getTitle() ;
	desc = tag.getDesc() ;
		
%>
<html>
<head>
<title>Tag Rename Editor </title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(600,500);
</script>

</head>
<body>
<form class="layui-form" action="">
	  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  
  </div>

    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <input type="text"  id="desc"  name="desc"  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
  
 </form>
</body>
<script type="text/javascript">


var name= "<%=html_str(name) %>" ;
var title = "<%=html_str(title)%>" ;
var desc = "<%=html_str(desc)%>";



layui.use('form', function(){
	  var form = layui.form;
	  $("#name").val(name) ;
	  $("#title").val(title) ;
	  
	  $("#desc").val(desc) ;
	 
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
		cb(false,'please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'please input title') ;
		return ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	cb(true,{id:id,name:n,title:tt,desc:desc});
}

</script>
</html>