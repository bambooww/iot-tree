<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.store.*,
	org.iottree.core.msgnet.*,
	org.iottree.ext.vo.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<style>
.k {width:100px;}
.v {width:390px;}
td {font-size:12px;}
td button{height:20px;}
tr {height:20px;line-height: 1.2; }
.err {border:1px solid red;margin:2px;}
</style>
<div class="layui-form-item">
    <label class="layui-form-label">Language:</label>
    <div class="layui-input-inline" style="width:250px;">
      <select id="lang_m">
<%
	boolean bfirst = true;
	for(VoReco_NM.ModelItem mi:VoReco_NM.listModelItems().values())
	{
		String n = mi.name ;
		String t = mi.title ;
		String chked = bfirst?"selected":"" ;
		bfirst=false;
%><option value="<%=n %>" <%=chked %>><%=t %></option><%
	}
%>
      </select>
    </div>
    <div class="layui-form-mid">&nbsp;</div>
    <div class="layui-input-inline" style="width:40px;">
    	<input type="checkbox" id="en_limit" lay-skin="switch"  lay-filter="en_limit" lay-skin="primary" />
    </div>
    <div class="layui-form-mid"><w:g>limit,cmd</w:g></div>
</div>

<div class="layui-form-item" id="limit_c" style="display:none">
    <label class="layui-form-label">Limit Words:</label>
    <div class="layui-input-inline" style="width:600px;height:500px;border:0px solid;">
      <div id="words_limit" style="width:602px;height:100px;font-size:12px;border:0px solid #ccc;">
      	<table style="width:602px;height:40px" class0="lay-table">
      		<thead style="border:1px solid green;">
      			<tr style="background-color: #ccc;">
      				<td>Key</td>
      				<td>Related words</td>
      				<td style="width:150px">Oper</td>
      			</tr>
      			<tr>
      				<td><input id="newk" class0="k" /></td>
      				<td><input id="newv" class0="v"  /></td>
      				<td><button onclick="add_row()">&nbsp;<i class="fa fa-plus"></i>&nbsp;</button></td>
      			</tr>
      		</thead>
      		
      	</table>
      	
      	<div style="overflow-y:auto;border:1px solid blue;width:600px;margin-top:3px;height:350px;">
      		<table style="width:580px;display:block;border-collapse: collapse;" id="limit_bd">
      		
      		</table>
      	</div>
      	<div style="overflow-y:auto;border:1px solid red;width:600px;margin-top:3px;height:100px;position: relative;">
      		<div id="err_words" ></div>
      		<button style="position: absolute;right:1px;top:3px;" onclick="chk_words()">Check Words</button>	
      	</div>
      	
      </div>
      
    </div>
    
</div>

<script>

var last_id_cc = 0 ;
function new_id()
{
	last_id_cc++;
    return "x"+last_id_cc;
}


function on_after_pm_show(form)
{
	//update_ui();
	form.on('switch(en_limit)', function(obj){
		show_hide_limit(); 
	});
	form.render();
	show_hide_limit();
}

function show_hide_limit()
{
	let ben = $("#en_limit").prop("checked") ;
	$("#limit_c").css("display",ben?"":"none") ;
}

function add_row()
{
	let k = $("#newk").val() ;
	if(!k)
	{
		dlg.msg("<w:g>pls,input</w:g> Key");
		$("#newk").focus();
		return;
	}
	let row = get_row_by_k(k);
	if(row)
	{
		dlg.msg("Key="+k+" existed");
		$("#newk").focus();
		return;
	}
	let v = $("#newv").val()||"" ;
	set_row(k,v) ;
	
	$("#newk").val("") ;
	$("#newv").val("")
	$("#newk").focus() ;
}

function set_row(k,v)
{
	if(!k) return;
	let id = new_id();
	v = v||"" ;
	let row = get_row_by_k(k) ;
	if(row)
	{
		row.find(".v").val(v) ;
	}
	else
	{
		$("#limit_bd").append(`
				<tr id="\${id}">
					<td><input class="k" value="\${k}" readonly /></td>
					<td><input class="v" value="\${v}"  /></td>
					<td><button onclick="del_row('\${id}')">&nbsp;<i class="fa fa-times"></i>&nbsp;</button></td>
				</tr>
		`);
	}
}

