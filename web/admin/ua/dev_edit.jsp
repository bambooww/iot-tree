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
	
//	if(!ch.hasDriver())
//	{
//		out.print("Channel has no Device Driver") ;
//		return ;
//	}

	String drv_name = "";
	DevDriver drv = ch.getDriver() ;
	if(drv!=null)
	{
		if(!ch.isDriverFit())
			out.print("Channel Driver is not fit") ;
		drv_name = drv.getName() ;
		//return ;
	}
	
	String name = "" ;
	String title = "" ;
	String desc = "" ;
	DevDef dd = null ;
	String libid = "" ;
	String catid = "" ;
	String devid = "" ;
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
			devid = dd.getId() ;
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
      <input type="hidden" id="libid" name="libid"  value="<%=libid%>"/>
      <input type="hidden" id="catid" name="catid"  value="<%=catid%>"/>
      <input type="hidden" id="devid" name="devid"  value="<%=devid%>"/>
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
	var libid = $("#libid").val() ;
	var catid = $("#catid").val() ;
	var devid = $("#devid").val() ;
	dlg.open_win("../dev/dev_main.jsp?dlg=true&drv="+drv_name+"&sel_libcat=true&sel_dev=true"+
			"&sel_libid="+libid+"&sel_catid="+catid+"&sel_devid="+devid,
			{title:"Select Device in Library",w:'1000',h:'560'},
			['<wbt:lang>ok</wbt:lang>','No choice','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					if(!dlgw.check_selected_ok())
					{
						dlg.msg("")
						return ;
					}
					var sel = dlgw.get_selected() ;
					if(sel==null)
					{
						dlg.msg("please select device") ;
						return ;
					}//sel.cat_title+"-"+
					$("#devdef_tt").val(sel.dev_tt+"["+sel.dev_n+"]") ;
					$("#libid").val(sel.libid) ;
					$("#catid").val(sel.catid) ;
					$("#devid").val(sel.devid) ;
					dlg.close();
				},
				function(dlgw)
				{
					$("#devdef_tt").val("") ;
					$("#devdef_id").val("") ;
					$("#libid").val("") ;
					$("#catid").val("") ;
					$("#devid").val("") ;
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
		//cb(false,'<wbt:lang>pls_input_title</wbt:lang>') ;
		//return ;
		tt = n ;
	}
	var libid = $("#libid").val() ;
	var catid = $("#catid").val() ;
	var devid = $("#devid").val() ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	cb(true,{name:n,title:tt,libid:libid,catid:catid,devid:devid,desc:desc});
}

</script>
</html>