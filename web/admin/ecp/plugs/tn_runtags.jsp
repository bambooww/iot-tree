<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,org.json.*,
	java.util.*,
	java.net.*,org.iottree.core.devtree.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "treeid"))
	return ;

String treeid = request.getParameter("treeid") ;
String tree_nid = request.getParameter("tree_nid") ;
DTTree tree = DTTreeManager.getInstance().getTreeById(treeid);
if(tree==null)
{
out.println("no device tree found") ;
return ;
}
String title = "" ;
DTNode node = null;
JSONArray jarr = new JSONArray() ;
DTRunTag last_runtag = null ;
if(Convert.isNotNullEmpty(tree_nid))
{
	node = tree.findNodeById(tree_nid);
	if(node==null)
	{
		out.print("no tree node found") ;
		return ;
	}
	title = node.getTitle() ;
	LinkedHashMap<String,DTRunTag> tags_map = node.getRunTagsMap() ;
	
	for(DTRunTag rt:tags_map.values())
	{
		jarr.put(rt.toJO(true)) ;
		last_runtag = rt ;
	}
}

if(node==null)
{
	response.sendRedirect("tn_null.jsp") ;
	return;
}

String prjn = request.getParameter("prjn") ;
if(prjn==null)
{
	prjn = "" ;
	if(last_runtag!=null)
	{
		UAPrj lastprj = last_runtag.getPrj() ;
		if(lastprj!=null)
			prjn = lastprj.getName() ;
	}
}
String search_txt = request.getParameter("search_txt") ;
if(search_txt==null)
	search_txt = "" ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style>
