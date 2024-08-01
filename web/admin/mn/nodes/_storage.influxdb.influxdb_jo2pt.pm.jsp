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
	
	InfluxDB_JO2Point jo2pt = (InfluxDB_JO2Point)net.getNodeById(itemid) ;
	if(jo2pt==null)
	{
		out.print("no node item found") ;
		return ;
	}
	
	JSONObject last_jo = jo2pt.RT_getLastMsgJO() ;
%>
<div class="layui-form-item">
    <label class="layui-form-label">Measurement:</label>
    <div class="layui-input-inline" style="width: 228px;">
      <input type="text" id="measurement" name="measurement" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid">Tag1:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag1_name" name="tag1_name" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid">=</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag1_val" name="tag1_val" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Tag2:</label>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag2_name" name="tag2_name" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid">=</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag2_val" name="tag2_val" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"> Tag3:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag3_name" name="tag3_name" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"> =</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag3_val" name="tag3_val" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Timestamp </label>
    <div class="layui-form-mid">Member Name</div>
    <div class="layui-input-inline" style="width: 150px;" id="ts_n_div">
      
    </div>
    <div class="layui-form-mid"> Format:</div>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="text" id="ts_fmt" name="ts_fmt" value=""  autocomplete="off"  class="layui-input" >
    </div>
    
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Fields:<br>
    <button onclick="input_json_txt()">Input JSON</button>
    <button onclick="use_last_msg()">Use last Msg</button>
    </label>
    <div id="fields_cont" class="layui-input-inline" style="width: 550px;height:300px;border:1px solid #e6e6e6;overflow-y: auto">
    	<div></div>
    </div>
   
 </div>
<style>

.f_table
{
	width:100%;
	font-size: 12px;
}
.f_item
{
	
}

.f_item td
{
	border:1px solid;
}

.f_item .fn
{
	width:100%;
}

.f_item:hover
{
	background-color: #e0e0e0;
}

.btn
{
	visibility: hidden;
	width:90%;
}
.f_item:hover .btn
{
	visibility:visible;
}

</style>
<script>

var last_jo = <%=last_jo%>;
var pm_jo = null ;
var using_jo ;
var fields=[];
var taglist_cat ;
var ts_n ;
var ts_fmt;

function get_field_by_name(n)
{
	if(!fields) return null ;
	for(let f of fields)
	{
		if(f.n==n)
			return f;
	}
	return null ;
}

function update_ui()
{
	let tmps ="" ;
	if(using_jo)
	{
		tmps += `<select id="ts_n"><option value=""> --- </option>`;
		for(let n in using_jo)
		{
			let sel = (n==ts_n)?"selected":"" ;
			tmps += `<option value="\${n}" \${sel}>\${n}</option>`;
		}
		tmps += "</select>";
		$("#ts_n_div").html(tmps) ;
		
		tmps = `<table class="f_table">
			  <thead >
			    <tr>
			     <th><input type="checkbox" lay-ignore onclick="sel_all()"/></th>
			     <th>Member Name</th>
			     <th>Value</th>
				 <th>Type</th>
				 <th>Not Null</th> 
				 <th>Field Name<button onclick="on_map_sel()">Map</button></th>
				 <th>Timestamp</th>
				 </tr>
				</thead><tbody>`;
		for(let n in using_jo)
		{
			let f = get_field_by_name(n) ;
			let item_chked ="" ;
			let not_null_chked = "" ;
			let fn= "";
			let vtp = 3 ;
			let v = using_jo[n] ;
			if(f)
			{
				item_chked = f.chked?"checked":"" ;
				not_null_chked = (f.not_null?"checked":"") ;
				fn= f.fn||"";
				vtp = f.vtp||3;
			}
			else
			{
				switch(typeof(v))
				{
				case "number":
					if((""+v).indexOf('.')>=0)
						vtp = 2 ;
					else
						vtp = 1;
					break;
				case "boolean":
					vtp=  4;break ;
				default:
					vtp = 3 ;break ;
				}
			}
			
			tmps += `
			  <tr class="f_item" id="_n_\${n}" mem_n="\${n}">
			     <td><input type="checkbox" id="_ck_\${n}" class="seled" mem_n="\${n}" lay-ignore \${item_chked}/></td>
			     <td>\${n}</td>
			     <td>\${v}</td>
				 <td>
					 <select class='vtp'  lay-ignore>
					 <option value="1" \${(vtp==1?"selected":"")}>integer</option>
					 <option value="2" \${(vtp==2?"selected":"")}>float</option>
					 <option value="3" \${(vtp==3?"selected":"")}>string</option>
					 <option value="4" \${(vtp==4?"selected":"")}>bool</option>
					</select>
				</td>
				<td><input type="checkbox" class="not_null" \${not_null_chked}  lay-ignore/>Not Null</td> 
				 <td><input id="_fn_\${n}" type="text" class="fn" value="\${fn}" /></td>
				 <td class="ts_td"><button class="btn" onclick="set_ts_n('\${n}')" title="set this member as timestamp" >TS</button></td>
				 </tr>`;
		}
		tmps += '</tbody></table>' ;
		$("#fields_cont").html(tmps) ;
		
		form.render() ;
	}
	//else if(fields && fields.length>0)
	//{
	//	
	//}
	else
	{
		$("#fields_cont").html(`<div style="color:red">no msg json input </div>`) ;
	}
}

