<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
	static void renderTreeData(Writer out,UACh ch) throws Exception
	{
		out.write("[{") ;
		out.write("\"text\":\"Drivers\",\"id\":\"000\",\"type\":\"root\"") ;
		out.write(",\"icon0\": \"icon_prj\",\"state\": {\"opened\": true},\"children\": [");
		boolean bcat_first=true ;
		for(DevDrvCat ddc:DevManager.getInstance().getDriverCats())
		{
			if(bcat_first) bcat_first=false;
			else out.write(",") ;
			out.write("{\"text\": \""+ddc.getTitle()+"\"") ;
			out.write(",\"id\": \""+ddc.getName()+"\",\"type\":\"cat\"") ;
			out.write(",\"icon0\":\"icon_tagg\",\"state\": {\"opened\": true}") ;
			out.write(",\"children\": [") ;
			boolean b_first = true ;
			List<DevDriver> drvs = ddc.getDrivers() ;
			if(ch!=null)
				drvs = ch.filterSupportedDrivers(drvs) ;
			if(drvs==null||drvs.size()<=0)
				continue ;
			
			for(DevDriver dd:drvs)
			{
				if(b_first) b_first=false;
				else out.write(",") ;
				out.write("{\"text\": \""+dd.getTitle()+"\"") ;
				out.write(",\"id\": \""+dd.getName()+"\"}") ;
			}
			out.write("]}") ;
		}
		out.write("]}]") ;
	}
%><%
String prjid = request.getParameter("prjid") ;
String chid = request.getParameter("chid") ;
UACh ch = null ;
String seldrv = "" ;
if(Convert.isNotNullEmpty(chid))
{
	UAPrj rep = UAManager.getInstance().getPrjById(prjid) ;
	
	if(rep==null)
	{
		out.print("no rep found") ;
		return ;
	}
	
	ch = rep.getChById(chid) ;
	if(ch==null)
	{
		out.print("no ch found") ;
		return ;
	}
	seldrv = ch.getDriverName() ;
}
%>
<html>
<head>
<title>drv selector</title>

<jsp:include page="../head.jsp">
	<jsp:param value="true" name="tree"/>
</jsp:include>
<script>
dlg.resize_to(400,500);

</script>
</head>
<style>
tr:hover
{
	background-color: grey;
}
</style>
<body>
<blockquote class="layui-elem-quote " id="selected_info">&nbsp;
 <wbt:g>selected</wbt:g>:<span id="seled"></span>
 </blockquote>
<div id="cat_tree"></div>
<%--

<table class="layui-table" lay-size="sm">
  <colgroup>
    <col width="100" />
    <col />
  </colgroup>
  <tbody>
<%
for(DevDriver dd:dds)
{
%>
<tr onclick="sel_drv('<%=dd.getName()%>','<%=dd.getTitle() %>')">
      <td><%=dd.getName()%></td>
      <td><%=dd.getTitle() %></td>
</tr>
<%
}
%>
  </tbody>
</table>

 --%>
</body>
<script type="text/javascript">
layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	

var tree_data=<%renderTreeData(out,ch);%>;
var tree_sel_id = '<%=seldrv%>' ;
var drv_name = dlg.get_opener_opt('drv_name');
if(!tree_sel_id)
{
	if(drv_name)
		tree_sel_id = drv_name ;
}

function tree_init()
{
	$.jstree.destroy();
	//console.log(js_cxt);
	this.jsTree = $('#cat_tree').jstree(
				{
					'core' : {
						'data' : tree_data,
						'themes' : {
							//'responsive' : false,
							'variant' : 'small',
							'stripes' : true
						}
					},
					check_callback : true,
					'contextmenu' : { //
						
						'items' :(node)=>{
							//this.get_type(node)==='ch''
							//console.log(node)
							var tp = node.original.type
							return this.get_cxt_menu(tp,node.original) ;
		                }
					},
					
					'unique' : {
						'duplicate' : function (name, counter) {
							return name + ' ' + counter;
						}
					},
					'plugins' : ['types','unique'] //'state',','contextmenu' 'dnd',
				}
		).bind("activate_node.jstree", function (obj, e) {
		    var currentNode = e.node;
		    if (!e.instance.is_leaf(currentNode)) {
		        e.instance.deselect_node(currentNode);
		    };
		});
		this.jsTree.on('activate_node.jstree',(e,data)=>{
			on_tree_node_sel(data.node.original)
		});
		
		if(tree_sel_id)
			$('#cat_tree').jstree('select_node', tree_sel_id);
}

function on_tree_node_sel(n)
{
	if(n.type=='cat')
		return ;
	
	sel_drv(n.id,n.text) ;
}

tree_init()

var seled_drv=null ;
	
function win_close()
{
	dlg.close(0);
}

function sel_drv(n,t)
{
	seled_drv={name:n,title:t} ;
	$("#seled").html(t);
}

function do_submit(cb)
{
	if(seled_drv==null)
	{
		cb(false,'<wbt:g>pls,select,driver</wbt:g>') ;
		return ;
	}
	cb(true,seled_drv);
}

</script>
</html>