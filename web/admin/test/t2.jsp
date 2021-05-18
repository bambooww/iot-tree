<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*"%>
<%
	String rid = request.getParameter("rid");
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title></title>

<script src="/_js/jquery.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>


<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>

<script src="/_js/dlg_layer.js"></script>
	<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/opencharts/dist/oc.js"></script>

<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
	
</head>
<script type="text/javascript">

layui.use('layer', function(){
	  var layer = layui.layer;
	  
	  //layer.msg('hello');
	  
	 
	  
	}); 

</script>
<body style="height:100%;">
<div id="c1" style="width:100px;height:100px;background-color: #1e1e1e"></div>
&nbsp;
<div id="c2" style="width:100px;height:100px;background-color: #1e1e1e"></div>

</body>
<script>
var c1 = document.getElementById("c1");
var p1;

function btn_load_unit()
{
	send_ajax("unit/unit_ajax.jsp","op=load&id=x20200302175530_1",function(bsucc,ret){
		if(!bsucc&&ret.indexOf("{")!=0)
		{
			dlg.msg(ret);
			return ;
		}
		
		
		var ob = null ;
		eval("ob="+ret);
		var un = new oc.DrawUnit();
		un.inject(ob);
		un.addSquareBorder();
		
		/*
		panel = new oc.DrawPanel("c1");
		if(panel.init_panel)
			panel.init_panel();
		var lay1 = new oc.DrawLayer("lay1") ;
		panel.addLayer(lay1);
		lay1.addItem(un);
		lay1.ajustDrawFit();
		*/
		
		p1 = new oc.DrawPanelDiv("c1") ;
		p1.setDrawItem(un);
		
	}) ;

}

btn_load_unit();
</script>
</html>