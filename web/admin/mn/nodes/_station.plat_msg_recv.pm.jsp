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
	org.iottree.core.msgnet.store.*,
	org.iottree.core.station.*
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
	IMNContainer mnc = net.getBelongTo().getBelongTo() ;
	if(mnc==null || !(mnc instanceof UAPrj))
	{
		out.print("no in prj") ;
		return ;
	}
	UAPrj prj = (UAPrj)mnc ;
	String prj_path = "/"+prj.getName() ;
	MNBase item =net.getItemById(itemid) ;
	if(item==null || !(item instanceof PlatMsgRecv_NS))
	{
		out.print("no item found") ;
		return ;
	}
	
	PlatMsgRecv_NS ms_node= (PlatMsgRecv_NS)item ;
	
%>

 <div class="layui-form-item">
    <label class="layui-form-label">Match Topics</label>
    <div class="layui-input-inline" style="width: 300px;">
      <input type="text" id="match_topics" name="match_topics" value=""  placeholder="abc,tpcat.*,xy.123" autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"></div>
 </div>
 
<script>
var prj_path = "<%=prj_path%>";
var container_id="<%=container_id%>";
var netid="<%=netid%>";

function on_after_pm_show(form)
{
	
}


function get_pm_jo()
{
	let match_topics = $("#match_topics").val() ;
	return {match_topics:match_topics} ;
}

function set_pm_jo(jo)
{
	$("#match_topics").val(jo.match_topics||"") ;
}

function get_pm_size()
{
	return {w:600,h:450} ;
}

//on_init_pm_ok() ;
</script>