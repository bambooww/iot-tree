<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
boolean hide_d = "true".equals(request.getParameter("hide_d")) ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(450,350);
</script>
</head>
<body>
<form class="layui-form" action="">
	<div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>icon</wbt:g>:</label>
    <div class="layui-input-inline" style="width:200px;height:45px;">
    	<div style="border:1px solid #aaa;width:40px;height:40px;font-size: 30px;" onclick="sel_icon_color()"><i class="fa" id="icon_show"></i></div>
      <input type="hidden" id="icon" name="icon" value=""  >
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang>:</label>
    <div class="layui-input-inline"  style="width:300px;">
      <input type="text" id="title" name="title" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var input = dlg.get_opener_opt("input") ;
if(input)
{
	$("#title").val(input.title||"");
	$("#icon").val(input.icon||"");
}
var form ;
layui.use('form', function(){
	  form = layui.form;
	  form.render() ;
});
	
function win_close()
{
	dlg.close(0);
}


function sel_icon_color()
{
	let pm = null;// {color:color,icon:icon} ;
	//console.log(pm) ;
	dlg.open("../util/icon_color_selector.jsp",
			{title:"<wbt:g>select,icon,color</wbt:g>",pm:pm},
			['<wbt:g>ok</wbt:g>','<wbt:g>close</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						console.log(ret) ;
						let ico = "&#x"+ret.icon;
						$("#icon").val(ico)
						$("#icon_show").html(ico) ;
						//update_ui();
						dlg.close();
					}) ;
					
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function do_submit(cb)
{
	let icon = $('#icon').val();
	if(!icon)
	{
		cb(false,"<wbt:g>pls,input,icon</wbt:g>") ;return ;
	}
	let tt = $('#title').val();
	if(!tt && b_title_need)
	{
		cb(false,"<wbt:g>pls,input,title</wbt:g>") ;return ;
	}
	cb(true,{title:tt,icon:icon});
}

</script>
</html>                                                                                                                                                                                                                            