<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.json.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
	public static String html_str(Object o)
	{
		if(o==null)
			return "" ;
		return Convert.plainToJsStr(""+o) ;
	}
	 %><%
	 if(!Convert.checkReqEmpty(request, out, "path"))
			return ;
	boolean bmid = "true".equalsIgnoreCase(request.getParameter("mid")) ;
	//boolean blocal= "true".equalsIgnoreCase(request.getParameter("local")) ;
	String path = request.getParameter("path") ;
	String id = request.getParameter("id") ;
	UATag tag = null ;
	String name= "" ;
	String title = "" ;
	String addr = "" ;
	boolean blocal=false;
	String local_defval = "" ;
	boolean local_autosave = false;
	UAVal.ValTP valtp = null ;
	String valtp_str = "" ;
	long srate = 200;
	int dec_digits = -1 ;
	String canw = "";
	String desc = "" ;
	String trans = null ;
	boolean b_val_filter=false;
	String min_val_str="" ;
	String max_val_str="" ;
	//String alert_low="" ;
	//String alert_high="" ;
	String alerts = null ;
	
	if(id==null)
		id = "" ;
	
	UANode tmpn = UAUtil.findNodeByPath(path);
	if(tmpn instanceof UAHmi)
	{
		tmpn = tmpn.getParentNode();
		path = tmpn.getNodePath() ;
	}
	UANodeOCTags n = (UANodeOCTags)tmpn;
	if(n==null)
	{
		out.print("no node with path="+path) ;
		return ;
	}
	
	UADev dev = n.getBelongToDev() ;
	UACh ch = n.getBelongToCh() ;
	UAVal.ValTP[] vtps = null;
	if(ch!=null)
	{
		DevDriver dd = ch.getDriver() ;
		if(dd!=null)
		{
			vtps = dd.getLimitValTPs(dev);
		}
	}
	if(vtps==null)
		vtps = UAVal.ValTP.values() ;
	
	if( Convert.isNotNullEmpty(id))
	{
 		tag = n.getTagById(id) ;
 		if(tag==null)
 		{
 			out.print("no edit tag found") ;
 			return ;
 		}
 		name = tag.getName() ;
 		title = tag.getTitle() ;
 		desc = tag.getDesc() ;
 		bmid = tag.isMidExpress();
 		addr = tag.getAddress() ;
 		blocal = tag.isLocalTag() ;
 		local_defval = tag.getLocalDefaultVal() ;
 		local_autosave = tag.isLocalAutoSave() ;
 		valtp = tag.getValTpRaw() ;
 		if(valtp!=null)
 			valtp_str = ""+valtp.getInt() ;
 		dec_digits = tag.getDecDigits() ;
 		srate = tag.getScanRate() ;
 		canw = ""+tag.isCanWrite();
 		trans = tag.getValTranser() ;
 		if(Convert.isNullOrEmpty(trans))
 			trans = null ;
 		b_val_filter = tag.isValFilter() ;
 		min_val_str = tag.getMinValStr() ;
 		max_val_str = tag.getMaxValStr() ;
 		//alert_low = tag.getAlertLowValStr() ;
 		//alert_high = tag.getAlertHighValStr() ;
 		JSONArray jarr = tag.getValAlertsJArr() ;
 		if(jarr!=null)
 		{
 			alerts = jarr.toString();
 		}
	}
%><html>
<head>
<title>Tag Editor </title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>

<style type="text/css">
.btn-group button {
    left:10px;
    padding: 10px 24px;
    cursor: pointer;
    width: 90%; 
    display: block; 
}
.alert_item
{
	position: relative;
	border:1px solid;
	height:30px;
	min-width:70px;
	margin-left:5px;
	width:fit-content;
	float: left;
}

.alert_item .oper
{
left:5px;
	color: red;
}

.alert_item .tt
{
top:5px;
	cursor:pointer;
}
</style>
<script>
dlg.resize_to(850,600);
</script>

