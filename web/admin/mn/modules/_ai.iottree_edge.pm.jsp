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
	org.iottree.ext.ai.*,org.iottree.ext.ai.edge.*,
	org.iottree.core.msgnet.nodes.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid","itemid"))
		return ;
	
	String container_id = request.getParameter("container_id") ;
UAPrj prj = UAManager.getInstance().getPrjById(container_id) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}
MNManager mnm = MNManager.getInstance(prj) ;
String netid = request.getParameter("netid") ;
MNNet net = mnm.getNetById(netid) ;
if(net==null)
{
	out.print("no net found") ;
	return ;
}
String itemid = request.getParameter("itemid") ;
IOTTreeEdge_M node = (IOTTreeEdge_M)net.getModuleById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

String host = node.getEdgeHost() ;
int port = node.getEdgePort() ;
List<IOTTreeEdge_M.CamIP> nd_camips = node.listCamIP() ;
List<IOTTreeEdge_M.CamLoc> nd_camlocs = node.listCamLoc() ;
JSONArray cameras = new JSONArray() ;
JSONArray node_camips = new JSONArray() ;

if(nd_camlocs!=null)
{
	for(IOTTreeEdge_M.CamLoc cip:nd_camlocs)
	{
		JSONObject job = cip.toJO();
		job.put("tp","loc") ;
		cameras.put(job) ;
	}
}

if(nd_camips!=null)
{
	for(IOTTreeEdge_M.CamIP cip:nd_camips)
	{
		JSONObject job = cip.toJO();
		job.put("tp","ip") ;
		node_camips.put(job) ;
		cameras.put(job);
	}
}

%>
<style>
.tb_cameras {font-size:12px;width:100%;}
.tb_cameras td {border:1px solid ;}
.tb_cameras thead {font-weight:bold;}
.url_ppt {color:red;}
</style>

<div class="layui-form-item">
    <label class="layui-form-label">Edge Host</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" id="edge_host" class="layui-input" value="<%=host%>"/>
    </div>
    <div class="layui-form-mid">Port</div>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="edge_port" class="layui-input" value="<%=port%>"/>
    </div>
    <div class="layui-form-mid"><button class="layui-btn layui-btn-sm layui-btn-primary" onclick="refresh_edge()"><i class="fa fa-refresh" onclick=""></i></button></div>
  </div>

<div class="layui-form-item">
    <label class="layui-form-label">Edge Cameras </label>
    <div class="layui-input-inline" style="width:650px;height:110px;overflow-y:auto;border:1px solid #ccc">
      <table class="tb_cameras">
      	<thead>
      		<tr>
      			<td>ID</td>
      			<td>Title</td>
      			<td>Type</td>
      			<td>PM</td>
      			<td> 
      			<%--Oper: <button onclick="camera_edit()"><i class="fa fa-plus"></i></button> --%>
      			</td>
      		</tr>
      	</thead>
      	<tbody  id="cam_list" >
      	</tbody>
      </table>
    </div>
</div>
<br>
<div class="layui-form-item">
    <label class="layui-form-label">IP Cameras </label>
    <div class="layui-input-inline" style="width:650px;height:230px;overflow-y:auto;border:1px solid #ccc">
      <div style="width:645px;border:1px solid #ccc;height:20px;background-color: #aaa;padding:1px;text-align: right;">
      	<button onclick="node_camip_syn()"><i class="fa fa-arrow-right"></i>synchronize to edge</button>
      </div>
      <table class="tb_cameras">
      	<thead>
      		<tr>
      			<td>ID</td>
      			<td>Title</td>
      			<td>Url</td>
      			<td>Oper: <button onclick="node_camip_edit()"><i class="fa fa-plus"></i></button></td>
      		</tr>
      	</thead>
      	<tbody  id="node_camip_list" >
      	</tbody>
      </table>
    </div>
  </div>
<%--
<div class="layui-form-item">
    <label class="layui-form-label">LLM Model</label>
    <div class="layui-input-inline" style="width:450px;">
      <select id="model_name">
      	<option></option>
      </select>
    </div>
    <div class="layui-form-mid"><button onclick="update_models()"><i class="fa fa-refresh"></i></button></div>
</div>
--%>

<script>

var cameras = <%=cameras%>;
console.log(cameras);
var node_camips = <%=node_camips%>;

function update_node_camips()
{
	let ss = "" ;
	for(let c of node_camips)
	{
		let cid = c.id||"";
		if(!cid)
			continue ;
		ss += `<tr>
  			<td>\${c.id}</td>
  			<td>\${c.t}</td>
  			<td>\${c.u}</td>
  			<td>`;
  		if(cid.indexOf("ip_")==0)
  		{
  			ss += `<button><i class="fa fa-pencil" onclick="node_camip_edit('\${cid}')"></i></button>
  				<button><i class="fa fa-times" onclick="node_camip_del('\${cid}')"></i></button>
  				`
  		}
  		ss += `</td></tr>`;
	}
	$("#node_camip_list").html(ss) ;
}

update_node_camips();

function get_node_camip(id)
{
	for(let c of node_camips)
		if(c.id==id)
			return c;
	return null;
}

