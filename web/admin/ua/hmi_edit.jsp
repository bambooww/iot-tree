<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
	
	%><%
	String hmipath = request.getParameter("hmi_path") ;
	String ppath = request.getParameter("ppath") ;
	boolean bedit = false;
	UANode pnode = null ;
	UAHmi hmi = null ;
	if(Convert.isNotNullEmpty(hmipath))
	{//edit
		bedit = true;
		hmi = (UAHmi)UAUtil.findNodeByPath(hmipath) ;
		if(hmi==null)
		{
			out.print("no hmi found") ;
			return ;
		}
		pnode = hmi.getParentNode() ;
	}
	else
	{
		pnode = UAUtil.findNodeByPath(ppath) ;
		if(pnode==null)
		{
			out.print("no parent node found") ;
			return ;
		}
		hmi = new UAHmi() ;
	}
	String name = hmi.getName() ;
	String title = hmi.getTitle() ;
%>
<html>
<head>
<title>HMI Add</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
<input type="hidden" id="id" value="<%=hmi.getId()%>"/>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>name</wbt:g>:</label>
    <div class="layui-input-block">
      <input type="text" id="name" name="name" lay-verify="required" value="<%=name %>" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>title</wbt:g>:</label>
    <div class="layui-input-block">
      <input type="text" id="title" name="title"  lay-verify="required" value="<%=title %>" autocomplete="off" class="layui-input">
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
dlg.set_dlg_title("<wbt:g>add</wbt:g>/<wbt:g>edit</wbt:g> HMI") ;

layui.use('form', function(){
	  var form = layui.form;
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
	var id =$('#id').val();
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = n ;
	}
	var desc ='' ;
	
	
	cb(true,{id:id,name:n,title:tt,desc:desc
		});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>