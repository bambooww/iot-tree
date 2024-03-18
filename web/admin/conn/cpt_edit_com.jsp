<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
String cptp = ConnPtCOM.TP;//request.getParameter("cptp") ;
ConnProCOM cp = (ConnProCOM)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtCOM cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtCOM() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtCOM)cp.getConnById(connid) ;
	if(cpt==null)
	{
		out.print("no connection found") ;
		return ;
	}
}

String name = cpt.getName() ;
String title= cpt.getTitle() ;
String chked = "" ;
if(cpt.isEnable())
	chked = "checked='checked'" ;
String desc = cpt.getDesc();
String comid = cpt.getComId() ;
int baud  = cpt.getBaud() ;
int databits = cpt.getDataBits() ;
int parity = cpt.getParity() ;
int stopbits = cpt.getStopBits() ;
int flowctl = cpt.getFlowCtl() ;
String cp_tp = cp.getProviderType() ;

%>
<html>
<head>
<title>conn editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(800,430);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>name</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>title</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>enable</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">ID:</label>
    <div class="layui-input-inline">
      	    <select id="comid" lay-filter="comid">
      	    <option value="">---</option>
<%
for(String cid:ConnProCOM.listSysComs())
{
%><option value="<%=cid%>"><%=cid %></option>
<%
}
%>   </select>
    </div>
    <div class="layui-form-mid"><wbt:g>baud</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="baud"  lay-filter="baud">
<%
for(int b:ConnPtCOM.BAUDS)
{
%><option value="<%=b%>"><%=b %></option>
<%
}
%>   </select>
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>data_bits</wbt:g>:</label>
    <div class="layui-input-inline">
      	    <select id="databits" lay-filter="databits">
<%
for(int dbit:ConnPtCOM.DATABITS)
{
%><option value="<%=dbit%>"><%=dbit %></option>
<%
}
%>   </select>
    </div>
    <div class="layui-form-mid"><wbt:g>parity</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="parity" lay-filter="parity">
<%

for(int i = 0 ; i < ConnPtCOM.PARITY.length ; i ++)
{
	int pri = ConnPtCOM.PARITY[i] ;
	String tt =  ConnPtCOM.PARITY_NAME[i] ;
%><option value="<%=pri%>"><wbt:g>parity_<%=tt %></wbt:g></option>
<%
}
%>   </select>
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>stop_bits</wbt:g>:</label>
    <div class="layui-input-inline">
      	    <select id="stopbits" lay-filter="stopbits">
      	    	
<%
for(int dbit:ConnPtCOM.STOPBITS)
{
%><option value="<%=dbit%>"><%=dbit %></option>
<%
}
%>   </select>
    </div>
    <div class="layui-form-mid"><wbt:g>flow_ctrl</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="flowctl" lay-filter="flowctl">
<%
for(int i = 0 ; i < ConnPtCOM.FLOWCTL.length;i++)
{
	int fctl = ConnPtCOM.FLOWCTL[i] ;
	String tt = ConnPtCOM.FLOWCTL_TITLE[i] ;
%><option value="<%=fctl%>"><%=tt %></option>
<%
}
%>   </select>
	  </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>desc</wbt:g>:</label>
    <div class="layui-input-block" style="width:650px">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var comid = '<%=comid%>';
var baud = <%=baud%>;
var databits = <%=databits%>;
var parity = <%=parity%>;
var stopbits = <%=stopbits%>;
var flowctl = <%=flowctl%>;
$("#comid").val(comid) ;
$("#baud").val(baud) ;
$("#databits").val(databits) ;
$("#parity").val(parity) ;
$("#stopbits").val(stopbits) ;
$("#flowctl").val(flowctl) ;

var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  
	  
	  form.on("select(comid)",function(obj){
		  setDirty(true);
		  });
	  form.on("select(baud)",function(obj){
		  setDirty(true);
		  });
	  form.on("select(databits)",function(obj){
		  setDirty(true);
		  });
	  form.on("select(parity)",function(obj){
		  setDirty(true);
		  });
	  form.on("select(stopbits)",function(obj){
		  setDirty(true);
		  });
	  form.on("select(flowctl)",function(obj){
		  setDirty(true);
		  });
	  $("#desc").on("input",function(e){
		  setDirty(true);
		  });
	  form.on('switch(enable)', function(obj){
		  setDirty(true);
		  });
		  
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cp_tp%>" ;
var conn_id = "<%=connid%>" ;

function isDirty()
{
	return bdirty;
}
function setDirty(b)
{
	if(!(b===false))
		b = true ;
	bdirty= b;
	dlg.btn_set_enable(1,b);
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

function get_val(id,title,cb,bnum)
{
	var v = $('#'+id).val();
	if(v==null||v=='')
	{
		cb(false,'<wbt:g>pls,input</wbt:g>'+title) ;
		throw "no "+title+" input" ;
	}
	if(bnum)
	{
		v = parseInt(v);
		if(v==NaN)
		{
			cb(false,'<wbt:g>pls,input,valid</wbt:g>'+title) ;
			throw "invalid "+title+" input" ;
		}
	}
	
	return v ;
}

function do_submit(cb)
{
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'<wbt:g>pls,input,title</wbt:g>') ;
		return ;
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var comid = get_val("comid","<wbt:g>com_port,id</wbt:g>",cb,false);
	var baud = get_val("baud","<wbt:g>baud</wbt:g>",cb,true);
	var databits = get_val("databits","<wbt:g>data_bits</wbt:g>",cb,true);
	var parity = get_val("parity","<wbt:g>parity</wbt:g>",cb,true);
	var stopbits = get_val("stopbits","<wbt:g>stop_bits</wbt:g>",cb,true);
	var flowctl = get_val("flowctl","<wbt:g>flow_ctrl</wbt:g>",cb,true);
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,
		comid:comid,baud:baud,databits:databits,
		parity:parity,stopbits:stopbits,flowctl:flowctl
		});
}

</script>
</html>