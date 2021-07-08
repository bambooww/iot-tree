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
				 org.iottree.core.util.xmldata.*"%>
<html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}

.citem
{
margin-top:15px;margin-left:30px;width:40px;height:20px;border:2px solid;
}

.contitem
{
border:0px solid;height:120px;
}
</style>
</head>
<script>
dlg.resize_to(500,450);
</script>
<body>
<div style="float:left; display:block;width:100%;border0:1px solid">
   <div style="float:left;width:19%">
     <form class="layui-form" action="">
	  <div class="layui-form-item">
	    <label >Color</label><br/>
	    <div style="">
	      <div id="fcolor_0" class="citem">&nbsp;</div>
	      <div id="fcolor_1" class="citem" style="display:none">&nbsp;</div>
	      <div id="fcolor_2" class="citem" style="display:none">&nbsp;</div>
	      <div id="fcolor_3" class="citem" style="display:none">&nbsp;</div>
	    </div>
	  </div>
	 </form>
   </div>
   <div style="float:left;width:79%;">
     <div class="layui-tab layui-tab-brief" lay-filter="fill_tp_tab">
		  <ul class="layui-tab-title">
		    <li class="layui-this" id="tp_nor" fill_tp="nor">Normal</li>
		    <li id="tp_lin" fill_tp="lin">Linear</li>
		    <li id="tp_rad" fill_tp="rad">Radial</li>
		    <li id="tp_tt"fill_tp="tt">Texture</li>
		  </ul>
		  <div class="layui-tab-content">
		    <div class="layui-tab-item layui-show contitem">内容1</div>
		    <div class="layui-tab-item contitem">
		    	Color Number:<select>
		    		<option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option>
		    	</select>
		    	Rotation:<input type="number" size="3"/>
			</div>
		    <div class="layui-tab-item contitem">内容3</div>
		    <div class="layui-tab-item contitem">内容4</div>
		  </div>
		</div> 
   </div>
</div>
<div style="float:left;position:relative;text-align:center; width:230px;height:130px;white-space: nowrap;margin-left:50px">
    Preview:<div style="width:120px;border:1px solid;height:120px;margin-left:50px">

</div>
</div>
 
</body>
<script type="text/javascript">

layui.use('element', function(){
	  //var form = layui.form;
	  var element = layui.element;
	  
	    element.on('tab(fill_tp_tab)', function()
	    {
	    	on_fill_tp_chg($(this).attr("fill_tp"));
	    });
});

var ow = dlg.get_opener_w() ;
var plugpm = ow.editor_plugcb_pm;
var fill = null ;
if(plugpm!=null)
{
	$("#name").val(plugpm.name) ;
	console.log(plugpm);
	var val = plugpm.val ;
	if(val)
	{
		fill = oc.base.Fill.parseStr(val) ;
	}
	
	if(fill==null)
		fill = oc.base.Fill.createNor("#0000ee") ;
	console.log("#tp_"+fill.tp);
	$("#tp_"+fill.tp).click();
	update_ui();

}

function init_edit(v)
{
	if(!v)
		return ;
	
}

function on_fill_tp_chg(tp)
{
	fill.tp=tp ;
	
	update_ui();
}

function update_ui()
{
	switch(fill.tp)
	{
	case "nor":
		$("#fcolor_0").css("background-color",fill.getColorFirst()) ;
		$("#fcolor_1").css("display","none") ;
		$("#fcolor_2").css("display","none") ;
		$("#fcolor_3").css("display","none") ;
		break ;
	case "lin":
	case "rad":
		var cs = fill.getColors() ;
		for(var i = 0 ; i < cs.length ; i ++)
		{
			if(i < cs.length)
			{
				$("#fcolor_"+i).css("background-color",cs[i]) ;
				$("#fcolor_"+i).css("display","") ;
			}
			else
			{
				$("#fcolor_"+i).css("display","none") ;
			}
		}
		break;
	case "tt":
		$("#fcolor_1").css("display","") ;
		$("#fcolor_2").css("display","none") ;
		$("#fcolor_3").css("display","none") ;
		break;
	}
}

function win_close()
{
	dlg.close(0);
}


function editplug_get(cb)
{
	var n = $('#name').val();
	var js = $('#js').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	return {n:n,js:js};
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>