function get_row_by_k(k)
{
	let row = null ;
	$("#limit_bd tr").each(function(){
		let ob = $(this);
		let kv = ob.find(".k").val() ;
		if(k==kv)
			row = ob ;
	});
	return row ;
}

function del_row(id)
{
	$("#"+id).remove();
}

function get_all_rows_kvs()
{
	let kvs=[];
	$("#limit_bd tr").each(function(){
		let tr = $(this) ;
		let k = tr.find(".k").val();
		let v = tr.find(".v").val();
		kvs.push({k:k,v:v}) ;
	});
	return kvs ;
}

function get_pm_jo()
{
	let lang_m = $('#lang_m').val();
	if(!lang_m)
	{
		return '<w:g>pls,select,lang</w:g>' ;
	}
	let en_limit = $("#en_limit").prop("checked");
	let js_ob={} ;
	js_ob.lang_m = lang_m ;
	js_ob.en_limit = en_limit ;
	let kvs=get_all_rows_kvs();
	
	if(js_ob.en_limit)
	{
		if(kvs.length<=0)
			return "no key = related words input";
		let b_words = false;
		for(let kv of kvs)
		{
			if(kv.v) {b_words=true;break;}
		}
		if(!b_words)
			return "no related words input";
	}
	js_ob.limit_kvs = kvs ;
	return js_ob ;
}

function set_pm_jo(jo)
{
	$('#lang_m').val(jo.lang_m||"en_us");
	$("#en_limit").prop("checked",jo.en_limit||false);
	let limit_kvs = jo.limit_kvs||[];
	for(let kv of limit_kvs)
	{
		set_row(kv.k,kv.v)
	}
}

function chk_words()
{
	let lang_m = $('#lang_m').val();
	if(!lang_m)
	{
		dlg.msg('<w:g>pls,select,lang</w:g>') ;
		return;
	}
	let wds=[];
	$("#limit_bd tr").each(function(){
		let tr = $(this) ;
		let v = tr.find(".v").val();
		if(!v) return ;
		wds.push(v) ;
	});
	if(wds.length<=0)
	{
		dlg.msg('no words input') ;
		return;
	}
	dlg.loading(true);
	send_ajax("./nodes/util/vo_reco_ajax.jsp",{op:"chk_err_words",model:lang_m,words:wds.join(',')},(bsucc,ret)=>{
		dlg.loading(false);
		if(!bsucc||ret.indexOf("[")!=0)
		{
			$("#err_words").html(ret) ;return;
		}
		let err_wds= null ;
		eval("err_wds="+ret) ;
		//console.log(err_wds) ;
		if(err_wds.length<=0)
		{
			$("#err_words").html(`<b style='color:blue'>no error words</b>`) ;return;
		}
		let tmps = `<b style='color:red'><w:g>vo_no_word</w:g></b><br>` ;
		for(let wd of err_wds)
		{
			tmps += `<span class="err">\${wd}</span>` ;
		}
		tmps+=`<button onclick="del_no_words()"><w:g>rm_vo_no_words</w:g></button>`;
		$("#err_words").html(tmps);
	});
}

function del_no_words()
{
	let wds = [];
	$("#err_words .err").each(function(){
		let ob = $(this) ;
		let wd = ob.text() ;
		if(!wd) return ;
		wds.push(wd) ;
	}) ;
	let kvs=get_all_rows_kvs();
	if(!kvs||kvs.length<=0)
		return ;
	
	for(let kv of kvs)
	{
		let v = kv.v ;
		if(!v) continue ;
		
		for(let wd of wds)
			v = v.split(wd).join('');
		
		if(v!=kv.v)
			set_row(kv.k,v);
	}
	
}

function get_pm_size()
{
	return {w:700,h:650} ;
}

//on_init_pm_ok() ;
</script>