<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	List<DevDriver> dds = DevManager.getInstance().getDrivers() ;
%>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,400);
</script>
<style>
</style>
</head>
<body>

<textarea name="txt" id="txt" placeholder=""   style="width:600px;overflow: auto;height:300px;">
</textarea>

</body>
<script type="text/javascript">

function win_close()
{
	dlg.close(0);
}

function init_val()
{
	var txt = get_dlg_w().opener.cur_txt;
	if(txt==null)
		txt="" ;
	//var opts = get_dlg_w().opener_opts ;
	//if(!opts)
	//	return ;
	//if(!opts.txt)
	//	return ;
	$("#txt").val(txt) ;
}

init_val() ;

function do_submit(cb)
{
	var txt = $('#txt').val();
	
	cb(true,{txt:txt});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>