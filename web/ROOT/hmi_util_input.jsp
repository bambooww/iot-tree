<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="lan"%>
<%
String tp = request.getParameter("tp") ;
String strv = request.getParameter("v") ;
boolean bnum = "true".equals(request.getParameter("num")) ;
boolean bfloat = "true".equals(request.getParameter("float")) ;
double min = Convert.parseToDouble(request.getParameter("min"), 0) ;
double max = Convert.parseToDouble(request.getParameter("max"), 100) ;
Object val = null ;
if(bnum)
{
	if(bfloat)
		val = Convert.parseToDouble(strv, 0) ;
	else
		val = Convert.parseToInt64(strv, 0) ;
}
else
{
	val = strv ;
	if(val==null)
		val = "" ;
}
%>	
<html>
<head>
<title>hmi input</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/jquery-ui.css" />
<script>
dlg.resize_to(400,250);

var bnum = <%=bnum%>;
var bfloat = <%=bfloat%>
var min = <%=min%>;
var max = <%=max%> ;
var val = null ;
if(bnum)
{
	val = <%=val%>;
}
else
{
	val = '<%=val%>' ;
}
</script>
</head>
<body>

<%
if("slide".equals(tp))
{
%>
<p>
  <label for="amount"><lan:g>input,val</lan:g>:</label>
  <input type="text" id="val" style="border:0; color:#f6931f; font-weight:bold;">
</p>
<div id="slider" style="height:200px;"></div>
 <script type="text/javascript">
 $(function() {
	    $( "#slider" ).slider({
	      orientation: "horizontal",//
	      range: "min",
	      min: min,
	      max: max,
	      value: val,
	      slide: function( event, ui ) {
	        $( "#val" ).val( ui.value );
	      }
	    });
	    $( "#val" ).val( $( "#slider" ).slider( "value" ) );
	  });
 </script>
 <%
}
 %>
 
</body>
<script type="text/javascript">



function win_close()
{
	dlg.close(0);
}
function do_submit(cb)
{
	var v = 20 ;
	cb(true,{val:v});
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>