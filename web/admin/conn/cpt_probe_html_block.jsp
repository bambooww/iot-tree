<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*,org.json.*,
  org.iottree.core.util.*,
  org.iottree.core.conn.html.*,
	org.iottree.core.util.logger.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>
<%
//HtmlParser.readUrl() ;
//String url = request.getParameter("url") ;
//if(url==null)
//	url = "" ;

//String name ="" ;
//String title ="" ;
%>
<html>
<head>
<title>html block</title>
<jsp:include page="../head.jsp"></jsp:include>
<script src="/_js/jstree/jstree.min.js"></script>
<link rel="stylesheet" href="/_js/jstree/themes/default/style.min.css" />
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}

table, th, td
{
border:1px solid;
}
th
{
	font-size: 12px;
	font-weight: bold;
}
td
{font-size: 12px;
}

.pi_edit_table tr:hover {
	background-color: #979797;
}

#eps_list tr:hover{
background-color: #979797;
}

.prop_table
{
width:99%;
border: 0px;
margin: 0 auto;
}

.prop_table tr>td
{
	border: 0;
	height:100%
}

.prop_table tr>div
{
	border: 0;

}

.prop_edit_cat
{
border: 1px solid #cccccc;
height:400px;
padding: 3px;
margin: 2px;
overflow: auto; 
}

.prop_edit_panel
{
border: 1px solid #cccccc;
height:200px;
padding: 0px;
margin: 2px;
overflow: auto;
}

.prop_edit_path
{
font-weight:bold;
border: 1px solid #cccccc;
background-color:#f0f0f0;
padding: 3px;
margin: 2px;
overflow: hidden;
}

.prop_edit_desc
{
border: 1px solid #cccccc;
background-color:#f0f0f0;
height:48px;
padding-left:3px;
padding-right:3px;
padding-bottom: 0px;
padding-top: 0px;
margin-left: 2px;
margin-right: 2px;
margin-top: 0px;
margin-bottom: 0px;
overflow: hidden;
}

.site-dir li {
    line-height: 26px;
    margin-left: 20px;
    overflow: visible;
    list-style-type: square;
}
li {
    list-style: none;
}

.site-dir li a {
    display: block;
    color: #333;
    cursor:pointer;
    text-decoration: none;
}


.site-dir li a.layui-this {
    color: #01AAED;
}

.pi_edit_table
{
width:100%;
border: 0px solid #b4b4b4;
margin: 0 auto;
}


.pi_edit_table tr>td
{
	border: 1px solid #b4b4b4;
	height:100%;
	
	
}

.pi_edit_table .td_left
{
	padding-left: 20px;
}

.pi_edit_table tr>div
{
	border: 0;

}

.pi_sel
{
background-color: #0078d7;
color:#ffffff;
}

.pi_edit_unit
{
border: 0px;
width:100%;
}

.left_sel
{
 background-color: #1e90ff;
}

.trace_pts span
{
	height:30px;
	border:1px solid;
	border-color: #499ef3;
	margin:5px;
	white-space: nowrap;
	display:inline-block;
	padding:5px;
}

.nav-tabs li
{
	border-bottom: 1px solid rgba(0, 0, 0, 0.3);
	border-top-left-radius: 4px;
	border-top-right-radius: 4px;
	cursor: pointer;
	padding: 6px;
}

.active
{
	border-width: 1px;
	border-style: solid;
	border-color: rgba(0, 0, 0, 0.3) rgba(0, 0, 0, 0.3) transparent;  // 下边框为透明
	border-image: initial;
	border-top-left-radius: 4px;
	border-top-right-radius: 4px;
	cursor: pointer;
	padding: 6px;
}


.tab_text {
	font-size: 12px;
}

.eis span
{
height:30px;
	border:1px solid;
	border-color: #499ef3;
	margin:5px;
	white-space: nowrap;
	display:inline-block;
	padding:5px;
}

.subs span
{
height:30px;
	border:1px solid;
	border-color: #068997;
	margin:5px;
	white-space: nowrap;
	display:inline-block;
	padding:5px;
}

.eis span:hover
 {
	background-color: grey;
 }
 
 .ei_sel
 {
 	background-color: grey;
 }
