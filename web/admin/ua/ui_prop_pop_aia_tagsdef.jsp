<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "nodepath"))
		return ;
	String nodep = request.getParameter("nodepath") ;
	UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
	if(node==null)
	{
		out.print("no node found with path="+nodep) ;
		return ;
	}
	
	UANodeOCTagsCxt cxt_node =  (UANodeOCTagsCxt)node ;
	UAPrj prj = cxt_node.getBelongToPrj() ;
	String node_t = cxt_node.getNodeCxtPathTitleIn(prj) ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(800,550);
</script>
<style>
.layui-elem-quote {
    margin-bottom:0px;
    padding:5px;
}
.msg
{
	height:85px;width:100%;
}
table {font-size:12px;margin:0px;width:96%;margin-left:2%;margin-top:3px;}
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
</head>
<body>
<%--
<div class="layui-form-item">
    <label class="layui-form-label">Device Id:</label>
    <div class="layui-input-inline" style="width:200px;">
      <input id="dev_id" class="layui-input"  value="<%="" %>" />
    </div>
    <div class="layui-form-mid">Title:</div>
    <div class="layui-input-inline" style="width:100px;">
      <input id="dev_t" class="layui-input"  value="<%="" %>" />
    </div>
</div>
 --%>
<blockquote class="layui-elem-quote">
 <%=node_t %>
 <div style="float:right;margin-right:30px;">
 
 <button onclick="add_pm('state')" title="add state" class="layui-btn layui-btn-xs layui-btn-primary"><i class="fa fa-plus"></i> x|y|z</button>
 <button onclick="add_pm('range')" title="add range" class="layui-btn layui-btn-xs layui-btn-primary"><i class="fa fa-plus"></i> [ , ]</button>
      					
 <button title="copy parameters" onclick="pm_copy()" class="layui-btn layui-btn-xs layui-btn-primary">copy</button>
 <button title="paste parameters" onclick="pm_paste()" class="layui-btn layui-btn-xs layui-btn-primary">paste</button>
</div>
</blockquote>
 <table id="tb_pms" >
      		<thead style="background-color:#aaa;">
      			<tr>
      				<td style="width:15%;">Key</td>
      				<td style="width:20%;">Title</td>
      				<td style="width:10%;">Type</td>
      				<td style="width:25%;">Limit</td>
      				<td style="width:20%;" style="border-right:0px;">Value
      				
      				</td>
      				<td style="width:1%;border-left:0px;"">
      				</td>
      			</tr>
      		</thead>
      		<tbody id="tbd_pms">
      		</tbody>
      	</table>
</body>
<script type="text/javascript">

var input_txt = dlg.get_opener_opt("inputv") ;

var nodep = "<%=nodep%>" ;
var pms_jarr = [];//;
//console.log(pms_jarr) ;
function on_after_pm_show(form)
{
	update_tbs()
}
//<button class="btn_bind write" onclick="set_bind_tag('w',this)" tagp="\${bind_w_tag}">write tag</button>
function update_tbs()
{
	let ss = '' ;
	for(let pm of pms_jarr)
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
			for(let alv of pm.allowed_values||[])
			{
				lim_inp += `<div class="alv item">
					  <input class="field val" placeholder="value" value="\${alv.value}">
					  <input class="field tt" placeholder="title" value="\${alv.title}">
					  <button class="btn" onclick="state_del_item(this)" lay-ignore>X</button>
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
					<button class="btn_bind write" onclick="set_bind_tag('w',this)"  tagp="\${bind_w_tag}">write tag</button>
				</td>
				<td><button class="del" onclick="del_pm(this)">&nbsp;<i class="fa fa-times"></i>&nbsp;</button></td>
			</tr>`;
	}
	$("#tbd_pms").html(ss);
	//form.render();
	update_btn_bind();
}


function calc_input_pms(err_rets)
{
	let ret = [] ;
	let errs = err_rets||[] ;
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
	return ret;
}

function add_pm(tp)
{
	pms_jarr = calc_input_pms();
	pms_jarr.push({type:tp}) ;
	update_tbs()
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
	dlg.open("/admin/ua_cxt/di_cxt_tag_selector.jsp?path="+nodep+"&val="+tmpv+"&bind_tag_only=true",
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
	let errs = [] ;
	let ret = calc_input_pms(errs)
	
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



function get_pm_jo()
{
	let dev_id =  $("#dev_id").val()||"";
	let dev_t =  $("#dev_t").val()||"";
	//if(!dev_id || !dev_t)
	//	return "No Device id or title set";
	let pms = read_input_pms() ;
	if(typeof(pms)=='string')
		return pms ;
	
	return {dev_id:dev_id,dev_t:dev_t,params:pms} ;
}

function set_pm_jo(jo)
{
	
}

function do_submit(cb)
{
	let ob = get_pm_jo();
	if(typeof(ob)=='string')
	{
		cb(false,ob);return ;
	}
	cb(true,{txt:JSON.stringify(ob)});
}


function init_ui()
{
	if(!input_txt)
	{
		return ;
	}
	let ob = null;
	eval("ob="+input_txt) ;
	$("#dev_id").val(ob.dev_id||"");
	$("#dev_t").val(ob.dev_t||"") ;
	pms_jarr = ob.params||[] ;
	
	update_tbs();
}

init_ui();
</script>
</html>