<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.cxt.*,
	org.iottree.core.ws.*,
	org.iottree.core.sim.*,
	org.iottree.pro.*,org.iottree.core.station.*,
	org.iottree.core.util.xmldata.*
"%><%@ taglib uri="wb_tag" prefix="wbt"%><%//UserProfile up = UserProfile.getUserProfile(request);
//String un = up.getUserInfo().getFullName();
	//if(PlatformManager.isInPlatform())
	//{
	//	response.sendRedirect("platform.jsp");
	//	return ;
	//}
List<UAPrj> prjs = UAManager.getInstance().listPrjs();
String using_lan = Lan.getUsingLang() ;
String sname = "Server";
if(PlatformManager.isInPlatform())
	sname = "Platform" ;
//UAContext.getOrLoadJsApi() ;
%><!DOCTYPE html>
<html class="">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1">
    <title>IOT-Tree</title>
    <link href="/favicon.ico" rel="shortcut icon" type="image/x-icon">
<jsp:include page="head.jsp"/>
            <link href="./inc/common.css" rel="stylesheet" type="text/css">
        <link href="./inc/index.css" rel="stylesheet" type="text/css">
 <style>
 .btn_sh
 {
  //display:none;
  visibility: hidden;
 }
 
 .btn_sh_c:hover .btn_sh
 {
visibility: visible;
 }

.fz a
{
background:#aaaaaa; 
}

.top_lan
{
	position: absolute;
	right:10px;
	top:15px;
	width:100px;
}

.lib_item
{
	height:30px;
	border:1px solid;
	border-color: #499ef3;
	margin:2px;
	white-space: nowrap;
	display:inline-block;
	padding:2px;
}

.mod-head .tt
{
	position: relative;
}
.mod-head .op
{
	position:absolute;
	right:10px;
	height:25px;
	width:25px;
	cursor:pointer;
	border:1px solid;
	border-color: #499ef3;
}


.sor_item
{
	position:relative;
	left:2%;
	width:95%;
	height:50px;
	border:1px solid;
	border-color: #499ef3;
	color:#0061aa;
	margin-bottom: 10px;
}
.sor_item .n
{
	position:absolute;
	font-size: 16px;
	left:5px;
}
.sor_item .tp
{
	position:absolute;
	font-size: 15px;
	bottom:3px;
	left:10px;
}
.sor_item .oper
{
	position:absolute;
	font-size: 15px;
	right:3px;
	visibility:hidden;
	bottom:5px;
}
.sor_item:hover .oper{
	visibility:visible;
}
 </style>
</head>
<body aria-hidden="false">
	<div class="iot-top-menu-wrap">
		<div class="container">
			<!-- start logo -->
			<div class="iot-logo">
				<a><img src="inc/logo1.png" width="40px" height="40px"/> IOT-Tree <%=sname %></a>
			</div>
			<div class="top_lan">
	 <button class="layui-btn layui-btn-primary layui-btn-xs  <%=("en".equals(using_lan)?"layui-btn-normal":"") %>" onclick="chg_lan('en')">EN</button>
	 <button class="layui-btn layui-btn-primary layui-btn-xs <%=("cn".equals(using_lan)?"layui-btn-normal":"") %>" onclick="chg_lan('cn')">CN</button>
	</div>
			<!-- end logo -->
			<!-- start nav -->
			<div class="iot-top-nav navbar">
				<div class="navbar-header">
				  <button class="navbar-toggle pull-left">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				  </button>
				</div>
				<nav role="navigation" class="collapse navbar-collapse bs-navbar-collapse">
				  <ul class="nav navbar-nav">
					  <li><a href="https://github.com/bambooww/iot-tree.git"  target="_blank" class=""><i class="icon icon-home"></i><wbt:lang>home</wbt:lang></a></li>
					  <li><a href="/doc" target="_blank"><i class="icon icon-topic"></i><wbt:lang>helper</wbt:lang></a></li>
					  <li><a href="mailto:iottree@hotmail.com"  ><i class="icon icon-topic"></i><wbt:lang>feedback</wbt:lang></a></li>
				  </ul>
				</nav>
			</div>
			<!-- end nav-->
			
			<!-- search 
			<div class="iot-search-box  hidden-xs hidden-sm">
				<form class="navbar-search" id="global_search_form" method="post">
					<input class="form-control search-query" type="text" placeholder="" autocomplete="off" name="q" id="iot-search-query">
					
				</form>
			</div>
			end search -->

			<div class="iot-user-nav">
					<div class="iot-top-user"><a class="login" href="javascript:logout()"><wbt:lang>logout</wbt:lang></a>
					
<%
List<IPro> pros = ProManager.getInstance().listPros() ;
if(pros.size()>0)
{
%>
&nbsp;&nbsp;&nbsp;<a class="login" href="javascript:pro_mgr()">Pro</a>
<%
}
%></div>
			</div>

			
		</div>
	</div>
	
	

