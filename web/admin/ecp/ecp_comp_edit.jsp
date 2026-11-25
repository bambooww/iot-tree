<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,org.iottree.core.ecp.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
//if(!Convert.checkReqEmpty(request, out, "libid"))
//	return ;

	String libid = request.getParameter("libid") ;
String compid = request.getParameter("compid") ;
	String title = "" ;
	String desc="" ;
	String lib_title = "" ;
	ECPCompLib lib = null;
	if(Convert.isNotNullEmpty(libid))
	{
		lib = ECPCompManager.getInstance().getLibById(libid) ;
		if(lib==null)
		{
			out.print("no lib found") ;
			return ;
		}
		lib_title = lib.getLibTitle() ;
	}
		
		ECPComp comp = null ;
		if(Convert.isNotNullEmpty(compid))
		{
			comp = lib.getCompById(compid) ;
			if(comp==null)
			{
				out.print("no comp found") ;
				return ;
			}
			title = comp.getTitle() ;
			desc = comp.getDesc() ;
		}
		
		
				
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(450,320);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>lib</wbt:lang>:</label>
    <div class="layui-input-inline" style="width:300px" >
<%
if(lib!=null)
{
	String tt = lib.getLibTitle() ;
%><input type="hidden" id="libid"  name="libid" value="<%=lib.getLibId() %>"  autocomplete="off" class="layui-input">
<input type="text" id="libtt" readonly name="libtt" value="<%=tt %>"  autocomplete="off" class="layui-input">
<%
}
else
{
%>
      <select id="libid" >
<%
    for(ECPCompLib complib:ECPCompManager.getInstance().getLibMap().values())
	{
			String cssstr = "" ;
			String tmpid = "" ;
			String clibid = complib.getLibId() ;
			String tt = complib.getLibTitle() ;
			%><option value="<%=clibid%>"><%=tt %></option><%
	}
%>
      </select>
<%
}
%>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang>:</label>
    <div class="layui-input-inline" style="width:300px">
      <input type="text" id="title" name="title" value="<%=title %>"  autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>desc</wbt:lang>:</label>
    <div class="layui-input-inline" style="width:300px">
      <textarea id="desc" name="desc"  autocomplete="off" class="layui-input" style="height:70px;"><%=desc %></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var libid= "<%=libid%>";

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
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
	let desc = $("#desc").val() ;
	let libid = $("#libid").val();
	if(!libid)
	{
		cb(false,'<wbt:g>pls,select,lib</wbt:g>') ;
		return ;
	}
	cb(true,{libid:libid,title:tt,desc:desc});
}

</script>
</html>                                                                                                                                                                                                                            