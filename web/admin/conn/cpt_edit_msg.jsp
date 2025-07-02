<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid"))
	return;
String repid = request.getParameter("prjid") ;
String cpid =  request.getParameter("cpid") ;
//String cptp = ConnProHTTP.TP;//request.getParameter("cptp") ;
ConnProvider cp = ConnManager.getInstance().getConnProviderById(repid, cpid);
if(cp==null)
{
	out.print("no single provider found with id"+cpid);
	return ;
}

String connid = request.getParameter("connid") ;
if(connid==null)
	connid = "" ;
ConnPtMSGNor cpt = null ;

ConnPt.DataTp sor_tp = ConnPt.DataTp.json;
String init_js = "" ;
String trans_js = "";
String handle = "bind" ;

String encod = "UTF-8" ;
String bind_probe_str = null;
String bind_map_str =null;

boolean run_js_page = false;
long run_js_to = 30000 ;
UACh joined_ch = null;
if(Convert.isNotNullEmpty(connid))
{
	cpt = (ConnPtMSGNor)cp.getConnById(connid) ;
	if(cpt==null)
	{
		out.print("no ConnPtMSG found") ;
		return ;
	}
	
	sor_tp = cpt.getSorTp();
	ConnPtMSGNor.TransHandler th = cpt.getTransHandler();
	ConnPtMSGNor.BindHandler bh = cpt.getBindHandler();
	
	init_js = th.getInitJS() ;
	trans_js = th.getTransJS();
	encod = cpt.getEncod() ;
	if(cpt.getHandleSty()!=null)
		handle = cpt.getHandleSty().toString();
	
	bind_probe_str = bh.getBindProbeStr() ;
	
	bind_map_str = bh.getBindMapStr() ;
	
	if(cpt instanceof ConnPtHTTP)
	{
		ConnPtHTTP cpth = (ConnPtHTTP)cpt ;
		run_js_page = cpth.isRunJsPage() ;
		run_js_to = cpth.getRunJsTO();
	}
	
	joined_ch = cpt.getJoinedCh() ;
}

String path = "/"  ;
if(joined_ch!=null)
	path = joined_ch.getNodePath();

if(Convert.isNullOrEmpty(encod))
	encod = "UTF-8";


if(Convert.isNullOrEmpty(bind_probe_str))
	bind_probe_str = "[]" ;

if(Convert.isNullOrEmpty(bind_map_str))
	bind_map_str = "[]" ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>
.layui-form-item
{
	margin-bottom:5px;
}

.layui-input, .layui-select, .layui-textarea {
    height: 28px;
}
.layui-form-label {

    padding: 9px 15px;
    line-height: 15px;
}
.layui-form-mid {
    padding: 9px 0!important;
    line-height: 10px;
}
.layui-form-switch {

    margin-top: 2px;
}
</style>
<body>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label" >Format</label>
    <div class="layui-input-inline" style="width:90px">
    	<select id="sor_tp" lay-filter="sor_tp" >
<%
	for(ConnPt.DataTp stp:ConnPt.DataTp.values())
{
%><option value="<%=stp.toString()%>"><%=stp.getTitle()%></option>
<%
	}
%>
    	</select>
    </div>
    <label class="layui-form-label">Encoding</label>
    <div class="layui-input-inline" style="width:90px">
    <select id="encod" lay-filter="encod" >
<%
	for(String chartset:java.nio.charset.Charset.availableCharsets().keySet())
{
%><option value="<%=chartset%>"><%=chartset%></option><%
	}
%>
    </select>
    </div>
    <label class="layui-form-label">Handler</label>
    <div class="layui-input-inline" style="width:150px;white-space: nowrap;">
    	<select id="handle" lay-filter="handle" >
<%
	for(ConnPtMSGNor.HandleSty st:ConnPtMSGNor.HandleSty.values())
{
%><option value="<%=st.toString() %>" ><%=st.getTitle() %></option>
<%
}
%>
</select>
    </div>
  </div>