</style>
<script>
dlg.resize_to(1024,760);
</script>
</head>
<body>
<table class="prop_table" style="border:solid 1px" >
	<tr>
	 <td colspan="5">
	 <div id="prop_edit_path" class="prop_edit_path" style="height:50px">&nbsp;
	 URL:<input type="text" id="input_url" name="input_url" style="width:70%" value="" readonly="readonly"/><br>
	 Name:<input type="text" id="input_name" name="input_name"  value=""/>&nbsp;&nbsp;&nbsp;
	 Title:<input type="text" id="input_title" name="input_title" value=""/>
	
	 </div>
	 </td>
	</tr>
  <tr>
    <td style="width:45%;vertical-align: top;" >
    <div id="" class="prop_edit_path" style="height0:100px">Trace Points:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <div id="trace_pts" class="trace_pts">
<%--
        <span id="tpt_">（今天） <i class="fa-solid fa-check"></i></span>
        <span id="tpt_">（明天） <i class="fa-solid fa-check"></i></span>
--%>
      </div>
      Txt <input id="tpt_txt" value=""/>  Must Have<input id="tpt_mh" type="checkbox"  checked="checked"/>
      <button class="layui-btn layui-btn-xs " title="input " onclick="set_trace_pt_clk()"><i class="fa-solid fa-plus"></i>Set Trace Point</button>
    <button class="layui-btn layui-btn-xs layui-btn-warm" title="input " onclick="do_trace()"><i class="fa-solid fa-eye-dropper"></i>Trace Root</button>
    <span id="trace_info"></span>
    &nbsp;<span id="uplvl_info" style="font:bold;color:blue">0</span>
    <button class="layui-btn layui-btn-xs layui-btn-normal" title="input " onclick="do_uplvl(true)"><i class="fa-solid fa-arrow-up"></i>Upper Level</button>
    <button class="layui-btn layui-btn-xs layui-btn-normal" title="input " onclick="do_uplvl(false)"><i class="fa-solid fa-arrow-down"></i>Down Level</button>
    </div>
<%--
    <div id="left_list" class="prop_edit_cat" style="height:20px;">
    	
	</div>
	 --%>
    <div id="left_tree" class="prop_edit_cat" style="height:280px;">
    	
	</div>
	<ul class="nav nav-tabs">
		<li class="active active-tab">preview</li>
		<li class="">source</li>
	</ul>
	<div class="tabbox">
		<div>
		<Iframe id="preview_f" style="width:100%;height:180px" ></Iframe>
		</div>
		<div>
		<textarea id="preview_t" style="width:100%;height:180px"></textarea>
		</div>
	</div>
	
    </td>
    <td style="width:55%;vertical-align: top;" >
       <table style="border:0px solid;height:100%;width:100%">
       <tr style="width:100%;height:100%;border:solid 0px">
	    <td style="width:100%;vertical-align: top;height:100%"  >
	    <div id="prop_edit_path" class="prop_edit_path">Extract Points:
	    <div class="prop_edit_panel" style="height:160px;width:100%;overflow: auto;">
	    	<table style="width:100%">
	    		<thead>
	    		   <tr>
	    			<th>Name</th>
	    			<th>Title</th>
	    			<th>Extract</th>
	    			<th>Oper</th>
	    		   </tr>
	    		</thead>
	    		<tbody id="eps_list" class="eps_list">
	    			
	    		</tbody>
	    	</table>
	    </div>
    </div>
	    
	    <div id="prop_edit_path" class="prop_edit_path">Extractable data:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	      Name:<input id="ep_name" style="width:100px"/> Title:<input id="ep_title" style="width:100px"/>
	      <button id="btn_tag_syn" class="layui-btn layui-btn-xs layui-btn-primary" title="export" onclick="set_extract()"><i class="fa-solid fa-arrow-up"></i>&nbsp;&nbsp;<i class="fa-solid fa-arrow-up"></i></button>
	      <br>
	      Selected Item :<span id="eds_sel" style="font:bold;color:blue;"></span>
    </div>
	    <div class="prop_edit_panel" style="height:360px;overflow:hidden;">
	    	
	    	<div id="right_pp" class="eis" style="height:300px;overflow: auto;"></div>
	    	
	    	<div id="right_substr"  class="subs" style="height:60px;overflow: auto;">Sub Items:</div>
	    </div>
    </td>
  </tr>
</table>
</td>
</tr>
</table>
</body>
<script type="text/javascript">

var ow = dlg.get_opener_w();

var url = ow.get_probe_url() ;

var curbk = ow.cur_block ; 

var run_js = ow.get_run_js() ;

var tmpbuf_fp = ow.get_tmpbuf_fp();

