<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.json.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "nodepath"))
		return ;
	String nodep = request.getParameter("nodepath") ;
	UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
	if(node==null || !(node instanceof UAPrj))
	{
		out.print("no prj node found with path="+nodep) ;
		return ;
	}
	
	
	UAPrj prj = (UAPrj)node ;
	
	List<UAHmi> hmis = prj.listHmiNodesAll() ;
	JSONArray jarr = new JSONArray() ;
	
	for(UAHmi hmi:hmis)
	{
		String p = hmi.getNodeCxtPathInPrj() ;
		String t = hmi.getTitle() ;
		JSONObject jo = new JSONObject() ;
		jo.put("path",p) ;
		jo.putOpt("title",t) ;
		jo.put("show",false) ;
		jarr.put(jo);
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(600,550);
</script>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}
.hmi_item
{
	position: relative;
	left:10%;
	width:80%;
	height:60px;
	border:1px solid;
	margin:5px;
	font-size: 15px;
}

.hmi_item .chk
{
	position: absolute;
	left:10px;
	top:15px;
}
.hmi_item .topt
{
	position: absolute;
	left:10px;
	font-weight: bold;
}
.hmi_item .inputt
{
	position: absolute;
	left:10px;
	top:30px;
}

.hmi_item .icon
{
	position: absolute;
	right:70px;
	top:5px;
	font-size: 50px;
	cursor:pointer;
}
.hmi_item .u
{
position: absolute;
	right:25px;
	top:4px;
	cursor: pointer;
}
.hmi_item .d
{
position: absolute;
	right:25px;
	bottom:10px;
	cursor: pointer;
}
.hmi_item .usel
{
position: absolute;
	right:5px;
	color:red;
	top:5px;
	cursor: pointer;
}
.hmi_item .s
{
position: absolute;
	right:25px;
	top:1px;
	cursor: pointer;
}
.btop
{
	font-weight: bold;
	font-size:16px;
	background-color: #b2b2b2;
}
</style>
</head>
<body>
<div class="layui-panel">
<div class="btop"><wbt:g>selected</wbt:g></div>
<div id="hmi_list" style="height:50%;overflow: auto;border:0px solid;"> 
</div>
</div>
<div class="btop"><wbt:g>un_selected</wbt:g></div>
<div id="hmi_list_unsel" style="height:38%;overflow: auto;border:0px solid;"> 
</div>

</body>
<script type="text/javascript">

var input_txt = dlg.get_opener_opt("inputv") ;

var hmi_list_all = <%=jarr%> ;

var hmi_list=[] ;

function get_hmi_in_all(path)
{
	for(let h of hmi_list_all)
	{
		if(h.path==path)
			return h ;
	}
	return null ;
}

function get_hmi_in_sel(path)
{
	for(let h of hmi_list)
	{
		if(h.path==path)
			return h ;
	}
	return null ;
}

function update_ui()
{
	let tmps="";
	let sel_ps = [];
	let n = hmi_list.length ;
	for(let i = 0 ; i < n ; i ++)
	{
		let h = hmi_list[i] ;
		let shmi = get_hmi_in_all(h.path) ;
		if(!shmi)
			continue ;
		let up_hidden = i==0?"visibility: hidden;":"" ;
		let down_hidden = (i==n-1)?"visibility: hidden;":"" ;
		sel_ps.push(h.path) ;
		let icon = h.icon||"f1d8" ;
		let color = h.color||"#76d170" ;
		tmps += `<div class="hmi_item" id="hi_\${h.path}" nodep="\${h.path}">
		<div class="topt">\${h.path} (\${shmi.title})</div>
		<div class="inputt"><wbt:g>list,title</wbt:g>:<input type="text" id="inp_\${h.path}" value="\${h.title}"/></div>
		<div class="icon" ><i class="fa" style="color:\${color};" path="\${h.path}" sel_color="\${color}" sel_icon="\${icon}" onclick="sel_icon_color(this)">&#x\${icon}</i></div>
		
		<span class="u" style="\${up_hidden}" onclick="up_down('\${h.path}',true)"><i class="fa fa-arrow-up"></i></span>
		<span class="d" style="\${down_hidden}" onclick="up_down('\${h.path}',false)"><i class="fa fa-arrow-down"></i></span>
		<span class="usel" onclick="unselect('\${h.path}')"><i class="fa fa-times"></i></span>
	</div>`;
	}
	$("#hmi_list").html(tmps) ;
	
	tmps = "" ;
	for(let h of hmi_list_all)
	{
		if(sel_ps.indexOf(h.path)>=0)
			continue ;
		tmps += `<div class="hmi_item" style="height:35px;" id="hi_\${h.path}" nodep="\${h.path}">
			<div class="topt">\${h.path} (\${h.title})</div>
			<span class="s"><button class="layui-btn layui-btn-sm" onclick="select('\${h.path}')" title="<wbt:g>select</wbt:g>"><i class="fa fa-arrow-up"></i></button></span>
		</div>`;
	}
	$("#hmi_list_unsel").html(tmps) ;
}

function unselect(path)
{
	let ob = get_hmi_in_sel(path);
	if(!ob) return ;
	let idx = hmi_list.indexOf(ob) ;
	hmi_list.splice(idx,1);//.push(ob) ;
	update_ui() ;
}

function up_down(path,b_up)
{
	let ob = get_hmi_in_sel(path);
	if(!ob) return ;
	let idx = hmi_list.indexOf(ob) ;
	if(b_up)
	{
		if(idx==0)
			return ;
		let tmpo = hmi_list[idx] ;
		hmi_list[idx] = hmi_list[idx-1] ;
		hmi_list[idx-1] = tmpo ;
	}
	else
	{
		if(idx==hmi_list.length-1)
			return ;
		let tmpo = hmi_list[idx] ;
		hmi_list[idx] = hmi_list[idx+1] ;
		hmi_list[idx+1] = tmpo ;
	}
	update_ui() ;
}

function select(path)
{
	let ob = get_hmi_in_all(path);
	if(!ob) return ;
	hmi_list.push(ob) ;
	update_ui() ;
}

function init()
{
	if(input_txt)
	{
		eval("hmi_list="+input_txt) ; ;
	}
	
	update_ui();
}

init() ;

function win_close()
{
	dlg.close(0);
}

function sel_icon_color(ele)
{
	let ob = $(ele) ;
	let color=ob.attr("sel_color") ;
	let icon = ob.attr("sel_icon") ;
	let pm = {color:color,icon:icon} ;
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
						ob.attr("sel_icon",ret.icon) ;
						ob.attr("sel_color",ret.color) ;
						ob.css("color",ret.color) ;
						ob.html("&#x"+ret.icon)
						let p = ob.attr("path") ;
						let h = get_hmi_in_sel(p) ;
						if(h)
						{
							h.icon = ret.icon ;
							h.color=ret.color ;
						}
						update_ui();
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
	for(let h of hmi_list)
	{
		let inp = document.getElementById("inp_"+h.path) ;
		let title = $(inp).val() ;
		title = trim(title) ;
		if(!title)
		{
			cb(false,"<wbt:g>pls,input,list,title</wbt:g>") ;
			$(inp).focus() ;
			return;
		}
	}
	cb(true,{txt:JSON.stringify(hmi_list)});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>