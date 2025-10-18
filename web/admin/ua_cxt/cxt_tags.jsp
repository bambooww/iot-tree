<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return ;
//boolean bdev = "true".equals(request.getParameter("bdev")) ;
//boolean bmgr ="true".equals(request.getParameter("mgr")) ;
boolean bsys = "true".equals(request.getParameter("sys")) ;
boolean bsub = "true".equals(request.getParameter("sub")) ;
String path = request.getParameter("path") ;
UANode node = UAUtil.findNodeByPath(path) ;

if(node==null)
{
	out.print("node not found"); 
	return ;
}
//DevDef devdef = null ;
UANode topn = node.getTopNode() ;
String prj_name = topn.getName();//.getName() ;
String node_id = node.getId() ;
UAHmi hmi = null ;
boolean bhmi = false;
if(node instanceof UAHmi)
{
	bhmi = true ;
	hmi = (UAHmi)node;
}

UAPrj prj = null ;
String prjid = "" ;
if(topn instanceof UAPrj)
{
	prj = (UAPrj)topn ;
	prjid = prj.getId() ;
}

/*
if(node instanceof UAHmi)
{
	bhmi = true ;
	hmi = (UAHmi)node ;
	node = node.getParentNode() ;
}*/
UANodeOCTags node_tags = null;
boolean b_tags=(node instanceof UANodeOCTags);
boolean b_tags_g = false;
/*
if(!(node instanceof UANodeOCTags))
{
	out.print("node has no tags") ;
	return ;
}*/

b_tags_g = node instanceof UANodeOCTagsGCxt ;

boolean ref_locked = false;
if(b_tags_g)
{
	ref_locked = ((UANodeOCTagsGCxt)node).isRefLocked();
}

String node_cxtpath = node.getNodePath();
if(node instanceof UANodeOCTags)
	node_tags = (UANodeOCTags)node;
//UATagList taglist  = node_tags.getTagList() ;

boolean bdevdef = UAUtil.isDevDefPath(path) ;
List<UATag> cur_tags = null; 
List<UANodeOCTags>  tns =null;

boolean brefowner = false;
boolean brefed = false;

String ext_str = null;

if(node_tags!=null)
{
	cur_tags = node_tags.getNorTags() ;
	tns = node_tags.listSelfAndSubTagsNode() ;
	brefowner = node_tags.isRefOwner();
	brefed = node_tags.isRefedNode() ;
	ext_str = node_tags.getExtAttrStr() ;
}


String hmitt = "" ;
if(bhmi)
{
	hmitt ="UI ["+hmi.getNodePath()+"]";
}

String ext_color = "" ;
if(Convert.isNotNullEmpty(ext_str))
	ext_color="color:#17c680" ;

%><html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
<script src="/_js/selectmenu/selectmenu.min.js"></script>
</head>
<style>
body
{
user-select: none;
}
.oc-toolbar .toolbarbtn
{
width:40px;height:40px;margin: 5px;
font-size: 13px;
background-color: #eeeeee
}
.layui-elem-quote {height:50px;}
.rmenu_item:hover {
	background-color: #373737;
}

.tag_row:hover {
	background-color: #a2a2a2;
	//border:2px solid;
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
overflow:hidden;
text-overflow:ellipsis;
}

.tagsel
{
	background-color: #0078d7;
	color: white;
}

.tagsel a
{
	color: white;
}

