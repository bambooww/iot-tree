<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.store.record.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "prjid","tag"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
String tagpath = request.getParameter("tag") ;
UATag tag =prj.getTagByPath(tagpath) ;
if(tag==null || tag.getBelongToPrj()!=prj)
{
	out.print("no tag found") ;
	return ;
}
RecManager recm = RecManager.getInstance(prjid) ;
if(!recm.checkTagCanRecord(tag))
{
	out.print("tag cannot be recorded") ;
	return ;
}
List<RecProL1> rps = recm.listUsingRecProsByTag(tag) ;
if(rps==null)
	rps = Arrays.asList() ;
%><html>
<head>
<title></title>
<jsp:include page="./head.jsp"></jsp:include>
</head>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
border:0px;
}


.layui-tab
{
	margin:0px;
}

.layui-tab-content {
    padding: 0px;
}

.layui-tab-card>.layui-tab-title .layui-this
{
	background-color: #aeecff;
}

ol,ul
{
	margin-bottom: 1px;
}

</style>
<body marginwidth="0" marginheight="0">
<div class="layui-tab layui-tab-card" lay-filter="pro_tabs" style="top:0px;">
  <ul class="layui-tab-title">
    <li class="layui-this" lay-id="11">Basic Recorder</li>
<%
for(RecProL1 rp:rps)
{
	String id = rp.getId() ;
	String tp = rp.getTp() ;
	String tt = rp.getTitle()+"["+rp.getName()+"]" ;
	String ttt = rp.getTpTitle()+"\r\n"+rp.getTpDesc() ;
%>
    <li lay-id="22" onclick="show_pro('<%=id%>','<%=tp%>')" title="<%=ttt%>"><%=tt %></li>
<%
}
%>    
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
      <Iframe id="f1" style="width:100%;height:500px;border:0px;overflow: hidden;" src="prj_tag_rec_tss.jsp?prjid=<%=prjid%>&tag=<%=tagpath%>"></Iframe>
	</div>
<%
for(RecProL1 rp:rps)
{
	String tt = rp.getTitle()+"["+rp.getName()+"]" ;
%>
    <div class="layui-tab-item">
    	<Iframe id="f_<%=rp.getId() %>" style="width:100%;height:500px;border:0px;overflow: hidden;" ></Iframe>
	</div>
<%
}
%>
  </div>
</div>

</body>
<script type="text/javascript">
dlg.resize_to(1000,800) ;
var prjid = "<%=prjid%>"
var tagp = "<%=tagpath%>" ;
var element ;
layui.use(function(){
	 
	element = layui.element;
	
	//element.on("tab(pro_tabs)",function(data){
	//	console.log(data.index,data.elem) ;
	//});
});

function resize_h()
{
	var h = $(window).height()-55;
	$("iframe").css("height",h+"px") ;
	//$("#f2").css("height",h+"px") ;
}

function show_pro(proid,tp)
{
	let src = $("#f_"+proid).attr("src") ;
	if(src)
		return ;
	$("#f_"+proid).attr("src","prj_tag_recp_"+tp+".jsp?prjid="+prjid+"&tag="+tagp+"&proid="+proid) ;
}

$(window).resize(function(){
	resize_h();
	if(element)
		element.render();
});
resize_h();
//$("#f1").attr("src","rec_mgr.jsp?prjid="+prjid);
//$("#f2").attr("src","store.jsp?prjid="+prjid);
</script>
</html>