function init_from_parent()
{
	$("#input_url").val(url) ;
	if(!curbk)
	{
		curbk={}
		return ;
	}
		
	$("#input_name").val(curbk.n);
	$("#input_title").val(curbk.t);
	$("#uplvl_info").html(curbk.trace_up_lvl);
	
	
	var tracepts = curbk.trace_pts ;
	if(tracepts)
	{
		for(var tpt of tracepts)
		{
			set_trace_pt(tpt)
		}
	}
	
	var extractpts = curbk.extract_pts;
	if(extractpts)
	{
		
	}
	
	refresh_trace_pts();
}



function set_trace_pt(tpt)
{
	if(!curbk.trace_pts)
		curbk.trace_pts=[];
	var s = curbk.trace_pts.length ;
	for(var i = 0 ; i < s ; i ++)
	{
		if(curbk.trace_pts[i].txt==tpt.txt)
		{
			curbk.trace_pts[i] = tpt ;
			return ;
		}
	}
	curbk.trace_pts.push(tpt) ;
}

function del_trace_pt(txt)
{
	var pts = curbk.trace_pts ;
	if(!pts)
		return false;
	for(var i = 0 ; i < pts.length ; i ++)
	{
		if(pts[i].txt==txt)
		{
			pts.splice(i,1) ;
			refresh_trace_pts();
			return true ;
		}
	}
	return false;
}

function refresh_trace_pts()
{
	var tmps = "" ;
	if(curbk.trace_pts)
	{
		for(var tpt of curbk.trace_pts)
		{
			var ico = "&nbsp;<i class='fa-regular fa-square-check'></i>" ;
			if(!tpt.mh)
				ico = "&nbsp;<i class='fa-regular fa-square'></i>" ;
			tmps += "<span id='tpt_"+tpt.txt+"'>"+tpt.txt+ico+"&nbsp;&nbsp;<a href='javascript:del_trace_pt(\""+tpt.txt+"\")'>X</a></span>";
		}
	}
	
	$("#trace_pts").html(tmps) ;
}


init_from_parent();

function set_trace_pt_clk()
{
	var txt = $("#tpt_txt").val() ;
	var mh = $("#tpt_mh").is(':checked');
	txt=  trim(txt) ;
	if(!txt)
	{
		dlg.msg("please input Trace Txt") ;
		return ;
	}
	if(txt.indexOf('\"')>=0 || txt.indexOf("\'")>=0)
	{
		dlg.msg("txt contains invalid char \\\' \\\"") ;
	}
	set_trace_pt({txt:txt,mh:mh});
	refresh_trace_pts();
}

$("#input_url").keyup(function(e){
	if(e.which === 13)
		do_trace() ;
	}) ;
	
