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
String sor_tp = request.getParameter("sor_tp") ;
if(Convert.isNullOrEmpty(sor_tp))
	sor_tp = "json" ;
%>
<html>
<head>
<title>Probe For JSON or XML</title>
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

</style>
<script>
dlg.resize_to(900,580);
</script>
</head>
<body>
<table class="prop_table" style="border:solid 1px" >
  <tr>
    <td style="width:50%" >
    <div id="prop_edit_path" class="prop_edit_path">Source Input&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <button class="layui-btn layui-btn-xs layui-btn-primary" title="input <%=sor_tp %> text" onclick="input_txt()"><i class="fa-solid fa-eye-dropper"></i><%=sor_tp %> </button>
<%--
         <button class="layui-btn layui-btn-xs layui-btn-primary" title="refresh" onclick="refresh_tb_list()"><i class="fa fa-refresh" aria-hidden="true"></i></button> 
         &nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="inp_search" size="10" onkeydown="do_search_ret(event)"/>
         <button class="layui-btn layui-btn-xs layui-btn-primary" onclick="search()" title="search"><i class="fa-solid fa-magnifying-glass"></i></button>
         <button class="layui-btn layui-btn-xs layui-btn-primary" onclick="search(true)" title="clear search"><i class="fa-solid fa-eraser"></i></button>
 --%>
    </div>
       <div id="left_cont" class="prop_edit_cat" style="height:420px;width:450px;overflow: auto">
    	
	</div>
    </td>
    <td style="width:50%" >
      <table style="border:0px;height:100%">

       <tr style="height:100%;border:solid 0px">
         <td style="width:5%;vertical-align:middle;"  >
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="probe_or_not(true)" title="extract data"><i class="fa-solid fa-arrow-right"></i></button><br>
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="probe_or_not_all(true)" title="extract all data"><i class="fa-solid fa-arrow-right"></i><br><i class="fa-solid fa-arrow-right"></i></button>
	     	<br><br>
	     	<%--
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="copy_map()" title="copy create tag and bind"><i class="fa fa-angles-right"></i><br><i class="fa fa-tag"></i></button><br><br>
	     	 --%>
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="probe_or_not(false)" title="unextract data"><i class="fa-solid fa-arrow-left"></i></button>
	    </td>
	    <td style="width:95%;vertical-align: top;height:100%"  >
	    <div id="prop_edit_path" class="prop_edit_path">Probed List &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <button id="btn_tag_syn" class="layui-btn layui-btn-xs layui-btn-primary" title="export" onclick="bind_export()"><i class="fa-solid fa-arrow-up"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
      <button id="" class="layui-btn layui-btn-xs layui-btn-primary" title="import" onclick="bind_import()"><i class="fa-solid fa-arrow-down"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
      <button id="" class="layui-btn layui-btn-xs layui-btn-primary" title="add tag in channel" onclick="add_tag()"><i class="fa-solid fa-plus"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
    </div>
	    <div id=""  class="prop_edit_panel" style="height:420px;width:400px">
	       <table style="width:100%;overflow: auto;left_ob.attr("jp")" >
	       	 <thead>
	       	   <tr>
	       	    <th style="width:40%">Path</th>
	       	    <th style="width:40%">Title</th>
	       	    <th style="width:20%">Type</th>
	       	    <th style="width:5px">&nbsp;</th>
	       	   </tr>
	       	 </thead>
	       	 <tbody id="tb_extracts">
			</tbody>
	       </table>
		 </div>
	    </td>
       </tr>
      </table>
    </td>
  </tr>
</table>
</body>
<script type="text/javascript">

var cur_bind_map_tr = null ;
var sor_tp = "<%=sor_tp%>" ;

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

var left_ob = null ;

function set_left_str(txt)
{
	//console.log(txt) ;
	if(!txt)
		return false;
	if("xml"==sor_tp)
	{
		var cont = new XMLFormat(txt, 4).toString();
		$("#left_cont").html(cont) ;
	}
	else
	{
		var cont = new JSONFormat(txt, 4).toString();
		$("#left_cont").html(cont) ;
	}
	
	let spobs = $("span[jp]");
	spobs.css("min-width","50px");
	spobs.css("border","1px solid");
	spobs.css("display","inline-block");
	spobs.css("cursor","pointer");
	spobs.click(function(){
		if(left_ob!=null)
			left_ob.removeClass("left_sel")
		left_ob = $(this);
		left_ob.addClass("left_sel")
	});
	return true;
}

function init()
{
	var ow = dlg.get_opener_w();
	if(!ow)
		return ;
	//console.log(ow);
	//console.log(ow.probe_ob);
	if(ow.get_probe_sor_txt)
	{
		set_left_str(ow.get_probe_sor_txt())
	}
	
	if(ow.probe_ob)
	{
		for(var ob of ow.probe_ob)
			add_item(ob)
	}
}

init();


function input_txt()
{
	event.preventDefault();
	dlg.open("../util/dlg_input_txt.jsp?multi=true&opener_txt_id=",
			{title:"Input Source Text ["+sor_tp+"]"},['Ok','Cancel'],
			[
				function(dlgw)
				{
					var jstxt = dlgw.get_input() ;
					 if(jstxt==null||jstxt=="")
					 {
						 dlg.msg("please input "+sor_tp+" text") ;
						 return ;
					 }
					 try
					 {
						 if(!set_left_str(jstxt))
					     {
							 dlg.msg("invalid "+sor_tp+" input!")
							 return;
					     }
					 }
					 catch(e)
					 {console.log(e);
						 dlg.msg("invalid "+sor_tp+" input!")
						 return;
					 }
					 dlg.close() ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
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

function probe_or_not_all(b)
{
	if(b)
	{
		let spobs = $("span[jp]");
		spobs.each(function(){
			let ob = $(this) ;
			add_item({path:ob.attr("jp"),vt:ob.attr("tp")});
		});
		
	}
	else
	{
		//$("#tb_extracts").append(tmps) ;
	}
}

function add_item(ob)
{
	var p = ob.path ;
	if(p.length>25)
		p = "..."+p.substring(p.length-25);
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
	cb(true,get_probed()) ;
}
</script>
</html>