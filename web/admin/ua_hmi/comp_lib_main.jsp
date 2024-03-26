<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.comp.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
boolean bedit = "true".equalsIgnoreCase(request.getParameter("edit")) ;
String tbh = "100%";
if(!bedit)
	tbh = "90%";

String libid = request.getParameter("libid") ;
if(libid==null)
	libid ="" ;

boolean bdlg = "true".equals(request.getParameter("dlg")) ;
boolean bsel_libcat = "true".equals(request.getParameter("sel_libcat")) ;
boolean bsel_comp = "true".equals(request.getParameter("sel_comp")) ;

String sel_libid = request.getParameter("sel_libid") ;
String sel_catid = request.getParameter("sel_catid") ;
String sel_compid = request.getParameter("sel_compid") ;
if(sel_libid==null)
	sel_libid="";
if(sel_catid==null)
	sel_catid="";
if(sel_compid==null)
	sel_compid="" ;
CompLib sel_lib = null ;
CompCat sel_cat = null ;
String sel_lib_tt = "" ;
String sel_cat_tt = "" ;
CompItem sel_comp = null ;

String sel_comp_tt="" ;
if(!bedit)
{
	if(Convert.isNotNullEmpty(sel_libid))
	{
		libid = sel_libid;
		sel_lib = CompManager.getInstance().getCompLibById(sel_libid) ;
		if(sel_lib!=null)
			sel_lib_tt = sel_lib.getTitle();
	}
	if(Convert.isNotNullEmpty(sel_catid))
	{
		sel_cat = sel_lib.getCatById(sel_catid) ;
		if(sel_cat!=null)
		{
			sel_cat_tt = sel_cat.getTitle();
			if(Convert.isNotNullEmpty(sel_compid))
			{
				sel_comp = sel_cat.getItemById(sel_compid) ;
				if(sel_comp!=null)
					sel_comp_tt = sel_comp.getTitle() ;
			}
		}
	}
}

if(bdlg)
	tbh = "400px";
%>
<html>
<head>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="oc"/>
</jsp:include>
<link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
<script src="/_js/selectmenu/selectmenu.min.js"></script>
</head>
<style>
table{border:0px solid skyblue;}

.oc-toolbar .toolbarbtn
{
position:relative;
margin: 5px;
font-size: 13px;

width:100px;height:120px;

}

select option
{
font-size: 12px;
}

.btns
{
overflow: auto;
}

.toolbarbtn div
{
background-color: #2f2f2f;width:100px;height:100px;float:left; 
}

.toolbarbtn span
{
position:absolute;
bottom:10px;
left:0px;
}


.rmenu_item:hover {
	background-color: #373737;
}

.toolbarbtn:hover .item_edit{
visibility: visible;
}

.item_edit
{
	position:relative;
	top:70px;
	margin:0px;
	left:0px;
	padding-top:3px;
	width:100%;
	height:30px;
	visibility:hidden;
	background-color: #f2f6fb;
}

.item_title
{
	position:relative;
	top:80px;
	font-size:15px;
	font-weight:bold;
	margin:0px;
	left:0px;
	opacity: 0.9;
	padding-top:8px;
	width:100%;
	height:20px;
	background-color: #b8dbfe;
}

</style>
<script type="text/javascript">
var bdlg = <%=bdlg%>
if(bdlg)
	dlg.resize_to(500,700) ;
</script>
<body>
<%
if(!bedit)
{
%><blockquote class="layui-elem-quote " id="selected_info">&nbsp;
 <wbt:g>selected,cat</wbt:g>:<span id="selected_libcat_tt" style="color:red"><%=sel_lib_tt %> - <%=sel_cat_tt %></span> 
<%
if(bsel_comp)
{
%>
 <wbt:g>selected</wbt:g> HMI <wbt:g>comp</wbt:g>:<span id="selected_comp_tt" style="color:red"><%=sel_comp_tt %></span>
<%
}
%>
 </blockquote>
<%
}