</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
 <table style="width:100%	">
   <tr>
     <td width="90%">
	<input type="hidden" id="id" name="name" value="<%=html_str(id)%>">
	  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>name</wbt:g>:</label>
    <div class="layui-input-inline" style="width: 200px;">
      <input type="text" id="name" name="name" lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>title</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 300px;">
	    <input type="text" id="title" name="title" lay-verify="required" size="0" autocomplete="off" class="layui-input">
	  </div>
	  
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>data,type</wbt:g></label>
    <div class="layui-input-inline" style="width: 100px;">
      <select  id="vt"  name="vt"  class="layui-input" lay-filter="vt" >
        <option value="">---</option>
<%
for(UAVal.ValTP vt:vtps)
{
	boolean bnum = vt.isNumberVT() ;
	 %><option value="<%=vt.getInt()%>" b_num="<%=bnum%>"><%=vt.getStr() %></option><%
}
%>
      </select>
    </div>
    <div class="layui-form-mid"><wbt:g>dec_digit</wbt:g>:</div>
    <div class="layui-input-inline" style="width: 50px;">
      <input type="text" id="dec_digits" name="dec_digits" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>r_or_w</wbt:g>:</div>
    <div class="layui-input-inline" style="width: 150px;">
      <select id="canw"  name="canw" class="layui-input">
        <option value="">---</option>
        <option value="false"><wbt:g>r</wbt:g></option>
        <option value="true"><wbt:g>rw</wbt:g></option>
      </select>
    </div>
  </div>
<%

String loc_set="display:none" ;
	String loc_autosave_chk="" ;
	String loc_chked = "" ;
	String b_val_filter_chked="" ;
	if(blocal)
	{
		loc_chked= "checked=checked" ;
		loc_set="" ;
	}
	if(local_autosave)
		loc_autosave_chk = "checked=checked" ;
	if(b_val_filter)
		b_val_filter_chked= "checked=checked" ;

if(!bmid)
{
%>
    <div class="layui-form-item" id="fi_local">
    <div class="layui-form-label"><wbt:g>local</wbt:g></div>
	  <div class="layui-input-inline" style="width: 100px;" title="<wbt:g>local_ptt</wbt:g>">
	   <input type="checkbox" id="local" name="local" <%=loc_chked%> lay-skin="switch"  lay-filter="local" class="layui-input">
	  </div>
	  <div id="local_setting" style="<%=loc_set%>">
    <label class="layui-form-mid"><wbt:g>default_val</wbt:g></label>
    <div class="layui-input-inline">
      <input type="text"  id="local_defval"  name="local_defval"  class="layui-input" style="width: 150px;">
    </div>
    <div class="layui-form-mid"><wbt:g>auto_save</wbt:g></div>
	  <div class="layui-input-inline" style="width: 80px;">
	   <input type="checkbox" id="local_autosave" name="local_autosave" <%=loc_autosave_chk%> lay-skin="switch"  lay-filter="local_autosave" class="layui-input">
	  </div>
	</div>
  </div>
<%
}
%>
    <div class="layui-form-item" id="addr_setting">
    <label class="layui-form-label"><wbt:g><%=(bmid?"js_exp":"addr") %></wbt:g>:</label>
    <div class="layui-input-inline" style="width:400px">
<%
if(bmid)
{
%><textarea style="width:100%;height:100px;overflow:auto;white-space: nowrap;"  id="addr"  name="addr"   class="layui-input" ondblclick="on_js_edit()" title="<wbt:g>dbclk_open_jse</wbt:g>"></textarea>
<%
}
else
{
%>
<input type="text"  id="addr"  name="addr" autocomplete="off" class="layui-input" value="<%=addr%>"/>
<%
}
%>      
    </div>
    <div class="layui-input-inline" >
