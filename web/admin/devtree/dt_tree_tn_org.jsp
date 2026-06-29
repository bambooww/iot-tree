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
DTTree tree = DTTreeManager.getInstance().getTreeById(treeid); ;
if(tree==null)
{
	out.println("no device tree found") ;
	return ;
}
DTNode dn = tree ;
//String title  = tree.getTitle() ;

	String tree_nid = request.getParameter("tree_nid") ;
	if(Convert.isNotNullEmpty(tree_nid))
	{
	dn =  tree.findNodeById(tree_nid);
	
		if(dn==null)
		{
			out.print("no tree node found") ;
			return ;
		}
	}
	String title  = dn.getPathTitle() ;
	
	int tn_num = 1;
	String dev_tpid = null;// dn.getDevTpId() ;

	
	//Date last_snap_dt = new Date(device.RT_getRTDevice().RT_getLastCxtSaveDT()) ;
	
	//String last_cxt_save_dt = Convert.toFullYMDHMS(last_snap_dt) ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tree"/>
</jsp:include>
<style type="text/css">
.top {position: absolute;top:0px;left:0px;width:100%;height:30px;background-color: #f2f2f2}
.main {position: absolute;top:31px;left:0px;width:100%;bottom: 0px;overflow: auto;}
.jstree-icon {display:none;}
.tn_icon {font-size: 10px;}
.tn_edit {color:green;}
.tn_more {color:red;}
</style>
<script>
dlg.resize_to(400,620);

</script>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">

<fieldset class="layui-elem-field">
  <legend>节点：<%=title%>

  </legend>
  <div class="layui-field-box">
<table style="width:100%;height:20px;">
	<tr>
		<td></td>
		<td style="padding:5px;">
		</td>
		<td>
<%
if(dn instanceof DTNodeGrp)
{
%>
		<button id="" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_tn('add_sub_grp')" title=""><i class="fa fa-plus" ></i>添加子节点(分组)</button>
		<button id="" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_tn('add_sub_part')" title=""><i class="fa fa-plus" ></i>添加子节点(部件)</button>
<%
}
%>
<button id="" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_tn('edit_node')" title=""><i class="fa fa-plus" ></i>编辑节点</button>

<button id="" class="layui-btn layui-btn-sm layui-btn-primary" onclick="del_tn()" title=""><i class="fa fa-times" ></i>删除节点</button>
<%


if(dn instanceof DTTree)
{
	%>
	<button id="" class="layui-btn layui-btn-sm layui-btn-primary" onclick="back_device()" title=""><i class="fa-regular fa-floppy-disk"></i>备份</button>
	<%
}
%>
<span style="color:#cc6c1d">当前替换部件：<%="" %></span>
<button id="" class="layui-btn layui-btn-sm layui-btn-primary" onclick="replace_devpart()" title=""><i class="fa-solid fa-arrow-right-arrow-left"></i>替换部件</button>


<button id="" class="layui-btn layui-btn-sm layui-btn-primary" onclick="clear_cache()" title=""><i class="fa-solid fa-brush"></i>清除缓存</button>

&nbsp;快照存储时间<span ><%="" %></span>
		</td>
	</tr>
</table>
</div>
</fieldset>

<%
if(dn!=null)
{
%>
<fieldset class="layui-elem-field">
  <legend>应用插件</legend>
  <div class="layui-field-box">

  </div>
</fieldset>
<%
}
%>
 </form>
</body>
<script type="text/javascript">

var treeid="<%=treeid%>";
var tree_nid = "<%=tree_nid%>" ;
var title = "<%=Convert.plainToJsStr(title)%>" ;

var form ;

layui.use(['form', 'laydate', 'util'], function(){
	  form = layui.form;
	  
	  form.render();
});

function back_device()
{
	send_ajax("device_tree_ajax.jsp",{op:"backup_device",deviceid:deviceid},(bsucc,ret)=>{
		dlg.msg(ret) ;
	}) ;
}

function clear_cache()
{
	send_ajax("device_tree_ajax.jsp",{op:"clear_cache",deviceid:deviceid},(bsucc,ret)=>{
		dlg.msg(ret) ;
	}) ;
}

function trigger_refresh_tree()
{
	let fwin = FindFrameWin('device_mid');
	  if(fwin)
		  fwin.refresh_tree() ;
}

function edit_tn(op)
{
		let editt = `在\${title}下新增节点` ;
		let u = "dt_tree_tn_edit.jsp?treeid="+treeid+"&tree_nid="+tree_nid ;
		switch(op)
		{
		case "add_sub_grp":
			editt = `在\${title}下新增分组节点` ;
			u = "dt_tree_tn_edit.jsp?treeid="+treeid+"&tree_nid=" ;
			break ;
		case "add_sub_part":
			editt = `在\${title}下新增部件节点` ;
			u = "dt_tree_tn_edit.jsp?treeid="+treeid+"&tree_nid=" ;
			break ;
		case "edit_node":
			editt = `编辑节点` ;
			break;
		default:
			return ;
		}
		
		dlg.open(u,{title:editt,w:'500px',h:'400px'},
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
							 
							 send_ajax("dt_tree_ajax.jsp",{op:op,treeid:treeid,tree_nid:tree_nid,title:ret.title},(bsucc,ret)=>{
								 if(!bsucc || ret.indexOf("succ")!=0)
								 {
									 dlg.msg(ret) ;
									 return ;
								 }
								 trigger_refresh_tree();
								 dlg.close() ;
								 location.reload() ;
							 }) ;
							 
					 	});
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
}

