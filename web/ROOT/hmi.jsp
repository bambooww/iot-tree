<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.json.*,
				org.iottree.core.*,
				org.iottree.core.store.record.*,
				org.iottree.core.ui.*,
				org.iottree.core.res.*,
				org.iottree.core.alert.*,
				org.iottree.core.store.*,
				org.iottree.core.plugin.*,
	org.iottree.core.util.*,org.iottree.core.station.*,
	org.iottree.core.comp.*,
				java.net.*"%><%@ taglib uri="wb_tag" prefix="lan"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
   String bkc = request.getParameter("bkc") ;
   String user = request.getParameter("user") ;
   if(Convert.isNullOrEmpty(user))
	   user="" ;
   
   boolean b_zoom_show = !"false".equals(request.getParameter("zoom")) ;
	//String op = request.getParameter("op");
	String path = request.getParameter("path");
	UAHmi uahmi = UAUtil.findHmiByPath(path) ;
	if(uahmi==null)
	{
		out.print("no hmi node found") ;
	}
	String hmitt = uahmi.getTitle();
	UANode topn = uahmi.getTopNode() ;
	UANodeOCTagsCxt cxtn = uahmi.getBelongTo() ;
	UAPrj prj = null ;
	String prjid = "" ;
	String prjname = "" ;
	String hmiid = uahmi.getId() ;
	String path_in_prj = null ;
	boolean b_station_ins=false;
	if(topn instanceof UAPrj)
	{
		prj = (UAPrj)topn ;
		prjid = prj.getId() ;
		prjname = prj.getName() ;
		path_in_prj = uahmi.getNodeCxtPathInPrj() ;
		b_station_ins = prj.isPrjPStationIns() ;
	}
	
	String res_ref_id="" ;
	String reslibid = "" ;
	String resid = "" ;

	if(topn instanceof IResNode)
	{
		res_ref_id = reslibid = ((IResNode)topn).getResLibId();
		//.getResNodeUID() ;
	}
	
	
	boolean bprj = topn instanceof UAPrj ;
	UADev owner_dev = null;
	DevDef owner_def = null ;
	RecManager rec_mgr = null ;
	if(bprj)
	{
		prj = (UAPrj)topn;
		owner_dev = uahmi.getOwnerUADev() ;
		if(owner_dev!=null)
			owner_def = owner_dev.getDevDef() ; 
	}
	
	if(owner_def!=null)
	{// use UADev as top res_ref_id
		res_ref_id = prj.getResLibId() ;
		reslibid = owner_def.getResLibId() ;
		resid = owner_def.getId();
	}
	if(owner_dev!=null)
	{
		res_ref_id =reslibid= prj.getResLibId() ;
		//reslibid = owner_dev.getId();
		resid = owner_dev.getId();
	}
	
	AlertDef alert_def = null;
	JSONObject alert_def_lvl_jo =null;
	if(prj!=null)
	{
		rec_mgr = RecManager.getInstance(prj) ;
		alert_def = AlertManager.getInstance(prjid).getAlertDef();
		alert_def_lvl_jo = alert_def.LVL_toJO() ;
	}
	
	PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
	String n_w_p = "" ;
	boolean can_write = true ;
	if(pa!=null)
	{
		can_write  = pa.checkWriteRight(path, request) ;
		n_w_p = Convert.plainToJsStr(pa.getNoWriteRightPrompt()) ;
	}
	String conn_brk_prompt = uahmi.getConnBrokenPrompt() ;
	if(Convert.isNullOrEmpty(conn_brk_prompt))
		conn_brk_prompt = "Connection is Broken." ;
	String not_run_prompt = uahmi.getNotRunPrompt() ;
	if(Convert.isNullOrEmpty(not_run_prompt))
		not_run_prompt = "Project is not running." ;
	
	//String bd_css = "" ;
	String bkcolor = uahmi.getBkColor() ;
	if(Convert.isNullOrEmpty(bkc) && Convert.isNotNullEmpty(bkcolor))
	{
		//bd_css = "background-color:"+bd_css ;
		bkc = bkcolor ;
		
	}
	if(Convert.isNullOrEmpty(bkc))
		   bkc = "#1e1e1e";
	//String repname = rep.getName() ;%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title><%=hmitt %></title>
<%--
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/hmi_util.js?v=<%=Config.getVersion()%>"></script>
<script src="/_js/oc/oc.min.js?v=<%=Config.getVersion()%>"></script>
<link type="text/css" href="/_js/oc/oc.css?v=<%=Config.getVersion()%>" rel="stylesheet" />
<link href="/_js/font6/css/all.css" rel="stylesheet">
 --%>
 <jsp:include page="head.jsp">
 	<jsp:param value="true" name="oc_min"/>
 </jsp:include>
 <script src="/_js/oc/hmi_util.js?v=<%=Config.getVersion()%>"></script>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}
.hd {font-size:16px;top:6px;}
th
{
	border:1px solid;
}
.top {
	position: fixed;
	
	left: 0;
	top: 0;
	bottom: 0;
	z-index: 999;
	height: 45px;
	width:100%;
	text-align: left;
	margin:0px;
	padding:0px;
	overflow: hidden
}


.left {
	position: fixed;
	float: left;
	left: 0;
	top: 0px;
	bottom: 0;
	z-index: 999;
	width: 45px;
	overflow-x: hidden
}


.left_pan {
	position: fixed;
	float: left;
	left: 45px;
	top: 45px;
	bottom: 0;
	z-index: 999;
	width: 145px;
	overflow-x: hidden
}

