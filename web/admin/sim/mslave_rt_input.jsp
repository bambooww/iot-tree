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

boolean b_bool = seg.isBoolData() ;

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
<%
if(b_bool)
{
	boolean bv = seg.getSlaveDataBool()[regidx] ;
	String chked_en = "" ;
	if(bv)
		chked_en="checked=checked" ;
%>
<div class="layui-form-item">
    <label class="layui-form-label">Bool Value</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="boolv" name="boolv" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
<%
}
else
{
	short int16v = seg.getSlaveDataInt16()[regidx] ;
%>
 <div class="layui-form-item">
    <label class="layui-form-label">Int16 Value</label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="int16v" name="int16v" id="int16v" value="<%=int16v%>" autocomplete="off" class="layui-input"/>
    </div>
    
  </div>
<%
}
%>   
</form>
</body>
<script type="text/javascript">

var b_bool = <%=b_bool%>;

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
	if(b_bool)
	{
		var boolv = $("#boolv").prop("checked") ;
		cb(true,{v:boolv});
	}
	else
	{
		var int16v = $("#int16v").val() ;
		int16v = parseInt(int16v) ;
		if(isNaN(int16v))
		{
			cb(false,"invalid int16 value") ;
			return ;
		}
		cb(true,{v:int16v});
	}
		
		
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>