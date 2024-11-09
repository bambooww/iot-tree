<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.cxt.*,
	org.iottree.core.ws.*,
	org.iottree.core.sim.*,
	org.iottree.pro.*,
	org.iottree.core.station.*,
	org.iottree.core.util.xmldata.*
"%><%@ taglib uri="wb_tag" prefix="wbt"%><%//Platfrom may has many prj in it . which is created by msg in and admin

List<UAPrj> prjs = UAManager.getInstance().listPrjs();
String using_lan = Lan.getUsingLang() ;

PlatInsManager platf = PlatInsManager.getInstance() ;
%><!DOCTYPE html>
<html class="">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1">
    <title>IOT-Tree</title>
    <link href="/favicon.ico" rel="shortcut icon" type="image/x-icon">
<jsp:include page="../head.jsp">
<jsp:param value="true" name="tab"/>
</jsp:include>
            <link href="../inc/common.css" rel="stylesheet" type="text/css">
        <link href="../inc/index.css" rel="stylesheet" type="text/css">
        <script type="text/javascript" src="../js/tab.js" ></script>
<link rel="stylesheet" href="../js/tab.css" />
 <style>
 .left
 {
 	position: absolute;
 	left:0px;
 	top:0px;
 	width:30% ;
 	border:1px solid ;
 	height:100%;
 	overflow: auto;
 }
 
  .mid_selected
 {
 	position: absolute;
 	right:0px;
 	top:0px;
 	left:31%;
 	width:29% ;
 	border:1px solid ;
 	height:100%;
 	overflow: hidden;
 }
 
   .mid_list
 {
 	position: absolute;
 	right:0px;
 	top:0px;
 	left:60%;
 	width:29% ;
 	border:1px solid ;
 	height:100%;
 	overflow: auto;
 }
 
 
  .right
 {
 	position: absolute;
 	right:0px;
 	top:0px;
 	width:10% ;
 	border:1px solid ;
 	height:100%;
 	overflow: auto;
 }
 
 
.station
{
	border:1px solid;
	margin-top: 10px;
}
.prj_item
{
	position:relative;
	left:20px;
	right:10px;
	background-color: #cdcdcd;
	margin:5px;
}

.file_list
{
	position:relative;
	border:1px solid;
	margin: 5px;
	height:30%;
}
 </style>
