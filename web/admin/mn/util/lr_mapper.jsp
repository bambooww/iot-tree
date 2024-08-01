<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id"))
		return ;
	
	String container_id = request.getParameter("container_id") ;
	MNManager mgr= MNManager.getInstanceByContainerId(container_id) ;
	if(mgr==null)
	{
		out.println("no MNManaager found") ;
		return ;
	}
	IMNContainer cont = mgr.getBelongTo() ;
	if(!(cont instanceof IMNContTagListMapper))
	{
		out.println("no IMNContTagListMapper found") ;
		return ;
	}
	
	IMNContTagListMapper cont_taglist = (IMNContTagListMapper)cont ;
%>
<html>
<head>
<title>Left Right Mapper</title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,600);
</script>
<style>
.left
{
	position: absolute;
	left:0px;
	width:40%;
	bottom: 0px;
	top:0px;
	border:1px solid #ececev;
}
.left .tt
{
	top:2px;
	height:30px;
}

.left .c
{
	position: absolute;
	top:20px;
	width:100%;
	overflow-y: auto;
	bottom: 0px;
}

.c::-webkit-scrollbar {
  	display: none;
}

.mid
{
	position: absolute;
	top:20px;
	left:40%;
	right:35%;
	bottom: 0px;
}

.right
{
	position: absolute;
	right:0px;
	width:35%;
	bottom: 0px;
	top:0px;
	border:1px solid #ececev;
}
.right .tt
{
	top:2px;
	height:30px;
}
.right .c
{
	position: absolute;
	top:20px;
	width:100%;
	overflow-y: auto;
	bottom: 0px;
}

.l_item
{
	position: relative;
	left:5%;
	width:95%;
	margin-top: 3px;
	height:22px;
	border:1px solid;
	white-space: nowrap;
}

.l_item:hover {
	background-color: #dddddd;
}

.r_item
{
	position: relative;
	left:0%;
	width:95%;
	margin-top: 3px;
	height:22px;
	color:#777777;
	border:1px solid #dddddd;
	white-space: nowrap;
}

.r_item:hover {
	background-color: #dddddd;
}

.seled
{
	background-color: #c1c1c1;
}
</style>
</head>
<body>

<div class="left">
	<div class="tt" id="left_tt"></div>
	<div class="c" id="left_c"></div>
</div>
<div class="mid" id="mid">
<button  id="conn_btn" style="z-index:10;position: absolute;display:none;" onclick="set_or_unset_conn()">Set Map</button>
</div>
<div class="right">
	<div class="tt" id="">
		<select id="taglist_cat" onchange="on_taglist_cat_chg()">
		<%
		for(NameTitle nt:cont_taglist.getMNContTagListCatTitles())
		{
%><option value="<%=nt.getName() %>"><%=nt.getTitle() %></option>
<%
		}
		%>
		</select>
	<span id="right_tt"></span></div>
	<div class="c" id="right_c"></div>
</div>

</body>
<script type="text/javascript">

var container_id = "<%=container_id%>" ;

var left_tt =  dlg.get_opener_opt("left_tt") ;
var right_tt =  dlg.get_opener_opt("right_tt") ;
var left_list = dlg.get_opener_opt("left_list") ;
var right_list = [];//dlg.get_opener_opt("right_list") ;
var taglist_cat = dlg.get_opener_opt("taglist_cat") ;
if(taglist_cat)
{
	$("#taglist_cat").val(taglist_cat) ;
}

var mapped = dlg.get_opener_opt("mapped")||{} ;

var cur_left_n = null;
var cur_right_n = null ;

var m_ln = {} ;

var can_cxt = null ;
var can = null ;
var mid = null ;

function init()
{
	can_cxt = document.createElement('canvas').getContext('2d');
	can = $(can_cxt.canvas);
	
	can.css("position", "relative");
	can.css("left", "0px");
	can.css("top", "0px");
	can.css("display","");
	mid= $("#mid");
	can.attr('width', mid[0].offsetWidth) ;
	can.attr('height', mid[0].offsetHeight-5) ;
	//can.attr('height', "100%") ;
	mid.append(can);
	mid.resize(()=>{
		var w = mid[0].offsetWidth;
		var h = mid[0].offsetHeight;
		//console.log(w,h)
		can.attr('width', w) ;
		can.attr('height', h-5) ;
		//this.redraw(false,true,false);
		redraw_lns();
	});
	
	$("#left_c").scroll(()=>{
		redraw_lns();
		update_conn_btn();
	});
	
	$("#right_c").scroll(()=>{
		redraw_lns();
		update_conn_btn();
	});
}

init();

function get_left_item_xy(n)
{
	let ele = $(document.getElementById("li_"+n)) ;
	if(!ele || ele.length<=0)
		return null ;
	let y = ele.offset().top - ele.parent().offset().top + ele.height()/2 ;
	return {x:0,y:y} ;
}

function get_right_item_xy(n)
{
	let ele = $(document.getElementById("ri_"+n)) ;
	if(!ele || ele.length<=0)
		return null ;
	let y = ele.offset().top - ele.parent().offset().top+ele.height()/2 ;
	return {x:mid[0].offsetWidth,y:y} ;
}

