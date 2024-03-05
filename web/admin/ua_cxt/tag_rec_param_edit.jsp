<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.store.record.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%!

	 %><%
	if(!Convert.checkReqEmpty(request, out,"prjid", "tag"))
		return ;
	 String prjid = request.getParameter("prjid") ;
	 String tagp = request.getParameter("tag") ;
	 
	 UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	 if(prj==null)
	 {
		 out.print("no prj found") ;
		 return ;
	 }
	 
	 UATag tag = (UATag)prj.getDescendantNodeByPath(tagp) ;
	 if(tag==null)
	 {
		 out.print("no tag found in prj") ;
		 return ;
	 }
	 RecManager recmgr = RecManager.getInstance(prj) ;
	 if(!recmgr.checkTagCanRecord(tag))
	 {
		 out.print("the tag is cannot be recorded") ;
		 return ;
	 }
	 
	 RecTagParam tagparam = recmgr.getRecTagParam(tag) ;
	 long gather_intv = -1 ;
	 RecValStyle val_style = RecValStyle.successive_normal ;
	 if(tag.getValTp()==UAVal.ValTP.vt_bool)
		 val_style = RecValStyle.discrete ;
	 if(tagparam!=null)
	 {
		 gather_intv = tagparam.getGatherIntv() ;
		 val_style = tagparam.getValStyle() ;
	 }
	 int val_style_i = val_style.getVal() ;
%>
<html>
<head>
<title>Tag Record Param Editor </title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(590,500);
</script>
<style type="text/css">
.layui-form-label
{
	width:120px;
}
.prompt
{
	border:1px solid;
	margin:10px;
}
</style>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item"  id="dt_tp">
    <label class="layui-form-label">Value Style</label>
    <div class="layui-input-inline" style="width: 210px;">
      <select  id="val_style"  name="val_style"  class="layui-input" placeholder="" lay-filter="val_style">
<%
boolean bfirst = true ;
for(RecValStyle tp:RecValStyle.values())
{
	int tpv = tp.getVal() ;
	 %><option value="<%=tpv%>"><%=tp.getTitle() %></option><%
}
%>
      </select>
    </div>
    <div class="layui-form-mid">Enable</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="en" name="en"  title="Enable or Not" lay-skin="switch"> 
	  </div>
</div>

  <div class="layui-card" id="card_scaling" >

   <div class="layui-form-item">
    <label class="layui-form-label">Gather Interval MS</label>
		<div class="layui-input-inline" style="width: 250px;">
	    <input type="number" id="gather_intv" name="gather_intv" class="layui-input" value=""/>
	  </div>
	</div>
   
</div>

 </form>
</body>
<script type="text/javascript">
var form ;
var ow =dlg.get_opener_w();
var dd = dlg.get_opener_opt("dd");
var tag = "<%=tagp%>" ;
var gather_intv = <%=gather_intv%> ;
var val_style = <%=val_style_i%> ;

layui.use('form', function(){
	  form = layui.form;
	  
	  form.on('select(val_style)', function (data) {
		　//console.log(data);　
		　//var n = data.value;
		
			update_ui();
		});

	  $("#val_style").val(val_style) ;
	  $("#gather_intv").val(gather_intv);
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
	var valsty = $("#val_style").val() ;
	let ret = {val_style:valsty} ;
	ret.en = $("#en").prop("checked") ;
    ret.gather_intv = get_input_val("gather_intv",-1,true) ;
	cb(true,ret);
	return ;
}

</script>
</html>