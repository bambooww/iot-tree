<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				org.json.*,
				org.iottree.core.*,
				org.iottree.core.res.*,
				org.iottree.core.util.*,
				java.net.*"%><%!
	static void listSubHmis(UANode node,ArrayList<UAHmi> hmis,UANode ignorehmi)
	{
		List<UANode> subns = node.getSubNodes();
		if(subns==null)
			return ;
		for(UANode subn:subns)
		{
			if(subn instanceof UAHmi)
			{
				if(subn==ignorehmi)
					continue ;
				hmis.add((UAHmi)subn) ;
				continue ;
			}
			listSubHmis(subn,hmis,ignorehmi) ;
		}
	}
		
	//list sub cxt hmis ,no include node itself
	static List<UAHmi> listCxtSubHmis(UANode cxtnode,UANode ignorehmi)
	{
		ArrayList<UAHmi> rets=  new ArrayList<>() ;
		listSubHmis(cxtnode,rets,ignorehmi) ;
		/*
		List<UANode> subns = cxtnode.getSubNodes() ;
		if(subns==null)
			return rets ;
		for(UANode subn:subns)
		{
			if(subn instanceof UAHmi)
				continue ;//no include self node
			listSubHmis(subn,rets,ignorehmi) ;
		}
		*/
		return rets ;
	}
%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return;
//String op = request.getParameter("op");
String path=request.getParameter("path");
UANode n = UAUtil.findNodeByPath(path) ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}
UANode topn = n.getTopNode();
UANode pnode = n.getParentNode() ;
String p_path = pnode.getNodePath() ;
int p_path_len = p_path.length() ;
List<UAHmi> hmis = listCxtSubHmis(pnode,n);
String ids_str="" ;
String paths_str = "" ;
String subpaths_str="" ;
String titles_str="" ;
int s = hmis.size();
if(s>0)
{
	ids_str+= "'"+hmis.get(0).getId() +"'";
	paths_str+= "'"+hmis.get(0).getNodePath() +"'";
	subpaths_str+="'"+hmis.get(0).getNodePath().substring(p_path_len) +"'";
	titles_str = "'"+hmis.get(0).getTitle() +"'";
	for(int i = 1 ; i < s ; i ++)
	{
		ids_str+= ",'"+hmis.get(i).getId() +"'";
		paths_str+= ",'"+hmis.get(i).getNodePath() +"'";
		subpaths_str+= ",'"+hmis.get(i).getNodePath().substring(p_path_len)  +"'";
		titles_str+= ",'"+hmis.get(i).getTitle() +"'";
	}
}

String res_ref_id="" ;
String reslibid = "" ;
//String resid = "" ;

if(topn instanceof IResNode)
{
	res_ref_id = reslibid = ((IResNode)topn).getResLibId();
	//.getResNodeUID() ;
}
%><!DOCTYPE html>
<html>
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
 <%--
 <td valign="top" width="25%">Category <a href="javascript:add_cat()">+Add</a>
   <select id='var_cat' multiple="multiple" style="width: 100%;height: 100%" onchange="cat_sel_chg()">
<%
	//for(CompCat cc:ccs)
	{

%><option value=""></option><%
	}
%>
   </select>
 </td>
  --%>
 <td id="td_add_comp" valign="top" width="75%" class="oc-toolbar">
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
var ids = [<%=ids_str%>] ;
var paths=[<%=paths_str%>] ;
var titles= [<%=titles_str%>] ;
var sub_paths=[<%=subpaths_str%>] ;
var loadidx= 0 ;

var res_ref_id ="<%=res_ref_id%>";
var res_lib_id="<%=reslibid%>";

function get_cur_catid()
{
	return get_cur_cat_id_title()[0];
}

