<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%

List<CompCat> ccs = CompManager.getInstance().getCatAll() ;
boolean bedit ="true".equals(request.getParameter("edit")) ;
%><html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/ajax.js" ></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
</head>
<style>
.oc-toolbar .toolbarbtn
{
position:relative;
margin: 5px;
font-size: 13px;

width:100px;height:120px;

}

.toolbarbtn div
{
background-color: #2f2f2f;width:100px;height:100px;float:left; 
}

.toolbarbtn span
{
position:absolute;
bottom:10px;
left:10px;
}


.rmenu_item:hover {
	background-color: #373737;
}



</style>
<body marginwidth="0" marginheight="0">
<table width='100%' height='99%'>
 <tr>
 <td valign="top" width="25%">Category <a href="javascript:add_cat()">+Add</a>
   <select id='var_cat' multiple="multiple" style="width: 100%;height: 100%" onchange="cat_sel_chg()">
<%
	for(CompCat cc:ccs)
	{

%><option value="<%=cc.getId() %>"><%=cc.getTitle() %></option><%
	}
%>
   </select>
 </td>
 <td id="td_add_comp" valign="top" width="75%" class="oc-toolbar">
 	Components <a href="javascript:add_comp()">+Add</a>
 	<div  id="var_items" class="btns" >
 		<div class="toolbarbtn" onclick="" title=""><img src="" /></div>
 	</div>
 </td>
 
 </tr>
 <tr height="30">
  <td colspan='2'></td>
 </tr>
</table>
 <div  class="oc_menu"  style=" display: none;position: absolute;background: #6E6C79;z-index: 60000">
        <div data-type="edit" class="menu"  data-cat="" data-id=""  style="cursor:pointer;" ><span>编辑</span></div>
 </div>

<script>

var cur_catid = null ;
var cur_cat_itemids=[] ;
var loadidx= 0 ;
var bedit=<%=bedit%>;

function get_cur_cat_id_title()
{
	var catid = $("#var_cat").val() ;
	var cattt =  $("#var_cat option:selected").text() ;
	if(catid==null||catid==undefined||catid==""||catid.length==0)
	{
		dlg.msg("please select a category!");
		return;
	}
	catid = catid[0];
	return [catid,cattt] ;
}

function get_cur_catid()
{
	return get_cur_cat_id_title()[0];
}

function get_sel_cat_ids()
{
	return $("#var_cat").val() ;
}

function drag(ev)
{
	var tar = ev.target;
	//var dxy = panel.transPixelPt2DrawPt(ev.x, ev.y);
	var p = tar[oc.DrawPanelDiv.DRAW_PANEL_DIV];
	if(p==null||p==undefined)
		return;
	var compid = p["compid"] ;
	if(compid==null||compid==undefined)
		return ;
	var r = {_val:compid,_tp:"comp"};
	console.log(r) ;
	oc.util.setDragEventData(ev,r);
}

function cat_sel_chg()
{
	var pm ={} ;
	pm.op = "comp_list";
	var catidtt = get_cur_cat_id_title();
	cur_catid = pm.catid = catidtt[0] ;
	
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
			//tmps += "<option value='"+ci.id+"'>"+ci.title+"</option>";
			if(bedit)
			{
				tmps += "<div id='"+ci.id+"' data-cat='"+pm.catid+"' data-id='"+ci.id+"' data-tt='"+catidtt[1]+"-"+ci.title+"' class=\"toolbarbtn\" onclick=\"item_clk('"+ci.id+"')\" >"
				+"<div id='panel_"+ci.id+"'  ></div><span>"
				+ci.title+"</span><button onclick='open_comp_editor(\""+pm.catid+"\",\""+ci.id+"\",\""+catidtt[1]+"-"+ci.title+"\")'>edit</button></div>";
			}
			else
			{
				tmps += "<div id='"+ci.id+"' data-cat='"+pm.catid+"' data-id='"+ci.id+"' data-tt='"+catidtt[1]+"-"+ci.title+"' class=\"toolbarbtn\" onclick=\"item_clk('"+ci.id+"')\" >"
				+"<div id='panel_"+ci.id+"' draggable='true' ondragstart='drag(event)'></div><span>"
				+ci.title+"</span></div>";
			}
			
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
	send_ajax("comp_ajax.jsp","op=comp_txt&catid="+cur_catid+"&id="+cur_cat_itemids[loadidx],function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			if(ret!="")
				dlg.msg(ret) ;
		}
		else
		{
			var lay = new oc.DrawLayer();
			var fl = oc.util.splitFirstLnAndLeft(ret) ;
			var resnid = fl.first_ob["res_node_id"];
			lay.inject(fl.left_ob) ;
			var panel = new oc.hmi.HMICompPanel(cur_cat_itemids[loadidx],resnid,"panel_"+cur_cat_itemids[loadidx],{});
			var p1 = new oc.DrawPanelDiv("",{layer:lay,panel:panel}) ;
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