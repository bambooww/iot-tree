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
	String vt = request.getParameter("vt") ;
	if(Convert.isNullOrEmpty(vt))
		vt = "" ;
	
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<script type="text/javascript">
dlg.resize_to(600,400);
</script>
<body>
<form class="layui-form" action="">
  
  <div class="layui-form-item">
    <label class="layui-form-label">Types</label>
    <div class="layui-input-inline" style="width:500px">
<%
for(UAVal.ValTP vt0:UAVal.ValTP.values())
{
%><input type="radio" name="vt" value="<%=vt0.getStr() %>" title="<%=vt0.getStr()%>" />
<%
}
%>
    </div>
  </div>
  
 </form>
 
</body>
<script type="text/javascript">
var form = null;
var vt = "<%=vt%>" ;
layui.use('form', function(){
	  form = layui.form;

	  if(vt)
	  {
	  	$("input:radio[value="+vt+"]").attr('checked','true');
	  }

	  form.render(); 
});




function get_input()
{
	return $("input[type='radio']:checked").val();
}

</script>
</html>