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
	boolean bmid = "true".equalsIgnoreCase(request.getParameter("mid")) ;
	String path = request.getParameter("path") ;
	String id = request.getParameter("id") ;
	UATag tag = null ;
	String name= "" ;
	String title = "" ;
	String addr = "" ;
	UAVal.ValTP valtp = null ;
	String valtp_str = "" ;
	long srate = 200;
	int dec_digits = -1 ;
	boolean canw = false;
	String desc = "" ;
	String trans = null ;
	if(Convert.isNotNullEmpty(path) && Convert.isNotNullEmpty(id))
	{
		UANodeOCTags n = (UANodeOCTags)UAUtil.findNodeByPath(path);
		if(n==null)
		{
			out.print("no node with path="+path) ;
			return ;
		}
 		tag = n.getTagById(id) ;
 		name = tag.getName() ;
 		title = tag.getTitle() ;
 		desc = tag.getDesc() ;
 		bmid = tag.isMidExpress();
 		addr = tag.getAddress() ;
 		valtp = tag.getValTpRaw() ;
 		if(valtp!=null)
 			valtp_str = ""+valtp.getInt() ;
 		dec_digits = tag.getDecDigits() ;
 		srate = tag.getScanRate() ;
 		canw = tag.isCanWrite();
 		trans = tag.getValTranser() ;
 		if(Convert.isNullOrEmpty(trans))
 			trans = null ;
	}
%>
<html>
<head>
<title>Tag Editor </title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(600,500);
</script>

</head>
<body>
<form class="layui-form" action="">
	<input type="hidden" id="id" name="name" value="<%=html_str(id)%>">
	  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  
  </div>

    <div class="layui-form-item">
    <label class="layui-form-label"><%=(bmid?"Express":"Address") %>:</label>
    <div class="layui-input-block">
      <input type="text"  id="addr"  name="addr"  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <input type="text"  id="desc"  name="desc"  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Data type</label>
    <div class="layui-input-inline" style="width: 120px;">
      <select  id="vt"  name="vt"  class="layui-input" placeholder="">
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
    <div class="layui-input-inline" style="width: 120px;">
      <input type="text" id="dec_digits" name="dec_digits" placeholder="" autocomplete="off" class="layui-input">
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
    <div class="layui-form-item">
    <label class="layui-form-label">Client access</label>
    <div class="layui-input-block">
      <select id="canw"  name="canw" class="layui-input">
        <option value="false">Read Only</option>
        <option value="true">Read/Write</option>
      </select>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Transfer</label>
    <div class="layui-input-block">
      <input id="transfer_s" name="transfer_s" class="layui-input" readonly="readonly" onclick="edit_trans()"/>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">

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


layui.use('form', function(){
	  var form = layui.form;
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
		cb(false,'please input title') ;
		return ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var canw = get_input_val("canw",null)=="true";
	cb(true,{id:id,name:n,title:tt,desc:desc,mid:bmid,
		addr:get_input_val("addr",""),
		vt:get_input_val("vt",""),
		dec_digits:get_input_val("dec_digits",-1,true),
		srate:get_input_val("srate",100,true),
		canw:canw,
		trans:JSON.stringify(trans_dd)
		});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>