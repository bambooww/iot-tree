<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>	
<%
//String prjid = 
String classn = request.getParameter("cn") ;
Class<?> cc = Class.forName(classn) ;

boolean bdlg = "true".equalsIgnoreCase(request.getParameter("dlg"));
%>
<html>
<head>
<title>context script obj helper</title>

<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tree"/>
</jsp:include>

<style>
		table{border-collapse:collapse;}
		body,td{font-size:12px;cursor:default;}
		.tree_c
		{
			position: absolute;
			top:20px;
			bottom:0px;
			width:100%;
			overflow: auto;
		}
</style>
<script>
	dlg.resize_to(500,400) ;
</script>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<b>Member of  - <%=cc.getSimpleName() %></b>
<div class="tree_c">
<div id="tree" class="tree" style="height:100%;width:100%;overflow: auto;"></div>
</div>

<div id='opc_info'>
</div>
</body>
<script>
var bdlg = <%=bdlg%>;
var classn = "<%=classn%>" ;
var pm_objs = dlg.get_opener_opt("pm_objs");

if(pm_objs)
	pm_objs = JSON.stringify(pm_objs);
else
	pm_objs="";

function log(s)
{
	document.getElementById('log_inf').innerHTML = s ;
}

function tree_init()
{
	$.jstree.destroy();
	this.jsTree = $('#tree').jstree(
				{
					'core' : {
						'data' : {
							'url' :"mn_help_ob_ajax.jsp?op=sub&cn="+classn,
							"dataType" : "json",
							"data":function(node){
		                        return {"id" : node.id};
		                    }
						},
						
						'themes' : {
							//'responsive' : false,
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
					'plugins' : ['types','unique'] //'state',','contextmenu' 'dnd',
				}
		);
	
	
		this.jsTree.on('activate_node.jstree',(e,data)=>{
			on_tree_node_sel(data.node.original)
		})
		
}

function on_tree_node_sel(n)
{
	//console.log("select",n) ;
	var id = n.id;
	//send_ajax("cxt_script_help_ajax.jsp",{op:"sub_detail",path:path,id:id,pm_objs:pm_objs},(bsucc,ret)=>{
	//	$("#node_detail").html(ret) ;
	//}) ;
}



tree_init();

</script>
</html>