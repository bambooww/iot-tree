<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.service.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.driver.bacnet.*,
				java.security.cert.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
BACnetService ser = (BACnetService)ServiceManager.getInstance().getService(BACnetService.NAME);
	HashMap<String,String> pms = ser.getConfPMS() ;
	boolean enable = ser.isEnable();//ser.isMqttEn();
	//boolean tcp_en = false;//ser.isTcpEn();
	int port =  ser.getLocPort() ;
	
	String chked_en = "" ;
	if(enable)
		chked_en = "checked=checked";
	//if(tcp_en)
	//	chked_tcp_en = "checked=checked";
	
	String user = "";// ser.getAuthUser() ;
	String psw =  "";// ser.getAuthPsw() ;
	
	String loc_ip = ser.getLocIP() ;
	String dev_id = ser.getDevId()>0?""+ser.getDevId():"";
	String dev_n = ser.getDevName() ;
	HashMap<String,Integer> prjid2base_iid = ser.getPrjId2BaseIID() ;
	
	JSONObject prjid2biid_jo = new JSONObject() ;
	if(prjid2base_iid!=null)
		prjid2biid_jo = new JSONObject(prjid2base_iid);
%>
<html>
<head>
<title>editor</title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(700,720);
</script>
<style>
.prji {position: relative;min-width:50px;height:40px;border:1px solid;border-radius: 3px;}
.prji .tt {position: absolute;left:3px;top:1px;font-size:12px;font-weight: bold;}
.prji .base_iid {position: absolute;left:7px;top:18px;font-size:12px;}
.prji .addr {position: absolute;left:200px;top:19px;font-size:12px;color:green;font-weight: bold;}
.rejected {border-color: red;}
.trusted {border-color: green;}
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>enable</wbt:g>:</label>
	  <div class="layui-input-inline" style="width: 80px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>local</wbt:g> IP:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	  	<select id="loc_ip">
	  	<option value="" > --- </option>
<%
for(BACnetService.AddrItem ai: BACnetService.listAddrItemAll())
{
	String seled = ai.equalsIP(loc_ip)?"selected":"" ;
%><option value="<%=ai.ipAddr.getHostAddress()%>" <%=seled %>><%=ai.toString() %></option>
<%
}
%>
</select>
	  </div>
	  <div class="layui-form-mid"><wbt:g>port</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="number" id="loc_port" name="loc_port" value="<%=port%>"  class="layui-input">
	  </div>
  </div>

<div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>dev</wbt:g> ID:</label>
	  <div class="layui-input-inline" style="width: 80px;">
	    <input type="number" id="dev_id" name="dev_id" value="<%=dev_id%>"  class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>dev,name</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	  	<input type="text" id="dev_n" name="dev_n" value="<%=dev_n%>"  class="layui-input">
	 </div>
 </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:g>prj</wbt:g></label>
    <div class="layui-input-inline" style="width:500px;height:350px;" >
    	<div id="prj_base_iid_list" style="overflow-y:auto;width:100%;height:50%;border:1px solid #ccc;">
    	</div>
    	<div id="prj_list" style="overflow-y:auto;width:100%;height:50%;border:1px solid #ccc;">
<%
List<UAPrj> prjs = UAManager.getInstance().listPrjs() ;
for(UAPrj prj:prjs)
{
	String chked = ser.hasPrjId(prj.getId())?"checked":"" ;
			
%><div class="prj"><input type="checkbox" class="chk_prj" id="prj_<%=prj.getId() %>" value="<%=prj.getId() %>" prj_tt="<%=prj.getTitle() %>" <%=chked %> lay-ignore onchange="on_prj_sel_chg()"/><%=prj.getTitle() %></div><%
}
%>
    	</div>
      
    </div>
  </div>
  <%--
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>security,mode</wbt:g>:</label>
	  <div class="layui-input-inline" style="width: 350px;">
	    <input type="checkbox" id="sm_none" name="sm_none"  lay-skin="primary"  lay-filter="sm_none" class="layui-input"> None&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    <input type="checkbox" id="sm_sign" name="sm_sign"   lay-skin="primary"   lay-filter="sm_sign" class="layui-input"> Sign&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    <input type="checkbox" id="sm_sign_enc" name="sm_sign_enc"   lay-skin="primary"   lay-filter="sm_sign_enc" class="layui-input"> Sign &amp; Encrypt
	  </div>
	  
  </div>
   --%>
</form>
</body>
<script type="text/javascript">

var prjid2biid = <%=prjid2biid_jo%> ;

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	
var cert_jo = null ;
	
