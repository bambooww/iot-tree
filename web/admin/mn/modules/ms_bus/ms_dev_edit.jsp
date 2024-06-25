<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.pro.modbuss.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	String prjid = request.getParameter("prjid");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	String prjpath = prj.getNodePath() ;
	LinkedHashMap<Integer,String>fc2tt = SlaveDevSeg.listFCs() ;
	JSONObject fc2ttjo = new JSONObject(fc2tt) ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>

<style>

th
{
	font-size: 12px;
}

td
{
	font-size: 14px;
}

.left
{
	position: absolute;
	width:40%;
	left:2px;
	top:2px;
	bottom:2px;
	border:1px solid #dddddd;
}

.right
{
	position: absolute;
	width:59%;
	right:2px;
	top:2px;
	bottom:2px;
	border:1px solid #dddddd;
	overflow-y:auto;
}

.right table
{
	width:100%;
}

.right table td
{
	border:1px solid #dddddd;
}

.seg_item
{
		position: relative;
	height:40px;
	border:1px solid #dddddd;
	left:5%;
	width:90%;
	margin-top: 3px;
}

.seg_item .op
{
	position: absolute;
	right:5px;top:3px;
}

.seg_item:hover
{
	background-color: #cccccc;
}

.fix
{
background-color: #eeeeee;
}
.set
{
	cursor: pointer;
}
.set:hover
{
	background-color: #888888;
}
</style>
</head>

<body>
<div class="left">
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>name</w:g>:</label>
    <div class="layui-input-inline" style="width:36%;">
      <input type="text" id="name" name="name" value=""  class="layui-input">
    </div>
    <div class="layui-form-mid">Addr</div>
    <div class="layui-input-inline" style="width:55px;">
      <input type="number" id="dev_addr" class="layui-input" lay-skin="primary" value="1"/>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>title</w:g>:</label>
    <div class="layui-input-inline" style="width:66%;">
      <input type="text" id="title" name="title" value=""  class="layui-input">
    </div>
  </div>
  <div id="pm_cont">
  	Segments <button class="layui-btn layui-btn-sm layui-btn-primary" onclick="add_edit_seg()">+</button>
  	<div id="segs_list">
  	</div>
  </div>
 </form>
</div>
<div class="right" id="seg_detail">
</div>
</body>

<script type="text/javascript">
var prjid="<%=prjid%>";
var prjpath = "<%=prjpath%>";

var FC2TT = <%=fc2ttjo%> ;
var dev = dlg.get_opener_opt("dev") ;
if(!dev)
	dev = {id:dlg.create_new_tmp_id(),name:"",title:"",segs:[]} ;
	
var cur_seg = null ;

function get_seg_by_id(id)
{
	for(let seg of dev.segs)
	{
		if(seg.id==id)
			return seg ;
	}
	return null ;
}

