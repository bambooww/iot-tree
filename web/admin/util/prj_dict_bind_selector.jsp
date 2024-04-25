<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*
	"%><%! 

%>
<%
if(!Convert.checkReqEmpty(request, out, "path"))
	return ;
String path = request.getParameter("path");
UANode node = UAUtil.findNodeByPath(path) ;
if(node==null)
{
	out.print("node not found"); 
	return ;
} 

UAPrj prj = (UAPrj)node.getTopNode();// UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
String prjid = prj.getId() ;
PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prjid) ;
String jstr = node.getExtAttrStr() ;
if(jstr=="")
	jstr = null ;

int dc_num = 0 ;

for(DataClass dc:pdc.getDataClassAll())
{
	if(!dc.isBindFor(node.getNodeTp()))
		continue ;
	if(!dc.isClassEnable())
		continue ;
	dc_num ++ ;
}
int height = dc_num*100 ;
if(height<500)
	height = 500 ;
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
<style>
    .layui-form-label{
        width: 120px;
    }
    .layui-input-block {
        margin-left: 10px;
        min-height: 36px;
        width:95%;
    }
    .layui-table-view
    {
    	margin-top: 3px;
    }
</style>
</head>
<script>
dlg.resize_to(600,<%=height%>);
</script>
<body>
<form class="layui-form" action="" onsubmit="return false;">
<blockquote class="layui-elem-quote ">
<div>Node Path:[<%=path %>] <%=node.getTitle() %></div>
</blockquote>
<%
String dc_names = "" ;
for(DataClass dc:pdc.getDataClassAll())
{
	if(!dc.isBindFor(node.getNodeTp()))
		continue ;
	if(!dc.isClassEnable())
		continue ;
	String c_n = dc.getClassName() ;
	dc_names+=",'"+dc.getClassName()+"'" ;
	DataClass.BindStyle bs = dc.getBindStyle() ;
	boolean bmulti =bs==DataClass.BindStyle.m; 
	boolean binput = bs.isInput() ;
%>
		<div class="layui-form-item" id="dc_<%=c_n %>" dc_multi="<%=bmulti%>" dc_input="<%=binput%>">
		<label class="layui-form-label"><%=dc.getClassTitle() %></label>
	  	<div class="layui-input-inline"  style="width:350px;">
<%
	if(bs.isInput())
	{
		switch(bs)
		{
		case i_b:
%>
	<select id="dc_inp_<%=c_n%>" dc_bs="b" lay-filter="dc_inp_<%=c_n%>" >
			<option value="" selected="selected"> -- </option>
			<option value="true">True</option>
			<option value="false">False</option>
	</select>
<%
			break ;
		case i_i:
%><input type="number" oninput="this.value = this.value.replace(/\-[^0-9]/g, '');"  dc_bs="i" id="dc_inp_<%=c_n%>"  name="dn_<%=c_n%>" class="layui-input"/><%
			break ;
		case i_f:
%><input type="number"  id="dc_inp_<%=c_n%>"  name="dn_<%=c_n%>" dc_bs="f" class="layui-input"/><%
			break ;
		case i_s:
		default:
%><input type="text"  id="dc_inp_<%=c_n%>"  name="dn_<%=c_n%>" dc_bs="s" class="layui-input"/><%
			break ;
		}
	}
	else
	{
		
		String tp = "radio";
		if(bmulti)
			tp = "checkbox" ;
	
%>

<%
	for(DataNode dn:dc.getRootNodes())
	{
%>
<span style="border:1;white-space: nowrap;">
<input dc_name="<%=c_n %>" dn_name="<%=dn.getName() %>"  lay-skin="primary" type="<%=tp %>"  id="dn_<%=dc.getClassName() %>_<%=dn.getName() %>" 
	name="dn_<%=c_n%>"  title="<%=dn.getTitle() %>"  value="<%=dn.getName()%>">
</span>
<%
	}
%> <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" onclick="clear_sel('<%=dc.getClassName() %>')" title="<wbt:g>clear</wbt:g>"><i class="fa fa-x"></i></button>
    
<%
	}
%></div>
  </div><%
} //end of for

if(dc_names.length()>0)
{
	dc_names = dc_names.substring(1) ;
}
%>

</form>
</body>
<script>
var form = null ;
var cur_pn= null ;

var jstr = <%=jstr%> ;


function set_val_in_dc(dc)
{
	let ob = $('#dc_'+dc);
	let bmulti = ("true"==ob.attr("dc_multi")) ;
	let binput =  ("true"==ob.attr("dc_input")) ;
	let v = null ;
	if(jstr!=null)
		v = jstr[dc] ;
	if(v===null||v===""||v===undefined)
		return ;

	if(binput)
	{
		let inp = $("#dc_inp_"+dc) ;
		inp.val(""+v) ;
	}
	else
	{
		if(bmulti)
		{
			for(var tmpv of v)
			{
				$("#dn_"+dc+"_"+tmpv).prop("checked",true) ;
			}
		}
		else
		{
			$("#dn_"+dc+"_"+v).prop("checked",true) ;
		}
	}
	
}

function set_vals()
{
	for(var dcn of dc_names)
	{
		set_val_in_dc(dcn);
	}
	//form.render();
}


var dc_names=[<%=dc_names%>];

layui.use('form', function(){
	  form = layui.form;
	  
	  set_vals();
	  form.render();
	  form.render("select");
});


function clear_sel(dc)
{
	
	event.preventDefault() || (event.returnValue = false);
	var ob = $("#dc_"+dc) ;
	ob.find("input[type=checkbox]:checked").each(function(){
		$(this).prop("checked",false) ;
	}) ;
	
	ob.find("input[type=radio]:checked").each(function(){
		$(this).prop("checked",false) ;
	}) ;
	form.render();
}

function get_val_in_dc(dc)
{
	let ob = $('#dc_'+dc);
	let bmulti = ("true"==ob.attr("dc_multi")) ;
	let binput =  ("true"==ob.attr("dc_input")) ;
	let ret = null ;
	if(binput)
	{
		let dcob = $("#dc_inp_"+dc) ;
		ret = dcob.val() ;
		let dcbs = dcob.attr("dc_bs") ;
		console.log(ret,dcbs) ;
		switch(dcbs)
		{
		case "b":
			if(ret=='true') ret = true ;
			else if(ret=='false') ret=false;
			else ret = null ;
			break ;
		case "i":
			ret = parseInt(ret) ;
			break ;
		case "f":
			ret = parseFloat(ret) ;
			break ;
		}
	}
	else
	{
		if(bmulti)
		{
			ret=[] ;
			ob.find("input[type=checkbox]:checked").each(function(){
				var dnname = $(this).attr("dn_name") ;
				var v = $(this).val() ;
				ret.push(v) ;
			}) ;
		}
		else
		{
			ob.find("input[type=radio]:checked").each(function(){
				var dnname = $(this).attr("dn_name") ;
				ret = $(this).val() ;
			}) ;
		}
	}
	
	return ret ;
}

function do_submit(cb)
{
	var ret={} ;
	var bgit = false;
	for(var dcn of dc_names)
	{
		var v = get_val_in_dc(dcn) ;
		if(v==null||v=="")
			continue ;
		ret[dcn] = v ;
		bgit=true;
	}
	if(!bgit)
		ret="" ;
	else
		ret = JSON.stringify(ret);
	
	cb(true,{jstr:ret});
	
}
</script>
</html>