#div_list_bd button
{
	border:0px;
	background-color: rgba(0,0,0,0);
}
.wbool {width:90px;border:0px solid #999999;height:100%;}
</style>
<body marginwidth="0" marginheight="0">
<form class="layui-form" action="" onsubmit="return false;">
 <blockquote class="layui-elem-quote ">&nbsp;
 <div style="left:20px;top:2px;position:absolute;font-weight:bold;font-size:14px;"><span title=""><%=node.getNodePath() %></span>  <span>[<wbt:g>tags_num</wbt:g>:</span><span id="tags_num"></span>]
 <a href="javascript:bind_ext('<%=node.getNodePath() %>')" id="node_ext_<%=node.getId() %>"  title="Set extended properties" style="<%=ext_color%>"><i class="fa fa fa-paperclip" aria-hidden="true"></i></a>
 <a href="javascript:node_access('<%=node.getNodePath() %>')" id="node_access_<%=node.getId() %>"  title="Access" ><i class="fa fa-paper-plane" aria-hidden="true"></i></a>
 
 </div>
  <div style="left:20px;top:22px;position:absolute;font-size:13px;">
  <%=node.getNodePathTitle() %>
  &nbsp;<input id="search_txt" style="width:100px;height:25px;border:1px solid #ccc;" onkeydown="on_search_key()"/><button style="color:#333;border:1px solid #ccc;height:25px;width:25px;" onclick="on_search()"><i class="fa fa-search"></i></button>
  </div>
  <div style="float: right;margin-right:10px;font:15px solid;color:#fff5e2;top:10px;margin-top:8px;border:0px solid;">
   
   <%
   if(b_tags)
   {
 	if(true) //(bdevdef||!ref_locked)
 	{
 %>

<%
 	}
%>
<button type="button" class="layui-btn layui-btn-xs layui-border-blue" onclick="add_or_modify_tag('')">+<wbt:g>add,tag</wbt:g></button>
 	<button type="button" class="layui-btn layui-btn-xs layui-border-blue" onclick="add_or_modify_tag('',true)">+<wbt:g>add,middle,tag</wbt:g></button>
 	<button type="button" class="layui-btn layui-btn-xs layui-border-blue" onclick="imp_tag()"><wbt:g>imp,tag</wbt:g></button>
 	<button type="button" class="layui-btn layui-btn-xs layui-border-blue" onclick="exp_tag()"><wbt:g>export,tag</wbt:g></button>
 	<%--
 	<button type="button" class="layui-btn layui-btn-sm layui-btn-danger" onclick="del_tag()">+Delete Tag</button>
 	 --%>
  
  <button type="button" class="layui-btn layui-btn-xs layui-border-blue" onclick="to_excel()"><i class="fa fa-arrow-right"></i>Excel</button>
  

<%
   }
%>
 </div>
</blockquote>
<div style="position: absolute;right:1px;top:-8px;">
  <input type="checkbox" id="show_sys"  name="show_sys" lay-filter="show_sys" lay-skin="switch" lay-text="<wbt:g>hide,system,tags</wbt:g>|<wbt:g>show,system,tags</wbt:g>" />
  <input type="checkbox" id="show_sub"  name="show_sub" lay-filter="show_sub" lay-skin="switch" lay-text="<wbt:g>hide,sub,tags</wbt:g>|<wbt:g>show,sub,tags</wbt:g>" />
</div>
<%
if(b_tags)
{
%>
<div id="tb_tags" style="position:absoluate;bottom:120px;top:100px;height:300px;overflow-y: auto;">
<table class="oc_div_list" style="margin-top:0px;width:99%" id="tb_cur" >
  <thead>
     <tr>
     <th style="width:20px;text-align: center;">
        <input type="checkbox" lay-skin="primary"  id="chkall" lay-filter="chkall" />
</th>
        <th style="width:15px;text-align: center;">T</th>
        <th sort_by="id">ID</th>
    	<th sort_by="iid">IID</th>
    	<th sort_by="name"><wbt:g>tag</wbt:g></th>
    	<th sort_by="title"><wbt:g>title</wbt:g></th>
        <th sort_by="addr"><wbt:g>addr</wbt:g></th>
        <th sort_by="valtp" style="width:80px;"><wbt:g>val,type</wbt:g></th>
        <th ><wbt:g>indicator</wbt:g></th>
        <th style="min-width:120px;text-align:right;"><wbt:g>val</wbt:g></th>
        <th><wbt:g>unit</wbt:g></th>
        <th ><wbt:g>alert</wbt:g></th>
        <th><wbt:g>update,time</wbt:g></th>
        <th><wbt:g>change,time</wbt:g></th>
        <th><wbt:g>valid</wbt:g></th>
        <th style="width:110px;"><wbt:g>write</wbt:g></th>
        <th><wbt:g>oper</wbt:g></th>
<%
if(prj!=null)
{
%>
		<th title="inner recorder"><wbt:g>recorder</wbt:g></th>
        <th><wbt:g>store</wbt:g></th>
<%
}
%>
     </tr>
   </thead>
   <tbody id="div_list_bd">

    </tbody>
</table>
<div style="height:180px;"></div>
</div>
<%
}
%>
</form>

<br><br>
</body>
<script>

var prjid = "<%=prjid%>" ;
var prj_name = "<%=prj_name%>" ;
var node_id = "<%=node_id%>" ;
var path = "<%=path%>" ;
var b_tags = <%=b_tags%> ;
var cxt_path= "<%=node_cxtpath%>";
var b_devdef = <%=bdevdef%>;
var b_refed = <%=brefed%>;
var b_sys = <%=bsys%>;
var b_sub = <%=bsub%>;
var sort_by=null ;

var search_txt = null ;

var tags_num = 0;
$("#tags_num").html(tags_num);
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  if(b_sys)
	  	$("#show_sys").attr('checked', 'checked');
	  if(b_sub)
		$("#show_sub").attr('checked', 'checked');
	  
	  form.on('checkbox(chkall)', function(obj){
		  var bshow = obj.elem.checked ;
          sel_tags_all();
      });
	  
	  form.on('switch(show_sys)', function(obj){
		  var bshow = obj.elem.checked ;
		  b_sys = bshow ;
          //document.location.href="cxt_tags.jsp?path="+path+"&sys="+bshow+"&sub="+b_sub ;
          refresh_tags();
      });
	  form.on('switch(show_sub)', function(obj){
		  var bsub = obj.elem.checked ;
		  b_sub = bsub ;
          //document.location.href="cxt_tags.jsp?path="+path+"&sys="+b_sys+"&sub="+bsub ;
		  refresh_tags();
      });
	  
	  form.render();
	});

