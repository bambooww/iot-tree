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

if(!Convert.checkReqEmpty(request, out, "prjid","cpid"))
	return;
String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
//String cptp = request.getParameter("cptp") ;//ConnProOPCUA.TP;//
ConnProvider cp = ConnManager.getInstance().getConnProviderById(prjid, cpid);
boolean no_ajax = "true".equalsIgnoreCase(request.getParameter("no_ajax")) ;
/*
if(cp==null)
{
	String cpid = request.getParameter("cpid") ;
	if(Convert.isNotNullEmpty(cpid))
	{
		cp =  ConnManager.getInstance().getConnProviderById(prjid, cpid);
	}
}
*/
if(cp==null)
{
	out.print("no ConnProvider found");
	return ;
}

String cptp =cp.getProviderType() ;
//String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtBinder cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	out.print("no connid input") ;
	return;
}

cpt = (ConnPtBinder)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

boolean support_tree = cpt.supportBindBeSelectTree() ;

List<String> bindids = cpt.getBindList() ;
if(bindids==null)
	bindids =new ArrayList<>(0) ;
/*
List<UaNode> nodes = cpt.opcBrowseNodes();
if(nodes==null)
{
	nodes = new ArrayList<>(0);
}
*/

UACh joined_ch = cpt.getJoinedCh() ;
List<UATag> ch_tags = null ;
if(joined_ch!=null)
	ch_tags = joined_ch.listTagsNorAll();
if(ch_tags==null)
	ch_tags = new ArrayList<>(0);
%>
<html>
<head>
<title>ConnPt Bind Selector</title>
<jsp:include page="../head.jsp"></jsp:include>
<script src="/_js/jstree/jstree.min.js"></script>
<link rel="stylesheet" href="/_js/jstree/themes/default/style.min.css" />
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

.map_sel
{
 background-color: #1e90ff;
}

</style>
<script>
dlg.resize_to(800,620);
</script>
</head>
<body>
<table class="prop_table" style="border:solid 1px" >
  <tr>
    <td style="width:45%" >
    <div id="prop_edit_path" class="prop_edit_path">[<%=cptp %>]&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    
    
<%--
 <button id="btn_tree_list" class="layui-btn layui-btn-xs layui-btn-primary" title="set tree or list" onclick="set_tree_list()"><i class="fa-solid fa-folder-tree"></i></button>
        <button class="layui-btn layui-btn-xs layui-btn-primary" title="set bind parameters" onclick="set_bind_pm()"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
         --%>
         <button class="layui-btn layui-btn-xs layui-btn-primary" title="refresh" onclick="refresh_tb_list()"><i class="fa fa-refresh" aria-hidden="true"></i></button> 
         &nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="inp_search" size="10" onkeydown="do_search_ret(event)"/>
         <button class="layui-btn layui-btn-xs layui-btn-primary" onclick="search()" title="search"><i class="fa-solid fa-magnifying-glass"></i></button>
         <button class="layui-btn layui-btn-xs layui-btn-primary" onclick="search(true)" title="clear search"><i class="fa-solid fa-eraser"></i></button>
<%
if(support_tree)
{
%>
         <button id="btn_tree_list" class="layui-btn layui-btn-xs layui-btn-primary" title="set tree or list" onclick="set_tree_or_tb()"><i class="fa-solid fa-folder-tree"></i></button>
<%
}
%>
    </div>
       <div id="list_table" class="prop_edit_cat" style="height:420px;width:400px">
    	<table style="width:100%;border:0px" class='besel'>
    		<thead>
    			<tr style="background-color: #f0f0f0">
    				<td width="50%">Path</td>
    				<td width="30%">Title</td>
    				<td width="10%">Type</td>
    				<td width="20%">Value</td>
    			</tr>
    		</thead>
    		<tbody id="bind_tb_body" style="height0:390px">
    			
    		</tbody>
		</table>
	</div>
