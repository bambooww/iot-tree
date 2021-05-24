<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return ;
//boolean bdev = "true".equals(request.getParameter("bdev")) ;
boolean bmgr ="true".equals(request.getParameter("mgr")) ;
String path = request.getParameter("path") ;
UANode node = UAUtil.findNodeByPath(path) ;
if(node==null)
{
	out.print("node not found"); 
	return ;
}
if(!(node instanceof UANodeOCTags))
{
	out.print("node has no tags") ;
	return ;
}
UANodeOCTags node_tags = (UANodeOCTags)node;
//UATagList taglist  = node_tags.getTagList() ;

boolean bdevdef = UAUtil.isDevDefPath(path) ;
List<UATag> cur_tags = node_tags.listTags() ;
List<UANodeOCTags>  tns = node_tags.listSelfAndSubTagsNode() ;
boolean brefowner = node_tags.isRefOwner();
boolean brefed = node_tags.isRefedNode() ;
%><html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/ajax.js" ></script>
<script src="/_js/layui/layui.all.js"></script>
  <link   href="/_js/layui/css/layui.css" rel="stylesheet" />
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
<link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
<script src="/_js/selectmenu/selectmenu.min.js"></script>
</head>
<style>
.oc-toolbar .toolbarbtn
{
width:40px;height:40px;margin: 5px;
font-size: 13px;
background-color: #eeeeee
}

.rmenu_item:hover {
	background-color: #373737;
}

table, th, td
{
border:1px solid;
}
th
{
	font-size: 15px;
}


</style>
<body marginwidth="0" marginheight="0">
<form class="layui-form" action="">
<blockquote class="layui-elem-quote"><%=node_tags.getNodePathTitle() %> [<%=node_tags.getNodePath() %>]
<%
if(!brefed)
{
%>
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 <%
 	if(bdevdef)
 	{
 %>
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_modify_tag('')">+Add Tag</button>&nbsp;&nbsp;
<%
 	}
%>
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_modify_tag('',true)">+Add Middle Tag</button>&nbsp;&nbsp;
 	<button type="button" class="layui-btn layui-btn-sm layui-btn-danger" onclick="del_tag()">+Delete Tag</button>
 </div>
<%
}
%>
</blockquote>
<div style="height:100px;overflow: auto;">
<table class="oc_div_list" id="tb_cur" >
  <thead>
     <tr>
<%
	if(!brefed)
	{
%>
        <th><input type="checkbox" lay-skin="primary"  id="chkall" onclick="sel_tags_all()"/></th>
<%
	}
%>
        <th>Mid</th>
        <th>Tag Name</th>
        <th>Title</th>
        <th>Address</th>
        <th>Value Type</th>
        <th>Write</th>
        <th></th>
     </tr>
   </thead>
   <tbody id="div_list_bd_">
<%
for(UATag tag:cur_tags)
{
	String cxtpath=  tag.getNodeCxtPathIn(node_tags) ;
%>
   <tr id="tag_<%=tag.getId() %>"  tag_id="<%=tag.getId()%>">
<%
	if(!brefed)
	{
%>
        <td style="text-align: center;"><input type="checkbox" lay-skin="primary"  id="chk_<%=tag.getId()%>"/></td>
<%
	}
%>
        <td><%=(tag.isMidExpress()?"✔":"") %></td>
        <td title="<%=cxtpath%>"><%=tag.getName() %></td>
        <td><%=tag.getTitle() %></td>
        <td><%=tag.getAddress() %></td>
        <td><%=tag.getValTp() %></td>
        <td><%=(tag.isCanWrite()?"✔":"") %></td>
        <td>
        <a href="javascript:del_tag('<%=tag.getId()%>')"><i class="fa fa-times" aria-hidden="true"></i></a>&nbsp;&nbsp;
        <a href="javascript:add_or_modify_tag('<%=tag.getId()%>')"><i class="fa fa-pencil " aria-hidden="true"></i></a>
        </td>
      </tr>
<%
}
%>
    </tbody>
</table>
</div>
<hr class="layui-bg-green">
 <blockquote class="layui-elem-quote ">Context under  [<%=node_tags.getNodePath() %>]
   <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
  <input type="checkbox" name="show_sys" lay-skin="switch" lay-text="Hide System Tags|Show System Tags" />
 </div>
</blockquote>
<div style="position:absoluate;bottom:120px;top:110px;overflow: auto;">
<table class="oc_div_list" style="margin-top:10px">
  <thead>
     <tr>
        <th>Mid</th>
    <th>Tag</th>
        <th>Address</th>
        <th>Value Type</th>
        
        <th>Value</th>
        <th>Timestamp</th>
        <th>Quality</th>
        <th>Write</th>
     </tr>
   </thead>
   <tbody id="div_list_bd_">
<%
for(UANodeOCTags tn:tns)
{
	//if(tn.getRefBranchNode()!=null)
	//	continue ;
	List<UATag> tags = tn.listTags() ;

	String tn_id = tn.getId() ;
	String tn_path = tn.getNodePath() ;
	for(UATag tag:tags)
	{
		String cxtpath=  tag.getNodeCxtPathIn(node_tags) ;
%>
   <tr id="ctag_<%=tag.getId() %>"  tag_path="<%=tn_path %>" tag_id="<%=tag.getId()%>" cxt_path="<%=cxtpath%>">
        <td></td>
<td title="<%=tag.getNodeCxtPathTitleIn(node_tags)%>"><%=cxtpath%></td>

        <td><%=tag.getAddress() %></td>
        <td><%=tag.getValTp() %></td>
        <td id="ctag_v_<%=cxtpath%>"></td>
        <td id="ctag_dt_<%=cxtpath%>"></td>
        <td id="ctag_q_<%=cxtpath%>"></td>
        <td>
<%
	if(tag.isCanWrite())
	{
%>
        <%=(tag.isCanWrite()?"✔":"") %>
        	<input type="text" id="ctag_w_<%=cxtpath%>" value="" size="8"/><button>w</button>
<%
	}
%>
        </td>
      </tr>
<%
	}
}
%>
    </tbody>
