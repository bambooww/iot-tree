<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	MNNet net = null;
	MNManager mnm= null;
	String jo_txt = "" ;
	if(Convert.isNotNullEmpty(container_id))
	{
		mnm = MNManager.getInstanceByContainerId(container_id) ;
		if(mnm==null)
		{
			out.print("no MsgNet Manager with container_id="+container_id) ;
			return ;
		}
		if(Convert.isNotNullEmpty(netid))
		{
			net = mnm.getNetById(netid) ;
			if(net==null)
			{
				out.print("no net found with id="+netid) ;
				return ;
			}
			jo_txt = net.toJO().toString() ;
		}
		
	}
	
%>
<html>
<head>
<title>net editor</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(700,600);
</script>
<style>
.top {width:100%;height:30px;}
.cont {width:100%;height:400px;}
</style>
</head>
<body>
<div class="top">
<%
if(net!=null)
{
%>当前流程：<%=net.getTitle()%>&nbsp;
<button class="layui-btn layui-btn-sm layui-btn-primary"   onclick="cpy_txt()">Copy</button>
<%
}
%>
</div>
<div class="cont">
	<textarea id="txt" style="width:100%;height:100%;"><%=jo_txt %></textarea>
</div>
</body>
<script type="text/javascript">
let inp = dlg.get_opener_opt("input") ;

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	
function win_close()
{
	dlg.close(0);
}

function cpy_txt()
{
	let txt = $("#txt").val() ;
	let $txt = $('#txt');

    // 1. 聚焦并选中
    $txt.trigger('focus').select();
    $txt[0].setSelectionRange(0, $txt.val().length); // 兼容 iOS

    // 2. 执行复制
    let ok = false;
    try {
        ok = document.execCommand('copy');
        if(ok)
        {
        	dlg.msg("Copy Ok");
        	return;
        }
    } catch (e) {
        dlg.msg("Copy Failed:"+e) ;
    }
    if(!ok)
    	dlg.msg("copy failed") ;
}

function get_txt()
{
	return $("#txt").val() ;
}

</script>
</html>