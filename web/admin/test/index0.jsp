<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.user.*,
	org.iottree.system.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.scada.*,
	org.iottree.system.xmldata.*,
	org.iottree.biz.*,
	org.iottree.system.gdb.*"%>
<%@ taglib uri="wb_tag" prefix="wbt"%><%
UserProfile up = UserProfile.getUserProfile(request);
String un = up.getUserInfo().getFullName();
List<DevContainer> dcs = DevManager.getInstance().listContainers();

%><!DOCTYPE html>
<html>
<head>
<title>IOT Tree Server</title>
<script src="/_js/jquery.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
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
  </div>
  <div id="win_act1"  class="oc-toolbar" style="width:100%;top:70px;bottom: 50px;overflow:auto" >
  <div id="main" class="btns" style="top:70px;width:100%">
  <%
  String idstr = "";
  for(DevContainer dc:dcs)
  {
	  String cid = dc.getAutoId() ;
	  idstr += ",\""+cid+"\"";
	  String tt = dc.getTitle() ;
  %>
    <div class="toolbarbtn" style="background-color: #515658" onclick="open_rep('<%=cid%>')">
      <div id="panel_<%=cid %>" style="background-color: #2f2f2f;width:100%;height:170px" ></div>
	  <div style="height:50px;margin:12px;color:#8dcef7"><%=tt %></div>
    </div>
<%
  }
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
var ids=[<%=idstr.substring(1)%>];
var all_panels=[];
function open_rep(id)
{
	window.open("rep.jsp?id="+id);
}
function add()
{
	//var id = oc.util.create_new_tmp_id()
	//window.open("rep.jsp?op=new&id="+id);
	
	//dlg.open("","")
	
	dlg.open("dev/container_edit.jsp",
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
						 location.reload();
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
	send_ajax("unit/unit_ajax.jsp","op=load_all",function(bsucc,ret){
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
	send_ajax("rep/rep_ajax.jsp","op=load&id="+ids[loadidx],function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
		}
		else
		{
			var lay = new oc.DrawLayer();
			lay.inject(ret) ;
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
	
</script>
</html>
