<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	org.iottree.core.devtree.*,
	java.util.*"%><%
String treeid = request.getParameter("treeid") ;
if(treeid==null)
	treeid = "" ;
DTTree tree = DTTreeManager.getInstance().getTreeById(treeid) ;
if(tree==null)
{
	out.print("no tree found") ;
	return ;
}
boolean err_model_only = "true".equals(request.getParameter("err_model_only"));
%><html>
<head>
        <jsp:include page="../head.jsp">
        <jsp:param value="true" name="simple"/>
        </jsp:include>
</head>
<style>
iframe
{
width:100%;height:100%;
overflow: hidden;
border:0px;
}
.top {
	position: fixed;
	
	left: 0;
	top: 0;
	bottom: 0;
	z-index: 999;
	height: 45px;
	width:100%;
	text-align: left;
	margin:0px;
	padding:0px;
	overflow: hidden
}
.ccc
{
position:absolute;left:0px;
top:45px;bottom:0px;
overflow:hidden;
margin:5px;
border:1px solid #cecece;
}
</style>
<script>
dlg.dlg_top=true ;
</script>
<body  style="overflow: hidden;">
<div class="top " style="background-color: #007ad4;color:#ffffff;">
<div style="float: left;position:relative;left:0px;margin-left:5px;top:2px;font: 30px solid;font-weight:600;font-size:16px;color:#d6ccd4">
 <img src="../inc/logo1.png" width="40px" height="40px"/>IOTTree</div>
 <div style="float: left;position:relative;left:80px;margin-left:5px;top:2px;font: 25px solid">
		 <span onclick="open_tree()">Device Tree Editor - <%=tree.getTitle()%></span>
		</div>
 <div style="float: right;margin-right:10px;margin-top:10px;font: 20px solid;color:#ffffff">
 			
 			<button class="layui-btn layui-btn-warm"  onclick="up_to_prj()"><wbt:g>up_to_prjs</wbt:g></button>
 			&nbsp;
 			<i class="fa-brands fa-squarespace fa-lg top_btn" onclick="open_res()" title="<wbt:g>resources</wbt:g>"></i>
			<i class="fa fa-floppy-disk fa-lg top_btn" onclick="tab_save()" title="<wbt:g>save,this,comp</wbt:g>"></i>
		    <i id="lr_btn_fitwin"  class="fa fa-crosshairs fa-lg top_btn" onclick="draw_fit()" title="show fit"></i>
</div>
</div>
<div class="ccc"  style="position:absolute;left:0%;top:45px;bottom:0px;width:390px;">
    <iframe name="device_mid" src="dt_tree.jsp?treeid=<%=treeid%>"></iframe>
</div>
<div class="ccc"  style="position:absolute;left:390px;top:45px;bottom:0px;right:0px;">
<iframe name="device_right" src="dt_tree_detail.jsp?treeid=<%=treeid%>"></iframe>
</div>

</body>
<script type="text/javascript">
dlg.dlg_top=true;
var treeid = "<%=treeid%>" ;

function open_tree()
{
	window.open("dt_tree_graph_edit.jsp?treeid="+treeid);
}

function set_cur_device(id)
{
	let fwin = FindFrameWin('device_right');
	let fmid = FindFrameWin('device_mid');
	if(fmid)
		fmid.location.href = "device_tree.jsp?deviceid="+id ;
	 // if(fwin&&id)
	//	  fwin.set_device(id) ;//.href="dev_tp_detail.jsp?libn="+lib.n+"&catid="+c
}

</script>
</html>