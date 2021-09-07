<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*,org.json.*,
  org.iottree.core.util.*,
	org.iottree.core.util.logger.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>
<%

//String txt = Convert.readFileTxt(new File("D:/work/work_dj/nha_node/testdir/r_comp3/filetp_check.json"), "utf-8") ;
//JSONArray jarr = new JSONArray(txt);

ILogger[] logs = LoggerManager.getAllLoggers() ;
Arrays.sort(logs, new Comparator<ILogger>(){

	public int compare(ILogger o1, ILogger o2)
	{
		return o1.getLoggerId().compareTo(o2.getLoggerId()) ;
	}}) ;

HashSet<String> hs = LoggerManager.getInCtrlEnableIds() ;
if(hs==null)
	hs = new HashSet<String>() ;
%>
<html>
<head>
<title>log controller</title>
<jsp:include page="../head.jsp"/>
<style>
table{border-collapse:collapse;}
body,td{font-size:12px;cursor:default;}
</style>
</head>
<body style="background-color: rgb(238, 243, 249)" topmargin="0" leftmargin="0" rightMargin="0" marginwidth="0" marginheight="0">
<table border="0" cellpadding="0" cellspacing="0" width="100%" style="font-size: 10pt;margin-left: 0;margin-top: 0">
	<tr>
		<td>
<button onclick="set_enable_in_ctrl()">log selected item</button>
<button onclick="set_default()">recover default</button>

		</td>
	</tr>
	<tr>
		<td valign="top">
			<span id='enable_ids'>
<%
if(hs!=null)
{
	for(Iterator<String> ir=hs.iterator();ir.hasNext();)
	{
%><%=ir.next()%>,<%
	}
}
%>
			</span>
			<table width="100%">
				<tr>
				 	<td>
						<table width="100%" cellspacing="0" id="l_header" >		
							<tr style="border-bottom: 1px solid rgb(204, 204, 204);background-image: url(WebRes?r=com/dw/web_ui/res/tool-bkgd.jpg);">
								<td width="15%">&nbsp;</td>
								<td width="85%">日志Id</td>		
							</tr>
<%
if(logs!=null)
{
	for(ILogger u:logs)
	{
		String chked = "" ;
		if(hs.contains(u.getLoggerId()))
			chked = "checked=checked" ;

%>
							<tr>
								<td><input type="checkbox" name="logids" value="<%=u.getLoggerId() %>" <%=chked %>/></td>
								<td><%=u.getLoggerId() %></td>
							</tr>	
<%
	}
}
%>							
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</body>
<script>

function set_enable_in_ctrl(btrace)
{
	var cs = document.getElementsByName('logids') ;
	if(cs==null||cs.length<=0)
	{
		alert('no log found!') ;
		return ;
	}
	var i ;
	var ids='' ;
	for(i =0 ; i < cs.length ; i ++)
	{
		if(!cs[i].checked)
			continue ;
			
		ids += (cs[i].value+'|') ;
	}
	
	if(ids=='')
	{
		alert('no log choice!') ;
		return ;
	}
	
	if(btrace)
	{
		sendWithResCallback('log_mgr_set_ajax.jsp?ctrl=true&trace=true','logids='+ids,set_cb,true) ;
	}
	else
	{
		sendWithResCallback('log_mgr_set_ajax.jsp?ctrl=true','logids='+ids,set_cb,true) ;
	}
}

function set_cb(bsucc,ret)
{
	if(!bsucc)
	{
		alert(ret) ;
		return ;
	}
	
	document.getElementById('enable_ids').innerHTML = ret ;
}

function set_default()
{
	sendWithResCallback('log_mgr_set_ajax.jsp?ctrl=false','',set_cb,true) ;
}

</script>
</html>