<div class="iot-container-wrap" style="height: auto !important;">
		<div class="container" style="height: auto !important;">
		<div class="row" style="height: auto !important;">
			<div class="iot-content-wrap clearfix" style="height: auto !important;">
				<div class="iot-main-content" style="height: auto !important; min-height: 0px !important;">
					<div class="iot-mod iot-question-detail iot-item">
						<div class="mod-head">
							<h1><wbt:lang>local_prj</wbt:lang></h1>
							
							<div style="float:left;top:5px;position: absolute;left:160px" >
							<a class0="btn btn-success" style0="width:80px;height:40px;align-content: center;" href="javascript:add_prj()">
							
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-plus fa-stack-1x fa-inverse"></i>
							</span>&nbsp;<wbt:lang>add</wbt:lang>
							</a>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<a class0="btn btn-success"  style0="width:100px;height:40px;" href="javascript:imp_prj()">
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x fa-inverse"></i>
							</span>&nbsp;<wbt:lang>import</wbt:lang>
							 <input type="file" id='add_file' onchange="add_file_onchg()" name="file" style="left:-9999px;position:absolute;" accept=".zip"/>
							</a>
&nbsp;&nbsp;&nbsp;&nbsp;
							<a class0="btn btn-success"  style0="width:100px;height:40px;" href="javascript:imp_prj_demo()">
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x fa-inverse"></i>
							</span>&nbsp;<wbt:lang>import</wbt:lang> Demo
							</a>
							
							<%--
							&nbsp;&nbsp;&nbsp;&nbsp;
							<a class0="btn btn-success"  style0="width:100px;height:40px;" href="javascript:exp_prj()">
							<span class="fa-stack">
							  <i class="fa fa-square-o fa-stack-2x"></i>
							  <i class="fa fa-arrow-up fa-stack-1x"></i>
							</span>&nbsp;Export
							</a>
							 --%>
							</div>
				           
						</div>
						<div class="mod-body">
							<div class="content markitup-box" style="height:200px:overflow:auto">
<%
	int cc = 0 ;
	for(UAPrj rep:prjs)
{
		cc ++ ;
		String cssstr = "" ;
		String tmpid = "" ;
		if(cc>6)
		{
			tmpid = "div_prj_"+rep.getId() ;
			cssstr = "display:none";
		}
%>
	<div class="aw-item btn_sh_c" id="<%=tmpid%>" style="<%=cssstr%>">
	   
       <a class="img aw-border-radius-5" >
         <i class="fa fa-sitemap fa-1x"></i>
       </a>
       <a class="text title" href="javascript:open_rep('<%=rep.getId()%>')" data-id="8" title="<%=rep.getName() %>"><%=rep.getTitle() %></a>
       <div class="inline-block pull-right text-left ">
          <span class="btn_sh">
          <span>
           <a class=" " href="javascript:window.open('/<%=rep.getName()%>?op=ui')" title="Access">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-paper-plane  fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           </span>
<%
if(!rep.isMainPrj())
{
%>
          <span>
           <a class=" " href="javascript:set_prj_main('<%=rep.getId()%>')" title="set as main">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa-thin fa-m  fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           </span>
<%
}
if(rep.isAutoStart())
{
%><a class=" " href="javascript:set_prj_auto_start('<%=rep.getId()%>',false)" title="unset auto start">
<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa-thin fa-a  fa-stack-1x fa-inverse"></i>
							</span>
</a><%
}
else
{
%><a class=" " href="javascript:set_prj_auto_start('<%=rep.getId()%>',true)" title="set auto start">
<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa-thin fa-a  fa-stack-1x fa-inverse"></i>
							</span>
</a><%
}
%>
           <a class0="btn btn-success download-btn white" href="javascript:open_rep('<%=rep.getId()%>')" title="show detail">
              <span class="fa-stack fa-1x">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-pencil  fa-stack-1x fa-inverse"></i>
							</span>
           </a>

           <a href="javascript:exp_prj('<%=rep.getId()%>')" title="export">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-up fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           <a class0="btn btn-success " style="color: #e33a3e" href="javascript:del_rep('<%=rep.getId()%>')" title="delete">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-times fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           </span>
           
           <div style="width:150px;border:0px solid;float: right">&nbsp;
<%
if(rep.isMainPrj())
{
%>&nbsp;&nbsp;<span class="layui-badge layui-bg-blue"><wbt:lang>main_prj</wbt:lang></span><%
}

if(rep.isAutoStart())
{
%>&nbsp;&nbsp;<span class="layui-badge layui-bg-blue"><wbt:lang>auto_start</wbt:lang></span><%
}
%>
         </div>
       </div>

       <div class="text-color-999">
           <span class="text-color-666">&nbsp;&nbsp;&nbsp;[<%=rep.getName() %>]</span>
           <span class="text-color-666" style="left:200px;position: absolute;">•<wbt:lang>modified_date</wbt:lang>:<%=Convert.toFullYMDHMS(new Date(rep.getSavedDT())) %></span>
       </div>
   </div>
<%
}
	
	if(cc>6)
	{
%>
<div style="border:0px solid #ffff00;height:45px;text-align:right;padding-right:30px;"><a id="more_prj_show"  onclick="more_prj()"><wbt:lang>more_prjs</wbt:lang>...</a></div>
<%
	}
