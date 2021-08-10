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
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String prjid = request.getParameter("prjid") ;
String cptp = ConnProOPCUA.TP;//request.getParameter("cptp") ;
ConnProOPCUA cp = (ConnProOPCUA)ConnManager.getInstance().getOrCreateConnProviderSingle(prjid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtOPCUA cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	out.print("no connid input") ;
	return;
}

cpt = (ConnPtOPCUA)cp.getConnById(connid) ;
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
<title>Opc UA Browser</title>
<jsp:include page="../../head.jsp"></jsp:include>
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
    <td colspan="2"><div id="prop_edit_path" class="prop_edit_path">OPC UA - <%=cpt.getName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </div></td>
  </tr>
  <tr>
    <td style="width:30%" >
    	<div id="opc_tree" class="prop_edit_cat">
    		
		</div>
    </td>
    <td style="width:70%;vertical-align: top;"  >
    <div id="editpanel"  class="prop_edit_panel" >
		<table id="sub_list" class="pi_edit_table">
		    
		</table>
	 </div>
	  <div id="editdesc"  class="prop_edit_desc">
	  
	  </div>
    </td>
  </tr>
</table>
</body>
<script type="text/javascript">
var prjid="<%=prjid%>";
var connid = "<%=connid%>";

function init_tree()
{
		$.jstree.destroy();
		this.jsTree = $('#opc_tree').jstree(
				{
					'core' : {
						'data' : {
							'url' : "opc_ua_ajax.jsp?prjid="+prjid+"&op=tree&connid="+connid,
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
							//'variant' : 'small',
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

function on_tree_node_sel(n)
{
	send_ajax("opc_ua_ajax.jsp","prjid="+prjid+"&op=sub_nodes&connid="+connid+"&nodeid="+n.id,function(bsucc,ret){
		if(!bsucc&&ret.indexOf("[")!=0)
		{
			dlg.msg(ret);
			return ;
		}
		if(typeof(ret)=="string")
			eval("ret="+ret) ;
		console.log(ret);
		var tmps = "" ;
		tmps += "<tr style='background-color:#979797'>";
		tmps += "<td>Node Type</td>"
		tmps += "<td>Name</td>"
		tmps += "<td>Data Type</td>"
		tmps += "<td>Value</td>"
		tmps += "<td>Date Time</td>"
		tmps += "</tr>"
		for(var r of ret)
		{
			tmps += "<tr>";
			tmps += "<td>"+r.tp+"</td>"
			tmps += "<td>"+r.name+"</td>"
			tmps += "<td>"+(r.datatp?r.datatp:"")+"</td>"
			var tmpv = r.val?r.val:"";
			if(tmpv.length>20)
				tmps += "<td title='"+tmpv+"'>"+tmpv.substr(0,20)+"...</td>";
			else
				tmps += "<td>"+tmpv+"</td>";
			tmps += "<td>"+(r.val_dt?r.val_dt:"&nbsp;")+"</td>"
			tmps += "</tr>"
		}
		$("#sub_list").html(tmps) ;
	});
}

init_tree();
</script>
</html>