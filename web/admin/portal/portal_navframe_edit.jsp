<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%@ taglib uri="wb_tag" prefix="w"%><%
	String prjid = request.getParameter("prjid") ;
	String nf_id = request.getParameter("nf_id") ;
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

.rrr
{
position:absolute;right:0px;
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

.rrrr-box {
position: absolute;right:0px;top:200px;
  display: inline-block;
  padding: 2px; 
  border: 2px solid #999999;
  border-radius: 5px; 
  background-color: #a0a0a0; 
  text-align: center; 
  line-height: 1.5;
  font-size: 14px; cursor:pointer;
}

#right_panel {right:0%;top:0px;bottom:0px;z-index: 10px;background-color: #ffffff;
box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
	transition: box-shadow 0.3s, transform 0.3s;}

</style>
<script>
dlg.dlg_top=true ;
</script>
<body style="overflow: hidden;">

<div class="ccc"  style="left:0px;top:0px;bottom:0px;right:0px;">
	<iframe id="if_detail" name="if_detail"  src="" style="width:100%;overflow: hidden;"></iframe>
</div>


<div class="ccc" id="left_panel" style="left:0%;top:0px;bottom:0px;width:550px;display:none;z-index: 10px;background-color: #ffffff" >


</div>
<div class="rrr" id="right_panel" style="" topm_show="0">
    <iframe id="if_item_list" name="if_item_list"  src="portal_navframe_right.jsp?prjid=<%=prjid %>&nf_id=<%=nf_id %>" style="width:100%;overflow: hidden;"></iframe>
</div>
<button id="topr_show_hd" style="position:absolute;top:5px;right:5px;" class="layui-btn layui-btn-sm layui-btn-primary" onclick="show_right_setup()" title="&nbsp;"><i class="fa-solid fa-angle-right"></i></button>

</body>
<script type="text/javascript">

function on_page_preview(u)
{
	$("#if_detail").attr("src",u)
}

function update_detail_show()
{
	$("#if_detail")[0].contentWindow.location.reload();
}

function show_right_setup()
{
	let obj = $('#right_panel') ;
	if(obj.attr('topm_show')=='1')
	{
		obj.animate({width: '0px', opacity: 'hide'}, 'normal',function(){ obj.hide();});
		obj.attr('topm_show',"0") ;
		$("#topr_show_hd").html(`<i class="fa-solid fa-angle-left"></i>`)
		return 0 ;
	}
	else
	{
		obj.animate({width: "650px", opacity: 'show'}, 'normal',function(){ obj.show();});
		obj.attr('topm_show',"1") ;
		$("#topr_show_hd").html(`<i class="fa-solid fa-angle-right"></i>`)
		return 1 ;
	}
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

show_right_setup()
</script>
</html>