<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
UAPrj prj = UAManager.getInstance().getPrjById(repid) ;
if(prj==null)
{
	out.print("no prj foudn") ;
	return ;
}

String cptp = ConnPtTcpClient.TP;//request.getParameter("cptp") ;
ConnProTcpClient cp = (ConnProTcpClient)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
//ConnProTcpClient cp = (ConnProTcpClient)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
ConnPtTcpClient cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtTcpClient() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtTcpClient)cp.getConnById(connid) ;
	if(cpt==null)
	{
		out.print("no connection found") ;
		return ;
	}
}

String name = cpt.getName() ;
String title= cpt.getTitle() ;
String chked = "" ;
if(cpt.isEnable())
	chked = "checked='checked'" ;
String desc = cpt.getDesc();
String host = cpt.getHost() ;
String port  = cpt.getPortStr() ;
String local_ip = cpt.getLocalIP() ;
int connto = cpt.getConnTimeout();
String cp_tp = cp.getProviderType() ;
long read_no_to = cpt.getReadNoDataTimeout();

boolean en_at_ps = cpt.isEnabledAtPStation() ;
String en_at_ps_chked =  en_at_ps?"checked":"";
%>
<html>
<head>
<title>tcp client conn editor</title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(800,500);
</script>
</head>
<body>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>name</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>title</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"   class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>enable</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <%
  if(prj.isPrjPStationIns())
  {
  %>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
	  <div class="layui-form-mid"><wbt:g>en_at_ps</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 200px;">
	    <input type="checkbox" id="en_at_ps" name="en_at_ps" <%=en_at_ps_chked%> lay-skin="switch"  lay-filter="en_at_ps" class="layui-input">
	  </div>
  </div>
  <%
  }
  %>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>host</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="host" name="host" value="<%=host%>"  class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>port</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="number" id="port" name="port" value="<%=port%>"  class="layui-input">
	  </div>
	 <div class="layui-form-mid"><button><i class="fa-regular fa-circle-question "></i></button></div>
	  
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>conn,timeout</wbt:g>(ms):</label>
    <div class="layui-input-inline" style="width: 120px;">
      <input type="number" id="conn_to" name="conn_to" value="<%=connto%>"  class="layui-input">
    </div>
    <div class="layui-form-mid">Read No Data <wbt:g>timeout</wbt:g>(ms):</div>
	  <div class="layui-input-inline" style="width: 120px;">
	    <input type="number" id="read_no_to" name="read_no_to" class="layui-input" value="<%=read_no_to%>">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>net_interface</wbt:g>:</label>
    <div class="layui-input-inline" style="width:80%;">
      <select id="local_ip" name="local_ip" lay-filter="local_ip">
      	<option value="" > --- </option>
<%
for(Enumeration<NetworkInterface> nis=NetworkInterface.getNetworkInterfaces();nis.hasMoreElements();)
{
	NetworkInterface ni = nis.nextElement() ;
	Inet4Address ip4 = null ;
	for(Enumeration<InetAddress> addrs = ni.getInetAddresses();addrs.hasMoreElements();)
	{
		InetAddress addr = addrs.nextElement() ;
		if(addr.isLoopbackAddress())
			continue ;
		if(!(addr instanceof Inet4Address))
			continue ;
		ip4 = (Inet4Address)addr ;
	
	
	String tmpip = ip4.getHostAddress();
	String seled = tmpip.equals(local_ip)?"selected":"" ;
	
%><option value="<%=tmpip %>" <%=seled %>>[<%=tmpip %>]  -  <%=ni.getDisplayName() %></option>
<%
	}
}
%>
	</select>
    </div>
    
  </div>
  
    <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>desc</wbt:g>:</label>
    <div class="layui-input-inline" style="width:80%">
      <textarea  id="desc"  name="desc"  class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name").on("input",function(e){
		  setDirty();
		  });
	  $("#title").on("input",function(e){
		  setDirty();
		  });
	  $("#desc").on("input",function(e){
		  setDirty();
		  });
	  $("#conn_to").on("input",function(e){
		  setDirty();
		  });
	  $("#read_no_to").on("input",function(e){
		  setDirty();
		  });
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
	  form.on('switch(en_at_ps)', function(obj){
		       setDirty();
		  });
	  form.on("select(local_ip)",function(obj){
		  setDirty();
	  });
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cp_tp%>" ;
var conn_id = "<%=connid%>" ;

function isDirty()
{
	return bdirty;
}
function setDirty(b)
{
	if(!(b===false))
		b = true ;
	bdirty= b;
	dlg.btn_set_enable(1,b);
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
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		//cb(false,'Please input title') ;
		//return ;
		tt = n;
	}
	var ben = $("#enable").prop("checked") ;
	
	let en_at_ps = $("#en_at_ps").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var host = $('#host').val();
	if(host==null||host=='')
	{
		cb(false,'<wbt:g>pls,input,host</wbt:g>') ;
		return ;
	}
	var port = $('#port').val();
	if(port==null||port=='')
	{
		cb(false,'<wbt:g>pls,input,port</wbt:g>') ;
		return ;
	}
	var vp = parseInt(port);
	if(vp==NaN||vp<0)
	{
		cb(false,'<wbt:g>pls,input,valid,port</wbt:g>') ;
	}
	var connto = parseInt($("#conn_to").val()) ;
	if(connto==NaN)
	{
		cb(false,'<wbt:g>pls,input,valid,conn,timeout</wbt:g>') ;
	}
	let local_ip = $("#local_ip").val() ;
	let read_no_to = parseInt($("#read_no_to").val()) ;
	if(read_no_to==NaN||read_no_to==null||read_no_to==undefined)
		read_no_to = 60000 ;
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,en_at_ps:en_at_ps,host:host,port:vp,conn_to:connto,read_no_to:read_no_to,local_ip:local_ip});
}

</script>
</html>