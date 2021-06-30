<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,org.iottree.core.*,org.iottree.core.util.*,
	java.io.*,
	org.iottree.core.res.*,
	org.iottree.core.comp.*,
	org.iottree.core.gr.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "res_node_id"))
	return ;
	String resnodeid = request.getParameter("res_node_id") ;
	//String compid = request.getParameter("compid") ;
	ResDir dr = ResManager.getInstance().getResDir(resnodeid) ;
	
	if(dr==null)
	{
		out.print("no ResDir input") ;
		return ;
	}

	IResNode prn = dr.getResNode().getResNodeParent() ;
	
	ResDir prd = null;
	if(prn!=null)
		prd = prn.getResDir() ;
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Res Editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
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
height:350px;
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
}

.pi_edit_unit
{
border: 0px;
width:100%;
}


.oc-toolbar .toolbarbtn
{
width:80px;height:80px;margin: 10px;
}

.resitem
{
border-width:2px; border-style:solid; background-color0: #515658;
}

.res_sel
{
	background-color: #1173ec;
	color: #eeeeee;
}

.res_node
{

}

.res_node:hover {
	background-color: #1173ec;
	color: #eeeeee;
}
</style>
</head>
<script type="text/javascript">
</script>
<body>
<table class="prop_table">
  <tr>
    <td colspan="2"><div id="prop_edit_path" class="prop_edit_path">Component Resources</div></td>
  </tr>
  <tr>
    <td style="width:20%;height:90%" valign="top" >
     <div id="rescxt_list" class="prop_edit_cat" style="overflow: hidden;">
       <ul type="square">
<%
if(prd!=null)
{
	String nid = prd.getResNodeUID();
%>
        <li node_id="<%=nid%>" class="res_node" onclick="show_res_node_id('<%=nid%>')"><%=prn.getResNodeTitle() %></li>
<%
}

String nid = dr.getResNodeUID() ;
%>
    	<li node_id="<%=nid%>"  class="res_node res_sel"  onclick="show_res_node_id('<%=nid%>')"><%=dr.getTitle() %></li>
       </ul>
    	</div>
    </td>
    <td style="width:80%;vertical-align: top;"  >
    <div id="editpanel"  class="prop_edit_panel oc-toolbar" >
       <div id="res_list" class="btns" style="width:100%">
			  
    </div>
	 </div>
	  <div id="editop"  class="prop_edit_desc">
	  	<input type="text" id="add_name" placeholder="input name" onkeyup="on_name_chged()" />
	  	<input type="file" id='add_file' onchange="add_file_onchg()" name="file" style="left:-9999px;position:absolute;"/>
			<button id="btn_add_file" onclick="add_res()">Add</button>
	  </div>
    </td>
  </tr>
</table>
</body>
<script type="text/javascript">
dlg.resize_to(800,550);

var cur_cxtid=null ;
var cur_resitems = null;
var cur_resitem = null ;

var cur_resnodeid="<%=resnodeid%>" ;

function set_res_node_sel(resnodeid)
{
	cur_resnodeid = resnodeid;
	$("li.res_node").each(function(){
		if($(this).attr("node_id")==resnodeid)
			$(this).addClass("res_sel") ;
		else
			$(this).removeClass("res_sel") ;
	}) ;
}

function refresh_cur_node()
{
	show_res_node_id(cur_resnodeid,false) ;
}

