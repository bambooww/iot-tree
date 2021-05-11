<%@ page contentType="text/html;charset=UTF-8"%><%@page import="org.iottree.system.*,
		org.iottree.user.*" %>
<%@ taglib uri="wb_tag" prefix="wbt"%><%
UserProfile up = UserProfile.getUserProfile(request);
String un = up.getUserInfo().getFullName();
if(Convert.isNullOrEmpty(un))
	un=up.getUserName();
	boolean badmin = up.isAdministrator() ;
	boolean bduty = up.containsRoleName("qx_duty") ;
	boolean baudit = up.containsRoleName("qx_auditor") ;

%><!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <title>Home</title>
  <link rel="stylesheet" href="/_js/layui/css/layui.css" media="all">
    <script src="/_js/jquery.min.js"></script>
    <link  href="inc/font4.7.0/css/font-awesome.min.css"  rel="stylesheet" type="text/css" >
	<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
	<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
  <link   href="/_js/layui/css/layui.css" rel="stylesheet" />
    <script src="/_js/ajax.js"></script>
    <script src="/_js/layer/layer.js"></script>
        <script src="/_js/dlg_layer.js"></script>
      <script src="/_js/layui/layui.js"></script>
</head>
  <style>
  .dashboard-stats {
      float: left;
      width: 25%;
  }

  .dashboard-stats-item {
      position: relative;
      overflow: hidden;
      color: #fff;
      cursor: pointer;
      height: 105px;
      margin-right: 10px;
      margin-bottom: 10px;
      padding-left: 15px;
      padding-top: 20px;
  }

      .dashboard-stats-item .m-top-none {
          margin-top: 5px;
      }

      .dashboard-stats-item h2 {
          font-size: 28px;
          font-family: inherit;
          line-height: 1.1;
          font-weight: 500;
          padding-left: 70px;
      }

          .dashboard-stats-item h2 span {
              font-size: 12px;
              padding-left: 5px;
          }

      .dashboard-stats-item h5 {
          font-size: 12px;
          font-family: inherit;
          margin-top: 1px;
          line-height: 1.1;
          padding-left: 70px;
      }


      .dashboard-stats-item .stat-icon {
          position: absolute;
          top: 18px;
          font-size: 50px;
          opacity: .3;
      }

  .dashboard-stats i.fa.stats-icon {
      width: 50px;
      padding: 20px;
      font-size: 50px;
      text-align: center;
      color: #fff;
      height: 50px;
      border-radius: 10px;
  }

  .panel-default {
      border: none;
      border-radius: 0px;
      margin-bottom: 0px;
      box-shadow: none;
      -webkit-box-shadow: none;
  }

      .panel-default > .panel-heading {
          color: #777;
          background-color: #fff;
          border-color: #e6e6e6;
          padding: 10px 10px;
      }

      .panel-default > .panel-body {
          padding: 10px;
          padding-bottom: 0px;
      }

          .panel-default > .panel-body ul {
              overflow: hidden;
              padding: 0;
              margin: 0px;
              margin-top: -5px;
          }

              .panel-default > .panel-body ul li {
                  line-height: 27px;
                  list-style-type: none;
                  white-space: nowrap;
                  text-overflow: ellipsis;
              }

     .panel-default > .panel-body ul li .time {
         color: #a1a1a1;
         float: right;
         padding-right: 5px;
     }
</style>
<body style="overflow: hidden;">

