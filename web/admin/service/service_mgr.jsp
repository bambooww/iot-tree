<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.service.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	List<AbstractService> ass = ServiceManager.getInstance().listServices() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(700,400);
</script>
<style>
</style>
</head>
<body>

<table class="layui-table">
  <colgroup>
    <col width="150">
    <col width="200">
    <col>
  </colgroup>
  <thead>
    <tr>
      <th><wbt:g>name</wbt:g></th>
      <th><wbt:g>desc</wbt:g></th>
      <th><wbt:g>status</wbt:g></th>
      <th></th>
    </tr> 
  </thead>
  <tbody>
<%
for(AbstractService as:ass)
{
	String run_c = "grey" ;
	String run_t = "disabled" ;
	if(as.isEnable())
	{
		if(as.isRunning())
		{
			run_c = "green" ;
			run_t = "running" ;
		}
		else
		{
			run_c = "red" ;
			run_t = "pause" ;
		}
	}
%>
  
    <tr>
      <td><%=as.getTitle() %></td>
      <td><%=as.getBrief() %></td>
      <td><span id="" style="width:20px;height:20px;background-color: <%=run_c %>;" >&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;<wbt:g><%=run_t %></wbt:g>
      <%
if(as.isEnable())
{
	if(as.isRunning())
	{
%>

		 <i id="prj_btn_stop"  class="fa fa-pause fa-lg" style="color:red" title="stop service" onclick="start_stop(false,'<%=as.getName()%>')"></i>
		 
<%
	}
	else
	{
%>
<i id="prj_btn_start"  class="fa fa-play fa-lg" style="color:green" title="start service" onclick="start_stop(true,'<%=as.getName()%>')"></i>
<%
	}
}
%>
      </td>
      <td>

        
        <a href="javascript:edit_server('<%=as.getName()%>','<%=as.getTitle()%>')"><i class="fa fa-pencil-square fa-lg " aria-hidden="true"></i></a> 
	  </td>
    </tr>
    

<%
}
%>

  </tbody>
</table>
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

function edit_server(n,t)
{
	dlg.open("service_edit_"+n+".jsp",
			{title:t+" <wbt:g>setup</wbt:g>",w:'500px',h:'400px'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
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
	
	send_ajax("service_ajax.jsp",{op:op,n:n},(bsucc,ret)=>{
		if(!bsucc||ret!='ok')
		{
			dlg.msg(ret);return;
		}
		location.reload() ;
	});
	/*
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
	*/
}

</script>
</html>