.right {
	position: fixed;
	float: right;
	right: 0;
	top: 0px;
	bottom: 0;
	z-index: 999;
	width: 250px;
	height: 100%;
	overflow-x: hidden
}

.mid {
	position: absolute;
	left: 0px;
	right: 0px;
	top: 0px;
	bottom: 0;
	z-index: 998;
	width: auto;
	overflow: hidden;
	box-sizing: border-box
}

.top_btn
{
	color:#009999;
	margin-top: 5px;
	margin-left:20px;
	cursor: pointer;
}

.top i:hover
{
color: #fdd000;
}

.lr_btn
{
	margin-top: 10px;
	color:#009999;
	cursor: pointer;
}

.lr_btn_div
{
	margin-top: 0px;
	color:#858585;
	background-color:#eeeeee;
	cursor: pointer;
}

.lr_btn_btm
{
	margin-bottom: 20px;
	position:absolute;
	left:5px;
	bottom:20px;
	color:#858585;
	
	cursor: pointer;
}

.left i:hover{
color: #fdd000;
}

.lr_btn i:hover
{
color: #fdd000;
}

.right i:hover{
color: #ffffff;
}

.props_panel_edit
{
	position0: absolute;
	left: 0px;
	right: 0px;
	top: 18px;
	bottom0: 50px;
	height:80%
	z-index: 998;

	overflow-y: auto;
	vertical-align:top;
	box-sizing: border-box
}

.props_panel_pos
{
	position: absolute;
	bottom: 50px;
	
	z-index: 998;
	box-sizing: border-box
}

.top_menu_close {
    font-family: Tahoma;
    border: solid 2px #ccc;
    padding: 0px 5px;
    text-align: center;
    font-size: 12px;
    color: blue;
    position: absolute;
    top: 2px;
    line-height: 14px;
    height: 14px;
    width: 26px;
    border-radius: 14px;
    -moz-border-radius: 14px;
    background-color: white;
}

.top_menu_left{
	position:absolute;z-index: 50000;width: 25;height:25;TOP:100px;right:0px;
	text-align: center;
	font-size: 12px;
 font-weight: bold;
 background-color:#4770a1;
 color: #eeeeee;
 line-height: 35px;
 border:2px solid;
border-radius:5px;
//box-shadow: 5px 5px 2px #888888;
}

.top_win_left
{
border:solid 3px gray;		
background-color:silver;
top:0;
left:30;
height:230;
width:830;
padding:1px;
line-height:21px;
border-radius:15px;
-moz-border-radius:15px;
box-shadow:0 5px 27px rgba(0,0,0,0.3);
-webkit-box-shadow:0 5px 27px rgba(0,0,0,0.3);
-moz-box-shadow:0 5px 27px rgba(0,0,0,0.3);
_position:absolute;
_display:block;
z-index:10000;
}

.left_panel_win
{
position:absolute;display:none;z-index:1000;left:45px;
background-color: #eeeeee;
top:0px;height:100%;
}
.left_panel_bar
{
height:30px;
}

.layui-tab {
    margin: 0px;
    padding:0px;
    text-align: left!important;
    height:35px;
}
.layui-tab-content {
    padding: 0px;
}

.oper
{
position: absolute;width:45px;height:45px;right:10px;background-color:#67e0e3;top:10px;z-index: 60000;cursor: pointer;
}

.oper i
{
margin-top:5px;
}



.overlay_msg
{
	position:absolute;
	background:#888888;
	opacity:0.8;
	clear:both;	
	top:50px;
	left:50px;
	border:solid 3px;
	text-align:center;
	vertical-align:middle;
	width:300px;
	height:130px;
	zIndex:65535;
}

.pwin
{
	position: absolute;
	right:60px;top:180px;width:500px;height:300px;
	background-color: #555555;
	z-index: 60002;
	opacity: 0.5;color: #bbbbbb;
}

.pwin:hover
{
	opacity: 1.0;
}

.pwin .op
{
	position: absolute;
	right:0px;
	top:0px;
	background-color: #aaaaaa;
}

.navwin
{
	position: absolute;
	left:0px;top:0px;width:100px;height:100%;
	background-color: #555555;
	
	z-index:65534;
	opacity: 0.5;color: #bbbbbb;
}

.navwin:hover
{
	opacity: 1.0;
}

.navwin .op
{
	position: absolute;
	right:0px;
	top:0px;
	background-color: #aaaaaa;
}

.nav_item
{
	position:relative;
	width:90%;
	left:3%;
	height:90px;
	
	margin-top: 20px;
	text-align: center;
}

.nav_item:hover
{
	background-color: yellow;
}

.nav_item .icon
{
	width:50px;
	height:50px;
	
	font-size:50px;
}

.nav_cur
{
	border:2px solid;
	border-color:#17c6a3;
}

.data_list_c::-webkit-scrollbar {
    width: 10px;
}

.data_list_c::-webkit-scrollbar-thumb {
    background: #ccc; 
    border-radius: 5px; 
}

.data_list_c::-webkit-scrollbar-thumb:hover {
    background: #81ec26;
}

.ui_list_c
{
display: flex;
 flex-wrap: wrap;
}

.ui_item
{
position:relative;
	width:80px;
	height:80px;
	border:0px solid ;
	border-color:#0699f0;
	margin:5px;
}

.ui_item .t
{
	position:relative;
	font-size: 16px;
	left:0px;
	right:0px;
	top:5px;
	line-height:12px;
}