%>

							</div>
						</div>
						<%--
						<div class="mod-footer">
							<div class="meta">
																
								
								<div class="pull-right more-operate">
									<a class="text-color-999 dropdown-toggle" data-toggle="dropdown">
										<i class="icon icon-share"></i>bottom									</a>
									<div aria-labelledby="dropdownMenu" role="menu" class="iot-dropdown shareout pull-right">
										<ul class="iot-dropdown-list">
											<li><a ><i class="icon icon-weibo"></i>act1</a></li>
											<li><a ><i class="icon icon-qzone"></i> act2</a></li>
											<li><a ><i class="icon icon-wechat"></i> act3</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>
						 --%>
					</div>
					
					<div class="iot-mod iot-question-detail iot-item">
					    <div class="mod-head">
					        <h1><wbt:lang>dev_lib</wbt:lang></h1>
					        
					        <div style="float:left;top:5px;position: absolute;left:210px" >
					        	<a href="javascript:dev_lib_import()">
					        	<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x fa-inverse"></i>
							</span>&nbsp;&nbsp; <wbt:lang>import</wbt:lang>
							<input type="file" id='devlib_add_file' onchange="devlib_add_file_onchg()" name="devlib_file" style="left:-9999px;position:absolute;" accept=".zip"/>
							</a>
<%--
					        	&nbsp;&nbsp;&nbsp;&nbsp;
					        	<a class0="btn btn-success"  style="width:100px;height:40px;" href="javascript:devdef_cat_export()">
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-up fa-stack-1x fa-inverse"></i>
							</span>&nbsp;&nbsp;Export
							</a>
 --%>							
							&nbsp;&nbsp;&nbsp;&nbsp;
					        	<a  title="device library help" style="width:100px;height:40px;" href="/doc/en/quick/quick_know_devlib.md" target="_blank">
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-question fa-stack-1x fa-inverse"></i>
							</span>
							</a>
							
					        </div>
<%--
					        <div style="float:right;top:0px;position: absolute;right:10px" onclick="show_hide('cont_devlib')"><i class="fa fa-bars fa-lg"></i></div>
					         --%>
					    </div>
					   <div class="mod-body">
							
							<%
	
	for(DevLib lib:DevManager.getInstance().getDevLibs())
{
		cc ++ ;
		String cssstr = "" ;
		String tmpid = "" ;
		
			tmpid = "div_lib_"+lib.getId() ;
		
%>
<%--
<span class="text-color-666"><%=Convert.toFullYMDHMS(new Date(lib.getCreateDT())) %></span>
 --%>
	<span class="lib_item btn_sh_c" >
		<img src="./inc/sm_icon_dev.png"/> &nbsp;<a class="text title" href="javascript:open_devlib('<%=lib.getId()%>')" data-id="8"><%=lib.getTitle() %></a>
		
		
		<span class="btn_sh">

           <a class0="btn btn-success download-btn white" href="javascript:devlib_add_or_edit('<%=lib.getId()%>')" title="show detail">
              <span class="fa-stack fa-1x">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-pencil  fa-stack-1x fa-inverse"></i>
							</span>
           </a>

           <a href="javascript:devlib_export('<%=lib.getId()%>')" title="export">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-up fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           <a class0="btn btn-success " style="color: #e33a3e" href="javascript:devlib_del('<%=lib.getId()%>')" title="delete">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-times fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           </span>
           
	</span>
<%
}
%>
	<span class="lib_item" onclick="devlib_add_or_edit()"><i class="fa-solid fa-plus fa-lg"></i></span>
				
						</div>
					</div>
					
					<div class="iot-mod iot-question-detail iot-item">
					    <div class="mod-head">
					        <h1 style="width:200px"><wbt:lang>hmi_lib</wbt:lang></h1>
					        
					        <div style="float:left;top:5px;position: absolute;left:210px" >
					        	<a href="javascript:comp_lib_import()"><span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x fa-inverse"></i>
							</span>&nbsp;&nbsp; <wbt:lang>import</wbt:lang>
							<input type="file" id='complib_add_file' onchange="complib_add_file_onchg()" name="devlib_file" style="left:-9999px;position:absolute;" accept=".zip"/>
							</a>
							<%--
							&nbsp;&nbsp;&nbsp;&nbsp;
					        	<a href="javascript:comp_cat_export()"><span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-up fa-stack-1x fa-inverse"></i>
							</span> Export</a>
							 --%>
							&nbsp;&nbsp;&nbsp;&nbsp;
					        	<a  title="git help" style="width:100px;height:40px;" href="/doc/en/quick/quick_know_hmilib.md" target="_blank">
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-question fa-stack-1x fa-inverse"></i>
							</span>
							</a>
							
					        </div>
					        <%--
					        <div style="float:right;top:5px;position: absolute;right:10px" onclick="show_hide('cont_hmilib')"><i class="fa fa-bars fa-lg"></i></div>
					         --%>
					    </div>
					    <div class="mod-body"  id="cont_hmilib" style0="display:none">
					    <%-- 
					        <iframe id="comp_lister" src="/admin/ua_hmi/hmi_left_comp.jsp" style="width:100%;height:500px;"></iframe>
					        --%>
					        <%
	
	for(CompLib lib:CompManager.getInstance().getCompLibs())
{
		cc ++ ;
		String cssstr = "" ;
		String tmpid = "" ;
		
			tmpid = "div_lib_"+lib.getId() ;
		
%>
<%--
<span class="text-color-666"><%=Convert.toFullYMDHMS(new Date(lib.getCreateDT())) %></span>
 --%>
	<span class="lib_item btn_sh_c" >
		<i class="fa-solid fa-puzzle-piece"></i> &nbsp;<a class="text title" href="javascript:open_complib('<%=lib.getId()%>')" data-id="8"><%=lib.getTitle() %></a>
		
		
		<span class="btn_sh">

           <a class0="btn btn-success download-btn white" href="javascript:complib_add_or_edit('<%=lib.getId()%>')" title="show detail">
              <span class="fa-stack fa-1x">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-pencil  fa-stack-1x fa-inverse"></i>
							</span>
           </a>

           <a href="javascript:complib_export('<%=lib.getId()%>')" title="export">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-up fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           <a class0="btn btn-success " style="color: #e33a3e" href="javascript:complib_del('<%=lib.getId()%>')" title="delete">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-times fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           </span>
           
	</span>
<%
}
%>
	<span class="lib_item" onclick="complib_add_or_edit()"><i class="fa-solid fa-plus fa-lg"></i></span>
				
					    </div>
					</div>
					
					<div class="iot-mod iot-question-detail iot-item">
					    <div class="mod-head">
					        <h1 style="width:200px"><wbt:lang>simulator</wbt:lang></h1>
					        
					        <div style="float:left;top:5px;position: absolute;left:210px" >
					        <a class0="btn btn-success" style0="width:80px;height:40px;align-content: center;" href="javascript:add_or_edit_simins()">
							
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-plus fa-stack-1x fa-inverse"></i>
							</span>&nbsp;<wbt:lang>add</wbt:lang>
							</a>&nbsp;&nbsp;&nbsp;&nbsp;
							
					        	<a href="javascript:sim_ins_import()"><span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x fa-inverse"></i>
							</span>&nbsp;&nbsp; <wbt:lang>import</wbt:lang></a>
							<input type="file" id='add_sim_file' onchange="add_sim_file_onchg()" name="file" style="left:-9999px;position:absolute;" accept=".zip"/>
							&nbsp;&nbsp;&nbsp;&nbsp;
							<a class0="btn btn-success"  href="javascript:imp_simins_demo()">
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x fa-inverse"></i>
							</span>&nbsp;<wbt:lang>import</wbt:lang> Demo
							</a>
							&nbsp;&nbsp;&nbsp;&nbsp;
					        	<a title="simulator help" style="width:100px;height:40px;" href="/doc/en/sim/index.md" target="_blank">
							<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-question fa-stack-1x fa-inverse"></i>
							</span>
							</a>
							
					        </div>
					        
					        <div style="float:right;top:5px;position: absolute;right:10px" onclick="show_hide('cont_sim')"><i class="fa fa-bars fa-lg"></i></div>
					    </div>
					    <div class="mod-body"  id="cont_sim" style0="display:none">
							<div class="content markitup-box" style="height:200px:overflow:auto">
