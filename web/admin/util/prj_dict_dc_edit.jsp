<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;
	String prjid = request.getParameter("prjid") ;
UAPrj rep  = UAManager.getInstance().getPrjById(prjid) ;
if(rep==null)
{
	out.print("no prj found");
	return ;
}
PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prjid) ;
String id = request.getParameter("id") ;

String name = "" ;
boolean benable = true;
String title = "" ;
String desc = "" ;
List<String> bind_for = null ;
DataClass.BindStyle bind_sty = null;
DataClass dc = null ;
if(Convert.isNotNullEmpty(id))
{
	dc = pdc.getDataClassById(id) ;
	if(dc==null)
	{
		out.println("no PrjDataClass found");
		return ;
	}
	
	name = dc.getClassName() ;
	title = dc.getClassTitle() ;
	bind_for = dc.getBindForList() ;
	bind_sty = dc.getBindStyle() ;
	benable = dc.isClassEnable() ;
}
String chked = "" ;
if(benable)
	chked = "checked='checked'" ;
%>
<html>
<head>
<title>DataClass editor</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(600,400);
</script>
<style>
.layui-form-label
{
	width:100px;
}
</style>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label"><w:g>name</w:g></label>
    <div class="layui-input-inline" style="width:100px;">
      <input type="text" name="name" id="name" value="<%=name %>" class="layui-input"/>
    </div>
    <div class="layui-form-mid"><w:g>title</w:g></div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" name="title" id="title" value="<%=title %>" class="layui-input"/>
	  </div>
    <div class="layui-form-mid"><w:g>enable</w:g></div>
	  <div class="layui-input-inline" style="width: 50px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label"><w:g>bind_for</w:g></label>
    <div class="layui-input-inline">
    <span style="border:1;white-space: nowrap;">
<%
	for(int k = 0 ; k < DataClass.BIND_FOR.length ; k ++)
	{
		String tmpn = DataClass.BIND_FOR[k] ;
		String tmpt=  DataClass.BIND_FOR_TITLE[k] ;
		chked = "" ;
		if(dc!=null&&dc.hasBindFor(tmpn))
			chked="checked=checked" ;
%>
<input type="checkbox"  id="bind_for_<%=tmpn %>" bind_for="<%=tmpn %>"
	 name="bind_for" title="<%=tmpt %>"  value="<%=tmpn %>"  <%=chked %>/>
<%
		if((k+1)%3==0)
		{
%><br><%
		}
	}
%>
	</span>
        
    </div>
  </div>
  
     <div class="layui-form-item">
    <label class="layui-form-label"><w:g>bind_sty</w:g></label>
    <div class="layui-input-inline" style="white-space: nowrap;">
       
<%
int cc = 0 ;
for(DataClass.BindStyle bs:DataClass.BindStyle.values())
{
	String bn = bs.name() ;
	String ck = (bind_sty==bs)?"checked":"";
	
%>
	<input type="radio"  id="bind_style_<%=bn %>" name="bind_style" title="<%=bs.getTitle() %>"  value="<%=bn %>"  <%=ck %>/>
<%
	if(cc==2)
	{
%><br/><%
	}
	cc++ ;
}
%>
	
    </div>
  </div>


</form>
</body>
<script type="text/javascript">

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	  
	  
	});
	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'请输入名称') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = n;
	}
	
	var desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var bind_for='' ;
	$('input[type=checkbox]:checked').each(function() {
		var bf = $(this).val() ;
	      if(bf)
	    	  bind_for+=','+bf;
	    });
	
	if(bind_for!='')
		bind_for = bind_for.substr(1) ;
	var bind_style=$("input[name='bind_style']:checked").val();
	if(!bind_style)
		bind_style="" ;
	var ben = $("#enable").prop("checked") ;
	cb(true,{name:n,title:tt,enable:ben,desc:desc,bind_for:bind_for,bind_style:bind_style});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>