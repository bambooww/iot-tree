<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.res.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	String bkcolor="#eeeeee" ;
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>hmi context editor</title>
<jsp:include page="../head.jsp">
 <jsp:param value="true" name="os3"/>
</jsp:include>
<link href="./inc/1.css" rel="stylesheet" />
<style>

</style>
</head>
<body class="layout-body">
	
		<div class="left " style="overflow: hidden;">
		
			<div id="leftcat_cxt_sub_hmi" onclick="leftcat_sel('cxt_sub_hmis','Context Sub HMI',350)" title="Context Sub-HMI"><i class="fa fa-puzzle-piece fa-3x lr_btn"></i><br>&nbsp;</div>
			<div id="leftcat_comp" onclick="leftcat_sel('comp','HMI Components',500)" title="Components"><i class="fa fa-cogs fa-3x lr_btn"></i><br>&nbsp; </div>
			<div id="leftcat_divcomp" onclick="leftcat_sel('divcomp','Components',330)" title="Controller"><i class="fa fa-cog fa-3x lr_btn"></i><br> &nbsp;</div>

		</div>
<%--		
		<div id="left_panel" class="left_panel_win" pop_width="300px" >
			<div class="left_panel_bar" >
				<span id="left_panel_title" style="font-size: 20px;">Basic Shape</span><div onclick="leftcat_close()" class="top_menu_close"  style="position:absolute;top:1px;right:10px;top:2px;">X</div>
			</div>
			<iframe id="left_pan_iframe" src="" style="width:100%;height:90%;overflow:hidden;margin: 0px;border:0px;padding: 0px" ></iframe>
		</div>
 --%>
		<div class="mid">
			
			<div id="main_panel" style="border: 1px solid #000;margin:0px; width: 100%; height: 100%; background-color: <%=bkcolor %>" ondrop0="drop(event)" ondragover0="allowDrop(event)"></div>
		

<div style="position:absolute;right:5px;top:5px;z-index:1001;color:#1e1e1e" title="show or hide properties panel" id="btn_prop_showhidden">&nbsp;&nbsp;<i class="fa fa-bars fa-lg"></i>&nbsp;&nbsp;</div>
<%--
	<div id='edit_panel'  class="right_panel_win" >
		<div style="height:100px;background-color: grey" class="edit_toolbar">
					<button id="oper_save"  title="save"><i class="fa fa-floppy-disk fa-2x"></i></button>
					<span id="edit_toolbar" class="edit_toolbar"></span>
					<div id="p_info" style="height: 20;display0:none" class0="props_panel_pos">&nbsp;</div>
					</div>
	  
		
			<div class="layui-tab">
		  <ul class="layui-tab-title">
		    <li class="layui-this">Properties</li>
		    <li>Events</li>
		  </ul>
		  <div class="layui-tab-content">
		    <div class="layui-tab-item layui-show">
		    	<div  id='edit_props'  style="width:100%;height:300px;overflow:auto;"></div>
			</div>
		    <div class="layui-tab-item">
		      <div  id='edit_events'  style="width:100%;height:500px;overflow:auto"></div>
			</div>
		   
		  </div>
		</div>
	</div>
 --%>
 </div>
<%--
	<div id="oper_fitwin" class="oper" style="top:10px"><i class="fa fa-crosshairs fa-3x"></i></div>
	<div id="oper_zoomup" class="oper" style="top:60px"><i class="fa-regular fa-square-plus fa-3x"></i></div>
	<div id="oper_zoomdown" class="oper" style="top:110px"><i class="fa-regular fa-square-minus fa-3x"></i></div>

	<div id="toolbar_basic" class="toolbox">
				<div class="title" style="float:left;width:100%"><i class="fa fa-wrench fa-2x" aria-hidden="true" onclick="show_hide_toolbar()"></i></div>
		<div id="toolbar_list" class="content" style="height:230px"> 
			<iframe src="hmi_left_basic_di.jsp" height="230px" width="100%"></iframe>
		</div>
	</div>
--%>
<div id="prompt_p" class="prompt_p"></div>
<script>

toolbox_init("#toolbar_basic");


layui.use('element', function(){
	layuiEle = layui.element;
  
  //…
});

</script>
</body>
</html>