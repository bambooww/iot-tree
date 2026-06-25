<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	org.iottree.core.devtree.*,
	java.util.*"%><%!
		
%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%

String treeid = request.getParameter("treeid") ;
if(treeid==null)
	treeid = "" ;

if(Convert.isNotNullEmpty(treeid))
{
	DTTree tree = DTTreeManager.getInstance().getTreeById(treeid) ;
	if(tree==null)
	{
		out.print("no device tree found with id="+treeid) ;
		return ;
	}
}

%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
    <style>
        .layui-form-label{
            width: 120px;
        }
        .layui-input-block {
            margin-left: 140px;
            min-height: 36px;
            width:240px;
        }
        .layui-table-view
        {
        	margin-top: 3px;
        }
        .tab_js
        {
        	display:none;
        }
    </style>
</head>
<body>
<div class="layui-tab layui-tab-brief" lay-filter="device_tab" >
  <ul class="layui-tab-title" id="tab_hd">
<%

%>  
  	<li id="tab_tn_org" tabnn="tn_org" class="layui-this" >组织层次</li>
    <li id="tab_device_tn_prop_s" tabnn="device_tn_prop_s">设备运行属性</li>
    <li id="tab_device_tn_runblkr_s" tabnn="device_tn_runblkr_s" >设备运行模块</li>
    <li id="tab_tn_parts" tabnn="tn_parts">设备部件</li>
    <li id="tab_tn_runblks" tabnn="tn_runblks">节点运行模块</li>
    <li id="tab_tn_prop" tabnn="tn_prop">节点属性</li>

     <li id="tab_edef_set" tabnn="edef_set" >故障定义</li>
    <li id="tab_edef_model" tabnn="edef_model" tabsrc="">故障模型</li>
<%

%>
<%--
     <li>设备运行调试</li>
     <li>关联故障定义</li>
<li>设备故障绑定</li>
属性
	for(DNPlug plug:plugs)
	{
		if(!plug.supportPlugParamUI())
	continue ;
%><li><%=plug.getPlugTitle()%></li><%
	}
--%>
  </ul>
  <div class="layui-tab-content" style="height:600px;" id="tab_bd">

  <div class="layui-tab-item layui-show" style="height:100%">
     	<iframe class="if_in_tab" id="if_tn_org"  width="100%" height="100%" style="height:100%;border: 0"></iframe>
     	
       </div>
       <div class="layui-tab-item" style="height:100%">
     	<iframe class="if_in_tab" id="if_device_tn_prop_s"  width="100%" height="100%" style="height:100%;border: 0"></iframe>
       </div>
       
  	<div class="layui-tab-item " style="height:100%;">
  	<iframe class="if_in_tab" id="if_device_tn_runblkr_s"  width="100%" height="100%" style="height:100%;border: 0"></iframe>
     
    </div>
    <div class="layui-tab-item" style="height:100%">
      <iframe class="if_in_tab" id="if_tn_parts"  width="100%" height="100%" style="height:100%;border: 0"></iframe>
    </div>
    
    
    <div class="layui-tab-item" style="height:100%">
     	<iframe class="if_in_tab" id="if_tn_runblks"  width="100%" height="100%" style="height:100%;border: 0"></iframe>
       </div>
    <div class="layui-tab-item" style="height:100%">
      <iframe class="if_in_tab" id="if_tn_prop"  width="100%" height="100%" style="height:100%;border: 0"></iframe>
    </div>
  	<div class="layui-tab-item" style="height:100%;position: relative;" id="">
		<iframe class="if_in_tab" id="if_edef_set"  width="100%" height="100%" style="height:100%;border: 0" src=""></iframe>
    </div>
   
    <div class="layui-tab-item" style="height:100%">
      <iframe class="if_in_tab" id="if_edef_model"  width="100%" height="100%" style="height:100%;border: 0" src=""></iframe>
    </div>

     

  </div>
</div>
</body>
<script>
var element ;

var cur_tabnn = "tn_org" ;

layui.use('element', function(){
	
  element = layui.element;
  element.on('tab(device_tab)', function (data) {
    let ele = $(this);
    let tabnn = ele.attr("tabnn") ;
    let tabsrc = ele.attr("tabsrc") ;
    if(!tabnn)
    	return ;
    cur_tabnn = tabnn ;
    update_cur_tab();
  });
});

function update_cur_tab()
{
	if(!cur_tabnn) return ;
	let src = $("#tab_"+cur_tabnn).attr("tabsrc") ;
	if(!src) return ;
	let oldsrc = $("#if_"+cur_tabnn).attr("src");
	if(oldsrc!=src)
		 $("#if_"+cur_tabnn).attr("src",src) ;
}

function set_tree_node(treeid,tree_nid,nd)
{
	//console.log(nd) ;
	$("#tab_tn_org").attr("tabsrc","dt_tree_tn_org.jsp?treeid="+treeid+"&tree_nid="+tree_nid) ;
	/*
	$("#tab_tn_prop").attr("tabsrc","tn_prop.jsp?cuid="+cuid+"&tree_nid="+tree_nid) ;
	$("#tab_tn_parts").attr("tabsrc","tn_parts.jsp?cuid="+cuid+"&tree_nid="+tree_nid) ;
	$("#tab_tn_runblks").attr("tabsrc","tn_runblks.jsp?cuid="+cuid+"&tree_nid="+tree_nid) ;
	$("#tab_device_tn_runblkr_s").attr("tabsrc","tn_runblk_runners.jsp?cuid="+cuid+"&tree_nid="+tree_nid) ;
	$("#tab_device_tn_prop_s").attr("tabsrc","tn_props_runner.jsp?cuid="+cuid+"&tree_nid="+tree_nid) ;
	
	$("#tab_edef_set").attr("tabsrc", `../dev_err/dev_err_def_set.jsp?cuid=\${cuid}&tree_nid=\${tree_nid}`) ;
	$("#tab_edef_model").attr("tabsrc",`../ml/device_edef_kgedit.jsp?cuid=\${cuid}&tree_nid=\${tree_nid}`) ;
	*/
	update_cur_tab();
	
}

function set_tn_js(nid,b_show,nd)
{
	//console.log(nd) ;
	//$("#if_def_set")[0].contentWindow.on_tn_sel(nd) ;
}


function reset_height()
{
	var h = $(window).height()-55;
	$(".if_in_tab").css("height",h+"px");
	//$(".layui-tab").css("height",h+"px");
}

$(window).resize(function () {
	reset_height();
});

reset_height() ;
//refresh_ui() ;
</script>
</html>