function add_edit_seg(id)
{
	let tt = "Add Segment" ;
	let seg = null ;
	let seg_idx = -1 ;
	if(id)
	{
		tt = "Edit Segment" ;
		seg = get_seg_by_id(id) ;
		seg_idx = dev.segs.indexOf(seg) ;
	}
	
	dlg.open(`./ms_dev_seg_edit.jsp`,{title:tt,seg:seg},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.get_edit_seg((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						//console.log(ret) ;
						if(seg_idx<0)
							dev.segs.push(ret) ;
						else
							dev.segs[seg_idx]=ret ;
						dlg.close() ;
						update_segs();
					});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function update_segs()
{
	let tmps="" ;
	for(let seg of dev.segs)
	{
		tmps += `<div class="seg_item" onclick="show_seg_detail('\${seg.id}')">
			<div class="t">[FC:\${seg.fc} \${FC2TT[""+seg.fc]}]  \${seg.title}</div>
			<div class="addr">Register Index:\${seg.reg_idx} Number:\${seg.reg_num}</div>
			<span class="op"><button onclick="add_edit_seg('\${seg.id}')">edit</button></span>
		</div>` ;
	}
	
	$("#segs_list").html(tmps) ;
}


function fmt_addr(fc,idx)
{
	switch(fc)
	{
	case 1: //0000X
		return idx.toString().padStart(5,'0') ;
	case 2: //1000X
		return (10000+idx).toString();
	case 3://4000X
		return (40000+idx).toString();
	case 4://3000X
		return (30000+idx).toString();
	default:
		return "" ;
	}
}

function show_seg_detail(id)
{
	cur_seg = get_seg_by_id(id) ;
	let tmps = "" ;
	if(cur_seg==null)
	{
		$("#seg_detail").html(tmps) ;
		return ;
	}
	
	tmps += `<table><thead>
	    <tr>
	      <th style="width:70px">Address</th>
	      <th style="width:50px">Type</th>
	      <th>Var</th>
	      <th>Var</th>
	      <th>Bind Tag</th>
	    </tr>
	  </thead>
	  <tbody>` ;
	for(let i= 0 ; i < cur_seg.reg_num ; i ++)
	{
		let idx = cur_seg.reg_idx+i ;
		let pos = idx+1 ;
		let addr = fmt_addr(cur_seg.fc,pos) ;
		let tp = "bool" ;
		if(cur_seg.fc>2)
			tp = "int16"
		tmps += `<tr>
		      <td class="fix">\${addr}</td>
		      <td class="fix">\${tp}</td>
		      <td class="set var" id="td_0_\${idx}" onclick="set_var(0,\${idx})"></td>
		      <td class="set var" id="td_1_\${idx}" onclick="set_var(1,\${idx})"></td>
		      <td class="set tag" id="td_tag_\${idx}" onclick="set_bind(\${idx})"></td>
		    </tr>`;
	}
	tmps += `</tbody></table>` ;
	$("#seg_detail").html(tmps) ;
	
	update_curseg_vars();
	update_curseg_tags();
}

function get_var_in_curseg(col,idx)
{
	if(!cur_seg)
		return null;
	if(!cur_seg.vars)
		return null ;
	for(let vv of cur_seg.vars)
	{
		if(vv.idx==idx && vv.col==col)
			return vv ;
	}
	return null ;
}

function update_curseg_vars()
{
	if(!cur_seg)
		return;
	$(".var").html("") ;
	if(!cur_seg.vars)
		return ;
	for(let vv of cur_seg.vars)
	{
		let col = vv.col ;
		let idx = vv.idx ;
		let ele = $("#td_"+col+"_"+idx) ;
		if(ele.length<=0)
			continue ;
		ele.html(vv.name+":"+vv.valtp) ;
	}
}

function set_var(col,idx)
{
	if(!cur_seg)
		return ;
	let oldvv = get_var_in_curseg(col,idx) ;
	dlg.open(`./ms_var_edit.jsp?fc=\${cur_seg.fc}`,{title:"Set Var",vv:oldvv},
			['<w:g>ok</w:g>','<w:g>del</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlgw.get_edit_var((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						
						//dlg.msg(JSON.stringify(ret)) ;
						
						if(oldvv)
						{
							oldvv.name = ret.name ;
							oldvv.valtp = ret.valtp ;
						}
						else
						{
							if(!cur_seg.vars)
								cur_seg.vars=[] ;
							ret.col = col;
							ret.idx=idx ;
							cur_seg.vars.push(ret) ;
						}
						update_curseg_vars();
						dlg.close() ;
					});
				},
				function(dlgw)
				{
					if(oldvv)
					{
						let k = cur_seg.vars.indexOf(oldvv) ;
						cur_seg.vars.splice(k,1) ;
						update_curseg_vars();
					}
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function get_tag_in_curseg(idx)
{
	if(!cur_seg)
		return null;
	if(!cur_seg.tags)
		return null ;
	for(let vv of cur_seg.tags)
	{
		if(vv.idx==idx)
			return vv ;
	}
	return null ;
}

function update_curseg_tags()
{
	if(!cur_seg)
		return;
	$(".tag").html("") ;
	if(!cur_seg.tags)
		return ;
	for(let vv of cur_seg.tags)
	{
		let idx = vv.idx ;
		let ele = $("#td_tag_"+idx) ;
		if(ele.length<=0)
			continue ;
		ele.html(vv.tag) ;
	}
}

function set_bind(idx)
{
	if(!cur_seg)
		return ;
	let oldvv = get_tag_in_curseg(idx) ;
	let old_tags=[];
	if(oldvv) old_tags.push(oldvv.tag) ;
	dlg.open("../../../ua_cxt/cxt_tag_selector.jsp?w_only="+false+"&multi=false&path="+prjpath,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:old_tags},
			['<w:g>ok</w:g>','<w:g>unbind</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ps = dlgw.get_selected_tagpaths();
					if(ps.length<=0)
					{
						dlg.msg("<w:g>pls,select,tag</w:g>");
						return ;
					}
					let txt = dlgw.get_selected_tagtxt();
					let selp = ps[0] ;
					if(oldvv)
					{
						oldvv.tag = selp ;
					}
					else
					{
						if(!cur_seg.tags)
							cur_seg.tags=[] ;
						cur_seg.tags.push({tag:selp,idx:idx}) ;
					}
					
					update_curseg_tags()
					dlg.close();
				},
				function(dlgw)
				{
					if(oldvv)
					{
						let k = cur_seg.tags.indexOf(oldvv) ;
						cur_seg.tags.splice(k,1) ;
						update_curseg_tags();
					}
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function update_dev()
{
	$("#name").val(dev.name) ;
	$("#title").val(dev.title) ;
	$("#dev_addr").val(dev.dev_addr||1) ;
	
	update_segs() ;
}

function get_edit_dev(cb)
{
	let n = $("#name").val() ;
	if(!n)
	{
		cb(false,"<w:g>pls,input,name</w:g>");
		return false;
	}
	dev.name = n ;
	dev.title = $("#title").val() ;
	dev.dev_addr = get_input_val("dev_addr",-1,true) ;
	if(dev.dev_addr<=0 || dev.dev_addr>=255)
	{
		cb(false,"<w:g>pls,input,valid</w:g> Addr (1-254)")
		return false;
	}
	cb(true,dev) ;
}

update_dev() ;
dlg.resize_to(900,600) ;

</script>

</html>                                                                                                                                                                                                                            