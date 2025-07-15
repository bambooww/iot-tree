<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "cxt_uid"))
		return ;
	String cxt_uid = request.getParameter("cxt_uid");
	MNCxtPk cxt_pk = MNCxtPk.getCxtPkByUID(cxt_uid) ;
	if(cxt_pk==null)
	{
		out.print("no cxt pk") ;
		return;
	}
	String n = request.getParameter("n") ;
	MNCxtValTP vt = null;
	String def_vstr = "" ;
	MNCxtVar.KeepTP keep_tp = MNCxtVar.KeepTP.mem ;
	
	MNCxtVar cxt_var =null;
	if(Convert.isNotNullEmpty(n))
	{
		cxt_var = cxt_pk.CXT_getVar(n) ;
		if(cxt_var!=null)
		{
			//out.print("no var with name="+n+" found") ;
			//return ;
			vt = cxt_var.getValTP() ;
			def_vstr = cxt_var.getDefaultValStr() ;
			keep_tp = cxt_var.getKeepTP() ;
		}
	}
	
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(550,500);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">

 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>name</wbt:lang></label>
    <div class="layui-input-inline" style="width:200px;">
<%
if(Convert.isNotNullEmpty(n))
{
%><div class="layui-form-mid"><%=n %></div><%
}
else
{
%>
      <input type="text" name="n" id="n" value="<%=n %>"  class="layui-input"/>
<%
}
%>
    </div>
  </div>

 <div class="layui-form-item">
    <label class="layui-form-label">Keep TP</label>
    <div class="layui-input-inline" style="width:30%;">
      <select id="ktp">
<%
for(MNCxtVar.KeepTP ctp:MNCxtVar.KeepTP.values())
{
%><option value="<%=ctp.getInt()%>" <%=(keep_tp==ctp?"selected":"") %>><%=ctp.getTitle() %></option>
<%
}
%>
      </select>
    </div>
    <div class="layui-form-mid">Value TP</div>
    <div class="layui-input-inline" style="width:30%;">
      <select id="vt">
<%
for(MNCxtValTP ctp:MNCxtValTP.values())
{
%><option value="<%=ctp.name()%>" <%=(vt==ctp?"selected":"") %>><%=ctp.getTitle() %></option>
<%
}
%>
      </select>
    </div>
  </div>
  
   <div class="layui-form-item">
    <label class="layui-form-label">Default Val</label>
    <div class="layui-input-inline" style="width:200px;">
      <input type="text" name="defv" id="defv" value="<%=def_vstr %>"  class="layui-input"/>
    </div>
  </div>
</form>
</body>
<script type="text/javascript">
var n = "<%=n%>" ;

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
	if(!n)
	{
		n = $("#n").val() ;
		if(!n)
		{
			cb(false,"please input name") ;
			return ;
		}
	}
	let ktp = get_input_val('ktp',0,true);
	let vt =  $('#vt').val();
	let defv = $("#defv").val() ;
	cb(true,{n:n,ktp:ktp,vt:vt,defv:defv});
}

</script>
</html>