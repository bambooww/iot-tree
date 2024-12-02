<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.pro.mitsubishi.mc_eth.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "nodepath"))
		return ;
	String nodep = request.getParameter("nodepath") ;
	UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
	if(node==null || !(node instanceof UACh))
	{
		out.print("no UACh found with path="+nodep) ;
		return ;
	}
	
	UACh ch = (UACh)node ;
	DevDriver drv = ch.getDriver() ;
	if(drv==null || !(drv instanceof MCEthTCPDriver))
	{
		out.println("no driver found") ;
		return ;
	}
	MCEthTCPDriver mceth = (MCEthTCPDriver)drv ;
	List<MCDevItem> mcdevs = mceth.listMCDevItem() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,550);
</script>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
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


.mcdev
{
position:relative;
margin:10px;
 border:1px solid #bbbbbb;
display: inline-block;
text-align: left;
}

.mcblk
{
	position:relative;
	display:inline-block;
	margin-left:10px;
	float: left;
}
.mccmd
{
margin-left:10px;
display:inline-block;
}

.addr
{
	margin:3px;border:1px solid #777777;
}
</style>
</head>
<body>
<%
for(MCDevItem di:mcdevs)
{
		UADev dev = di.getUADev() ;
%>
<div class="mcdev">Device:<%=dev.getTitle() %> - <%=dev.getName() %> <br>
<%
	for(MCBlock blk:di.listMCBlock())
	{
		List<MCAddr> addrs = blk.getAddrs() ;
		MCAddr from = addrs.size()>0?addrs.get(0):null ;
		MCAddr to = addrs.size()>0?addrs.get(addrs.size()-1):null ;
		
		Map<MCCmd,List<MCAddr>> cmd2addr = blk.getCmd2AddrMap() ;
%>
	<br><br>Block:<span class="mcblk"><%=blk.getMCCode() %> Addr  <%=from %> - <%=to %>
<%
		for(Map.Entry<MCCmd,List<MCAddr>> c2addr:cmd2addr.entrySet())
		{
			MCCmd cmd = c2addr.getKey() ;
			List<MCAddr> c_addrs = c2addr.getValue() ;
%>
<br>Cmd:<span class="mccmd"><%=cmd %>&nbsp;
<%
			for(MCAddr caddr:c_addrs)
			{
%><span class="addr"><%=caddr.getAddr() %></span>
<%
			}
%>
</span><br>
<%
		}
%>
	</span><br>
<%
	}
%>
</div>
<%
}
%>
</body>
<script type="text/javascript">

var input_txt = dlg.get_opener_opt("inputv") ;

var sel_obs = [] ;


var cur_tag_tr = null ;
var cur_sel_tr = null ;

$("#tb_left_tags tr").click(function(){
	cur_tag_tr = $(this) ;
	refresh_tag_list();
});

//function on_right(ob)
//{
//	cur_bind_map_tr = $(ob) ;
//	refresh_bind_map();
//}

function refresh_tag_list()
{
	$("#tb_left_tags tr").each(function(){
		$(this).removeClass("tag_sel") ;
	});
	if(cur_tag_tr!=null)
		cur_tag_tr.addClass("tag_sel") ;
}

function sel_or_not(b)
{
	if(b)
	{
		if(!cur_tag_tr)
		{
			dlg.msg("<wbt:g>pls,select,tag,left</wbt:g>") ;
			return ;
		}
		let tagp = cur_tag_tr.attr("tagp") ;
		let tt = cur_tag_tr.attr("tagt") ;
		set_right_tag(tagp,tt)
	}
	else
	{
		if(!cur_sel_tr)
		{
			dlg.msg("<wbt:g>pls,select,item,right</wbt:g>") ;
			return ;
		}
		cur_sel_tr.remove() ;
		cur_sel_tr=null ;
	}
}

function on_right_sel(tr)
{
	cur_sel_tr = $(tr) ;
	refresh_sel_list() ;
}

function refresh_sel_list()
{
	$("#tb_right_seled tr").each(function(){
		$(this).removeClass("tag_sel") ;
	});
	if(cur_sel_tr!=null)
		cur_sel_tr.addClass("tag_sel") ;
}

function set_right_tag(tagp,title)
{
	let old = get_right_tag(tagp) ;
	if(old)
	{
		dlg.msg("<wbt:g>this,tag,be_selected</wbt:g>");
		return ;
	}
	
	$("#tb_right_seled").append(`<tr onclick="on_right_sel(this)">
		<td>\${tagp}</td>
		<td><input type="text" value="\${title}"  /></td>
		</tr>`);
}

function set_right_tags(obs)
{
	for(let ob of obs)
	{
		set_right_tag(ob.tag,ob.title) ;
	}
}

function get_right_tags()
{
	let obs = [] ;
	$("#tb_right_seled").find("tr").each(function(){
		let curtr = $(this) ;
		let tag = curtr.find("td").eq(0).text() ;
		let tt = curtr.find("input").eq(0).val() ;
		obs.push({tag:tag,title:tt}) ;
	}) ;
	return obs ;
}

function get_right_tag(tagp)
{
	let ret = null ;
	$("#tb_right_seled").find("tr").each(function(){
		let curtr = $(this) ;
		let tag = curtr.find("td").eq(0).text() ;
		let tt = curtr.find("input").eq(0).val() ;
		if(tag==tagp)
			ret = {tag:tag,title:tt} ;
	}) ;
	return ret ;
}

function init_sel()
{
	if(!input_txt)
	{
		return ;
	}
	eval("sel_obs="+input_txt) ;
	set_right_tags(sel_obs) ;
}

init_sel() ;

function win_close()
{
	dlg.close(0);
}



function do_submit(cb)
{
	let obs = get_right_tags();
	console.log(obs) ;
	
	cb(true,{txt:JSON.stringify(obs)});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>