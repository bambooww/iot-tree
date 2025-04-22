<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><html>
<head>
<title></title>
<jsp:include page="../head.jsp">

	<jsp:param value="true" name="tree"/>
</jsp:include>
<style type="text/css">
.top {position: absolute;top:0px;left:0px;width:100%;height:40px;background-color0: #f2f2f2;border-bottom: 1px solid #e6e6e6;}
.btm {position: absolute;top:45px;left:0px;width:100%;bottom: 0px;overflow: auto;}

.jstree-icon {display:none;}
.tn_icon {font-size: 10px;}
.tn_edit {color:green;}
.tn_plug_ico {width:30px;height:30px;background-color:#333333;color:#81ec21;}
</style>
<script>

</script>
</head>
<body>
<div class="top">
<table style="width:100%;height:100%;">
	<tr>
		<td style="width:99%">
			<select id="root"  class="layui-input" onchange="on_root_chg()">
<%
String first_path = null ;
File[] root_files = File.listRoots() ;
for(File rootf:root_files)
{
	String path = rootf.getAbsolutePath() ;
	String id = path.replaceAll("\\\\", "/") ;
	if(first_path==null)
		first_path = id ;
%><option value="<%=id %>"><%=path %></option><%
}

%>
			</select>
		</td>
	</tr>
</table>

</div>
<div class="btm">
<div id="dir_tree"></div>
</div>
</body>
<script type="text/javascript">
dlg.resize_to(900,600)
var cur_root = "<%=first_path%>"
var form ;
var jsTree ;

layui.use('form', function(){
	  form = layui.form;
});

function on_root_chg()
{
	cur_root = $("#root").val() ;
	//console.log(cur_root) ;
	refresh_tree();
}

function tree_init()
{
	$.jstree.destroy();
	jsTree = $('#dir_tree').jstree(
			{
				'core' : {
					'data' : {
						'url' : function(node){
							var pnid = "" ;
							//console.log(node) ;
							if(node)
								pnid = node.id ;
	                    	return "dir_selector_ajax.jsp?op=treen&root="+cur_root+"&tree_nid="+pnid;
	                    },
						"dataType" : "json",
						"data":function(node){
	                        return {"id" : node.id};
	                    }
					},
					'check_callback' : function(o, n, p, i, m) {
						
						if(o === "move_node") // || o === "copy_node")
						{
							let nn = n.original ;
							let pp = p.original ;
							if(!pp) return false;
							let broot  = pp.root ;
							
							if(nn.tn_loc!=true || pp.tn_loc!=true) return false;
						}
						return true;
					},
					'themes' : {
						'responsive' : false,
						'variant0' : 'small',
						'stripes' : false
					}
				},
				'contextmenu' : { //
					
					'items' :(node)=>{
						let nd = node.original ;
						return get_cxt_menu(nd) ;
	                }
				},
				'unique' : {
					'duplicate' : function (name, counter) {
						return name + ' ' + counter;
					}
				},
				'plugins' : ['state','dnd','types','contextmenu','unique']
			}
	)
	
	jsTree.on('activate_node.jstree',(e,data)=>{
		on_tree_node_sel(data.node.original)
	})

}

var cur_sel_node = null ;

function on_tree_node_sel(n)
{
	cur_sel_node = n ;
	console.log(n) ;
}

function get_sel_dir()
{
	return cur_sel_node?.id||"" ;
}

function refresh_tree()
{
	//jsTree.jstree(true).refresh();
	jsTree.jstree(true).refresh();
}


function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

tree_init();
</script>
</html>                                                                                                                                                                                                                            