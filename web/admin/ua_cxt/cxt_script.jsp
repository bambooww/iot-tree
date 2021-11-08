<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>	
<%

		if(!Convert.checkReqEmpty(request, out, "path"))
			return;
		String path = request.getParameter("path") ;
		UANode n = UAUtil.findNodeByPath(path) ;
		if(n==null)
		{
			out.print("no node found") ;
			return ;
		}
		//UANode n = rep.findNodeById(id) ;
		
		if(!(n instanceof UANodeOCTags))
		{
			out.print("not node oc tags") ;
			return ;
		}
		
		UANode topn = n.getTopNode() ;
		UAPrj rep = null ;
		String repname = "" ;
		String repid = "" ;
		if(topn instanceof UAPrj)
		{
			rep = (UAPrj)topn ;
			repname = rep.getName() ;
			repid = rep.getId() ;
		}
		
		UANodeOCTags ntags = (UANodeOCTags)n ;
		List<UATag> tags = ntags.listTagsAll() ;
		

		String parent_p = ntags.getNodePathName() ;
		if(Convert.isNotNullEmpty(parent_p))
			parent_p +="." ;
		boolean bdlg = "true".equalsIgnoreCase(request.getParameter("dlg"));
	%>
<html>
<head>
<title>context tags lister</title>
<style>
		table{border-collapse:collapse;}
		body,td{font-size:12px;cursor:default;}
</style>

<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/ajax.js" ></script>
<script src="/_js/dlg.js" ></script>
<script>
	
</script>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<b>Context:<%=topn.getTitle() %>] / [<%=ntags.getNodePathName() %>] </b>

<table border='1' style="height:90%;width:100%">
<tr>
 <td>
 script test <input type='button' value='run' onclick="run_script_test('')"/>
 </td>
</tr>
 <tr height="70%">
  <td colspan="2">
   <textarea id='script_test' rows="6" style="overflow: scroll;width:100%;height:100%"></textarea>
  </td>
 </tr>
  <tr height="20%">

  <td  colspan="2">
  script test result
   <textarea id='script_res' rows="6" style="overflow: scroll;width:100%;height:100%"></textarea>
  </td>
 </tr>
</table>
<div id='opc_info'>
</div>
</body>
<script>
var path="<%=path%>" ;
var repname = "<%=repname%>" ;
var id = "<%=repid%>" ;

function log(s)
{
	document.getElementById('log_inf').innerHTML = s ;
}
	

function run_script_test(fn)
{
	var scode = document.getElementById('script_test').value ;
	if(scode==null||scode==''||trim(scode)=='')
		return ;
	send_ajax('cxt_script_test.jsp','path='+path+'&txt='+utf8UrlEncode(scode),
		function(bsucc,ret)
		{
			document.getElementById('script_res').value = ret ;
		},false) ;
}

</script>
</html>