<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.node.*,
				org.iottree.core.router.*,
				org.iottree.ext.kafka.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
	
	String prjid = request.getParameter("prjid") ;
	String id = request.getParameter("id") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}

RouterManager rmgr = RouterManager.getInstance(prj) ;
ROAKafka roa = null ;
if(Convert.isNotNullEmpty(id))
{
	roa = (ROAKafka)rmgr.getOuterAdpById(id) ;
	if(roa==null)
	{
		out.println("no ROA found with id="+id) ;
		return ;
	}
}
else
	roa = new ROAKafka(rmgr) ;

boolean benable = roa.isEnable();
String chk_en = "" ;
if(benable)
	chk_en = "checked" ;

String name =roa.getName() ;
String title = roa.getTitle() ;
String host = roa.getBrokerHost();
int port = roa.getBrokerPort() ;

int conn_to = 30;
String user = roa.getUser();
String psw = roa.getPsw() ;
//int conn_int = 60;

//long push_int = 10000 ;


JSONObject js_ob = roa.toJO() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(750,500);
</script>
<style>
.layui-form-label
{
	width:120px;
}
.conf
{
	position: relative;
	width:90%;
	height:30px;
	border:1px solid ;
}

.conf .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:7px;
}
.pclist
{
border:1px solid;border-color:#d2d2d2;overflow: auto;width:200px;height:150px;
}
</style>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>name</w:g>:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off"  class="layui-input" <%=Convert.isNotNullEmpty(name)?"readonly":"" %>>
    </div>
    <div class="layui-form-mid"><w:g>title</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><w:g>enable</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="en" name="en" <%=chk_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
 </div>

  <%--
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-block">
      <input type="radio" name="share_tp" value="" title="Not share">
      <input type="radio" name="share_tp" value="mqtt" title="MQTT">
      <input type="radio" name="share_tp" value="rt_conn" title="rt conn">
    </div>
    --%>
    <div id="edit_mqtt">
     <div class="layui-form-item">
    <label class="layui-form-label">Broker <wbt:g>host</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="host" name="host" value="<%=host%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>port</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="number" id="port" name="port" value="<%=port%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 <div class="layui-form-mid"><wbt:g>conn,timeout</wbt:g>(<wbt:g>second</wbt:g>):</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    
	    <input type="number" id="mqtt_conn_to" name="mqtt_conn_to" value="<%=conn_to%>"  title="seconds" autocomplete="off" class="layui-input">
	  </div>
  </div>

  <div class="layui-form-item">
    <label class="layui-form-label">Broker <wbt:g>user</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="mqtt_user" name="mqtt_user" value="<%=user%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>psw</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="mqtt_psw" name="mqtt_psw" value="<%=psw%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>producer</wbt:g></label>
    <div class="layui-input-inline pclist" id="send_confs" >
      &nbsp;
    </div>
    <div class="layui-form-mid"><button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_prod()">+<wbt:g>add</wbt:g></button></div>
    <div class="layui-form-mid"><wbt:g>consumer</wbt:g></div>
    <div class="layui-input-inline pclist" id="recv_confs" >
      &nbsp;
    </div>
    <div class="layui-form-mid"><button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_cons()">+<wbt:g>add</wbt:g></button></div>
  </div>
  
  
    </div>

</form>
</body>
<script type="text/javascript">

var prjid="<%=prjid%>";

var js_ob = <%=js_ob%> ;

var cur_prod_ob = null ;
var cur_cons_ob = null ;

layui.use('form', function(){
	  var form = layui.form;
	  
	  update_ui() ;
	  
	  form.render();
	});

function get_send_conf(id)
{
	if(!id) return null ;
	if(!js_ob.send_confs)
		return null ;
	for(let ob of js_ob.send_confs)
	{
		if(ob.id==id) return ob ;
	}
	return false;
}
function get_recv_conf(id)
{
	if(!id) return null ;
	if(!js_ob.recv_confs)
		return null ;
	for(let ob of js_ob.recv_confs)
	{
		if(ob.id==id) return ob ;
	}
	return false;
}
	
