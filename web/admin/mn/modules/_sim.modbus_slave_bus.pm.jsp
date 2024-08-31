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
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	MNManager mnm = MNManager.getInstanceByContainerId(container_id) ;

	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
%>
<style>
.dev_list
{
	position: relative;
	height:200px;
	overflow-y:auto;
	overflow-x: hidden;
	border:1px solid #dddddd;
	left:5%;
	width:90%;
}
.dev_item
{
	position: relative;
	height:50px;
	overflow-y:auto;
	overflow-x: hidden;
	border:1px solid #dddddd;
	left:5%;
	min-width:150px;
	margin-right:10px;
	display:inline-block;
}
.dev_item .op
{
	position: absolute;
	right:5px;top:3px;
}
.conn_list
{
	position: relative;
	height:200px;
	overflow-y:auto;
	overflow-x: hidden;
	border:1px solid #dddddd;
	left:5%;
	width:90%;
}
.op button
{
	width:20px;
	height:20px;
}
</style>
Devices <button class="layui-btn layui-btn-sm layui-btn-primary" onclick="add_edit_dev()">+</button>
<div class="dev_list" id="dev_list">
	
</div>
Connector <button class="layui-btn layui-btn-sm layui-btn-primary" onclick="add_cp_sel()">+</button>
<div class="conn_list" id="conn_list">
</div>
<script>

var container_id="<%=container_id%>";
var devs = [] ;
var cps = [] ;

function get_dev_by_id(id)
{
	for(let dev of devs)
	{
		if(dev.id==id)
			return dev ;
	}
	return null ;
}

function get_dev_by_addr(dev_addr)
{
	for(let dev of devs)
	{
		if(dev.dev_addr==dev_addr)
			return dev ;
	}
	return null ;
}

function del_dev(id)
{
	let cp = get_dev_by_id(id) ;
	if(!cp) return ;
	let cp_idx = devs.indexOf(cp) ;
	devs.splice(cp_idx,1) ;
	update_devs();
}

function add_edit_dev(id)
{
	let tt = "Add Device" ;
	let dev = null ;
	let dev_idx = -1 ;
	if(id)
	{
		tt = "Edit Device" ;
		dev = get_dev_by_id(id) ;
		dev_idx = devs.indexOf(dev) ;
	}
	
	dlg.open(`\${PM_URL_BASE}/ms_bus/ms_dev_edit.jsp?container_id=\${container_id}`,{title:tt,dev:dev},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlgw.get_edit_dev((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						let olddev = get_dev_by_addr(ret.dev_addr);
						if(olddev!=null && olddev!=dev)
						{
							dlg.msg("Device address ="+ret.dev_addr+" existed!") ;
							return ;
						}
						if(dev_idx<0)
							devs.push(ret) ;
						else
							devs[dev_idx]=ret ;
						dlg.close() ;
						update_devs();
					});
					
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function update_devs()
{
	let tmps="" ;
	for(let dev of devs)
	{
		tmps += `<div class="dev_item">
			<div class="t">\${dev.title}</div>
			<div class="addr">Address:\${dev.dev_addr}</div>
			<span class="op">
				<button onclick="add_edit_dev('\${dev.id}')"><i class="fa fa-pencil"></i></button>
				<button onclick="del_dev('\${dev.id}')"><i class="fa fa-times"></i></button>
			</span>
		</div>` ;
	}
	
	$("#dev_list").html(tmps) ;
}


function add_cp_sel()
{
	dlg.open(`\${PM_URL_BASE}/ms_bus/ms_conn_tp_sel.jsp`,
			{title:"<wbt:g>select,type</wbt:g>"},
			['<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				add_edit_cp(ret.tp,ret.tt,null) ;
			});
}

function get_cp_by_id(id)
{
	for(let cp of cps)
	{
		if(cp.id==id)
			return cp ;
	}
	return null ;
}

function del_cp(id)
{
	let cp = get_cp_by_id(id) ;
	if(!cp) return ;
	let cp_idx = cps.indexOf(cp) ;
	cps.splice(cp_idx,1) ;
	update_cps();
}

function add_edit_cp(tp,tpt,id)
{
	let tt = "Add "+tpt ;
	let cp = null ;
	let cp_idx = -1 ;
	if(id)
	{
		tt = "Edit "+tpt ;
		cp = get_cp_by_id(id) ;
		//console.log(id,cp) ;
		cp_idx = cps.indexOf(cp) ;
	}
	
	dlg.open(`\${PM_URL_BASE}/ms_bus/ms_conn_\${tp}_edit.jsp`,{title:tt,cp:cp},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						ret._tp = tp;
						ret._tpt = tpt ;
						if(cp_idx<0)
							cps.push(ret) ;
						else
							cps[cp_idx]=ret ;
						dlg.close() ;
						update_cps();
					});
					
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function update_cps()
{
	let tmps="" ;
	for(let cp of cps)
	{
		tmps += `<div class="dev_item" style="height:35px;min-width:250px;">
			<div class="t">\${cp._tpt}  \${cp.proto} \${cp.enable?"Enabled":"Disabled"}</div>
			<div class="addr">\${cp.tt}</div>
			<span class="op">
				<button onclick="add_edit_cp('\${cp._tp}','\${cp._tpt}','\${cp.id}')"><i class="fa fa-pencil"></i></button>
				<button onclick="del_cp('\${cp.id}')"><i class="fa fa-times"></i></button>
				</span>
		</div>` ;
	}
	$("#conn_list").html(tmps) ;
}

function on_after_pm_show(form)
{
	
}


function update_fm()
{
	
	form.render();
}

function get_pm_jo()
{
	let ret={} ;
	ret.devs = devs ;
	ret.cps = cps ;
	return ret ;
}

function set_pm_jo(jo)
{
	devs = jo.devs||[] ;
	cps = jo.cps||[];
	update_devs();
	update_cps();
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>