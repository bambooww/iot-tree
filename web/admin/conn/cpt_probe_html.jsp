<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*,
				org.eclipse.milo.opcua.sdk.client.nodes.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"
	%><%!
	static final int map_show_len = 25 ;
	%><%
String sor_tp = "json" ;
%>
<html>
<head>
<title>Probe For HTML</title>
<jsp:include page="../head.jsp"></jsp:include>
<script type="text/javascript" src="/_js/bignumber.min.js"></script>
<script type="text/javascript" src="/_js/jquery.json.js"></script>
<script type="text/javascript" src="/_js/jquery.xml.js"></script>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}

table, th, td
{
border:1px solid;
}
th
{
	font-size: 12px;
	font-weight: bold;
}
td
{font-size: 12px;
}

.pi_edit_table tr:hover {
	background-color: #979797;
}

.prop_table
{
width:99%;
border: 0px;
margin: 0 auto;
}

.prop_table tr>td
{
	border: 0;
	height:100%
}

.prop_table tr>div
{
	border: 0;

}

.prop_edit_cat
{
border: 1px solid #cccccc;
height:400px;
padding: 3px;
margin: 2px;
overflow: auto; 
}

.prop_edit_panel
{
border: 1px solid #cccccc;
height:200px;
padding: 0px;
margin: 2px;
overflow: auto;
}

.prop_edit_path
{
font-weight:bold;
border: 1px solid #cccccc;
background-color:#f0f0f0;
padding: 3px;
margin: 2px;
overflow: hidden;
}

.prop_edit_desc
{
border: 1px solid #cccccc;
background-color:#f0f0f0;
height:48px;
padding-left:3px;
padding-right:3px;
padding-bottom: 0px;
padding-top: 0px;
margin-left: 2px;
margin-right: 2px;
margin-top: 0px;
margin-bottom: 0px;
overflow: hidden;
}

.site-dir li {
    line-height: 26px;
    margin-left: 20px;
    overflow: visible;
    list-style-type: square;
}
li {
    list-style: none;
}

.site-dir li a {
    display: block;
    color: #333;
    cursor:pointer;
    text-decoration: none;
}


.site-dir li a.layui-this {
    color: #01AAED;
}

.pi_edit_table
{
width:100%;
border: 0px solid #b4b4b4;
margin: 0 auto;
}


.pi_edit_table tr>td
{
	border: 1px solid #b4b4b4;
	height:100%;
	
	
}

.pi_edit_table .td_left
{
	padding-left: 20px;
}

.pi_edit_table tr>div
{
	border: 0;

}

.pi_sel
{
background-color: #0078d7;
color:#ffffff;
}

.pi_edit_unit
{
border: 0px;
width:100%;
}

.left_sel
{
 background-color: #1e90ff;
}

.bl_item
{
	
	border:1px solid;
	border-color: #499ef3;
	margin-bottom:5px;
	white-space: nowrap;
	display:inline-block;
	width:99%;
	padding:5px;
}

.bl_item span
{
	
	border:1px solid;
	border-color: #2f2f2f;
	
	white-space: nowrap;
	display:inline-block;
	margin-left:3px;
}

</style>
<script>
dlg.resize_to(500,580);
</script>
</head>
<body>
<table class="prop_table" style="border:solid 1px" >
  <tr>
    <td style0="width:55%" >
    <div id="prop_edit_path" class="prop_edit_path">HTML Trace and Extract&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    
	
	<button class="layui-btn layui-btn-xs layui-btn-primary" title="Add Block Locator" onclick="edit_block()"><i class="fa-solid fa-plus"></i>Add Block Locator</button>
	
    </div>
       <div id="left_cont" class="prop_edit_cat" style="height:420px;width:500px;">
    	 <div class="bl_item">
    	 	Name:b1 Title:block1
    	 	<button class="layui-btn layui-btn-xs layui-btn-primary" onclick="edit_block(‘b1’)" style="float:right;right:5px;top:5px;position:relative;"><i class="fa-solid fa-pencil"></i>Edit</button>
    	 	<div class="trace_pts">Trace Points <span></span></div>
    	 	<div class="extract_pts">Extract Points <span></span></div>
    	 </div>
	</div>
    </td>
    
  </tr>
</table>
</body>
<script type="text/javascript">

var cur_bind_map_tr = null ;
var sor_tp = "html" ;
var ow = dlg.get_opener_w();
var prjid = ow.prjid;
var cpid = ow.cpid ;
var connid = ow.connid ;
var probe_url = ow.get_probe_url() ;
var probe_enc = null ; //

function get_prjid()
{
	return prjid;
}

function get_cpid()
{
	return cpid;
}

function get_connid()
{
	return connid ;
}

function get_probe_url()
{
	return probe_url;
}

var probe_ob = ow.probe_ob;

if(!probe_ob)
{
	probe_ob=[];
}

function get_probe_block(n)
{
	for(var ob of probe_ob)
	{
		if(n==ob.n)
			return ob ;
	}
	return null ;
}