<%
	List<SimInstance> inss = SimManager.getInstance().getInstances();
	for(SimInstance ins:inss)
{
		String cssstr = "" ;
		String tmpid = "div_simins_"+ins.getId() ;
%>
	<div class="aw-item btn_sh_c" id="<%=tmpid%>" >
	   
       <a class="img aw-border-radius-5" >
         <i class="fa fa-sitemap fa-1x"></i>
       </a>
       <a class="text title" href="javascript:open_simins('<%=ins.getId()%>')" data-id="8"><%=ins.getTitle() %></a>
       <div class="inline-block pull-right text-left ">

          
          <span class="btn_sh">

           <a class0="btn btn-success download-btn white" href="javascript:add_or_edit_simins('<%=ins.getId()%>')" title="show detail">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-pencil fa-stack-1x fa-inverse"></i>
							</span>
           </a>
<%
if(ins.isAutoStart())
{
%><a class=" " href="javascript:set_simins_auto_start('<%=ins.getId()%>',false)" title="unset auto start">
<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa-thin fa-a  fa-stack-1x fa-inverse"></i>
							</span>
</a><%
}
else
{
%><a class=" " href="javascript:set_simins_auto_start('<%=ins.getId()%>',true)" title="set auto start">
<span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa-thin fa-a  fa-stack-1x fa-inverse"></i>
							</span>
</a><%
}
%>
           
           <a class0="btn btn-success"  href="javascript:exp_simins('<%=ins.getId()%>')" title="export">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa-arrow-up fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           <a class0="btn btn-success " style="color: #e33a3e" href="javascript:del_simins('<%=ins.getId()%>')" title="delete">
               <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-times fa-stack-1x fa-inverse"></i>
							</span>
           </a>
           
           </span>
           
           <div style="width:150px;border:0px solid;float: right">&nbsp;
<%
if(ins.isAutoStart())
{
%>&nbsp;&nbsp;<span class="layui-badge layui-bg-blue"><wbt:lang>auto_start</wbt:lang></span><%
}
%>
         </div>
       </div>

       <div class="text-color-999">
           <span class="text-color-666">&nbsp;&nbsp;&nbsp;</span>
           • <wbt:lang>modified_date</wbt:lang>:<span class="text-color-666"><%=Convert.toFullYMDHMS(new Date(ins.getSavedDT())) %></span>
       </div>
   </div>
<%
}
%>
							</div>
						</div>
					</div>
					
				</div>
				<!--right side -->
				<div class="iot-side-bar">
					<div class="iot-mod iot-text-align-justify">
					    <div class="mod-head">
					        <h3><wbt:lang>services</wbt:lang></h3>
					    </div>
					    <div class="mod-body fz">
					       
					       <a href="javascript:service_setup()"><wbt:lang>setup</wbt:lang></a>
					        
					    </div>
					</div>
					
					<div class="iot-mod iot-text-align-justify">
					    <div class="mod-head">
					        <h3><wbt:lang>num_of_sess</wbt:lang></h3>
					    </div>
					    <div class="mod-body fz">
					       <%=WSServer.getSessionNum() %>
					    </div>
					</div>

					<div class="iot-mod iot-text-align-justify">
					    <div class="mod-head">
					        <span class="tt"><wbt:lang>data_sor</wbt:lang></span>
					        <span class="op" onclick="add_sor_sel()"><i class="fa-solid fa-plus fa-lg"></i></span>
					    </div>
					    <div class="mod-body fz" id="data_sor_list">
					        
					    </div>
					    
					</div>

					<div class="iot-mod iot-text-align-justify">
					    <div class="mod-head">
					        <h3><wbt:lang>plugins_dict</wbt:lang></h3>
					    </div>
					    <div class="mod-body fz">
					    	<a href="javascript:open_plugins()" ><wbt:lang>plugins</wbt:lang></a>&nbsp;
					    	<a href="javascript:open_ext_prop()" ><wbt:lang>g_dict</wbt:lang></a>
					    	
					    </div>
					</div>

					<div class="iot-mod iot-text-align-justify">
					    <div class="mod-head">
					        <h3><wbt:lang>others</wbt:lang></h3>
					    </div>
					    <div class="mod-body fz">
					       <a href="javascript:log_ctrl()" ><wbt:lang>log_ctrl</wbt:lang></a>
					       <a href="javascript:conn_platform()" ><wbt:lang>conn_platform</wbt:lang></a>
					     </div>
					</div>
				</div>
				<!-- end 侧边栏 -->
			</div>
		</div>
	</div>