function do_uplvl(b_up)
{
	var op = "trace_up";
	if(!b_up)
		op =  "trace_down";
send_ajax("./html/html_ajax.jsp",{op:op},function(bsucc,ret){
		
		if(!bsucc || ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		var ob=null;
		eval("ob="+ret)
		//console.log(ob);
		//tree_init(ob);
		tree_init(ob.ele_id) ;
		curbk.trace_up_lvl = ob.trace_up_lvl ;
		$("#uplvl_info").html(curbk.trace_up_lvl);
	});
	
	
}

function do_trace()
{
	if(!tmpbuf_fp)
	{
		dlg.msg("no buffered content found!") ;
		return ;
	}
	var u = $("#input_url").val();
	u = trim(u) ;
	if(!u)
	{
		dlg.msg("please input url") ;
		return ;
	}
	if(!curbk.trace_pts)
	{
		dlg.msg("no trace point set") ;
		return ;
	}
	
	send_ajax("./html/html_ajax.jsp",{op:"trace",bfp:tmpbuf_fp,run_js_page:run_js.run_js_page,run_js_to:run_js.run_js_to,
		jstr:JSON.stringify(curbk)},function(bsucc,ret){
		
		if(!bsucc || ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		var ob=null;
		eval("ob="+ret)
		//tree_init(ob);
		tree_init(ob.ele_id) ;
		var enc = ob.encode ;
		$("#trace_info").html(enc) ;
		if(enc && ow)
			ow.probe_enc = enc ;
		//var tree = $('#left_tree');
		//tree.jstree(true).settings.core.data = ob;
		//tree.jstree(true).refresh();
	});
}

function tree_init(rootid)
{
	$.jstree.destroy();
	if(!rootid)
		rootid = "" ;
	
	this.jsTree = $('#left_tree').jstree(
				{
					'core' : {
						'data' : {
							'url' : function(node){
								if(!node||"#"==node.id)
									return "./html/html_ajax.jsp?op=treen&rootid="+rootid;
								else
									return "./html/html_ajax.jsp?op=treen&pnid="+ node.id ;
								  	
						},
							"dataType" : "json",
							"data":function(node){
		                        return {"id" : node.id};
		                    }
						},
						'check_callback' : function(o, n, p, i, m) {
							if(m && m.dnd && m.pos !== 'i') { return false; }
							if(o === "move_node" || o === "copy_node") {
								if(this.get_node(n).parent === this.get_node(p).id) { return false; }
							}
							return true;
						},
						'themes' : {
							'responsive' : false,
							'variant' : 'small',
							'stripes' : true
						}
					},
					'contextmenu' : { //
						
						'items' :(node)=>{
							//this.get_type(node)==='ch''
							//console.log(node)
							var tp = node.original.type
							//console.log(tp) ;
							return this.get_cxt_menu(tp,node.original) ;
		                }
					},
					'types' : {
						'default' : { 'icon' : 'folder' },
						'file' : { 'valid_children' : [], 'icon' : 'file' }
					},
					'unique' : {
						'duplicate' : function (name, counter) {
							return name + ' ' + counter;
						}
					},
					'plugins' : ['state','dnd','types','contextmenu','unique']
				}
		)
		
		
		this.jsTree.on('activate_node.jstree',(e,data)=>{
			on_tree_node_sel(data.node.original)
		})
		
		this.jsTree.on('loaded.jstree', function(e, data){  
		    var inst = data.instance;  
		    var obj = inst.get_node(e.target.firstChild.firstChild.lastChild);  
		    inst.select_node(obj);  
		});
		
		
}

//tree_init()

function on_tree_node_sel(tn)
{
	//console.log(tn) ;
	send_ajax("./html/html_ajax.jsp",{op:"treen_html",pnid:tn.id},(bsucc,ret)=>{
		//$("#right_pp").html(ret) ;
		$("#preview_t").val(ret) ;
		$('#preview_f').contents().find('html').html(ret);
		
		show_extractable_data(tn.id)
	}) ;
}

var cur_extractable = null ;
var cur_eis_data = null ;

function show_extractable_data(tn_id)
{
	send_ajax("./html/html_ajax.jsp",{op:"extractable_data",pnid:tn_id},(bsucc,ret)=>{
		//$("#right_pp").html(ret) ;
		if(!bsucc&&ret.indexOf("{")!=0)
		{
			$("#right_pp").html(ret) ;
			return ;
		}
		var ob = null ;
		eval("ob="+ret) ;
		cur_eis_data = ob;
		var tmps ="" ;
		tmps += "node_path="+ob.np +"<br>" ;
		if(ob.eis)
		{
			for(var ei of ob.eis)
			{
				var tmpt = ei.txt ;
				if(tmpt.length>30)
					tmpt = tmpt.substring(0,30)+"..." ;
				tmps += "<span xp='"+ei.xp+"' title='"+ei.xp+"'>"+tmpt+"</span>"
			}
		}
		$("#right_pp").html(tmps) ;
		
		$("#right_pp span").on("click",function(){
			var xp = $(this).attr("xp") ;
			var txt = $(this).html() ;
			//alert(xp+" "+txt) ;
			cur_extractable = {txt:txt,xp:xp} ;
			refresh_extractable();
		})
	
	}) ;
}

function get_ei_by_xp(xp)
{
	if(!cur_eis_data)
		return null ;
	if(!cur_eis_data.eis)
		return ;
	
	for(var ei of cur_eis_data.eis)
	{
		if(ei.xp==xp)
			return ei ;
	}
	return null ;
	
}
function refresh_extractable()
{
	$("#right_pp span").each(function(){
		var xp = $(this).attr("xp")
		if(cur_extractable!=null&&cur_extractable.xp==xp)
		{
			$(this).addClass("ei_sel") ;
			$("#eds_sel").html(xp) ;
			var ei = get_ei_by_xp(xp);
			var tmps = "" ;
			if(ei && ei.segs)
			{
				var cc = 0;
				
				for(var seg of ei.segs)
				{
					var tmpt = seg;
					cc ++ ;
					if(tmpt.length>30)
						tmpt = tmpt.substring(0,30)+"..." ;
					var tmpxp = xp+"[seg("+cc+")]";
					tmps += "<span xp='"+tmpxp+"' title='"+tmpxp+"'>"+tmpt+"</span>"
				}
			}
			$("#right_substr").html("Sub Items:"+tmps) ;
			$("#right_substr span").on("click",function(){
				var xp = $(this).attr("xp") ;
				var txt = $(this).html() ;
				//alert(xp+" "+txt) ;
				cur_extractable = {txt:txt,xp:xp} ;
				refresh_sub();
			})
		}
		else
			$(this).removeClass("ei_sel") ;
	});
}

function refresh_sub()
{
	$("#right_substr span").each(function(){
		var xp = $(this).attr("xp")
		if(cur_extractable!=null&&cur_extractable.xp==xp)
		{
			$(this).addClass("ei_sel") ;
			$("#eds_sel").html(xp) ;
		}
		else
			$(this).removeClass("ei_sel") ;
	});

}

function set_extract()
{
	var n = $("#ep_name").val();
	var t = $("#ep_title").val();
	if(!n)
	{
		dlg.msg("please input extract point name") ;
		$("#ep_name").focus();
		return ;
	}
	if(cur_extractable==null)
	{
		dlg.msg("please select extractable item below");
		return;
	}
	set_extract_pt({n:n,t:t,txt:cur_extractable.txt,xp:cur_extractable.xp}) ;
	refresh_extract_pts();
}

function set_extract_pt(tpt)
{
	if(!curbk.extract_pts)
		curbk.extract_pts=[];
	var s = curbk.extract_pts.length ;
	for(var i = 0 ; i < s ; i ++)
	{
		if(curbk.extract_pts[i].n==tpt.n)
		{
			curbk.extract_pts[i] = tpt ;
			return ;
		}
	}
	curbk.extract_pts.push(tpt) ;
}

function del_extract_pt(n)
{
	var pts = curbk.extract_pts ;
	if(!pts)
		return false;
	for(var i = 0 ; i < pts.length ; i ++)
	{
		if(pts[i].n==n)
		{
			pts.splice(i,1) ;
			refresh_extract_pts();
			return true ;
		}
	}
	return false;
}

function refresh_extract_pts()
{
	var tmps = "" ;
	if(curbk.extract_pts)
	{
		for(var tpt of curbk.extract_pts)
		{
			var xp = tpt.xp ;
			if(xp.length>50)
				xp = xp.substring(0,50)+".." ;
			tmps += "<tr onclick='extract_pt_clk(this)'><td>"+tpt.n+"</td><td>"+tpt.t+"</td><td>"+xp+"</td><td><i class='fa-solid fa-x' onclick='del_extract_pt_clk(\""+tpt.n+"\")'></i></td></tr>"
		}
	}
	
	$("#eps_list").html(tmps) ;
}

function extract_pt_clk(tr)
{
	var tds = $(tr).children('td');
	var n = tds.eq(0).html();
	var t = tds.eq(1).html();
	$("#ep_name").val(n) ;
	$("#ep_title").val(t) ;
}

function del_extract_pt_clk(n)
{
	if(!curbk.extract_pts)
		return ;
	for(var i = 0 ; i < curbk.extract_pts.length ; i ++)
	{
		var tpt = curbk.extract_pts[i] ;
		if(n==tpt.n)
		{
			curbk.extract_pts.splice(i,1) ;
			refresh_extract_pts();
			return ;
		}
	}
}

refresh_extract_pts();

function tab_init()
{
	$(".tabbox>div").hide() ;
	$(".tabbox>div").eq(0).show() ;
	$(".nav li").click(function(){
		$(".nav li").removeClass("active") ;
		$(this).addClass("active") ;
		var idx = $(this).index() ;
		$(".tabbox>div").hide().eq(idx).show();
	});
}

tab_init();

function do_preview()
{
	var txt = $("#preview_t").val() ;
	$('#preview_f').contents().find('html').html(txt);
}

function on_sz_chg()
{
	var h = $(window).height();
	//$("#right_pp").css("height",(h-100)+"px") ;
	//$("#preview_f").css("height",(h-100-220)+"px") ;
	//$("#left_tree").css("height",(h-100-20)+"px") ;
}

$(window).resize(function(){

	on_sz_chg()
}) ;

on_sz_chg();



function get_block()
{
	var n = $("#input_name").val() ;
	if(!n)
	{
		dlg.msg("Please input name");
		return null;
	}
	var t = $("#input_title").val() ;
	
	//
	curbk.n = n ;
	curbk.t = t;
	return curbk ;
}
//if(url)
//	do_nav();
</script>
</html>