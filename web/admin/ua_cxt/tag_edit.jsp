<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%!
	public static String html_str(Object o)
	{
		if(o==null)
			return "" ;
		return ""+o ;
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
	boolean canw = false;
	String desc = "" ;
	String trans = null ;
	boolean b_val_filter=false;
	
	if(id==null)
		id = "" ;
	
	UANode tmpn = UAUtil.findNodeByPath(path);
	if(tmpn instanceof UAHmi)
		tmpn = tmpn.getParentNode();
	UANodeOCTags n = (UANodeOCTags)tmpn;
	if(n==null)
	{
		out.print("no node with path="+path) ;
		return ;
	}
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
 		canw = tag.isCanWrite();
 		trans = tag.getValTranser() ;
 		if(Convert.isNullOrEmpty(trans))
 			trans = null ;
 		b_val_filter = tag.isValFilter() ;
	}
%>
<html>
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

</style>
<script>
dlg.resize_to(800,500);
</script>

</head>
<body>
<form class="layui-form" action="">
 <table style="width:100%	">
   <tr>
     <td width="90%">
	<input type="hidden" id="id" name="name" value="<%=html_str(id)%>">
	  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline" style="width: 200px;">
      <input type="text" id="name" name="name" lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 300px;">
	    <input type="text" id="title" name="title" lay-verify="required" size="0" autocomplete="off" class="layui-input">
	  </div>
	  
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Data type</label>
    <div class="layui-input-inline" style="width: 100px;">
      <select  id="vt"  name="vt"  class="layui-input" >
        <option value="">-</option>
<%
for(UAVal.ValTP vt:UAVal.ValTP.values())
{
	 %><option value="<%=vt.getInt()%>"><%=vt.getStr() %></option><%
}
%>
      </select>
    </div>
    <div class="layui-form-mid">Decimal Digits:</div>
    <div class="layui-input-inline" style="width: 50px;">
      <input type="text" id="dec_digits" name="dec_digits" class="layui-input">
    </div>
    <div class="layui-form-mid">R/W:</div>
    <div class="layui-input-inline" style="width: 150px;">
      <select id="canw"  name="canw" class="layui-input">
        <option value="false">Read Only</option>
        <option value="true">Read/Write</option>
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
		
%>
    <div class="layui-form-item">
    <div class="layui-form-label">Local</div>
	  <div class="layui-input-inline" style="width: 100px;">
	   <input type="checkbox" id="local" name="local" <%=loc_chked%> lay-skin="switch"  lay-filter="local" class="layui-input">
	  </div>
	  <div id="local_setting" style="<%=loc_set%>">
    <label class="layui-form-mid">DefaultVal</label>
    <div class="layui-input-inline">
      <input type="text"  id="local_defval"  name="local_defval"  class="layui-input" style="width: 150px;">
    </div>
    <div class="layui-form-mid">Auto Save</div>
	  <div class="layui-input-inline" style="width: 80px;">
	   <input type="checkbox" id="local_autosave" name="local_autosave" <%=loc_autosave_chk%> lay-skin="switch"  lay-filter="local_autosave" class="layui-input">
	  </div>
	</div>
  </div>

    <div class="layui-form-item" id="addr_setting">
    <label class="layui-form-label"><%=(bmid?"Express":"Address") %>:</label>
    <div class="layui-input-inline" style="width:300px">
      <input type="text"  id="addr"  name="addr" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-input-inline" >
    	<button class="layui-btn layui-btn-primary" title="Check Address" onclick="chk_addr()"><i class="fa-solid fa-check"></i></button>
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
    

  <div class="layui-form-item" id="transfer_setting">
    <label class="layui-form-label">Transfer</label>
    <div class="layui-input-block" style="width:370px">
      <input id="transfer_s" name="transfer_s" class="layui-input" readonly="readonly" onclick="edit_trans()"/>
    </div>
  </div>
  
  <div class="layui-form-item" id="val_filter_setting">
    <label class="layui-form-label">Filter</label>
    <div class="layui-input-block" style="width:370px">
      <input type="checkbox" id="b_val_filter" name="b_val_filter" <%=b_val_filter_chked%> lay-skin="switch"  lay-filter="b_val_filter" class="layui-input">
      Enable anti-interference
    </div>
  </div>

  <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <input type="text"  id="desc"  name="desc"  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
  </td>
  <td>
 <div class="layui-btn-container">
    <button onclick="on_new_tag()"  class="layui-btn">&nbsp;New&nbsp;&nbsp;</button>
    <button onclick="on_copy_tag()" class="layui-btn">&nbsp;Copy&nbsp;</button>
    <button  class="layui-btn">Delete</button>
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
var addr = "<%=html_str(addr)%>" ;
var vt = "<%=valtp_str%>" ;
var srate = "<%=srate%>";
var dec_digits = <%=dec_digits%> ;
var canw = "<%=canw%>"
var trans_dd = <%=trans%>;
var bloc=<%=blocal%>
var loc_devf="<%=html_str(local_defval)%>" ;
var bloc_autosave = <%=local_autosave%> ;

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
	  
	  form.on('switch(local)', function(obj){
		        var b = obj.elem.checked ;
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

update_transfer_s();

function edit_trans()
{
	
	dlg.open("./tag_trans_edit.jsp",
			{title:"value transfer",w:'600px',h:'400px'},
			['Ok','Cancel'],
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
	var id=$("#id").val() ;
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		//cb(false,'please input title') ;
		//return ;
		tt = n ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var bloc = $("#local").prop("checked") ;
	var b_val_filter = $("#b_val_filter").prop("checked") ;
	var loc_defv = get_input_val("local_defval") ;
	var bloc_autosave = $("#local_autosave").prop("checked") ;
	
	var canw = get_input_val("canw",null)=="true";
	cb(true,{id:id,name:n,title:tt,desc:desc,mid:bmid,
		addr:get_input_val("addr",""),
		vt:get_input_val("vt",""),
		dec_digits:get_input_val("dec_digits",-1,true),
		srate:get_input_val("srate",100,true),
		canw:canw,
		trans:JSON.stringify(trans_dd),
		b_val_filter:b_val_filter,
		bloc:bloc,loc_defv:loc_defv,bloc_autosave:bloc_autosave
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
		path:node_path
		},(bsucc,ret)=>{
			if(ret=="{}")
				return ;
			var r ;
			eval("r="+ret) ;
			if(r.res>0)
			{
				dlg.msg("check ok");
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
	});
}

</script>
</html>