if(bedit || bsel_comp)
{
%>
<table style="width:99%;height:<%=tbh %>;border:0px;">
	<tr >
		<td style="width:40%;height:100%"><iframe name="comp_left" src="comp_lib_left.jsp?edit=<%=bedit %>&libid=<%=libid %>&catid=<%=sel_catid %>" style="width:100%;height:100%;border:0"></iframe></td>
		<td style="width:60%;height:100%;vertical-align: top;" class="oc-toolbar">
		 <%
 if(bedit)
 {
 %>
 <div id="right_add_div" style="float:left;margin-left:8px;margin-top:4px;width:100%;display:none;">
  <span id="top_right_tt"></span>
  <a id="top_oper_add_comp" class="layui-btn  layui-btn-sm layui-btn-primary " ><i class="fa-regular fa-square-plus"></i>&nbsp;<wbt:g>add_comp</wbt:g></a>
  </div>
  <%
 }
  %>
  
			<div  id="var_items" class="btns" style="height:430px;overflow: auto;">
			</div>
		</td>
	</tr>
</table>
<%
}
else
{
%>
<table style="width:99%;height:<%=tbh %>;border:0px;">
	<tr >
		<td style="width:100%;height:100%"><iframe name="comp_left" src="comp_lib_left.jsp?edit=<%=bedit %>&libid=<%=libid %>" style="width:100%;height:100%;border:0"></iframe></td>
	</tr>
</table>
<%
}
%>
<%--
<frameset rows="*" cols="45%,*" id="frame1">
    <frame name="dev_left" src="dev_left.jsp?edit=<%=bedit %>" frameborder="0">
    <frame name="dev_right" src="" frameborder="0">
</frameset>
 --%>
</body>
<script type="text/javascript">
var sel_libid = "<%=sel_libid%>" ;
var sel_catid = "<%=sel_catid%>" ;
var sel_compid = null ;
var cur_cat_itemids=[] ;
var sel_libcat_tt = "<%=sel_lib_tt %> - <%=sel_cat_tt %>" ;
var sel_comp_tt = "" ;
var sel_comp_n = "" ;
var draw_layers = [];

var bedit = <%=bedit%>

var bsel_libcat = <%=bsel_libcat%>;
var bsel_comp = <%=bsel_comp%>;

$("#right_add_div").on("click","#top_oper_add_comp",add_comp);

function on_selected_libcat(libid,catid,tt)
{
	sel_libid = libid ;
	sel_catid = catid ;
	sel_libcat_tt = tt; 
	$("#selected_libcat_tt").html(tt) ;
	$("#top_right_tt").html(tt) ;
	if(sel_libid && sel_catid)
		cat_sel_chg()
}

function on_selected_comp(id,n,tt)
{
	sel_compid = id ;
	sel_comp_n = n ;
	sel_comp_tt = tt ;
	$("#selected_comp_tt").html(tt) ;
}

function check_selected_ok()
{
	if(bsel_libcat || bsel_dev)
	{
		if(!sel_libid || !sel_catid)
			return false;
	}
	
	if(bsel_dev)
	{
		if(!sel_devid)
			return false;
	}
	
	return true ;
}

function get_selected()
{
	return {libid:sel_libid,catid:sel_catid,devid:sel_devid,libcat_tt:sel_libcat_tt,dev_n:sel_dev_n,dev_tt:sel_dev_tt};
}