<%

String run_js_page_chk = "" ;
if(run_js_page)
	run_js_page_chk = "checked='checked'";

String last_rtf_str = "<span style='color:red'>no read buffer</span>" ;
String tmpbuf_fp = "" ;
if(cpt!=null)
{
	Date tmpdt = cpt.getMsgTmpBufLastDT();
	File tmpbuf_f = cpt.getTmpBufFile() ;
	if(tmpbuf_f.exists())
		tmpbuf_fp = "connpt_msg/"+tmpbuf_f.getName() ;
	if(tmpdt!=null)
	{
		last_rtf_str = "<span style='color:blue'>"+Convert.toFullYMDHMS(tmpdt)+"</span>" ;
	}
}
%>
  <div id="run_page_cont" class="layui-form-item" style="display:none">

    <label class="layui-form-label" style="width:150px">Run Page JS</label>
    	<div class="layui-input-inline" style="width: 50px;">
	    <input type="checkbox" id=run_js_page name="run_js_page" <%=run_js_page_chk%> lay-skin="switch"  lay-filter="enable" class="layui-input">
    </div>
    <label class="layui-form-label" style="width:150px">Run JS Timeout</label>
    <div class="layui-input-inline" style="width:90px">
    	<input type="number" id="run_js_to" name="run_js_to" value="<%=run_js_to%>"  class="layui-input">
    </div>

  </div>
<%
if(cpt!=null)
{
	if(cpt.isPassiveRecv())
	{
		ConnPt.MonData md = cpt.getLastMsgFromMon() ;
		String dtstr = "<span style='color:red'>no received data</span>";
		if(md!=null)
		{
			long dt = md.getDT() ;
			dtstr = Convert.toFullYMDHMS(new Date(dt)) ;
		}
%>
	    <div id="run_page_cont" class="layui-form-item" >
	    <label class="layui-form-label" style="width:150px">Recved data</label>
	    	<div class="layui-input-inline" style="width: 150px;" id="read_to_buf_inf">
		     &nbsp;<%=dtstr %>
	    	</div>
	  </div>
<%
	}
	else
	{
%>
    <div id="run_page_cont" class="layui-form-item" >
    <label class="layui-form-label" style="width:130px">Read to Buffer</label>
    	<div class="layui-input-inline" style="width: 150px;" id="read_to_buf_inf">
	     &nbsp;<%=last_rtf_str %>
    	</div>
   
    <div class="layui-input-inline" style="width:90px">
    	<input type="button" id="btn_readtobuf" name="btn_readtobuf" value="Read"  class="layui-btn layui-btn-warm layui-border-blue layui-btn-sm" onclick="read_to_buf()">
    </div>

  </div>
<%
	}
}
%>
  <div id="edit_trans_js">
  <div class="layui-form-item">
    <label class="layui-form-label">Initial JS:</label>
    <div class="layui-input-inline" style="width:500px">
      <textarea  id="init_js"  name="init_js"  style="height:30px;width:100%;border-color: #e6e6e6"><%=init_js%></textarea>
    </div>
    <button onclick="edit_js_init()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Transfer JS:</label>
    <div class="layui-input-inline" style="width:500px">
    ($topic,$msg)=>{
      <textarea  id="trans_js"  name="trans_js"  class="layui-textarea" style="height:50px"><%=trans_js%></textarea>
      }
    </div>
    <button onclick="edit_js_trans()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
  </div>
   </div>
   
   <div id="edit_bind" style="display:none">
  <div class="layui-form-item">
    <label class="layui-form-label" style="width:130px">Bind Style:</label>
    <div class="layui-input-inline" style="width:100px">
      	<button onclick="probe_setup()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">Data Probe</button>
    </div>
    <label class="layui-form-label" style="width:150px">Bind Channel:</label>
    <div class="layui-input-inline" style="width:100px">
   	<button onclick="bind_to_ch()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">Bind Channel</button>
    </div>
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
var prjid=  "<%=repid%>";
var cpid=  "<%=cpid%>";
var connid = "<%=connid%>";
var sor_tp = "<%=sor_tp%>";
var handle = "<%=handle%>" ;
var encod = "<%=encod%>";
var run_js_page = <%=run_js_page%>;
var run_js_to =  <%=run_js_to%>;

