<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.json.*,org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%
	if(!Convert.checkReqEmpty(request, out, "nodepath"))
		return ;
	String nodep = request.getParameter("nodepath") ;
	UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
	if(node==null || !(node instanceof UANodeOC))
	{
		out.print("no UANodeOC node found with path="+nodep) ;
		return ;
	}
	UANodeOC node_oc = (UANodeOC)node ;
	JSONArray roles_jarr = LoginUtil.listRoleAllJArr() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(400,500);
</script>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}
.role {position: relative;width:90%;margin:10px;border:1px solid #ccc;border-radius: 5px;height:30px;}
.role .role_chk {position: absolute;top:3px;left:5px;}
.role .n {position: absolute;bottom:5px;right:55px;}
.role .t {position: absolute;top:5px;left:23px;font-weight:bold;}
.role .op{position: absolute;top:5px;right:5px;}
.role .op .ico{cursor: pointer;}
</style>
</head>
<body>
<div class="layui-panel">
<div class="role"><button onclick="cancel_all()"><i class="fa fa-times" style="color:red"></i><w:g>cancel,all</w:g></button></div>
<div  id="role_list"></div>
</div>
</body>
<script type="text/javascript">

var input_txt = dlg.get_opener_opt("inputv") ;
var roles_all = <%=roles_jarr%> ;
var roles=[];
if(input_txt)
{
	roles = input_txt.split(",") ||[];
}

function update_roles()
{
	let ss = "" ;
	for(let r of roles_all)
	{
		ss += `<div class="role">
			<input type="checkbox" class="role_chk" role="\${r.role_n}" id="rolechk_\${r.role_n}" onclick='on_role_chk_clk()'/>
			<div class="t" onclick="sel_role('\${r.role_n}')">\${r.role_t}</div><div class="n">\${r.role_n}</div>
			<div class="op">`;
		ss += `</div></div>` ;
	}
	$("#role_list").html(ss) ;
	
	for(let r of roles)
	{
		$("#rolechk_"+r).prop("checked",true);
	}
}

function sel_role(n)
{
	let ob = $("#rolechk_"+n);
	let s = ob.prop("checked");
	ob.prop("checked",!s) ;
}

update_roles()

function cancel_all()
{
	$(".role_chk").prop("checked",false) ;
}

function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let selrs = [];
	$(".role_chk").each(function(){
		let ob = $(this) ;
		let rolen = ob.attr("role") ;
		if(ob.prop("checked"))
			selrs.push(rolen) ;
	})
	
	cb(true,{txt:selrs.join(',')});
}

</script>
</html>