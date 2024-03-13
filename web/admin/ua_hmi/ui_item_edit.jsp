<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.ui.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.store.record.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%!

	 %><%
	if(!Convert.checkReqEmpty(request, out,"prjid"))
		return ;
	 String prjid = request.getParameter("prjid") ;
	 String tagids_str = request.getParameter("tagids") ;
	 String tempn = request.getParameter("tempn") ;
	 String id = request.getParameter("id") ;
	 
	 if(id==null)
		 id="" ;
	 UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	 if(prj==null)
	 {
		 out.print("no prj found") ;
		 return ;
	 }
	 UIManager uimgr = UIManager.getInstance(prj) ;
	 //UATag tag = null;
	 //String tagp = "" ;
	 List<String> tagids = Convert.splitStrWith(tagids_str, ",") ;
	 
	 UIItem uii = null ;
	 if(Convert.isNotNullEmpty(id))
	 {
		 uii = uimgr.getItemById(id) ;
		 if(uii==null)
		 {
			 out.println("no UIItem found") ;
			 return ;
		 }
		 tagids = uii.getTagIds() ;
		 tempn = uii.getTempName() ;
	 }
	 
	 ArrayList<UATag> tags  = null ;
	 //if(Convert.isNotNullEmpty(tagids_str))
	 {
		 tags = new ArrayList<>(tagids.size()) ;
		 for(String tagid:tagids)
		 {
			 UATag tag = (UATag)prj.findTagById(tagid) ;
			 if(tag==null)
			 {
				 out.print("no tag found in prj with id="+tagid) ;
				 return ;
			 }
			 tags.add(tag) ;
		 }
		 //tagp = tag.getNodeCxtPathInPrj() ;
	 }
	 
	 IUITemp ui_temp = uimgr.getTempByName(tempn) ;
	 if(ui_temp==null)
	 {
		 out.print("no UITemp found with name="+tempn) ;
		 return ;
	 }
	 
	 
	 String name = "" ;
	 String title = "" ;
	 String desc ="" ;
	 String chked = "" ;
	 if(uii!=null)
	 {
		 name = uii.getName() ;
		 title = uii.getTitle() ;
		 desc = uii.getDesc() ;
	 }
	 
	 JSONArray tagids_jarr = new JSONArray(tagids) ;
%>
<html>
<head>
<title>UI Item Editor </title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(690,500);
</script>
<style type="text/css">
.layui-form-label
{
	width:120px;
}

.layui-form-checkbox i
{
border-style:solid solid solid solid;
border-left: 1px;
border-color:blue;
}


.psel
{
  position:absolute;
	font-size: 15px;
	width:35px;
	top:2px;
	left:10px;
}

.enable_c
{
	font-size: 15px;
}

.toppp
{
position:relative;
	border:1px solid;
	min-height:30px;
	left:0%;
	width:100%;
	color:#279894;
	border-color:#e6e6e6;
	margin-bottom: 3px;
}
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
<div class="layui-form-item">
    <label class="layui-form-label">Selected Tags:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <ul class="toppp">
<%
for(UATag tag:tags)
{
	%>
<li><%=tag.getNodeCxtPathInPrj() %> (<%=tag.getTitle() %>)</li>
	<%
}
%>
</ul>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">UI Templates:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <div class="toppp">
 <%=ui_temp.getName() %> (<%=ui_temp.getTitle() %>)  <%=ui_temp.getWidth() %> X <%=ui_temp.getHeight() %> <br>
 <%=ui_temp.getDesc() %> 
</div>
    </div>
  </div>
  <hr class="layui-border-green">
<div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  autocomplete="off" class="layui-input">
	  </div>
	  <%--
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 100px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	   --%>
 </div>
 
<div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-inline" style="width: 450px;">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>

 </form>
</body>
<script type="text/javascript">
var form ;
var ow =dlg.get_opener_w();
var dd = dlg.get_opener_opt("dd");
var prjid = "<%=prjid%>" ;
var id = "<%=id%>" ;
var tagids = <%=tagids_jarr%>
;var tempn = "<%=tempn%>" ;

layui.use('form', function(){
	  form = layui.form;
	  
	  form.render();
});



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
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = n;
	}
	
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;

	var ben = $("#enable").prop("checked") ;
	
	let sor_name = $('#sor_name').val();
	if(!sor_name)
	{
		//cb(false,"Data source cannot be null") ;
		//return ;
		sor_name="" ;
	}

	cb(true,{id:id,tagids:tagids,temp:tempn,n:n,t:tt,d:desc});
	return ;
}

</script>
</html>