function cat_sel_chg()
{
	var pm ={} ;
	pm.libid = sel_libid ;
	pm.catid=sel_catid;
	pm.op = "comp_list";
	//var catidtt = get_cur_cat_id_title();
	//cur_catid = pm.catid = catidtt[0] ;
	
	send_ajax('comp_ajax.jsp',pm,(bsucc,ret)=>
	{
		if(!bsucc || ret.indexOf('[')<0)
		{
			dlg.msg(ret);
			return ;
		}
		//dlg.close();
		var ob = null ;
		eval("ob="+ret) ;
		
		var tmps ="" ;
		var ids=[];
		for(var ci of ob)
		{
			//$("#var_item").append("<option value='"+ci.id+"'>"+ci.title+"</option>");
			//tmps += "<option value='"+ci.id+"'>"+ci.title+"</option>";   <span class='item_title'>"+ci.title+"</span>
				
			if(bedit)
			{
				tmps += "<div id='"+ci.id+"' data-cat='"+pm.catid+"' data-id='"+ci.id+"' data-tt='"+sel_libcat_tt+"-"+ci.title+"' class=\"toolbarbtn\" onclick=\"item_clk('"+ci.id+"')\" title=\""+ci.title+"\">"
				+"<div id='panel_"+ci.id+"'  ></div><span class='item_edit'>"
				
				+"<button type='button' class='layui-btn layui-btn-xs layui-btn-primary' item_id='"+ci.id+"' onclick=\"edit_comp_item(this)\"><i class='fa fa-pencil'></i></button>"
				+"<button type='button' class='layui-btn layui-btn-xs layui-btn-normal' item_id='"+ci.id+"' onclick=\"open_comp_editor(this)\"><i class='fa fa-pencil'></i></button>"
				+"<button type='button' class='layui-btn layui-btn-xs layui-btn-danger' item_id='"+ci.id+"' onclick=\"del_comp_item(this)\"><i class='fa-solid fa-x'></i></button>"

				+"</span></div>";
			}
			else
			{
				tmps += "<div id='"+ci.id+"' data-cat='"+pm.catid+"' data-id='"+ci.id+"' data-tt='"+sel_libcat_tt+"-"+ci.title+"' class=\"toolbarbtn\" onclick=\"item_clk('"+ci.id+"')\" title=\""+ci.title+"\">"
				+"<div id='panel_"+ci.id+"' draggable='true' ondragstart='drag(event)'></div></div>";
			}
			
			ids.push(ci.id);
		}
		cur_cat_itemids = ids ;
		loadidx= 0 ;
		draw_layers = [];
		$("#right_add_div").css("display","");
		$("#var_items").html(tmps) ;
		//reg_right_menu();
		load_preview();
		
		init_comp_menu();
	},false);
}

function refresh_right()
{
	cat_sel_chg();
}

function up_later()
{
	if(!draw_layers||draw_layers.length<=0)
		return ;
	for(let dlay of draw_layers)
	{
		dlay.ajustDrawFit();
	}
}

function load_preview()
{
	if(loadidx>=cur_cat_itemids.length)
	{
		setTimeout(up_later,1200) ;
		return;//end
	}
		
	send_ajax("comp_ajax.jsp",{libid:sel_libid,op:"comp_txt",catid:sel_catid,id:cur_cat_itemids[loadidx]},function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			if(ret!="")
				dlg.msg(ret) ;
		}
		else
		{
			var lay = new oc.DrawLayer();
			var fl = oc.util.splitFirstLnAndLeft(ret) ;
			var reslibid = fl.first_ob["res_lib_id"];
			var res_id = fl.first_ob["res_id"];
			lay.inject(fl.left_ob) ;
			var panel = new oc.hmi.HMICompPanel(cur_cat_itemids[loadidx],reslibid,res_id,"panel_"+cur_cat_itemids[loadidx],{});
			var p1 = new oc.DrawPanelDiv("",{layer:lay,panel:panel}) ;
			p1["compid"] = cur_cat_itemids[loadidx];
			//all_panels.push(p1);
			loadidx ++ ;
			lay.ajustDrawFit();
			draw_layers.push(lay) ;
			load_preview();
		}
	});
}
	

function open_comp_editor(ob)
{
	event.preventDefault();
	event.stopPropagation();
	var id = $(ob).attr('item_id') ;
	window.open("./hmi_editor_comp.jsp?libid="+sel_libid+"&tabid="+id+"&catid="+sel_catid+"&id="+id);
}

