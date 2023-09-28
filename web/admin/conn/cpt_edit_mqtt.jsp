<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
	org.iottree.core.conn.mqtt.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
ConnProMQTT cp = null ;
if(Convert.isNullOrEmpty(cpid))
{
	cp = new ConnProMQTT() ;
	cpid = cp.getId() ;
	ConnManager.getInstance().setConnProvider(repid, cp);
}
else
{
	cp = (ConnProMQTT)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
	if(cp==null)
	{
		out.print("no ConnProMQTT found") ;
		return ;
	}
	cpid = cp.getId();
}

String cptp = ConnProMQTT.TP;//request.getParameter("cptp") ;


String connid = request.getParameter("connid") ;
String cid = connid ;
if(cid==null)
	cid = "";
ConnPtMQTT cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtMQTT() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtMQTT)cp.getConnById(connid) ;
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
//String opc_appn = cpt.getOpcAppName();
//String opc_epuri  = cpt.getOpcEndPointURI();
ConnPt.DataTp sor_tp = cpt.getSorTp();
//String init_js = cpt.getInitJS() ;
//String trans_js = cpt.getTransJS();

List<String> topics = cpt.getMsgTopics();
String topics_str = Convert.transListToMultiLineStr(topics) ;
if(topics_str==null)
	topics_str = "" ;
String encod = cpt.getEncod() ;
if(Convert.isNullOrEmpty(encod))
	encod = "UTF-8";
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,600);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  class="layui-input">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  class="layui-input">
	  </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch" class="layui-input">
	  </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">Subscribe Topics</label>
    <div class="layui-input-inline">
      <textarea  id="topics"  name="topics"  class0="layui-textarea" style="height:70px;width:350px;border-color: #e6e6e6"><%=topics_str%></textarea>
    </div>
   </div>
   <%--
  <div class="layui-form-item">
    <label class="layui-form-label">Subscribe Topics</label>
    <div class="layui-input-inline">
      <textarea  id="topics"  name="topics"  class0="layui-textarea" style="height:50px;width:150px;border-color: #e6e6e6"><%=topics_str%></textarea>
    </div>
    <label class="layui-form-label">Message Source Type</label>
    <div class="layui-input-inline" style="width:70px">
    	<select id="sor_tp" lay-filter="sor_tp" >
<%
	for(ConnPt.DataTp stp:ConnPt.DataTp.values())
{
%><option value="<%=stp.toString()%>"><%=stp.getTitle() %></option>
<%
}
%>
    	</select>
    </div>
    <label class="layui-form-label">Encoding</label>
    <div class="layui-input-inline" style="width:100px">
    <select id="encod" lay-filter="encod" >
<%
for(String chartset:java.nio.charset.Charset.availableCharsets().keySet())
{
%><option value="<%=chartset%>"><%=chartset %></option><%
}
%>
		
		
    </select>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Initial JS:</label>
    <div class="layui-input-inline" style="width:600px">
      <textarea  id="init_js"  name="init_js"  style="height:60px;width:100%;border-color: #e6e6e6"><%=init_js%></textarea>
    </div>
    <button onclick="edit_js_init()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Transfer JS:</label>
    <div class="layui-input-inline" style="width:600px">
    ($topic,$msg)=>{
      <textarea  id="trans_js"  name="trans_js"  class="layui-textarea" style="height:150px"><%=trans_js%></textarea>
      }
    </div>
    <button onclick="edit_js_trans()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
  </div>
  --%>
    
   <iframe id="if_msg" src="cpt_edit_msg.jsp?prjid=<%=repid%>&cpid=<%=cpid%>&connid=<%=cid%>" style="width:100%;height:270px;border:0px;overflow: hidden;"></iframe>
   <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-inline" style="width:600px">
      <textarea  id="desc"  name="desc"  style="height:30px;width:100%;border-color: #e6e6e6"><%=desc%></textarea>
    </div>
  </div>
 </form>
 
</body>
<script type="text/javascript">
var form = null;
var sor_tp = "<%=sor_tp%>";
var encod = "<%=encod%>";
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name").on("input",function(e){
		  setDirty(true);
		  });
	  $("#title").on("input",function(e){
		  setDirty(true);
		  });
	  $("#desc").on("input",function(e){
		  setDirty(true);
		  });
	  $("#topics").on("input",function(e){
		  setDirty(true);
		  });
	  form.on('switch(enable)', function(obj){
		       setDirty(true);
		  });
	  /*
	  $("#init_js").on("input",function(e){
		  setDirty(true);
		  });
	  
	  $("#trans_js").on("input",function(e){
		  setDirty(true);
		  });
	  form.on('select(sor_tp)', function(obj){
		       setDirty(true);
		  });
	  form.on('select(encod)', function(obj){
		       setDirty(true);
		  });
	  
	 
	  $("#sor_tp").val(sor_tp) ;
	  $("#encod").val(encod) ;
	  */
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cptp%>" ;
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

function edit_js_trans()
{
	edit_js('trans_js','Transfer JS','$topic,$msg','trans_sample') ;
}

function edit_js_init()
{
	edit_js('init_js','Initial JS','','') ;
}

function edit_js(taid,tt,funcp,sample_id)
{
	event.preventDefault();
	dlg.open("../ua_cxt/cxt_script.jsp?opener_txt_id="+taid+"&sample_txt_id="+sample_id+"&func_params="+funcp,
			{title:tt},['Ok','Cancel'],
			[
				function(dlgw)
				{
					var jstxt = dlgw.get_edited_js() ;
					 if(jstxt==null)
						 jstxt='' ;
					 $("#"+taid).val(jstxt) ;
					 setDirty();
					 dlg.close() ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function do_submit(cb)
{
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'Please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'Please input title') ;
		return ;
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var topics = $('#topics').val();
	if(topics==null||topics=='')
	{
		cb(false,'Please input topics') ;
		return ;
	}
	var topicsstr = $("#topics").val() ;
	var tps = str2lns(topicsstr)
	
	/*
	var sor_tp = $('#sor_tp').val();
	if(sor_tp==null||sor_tp=='')
	{
		cb(false,'Please input sor_tp') ;
		return ;
	}
	var init_js = $('#init_js').val();
	var trans_js = $('#trans_js').val();
	var enc =  $("#encod").val() ;
	*/
	
	var msgob = null ;
	$("#if_msg")[0].contentWindow.do_submit((bok,ret)=>{
		if(!bok)
		{
			cb(false,ret) ;
			return;
		}
		msgob = ret ;
	})

	var oball = Object.assign({id:conn_id,name:n,title:tt,desc:desc,enable:ben,topics:tps},msgob);
	cb(true,oball) ;
}

function str2lns(str)
{
	var arr = str.split('\n');
	var res = [];
	arr.forEach(function (item)
	{
		var ln = item.replace(/(^\s*)|(\s*$)/g, "").replace(/\s+/g, " ")
		if(ln=='')
			return ;
	    res.push(ln);
	})

	return res ;
}

</script>
</html>