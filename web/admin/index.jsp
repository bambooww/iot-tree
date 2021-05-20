<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.xmldata.*
"%><%@ taglib uri="wb_tag" prefix="wbt"%><%//UserProfile up = UserProfile.getUserProfile(request);
//String un = up.getUserInfo().getFullName();
List<UAPrj> reps = UAManager.getInstance().listPrjs();

Collections.sort(reps, new Comparator<UAPrj>() {

    @Override
    public int compare(UAPrj o1, UAPrj o2) {
        long v = o1.getSavedDT()-o2.getSavedDT() ;
        if(v>0)
        	return -1 ;
        else if(v<0)
        	return 1 ;
        return 0 ;
    }
}) ;%><!DOCTYPE html>
<html class="">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1">
    <title>IOT-Tree</title>
    <link href="/favicon.ico" rel="shortcut icon" type="image/x-icon">
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
            <link href="./inc/common.css" rel="stylesheet" type="text/css">
        <link href="./inc/index.css" rel="stylesheet" type="text/css">

    </head>
<body aria-hidden="false">
	<div class="iot-top-menu-wrap">
		<div class="container">
			<!-- start logo -->
			<div class="iot-logo">
				<a><img src="inc/logo1.png" width="40px" height="40px"/> IOT-Tree Server</a>
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
					  <li><a href="https://github.com/bambooww/iot-tree.git"  target="_blank" class=""><i class="icon icon-home"></i> Home</a></li>
					  <li><a href=""><i class="icon icon-topic"></i> Helper</a></li>
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
			<!-- user -->
			<div class="iot-user-nav">
					<div class="iot-top-user"><a class="login" href="javascript:logout()">logout</a></div>
			</div>
			<!-- end user -->
			
		</div>
	</div>
	

<div class="iot-container-wrap" style="height: auto !important;">
		<div class="container" style="height: auto !important;">
		<div class="row" style="height: auto !important;">
			<div class="iot-content-wrap clearfix" style="height: auto !important;">
				<div class="iot-main-content" style="height: auto !important; min-height: 0px !important;">
					<div class="iot-mod iot-question-detail iot-item">
						<div class="mod-head">
							<h1>Local Projects
							<a class="btn btn-success" style="width:80px;height:40px;align-content: center;" href="javascript:add_rep()">
							<i class="fa fa-plus-circle fa-lg" ></i>&nbsp;&nbsp;Add
							</a>
							
							<a class="btn btn-success"  style="width:100px;height:40px;" href="javascript:imp_prj()">
							<span class="fa-stack">
							  <i class="fa fa-square-o fa-stack-2x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x"></i>
							</span>&nbsp;&nbsp;Import
							</a>

							<a class="btn btn-success"  style="width:100px;height:40px;" href="javascript:imp_demo()">
							<span class="fa-stack">
							  <i class="fa fa-square-o fa-stack-2x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x"></i>
							</span>&nbsp;&nbsp;Import Demo
							</a>
							
				           </h1>
						</div>
						<div class="mod-body">
							<div class="content markitup-box" style="height:100%">
<%
	for(UAPrj rep:reps)
{
%>
	<div class="aw-item">
       <a class="img aw-border-radius-5" >
         <i class="fa fa-sitemap fa-2x"></i>
       </a>
       <a class="text title" href="javascript:open_rep('<%=rep.getId()%>')" data-id="8"><%=rep.getTitle() %></a>
       <div class="inline-block pull-right text-left">
           <a class="btn btn-success download-btn white" href="javascript:open_rep('<%=rep.getId()%>')" title="show detail">
              <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
           </a>
           <a class="btn btn-success"  href="javascript:del_rep('<%=rep.getId()%>')" title="export">
              <i class="fa fa-arrow-right"></i>
           </a>
           <a class="btn btn-success " style="background-color: #e33a3e" href="javascript:del_rep('<%=rep.getId()%>')" title="delete">
              <i class="fa fa-times" aria-hidden="true"></i>
           </a>
       </div>

       <div class="text-color-999">
           <span class="text-color-666">&nbsp;&nbsp;&nbsp;</span>
           • modified date:<span class="text-color-666"><%=new Date(rep.getSavedDT()) %></span>
       </div>
   </div>
<%
}
%>

							</div>
						</div>
						
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
						
					</div>
				</div>
				<!--right side -->
				<div class="iot-side-bar">
					


					<div class="iot-mod iot-text-align-justify">
					    <div class="mod-head">
					        <h3>Device Library</h3>
					    </div>
					    <div class="mod-body">
					       
					        <a href="javascript:open_devlib()">Open</a> Device Library
					        <p>supported drivers:</p>
					        
					    </div>
					</div>

					<div class="iot-mod iot-text-align-justify">
					    <div class="mod-head">
					        <h3>Remote Connections</h3>
					    </div>
					    <div class="mod-body">
					                
					                
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
		Copyright 

	</div>
</div>
</body>
<script type="text/javascript">

var all_panels=[];
function open_rep(id)
{
	window.open("rep_editor.jsp?id="+id);
	//window.open("ua_rep.jsp?repid="+id);
}

function del_rep(id)
{
	dlg.confirm("make sure to delete it？",{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
    {
		send_ajax('ua/rep_ajax.jsp',{op:"del",id:id},function(bsucc,ret){
			if(!bsucc||ret!='ok')
			{
				dlg.msg(ret) ;
				return ;
			}
			document.location.href=document.location.href;
		});
     });
}

function open_devlib()
{
	dlg.open_win("dev/dev_lib_lister.jsp?mgr=true",
			{title:"Device Library",w:'1000',h:'560'},
			[{title:'Close',style:"primary"},{title:'Help',style:"primary"}],
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
function add_rep()
{
	dlg.open("ua/rep_edit.jsp",
			{title:"新增容器",w:'500px',h:'400px'},
			['确定','取消'],
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
						 document.location.href=document.location.href;
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
        		dlg.msg("Login failed") ;
        	}
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}
</script>
</html>