</table>
</div>
</form>
<table width='100%' border='1' height="120">
<tr>
 <td>
 script test <input type='button' value='run' onclick="run_script_test('')"/>
 </td>
 <td>script test result</td>
</tr>
 <tr>
  <td>
   <textarea id='script_test' rows="6" style="overflow: scroll;width:98%"></textarea>
  </td>
  <td>
   <textarea id='script_res' rows="6" style="overflow: scroll;width:98%"></textarea>
  </td>
 </tr>
</table>
</body>
<script>
var path = "<%=path%>" ;
var b_devdef = <%=bdevdef%>;
var b_refed = <%=brefed%>;

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});

document.oncontextmenu = function() {
    return false;
}
function init_right_menu()
{
	if(b_refed)
		return ;
	$('#tb_cur').mouseup(function(e) {
	    if (3 == e.which)
	    {
	    	$('.sm_container').css("display","none") ;
	    	
	        $(this).selectMenu({
	        	title0:'Add Tag',
	        	regular : true,
	        	rightClick : true,
	        	data : ()=>{
					var d = [
						{ content : 'Add Tag', callback:()=>{
							add_or_modify_tag("");
						}}
					];
					return d;
				}
	        });
	    }
	})

	$('#tb_cur tr[id*="tag_"]').mouseup(function(e) {
	    if (3 == e.which)
	    {
	    	e.stopPropagation();
	    	
	    	//$('body').selectMenu({rightClick : true}) ;//clear outter menu
	    	$('.sm_container').css("display","none") ;
	    	var t_path = $(this).attr("tag_path");
	    	var t_id = $(this).attr("tag_id");
	        $(this).selectMenu({
	        	title0:'Modify Tag',
	        	regular : true,
	        	rightClick : true,
	        	data : ()=>{
					var d = [
						{ content : 'Add Tag', callback:()=>{
							add_or_modify_tag("");
						}},
						{ content : 'Modify Tag', callback:()=>{
							add_or_modify_tag(t_id);
						}},
						{ content : 'Copy Tag', callback:()=>{
							copy_tag($(this).attr("tag_id"));
						}},
						{ content : 'Paste Tag', callback:()=>{
							paste_tag(t_id);
						}},
						{ content : 'Delete Tag', callback:()=>{
							del_tag(t_id);
						}},
					];
					return d;
				}
	        });
	    }
	})

}

init_right_menu();


function sel_tags_all()
{
	var tb = document.getElementById("tb_"+path) ;
	var inputchkall = document.getElementById("chkall_"+path) ;
	var bchk = $(inputchkall).prop("checked") ;
	var tb = $(tb) ;
	var r = "" ;
	tb.find("input").each(function(){
		$(this).prop('checked',bchk);
	  });
	return r ;
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
	var tt = "Modify Tag";
	if(id==null||id=='')
		tt = "Add Tag" ;
	if(bmid)
		bmid = true;
	else
		bmid = false;
		dlg.open("./tag_edit.jsp?path="+path+"&id="+id+"&mid="+bmid,
				{title:tt,w:'500px',h:'400px'},
				['Ok','Cancel'],
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
									document.location.href=document.location.href;
								},false);
								
							 
							 //document.location.href=document.location.href;
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
	 dlg.confirm("make sure to delete selected tags？",{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
	 { 
		 send_ajax('./tag_ajax.jsp',ret,function(bsucc,ret)
			{
				if(!bsucc || ret.indexOf('succ')<0)
				{
					dlg.msg(ret);
					return ;
				}
				dlg.close();
				document.location.href=document.location.href;
			},false);
	 });
}


function cxt_rt()
{
	send_ajax("./cxt_dyn_ajax.jsp",{path:path,op:"rt"},(bsucc,ret)=>{
		if(!bsucc)
			return ;
		if(typeof(ret) == 'string')
			eval("ret="+ret) ;
		show_cxt_dyn("/",ret) ;
	},false) ;
}

function show_cxt_dyn(p,cxt)
{
	for(var tg of cxt.tags)
	{
		var tagp =p+tg.n ;
		var bvalid = tg.valid ;
		var dt = tg.dt ;
		var strv = tg.v ;
		show_ele_html("ctag_v_"+tagp,strv) ;
		show_ele_html("ctag_dt_"+tagp,dt) ;
		show_ele_html("ctag_q_"+tagp,""+bvalid) ;
	}
	
	for(var subn in cxt.subs)
	{
		show_cxt_dyn(p+subn+"/",cxt.subs[subn]) ;
	}
}

function show_ele_html(n,v)
{
	var ele = document.getElementById(n) ;
	if(ele==null||ele==undefined)
		return ;
	ele.innerHTML=v||"" ;
}

if(!b_devdef)
	setInterval(cxt_rt,3000) ;
	
function run_script_test(fn)
{
	var scode = document.getElementById('script_test').value ;
	if(scode==null||scode==''||trim(scode)=='')
		return ;
	send_ajax('cxt_script_test.jsp','path='+path+'&txt='+utf8UrlEncode(scode),
		function(bsucc,ret)
		{
			document.getElementById('script_res').value = ret ;
		},false) ;
}

</script>
</html>