.layui-table-view .layui-table td, .layui-table-view .layui-table th {
    padding: 1px 0;
}
.ellipsis-left {
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    direction: rtl;
}
.seled {background-color: #aaaaaa;}
.b_set {background-color: yellow;}
table {table-layout: fixed;width:100%;border:1px solid #ccc;}
button {border:1px solid #ccc;}
</style>
</head>
<body style="overflow: hidden;">
<div class="toolbar" style="width:100%;height:30px;line-height:30px;border:1px solid #ccc;text-align: center;">
	<wbt:g>node</wbt:g>:[<%=title %>] Tags<button id="btn_set_tags" style="width:30px;height:99%;font-size:20px;color:#ccc;float:right;margin-right:5px;" title="set tags to node" onclick="set_tags_to_node()"><i class="fa-solid fa-angles-right"></i></button>
</div>
<table id="tags_list" lay-filter="tags_list"  lay-size="sm" lay-even="true" class="layui-table" style="position: absolute;top:20px;width:100%;height:220px;">
</table>

<div style="border:0px solid red;position: absolute;top:235px;bottom:0px;width:100%;overflow: hidden;">
<iframe id="tags_if" src="../util/tags_selector.jsp?multi=true&prjn=<%=prjn %>&search_txt=<%=search_txt %>" style="border:0px;width:100%;height:100%;"></iframe>
</div>
</body>
<script type="text/html" id="tag_toolbar">
<div class="layui-btn-group">
  <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="del" style="color:red;"><i class="fa-solid fa-times"></i></button>
  </div>
</script>
<script type="text/javascript">
var treeid= "<%=treeid%>";
var tree_nid = "<%=tree_nid%>" ;
var b_dirty = false;
var tags_list = <%=jarr%>;
var form;
var table;
layui.use(['form','table'], function(){
	  form = layui.form;
	  form.render();
	  
	  table = layui.table;
	  
	  render_tb();
	  update_ui();
});

function render_tb()
{
	  let cols = [];
	 cols.push({field:'tagp',title: 'Tag', width:'40%',templet: function(d) {
	      var str = d.tagp;
	      return `<div class="ellipsis-left" title="\${str}">\${str}</div>`;
	    }});
	 cols.push({field: 'tagt', title: 'Title', width:'30%',templet: function(d) {
	      var str = d.tagt;
	      return `<div class="ellipsis-left" title="\${str}">\${str}</div>`;
	    }});
	 cols.push({field: 'tagvt', title: 'Type', width:'20%'});
	 cols.push({field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"10%" ,toolbar: '#tag_toolbar'}) ;
	 
	table.render({
	    elem: '#tags_list'
    	    ,height: "200px",className: 'ellipsis-left'
    	    ,page: false,even: true,limit:100000
    	    ,cols: [cols]
    		  ,data:[]
    		  ,text: {
    		      none: 'No Data'
    		  }
    	  });
	  
	  table.on('tool(tags_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM

		  if(lay_evt === 'del')
		  {
			  del_tg(data);
		  }
		  else if(lay_evt === 'edit')
		  {
			  edit_runblk(data) ;
		  }
		  else if(lay_evt === 'up')
		  {
			  updown_runblk(data,true) ;
		  }
		  else if(lay_evt === 'down')
		  {
			  updown_runblk(data,false) ;
		  }
		});
	  table.on('row(tags_list)', function(obj)
			  {
		  var trs = $(".layui-table-body.layui-table-main tr");
		  trs.each(function(){
			  $(this).removeClass("seled") ;
		  })
		  obj.tr.addClass("seled");
				  var data = obj.data; //cur d
				  	on_sel_tag(data)
			  });
	  
	  //refresh_table(true);
}

function on_sel_tag(d){}

function tb_reload(data)
{
	table.reload("tags_list",{data:data});
}

function update_ui()
{
	tb_reload(tags_list);
}

function get_tg_by_path(p)
{
	for(let tg of tags_list)
	{
		if(tg.tagp ==p)
			return tg ;
	}
	return null ;
}

function get_tgidx_by_path(p)
{
	let idx = -1;
	let cc = 0 ;
	for(let tg of tags_list)
	{
		if(tg.tagp ==p)
		{
			idx = cc ;
			break;
		}
		cc ++ ;
	}
	return idx ;
}

function del_tg(d)
{
	let p = d.tagp ;
	let idx = get_tgidx_by_path(p) ;
	if(idx<0) return;
	tags_list.splice(idx,1) ;
	update_ui();
	b_dirty = true;
	$("#btn_set_tags").css("background-color","yellow") ;
}

//var cur_seled_tags = null ;


function set_tags_to_node()
{
	if(!b_dirty)
		return;
	let tags = tags_list;
	dlg.loading(true);
	send_ajax("../dt_tree_ajax.jsp",{op:'set_node_tags',treeid:treeid,tree_nid:tree_nid,jarr:JSON.stringify(tags)},(bsucc,ret)=>{
		dlg.loading(false);
		if(!bsucc || ret!='succ')
		{
			dlg.msg(ret);return;
		}
		$("#btn_set_tags").css("background-color","") ;
		parent.reload_tree();
		b_dirty = false;
	})
	
}

function on_tag_set_to_node(tg)
{
	//console.log("on tag set",tg);
	let oldtg = get_tg_by_path(tg.tagp);
	if(oldtg)
	{
		dlg.msg("Tag "+tg.tagp+" is already existed") ;return;
	}
	tags_list.push(tg) ;
	update_ui();
	$("#btn_set_tags").css("background-color","yellow") ;
	b_dirty = true;
}

function on_single_node_seled(node)
{
	if(!node)
	{
		location.href="dt_tree_tn_runtags.jsp?treeid="+treeid;
		return;
	}
	let tags_if = $("#tags_if")[0].contentWindow;
	let prjn = "";
	let search_txt="" ;
	if(tags_if)
	{
		prjn = tags_if.prjn||"";
		search_txt = tags_if.search_txt||"";
	}
	location.href="tn_runtags.jsp?treeid="+treeid+"&tree_nid="+node.id+"&prjn="+prjn+"&search_txt="+search_txt;
}
	
function win_close()
{
	dlg.close(0);
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

function get_tags_list()
{
	return tags_list;
}

</script>
</html>                                                                                                                                                                                                                            