function update_ui()
{
	let tmps = `` ;
	for(let ob of js_ob.send_confs)
	{
		tmps += `<div class="conf" id="\${ob.id}">
			 <span>\${ob.topic}</span>
			 <span>\${ob.t}</span>
			 <span class="oper">
			 	<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_prod('\${ob.id}')"><i class="fa fa-pencil"></i></button>
				<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_prod('\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
			 </span>
			</div>` ;
	}
	$("#send_confs").html(tmps) ;
	
	tmps = `` ;
	for(let ob of js_ob.recv_confs)
	{
		tmps += `<div class="conf" id="\${ob.id}">
			 <span>\${ob.topic}</span>
			 <span>\${ob.t}</span>
			 <span class="oper">
			 	<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_cons('\${ob.id}')"><i class="fa fa-pencil"></i></button>
				<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_cons('\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
			 </span>
			</div>` ;
	}
	$("#recv_confs").html(tmps) ;
}

function add_or_edit_prod(id)
{
	cur_prod_ob = get_send_conf(id) ;
	console.log(id,cur_prod_ob) ;
	let tt = '<wbt:g>add</wbt:g> Producer Parameter';
	if(cur_prod_ob)
		tt = '<wbt:g>edit</wbt:g> Producer Parameter';
	dlg.open("kafka_adp_topic_edit.jsp",
			{title:tt,pm:cur_prod_ob},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 if(!js_ob.send_confs)
							 js_ob.send_confs = [] ;
						 if(cur_prod_ob)
							 Object.assign(cur_prod_ob,ret) ;
						 else
						 	js_ob.send_confs.push(ret) ;
						 cur_prod_ob = ret ;
						 update_ui();
						 dlg.close() ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_prod(id)
{
	if(!js_ob.send_confs)
		return  ;
	let n = js_ob.send_confs.length ;
	for(let i = 0 ; i < n ; i ++)
	{
		let ob = js_ob.send_confs[i] ;
		if(ob.id==id)
		{
			js_ob.send_confs.splice(i,1) ;
			update_ui();
			return ;
		}
	}
}

function del_cons(id)
{
	if(!js_ob.recv_confs)
		return  ;
	let n = js_ob.recv_confs.length ;
	for(let i = 0 ; i < n ; i ++)
	{
		let ob = js_ob.recv_confs[i] ;
		if(ob.id==id)
		{
			js_ob.recv_confs.splice(i,1) ;
			update_ui();
			return ;
		}
	}
}

function add_or_edit_cons(id)
{
	cur_cons_ob = get_recv_conf(id) ;
	let tt = '<wbt:g>add</wbt:g> Consumer Parameter';
	if(cur_cons_ob)
		tt = '<wbt:g>edit</wbt:g> Consumer Parameter';
	dlg.open("kafka_adp_topic_edit.jsp",
			{title:tt,pm:cur_cons_ob},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 if(!js_ob.recv_confs)
							 js_ob.recv_confs = [] ;
						 if(cur_cons_ob)
							 Object.assign(cur_cons_ob,ret) ;
						 else
						 	js_ob.recv_confs.push(ret) ;
						 cur_cons_ob = ret ;
						 update_ui();
						 dlg.close() ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let n = $("#name").val() ;
	if(!n)
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	let t =  $("#title").val() ;
	if(!t) t = "" ;
	var ben = $("#en").prop("checked") ;
	
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
	
	
	var user = $('#user').val();
	if(user==null||user=='')
	{
		//cb(false,'Please input Opc Id User') ;
		user="";
		//return ;
	}
	
	var psw = $('#psw').val();
	if(psw==null||psw=='')
	{
		psw="";
	}
	
	js_ob.en= ben ;
	js_ob.n = n ;
	js_ob.t = t ;;
	js_ob.host = host ;
	js_ob.port = port ;
	js_ob.user = user ;
	js_ob.psw = psw ;
	cb(true,js_ob) ;
}

</script>
</html>