.ui_item:hover
{
	background-color:#f3cf56;
}

.ui_item img
{
	position:relative;
	top:5px;
	width:50px;
	height:50px;
	border:1px solid;
	
}

#ws_updt {z-index:65534;position: absolute;right:10px;bottom: 10px;color:#95ec28;}
</style>

</head>
<script type="text/javascript">
dlg.dlg_top=true;

var b_station_ins = <%=b_station_ins%>;
</script>
<body class="layout-body" >
<div style="z-index: 60000"><button onclick="cxt_rt()" >cxtrt</button></div>
<%--
		<div class="left " style="background-color: #aaaaaa;overflow: hidden;">
			<div id="leftcat_rep_unit" class0="lr_btn_div" onclick="leftcat_sel('rep_unit','Project Lib')"><i class="fa fa-cube fa-3x lr_btn"></i><br>Project</div>
			<div id="leftcat_basic_di" onclick="leftcat_sel('basic_di','Basic')"><i class="fa fa-circle-o fa-3x lr_btn" ></i><br>Basic</div>
			<div id="leftcat_basic_icon" onclick="leftcat_sel('basic_icon','Basic Icons')"><i class="fa fa-picture-o fa-3x lr_btn"></i><br>Icon</div>
			<div id="leftcat_pic" onclick="leftcat_sel('pic','Pictures Lib',500)"><i class="fa fa-cubes fa-3x lr_btn"></i><br>Pic Lib</div>
			<div id="leftcat_comp" onclick="leftcat_sel('comp','HMI Components',500)"><i class="fa fa-cogs fa-3x lr_btn"></i><br> Components</div>
		</div>
		
		<div id="left_panel" class="left_panel_win" pop_width="300px" >
			<div class="left_panel_bar" >
				<span id="left_panel_title" style="font-size: 20px;">Basic Shape</span><div onclick="leftcat_close()" class="top_menu_close"  style="position:absolute;top:1px;right:10px;top:2px;">X</div>
			</div>
			<iframe id="left_pan_iframe" src="" style="width:100%;height:90%;overflow:hidden;margin: 0px;border:0px;padding: 0px" ></iframe>
		</div>
		 --%>
		 
		<div class="mid">
			<div id="main_panel" style="border: 0px solid #000; width: 100%; height: 100%; background-color:<%=bkc%>">
			<%-- 
				<div id="win_act_store" style="position: absolute; display0: none; background-color: #cccccc;z-index:1">
					<div class="layui-btn-group">
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="add new store"  >
					    <i class="layui-icon">&#xe654;</i>
					  </button>
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
					    <i class="layui-icon">&#xe642;</i>
					  </button>
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
					    <i class="layui-icon">&#xe640;</i>
					  </button>
					</div>
				</div>
				
				<div id="win_act_conn" style="position: absolute; display: none; background-color: #cccccc;z-index:1">
					<div class="layui-btn-group" style="width:40px">
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="" >
					    <i class="layui-icon">&#xe654;</i>
					  </button>
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
					    <i class="layui-icon">&#xe642;</i>
					  </button>
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
					    <i class="layui-icon">&#xe640;</i>
					  </button>
					</div>
				</div>
				--%>
		</div>
		
		</div>