<%
if(support_tree)
{
%>
	<div id="list_tree" class="prop_edit_cat" style="height:420px;display:none;width:400px">
	</div>
<%
}
%>
    </td>
    <td style="width:55%" >
      <table style="border:0px;height:100%">
      <%--
       <tr style="height:30%;border:solid 0px">
	       <td style="width:5%;vertical-align:middle;"  >
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="copy_or_not(true)"><i class="fa-solid fa-arrow-right"></i></button><br><br>
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="copy_or_not(false)"><i class="fa-solid fa-arrow-left"></i></button>
	    </td>
	    <td style="width:95%;vertical-align: top;height:50%"  >
	    <div id="prop_edit_path" class="prop_edit_path">Bind Copy&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     <button id="btn_tag_syn" class="layui-btn layui-btn-xs layui-btn-primary" title="create tag groups and tags" onclick="syn_tag_ch()"><i class="fa fa-angles-right"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
    </div>
	    <div id=""  class="prop_edit_panel"  style="height:170px"> 
			<select multiple="multiple" id="bind_selected" style="width:100%;height:100%;overflow: auto;">
	<%
	for(String tmps:bindids)
	{
		%><option value="<%=tmps %>"><%=tmps %></option><%
	}
	%>
			</select>
		 </div>
	    </td>
       </tr>
       --%>
       <tr style="height:100%;border:solid 0px">
         <td style="width:5%;vertical-align:middle;"  >
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="map_or_not(true)" title="bind to tag"><i class="fa-solid fa-arrow-right"></i></button><br><br>
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="map_copy()" title="copy create tag and bind"><i class="fa fa-angles-right"></i><br><i class="fa fa-tag"></i></button><br><br>
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="map_or_not(false)" title="unbind from tag"><i class="fa-solid fa-arrow-left"></i></button>
	    </td>
	    <td style="width:95%;vertical-align: top;height:100%"  >
	    <div id="prop_edit_path" class="prop_edit_path">Bind Map &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <button id="btn_tag_syn" class="layui-btn layui-btn-xs layui-btn-primary" title="export" onclick="bind_export()"><i class="fa-solid fa-arrow-up"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
      <button id="" class="layui-btn layui-btn-xs layui-btn-primary" title="import" onclick="bind_import()"><i class="fa-solid fa-arrow-down"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
      <button id="" class="layui-btn layui-btn-xs layui-btn-primary" title="add tag in channel" onclick="add_tag()"><i class="fa-solid fa-plus"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
    </div>
	    <div id=""  class="prop_edit_panel" style="height:420px">
	       <table style="width:100%;overflow: auto;" >
	       	 <thead>
	       	   <tr>
	       	    <th style="width:50%">Map Binded</th>
	       	    <th style="width:50%">Tags In Channel</th>
	       	   </tr>
	       	 </thead>
	       	 <tbody id="tb_bind_map">
<%
	if(joined_ch!=null)
{
	for(UATag tag:ch_tags)
	{
		String tagp = tag.getNodeCxtPathIn(joined_ch) ;
		if(cpt.isInBindList(tagp))
			continue ;
		
		String c_p = cpt.getBindMap().get(tagp) ;
		if(c_p==null)
	c_p = "" ;
%>
<tr style="border:solid 1px;height:15px" tagp="<%=tagp%>" bindp="<%=c_p%>">
<%
	if(tagp.length()>map_show_len)
		tagp = "..."+tagp.substring(tagp.length()-map_show_len) ;
	if(c_p.length()>map_show_len)
		c_p = "..."+c_p.substring(c_p.length()-map_show_len) ;
	String vtstr = "" ;
	UAVal v;
	UAVal.ValTP vt = tag.getValTp();
	if(vt!=null)
		vtstr = vt.getStr();
%>
  <td><%=c_p %></td>
  <td title="<%=tag.getTitle()%>"><%=tagp %>:<%=vtstr %></td>
   <td></td>
</tr>
<%
	}
}
%>
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
var no_ajax = <%=no_ajax%>;
var prjid="<%=prjid%>";
var cpid = "<%=cpid%>";
var cptp = "<%=cptp%>";
var connid = "<%=connid%>";
var support_tree = <%=support_tree%>
var map_show_len = <%=map_show_len%> ;



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

function add_bind_item(bpath,tpath)
{
	var k = tpath.indexOf(':') ;
	if(k<0)
	{
		dlg.msg("tag path must end with value type like :int16") ;
		return false;
	}
	var vt = tpath.substring(k+1) ;
	var tagp = tpath.substring(0,k) ;
	if(!chk_var_path(tagp,true))
	{
		dlg.msg("tag path must be combined by var name,which must use a-z A-Z 0-9 _ and a-z A-Z first");
		return false;
	}
	if(bpath==null||bpath==undefined)
		bpath ="" ;
	var tmpid =new_id();
	var tmps = "<tr id='ntag_"+tmpid+"' tagp='"+tpath+"' bindp='"+bpath+"' onclick='on_right(this)'>";
	
		if(tagp.length>map_show_len)
			tagp = "..."+tagp.substring(tagp.length-map_show_len) ;
		if(bpath.length>map_show_len)
			bpath = "..."+bpath.substring(bpath.length-map_show_len) ;

	 tmps += "<td>"+bpath+"</td>";
	 tmps += "<td>"+tagp+":"+vt+"</td>";
	 tmps += "<td onclick=\"ntag_del('"+tmpid+"')\">X</td></tr>";
	 
	 $("#tb_bind_map").append(tmps) ;
	 return true;
}


