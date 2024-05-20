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
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	
 List<MNNode> nodes = MNManager.listRegisteredNodes() ;

%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="mn"/>
</jsp:include>
<style>

.cont
{
scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  overflow-x: hidden;
  overflow-y: auto;
  position:absolute;
  top:0px;bottom:0px;
  left:0px;
  width:180px;border:0px solid;
}

.nitem
{
	height: 30px;
	cursor: pointer;
	width:170px;
	left:5px;
	margin-top: 10px;
	position: relative;
	border:0px solid;
}

</style>
</head>
<body style="overflow:hidden;">
<div class="cont">
<%
JSONArray jarr = new JSONArray() ;
for(MNNode n:nodes)
{
	JSONObject tmpjo = n.toJO() ;
	jarr.put(tmpjo) ;
	String sty = "" ;
%><div class="nitem"  id="n_<%=n.getNodeTP()%>"  _tp="<%=n.getNodeTP()%>" draggable='true' ondragstart='drag(event)' onclick="test(this)" title="<%=n.getTitle()%>">
	</div>
<%
}
%>
</div>
</body>
<script type="text/javascript">

var nodes = <%=jarr%> ;

function draw_nodes()
{
	for(let n of nodes)
	{
		draw_node(n);
	}
}


function draw_node(n)
{
	let sz = mn.view.DINode.calDrawSize(n) ;
	console.log(sz.w,sz.h) ;
	$("#n_"+n._tp).css("height",sz.h+"px") ;
	
	let lay = new mn.DrawLayer();
	let din = new mn.view.DINode(n,{});
	lay.addItem(din,false);
	var panel = new mn.DrawPanel("n_"+n._tp,{});
	var p1 = new mn.DrawPanelDiv("",{layer:lay,panel:panel}) ;
	var rect = lay.getShowItemsRect();
	p1["w"] = rect.w;
	p1["h"] = rect.h;
	lay.ajustDrawFit();
}


draw_nodes() ;

function drag(ev)
{
	var tar = ev.target;
	let tp = $(tar).attr("_tp") ;
	var r = {_val:{node_tp:tp},_tp:"node"};
	mn.util.setDragEventData(ev,r);
}

</script>
</html>