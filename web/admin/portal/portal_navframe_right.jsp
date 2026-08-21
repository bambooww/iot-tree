<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%@ taglib uri="wb_tag" prefix="w"%><%! 

%><%
if(!Convert.checkReqEmpty(request, out, "prjid","nf_id"))
	return ;
String prjid = request.getParameter("prjid") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
PortalManager pm = prj.getPortalManager() ;
String nf_id = request.getParameter("nf_id") ;
NavFrame navf = NavFrame.getNavFrame(prjid,nf_id) ;
if(navf==null)
{
	out.print("no NavFrame found") ;
	return ;
}
JSONArray navs_jarr = navf.getNavNodeInssJArr() ;

List<UAHmi> hmis = prj.listHmiNodesAll() ;
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
    <style>
.layui-form-label{
    width: 100px;
}
.layui-input-block {
    margin-left: 140px;
    min-height: 36px;
    width:240px;
}
.layui-form-item {
    margin-bottom: 5px;
}
  .layui-table {
  margin:0px;
    font-size: 12px; 
    line-height0: 1.2; 
  }
  .layui-table th, .layui-table td {
    padding: 1px 2px;
  }
    .layui-table-view th, .layui-table-view td {
    padding: 1px 2px;
  }
  .layui-tab-content {
    padding: 1px;
}
  
 .cat {margin:5px;position: relative;border:1px solid #ccc;height:35px;min-width:200px;border-radius:3px;cursor:pointer;display:inline-block;}
 .cat:hover {background-color: #ccc;}
 .cat .t {position: absolute;left:5px;top:3px;font-size: 14px;font-weight: bold;}
 .cat .n {position: absolute;left:8px;bottom:1px;font-size: 12px;}
 
 .app {background-color: #ccc;}
 .nav {margin-left: 20px;border:2px solid #ccc;margin-top:3px;margin-right:20px;height:20px;line-height:20px;}
 .ins_nav1 {cursor:pointer;}
 .ins_nav1:hover {background:#aaa;}
 .nav .op {float: right;}
 .nav .op button {width:30px;}
 .ins {margin-left:5px;margin-right:10px;}
 .ins .op button {width:20px;}

 .nav_left {position:absolute ;left:0px;top:175px;bottom:0px;width:50%;border:1px solid #ccc;}
 .nav_right {position:absolute;right:0px;top:175px;bottom:0px;width:50%;border-top:1px solid #ccc;}

.navs {position:absolute ;left:0px;top:25px;bottom:0px;width:100%;border:1px solid #ccc;overflow-y:auto;}
.nav_top {background-color: #4d87b5;height:25px;line-height: 25px;color:#eee;}
.nav_top button {width:30px;}
.logo_c {position: absolute;top:45px;right:10px;border:1px solid #ccc;width:120px;height:80px;cursor:pointer;}
.logo_c img {width:75px;height:75px;}
.pics {overflow-y:auto;}
.pic_item {border:0px solid;text-align: center;margin: 5px;position: relative;}
.pic_item img {width:100px;height:100px;border:1px solid #ececec;}
.colicon {cursor: pointer;}
.sel {border:2px solid blue;}
    </style>
</head>
<body  style="overflow: hidden;">
<form class="layui-form"  onsubmit="return false;" >
<table style="width:100%;height:40px;border-bottom: 1px solid #e6e6e6;">
	<tr>
		<td style="width:150px;padding-left:5px;font-weight: bold;"><w:g>nav_frame</w:g> - <span id="top_tt"><%=navf.getTitle() %></span></td>
		<td style="text-align: left;padding-right:5px;width:250px;border:0px solid">
		<button id="btn_save_detail" class="layui-btn layui-btn-sm layui-btn-primary" onclick="save_detail()" ><i class="fa fa-save"></i></button>
		<button id="btn_open_url" class="layui-btn layui-btn-sm layui-btn-primary" onclick="show_page()" ><i class="fa-regular fa-paper-plane"></i></button>
		</td>
		
	</tr>
</table>
  <div class="layui-form-item" id="">
    <label class="layui-form-label"><w:g>layout,temp</w:g>:</label>
    <div class="layui-input-inline"  style="width:200px;">
      <select id="layout" class="layui-input" lay-filter="layout">
      	<option value=""> --- </option>
      	<option value="default">default</option>
      </select>
  </div>
  <div class="layui-form-item" id="">
    <label class="layui-form-label"><w:g>sys,title</w:g>:</label>
    <div class="layui-input-inline"  style="width:200px;">
      <input type="text" id="sys_t" value="<%=navf.getSysTitle()%>" class="layui-input"/>
    </div>
  </div>
</div>
<div class="logo_c" onclick="change_logo()">
	<img id="img_logo" src="/portal__logo.jsp?prjid=<%=prjid %>&nf_id=<%=nf_id %>" />
</div>
<input type="file" id='add_file' onchange="add_file_onchg()" name="file" style="left:-9999px;position:absolute;" 
	accept=".png, .jpg, .jpeg"/>
</form>

<div class="nav_left" >
	<div class="nav_top"><w:g>nav,setup,list</w:g> <button onclick="add_nav1()" title="add new level 1"><i class="fa fa-plus"></i> 1</button></div>
	<div class="navs" id="navs">
	
	</div>
</div>
<div class="nav_right">
<div id="nav_right_tab" class="layui-tab layui-tab-brief"  lay-filter="tab_left_btm_ext" style="width:100%;height:500px">
  <ul class="layui-tab-title">
    <li class="layui-this">App</li>
	<li >HMI</li>
    <li >Page</li>
  </ul>
  <div class="layui-tab-content" style="position:relative;bottom:0px;height:100%">
  	<div class="layui-tab-item layui-show tab_bd" style="position:relative;top:0px;bottom:0px;width:99%;height:100%">
      <div class="navs" id="bsel_navs" style="top:0px">
<%
JSONArray tobesel_jarr = new JSONArray() ;
for(NavApp napp:NavApp.listNavAppAll())
{
%><div class="app"><%=napp.getTitle()%></div><%
	for(NavNode nnode:napp.getChildNodes())
	{
		tobesel_jarr.put(nnode.toNavJO()) ;
		String uid = nnode.getNavUID() ;
		String tmpurl = nnode.getUrl() ;
		
%><div class='nav'><i class="<%=nnode.getIcon()%>"></i> <%=nnode.getTitle() %>
	<span class='op'>
<%
	if(Convert.isNotNullEmpty(tmpurl))
	{
%><button onclick="add_to_nav2('<%=uid%>')" title="add to level 2"><i class="fa fa-angle-left"></i>2</button><%
	}
%>
	<button onclick="add_to_nav1('<%=uid%>',true)" title="add to level 1"><i class="fa fa-angle-left"></i>1</button>
	</span>
</div>
<%
		for(NavNode nav2:nnode.getChildNodes())
		{
			tmpurl = nav2.getUrl() ;
			String uid2 = nav2.getNavUID() ;
%><div class='nav' style="margin-left:30px;"><i class="<%=nav2.getIcon()%>"></i> <%=nav2.getTitle() %>
<span class='op'>
	<%
	if(Convert.isNotNullEmpty(tmpurl))
	{
%><button onclick="add_to_nav2('<%=uid2%>')" title="add to level 2"><i class="fa fa-angle-left"></i>2</button>
<%}
%>
	<button onclick="add_to_nav1('<%=uid2%>',false)" title="add to level 1"><i class="fa fa-angle-left"></i>1</button>
	</span>
</div>
<%
		}
	}
}
%>
</div>
	</div>

	<div class="layui-tab-item tab_bd"  style="position:relative;top:0px;bottom:0px;width:100%;height:210px">
      <div class="navs" style="top:0px">
<%
for(UAHmi hmi:hmis)
{
	String path = "/"+hmi.getNodePathCxt("/") ;
%>
<div class='nav' style="margin-left:30px;height:30px;line-height:15px"><i class="fa-regular fa-image"></i> <%=hmi.getTitle() %>
	
	<span class='op'>
		<button onclick="add_url_nav('<%=hmi.getTitle() %>','<%=path%>',true)" title="add to level 2"><i class="fa fa-angle-left"></i>2</button>
		<button onclick="add_url_nav('<%=hmi.getTitle() %>','<%=path%>',false)" title="add to level 1"><i class="fa fa-angle-left"></i>1</button>
	</span>
	<br><span>&nbsp;&nbsp;<%=path%></span>
</div>
<%
}
%>
      </div>
	</div>
	<div class="layui-tab-item tab_bd"  style="position:relative;top:0px;bottom:0px;width:100%;height:200px">
	<div class="navs" id="bsel_navs" style="top:0px">
<%

for(PageCat cat:pm.listPageCats().values())
{
%><div class="app"><%=cat.getTitle()%></div><%
	for(Page pg:cat.getId2PageMap().values())
	{
		String tt = pg.getTitle() ;
		String tmpurl = pg.getAccessPath() ;
%><div class='nav'><i class="fa-regular fa-file-lines"></i> <%=tt %>
	<span class='op'>
	<button onclick="add_url_nav('<%=tt%>','<%=tmpurl %>',true)" title="add to level 2"><i class="fa fa-angle-left"></i>2</button>
	<button onclick="add_url_nav('<%=tt%>','<%=tmpurl %>',false)" title="add to level 1"><i class="fa fa-angle-left"></i>1</button>
	</span>
</div>
<%
		
	}
}
%>
</div>
	</div>
  </div>
</div>

</div>
<script>
var prjid="<%=prjid%>"
var nf_id="<%=nf_id%>"
var form ;
var table ;
var url_path = "<%=navf.getUrlPath(true)%>" ;

var tobesel_navs = <%=tobesel_jarr.toString(4)%> ;

var navs = <%=navs_jarr%> ;
var cccc = 0 ;
function newId() {
	cccc ++ ;
    return 'n_' + Date.now().toString(36) + '_' + cccc;
}

function get_selnode_by_id(id)
{
	if(!id) return null ;
	for(let n of tobesel_navs)
	{
		if(n.id==id)
			return n ;
		if(n.sub)
		{
			for(let subn of n.sub)
			{
				if(subn.id==id)
					return subn ;
			}
		}
	}
	return null ;
}

layui.use(['table','form'], function()
{
	form = layui.form;
	table = layui.table;
	$("#sys_t").on("input",function(e){
		set_dirty(true);
	});
	 form.on('select(layout)', function(data){   
		 set_dirty(true);
	 });
	 
	form.render();
});

function get_node_by_id(id)
{
	if(!id) return null ;
	for(let n of navs)
	{
		if(n.id==id)
			return n ;
		if(n.sub)
		{
			for(let subn of n.sub)
			{
				if(subn.id==id)
					return subn ;
			}
		}
	}
	return null ;
}

function del_node_by_id(id)
{
	if(!id) return null ;
	let i1 = 0 ;
	for(let n of navs)
	{
		if(n.id==id)
		{
			navs.splice(i1,1) ;
			return n;
		}
		i1 ++ ;
		
		if(n.sub)
		{
			let i2 = 0 ;
			for(let subn of n.sub)
			{
				if(subn.id==id)
				{
					n.sub.splice(i2,1)
					return subn ;
				}
				i2 ++ ;
			}
		}
	}
	return null ;
}



function show_page()
{
	window.open(url_path);
}

var selected_nav1_id= null ;
function select_nav1(id)
{
	selected_nav1_id = id ;
	$(".ins_nav1").removeClass('sel') ;
	if(id)
		$("#"+id).addClass('sel');
}

function calc_icon(icon)
{
	if(!icon) return "" ;
	if(icon.indexOf("<")==0)
		return icon ;
	if(icon.indexOf("&#x")==0)
		return `<i class="fa">\${icon}</i>`
	return `<i class="\${icon}"></i>`;
}

function update_navs()
{
	let ss = "" ;
	for(let n of navs)
	{
		let ico = calc_icon(n.icon) ;
		ss += `<div class='nav ins ins_nav1' id='\${n.id}' onclick="select_nav1('\${n.id}')"  title="\${n.url}">\${ico} \${n.title}
			<span class='op'>
				<button onclick="updown_nav('\${n.id}',true)"><i class="fa fa-arrow-up"></i></button>
				<button onclick="updown_nav('\${n.id}',false)"><i class="fa fa-arrow-down"></i></button>
				<button onclick="del_node('\${n.id}')"><i class="fa fa-times"></i></button>
				</span>
			</div>` ;
		for(let subn of n.sub||[])
		{
			let ico2 = calc_icon(subn.icon);
			ss += `<div class='nav ins ins_nav2' style="margin-left:30px;" id='\${subn.id}' style="margin-left:30px;" title="\${subn.url}">\${ico2} \${subn.title}
				<span class='op'>
				<button onclick="updown_nav('\${subn.id}',true)"><i class="fa fa-arrow-up"></i></button>
				<button onclick="updown_nav('\${subn.id}',false)"><i class="fa fa-arrow-down"></i></button>
				<button onclick="del_node('\${subn.id}')"><i class="fa fa-times"></i></button>
				</span>
				</div>` ;
		}
	}
	$("#navs").html(ss) ;
	select_nav1(selected_nav1_id)
}

update_navs();


function del_node(id)
{
	let nd = del_node_by_id(id) ;
	if(!nd) return ;
	update_navs();
	set_dirty(true);
}

function updown_nav(id,b_up)
{
	if(!id) return null ;
	let pnavs = navs ;
	let mv_nd = null ;
	let mv_idx = -1 ;
	
	let idx1 = 0 ;
	for(let n of navs)
	{
		if(n.id==id)
		{
			mv_nd = n ;
			mv_idx = idx1 ;
			break ;
		}
		idx1 ++ ;
		
		if(n.sub)
		{
			let idx2 = 0 ;
			for(let subn of n.sub)
			{
				if(subn.id==id)
				{
					pnavs = n.sub ;
					mv_nd = subn ;
					mv_idx = idx2 ;
					break ;
				}
				idx2 ++ ;
			}
			
			if(mv_nd)
				break ;
		}
	}
	if(!mv_nd)
		return null ;
	
	if(b_up)
	{
		if(mv_idx<=0) return ;
		let tmpnd = pnavs[mv_idx] ;
		pnavs[mv_idx] = pnavs[mv_idx-1];
		pnavs[mv_idx-1] = tmpnd ;
	}
	else
	{
		if(mv_idx>=pnavs.length-1) return;
		let tmpnd = pnavs[mv_idx] ;
		pnavs[mv_idx] = pnavs[mv_idx+1];
		pnavs[mv_idx+1] = tmpnd ;
	}
	update_navs();
	set_dirty(true);
}

function add_nav1()
{
	dlg.open("./util_nav_node_edit.jsp",
			{title:"<w:g>edit,node</w:g>",w:'500px',h:'400px'},
			['<w:g>ok</w:g>','<w:g>close</w:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 let r = {id:newId(),title:ret.title,icon:ret.icon}
						 navs.push(r) ;
						 update_navs();
						 set_dirty(true)
						 dlg.close();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function create_ins(nd,b_sub)
{
	let r = {id:newId(),title:nd.title,url:nd.url,icon:nd.icon,target:nd.target}
	if(b_sub)
	{
		r.sub = [] ;
		if(nd.sub && nd.sub.length)
		{
			for(let subn of nd.sub)
			{
				let nn = {id:newId(),title:subn.title,url:subn.url,icon:subn.icon,target:subn.target}
				r.sub.push(nn)
			}
		}
	}
	return r ;
}


function add_to_nav1(sel_id,b_sub)
{
	let nd = get_selnode_by_id(sel_id)
	if(!nd) return ;
	let newnd = create_ins(nd,b_sub);
	navs.push(newnd) ;
	update_navs();
	set_dirty(true)
}

function add_to_nav2(sel_id)
{
	if(!selected_nav1_id)
	{
		dlg.msg("<w:g>pls,select,left_nav1_node</w:g>");return;
	}
	let tar_nd = get_node_by_id(selected_nav1_id);
	if(!tar_nd) return ;
	let nd = get_selnode_by_id(sel_id)
	if(!nd) return ;
	let newnd = create_ins(nd,false);
	if(!tar_nd.sub) tar_nd.sub=[];
	tar_nd.sub.push(newnd) ;
	update_navs();
	set_dirty(true)
}

function add_url_nav(tt,url,b_nav2)
{
	let p_nds = navs ;
	if(b_nav2)
	{
		if(!selected_nav1_id)
		{
			dlg.msg("<w:g>pls,select,left_nav1_node</w:g>");return;
		}
		let tar_nd = get_node_by_id(selected_nav1_id);
		if(!tar_nd) return ;
		if(!tar_nd.sub) tar_nd.sub=[];
		p_nds = tar_nd.sub ;
	}
	
	dlg.open("./util_nav_node_edit.jsp",
			{title:"<w:g>edit,node</w:g>",w:'500px',h:'400px',input:{title:tt}},
			['<w:g>ok</w:g>','<w:g>close</w:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 let r = {id:newId(),title:ret.title,icon:ret.icon,url:url}
						 p_nds.push(r) ;
						 update_navs();
						 set_dirty(true)
						 dlg.close();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function get_detail()
{
	let ret = {} ;
	ret.sys_t = $("#sys_t").val()||"";
	ret.layout = $("#layout").val()||"default";
	ret.home_url = $("#home_url").val()||"" ;
	ret.node_inss = navs ;
	return ret;
}

function save_detail()
{
	let ob = get_detail() ;
	send_ajax("portal_ajax.jsp",{op:"set_nf_detail",prjid:prjid,nf_id:nf_id,jstr:JSON.stringify(ob)},(bsucc,ret)=>{
		if(!bsucc || ret!='succ')
		{
			dlg.msg(ret);return;
		}
		set_dirty(false);
		reload_preview();
	})
}

function change_logo()
{
	add_file.click() ;
	return false;
}

function add_file_onchg()
{
	//$("#"+id).
	var fs = $("#add_file")[0].files ;
	if(fs==undefined||fs==null||fs.length<=0)
	{
		return ;
	}
	var f = fs[0];
	//console.log(f) ;
	var wurl = window.URL || window.webkitURL;
	var imgurl = wurl.createObjectURL(f);
	$("#img_add").attr('src',imgurl);
	//upload
	var fd = new FormData();
	fd.append("prjid",prjid) ;
    fd.append("nf_id",nf_id) ;
    fd.append("file",f);
     $.ajax({"url": "portal_file_up.jsp",type: "post","processData": false,"contentType": false,
		"data": fd,
		success: function(data)
       	{
			//lj.show_loading(false) ;
 	  		if(data.indexOf("succ=")==0)
 	  		{
 	  			dlg.msg("set ok");
 	  			let fn = data.substring(5) ;
 	  			let img_u = `/portal__logo.jsp?prjid=\${prjid}&fn=\${fn}&v=`+new Date().getTime();
 	  		 	$("#img_logo").attr("src",img_u) ;
 	  		}
 	  		else
 	  		{
 	  			dlg.msg(data);
 	  		}
   　  },
      　error: function(data)
         {
	  				dlg.msg("set logo "+data);
  　　　　}
  　　});
}


function set_dirty(b)
{
	$("#btn_save_detail").css("background-color",b?"yellow":"") ;
}

function reload_preview()
{
	parent.on_page_preview(url_path)
}

reload_preview()

function fit_height()
{
	let hpx =($(window).height()-220);
	$(".tab_bd").css("height",hpx+"px")
}
fit_height();
$(window).resize(function(){
	fit_height();
});

//

</script>
</body>
</html>