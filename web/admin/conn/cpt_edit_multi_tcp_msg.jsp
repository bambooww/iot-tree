<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.json.*,
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
String cptp = ConnPtMultiTcpMSG.TP;//request.getParameter("cptp") ;
ConnProMultiTcpMsg cp = (ConnProMultiTcpMsg)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
//ConnProTcpClient cp = (ConnProTcpClient)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
ConnPtMultiTcpMSG cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtMultiTcpMSG() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtMultiTcpMSG)cp.getConnById(connid) ;
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
int connto = -1;//cpt.getConnTimeout();
String cp_tp = cp.getProviderType() ;


ConnPtMultiTcpMSG.TcpRunner tcpRun = cpt.getTcpRunner() ;
ConnPtMultiTcpMSG.TcpServerRun tcpServerRun = null ;
ConnPtMultiTcpMSG.TcpClientsRun tcpClientsRun = null ;

String loc_ip = "" ;
int loc_port = 26000 ;
int max_c = 5 ;
String tcp_run_tp="c" ;

int recv_broken_to = 60000;

int conn_to = 3000 ;
//String host = "" ;
//int port = -1 ;
//long read_no_to = -1 ;
JSONObject tcp_run_c = null ;

if(tcpRun!=null)
{
	recv_broken_to = tcpRun.getRecvBrokenTO() ;
	if(tcpRun instanceof ConnPtMultiTcpMSG.TcpServerRun)
	{
		tcpServerRun = (ConnPtMultiTcpMSG.TcpServerRun)tcpRun ;
		loc_ip = tcpServerRun.getLocalIP() ;
		loc_port = tcpServerRun.getLocalPort() ;
		max_c = tcpServerRun.getMaxConn() ;
		tcp_run_tp = tcpServerRun.getTP() ;
	}
	
	if(tcpRun instanceof ConnPtMultiTcpMSG.TcpClientsRun)
	{
		tcpClientsRun = (ConnPtMultiTcpMSG.TcpClientsRun)tcpRun ;
		conn_to = tcpClientsRun.getConnTimeout() ;
		tcp_run_c = tcpClientsRun.toJO() ;
	}
}

String hex_s="" ;
boolean keep_s = false;
String hex_e="" ;
boolean keep_e = false;
int max_len = 1024 ;

String data_pro_tp="sp" ;

ConnPtMultiTcpMSG.TcpDataPro dataPro = cpt.getTcpDataPro() ;
ConnPtMultiTcpMSG.TcpDataProSpliter dataProSP = null ;
ConnPtMultiTcpMSG.TcpDataProStrLine dataProLN = null ;

if(dataPro!=null)
{
	data_pro_tp=dataPro.getTP() ;
	max_len = dataPro.getMaxLen();
	
	if(dataPro instanceof ConnPtMultiTcpMSG.TcpDataProSpliter)
	{
		dataProSP = (ConnPtMultiTcpMSG.TcpDataProSpliter)dataPro ;
		hex_s = dataProSP.getStartHex() ;
		keep_s = dataProSP.isKeepStart() ;
		hex_e = dataProSP.getEndHex() ;
		keep_e = dataProSP.isKeepEnd() ;
		data_pro_tp = "sp" ;
	}
	
	if(dataPro instanceof ConnPtMultiTcpMSG.TcpDataProStrLine)
	{
		dataProLN = (ConnPtMultiTcpMSG.TcpDataProStrLine)dataPro ;
		data_pro_tp = "ln" ;
	}
}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style type="text/css">
.remote_item
{
	border:1px solid #03a8d8;
	min-height:30px;
	margin:3px;
	min-width: 50px;
}
</style>
<script>
dlg.resize_to(800,500);
</script>
</head>
<body>
<form class="layui-form" onsubmit="return false;">
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
  
  <div class="layui-form-item">
    <label class="layui-form-label">
    	Tcp Run
    </label>
    <div class="layui-input-inline" style="width:150px">
      <select id="tcp_run_tp" lay-filter="tcp_run_tp" >
    		<option value="c">Clients</option>
    		<option value="s">Server</option>
    	</select>
	</div>
	<div class="layui-form-mid">Recv Broken Timeout:</div>
    <div class="layui-input-inline">
      <input type="number" id="recv_broken_to" name="recv_broken_to" value="<%=recv_broken_to%>"  class="layui-input"
      	title="The tcp connection will be closed by no data received timeout">
    </div>
	
  </div>
  
  <div id="tcp_run_tp_s" style="display:none">
  <div class="layui-form-item" >
    <label class="layui-form-label">Server:</label>
    <div class="layui-form-mid">Local Port:</div>
    <div class="layui-input-inline" style="width:100px">
      <input type="number" id="loc_port" name="loc_port" value="<%=loc_port%>"  class="layui-input">
    </div>
    <div class="layui-form-mid">Local IP:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="loc_ip"  lay-filter="loc_ip" >
	    	<option value="">--</option>
