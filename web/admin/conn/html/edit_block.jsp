<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
	org.iottree.core.conn.html.*,
	org.iottree.core.conn.mqtt.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid","connid"))
	return;
String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnProHTTP cp = (ConnProHTTP)ConnManager.getInstance().getConnProviderById(prjid, cpid);
if(cp==null)
{
	out.print("no provider found with ");
	return ;
}
ConnPtHTTP cpt = (ConnPtHTTP)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no conn found") ;
	return ;
}

BindHandlerHtml bhh = (BindHandlerHtml)cpt.getBindHandler() ;
String id = request.getParameter("id") ;
HtmlBlockLocator hbl = bhh.getBlockLocator(id);

String name = "" ;
String title = "" ;
if(hbl!=null)
{
	name = hbl.getName() ;
	title = hbl.getTitle() ;
}
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,600);
</script>
</head>
<body>
<blockquote class="layui-elem-quote " id="selected_info">&nbsp;
URL:<span id="url"></span>
	<button onclick="chk_in_page()">Check With Html Page</button>
 </blockquote>
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
	  
  </div>
Trace Info
   <table id="view_colorval"  class="layui-table" lay-filter="dl_list"  lay-size="sm" lay-even="true" style="margin-left:30px;width:80%" border="1">
   <thead style="background-color: #cccccc">
     <tr>
	  <td style="width:45%">Trace Txt</td>
	  <td style="width:40%">Must Have</td>
	  <td style="width:15%">Oper</td>
	</tr>
  </thead>
  <tbody id="k_list">
<%
	if(hbl!=null)
{
	List<HtmlBlockLocator.TracePoint> lks = hbl.getTracePts();
	for(HtmlBlockLocator.TracePoint lk:lks)
	{
%>
	<tr id="lk_" >
		<td><%=lk.getTraceTxt()%></td>
		<td><%=lk.isMustHave()%></td>
		<td>

<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="edit basic" onclick="edit_devdef('<%=""%>')"><i class="fa fa-pencil"></i></button>

		</td>
	</tr>
<%
	}
}
%>
  </tbody>
  <tfoot>
  <tr>
	  <td style="width:15%"><input type="text" id="inp_keytxt"/></td>
	  <td style="width:40%"><input type="checkbox" id="inp_need" /></td>
	  <td style="width:35%"><button onclick="add_item()">&nbsp;&nbsp;&nbsp;+&nbsp;&nbsp;&nbsp;</button></td>
	</tr>
  </tfoot>
</table>
 </form>
 
</body>
<script type="text/javascript">
var form = null;
var _tmpid = 0 ;

var bdirty=false;
var prjid = "<%=prjid%>";
var cp_id = "<%=cpid%>" ;
var conn_id = "<%=connid%>" ;
var ow = dlg.get_opener_w();

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
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });

	  form.render();
	  
	  $("#url").html(ow.get_probe_url()) ;
});



function isDirty()
{
	return bdirty;
}
function setDirty()
{
	bdirty= true;
	dlg.btn_set_enable(1,true);
}

function add_item()
{
	event.preventDefault();
	var n = $("#inp_keytxt").val() ;
	var b = $('#inp_need').is(':checked');
	if(!n)
	{
		dlg.msg("please input key text")
		return ;
	}
	var hstr= "<tr><td>"+n+"</td><td>"+b+"</td><td></td></tr>" ;
	$("#k_list").append(hstr);
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

function get_tracetxt_obs()
{
	var rets = [] ;
	var trs = $("#k_list").children("tr");
	for(var tr of trs)
	{
		var tds = tr.children('td') ;
		var n = tds.eq(0).html() ;
		var need = "true"==tds.eq(1).html() ;
		rets.append({keytxt:n,need:need}) ;
	}

	return rets ;
}

function get_loc_info()
{
	return {};
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
	
	var ktxts = get_tracetxt_obs() ;
	var ob = {n:n,t:tt,tracetxts:ktxts};
	cb(true,ob) ;
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

function chk_in_page()
{
	event.preventDefault();
	dlg.open("./html_parser.jsp?url="+ow.get_probe_url(),
			{title:"Check Page"},['Ok','Cancel'],
			[
				function(dlgw)
				{
					
					 dlg.close() ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

</script>
</html>