</head>
<body aria-hidden="false">
<div class="left">
<%
Map<String,PStation> id2station = platf.getPStationMap() ;
for(Map.Entry<String,PStation> id2s:id2station.entrySet())
{
	String id = id2s.getKey() ;
	PStation ps = id2s.getValue() ;
	String clientip = ps.RT_getClientIP() ;
	String conndt = Convert.toFullYMDHMS(new Date(ps.RT_getClientOpenDT())) ;
	List<PStation.PrjST> prjsts = ps.RT_getPrjSTs() ;
	%>
	<div class="station">
	<%=ps.getTitle() %> [<%=ps.getId() %>] 
	<button onclick="station_reboot('<%=ps.getId() %>')">reboot</button>
	<button onclick="station_downfiles('<%=ps.getId() %>')">down files</button>
	<button onclick="station_syn_dir('<%=ps.getId() %>')">Syn Dir</button>
	<br> &lt;- <%=clientip %> [<%=conndt %>]
	<br>
<%
	if(prjsts!=null)
	{
		for(PStation.PrjST prjst:prjsts)
		{
			String chked = prjst.isAutoStart()?"checked":"" ;
			String syn_chked = prjst.isDataSynEnable()?"checked":"" ;
			long syn_intv= prjst.getDataSynIntvMs() ;
			
			String failed_keep_chked = prjst.isFailedKeep()?"checked":"" ;
			long keep_max_len = prjst.getKeepMaxLen() ;
			long last_recv_dt = prjst.getLastRecvDT() ;
			String lastrdt = "" ;
			if(last_recv_dt>0)
				lastrdt = Convert.toFullYMDHMS(new Date(last_recv_dt)) ;
%>
	<div class="prj_item">
	<%=prjst.getPrjName() %>  running=<%=prjst.isRunning() %> auto start=<%=prjst.isAutoStart() %>
	  -&gt;[<%=lastrdt%>]
	<br>
		
	 	<button onclick="station_prj_start_stop('<%=ps.getId() %>','<%=prjst.getPrjName() %>',true)">start</button>
	 	<button onclick="station_prj_start_stop('<%=ps.getId() %>','<%=prjst.getPrjName() %>',false)">stop</button>
	 
	 <button title="read prj from station" onclick="station_up_prj('<%=ps.getId() %>','<%=prjst.getPrjName() %>')">up project</button>
<%
	String p_station_prj = ps.getId()+"_"+prjst.getPrjName() ;
	UAPrj p_prj = UAManager.getInstance().getPrjByName(p_station_prj) ;
	if(p_prj!=null)
	{
%>
		 <button title="write prj to station">down project</button> <br>
<%
	}
%>
	 <input type="checkbox" id="autostart_<%=ps.getId() %>_<%=prjst.getPrjName() %>" <%=chked %>/>Auto Start <br>
	 <input type="checkbox" id="syn_en_<%=ps.getId() %>_<%=prjst.getPrjName() %>" <%=syn_chked %>/>Data Syn 
	 Interval <input type="number" id="syn_intv_<%=ps.getId() %>_<%=prjst.getPrjName() %>"  value="<%=syn_intv %>" style="width:65px"/>
	 <br>Failed Keep <input type="checkbox" id="failed_keep_<%=ps.getId() %>_<%=prjst.getPrjName() %>"  <%=failed_keep_chked %> style="width:65px"/>
	 Max Len <input type="number" id="keep_max_len_<%=ps.getId() %>_<%=prjst.getPrjName() %>"  value="<%=keep_max_len %>" style="width:65px"/>
	 <button title="set param" onclick="station_prj_set_pm('<%=ps.getId() %>','<%=prjst.getPrjName() %>')">Set PM</button>
	 
	 </div>
<%
		}
	}
%>
<select id="prjlist_<%=ps.getId() %>">
<%
for(UAPrj prj:prjs)
{
%><option value="<%=prj.getName()%>">[<%=prj.getName()%>] <%=prj.getTitle()%></option>
<%
}
%>
</select>
<button onclick="station_down_prj('<%=ps.getId() %>')">down project</button>
</div>
	<%
}
%>
</div>
<div class="mid_selected">
<div class="left_btm_tab">
    	<ul></ul>
          <div></div>
 </div>
        

</div>


<div  class="mid_list">
File List 
<select id="file_list_start">
	<option value=""> All</option>
	<option value="1h"> -1h</option>
	<option value="2h"> -2h</option>
	<option value="4h"> -4h</option>
	<option value="8h"> -8h</option>
	<option value="16h"> -16h</option>
	<option value="1d"> -1d</option>
	<option value="2d"> -2d</option>
	<option value="4d"> -4d</option>
	<option value="8d"> -8d</option>
	<option value="16d"> -16d</option>
</select>
<button onclick="show_files()">Show Files</button>
<button onclick="show_dirs()">Show Dirs</button>
<div id="file_list" style="height:90%"></div>
</div>
<div class="right">

	 项目列表
		   <select id='m_list' multiple="multiple" style="width: 100%;height: 90%" >
<%
List<String> webapp_ms = Config.getWebappModules() ;
String datafb = Config.getDataDirBase() ;
File dataf = new File(datafb) ;
	for(String s:webapp_ms)
	{
%><option value="web:<%=s %>">web:<%=s %></option>
<%
	}
	for(File tmpf:dataf.listFiles())
	{
		if(!tmpf.isDirectory())
			continue ;
%><option value="data:<%=tmpf.getName() %>">data:<%=tmpf.getName() %></option>
<%
	}
%>
	<option value="lib">lib</option>
	
    	</select>
</div>
</body>
<script type="text/javascript">

var module2files = {} ;

