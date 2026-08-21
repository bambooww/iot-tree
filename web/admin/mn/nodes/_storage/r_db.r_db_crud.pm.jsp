<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,org.iottree.core.store.gdb.autofit.*,
	org.iottree.core.msgnet.util.*,org.iottree.core.msgnet.modules.*,
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
	RelationalDB_CRUD item =(RelationalDB_CRUD)net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	
%>
<button onclick="read_tb_cols()">read columns from db</button> 
<%
for(Map.Entry<String,JavaTableInfo> n2jti:RelationalDB_CRUD.getRegisteredName2JTI().entrySet())
{
	String r_name = n2jti.getKey() ;
%><button onclick="read_tb_cols_reg('<%=r_name%>')">read:<%=r_name %></button><%
}
%>
<div class="layui-form-item">
    <label class="layui-form-label"><span id='lb_col'>Columns</span></label>
    <div class="layui-input-inline" style="width:800px;" >
      <div id="cols" style="max-height:280px;overflow-y:auto;border:0px solid red">
      </div>
    </div>
    <div class="layui-form-mid"></div>
    <div class="layui-input-inline" style="width: 100px;">
      
    </div>
 </div>
 <<style>
.max_len {width:70px;}
.dbc {width:100px;}
.colt {width:100px;}
</style>
<script>
var prjid="<%=prjid%>";
var netid="<%=netid%>";
var itemid="<%=itemid%>";
var crud_nd_uid = "<%=item.CXT_getUID()%>" ;
var all_cols = [];

var val_tp_all = <%=RelationalDB_M.toValTpsAllJArr()%>;
//console.log(val_tp_all)
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

function update_ui()
{
	let ss = `<table style='width:100%' class="tb_cols layui-table">
		<tr><td>Column</td>
		<td>Val TP</td>
		<td>Title</td>
		<td>Max Len</td><td>Unique</td><td>Index</td><td>Pk</td>
		<td>Nullable</td>
		<td><i class="fa fa-plus" onclick="add_row()"></i></td></tr>` ;
	for(let col of all_cols)
	{
		let unique_chk = col.unique?"checked":"";
		let idx_chk= col.idx?"checked":"";
		let pk_chk = col.pk?"checked":"";
		let null_chk = col.nullable?"checked":"";
		ss += `<tr class='rowitem'>
			<td><input class="dbc" value="\${col.coln||''}"/></td>
			<td><select class="val_tp" lay-ignore>\${calc_vt_opts(col.val_tp)}</select></td>
			<td><input class="colt" value="\${col.colt||''}"/></td>
			<td><input class="max_len" value="\${col.max_len||""}" type="number"/></td>
			<td><input class="unique" type="checkbox" lay-skin="primary" \${unique_chk} /></td>
			<td><input class="idx" type="checkbox" lay-skin="primary" \${idx_chk} /></td>
			<td><input class="pk" type="checkbox" lay-skin="primary" \${pk_chk} /></td>
			<td><input class="nullable" type="checkbox" lay-skin="primary" \${null_chk} /></td>
			<td><i class="fa fa-times" onclick="del_row(this)"></i></td></tr>` ;
	}
	ss += "</table>"
	$("#cols").html(ss) ;
	form.render();
}

function del_row(ele)
{
	let p = $(ele).parent().parent();
	p.remove();
}

function add_row()
{
	let ss = extract_cols()
	//console.log(ss) ;
	ss.push({});
	all_cols=ss
	update_ui();
}

function extract_cols()
{
	let ret=[];
	$(".rowitem").each(function(){
		let tr = $(this) ;
		let val_tp = tr.find('.val_tp').val();
		let coln = tr.find('.dbc').val();
		let colt = tr.find(".colt").val() ;
		let max_len = parseInt(tr.find('.max_len').val());
		if(isNaN(max_len))
			max_len = -1 ;
		let unique = tr.find(".unique").prop("checked") ;
		let idx = tr.find(".idx").prop("checked") ;
		let pk = tr.find(".pk").prop("checked") ;
		let nullable = tr.find(".nullable").prop("checked") ;
		ret.push({coln:coln,colt:colt,val_tp:val_tp,max_len:max_len,
			unique:unique,idx:idx,pk:pk,nullable:nullable});
	})
	return ret;
}

function check_cols(cols)
{
	if(cols==null||cols.length<=0)
		return "no columns set";
	let colns = [] ;
	for(let col of cols)
	{
		if(!col.coln)
			return "DB Column cannot empty" ;
		if(colns.indexOf(col.coln)>=0)
			return "DB Column "+col.coln+" is duplicated" ;
		colns.push(col.coln);
	}
	return true;
}

function read_tb_cols()
{
	dlg.confirm('<w:g>will_over_exists</w:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>overwrite,confirm</wbt:g>"},function ()
	{
		let pm = {op:"read_cols_from_db",container_id:prjid,netid:netid,itemid:itemid} ;
		send_ajax(PM_URL_BASE+"/r_db.r_db_crud.ajax.jsp",pm,(bsucc,ret)=>{
			if(!bsucc||ret.indexOf("[")!=0)
			{
				dlg.msg(ret);return ;
			}
			let cols = null ;
			eval("cols="+ret) ;
			//console.log(cols) ;
			if(cols&&cols.length>0)
			{
				all_cols = cols ;
				update_ui() ;
			}
		})
	});
}

function read_tb_cols_reg(r_name)
{
	dlg.confirm('<w:g>will_over_exists</w:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>overwrite,confirm</wbt:g>"},function ()
			{
				let pm = {op:"read_cols_from_reg",container_id:prjid,netid:netid,itemid:itemid,name:r_name} ;
				send_ajax(PM_URL_BASE+"/r_db.r_db_crud.ajax.jsp",pm,(bsucc,ret)=>{
					if(!bsucc||ret.indexOf("[")!=0)
					{
						dlg.msg(ret);return ;
					}
					let cols = null ;
					eval("cols="+ret) ;
					//console.log(cols) ;
					if(cols&&cols.length>0)
					{
						all_cols = cols ;
						update_ui() ;
					}
				})
			});
}
		
function on_after_pm_show(form)
{
	update_ui();
}


function get_pm_jo()
{
	let ret = {} ;
	ret.cols = extract_cols();
	let chkr = check_cols(ret.cols);
	if(chkr!==true)
		return chkr;
	return ret ;
}

function set_pm_jo(jo)
{
	all_cols = jo.cols||[];
}

function get_pm_size()
{
	return {w:950,h:450} ;
}
var cc = 0 ;
$("#lb_col").on("click",()=>{
	cc ++ ;if(cc>=5){cc =0;open("/ent/util/crud_code_g.jsp?uid="+crud_nd_uid);}
})
//on_init_pm_ok() ;
</script>