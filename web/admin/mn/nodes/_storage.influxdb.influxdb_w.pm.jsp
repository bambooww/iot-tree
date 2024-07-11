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
	String prjid = request.getParameter("prjid");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
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
	MNBase item =net.getItemById(itemid) ;
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
    <label class="layui-form-label"><w:g>topic</w:g>:</label>
    <div class="layui-input-inline" style="width: 250px;">
      <input type="text" id="topic" name="topic" value=""  autocomplete="off"  class="layui-input" >
    </div>
   
 </div>
 <div class="layui-form-item">
    <div class="layui-form-label"><w:g>desc</w:g>:</div>
	  <div class="layui-input-inline" style="width: 350px;">
	    <textarea type="text" id="tags" name="tags" style="height:60px;"  autocomplete="off" class="layui-input"
	    	
	    	></textarea>
	  </div>
 </div>
<script>


function on_after_pm_show(form)
{
	
}


function get_pm_jo()
{
	var topic = $('#topic').val();
	if(!topic)
	{
		return '<w:g>pls,input,topic</w:g>';
	}
	
	return {topic:topic} ;
}

function set_pm_jo(jo)
{
	$('#topic').val(jo.topic||"");
}

function get_pm_size()
{
	return {w:500,h:350} ;
}

//on_init_pm_ok() ;
</script>