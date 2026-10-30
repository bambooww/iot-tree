<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,org.json.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
					org.iottree.driver.zigbee.zstack.*,
	org.iottree.driver.zigbee.zspec.*,
	org.iottree.driver.zigbee.zspec.util.*,org.iottree.driver.zigbee.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String path = request.getParameter("path") ;
	StringBuilder failedr = new StringBuilder() ;
	ZSDriver zs_drv = ZigbeeManager.getZSDriverByPath(path, failedr) ;
	if(zs_drv==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	ZSCoor z_coor = zs_drv.getZSCoor() ;
	List<ZSDevice> eds = z_coor.listDevices() ;
	if(eds==null)
		eds = Arrays.asList() ;
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../../head.jsp">
<jsp:param value="true" name="simple"/>
</jsp:include>
    <style>
.layui-form-label{width: 120px;}
.layui-input-block {margin-left: 140px;min-height: 36px;width:240px;}
.layui-table-view{margin-top: 1px;}
.layui-table-cell {height: auto;line-height: 18px;}

.item {
position:relative;height:155px;min-width:250px;margin:5px;
border:2px solid #cecece;border-radius:5px;color:#555;
float:left;background-color: #f8f8f8;
}
.item .t {position:absolute;top:20px;font-size: 14px;left:8px;font-weight: bold;}
.item .pow {position:absolute;top:1px;font-size: 12px;left:4px;background-color:#ccc;width:70px;text-align:center;border-radius: 2px;}
.item .op {position:absolute;bottom:23px;font-size:14px;right:3px;border:0px solid #ccc;width:90%;text-align:right;}
.item .op button {border-color0:orange;min-width:25px;color:#333;}
.item .v {position:absolute;top:25px;font-size:20px;left:2px;right:52px;border:0px solid;text-align:right;}
.item .dynd {position:absolute;top:43px;font-size:17px;right:4px;width:130px;height:65px;border:0px solid #ccc;font-weight:bold;}
.item .dt {position:absolute;bottom:3px;font-size:12px;left:7px;color:#1d89f0}
.item .img  {position:absolute;top:43px;left:10px;}
.item .expf  {position:absolute;top:83px;font-size: 16px;right:10px;border:1px solid #ccc;cursor:pointer;}
.item .img img {width:80px;height:80px;}
.item .link_qual {background-color: orange}
.item .addr {position:absolute;bottom:3px;font-size: 12px;right:3px;}
.item .fac {position:absolute;top:2px;font-size: 12px;right:23px;color:#5bb734;}
.d_btn {margin: 2px;width:18px;height:18px;font-size: 12px;}
.left  {position:absolute;left:0px;top:0px;width:300px;bottom:0px;}
.right {position:absolute;left:300px;top:0px;right:0px;bottom:0px;}
.item .del {position:absolute;right:0px;top:0px;color:#ee931e;}
</style>
</head>
<%--
<div style="width:100%;white-space:nowrap;display:inline-block;z-index:10;background-color: #fff;">
		<div style="float: left;margin-top:7px;">&nbsp;设list</div>
		<div style="float: left;margin-top:7px;">&nbsp;&nbsp;&nbsp;时间&nbsp;</div> 
	<div style="float: left;">
		<input class="layui-input" type="datetime-local" id="snap_dt" value="" onchange0="up_start_end_dt()"/>
	</div>
	
	<div class="layui-btn-container" style="float: left;margin-left:10px;">
	  <button type="button" id="btn_search" class="layui-btn layui-btn-sm" onclick="que_at()"><i class="fa-solid fa-magnifying-glass"></i> 查询</button>
	</div>
	<div class="layui-btn-container" style="float: right;margin-left:10px;">
	  <button type="button" id="btn_add_item" class="layui-btn layui-btn-primary layui-btn-sm" onclick="edit_item()"><i class="fa-solid fa-plus"></i>新增数据项</button>
	</div>
</div>
 --%>
<div id="item_list" style="position:absolute;left:0px;top:3px;right:0px;bottom:0px;border:1px solid #ccc;">
<%
String id= "" ;
String autoid ="" ;
JSONArray ed_addrs_jarr = new JSONArray() ;
for(ZSDevice ed : eds)
{
	String ieee = ed.getIEEEAddr() ;
	String tt = Convert.plainToHtml(ed.getTitle()) ;
	ShortAddr addr = ed.getShortAddr() ;
	if(Convert.isNullOrEmpty(tt))
		tt = addr.toHexStr();
	String up_dt = Convert.toYMDHM(new Date(ed.getUpdateDT())) ;
	int link_qual = ed.getLinkQual() ;
	String link_dt = Convert.toYMDHM(new Date(ed.getLinkDT())) ;
	String fac_n = ed.getFacName() ;
	String fac_m = ed.getFacModel() ;
	PowerSrc pow = ed.getPowerSrc() ;
	int pow_v = -1 ;
	String pow_t = "" ;
	String pow_ico = "" ;
	pow_ico = ed.getPowerIco();
	if(pow!=null)
	{
		
		pow_v = pow.getValInt() ;
		pow_t = pow.getTitle() ;
	}
	ed_addrs_jarr.put(ieee) ;
%><div class="item" id="ed_<%=ieee%>">
		<div class="t"><%=tt %> <span style="cursor: pointer;border:1px solid #ccc;" ed_ieee="<%=ieee%>"  ed_title="<%=tt %>" onclick="edit_ed_title(this)"><i class="fa fa-pencil"></i></span></div>
		<div class="v" id="item_v_<%=id%>" ></div>
		<div class="dynd">
		</div>
		<div class="img"><img src="/_iottree/zigbee/ed_pics/<%=fac_n.toLowerCase()%>/<%=fac_m.toLowerCase()%>.png" ></div>
		<div class="addr"><%=ieee %></div>
		<div class="fac"><%=fac_n %> [<%=fac_m %>]</div>
		<div class="dt"><span class="updt"><%=up_dt %></span> <span class="link_qual"><%=link_qual %></span></div>
		<div class="pow" title="<%=pow_t %>"><%=pow_ico %> [<%=addr %>]</div>
		<%--
		<div class="expf" onclick="exp_ed_func('<%=ieee %>','<%=tt %> - [<%=addr %>]')" ><w:g>exp_func</w:g> <i class="fa-solid fa-right-long"></i></div>
		 --%>
		 <button class="del" onclick="del_ed('<%=ieee%>')" style="width:16px;"><i class="fa fa-times"></i></button>
		<div class="op" >
		<button onclick="refresh_ed('<%=ieee%>')"><i class="fa-solid fa-arrows-rotate"></i></button>
		<button onclick="syn_ed_dev('<%=ieee%>')" title="<w:g>syn_tags</w:g>"><i class="fa-solid fa-arrow-right-arrow-left"></i></button>
		
		<button onclick="exp_ed_func('<%=ieee %>','<%=tt %> - [<%=addr %>]')"  title="<w:g>exp_func</w:g>" style="width:40px;"><i class="fa-solid fa-right-long"></i></button>
		</div>
</div>
<%
}
%>
</div>
<script>
var form ;
var path = "<%=path%>" ;
var device_tt = null ;
var ed_addrs = <%=ed_addrs_jarr%> ;

layui.use(['table','form'], function()
		{
	form = layui.form;
	  
		});

function edit_ed_title(ele)
{
	let ob = $(ele) ;
	let ieee = ob.attr("ed_ieee") ;
	let tt = ob.attr("ed_title") ;
	dlg.open('../../util/dlg_input_txt.jsp?txt_title=<w:g>title</w:g>',{title:'<w:g>edit,title</w:g>',w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let tt = dlgw.get_input() ;
					if(!tt) {dlg.msg("please input title");return;}

					send_ajax('./drv_ins.ajax.jsp',{op:"ed_set_title",path:path,ieee:ieee,title:tt},(bsucc,ret)=>
					{
							if(!bsucc || ret.indexOf('succ')<0)
							{
								dlg.msg(ret);
								return ;
							}
							dlg.close();
							location.reload();
					});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function exp_ed_func(ieee,tt)
{
	dlg.open_win('./ed_exp_func.jsp?path='+path+"&ieee="+ieee,{title:tt,w:600,h:400},
			[],
			[]);
}

function syn_rt_eds()
{
	send_ajax('./drv_ins.ajax.jsp',{op:"eds_rt_list",path:path},(bsucc,ret)=>
	{
			if(!bsucc || ret.indexOf('[')<0)
			{
				console.log(ret) ;
				return ;
			}
			let rts = null;
			eval("rts="+ret) ;
			//console.log(rts) ;
			for(let rt of rts)
				update_ed_rt(rt)
	});
}

function update_ed_rt(jo)
{
	let ieee = jo.ieee_addr||"" ;
	if(!ieee) return ;
	let item = $("#ed_"+ieee) ;
	if(item.length<=0) return ;
	
	item.find(".updt").html(new Date(jo.update_dt).format_local('yyyy-MM-dd hh:mm'));
	item.css("border-color",jo.no_resp_lost?"red":"#5bb734")
	let attr_vals = jo.attr_vals||[] ;
	let v_str = "" ;
	for(let i = 0 ; i < attr_vals.length && i < 3 ; i ++)
	{
		let av = attr_vals[i];
		let vvv = av.val ;
		if(vvv===null||vvv===undefined)
			vvv = "" ;
		v_str += `<div>\${vvv} \${av.unit||""}</div>` ;
	}
	item.find(".dynd").html(v_str) ;
}

var tick_cc = 0 ;

function on_permit_join_tick()
{
	tick_cc ++ ;
	if(tick_cc<4)
		return ;
	tick_cc = 0 ;
	send_ajax('./drv_ins.ajax.jsp',{op:"get_ep_addrs",path:path},(bsucc,ret)=>
	{
			if(!bsucc || ret.indexOf('[')<0)
			{
				console.log(ret) ;
				return ;
			}
			let ids = null;
			eval("ids="+ret) ;
			if(ids.length!=ed_addrs.length)
				location.reload();
	});
}

function refresh_ed(ieee)
{
	send_ajax('./drv_ins.ajax.jsp',{op:"refresh_ed",path:path,ieee:ieee},(bsucc,ret)=>
	{
			if(!bsucc || ret.indexOf('succ')<0)
			{
				dlg.msg(ret);
				return ;
			}
			dlg.msg(ret) ;
	});
}

function syn_ed_dev(ieee)
{
	send_ajax('./drv_ins.ajax.jsp',{op:"ed_syn_dev",path:path,ieee:ieee},(bsucc,ret)=>
	{
			if(!bsucc || ret.indexOf('succ')<0)
			{
				dlg.msg(ret);
				return ;
			}
			dlg.msg(ret) ;
	});
}



function del_ed(ieee)
{
	dlg.confirm('<w:g>del,this,dev</w:g>?',{btn:["<w:g>yes</w:g>","<w:g>cancel</w:g>"],title:"<w:g>del,confirm</w:g>"},function ()
	{
		send_ajax('./drv_ins.ajax.jsp',{op:"ed_leave",path:path,ieee:ieee},function(bsucc,ret)
		{
				if(!bsucc || ret.indexOf('succ')<0)
				{
					dlg.msg(ret);
					return ;
				}
				if(ret.indexOf("succ=")==0)
				{
					dlg.msg(ret.substring(5)) ;
				}
				dlg.close();
				location.reload();
		});
	});
}

syn_rt_eds();
setInterval(syn_rt_eds,2000) ;

</script>
</body>
</html>