var tmpbuf_fp = "<%=tmpbuf_fp%>" ;

var path = "<%=path%>" ;

layui.use('form', function(){
	  form = layui.form;
	  
	
	  $("#init_js").on("input",function(e){
		  setDirty();
		  });
	  
	  $("#trans_js").on("input",function(e){
		  setDirty();
		  });
	  form.on('select(sor_tp)', function(obj){
		       setDirty();
		  update_ui();
		  });
	  form.on('select(encod)', function(obj){
		       setDirty();
		  });
	  form.on('select(handle)', function(obj){
		       setDirty();
		  	update_ui()
		  });

	  $("#sor_tp").val(sor_tp) ;
	  $("#handle").val(handle) ;
	  $("#encod").val(encod) ;
	  update_ui();
	  form.render(); 
});

function get_probe_url()
{
	return parent.get_url();
}

function get_run_js()
{
	var rjp = $("#run_js_page").prop("checked") ;
	var rjto = $("#run_js_to").val() ;
	return {run_js_page:rjp,run_js_to:rjto};
}

function get_tmpbuf_fp()
{
	return tmpbuf_fp;
}

function is_sor_can_bind()
{
	var stp = $("#sor_tp").val();
	return stp=='json'||stp=='xml'||stp=='html' ;
}

function update_ui()
{
	if("bind"==$("#handle").val())
	{
		if(!is_sor_can_bind())
		{
			dlg.msg("Source Type ["+ $("#sor_tp").val()+"] cannot support handler [bind]") ;
			$("#handle").val('js_trans');
			form.render(); 
			update_ui();
			return ;
		}
		$("#edit_bind").css("display","");
		$("#edit_trans_js").css("display","none");
	}
	else
	{
		$("#edit_trans_js").css("display","");
		$("#edit_bind").css("display","none");
	}
	
	var stp = $("#sor_tp").val();
	if(stp=='html')
	{
		$("#run_page_cont").css("display","");
	}
	else
	{
		$("#run_page_cont").css("display","none");
	}
	
}

var _tmpid = 0 ;

var bdirty=false;
var cp_tp = parent.cp_tp ;
var cp_id = "<%=cpid%>" ;
var conn_id = "<%=connid%>" ;