<%
LinkedHashMap<String,UAPrj.HmiNavItem> path2tt = prj.getClientHMIPath2NavList() ;
if(path2tt!=null && path2tt.size()>0)
{
%>
<div id="nav_list" style="" class="navwin">
<%
	for(Map.Entry<String,UAPrj.HmiNavItem> p2t:path2tt.entrySet())
	{
		boolean bcur = false ;
		String pp = p2t.getKey() ;
		bcur = pp.equals(path_in_prj) ;
		String nav_cur = "" ;
		if(bcur)
			nav_cur = "nav_cur" ;
		UAPrj.HmiNavItem navi = p2t.getValue() ;
		String icon = navi.icon ;
		String tt = navi.title ;
		String color = navi.color ;
		if(Convert.isNullOrEmpty(icon))
			icon = "f1d8" ;
		if(Convert.isNullOrEmpty(color))
			color = "#76d170" ;
%><div class="nav_item <%=nav_cur %>" onclick="nav_hmi('<%=pp%>')">
		<span class="icon"><i class="fa" style="color:<%=color%>;">&#x<%=icon %></i></span>
		<div class="tt"><%=tt %></div>
</div>
<%
	}
%>
</div>
<%
}
%>
<div style="z-index:65534;position: absolute;right:0px;top:0px">
<%
if(b_zoom_show)
{
%>
	<div id="oper_fitwin" class="oper" style="top:10px"><i class="fa fa-crosshairs fa-3x" title="fit windows"></i></div>
	<div id="oper_zoomup" class="oper" style="top:60px"><i class="fa fa-plus fa-3x" title="zoom up"></i></div>
	<div id="oper_zoomdown" class="oper" style="top:110px"><i class="fa fa-minus fa-3x" title="zoom down"></i></div>
	<div id="oper_alert" class="oper" style="top:180px;border:1px solid;border-color:#469424;background-color: #1e1e1e;" title="show alerts"><i id="oper_alert_i" class="fa fa-bell fa-3x"></i></div>
	<div id="oper_data" class="oper" style="top:230px;border:1px solid;border-color:#469424;background-color: #1e1e1e;color:#83ec21" title="show tags data"><i id="oper_data_i" class="fa fa-list-alt fa-3x"></i></div>
	<div id="oper_ui" class="oper" style="top:280px;border:1px solid;border-color:#469424;background-color: #1e1e1e;color:#ffd898" title="show UI Dialog List"><i class="fa fa-area-chart fa-3x"></i></div>
<%
}
else
{
%>
	<div id="oper_alert" class="oper" style="top:10px;border:1px solid;border-color:#469424;background-color: #1e1e1e;" title="show alerts"><i id="oper_alert_i" class="fa fa-bell fa-3x"></i></div>
	<div id="oper_data" class="oper" style="top:60px;border:1px solid;border-color:#469424;background-color: #1e1e1e;color:#83ec21" title="show tags data"><i id="oper_data_i" class="fa fa-list-alt fa-3x"></i></div>
	<div id="oper_ui" class="oper" style="top:110px;border:1px solid;border-color:#469424;background-color: #1e1e1e;color:#ffd898" title="show UI Dialog List"><i class="fa fa-area-chart fa-3x"></i></div>
<%
}
%>
	<div id="alert_list_c" style="display:none;width:700px" class="pwin">
 		<span class="op">
 		    <button type="button" class="layui-btn layui-btn-xs layui-btn-warn" onclick="show_alerts_his()" title="Show Alerts History"><lan:g>history_d</lan:g></button>
			<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="hide_alerts()" ><i class="fa fa-times"></i></button>
		</span>
		<div class="hd"><lan:g>alerts,list</lan:g></div>
 		<div class="" style="overflow-y: auto;width:100%;top:25px;bottom:2px;position: absolute;">
 			<table cellpadding="0" cellspacing="0" style="width:100%;">
            <thead>
                <tr>
                  <th><lan:g>lvl</lan:g></th>
                	<th><lan:g>time</lan:g></th>
                    <th><lan:g>tag</lan:g></th>
                    <th><lan:g>type</lan:g></th>
                    <th><lan:g>val</lan:g></th>
                    <th><lan:g>prompt</lan:g></th>
                </tr>
            </thead>
 
            <tbody id="alert_list" >
                
            </tbody>
        </table>
 		</div>
 	</div>
 	
 	<div id="data_list_c" style="display:none;top:230px;width:800px;" class="pwin" >
 		<span class="op">
			<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="hide_datas()" title="hidde"><i class="fa fa-times"></i></button>
		</span>
		<div class="hd"><lan:g>data,list</lan:g></div>
 		<div class="data_list_c" style="overflow-y: auto;width:100%;top:25px;bottom:2px;position: absolute;">
 			<table cellpadding="0" cellspacing="0" style="width:100%;">
            <thead>
                <tr>
                	<th><lan:g>title</lan:g></th>
                	<th><lan:g>up_dt</lan:g></th>
                    <th><lan:g>chg_dt</lan:g></th>
                    <th><lan:g>valid</lan:g></th>
                    <th><lan:g>val</lan:g></th>
                    <th><lan:g>oper</lan:g></th>
                </tr>
            </thead>
 
            <tbody id="data_list" >
<%
LinkedHashMap<UATag,String> tag2tt = uahmi.getShowTag2Title() ;
JSONArray jarr_dtags = new JSONArray() ;
StoreManager storem = StoreManager.getInstance(prjid) ;
for(Map.Entry<UATag,String> en:tag2tt.entrySet())
{
	UATag tag = en.getKey() ;
	String tt = en.getValue() ;
	String pp = tag.getNodeCxtPathIn(cxtn) ;
	String tagp = tag.getNodePathCxt();//.getNodePath() ;
	
	List<StoreOut> storeos = storem.findStoreOutsByTag(tag, true, true);
	boolean b_his = (storeos!=null && storeos.size()>0) ;
	jarr_dtags.put(pp) ;
%>
	<tr id="dtag_<%=pp %>" title="<%=tagp %>">
	  <td><%=tt %></td>
	  <td class="updt"></td>
	  <td class="chgdt"></td>
	  <td class="valid"></td>
	  <td class="val"></td>
	  <td >
<%

if(rec_mgr!=null && rec_mgr.checkTagCanRecord(tag))
{
	String tmppath = tag.getNodeCxtPathInPrj() ;
	RecTagParam rtp = rec_mgr.getRecTagParam(tag) ;
	if(rtp!=null)
	{
%>
<button onclick="rec_tag_show('<%=tmppath %>','<%=tag.getTitle() %> [<%=tmppath %>]')" title="show inner recorded history" style="background-color:#4def3e">&nbsp;<i class="fa fa-line-chart" ></i>&nbsp;</button>
<%
	}
}

if(b_his)
{
	for(StoreOut so:storeos)
	{
		String outtp = so.getOutTp() ;
		String outtpt = so.getOutTpTitle() ;
		String outid = so.getId() ;
%>
<button onclick="show_data_his('<%=outtp %>','<%=outid %>','<%=tagp%>','<%=tt %>')" title="show outer data source history - <%=outtpt%>">&nbsp;<i class="fa fa-line-chart" /></i>&nbsp;</button>
<%
	}
}
%>
	  </td>
	</tr>
<%
}
%>
            </tbody>
        </table>
 		</div>
 	</div>
 	
 	<div id="ui_list_c" style="display:none;top:280px;width:500px;height:280px" class="pwin" >
 		<span class="op">
			<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="hide_uis()" title="hidde"><i class="fa fa-times"></i></button>
		</span>
		<div class="hd"><lan:g>dlg_ui,list</lan:g></div>
 		<div class="ui_list_c" style="overflow-y: auto;width:100%;top:25px;bottom:2px;position: absolute;">
