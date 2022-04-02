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

if(!Convert.checkReqEmpty(request, out, "prjid","cptp"))
	return;
String prjid = request.getParameter("prjid") ;
String cptp = request.getParameter("cptp") ;//ConnProOPCUA.TP;//
ConnProvider cp = ConnManager.getInstance().getOrCreateConnProviderSingle(prjid, cptp);
if(cp==null)
{
	String cpid = request.getParameter("cpid") ;
	if(Convert.isNotNullEmpty(cpid))
	{
		cp =  ConnManager.getInstance().getConnProviderById(prjid, cpid);
	}
}
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
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
dlg.resize_to(600,520);
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
    </div>
       <div id="list_table" class="prop_edit_cat" style="height:420px">
    	<table style="width:100%;border:0px" class='besel'>
    		<thead>
    			<tr style="background-color: #f0f0f0">
    				<td width="70%">Path</td>
    				<td width="10%">Type</td>
    				<td width="20%">Value</td>
    			</tr>
    		</thead>
    		<tbody id="bind_tb_body" style="height0:390px">
    			
    		</tbody>
		</table>
	</div>
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
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="copy_map()" title="copy create tag and bind"><i class="fa fa-angles-right"></i><br><i class="fa fa-tag"></i></button><br><br>
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
var prjid="<%=prjid%>";
var cpid = "<%=cpid%>";
var cptp = "<%=cptp%>";
var connid = "<%=connid%>";

var map_show_len = <%=map_show_len%> ;

var cur_bind_map_tr = null ;

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

function ntag_del(id)
{
	$("#ntag_"+id).remove();
}

var cur_lefts_trs = [] ;


function on_left(tr)
{
	if(!b_ctrl_down)
	{
		cur_lefts_trs.length=0;
	}
	
	//TODO shift support later
	
	if(cur_lefts_trs.indexOf(tr)<0)
			cur_lefts_trs.push(tr) ;
	
	refresh_left();
}

function refresh_left()
{
	$("#bind_tb_body tr").each(function(){
		$(this).removeClass("map_sel") ;
	});
	for(var tmptr of cur_lefts_trs)
		$(tmptr).addClass("map_sel") ;
}


function copy_or_not(b)
{
	
	if(b)
	{
		if(cur_lefts_trs.length<=0)
		{
			dlg.msg("please select items left") ;
			return ;
		}
		
		for(var tr of cur_lefts_trs)
		{
			var tn = $(tr) ;
			//var opctp = tn.attr("opc_tp") ;
			var v = tn.attr("path") + ":"+tn.attr("vt") ;
			if(has_selected_val(v))
				continue ;
			$("#bind_selected").append("<option value='"+v+"'>"+v+"</option>");
		}
		return;
	}
	
	//un sel
	$("#bind_selected  option:selected").each(function(){
	    //var tmpv = $(this).attr("value") ;
	    $(this).remove() ;
	})
}

function copy_map()
{
	if(cur_lefts_trs.length<=0)
	{
		dlg.msg("please select item left") ;
		return ;
	}
	
	for(var tr of cur_lefts_trs)
	{
		var tn = $(tr) ;
		var p = tn.attr("path");
		var v = p + ":"+tn.attr("vt") ;
		
		add_bind_item(v,v);
	}
	
}

function map_or_not(b)
{
	if(b)
	{
		if(cur_lefts_trs.length!=1)
		{
			dlg.msg("please select one item left") ;
			return ;
		}
		
		var tn = $(cur_lefts_trs[0]) ;
		var v = tn.attr("path") + ":"+tn.attr("vt") ;
		
		if(cur_bind_map_tr==null)
		{
			dlg.msg("please select tag in channel right")
			return ;
		}
		
		var tmptd = cur_bind_map_tr.children('td').eq(0) ;
		cur_bind_map_tr.attr("bindp",v) ;
		tmptd.attr("title",v) ;
		if(v.length>map_show_len)
			v = "..."+v.substring(v.length-map_show_len) ;
		tmptd.html(v);
		
		return ;
	}
	
	//un sel
	if(cur_bind_map_tr==null)
	{
		dlg.msg("please select tag in channel right")
		return ;
	}
	var tmptd = cur_bind_map_tr.children('td').eq(0) ;
	tmptd.html("");
	tmptd.attr("title","") ;
	cur_bind_map_tr.attr("bindp","") ;
}

function get_selected_vals()
{
	var ret=[];
	$("#bind_selected  option").each(function(){
	    var tmpv = $(this).attr("value") ;
	    ret.push(tmpv) ;
	})
	return ret;
}

function get_bindlist_valstr()
{
	var bindids = get_selected_vals()
	var bindstr = "" ;
	if(bindids!=null)
	{
		for(var bid of bindids)
			bindstr += '|'+bid ;
		bindstr = bindstr.substr(1) ;
	}
	return bindstr ;
}