function del_tn()
{
	if(!tree_nid)
	{
		dlg.msg("请选择节点，根节点除外");return ;
	}
	dlg.confirm('确定要删除此节点?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
		    		send_ajax("dt_tree_ajax.jsp",{op:"del_node",treeid:treeid,tree_nid:tree_nid},(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
								 return ;
							 }
							 trigger_refresh_tree();
							 location.reload() ;
						 }) ;
		});
}

function replace_devpart()
{
	if(!tree_nid)
	{
		dlg.msg("请选择节点，根节点除外");return ;
	}
	dlg.show_over_dlg(true,"/platform/dev_org/dev_part_list.jsp?selmulti=false&selonly=true&dc_id=0&dn_id=-1",{ratio:"50%",h:false,title:"部件选择"},["确定选择","取消替换"],[
		function(dlgw)
		{
			let selpart = dlgw.get_selected_part() ;
			if(!selpart)
			{
				dlg.msg("请选择部件");return;
			}
			let partid = selpart.partid ;
			//dlg.msg(partid)
			send_ajax("device_tree_ajax.jsp",{op:"set_replace_devpart",treeid:treeid,tree_nid:tree_nid,partid:partid},(bsucc,ret)=>{
				 if(!bsucc || ret.indexOf("succ")!=0)
				 {
					 dlg.msg("设置错误:"+ret) ;
					 return ;
				 }
				 dlg.show_over_dlg(false);
				 trigger_refresh_tree();
				 location.reload() ;
			 }) ;
		},
		function(dlgw)
		{
			send_ajax("device_tree_ajax.jsp",{op:"set_replace_devpart",treeid:treeid,tree_nid:tree_nid},(bsucc,ret)=>{
				 if(!bsucc || ret.indexOf("succ")!=0)
				 {
					 dlg.msg("设置错误:"+ret) ;
					 return ;
				 }
				 dlg.show_over_dlg(false);
				 trigger_refresh_tree();
				 location.reload() ;
			 }) ;
		}
	]) ;
	
}

function set_tn_num()
{
	let tnn = get_input_val("tn_num",1,true) ;
	if(tnn<1)
	{
		dlg.msg("数量不能小于1");
		return ;
	}
	send_ajax("device_tree_ajax.jsp",{op:"set_node_num",treeid:treeid,nid:nid,num:tnn},(bsucc,ret)=>{
		 if(!bsucc || ret.indexOf("succ")!=0)
		 {
			 dlg.msg(ret) ;
			 return ;
		 }
		 trigger_refresh_tree();
		 //location.reload() ;
	 }) ;
}


function set_tn_part(set_or_not)
{
	if(!set_or_not)
	{
		dlg.confirm('确定要删除关联部件么?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>confirm</wbt:g>"},function ()
		{
			send_ajax("tn_ajax.jsp",{op:"set_tn_devpart",cuid:cuid,nid:nid,partid:""},(bsucc,ret)=>{
				if(!bsucc || ret!="succ")
				{
					dlg.msg(ret) ;
					return ;
				}
				dlg.show_over_dlg(false) ;
				trigger_refresh_tree();
				location.reload() ;
			}) ;
		});
		return ;
	}
	
	dlg.show_over_dlg(true,"dev_part_main.jsp?selonly=true",{title:"选择部件",ratio:"80%",reload:true},["确定选择"],[
		function(dlgw)
		{
			let selpart = dlgw.get_selected_part() ;
			//console.log(selpart);
			send_ajax("tn_ajax.jsp",{op:"set_tn_devpart",cuid:cuid,nid:nid,partid:selpart.partid},(bsucc,ret)=>{
				if(!bsucc || ret!="succ")
				{
					dlg.msg(ret) ;
					return ;
				}
				dlg.show_over_dlg(false) ;
				trigger_refresh_tree();
				location.reload() ;
			}) ;
			
		}
	]) ;
}


function set_part_setup(partid,tt)
{
	dlg.show_over_dlg(true,"dev_part_setup.jsp?partid="+partid,{ratio:"90%",h:false,title:"部件["+tt+"]详细设置"});
}

function set_plugs()
{
	let pns = [] ;
	$(".plugs").each(function(){
		let ob = $(this);
		if(!ob.prop("checked"))
			return ;
		let pn = ob.attr("plug_n") ;
		pns.push(pn) ;
	}) ;
	
	let pm = {op:"set_dn_plugs",treeid:treeid,nid:nid,pns:pns.join(',')} ;
	send_ajax("device_tree_ajax.jsp",pm,(bsucc,ret)=>{
		 if(!bsucc || ret.indexOf("succ")!=0)
		 {
			 dlg.msg(ret) ;
			 return ;
		 }
		 trigger_refresh_tree();
	  	dlg.msg("设置成功");
	 }) ;
}
</script>
</html>                                                                                                                                                                                                                            