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
	org.iottree.core.msgnet.modules.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "uid"))
		return ;
	
	String uid = request.getParameter("uid") ;
	List<String> ss = Convert.splitStrWith(uid, "-") ;
	if(ss.size()!=3)
		return ;
UAPrj prj = UAManager.getInstance().getPrjById(ss.get(0)) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}
MNManager mnm = MNManager.getInstance(prj) ;
MNNet net = mnm.getNetById(ss.get(1)) ;
if(net==null)
{
	out.print("no net found") ;
	return ;
}
RESTful_M node = (RESTful_M)net.getModuleById(ss.get(2)) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

List<RESTful_Req> reqs = node.listRelatedNodes(RESTful_Req.class) ;
if(reqs==null)
	reqs = Arrays.asList();
%><html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style>
		table{border-collapse:collapse;}
		body,td{font-size:12px;cursor:default;}
.left {position: absolute;left:0px;top:0px;width:40%;bottom:0px;border:1px solid;overflow-y:auto}
.right  {position: absolute;right:0px;top:0px;width:60%;bottom:0px;;overflow-y:auto}
.cat {margin:2px;width:95%;height:20px;border:1px solid #ccc;border-radius: 3px;}
.cat .t {font-weight:bold;}
.cat:hover {background-color: grey;}
.caller {margin:2px;width:95%;height:20px;border:1px solid #ccc;border-radius: 3px;}
.caller .t {font-weight:bold;}
.caller:hover {background-color: grey;}
.sel {background-color: grey;}
</style>
<script>
	dlg.resize_to(800,600) ;
</script>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<div class="left">

</div>
<div id="right" class="right">
<%
for(RESTful_Req req:reqs)
{
	String api_n = req.getApiName() ;
	if(Convert.isNullOrEmpty(api_n))
		continue ;
%>
	<div class="api">
		<div><%=api_n %></div>
		
	</div>
<%
}

%>
</div>
</body>
<script>

	
</script>
</html>