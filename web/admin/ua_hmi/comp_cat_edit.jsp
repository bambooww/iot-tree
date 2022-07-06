<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.comp.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
if(!Convert.checkReqEmpty(request, out, "libid"))
	return ;
	String libid = request.getParameter("libid") ;
	String catid = request.getParameter("catid") ;
	if(catid==null)
		catid = "" ;
	
	CompLib lib = CompManager.getInstance().getCompLibById(libid) ;
	if(lib==null)
	{
		out.print("no lib found") ;
		return ;
	}
	
	String name = "" ;
	String title = "" ;
	
	CompCat cat = null ;
	if(Convert.isNotNullEmpty(catid))
	{
		cat = lib.getCatById(catid) ;
		if(cat==null)
		{
			out.print("no cat found") ;
			return ;
		}
		
		//name = cat.getName() ;
		title = cat.getTitle() ;
	}
	
%>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(400,220);
</script>
</head>
<body>
<form class="layui-form" action="">
<%--
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-block">
      <input type="text" id="name" name="title" value="<%=name %>"  class="layui-input">
    </div>
  </div>
   --%>
  <div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
    <div class="layui-input-block">
      <input type="text" id="title" name="title" value="<%=title %>"  autocomplete="off" class="layui-input">
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">

var libid = "<%=libid%>" ;
var catid = "<%=catid%>" ;
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
	/*
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	*/
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'please input title') ;
		return ;
	}
	cb(true,{libid:libid,catid:catid,title:tt});
}

</script>
</html>                                                                                                                                                                                                                            