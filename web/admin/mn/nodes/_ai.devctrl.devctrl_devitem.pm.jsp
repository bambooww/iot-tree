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
	org.iottree.ext.ai.*,org.iottree.ext.ai.mn.*,org.iottree.ext.ai.dev.*,
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
String prj_path = prj.getNodePath() ;

MNManager mnm = MNManager.getInstance(prj) ;
String netid = request.getParameter("netid") ;
MNNet net = mnm.getNetById(netid) ;
if(net==null)
{
	out.print("no net found") ;
	return ;
}
String itemid = request.getParameter("itemid") ;
DevCtrlDevItem_NS node = (DevCtrlDevItem_NS)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

String dev_id = node.getDevId() ;
String dev_t = node.getDevTitle() ;
List<DevCtrlDevItem_NS.Param> params = node.getParams() ;
JSONArray pms_jarr = new JSONArray() ;
if(params!=null)
{
	for(DevCtrlDevItem_NS.Param pm : params)
	{
		pms_jarr.put(pm.toJO()) ;
	}
}
%>
<style>
.msg
{
	height:85px;width:100%;
}
table {font-size:12px;margin:0px;width:100%;}
td {border:1px solid #ccc;vertical-align: top;}
#tb_pms button {white-space: nowrap;}
table input {width:100%;}
.alv {
  display: flex;
  gap: 8px;
  align-items: center;
}

.alv .field {
  flex: 1;
  min-width: 0;
}

.alv .btn {
  flex-shrink: 0;
}
</style>
<div class="layui-form-item">
    <label class="layui-form-label">Device Id:</label>
    <div class="layui-input-inline" style="width:200px;">
      <input id="dev_id" class="layui-input"  value="<%=dev_id %>" />
    </div>
    <div class="layui-form-mid">Title:</div>
    <div class="layui-input-inline" style="width:100px;">
      <input id="dev_t" class="layui-input"  value="<%=dev_t %>" />
    </div>
</div>
<div class="layui-form-item">
    <label class="layui-form-label">Parameters:<br>
    	<button title="copy parameters" onclick="pm_copy()">copy</button>
    	<button title="paste parameters" onclick="pm_paste()">paste</button>
    </label>
    <div class="layui-input-inline" style="width:650px;">
      <div style="width:100%;height:300px;border:1px solid #ccc;">
      	<table id="tb_pms" >
      		<thead>
      			<tr>
      				<td style="width:15%;">Key</td>
      				<td style="width:20%;">Title</td>
      				<td style="width:10%;">Type</td>
      				<td style="width:25%;">Limit</td>
      				<td style="width:20%;" style="border-right:0px;">Value
      				<button class="add" onclick="add_pm('state')" title="add state"><i class="fa fa-plus"></i> x|y|z</button>
      					<button class="add" onclick="add_pm('range')" title="add range"><i class="fa fa-plus"></i> [ , ]</button>
      				</td>
      				<td style="width:1%;border-left:0px;"">
      				</td>
      			</tr>
      		</thead>
      		<tbody id="tbd_pms">
      		</tbody>
      	</table>
      </div>
    </div>
</div>

<script>
var prj_path = "<%=prj_path%>" ;
var pms_jarr = <%=pms_jarr%> ;
//console.log(pms_jarr) ;
function on_after_pm_show(form)
{
	update_tbs(pms_jarr)
}
//<button class="btn_bind write" onclick="set_bind_tag('w',this)" tagp="\${bind_w_tag}">write tag</button>
function update_tbs(pms)
{
	let ss = '' ;
	for(let pm of pms)
	{
		let lim_inp = "" ;
		if(pm.type=='range')
			lim_inp = `min:<input type='number' class='min' style='width:50px;' value="\${pm.min}" /> - max:<input type='number' class='max' style='width:50px;' value="\${pm.max}" />`;
		else if(pm.type=='state')
		{
			lim_inp = `<div class='allowed_values'>
				<div class="alv">
				  <span class="field ">Value</span>
				  <span class="field">Title</span>
				  <button class="btn" onclick='state_add_item(this)'>&nbsp;+&nbsp;</button>
				</div>
				`;
			for(let alv of pm.allowed_values)
			{
				lim_inp += `<div class="alv item">
					  <input class="field val" placeholder="value" value="\${alv.value}">
					  <input class="field tt" placeholder="title" value="\${alv.title}">
					  <button class="btn" onclick="state_del_item(this)">X</button>
					</div>` ;
			}
			lim_inp += "</div>";
		}
		
		let bind_r_tag = pm.bind_r_tag||"";
		let bind_w_tag = pm.bind_w_tag||"";
		
		ss += `<tr>
				<td><input class="key" value="\${pm.key||""}" /></td>
				<td><input class="title" value="\${pm.title||""}" /></td>
				<td><input class="tp" value="\${pm.type||""}" readonly /></td>
				<td>\${lim_inp}</td>
				<td style="text-align:center" class="value">
					<button class="btn_bind read" onclick="set_bind_tag('r',this)"  tagp="\${bind_r_tag}">read tag</button>
				</td>
				<td><button class="del" onclick="del_pm(this)">&nbsp;<i class="fa fa-times"></i>&nbsp;</button></td>
			</tr>`;
	}
	$("#tbd_pms").html(ss);
	form.render();
	update_btn_bind();
}

function add_pm(tp)
{
	let lim_inp = "" ;
	if(tp=='range')
		lim_inp = `min:<input type='number' class='min' style='width:50px;' /> - max:<input type='number' class='max' style='width:50px;' />`;
	else if(tp=='state')
	{
		lim_inp = `<div class='allowed_values'>
			<div class="alv">
			  <span class="field ">Value</span>
			  <span class="field">Title</span>
			  <button class="btn" onclick='state_add_item(this)'>&nbsp;+&nbsp;</button>
			</div>
			`;

		lim_inp += `<div class="alv item">
			  <input class="field val" placeholder="value" value="">
			  <input class="field tt" placeholder="title" value="">
			  <button class="btn" onclick="state_del_item(this)">X</button>
			</div>` ;
	}
		
	let ss = `<tr>
			<td><input class="key" value="" /></td>
			<td><input class="title" value="" /></td>
			<td><input class="tp" value="\${tp}" readonly /></td>
			<td>\${lim_inp}</td>
			<td style="text-align:center" class="val">
			<button class="btn_bind read" onclick="set_bind_tag('r',this)" style="">read tag</button>
			</td>
			<td><button class="del" onclick="del_pm(this)">&nbsp;<i class="fa fa-times"></i>&nbsp;</button></td>
		</tr>`;
	
	$("#tbd_pms").append(ss);
	form.render();
	
}

function update_btn_bind()
{
	$(".btn_bind").each(function(){
		let btn = $(this) ;
		let tagp =btn.attr("tagp") ;
		btn.attr("title",tagp);
		btn.css("background-color",tagp?"green":"") ;
	});
}

function state_add_item(btn)
{
	let ss = `<div class="alv item">
		  <input class="field val" placeholder="value" value="">
		  <input class="field tt" placeholder="title" value="">
		  <button class="btn" onclick="state_del_item(this)">X</button>
		</div>`;
	$(btn).parent().parent().append(ss) ;
}

function state_del_item(btn)
{
	$(btn).parent().remove();
}

function set_bind_tag(rw,btn)
{
	let bb = $(btn) ;
	//dlg.msg(rw) ;
	let tmpv = bb.attr("tagp")||"";
	dlg.open("/admin/ua_cxt/di_cxt_tag_selector.jsp?path="+prj_path+"&val="+tmpv+"&bind_tag_only=true",
				{title:"Select Tag in Project",w:'500px',h:'400px'},
				['Ok','Cancel','Unbind'],
				[
					function(dlgw)
					{
						var ret = dlgw.get_val() ;
						if(ret==null)
							return ;
						//$("#tag").val(ret) ;
						//dlg.msg(ret) ;
						bb.attr("tagp",ret) ;
						bb.attr("title",ret) ;
						update_btn_bind();
						dlg.close();
					},
					function(dlgw)
					{
						dlg.close();
					},
					function(dlgw)
					{
						bb.attr("tagp","") ;
						bb.attr("title","") ;
						update_btn_bind();
						dlg.close();
					}
				]);
}

function read_input_pms()
{
	let ret = [] ;
	let errs = [] ;
	$("#tbd_pms").find("tr").each(function(){
		let tr = $(this) ;
		let key = tr.find(".key").val() ;
		if(!key)
		{
			errs.push("Key cannot be null") ; return;
		}
		let tt = tr.find(".title").val() ;
		if(!tt)
		{
			errs.push("Title cannot be null") ; return;
		}
			
		let tp = tr.find(".tp").val() ;
		let pm = {key:key,title:tt,type:tp||'state'}
		if(tp=='range')
		{
			pm.min = tr.find(".min").val()||"";
			pm.max = tr.find(".max").val()||"";
		}
		else if(tp=='state')
		{
			let alv = tr.find(".allowed_values") ;
			let vts = [] ;
			alv.find(".item").each(function(){
				let item = $(this) ;
				let val = item.find(".val").val() || "";
				let tt = item.find(".tt").val()||"" ;
				if(val)
					vts.push({value:val,title:tt}) ;
			}) ;
			pm.allowed_values = vts ;
		}
		let td_val = tr.find(".value") ;
		pm.bind_r_tag = td_val.find(".read").attr("tagp")||"";
		pm.bind_w_tag = td_val.find(".write").attr("tagp")||"";
		
		ret.push(pm);
	});
	
	let keys = [] ;
	for(let pm of ret)
	{
		let k = pm.key ;
		if(!k) continue ;
		keys.push(k) ;
	}
	if(keys.length<ret.length)
		errs.push("Parameter key cannot duplicate")
	if(errs.length>0)
		return errs.join(',') ;
	return ret ;
}

function del_pm(ele)
{
	$(ele).parent().parent().remove() ;
}

function get_pm_jo()
{
	let dev_id =  $("#dev_id").val();
	let dev_t =  $("#dev_t").val();
	if(!dev_id || !dev_t)
		return "No Device id or title set";
	let pms = read_input_pms() ;
	if(typeof(pms)=='string')
		return pms ;
	//if(pms.length<=0)
	//	return "No parameters set" ;
	
	return {dev_id:dev_id,dev_t:dev_t,params:pms} ;
}

function set_pm_jo(jo)
{
	
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

function pm_copy()
{
	let pms = read_input_pms();
	if(!pms || pms.length<=0)
	{
		dlg.msg("no param list");return;
	}
	
	try {
		let txt = JSON.stringify(pms);
		//console.log(txt) ;
	    navigator.clipboard.writeText(txt);
	    dlg.msg('copy ok');
	  } catch (err) {
		  dlg.msg('copy failed'+err);
	  }
}

function pm_paste()
{
	try {
	    navigator.clipboard.readText()
	    	.then(text=>{
	    	    if(!text) {dlg.msg("no copied content");return;}
	    	    let pms = null;
	    	    //console.log(text) ;
	    	    eval("pms="+text) ;
	    	    update_tbs(pms)
	    	});
	    
	  } catch (err) {
		  dlg.msg('paste failed'+err);
	  }
}
</script>