<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.basic.*,
				 org.iottree.core.util.xmldata.*"%><%

%>
<html>
<head>
<title>Edit</title>
<jsp:include page="../head.jsp">
 <jsp:param value="true" name="oc"/>
</jsp:include>
<script src='/_js/tinycolor/tinycolor-min.js'></script>
<script src="/_js/tinycolor/colorpicker.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/tinycolor/style.css" />
<script>
dlg.resize_to(520,550);
</script>
<style>
table {color:#333333;}
td {border:1px solid;font-size: 12px;}
</style>
</head>
<body>
<table style="height0:450px;width:100%;border:solid 1px">
 <thead style="font-size: 14px;font-weight: bold;">
 	<tr>
 	  <td width="10%">Default</td>
 	  <td width="60%">Input $V</td>
 	  <td width="30%">Display Value</td>
 	</tr>
 </thead>
 <tbody id="opt_bd">
 	
 </tbody>
</table>
</body>
<script type="text/javascript">

var inp_dis = {} ; //{def_dis:"",opts:[{inp:"",dis:""}]}
var limit_diss = [];//dlg.get_opener_opt("limit_diss") ;
var ow = dlg.get_opener_w() ;
var plugpm = null;
if(ow)
{
	plugpm = ow.editor_plugcb_pm;
}

function inp_dis2ln(inpdis)
{
	return oc.hmi.trans_optinpdis_ln(inpdis) ;
}

function ln2inp_dis(ln)
{
	return oc.hmi.trans_ln_optinpdis(ln) ;
}
	
//console.log(plugpm);
if(plugpm!=null)
{
	let di = plugpm.di ;
	if(di.getCompInterPropByName)
	{
		let cip = di.getCompInterPropByName(plugpm.name) ;
		if(cip)
			limit_diss = cip.limit_diss ;
	}
	var v = plugpm.val ;
	//console.log(v) ;
	if(v)
	{
		try
		{
			inp_dis =ln2inp_dis(v) ;
		}
		catch
		{
			inp_dis={};
		}
	}
}

function get_inp_by_dis(dis_val)
{
	if(!inp_dis || !inp_dis.opts)
		return "" ;
	for(let opt of inp_dis.opts)
	{
		if(opt.dis==dis_val)
			return opt.inp ;
	}
	return "" ;
}

function update_ui()
{
	let tmps = "" ;
	if(limit_diss)
	{
		for(let dis of limit_diss)
		{
			let inp = get_inp_by_dis(dis) ;
			let chked = (inp_dis && inp_dis.def_dis && inp_dis.def_dis==dis)?"checked":"";
			tmps += `<tr class="opt_row">
			    <td><input value="\${dis}" class="def" type="radio" name="defdis" \${chked} /></td>
				<td><input value="\${inp}" class="inp" dis="\${dis}" /></td>
				<td><input value="\${dis}" class="dis" readonly/></td>
				</tr>` ;
		}
	}
	else
	{
		let opts = [];
		if(inp_dis && inp_dis.opts)
			opts = inp_dis.opts||[] ;
		for(let opt of opts)
		{
			let chked = (inp_dis && inp_dis.def_dis && inp_dis.def_dis==opt.dis)?"checked":"";
			tmps += `<tr class="opt_row">
				<td><input value="\${opt.dis}" class="def"  type="radio" name="defdis" \${chked}/></td>
				<td><input value="\${opt.inp}" class="inp"/></td>
				<td><input value="\${opt.dis}" class="dis"/></td>
				<td><button onclick="del_opt_row(this)">X<button></td>
			</tr>` ;
		}
		
		tmps += `<tr>
		    <td colspan="3"></td>
			<td ><button onclick="add_opt_row()">&nbsp;&nbsp;+&nbsp;&nbsp;</button></td>
		</tr>` ;
	}
	
	$("#opt_bd").html(tmps) ;
}

function add_opt_row()
{
	inp_dis.opts = get_opts() ;
	inp_dis.opts.push({dis:"",inp:""}) ;
	update_ui();
}

function del_opt_row(ele)
{
	$(ele).parent().parent().remove();
}

layui.use('form', function(){
	  var form = layui.form;
	  update_ui();
});

function win_close()
{
	dlg.close(0);
}

function get_opts()
{
	let opts=[] ;
	$(".opt_row").each(function(){
		let tr = $(this) ;
		let inp = tr.find(".inp").val() ;
		let dis =tr.find(".dis").val() ;
		let opt = {inp:inp,dis:dis} ;
		opts.push(opt) ;
	}) ;
	return opts ;
}

function get_defdis()
{
	let ret = null ;
	$(".opt_row").each(function(){
		let tr = $(this) ;
		let chk = tr.find(".def").prop("checked") ;
		if(chk)
			ret=tr.find(".dis").val() ;
	}) ;
	return ret ;
}

function editplug_get()
{
	let defdis = get_defdis();//$("input[name='defdis']:checked").val();
	let opts = get_opts() ;
	
	let ret ={def_dis:defdis,opts:opts} ;
	ret = inp_dis2ln(ret) ;
	//console.log(ret) ;
	return {v:ret};
}

</script>
</html>