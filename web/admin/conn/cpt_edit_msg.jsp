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
ConnPtMSG cpt = null ;

ConnPt.DataTp sor_tp = ConnPt.DataTp.json;
String init_js = "" ;
String trans_js = "";
String handle = "" ;

String encod = "UTF-8" ;
String bind_probe_str = null;
String bind_map_str =null;
if(Convert.isNotNullEmpty(connid))
{
	cpt = (ConnPtMSG)cp.getConnById(connid) ;
	if(cpt==null)
	{
		out.print("no ConnPtMSG found") ;
		return ;
	}
	
	sor_tp = cpt.getSorTp();
	ConnPtMSG.TransHandler th = cpt.getTransHandler();
	ConnPtMSG.BindHandler bh = cpt.getBindHandler();
	
	
	init_js = th.getInitJS() ;
	trans_js = th.getTransJS();
	encod = cpt.getEncod() ;
	if(cpt.getHandleSty()!=null)
		handle = cpt.getHandleSty().toString();
	
	bind_probe_str = bh.getBindProbeStr() ;
	
	bind_map_str = bh.getBindMapStr() ;
}


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
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Source Type</label>
    <div class="layui-input-inline" style="width:90px">
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
    <div class="layui-input-inline" style="width:90px">
    <select id="encod" lay-filter="encod" >
<%
for(String chartset:java.nio.charset.Charset.availableCharsets().keySet())
{
%><option value="<%=chartset%>"><%=chartset %></option><%
}
%>
    </select>
    </div>
    <label class="layui-form-label">Handler</label>
    <div class="layui-input-inline" style="width:150px;white-space: nowrap;">
    	<select id="handle" lay-filter="handle" >
<%
for(ConnPtMSG.HandleSty st:ConnPtMSG.HandleSty.values())
{
%><option value="<%=st.toString() %>"><%=st.getTitle() %></option>
<%
}
%>
</select>
    </div>
  </div>
  
  <div id="edit_trans_js">
  <div class="layui-form-item">
    <label class="layui-form-label">Initial JS:</label>
    <div class="layui-input-inline" style="width:600px">
      <textarea  id="init_js"  name="init_js"  style="height:30px;width:100%;border-color: #e6e6e6"><%=init_js%></textarea>
    </div>
    <button onclick="edit_js_init()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Transfer JS:</label>
    <div class="layui-input-inline" style="width:600px">
    ($topic,$msg)=>{
      <textarea  id="trans_js"  name="trans_js"  class="layui-textarea" style="height:50px"><%=trans_js%></textarea>
      }
    </div>
    <button onclick="edit_js_trans()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
  </div>
   </div>
   
   <div id="edit_bind" style="display:none">
  <div class="layui-form-item">
    <label class="layui-form-label">Bind Style:</label>
    <div class="layui-input-inline" style="width:100px">
      	<button onclick="probe_setup()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">Data Probe</button>
    </div>
    <label class="layui-form-label">Bind Channel:</label>
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

var sor_txt = null ;
var probe_ob = <%=bind_probe_str%> ;

var map_list = <%=bind_map_str%> ;

function get_bind_list()
{
	console.log(probe_ob,sor_tp);
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
	var sor_tp = $('#sor_tp').val();
	if(sor_tp==null||sor_tp=='')
	{
		cb(false,'Please select source type json or xml') ;
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
				['Ok','Cancel'],
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
							dlg.close();
						});
					},
					function(dlgw)
					{
						dlg.close();
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

	cb(true,{sor_tp:sor_tp,handle:h,init_js:init_js,trans_js:trans_js,encod:enc,
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