<%
	for(UIItem uii:UIManager.getInstance(prj).getId2Items().values())
	{
		List<String> tagids = uii.getTagIds() ;
		if(tagids!=null && tagids.size()>0)
		{
			boolean ball_under=true ;
			for(String tmpid:tagids)
			{
				UATag tag = cxtn.findTagById(tmpid) ;
				if(tag==null)
				{
					ball_under = false;
					break ;
				}
			}
			if(!ball_under)
				continue ;  //
		}
		String uiid = uii.getId() ;
		String tt = uii.getTitle() ;
		String tip="" ;
		if(tt.length()>10)
		{
			tip = tt ;
			tt = tt.substring(10)+".." ;
		}
		String url = Convert.plainToHtml(uii.getUrl()) ;
		int w = uii.getWidth() ;
		int h = uii.getHeight() ;
		
		String icon = uii.getIconUrl() ;
		if(Convert.isNullOrEmpty(icon))
			icon = "/_iottree/res/ui_def.png" ;
%>
	<div class="ui_item" uiid="<%=uiid%>" uitt="<%=tt %>" ui_url="<%=url%>" ui_w="<%=w %>" ui_h="<%=h%>">
	    <img src="<%=icon %>" />
		<div class="t" title="<%=tip%>"><%=tt %></div>
	</div>
<%
	}
%>
 		</div>
 	</div>
</div>

<div id="ws_updt"></div>
	<%-- 
<script src="/_iottree/di_div_comps/echarts.min.js"></script>
<script src="/_iottree/di_div_comps/switchs/comp_button.js"></script>
<script src="/_iottree/di_div_comps/meters/comp_gauge2.js"></script>
 --%>
<script>
var show_tick = false;
var prjid = "<%=prjid%>" ;
document.addEventListener('touchmove', function (event) {
	    event.preventDefault();
 }, false);
document.addEventListener('touchmove', function (event) {
window.event.returnValue = false;
}, false);

var layuiEle ;
var path="<%=path%>";
var hmi_user="<%=user%>";
var hmi_path = path;
var ppath = "<%=path.substring(0,path.lastIndexOf("/")+1)%>";
var prj_name = "<%=prjname%>" ;
var hmi_id="<%=hmiid%>" ;

var res_ref_id ="<%=res_ref_id%>";
var res_lib_id="<%=reslibid%>";
var res_id="<%=resid%>";

var can_write=<%=can_write%>;
var no_write_p = "<%=n_w_p%>" ;

var conn_brk_prompt = "<%=conn_brk_prompt%>" ;
var not_run_prompt = "<%=not_run_prompt%>" ;
//$util.hmi_can_write = can_write;

var alert_def_lvl_jo = <%=alert_def_lvl_jo%> ;

function get_alert_lvl(lv)
{
	if(!alert_def_lvl_jo) return null ;
	if(lv<=0||lv>5)
		lv = alert_def_lvl_jo.def_lvl ;
	for(let lvl of alert_def_lvl_jo.lvls)
	{
		if(lvl.lvl==lv)
			return lvl ;
	}
	return null ;
}

function get_alert_lvl_color(lv)
{
	let lvl = get_alert_lvl(lv) ;
	if(!lvl || !lvl.color) return "yellow";
	return lvl.color;
}

layui.use('element', function(){
	layuiEle = layui.element;
  
  //…
});

$('#oper_fitwin').click(function()
{
	draw_fit();
});

$('#oper_zoomup').click(function()
{
	zoom(-1)
});

$('#oper_zoomdown').click(function()
{
	zoom(1)
});

$("#oper_alert").click(function()
{
	show_or_hide_alerts() ;
});

$("#oper_data").click(function()
{
	show_or_hide_datas() ;
});

$("#oper_ui").click(function()
{
	show_or_hide_uis() ;
});


function add_tab()
{
	
}
var panel = null;
//var editor = null ;

var loadLayer = null ;
var intedit =null;

var hmiModel=null;
var hmiView=null;

function on_panel_mousemv(p,d)
{
	$("#p_info").html("["+p.x+","+p.y+"] - ("+Math.round(d.x*10)/10+","+Math.round(d.y*10)/10+")");
}


function draw_fit()
{
	if(loadLayer==null)
		return ;
	loadLayer.ajustDrawFit();
}

function zoom(v)
{
	panel.ajustDrawResolution(0,0,v) ;
}


function init_iottpanel()
{
	oc.DrawItem.G_REF_LIB_ID =res_ref_id ;
	hmiModel = new oc.hmi.HMIModel({
		temp_url:"/hmi_ajax.jsp?op=load&path="+path,
		comp_url:"/comp_ajax.jsp?op=comp_load",
		hmi_path:path,hmi_user:hmi_user
	});
	
	hmiModel.setCanWrite(can_write,()=>{
		dlg.msg(no_write_p) ;
	})

	panel = new oc.hmi.HMIPanel("main_panel",res_lib_id,res_id,{
		on_mouse_mv:on_panel_mousemv,
		on_model_chg:on_model_chg
	});

	//editor = new oc.DrawEditor("edit_props","edit_events",panel,{
	//	plug_cb:editor_plugcb
	//}) ;
	hmiView = new oc.hmi.HMIView(hmiModel,panel,null,{
		copy_paste_url:"util/copy_paste_ajax.jsp",
		show_only:true,
		on_model_loaded:()=>{
			//console.log("loaded") ;
			draw_fit()
			setTimeout("draw_fit();",1000);
		},
		on_new_dlg:(p,title,w,h)=>{
			var fp = p ;
			if(p.indexOf("/")!=0)
				fp = ppath+p ;
			dlg.open(fp,
					{title:title,w:w+'px',h:h+'px'},
					['Cancel'],
					[
						function(dlgw)
						{
							dlg.close();
						}
					]);
		},
		on_new_win:(p)=>{
			
		}
	});
	
	hmiView.init();
	
	loadLayer = hmiView.getLayer();
	intedit = hmiView.getInteract();
	
}


