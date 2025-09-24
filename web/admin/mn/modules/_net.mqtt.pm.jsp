<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<div class="layui-form-item">
    <label class="layui-form-label">Broker <w:g>host</w:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="host" name="host" value=""  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><w:g>port</w:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="number" id="port" name="port" value=""  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 <div class="layui-form-mid"><w:g>conn,timeout</w:g>(<w:g>second</w:g>):</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    
	    <input type="number" id="to_sec" name="to_sec" value=""  title="seconds" autocomplete="off" class="layui-input">
	  </div>
  </div>

  <div class="layui-form-item">
    <label class="layui-form-label">Broker <w:g>user</w:g>:</label>
    <div class="layui-input-inline" style="width: 120px;">
      <input type="text" id="user" name="user" value=""  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><w:g>psw</w:g>:</div>
	  <div class="layui-input-inline" style="width: 120px;">
	    <input type="text" id="psw" name="psw" value=""  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 <div class="layui-form-mid"><w:g>keep_intv</w:g>(<w:g>second</w:g>):</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    
	    <input type="number" id="keep_intv" name="keep_intv" value=""  title="seconds" autocomplete="off" class="layui-input">
	  </div>
	  
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label"><w:g>producer</w:g></label>
    <div class="layui-input-inline pclist" id="send_confs"  style="width:250px;border:1px solid #ccc;height:250px;">
      &nbsp;
    </div>
    <div class="layui-form-mid"><button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_prod()">+<w:g>add</w:g></button></div>
    <div class="layui-form-mid"><w:g>consumer</w:g></div>
    <div class="layui-input-inline pclist" id="recv_confs" style="width:250px;border:1px solid #ccc;height:250px;">
      &nbsp;
    </div>
    <div class="layui-form-mid"><button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_cons()">+<w:g>add</w:g></button></div>
  </div>

<script>

var js_ob = {} ;
var cur_prod_ob=  null ;
var cur_cons_ob = null ;

function on_after_pm_show(form)
{
	update_ui();
}

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
	if(js_ob.send_confs)
	{
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
	}
	
	$("#send_confs").html(tmps) ;
	
	tmps = `` ;
	if( js_ob.recv_confs)
	{
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
	}
	
	$("#recv_confs").html(tmps) ;
}

function add_or_edit_prod(id)
{
	cur_prod_ob = get_send_conf(id) ;
	//console.log(id,cur_prod_ob) ;
	let tt = '<w:g>add</w:g> Producer Parameter';
	if(cur_prod_ob)
		tt = '<w:g>edit</w:g> Producer Parameter';
	dlg.open(`\${PM_URL_BASE}/util_topic_edit.jsp`,
			{title:tt,pm:cur_prod_ob},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
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
	let tt = '<w:g>add</w:g> Consumer Parameter';
	if(cur_cons_ob)
		tt = '<w:g>edit</w:g> Consumer Parameter';
	dlg.open(`\${PM_URL_BASE}/util_topic_edit.jsp`,
			{title:tt,pm:cur_cons_ob},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
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
	

function get_pm_jo()
{
	let host = $('#host').val();
	if(host==null||host=='')
	{
		return '<w:g>pls,input,host</w:g>' ;
	}
	let port = get_input_val('port',-1,true);
	if(port<=0)
	{
		return '<w:g>pls,input,port</w:g>' ;
	}
	
	var user = $('#user').val();
	if(user==null||user=='')
	{
		user="";
	}
	
	var psw = $('#psw').val();
	if(psw==null||psw=='')
	{
		psw="";
	}
	js_ob.to_sec = get_input_val("to_sec",30,true) ;
	js_ob.host = host ;
	js_ob.port = port ;
	js_ob.user = user ;
	js_ob.psw = psw ;
	console.log(JSON.stringify(js_ob));
	return js_ob ;
}

function set_pm_jo(jo)
{
	$('#host').val(jo.host||"");
	$('#port').val(jo.port||1883);
	$("#to_sec").val(jo.to_sec||30) ;
	$("#send_to").val(jo.send_to||1000);
	$("#user").val(jo.user||"");
	$("#psw").val(jo.psw||"");
	js_ob = jo ;
}

function get_pm_size()
{
	return {w:800,h:500} ;
}

//on_init_pm_ok() ;
</script>