<%
if(!bmid)
{
%>
    	<button class="layui-btn layui-btn-primary" title="Check Address" onclick="chk_addr()"><i class="fa-solid fa-check"></i></button>
    	<button class="layui-btn layui-btn-primary" title="Address Help" onclick="help_addr()"><i class="fa-solid fa-question"></i></button>
<%
}
%>
    </div>
  </div>
  <%--
   <div class="layui-form-item">
    <label class="layui-form-label">Scan rate:</label>
    <div class="layui-input-block">
      <input type="text" id="srate" name="srate" required  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
   --%>
    
<%
if(!bmid)
{
%>
  <div class="layui-form-item" id="transfer_setting">
    <label class="layui-form-label"><wbt:g>transfer</wbt:g></label>
    <div class="layui-input-block" style="width:370px">
      <input id="transfer_s" name="transfer_s" class="layui-input" readonly="readonly" onclick="edit_trans()"/>
    </div>
  </div>
  
  <div class="layui-form-item" id="val_filter_setting">
    <label class="layui-form-label"><wbt:g>filter</wbt:g></label>
    <div class="layui-input-block" style="width:370px">
      <input type="checkbox" id="b_val_filter" name="b_val_filter" <%=b_val_filter_chked%> lay-skin="switch"  lay-filter="b_val_filter" class="layui-input">
      <wbt:g>en_anti_interf</wbt:g>
    </div>
  </div>
<%
}
%>
  <div class="layui-form-item" id="max_min_val" style="display:none;">
    <label class="layui-form-label"><wbt:g>min,val</wbt:g></label>
    <div class="layui-input-inline" style="width: 80px;">
     <input type="number" id="min_val_str" name="min_val_str" class="layui-input">
    </div>
     
    <div class="layui-form-mid"><wbt:g>max,val</wbt:g></div>
    <div class="layui-input-inline" style="width: 80px;">
      <input type="number" id="max_val_str" name="max_val_str" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>alert</wbt:g><i class="fa-solid fa-bell"></i></label>
    <div class="layui-input-inline"  style="width:500px;">
      <div id="alert_list" style="width:100%;white-space: nowrap;"></div>
    </div>
    <div class="layui-input-inline"  style="width:50px;">
    <button class="layui-btn layui-btn-primary" title="Add Alert Source" onclick="edit_alert()"><i class="fa-solid fa-plus"></i></button>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>desc</wbt:g>:</label>
    <div class="layui-input-block">
      <input type="text"  id="desc"  name="desc"  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
  </td>
  <td>
 <div class="layui-btn-container">
    <button onclick="on_new_tag()"  class="layui-btn layui-btn-primary" style="width:90px;"><wbt:g>add</wbt:g></button>
    <button onclick="on_copy_tag()" class="layui-btn layui-btn-primary" style="width:90px;"><wbt:g>copy</wbt:g></button>
    <button  class="layui-btn layui-btn-primary" style="width:90px;"><wbt:g>del</wbt:g></button>
    </div>
    
  </td>
  </tr>
  </table>
 </form>
</body>
<script type="text/javascript">

var node_path="<%=path%>";
var tag_id = "<%=id%>"
var bmid = <%=bmid%>;

var name= "<%=html_str(name) %>" ;
var title = "<%=html_str(title)%>" ;
var desc = "<%=html_str(desc)%>";
var addr = "<%=html_str(addr)%>";
var vt = "<%=valtp_str%>" ;
var srate = "<%=srate%>";
var dec_digits = <%=dec_digits%> ;
var canw = "<%=canw%>"
var trans_dd = <%=trans%>;
var bloc=<%=blocal%>
var loc_devf="<%=html_str(local_defval)%>" ;
var bloc_autosave = <%=local_autosave%> ;
var min_val_str = "<%=min_val_str%>";
var max_val_str = "<%=max_val_str%>";
var alerts_dd = <%=alerts%>;
if(!alerts_dd)
	alerts_dd=[];