function show_res_node_id(resnodeid,binit)
{
	var ow = dlg.get_opener_w() ;
	var plugpm = null;
	if(ow!=null)
		plugpm = ow.editor_plugcb_pm;
	if(plugpm==null)
		return ;
	
	var pm={}; 
	pm.editor=plugpm.editor;
	pm.editor_id=plugpm.editor_id;
	pm.res_node_id=resnodeid;
	set_res_node_sel(resnodeid);
	$.ajax( {
		type : 'post',
		url : "rescxt_items_ajax.jsp",
		data : pm
	}).done(function(ret) {
		if(ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		var rcs = null ;
		eval("rcs="+ret) ;
		var tmps="" ;
		//console.log(rcs);
		for(var item of rcs)
		{
			var resid = item.id ;
			var n = item.name ;
			tmps+=`<div id="resdiv_`+resid+`" class="toolbarbtn resitem" style="border-width:2px; border-style:solid;" onclick="on_res_clk('`+n+`')">
			     <img id="panel_`+resid+`" src="/res.jsp?res_node_id=`+resnodeid+`&name=`+n+`" width="100%" height="100%"/>
				  <div style="height:20px;margin:12px;color:#8dcef7;bottom:0px">`+n+`</div>
			   </div>`;
		}
		cur_resitems = rcs ;
		$("#res_list").html(tmps) ;
		
		if(binit)
		{
			init_val();
		}
	
	}).fail(function(req, st, err) {
		dlg.msg(err) ;
	});
}

function add_res()
{
	var n= $("#add_name").val() ;
	if(n==null||n=="")
	{
		dlg.msg("please input name") ;
		return ;
	}
	if(!chk_name_nodiv(n))
	{
		dlg.msg("name must a-z A-z 0-9") ;
		return ;
	}
	
	add_file.click() ;
	return false;
}

function get_cur_resitem_byid(id)
{
	if(cur_resitems==null)
		return null;
	for(var ri of cur_resitems)
	{
		if(ri.resid==id)
			return ri ;
	}
	return null ;
}

function get_cur_resitem_byname(n)
{
	if(cur_resitems==null)
		return null;
	for(var ri of cur_resitems)
	{
		if(ri.name==n)
			return ri ;
	}
	return null ;
}

function refresh_list()
{
	if(cur_resitems==null)
		return ;
	for(var ri of cur_resitems)
	{
		var rdiv = $("#resdiv_"+ri.id);
		rdiv.removeClass("res_sel");
	}
	if(cur_resitem!=null)
	{
		var rdiv = $("#resdiv_"+cur_resitem.id);
		rdiv.addClass("res_sel");
	}
}

function on_res_clk(n)
{
	var ri = get_cur_resitem_byname(n) ;
	if(ri==null)
		return ;
	$("#add_name").val(ri.name) ;
	$("#btn_add_file").html("Change") ;
	cur_resitem = ri ;
	refresh_list();
}

function on_name_chged()
{
	if(cur_resitems==null)
		return ;
	var n = $("#add_name").val() ;
	for(var ri of cur_resitems)
	{
		if(n==ri.name)
		{
			$("#btn_add_file").html("Change") ;
			cur_resitem = ri ;
			refresh_list();
			return ;
		}
	}
	
	$("#btn_add_file").html("Add") ;
	cur_resitem = null ;
	refresh_list();
}

function add_file_onchg()
{
	if(cur_resnodeid==null||cur_resnodeid=="")
	{
		dlg.msg("no resnodeid") ;
		return ;
	}
	//$("#"+id).
	var fs = $("#add_file")[0].files ;
	if(fs==undefined||fs==null||fs.length<=0)
	{
		return ;
	}
	var f = fs[0];
	//console.log(f) ;
	var wurl = window.URL || window.webkitURL;
	var imgurl = wurl.createObjectURL(f);
	$("#img_add").attr('src',imgurl);
	//upload
	var fd = new FormData();
	var n= $("#add_name").val() ;
    fd.append("res_node_id",cur_resnodeid) ;
    fd.append("name",n) ;
    fd.append("file",f);
     $.ajax({"url": "rescxt_item_fileup.jsp",type: "post","processData": false,"contentType": false,
		"data": fd,
		success: function(data)
       	{
			//lj.show_loading(false) ;
 	  		if(data=="succ")
 	  		{
 	  			dlg.msg("add ok");
 	  		 refresh_cur_node();
 	  		}
 	  		else
 	  		{
 	  			lj.msg(data);
 	  		}
   　  },
      　error: function(data)
         {
    	  	//lj.show_loading(false) ;
	  				lj.msg("add failed "+data);
  　　　　}
  　　});
}



function init_val()
{
	var ow = dlg.get_opener_w() ;
	var plugpm = ow.editor_plugcb_pm;
	console.log(plugpm);
	if(plugpm==null)
		return ;
	var v = plugpm.val ;
	if(v!=null)
	{
		var ri = get_cur_resitem_byid(v) ;
		console.log(v,ri) ;
		if(ri==null)
			return ;
		cur_resitem = ri ;
		refresh_list();
	}
}

show_res_node_id(cur_resnodeid,true);


function win_close()
{
	dlg.close(0);
}


function editplug_get()
{
	if(cur_resitem==null)
		return {v:""};
	return {v:cur_resitem.name};
}

</script>
</html>