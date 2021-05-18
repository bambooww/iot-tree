<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%

%>
<html>
<head>
<title>Category Add</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(500,600);
</script>
</head>
<body>
<div id="show_comp_panel" style="width:100%;height:300px;background-color: #333333">
	
</div>
<div id="ctrl_panel"  style="width:100%;height:200px;">
</div>
</body>
<script type="text/javascript">

function panel_init()
{
	
}

var panel = null ;
var loadLayer = null ;

var hmiView=null;
var hmiComp = null ;
var hmiCompIns = null ;

function init_panel()
{
	
	panel = new oc.hmi.HMICompPanel("show_comp_panel",{
		
	});
	
	var editlay = dlg.get_opener_w().loadLayer;

	hmiView = new oc.hmi.HMICompViewShow(editlay,panel);
	//hmiView.init();
	
	hmiComp = hmiView.getComp() ;
	hmiCompIns = hmiView.getCompIns() ;
	
	init_comp_ctrlitems();
	
}


function init_comp_ctrlitems()
{
	var inter = hmiComp.getCompInter();
	var tmps = "" ;
	for(var ci of inter.getInterProps())
	{
		tmps += "<div>"+ci.t+"["+ci.n+"]"+"<input type='text' id='ci_"+ci.n+"'/><button onclick='ci_input(\""+ci.n+"\")'>set</buttom></div>"
	}
	$("#ctrl_panel").html(tmps) ;
}

init_panel();

function ci_input(n)
{
	var v = $("#ci_"+n).val() ;
	hmiCompIns.setInterPropVal(n,v) ;
}

layui.use('form', function(){
	  var form = layui.form;
});
	
function win_close()
{
	dlg.close(0);
}


</script>
</html>