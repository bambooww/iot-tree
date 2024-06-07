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
	String itemid = request.getParameter("itemid") ;
	
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
	MNBase item =net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	
	String tp = item.getTPFull() ;
	String title = item.getTitle() ;

	String pm_url = item.getCat().getParamUrl(item);// "./nodes/"+tp+"_pm.jsp" ;
	if(pm_url==null)
		pm_url="" ;
	if(Convert.isNotNullEmpty(pm_url))
	{
		int k = pm_url.lastIndexOf('?') ;
		if(k<=0)
			pm_url+="?prjid="+prjid ;
		else
			pm_url+= "&prjid="+prjid ;
		
	}	
	//System.out.println(pm_url);
	JSONObject jo = item.getParamJO() ;
	String jstr = "{}" ;
	if(jo!=null)
		jstr = jo.toString() ;
	
	String ben_chked = item.isEnable()?"checked":"" ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tree"/>

</jsp:include>
<script type="text/javascript">

var prjid="<%=prjid%>";
var netid="<%=netid%>";
var itemid="<%=itemid%>";

var pm_url="<%=pm_url%>" ;
var pm_jo = <%=jstr%> ;

var form ;
var element;

function init_pm()
{
	if(!pm_url)
		return ;

	send_ajax(pm_url,{prjid:prjid,netid:netid,itemid:itemid,pm_jo:JSON.stringify(pm_jo)},(bsucc,ret)=>{
		$("#pm_cont").html(ret) ;
		
		on_init_pm_ok() ;
		
		if(typeof(on_after_pm_show)=="function")
			on_after_pm_show(form);
		
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
	
	let tt = $('#title').val();
	let ben = $("#enable").prop("checked") ;
	let pmjo = get_pm_jo();
	if(typeof(pmjo) == "string")
	{
		cb(false,pmjo) ;
		return ;
	}
	let rr = {title:tt,enable:ben,pm_jo:pmjo};
	cb(true,rr);
}
</script>
<style>
</style>
</head>

<body>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
    <div class="layui-input-inline" style="width:46%;">
      <input type="text" id="title" name="title" value="<%=title %>"  class="layui-input">
    </div>
    <div class="layui-input-inline" style="width:20%;">
      <input type="checkbox" class="layui-input" lay-skin="primary" id="enable"  <%=ben_chked %> /> Enable
    </div>
  </div>
  <div id="pm_cont">
  	
  </div>
 </form>
</body>

<script type="text/javascript">


layui.use('form', function(){
	  form = layui.form;
	  element = layui.element;
	  form.render();
	  
	  init_pm() ;
	 
});
</script>

</html>                                                                                                                                                                                                                            