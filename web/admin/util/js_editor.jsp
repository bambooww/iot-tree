<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.basic.*,
				 org.iottree.core.util.xmldata.*"%><%
	if(!Convert.checkReqEmpty(request, out, "repid"))
		return ;
	String repid = request.getParameter("repid") ;
	String nodeid = request.getParameter("nodeid") ;
	
	UAManager uam = UAManager.getInstance();
	UARep dc = uam.getRepById(repid) ;
	if(dc==null)
	{
		out.print("no rep found with id="+repid) ;
		return ;
	}
	UANode n = dc.findNodeById(nodeid);
	if(n==null&&dc.getId().equals(nodeid))
		n = dc ;
	if(n==null)
	{
		out.print("no node found");
		return ;
	}
	String t = n.getTitle();
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Properties Editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}

.prop_table
{
width:99%;
border: 0px;
margin: 0 auto;
}

.prop_table tr>td
{
	border: 0;
	height:100%
}

.prop_table tr>div
{
	border: 0;

}

.prop_edit_cat
{
border: 1px solid #cccccc;
height:400px;
padding: 3px;
margin: 2px;
overflow: auto;
}

.prop_edit_panel
{
border: 1px solid #cccccc;
height:350px;
padding: 0px;
margin: 2px;
overflow: auto;
}

.prop_edit_path
{
font-weight:bold;
border: 1px solid #cccccc;
background-color:#f0f0f0;
padding: 3px;
margin: 2px;
overflow: hidden;
}

.prop_edit_desc
{
border: 1px solid #cccccc;
background-color:#f0f0f0;
height:48px;
padding-left:3px;
padding-right:3px;
padding-bottom: 0px;
padding-top: 0px;
margin-left: 2px;
margin-right: 2px;
margin-top: 0px;
margin-bottom: 0px;
overflow: hidden;
}

.site-dir li {
    line-height: 26px;
    margin-left: 20px;
    overflow: visible;
    list-style-type: square;
}
li {
    list-style: none;
}

.site-dir li a {
    display: block;
    color: #333;
    cursor:pointer;
    text-decoration: none;
}


.site-dir li a.layui-this {
    color: #01AAED;
}

.pi_edit_table
{
width:100%;
border: 0px solid #b4b4b4;
margin: 0 auto;
}


.pi_edit_table tr>td
{
	border: 1px solid #b4b4b4;
	height:100%;
	
	
}

.pi_edit_table .td_left
{
	padding-left: 20px;
}

.pi_edit_table tr>div
{
	border: 0;

}

.pi_sel
{
background-color: #0078d7;
}

.pi_edit_unit
{
border: 0px;
width:100%;
}

</style>
</head>
<script type="text/javascript">
</script>
<body>
<table class="prop_table">
  <tr>
    <td colspan="2"><div id="prop_edit_path" class="prop_edit_path"><%=n.getNodePathTitle() %></div></td>
  </tr>
  <tr>
    <td style="width:30%" >
    	<div id="editcats" class="prop_edit_cat">
    		<ul class="site-dir layui-layer-wrap" style="display: block;">
<%
List<PropGroup> pgs = n.listPropGroups();
if(pgs==null)
	pgs = new ArrayList<>(0);
String pg0="" ;

	for(PropGroup pg:pgs)
	{
		if("".equals(pg0))
			pg0 = pg.getName() ;
%>
 <li><a id="pg_<%=pg.getName() %>" onclick="sel_pg('<%=pg.getName() %>')"><%=pg.getTitle() %></a></li>
<%
	}

%>
</ul>
		</div>
    </td>
    <td style="width:70%;vertical-align: top;"  >
    <div id="editpanel"  class="prop_edit_panel">
     
