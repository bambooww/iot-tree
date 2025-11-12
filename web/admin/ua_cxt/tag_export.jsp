<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.json.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
	
%><%
	String cxt_path = request.getParameter("cxt_path") ;
	List<UATag> tags = null ;
	JSONArray jarr = null ;
	if(Convert.isNotNullEmpty(cxt_path))
	{
		UANode node = UAUtil.findNodeByPath(cxt_path) ;
		if(node==null)
		{
			out.print("node not found"); 
			return ;
		}
		
		if(!(node instanceof UANodeOCTags))
		{
			out.print("node has no tags") ;
			return ;
		}
		
		UANodeOCTags ntags = (UANodeOCTags)node ;
		tags = ntags.getNorTags() ;
		jarr = new JSONArray() ;
		for(UATag tag:tags)
		{
			JSONObject jo = new JSONObject() ;
			jo.put("n", tag.getName()) ;
			
			jo.putOpt("tp", tag.getValTpRaw().name()) ;
			jo.putOpt("addr", tag.getAddress()) ;
			jo.putOpt("t",tag.getTitle()) ;
			jo.putOpt("d", tag.getDesc()) ;
			jo.put("dec_digits",tag.getDecDigits()) ;
			jo.put("srate",tag.getScanRate()) ;
			jo.put("canw",tag.isCanWrite()) ;
			jo.putOpt("trans",tag.getValTranser()) ;
			jo.putOpt("val_opt",tag.getValOption()) ;
			jo.putOpt("unit", tag.getUnit());
			jo.putOpt("ind", tag.getIndicator()) ;
			jarr.put(jo) ;
		}
	}
	
%><html>
<head>
<title>Tag Exporter </title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>

<style type="text/css">

</style>
<script>
dlg.resize_to(850,600);
</script>

</head>
<body>
<textarea id="txt" style="width:800px;height:470px;margin-left:25px;" placeholder="[tagname] [value type] [address] [title]">
</textarea>
</body>
<script type="text/javascript">
var inp_tags = <%=jarr%> ;
var chk_tag_ns = dlg.get_opener_opt("chk_tag_ns") ;
if(!inp_tags)
{
	var cxt_path = dlg.get_opener_opt("cxt_path") ;
	inp_tags = dlg.get_opener_opt("tags") ;
	//console.log(cxt_path,inp_tags) ;
}
//console.log(chk_tag_ns,inp_tags) ;

function show_tags(tags)
{
	
	if(chk_tag_ns&&chk_tag_ns.length>0)
	{
		let tgs = [] ;
		for(let tag of tags)
		{
			if(chk_tag_ns.indexOf(tag.n)<0)
				continue ;
			tgs.push(tag) ;
		}
		tags = tgs ;
	}
	$("#txt").val(JSON.stringify(tags)) ;
}

if(inp_tags)
	show_tags(inp_tags);

</script>
</html>