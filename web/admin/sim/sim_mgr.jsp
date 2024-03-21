<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.service.*,
				org.iottree.core.util.web.*,
				org.iottree.core.sim.*,
				org.iottree.driver.common.modbus.sim.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	
	List<SimInstance> inss = SimManager.getInstance().getInstances() ;
%>
<html>
<head>
<title>simulator manager</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(700,400);
</script>
<style>
</style>
</head>
<body>
<div class="layui-tab layui-tab-brief" lay-filter="docDemoTabBrief">
  <ul class="layui-tab-title">
    <li class="layui-this">Modbus Slave</li>
    
  </ul>
  <div class="layui-tab-content">
  <div class="layui-tab-item layui-show">
  	<iframe src="mslave_mgr.jsp" style="width:100%;height:300px;border:0px;margin:0px"></iframe>
  </div>
    
  </div>
</div> 
</body>
<script type="text/javascript">


var table = null ;

var cur_selected = null ;

//var on_devdef_selected = null ;

layui.use('table', function()
{
  table = layui.table;
});

function refresh_ui()
{
	location.reload();
}

function edit_server(n)
{
	dlg.open("service_edit_"+n+".jsp",
			{title:"active mq setup",w:'500px',h:'400px'},
			['Ok','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						ret.n = n;
						ret.op='setup';
						dlg.loading(true);
						send_ajax('service_ajax.jsp',ret,function(bsucc,ret)
						{
							dlg.loading(false);
							if(!bsucc || ret.indexOf('ok')<0)
							{
								dlg.msg(""+ret);
								return ;
							}
							dlg.close();
							refresh_ui();
						},false);
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function start_stop(b,n)
{
	var op = "start" ;
	if(!b)
		op = "stop";
	$.ajax({
        type: 'post',
        url:'service_ajax.jsp',
        data: {op:op,n:n},
        async: true,  
        success: function (result) {  
        	if("ok"==result)
        	{
        		location.reload() ;
        	}
        	else
        	{
        		dlg.msg(result) ;
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