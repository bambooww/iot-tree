<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*,org.json.*,
  org.iottree.core.util.*,
	org.iottree.core.util.logger.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>
<%

//String txt = Convert.readFileTxt(new File("D:/work/work_dj/nha_node/testdir/r_comp3/filetp_check.json"), "utf-8") ;
//JSONArray jarr = new JSONArray(txt);
int deflvl = LoggerManager.getDefaultLogLevel();

ILogger[] logs = LoggerManager.getAllLoggers() ;
Arrays.sort(logs, new Comparator<ILogger>(){

	public int compare(ILogger o1, ILogger o2)
	{
		return o1.getLoggerId().compareTo(o2.getLoggerId()) ;
	}}) ;
if(logs==null)
	logs = new ILogger[0] ;
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
.ctrl
{
	width:20px;height:20px;float:left;
	margin-left:5px;
}

.ctrl_sel
{
	border:solid 2px;border-color: blue;
}
</style>
</head>
<script type="text/javascript">
dlg.resize_to(800,600) ;
</script>
<body style="background-color: rgb(238, 243, 249)" topmargin="0" leftmargin="0" rightMargin="0" marginwidth="0" marginheight="0">
<form class="layui-form" action="">
<div class="layui-form-item">
    <label class="layui-form-label">Default</label>
    <div class="layui-input-inline">
      <select id="def_lvl"  lay-filter="def_lvl">
<%
for(int i = 0 ; i < ILogger.LEVELS.length ; i ++)
{
%><option value="<%=ILogger.LEVELS[i] %>"><%=ILogger.LEVELS_TT[i]  %></option><%
}
%>
</select>
    </div>
    <label class="layui-form-label"></label>
    <div class="layui-input-mid">
    	<button onclick="save_log()" class="layui-btn" title="Save the current configuration and continue to use it at the next startup">Save Config</button>
    	<button onclick="set_all_def()" class="layui-btn layui-btn-primary" title="">Set All to Default</button>
    </div>
   
  </div>
  <div style="overflow: auto;height:420px">
  <table class="layui-table" lay-size="sm" >
  <colgroup>
    <col width="60%">
    <col width="40%">
    <col>
  </colgroup>
  <thead>
    <tr>
      <th>Name</th>
      <!--
      <th>Controller</th>
        -->
      <th>Level</th>
    </tr> 
  </thead>
  <tbody>
<%
for(ILogger u:logs)
{
	String chked = "" ;
	if(hs.contains(u.getLoggerId()))
		chked = "checked=checked" ;

	String logid = u.getLoggerId() ;
	String tmps = logid ;
	if(tmps.length()>60)
		tmps = "..."+tmps.substring(tmps.length()-60) ;
	
	int ctrl = u.getCtrl() ;
	
%>
  
    <tr>
      <td title="<%=logid%>"><%=tmps %></td>
      <%--
      <td style="white-space: nowrap;width:170px">
      
      <div class="ctrl"  style="background-color: green;" title="set log all" onclick="set_ctrl('<%=logid%>',1)"></div>
      <div class="ctrl"  style="background-color: red;" title="disable log"  onclick="set_ctrl('<%=logid%>',-1)"></div>
      <div class="ctrl ctrl_sel"  style="background-color: gray;" title="log by level"  onclick="set_ctrl('<%=logid%>',0)"></div>
      </td>
       --%>
      <td style="white-space: nowrap">
      <div id="lvl_none" style="display:none">
      </div>
      <div id="lvl_<%=logid%>" >
 	  <input onclick="set_lvl('<%=logid%>',<%=ILogger.LOG_LEVEL_TRACE %>)" type="checkbox" id="lvl_trace_<%=logid %>" value="trace" title="Trace"  <%=(u.isTraceEnabled()?"checked=\"checked\"":"") %> lay-ignore>Trace
      <input onclick="set_lvl('<%=logid%>',<%=ILogger.LOG_LEVEL_DEBUG %>)" type="checkbox" id="lvl_debug_<%=logid %>" value="debug" title="Debug"  <%=(u.isDebugEnabled()?"checked=\"checked\"":"") %> lay-ignore >Debug
      <input onclick="set_lvl('<%=logid%>',<%=ILogger.LOG_LEVEL_INFO %>)" type="checkbox" id="lvl_info_<%=logid %>" value="info" title="Info"  <%=(u.isInfoEnabled()?"checked=\"checked\"":"") %> lay-ignore>Info
      <input onclick="set_lvl('<%=logid%>',<%=ILogger.LOG_LEVEL_WARN %>)" type="checkbox" id="lvl_warn_<%=logid %>" value="warn" title="Warn" <%=(u.isWarnEnabled()?"checked=\"checked\"":"") %> lay-ignore>Warn
      <input onclick="set_lvl('<%=logid%>',<%=ILogger.LOG_LEVEL_ERROR %>)" type="checkbox" id="lvl_error_<%=logid %>" value="error" title="Error"  <%=(u.isErrorEnabled()?"checked=\"checked\"":"") %> lay-ignore>Error
      </div>
	  </td>
    </tr>
<%
}
%>

  </tbody>
</table>
</div>
</form>
</body>
<script>
var form = null ;

$("#def_lvl").val(<%=deflvl%>) ;
layui.use('form', function(){
	  form = layui.form;
	  
	  form.on("select(def_lvl)",function(obj){
		  var v = $("#def_lvl").val() ;
		  send_ajax('log_ajax.jsp',{op:'def_lvl',v:v},(bsucc,ret)=>{
				if(!bsucc&&ret.indexOf("{")!=0)
				{
					dlg.msg(ret) ;
					return ;
				}
				document.location.href=document.location.href;
			}) ;
		  });
	 form.render();
	});

function save_log()
{
	event.preventDefault();
	send_ajax('log_ajax.jsp',{op:'save'},(bsucc,ret)=>{
		if(!bsucc||ret!='ok')
		{
			dlg.msg(ret);
			return ;
		}
		dlg.msg("save ok") ;
	}) ;
}

function set_all_def()
{
	event.preventDefault();
	send_ajax('log_ajax.jsp',{op:'set_def_all'},(bsucc,ret)=>{
		if(!bsucc||ret!='ok')
		{
			dlg.msg(ret);
			return ;
		}
		document.location.href=document.location.href;
	}) ;
}

function set_ctrl(logid,v)
{
		send_ajax('log_ajax.jsp',{op:'ctrl',logid:logid,v:v},(bsucc,ret)=>{
			
		}) ;

}

function set_lvl(logid,lvl)
{
	send_ajax('log_ajax.jsp',{op:'lvl',logid:logid,v:lvl},(bsucc,ret)=>{
		if(!bsucc&&ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		
		var ob ;
		eval("ob="+ret) ;
		for(var n in ob)
		{
			var t = document.getElementById("lvl_"+n+"_"+logid);
			if(!t)
				continue ;
			$(t).prop("checked",ob[n]);
		}
		 form.render();
	}) ;
}


</script>
</html>