function add_tag()
{
	dlg.open("../ua/tag_path_simple.jsp",
			{title:"Edit Tag Path Under Channel",w:'500px',h:'400px'},
			['Ok','Close'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 if(add_bind_item("",ret.path+":"+ret.vt))
							 dlg.close() ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function ntag_del(id)
{
	$("#ntag_"+id).remove();
}

var cur_bind_map_tr = null ;

$("#tb_bind_map tr").click(function(){
	cur_bind_map_tr = $(this) ;
	//var tagp = $(this).attr("tagp") ;
	//console.log(tagp) ;
	refresh_bind_map();
});

function on_right(ob)
{
	cur_bind_map_tr = $(ob) ;
	refresh_bind_map();
}

function refresh_bind_map()
{
	$("#tb_bind_map tr").each(function(){
		$(this).removeClass("map_sel") ;
	});
	if(cur_bind_map_tr!=null)
		cur_bind_map_tr.addClass("map_sel") ;
}


function map_set_to_tr(tr,bpath)
{
	var tmptd = tr.children('td').eq(0) ;
	tr.attr("bindp",bpath) ;
	tmptd.attr("title",bpath) ;
	if(bpath.length>map_show_len)
		bpath = "..."+bpath.substring(bpath.length-map_show_len) ;
	tmptd.html(bpath);
}

function get_map_vals()
{
	var r = "" ;
	var bfirst = true;
	$("#tb_bind_map tr").each(function(){
		var bp = $(this).attr("bindp") ;
		var tp =  $(this).attr("tagp") ;
		if(bp && tp)
		{
			if(bfirst) bfirst=false;
			else r += "|" ;
			r+= tp+'='+bp ;
		}
	});
	return r ;
}

function get_map_list()
{
	var r = [] ;
	$("#tb_bind_map tr").each(function(){
		var bp = $(this).attr("bindp") ;
		var tp =  $(this).attr("tagp") ;
		
		if(bp && tp)
		{
			r.push({bindp:bp,tagp:tp}) ;
		}
	});
	return r ;
}


function bind_export()
{
	dlg.open("cpt_bind_port.jsp?op=export&prjid="+prjid+"&cptp="+cptp+"&cpid="+cpid+"&connid="+connid,
			{title:"Export Bind",w:'800px',h:'600px'},
			['Close'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function bind_import()
{
	dlg.open("cpt_bind_port.jsp?op=import&prjid="+prjid+"&cptp="+cptp+"&cpid="+cpid+"&connid="+connid,
			{title:"Export Bind",w:'500px',h:'400px'},
			['Import','Close'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.prjid = prjid ;
						 ret.cptp = cptp ;
						 ret.connid = connid ;
						 ret.op='import';
						 //console.log(ret);
						 send_ajax('cpt_bind_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.close();
								//refresh_ui() ;
								ua_panel.redraw(false,false,true) ;
							},false);
							
						 
						 //document.location.href=document.location.href;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
</script>

<jsp:include page="cpt_bind_left_tb.jsp"></jsp:include>
<jsp:include page="cpt_bind_left_tree.jsp"></jsp:include>

<script type="text/javascript">

var b_tb = true  ;

function set_tree_or_tb()
{
	if(!support_tree)
		return ;
	
	var btl = $('#btn_tree_list');
	b_tb=!b_tb;
	if(b_tb)
	{
		btl.html("<i class='fa-solid fa-folder-tree'></i>");
		$("#list_tree").css("display","none");
		$("#list_table").css("display","");
	}
	else
	{
		btl.html("<i class='fa fa-list'></i>");
		$("#list_tree").css("display","");
		$("#list_table").css("display","none");
		tree_init();
	}
	
}

//set_tree_or_tb();//show tree

function map_or_not(b)
{
	if(cur_bind_map_tr==null)
	{
		dlg.msg("please select tag in channel right")
		return ;
	}
	
	if(b)
	{
		var vs ;
		if(b_tb)
			vs = tb_get_left_vals() ;
		else
			vs = tree_get_left_vals() ;
		if(vs.length<=0)
		{
			dlg.msg("please select item left") ;
			return ;
		}
		if(vs.length!=1)
		{
			dlg.msg("please select one item left") ;
			return ;
		}
		map_set_to_tr(cur_bind_map_tr,vs[0])
		return ;
	}
	
	//un sel
	var tmptd = cur_bind_map_tr.children('td').eq(0) ;
	tmptd.html("");
	tmptd.attr("title","") ;
	cur_bind_map_tr.attr("bindp","") ;
}

function map_copy()
{
	var vs ;
	if(b_tb)
		vs = tb_get_left_vals() ;
	else
		vs = tree_get_left_vals() ;
	if(vs.length<=0)
	{
		dlg.msg("please select item left") ;
		return ;
	}
	for(var v of vs)
		add_bind_item(v,v);
}
</script>
</html>