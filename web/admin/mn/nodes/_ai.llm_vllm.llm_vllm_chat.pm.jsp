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
VLLMChat_NM node = (VLLMChat_NM)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}


VLLM_M m = (VLLM_M)node.getOwnRelatedModule() ;
String model_name = node.getModelName() ;
List<LLMModel> models = m.listModelItems(true) ;
String sys_msg = node.getSystemMsg() ;
String last_user_msg=node.getLastUserMsg();
String last_assistant_msg=node.getLastAssistantMsg() ;
long max_tokens = node.getMaxTokens() ;
JSONObject json_schema = node.getJsonSchema() ;
String json_schema_str = "" ;
if(json_schema!=null)
	json_schema_str = json_schema.toString(2) ;
%>
<style>
.msg
{
	height:85px;width:100%;
}
table {font-size:12px;margin:3px;}
td {border:2px solid #fff;}
</style>
<div class="layui-form-item">
    <label class="layui-form-label">vLLM LLM:</label>
    <div class="layui-form-mid"><%=m.getUrl() %></div>
    <div class="layui-form-mid">Model</div>
    <div class="layui-input-inline" style="width:200px;">
      <select id="model_name">
      	<option value=""> --- </option>
<%
for(LLMModel md:models)
{
	String seled = md.getName().equals(model_name)?"selected":"" ;
%><option value="<%=md.getName() %>" <%=seled %>><%=md.getName() %></option>
<%
}
%>
      </select>
    </div>
    <div class="layui-form-mid">Max Tokens Num:</div>
    <div class="layui-input-inline" style="width:100px;">
      <input id="max_tokens" class="layui-input"  value="<%=max_tokens %>" min="1"  type="number"/>
    </div>
</div>
<div class="layui-form-item">
    
</div>
<table>
 <tr>
  <td style="width:60%;">
system message:
<textarea id="sys_msg" class="msg" style="height:220px;"><%=sys_msg %></textarea>
Last user message:
<textarea id="last_user_msg" class="msg"><%=last_user_msg %></textarea>
Last assistant message:
<textarea id="last_assistant_msg" class="msg"><%=last_assistant_msg %></textarea>

 </td>
 <td style="width:40%;vertical-align: top;">
 Response JSON Schema:
 <textarea id="json_schema" style="width:100%;height:418px"><%=json_schema_str %></textarea>
 </td>
 </tr>
</table>
<script>

var model_name="<%=model_name%>";

function on_after_pm_show(form)
{
	 
}

function get_pm_jo()
{
	let model_name =  $("#model_name").val();
	let sys_msg =  $("#sys_msg").val();
	let last_user_msg =  $("#last_user_msg").val();
	let last_assistant_msg =  $("#last_assistant_msg").val();
	let max_tokens = get_input_val("max_tokens",1024,true) ;
	let json_schema = $("#json_schema").val() ;
	return {model_name:model_name,sys_msg:sys_msg,
		last_user_msg:last_user_msg,last_assistant_msg:last_assistant_msg,
		max_tokens:max_tokens,json_schema:json_schema||""} ;
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