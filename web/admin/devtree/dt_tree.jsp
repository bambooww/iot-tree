<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	org.iottree.core.devtree.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "treeid"))
		return ;

	String treeid = request.getParameter("treeid") ;
	DTTree dttree = DTTreeManager.getInstance().getTreeById(treeid) ;
	if(dttree==null)
	{
		out.println("no device tree found") ;
		return ;
	}
	String title = dttree.getTitle() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tree"/>
</jsp:include>
<style type="text/css">
.top {position: absolute;top:0px;left:0px;width:100%;height:40px;background-color0: #f2f2f2;border-bottom: 1px solid #e6e6e6;}
.btm {position: absolute;top:45px;left:0px;width:100%;bottom: 0px;overflow: auto;}
.tn_icon {font-size: 10px;}
.tn_edit {color:green;}
.tn_plug_ico {width:30px;height:30px;background-color:#333333;color:#81ec21;}
.jstree-icon {display:none;}
</style>
<script>
//dlg.resize_to(400,220);
</script>
</head>
<body>
<div class="top">

<table style="width:100%;height:100%;">
	<tr>
		<td style="width:30%;">设备：<%=title%></td>
		<td style="padding:5px;width:100px">
			<input type="text" id="search_txt" class="layui-input" style="height:30px;"/>
		</td>
		<td><button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="search()" ><i class="fa fa-search" ></i></button></td>
	</tr>
</table>

</div>
<div class="btm">
<div id="device_tree"></div>
</div>
</body>
<script type="text/javascript">

var treeid = "<%=treeid%>" ;
var form ;
var jsTree ;

function act_node_copy(node,op_name)
{
	let nid = node.id ;
	send_ajax("device_tree_ajax.jsp",{op:"tn_copy",cp_deviceid:deviceid,cp_nid:nid},(bsucc,ret)=>{
		if(ret=='succ')
			dlg.msg("复制成功");
		else
			dlg.msg(ret) ;
	}) ;
}

function act_node_paste(node,op_name)
{
	let nid = node.id ;
	let paste_sub = "paste_sub"==op_name ;
	send_ajax("device_tree_ajax.jsp",{op:"tn_paste",deviceid:deviceid,nid:nid,paste_sub:paste_sub},(bsucc,ret)=>{
		if(ret=='succ')
		{
			dlg.msg("粘贴新建成功");
			refresh_tree();
		}
		else
			dlg.msg(ret) ;
	}) ;
}

function CXTMENU_ACT(data)
{
	var inst = $.jstree.reference(data.reference);
    var node = inst.get_node(data.reference).original;
	
	//console.log(data.item.op_name,node) ;
	var act = data.item.op_action
	if(act!=undefined&&act!=null&&act!="")
		act(node,data.item.op_name) ;
}

function get_cxt_menu(nd)
{
	let r={} ;
	r['copy']={op_name:"copy",op_action:act_node_copy,label:"复制",icon:"fa fa-copy", "separator_after": true,action:CXTMENU_ACT} ;
	r['paste']={op_name:"paste",op_action:act_node_paste,label:"粘贴",icon:"fa fa-clipboard", "separator_after": true,action:CXTMENU_ACT} ;
	//r['paste_sub']={op_name:"paste_sub",op_action:act_node_paste,label:"粘贴子节点",icon:"fa fa-clipboard", "separator_after": true,action:CXTMENU_ACT} ;
	return r ;
}

layui.use('form', function(){
	  form = layui.form;
});

function tree_init()
{
	$.jstree.destroy();
	jsTree = $('#device_tree').jstree(
			{
				'core' : {
					'data' : {
						'url' : function(node){
							var pnid = "" ;
							//console.log(node) ;
							if(node)
								pnid = node.id ;
	                    	return "dt_tree_ajax.jsp?op=treen&treeid="+treeid+"&tree_nid="+pnid;
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
							
							//if(broot==true && nn.tp=='d'&&nn.tn_loc==true)
							//	return true ;
							if(nn.tn_loc!=true || pp.tn_loc!=true) return false;
							//console.log(nn,pp) ;
						}
						return true;
					},
					'themes' : {
						'responsive' : false,
						'variant0' : 'small',
						'icons': false,
						'stripes' : false
					}
				},
				'contextmenu0' : { //
					
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
				'plugins' : ['state','dnd','types','contextmenu0','unique']
			}
	)
	
	jsTree.on('activate_node.jstree',(e,data)=>{
		on_tree_node_sel(data.node.original)
	})

	jsTree.on('state_ready.jstree', function (e, data) {
	    var selns = $('#device_tree').jstree(true).get_selected(true);
	    if(selns && selns.length==1)
	    	on_tree_node_sel(selns[0].original)
	    else
	    {
	    	jsTree.jstree('select_node', deviceid);
	    	var selns = $('#device_tree').jstree(true).get_selected(true);
	    	if(selns&&selns.length>0)
	    		on_tree_node_sel(selns[0].original)
	    }
	});
	
	jsTree.on('move_node.jstree', function (e, data) {
		if(!mv_nodes)
			mv_nodes = [] ;
		let mv_nd = data.node.original;
		let new_pid = data.parent;
		if(!mv_new_pid)
		{
			mv_new_pid = new_pid ;
		}
		else if(mv_new_pid!=new_pid)
		{
			return ;
		}
			
		mv_nodes.push(mv_nd.id) ;
		//let old_pid = data.old_parent;
        //mv_node(mv_nd, old_pid, new_pid);
		clearTimeout(mv_node_timeout);
        mv_node_timeout = setTimeout(function() {
	        do_mv_nodes();
	    }, 300); 
	});
}

var mv_node_timeout = null ;
var mv_nodes = null ;
var mv_new_pid = null ;
function do_mv_nodes()
{
	clearTimeout(mv_node_timeout);
	mv_node_timeout = null;
	if(!mv_nodes||mv_nodes.length<=0||!mv_new_pid)
	{
		mv_nodes=null ;
		mv_new_pid = null ;
		return ;
	}
	let new_pid = mv_new_pid ;
	let mv_nids = mv_nodes.join(',');
	let mv_num = mv_nodes.length ;
	mv_nodes=null ;
	mv_new_pid = null ;
	
	dlg.confirm(`确定要移动\${mv_num}个节点么？`,{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"请确认"},function ()
	{
		let pm = {op:"mv_node",deviceid:deviceid,mv_nids:mv_nids,tar_pid:new_pid};
		dlg.loading(true) ;
		send_ajax("device_tree_ajax.jsp",pm,(bsucc,ret)=>{
			dlg.loading(false) ;
			if(!bsucc||ret.indexOf("succ=")!=0)
			{
				dlg.msg(ret);return ;
			}
			dlg.msg("移动成功节点数="+ret.substring(5)) ;
			refresh_tree();
		}) ;
	},
	function(){//cancel
		refresh_tree();
	});
	
}

function mv_node(mv_nd, old_pid, new_pid)
{
	console.log(mv_nd) ;
	
}

var cur_sel_node = null ;

function on_tree_node_sel(n)
{
	cur_sel_node = n ;
	console.log(n) ;
	let tree_nid="" ;
	if(!n.root)
		tree_nid = n.id ;
	let fwin = FindFrameWin('device_right');
	//console.log(fwin) ;
	if(fwin &&  fwin.set_tree_node)
	{
		  fwin.set_tree_node(treeid,tree_nid,n) ;
		  if(n.root===true)
			  fwin.set_tn_js(n.id,true,n) ;
		  else
			  fwin.set_tn_js(n.id,false,n) ;
	}
		  //fwin.location.href="device_detail.jsp?deviceid="+deviceid+"&nid="+n.id ;
}



function get_sel_tree_nodes()
{
	var tns = $('#device_tree').jstree(true).get_selected(true);
	var rets=[] ;
	for(var tn of tns)
	{
		rets.push(tn.original) ;
	}
	return rets;
}

function refresh_tree()
{
	//jsTree.jstree(true).refresh();
	jsTree.jstree(true).refresh();
}

function on_plug_clk(nid,plugn,pm_url)
{
	stop_event() ;
	if(!pm_url)
		pm_url = "./plugs/plug_"+plugn+".jsp?cuid="+cuid+"&nid="+nid;
	else
	{
		if(pm_url.lastIndexOf("?")>0)
			pm_url += "&cuid="+cuid+"&nid="+nid;
		else
			pm_url += "?cuid="+cuid+"&nid="+nid;
	}
		 
	dlg.show_over_dlg(true,pm_url) ;
}

function add_branch()
{
	
}

function add_edit_tn()
{
	if(!cur_sel_node)
	{
		dlg.msg("请选择一个父节点");
		return ;
	}
	var op="add" ;
	var editt = "新增设备类型" ;

	if(tpid)
	{
		op="update" ;
		editt = "编辑分类" ;
	}
	else
	{
		tpid="" ;
	}
	
	dlg.open("dev_tp_edit.jsp?libn="+lib.n+"&catid="+catid+"&tpid="+tpid,
			{title:editt,w:'500px',h:'400px'},
			['确定','取消'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
		        	     {
							 dlg.msg(ret) ;
							 return ;
		        	     }
						 
						 send_ajax("dev_tp_ajax.jsp",{op:"edit_tp",libn:lib.n,title:ret.title,catid:catid,tpid:tpid},(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 refresh_table() ;
							 dlg.close() ;
						 }) ;
						 
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
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