<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,org.iottree.core.devtree.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "treeid"))
	return ;

String treeid = request.getParameter("treeid") ;
DTTree tree = DTTreeManager.getInstance().getTreeById(treeid);
if(tree==null)
{
out.println("no device tree found") ;
return ;
}

String title  = "";

String tree_nid = request.getParameter("tree_nid") ;
if(Convert.isNotNullEmpty(tree_nid))
{
	DTNode ab = tree.findNodeById(tree_nid);
	if(ab==null)
	{
		out.print("no tree node found") ;
		return ;
	}
	title = ab.getTitle() ;
}

%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(400,220);
</script>
</head>
<body>
<form class="layui-form" action="">
  
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang>:</label>
    <div class="layui-input-block">
      <input type="text" id="title" name="title" value="<%=title %>"  autocomplete="off" class="layui-input">
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var treeid= "<%=treeid%>";
var tree_nid = "<%=tree_nid%>" ;

layui.use('form', function(){
	  var form = layui.form;
});
	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function do_submit(cb)
{
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'<wbt:g>pls,input,title</wbt:g>') ;
		return ;
	}
	cb(true,{title:tt});
}

</script>
</html>                                                                                                                                                                                                                            