function on_model_chg()
{
	//tab_notify();
}



function draw_fit()
{
	if(loadLayer==null)
		return ;
	loadLayer.ajustDrawFit();
}

function print_cache()
{
	if(loadLayer==null)
		return ;
	var sts = loadLayer.getRunCacheST();
	console.log(sts) ;
}

var bInRefresh=false;
var lastRefreshDT = -1 ;

function refresh_dyn()
{
	if(bInRefresh)
		return ;
	if(new Date().getTime()-lastRefreshDT<2000)
		return ;
	try
	{
		bInRefresh = true;
		hmiModel.refreshDyn(function(){
			lastRefreshDT = new Date().getTime();
			bInRefresh = false;
		});
	}
	finally
	{
		
	}
}

//setInterval("hmiModel.refreshDyn();",5000);
//setInterval("refresh_dyn()",2000);

function btn_load_unit()
{
	send_ajax("t_ajax.jsp","id=u_u1",function(bsucc,ret){
		//alert(ret);
		oc.DrawUnit.addUnitByJSON(ret);
	}) ;
}

layui.use('form', function(){

});



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



var left_cur = null ;

function leftcat_sel(n,t,w)
{
	if(w==undefined)
		w = "300px" ;
	else
		w = w+"px" ;
	if(left_cur!=null)
	{
		//slide_toggle($('#left_panel'));
		hide_toggle($('#left_panel'))
		if(left_cur==n)
		{//close only		
			$('.lr_btn_div').removeClass("lr_btn_div");
			left_cur=null ;
			return ;
		}
	}
	
	//if()
	left_cur=n;
	$('.lr_btn_div').removeClass("lr_btn_div");
	$("#leftcat_"+n).addClass("lr_btn_div") ;
	$("#left_panel_title").html(t) ;
	if("basic_icon"==n)
		document.getElementById("left_pan_iframe").src="../pic/icon_fa.jsp" ;
	else
		document.getElementById("left_pan_iframe").src="hmi_left_"+n+".jsp" ;
	
	//top_menu_hide_other('filter');
	//$('#left_panel').hide();
	//$('#topm_filter_panel').slideToggle();
	var r = slide_toggle($('#left_panel'),w);
	//$(this).toggleClass("top_menu_tog");
}

function leftcat_close()
{
	$('.lr_btn_div').removeClass("lr_btn_div");
	left_cur=null ;
	slide_toggle($('#left_panel'));
}

var resize_cc = 0 ;
$(window).resize(function(){
	panel.updatePixelSize() ;
	resize_cc ++ ;
	//if(resize_cc<=1)
	draw_fit();
	});
	


function log(txt)
{
	console.log(txt) ;
}

var overlay_div = null ;
var overlay_msg_div = null ;

function show_overlay(bshow,title)
{
	if(overlay_div == null)
	{
		overlay_div = document.createElement('div');
		overlay_div.style.position = 'absolute';
		overlay_div.style.background = "#000000";
		//overlay_div.style.filter = 'alpha(opacity=40)';
		overlay_div.style.opacity = 0.4;
		overlay_div.style.top = 0;
		overlay_div.style.left = 0 ;
		overlay_div.style.width = '100%';
		overlay_div.style.height = '100%';
		
		overlay_div.style.zIndex=65530;
		document.body.appendChild(overlay_div);
	}
	
	if(overlay_msg_div == null)
	{
		overlay_msg_div = $(document.createElement('div'));//;
		var wh = $(window).height();
		var ww = $(window).width();
		var w=500;
		var h=w*(1-0.618);
		var left=ww/2-w/2;
		var top=wh/2-h/2;
		overlay_msg_div.css("position","absolute");
		overlay_msg_div.css("background","#888888");
		overlay_msg_div.css("opacity","0.8");
		overlay_msg_div.css("clear","both");	
		overlay_msg_div.css("top",top+"px");
		overlay_msg_div.css("left",left+"px");
		overlay_msg_div.css("border","solid 3px");
		overlay_msg_div.css("text-align","center");
		overlay_msg_div.css("vertical-align","middle");
		overlay_msg_div.css("width",w+"px");
		overlay_msg_div.css("height",h+"px");
		overlay_msg_div.css("zIndex","65535");
		overlay_msg_div.css("color","#ffffff");
		overlay_msg_div.css("font-size","30px");
		
		//overlay_msg_div.addClass("overlay_msg");
		$(document.body).append(overlay_msg_div);

	}
	
	overlay_msg_div.html(`<span style="display:table-cell; vertical-align:middle;color:#990000">\${title}</span>`);
	if(bshow)
	{
		overlay_msg_div.css("display","table");
		overlay_div.style.display = '';
	}
	else
	{
		overlay_msg_div.css("display","none");
		overlay_div.style.display = 'none';
	}
	//
}


var b_conn_first=true;

