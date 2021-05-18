<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%

Collection<DivCompCat> ccs = DivCompManager.getInstance().listCats() ;
 
%><html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/ajax.js" ></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
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
<table width='100%' height='99%'>
 <tr>
 <td valign="top" width="25%">
   <select id='var_cat' multiple="multiple" style="width: 100%;height: 100%" onchange="cat_sel_chg()">
<%
	boolean bfirst = true ;
	for(DivCompCat cc:ccs)
	{
		String seled = "" ;
		if(bfirst)
		{
			bfirst=false;
			seled= "selected='selected'" ;
		}
%><option value="<%=cc.getName() %>" <%=seled %>><%=cc.getTitle() %></option><%
	}
%>
   </select>
 </td>
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

var cur_catid = null ;
var cur_cat_itemids=[] ;
var loadidx= 0 ;


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

function drag(ev)
{
	var tar = ev.target;
	var id = tar.id;
	if(id==null||id==""||id==undefined)
		return ;
	if(id.indexOf('divcomp_')!=0)
		return ;
	var uid = id.substring(8) ;
	var r = {_val:uid,_tp:"divcomp"};
	console.log(r) ;
	oc.util.setDragEventData(ev,r);
}

function cat_sel_chg()
{
	var pm ={} ;
	pm.op = "comp_list";
	var catidtt = get_cur_cat_id_title();
	cur_catid = pm.cat = catidtt[0] ;
	send_ajax('divcomp_ajax.jsp',pm,(bsucc,ret)=>
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
			tmps += "<div id='"+ci.id+"' data-cat='"+pm.catid+"' data-id='"+ci.id+"' data-tt='"+catidtt[1]+"-"+ci.title+"' class=\"toolbarbtn\" style=\"width:100px;height:120px\" onclick=\"item_clk('"+ci.id+"')\" >"
				+"<div id='divcomp_"+ci.id+"' style='width:100px;height:100px' draggable='true' ondragstart='drag(event)'>"
				+"<img draggable='false' src='"+ci.icon+"' width='80px' height='80px'/>"
				+"</div>"
				+ci.title+"</div>";
			ids.push(ci.id);
		}
		cur_cat_itemids = ids ;
		loadidx= 0 ;
		$("#var_items").html(tmps) ;
	},false);
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

cat_sel_chg();

</script>

</body>
</html>