function has_selected_val(v)
{
	var r = false;
	$("#bind_selected  option").each(function(){
	    if( $(this).attr("value") ==v)
	    	r = true ;
	})
	return r ;
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

var LOAD_ROWS = 40 ;
var page_last_idx = 0 ;
var page_has_next = true;
var search_key = "" ;

function search(bclear)
{
	
	var sk = "";
	if(bclear)
		$("#inp_search").val("") ;
	else
		sk = $("#inp_search").val() ;
	search_key = sk ;
	if(!sk)
	{
		refresh_tb_list(false)
		return ;
	}
	
	$('#bind_tb_body').html("") ;
	page_last_idx = 0 ;
	page_has_next = true;
	show_tb_list();
	return ;
}

function do_search_ret(e)
{
    var evt = window.event || e;
    if (evt.keyCode == 13) {
    	search(false)
    }
}

function refresh_tb_list(breload)
{
	if(!breload)
	{
		$('#bind_tb_body').html("") ;
		page_last_idx = 0 ;
		page_has_next = true;
		show_tb_list();
		return ;
	}
	
	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"clear_cache",cpid:cpid,cptp:cptp,connid:connid},(bsucc,ret)=>{
			$('#bind_tb_body').html("") ;
			page_last_idx = 0 ;
			page_has_next = true;
			show_tb_list();
			return ;
	}) ;
}

function show_tb_list()
{
	var idx = page_last_idx;
	var size = LOAD_ROWS ;
	dlg.loading(true) ;
	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"list",cpid:cpid,cptp:cptp,connid:connid,idx:idx,size:size,sk:search_key},function(bsucc,ret){
		dlg.loading(false) ;
	//	console.log("ret len="+ret.length) ;
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		var tbb = $('#bind_tb_body');
		var obs = null;
		eval("obs="+ret) ;
		page_has_next = obs.length>=size;
		page_last_idx += obs.length ;
		for(var ob of obs)
		{
			tbb.append(ob2tr_row(ob));
		}
		
	});
}


var ROW_MAX_LEN = 30 ;

function ob2tr_row(ob)
{
	var ret = "<tr path='"+ob.path+"' vt='"+ob.vt+"' onclick='on_left(this)'>" ;
	var txt = ob.path ;
	var txtlen = txt.length ;
	if(txtlen>ROW_MAX_LEN)
	{
		ret += "<td title='"+txt+"'>..."+txt.substring(txtlen-ROW_MAX_LEN)+"</td>";
	}
	else
	{
		ret += "<td>"+txt+"</td>";
	}
	
	ret += "<td>"+ob.vt+"</td>";
	ret += "<td></td>";
	ret += "</tr>"
	return ret ;
}

function read_tmp_paths_vals()
{
	if(cur_lefts_trs.length<=0)
		return ;
	var pstr = $(cur_lefts_trs[0]).attr("path") ;
	for(var i = 1 ; i < cur_lefts_trs.length;i++)
		pstr += ","+$(cur_lefts_trs[i]).attr("path") ;
	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"tmp_paths_vals",cpid:cpid,cptp:cptp,connid:connid,paths:pstr},function(bsucc,ret){
		
	//	console.log("ret len="+ret.length) ;
		if(!bsucc||ret.indexOf("{")!=0)
		{
			console.log(ret) ;
			return ;
		}
		var tbb = $('#bind_tb_body');
		var ob = null;
		eval("ob="+ret) ;
		for(var n in ob)
		{
			var v = ob[n] ;
			var tr = tbb.find("tr[path$='"+n+"']") ;
			tr.children('td').eq(2).html(v);
		}
		
	});
	
}

//setInterval(read_tmp_paths_vals,2000) ;

var allshow=false;

var sdiv = $("#list_table")[0] ;
$("#list_table").scroll(()=>{
	 var wholeHeight=sdiv.scrollHeight;
	 var scrollTop=sdiv.scrollTop;
	 var divHeight=sdiv.clientHeight;
	 if(divHeight+scrollTop>=wholeHeight)
	 {//reach btm
		 if(!page_has_next)
			{
				if(!allshow)
					lj.msg("已经显示全部");
				allshow=true;
				return;
			}
				
			//console.log("show more");
			show_tb_list();
		    $("list_table").scroll(scrollTop);
	 }
	 if(scrollTop==0)
	 {//reach top
		
	}
});
	
show_tb_list();

function set_bind_pm()
{
	dlg.open_win("./ext/cpt_bindparam_"+cptp+".jsp?prjid="+prjid+"&cptp="+cptp+"&cpid="+cpid+"&connid="+connid,
			{title:"Binding Parameters",w:'600',h:'450'},
			['Ok',{title:'Cancel',style:"primary"}],
			[
				function(dlgw)
				{
					var bindids = dlgw.get_selected_vals();
					
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
				
			]);
}

function syn_tag_ch()
{
	var bindstr = get_bindlist_valstr();

	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"syn_bind_tags",cpid:cpid,cptp:cptp,connid:connid,bindids:bindstr},function(bsucc,ret){
		if(!bsucc||ret!='succ')
		{
			dlg.msg(ret) ;
			return ;
		}
		dlg.msg("syn ok,please refresh tree to check");
	});
}

</script>
</html>