function station_prj_start_stop(stationid,prjname,b_start_stop)
{
	let bautostart = $(`#autostart_\${stationid}_\${prjname}`).prop("checked") ;
	send_ajax("platform_ajax.jsp",
		{op:"station_prj_start_stop",stationid:stationid,prj:prjname,start_stop:b_start_stop,auto_start:bautostart},
		(bsucc,ret)=>{
		dlg.msg(ret) ;
	});
}

function station_prj_set_pm(stationid,prjname)
{
	let bautostart = $(`#autostart_\${stationid}_\${prjname}`).prop("checked") ;
	let data_syn_en = $(`#syn_en_\${stationid}_\${prjname}`).prop("checked") ;
	let data_syn_intv = $(`#syn_intv_\${stationid}_\${prjname}`).val() ;
	let bfailed_keep = $(`#failed_keep_\${stationid}_\${prjname}`).prop("checked") ;
	let keep_max_len  = $(`#keep_max_len_\${stationid}_\${prjname}`).val() ;
	send_ajax("platform_ajax.jsp",
		{op:"station_prj_pm",stationid:stationid,prj:prjname,auto_start:bautostart,data_syn_en:data_syn_en,data_syn_intv:data_syn_intv,
			failed_keep:bfailed_keep,keep_max_len:keep_max_len},
		(bsucc,ret)=>{
		dlg.msg(ret) ;
	});
}

function station_downfiles(stationid)
{
	let fn = 0 ;
	for(let m in module2files)
	{
		let fs  = module2files[m] ;
		if(!fs) continue ;
		fn += fs.length ;
	}
	if(fn<=0)
	{
		dlg.msg("please select file at right") ;
		return ;
	}
	
	
}

function station_reboot(stationid)
{
	send_ajax("platform_ajax.jsp",
			{op:"station_reboot",stationid:stationid},
			(bsucc,ret)=>{
			dlg.msg(ret) ;
		});
}

function station_down_prj(stationid)
{
	let prjname = $("#prjlist_"+stationid).val();
	send_ajax("platform_ajax.jsp",
			{op:"station_prj_down",stationid:stationid,prj:prjname},
			(bsucc,ret)=>{
			dlg.msg(ret) ;
		});
}

function station_up_prj(stationid,prjname)
{
	send_ajax("platform_ajax.jsp",
			{op:"station_prj_up",stationid:stationid,prj:prjname},
			(bsucc,ret)=>{
			dlg.msg(ret) ;
		});
}



function station_syn_dir(stationid)
{
	let dirs = read_dir_sels();
	if(!dirs || dirs.length!=1)
	{
		dlg.msg("please select single dir item")
		return ;
	}
	let dir =  dirs[0] ;
	let tmps = `<div id="syn_chk_head">syn [\${stationid}] - [\${dir.m}:\${dir.path}] <br>
			<button onclick="station_syndir_chk_diff('\${stationid}','\${dir.m}','\${dir.path}')">check diff</button>  
			<button onclick="station_syndir_syn('\${stationid}','\${dir.m}','\${dir.path}')">syn to station</button>
			</div>
		<div id="syn_chk_res" style="position: absolute;top:50px;bottom:10px;border:1px solid;left:1px;right:3px;"></div>` ;
	$("#dir_syner").html(tmps) ;
}

function station_syndir_chk_diff(stationid,module,path,diff_cb)
{
	dlg.loading(true);
	send_ajax("platform_ajax.jsp",{op:"station_syn_dir_diff",stationid:stationid,module:module,path:path},(bsucc,ret)=>{
		dlg.loading(false);
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let ob ;
		eval("ob="+ret) ;
		//console.log(ob) ;
		update_syndir_diff(ob);
		if(diff_cb)
			diff_cb(ob) ;
	}) ;
}
	
