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
	
// List<MNNode> nodes = MNManager.listRegisteredNodes() ;

%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="mn"/>
</jsp:include>
<style>

.cont
{
scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  overflow-x: hidden;
  overflow-y: auto;
  position:absolute;
  top:0px;bottom:30px;
  left:0px;
  width:180px;border:0px solid;
  border:0px solid;
  background-color: #f3f3f3;
}

.btm
{
	 position:absolute;
  height:30px;bottom:0px;
  left:0px;background-color:#f3f3f3;
  width:180px;border:0px solid;
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
#module_list
{
	//display: none ;
	position: absolute;
	bottom: 31px;
	width:180px;
	height:0px;
	z-index:20;
	background-color:#e0e0e0;
	border0:0px solid;
	box-sizing: border-box;
border: 1px #ced8e4 solid;
border-right: 0;
border-radius: 20px 20px 0px 0px ;
box-shadow: 0 0 10px 2px #4d5053; //#bdcee0;
}

#module_list #mlist_hd
{
	position:absolute;
  height:30px;top:0px;
  left:0px;
  //background-color:#aaaaaa;
  width:180px;
  border-top:0px solid #bbbbbb;
  padding-top: 8px;
	padding-left: 18px;
	font-size: 16px;
	font-weight: bold;
	color:#555555;
}
#module_list #mlist_bd
{
	position:absolute;
  bottom:0px;top:30px;
  left:0px;
  
  width:180px;
  border:0px solid red;
  text-align: center;
  scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  overflow-x: hidden;
  overflow-y: auto;
}

.nitem
{
	height: 30px;
	cursor: pointer;
	width:170px;
	left:5px;
	margin-top: 5px;
	margin-bottom: 5px;
	position: relative;
	border:0px solid;
}

.citem
{
	height: 30px;
	cursor: pointer;
	width:100%;
	left:0px;
	top:0px;
	border-top:1px solid #dddddd;
	position: relative;
	padding-top: 12px;
	padding-left: 20px;
	font-size: 16px;
	font-weight: bold;
	color:#555555;
}

.draw
{
	position: absolute;
	left:0px;top:0px;
	width:100%;height:100%;
	z-index: 10px;
}
.cov
{
	position: absolute;
	left:0px;top:0px;
	width:100%;height:100%;
	background-color: green;
	opacity: 0.0;
	z-index: 11px;
}

</style>
</head>
<body style="overflow:hidden;" >

<div class="cont">
<%
	JSONArray jarr_ns = new JSONArray() ;
JSONArray jarr_ms = new JSONArray() ;
for(MNCat cat:MNManager.listRegisteredCats())
{
	List<MNNode> nodes = cat.getNodes() ;
	List<MNModule> modules = cat.getModules() ;
%>
<div class="citem"  id="cat_<%=cat.getName()%>"  onclick="show_hiddle(this)" cat_n="<%=cat.getName()%>"><span id="cat_i_<%=cat.getName()%>"><i class="fa fa-angle-down"></i></span>  <%=cat.getTitle() %>	</div>
<div id="cat_list_<%=cat.getName()%>">
<%
	for(MNNode n:nodes)
	{
		JSONObject tmpjo = n.toListJO() ;
		jarr_ns.put(tmpjo) ;
		String sty = "" ;
		String fulltp = n.getTPFull() ;
%><div class="nitem"  id="n_<%=fulltp%>" tp_tp="node"  _tp="<%=fulltp%>" draggable='true' ondragstart='drag(event)'  title="<w:g>node</w:g> <%=n.getTPTitle()%>">
		<div class="draw" id="n_d_<%=fulltp%>"></div>
		<div class="cov"></div>
	</div>
<%
	}
	for(MNModule m:modules)
	{
		JSONObject tmpjo = m.toListJO() ;
		jarr_ms.put(tmpjo) ;
		String fulltp = m.getTPFull() ;
%><div class="nitem"  style="height:60px;" id="m_<%=fulltp%>"  tp_tp="module"  _tp="<%=fulltp%>" draggable='true' ondragstart='drag(event)'  title="<w:g>module</w:g> <%=m.getTPTitle()%>">
		<div class="draw" id="m_d_<%=fulltp%>"></div>
		<div class="cov"></div>
	</div>
<%
	}
%>
</div>
<%
}
%>
</div>
<div class="btm">
	<button onclick="show_hiddle_all(false)"><i class="fa fa-angle-double-up"></i></button>
	<button onclick="show_hiddle_all(true)"><i class="fa fa-angle-double-down"></i></button>&nbsp;&nbsp;&nbsp;
</div>
<div id="module_list" >
	<span id="mlist_hd"></span>
	<div id="mlist_bd"></div>
</div>
</body>
<script type="text/javascript">

var nodes = <%=jarr_ns%> ;
var modules = <%=jarr_ms%> ;

var draw_panels = [] ;
var m_panels=[] ;

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

function draw_nodes()
{
	for(let n of nodes)
	{
		let dp = draw_node(n);
		if(dp)
			draw_panels.push(dp) ;
	}
	for(let m of modules)
	{
		let dp = draw_module(m);
		if(dp)
			draw_panels.push(dp) ;
	}
}


function draw_node(n)
{
	let h = mn.view.DINode.calDrawHeight(n);
	let ele = document.getElementById("n_"+n._tp);
	if(!ele) return null;
	$(ele).css("height",h+"px") ;
	
	let din = new mn.view.DINode(n,{});
	din.in_list=true ;
	var draw_lay = new mn.DrawLayer();
	draw_lay.addItem(din,false);
	var panel = new mn.DrawPanel("n_d_"+n._tp,{});
	var draw_panel = new mn.DrawPanelDiv("",{layer:draw_lay,panel:panel}) ;
	var rect = draw_lay.getShowItemsRect();
	draw_panel["w"] = rect.w;
	draw_panel["h"] = rect.h;
	draw_lay.ajustDrawFit();
	return draw_panel ;
}

function draw_module(m)
{
	let ele = document.getElementById("m_"+m._tp)
	if(!ele) return null ;
	let din = new mn.view.DIModule(m,{});
	din.in_list=true ;
	var draw_lay = new mn.DrawLayer();
	draw_lay.addItem(din,false);
	var panel = new mn.DrawPanel("m_d_"+m._tp,{});
	var draw_panel = new mn.DrawPanelDiv("",{layer:draw_lay,panel:panel}) ;
	var rect = draw_lay.getShowItemsRect();
	draw_panel["w"] = rect.w;
	draw_panel["h"] = rect.h;
	draw_lay.ajustDrawFit();
	
	return draw_panel ;
	
}


draw_nodes() ;

function drag(ev)
{
	var tar = $(ev.target);
	let x = ev.offsetX ;
	let y = ev.offsetY ;
	let w = tar.width() ;
	let h = tar.height() ;
	let tp = tar.attr("_tp") ;
	let tptp = tar.attr("tp_tp") ;
	let moduleid = tar.attr("moduleid") ||"";
	var r = {_val:{tp:tp,moduleid:moduleid,x_ratio:x/w,y_ratio:y/h,x_offset:x,y_offset:y},_tp:tptp};
	mn.util.setDragEventData(ev,r);
}

function redraw_list()
{
	for(let dp of draw_panels)
	{
		dp.getLayer().ajustDrawFit();
	}
	
	for(let dp of m_panels)
	{
		dp.getLayer().ajustDrawFit();
	}
}

document.addEventListener('visibilitychange', function() {
    if (document.visibilityState === 'visible')
    {
        redraw_list() ;
    } else {
       
    }
});

var last_module_id = null ;

function show_by_module(prjid,netid,moduleid,tt)
{
	//console.log(moduleid)
	if(last_module_id==moduleid)
		return ;
	
	last_module_id = moduleid ;
	if(!moduleid)
	{//hid
		//$("#module_list").css("height","0px") ;
		slide_toggle($("#module_list"),0);
		m_panels=[];
		return ;
	}
	send_ajax("mn_ajax.jsp",{op:"module_list_nodes",prjid:prjid,netid:netid,moduleid:moduleid},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let obs ;
		eval("obs="+ret) ;
		show_m_ns(moduleid,tt,obs) ;
	}) ;
}

function show_m_ns(moduleid,tt,ns)
{
	let tmps = "" ;
	for(let n of ns)
	{
		let tp = n._tp ;
		tmps += `<div class="nitem"  id="n_\${tp}" tp_tp="node" moduleid="\${moduleid}"  _tp="\${tp}" draggable='true' ondragstart='drag(event)'  title=" \${n.tpt}">
			<div class="draw" id="n_d_\${tp}"></div>
			<div class="cov"></div>
		</div>`
	}
	$("#mlist_hd").html(tt) ;
	$("#mlist_bd").html(tmps) ;
	slide_toggle($("#module_list"),280);
	let dps =[];
	for(let n of ns)
	{
		let dp = draw_node(n);
		if(dp)
			dps.push(dp) ;
	}
	m_panels=dps;
	//$("#module_list").css("height","280px") ;
	
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