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
List<UATag> tags = node_tags.listTags() ;
boolean bdevdef = UAUtil.isDevDefPath(path) ;
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



</style>
<body marginwidth="0" marginheight="0">
<%=node_tags.getNodePathTitle() %>
<hr class="layui-bg-green">
<table class="oc_div_list" id="divlist_tb_">
  <thead>
     <tr>
        <th>Mid</th>
        <th>Tag Name</th>
        <th>Title</th>
        <th>Address</th>
        <th>Value Type</th>
        <th>Write</th>
        <th>Value</th>
        <th>Timestamp</th>
        <th>Quality</th>
     </tr>
   </thead>
   <tbody id="div_list_bd_">
<%
for(UATag tag:tags)
{
%>
   <tr id="tag_<%=tag.getId() %>"  tag_id="<%=tag.getId()%>">
        <td></td>
        <td><%=tag.getName() %></td>
        <td><%=tag.getTitle() %></td>
        <td><%=tag.getAddress() %></td>
        <td><%=tag.getValTp() %></td>
        <td><%=(tag.isCanWrite()?"✔":"") %></td>
        <td id="tag_v_<%=tag.getId() %>"></td>
        <td id="tag_dt_<%=tag.getId() %>"></td>
        <td id="tag_q_<%=tag.getId() %>"></td>
      </tr>
<%
}
%>
    </tbody>
</table>
</body>
<script>
var path = "<%=path%>" ;
var b_devdef = <%=bdevdef%>;

document.oncontextmenu = function() {
    return false;
}

$('body').mouseup(function(e) {
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

$('tr[id*="tag_"]').mouseup(function(e) {
    if (3 == e.which)
    {
    	e.stopPropagation();
    	
    	//$('body').selectMenu({rightClick : true}) ;//clear outter menu
    	$('.sm_container').css("display","none") ;
    	
        $(this).selectMenu({
        	title0:'Modify Tag',
        	regular : true,
        	rightClick : true,
        	data : ()=>{
				var d = [
					{ content : 'Modify Tag', callback:()=>{
						add_or_modify_tag($(this).attr("tag_id"));
					}},
					{ content : 'Copy Tag', callback:()=>{
						copy_tag($(this).attr("tag_id"));
					}},
					{ content : 'Paste Tag', callback:()=>{
						paste_tag($(this).attr("tag_id"));
					}},
					{ content : 'Delete Tag', callback:()=>{
						del_tag($(this).attr("tag_id"));
					}},
				];
				return d;
			}
        });
    }
})


function add_or_modify_tag(id)
{
		dlg.open("./tag_edit.jsp?path="+path+"&id="+id,
				{title:"Modify Tag",w:'500px',h:'400px'},
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
	 ret.id = id ;
	 
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
}
/*
$("[id^='tag_']").selectMenu({
	title : 'Modify Tag',
	regular : true,
	rightClick : true,
	data : menudata
});
*/

function tags_rt()
{
	send_ajax("./tag_ajax.jsp",{path:path,op:"rt"},(bsucc,ret)=>{
		if(!bsucc)
			return ;
		if(typeof(ret) == 'string')
			eval("ret="+ret) ;
		for(var tg of ret)
		{
			var id =tg.tag_id ;
			var bvalid = tg.valid ;
			var dt = tg.dt ;
			var strv = tg.strv ;
			$("#tag_v_"+id).html(strv) ;
			$("#tag_dt_"+id).html(dt) ;
			$("#tag_q_"+id).html(bvalid) ;
		}
		
	},false) ;
}

if(!b_devdef)
	setInterval(tags_rt,3000) ;
</script>
</html>