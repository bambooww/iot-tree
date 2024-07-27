<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid","mn","tp"))
			return ;
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String mn = request.getParameter("mn") ;
	String tp = request.getParameter("tp") ;
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
	
	MNBase mnb = null ;
	String mn_t = "node" ;
	if("n".equals(mn))
	{
		mnb = MNManager.getNodeByFullTP(tp) ;
		
	}
	else
	{
		mnb = MNManager.getModuleByFullTP(tp) ;
		mn_t = "module" ;
	}
	if(mnb==null)
	{
		out.print("no node found") ;
		return ;
	}
	
	String cc = mnb.getColor() ;
	String tcc = mnb.getTitleColor() ;
	List<MNLib.Item> items = MNLib.listItems(mn, tp) ;
	if(items==null)
		items = new ArrayList<>(0) ;
%><!DOCTYPE html>
<html >
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="mn"/>
</jsp:include>
    <title>Node Param Library</title>
    <style>
    
.lib_list
{
	position: relative;
	width:100%;
}

.lib_item
{
	position: relative;
	width:90%;
	margin-top:5px;
	left:5%;
	height:40px;
	border:1px solid #f3f3f3;
	background-color: <%=cc%>;
	color:<%=tcc%>;
	cursor: pointer;
}

.lib_item .tt
{
	position: absolute;
	left:10px;
	top:3px;
}

.lib_item .dt
{
	position: absolute;
	right:10px;
	bottom:3px;
	font-size: 12px;
}
    </style>
</head>
<body>
    <w:g><%=mn_t %></w:g>: <%=mnb.getTPTitle() %>
<div class="lib_list">
<%
	for(MNLib.Item item:items)
	{
		String id = item.getId() ;
		String tt = item.getTitle() ;
		String dtstr = Convert.toFullYMDHMS(new Date(item.getDT())) ;
%>
<div class="lib_item" id="li_<%=id%>" libid="<%=id %>" draggable='true' ondragstart='drag(event)'>
	<span class="tt"><%=tt %></span>
	<span class="dt"><%=dtstr %></span>
</div>
<%
	}
%>
</div>
</body>
<script type="text/javascript">
var tptp="<%=mn%>";
var tp="<%=tp%>";

function drag(ev)
{
	var tar = $(ev.target);
	let x = ev.offsetX ;
	let y = ev.offsetY ;
	let w = tar.width() ;
	let h = tar.height() ;
	let moduleid = "";
	let lib_item_id = tar.attr("libid") ;
	var r = {_val:{tp:tp,moduleid:moduleid,lib_item_id:lib_item_id,x_ratio:x/w,y_ratio:y/h,x_offset:x,y_offset:y},_tp:tptp};
	mn.util.setDragEventData(ev,r);
}
</script>
</html>