document.oncontextmenu = function() {
    return false;
}

function on_search()
{
	search_txt = $("#search_txt").val()||"" ;
	refresh_tags();
}

function on_search_key()
{
	if(event.keyCode==13)
		on_search();
}

var b_ctrl_down = false;
var b_shift_down=false;
var b_menu_show=false;

function init_right_menu()
{
	 $(document).keydown(function(e){
		 if(e.keyCode==17)
			 b_ctrl_down=true;
		 else if(e.keyCode==16)
			 b_shift_down = true ;
		// console.log(e.keyCode);
		 });
	 $(document).keyup(function(e){
		 if(e.keyCode==17)
			 b_ctrl_down=false;
		 else if(e.keyCode==16)
			 b_shift_down = false ;
		 });
	$(document.body).mouseup(function(e) {
		if(1==e.which)
		{
			on_tag_mousedown(null);
			on_tag_mouseup(null);
			return ;
		}
		
	    if (3 == e.which)
	    {
	    	$('.sm_container').css("display","none") ;
	    	
	        $(this).selectMenu({
	        	regular : true,
	        	rightClick : true,
	        	data : ()=>{
					var d = [
						
						{ content : '<wbt:g>add,tag</wbt:g>', callback:()=>{
	        				add_or_modify_tag("");
						}},
						{ content : '<wbt:g>add,middle,tag</wbt:g>', callback:()=>{
	        				add_or_modify_tag("",true);
						}},
	        			
						{ content : '<wbt:g>paste,tag</wbt:g>', callback:()=>{
							paste_tag();
						}}
					];
					if(get_selected_tagids()!='')
						d.push({ content : '<wbt:g>copy,selected,tag</wbt:g>', callback:()=>{
							copy_tag(get_selected_tagids());
						}})
					return d;
				}
	        });
	    }
	})
	
	init_tr();
}