<fieldset class="layui-elem-field" style="margin: 10px">
  <legend>系统整体</legend>
  <div class="layui-field-box">
 

                    <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #578ebe;" onclick="">
                            <div class="stat-icon">
                                <i class="fa fa-cloud-download"></i>
                            </div>
                            <h2 class="m-top-none">今日下载<br><span>上午12点下载气象数据完成</span></h2>
                            <h5></h5>
                        </div>
                    </div>
                    <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #e35b5a;" onclick="">
                            <h2 class="m-top-none" id="total3">28<span>%</span></h2>
                            <h5>任务链运行状态</h5>
                            <div class="stat-icon">
                                <i class="fa fa-superpowers"></i>
                            </div>
                        </div>
                    </div>
                    <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #44b6ae;" onclick="">
                            <h2 class="m-top-none" id="areatotal">0<span>条</span></h2>
                            <h5>本周推送消息</h5>
                            <div class="stat-icon">
                                <i class="fa fa-paper-plane-o"></i>
                            </div>
                        </div>
                    </div>
                    <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #8775a7; margin-right: 0px;" onclick="OpenNav('ce445861-579c-4f52-9d7a-5e783715d82d');">
                            <h2 class="m-top-none" id="total">0 <span>条</span></h2>
                            <h5>今天接入动态数据</h5>
                            <div class="stat-icon">
                                <i class="fa fa-plug"></i>
                            </div>
                        </div>
                    </div>

                    
                    
                    <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #f29503; margin-bottom: 0px;" onclick="">
                        <div style="padding-left:50px;" id="alert_info">
                              
                            </div>
                            <h2 class="m-top-none" id="report_edit_num" style="padding-top:10px">0<span>份报告需编辑</span></h2>
                            <h5>
                            </h5>
                            <div class="stat-icon">
                                <i class="fa fa-file-text-o"></i>
                            </div>
                        </div>
                    </div>
                    
                    
                    <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #949FB1; margin-bottom: 0px;" onclick="">
                            <h2 class="m-top-none">0<span>份</span></h2>
                            <h5>最近预警报告</h5>
                            <div class="stat-icon">
                                <i class="fa fa-file-text-o"></i>
                            </div>
                        </div>
                    </div>
   
            </div>
</fieldset>
<fieldset class="layui-elem-field" style="margin: 10px">
  <legend>系统辅助</legend>
  <div class="layui-field-box">
  
    
    <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #578ebe;" onclick="">
                            <div class="stat-icon">
                                <i class="fa fa-th"></i>
                            </div>
                            <h2 class="m-top-none">网格<br><span></span></h2>
                            <h5 ><span onclick="down_geojson()">下载GeoJSON</span>&nbsp;&nbsp;<span onclick="down_shp()">下载shp</span></h5>
                        </div>
                    </div>
                    

 <%
 if(badmin||baudit)
 {
 %>
 <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #14aae4; margin-bottom: 0px;" onclick="">
                            <h2 class="m-top-none" id="cjtotal">审核<span></span></h2>
                            <h5>报告需要您审核</h5>
                            <div class="stat-icon">
                                <i class="fa fa-file-text-o"></i>
                            </div>
                        </div>
                    </div>
<%
 }
 if(badmin||bduty)
 {
 %>
 <div class="dashboard-stats">
                        <div class="dashboard-stats-item" style="background-color: #14aae4; margin-bottom: 0px;" onclick="">
                            <h2 class="m-top-none" id="cjtotal">值班<span></span></h2>
                            <h5>新报告</h5>
                            <div class="stat-icon">
                                <i class="fa fa-file-text-o"></i>
                            </div>
                        </div>
                    </div>
<%
 }
%>
 </div>
</fieldset>
</body>
<script>

function show_alert()
{
	send_ajax("report/pre_alert_new_ajax.jsp","",function(bsucc,ret){
		if(!bsucc)
		{
			//dlg.msg(ret) ;
			return ;
		}
		//alert(ret);
		var ob = null ;
		eval("ob="+ret) ;
		var rps = ob.data ;
		var txt="" ;
		for(var i = 0 ; i < rps.length&&i<2 ; i ++)
		{
			var autoid = rps[i].autoId;
			var rid = rps[i].relatedid;
			var title = rps[i].ctitle;
			txt += "<div><i class='fa fa-snowflake-o' ></i><a href=\"javascript:open_report('"+rid+"')\">"+title+"</a></div>";
		}
		$("#report_edit_num").html(rps.length+"<span>份报告需编辑</span>");
		$("#alert_info").html(txt) ;
	}) ;
	
}

function open_report(rid)
{
	window.open("report/report_edit.jsp?rid="+rid) ;
}

function open_report_edit()
{
	//parent.
}

function down_geojson()
{
	window.open("/gis/grid/get_grid_json.jsp") ;
}

function down_shp()
{
	window.open("/gis/grid/get_grid_shp.jsp") ;
}


setInterval("show_alert()",5000) ;
</script>
</html>