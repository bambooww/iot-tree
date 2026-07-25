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
	InfluxDB_TagAggr2RDB item =(InfluxDB_TagAggr2RDB)net.getItemById(itemid) ;
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
    <div class="layui-form-mid">Period</div>
    <div class="layui-input-inline" style="width: 150px;">
      <select id="aggr_dt" name="aggr_dt" class="layui-input" >
<%
	for(InfluxDB_TagAggr2RDB.AggrDT dtp:InfluxDB_TagAggr2RDB.AggrDT.values())
	{
%><option value="<%=dtp.getInt()%>"><%=dtp.getTitle() %></option><%
	}
%>
      </select>
    </div>
 </div>
<div class="layui-form-item">
    <label class="layui-form-label">Tag-Column</label>
    <div class="layui-input-inline" style="width:500px;" id="aggr_tags_c">
      
    </div>
    <div class="layui-form-mid"></div>
    <div class="layui-input-inline" style="width: 100px;">
      
    </div>
 </div>
<script>
var prjid="<%=prjid%>";
var prj_path="<%=prj_path%>";

var aggr_tags = [];

function sel_tags()
{	
	dlg.open("../ua_cxt/cxt_tag_selector.jsp?w_only="+false+"&multi=true&path="+prj_path,//+"&val="+tmpv,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:filter_tags},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tagpaths();
					//let txt = dlgw.get_selected_tagtxt();
					//filter_tags = ret ;
					update_ui();
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

var val_tp_all = <%=InfluxDB_TagAggr2RDB.toValTpsAllJArr()%>;
var aggr_tp_all = <%=InfluxDB_TagAggr2RDB.AggrTP.toNameTitleJarr()%>;
function calc_vt_opts(selvt)
{
	let ret="" ;
	for(let vt of val_tp_all)
	{
		let sel = (vt==selvt)?"selected":"" ;
		ret += `<option value="\${vt}" \${sel}>\${vt}</option>` ;
	}
	return ret ;
}
function calc_aggrtp_opts(seltp)
{
	let ret="" ;
	for(let vt of aggr_tp_all)
	{
		let sel = (vt.n==seltp)?"selected":"" ;
		ret += `<option value="\${vt.n}" \${sel}>\${vt.t}</option>` ;
	}
	return ret ;
}

function update_ui()
{
	let ss = `<table style='width:100%' class="status_tb layui-table">
		<tr><td>DB Column</td><td>Tag</td>
		<td>Val TP</td><td>Aggr TP</td><td><i class="fa fa-plus" onclick="add_row()"></i></td></tr>` ;
	for(let agt of aggr_tags)
	{
		ss += `<tr class='rowitem'><td><input class="dbc" value="\${agt.dbc||''}"/></td><td><input class="tagp" value="\${agt.tagp||""}" onclick="sel_tag(this,'r')"/></td>
			<td><select class="val_tp" lay-ignore>\${calc_vt_opts(agt.val_tp)}</select></td>
			<td><select class="aggr_tp" lay-ignore>\${calc_aggrtp_opts(agt.aggr_tp)}</select></td>
			<td><i class="fa fa-times" onclick="del_row(this)"></i></td></tr>` ;
	}
	ss += "</table>"
	$("#aggr_tags_c").html(ss) ;
	form.render();
}

function del_row(ele)
{
	let p = $(ele).parent().parent();
	p.remove();
}

function add_row()
{
	let ss = extract_aggr_tags()
	console.log(ss) ;
	ss.push({});
	aggr_tags=ss
	update_ui();
}

function extract_aggr_tags()
{
	let ret=[];
	$(".rowitem").each(function(){
		let tr = $(this) ;
		let tagp = tr.find('.tagp').val();
		let dbc = tr.find('.dbc').val();
		let aggr_tp = tr.find('.aggr_tp').val();
		let val_tp = tr.find('.val_tp').val();
		
		ret.push({tagp:tagp,dbc:dbc,aggr_tp:aggr_tp,val_tp:val_tp});
	})
	return ret;
}

function chk_aggr_tags(svs)
{
	if(svs==null||svs.length<=0)
		return "no tag - column set";
	let cols=[]
	for(let sv of svs)
	{
		if(!sv.dbc)
			return "DB Column cannot empty" ;
		if(!sv.tagp)
			return "tag cannot empty" ;
		if(cols.indexOf(sv.dbc)>=0)
			return "DB Column "+sv.dbc+" is duplicated" ;
		cols.push(sv.dbc);
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
	//ret.tagp = $("#tagp").val() ;
	ret.aggr_dt = parseInt($("#aggr_dt").val()) ;
	ret.aggr_tags = extract_aggr_tags();
	let chkr = chk_aggr_tags(ret.aggr_tags);
	if(chkr!==true)
		return chkr;
	return ret ;
}

function set_pm_jo(jo)
{
	$('#m').val(jo.m||"");
	//$("#tagp").val(jo.tagp||"") ;
	$("#aggr_dt").val(jo.aggr_dt||0) ;
	aggr_tags = jo.aggr_tags||[];
}

function get_pm_size()
{
	return {w:500,h:450} ;
}

//on_init_pm_ok() ;
</script>