<%
	for(NetUtil.Adapter adp:NetUtil.listAdapters())
	{
%>
	<option value="<%=adp.getIp4()%>"><%=adp.getIp4() %></option>
<%
	}
%>	  
	    </select>
	  </div>
	<div class="layui-form-mid">Max Conn:</div>
	  <div class="layui-input-inline" style="width: 80px;">
	  	<input type="number" id="max_c" name="max_c" value="<%=max_c%>"  class="layui-input">
	  </div>
  </div>
  </div>
  
  <div id="tcp_run_tp_c" style="display:none">
  <div class="layui-form-item" >
    <label class="layui-form-label">Clients:</label>
    <div class="layui-input-inline" style="width:80%">
    	<div id="clients_list" style="width:100%;height:50px;overflow-y:auto;"></div>
    	<div class="layui-form-mid" style="height:28px">Remote <wbt:g>host</wbt:g>:</div>
	    <div class="layui-input-inline" >
	      <input type="text" id="remote_host" name="remote_host" value=""  style="height:28px" class="layui-input">
	    </div>
	    <div class="layui-form-mid" style="height:28px"><wbt:g>port</wbt:g>:</div>
		  <div class="layui-input-inline" style="width: 80px;">
		    <input type="number" id="remote_port" name="remote_port" value="" style="height:28px" class="layui-input">
		  </div>
		 <div class="layui-form-mid"><button class="layui-button" onclick="set_addr_tcp_run_c()"><i class="fa fa-plus "></i></button></div>
	  </div>
    </div>
     
     <div class="layui-form-item" >
    <label class="layui-form-label"></label>
    <div class="layui-form-mid" >Connect Timeout</div>
    <div class="layui-input-inline" style="width:100px">
    	<input type="number" id="conn_to" name="conn_to" value="<%=conn_to %>"  class="layui-input">
    </div>
  
  </div>
  </div>
  
  <div class="layui-form-item">
    <label class="layui-form-label">
    	Process
    </label>
    <div class="layui-input-inline" style="width:150px">
      <select id="data_pro_tp" lay-filter="data_pro_tp" >
<%
for(ConnPtMultiTcpMSG.TcpDataPro tdp:ConnPtMultiTcpMSG.ALL_DATAPROS)
{
%>
    		<option value="<%=tdp.getTP()%>"><%=tdp.getTPT() %></option>
<%
} //<option value="ln">Str Line</option>
%>
    		
    	</select>
	</div>
	<div class="layui-form-mid">Max Length</div>
    <div class="layui-input-inline" style="width:100px">
      <input type="number" id="max_len" name="max_len" value="<%=max_len%>"  class="layui-input">
    </div>
  </div>
  
  <div class="layui-form-item" id="data_pro_tp_sp">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid">Start (HEX)</div>
    <div class="layui-input-inline" style="width:120px">
      <input type="text" id="hex_s" name="hex_s" value="<%=hex_s%>"  class="layui-input">
    </div>
    <div class="layui-form-mid">Keep</div>
    <div class="layui-input-inline" style="width:30px;">
      <input type="checkbox" id="keep_s" name="keep_s"  <%=(keep_s?"checked":"") %> class="layui-input" lay-skin="primary">
    </div>
    <div class="layui-form-mid">|----|&nbsp;&nbsp;&nbsp;&nbsp; End (HEX)</div>
	   <div class="layui-input-inline" style="width:120px">
      <input type="text" id="hex_e" name="hex_e" value="<%=hex_e%>"  class="layui-input">
    </div>
    <div class="layui-form-mid">Keep</div>
    <div class="layui-input-inline" style="width:30px;">
      <input type="checkbox" id="keep_e" name="keep_e" <%=(keep_e?"checked":"") %> class="layui-input" lay-skin="primary">
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

var tcp_run_tp = "<%=tcp_run_tp%>" ;
var data_pro_tp = "<%=data_pro_tp%>";

var tcp_run_c = <%=tcp_run_c%> ;

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
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
	  form.on('select(tcp_run_tp)', function(obj){
		       setDirty();
		  	update_tcp_run();
		  });
	  form.on('select(data_pro_tp)', function(obj){
		       setDirty();
		  	update_data_pro();
		  });
	  
	  $("#tcp_run_tp").val(tcp_run_tp) ;
	  $("#data_pro_tp").val(data_pro_tp) ;
	  
	  update_tcp_run();
	  update_data_pro();
	  
	  form.render(); 
});

function update_tcp_run_c()
{
	let tmps = "" ;
	if(tcp_run_c && tcp_run_c.cis)
	{
		for(let ci of tcp_run_c.cis)
		{
			tmps += `<span class="remote_item">\${ci.host}:\${ci.port} <button onclick="rm_tcp_run_c_item('\${ci.host}',\${ci.port})">X</button></span>` ;
		}
	}
	$("#clients_list").html(tmps) ;
}

