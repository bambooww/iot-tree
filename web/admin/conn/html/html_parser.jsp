<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*,org.json.*,
  org.iottree.core.util.*,
  org.iottree.core.conn.html.*,
	org.iottree.core.util.logger.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>
<%
//HtmlParser.readUrl() ;
String url = request.getParameter("url") ;
if(url==null)
	url = "" ;
%>
<html>
<head>
<title>html parser</title>
<jsp:include page="../../head.jsp"></jsp:include>
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

</style>
<script>
dlg.resize_to(900,580);
</script>
</head>
<body>
<table class="prop_table" style="border:solid 1px" >
	<tr>
	 <td colspan="5">
	 <div id="prop_edit_path" class="prop_edit_path">&nbsp;
	 URL:<input type="text" id="input_url" name="input_url" style="width:70%" value="<%=url%>"/>
	 <button onclick="do_nav()">Navigate</button>
	 </div>
	 </td>
	</tr>
  <tr>
    <td style="width:45%" >
    <div id="" class="prop_edit_path">&nbsp;
    <input type="text" style="width:200px" id="search_txt" />
    <button class="layui-btn layui-btn-xs layui-btn-primary" title="input <%="" %> text" onclick="input_txt()"><i class="fa-solid fa-eye-dropper"></i><%="" %> </button>
    </div>
    <div id="left_list" class="prop_edit_cat" style="height:220px;">
    	
	</div>
    <div id="left_tree" class="prop_edit_cat" style="height:420px;">
    	
	</div>
    </td>
    <td style="width:55%" >
       <table style="border:0px solid;height:100%;width:100%">
       <tr style="width:100%;height:100%;border:solid 0px">
	    <td style="width:100%;vertical-align: top;height:100%"  >
	    <div id="prop_edit_path" class="prop_edit_path">Preview and Probe:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	      <button id="btn_tag_syn" class="layui-btn layui-btn-xs layui-btn-primary" title="export" onclick="bind_export()"><i class="fa-solid fa-arrow-up"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
	      <button id="" class="layui-btn layui-btn-xs layui-btn-primary" title="import" onclick="bind_import()"><i class="fa-solid fa-arrow-down"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
	      <button id="" class="layui-btn layui-btn-xs layui-btn-primary" title="add tag in channel" onclick="add_tag()"><i class="fa-solid fa-plus"></i>&nbsp;&nbsp;<i class="fa fa-tag"></i></button>
    </div>
	    <div id="right_pp"  class="prop_edit_panel" style="height:640px;width:100%;overflow: auto;">
	    	<textarea id="preview_t" style="width:100%;height:220px"></textarea>
	    	<button onclick="do_preview()">preview</button>
	    	<Iframe id="preview_f" style="width:100%;height:400px" ></Iframe>
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
var url = "<%=url%>" ;
$("#input_url").keyup(function(e){
	if(e.which === 13)
		do_nav() ;
	}) ;

function do_nav()
{
	var u = $("#input_url").val();
	u = trim(u) ;
	if(!u)
	{
		dlg.msg("please input url") ;
		return ;
	}
		
	send_ajax("html_ajax.jsp",{op:"nav",url:u},function(bsucc,ret){
		if(!bsucc || ret!='succ')
		{
			dlg.msg(ret) ;
			return ;
		}
		
		tree_init();
	});
}

function tree_init()
{
	$.jstree.destroy();
	
		this.jsTree = $('#left_tree').jstree(
				{
					'core' : {
						'data' : {
							'url' : function(node){
								var pnid = "" ;
								if(node)
									pnid = node.id ;
		                    	return "html_ajax.jsp?op=treen&pnid="+pnid;
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
}

function on_tree_node_sel(tn)
{
	//console.log(tn) ;
	send_ajax("html_ajax.jsp",{op:"treen_html",pnid:tn.id},(bsucc,ret)=>{
		//$("#right_pp").html(ret) ;
		$("#preview_t").val(ret) ;
		$('#preview_f').contents().find('html').html(ret);
	}) ;
}

function do_preview()
{
	var txt = $("#preview_t").val() ;
	$('#preview_f').contents().find('html').html(txt);
}

function on_sz_chg()
{
	var h = $(window).height();
	//$("#right_pp").css("height",(h-100)+"px") ;
	$("#preview_f").css("height",(h-100-220)+"px") ;
	$("#left_tree").css("height",(h-100-220)+"px") ;
}

$(window).resize(function(){

	on_sz_chg()
}) ;

on_sz_chg();

function init_from_parent()
{
	if(!ow)
		return false;
	
	if(!(ow.get_loc_info))
		return false;
	
	var loc = ow.get_loc_info() ;
	loc.
}
if(url)
	do_nav();
</script>
</html>