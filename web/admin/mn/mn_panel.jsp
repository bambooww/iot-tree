<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","netid"))
			return ;
	String prjid = request.getParameter("prjid");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager mnm= MNManager.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	MNBase item = null;
	if(Convert.isNotNullEmpty(itemid))
	{
		item = net.getItemById(itemid) ;
		if(item==null)
		{
			out.print("no item found") ;
			return ;
		}
	}
	
	String net_en_chk = net.isEnable()?"checked":"" ;
	//String tp = item.getTPFull() ;
	//String title = item.getTitle() ;

%><html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="mn"/>
</jsp:include>
<style>

.cont
{
scrollbar-width0: none; /* firefox */
  -ms-overflow-style0: none; /* IE 10+ */
  overflow-x: hidden;
  overflow-y: auto;
  position:absolute;
  top:0px;bottom:30px;
  left:0px;
  width:100%;border:0px solid;
  border:0px solid;
  background-color: #ffffff;
}

.btm
{
	 position:absolute;
  height:30px;bottom:0px;
  left:0px;background-color:#f3f3f3;
  width:100%;border:0px solid;
  border-top:1px solid #bbbbbb;
  text-align: right;
  z-index:30;
}

.btm button
{
	width:24px;height:24px;
	margin-top:3px;
	color:#888888;
}

.citem
{
	height: 30px;
	cursor: pointer;
	width:100%;
	left:0px;
	top:5px;
	border-top:0px solid #dddddd;
	position: relative;
	padding-top: 12px;
	padding-left: 20px;
	font-size: 16px;
	font-weight: bold;
	color:#555555;
}

</style>
</head>
<body style="overflow:hidden;" >

<div class="cont">

<%
	if(true)
	{
		String catn = "flow" ;
%>
<div class="citem"  id="cat_<%=catn%>"  onclick="show_hiddle(this)" cat_n="<%=catn%>"><span id="cat_i_<%=catn%>"><i class="fa fa-angle-down"></i></span> <w:g>flow</w:g></div>
<div id="cat_list_<%=catn%>">
<form class="layui-form"  onsubmit="return false;">
<div class="layui-form-item">
    <label class="layui-form-label">Between:</label>
    <div class="layui-input-inline" style="width:100px;">
    	<input type="time" class="layui-input" id="between_s" />
    </div>
    <div class="layui-form-mid"> -- </div>
    <div class="layui-input-inline" style="width:100px;">
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="flow_enable"  <%=net_en_chk %>/> Enable
    </div>
</div>
</form>
</div>
<%
	}

	

if(item!=null)
{
	String catn="rt_vars";
%>

<div class="citem"  id="cat_<%=catn%>"  onclick="show_hiddle(this)" cat_n="<%=catn%>"><span id="cat_i_<%=catn%>"><i class="fa fa-angle-down"></i></span> <w:g>rt_vars</w:g></div>
<div id="cat_list_<%=catn%>">
当前节点变量
<table>
 
</table>
</div>
<%
}

if(item!=null)
	{
		String catn="pm";
%>

<div class="citem"  id="cat_<%=catn%>"  onclick="show_hiddle(this)" cat_n="<%=catn%>"><span id="cat_i_<%=catn%>"><i class="fa fa-angle-down"></i></span> <w:g>pm_st</w:g></div>
<div id="cat_list_<%=catn%>">
	<table>
	</table>
</div>
<%
	}
%>

</div>
<div class="btm">
	<button onclick="show_hiddle_all(false)"><i class="fa fa-angle-double-up"></i></button>
	<button onclick="show_hiddle_all(true)"><i class="fa fa-angle-double-down"></i></button>&nbsp;&nbsp;&nbsp;
</div>

</body>
<script type="text/javascript">
var form ;

layui.use('form', function(){
	  form = layui.form;
	  form.render();
	  
});

function show_hiddle(ele)
{
	let ob  = $(ele) ;
	let catn = ob.attr("cat_n") ;
	let catob = $("#cat_list_"+catn) ;
	if(catob.css("display")!="none")
	{
		catob.css("display","none");
		$("#cat_i_"+catn).html(`<i class="fa fa-angle-right"></i>`);
	}
	else
	{
		catob.css("display","");
		$("#cat_i_"+catn).html(`<i class="fa fa-angle-down"></i>`);
	}
}

function show_hiddle_all(b_show)
{
	$(".citem").each(function(){
		let ob  = $(this) ;
		let catn = ob.attr("cat_n") ;
		let catob = $("#cat_list_"+catn) ;
		if(b_show)
		{
			catob.css("display","");
			$("#cat_i_"+catn).html(`<i class="fa fa-angle-down"></i>`);
		}
		else
		{
			catob.css("display","none");
			$("#cat_i_"+catn).html(`<i class="fa fa-angle-right"></i>`);
		}
	});
}

function slide_toggle(obj,h)
{
	if(h==0) //obj.attr('topm_show')=='1')
	{
		obj.animate({height: '0px', opacity: 'hide'}, 'normal',function(){ obj.hide();});
		//obj.attr('topm_show',"0") ;
		return 0 ;
	}
	else
	{
		obj.animate({height: h+"px", opacity: 'show'}, 'normal',function(){ obj.show();});
		//obj.attr('topm_show',"1") ;
		return 1 ;
	}
}

</script>
</html>                                                                                                                                                                                                                            