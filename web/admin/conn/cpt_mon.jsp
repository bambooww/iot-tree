<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%><%!
final int LN_LEN = 120 ;
%><%
	if(!Convert.checkReqEmpty(request, out,"prjid", "cpid","cid"))
	return;
String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String cid = request.getParameter("cid") ;

ConnProvider cp = ConnManager.getInstance().getConnProviderById(prjid, cpid) ;
if(cp==null)
{
	out.print("no ConnProvider found") ;
	return ;
}

ConnPt cpt = cp.getConnById(cid) ;
if(cpt==null)
{
	out.print("no ConnPt found") ;
	return ;
}
	
//List<ConnPt.MonItem> monitems = cpt.getMonitorList() ;
%>
<html>
<head>
<title>Connection Monitor</title>
<jsp:include page="../head.jsp"></jsp:include>
<style type="text/css" ></style>

<link  href="/_js/font4.7.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" >
<script type="text/javascript" src="/_js/bignumber.min.js"></script>
<script type="text/javascript" src="/_js/jquery.json.js"></script>
<script type="text/javascript" src="/_js/jquery.xml.js"></script>
<style type="text/css">

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
overflow:hidden;
text-overflow:ellipsis;
}

.detail
{
	width:100%
}
.detail_head
{
	width:100%;
	background-color: #03a8d8;
	color: white;
}
.detail_cont
{
	width:100%;
	overflow: auto;
}
</style>
</head>
<body>
<div>
<span style="color:blue">Monitor: <%=cp.getTitle()%>-<%=cpt.getTitle()%></span>
<input type='button' value="stop" onclick="stop_refresh()"/>
</div>
<table style="width:100%">
	<tr>
	  <td style="width:40%;">
	  	<div id="mon_mtb_c" style="overflow: auto;height:500px">
	    <table id="mon_mtb" style="width:99%" border="1">

        </table>
        </div>
	  </td>
	  <td style="width:60%;">
	  	<div id="r_detail" style="height:700;width:100%">

	  	</div>
	  </td>
	</tr>
</table>

</body>
<script>
var brefresh = true ;
var prjid="<%=prjid%>" ;
var cpid="<%=cpid%>";
var cid="<%=cid%>";

var last_dt = -1 ;

function stop_refresh()
{
	brefresh = false ;
}

function refresh_me()
{
	if(!brefresh)
		return ;
		
	document.location.href = document.location.href;
}

function read_dt()
{
	send_ajax("cpt_mon_ajax.jsp",{op:"list",prjid:prjid, cpid:cpid,cid:cid,last_dt:last_dt},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		
		var ob = null ;
		eval("ob="+ret) ;
		var items = ob.items ;
		var minid = ob.min_id ;
		var maxid = ob.max_id ;
		if(items.length>0)
		{
			last_dt = items[0].dt ;
			var tmps = "" ;
			for(var item of items)
			{
				tmps += ob2tr(item);
			}
			
			var trs = $(tmps) ;
			$("#mon_mtb").prepend(trs);
		}
		//clear old id
		if(minid)
		{
			var eles = [] ;
			$("#mon_mtb tr").each(function(idx,ele){
				var id = $(ele).attr("id") ;
				if(id<minid)
					eles.push(ele) ;
			});
			for(var ele of eles)
			{
				$(ele).remove() ;
			}
		}
		
		
		setTimeout(read_dt,3000) ;
	});
}

function ob2tr(ob)
{
	var dir = "→" ;

	var bcolor="pink";
	if(!ob.bin)
	{
		dir = "←";
		bcolor = "yellow" ;
	}
	
	var datas = ob.datas;
	var strt = "" ;
	for(var dd of datas)
	{
		strt += "&nbsp;&nbsp;"+dd.n +":"+dd.tp+"["+dd.len+"]" ;
		if(dd.txt)
			strt += "&nbsp;"+dd.txt ;
	}
	
	
	var st = new Date(ob.dt).format("yyyy-MM-dd hh:mm:ss");
	
	//{\"dt\":"+stDT+",\"bin\":"+bInput+",\"n\":\""+this.monName+"\",\"len\":"+getMonDataLen()+",\"txt\":\"
	return "<tr id='"+ob.id+"' onclick='show_detail(this)'>"+
		"<td  width='40%' style='background-color:"+bcolor+"'>"+
		dir+" "+st +
		"</td><td>"+ob.n+"</td><td width='60%'>"+strt+"</td></tr>";
}
	
function show_detail(tr)
{
	var id = $(tr).attr("id") ;	
	send_ajax("cpt_mon_ajax.jsp",{op:"mitem",prjid:prjid, cpid:cpid,cid:cid,id:id},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		var ob = null ;
		eval("ob="+ret) ;
		console.log(ob) ;
		var tmps = "date="+new Date(ob.dt).format("yyyy-MM-dd hh:mm:ss")+"<br>" ;
		tmps += "name="+ob.n+"<br>" ;
		if(ob.bin)
			tmps += "read in<br>" ;
		else
			tmps += "write out<br>" ;
		var dds = ob.datas ;
		var dds_n = dds.length ;
		var w = $(window).width()*6/10;
		var tth = $(window).height()-100;
		var h = tth/dds_n ;
		
		for(var dd of dds)
		{
			tmps += dd2html(dd,w+'px',h+'px') ;
		}
		
		$("#r_detail").html(tmps);
	});
}
	
function dd2html(dd,w,h)
{
	var cont = "" ;

	if("json"==dd.tp&&dd.txt)
	{
		try
		{
			cont = new JSONFormat(dd.txt, 4).toString();
		}
		catch(e)
		{
			cont = "JSONFormat err"+e+"<br/><xmp>"+dd.txt+"</xmp>" ;
		}
	}
	else if("xml"==dd.tp&&dd.txt)
	{
		try
		{
				cont = new XMLFormat(dd.txt, 4).toString();
		}
		catch(e)
		{
			cont = "XMLFormat err"+e+"<br/><pre>"+dd.txt+"</pre>" ;
		}
	}	
	else
		cont = "<xmp>"+dd.txt+"</xmp>" ;
	
	
	return "<div class='detail'>"+
	    	"<div class='detail_head'>"+dd.n+"</div>"+
	    	"<div  class='detail_cont' style='height:"+h+";width:"+w+";overflow: auto;'>"+cont+"</div>"+
	  		"</div>";
	
}
var cont_h = (document.body.clientHeight-40)+"px";
$("#mon_mtb_c").css("height",cont_h) ;
$("#r_detail").css("height",cont_h) ;
setTimeout(read_dt,1000) ;

Date.prototype.format = function (fmt) { // author: meizz
	  var o = {
	    "M+": this.getMonth() + 1, 
	    "d+": this.getDate(), // 
	    "h+": this.getHours(), // 
	    "m+": this.getMinutes(), 
	    "s+": this.getSeconds(), 
	    "q+": Math.floor((this.getMonth() + 3) / 3), 
	    "S": this.getMilliseconds() 
	  };
	  if (/(y+)/.test(fmt))
	    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	  for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	      return fmt;
	}
	
</script>
</html>