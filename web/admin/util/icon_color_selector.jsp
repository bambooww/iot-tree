<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="java.io.*,java.util.*,org.iottree.core.*,
				org.iottree.core.util.*
		" %><%@ taglib uri="wb_tag" prefix="wbt"%><%!
		static ArrayList<String> faNames = null;
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
.btns
{
	top:0px;
	height:430px;
	width:100%;
	overflow: auto;
}
.icon_item
{
}
.icon_item:hover {
	background-color: grey;
}
.btop
{
	font-weight: bold;
	font-size:16px;
	background-color: #f2f2f2;
	height:40px;
}
</style>
<script type="text/javascript">
dlg.resize_to(700,600) ;


</script>
<body>
<div class="btop" style="">
	<div style0="color:red"><i id="sel_icon" class="fa" style="font-size: 30px"></i> <span id="sel_txt"></span>
		<wbt:g>modify,color</wbt:g><input type="color" id="sel_color" onchange="on_chg(this)"/>
	</div>
</div>
	<div class="oc-toolbar btns">
<%
//int i = 0 ;
//for(String fan:fans)
for(int i = 0;i<675;i++)
{
	String fan = Integer.toHexString(0xf000+i);
	//i++;
	//if(i>=100)
	//	break;
	%>
<div title=""  class="toolbarbtn icon_item"  style="border:1px solid;" fa_icon="<%=fan%>"  onclick="select(this)"><i class="fa" style="font-size: 20px">&#x<%=fan %></i></div>
	<%
}
%>
</div>
</body>
<script type="text/javascript">
let pm = dlg.get_opener_opt("pm") ;
if(pm)
{
	//console.log(pm) ;
	$("#sel_icon").html("&#x"+pm.icon) ;
	$("#sel_icon").css("color",pm.color) ;
	$("#sel_color").val(pm.color) ;
}
	

var cur_ob = null ;

function select(ob)
{
	cur_ob = $(ob) ;
	let icon = cur_ob.attr("fa_icon") ;
	$("#sel_icon").html("&#x"+icon) ;
	$("#sel_txt").html(icon) ;
	pm = {icon:icon,color:pm.color} ;
}

function on_chg(ele)
{
	let color = $(ele).val() ;
	if(pm)
		pm.color = color ;
	$("#sel_icon").css("color",pm.color) ;
}

function do_submit(cb)
{
	if(!pm)
	{
		cb(false,"<wbt:g>pls,select</wbt:g>")
		return ;
	}
	cb(true,pm) ;
}
</script>
</html>