function draw_map_lns()
{
	for(let l_n in mapped)
	{
		let r_n = mapped[l_n] ;
		let pt1 = get_left_item_xy(l_n);
		let pt2 = get_right_item_xy(r_n);
		if(pt1 && pt2)
		{
			can_cxt.beginPath() ;
			can_cxt.moveTo(pt1.x,pt1.y);
			can_cxt.lineTo(pt2.x,pt2.y);
			can_cxt.stroke() ;
		}
	}
}

function redraw_lns()
{
	this.can[0].width = this.can[0].width;
	draw_map_lns() ;
}

function update_left_list()
{
	let tmps="" ;
	$("#left_tt").html(left_tt||"Left");
	let tmpdiv = document.createElement("div") ;
	
	if(left_list)
	{
		for(let n of left_list)
		{
			let mpv = mapped[n]||"" ;
			let tt = "" ;
			if(mpv)
			{
				let ele = $(document.getElementById("ri_"+mpv)) ;
				tt = ele.attr("title")||"" ;
				tmpdiv.innerText = tt ;
				tt = tmpdiv.innerHTML ;
				mpv = `<span style='color:green;'> - \${mpv} \${tt}</span>` ;
			}
			tmps += `<div class="l_item" onclick="on_left_clk(this)" id="li_\${n}" nn="\${n}" title="\${tt}">\${n} \${mpv}</div>` ;
		}
	}
	$("#left_c").html(tmps) ;
}

function update_right_list()
{
	let tmps="" ;

	$("#right_tt").html(right_tt||"Right");
	let tmpdiv = document.createElement("div") ;
	tmps="" ;
	if(right_list)
	{
		for(let n of right_list)
		{
			if(typeof(n)=='string')
				tmps += `<div class="r_item" onclick="on_right_clk(this)" id="ri_\${n}" nn="\${n}" ondblclick="set_or_unset_conn()">\${n}</div>` ;
			else
			{
				let tt = n.t ;
				let k = tt.lastIndexOf("/") ;
				if(k>0)
					tt = tt.substring(k+1) ;
				tmpdiv.innerText = tt ;
				tt = tmpdiv.innerHTML ;
				tmps += `<div class="r_item" onclick="on_right_clk(this)" id="ri_\${n.n}" nn="\${n.n}" title="\${n.t}" ondblclick="set_or_unset_conn()">\${n.n} \${tt}</div>` ;
			}
				
		}
	}
	$("#right_c").html(tmps) ;
}

function update_sel_left()
{
	$(".l_item").each(function(){
		let thiz = $(this);
		let n = thiz.attr("nn");
		if(n==cur_left_n)
			thiz.addClass("seled") ;
		else
			thiz.removeClass("seled") ;
	}) ;
}

function update_sel_right()
{
	$(".r_item").each(function(){
		let thiz = $(this);
		let n = thiz.attr("nn");
		if(n==cur_right_n)
			thiz.addClass("seled") ;
		else
			thiz.removeClass("seled") ;
	}) ;
}

function update_conn_btn()
{
	let connob = $("#conn_btn") ;
	if(!cur_left_n || !cur_right_n)
	{
		connob.css("display","none") ;
		return ;
	}
	
	let pt1 = get_left_item_xy(cur_left_n);
	let pt2 = get_right_item_xy(cur_right_n);
	if(!pt1 || !pt2)
	{
		connob.css("display","none") ;
		return ;
	}
	
	let x = (pt1.x+pt2.x)/2 ;
	let y = (pt1.y+pt2.y)/2 ;
	let mpd = mapped[cur_left_n] ;
	connob.html(mpd==cur_right_n?"Unset":"Set") ;
	
	
	connob.css("left",x+"px");
	connob.css("top",y+"px");
	connob.css("display","") ;
}

function on_left_clk(ele)
{
	let ob = $(ele) ;
	cur_left_n = ob.attr("nn") ;
	update_sel_left() ;
	
	update_conn_btn();
}

function on_right_clk(ele)
{
	let ob = $(ele) ;
	cur_right_n = ob.attr("nn") ;
	update_sel_right() ;
	
	update_conn_btn();
}

function set_or_unset_conn()
{
	if(!cur_left_n || !cur_right_n)
	{
		return ;
	}

	let mpd = mapped[cur_left_n] ;
	if(mpd==cur_right_n)
		delete mapped[cur_left_n];
	else
		mapped[cur_left_n] = cur_right_n ;
	
	update_left_list();
	update_sel_left() ;
	redraw_lns();
	update_conn_btn();
}



function update_ui()
{
	update_left_list() ;
	update_right_list() ;
	redraw_lns();
}

update_ui(); 

function on_taglist_cat_chg()
{
	let cat = $("#taglist_cat").val() ;
	taglist_cat = cat ;
	send_ajax("lr_mapper_ajax.jsp",{op:"cat_tag_list",container_id:container_id,taglist_cat:cat},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")<0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let nts  =null;
		eval("nts="+ret) ;
		right_list = nts ;
		
		update_right_list() ;
		update_left_list();
		redraw_lns();
	}) ;
}

on_taglist_cat_chg();

function get_mapped()
{
	return mapped;
}

function get_taglist_cat()
{
	return taglist_cat ;
}

</script>
</html>