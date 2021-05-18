<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>	
<%
	if(!Convert.checkReqEmpty(request, out, "repid","id"))
		return;
	
	//String op = request.getParameter("op");
	String repid=request.getParameter("repid");
	String id = request.getParameter("id") ;
	UARep rep = UAManager.getInstance().getRepById(repid) ;
	if(rep==null)
	{
		out.print("no rep found");
		return ;
	}
	
	String repname = rep.getName() ;
	
	UANode n = rep.findNodeById(id) ;
	if(n==null)
	{
		out.print("no node found") ;
		return ;
	}
	if(!(n instanceof UANodeOCTags))
	{
		out.print("not node oc tags") ;
		return ;
	}
	UANodeOCTags ntags = (UANodeOCTags)n ;
	List<UATag> tags = ntags.listTagsAll() ;
	

	String parent_p = ntags.getNodePathName() ;
	if(Convert.isNotNullEmpty(parent_p))
		parent_p +="." ;
	boolean bdlg = "true".equalsIgnoreCase(request.getParameter("dlg"));
	%>
<html>
<head>
<title>context tags lister</title>
<style>
		table{border-collapse:collapse;}
		body,td{font-size:12px;cursor:default;}
</style>

<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/ajax.js" ></script>
<script src="/_js/dlg.js" ></script>
<script>
	
</script>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<b>Context:<%=rep.getTitle() %>] / [<%=ntags.getNodePathName() %>] </b>
<%

%>
<span id="updt"></span><span id="log_inf"></span>
<div style="overflow: scroll;height: 67%">
<table width='100%' border='1' height0="100%">
 <tr height0='20'>
  <td width='2%'>Mid</td>
  <td width='15%'>Path Name</td>
  <td width='15%'>Title</td>
  <td width='4%'>Address</td>
  <td width='6%'>Value Type</td>
  <td width='8%'>Value</td>
  <td width='4%'>Time Stamp</td>
  <td width='5%'>Quality</td>
 </tr>
<%
	
	for(UATag tg : tags)
	{
		String pathn = tg.getNodePathName();
		pathn = pathn.substring(parent_p.length()) ;
		String m = tg.isMidExpress()?"M":"" ;
%>
 <tr height0='1' style0="height:5" onmouseover="mouseover(this)" onmouseout="mouseout(this)">
 <td><%=m %></td>
  <td><%=pathn %></td>
  <td><%=tg.getNodePathTitle() %></td>
  <td><%=tg.getAddress() %></td>
  <td><%=tg.getValTp() %></td>
  <td id='rv_<%=pathn %>' align="right"><%="" %></td>
  <td nowrap="nowrap" id='rdt_<%=pathn %>'>	
  </td>
  <td nowrap="nowrap" id='rq_<%=pathn %>' ></td>
  </tr>
<%
	}
%>
</table>
</div>
<table width='100%' border='1' height="120">
<tr>
 <td>
 script test <input type='button' value='run' onclick="run_script_test('')"/>
 </td>
 <td>script test result</td>
</tr>
 <tr>
  <td>
   <textarea id='script_test' rows="6" style="overflow: scroll;width:98%"></textarea>
  </td>
  <td>
   <textarea id='script_res' rows="6" style="overflow: scroll;width:98%"></textarea>
  </td>
 </tr>
</table>
<div id='opc_info'>
</div>
</body>
<script>
var repid="<%=repid%>" ;
var repname = "<%=repname%>" ;
var id = "<%=id%>" ;
var rowbgcolor = '#ffffff';
function mouseover(sel)
{
 rowbgcolor = sel.style.backgroundColor;
 sel.style.backgroundColor='#dddddd';
}
function mouseout(sel)
{
 sel.style.backgroundColor=rowbgcolor;
}

function log(s)
{
	document.getElementById('log_inf').innerHTML = s ;
}
	

function run_script_test(fn)
{
	var scode = document.getElementById('script_test').value ;
	if(scode==null||scode==''||trim(scode)=='')
		return ;
	send_ajax('cxt_script_test.jsp','repid='+repid+'&id='+id+'&txt='+utf8UrlEncode(scode),
		function(bsucc,ret)
		{
			document.getElementById('script_res').value = ret ;
		},false) ;
}

function update_dyn(txt)
{
	var r = null ;
	eval("r="+txt) ;
	//log("r="+ret) ;
	$('#updt').html("更新时间"+r.dt) ;
	var num = r.vals.length ;
	for(var i = 0 ; i < num ; i++)
	{
		var cont = r.vals[i] ;
		var path=cont.path ;
		var v = cont.v ;
		//alert(n) ;
		var bvalid = cont.valid ;
		var chgdt = cont.chgdt ;
		var dt = cont.dt ;
		
		document.getElementById('rv_'+path).innerHTML = v ;
		var vhobj = document.getElementById('rq_'+path);
		if(bvalid)
			vhobj.innerHTML="√" ;
		else
			vhobj.innerHTML="×" ;//$('#rq_'+n).html("×") ;
		//	document.getElementById('chgdt_'+n).innerHTML=chgdt;
		document.getElementById('rdt_'+path).innerHTML=dt;//$('#rdt_'+n).html(dt) ;
	}
}

function update_st()
{
	send_ajax('cxt_var_ajax.jsp','repid='+repid+'&id='+id,function(bsucc,ret)
	{
		if(!bsucc||ret.indexOf('succ=')!=0)
		{
			return ;
		}
		ret = ret.substring(5) ;
		update_dyn(ret);
		
	},false) ;
}

//update_st();

//setInterval("update_st()",4000);

var ws = null;


function ws_conn()
{
    var url = 'ws://' + window.location.host + '/admin/ws/cxt_rt/'+repname+"/"+id;
    if ('WebSocket' in window) {
        ws = new WebSocket(url);
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(url);
    } else {
        alert('WebSocket is not supported by this browser.');
        return;
    }
    ws.onopen = function () {
        //setConnected(true);
        log('Info: WebSocket connection opened.');
    };
    ws.onmessage = function (event) {
        //log('Received: ' + event.data);
        //log(event.data.length) ;
        update_dyn(event.data) ;
    };
    ws.onclose = function (event) {
       
        log('Info: WebSocket connection closed, Code: ' + event.code + (event.reason == "" ? "" : ", Reason: " + event.reason));
    };
}	

function ws_disconn() {
    if (ws != null) {
        ws.close();
        ws = null;
    }
}

ws_conn();
</script>
</html>