function drag(ev)
{
	var tar = ev.target;
	//var dxy = panel.transPixelPt2DrawPt(ev.x, ev.y);
	var p = tar[oc.hmi.HMISubDiv.DRAW_PANEL_DIV];
	console.log(p);
	if(p==null||p==undefined)
		return;
	var hmipath = p["hmi_path"] ;
	var hmiid = p["hmi_id"] ;
	if(hmipath==null||hmipath==undefined)
		return ;
	var sz = p.getPreferSize();
	var w = sz.w;
	var h = sz.h;
	if(!w)
		w = 100 ;
	if(!h)
		h = 100;
	var pm= {hmi_path:hmipath,hmi_id:hmiid,w:w,h:h} ;
	var r = {_val:JSON.stringify(pm),_tp:"hmi_sub"};
	console.log(r) ;
	oc.util.setDragEventData(ev,r);
}

function show_cxt_hmi_list()
{
	if(paths.length<=0)
		return ;
	var tmps = "" ;
	for(var i = 0 ; i < ids.length ; i ++)
	{
		tmps += "<div id='"+ids[i]+"' class=\"toolbarbtn\"  onclick=\"item_clk('"+ids[i]+"')\" >"
		+"<div id='panel_"+ids[i]+"' hmi_path='"+paths[i]+"' draggable='true' ondragstart='drag(event)'></div><span>"
		+sub_paths[i]+"</span></div>";
	}
	
		loadidx= 0 ;
		$("#var_items").html(tmps) ;
		//reg_right_menu();
		load_preview();
}


function load_preview()
{
	if(loadidx>=ids.length)
		return;//end
		
		oc.DrawItem.G_REF_LIB_ID =res_ref_id ;
	var path = paths[loadidx];
	var id = ids[loadidx] ;
	//console.log("path="+path) ;
		var loadLayer=null;
			oc.DrawItem.G_REF_LIB_ID =res_ref_id ;
			var hmiModel = new oc.hmi.HMIModel({
				temp_url:"/hmi_ajax.jsp?op=load&path="+path,
				comp_url:"/comp_ajax.jsp?op=comp_load",
				hmi_path:path
			});
			
			var panel = new oc.hmi.HMIPanel("panel_"+ids[loadidx],res_lib_id,"",{});
			hmiView = new oc.hmi.HMIView(hmiModel,panel,null,{
				copy_paste_url:"util/copy_paste_ajax.jsp",
				show_only:true,
				on_model_loaded:()=>{
					if(loadLayer==null)
						return ;
					loadLayer.ajustDrawFit();
					
				}});
			
			hmiView.init();
			loadLayer = hmiView.getLayer();
			
			var p1 = new oc.hmi.HMISubDiv(panel,loadLayer) ;
			p1["hmi_path"] = path;
			p1["hmi_id"] = id;
			var r = loadLayer.getShowItemsRect() ;
			if(r)
				{
				p1["w"] = r.w;
				p1["h"] = r.h;
				}
			
			
		loadidx ++ ;
		load_preview();

}

function load_preview0()
{
	if(loadidx>=ids.length)
		return;//end
		
		oc.DrawItem.G_REF_LIB_ID =res_ref_id ;
	var path = paths[loadidx];
	console.log("path="+path) ;
	send_ajax("hmi_editor_ajax.jsp","op=load&path="+paths[loadidx],function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			if(ret!="")
				dlg.msg(ret) ;
		}
		else
		{
			var lay = new oc.DrawLayer();
            var fl = oc.util.splitFirstLnAndLeft(ret) ;
            var refpath = fl.first_ob["rb_path"];
            var np = fl.first_ob["path"] ;
            var resnodeid = fl.first_ob["res_lib_id"] ;
            var resid = fl.first_ob["res_id"] ;
			lay.inject(fl.left_txt) ;
			var panel = new oc.hmi.HMICompPanel(ids[loadidx],resnodeid,resid,"panel_"+ids[loadidx],{});
			var p1 = new oc.hmi.HMISubDiv("",{layer:lay,panel:panel,hmi_path:path}) ;
			p1["hmi_path"] = paths[loadidx];
			p1["hmi_id"] = ids[loadidx];
			var r = lay.getShowItemsRect() ;
			p1["w"] = r.w;
			p1["h"] = r.h;
			
			lay.ajustDrawFit();
			
		}
		loadidx ++ ;
		load_preview();
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
	parent.parent.set_comp_editor_tab(cat,id,"comp:"+tt) ;
}

function comp_sel_chg()
{
	
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

show_cxt_hmi_list();
</script>

</body>
</html>