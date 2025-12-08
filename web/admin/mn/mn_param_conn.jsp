<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,org.iottree.core.util.jt.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid"))
			return ;
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String uid = request.getParameter("uid") ;
	
	MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
	if(mnm==null)
	{
		out.print("no MsgNet Manager with container_id="+container_id) ;
		return ;
	}

	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	MNConn conn = net.getConnByUID(uid) ;
	if(conn==null)
	{
		out.print("no conn found") ;
		return ;
	}
	
	boolean can_save = true ;
	String title = conn.getTransTP();
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tree"/>
</jsp:include>
<script type="text/javascript">

var container_id="<%=container_id%>";
var netid="<%=netid%>";
var uid="<%=uid%>";

var form ;
var element;

function on_init_pm_ok()
{// called by sub pm js
	if(typeof(get_pm_size)=='function')
	{
		let wh = get_pm_size() ;
		if(wh.w<800)
			wh.w = 800;
		dlg.resize_to(wh.w,wh.h+110) ;
	}
	
	if(typeof(set_pm_jo)=='function')
	{
		set_pm_jo(__pm_jo) ;
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
	let pmjo = {};
	if(typeof (get_pm_jo)=='function')
	{
		//cb(false,"no pm edit items found") ;
		//return ;\
		pmjo = get_pm_jo();
	}
	
	let tt = $('#title').val();
	let marks = $("#marks").val();
	let ben = $("#enable").prop("checked") ;
	let show_out_tt = $("#show_out_tt").prop("checked") ;
	let res_name = $("#res_name").val() ;
	if(typeof(pmjo) == "string")
	{
		cb(false,pmjo) ;
		return ;
	}
	let rr = {title:tt,enable:ben,show_out_tt:show_out_tt,pm_jo:pmjo,res_name:res_name,marks:marks};
	cb(true,rr);
}

</script>
<style>
.save_btn
{
	position: absolute;
	right:5px;
	top:5px;
	color:#27ba7d;
	
}

.in_title {position: absolute;left:2px;top:50px;border:1px solid #ccc;background-color: #003a36;color:#00ffe2;cursor: pointer;}
.in_title:hover ~ .child  {display: block;}
.layui-form-item .layui-form-checkbox[lay-skin=primary] {
    margin-top:0px;
}
.layui-form-item {
    margin-bottom: 3px;
    margin-top: 3px;
}
</style>
</head>

<body>
<form class="layui-form"  onsubmit="return false;">
<div style="border-bottom:1px solid #ccc;">
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>title</w:g>:</label>
    <div class="layui-input-inline" style="width:230px;">
      <input type="text" id="title" name="title" value="<%=title %>"  class="layui-input">
    </div>
    <div class="layui-input-inline" style="width:150px;">
      <w:g>enable</w:g>&nbsp;<input type="checkbox" class="layui-input" lay-skin="primary" id="enable"  <%=ben_chked %> /><br>
      <w:g>show_out_tt</w:g>&nbsp;<input type="checkbox" class="layui-input" lay-skin="primary" id="show_out_tt"   <%=bshow_out_tt_chked %> />
    </div>
    <label class="layui-form-mid"><w:g>mark</w:g>:</label>
    <div class="layui-input-inline" style="width:170px;">
      <input type="text" class="layui-input" lay-skin="primary" id="marks"  value="<%=mark_str %>" />
    </div>
  </div>

</div>
 
 </form>
<%


%>

</body>

<script type="text/javascript">


layui.use('form', function(){
	  form = layui.form;
	  element = layui.element;
	  form.render();
	  
	  dlg.resize_to(800,300) ;
	  init_pm() ;
	 
});


</script>

</html>                                                                                                                                                                                                                            