function edit_comp_item(ob)
{
	event.preventDefault();
	event.stopPropagation();
	var id = $(ob).attr('item_id') ;
	edit_comp(id) ;
}

function del_comp_item(ob)
{
	event.preventDefault();
	event.stopPropagation();
	var id = $(ob).attr('item_id') ;
	del_comp(id);
}

function del_comp(id)
{
	dlg.confirm('<wbt:g>del,this,item</wbt:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("comp_ajax.jsp",{op:"comp_del",libid:sel_libid,catid:sel_catid,compid:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		//
			    		refresh_right()
			    	}) ;
				});
}

function add_comp()
{
	edit_comp("")
}

function edit_comp(id)
{
	var tt = "<wbt:g>edit,comp</wbt:g>";
	if(!id)
	{
		tt = "<wbt:g>add,comp</wbt:g>";
		id="" ;
	}
		
	dlg.open("comp_item_edit.jsp?libid="+sel_libid+"&catid="+sel_catid+"&compid="+id,
			{title:tt},['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="comp_add" ;
						 ret.libid=sel_libid;
						 ret.catid = sel_catid ;
						 ret.compid = id ;
						 var pm = {
									type : 'post',
									url : "./comp_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if(ret.indexOf("{")!=0)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								refresh_right();
							}).fail(function(req, st, err) {
								dlg.msg(err);
							});
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
				
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
			b_menu_show=false;
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
						{ content : '<wbt:g>paste</wbt:g>', callback:()=>{
							paste_item();
						}}
					];
			
					return d;
				}
	        });
	    	
	    }
	})
	
	
}


init_right_menu();

function init_comp_menu()
{
	$('.toolbarbtn').mouseenter(function(e) {
		var compid = $(this).attr("data-id");
		//on_item_mouseenter(compid);
	});
	
	$('.toolbarbtn').mouseout(function(e) {
		var compid = $(this).attr("data-id");
		//on_item_mouseout(compid);
	});
	
	$('.toolbarbtn').mousedown(function(e) {
		var compid = $(this).attr("data-id");
		if(1==e.which)
		{
			//on_item_mousedown(compid);
		}
	});
	
	$('.toolbarbtn').mouseup(function(e) {
		var compid = $(this).attr("data-id");
		if(1==e.which)
		{
			on_item_mouseup(compid);
		}
		
	    if (3 == e.which)
	    {
	    	e.stopPropagation();
	    	
	    	$('.sm_container').css("display","none") ;
	    	b_menu_show=true;
	        $(this).selectMenu({
	        	regular : true,
	        	rightClick : true,
	        	data : ()=>{

	        			var r=[] ;
	        			r.push({ content : '<wbt:g>modify</wbt:g>', callback:()=>{
	        				edit_comp(compid);
							}});
	        			
	        			r.push({ content : '<wbt:g>copy</wbt:g>', callback:()=>{
	        				copy_item(sel_libid,compid);
							}});

	        			
	        			r.push({ content : '<wbt:g>del</wbt:g>', callback:()=>{
	        				del_comp(compid);
							}});
						
	        			return r;
	        		}
	        });

	    }
	})

}

function copy_item(libid,compid)
{
	var pm={};
	pm.op="comp_copy";
	pm.libid=libid;
	pm.compid=compid;
	 send_ajax('./comp_ajax.jsp',pm,function(bsucc,ret)
				{
					if(!bsucc || ret.indexOf('succ')<0)
					{
						dlg.msg(ret);
						return ;
					}
					dlg.msg("<wbt:g>copy,item,ok</wbt:g>") ;
				},false);
	
}

function paste_item()
{
	var pm={};
	pm.op="comp_paste";
	pm.libid=sel_libid;
	pm.catid=sel_catid;
	 send_ajax('./comp_ajax.jsp',pm,function(bsucc,ret)
				{
					if(!bsucc || ret.indexOf('succ')<0)
					{
						dlg.msg(ret);
						return ;
					}
					cat_sel_chg();
				},false);
}
</script>
</html>