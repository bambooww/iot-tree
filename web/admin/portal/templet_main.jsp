<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%
	//List<Warehouse> whs = WareManager.getInstance().getHousesAll() ;
%><html>
<head>
        <jsp:include page="../head.jsp">
        <jsp:param value="true" name="simple"/>
        </jsp:include>
</head>
<style>
iframe
{
width:100%;height:100%;
overflow: hidden;
border:0px;
}
.ccc
{
position:absolute;left:0px;
top:0px;bottom:0px;
overflow:hidden;
margin:5px;
border:1px solid #cecece;
}
.top {position: absolute;top:0px;left:0px;width:100%;height:40px;background-color0: #f2f2f2;border-bottom: 1px solid #e6e6e6;}
.btm {position: absolute;top:45px;left:0px;width:100%;bottom: 0px;overflow: auto;}

.h_item {border:1px solid #5d6882;border-radius:5px;width:90%;height:40px;left:3%;position: relative;margin:3px;padding-top:10px;}
.h_item .ppt {position: absolute;top:8px;color:#333333;border:0px solid;text-overflow:ellipsis;white-space: nowrap;overflow: hidden;left:10px;}
.h_item .dt {position: absolute;top:25px;border:0px solid;font-size:10px;color:#a7ec21;padding-left:36px;line-height:20px;}
.h_item:hover {
	background-color: #aaaaaa;
}
.seled {background-color: #aaaaaa;}
.rounded-box {
position: absolute;left:0px;top:200px;
  display: inline-block;
  padding: 2px; 
  border: 2px solid #999999;
  border-radius: 5px; 
  background-color: #a0a0a0; 
  text-align: center; 
  line-height: 1.5;
  font-size: 14px; cursor:pointer;
}
</style>
<script>
dlg.dlg_top=true ;
</script>
<body style="overflow: hidden;">
<div class="ccc"  style="left:00px;top:0px;bottom:0px;width:300px;">
    <iframe id="if_list" name="if_list"  src="templet_list.jsp" style="width:100%;overflow: hidden;"></iframe>
</div>
<div class="ccc"  style="left:300px;top:0px;bottom:0px;right:0px;">
	<iframe id="if_detail" name="if_detail"  src="" style="width:100%;overflow: hidden;"></iframe>
</div>

<%-- 
<div class="ccc" id="left_panel" style="left:0%;top:0px;bottom:0px;width:250px;display:none;z-index: 10px;background-color: #ffffff" >
    <div class="top">

<table style="width:100%;height:100%;">
	<tr>
		<td style="width:150px;font-size:14px;">&nbsp;&nbsp;页面列表</td>
		<td style="width:100px;white-space: normal;">
<%
if(up.isAdministrator())
{
%><button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="add_house()" title="&nbsp;新增仓库"><i class="fa fa-plus"></i></button>
<%
}
%>
	<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="show_house_list(false)" title="&nbsp;隐藏列表"><i class="fa-solid fa-angle-left"></i></button>
	</tr>
</table>
</div>

</div>

<div class="ccc" id="dp_panel" style="left:0%;top:0px;bottom:0px;width:250px;display:none;z-index: 10px;background-color: #ffffff" >
<div class="btm" id="dp_list" style="top:0px;">
<iframe id="if_dp_list" src="" style="width:100%;height:100%;"></iframe>
</div>
<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="show_devpart_list(false)"
	title="&nbsp;隐藏列表" style="position:absolute;right:10px;top:5px;"><i class="fa-solid fa-angle-left"></i></button>
</div>

<div id="h_list_btn" class="rounded-box" onclick="show_house_list(true)">
  仓<br>库<br>列<br>表
</div>
<div id="h_list_btn" class="rounded-box" style="top:300px" onclick="show_devpart_list(true)">
  设<br>备<br>部<br>件
</div>
 --%>
</body>
<script type="text/javascript">


function on_templet_sel(uid)
{
	$("#if_detail").attr("src",`templet_detail.jsp?uid=\${uid}`)
}

function slide_toggle(obj,w)
{
	if(obj.attr('topm_show')=='1')
	{
		obj.animate({width: '0px', opacity: 'hide'}, 'normal',function(){ obj.hide();});
		obj.attr('topm_show',"0") ;
		return 0 ;
	}
	else
	{
		obj.animate({width: w, opacity: 'show'}, 'normal',function(){ obj.show();});
		obj.attr('topm_show',"1") ;
		return 1 ;
	}
}

function hide_toggle(obj)
{
	obj.hide();
	obj.attr('topm_show',"0") ;
}


</script>
</html>