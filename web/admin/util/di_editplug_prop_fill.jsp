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
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="oc"/>
</jsp:include>
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

.citem a
{
	color:red;
}

.contitem
{
border:0px solid;height:120px;
}
</style>
</head>
<script>
dlg.resize_to(500,500);
</script>
<body>
<div style="float:left; display:block;width:100%;border0:1px solid">
   <div style="float:left;width:19%">

	  <div class="layui-form-item">
	    <label >Color</label><br/>
	    <div style="">
	      <div id="fcolor_0" class="citem" onclick="sel_color(0)">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a onclick="sel_color_txt(0)"><i class="fa fa-pencil"></i></a></div>
	      <div id="fcolor_1" class="citem" onclick="sel_color(1)" style="display:none">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a onclick="sel_color_txt(1)"><i class="fa fa-pencil"></i></a></div>
	      <div id="fcolor_2" class="citem" onclick="sel_color(2)" style="display:none">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a onclick="sel_color_txt(2)"><i class="fa fa-pencil"></i></a></div>
	      <div id="fcolor_3" class="citem" onclick="sel_color(3)" style="display:none">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a onclick="sel_color_txt(3)"><i class="fa fa-pencil"></i></a></div>
	    </div>
	  </div>

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
		    <div class="layui-tab-item layui-show contitem">
		    	
		    </div>
		    <div class="layui-tab-item contitem">
		    	Color Number:<select id="sel_colornum">
		    		<option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option>
		    	</select><br><br>
		    	<div style="white-space: nowrap;width:300px">
		    	<span style="float:left;">Rotation:</span><div id="rotation_sld" style="float:left;margin:10px;width:200px"></div><input type="text" size="10" id="rotation" value="0" readonly="readonly" style="float:left;width:25px"/>
		    	</div>
			</div>
		    <div class="layui-tab-item contitem">
		      Color Number:<select id="sel_colornum2">
		    		<option value="1">1</option><option value="2">2</option><option value="3">3</option><option value="4">4</option>
		    	</select>
		    </div>
		    <div class="layui-tab-item contitem">内容4</div>
		  </div>
		</div> 
   </div>
</div>
<div style="float:left;position:relative;text-align:center; width:230px;height:130px;white-space: nowrap;margin-left:50px">
    Preview:<div id="div_preview" style="width:120px;border:1px solid;height:120px;margin-left:50px">

</div>
</div>
 
</body>
<script type="text/javascript">


var ow = dlg.get_opener_w() ;
var plugpm = ow.editor_plugcb_pm;
var fill = null ;
var canvas = null ;
var cxt = null ;

var rotition_sld=null;

layui.use(['element','slider'], function(){
	  //var form = layui.form;
	  var element = layui.element;
	  var slider = layui.slider;
	  
	  rotition_sld = slider.render({
		    elem: '#rotation_sld',
		    max:360,min:0,value:0,step:1,
		    change: function(v){
		        $("#rotation").val(v) ;
		        fill.rotate=v;
		        update_preview();
		    }
		});
	  
	    element.on('tab(fill_tp_tab)', function()
	    {
	    	on_fill_tp_chg($(this).attr("fill_tp"));
	    });
	    
	    init_edit();
});



function init_edit()
{
	canvas = document.createElement('canvas');
	cxt = canvas.getContext('2d');
	$("#div_preview")[0].appendChild(canvas);
	
	$("#name").val(plugpm.name) ;
	//console.log(plugpm);
	var val = plugpm.val ;
	if(val)
	{
		fill = oc.base.Fill.parseStr(val) ;
	}
	
	if(fill==null)
		fill = oc.base.Fill.createNor("#0000ee") ;
	//console.log("#tp_"+fill.tp);
	$("#tp_"+fill.tp).click();
	$("#sel_colornum").on('change',function(){
		var colorn = parseInt($(this).val());
		fill.setColorNum(colorn);
		update_preview();
	});
	$("#sel_colornum2").on('change',function(){
		var colorn = parseInt($(this).val());
		fill.setColorNum(colorn);
		update_preview();
	});
	update_ui();
}

function on_fill_tp_chg(tp)
{
	fill.tp=tp ;
	
	update_ui();
}

function update_ui()
{
	update_ui_item();
	update_preview();
	
}

function update_preview()
{
	update_ui_tp();
	fill.previewToElement($("#div_preview")[0],cxt)
	fire_chg();
}

function update_ui_tp()
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
		var n = fill.getColorNum() ;
		var cs = fill.getColors() ;
		for(var i = 0 ; i < cs.length ; i ++)
		{
			if(i < n)
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

function update_ui_item()
{
	rotition_sld.setValue(fill.rotate);
	$("#sel_colornum").val(fill.getColorNum());
	$("#sel_colornum2").val(fill.getColorNum());
}

function sel_color(n)
{
	
	var c = fill.colors[n] ;
	//console.log(fill.colors,n,c);
	dlg.open("./di_editplug_prop_color.jsp?color="+escape(c),
			{title:"Edit Color",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					var ret = dlgw.editplug_get() ;
					 fill.colors[n]=ret.v;
					 dlg.close();
					 update_preview();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function sel_color_txt(n)
{
	event.preventDefault();
	event.stopPropagation();
	var c = fill.colors[n] ;
	//console.log(fill.colors,n,c);
	dlg.open("./dlg_input_txt.jsp?v="+escape(c),
			{title:"Edit Color",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					var ret = dlgw.get_input() ;
					 fill.colors[n]=ret;
					 dlg.close();
					 update_preview();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function win_close()
{
	dlg.close(0);
}

function fire_chg()
{
	dlg.fire_val_chg(fill.toStr()) ;
}


function editplug_get(cb)
{
	return {v:fill.toStr(),fill:fill};
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>