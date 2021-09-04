<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	java.util.*
	"%><%
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

/*
List<UaNode> nodes = cpt.opcBrowseNodes();
if(nodes==null)
{
	nodes = new ArrayList<>(0);
}
*/
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
height:400px;
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

</style>
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<table class="prop_table" >
  <tr>
    <td colspan="3">
     <div id="prop_edit_path" class="prop_edit_path">[<%=cptp %>] <%=cp.getName() %>/<%=cpt.getName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <button class="layui-btn layui-btn-xs layui-btn-primary" title="set bind parameters" onclick="set_bind_pm()"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></button> 
         <button class="layui-btn layui-btn-xs layui-btn-primary" title="refresh" onclick="refresh_tree()"><i class="fa fa-refresh" aria-hidden="true"></i></button> 
    </div>
    </td>
  </tr>
  <tr>
    <td style="width:55%" >
    	<div id="bind_tree" class="prop_edit_cat">
    		
		</div>
    </td>
    <td style="width:5%;vertical-align:middle;"  >
     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="sel_or_not(true)">&gt;&gt;</button><br><br>
     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="sel_or_not(false)">&lt;&lt;</button>
    </td>
    <td style="width:40%;vertical-align: top;"  >
    <div id="editpanel"  class="prop_edit_panel" >
    <datalist id="cars">
  <option value="BMW">
  <option value="Ford">
  <option value="Volvo">
</datalist>
		<select multiple="multiple" id="bind_selected" style="width:100%;height:100%">
		
		</select>
	 </div>
	 
    </td>
  </tr>
</table>
</body>
<script type="text/javascript">
var prjid="<%=prjid%>";
var cpid = "<%=cpid%>";
var cptp = "<%=cptp%>";
var connid = "<%=connid%>";


function init_tree()
{
		$.jstree.destroy();
		this.jsTree = $('#bind_tree').jstree(
				{
					'core' : {
						'data' : {
							'url' : "cpt_bind_ajax.jsp?prjid="+prjid+"&op=tree&cpid="+cpid+"&cptp="+cptp+"&connid="+connid,
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

var cur_sel_node = null ;

function on_tree_node_sel(n)
{
	//"prjid="+prjid+"&op=sub_nodes&connid="+connid+"&nodeid="+n.id
	cur_sel_node = n ;
	console.log(n) ;
}

function sel_or_not(b)
{
	if(b)
	{
		if(cur_sel_node==null)
		{
			dlg.msg("please select a node left") ;
			return ;
		}
		var v = cur_sel_node.id ;
		if(has_selected_val(v))
			return ;
		$("#bind_selected").append("<option value='"+cur_sel_node.id+"'>"+cur_sel_node.id+"</option>");
		return ;
	}
	
	//un sel
	$("#bind_selected  option:selected").each(function(){
	    //var tmpv = $(this).attr("value") ;
	    $(this).remove() ;
	})
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

function has_selected_val(v)
{
	var r = false;
	$("#bind_selected  option").each(function(){
	    if( $(this).attr("value") ==v)
	    	r = true ;
	})
	return r ;
}

init_tree();

function refresh_tree()
{
	init_tree();
}

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



</script>
</html>