<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%

%><html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="oc_min"/>
	<jsp:param value="true" name="tree"/>
</jsp:include>
</head>
<style>
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

.item_title
{
	position:relative;
	top:80px;
	margin:0px;
	left:0px;
	opacity: 0.7;
	padding-top:8px;
	width:100%;
	height:20px;
	background-color: #a7d3f5;
}

</style>
<body marginwidth="0" marginheight="0">
<table width='100%' height='99%'>
 <tr>
 <td valign="top" width="25%">
 	<div id="lib_cat_tree" style="height:100%;overflow: auto;">
 		
 	</div> 
 </td>
 <td id="td_add_comp" valign="top" width="75%" class="oc-toolbar">
 	Components 
 	<div  id="var_items" class="btns"  style="height:98%;overflow: auto;">
 	</div>
 </td>
 
 </tr>
</table>
<script>

var libid = "" ;
var cur_catid = null ;
var cur_cattt = null ;
var cur_cat_itemids=[] ;
var loadidx= 0 ;


function tree_init()
{
	$.jstree.destroy();
	this.jsTree = $('#lib_cat_tree').jstree(
				{
					'core' : {
						'data' : {
							'url' :"comp_ajax.jsp?op=lib_cat_tree",
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

function on_tree_node_sel(n)
{
	var id = n.id;
	var k = id.indexOf("-") ;
	if(k<=0)
		return ;
	libid = id.substring(0,k) ;
	cur_catid = id.substring(k+1) ;
	cur_cattt = n.text ;
	cat_sel_chg();
}

tree_init();

function drag(ev)
{
	var tar = ev.target;
	//var dxy = panel.transPixelPt2DrawPt(ev.x, ev.y);
	var p = tar[oc.DrawPanelDiv.DRAW_PANEL_DIV];
	if(p==null||p==undefined)
		return;
	var res_lib_id = p["res_lib_id"]; 
	var compid = p["compid"] ;
	if(compid==null||compid==undefined)
		return ;
	var r = {_val:res_lib_id+"-"+compid,_tp:"comp"};
	//console.log(r) ;
	oc.util.setDragEventData(ev,r);
}

function cat_sel_chg()
{
	var pm ={} ;
	pm.libid = libid ;
	pm.catid = cur_catid ;
	pm.op = "comp_list";
	
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
			tmps += "<div id='"+ci.id+"' data-cat='"+pm.catid+"' data-id='"+ci.id+"' data-tt='"+cur_cattt+"-"+ci.title+"' class=\"toolbarbtn\" onclick=\"item_clk('"+ci.id+"')\" >"
			+"<div id='panel_"+ci.id+"' draggable='true' ondragstart='drag(event)'></div><span class='item_title'>"
			+ci.title+"</span></div>";
			
			ids.push(ci.id);
		}
		cur_cat_itemids = ids ;
		loadidx= 0 ;
		$("#var_items").html(tmps) ;
		reg_right_menu();
		load_preview();
	},false);
}


function load_preview()
{
	if(loadidx>=cur_cat_itemids.length)
		return;//end
	send_ajax("comp_ajax.jsp",{libid:libid,op:"comp_txt",catid:cur_catid,id:cur_cat_itemids[loadidx]},function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			if(ret!="")
				dlg.msg(ret) ;
		}
		else
		{
			var lay = new oc.DrawLayer();
			var fl = oc.util.splitFirstLnAndLeft(ret) ;
			var resnid = fl.first_ob["res_lib_id"];
			var resid =  fl.first_ob["res_id"];
			lay.inject(fl.left_ob) ;
			var panel = new oc.hmi.HMICompPanel(cur_cat_itemids[loadidx],resnid,resid,"panel_"+cur_cat_itemids[loadidx],{});
			var p1 = new oc.DrawPanelDiv("",{layer:lay,panel:panel}) ;
			p1["res_lib_id"] = resnid ;
			p1["compid"] = cur_cat_itemids[loadidx];
			//all_panels.push(p1);
			loadidx ++ ;
			lay.ajustDrawFit();
			load_preview();
		}
	});
}

