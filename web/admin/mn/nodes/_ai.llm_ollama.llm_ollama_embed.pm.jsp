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
LLMOllamaEmbed_NM node = (LLMOllamaEmbed_NM)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}


LLMOllama_M m = (LLMOllama_M)node.getOwnRelatedModule() ;
String model_name = node.getModelName() ;
List<LLMModel> models = m.listModelItems(true) ;

%>
<style>
.msg
{
	height:85px;width:100%;
}
</style>

<div class="layui-form-item">
    <label class="layui-form-label">Ollama LLM:</label>
    <div class="layui-form-mid"><%=m.getOllamaUrl() %></div>
    <div class="layui-form-mid">Using Model</div>
    <div class="layui-input-inline" style="width:250px;">
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
</div>



<script>

var model_name="<%=model_name%>";

function on_after_pm_show(form)
{
	 
}

function get_pm_jo()
{
	let model_name =  $("#model_name").val();
	
	return {model_name:model_name} ;
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