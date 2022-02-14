<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return ;
//boolean bdev = "true".equals(request.getParameter("bdev")) ;
//boolean bmgr ="true".equals(request.getParameter("mgr")) ;
boolean bsys = "true".equals(request.getParameter("sys")) ;
String path = request.getParameter("path") ;
UANode node = UAUtil.findNodeByPath(path) ;
if(node==null)
{
	out.print("node not found"); 
	return ;
} 
UAHmi hmi = null ;
boolean bhmi = false;
if(node instanceof UAHmi)
{
	bhmi = true ;
	hmi = (UAHmi)node ;
	node = node.getParentNode() ;
}

boolean b_tags_g = false;
if(!(node instanceof UANodeOCTags))
{
	out.print("node has no tags") ;
	return ;
}

b_tags_g = node instanceof UANodeOCTagsGCxt ;

boolean ref_locked = false;
if(b_tags_g)
{
	ref_locked = ((UANodeOCTagsGCxt)node).isRefLocked();
}

String node_cxtpath = node.getNodePath();
UANodeOCTags node_tags = (UANodeOCTags)node;
//UATagList taglist  = node_tags.getTagList() ;

boolean bdevdef = UAUtil.isDevDefPath(path) ;
List<UATag> cur_tags = node_tags.getNorTags() ;
List<UANodeOCTags>  tns = node_tags.listSelfAndSubTagsNode() ;
boolean brefowner = node_tags.isRefOwner();
boolean brefed = node_tags.isRefedNode() ;

String hmitt = "" ;
if(bhmi)
{
	hmitt ="UI ["+hmi.getNodePath()+"]";
}

String ext_str = node_tags.getExtAttrStr() ;
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
	font-size: 12px;
	font-weight: bold;
}
td
{font-size: 12px;
}


</style>
<body marginwidth="0" marginheight="0">
<form class="layui-form" action="">
 <blockquote class="layui-elem-quote ">&nbsp;
 <div style="left:20px;top:5px;position:absolute;font:bold;font-size: 18"><%=node_tags.getNodePath() %> <a href="javascript:bind_ext('<%=node_tags.getNodePath() %>')" id="node_ext_<%=node_tags.getId() %>"  title="Set extended properties" style="<%=ext_color%>"><i class="fa fa fa-paperclip" aria-hidden="true"></i></a></div>
  <div style="left:20px;top:25px;position:absolute;">tags number is:<span id="tags_num"></span></div>
   <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
   
   <%
 	if(true) //(bdevdef||!ref_locked)
 	{
 %>
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_modify_tag('')">+Add Tag</button>&nbsp;&nbsp;
<%
 	}
%>
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_modify_tag('',true)">+Add Middle Tag</button>&nbsp;&nbsp;
 	<%--
 	<button type="button" class="layui-btn layui-btn-sm layui-btn-danger" onclick="del_tag()">+Delete Tag</button>
 	 --%>
  <input type="checkbox" id="show_sys"  name="show_sys" lay-filter="show_sys" lay-skin="switch" lay-text="Hide System Tags|Show System Tags" />
 </div>
</blockquote>
<div style="position:absoluate;bottom:120px;top:110px;overflow: auto;">
<table class="oc_div_list" style="margin-top:10px;width:99%" id="tb_cur" >
  <thead>
     <tr>
     <th>
     <%
	//if(!brefed)
	{
%>
        <input type="checkbox" lay-skin="primary"  id="chkall" lay-filter="chkall" />
<%
	}
%></th>
        <th style="width:15px;">T</th>
    	<th sort_by="name">Tag</th>
    	<th sort_by="title">Title</th>
        <th sort_by="addr">Address</th>
        <th sort_by="valtp">Value Type</th>
        
        <th>Value</th>
        <th>Update DT</th>
        <th>Change DT</th>
        <th>Quality</th>
        <th>Write</th>
        <th>Oper</th>
     </tr>
   </thead>
   <tbody id="div_list_bd_">