function sel_all()
{
	$(".seled").prop("checked",true);
}

function read_input_fields()
{
	let ret = [] ;
	$(".f_item").each(function(){
		let item = $(this) ;
		let bsel = item.find(".seled").prop("checked") ;
		let n = item.attr("mem_n") ;
		let vtp = item.find('.vtp').val() ;
		let not_null = item.find(".not_null").prop("checked") ;
		let fn = item.find(".fn").val() ;
		ret.push({chked:bsel,n:n,vtp:vtp,not_null:not_null,fn:fn}) ;
	}) ;
	return ret ;
}

function set_ts_n(n)
{
	 $("#ts_n").val(n) ;
	 form.render() ;
}

function use_last_msg()
{
	if(!last_jo)
	{
		dlg.msg("no last msg") ;
		return ;
	}
	using_jo = last_jo ;
	update_ui() ;
}

function on_map_sel()
{
	if(!using_jo)
	{
		dlg.msg("no left jo members") ;
		return ;
	}
	let left_list = [] ;
	for(let n in using_jo)
	{
		let f = $("#_ck_"+n).prop("checked") ;
		if(f)
			left_list.push(n) ;
	}
		
	
	let u = get_page_url_base()+"util/lr_mapper.jsp?container_id="+container_id;
	let mapped = {} ;
	$(".seled").each(function(){
		if($(this).prop("checked"))
		{
			let n = $(this).attr("mem_n") ;
			let v = $("#_fn_"+n).val() ;
			if(v)
				mapped[n]=v ;
		}
	});
	
	dlg.open(u,{title:"Mapper",left_tt:"Member Name",right_tt:"Tag List",left_list:left_list,mapped:mapped,taglist_cat:taglist_cat},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					let mapped= dlgw.get_mapped() ;
					if(mapped)
					{
						$(".seled").each(function(){
							if($(this).prop("checked"))
							{
								let n = $(this).attr("mem_n") ;
								let v = mapped[n] ||"";
								$("#_fn_"+n).val(v) ;
							}
						});
					}
					taglist_cat = dlgw.get_taglist_cat() ; 
					dlg.close() ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function input_json_txt()
{
	console.log(read_input_fields()) ;
}

function on_after_pm_show(form)
{
	update_ui();
}

function get_pm_jo()
{
	let measurement = $('#measurement').val();
	//if(!measurement)
	//{
	//	return '<w:g>pls,input,</w:g> Measurement';
	//}
	
	let ret = {measurement:measurement} ;
	let n = $("#tag1_name").val() ;
	let v = $("#tag1_val").val() ;
	if(n && v)
	{
		ret.tag1 = {n:n,v:v} ;
	}
	
	n = $("#tag2_name").val() ;
	v = $("#tag2_val").val() ;
	if(n && v)
	{
		ret.tag2 = {n:n,v:v} ;
	}
	
	n = $("#tag3_name").val() ;
	v = $("#tag3_val").val() ;
	if(n && v)
	{
		ret.tag3 = {n:n,v:v} ;
	}
	ret.ts_n = $("#ts_n").val() ;
	ret.ts_fmt = $("#ts_fmt").val() ;
	ret.fields=read_input_fields();
	ret.taglist_cat = taglist_cat ;
	ret.last_msg_jo = using_jo ;
	return ret ;
}

function set_pm_jo(jo)
{
	$('#measurement').val(jo.measurement||"");
	let tag = jo.tag1 ;
	if(tag)
	{
		$("#tag1_name").val(tag.n) ;
		$("#tag1_val").val(tag.v) ;
	}
	tag = jo.tag2 ;
	if(tag)
	{
		$("#tag2_name").val(tag.n) ;
		$("#tag2_val").val(tag.v) ;
	}
	tag = jo.tag3 ;
	if(tag)
	{
		$("#tag3_name").val(tag.n) ;
		$("#tag3_val").val(tag.v) ;
	}
	
	ts_n = jo.ts_n||"";
	ts_fmt = jo.ts_fmt||"";
	$("#ts_n").val(ts_n);
	$("#ts_fmt").val(ts_fmt);
	
	fields = jo.fields||[] ;
	taglist_cat = jo.taglist_cat||"" ;
	pm_jo = jo.last_msg_jo ;
	
	using_jo = pm_jo||last_jo ;
	//if(!last_jo && jo.last_msg_jo)
	//	last_jo = jo.last_msg_jo ;
}

function get_pm_size()
{
	return {w:700,h:550} ;
}

//on_init_pm_ok() ;
</script>