function isDirty()
{
	return bdirty;
}
function setDirty()
{
	parent.setDirty();
}

	
function win_close()
{
	dlg.close(0);
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
	dlg.open("../ua_cxt/cxt_script.jsp?path="+path+"&opener_txt_id="+taid+"&sample_txt_id="+sample_id+"&func_params="+funcp,
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

var sor_txt = null ;
var probe_ob = <%=bind_probe_str%> ;

var map_list = <%=bind_map_str%> ;

function get_bind_list()
{
	//console.log(probe_ob,sor_tp);
	if(sor_tp=='html')
	{
		var ret = [] ;
		for(var blk of probe_ob)
		{
			var n = blk.n ;
			if(blk.extract_pts)
			{
				for(var ept of blk.extract_pts)
				{
					var p = "/"+n+"/"+ept.n ;
					var vt = "str" ;
					ret.push({path:p,vt:vt,tt:ept.t}) ;
				}
				
			}
			
		}
		return ret;
	}
	
	return probe_ob ;
}

function get_map_list()
{
	return map_list ;
}

function get_probe_sor_txt()
{
	return sor_txt ;
}

function probe_setup()
{
	event.preventDefault();
	//var u = get_probe_url();
	//if(!u)
	//{
	//	dlg.msg("please input Url first") ;
	//	return ;
	//}
	var sor_tp = $('#sor_tp').val();
	if(sor_tp==null||sor_tp=='')
	{
		dlg.msg('Please select source type json or xml') ;
		return ;
	}
	
	send_ajax("cpt_mon_ajax.jsp",{op:"last_sor_txt",prjid:prjid,cpid:cp_id,cid:conn_id},(bsucc,ret)=>{
		if(!bsucc||(ret.indexOf("{")!=0&&ret.indexOf('[')!=0&&ret.indexOf('<')!=0))
		{
			dlg.msg(ret);
			//return ;
		}
		else
			sor_txt = ret;
		
		var tmpu = "./cpt_probe.jsp?sor_tp="+sor_tp;
		if(sor_tp=='html')
			tmpu = "./cpt_probe_html.jsp?sor_tp="+sor_tp;
		dlg.open(tmpu,{title:"Probe Setting ",w:'900',h:'600'},
				['Ok','Cancel',"Help"],
				[
					function(dlgw)
					{
						dlgw.do_ok((bsucc,res,enc)=>{
							if(!bsucc)
							{
								dlg.msg(res) ;
								return ;
							}
							//console.log(res) ;
							probe_ob = res ;
							if(enc)
							{
								//if(parent.on_find_url_encod)
								//	parent.on_find_url_encod(enc);
								var oldenc = $("#encod").val() ;
								if(enc!=oldenc)
								{
									dlg.msg("find url encoding :"+enc) ;
									$("#encod").val(enc) ;
									form.render();
								}
							}
							setDirty();
							dlg.close();
						});
					},
					function(dlgw)
					{
						dlg.close();
					},
					function(dlgw)
					{
						alert("help");
					}
				]);
	});
	
}

function bind_to_ch()
{
	event.preventDefault();
	dlg.open("./cpt_bind_sel.jsp?no_ajax=true&prjid="+prjid+"&cpid="+cp_id+"&connid="+conn_id,
			{title:"Binding Setting ["+cp_tp+"] ",w:'800',h:'550'},
			['Ok','Cancel'], //{title:'Cancel',style:"primary"}
			[
				function(dlgw)
				{
					//r bindstr = dlgw.get_bindlist_valstr();
					map_list = dlgw.get_map_list();
					setDirty();
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
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

function read_to_buf()
{
	$("#read_to_buf_inf").html("<i class='fa-solid fa-spinner fa-spin-pulse'></i>") ;
	send_ajax("cpt_msg_ajax.jsp",{op:"read_tmp_to_buf",prjid:prjid,cpid:cpid,cid:connid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		var ob = null;
		eval("ob="+ret) ;
		tmpbuf_fp = ob.bfp;
		$("#read_to_buf_inf").html("<span style='color:blue'>"+ob.dt+"</span>") ;
	});
}

function do_submit(cb)
{
	var sor_tp = $('#sor_tp').val();
	if(sor_tp==null||sor_tp=='')
	{
		cb(false,'Please select sor_tp') ;
		return ;
	}
	var h = $("#handle").val() ;
	if(h==null||h=='')
	{
		cb(false,'Please select handler') ;
		return ;
	}
	var init_js = $('#init_js').val();
	var trans_js = $('#trans_js').val();
	var enc =  $("#encod").val() ;
	var run_js_page = $("#run_js_page").prop("checked") ;
	var run_js_to = $("#run_js_to").val() ;
	if(run_js_to)
		run_js_to = parseInt(run_js_to) ;

	cb(true,{sor_tp:sor_tp,handle:h,init_js:init_js,trans_js:trans_js,encod:enc,
		run_js_page:run_js_page,run_js_to:run_js_to,
		bind_probe:JSON.stringify(probe_ob),
		bind_map:JSON.stringify(map_list)});
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