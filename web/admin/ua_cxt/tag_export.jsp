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
	List<UATag> tags_sys = null ;
	JSONArray jarr = null ;
	String full_paths = "" ;
	String inprj_paths = "" ;
	String innd_paths = "" ;
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
		tags_sys = ntags.getSysTags() ;
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
			
			full_paths += tag.getNodePathCxt()+"\r\n" ;
			inprj_paths += tag.getNodeCxtPathInPrj() +"\r\n" ;
			innd_paths +=  tag.getNodeCxtPathIn(ntags) +"\r\n" ;
		}
		
		if(tags_sys!=null)
		{
			for(UATag tag:tags_sys)
			{
				full_paths += tag.getNodePathCxt()+"\r\n" ;
				inprj_paths += tag.getNodeCxtPathInPrj() +"\r\n" ;
				innd_paths +=  tag.getNodeCxtPathIn(ntags) +"\r\n" ;
			}
		}
		
		
	}
	
%><html>
<head>
<title>Tag Exporter </title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script type="text/javascript" src="../js/tab.js" ></script>
<link rel="stylesheet" href="../js/tab.css" />
<style type="text/css">
.hd{font-weight:bold;color:green;margin:3px;}
textarea {width:842px;height:485px;margin:3px;}
</style>
<script>
dlg.resize_to(850,600);
</script>

</head>
<body style="overflow: hidden;">
<div class="tab_c" style="background-color: #eee;">
    	<ul></ul>
         <div></div>
</div>
</body>
<script type="text/javascript">
var exp_all_pt = "<wbt:g>exp_all_pt</wbt:g>" ;
var exp_path_only = "<wbt:g>exp_path_only</wbt:g>" ;
var full_paths = `<%=full_paths%>`;
var inprj_paths = `<%=inprj_paths%>`;
var innd_paths =`<%=innd_paths%>`;
var inp_tags = <%=jarr%> ;
var chk_tag_ns = dlg.get_opener_opt("chk_tag_ns") ;
if(!inp_tags)
{
	var cxt_path = dlg.get_opener_opt("cxt_path") ;
	inp_tags = dlg.get_opener_opt("tags") ;
	//console.log(cxt_path,inp_tags) ;
}

$(".tab_c").tab();

let tmps = `<div class="hd">\${exp_all_pt}</div><textarea id="txt" style=""></textarea>`;
$('.tab_c').tab('addTab', {'title': '<wbt:g>exp_all_n</wbt:g>', 'id': 'exp_all', 'content': tmps});

tmps = `<div class="hd">\${exp_path_only} <i class="fa fa-arrow-right"></i>
	<input type="radio" name="path_tp" value="full" checked onclick="chg_path_tp()"/> <wbt:g>full,path</wbt:g>
	<input type="radio" name="path_tp" value="in_prj" onclick="chg_path_tp()"/> <wbt:g>in_prj,path</wbt:g>
	<input type="radio" name="path_tp" value="in_nd" onclick="chg_path_tp()"/> <wbt:g>in_nd,path</wbt:g>
	</div> <textarea id="txt_path">\${full_paths}</textarea>`;
$('.tab_c').tab('addTab', {'title': '<wbt:g>exp_path_only_n</wbt:g>', 'id': 'exp_path_only', 'content': tmps});
$(".tab_c").tab('selectTab', 'exp_all');

function chg_path_tp()
{
	const val = $('input[name="path_tp"]:checked').val();
	switch(val)
	{
	case 'full':
		$("#txt_path").val(full_paths) ;break;
	case 'in_prj':
		$("#txt_path").val(inprj_paths) ;break;
	case 'in_nd':
		$("#txt_path").val(innd_paths) ;break;
	}
}

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