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
	org.iottree.core.msgnet.util.*,org.iottree.core.msgnet.store.influxdb.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
	String prjid = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	//IMNContainer
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager mnm= MNManager.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	InfluxDB_TagStDur2RDB item =(InfluxDB_TagStDur2RDB)net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	String prj_path = prj.getNodePath() ;
	MNMsg msg = null;
	if(item instanceof MNNode)
		msg = ((MNNode)item).RT_getLastMsgIn() ;
	if(msg==null)
		msg = new MNMsg() ;
	JSONObject pld = msg.getPayloadJO(null) ;
	LinkedHashMap<String,Object> name2v = new LinkedHashMap<>() ;
	if(pld!=null)
	{
		for(String n:pld.keySet())
		{
			Object obj = pld.get(n) ;
			name2v.put(n,obj) ;
		}
	}
	
%>
<div class="layui-form-item">
    <label class="layui-form-label">Measurement:</label>
    <div class="layui-input-inline" style="width: 250px;">
      <input type="text" id="m" name="m" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Tag</label>
    <div class="layui-input-inline" style="width:300px;">
      <input type="text" id="tagp" name="tagp" onclick="sel_tag(this,'r')" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid">Type</div>
    <div class="layui-input-inline" style="width: 150px;">
      <select id="dur_tp" name="dur_tp" class="layui-input" >
<%
	for(InfluxDB_TagStDur2RDB.DurTP dtp:InfluxDB_TagStDur2RDB.DurTP.values())
	{
%><option value="<%=dtp.getInt()%>"><%=dtp.getTitle() %></option><%
	}
%>
      </select>
    </div>
 </div>
<div class="layui-form-item">
    <label class="layui-form-label">Value-Column</label>
    <div class="layui-input-inline" style="width:500px;" id="status_vals">
      
    </div>
    <div class="layui-form-mid"></div>
    <div class="layui-input-inline" style="width: 100px;">
      
    </div>
 </div>
 
<script>
var prjid="<%=prjid%>";
var prj_path="<%=prj_path%>";

var status_vals = [];

function update_ui()
{
	let ss = `<table style='width:100%' class="status_tb layui-table"><tr><td>Value</td><td>DB Column</td><td><i class="fa fa-plus" onclick="add_sv()"></i></td></tr>` ;
	for(let sv of status_vals)
	{
		ss += `<tr class='rowitem'><td><input class="strv" value="\${sv.strv||""}"/></td><td><input class="dbc" value="\${sv.dbc||''}"/></td>
			<td><i class="fa fa-times" onclick="del_sv(this)"></i></td></tr>` ;
	}
	ss += "</table>"
	$("#status_vals").html(ss) ;
	form.render();
}

function del_sv(ele)
{
	let p = $(ele).parent().parent();
	p.remove();
}

function add_sv()
{
	let ss = extract_status_vals()
	console.log(ss) ;
	ss.push({});
	status_vals=ss
	update_ui();
}

function extract_status_vals()
{
	let ret=[];
	$(".rowitem").each(function(){
		let tr = $(this) ;
		let strv = tr.find('.strv').val();
		let dbc = tr.find('.dbc').val();
		ret.push({strv:strv,dbc:dbc});
	})
	return ret;
}

function chk_status_vals(svs)
{
	if(svs==null||svs.length<=0)
		return "no status value column set";
	let cols=[],vs=[]
	for(let sv of svs)
	{
		if(!sv.dbc)
			return "DB Column cannot empty" ;
		if(!sv.strv)
			return "Value cannot empty" ;
		if(cols.indexOf(sv.dbc)>=0)
			return "DB Column "+sv.dbc+" is duplicated" ;
		if(vs.indexOf(sv.strv)>=0)
			return "Value "+sv.strv+" is duplicated" ;
		cols.push(sv.dbc);
		vs.push(sv.strv) ;
	}
	return true;
}


function sel_tag(ele,rw)
{
	let seltagids = [] ;
	let w_only = "" ;
	if(rw=='r')
	{
	}
	else if(rw=='w')
	{
		w_only = "true" ;
	}
	else
		return ;
	
	dlg.open(`\${ADMIN_URL_BASE}/ua_cxt/di_cxt_tag_selector.jsp?w_only=\${w_only}&multi=false&path=\${prj_path}`,//+"&val="+tmpv,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagids:seltagids},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_select_tag();
					if(!ret)
					{
						dlg.msg("<w:g>pls,select,tag</w:g>");return;
					}
					$(ele).val(ret.tagp) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_after_pm_show(form)
{
	update_ui();
}


function get_pm_jo()
{
	let m = $('#m').val();
	if(!m)
	{
		return '<w:g>pls,input,</w:g> Measurement';
	}
	
	let ret = {m:m} ;
	ret.tagp = $("#tagp").val() ;
	ret.dur_tp = parseInt($("#dur_tp").val()) ;
	ret.st_vals = extract_status_vals();
	let chkr = chk_status_vals(ret.st_vals);
	if(chkr!==true)
		return chkr;
	return ret ;
}

function set_pm_jo(jo)
{
	$('#m').val(jo.m||"");
	$("#tagp").val(jo.tagp||"") ;
	$("#dur_tp").val(jo.dur_tp||0) ;
	status_vals = jo.st_vals||[];
	
		
}

function get_pm_size()
{
	return {w:500,h:450} ;
}

//on_init_pm_ok() ;
</script>