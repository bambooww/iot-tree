<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.xmldata.*
"%><%@ taglib uri="wb_tag" prefix="wbt"%><%//UserProfile up = UserProfile.getUserProfile(request);
//String un = up.getUserInfo().getFullName();
List<UAPrj> reps = UAManager.getInstance().listPrjs();%><!DOCTYPE html>
<html>
<head>
<title>IOT Tree Server</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
</head>
<style>
.oc-toolbar .toolbarbtn
{
width:200px;height:200px;margin: 10px;
}

</style>
<body>
  <div id="header" style="white-space:nowrap;top:0;width:100%;height:70px;background-color: #dfdfdf">
  	<img src="inc/logo3.png"/>  
  	<b>admin panel</b>
  	<button onclick="logout()">Logout</button>
  </div>
  <div id="win_act2"  style="width:70%;top:30px;overflow:auto;text-align: right;right:100px" >
  		<a href="javascript:open_devlib()">Device Library</a>
  	</div>
  <div id="win_act1"  class="oc-toolbar" style="width:100%;top:70px;bottom: 50px;overflow:auto" >
  	
  <div id="main" class="btns" style="top:10px;width:100%">
  <%
  	String idstr = "";
    for(UAPrj dc:reps)
    {
  	  String cid = dc.getId();
  	  idstr += ",\""+cid+"\"";
  	  String tt = dc.getTitle() ;
  %>
    <div class="toolbarbtn" style="background-color: #515658" onclick="open_rep('<%=cid%>')">
      <div id="panel_<%=cid %>" style="background-color: #2f2f2f;width:100%;height:170px" ></div>
	  <div style="height:50px;margin:12px;color:#8dcef7"><%=tt %></div>
    </div>
<%
  }
  if(idstr.length()>0)
	  idstr = idstr.substring(1) ;
%>
    <div class="toolbarbtn" style="background-color: #515658" onclick="add()">
      <div style="background-color: #aaaaab;width:100%;height:170px;align-content: center;"><br><i class="fa fa-plus-circle fa-3x"></i></div>
	  <div style="height:50px;margin:12px;color:#8dcef7">新增</div>
    </div>
  </div>
  </div>
  <div class="" style="width:100%;bottom: 0px;height:50px;background-color: #dfdfdf"></div>
</body>
<script type="text/javascript">
var ids=[<%=idstr%>];
var all_panels=[];
function open_rep(id)
{
	window.open("rep_editor.jsp?id="+id);
	//window.open("ua_rep.jsp?repid="+id);
}

function open_devlib()
{
	dlg.open_win("dev/dev_lib_lister.jsp?mgr=true",
			{title:"Device Library",w:'1000',h:'560'},
			[{title:'Close',style:"primary"},{title:'Help',style:"primary"}],
			[
				function(dlgw)
				{
					dlg.close();
				},
				function(dlgw)
				{
					dlg.msg("help is under dev");
				}
			]);
}
function add()
{
	dlg.open("ua/rep_edit.jsp",
			{title:"新增容器",w:'500px',h:'400px'},
			['确定','取消'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 //enable_btn(true);
							 return;
						 }
						 //console.log(ret);
						 dlg.close();
						 document.location.href=document.location.href;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function load_all_unit(cb)
{
	//send_ajax("unit/unit_ajax.jsp","op=load_all",function(bsucc,ret){
	send_ajax("ua/ui_unit_ajax.jsp","op=load_all",function(bsucc,ret){
		if(!bsucc&&ret.indexOf("[")!=0)
		{
			dlg.msg("load allunit err:"+ret);
			return ;
		}
		var ob = null ;
		eval("ob="+ret);
		var items=[];
		for(var item of ob)
		{
			oc.DrawUnit.addUnitByJSON(item);
		}
		cb();
	}) ;

}

var loadidx= 0 ;
function load_preview()
{
	if(loadidx>=ids.length)
		return;//end
		//"?op=load&id="+repid
	//send_ajax("rep/rep_ajax.jsp","op=load&id="+ids[loadidx],function(bsucc,ret){
	send_ajax("ua/ui_cont_ajax.jsp","op=load&id="+ids[loadidx],function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
		}
		else
		{
			
			var ob = null;
			eval("ob="+ret) ;
			var lay = new oc.DrawLayer();
			oc.iott.IOTTView.injectLayerByCont(lay,ob) ;
			//lay.inject(ret) ;
			var p1 = new oc.DrawPanelDiv("panel_"+ids[loadidx],{layer:lay}) ;
			all_panels.push(p1);
			loadidx ++ ;
			lay.ajustDrawFit();
			load_preview();
		}
	});
}
	
$(document).ready(function()
{
	load_all_unit(load_preview);
});
	


$(window).resize(function(){
	for(var p of all_panels)
		p.updateByResize();
	});

function logout()
{
	$.ajax({
        type: 'post',
        url:'./login/login_ajax.jsp',
        data: {op:"logout"},
        async: true,  
        success: function (result) {  
        	if("ok"==result)
        	{
        		document.location.href="/admin/login/login.jsp" ;
        	}
        	else
        	{
        		dlg.msg("Login failed") ;
        	}
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}
</script>
</html>