function update_syndir_diff(ob)
{
	let tmps ="" ;
	for(let nn in ob)
	{
		let fs = ob[nn] ;
		tmps +=`\${nn}<br/><select id="diff_\${nn}" multiple="multiple" style="width:100%;height:30%;">`;
		for(let f of fs)
		{
			tmps += `<option value="\${f}">\${f}</option>`;
		}
		tmps += "</select>" ;
	}
	
	$("#syn_chk_res").html(tmps) ;
}

var cur_syn_mon=null;

function station_syndir_syn(stationid,module,path)
{
	let add_fs = $("#diff_add_fs").val()||[] ;
	let update_fs = $("#diff_update_fs").val()||[] ;
	let del_fs = $("#diff_del_fs").val() ||[];
	if(add_fs.length<=0 && update_fs.length<=0 && del_fs.length<=0)
	{
		dlg.msg("please select file to be syn") ;
		return ;
	}

	//dlg.loading(true);
	//console.log(add_fs,update_fs,del_fs) ;
	send_ajax("platform_ajax.jsp",
			{op:"station_syn_dir_syn",stationid:stationid,module:module,path:path,
				add_fs:add_fs,update_fs:update_fs,del_fs:del_fs},
			(bsucc,ret)=>{
		//dlg.loading(false);
		if(!bsucc||ret!='succ')
		{
			dlg.msg(ret) ;
			return ;
		}
		
		cur_syn_mon = {stationid:stationid,module:module,path:path,
				add_fs:add_fs,update_fs:update_fs,del_fs:del_fs} ;
		dlg.loading(true) ;
		station_syndir_synmon();
	}) ;
}
	
function station_syndir_synmon()
{
	let c = cur_syn_mon ;
	if(!c)
		return ;

	send_ajax("platform_ajax.jsp",{op:"station_syn_dir_syn_mon",stationid:c.stationid,module:c.module,path:c.path},(bsucc,ret)=>{
		if(!bsucc)
		{
			dlg.msg(ret) ;
			dlg.loading(false);
			return ;
		}
		let ack_c = null ;
		eval("ack_c="+ret) ;
		
		if(chk_syn_mon_end(c,ack_c))
		{
			dlg.msg("syn ok") ;
			cur_syn_mon = null ;
			dlg.loading(false);
		}
		else
		{
			setTimeout(station_syndir_synmon,3000) ;
		}
	}) ;
}

function chk_syn_mon_end(c,ack_c)
{
	let ops = ['add_fs','update_fs','del_fs'] ;
	for(let op of ops)
	{
		let fs = c[op] ;
		if(!fs ||fs.length<=0)
			continue ;
		
		let ack_fs = c[op] ;
		if(!ack_fs || ack_fs.length<=0 || fs.length!=ack_fs.length)
			return false;
		
		for(let f of fs)
		{
			if(ack_fs.indexOf(f)<0)
				return false;
		}
	}
	
	return true ;
}

function show_files()
{
	let mdt = $("#file_list_start").val() ;
	let m = $("#m_list").val() ;
	//console.log(m) ;
	if(!m||m.length<=0)
	{
		dlg.msg("pls select module")  ;
		return ;
	}
	m = m[0] ;
	let pm = {op:"module_file_list",m:m,last_mdt:mdt};
	//console.log(pm)
	send_ajax("platform_ajax.jsp",pm,(bsucc,ret)=>{
				if(!bsucc || ret.indexOf('[')!=0)
				{
					dlg.msg(ret) ;
					return ;
				}
				let fs ;
				eval("fs="+ret) ;
				let tmps = `<select id='ff_list' multiple="multiple" style="width: 100%;height:100%">` ;
	    		for(let f of fs)
	    		{
	    			tmps +=  `<option value="\${f}" m="\${m}">\${m}:\${f}</option>` ;
	    		}
	    		tmps += "</select>" ;
	    		$("#file_list").html(tmps) ;
		});
}

