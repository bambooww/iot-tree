<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="java.io.*,java.util.*,				org.iottree.core.*,
				org.iottree.core.util.*
		" %><%!static ArrayList<String> faNames = null;
public static ArrayList<String> getFANames() throws Exception
{
	if(faNames!=null)
		return faNames;
	ArrayList<String> ss = new ArrayList<>();
	File nf = new File(Config.getWebappBase()+"/_js/font470_icon_names.txt");
	try(FileInputStream fis = new FileInputStream(nf);)
	{
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String ln;
		while((ln=br.readLine())!=null)
		{
			ln = ln.trim();
			if(Convert.isNullOrEmpty(ln))
				continue ;
			int k = ln.indexOf(' ');
			if(k>0)
				ln = ln.substring(0,k);
			ss.add(ln) ;
		}
	}
	
	faNames=ss ;
	return ss ;
}%><%
	
//ArrayList<String> fans = getFANames();
%><!DOCTYPE html>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
</head>
<style>
i:hover{
color: red;
}

.oc-toolbar .toolbarbtn
{
width:100px;height:100px;margin: 10px;
}
</style>
<script type="text/javascript">

</script>
<body class="layout-body">

	<div id="win_act1"  class="oc-toolbar" style="width:100%;height:100%z-index:1;" >
		<div class="titlebar" >
			<span >图元列表</span> <a href="javascript:add_unit()">Add</a><div class="collapse icon-eda-fold"></div>
		</div>
		<div id="unit_list" class="btns" style="background-color: #1e1e1e;overflow: auto;width:100%;height:500px"></div>
	</div>
</body>
<script>
var loadLayer = null; 

function add_unit()
{
	window.open("ui_unit_edit.jsp?unitid="+oc.util.create_new_tmp_id());
}


function drag(ev)
{
	var tar = ev.target;
	//var dxy = panel.transPixelPt2DrawPt(ev.x, ev.y);
	var p = tar[oc.DrawPanelDiv.DRAW_PANEL_DIV];
	var di = p.getDrawItem();
	var n = di.getName();
	var g = di.getInsGroup();
	if(n==null||n==undefined)
		return ;
	//console.log(di.getId());
	oc.util.setDragEventData(ev,{_val:n,_tp:"unit",_g:g})
}

//init_panel();

var all_panels=[];

function btn_load_unit()
{
	send_ajax("ui_unit_ajax.jsp","op=load_all",(bsucc,ret)=>{
		//console.log(bsucc);
		//console.log(ret);
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret);
			return ;
		}
		var ob = null ;
		eval("ob="+ret);
		//console.log(ob);
		
		var items=[];
		var cc = 0 ;
		var allps = [] ;
		for(var item of ob)
		{
			cc ++;
			var un = new oc.DrawUnit();
			un.inject(item);
			un.addSquareBorder();
			var id = un.getId();
			var n = un.getName();
			var title = un.getTitle();
			var divstr = "<div class='toolbarbtn'  style='color:#cccccc'>"+title+
				"<div style='width:100%;height:80%' id='"+id+"' title='"+un.getTitle()+"'  draggable='true' ondragstart='drag(event)' ></div>"+
				"<a href='javascript:edit_unit(\""+id+"\")' style='font 10px;bottom:0px'><i class='fa fa-pencil-square'></i></a>"+
				"<a href='javascript:del_unit(\""+id+"\")' style='font 10px;bottom:0px'><i class='fa fa-window-close'></i></a>"+
				"</div>";
			var subdiv = $(divstr);
			$("#unit_list").append(subdiv);
			//console.log(id);
			
			p1 = new oc.DrawPanelDiv(id) ;
			p1.setDrawItem(un);
			allps.push(p1);
		}
		all_panels = allps;
		//updateByResize();
	}) ;

}

function edit_unit(id)
{
	window.open("ui_unit_edit.jsp?unitid="+id);
}

$(document).ready(function()
{
	btn_load_unit();
});

function updateByResize()
{
	try
	{
		for(var p of all_panels)
			p.updateByResize();
	}
	catch(E)
	{
		console.error(E);
	}
}

$(window).resize(function(){
	updateByResize();
	});
	
</script>
</html>