function win_close()
{
	dlg.close(0);
}

function update_ui()
{
	let prjs = [] ;
	$(".chk_prj").each(function(){
		let ob =$(this) ;
		if(ob.prop("checked"))
		{
			let tt = ob.attr("prj_tt") ;
			let prjid = ob.val() ;
			prjs.push({prjid:prjid,tt:tt}) ;
		}
	});
	let tmps ="";
	for(let prj of prjs)
	{
		let biid = prjid2biid[prj.prjid];
		if(biid===''||biid<0)
			biid="";
		let addr ="[]" ;
		if(biid>=0)
			addr = biid*1000000+" - " + ((biid+1)*1000000-1) ;
		tmps += `<div class="prji" id="pid_\${prj.prjid}">
			<div class="tt">\${prj.tt}</div>
			<div class="base_iid">IID Base:<input type="number" class="prj_biid" prjid="\${prj.prjid}" style="width:50px;" min="0" max="2000" step="1" value="\${biid}" onchange="on_base_iid_chg(this)"/> x 1000000</div>
			<div class="addr">\${addr}</div>
			</div>` ;
	}
	$("#prj_base_iid_list").html(tmps) ;
}

function on_base_iid_chg(ele)
{
	let ob = $(ele) ;
	let biid = parseInt(ob.val()) ;
	//dlg.msg(biid);
	if(isNaN(biid) || biid==="")
	{
		dlg.msg("please input IID Base") ;
		biid=-1 ;
	}
	else if(biid<0||biid>2000)
	{
		dlg.msg("IID Base must between 0-2000") ;
		biid=-1 ;
	}
	let addr ="[]" ;
	if(biid>=0)
		addr = biid*1000000+" - " + ((biid+1)*1000000-1) ;
	let prjid = ob.attr("prjid") ;
	let prji = $("#pid_"+prjid) ;
	prji.find(".addr").html(addr) ;
	prjid2biid[prjid]=biid;
}

function on_prj_sel_chg()
{
	update_ui();
}

update_ui();

function do_submit(cb)
{
	let enable = $("#enable").prop("checked") ;
	
	let dev_id = parseInt($('#dev_id').val());
	if(isNaN(dev_id)||dev_id<0)
	{
		cb(false,"<wbt:g>pls,input,valid,dev</wbt:g> ID") ;
		return ;
	}
	let dev_n = $("#dev_n").val();
	if(!dev_n)
	{
		cb(false,"<wbt:g>pls,input,dev,name</wbt:g>");
		return;
	}
	let loc_ip = $('#loc_ip').val();
	if(!loc_ip)
	{
		cb(false,"<wbt:g>pls,select,local</wbt:g> IP");
		return;
	}
	let loc_port = parseInt($("#loc_port").val());
	if(isNaN(loc_port)||loc_port<0||loc_port>65535)
	{
		cb(false,"<wbt:g>pls,input,valid,local,port</wbt:g>") ;
		return ;
	}
	//var auth_users = $('#users').val();
	let prjids = [] ;
	$(".chk_prj").each(function(){
		let ob =$(this) ;
		if(ob.prop("checked"))
			prjids.push(ob.val()) ;
	})
	
	let biid_err = null ;
	let biids=[];
	let prjid_biid = [] ;
	$(".prj_biid").each(function(){
		let ob = $(this) ;
		let biid = parseInt(ob.val()) ;
		if(biid==="" || isNaN(biid))
		{
			biid_err = "IID Base cannot empty ,it must be integer" ;
			return ;
		}
		if(biid<0||biid>2000)
		{
			biid_err = "IID Base must between 0-2000" ;
			return ;
		}
		if(biids.indexOf(biid)>=0)
		{
			biid_err = "IID Base must different in projects" ;
			return ;
		}
		let prjid = ob.attr("prjid") ;
		biids.push(biid) ;
		prjid_biid.push(prjid+"="+biid) ;
	});
	
	if(biid_err)
	{
		cb(false,biid_err) ;return ;
	}
	
	//let sm_none = ""+$("#sm_none").prop("checked") ;
	//let sm_sign = ""+$("#sm_sign").prop("checked") ;
	//let sm_sign_enc = ""+$("#sm_sign_enc").prop("checked") ;
	let pm = {enable:enable,dev_id:dev_id,dev_n:dev_n,loc_ip:loc_ip,
			loc_port:loc_port,prjs:prjids.join(","),prjid2base_iid:prjid_biid.join(',')};
	//console.log(pm);
	cb(true,pm);
}

</script>
</html>