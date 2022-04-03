<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid","cid"))
	return;
	String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String cid = request.getParameter("cid") ;
ConnProvider cp = ConnManager.getInstance().getConnProviderById(prjid, cpid);
if(cp==null)
{
	out.print("no provider found ");
	return ;
}
ConnPt cpt = cp.getConnById(cid) ;
if(cpt==null || !(cpt instanceof ConnPtDevFinder))
{
	out.print("no ConnPtDevFinder found") ;
	return ;
}
ConnPtDevFinder cdf  =(ConnPtDevFinder)cpt ;
LinkedHashMap<String,ConnDev> name2dev = cdf.getFoundConnDevs() ;
String cptitle = cp.getTitle() ;
//ConnMsg pmsg = cp.getConnMsgById(msgid)
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(400,300);
</script>
</head>
<body>
<div class="layui-card">
  <div class="layui-card-header"><%=cptitle %> - <%=cpt.getTitle() %></div>
  <div class="layui-card-body">
<%
for(Map.Entry<String,ConnDev> n2d:name2dev.entrySet())
{
	String n = n2d.getKey() ;
	ConnDev cd = n2d.getValue() ;
%>
<%=n %> - <%=cd.getTitle() %>  <button onclick="add_to_ch('<%=n%>')">Add To Channel</button>
<%
}
%>
  </div>
</div>
</body>
<script type="text/javascript">
var prjid='<%=prjid%>';
var cpid = '<%=cpid%>';
var cid = '<%=cid%>';
var form = null;
layui.use('form', function(){
	  form = layui.form;
	
	  form.render(); 
});

function add_to_ch(n)
{
	send_ajax("cp_ajax.jsp",{op:"conn_dev_new_add",prjid:prjid,cpid:cpid,connid:cid,name:n},function(bsucc,ret){
		if(!bsucc)
		{
			dlg.msg(ret) ;
			return ;
		}
		if(!ret.res)
		{
			dlg.msg(ret.err) ;
			return ;
		}
		document.location.href=document.location.href;
	});
}
</script>
</html>