var last_blink=-1 ;

function ws_conn()
{
	var url = 'ws://' + window.location.host + '/_ws/hmi/'+prj_name+"/"+hmi_id;
	if('https:' == document.location.protocol)
		url = 'wss://' + window.location.host + '/_ws/hmi/'+prj_name+"/"+hmi_id;
    if ('WebSocket' in window) {
        ws = new WebSocket(url);
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(url);
    } else {
        log('WebSocket is not supported by this browser.');
        return false ;
    }
    
    ws.onopen = function () {
        //setConnected(true);
        log('Info: WebSocket connection opened.');
        ws_opened = true;
        show_overlay(false);
        hmiModel.setWebSocket(ws);
    };
    ws.onmessage = function (event) {

    	//console.log(event.data) ;
    	if(show_tick)
    		$("#ws_updt").html(new Date().format_local('yyyy-MM-dd hh:mm:ss.SSS')) ;
    	var str = event.data ;
    	var k = str.indexOf("\r\n") ;
    	if(k<=0)
    		return ;
    	var firstln = str.substring(0,k);
    	str = str.substring(k+2) ;
    	
    	var d = null,s=null ;
    	//console.log(event.data);
    	eval("s="+firstln) ;
    	hmiModel.updateServerInfo(s);
    	eval("d="+str) ;
    	if(d.cxt_rt)
    	{
    		hmiModel.updateRtNodes(d.cxt_rt);
    		update_data_list(d.cxt_rt) ;
    	}
    	
    	let iob =$("#oper_alert_i") ; 
    	if(d.has_alert)
    	{
    		
    		iob.css("color","red") ;
    		let ms = new Date().getTime();
    		if(ms-last_blink>500)
    		{
        		if("none"==iob.css("display"))
        			iob.css("display","");
        		else
        			iob.css("display","none");
    			last_blink = ms ;
    		}
    		
    		if(d.alerts)
    		{
    			update_alert_list(d.alerts);
    		}
    	}
    	else if(d.has_tag_alert)
    	{
    		iob.css("color","red") ;
    		let ms = new Date().getTime();
    		if(ms-last_blink>500)
    		{
        		if("none"==iob.css("display"))
        			iob.css("display","");
        		else
        			iob.css("display","none");
    			last_blink = ms ;
    		}
    		
    		if(d.tag_alerts)
    		{
    			update_tag_alert_list(d.tag_alerts);
    		}
    	}
    	else
    	{
    		iob.css("color","#494949").css("display","") ;
    		update_alert_list(null);
    		update_tag_alert_list(null);
    	}
    		
    	
    	if(d.prj_run || b_station_ins)
    		show_overlay(false);
    	else
    		show_overlay(true,not_run_prompt);
    	 
    	if(b_conn_first)
		{
			b_conn_first=false;
			
		}
    };
    
    ws.onclose = function (event) {
    	show_overlay(true,conn_brk_prompt);
    	ws_disconn();
        log('Info: WebSocket connection closed, Code: ' + event.code + (event.reason == "" ? "" : ", Reason: " + event.reason));
    };
    
    return true;
}

function update_alert_list(alerts)
{
	if(!alerts)
	{
		$("#alert_list").html("") ;
		return ;
	}
	let tmps = "" ;
	for(let alert of alerts)
	{
		let trigger_c = alert.trigger_c?("color:"+alert.trigger_c):"" ;
		
		for(let item of alert.items)
		{
			let dt = new Date(item.t_dt).toShortGapNow();
			tmps += `<tr style="\${trigger_c};">
				<td>\${alert.lvl}</td>
	            <td>\${dt}</td>
	            <td>\${alert.t}</td>
	            <td>\${item.tt}</td>
	            <td>\${item.tp}</td>
	            <td>\${item.val}</td>
	            
	            <td>\${item.prompt}</td>
	        </tr>
			` ;
		}
	}
	
	$("#alert_list").html(tmps) ;
}

function update_tag_alert_list(alerts)
{
	if(!alerts)
	{
		$("#alert_list").html("") ;
		return ;
	}
	
	
	let tmps = "" ;
	for(let alert of alerts)
	{
		let trigger_c = true?("color:"+alert.trigger_c):"" ;
		let lv = alert.lvl ;
		let color = get_alert_lvl_color(lv) ;
		//console.log(lv,color) ;
		let item = alert ;

		let dt = new Date(item.trigger_dt).toShortGapNow();
		tmps += `<tr style="color:\${color};">
			<td style="text-align:center">L\${alert.lvl}</td>
            <td>\${dt}</td>
            <td title="\${alert.tag_path}\r\n\${alert.tag_tpath}">\${alert.tag_t}</td>
            <td>\${item.evt_tpt}</td>
            <td>\${item.trigger_v}</td>
            <td>\${item.evt_prompt}</td>
        </tr>
		` ;
	}
	$("#alert_list").html(tmps) ;
}

function ws_disconn() {
	
    if (ws != null) {
        ws.close();
        ws = null;
    }
    ws_opened = false;
}

var ws = null;
var ws_last_chk = -1 ;
var ws_opened = false;

function check_ws()
{
	if(ws!=null&&ws_opened)
	{
		ws_last_chk = new Date().getTime();
		return ;
	}

	if(ws==null)
	{
		ws_disconn();
		ws_conn();
		ws_last_chk = new Date().getTime();
		return ;
	}
	
	//ws_opened==false;
	var dt = new Date().getTime();
	if(dt-ws_last_chk<20000)
		return ;
	//time out
	ws_disconn();
	ws_conn();
	ws_last_chk = new Date().getTime();
	return ;
}