function set_probe_block(bk)
{
	var s = probe_ob.length ;
	for(var i = 0 ; i < s ; i ++)
	{
		if(probe_ob[i].n==bk.n)
		{
			probe_ob[i] = bk ;
			return ;
		}
	}
	probe_ob.push(bk) ;
}

function refresh_block_list()
{
	var tmps = "" ;
	
	for(var bk of probe_ob)
	{
		tmps += `<div class="bl_item">
	 	Name:`+bk.n+` Title:`+bk.t+
	 	`<button class="layui-btn layui-btn-xs layui-btn-primary" onclick="edit_block('`+bk.n+`')" style="float:right;right:5px;top:5px;position:relative;"><i class="fa-solid fa-pencil"></i>Edit</button>`;
	 	
	 	tmps += `<div class="trace_pts">Trace Points `;
	 	if(bk.trace_pts)
	 	{
	 		for(var tpt of bk.trace_pts)
	 		{
	 			tmps += "<span>"+tpt.txt+"</span>";
	 		}
	 	}
	 	tmps +=`</div>
	 	<div class="extract_pts">Extract Points `;
	 	
	 	if(bk.extract_pts)
	 	{
	 		for(var ept of bk.extract_pts)
	 		{
	 			tmps += "<span>"+ept.t+"&nbsp;"+ept.n+"</span>";
	 		}
	 	}
	 	
	 	tmps +=`</div>
	 </div>`;
	}
	$("#left_cont").html(tmps) ;
}


refresh_block_list();

var b_ctrl_down = false;
var b_shift_down=false;
$(document).keydown(function(e){
 if(e.keyCode==17)
	 b_ctrl_down=true;
 else if(e.keyCode==16)
	 b_shift_down = true ;
 });
$(document).keyup(function(e){
 if(e.keyCode==17)
	 b_ctrl_down=false;
 else if(e.keyCode==16)
	 b_shift_down = false ;
 });


var uid_cc = 0 ;
function new_id()
{
	uid_cc ++ ;
	return "i"+uid_cc ;
}

var cur_block=null ;

function edit_block(n)
{
	if(!n)
	{
		cur_block = null ;
	}
	else
	{
		cur_block = get_probe_block(n)
	}
	
	var u = "./cpt_probe_html_block.jsp" ;
	dlg.open(u,{title:"Edit Html Block Locator"},['Ok','Cancel'],
			[
				function(dlgw)
				{
					var bk = dlgw.get_block() ;
					if(!bk)
						return ;
					set_probe_block(bk);
					refresh_block_list();
					dlg.close() ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

var left_ob = null ;

function set_left_str(txt)
{
	if(!txt)
		return false;

	$("span[jp]").click(function(){
		if(left_ob!=null)
			left_ob.removeClass("left_sel")
		left_ob = $(this);
		left_ob.addClass("left_sel")
	});
	return true;
}



function probe_or_not(b)
{
	if(b)
	{
		if(left_ob==null)
		{
			dlg.msg("please select left json value");
			return ;
		}
		add_item({path:left_ob.attr("jp"),vt:left_ob.attr("tp")});
	}
	else
	{
		//$("#tb_extracts").append(tmps) ;
	}
}

function add_item(ob)
{
	var p = ob.path ;
	if(p.length>30)
		p = "..."+p.substring(p.length-30);
	var tmps = "<tr><td title='"+ob.path+"'>"+p+"</td><td onclick='set_tt(this)'>"+(ob.tt||"")+"<i class='fa fa-pencil'></i></td><td onclick='set_vt(this)'>"+ob.vt+"<i class='fa fa-pencil'></i></td><td onclick='del_item(this)'><i class='fa-solid fa-xmark'></i></td></tr>";
	$("#tb_extracts").append(tmps) ;
}

function del_item(ob)
{
	$(ob).parent().remove() ;
}

function set_vt(td)
{
	event.preventDefault();
	var oldvt = $(td).text() ;
	dlg.open("../util/dlg_val_vt_sel.jsp?vt="+oldvt,
			{title:"Set Value Type"},['Ok','Cancel'],
			[
				function(dlgw)
				{
					var vt = dlgw.get_input() ;
					$(td).html(vt) ;
					 dlg.close() ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function set_tt(td)
{
	event.preventDefault();
	var oldvt = $(td).text() ;
	dlg.open("../util/dlg_input_txt.jsp?v="+oldvt,
			{title:"Set Value Type"},['Ok','Cancel'],
			[
				function(dlgw)
				{
					var vt = dlgw.get_input() ;
					$(td).html(vt) ;
					 dlg.close() ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function get_probed()
{
	var ret = [] ;
	$("#tb_extracts tr").each(function(){
		var path = $(this).children("td").eq(0).attr('title') ;
		var tt = $(this).children("td").eq(1).text() ;
		var tp = $(this).children("td").eq(2).text() ;
		ret.push({path:path,tt:tt,vt:tp}) ;
	});
	return ret;
}

function do_ok(cb)
{
	cb(true,probe_ob,probe_enc);//get_probed()) ;
}
</script>
</html>