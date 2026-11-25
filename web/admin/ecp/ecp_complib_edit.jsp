<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,org.iottree.core.ecp.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	String libid = request.getParameter("libid") ;
	String title = "" ;
	String desc="" ;
	ECPCompLib lib = null;
	if(Convert.isNotNullEmpty(libid))
	{
		lib = ECPCompManager.getInstance().getLibById(libid) ;
		if(lib==null)
		{
			out.print("no lib found") ;
			return ;
		}
		title = lib.getLibTitle() ;
		desc= lib.getLibDesc() ;
	}
	else
	{
		libid = "" ;
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(450,320);
</script>
</head>
<body>
<form class="layui-form" action="">
  
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang>:</label>
    <div class="layui-input-inline" style="width:300px">
      <input type="text" id="title" name="title" value="<%=title %>"  autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>desc</wbt:lang>:</label>
    <div class="layui-input-inline" style="width:300px">
      <textarea id="desc" name="desc"  autocomplete="off" class="layui-input" style="height:120px;"><%=desc %></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var libid= "<%=libid%>";

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
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'<wbt:g>pls,input,title</wbt:g>') ;
		return ;
	}
	let desc = $("#desc").val() ;
	cb(true,{libid:libid,title:tt,desc:desc});
}

</script>
</html>                                                                                                                                                                                                                            