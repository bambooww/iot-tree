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
<table class="prop_table" >
  <tr>
    <td style="width:50%" >
    <div id="prop_edit_path" class="prop_edit_path">[<%=cptp %>] <%=cp.getName() %>/<%=cpt.getName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     <button id="btn_tree_list" class="layui-btn layui-btn-xs layui-btn-primary" title="set tree or list" onclick="set_tree_list()"><i class="fa-solid fa-folder-tree"></i></button>
<%--
        <button class="layui-btn layui-btn-xs layui-btn-primary" title="set bind parameters" onclick="set_bind_pm()"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button>
         --%>
         <button class="layui-btn layui-btn-xs layui-btn-primary" title="refresh" onclick="refresh_tree()"><i class="fa fa-refresh" aria-hidden="true"></i></button> 
    </div>
    	<div id="bind_tree" class="prop_edit_cat" style="height:420px;">
    		
		</div>
    </td>
    <td style="width:50%" >
      <table style="border:0px">
       <tr style="height:30%;border:solid 0px">
	       <td style="width:5%;vertical-align:middle;"  >
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="sel_or_not(true)"><i class="fa-solid fa-arrow-right"></i></button><br><br>
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="sel_or_not(false)"><i class="fa-solid fa-arrow-left"></i></button>
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
       <tr style="height:70%;border:solid 0px">
         <td style="width:5%;vertical-align:middle;"  >
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="map_or_not(true)"><i class="fa-solid fa-arrow-right"></i></button><br><br>
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="map_or_not(false)"><i class="fa-solid fa-arrow-left"></i></button>
	    </td>
	    <td style="width:95%;vertical-align: top;height:100%"  >
	    <div id="prop_edit_path" class="prop_edit_path">Bind Map &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      
    </div>
	    <div id=""  class="prop_edit_panel" style="height:210px">
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
  <td><%=tagp %>:<%=vtstr %></td>
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

$("#tb_bind_map tr").click(function(){
	cur_bind_map_tr = $(this) ;
	var tagp = $(this).attr("tagp") ;
	console.log(tagp) ;
	refresh_bind_map();
});

function refresh_bind_map()
{
	$("#tb_bind_map tr").each(function(){
		$(this).removeClass("map_sel") ;
	});
	if(cur_bind_map_tr!=null)
		cur_bind_map_tr.addClass("map_sel") ;
}


function init_tree()
{
		$.jstree.destroy();
		this.jsTree = $('#bind_tree').jstree(
				{
					'core' : {
						'data' : {
							//'url' : "cpt_bind_ajax.jsp?prjid="+prjid+"&op=tree&cpid="+cpid+"&cptp="+cptp+"&connid="+connid,
							"dataType" : "json"
						},
						'check_callback' : function(o, n, p, i, m) {
							if(m && m.dnd && m.pos !== 'i') { return false; }
							if(o === "move_node" || o === "copy_node") {
								if(this.get_node(n).parent === this.get_node(p).id) { return false; }
							}
							return true;
						},
						'themes' : {
							'responsive' : false,
							'variant' : 'small',
							'stripes' : true
						}
					},
					'contextmenu' : { //
						
						'items' :(node)=>{
							//this.get_type(node)==='ch''
							//console.log(node)
							var tp = node.original.type
							//console.log(tp) ;
							return this.get_cxt_menu(tp,node.original) ;
		                }
					},
					'types' : {
						'default' : { 'icon' : 'folder' },
						'file' : { 'valid_children' : [], 'icon' : 'file' }
					},
					'unique' : {
						'duplicate' : function (name, counter) {
							return name + ' ' + counter;
						}
					},
					'plugins' : ['state','dnd','types','contextmenu','unique']
				}
		)
		
		this.jsTree.on('activate_node.jstree',(e,data)=>{
			on_tree_node_sel(data.node.original)
		})
}

var b_list = true ;

function set_tree_list()
{
	var btl = $('#btn_tree_list');
	if(b_list)
	{
		b_list=false;
		btl.html("<i class='fa fa-list'></i>");
	}
	else
	{
		b_list=true ;
		btl.html("<i class='fa-solid fa-folder-tree'></i>");
	}
	refresh_tree();
}

var cur_sel_node = null ;

function on_tree_node_sel(n)
{
	//"prjid="+prjid+"&op=sub_nodes&connid="+connid+"&nodeid="+n.id
	cur_sel_node = n ;
	console.log(n) ;
}

function get_sel_tree_nodes()
{
	var tns = $('#bind_tree').jstree(true).get_selected(true);
	var rets=[] ;
	for(var tn of tns)
	{
		rets.push(tn.original) ;
	}
	return rets;
}

function sel_or_not(b)
{
	
	if(b)
	{
		var tns = get_sel_tree_nodes();
		if(tns.length<=0)
		{
			dlg.msg("please select nodes left") ;
			return ;
		}
		
		for(var tn of tns)
		{
			if(tn.tp!='tagg' && tn.tp!='tag')
				continue ;
			var v = tn.id;
			if(tn.nc=='tag')
				v += ":"+tn.vt ;
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

function map_or_not(b)
{
	if(b)
	{
		var tns = get_sel_tree_nodes();
		if(tns.length<=0)
		{
			dlg.msg("please select a tag node left") ;
			return ;
		}
		if(tns.length>1)
		{
			dlg.msg("please select one tag node left") ;
			return ;
		}
		
		var tn = tns[0] ;
		if(tn.tp!='tag')
		{
			dlg.msg("please select a tag node left") ;
			return ;
		}
		if(cur_bind_map_tr==null)
		{
			dlg.msg("please select tag in channel right")
			return ;
		}
		
		var v = tn.id+":"+tn.vt ;
		
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


init_tree();

function refresh_tree()
{
	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"tree",list:b_list,cpid:cpid,cptp:cptp,connid:connid},function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		var tree = $('#bind_tree');
		var ob = null;
		eval("ob="+ret) ;
		tree.jstree(true).settings.core.data = ob;
		tree.jstree(true).refresh();
	});
	
}
	
refresh_tree();

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