<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.task.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.sim.*,
				org.iottree.driver.common.modbus.sim.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out,"insid","chid","devid","segid","regidx"))
return ;

String insid=request.getParameter("insid");
SimInstance ins = SimManager.getInstance().getInstance(insid) ;
if(ins==null)
{
	out.print("no instance found") ;
	return ;
}

String chid=request.getParameter("chid");
String devid=request.getParameter("devid");
String segid = request.getParameter("segid") ;
int regidx = Convert.parseToInt32(request.getParameter("regidx"),-1) ;
if(regidx<0)
{
	out.print("invalid regidx input") ;
	return ;
}
SimChannel sch = ins.getChannel(chid);
if(sch==null)
{
	out.print("no channel found") ;
	return ;
}
SlaveDev dev = (SlaveDev)sch.getDev(devid) ;
	if(dev==null)
	{
out.print("no device found") ;
return ;
	}
int addr = dev.getDevAddr() ;
SlaveDevSeg seg = dev.getSegById(segid) ;
if(seg==null)
{
	out.print("no seg found") ;
	return ;
}

SlaveTag st = dev.findTagBySegIdx(segid, regidx);
String tagname = "" ;
if(st!=null)
	tagname = st.getName() ;
%>
<html>
<head>
<title>rt input</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,300);
</script>
</head>
<body>
<form class="layui-form" action="">

 <div class="layui-form-item">
    <label class="layui-form-label">Tag Name</label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="tagname" id="tagname" value="<%=tagname%>" autocomplete="off" class="layui-input"/>
    </div>
    
  </div>

</form>
</body>
<script type="text/javascript">
layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	
function win_close()
{
	dlg.close(0);
}
function do_submit(cb)
{
		var n = $("#tagname").val() ;
		cb(true,{name:n});
}
</script>
</html>