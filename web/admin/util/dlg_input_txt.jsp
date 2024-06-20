<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	String txt_title = request.getParameter("txt_title") ;
	if(Convert.isNullOrEmpty(txt_title))
		txt_title = "Text" ;
	String opener_txt_id = request.getParameter("opener_txt_id") ;
	if(Convert.isNullOrEmpty(opener_txt_id))
		opener_txt_id ="" ;
	
	String v = request.getParameter("v") ;
	if(Convert.isNullOrEmpty(v))
		v = "" ;
	boolean multi = "true".equalsIgnoreCase(request.getParameter("multi")) ;
	int height = 200 ;
	if(multi)
		height=400 ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<script type="text/javascript">
dlg.resize_to(650,<%=height%>);
</script>
<body>
<form class="layui-form" action="">
  
  <div class="layui-form-item">
    <label class="layui-form-label"><%=txt_title %>:</label>
    <div class="layui-input-inline" style="width:500px">
<%
if(multi)
{
%><textarea  id="txt"  name="txt"  style="height:230px;width:100%;border-color: #e6e6e6"><%=v %></textarea>
<%
}
else
{
%><input  id="txt"  name="txt" class="layui-input"  style="width:100%;border-color: #e6e6e6" value="<%=v %>" />
<%
}
%>
    </div>
  </div>
  
 </form>
 
</body>
<script type="text/javascript">
var form = null;
var opener_txt_id = "<%=opener_txt_id%>" ;

$("#txt").focus();

if(opener_txt_id)
{
	var ow = dlg.get_opener_w();
	var txtob = ow.document.getElementById(opener_txt_id) ;
	if(txtob!=null)
	{
		$("#txt").val(txtob.value) ;
	}
}
else
{
	let txt =dlg.get_opener_opt("txt") ;
	if(txt)
		$('#txt').val(txt);
}


function get_input()
{
	return $('#txt').val();
}

</script>
</html>