function reg_right_menu()
{
    //取消右键  rightmenu属性开始是隐藏的 ，当右击的时候显示，左击的时候隐藏
    $('#td_add_comp').on('contextmenu', function () { return false; })
    $('#td_add_comp').click(function () {
        $('.oc_menu').hide();
      });
    
    //right clk
    $('.toolbarbtn').on('contextmenu', function (e){
    	
    	var tar = e.currentTarget ;
    	var cat = tar.getAttribute('data-cat');
    	var id = tar.getAttribute('data-id');
    	var popupmenu = $(".oc_menu");
    	popupmenu.find("div").attr("data-cat",cat);
    	popupmenu.find("div").attr("data-id",id);

        l = ($(document).width() - e.clientX) < popupmenu.width() ? (e.clientX - popupmenu.width()) : e.clientX;
        t = ($(document).height() - e.clientY) < popupmenu.height() ? (e.clientY - popupmenu.height()) : e.clientY;
       
        popupmenu.css({ left: l, top: t }).show(); //
        return false;
    });


	  $(".oc_menu div").click(function () {
		    if ($(this).attr("data-type") == "edit")
		    {
		    	var cat = $(this).attr("data-cat");
		    	var id = $(this).attr("data-id");
		    	//console.log("cat="+cat+" id="+id) ;
		    	var tt = $("#"+id).attr("data-tt") ;
		    	//parent.parent.set_comp_editor_tab(cat,id,"comp:"+tt) ;
		    	open_comp_editor(cat,id,tt);
		    }
		    $('.oc_menu').hide(); 
	    });
	
	    $('.oc_menu').hide(); 
}

function open_comp_editor(cat,id,tt)
{
	//parent.parent.set_comp_editor_tab(cat,id,"comp:"+tt) ;
	window.open("./hmi_editor_comp.jsp?tabid="+id+"&catid="+cat+"&id="+id);
}

function comp_sel_chg()
{
	
}

function add_cat()
{
		dlg.open("comp_add.jsp",
				{title:"Add Component Category",w:'500px',h:'400px'},
				['Ok','Cancel'],
				[
					function(dlgw)
					{
						dlgw.do_submit((bsucc,ret)=>{
							 if(!bsucc)
							 {
								 dlg.msg(ret) ;
								 return;
							 }
							 
							 ret.op="cat_add" ;
							 send_ajax('comp_ajax.jsp',ret,(bsucc,ret)=>
								{
									if(!bsucc || ret.indexOf('{')<0)
									{
										dlg.msg(ret);
										return ;
									}
									dlg.close();
									var ob = null ;
									eval("ob="+ret) ;
									$("#var_cat").append("<option value='"+ob.id+"'>"+ob.title+"</option>");
									$("#var_cat").val(ob.id);
								},false);
					 	});
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
}
					
function add_comp()
{
	var catid = get_cur_catid();
	dlg.open("comp_add.jsp",
			{title:"Add Component",w:'500px',h:'400px'},
			['Ok','Cancel'],
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
						 ret.catid = catid ;
						 console.log(ret);
						 send_ajax('comp_ajax.jsp',ret,(bsucc,ret)=>
							{
								if(!bsucc || ret.indexOf('{')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.close();
								var ob = null ;
								eval("ob="+ret) ;
								$("#var_item").append("<option value='"+ob.id+"'>"+ob.title+"</option>");
								$("#var_item").val(ob.id);
							},false);
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function sel()
{
	var o = document.getElementById('var_cat');
	if(o.value==null||o.value=='')
	{
		alert('请选择分类') ;
		return ;
	}
	var tmps = o.value +'.';
	o = document.getElementById('var_item');
	if(o.value==null||o.value=='')
	{
		alert('请选择图元') ;
		return ;
	}
	
	//alert(o.value) ;
	dlg.close(o.value) ;
}

</script>

</body>
</html>