</div>

<div class="iot-footer-wrap">
	<div class="iot-footer">
		Copyright:  Version:<%=Config.getVersion() %>

	</div>
</div>
</body>
<script type="text/javascript">

layui.use(['form'], function(){
	  var form = layui.form;
	  
	  form.render() ;
});

var all_panels=[];
function open_rep(id)
{
	window.open("prj_editor.jsp?id="+id);
	//window.open("ua_rep.jsp?repid="+id);
}

function more_prj()
{
	var ps =$("#more_prj_show");
	var bshow = ps.attr("b_show")=="1" ;
	if(bshow)
	{
		$("div [id^='div_prj_']").css("display","none") ;
		ps.html("more project...");
		ps.attr("b_show","0") ;
	}
	else
	{
		$("div [id^='div_prj_']").css("display","") ;
		ps.html("^^^");
		ps.attr("b_show","1") ;
	}
	
}

function set_prj_main(id)
{
	send_ajax('ua/prj_ajax.jsp',{op:"main",id:id},function(bsucc,ret){
		if(!bsucc||ret!='ok')
		{
			dlg.msg(ret) ;
			return ;
		}
		location.reload();
	});
}

function set_prj_auto_start(id,b)
{
	send_ajax('ua/prj_ajax.jsp',{op:"auto_start",id:id,auto_start:b},function(bsucc,ret){
		if(!bsucc||ret!='ok')
		{
			dlg.msg(ret) ;
			return ;
		}
		location.reload();
	});
}

function set_simins_auto_start(id,b)
{
	send_ajax('sim/sim_ajax.jsp',{op:"auto_start",insid:id,auto_start:b},function(bsucc,ret){
		if(!bsucc||ret!='ok')
		{
			dlg.msg(ret) ;
			return ;
		}
		location.reload();
	});
}



function show_hide(id)
{
	var ob = $("#"+id) ;
	if(ob.css("display")=='none')
		ob.css("display","") ;
	else
		ob.css("display",'none') ;
}

function del_rep(id)
{
	dlg.confirm("<wbt:lang>del_sure</wbt:lang>",{btn:["<wbt:lang>yes</wbt:lang>","<wbt:lang>cancel</wbt:lang>"],title:"<wbt:lang>del_confirm</wbt:lang>"},function ()
    {
		send_ajax('ua/prj_ajax.jsp',{op:"del",id:id},function(bsucc,ret){
			if(!bsucc||ret!='ok')
			{
				dlg.msg(ret) ;
				return ;
			}
			location.reload();
		});
     });
}