function update_form()
{
	var bloc = $("#local").prop("checked") ;
	if(bloc)
	{
		$("#local_setting").css("display","") ;
		$("#addr_setting").css("display","none") ;
		$("#transfer_setting").css("display","none") ;
		$("#val_filter_setting").css("display","none") ;
		
	}
	else
	{
		$("#local_setting").css("display","none") ;
		$("#addr_setting").css("display","") ;
		$("#transfer_setting").css("display","") ;
		$("#val_filter_setting").css("display","") ;
	}
	
	if($("#vt").find("option:selected").attr("b_num")=="true")
		$("#max_min_val").css("display","") ;
	else
		$("#max_min_val").css("display","none") ;
	
}

var form ;

layui.use('form', function(){
	  form = layui.form;
	  $("#name").val(name) ;
	  $("#title").val(title) ;
	  $("#addr").val(addr) ;
	  $("#desc").val(desc) ;
	  if(dec_digits>0)
	  	$("#dec_digits").val(dec_digits);
	  else
		$("#dec_digits").val("");
	  $("#vt").val(vt) ;
	  $("#srate").val(srate) ;
	  $("#canw").val(canw) ;
	  $("#local_defval").val(loc_devf) ;
	  $("#min_val_str").val(min_val_str) ;
	  $("#max_val_str").val(max_val_str) ;
	  //$("#alert_low").val(alert_low) ;
	 // $("#alert_high").val(alert_high) ;
	  
	  form.on('switch(local)', function(obj){
		        var b = obj.elem.checked ;
		  update_form();
		  });
	  form.on("select(vt)",function(obj){
		  //      var b = obj.elem.checked ;
		  update_form();
		  });
	  update_form();
	  form.render();
});
	
function win_close()
{
	dlg.close(0);
}


function update_transfer_s()
{
	if(trans_dd==null||trans_dd._n=='none')
	{
		$("#transfer_s").val("") ;
		return ;
	}
	$("#transfer_s").val(trans_dd._t) ;
}

function update_alert_s()
{
	let tmps = "" ;
	for(let i=0 ; i<alerts_dd.length ; i ++)
	{
		let d = alerts_dd[i];
		tmps += `<div id="alert_\${i}" class="alert_item" >
			<span onclick="edit_alert(\${i})" class="tt">\${d.tpt} \${d.param1}(\${d.prompt})</span><span class="oper">&nbsp;&nbsp;<i class="fa fa-times fa-lg" onclick="del_alert(\${i})"></i></span>
			</div>`;
	}
	
	$("#alert_list").html(tmps) ;
}

update_transfer_s();
update_alert_s();

