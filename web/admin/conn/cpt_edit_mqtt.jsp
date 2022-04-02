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
String cptp = ConnProMQTT.TP;//request.getParameter("cptp") ;
ConnProMQTT cp = (ConnProMQTT)ConnManager.getInstance().getConnProviderById(repid, cpid);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String connid = request.getParameter("connid") ;

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
ConnPtMQTT.SorTp sor_tp = cpt.getSorTp();
String trans_js = cpt.getTransJS();
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
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(600,400);
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
      <textarea  id="topics"  name="topics"  class="layui-textarea" rows="2" cols="30"><%=topics_str%></textarea>
    </div>
    <label class="layui-form-label">Message Source Type</label>
    <div class="layui-input-inline" style="width:70px">
    	<select id="sor_tp" lay-filter="sor_tp" >
<%
for(ConnPtMQTT.SorTp stp:ConnPtMQTT.SorTp.values())
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
    <label class="layui-form-label">Transfer JS:</label>
    <div class="layui-input-inline">
      <textarea  id="trans_js"  name="trans_js"  required class="layui-textarea" rows="2"><%=trans_js%></textarea>
      <button onclick="edit_js_trans()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
    </div>
    <label class="layui-form-label">Device JS:</label>
    <div class="layui-input-inline">
      <textarea  id="devs_js"  name="devs_js"  required class="layui-textarea" rows="2"><%=""%></textarea>
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
   
 </form>
 <textarea style="display:none" id="trans_sample">
 
 // the function must return such json format
 //  vt
 /*
    return [
    	{"dev_name":"dev1","dev_title":"Device1","data":[
	    	{"n":"g1.v1","vt":"float","v":18.5},
	    	{"n":"st","vt":"bool","v":true}
	    	]
	    },
	    {"dev_name":"dev2","dev_title":"Device2","data":[
	    	{"n":"g1.v1","vt":"float","v":13.5},
	    	{"n":"st","vt":"bool","v":false}
	    	]
	    }
    ];
 */
 var retob = [] ;
 
 var dev1 = {} ;
 dev1.dev_name="dev1";  //name must a-z A-z 1-9
 dev1.dev_title="Device1"; //device title
 dev1.data=[];
 dev1.data.push({n:"g1.v1",vt:"float",v:18.5});
 dev1.data.push({n:"st",vt:"bool",v:true});
 
 retob.push(dev1);
 //you can add another device and data
 // retob.push(dev2) ;
 return retob ;
 </textarea>
</body>
<script type="text/javascript">
var form = null;
var sor_tp = "<%=sor_tp%>";
var encod = "<%=encod%>";
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name").on("input",function(e){
		  setDirty();
		  });
	  $("#title").on("input",function(e){
		  setDirty();
		  });
	  $("#desc").on("input",function(e){
		  setDirty();
		  });
	  $("#topics").on("input",function(e){
		  setDirty();
		  });
	  $("#trans_js").on("input",function(e){
		  setDirty();
		  });
	  form.on('select(sor_tp)', function(obj){
		       setDirty();
		  });
	  form.on('select(encod)', function(obj){
		       setDirty();
		  });
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
	  $("#sor_tp").val(sor_tp) ;
	  $("#encod").val(encod) ;
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
function setDirty()
{
	bdirty= true;
	dlg.btn_set_enable(1,true);
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
	var sor_tp = $('#sor_tp').val();
	if(sor_tp==null||sor_tp=='')
	{
		cb(false,'Please input sor_tp') ;
		return ;
	}
	var trans_js = $('#trans_js').val();
	var topicsstr = $("#topics").val() ;
	var enc =  $("#encod").val() ;
	var tps = str2lns(topicsstr)
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,topics:tps,sor_tp:sor_tp,trans_js:trans_js,encod:enc});
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