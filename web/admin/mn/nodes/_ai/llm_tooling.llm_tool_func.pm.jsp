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
	org.iottree.ext.ai.*,org.iottree.ext.ai.mn.*,
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
LLMToolFunc_RES node = (LLMToolFunc_RES)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

String name = node.getName() ;
String desc = node.getDesc() ;
LinkedHashMap<String,LLMToolFunc.Param> name2pm = node.getParams();
JSONArray pms_jarr = new JSONArray() ;
if(name2pm!=null)
{
	for(LLMToolFunc.Param pm:name2pm.values())
	{
		pms_jarr.put(pm.toJO()) ;
	}
}
%>
<style>
.msg
{
	height:85px;width:100%;border:1px solid #ccc;
}
.add {color:green;}
.del {color:red}
#tb_pms {width:100%;position: relative;}
#tb_pms td {border:1px solid #ccc;}
#tb_pms td input {width:100%;}
</style>

<div class="layui-form-item">
    <label class="layui-form-label">Function Name:</label>
    <div class="layui-input-inline" style="width:400px;">
      <input id="name" class="layui-input"  value="<%=name %>" />
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-inline" style="width:600px;">
      <textarea id="desc" class="msg"><%=desc %></textarea>
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label">Parameters:</label>
    <div class="layui-input-inline" style="width:600px;">
      <div style="width:100%;height:300px;border:1px solid #ccc;">
      	<table id="tb_pms" >
      		<thead>
      			<tr>
      				<td style="width:20%;">Name</td>
      				<td style="width:20%;">Type</td>
      				<td style="width:50%;">Description</td>
      				<td style="width:10%;">Required</td>
      				<td style="width:10%;"><button class="add" onclick="add_pm()"><i class="fa fa-plus"></i></button></td>
      			</tr>
      		</thead>
      		<tbody id="tbd_pms">
      		</tbody>
      	</table>
      </div>
    </div>
</div>

<script>

var pms = <%=pms_jarr%> ;
console.log(pms) ;
function on_after_pm_show(form)
{
	update_tbs()
}

function update_tbs()
{
	let ss = '' ;
	for(let pm of pms)
	{
		let chked = pm.required?"checked":"";
		ss += `<tr>
			<td><input class="n" value="\${pm.name||""}" /></td>
				<td><input class="tp" value="\${pm.tp||""}" /></td>
				<td><input class="desc" value="\${pm.desc||""}" /></td>
				<td style="text-align:center"><input type="checkbox" class="req" \${chked} lay-skin="primary" lay-ingore /></td>
				<td><button class="del" onclick="del_pm(this)"><i class="fa fa-times"></i></button></td>
			</tr>`;
	}
	$("#tbd_pms").html(ss);
	form.render();
}

function add_pm()
{
	let ss = `<tr>
		<td><input class="n" value="" /></td>
			<td><input class="tp" value="string" /></td>
			<td><input class="desc" value="" /></td>
			<td style="text-align:center"><input type="checkbox" class="req" lay-skin="primary"  lay-ingore /></td>
			<td><button class="del" onclick="del_pm(this)"><i class="fa fa-times"></i></button></td>
		</tr>`;
		
	$("#tbd_pms").append(ss);
	form.render();
}

function read_input_pms()
{
	let ret = [] ;
	let errs = [] ;
	$("#tbd_pms").find("tr").each(function(){
		let tr = $(this) ;
		let n = tr.find(".n").val() ;
		if(!n)
		{
			errs.push("Name cannot be null") ; return;
		}
			
		let tp = tr.find(".tp").val() ;
		let desc = tr.find(".desc").val() ;
		let req = tr.find(".req").prop("checked") ;
		ret.push({name:n,tp:tp||'string',desc:desc||'',required:req});
	});
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
	let name =  $("#name").val();
	let desc =  $("#desc").val();
	let pms = read_input_pms() ;
	if(typeof(pms)=='string')
		return pms ;
	
	return {name:name,desc:desc,params:pms} ;
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