//////////edit panel
$(document).ready(function()
{
	$('#edit_panel_btn').click(function()
	{
	$('#edit_panel').slideToggle();
	$(this).toggleClass("cerrar");
	});
	
	$('#lr_btn_fitwin').click(function()
	{
	draw_fit();
	});

	init_iottpanel();
	
	check_ws();
	setInterval(check_ws,5000) ;
});

//if(prj_name!=null&&prj_name!="")
//	ws_conn();

function cxt_rt()
{
	send_ajax("/hmi_ajax.jsp",{path:path,tp:"rt"},(bsucc,ret)=>{
		if(!bsucc)
			return ;
		if(typeof(ret) == 'string')
			eval("ret="+ret) ;
		hmiModel.updateRtNodes(ret);
		//var rtn = hmiModel.getCxtRtNode();
		//alert(JSON.stringify(rtn)) ;
	},false) ;
}

function show_or_hide_alerts()
{
	
	let dis= $("#alert_list_c").css("display");
	if("none"==dis)
	{
		let y = $("#oper_alert").offset().top;
		$("#alert_list_c").css("top",y+"px").css("display","");
	}
	else
		$("#alert_list_c").css("display","none");
}

function hide_alerts()
{
	$("#alert_list_c").css("display","none");
}

function show_alerts_his()
{
	event.stopPropagation();
	let u = "/prj_evt_alert_sel.jsp?prjid="+prjid;//"/prj_alert_his.jsp?prjid="+prjid;
	dlg.open(u,{title:"<lan:g>alert,his</lan:g>"},
			['<lan:g>close</lan:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function show_or_hide_datas()
{
	let dis= $("#data_list_c").css("display");
	if("none"==dis)
	{
		let y = $("#oper_data").offset().top;
		$("#data_list_c").css("top",y+"px").css("display","");
	}
		
	else
		$("#data_list_c").css("display","none");
}

function hide_datas()
{
	$("#data_list_c").css("display","none");
}

function rec_tag_show(tagpath,title)
{
	if(!prjid)
		return ;
	dlg.open_win("/prj_tag_rec.jsp?prjid="+prjid+"&tag="+tagpath,
			{title:"<lan:g>tag,rec,his</lan:g> - "+title,w:960,h:650,wh_auto:true},
			[],
			[]);
}

function show_data_his(outtp,outid,tagp,title)
{
	event.stopPropagation();
	dlg.open_win("/prj_data_"+outtp+".jsp?outid="+outid+"&prjid="+prjid+"&tag="+tagp,
			{title:"<lan:g>data,his</lan:g> - "+title,w:960,h:650},
			[],
			[]);
}


function show_or_hide_uis()
{
	let dis= $("#ui_list_c").css("display");
	if("none"==dis)
	{
		let y = $("#oper_ui").offset().top;
		$("#ui_list_c").css("top",y+"px").css("display","");
	}
	else
		$("#ui_list_c").css("display","none");
}

function hide_uis()
{
	$("#ui_list_c").css("display","none");
}



var dtags = <%=jarr_dtags%>;

function update_data_list(cxt_rt)
{
	for(let dtag of dtags)
	{
		let tr = document.getElementById("dtag_"+dtag) ;
		if(!tr)
			continue ;
		let ss = dtag.split('.') ;
		let curob = cxt_rt ;
		for(let i = 0 ; i < ss.length-1 ; i ++)
		{
			let s = ss[i] ;
			let bgit=false;
			for(let sub of curob.subs)
			{
				if(sub.n==s)
				{
					curob = sub;
					bgit=true;break ;
				}
			}
			if(!bgit)
			{
				curob=null;break ;
			}
		}
		if(!curob)
			continue ;
		let tag = null ;
		let tagn = ss[ss.length-1] ;
		for(let t of curob.tags)
		{
			if(t.n==tagn)
			{
				tag = t ;break ;
			}
		}
		if(tag==null)
			continue ;
		
		$(tr).find("td[class$='updt']").html(new Date(tag.dt).format_local('yyyy-MM-dd hh:mm:ss'));
		$(tr).find("td[class$='chgdt']").html(new Date(tag.chgdt).format_local('yyyy-MM-dd hh:mm:ss')) ;
		$(tr).find("td[class$='val']").html(tag.strv) ;
		$(tr).find("td[class$='valid']").html(tag.valid?'√':'×') ;
	}
	// console.log(cxt_rt) ;
}

$(".ui_item").click(function(){
	let ob = $(this) ;
	let uiid = ob.attr("uiid") ;
	let uitt = ob.attr("uitt") ;
	let url = ob.attr("ui_url") ;
	let w = parseInt(ob.attr("ui_w")||"900") ;
	let h = parseInt(ob.attr("ui_h")||"600") ;
	dlg.msg(uiid+uitt+url) ;
	dlg.open_win(url,
			{title:uitt,w:w,h:h},
			[],
			[]);
}) ;

function nav_hmi(p)
{
	p = '/'+prj_name+'/'+p.replace(/\./g, '/');
	dlg.msg(p) ;
	document.location.href=p ;
}

async function  f()
{
	const iottc = chrome?.webview?.hostObjects?.iottree_client;
	
	if(!iottc)
		return;
	
	let vv = await iottc.get_loc_lic("123");
		
}

f();

//setInterval("cxt_rt()",5000);
</script>
</body>
</html>