<%--   
<%
int tags_num = 0 ;
for(UANodeOCTags tn:tns)
{
	//if(tn.getRefBranchNode()!=null)
	//	continue ;
	List<UATag> tags = null;
	if(bsys)
		tags = tn.listTags() ;
	else
		tags = tn.getNorTags() ;
	
	String tn_id = tn.getId() ;
	String tn_path = tn.getNodePath() ;
	for(UATag tag:tags)
	{
		tags_num ++ ;
		
		String cxtpath=  tag.getNodeCxtPathIn(node_tags) ;
		boolean bloc = tag.getParentNode()==node_tags;
		String cssstr="" ;
		if(tag.isSysTag())
			cssstr="color:grey";
		else
			cssstr="color:blue";
		String tt = "" ;
		if(tag.getDesc()!=null)
			tt += tag.getDesc() ;
		if(tag.getNameSor()!=null)
			tt += "&#10;sor name:"+tag.getNameSor();
		if(tag.getTitleSor()!=null)
			tt += "&#10;sor title:"+tag.getTitleSor();
		if(tag.getDescSor()!=null)
			tt += "&#10;sor desc:"+tag.getDescSor();
		
		String addr = tag.getAddress() ;
		if(addr.length()>10)
			addr = addr.substring(0,10)+"..." ;
		
		String valtp_str = tag.getValTp().getStr();
		if(tag.getValTranserObj()!=null)
			valtp_str = tag.getValTpRaw().getStr()+"-"+valtp_str;
%>
   <tr id="ctag_<%=tag.getId() %>" tag_loc="<%=bloc %>"  tag_sys="<%=tag.isSysTag() %>" 
   	tag_path="<%=tn_path %>" tag_id="<%=tag.getId()%>" cxt_path="<%=cxtpath%>"
   	title="<%=tt%>"
<%
if(bloc&&!tag.isSysTag())
{
%>
   	 ondblclick="add_or_modify_tag('<%=tag.getId()%>')"
<%
}
%>
   	 >
   <td style="text-align: center;">
   <%
	if(bloc)
	{
%>
        <input type="checkbox" lay-skin="primary"  id="chk_<%=tag.getId()%>"/>
<%
	}
%></td>
        <td style="text-align: center;"><%=(tag.isMidExpress()?"✔":"") %></td>
<td title="<%=tag.getNodeCxtPathTitleIn(node_tags)%>"><span style="<%=cssstr%>"><%=cxtpath%></span></td>
<td><%=tag.getTitle() %></td>
        <td><%=addr%></td>
        <td><%=valtp_str %></td>
        <td style="width:100px" id="ctag_v_<%=cxtpath%>"></td>
        <td id="ctag_dt_<%=cxtpath%>"></td>
        <td id="ctag_q_<%=cxtpath%>"></td>
        <td>
<%
	if(tag.isCanWrite())
	{
%>
        	<input type="text" id="ctag_w_<%=tag.getId()%>" value="" size="8"/><a href="javascript:w_tag('<%=tag.getId()%>')"><i class="fa fa-pencil-square" aria-hidden="true"></i></a>
<%
	}
%>
        </td>
                <td>
<%
if(bloc&&!tag.isSysTag())
{
%>
        <a href="javascript:del_tag('<%=tag.getId()%>')"><i class="fa fa-times" aria-hidden="true"></i></a>&nbsp;&nbsp;
        <a href="javascript:add_or_modify_tag('<%=tag.getId()%>')"><i class="fa fa-pencil " aria-hidden="true"></i></a>
<%
}
%>&nbsp;	
        </td>
      </tr>
<%
	}
}
%>
    </tbody>
     --%>
</table>
</div>
</form>
<%
//if(brefed)
{
%>
<table width='100%' border='1' height="120">
<tr>
 <td>
 script test <input type='button' value='run' onclick="run_script_test('')"/> <input type='button' value='ttt' onclick="run_script_test1()"/>
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
<%
}
%>
<br><br>
</body>
<script>
var path = "<%=path%>" ;
var cxt_path= "<%=node_cxtpath%>";
var b_devdef = <%=bdevdef%>;
var b_refed = <%=brefed%>;
var b_sys = <%=bsys%>;
var sort_by=null ;

var tags_num = 0;//<%=0%>;
$("#tags_num").html(tags_num);
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  if(b_sys)
	  	$("#show_sys").attr('checked', 'checked');
	  
	  form.on('checkbox(chkall)', function(obj){
		  var bshow = obj.elem.checked ;
          sel_tags_all();
      });
	  
	  form.on('switch(show_sys)', function(obj){
		  var bshow = obj.elem.checked ;
          document.location.href="cxt_tags.jsp?path="+path+"&sys="+bshow ;
      });
	  
	  form.render();
	});