function node_camip_edit(id)
{
	event.preventDefault();
	let tt = "Add Camera";
	let ob = null ;
	if(id)
	{
		tt = "Edit Camera"
		ob = get_node_camip(id);
		if(ob==null)
		{
			dlg.msg("no camera found") ;
			return ;
		}
	}
	else
		id="";
	dlg.open("./util/iottree_edge_cam_edit.jsp?id="+id,
			{title:tt,node_inp:ob},['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_node_submit((bok,ob)=>{
						if(!bok)
						{
							dlg.msg(ob);return ;
						}
						set_node_camip(ob)
						dlg.close();
					}) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function set_node_camip(ob)
{
	let id = ob.id||"";
	if(!id) return false;
	let old = get_node_camip(id);
	if(old)
		Object.assign(old,ob) ;
	else
		node_camips.push(ob);
	update_node_camips();
}

function node_camip_del(id)
{
	let old = get_node_camip(id);
	if(!old) return;
	let idx = node_camips.indexOf(old);
	node_camips.splice(idx, 1);
	update_node_camips();
}

function node_camip_syn()
{
	event.preventDefault();
	let u = get_camera_url();
	if(!u){dlg.msg("no edge host set");return ;}
	
	u = `\${u}/syn_camera_ips`;
	dlg.confirm('After synchronization, all previous configurations of Edge will be overwritten. Please confirm',
			{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>confirm</wbt:g>"},function ()
		    {
				send_ajax_json(u,node_camips,(bsucc,ret)=>{
					if(!bsucc)
					{
						dlg.msg(ret) ;
						return;
					}
					refresh_edge();
					dlg.msg("syn ok");
				})
			});
}

function get_camera(id)
{
	for(let c of cameras)
		if(c.id==id)
			return c;
	return null;
}

function camera_edit(id)
{
	event.preventDefault();
	let tt = "Add Camera";
	let ob = null ;
	if(id)
	{
		tt = "Edit Camera"
		ob = get_camera(id);
		if(ob==null)
		{
			dlg.msg("no camera found") ;
			return ;
		}
	}
	else
		id="";
	dlg.open("./util/iottree_edge_cam_edit.jsp?id="+id,
			{title:tt,input:ob},['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bok,ret)=>{
						if(!bok)
						{
							dlg.msg(ret);return ;
						}
						ret.id = id ;
						set_camera(ret,(bset_ok,res)=>{
							console.log(res);
							if(bset_ok && res.success)
							{
								dlg.msg(res.message||"set ok") 
								dlg.close() ;
							}
							else
								dlg.msg(res.message||"error") ;
						}) ;
						
					}) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function set_camera(ob,cb)
{
	console.log(ob) ;
	let u = get_camera_url();
	if(!u) return ;
	
	u = `\${u}/set_camera`;
	send_ajax_json(u,ob,(bsucc,ret)=>{
		if(!bsucc)
		{
			cb(false,ret) ;
			return;
		}
		refresh_edge();
		cb(true,ret);
	})
}

function del_camera(id)
{
	
}

function camera_show(id)
{
	let u = get_camera_url()
	if(!u) return;
	window.open(u+"/detail?camera_id="+id) ;
}

function update_edge()
{
	let ss = "" ;
	for(let c of cameras)
	{
		let cid = c.id||"";
		if(!cid)
			continue ;
		ss += `<tr>
  			<td>\${c.id}</td>
  			<td>\${c.t}</td>
  			<td>\${c.tp}</td>
  			<td>\${c.u||""}</td>
  			<td><button onclick="camera_show('\${cid}')"><i class="fa-regular fa-paper-plane"></i></button>`;
  		if(cid.indexOf("ip_")==0)
  		{
  			ss += `<button><i class="fa fa-pencil" onclick="camera_edit('\${cid}')"></i></button>
  				<button><i class="fa fa-times" onclick="del_camera('\${cid}')"></i></button>
  				`
  		}
  		ss += `</td></tr>`;
	}
	$("#cam_list").html(ss) ;
}

function get_camera_url()
{
	let h = $("#edge_host").val() ;
	let p = get_input_val("edge_port",-1,true);
	if(!h || p<=0)
	{
		dlg.msg("no valid edge host port set");return null;
	}
	return `http://\${h}:\${p}/camera`;
}

function refresh_edge()
{
	let u = get_camera_url();
	if(!u) return ;
	u = `\${u}/list_cameras`;
	send_ajax(u,{},(bsucc,ret)=>{
		if(!bsucc)
		{
			dlg.msg(`read edge \${u} error:`+ret);return;
		}
		cameras = ret.cameras||[]
		console.log(cameras)
		update_edge();
	})
}

function on_after_pm_show(form)
{
	refresh_edge();
}

update_edge();

function get_pm_jo()
{
	let edge_host = $("#edge_host").val();
	let edge_port = get_input_val("edge_port",9091,true) ;
	//let model_name =  $("#model_name").val();
	let cam_locs = [];
	if(cameras)
	{
		for(let cam of cameras)
		{
			if(cam.tp=='loc')
				cam_locs.push(cam) ;
		}
	}
	console.log(cam_locs) ;
	return {edge_host:edge_host,edge_port:edge_port,cam_locs:cam_locs,cam_ips:node_camips} ;
}

function set_pm_jo(jo)
{
	
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>