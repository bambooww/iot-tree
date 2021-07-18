<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	//if(!Convert.checkReqEmpty(request, out, "ch_path"))
	//	return ;
	String chpath = request.getParameter("ch_path") ;
	String devpath = request.getParameter("dev_path") ;
	UACh ch= null;
	UADev dev = null ;
	if(Convert.isNotNullEmpty(chpath))
	{
		ch  = (UACh)UAUtil.findNodeByPath(chpath) ;
		if(ch==null)
		{
			out.print("no ch node found");
			return ;
		}
		
	}
	else if(Convert.isNotNullEmpty(devpath))
	{
		dev = (UADev)UAUtil.findNodeByPath(devpath) ;
		if(dev==null)
		{
			out.print("no device node found");
			return ;
		}
		ch = dev.getBelongTo() ;
	}
	else
	{
		out.print("no path input") ;
		return ;
	}
	
	DevDriver drv = ch.getDriver() ;
	if(!ch.isDriverFit())
	{
		out.print("Channel Driver is not fit") ;
		return ;
	}
	String drv_name = drv.getName() ;
	String name = "" ;
	String title = "" ;
	String desc = "" ;
	DevDef dd = null ;
	String ddid = "" ;
	String ddtt = "" ;
	if(dev!=null)
	{
		name = dev.getName() ;
		title = dev.getTitle() ;
		desc = dev.getDesc() ;
		if(desc==null)
			desc="" ;
		dd = dev.getDevDef() ;
		if(dd!=null)
		{
			ddid = dd.getId() ;
			ddtt = dd.getTitle()+"["+dd.getName()+"]" ;
		}
	}
%>
<html>
<head>
<title>add dev</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/dlg.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
   <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>name</wbt:lang>:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name %>"   autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:lang>title</wbt:lang>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title %>"   autocomplete="off" class="layui-input">
	  </div>
  </div>
     <div class="layui-form-item">
    <label class="layui-form-label">Device:</label>
    <div class="layui-input-inline">
      <input type="hidden" id="devdef_id" name="devdef_id"  value="<%=ddid%>"/>
      <input type="text" id="devdef_tt" name="devdef_tt" value="<%=ddtt %>" readonly="readonly"  value=""   lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><button type="button" onclick="sel_devdef()">...</button></div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:lang>description</wbt:lang></label>
    <div class="layui-input-block">
      <textarea name="desc" id="desc"  class="layui-textarea"><%=desc %></textarea>
    </div>
  </div>
</form>
</body>
<script type="text/javascript">

var drv_name = "<%=drv_name%>" ;

function win_close()
{
	dlg.close(0);
}


function sel_devdef()
{
	dlg.open_win("../dev/dev_lib_lister.jsp?drv="+drv_name,
			{title:"Select Device in Library",w:'1000',h:'560'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					var sel = dlgw.get_selected() ;
					if(sel==null)
					{
						dlg.msg("please select device") ;
						return ;
					}//sel.cat_title+"-"+
					$("#devdef_tt").val(sel.title+"["+sel.name+"]") ;
					$("#devdef_id").val(sel.id) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
	return false;
}

function do_submit(cb)
{
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'<wbt:lang>pls_input_name</wbt:lang>') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'<wbt:lang>pls_input_title</wbt:lang>') ;
		return ;
	}
	var devdef_id = $("#devdef_id").val() ;
	if(devdef_id==null||devdef_id=="")
	{
		cb(false,'<wbt:lang>pls_select_dev</wbt:lang>') ;
		return ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	cb(true,{name:n,title:tt,devdef_id:devdef_id,desc:desc});
}

</script>
</html>