function get_tcp_run_c_item(host,port)
{
	if(!tcp_run_c || !tcp_run_c.cis)
		return  null;
	for(let i = 0 ; i < tcp_run_c.cis.length ; i ++)
	{
		let ob = tcp_run_c.cis[i] ;
		if(ob.host==host && ob.port==port)
		{
			return ob ;
		}
	}
	return null ;
}

function rm_tcp_run_c_item(host,port)
{
	if(!tcp_run_c || !tcp_run_c.cis)
		return  ;
	for(let i = 0 ; i < tcp_run_c.cis.length ; i ++)
	{
		let ob = tcp_run_c.cis[i] ;
		if(ob.host==host && ob.port==port)
		{
			tcp_run_c.cis.splice(i,1) ;
			update_tcp_run_c();
			return ;
		}
	}
}


function set_addr_tcp_run_c()
{
	let h = $("#remote_host").val() ;
	let p = get_input_val("remote_port",-1,true) ;
	if(!h || p<=0 || p>65535)
	{
		dlg.msg("Please input remote host and port (1-65535)!") ;
		return ;
	}
	
	let old = get_tcp_run_c_item(h,p);
	if(old)
	{
		dlg.msg(h+":"+p+" is already existed") ;
		return ;
	}
	let cis = null ;
	if(!tcp_run_c)
	{
		cis = [] ;
		tcp_run_c = {cis:cis}
	}
	if(!tcp_run_c.cis)
	{
		tcp_run_c.cis = cis = [] ;
	}
	tcp_run_c.cis.push({host:h,port:p}) ;
	update_tcp_run_c();
}


function update_tcp_run()
{
	let tp = $("#tcp_run_tp").val() ;
	//console.log(tp);
	if(tp=='s')
	{
		$("#tcp_run_tp_s").css("display","") ;
		$("#tcp_run_tp_c").css("display","none") ;
	}
	else
	{
		$("#tcp_run_tp_s").css("display","none") ;
		$("#tcp_run_tp_c").css("display","") ;
		
		update_tcp_run_c() ;
	}
	//form.render(); 
}

function update_data_pro()
{
	let tp = $("#data_pro_tp").val() ;
	//console.log(tp);
	if(tp=='sp')
	{
		$("#data_pro_tp_sp").css("display","") ;
		
	}
	else
	{
		$("#data_pro_tp_sp").css("display","none") ;
	}
	//form.render(); 
}

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

function get_server_input()
{
	let loc_ip = $('#loc_ip').val();
	let recv_broken_to = get_input_val("recv_broken_to",60000,true) ;
	let port = get_input_val("loc_port",-1,true);
	if(port<=0)
	{
		cb(false,'<wbt:g>pls,input</wbt:g>Local Port') ;
		return ;
	}
	let max_c = get_input_val("max_c",-1,true);
	if(max_c<=0)
	{
		cb(false,'<wbt:g>pls,input</wbt:g>Max Conn Num') ;
		return ;
	}
	return {_tp:"s",recv_broken_to:recv_broken_to,loc_ip:loc_ip,loc_port:port,max_c:max_c} ;
}

function get_clients_input(cb)
{
	let recv_broken_to = get_input_val("recv_broken_to",60000,true) ;
	let conn_to = get_input_val("conn_to",3000,true) ;
	if(!tcp_run_c || !tcp_run_c.cis || tcp_run_c.cis.length<=0)
	{
		cb(false,'<wbt:g>pls,input</wbt:g> remote host:port aleast 1') ;
		return ;
	}
	
	return {_tp:"c",recv_broken_to:recv_broken_to,conn_to:conn_to,cis:tcp_run_c.cis};
}

function get_data_pro_sp_input(cb)
{
	let hex_s = $('#hex_s').val();
	let keep_s = $("#keep_s").prop("checked") ;
	
	let hex_e = $('#hex_e').val();
	let keep_e = $("#keep_e").prop("checked") ;
	
	let max_len = get_input_val("max_len",-1,true);
	if(max_len<=0)
	{
		cb(false,'<wbt:g>pls,input</wbt:g>Max Length') ;
		return ;
	}
	return {_tp:"sp",hex_s:hex_s,keep_s:keep_s,hex_e:hex_e,keep_e:keep_e,max_len:max_len} ;
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
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	let tcp_run_tp = $("#tcp_run_tp").val() ;
	let tcp_run = null ;
	if(tcp_run_tp=='s')
		tcp_run = get_server_input(cb);
	else if(tcp_run_tp=='c')
		tcp_run = get_clients_input(cb) ;
	
	if(tcp_run==null)
	{
		//cb(false,"no tcp run type found") ;
		return ;
	}
	
	let data_pro_tp = $("#data_pro_tp").val() ;
	let data_pro = null ;
	if(data_pro_tp=='sp')
	{
		data_pro = get_data_pro_sp_input(cb) ;
		if(data_pro==null)
		{
			cb(false,"no data pro found") ;
			return ;
		}
	}
	else
	{
		data_pro = {_tp:data_pro_tp} ;
	}
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,
		tcp_run:tcp_run,data_pro:data_pro});
}

</script>
</html>