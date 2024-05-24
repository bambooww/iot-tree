<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","netid"))
			return ;
	String prjid = request.getParameter("prjid");
	String netid = request.getParameter("netid") ;
	String nodeid = request.getParameter("nodeid") ;
	String moduleid = request.getParameter("moduleid") ;
	
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager mnm= MNManager.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	MNBase node = null;
	if(Convert.isNotNullEmpty(nodeid))
		node = net.getNodeById(nodeid) ;
	else
		node = net.getModuleById(moduleid) ;
	if(node==null)
	{
		out.print("no node found") ;
		return ;
	}
	
	String tp = node.getTPFull() ;
	String title = node.getTitle() ;

	String pm_url = "./nodes/"+tp+"_pm.jsp" ;
	System.out.println(pm_url);
	JSONObject jo = node.getParamJO() ;
	String jstr = "{}" ;
	if(jo!=null)
		jstr = jo.toString() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script type="text/javascript">

var pm_url="<%=pm_url%>" ;
var pm_jo = <%=jstr%> ;

var form ;

function init_pm()
{
	send_ajax(pm_url,{},(bsucc,ret)=>{
		$("#pm_cont").html(ret) ;
	}) ;

}

function on_init_pm_ok()
{// called by sub pm js
	if(get_pm_size!=undefined && get_pm_size)
	{
		let wh = get_pm_size() ;
		dlg.resize_to(wh.w,wh.h+100) ;
	}
	
	if(set_pm_jo)
	{
		set_pm_jo(pm_jo) ;
	}
	
	 form.render();
}
	
	
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
	if(!get_pm_jo)
	{
		cb(false,"no pm edit items found") ;
		return ;
	}
	
	var tt = $('#title').val();
	
	let pmjo = get_pm_jo();
	if(typeof(pmjo) == "string")
	{
		cb(false,pmjo) ;
		return ;
	}
	cb(true,{title:tt,pm_jo:pmjo});
}
</script>
<style>
</style>
</head>

<body>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
    <div class="layui-input-inline" style="width:66%;">
      <input type="text" id="title" name="title" value="<%=title %>"  class="layui-input">
    </div>
  </div>
  <div id="pm_cont">
  	
  </div>
 </form>
</body>

<script type="text/javascript">


layui.use('form', function(){
	  form = layui.form;
	  form.render();
	  
	  init_pm() ;
	 
});
</script>

</html>                                                                                                                                                                                                                            