function init_tr()
{
	$('#tb_cur tr[id*="ctag_"]').mouseenter(function(e) {
		var t_id = $(this).attr("tag_id");
		var t_path = $(this).attr("tag_path");
    	var t_loc = $(this).attr("tag_loc")=='true';
    	var t_sys = $(this).attr("tag_sys")=='true';
    	
		if(t_loc&&!t_sys)
		{
			on_tag_mouseenter(t_id);
			return ;
		}
	});
	
	$('#tb_cur tr[id*="ctag_"]').mouseout(function(e) {
		var t_id = $(this).attr("tag_id");
		var t_path = $(this).attr("tag_path");
    	var t_loc = $(this).attr("tag_loc")=='true';
    	var t_sys = $(this).attr("tag_sys")=='true';
    	
		if(t_loc&&!t_sys)
		{
			on_tag_mouseout(t_id);
			return ;
		}
	});
	
	$('#tb_cur tr[id*="ctag_"]').mousedown(function(e) {
		var t_id = $(this).attr("tag_id");
		var t_path = $(this).attr("tag_path");
    	var t_loc = $(this).attr("tag_loc")=='true';
    	var t_sys = $(this).attr("tag_sys")=='true';
    	
		if(1==e.which&&t_loc&&!t_sys)
		{
			on_tag_mousedown(t_id);
			return ;
		}
	});
	
	$('#tb_cur tr[id*="ctag_"]').mouseup(function(e) {
		var t_id = $(this).attr("tag_id");
		var t_path = $(this).attr("tag_path");
    	var t_loc = $(this).attr("tag_loc")=='true';
    	var t_sys = $(this).attr("tag_sys")=='true';
    	
		if(1==e.which&&t_loc&&!t_sys)
		{
			e.stopPropagation();
			on_tag_mouseup(t_id);
			return ;
		}
		
	    if (3 == e.which)
	    {
	    	e.stopPropagation();
	    	
	    	//$('body').selectMenu({rightClick : true}) ;//clear outter menu
	    	$('.sm_container').css("display","none") ;
	    	b_menu_show=true;
	        $(this).selectMenu({
	        	title0:'Modify Tag',
	        	regular : true,
	        	rightClick : true,
	        	data : ()=>{
	        		if(false)//b_refed&&!t_sys)
	        		{
	        			return [{ content : '<wbt:g>rename,tag</wbt:g>', callback:()=>{
							rename_tag(t_path,t_id);
						}}];
	        		}
	        		
	        		if(t_loc&&!t_sys)
	        		{
	        			var r=[] ;
	        			r.push({ content : '<wbt:g>modify,tag</wbt:g>', callback:()=>{
								add_or_modify_tag(t_id);
							}});
	        			
	        			r.push({ content : '<wbt:g>copy,tag</wbt:g>', callback:()=>{
								copy_tag($(this).attr("tag_id"));
							}});
	        			if(get_selected_tagids()!='')
	        			{
	        				r.push({ content : '<wbt:g>copy,selected,tag</wbt:g>', callback:()=>{
								
								copy_tag(get_selected_tagids());
							}});
	        				
							r.push({ content : '<wbt:g>move,selected,tag</wbt:g>', callback:()=>{
									move_tag(get_selected_tagids());
							}});
	        				
	        				r.push({ content : '<wbt:g>del,selected,tag</wbt:g>', callback:()=>{
								
								del_tag(get_selected_tagids());
							}});
	        			}
						
	        			
	        			r.push({ content : '<wbt:g>paste,tag</wbt:g>', callback:()=>{
								paste_tag();
							}});
	        			r.push({ content : '<wbt:g>del,tag</wbt:g>', callback:()=>{
								del_tag(t_id);
							}});
						
	        			return r;
	        		}
	        		
					return [];
				}
	        });

	    }
	})

}

init_right_menu();

function copy_tag(tagids)
{
	send_ajax('./tag_ajax.jsp',{op:"copy",path:cxt_path,tagids:tagids},function(bsucc,ret)
			{
				if(!bsucc || ret.indexOf('succ')<0)
				{
					dlg.msg(ret);
					return ;
				}
				dlg.msg("<wbt:g>copied,ok</wbt:g>") ;
			},false);
}

function move_tag(tagids)
{
	dlg.open("../util/prj_tree_node_sel.jsp?prjid="+prjid,{title:"<wbt:g>select,move,tar</wbt:g>",w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>close</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.get_select((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret);
							return ;
						}
						
						//dlg.msg(ret) ;
						
						send_ajax('./tag_ajax.jsp',{op:"move",path:cxt_path,tagids:tagids,tar:ret},function(bsucc,ret)
								{
									if(!bsucc || ret.indexOf('succ=')<0)
									{
										dlg.msg(ret);
										return ;
									}
									let mvn = parseInt(ret.substring(5)) ;
									if(mvn<0)
										dlg.msg("<wbt:g>move,failed</wbt:g>") ;
									else if(mvn==0)
										dlg.msg("<wbt:g>moved,tags</wbt:g>"+mvn) ;
									else
										dlg.msg("<wbt:g>moved,tags</wbt:g>"+mvn+"<wbt:g>ok</wbt:g>") ;
									dlg.close();
									if(mvn>0)
										location.reload() ;
								},false);
					});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
	
}

function paste_tag()
{
	send_ajax('./tag_ajax.jsp',{op:"paste",path:cxt_path},function(bsucc,ret)
			{
				if(!bsucc || ret.indexOf('succ')<0)
				{
					dlg.msg(ret);
					return ;
				}
				//location.reload();
				refresh_tags();
			},false);
}

function copy_paste_tag(path,tagids,cb)
{
	
	send_ajax('./tag_ajax.jsp',{op:"paste",path:path,tag_ids:tagids},function(bsucc,ret)
			{
				if(!bsucc || ret.indexOf('succ=')<0)
				{
					dlg.msg(ret);
					return ;
				}
				var retids = ret.substr(5);
				if(cb)
					cb(retids) ;
				refresh_tags();
			},false);
}