function show_dirs()
{
	let mdt = $("#file_list_start").val() ;
	let m = $("#m_list").val() ;
	//console.log(m) ;
	if(!m||m.length<=0)
	{
		dlg.msg("pls select module")  ;
		return ;
	}
	m = m[0] ;
	let pm = {op:"module_dir_list",m:m,last_mdt:mdt};
	//console.log(pm)
	send_ajax("platform_ajax.jsp",pm,(bsucc,ret)=>{
				if(!bsucc || ret.indexOf('[')!=0)
				{
					dlg.msg(ret) ;
					return ;
				}
				let fs ;
				eval("fs="+ret) ;
				let tmps = `<select id='ff_list' multiple="multiple" style="width: 100%;height:100%">` ;
	    		for(let f of fs)
	    		{
	    			tmps +=  `<option value="\${f}" m="\${m}">\${m}:\${f}</option>` ;
	    		}
	    		tmps += "</select>" ;
	    		$("#file_list").html(tmps) ;
		});
}

function update_sel_list()
{
	let tmps ="" ;
	for(let m in module2files)
	{
		tmps +=`\${m}<br/><select id="sel_m_\${m}" multiple="multiple" style="width:100%;min-height:200px;">`;
		let fs = module2files[m] ;
		for(let f of fs)
		{
			tmps += `<option value="\${f}">\${f}</option>`;
		}
		tmps += "</select>" ;
	}
	$("#file_sel").html(tmps) ;
}

function sel_files()
{
	let m2fs = read_file_sel() ;
	for(let m in m2fs)
	{
		let fs = m2fs[m] ;
		let ffs = module2files[m] ;
		if(!ffs)
		{
			module2files[m] = ffs = [] ;
		}
		for(let f of fs)
		{
			if(ffs.indexOf(f)<0)
				ffs.push(f) ;
		}
	}
	update_sel_list() ;
}

function sel_files_del()
{
	for(let m in module2files)
	{
		let selele = document.getElementById(`sel_m_\${m}`) ;
		if(!selele) continue ;
		let vs = $(selele).val() ;
		let fs = module2files[m] ;
		if(vs && fs)
		{
			for(let v of vs)
			{
				let k = fs.indexOf(v) ;
				if(k>=0)
					fs.splice(k,1) ;
			}
		}
	}
	update_sel_list() ;
}

function read_file_sel()
{
	var opts = document.getElementById('ff_list').options ;
	var r = {} ;
	for(var i = 0 ; i < opts.length ; i ++)
	{
		if(opts[i].selected)
		{
			let m = opts[i].getAttribute("m") ;
			let fs = r[m] ;
			if(!fs)
			{
				r[m] = fs = [] ;
			}
			fs.push(opts[i].value) ;
		}
	}
	return r ;
}

function read_dir_sels()
{
	let selele = document.getElementById('ff_list') ;
	if(!selele)
		return null ;
	var opts = selele.options ;
	var r = [] ;
	for(var i = 0 ; i < opts.length ; i ++)
	{
		if(opts[i].selected)
		{
			let m = opts[i].getAttribute("m") ;
			let v = opts[i].value ;
			if(!v.endsWith("/")) continue ;
			r.push({m:m,path:v}) ;
		}
	}
	return r ;
}

$(".left_btm_tab").tab();

let tmps=`<div id="dir_syner" style="position: absolute;top:2px;bottom:2px;border:0px solid;left:1px;right:3px;"></div>`;
$('.left_btm_tab').tab('addTab', {'title': 'Dir Syner', 'id': 'lb_tab_dir_syner', 'content': tmps});

tmps = `
	<div id="file_sel" style="position: absolute;top:2px;bottom:20px;border:0px solid;left:1px;right:34px;"></div>
	<button onclick="sel_files()" style="position: absolute;right:2px;top:300px;width:30px;"><i class="fa fa-arrow-left"></i></button>
	<button onclick="sel_files_del()" style="position: absolute;right:2px;top:350px;width:30px;"><i class="fa fa-arrow-right"></i></button>`
	;
$('.left_btm_tab').tab('addTab', {'title': 'Selected Files', 'id': 'lb_tab_sel_files', 'content': tmps});


$(".left_btm_tab").tab('selectTab', 'lb_tab_dir_syner');
</script>
</html>
