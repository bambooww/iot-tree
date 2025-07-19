<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,org.iottree.core.msgnet.util.*,
	org.iottree.core.dict.*,org.iottree.core.msgnet.nodes.*,
	org.iottree.core.comp.*,org.iottree.core.util.jt.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid","itemid"))
			return ;
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	String op = request.getParameter("op") ;
	MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
	if(mnm==null)
	{
		out.print("no MsgNet Manager with container_id="+container_id) ;
		return ;
	}

	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	ManualTrigger item =(ManualTrigger)net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	MsgSetRule msr = item.getSinglePldRule();
	if(msr==null)
	{
		out.print("no single pld rule") ;
		return ;
	}
	String val_str = msr.getSorSubN() ;
	if(val_str==null)
		val_str  = "" ;
	String inp_tp = "text" ;
	MNCxtValSty val_sty = msr.getSorValSty() ;
	
	if("send".equals(op))
	{//do send
		String strv = request.getParameter("strv") ;
		MNCxtValTP vtp = val_sty.getConstantValTP();
		if(vtp==null)
		{
			out.print("no contant val tp");return;
		}
		
		Object objv = vtp.transStrToObj(strv) ;
		if(objv==null)
		{
			out.print("input null");return;
		}
		MNMsg msg = new MNMsg() ;
		msg.CXT_PK_setSubVal(msr.getMsgSubN(),objv,null) ;
		item.RT_sendMsgOut(RTOut.createOutAll(msg)) ;
		out.print("succ") ;
		return ;
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script type="text/javascript">

</script>
<style>

</style>
</head>

<body>
<table style="width:100%;height:100%;border:0px;">
<tr>
 <td style="width:80%">
<%
if(val_sty.isNumber())
{
%><input type="number" id="strv" style="width:100%;" value="<%=val_str %>" />
<%
}
else if(val_sty==MNCxtValSty.vt_bool)
{
%>
<select id="strv">
	<option value="true">true</option>
	<option value="false">false</option>
</select>
<%
}
else if(val_sty==MNCxtValSty.vt_jo || val_sty==MNCxtValSty.vt_jarr)
{
%>
	<textarea id="strv" style="width:100%;height:100%;" ><%=Convert.plainToHtml(val_str) %></textarea>
<%
}
else
{
%>
	<input type="text" id="strv" style="width:100%;" value="<%=val_str %>" />
<%
}
%>
</td>
<td style="width:20%">
<button onclick="on_rt_panel_btn()">send</button>
</td>
</tr>
</table>
</body>

<script type="text/javascript">

var container_id="<%=container_id%>";
var netid="<%=netid%>";
var itemid="<%=itemid%>";
var val_str = "<%=val_str%>" ;

layui.use('form', function(){
	  form = layui.form;
	  element = layui.element;
	  form.render();
	  
	 // init_pm() ;
	 $("#strv").val(val_str) ;
});

function on_rt_panel_btn()
{
	let strv = $("#strv").val() ;
	if(!strv)
	{
		dlg.msg("no input") ;return ;
	}
	
	let pm = {op:"send",container_id:container_id,netid:netid,itemid:itemid,strv:strv} ;
	send_ajax("_com.manual.rt.jsp",pm,(bsucc,ret)=>{
		if(!bsucc||ret!="succ")
		{
			dlg.msg(ret);return;
		}
		dlg.msg("send ok") ;
	});
}

</script>

</html>                                                                                                                                                                                                                            