function open_devlib(id)
{
	if(!id)
		id = "" ;
	dlg.open_win("dev/dev_main.jsp?edit=true&libid="+id,
			{title:"<wbt:lang>dev_lib</wbt:lang>",w:'1000',h:'560'},
			[{title:'<wbt:lang>close</wbt:lang>',style:"primary"},{title:'<wbt:lang>help</wbt:lang>',style:"primary"}],
			[
				function(dlgw)
				{
					dlg.close();
				},
				function(dlgw)
				{
					dlg.msg("help is under dev");
				}
			]);
}
function add_prj()
{
	dlg.open("ua/prj_edit.jsp",
			{title:"<wbt:lang>add_prj</wbt:lang>",w:'500px',h:'400px'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 //enable_btn(true);
							 return;
						 }
						 
						ret.op="add" ;
						send_ajax('./ua/prj_ajax.jsp',ret,function(bsucc,rr)
						{
							if(!bsucc || rr.indexOf('succ')<0)
							{
								dlg.msg(rr);
								return ;
							}
							 dlg.close();
							 location.reload();
						},false);
						
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function comp_cat_export()
{
	var w = document.getElementById("comp_lister").contentWindow ;
	if(!w||!w.get_sel_cat_ids)
	{
		dlg.msg("no comp cat selected") ;
		return ;
	}
	var catids = w.get_sel_cat_ids() ;
	if(catids==null||catids.length==0)
	{
		dlg.msg("please select component catetory");
		return ;
	}
		
	window.open("./ua_hmi/comp_lib_export.jsp?catid="+catids[0]) ;
}

function comp_cat_import()
{
	
}

function exp_prj(id)
{
	window.open("./ua/prj_export.jsp?id="+id) ;
}

function open_plugins()
{
	window.open("./ua/plugins.jsp") ;
}

function add_file_onchg()
{
	//$("#"+id).
	var fs = $("#add_file")[0].files ;
	if(fs==undefined||fs==null||fs.length<=0)
	{
		return ;
	}
	var f = fs[0];

	//upload
	var fd = new FormData();
    //fd.append("cxtid",cur_cxtid) ;
    fd.append("file",f);
     $.ajax({"url": "ua/prj_imp_upload.jsp",type: "post","processData": false,"contentType": false,
		"data": fd,
		success: function(data)
       	{
 	  		//dlg.msg(data);
 	  		//location.reload();
 	  		if(data.indexOf("succ=")==0)
 	  			before_imp(data.substring(5)) ;
 	  		else
 	  			dlg.msg(data) ;
   　  },
      　error: function(data)
         {
  				dlg.msg("upload failed");
　　　}
  　　});
}


function before_imp(tmpfn,bdemo)
{
	dlg.open("ua/prj_import.jsp?tmpfn="+tmpfn+"&demo="+bdemo,
			{title:"<wbt:lang>imp_prj</wbt:lang>",w:'500px',h:'400px'},
			['<wbt:lang>do_imp</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 //enable_btn(true);
							 return;
						 }
						 //console.log(ret);
						 dlg.close();
						 location.reload();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}



function imp_prj()
{
	add_file.click() ;
}

function imp_prj_demo()
{
	dlg.open("ua/prj_demo_list.jsp",
			{title:"<wbt:lang>imp_demo_prj</wbt:lang>",w:'500px',h:'400px'},
			['<wbt:lang>select</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 //console.log(ret);
						 dlg.close();
						 //location.reload();
						 before_imp(ret,true);
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function imp_simins_demo()
{
	dlg.open("sim/sim_demo_list.jsp",
			{title:"<wbt:lang>imp_demo_sim</wbt:lang>",w:'500px',h:'400px'},
			['<wbt:lang>select</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 send_ajax("./sim/sim_ajax.jsp",{op:"imp_demo",fn:ret},(bsucc,ret)=>{
							 if(!bsucc||ret!='succ')
							{
								 dlg.msg(ret) ;
								 return ;
							}
							dlg.close();
							location.reload();
						 });
						
						 
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function devlib_export(libid)
{
	window.open("./dev/lib_export.jsp?libid="+libid) ;
}

function dev_lib_import()
{
	devlib_add_file.click();
}


function devlib_before_imp(tmpfn)
{

	dlg.open("dev/lib_import.jsp?tmpfn="+tmpfn,
			{title:"<wbt:lang>imp_dev_lib</wbt:lang>",w:'500px',h:'400px'},
			['<wbt:lang>do_imp</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 //enable_btn(true);
							 return;
						 }
						 //console.log(ret);
						 dlg.msg(ret) ;
						 
						 //var w = document.getElementById("devdef_lister").contentWindow ;
						// w.drv_sel_chg();
						 dlg.close();
						 location.reload();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function devlib_add_file_onchg()
{
	//$("#"+id).
	var fs = $("#devlib_add_file")[0].files ;
	if(fs==undefined||fs==null||fs.length<=0)
	{
		return ;
	}
	var f = fs[0];

	//upload
	var fd = new FormData();
    //fd.append("cxtid",cur_cxtid) ;
    fd.append("file",f);
     $.ajax({"url": "dev/lib_imp_upload.jsp",type: "post","processData": false,"contentType": false,
		"data": fd,
		success: function(data)
       	{
 	  		//dlg.msg(data);
 	  		//location.reload();
 	  		if(data.indexOf("succ=")==0)
 	  			devlib_before_imp(data.substring(5)) ;
 	  		else
 	  			dlg.msg(data) ;
   　  },
      　error: function(data)
         {
  				dlg.msg("upload failed");
　　　}
  　　});
}

function devlib_del(libid)
{
	dlg.confirm('<wbt:lang>del,this,dev,lib</wbt:lang>?',{btn:['<wbt:lang>yes</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],title:"<wbt:lang>del,confirm</wbt:lang>"},function ()
		    {
					send_ajax("./dev/lib_ajax.jsp","op=del&libid="+libid,function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:lang>del,err</wbt:lang>:"+ret) ;
			    			return ;
			    		}
			    		location.reload();
			    	}) ;
				});
}

function devlib_add_or_edit(libid)
{
	var tt = "<wbt:lang>edit,device,lib</wbt:lang>" ;
	if(!libid)
	{
		libid ="" ;
		tt = "<wbt:lang>add,device,lib</wbt:lang>" ;
	}
		
	dlg.open("./dev/lib_edit.jsp?libid="+libid,
			{title:tt},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="edit" ;
						 var pm = {
									type : 'post',
									url : "./dev/lib_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if(ret.indexOf("succ=")!=0)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								location.reload();
							}).fail(function(req, st, err) {
								dlg.msg(err);
							});
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function complib_export(libid)
{
	window.open("./ua_hmi/comp_lib_export.jsp?libid="+libid) ;
}



function comp_lib_import()
{
	complib_add_file.click();
}

function complib_before_imp(tmpfn)
{

	dlg.open("ua_hmi/comp_lib_import.jsp?tmpfn="+tmpfn,
			{title:"<wbt:lang>import,hmi,lib</wbt:lang>",w:'500px',h:'400px'},
			['<wbt:lang>do_imp</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 //enable_btn(true);
							 return;
						 }
						 //console.log(ret);
						 dlg.msg(ret) ;
						 
						 //var w = document.getElementById("devdef_lister").contentWindow ;
						// w.drv_sel_chg();
						 dlg.close();
						 location.reload();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function complib_add_file_onchg()
{
	//$("#"+id).
	var fs = $("#complib_add_file")[0].files ;
	if(fs==undefined||fs==null||fs.length<=0)
	{
		return ;
	}
	var f = fs[0];

	//upload
	var fd = new FormData();
    //fd.append("cxtid",cur_cxtid) ;
    fd.append("file",f);
     $.ajax({"url": "ua_hmi/comp_lib_imp_upload.jsp",type: "post","processData": false,"contentType": false,
		"data": fd,
		success: function(data)
       	{
 	  		//dlg.msg(data);
 	  		//location.reload();
 	  		if(data.indexOf("succ=")==0)
 	  			complib_before_imp(data.substring(5)) ;
 	  		else
 	  			dlg.msg(data) ;
   　  },
      　error: function(data)
         {
  				dlg.msg("<wbt:lang>upload,failed</wbt:lang>");
　　　}
  　　});
}

function open_complib(id)
{
	if(!id)
		id = "" ;
	dlg.open_win("ua_hmi/comp_lib_main.jsp?edit=true&libid="+id,
			{title:"<wbt:lang>hmi_comp_lib</wbt:lang>",w:'1000',h:'560'},
			[{title:'<wbt:lang>close</wbt:lang>',style:"primary"},{title:'<wbt:lang>help</wbt:lang>',style:"primary"}],
			[
				function(dlgw)
				{
					dlg.close();
				},
				function(dlgw)
				{
					dlg.msg("help is under dev");
				}
			]);
}

function complib_del(libid)
{
	dlg.confirm('<wbt:lang>del,this,hmi,lib</wbt:lang>?',{btn:["<wbt:lang>yes</wbt:lang>","<wbt:lang>cancel</wbt:lang>"],title:"<wbt:lang>del,confirm</wbt:lang>"},function ()
		    {
					send_ajax("./ua_hmi/comp_ajax.jsp","op=lib_del&libid="+libid,function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:lang>del,err</wbt:lang>:"+ret) ;
			    			return ;
			    		}
			    		location.reload();
			    	}) ;
				});
}

function complib_add_or_edit(libid)
{
	var tt = "<wbt:lang>edit,hmi,lib</wbt:lang>" ;
	if(!libid)
	{
		libid ="" ;
		tt = "<wbt:lang>add,hmi,lib</wbt:lang>" ;
	}
		
	dlg.open("./ua_hmi/comp_lib_edit.jsp?libid="+libid,
			{title:tt},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="edit" ;
						 var pm = {
									type : 'post',
									url : "./ua_hmi/comp_lib_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if(ret.indexOf("succ=")!=0)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								location.reload();
							}).fail(function(req, st, err) {
								dlg.msg(err);
							});
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function logout()
{
	$.ajax({
        type: 'post',
        url:'./login/login_ajax.jsp',
        data: {op:"logout"},
        async: true,  
        success: function (result) {  
        	if("ok"==result)
        	{
        		document.location.href="/admin/login/login.jsp" ;
        	}
        	else
        	{
        		dlg.msg("<wbt:lang>logout,failed</wbt:lang>") ;
        	}
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}

function service_setup()
{
	dlg.open("service/service_mgr.jsp",
			{title:"<wbt:lang>service_mgr</wbt:lang>",w:'500px',h:'400px'},
			['<wbt:lang>close</wbt:lang>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function open_simins(id)
{
	window.open("./sim/sim_ins_mgr.jsp?insid="+id);
	//window.open("ua_rep.jsp?repid="+id);
}

function del_simins(id)
{
	dlg.confirm("<wbt:lang>del,this,simins</wbt:lang>?",{btn:["<wbt:lang>yes</wbt:lang>","<wbt:lang>cancel</wbt:lang>"],title:"<wbt:lang>del,confirm</wbt:lang>"},function ()
    {
		send_ajax('sim/sim_ajax.jsp',{op:"ins_del",insid:id},function(bsucc,ret){
			if(!bsucc||ret!='succ')
			{
				dlg.msg(ret) ;
				return ;
			}
			location.reload();
		});
     });
}

function exp_simins(id)
{
	window.open("./sim/sim_ins_export.jsp?insid="+id) ;
}

function add_or_edit_simins(insid)
{
	var tt = "<wbt:g>add,simins</wbt:g>";
	if(insid)
		tt = "<wbt:g>edit,simins</wbt:g>";
	else
		insid="" ;
	dlg.open("sim/sim_ins_edit.jsp?insid="+insid,
			{title:tt,w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 //enable_btn(true);
							 return;
						 }
						 ret.insid = insid ;
						 if(insid)
							 ret.op="ins_edit" ;
						 else
							ret.op="ins_add" ;
						send_ajax('./sim/sim_ajax.jsp',ret,function(bsucc,rr)
						{
							if(!bsucc || rr.indexOf('succ')<0)
							{
								dlg.msg(rr);
								return ;
							}
							 dlg.close();
							 location.reload();
						},false);
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}



function add_sim_file_onchg()
{
	//$("#"+id).
	var fs = $("#add_sim_file")[0].files ;
	if(fs==undefined||fs==null||fs.length<=0)
	{
		return ;
	}
	var f = fs[0];

	//upload
	var fd = new FormData();
    //fd.append("cxtid",cur_cxtid) ;
    fd.append("file",f);
     $.ajax({"url": "./sim/sim_upload_imp.jsp",type: "post","processData": false,"contentType": false,
		"data": fd,
		success: function(data)
       	{
 	  		if(data.indexOf("succ")==0)
 	  			location.reload();
 	  		else
 	  			dlg.msg(data) ;
   　  },
      　error: function(data)
         {
  				dlg.msg("upload import failed");
　　　}
  　　});
}



function sim_ins_import()
{
	add_sim_file.click() ;
}


function log_ctrl()
{
	dlg.open("util/log_mgr.jsp",
			{title:"Log Controller",w:'700px',h:'600px'},
			['Close'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function conn_platform()
{
	dlg.open("util/station_mgr.jsp",
			{title:"Station Configuration to Platform",w:'700px',h:'600px'},
			['Close'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function pro_mgr()
{
	dlg.open("pro/pro_mgr.jsp",
			{title:"Pro Manager",w:'700px',h:'600px'},
			['Close'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function show_sors(objs)
{
	//console.log(objs);
	let tmps="" ;
	for(let ob of objs)
	{
		tmps += `<div id="sor_\${ob.id}" class="sor_item" tp="\${ob.tp}" t="\${ob.t}" n="\${ob.n}">
					<span class="n">[\${ob.n}] - \${ob.t}</span>
					<span class="tp">\${ob.tp} - \${ob.tpt}</span>
					<span class="oper">
						<button type="button" style="margin-left:2px;" class="layui-btn layui-btn-xs" onclick="test_sor('\${ob.id}')" title="Test Source"><i class="fa-solid fa-link"></i></button>
						<button type="button" style="margin-left:2px;" class="layui-btn layui-btn-xs layui-btn-normal" onclick="edit_sor('\${ob.id}')"><i class="fa fa-pencil"></i></button>
						<button type="button" style="margin-left:2px;" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_sor('\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
					</span>
					</div>` ;
	}
	$("#data_sor_list").html(tmps) ;
}

function update_sors()
{
	send_ajax("./store/store_ajax.jsp",{op:"list_sors"},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret);
			return;
		}
		let objs =null;
		eval("objs="+ret) ;
		show_sors(objs)
	});
}

update_sors();

function del_sor(id)
{
	let ob = $("#sor_"+id);
	event.stopPropagation();
	dlg.confirm('<wbt:g>del,this,sor</wbt:g> ['+ob.attr('n')+'] ?', {btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
	{
		send_ajax("./store/store_ajax.jsp",{op:"del_sor",id:id},(bsucc,ret)=>{
    		if(!bsucc||ret!="succ")
    		{
    			dlg.msg(ret);
    			return ;
    		}
    		update_sors();
    	}) ;
	});
}

function test_sor(id)
{
	event.stopPropagation();
		send_ajax("./store/store_ajax.jsp",{op:"test_sor",id:id},(bsucc,ret)=>{
    		dlg.msg(ret);
    	}) ;
}

function add_sor_sel()
{
	dlg.open("./store/store_sor_sel.jsp",
			{title:"<wbt:g>select,data,sor,type</wbt:g>"},
			['<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				add_or_edit_source(ret.tp,ret.tt,null) ;
			});
}

function edit_sor(id)
{
	let ob = $("#sor_"+id);
	let tp = ob.attr("tp");
	let t = ob.attr("t");
	add_or_edit_source(tp,t,id)
}

function add_or_edit_source(tp,t,id)
{
	if(event)
		event.stopPropagation();
	tt = "<wbt:g>add,data,sor</wbt:g> - "+t;
	if(id)
	{
		tt = "<wbt:g>edit,data,sor</wbt:g> - "+t;
	}
	if(!id)
		id = "" ;
	dlg.open("./store/store_sor_edit_"+tp+".jsp?id="+id,
			{title:tt},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="set_sor" ;
						 if(id)
						 	ret.id = id ;
						 ret.jstr = JSON.stringify(ret) ;
						 var pm = {
									type : 'post',
									url : "./store/store_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								update_sors();
							}).fail(function(req, st, err) {
								dlg.msg(err);
							});
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function chg_lan(ln)
{
	send_ajax("./login/login_ajax.jsp",{op:"set_session_lan",lan:ln},(bsucc,ret)=>{
		if(!bsucc||ret!='succ')
		{
			dlg.msg(ret) ;
			return;
		}
		location.reload();
	});
}
</script>
</html>
