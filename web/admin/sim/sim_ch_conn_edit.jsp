<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.sim.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "insid","chid"))
	return;
String insid = request.getParameter("insid") ;
String chid = request.getParameter("chid") ;
SimInstance ins = SimManager.getInstance().getInstance(insid) ;
if(ins==null)
{
	out.print("no instance found") ;
	return ;
}

SimChannel sch = ins.getChannel(chid);
	if(sch==null)
	{
out.print("no channel (bus) found") ;
return ;
	}

	SimCP conn = sch.getConn() ;

String local_ip = "";//cp.getLocalIP() ;
int local_port = SimCPTcp.DEF_PORT;
if(conn!=null)
{
	if(conn instanceof SimCPTcp)
	{
		SimCPTcp sct = (SimCPTcp)conn ;
		local_ip = sct.getServerIp() ;
		local_port = sct.getServerPort() ;
	}
}
%>
<html>
<head>
<title>conn editor</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
  
  <div class="layui-form-item">
    <label class="layui-form-label">Local Port:</label>
    <div class="layui-input-inline">
      <input type="text" id="local_port" name="local_port" value="<%=local_port%>"  class="layui-input">
    </div>
    <div class="layui-form-mid">Local IP:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="local_ip"  lay-filter="local_ip" >
	    	<option value="">--</option>
<%
	for(NetUtil.Adapter adp:NetUtil.listAdapters())
	{
%>
	<option value="<%=adp.getIp4()%>"><%=adp.getIp4() %></option>
<%
	}
%>	  
<!-- 
	    <input type="text" id="local_ip" name="local_ip" value="<%=local_ip%>"  class="layui-input">
	     -->
	    </select>
	  </div>

  </div>
  
   
      
 </form>
</body>
<script type="text/javascript">
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#local_ip,#local_port").on("input",function(e){
		  setDirty();
		  });
	  form.on('select(local_ip)', function(data){   
		    setDirty();
	 });
	 
	  $("#local_ip").val("<%=local_ip%>") ;

	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;

function isDirty()
{
	return bdirty;
}
function setDirty()
{
	bdirty= true;
	dlg.btn_set_enable(1,true);
}


	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function do_submit(cb)
{
	
	var local_port = $("#local_port").val() ;
	if(local_port==null||local_port=="")
	{
		cb(false,"Please input local port") ;
		return ;
	}
	var lpt =parseInt(local_port);
	if(lpt==NaN||lpt<=0||lpt>65535)
	{
		cb(false,"invalid local port") ;
		return ;
	}
	var local_ip = $("#local_ip").val() ;
	
	var ret = {tp:"tcp_server","dx_/server_ip:string":local_ip,"dx_/server_port:int32":local_port}
	console.log(ret) ;
	cb(true,ret);
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}
<%--
var ash2paramdef={
<%
boolean tmpbf=  true ;
for(ConnProTcpServer.AcceptedSockHandler ash: ashs)
{
	if(tmpbf)tmpbf=false;
	else out.write(",") ;
	out.write(ash.getName()+":") ;
	NameTitleVal.writeToJsonStr(ash.getParamDefs(),out) ;
}
%>
}
--%>

function sel_ash()
{
	var ashn = $("#ash").val() ;
	if(ashn==null||ashn==undefined)
		return ;
	send_ajax("cp_edit_tcp_server_ash.jsp",{ashn:ashn},(bsucc,ret)=>{
		$("#cont").html(ret) ;
		//form.render();
		
		$( "[id^='param_']" ).each(function(index,item){
			var id = $(this).attr("id") ;
			var v = $(this).val() ;
			console.log(id,v) ;
			
		}) ;
		
		$( "[id^='param_']" ).on("change",function(){
			var v = $(this).val() ;
			console.log(v) ;
			setDirty()
		}) ;
		
		$( "[id^='param_']" ).bind('input propertychange', function(){
			var v = $(this).val() ;
			console.log("textarea "+v) ;
			setDirty()
		});  
	});
}

sel_ash();
</script>
</html>