function sel_tags_all()
{
	//var tb = document.getElementById("tb_"+path) ;
	//var inputchkall = document.getElementById("chkall_"+path) ;
	var bchk = $("#chkall").prop("checked") ;
	
	$('input:checkbox').each(function(i){
	       var id = $(this).attr('id');
	      if(id.indexOf("chk_")==0)
	    	  $(this).prop('checked',bchk);
	      });
	form.render();
}

function get_selected_ids_in_table()
{
	var tb = document.getElementById("tb_"+path) ;
	var tb = $(tb) ;
	var r = "" ;
	tb.find("input").each(function(){
		if(!$(this).prop('checked'))
			return ;
	    var id = $(this).attr("id") ;
	    if(id.indexOf("chk_")==0)
	    {
	    	if(r=="")
	    		r += id.substring(4) ;
	    	else
	    		r+=","+id.substring(4) ;
	    }
	  });
	return r ;
}


function add_or_modify_tag(id,bmid)
{
	var tt = "<wbt:g>modify</wbt:g> "+(bmid?"<wbt:g>middle</wbt:g>":"")+" <wbt:g>tag</wbt:g>";
	if(id==null||id=='')
		tt = "<wbt:g>add</wbt:g> "+(bmid?"<wbt:g>middle</wbt:g>":"")+" <wbt:g>tag</wbt:g>" ;
	var u = "./tag_edit.jsp?path="+path+"&id="+id
	if(bmid)
		u+="&mid=true";

		dlg.open(u,{title:tt,w:'500px',h:'400px'},
				['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
				[
					function(dlgw)
					{
						dlgw.do_submit(function(bsucc,ret){
							 if(!bsucc)
							 {
								 dlg.msg(ret) ;
								 return;
							 }
							 
							 ret.path=path ;
							 ret.op = "edit_tag";
							 //ret.id = id ;
							 //console.log(ret);
							 send_ajax('./tag_ajax.jsp',ret,function(bsucc,ret)
								{
									if(!bsucc || ret.indexOf('succ')<0)
									{
										dlg.msg(ret);
										return ;
									}
									dlg.close();
									refresh_tags();
								},false);
					 	});
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
}

function imp_tag()
{
	dlg.open("tag_imp.jsp",{title:"Import Tag",w:'500px',h:'400px'},
				['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
				[
					function(dlgw)
					{
						dlgw.do_submit(function(bsucc,ret){
							 if(!bsucc)
							 {
								 dlg.msg(ret) ;
								 return;
							 }
							 
							 let pm={op:"imp_tag",txt:ret,path:path};
							 send_ajax('./tag_ajax.jsp',pm,function(bsucc,ret)
								{
									if(!bsucc || ret.indexOf('succ=')<0)
									{
										dlg.msg(ret);
										return ;
									}
									dlg.msg("Imported tags number is "+ret.substring(5)) ;
									dlg.close();
									refresh_tags();
								},false);
					 	});
					},
					function(dlgw)
					{
						dlg.close();
					}
	]);
}

function exp_tag()
{
	
	let tags=[];
	let chk_tag_ns = [] ;
	$(".tag_row").each(function(){
		let ob = $(this) ;
		if("true"==ob.attr("tag_loc"))
		{
			let tag_id = ob.attr("tag_id") ;
			let tag_n = ob.attr("tag_n") ;
			let chked = $("#chk_"+tag_id).prop("checked") ;
			if(chked)
				chk_tag_ns.push(tag_n) ;
			
			let val_tp = ob.attr("val_tp") ;
			let addr = ob.attr("addr") ||"";
			let tt = ob.attr("tag_t") ;
			tags.push({n:tag_n,tp:val_tp,addr:addr,t:tt}) ;
		}
	});
	
	dlg.open("tag_export.jsp?cxt_path="+cxt_path,{title:"Export Tag",w:'500px',h:'400px',chk_tag_ns:chk_tag_ns}, //tags:tags,cxt_path:cxt_path
			['<wbt:g>close</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
	]);
}

function rename_tag(p,id)
{
	dlg.open("./tag_rename.jsp?path="+p+"&id="+id,
			{title:"Rename Refered Tag",w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.path=p ;
						 ret.op = "rename_tag";
						 ret.id = id ;
						 //console.log(ret);
						 send_ajax('./tag_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.close();
								refresh_tags();
							},false);
							
						 
						 //location.reload();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_tag(id)
{
	var ret={} ;
	
	ret.path=path ;
	 ret.op = "del_tag";
	 if(id==null||id=='')
	 {
		 id = get_selected_ids_in_table(path);
	 }
	 if(id==null||id=='')
	 {
		 dlg.msg("no tag selected") ;
		 return ;
	 }
	 
	 ret.id = id ;
	 dlg.confirm("<wbt:g>del,selected,tags</wbt:g>？",{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
	 { 
		 send_ajax('./tag_ajax.jsp',ret,function(bsucc,ret)
			{
				if(!bsucc || ret.indexOf('succ')<0)
				{
					dlg.msg(ret);
					return ;
				}
				dlg.close();
				refresh_tags();
			},false);
	 });
}

function w_tag(id)
{
	let ele = $("#ctag_w_"+id) ;
	let strv = null ;
	if(ele.attr("bb")=='true')
		strv = ele.find('input[type="radio"]:checked').attr("value");
	else
		strv = ele.val();
	if(strv==null||strv=="")
	{
		dlg.msg("<wbt:g>pls,input,write,val</wbt:g>") ;
		return ;
	}
	send_ajax("./cxt_dyn_ajax.jsp",{path:path,tagid:id,v:strv,op:"w"},(bsucc,ret)=>{
		dlg.msg(ret) ;
	},false) ;
}

function cxt_rt()
{
	send_ajax("./cxt_dyn_ajax.jsp",{path:path,op:"rt"},(bsucc,ret)=>{
		if(!bsucc)
			return ;
		if(typeof(ret) == 'string')
			eval("ret="+ret) ;
		show_cxt_dyn("",ret) ;
	},false) ;
}

function show_cxt_dyn(p,cxt)
{
	for(var tg of cxt.tags)
	{
		var n = tg.n;
		var tagp =p+n ;
		var bvalid = tg.valid ;
		var dt = tg.dt ;
		var chgdt = tg.chgdt;
		let b_alert = tg.alert||false;
		
		var strv ="";
		if(tg.v!=null)
			strv = tg.strv ;
		var strerr = tg.err ;
		var strdt = "",strdtt="" ;
		var strchgdt = "",strchgdtt="" ;
		if(dt>0)
		{
			strdt =new Date(dt).format("hh:mm:ss")+"."+(dt%1000).toString().padStart(3,'0');//
			strdtt =new Date(dt).format("yyyy-MM-dd hh:mm:ss");
		}
			
		if(chgdt>0)
		{
			strchgdt = new Date(chgdt).format("hh:mm:ss");//
			strchgdtt = new Date(chgdt).format("yyyy-MM-dd hh:mm:ss");//
		}
		
		let bfilter = $(document.getElementById("ctag_v_"+tagp)).attr("filter")=='true' ;
		if(bfilter && strv)
		{
			strv = strv+(bfilter?"<span style='color:green' title='<wbt:g>has,filter</wbt:g>'>[F]</span>":"") ;
			show_ele_html("ctag_v_"+tagp,strv,false) ;
		}
		else
		{
			show_ele_html("ctag_v_"+tagp,strv,true) ;
		}
		
		show_ele_html("ctag_dt_"+tagp,strdt,0,strdtt) ;
		show_ele_html("ctag_chgdt_"+tagp,strchgdt,0,strchgdtt) ;
		var qstr = bvalid==true?"<span style='color:green'>✓</span>":"<span style='color:red'>✘</span>";
		if(!bvalid&&strerr!=null&&strerr!=""&&strerr!=undefined)
			qstr += "<span title='"+strerr+"'>err</span>";
		
		show_ele_html("ctag_q_"+tagp,qstr) ;
		let alert_ele = document.getElementById("ctag_alert_"+tagp) ;
		if(b_alert)
			$(alert_ele).css("color","red");
		else
			$(alert_ele).css("color","gray");
	}
	if(cxt.subs)
	{
		for(var sub of cxt.subs)
		{
			show_cxt_dyn(p+sub.n+".",sub) ;
		}
	}
}

//if(!b_devdef)
//	setInterval(cxt_rt,3000) ;

function log(txt)
{
	console.log(txt) ;
}

var ws = null;
var ws_last_chk = -1 ;
var ws_opened = false;

function ws_conn()
{
    var url = 'ws://' + window.location.host + '/admin/_ws/cxt_rt/'+prj_name+"/"+node_id+
    	"?sys="+b_sys+"&sub="+b_sub;
    //console.log(url) ;
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
    };
    ws.onmessage = function (event) {

    	var str = event.data ;
    	//console.log(str) ;
    	var k = str.indexOf("\r\n") ;
    	if(k<=0)
    		return ;
    	var firstln = str.substring(0,k);
    	str = str.substring(k+2) ;
    	var d = null ;
    	eval("d="+str) ;
    	//console.log(d) ;
    	show_cxt_dyn("",d);
    };
    
    ws.onclose = function (event) {
    	ws_disconn();
        log('Info: WebSocket connection closed, Code: ' + event.code + (event.reason == "" ? "" : ", Reason: " + event.reason));
    };
    return true;
}	

function ws_disconn() {
	
    if (ws != null) {
        ws.close();
        ws = null;
    }
    ws_opened = false;
}


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

function reconn_ws()
{
	ws_disconn();
	ws_conn();
	ws_last_chk = new Date().getTime();
}

if(!b_devdef)
{
	//check_ws();
	setInterval(check_ws,2000) ;
	//setInterval(cxt_rt,3000) ;
}

var MAX_VAL_SHOWLEN = 20 ;

function show_ele_html(n,v,chklen,title)
{
	var ele = document.getElementById(n) ;
	if(ele==null||ele==undefined)
		return ;
	if(chklen&&v!=null&&v.length>40)
	{
		ele.innerHTML = "<span title='"+title+"'>"+v.substr(0,40)+"...</span>";
	}
	else if(title)
	{
		ele.innerHTML = "<span title='"+title+"'>"+v||""+"...</span>";
	}
	else
		ele.innerHTML=v||"" ;
}

function run_script_test(fn)
{
	var scode = document.getElementById('script_test').value ;
	if(scode==null||scode==''||trim(scode)=='')
		return ;
	send_ajax('cxt_script_test.jsp',{'path':cxt_path,txt:scode},
		function(bsucc,ret)
		{
			document.getElementById('script_res').value = ret ;
		},false) ;
}

function refresh_tags()
{
	if(!b_tags)
		return ;
	dlg.loading(true);
	send_ajax("cxt_tags_tb_ajax.jsp",{path:cxt_path,sys:b_sys,sub:b_sub,sortby:sort_by,search_txt:search_txt},function(bsucc,ret){
		dlg.loading(false);
		$("#div_list_bd").html(ret);
		init_tr();
		
		var tn = $('#tb_cur tr[id*="ctag_"]:last-child').attr("tag_num");
		$("#tags_num").html(tn);
		 form.render();
		 reconn_ws();
	},false);
}


$("th").click(function() {
    var sortby = $(this).attr("sort_by");
    if(sortby==undefined||sortby==''||sortby==null)
    	return ;
    if(sort_by==sortby)
    	return ;
    sort_by = sortby ;
    refresh_tags();
});

refresh_tags();

var tag_mdown=false;
var tag_sels=[] ;



function get_selected_tagids()
{
	var tagids = "" ;
	for(var tid of tag_sels)
	{
    	  if(tagids!='')
    		  tagids += ',' ;
    	  tagids += tid;
	}
	return tagids ;
}

function redraw_tags()
{
	$('#tb_cur tr[id*="ctag_"]').each(function(i){
		 var tid = $(this).attr("tag_id");
		 if(tag_sels.indexOf(tid)>=0)
			 //$(this).css("background","#0078d7");
		 	$(this).addClass("tagsel") ;
		 else
			 $(this).removeClass("tagsel");//.css("background","");
	});
}

function find_tag_ids_from_to(f_tid,t_tid)
{
	var tmpids=[];
	$('#tb_cur tr[id*="ctag_"]').each(function(i){
		 var tid = $(this).attr("tag_id");
		tmpids.push(tid) ;
	});
	var f_idx = tmpids.indexOf(f_tid) ;
	var t_idx = tmpids.indexOf(t_tid) ;
	if(f_idx<0||t_idx<0)
		return [] ;
	if(f_idx>t_idx)
	{
		var k = f_idx ;
		f_idx = t_idx ;
		t_idx = k ;
	}
	var ret=[] ;
	for(var i = f_idx ; i <= t_idx ; i ++)
	{
		ret.push(tmpids[i]) ;
	}
	return ret ;
}

function on_tag_mousedown(tagid)
{
	if(b_menu_show)
	{
		b_menu_show=false;
		return ;
	}
		
	tag_mdown = true ;
	if(b_ctrl_down)
	{
		
	}
	else if(b_shift_down)
	{
		if(tag_sels.length<=0)
			return ;
		
		var sids = find_tag_ids_from_to(tag_sels[tag_sels.length-1],tagid);
		if(sids!=null&&sids.length>0)
			tag_sels.pushAll(sids) ;
	}
	else	
		tag_sels=[];
	
	if(tagid!=null&&tagid!=''&&tag_sels.indexOf(tagid)<0)
		tag_sels.push(tagid);
	redraw_tags();
}

function on_tag_mouseenter(tagid)
{
	if(!tag_mdown)
		return ;
	//if(tag_sels.indexOf(tagid)>=0)
	//	tag_sels.remove(tagid) ;
	//else
		tag_sels.push(tagid);
	redraw_tags();
}

function on_tag_mouseout(tagid)
{
	if(!tag_mdown)
		return ;
	
	//tag_sels.remove(tagid);
	//redraw_tags();
}

function on_tag_mouseup(tagid)
{
	tag_mdown= false;
}

function bind_ext(path)
{
	dlg.open("../util/prj_dict_bind_selector.jsp?path="+path,
			{title:"<wbt:g>edit,dict_ext_props</wbt:g>",w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.path=path ;
						 ret.op = "set_ext_attr";
						 //ret.id = id ;
						 console.log(ret);
						 send_ajax('./cxt_node_ext_attr_ajax.jsp',ret,function(bsucc,rr)
							{
								if(!bsucc || rr.indexOf('succ=')<0)
								{
									dlg.msg(rr);
									return ;
								}
								dlg.close();
								var id = rr.substring(5) ;
								if(ret.jstr)
									$("#node_ext_"+id).css("color","#17c680") ;
								else
									$("#node_ext_"+id).css("color","#111111") ;
								//location.reload();
							},false);
							
						 
						 //
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function node_access(p)
{
	window.open(p) ;
}

function rec_tag_set(ob,tag,tt)
{
	let show_c = $(document.getElementById("rec_tag_show_"+tag)) ;
	dlg.open("../store/rec_tag_param_edit.jsp?prjid="+prjid+"&tag="+tag,
			{title:"<wbt:g>tag</wbt:g> "+tt+" <wbt:g>recorder,setup</wbt:g>",w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>unset</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.tag=tag ;
						 ret.prjid=prjid;
						 ret.op = "set";
						 //ret.id = id ;
						 //console.log(ret);
						 send_ajax('../store/rec_tag_param_ajax.jsp',ret,function(bsucc,rr)
							{
								if(!bsucc || rr.indexOf('succ')<0)
								{
									dlg.msg(rr);
									return ;
								}
								if(ret.en)
									$(ob).css("color","green") ;
								else
									$(ob).css("color","#b46c24") ;
								
								show_c.css("display","inline") ;
								dlg.close();
								
							},false);
							
						 
						 //
				 	});
				},
				function(dlgw)
				{
					dlg.confirm('<wbt:g>unset,this,recorder</wbt:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>unset,confirm</wbt:g>"},function ()
				    {
							send_ajax("../store/rec_tag_param_ajax.jsp",{op:"unset",prjid:prjid,tag:tag},function(bsucc,ret){
					    		if(!bsucc || ret!='succ')
					    		{
					    			dlg.msg("<wbt:g>unset,err</wbt:g>:"+ret) ;
					    			return ;
					    		}
					    		//
					    		$(ob).css("color","#d2d2d2") ;
					    		show_c.css("display","none") ;
								dlg.close() ;
					    	}) ;
						});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
	return false;
}

function rec_tag_show(tagpath,title)
{
	if(!prjid)
		return ;
	dlg.open_win("/prj_tag_rec.jsp?prjid="+prjid+"&tag="+tagpath,
			{title:"<wbt:g>tag,recorded,history</wbt:g> - "+title,w:960,h:650,wh_auto:true},
			[],
			[]);
}

function show_data_his(outtp,outid,tagp,title)
{	
	if(!prjid)
		return ;
	dlg.open_win("/prj_data_"+outtp+".jsp?outid="+outid+"&prjid="+prjid+"&tag="+tagp,
			{title:"<wbt:g>data,history</wbt:g> - "+title,w:960,h:650},
			[],
			[]);
}

function resize_taglist()
{
	var h = $(window).height()-110;
	$("#tb_tags").css("height",h+"px");
}

resize_taglist();

$(window).resize(function(){
	resize_taglist();
	});
	
function to_excel()
{
    var exportFileContent = document.getElementById("tb_cur").outerHTML;                
    var blob = new Blob([exportFileContent], {type: "text/plain;charset=utf-8"});
    blob =  new Blob([String.fromCharCode(0xFEFF), blob], {type: blob.type});
   var link = window.URL.createObjectURL(blob); 
    var a = document.createElement("a"); 
    a.download = "tags.xls"; 
    a.href = link;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
}

</script>
</html>