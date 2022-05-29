<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%

if(!Convert.checkReqEmpty(request, out, "devpath"))
	return ;
String devpath = request.getParameter("devpath") ;
UADev dev = (UADev)UAUtil.findNodeByPath(devpath);
if(dev==null)
{
	out.print("no device found");
	return ;
}

String name = dev.getName() ;
String title = dev.getTitle() ;
UACh ch = dev.getBelongTo() ;
DevDriver dd = ch.getDriver() ;
String drv = "" ;

/*
DevDriver dd = dev.getBelongTo().getDriver() ;
if(dd==null)
{
	out.print("no driver found");
	return ;
}

	String drv = dd.getName() ;
	*/
	boolean hide_drv = "true".equals(request.getParameter("hide_drv")) ;
	String drv_tt = "" ;
	if(dd!=null)
	{
		drv = dd.getName() ;
		drv_tt = dd.getTitle();
	}
%><html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
}

select option
{
font-size: 12px;
}

.oc-toolbar .toolbarbtn
{
width:40px;height:40px;margin: 5px;
font-size: 13px;
background-color: #eeeeee
}

.rmenu_item:hover {
	background-color: #373737;
}



</style>
<body marginwidth="0" marginheight="0">
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-block">
      <input type="text" id="devdef_name" name="devdef_name" value="<%=name %>"  class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
    <div class="layui-input-block">
      <input type="text" id="devdef_title" name="devdef_title" value="<%=title %>"  autocomplete="off" class="layui-input">
    </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">Library</label>
    <div class="layui-input-block" >
      <input type="text" name="libcat_title" id="libcat_title" value="" onclick="select_libcat()"  class="layui-input"/>
      <input type="hidden" name="libid" id="libid" value="" />
      <input type="hidden" name="catid" id="catid" value="" />
    </div>
  </div>
 </form>

<script>
var hide_drv = <%=hide_drv%>
var cur_drv = "<%=drv%>" ;
var cur_drv_tt = "<%=drv_tt%>" ;
var cur_catid = null ;

function select_libcat()
{
	var sel_libid = $("#libid").val();
	var sel_catid = $("#catid").val();
	dlg.open("dev_main.jsp?dlg=true&sel_libcat=true&sel_libid="+sel_libid+"&sel_catid="+sel_catid,
			{title:"Select Category"},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					if(!dlgw.check_selected_ok())
					{
						dlg.msg("invalid selection") ;
						return;
					}
					
					var ret = dlgw.get_selected() ;
					$("#libid").val(ret.libid);
					$("#catid").val(ret.catid);
					$("#libcat_title").val(ret.libcat_tt);
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
	var libid = $("#libid").val() ;
	var catid = $("#catid").val() ;
	if(!libid==null||!catid)
	{
		dlg.msg("please select input library");
		return ;
	}
	var n = $("#devdef_name").val() ;
	var t = $("#devdef_title").val() ;
	if(n==null||n=='')
	{
		dlg.msg("please input device name") ;
		return ;
	}
	if(t==null||t=='')
		t= n ;
	var pm = {
			type : 'post',
			url : "./devdef_ajax.jsp",
			data :{op:"chk_name",libid:libid,catid:catid,name:n}
		};
	$.ajax(pm).done((ret)=>{
		if(typeof(ret)=='string')
		{
			if(ret=="ok")
			{
				dlg.confirm('device with name = '+n+' already exists, do you want to overwrite it?',{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
							    {
							cb(true,{libid:libid,catid:catid,name:n,title:t});
						 });
			}
			else
			{
				cb(true,{libid:libid,catid:catid,name:n,title:t});
			}
		}
	});
}


</script>

</body>
</html>