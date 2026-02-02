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
		//boolean bind_tag_only = "true".equalsIgnoreCase(request.getParameter("bind_tag_only")) ;
%>
<html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(500,500);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Item:</label>
    <div  class="layui-input-block">
      <input type="text" id="binded_id" name="binded_id" readonly="readonly" class="layui-input" >
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Map Interface</label>
	  <div class="layui-input-inline" style="width: 70px;">
	  <input type="checkbox" id="enable" name="enable" checked=checked lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  
   
 </form>
</body>
<script type="text/javascript">

var form = null ;

var bind_val = "false";

layui.use('form', function(){
	  form = layui.form;
	  
	  form.on('radio(bexp)', function (data) {
		　
		　　var value = data.value;
			sel_exp_or_not(value);
		});
	  
	  form.render() ;
});

var ow = dlg.get_opener_w() ;
var plugpm = ow.editor_plugcb_pm;
if(plugpm!=null)
{
	var di = plugpm.di ;
	var pdf = di.findPropDefItemByName(plugpm.name) ;
	$("#binded_id").val(pdf.title+"["+plugpm.name+"] ") ;
	$("#name").val(plugpm.name) ;
	var vv = plugpm.val ;
	if(vv)
	{
		document.getElementById("exp_"+vv.bExp).checked = true ;
		sel_exp_or_not(vv.bExp?"true":"false") ;
		if(vv.bExp)
			$("#js").val(vv.binderTxt) ;
		else
			$("#tag").val(vv.binderTxt) ;
	}
	form.render() ;
}

function sel_tag()
{
	if(plugpm==null)
		return ;
	//var cxtnodeid = plugpm.cxtnodeid ;
	var tmpv = $("#tag").val() ;
	dlg.open("../ua_cxt/di_cxt_tag_selector.jsp?path="+plugpm.path+"&val="+tmpv+"&bind_tag_only="+bind_tag_only,
			{title:"Select Tag in Context",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					var ret = dlgw.get_val() ;
					if(ret==null)
						return ;
					$("#tag").val(ret) ;
					 dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function sel_exp_or_not(v)
{
	bind_val = v ;
	if(v=="true")
	{
		$("#divtjs").css("display","") ;
		$("#divtag").css("display","none") ;
	}
	else if(v=="false")
	{
		$("#divtjs").css("display","none") ;
		$("#divtag").css("display","") ;
	}
	else if(v=="unbind")
	{
		$("#divtjs").css("display","none") ;
		$("#divtag").css("display","none") ;
	}
}

	
function win_close()
{
	dlg.close(0);
}

</script>
</html>