document.oncontextmenu = function() {
    return false;
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
						{ content : 'Paste Tag', callback:()=>{
							paste_tag();
						}}
					];
					if(get_selected_tagids()!='')
						d.push({ content : 'Copy Selected Tag', callback:()=>{
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
	        			return [{ content : 'Rename Tag', callback:()=>{
							rename_tag(t_path,t_id);
						}}];
	        		}
	        		
	        		if(t_loc&&!t_sys)
	        		{
	        			var r=[] ;
	        			r.push({ content : 'Modify Tag', callback:()=>{
								add_or_modify_tag(t_id);
							}});
	        			
	        			r.push({ content : 'Copy Tag', callback:()=>{
								copy_tag($(this).attr("tag_id"));
							}});
	        			if(get_selected_tagids()!='')
	        			{
	        				r.push({ content : 'Copy Selected Tag', callback:()=>{
								
								copy_tag(get_selected_tagids());
							}});
	        				
	        				r.push({ content : 'Delete Selected Tag', callback:()=>{
								
								del_tag(get_selected_tagids());
							}});
	        			}
							
							
	        			r.push({ content : 'Paste Tag', callback:()=>{
								paste_tag();
							}});
	        			r.push({ content : 'Delete Tag', callback:()=>{
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

function copy_tag(tagid)
{
	send_ajax('./tag_ajax.jsp',{op:"copy",path:cxt_path,tagids:tagid},function(bsucc,ret)
			{
				if(!bsucc || ret.indexOf('succ')<0)
				{
					dlg.msg(ret);
					return ;
				}
				dlg.msg("copied ok") ;
			},false);
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
				//document.location.href=document.location.href;
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
	var tt = "Modify Tag";
	if(id==null||id=='')
		tt = "Add Tag" ;
	var u = "./tag_edit.jsp?path="+path+"&id="+id
	if(bmid)
		u+="&mid=true";

		dlg.open(u,{title:tt,w:'500px',h:'400px'},
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
								
							 
							 //document.location.href=document.location.href;
					 	});
					},
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
				refresh_tags();
			},false);
	 });
}

function w_tag(id)
{
	var strv = $("#ctag_w_"+id).val();
	if(strv==null||strv=="")
	{
		dlg.msg("please input write value") ;
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
		
		var strv ="";
		if(tg.v!=null)
			strv = tg.strv ;
		var strerr = tg.err ;
		var strdt = "",strdtt="" ;
		var strchgdt = "",strchgdtt="" ;
		if(dt>0)
		{
			strdt =new Date(dt).format("hh:mm:ss");//
			strdtt =new Date(dt).format("yyyy-MM-dd hh:mm:ss");
		}
			
		if(chgdt>0)
		{
			strchgdt = new Date(dt).format("hh:mm:ss");//
			strchgdtt = new Date(dt).format("yyyy-MM-dd hh:mm:ss");//
		}
			
		show_ele_html("ctag_v_"+tagp,strv,true) ;
		show_ele_html("ctag_dt_"+tagp,strdt,0,strdtt) ;
		show_ele_html("ctag_chgdt_"+tagp,strchgdt,0,strchgdtt) ;
		var qstr = bvalid==true?"<span style='color:green'>✓</span>":"<span style='color:red'>✘</span>";
		if(!bvalid&&strerr!=null&&strerr!=""&&strerr!=undefined)
			qstr += "<span title='"+strerr+"'>err</span>";
		show_ele_html("ctag_q_"+tagp,qstr) ;
	}
	if(cxt.subs)
	{
		for(var sub of cxt.subs)
		{
			show_cxt_dyn(p+sub.n+".",sub) ;
		}
	}
}

var MAX_VAL_SHOWLEN = 20 ;

function show_ele_html(n,v,chklen,title)
{
	var ele = document.getElementById(n) ;
	if(ele==null||ele==undefined)
		return ;
	if(chklen&&v!=null&&v.length>20)
	{
		ele.innerHTML = "<span title='"+title+"'>"+v.substr(0,chklen)+"...</span>";
	}
	else if(title)
	{
		ele.innerHTML = "<span title='"+title+"'>"+v||""+"...</span>";
	}
	else
		ele.innerHTML=v||"" ;
}

if(!b_devdef)
	setInterval(cxt_rt,3000) ;
	
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
	send_ajax("cxt_tags_tb_ajax.jsp",'path='+cxt_path+'&sys='+b_sys+"&sortby="+sort_by,function(bsucc,ret){
		$("#div_list_bd_").html(ret);
		init_tr();
		
		var tn = $('#tb_cur tr[id*="ctag_"]:last-child').attr("tag_num");
		$("#tags_num").html(tn);
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
			 $(this).css("background","#0078d7");
		 else
			 $(this).css("background","");
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
			{title:"Ext Binder",w:'500px',h:'400px'},
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
								//document.location.href=document.location.href;
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

</script>
</html>