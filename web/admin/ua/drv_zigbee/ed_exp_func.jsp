<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,org.json.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
					org.iottree.driver.zigbee.zstack.*,
					org.iottree.driver.zigbee.*,
	org.iottree.driver.zigbee.zstack.msg_sys.*,
	org.iottree.driver.zigbee.zstack.msg_zdo.*,
	org.iottree.driver.zigbee.zstack.msg_util.*,
	org.iottree.driver.zigbee.zstack.req_rsp.*,
	org.iottree.driver.zigbee.zspec.util.*,
	java.io.*,org.iottree.driver.zigbee.zspec.*,org.iottree.driver.zigbee.zcl.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "path","ieee"))
		return ;
	String path = request.getParameter("path") ;
	String ieee = request.getParameter("ieee") ;
	StringBuilder failedr = new StringBuilder() ;
	ZSDriver z_dd = ZigbeeManager.getZSDriverByPath(path, failedr) ;
	if(z_dd==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	ZSCoor z_coor = z_dd.getZSCoor();//.getCoor() ;
	
	ZSDevice ed = z_coor.getEndDeviceByIEEEAddr(ieee) ;
	if(ed==null)
	{
		out.print("no ed found") ;
		return ;
	}
%><html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style type="text/css">
body {background-color: #f5f5f5;overflow: hidden;}

.ep
{
	position: relative;
	display:inline-block;
	float:left;
	width:96%;
	left:1%;
	border:1px solid #aaa;
	border-radius:6px;cursor:pointer;
	margin:3px;
}
.d_item:hover {background-color:#aeecff}
.d_item .i {position: absolute;left:5px;top:10px;font-size: 12px;}
.d_item .n
{
	position:absolute;
	left:25px;
	top:3px;line-height:30px;
	font-size:12px;
}
.d_item .nc
{
	position:absolute;
	left:25px;
	top:6px;line-height:20px;
	font-size:12px;
}
.d_item .c
{
	position:absolute;
	left:25px;
	bottom:3px;
	font-size:12px;
}
.dddd
{
	position: relative;
	display:inline-block;
	width:90%;
	left:5px;
	height:20px;
	margin:3px;
}
.seled {background-color: #aeecff}
</style>
<script>
</script>
</head>
<body>
<%
for(ZSEndPoint ep:ed.listEndPoints())
{
	
%><div class="ep">EP:<%=ep.getEndPointId() %>
<%
	for(Integer cid:ep.getInClusterIds())
	{
		ZSCluster zsc = ep.getInCluster(cid) ;
		String c_tt = "???";
		if(zsc!=null)
		{
			Cluster c = zsc.getClusterDef() ;
			if(c!=null)
			{
				c_tt = c.getName() ;
			}
		}
%><div class="c">Cluster <%=cid %>:<%=c_tt %></div>
<%
	}
%>
</div>
<%
}
%>
</body>
<script type="text/javascript">
dlg.resize_to(600,700) ;
var path = "<%=path%>" ;
var form ;

layui.use('form', function(){
	  form = layui.form;
	  form.on('select(cat_dds)', function(obj){
	});
	  
	  form.render() ;
});

</script>
</html>                                                                                                                                                                                                                            