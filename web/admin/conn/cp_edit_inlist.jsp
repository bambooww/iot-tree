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
	"%><%
	if(!Convert.checkReqEmpty(request, out, "repid"))
	return;
String repid = request.getParameter("repid") ;
String cpid = request.getParameter("cpid") ;
ConnProTcpClient cp = null ;
if(Convert.isNullOrEmpty(cpid))
{
	cp = new ConnProTcpClient() ;
	cpid = cp.getId() ;
}
else
{
	cp = (ConnProTcpClient)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
	if(cp==null)
	{
		out.print("no ConnProvider found") ;
		return ;
	}
}

String name = cp.getName() ;
String title= cp.getTitle() ;
String chked = "" ;
if(cp.isEnable())
	chked = "checked='checked'" ;
String desc = cp.getDesc();
String cp_tp = cp.getProviderType() ;
List<ConnProTcpClient.ClientItem> clients = cp.listClientItems();
%>
<html>
<head>
<title>tcp client cp editor</title>
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
      <input type="text" id="name" name="name" value="<%=name%>" required  lay-verify="required" placeholder="Pls input name" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>" required  lay-verify="required" placeholder="Pls input name" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">Clients:</label>
    <div class="layui-input-block">
      <table id="view_colorval"  class="layui-table" lay-filter="dl_list"  lay-size="sm" lay-even="true" style="width:95%" border="1">
		   <thead style="background-color: #cccccc">
		     <tr>
			  <td>Name</td>
			  <td>Host</td>
			  <td>Port</td>
			  <td>Title</td>
			  <td width="10px">Enable</td>
			  <td></td>
			</tr>
		  </thead>
		  <tbody id="client_list">
			
		  </tbody>
		  <tfoot style="height: 50px">
		    <td style="width:15%"><input id="input_id" type="hidden" value=""/><input id="input_name" type="text" size="4" /></td>
			 <td style="width:30%"><input id="input_host" type="text" size="13" /></td>
			 <td style="width:15%"><input id="input_port" type="number" size="4" /></td>
			  <td style="width:40%"><input id="input_title" type="text" size="20" /></td>
			  <td><input id="input_en" type="checkbox" lay-skin="switch" checked="checked" /></td>
			  <td><a href="javascript:set_item()">+</a></td>
			</tr>
		  </tfoot>
		</table>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var form = null;
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
	  form.on('switch(enable)', function(obj){
		  setDirty(true);
		  });
		  
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cp_tp%>" ;

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

function get_new_id()
{
	_tmpid++ ;
	var d = new Date() ;
	var tmps = 'id_' ;
	tmps += d.getFullYear() ;
	var i = d.getMonth() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i = d.getDay() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getHours();
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getMinutes();
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getSeconds() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	tmps += "_"+_tmpid ;
	return tmps ;
	//return "id_"+scada_tmpid ;
}

function set_item_c(id,name,host,port,title,en)
{
	if(id==null||id=='')
		id = get_new_id() ;
	if($("#"+id).length<=0)
	{
		var str = "<tr id='"+id+"' onclick=\"sel_item('"+id+"')\">"+
		  "<td>"+name+"</td>"+
		  "<td>"+host+"</td>"+
		  "<td>"+port+"</td>"+
		  "<td>"+title+"</td>"+
		  "<td>"+(en?"on":"off")+"</td>"+
		  "<td><a href=\"javascript:del_item('"+id+"')\">X</a></td></tr>";
		$('#client_list').append(str) ;
	}
	else
	{
		var str = "<td>"+name+"</td>"+
		  "<td>"+host+"</td>"+
		  "<td>"+port+"</td>"+
		  "<td>"+title+"</td>"+
		  "<td>"+(en?"on":"off")+"</td>"+
		  "<td><a href=\"javascript:del_item('"+id+"')\">X</a></td>" ;
		$("#"+id).html(str) ;
	}
}

<%for(ConnProTcpClient.ClientItem client:clients)
{
	String id = client.getId() ;
	String nn = client.getName();
	if(nn==null)
		nn="" ;
	String host = client.getHost();
	int port = client.getPort() ;
	String tt = client.getTitle() ;
	boolean en = client.isEnable() ;%>
set_item_c('<%=id%>','<%=nn%>','<%=host%>',<%=port%>,'<%=tt%>',<%=en%>);
<%
}
%>

function sel_item(id)
{
	var tds = $("#"+id).children('td') ;
	
	var name = tds.eq(0).html() ;
	var host = tds.eq(1).html() ;
	var port = parseInt(tds.eq(2).html()) ;
	var title = tds.eq(3).html() ;
	var ben =("on"==tds.eq(4).html()) ;
	$('#input_id').val(id);
	$('#input_name').val(name);
	$('#input_host').val(host);
	$('#input_port').val(port);
	$('#input_title').val(title);
	 $('#input_en').prop("checked",ben);
	 form.render(); 
}

function set_item()
{
	var id = $('#input_id').val();
	var name = $('#input_name').val();
	var host = $('#input_host').val();
	var port = $('#input_port').val();
	var title = $('#input_title').val();
	var en = $('#input_en').prop("checked") ;
	set_item_c(id,name,host,port,title,en);
	$('#input_id').val("");
	$('#input_name').val("");
	$('#input_host').val("");
	$('#input_port').val("");
	$('#input_title').val("");
	setDirty();
}

function del_item(id)
{
	//$('#view_colorval').remove($("#"+id));
	$("#"+id).remove();
	setDirty();
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
	
	var clients=[];
	$('#client_list').children('tr').each(function(){
		var id = $(this).attr('id');
		var tds = $(this).children('td') ;
		
		var name = tds.eq(0).html() ;
		var host = tds.eq(1).html() ;
		var port = parseInt(tds.eq(2).html()) ;
		var title = tds.eq(3).html() ;
		var ben =("on"==tds.eq(4).html()) ;
		
		clients.push({id:id,name:name,title:title,host:host,port:port,enable:ben});
	});
	
	cb(true,{id:cp_id,name:n,title:tt,desc:desc,enable:ben,tp:cp_tp,clients:clients});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>