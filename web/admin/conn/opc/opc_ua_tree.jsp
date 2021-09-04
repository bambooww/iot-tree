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
<title>opc ua tree</title>
<jsp:include page="../../head.jsp"></jsp:include>
<script src="/_js/jstree/jstree.min.js"></script>
<link rel="stylesheet" href="/_js/jstree/themes/default/style.min.css" />
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<div id="opc_tree">
</div>
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
}

init_tree();

</script>
</html>