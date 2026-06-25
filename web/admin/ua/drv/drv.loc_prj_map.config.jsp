<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,org.json.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.drv.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "nodepath"))
		return ;
	String nodep = request.getParameter("nodepath") ;
	UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
	if(node==null || !(node instanceof UACh))
	{
		out.print("no UACh found with path="+nodep) ;
		return ;
	}
	
	UACh ch = (UACh)node ;
	UAPrj self_prj = ch.getBelongTo() ;
	DevDriver drv = ch.getDriver() ;
	if(drv==null || !(drv instanceof LocPrjMapDriver))
	{
		out.println("no driver found") ;
		return ;
	}
	LocPrjMapDriver mceth = (LocPrjMapDriver)drv ;
	
	List<UAPrj> all_prjs = UAManager.getInstance().listPrjs() ;
	JSONObject all_prjs_ob = new JSONObject() ;
	for(UAPrj p:all_prjs)
	{
		JSONObject pjo = new JSONObject().put("id",p.getId()).put("t",p.getTitle()).put("n", p.getName()) ;
		all_prjs_ob.put(p.getId(),pjo) ;
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(900,600);
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
.top {position: absolute;left:0px;top:0px;width:100%;height:30px;background-color:#007ad4;
	color:#ddd;font-size: 15px;text-align: left;font-weight: bold;line-height: 30px;}
.left {position: absolute;left:0px;top:30px;width:200px;bottom:0px;border:1px solid #ccc;overflow-y:auto;}
.mid {position: absolute;left:200px;top:30px;right:200px;;bottom:0px;border:1px solid #ccc;overflow-y:auto;}
.right {position: absolute;right:0px;top:30px;width:200px;bottom:0px;border:1px solid #ccc;overflow-y:auto;}
.prji {margin:2px;border:1px solid blue;position: relative;cursor: pointer;}
.prji .t {margin-right:30px;}
.prji .i {position: absolute;right:1px;top:1px;float:right; }
.prj_item {position:relative;margin:5px;width:98%;border:1px solid green;border-radius:5px;;max-height:200px;overflow-y:auto;}
.prj_item table {width:100%;}
.prj_item .del {position: absolute;top:1px;right:5px;color:red;width:20px;}
td {border:1px solid #ccc;font-size: 12px;}
.chtagi {margin:2px;border:1px solid blue;position: relative;text-align: left;cursor: pointer;}
.tic {}
.tic:hover {background-color:#4b6fa1;color:#ddd;}
.seled {background-color:#4b6fa1;color:#ddd;}
.vt {color:#950055}
</style>
</head>
<body>
<div class="top">Local Project Mapping
	
</div>
<div class="left">
Other local projects
<%
for(UAPrj prj:all_prjs)
{
	if(self_prj==prj)
		continue ;
%><div class="prji" onclick="add_prj('<%=prj.getId()%>')"><span class="t"><%=prj.getTitle() %></span> <span class="i" ><i class="fa fa-arrow-right"></i></span></div>
<%
}
%>
</div>
<div class="right">
 Local Channel Tags
<%
for(UATag tag:ch.listTagsNorAll())
{
	String tagp = tag.getNodeCxtPathIn(ch) ;
	String valtp = tag.getValTp().getStr() ;
%><div class="chtagi" onclick="set_ch_tag_to_cur('<%=tagp%>','<%=valtp%>')"><span ><i class="fa fa-arrow-left"></i></span> <%=tagp %>:<span style="color:#7f0081"><%=valtp %></span></div><%
}
%>
</div>
<div class="mid">
</div>
</body>
<script type="text/javascript">
var ch_path = "<%=nodep%>" ;
var input_txt = dlg.get_opener_opt("inputv") ;
var id2prj = <%=all_prjs_ob%>;
var map_prjs = [] ;
if(input_txt)
{
	
}
var cur_prj_id = null ;
var cur_tic_td = null;

function update_ui()
{
	let ss="" ;
	for(let prj of map_prjs)
	{
		let prj_id = id2prj[prj.prjid]?.id ;
		let prj_t = id2prj[prj.prjid]?.t ;
		let prj_n = id2prj[prj.prjid]?.n ;
		let tis = prj.tis||[];
		let tiss = "" ;
		for(let ti of tis)
		{
			tiss += 	`<tr><td class="tip" tagp="\${ti.tip}">\${ti.tip} <span class="vt">\${ti.tip_vt}</span></td>
			<td class="tic" onclick="set_cur_tic('\${prj_id}',this)">\${ti.tic} <span class="vt">\${ti.tic_vt||""}</span></td>
			<td><a href="javascript:del_map_ti_item('\${prj_id}','\${ti.tip}')"><i class="fa fa-times"></i></a></td>
			</tr>`;
		}
		
		ss += `<div class="prj_item">
			<button class='del' onclick="del_prj('\${prj_id}')"><i class="fa fa-times"></i></button>
			<span class="t">\${prj_t}</span>
			<table>
				<tr>
					<td style="width:50px;text-align:center">Tags (\${prj.tis.length})
						<button></button>
					</td>
					<td>
						<table>
							<tr><td>Other Project Tags <a href="javascript:sel_tags('\${prj_id}','/\${prj_n}')"><i class="fa fa-pencil"></i></a></td><td>Local Channel Tags</td><td>Oper</td></tr>
							\${tiss}
						</table>
					</td>
				</tr>
				<tr>
				<td>Dirs (\${prj.dis.length})</td>
				<td></td>
			</tr>
			</table>
			
		    </div>
			</div>` ;
	}
	$(".mid").html(ss) ;
}

function init_input()
{
	if(!input_txt || input_txt.indexOf("[")!=0)
		return ;
	//map_prjs = JSON.parse(input_txt) || [];
	send_ajax("./drv_loc_prj_map.ajax.jsp",{op:"prj_items_detail",nodepath:ch_path,jarr:input_txt},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret);return ;
		}
		map_prjs = JSON.parse(ret) || [];
		//console.log(map_prjs);
		update_ui();
	});
}

function win_close()
{
	dlg.close(0);
}

function get_map_prj(prjid)
{
	for(let p of map_prjs)
	{
		if(prjid==p.prjid)
			return p;
	}
	return null;
}

function get_map_tag_paths(prj_id)
{
	let prji = get_map_prj(prj_id) ;
	if(!prji || !prji.tis) return [];
	let ret=[];
	for(let ti of prji.tis)
	{
		ret.push(ti.tip);
	}
	return ret;
}

function get_map_ti_item(prj_id,tag_p)
{
	let prji = get_map_prj(prj_id) ;
	if(!prji) return null ;
	for(let ti of prji.tis||[])
	{
		if(ti.tip==tag_p)
		{
			return ti ;
		}
	}
	return null ;
}


function del_map_ti_item(prjid,tip)
{
	let prji = get_map_prj(prjid);
	if(!prji) return ;
	let n = 0 ;
	if(prji.tis) n = prji.tis.length ;
	for(let i =0 ; i < n ; i ++)
	{
		let ti = prji.tis[i];
		if(ti.tip==tip)
		{
			prji.tis.splice(i,1) ;
			update_ui();
			return ;
		}
	}
}

function set_map_tag_paths(prj_id,tags)
{
	let prji = get_map_prj(prj_id) ;
	if(!prji) return;
	let newtis = [];
	for(let newtp of tags||[])
	{
		let tagp = newtp.tagp ;
		let vt = newtp.vt ;
		let oldti = get_map_ti_item(prj_id,newtp) ;
		if(oldti!=null)
		{
			newtis.push(oldti);continue ;
		}
		newtis.push({tip:tagp,tip_vt:vt,tic:""}) ;
	}
	prji.tis = newtis ;
}



function set_cur_tic(prjid,td)
{
	$(".tic").removeClass("seled");
	$(td).addClass("seled") ;
	cur_prj_id = prjid;
	cur_tic_td = td ;
}

function set_ch_tag_to_cur(tic,vt)
{
	if(!cur_tic_td||!cur_prj_id) return ;
	let ele = $(cur_tic_td) ;
	let td_tip = ele.prev("td") ;
	let tip = td_tip.attr("tagp");
	if(!tip) return ;
	let ti = get_map_ti_item(cur_prj_id,tip) ;
	if(!ti) return ;
	ti.tic = tic ;
	ti.tic_vt = vt ;
	//$(cur_tic_td).html(tic) ;
	update_ui();
}

function add_prj(prjid)
{
	let oldp = get_map_prj(prjid);
	if(oldp)
	{
		dlg.msg("project is already added") ;return;
	}
	map_prjs.push({prjid:prjid,tis:[],dis:[]}) ;
	update_ui();
}

function del_prj(prjid)
{
	let oldp = get_map_prj(prjid);
	if(!oldp) return;
	let idx = map_prjs.indexOf(oldp);//(item=>item==oldp) ;
	if(idx>=0)
	{
		map_prjs.splice(idx,1) ;
		update_ui();
	}
}


function sel_tags(prj_id,prj_path)
{
	dlg.open(`../../ua_cxt/cxt_tag_selector.jsp?path=\${prj_path}&multi=true&bind_tag_only=true`,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:get_map_tag_paths(prj_id)},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tags() ; //{tagid:tagid,tagp:tagp,tagt:patht}
					if(!ret || ret.length<=0)
					{
						dlg.msg("please select tags");return ;
					}
					set_map_tag_paths(prj_id,ret);
					update_ui();
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
	cb(true,{txt:JSON.stringify(map_prjs)});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

init_input();
</script>
</html>