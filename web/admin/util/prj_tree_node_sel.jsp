<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%!
	
%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
	String prjid = request.getParameter("prjid") ;
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="tree"/>
</jsp:include>
</head>
<script type="text/javascript">
dlg.resize_to(400,500);
</script>
<body>
<div id="sel_tree" class="tree" style="width:100%;height:400px;overflow: auto">
</div>
</body>
<script type="text/javascript">

var prjid="<%=prjid%>" ;

function init_tree()
{
	$.jstree.destroy();
	this.jsTree = $('#sel_tree').jstree(
			{
				'core' : {
					'data' : {
						'url' : "../prj_tree_ajax.jsp?sel=true&cont_only=true&id="+prjid,
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
				'types' : {
					'default' : { 'icon' : 'folder' },
					'file' : { 'valid_children' : [], 'icon' : 'file' }
				},
				'unique' : {
					'duplicate' : function (name, counter) {
						return name + ' ' + counter;
					}
				},
				'plugins' : ['types','unique'] //,'dnd',
			}
	);

	this.jsTree.on('activate_node.jstree',(e,data)=>{
		on_tree_node_selected(data.node.original)
	})
	
	return this.jsTree;
}

var sel_path = null ;

function on_tree_node_selected(tn)
{
	sel_path = tn.path ;
}

init_tree();

function get_select(cb)
{
	if(!sel_path)
	{
		cb(false,"<w:g>pls,select,node</w:g>") ;
		return ;
	}
	cb(true,sel_path) ;
}


</script>
</html>