function edit_trans()
{
	dlg.open("./tag_trans_edit.jsp",
			{title:"<wbt:g>val,transfer</wbt:g>",w:'600px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 trans_dd = ret ;
						 update_transfer_s();
						 dlg.close();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_alert(idx)
{
	if(event)
		event.preventDefault() || (event.returnValue = false);
	if(idx<0||idx>=alerts_dd.length)
		return;
	alerts_dd.splice(idx,1) ;
	update_alert_s();
}

function get_alert_idx_name(n)
{
	for(let i = 0 ; i < alerts_dd.length ; i ++)
	{
		let a = alerts_dd[i] ;
		if(n==a.name)
			return i ;
	}
	return -1 ;
}

function edit_alert(idx)
{
	if(event)
		event.preventDefault() || (event.returnValue = false);
	let tt = "<wbt:g>edit,tag,alt_evt,sor</wbt:g>"
	if(idx==undefined||idx==null)
		tt ="<wbt:g>add,tag,alt_evt,sor</wbt:g>"
	let dd = null;
	if(idx>=0)
		dd = alerts_dd[idx] ;
	dlg.open("./tag_alert_edit.jsp",	{title:tt,w:'600px',h:'400px',dd:dd},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 if(ret.name)
						 {
							 let oldidx = get_alert_idx_name(ret.name) ;
							 if(oldidx>=0 && oldidx!=idx)
							 {
								 dlg.msg("<wbt:g>name</wbt:g> ["+ret.name+"] <wbt:g>is_al_exist</wbt:g>");
								 return ;
							 }
						 }
						 if(idx>=0)
						 	alerts_dd[idx] = ret ;
						 else
							 alerts_dd.push(ret) ;
						 update_alert_s();
						 dlg.close();
				 	});
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
	let id=$("#id").val() ;
	let n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	let tt = $('#title').val();
	if(tt==null||tt=='')
	{
		//cb(false,'please input title') ;
		//return ;
		tt = n ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	let bloc = $("#local").prop("checked") ;
	let b_val_filter = $("#b_val_filter").prop("checked") ;
	let loc_defv = get_input_val("local_defval") ;
	let bloc_autosave = $("#local_autosave").prop("checked") ;
	let max_val_str = $("#max_val_str").val() ;
	let min_val_str = $("#min_val_str").val() ;
	//let alert_low = $("#alert_low").val();
	//let alert_high = $("#alert_high").val();
	
	let canw = get_input_val("canw","");
	cb(true,{id:id,name:n,title:tt,desc:desc,mid:bmid,
		addr:get_input_val("addr",""),
		vt:get_input_val("vt",""),
		dec_digits:get_input_val("dec_digits",-1,true),
		srate:get_input_val("srate",100,true),
		canw:canw,
		trans:JSON.stringify(trans_dd),
		b_val_filter:b_val_filter,
		bloc:bloc,loc_defv:loc_defv,bloc_autosave:bloc_autosave,
		min_val_str:min_val_str,max_val_str:max_val_str,alerts:JSON.stringify(alerts_dd),
		});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

function on_new_tag()
{
	event.preventDefault() || (event.returnValue = false);
	document.location.href="./tag_edit.jsp?path="+node_path;
}

function on_copy_tag()
{
	event.preventDefault() || (event.returnValue = false);
	if(id=='')
		return ;
	//console.log(dlg.get_opener_w())
	dlg.get_opener_w().copy_paste_tag(node_path,tag_id,(newid)=>{
		document.location.href="./tag_edit.jsp?path="+node_path+"&id="+newid;
	});
}

function chk_addr()
{
	event.preventDefault() || (event.returnValue = false);
	var addr = $("#addr").val() ;
	addr = trim(addr) 
	if(!addr)
		return ;
	
	send_ajax("./tag_ajax.jsp",{op:"chk_addr",
		vt:get_input_val("vt",""),
		addr:get_input_val("addr",""),
		canw:get_input_val("canw",""),
		path:node_path
		},(bsucc,ret)=>{
			if(ret=="{}")
				return ;
			var r ;
			eval("r="+ret) ;
			//console.log(r) ;
			if(r.guess)
			{
				if(r.addr)
					$("#addr").val(r.addr) ;
				var vt = r.vt+"" ;
				if(vt)
				{
					$("#vt").val(vt) ;
				}
				$("#canw").val(""+r.canw) ;
				form.render();
			}
			else
			{
				if(r.res>0)
				{
					dlg.msg("<wbt:g>chk,succ</wbt:g>");
					return ;//
				}
					
				if(r.res<0)
				{
					dlg.msg(r.prompt) ;
					return ;
				}
				if(r.addr)
					$("#addr").val(r.addr) ;
				var vt = r.vt+"" ;
				if(vt)
				{
					$("#vt").val(vt) ;
					form.render();
				}
				dlg.msg(r.prompt) ;
			}
			
	});
}

function help_addr()
{
	window.open("tag_addr_helper.jsp?path="+node_path) ;
}

function on_js_edit()
{
	let txt = $("#addr").val() ;
	dlg.open("./cxt_script.jsp?dlg=true&opener_txt_id=addr&path="+node_path,
			{title:"<wbt:g>edit_mid_tag_js</wbt:g>",w:'600px',h:'400px',},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#addr").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

</script>
</html>