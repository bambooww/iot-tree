<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.basic.*,
				 org.iottree.core.util.xmldata.*"%><%
	String reqcolor = request.getParameter("color") ;
    if(reqcolor==null)
    	reqcolor="" ;
%>
<html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src='/_js/tinycolor/tinycolor-min.js'></script>
<script src="/_js/tinycolor/colorpicker.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/tinycolor/style.css" />
<script>
dlg.resize_to(320,550);
</script>
</head>
<body>
<table style="height:450px;width:100%;border:solid 1px">
 <tr height="400px">
   <td width="250px"><div id="colorp" class="colorpicker" style="width:240px;height:400px"></div></td>
   <td width="10px"><div id="slide_alpha" ></div></td>
   <td width="30px">&nbsp;</td>
 </tr>
 <tr height="25px">
   <td colspan="3">color:<input type="text" id="input_color" value="" size="12"/>
 alpha:<input type="text" id="input_alpha" value="" size="3"/></td>
 </tr>
  <tr height="25px">
   <td colspan="3" align="center">rgba:<input type="text" id="rgba_color" value="" size="20"/>
 </tr>
</table>
</body>
<script type="text/javascript">
var req_color="<%=reqcolor%>" ;
$(document).ready(function() {
    
});
	
var input_v="" ;
var cur_color = "" ;
var cur_alpha = 1 ;
var ret_val="" ;
var ow = dlg.get_opener_w() ;
var plugpm = null;
if(ow)
	plugpm = ow.editor_plugcb_pm;
if(plugpm!=null)
{
	var v = plugpm.val ;
	if(v==null||v=='')
		v = '#ffffff';
	input_v=  v;
}
else
{
	input_v = req_color;
}

if(input_v!=null&&input_v!="")
{
	var tc = tinycolor(input_v)
	cur_color = input_v = tc.toHex() ;
	cur_alpha = tc.toRgb().a;
	$("#input_alpha").val(cur_alpha) ;
	$('#colorp').minimalColorpicker({
        color: input_v,
        onUpdateColor: function(e, color) {
            cur_color = color.hex;
            show_val();
        }
    });
}

layui.use('slider', function(){
	  var slider = layui.slider;
	  slider.render({
	    elem: '#slide_alpha',
	    type:"vertical",
	    height:300,
	    min:0,
	    max:100,
	    setTips: function(value){
	        return ""+value/100;
	      },
	    step:1,
	    value:cur_alpha*100,
	    change: function(value){
	    	cur_alpha = value;
	    	show_val();
	      }
	  });
	});

function show_val()
{
	$("#input_color").val(cur_color) ;
	$("#input_alpha").val(cur_alpha) ;
	var rgba = tinycolor(cur_color).toRgb() ;
	rgba.a = cur_alpha ;
	
	$("#rgba_color").val("rgba("+rgba.r+","+rgba.g+","+rgba.b+","+rgba.a+")") ;
	if(rgba.a==1)
		ret_val = "#"+cur_color ;
	else
		ret_val = $("#rgba_color").val() ;
}

layui.use('form', function(){
	  var form = layui.form;
});
	
function win_close()
{
	dlg.close(0);
}


function editplug_get()
{
	return {v:ret_val};
}

</script>
</html>