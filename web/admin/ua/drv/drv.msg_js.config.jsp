<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.driver.common.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "nodepath"))
		return ;
	String nodep = request.getParameter("nodepath") ;
	UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
	if(node==null || !(node instanceof UACh))
	{
		out.print("no UACh found with path="+nodep) ;
		return ;
	}
	
	UACh ch = (UACh)node ;
	DevDriver drv = ch.getDriver() ;
	if(drv==null || !(drv instanceof MsgJSDrv))
	{
		out.println("no driver found") ;
		return ;
	}
	String ch_path = ch.getNodePath() ;
	MsgJSDrv msgjs_drv = (MsgJSDrv)drv ;
	ConnPt connpt = ch.getConnPt() ;
	String connpt_cn = "" ;
	if(connpt!=null)
		connpt_cn = connpt.getClass().getCanonicalName() ;
	String conf_txt = ch.getDrvSpcConfigTxt() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,650);
</script>
<style>
body {font-size: 12px;}
.layui-form-label
{
	width:120px;
}
.layui-form-item {
    margin-bottom: 5px;
}
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="">
<div class="layui-form-item">
    <label class="layui-form-label">On Init (run once)</label>
    <div class="layui-input-block">
      &nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="4" cols="80" id="on_init_js" style="left:10px;border-color: #e6e6e6" ondblclick="on_init_js_edit()" title="double click to open edit JS dialog"></textarea>
    </div>
  </div>
  
<div class="layui-form-item">
    <label class="layui-form-label">On Msg In <br></label>
    <div class="layui-input-block">
    ($ch,$connpt,$msg)=>{ <input type="checkbox" id="msg_in_str" lay-skin="primary"/>Msg In String  <br>
      &nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="6" cols="80" id="on_msgin_js" style="left:10px;border-color: #e6e6e6" ondblclick="on_msgin_js_edit()" title="double click to open edit JS dialog"></textarea>
    <br>&nbsp;&nbsp;&nbsp;&nbsp;}
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">On Tag Write</label>
    <div class="layui-input-block">
     ($ch,$connpt,$tag,$input)=>{<br>
      &nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="6" cols="80" id="on_tagw_js" style="left:10px;border-color: #e6e6e6"  ondblclick="on_tagw_js_edit()" title="double click to open edit JS dialog"></textarea>
    <br>&nbsp;&nbsp;&nbsp;&nbsp;}
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">On Loop</label>
    <div class="layui-input-block">
     ($ch,$connpt)=>{<br>
      &nbsp;&nbsp;&nbsp;&nbsp;<textarea rows="6" cols="80" id="on_loop_js" style="left:10px;border-color: #e6e6e6"  ondblclick="on_loop_js_edit()" title="double click to open edit JS dialog"></textarea>
    <br>&nbsp;&nbsp;&nbsp;&nbsp;}
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var ch_path = "<%=ch_path%>" ;
var input_txt = dlg.get_opener_opt("inputv") ;
var connpt_cn = "<%=connpt_cn%>" ;
//console.log(input_txt) ;
var conf_ob=null;
if(input_txt)
{
	eval("conf_ob="+input_txt) ;
	$("#on_init_js").val(conf_ob.on_init_js||"") ;
	$("#on_msgin_js").val(conf_ob.on_msgin_js||"") ;
	$("#on_tagw_js").val(conf_ob.on_tagw_js||"") ;
	$("#on_loop_js").val(conf_ob.on_loop_js||"") ;
	$("#msg_in_str").prop("checked",conf_ob.msg_in_str!=false) ;
}
	

function get_conf_obj()
{
	let on_init_js = $("#on_init_js").val();
	let on_msgin_js = $("#on_msgin_js").val();
	let on_tagw_js = $("#on_tagw_js").val();
	let on_loop_js = $("#on_loop_js").val();
	let msg_in_str = $("#msg_in_str").prop("checked") ;
	return {on_init_js:on_init_js,on_msgin_js:on_msgin_js,msg_in_str:msg_in_str,
		on_tagw_js:on_tagw_js,on_loop_js:on_loop_js} ;
}

function do_submit(cb)
{
	let confob = get_conf_obj() ;
	
	cb(true,{txt:JSON.stringify(confob)});
}

function on_init_js_edit()
{
	let txt = $("#on_init_js").val() ;
	dlg.open("../../ua_cxt/cxt_script.jsp?dlg=true&no_parent=false&no_this=false&path="+ch_path,
			{title:"<wbt:g>edit</wbt:g> JS -  On Init (run once)",w:'600px',h:'400px',js_txt:txt,pm_objs:{$connpt:connpt_cn}},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					$("#on_init_js").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_msgin_js_edit()
{
	let txt = $("#on_msgin_js").val() ;
	dlg.open("../../ua_cxt/cxt_script.jsp?dlg=true&no_parent=false&no_this=false&path="+ch_path,
			{title:"<wbt:g>edit</wbt:g> JS -  On MsgIn",w:'600px',h:'400px',js_txt:txt,pm_objs:{$connpt:connpt_cn}},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					$("#on_msgin_js").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_tagw_js_edit()
{
	let txt = $("#on_tagw_js").val() ;
	dlg.open("../../ua_cxt/cxt_script.jsp?dlg=true&no_parent=false&no_this=false&path="+ch_path,
			{title:"<wbt:g>edit</wbt:g> JS - On Tag Write",w:'600px',h:'400px',js_txt:txt,pm_objs:{$connpt:connpt_cn}},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					$("#on_tagw_js").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_loop_js_edit()
{
	let txt = $("#on_loop_js").val() ;
	dlg.open("../../ua_cxt/cxt_script.jsp?dlg=true&no_parent=false&no_this=false&path="+ch_path,
			{title:"<wbt:g>edit</wbt:g> JS - On Tag Write",w:'600px',h:'400px',js_txt:txt,pm_objs:{$connpt:connpt_cn}},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					$("#on_loop_js").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

var form = null;
layui.use('form', function(){
	  form = layui.form;
	  form.render();
});
	
</script>
</html>