<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.ext.msg_net.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.core.msgnet.nodes.*,
	org.iottree.core.station.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	MNManager mnm = MNManager.getInstanceByContainerId(container_id) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	IMNContainer mnc = net.getBelongTo().getBelongTo() ;
	if(mnc==null || !(mnc instanceof UAPrj))
	{
		out.print("no in prj") ;
		return ;
	}
	UAPrj prj = (UAPrj)mnc ;
	String prj_path = "/"+prj.getName() ;
	MNBase item =net.getItemById(itemid) ;
	if(item==null || !(item instanceof MailSender_NM))
	{
		out.print("no item found") ;
		return ;
	}
	
	MailSender_NM send_nd= (MailSender_NM)item ;
	
	String smtp_host = send_nd.getSmtpHost();
	int smtp_port = send_nd.getSmtpPort();
	boolean smtp_ssl = send_nd.isSmtpSsl() ;
	String from_mail = "" ;
	String from_name = "" ;
	String from_encod = "" ;
	String auth_user = send_nd.getAuthUser() ;
	String auth_psw = send_nd.getAuthPsw() ;
	MailSender_NM.MailAddr from_addr = send_nd.getFromAddr();
	if(from_addr!=null)
	{
		from_mail = from_addr.getMail() ;
		from_name = from_addr.getName() ;
		from_encod = from_addr.getEncod() ;
	}
	List<MailSender_NM.MailAddr> toaddrs = send_nd.getFixToAddrs();
	JSONArray to_jarr = MailSender_NM.MailAddr.toJArr(toaddrs) ;
%>
<style>
#tb_fix_to {}
#tb_fix_to td {border:1px solid #ccc;}
</style>
<div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">SMTP Host:</span>
    </div>
	  <div class="layui-input-inline" style="width: 200px;">
	    <input type="text" id="smtp_host" class="layui-input" lay-skin="primary" value="<%=smtp_host%>"/>
	  </div>
	  <div class="layui-form-mid">Port:</div>
	  <div class="layui-input-inline" style="width: 80px;">
	    <input type="number" id="smtp_port" class="layui-input" lay-skin="primary"  value="<%=smtp_port%>"/>
	  </div>
	  <div class="layui-form-mid">SSL on connect:</div>
	  <div class="layui-input-inline" style="width: 80px;">
	    <input type="checkbox" id="smtp_ssl" class="layui-input" lay-skin="primary"  <%=(smtp_ssl?"checked":"") %>/>
	  </div>
	  
</div>
<div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">From Mail:</span>
    </div>
	  <div class="layui-input-inline" style="width: 200px;">
	    <input type="email" id="from_mail" class="layui-input" lay-skin="primary" value="<%=from_mail%>"/>
	  </div>
	  <div class="layui-form-mid">Name:</div>
	  <div class="layui-input-inline" style="width: 120px;">
	    <input type="text" id="from_name" class="layui-input" lay-skin="primary"  value="<%=from_name%>"/>
	  </div>
	  <div class="layui-form-mid">Encoder:</div>
	  <div class="layui-input-inline" style="width: 120px;">
	    <input type="text" id="from_encod" class="layui-input" lay-skin="primary"  value="<%=from_encod%>"/>
	  </div>
</div>
<div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Auth User:</span>
    </div>
	  <div class="layui-input-inline" style="width: 200px;">
	    <input type="text" id="auth_user" class="layui-input" lay-skin="primary" value="<%=auth_user%>"/>
	  </div>
	  <div class="layui-form-mid">Password:</div>
	  <div class="layui-input-inline" style="width: 120px;">
	    <input type="password" id="auth_psw" class="layui-input" lay-skin="primary"  value="<%=auth_psw%>"/>
	  </div>
</div>
 
 <div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Mail To:</span>
    </div>
	  <div class="layui-input-inline" style="width: 75%;">
	    <div id="fix_to"  style="border:1px solid #ececec;width:100%;height:220px;overflow:auto">
	    </div>
	  </div>
	  <div class="layui-form-mid"><button class="layui-btn layui-btn-xs layui-btn-primary" onclick="add_fix_to()">+</button></div>
 </div>
 
<script>
var prj_path = "<%=prj_path%>";
var container_id="<%=container_id%>";
var netid="<%=netid%>";
var to_jarr = <%=to_jarr%>;

function update_ui()
{console.log(to_jarr);
	let tmps = `<table id='tb_fix_to' style="width:100%;">
						<tr><td>Mail</td><td>Name</td><td>Oper</td></tr>` ;
	
	if(to_jarr)
	{
		for(let addr of to_jarr)
		{
			tmps += `<tr class="fix_i"><td><input class="mail" value="\${addr.mail}" /></td><td><input class="name" value="\${addr.name}" /></td><td><button onclick="del_addr(this)" class="layui-btn layui-btn-xs layui-btn-primary">X</button></td></tr>` ;
		}
	}
	tmps += "</table>"
	$("#fix_to").html(tmps) ;
}

function add_fix_to()
{
	let tmps = `<tr class="fix_i"><td><input class="mail" value="" /></td><td><input class="name" value="" /></td><td><button onclick="del_addr(this)" class="layui-btn layui-btn-xs layui-btn-primary">X</button></td></tr>` ;
	$("#tb_fix_to").append(tmps);
}

function del_addr(ele)
{
	$(ele).parent().parent().remove();
}

function on_after_pm_show(form)
{
	update_ui();
}


function get_pm_jo()
{
	let ret = {} ;
	ret.smtp_host = $("#smtp_host").val();
	//ret.ignore_update = $("#ignore_update").prop("checked") ;
	ret.smtp_port = parseInt($("#smtp_port").val());
	ret.smtp_ssl = $("#smtp_ssl").prop("checked") ;
	if(isNaN(ret.smtp_port))
		return "invalid smtp host port";
	ret.auth_user = $("#auth_user").val();
	ret.auth_psw = $("#auth_psw").val();
	let ff = {} ;
	ff.mail = $("#from_mail").val();
	ff.name = $("#from_name").val();
	ret.from = ff ;
	
	let fix_to=[];
	$(".fix_i").each(function(){
		let ob = $(this);
		//console.log(ob);
		let m = ob.find(".mail").val() ;
		let n = ob.find(".name").val();
		if(!m) return;
		fix_to.push({mail:m,name:n||""});
	});
	ret.fix_to = fix_to;
	return ret ;
}

function set_pm_jo(jo)
{//console.log(jo) ;
	//tag_paths = jo.tag_paths;
	
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>