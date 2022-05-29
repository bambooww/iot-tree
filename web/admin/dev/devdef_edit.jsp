<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
if(!Convert.checkReqEmpty(request, out, "libid","catid"))
	return ;
	String libid = request.getParameter("libid") ;
	String catid = request.getParameter("catid") ;
	String devid = request.getParameter("devid") ;
	if(devid==null)
		devid = "" ;
	
	DevLib lib = DevManager.getInstance().getDevLibById(libid) ;
	if(lib==null)
	{
		out.print("no lib found") ;
		return ;
	}
	
	
	DevCat cat = lib.getDevCatById(catid) ;
	if(cat==null)
	{
		out.print("no cat found") ;
		return ;
	}
	
	String name = "" ;
	String title = "" ;
	
	String drv_name="" ;
	String drv_tt ="" ;
	
	DevDef dev = null;
	if(Convert.isNotNullEmpty(devid))
	{
		dev = cat.getDevDefById(devid) ;
		if(dev==null)
		{
			out.print("no dev found") ;
			return ;
		}
		
		name = cat.getName() ;
		title = cat.getTitle() ;
		DevDriver dd = dev.getRelatedDrv() ;
		if(dd!=null)
		{
			drv_name = dd.getName() ;
			drv_tt = dd.getTitle() ;
		}
	}
	
	
		
	
	
%>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(400,320);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-block">
      <input type="text" id="name" name="name" value="<%=name %>"  class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
    <div class="layui-input-block">
      <input type="text" id="title" name="title" value="<%=title %>"  autocomplete="off" class="layui-input">
    </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>driver</wbt:lang></label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="drv_title" id="drv_title" value="<%=drv_tt %>" onclick="select_drv()"  class="layui-input"/>
      <input type="hidden" name="drv" id="drv" value="<%=drv_name %>" />
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">

var libid = "<%=libid%>" ;
var catid = "<%=catid%>" ;
var devid = "<%=devid%>" ;
layui.use('form', function(){
	  var form = layui.form;
});
	
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


function select_drv()
{
	dlg.open_win("../ua/drv_selector.jsp?edit=true",
			{title:"select driver",w:'400',h:'535'},
			[{title:'<wbt:lang>ok</wbt:lang>',style:""},{title:'Clear',style:"primary"},{title:'<wbt:lang>cancel</wbt:lang>',style:"primary"}],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(res,ret){
						if(res)
						{
							$("#drv_title").val(ret.title) ;
							$("#drv").val(ret.name) ;
							dlg.close();
						}
						else
						{
							dlg.msg(ret) ;
						}
					}) ;
					
				},
				function(dlgw)
				{
					$("#drv_title").val("") ;
					$("#drv").val("") ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function do_submit(cb)
{
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
	var drv = $("#drv").val()
	cb(true,{libid:libid,catid:catid,devid:devid,name:n,title:tt,drv:drv});
}

</script>
</html>                                                                                                                                                                                                                            