<%
for(PropGroup pg:pgs)
{
%>
<table id="tb_pg_<%=pg.getName() %>" class="pi_edit_table">
    <tr><td colspan="2" class="td_left" style="font-weight: bold;color: #000000;background-color: #f0f0f0"><%=pg.getTitle() %></td></tr>
<%
	for(PropItem pi:pg.getPropItems())
	{
%>
  <tr id="pi_<%=pi.getName()%>" onclick="sel_pi('<%=pi.getName()%>')">
    <td style="width:50%" class="td_left"><%=pi.getTitle() %></td>
    <td style="width:50%">
<%
	PropItem.ValOpts vopts = pi.getValOpts();
if(vopts==null || pi.isReadOnly())
{
	String inptp = "text" ;
	String inp_step="" ;
	String readonly="" ;
	if(pi.isReadOnly())
		readonly="readonly='readonly'";
	if(pi.getVT()==PropItem.PValTP.vt_int)
	{
		inptp = "number" ;
		inp_step="1" ;
	}
	else if(pi.getVT()==PropItem.PValTP.vt_float)
	{
		inptp = "number" ;
		//inp_step="1" ;
	}
	String defv = "" ;
	if(pi.getDefaultVal()!=null)
		defv = pi.getDefaultVal().toString() ;
%>
	<input id="piv_<%=pg.getName() %>.<%=pi.getName() %>" class="pi_edit_unit" type="<%=inptp %>" step="<%=inp_step %>" value="<%=defv %>" style="padding-left: 3px" <%=readonly %>/> 
<%
}
else
{
	List<PropItem.ValOpt> opts = vopts.getOpts();
%>
	<select id="piv_<%=pg.getName() %>.<%=pi.getName() %>" class="pi_edit_unit" >
<%
String sel="" ;
	for(PropItem.ValOpt opt:opts)
	{
		sel="" ;
		if(opt.getVal()!=null&&opt.getVal().equals(pi.getDefaultVal()))
			sel="selected=\"selected\"";
%><option value="<%=opt.getVal()%>" <%=sel %>><%=opt.getTitle() %></option><%
	}
%>
	</select>
<%
}
%>
	</td>
  </tr>
<%
	}
%>
</table>
<%
}
%>
	  
	 </div>
	  <div id="editdesc"  class="prop_edit_desc">
	  
	  </div>
    </td>
  </tr>
</table>
</body>
<script type="text/javascript">
var repid="<%=repid%>";
var nodeid="<%=nodeid%>";
var pg0="<%=pg0%>" ;
var bdirty=false;
function sel_pg(pgn)
{
	$('.layui-this').removeClass("layui-this");
	$("#pg_"+pgn).addClass("layui-this") ;
	
	show_pg_props(pgn);
}

function show_pg_props(pgn)
{
	
}

function init_chg()
{
	$("#editpanel input,select").on('input',function(e){
		dlg.btn_set_enable(1,true);
		bdirty=true;
	});
}

function isDirty()
{
	return bdirty;
}

function setDirty(b)
{
	bdirty= b;
}

function sel_pi(pin)
{
	$('.pi_sel').removeClass("pi_sel");
	$("#pi_"+pin).addClass("pi_sel") ;
}

function load_val()
{
	var pm={} ;
	pm.repid = repid ;
	pm.id = nodeid ;
	pm.op="load";
	send_ajax("ui_prop_ajax.jsp",pm,function(bsucc,ret){
		if(!bsucc)
		{
			dlg.msg(ret) ;
			return ;
		}
		var ob = null;
		eval("ob="+ret);
		for(var n in ob)
		{
			var v = ob[n] ;
			var piob = document.getElementById("piv_"+n);
			if(piob==null)
				continue ;
			$(piob).val(v+"") ;
		}
		
		init_chg();
	});
}
sel_pg(pg0);
load_val();

function get_prop_vals()
{
	var r = {} ;
	$("#editpanel input,select").each(function(){
		if(this.id.indexOf('piv_')!=0)
			return ;
		var id = this.id.substring(4) ;
		var v = $(this).val() ;
		r[id] = v ;
	});
	return JSON.stringify(r);
}
</script>
</html>