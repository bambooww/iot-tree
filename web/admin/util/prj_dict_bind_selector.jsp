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
dlg.resize_to(600,400);
</script>
<body>
<form class="layui-form" action="">

<%
String dc_names = "" ;
for(DataClass dc:pdc.getDataClassAll())
{
	if(!dc.isBindFor(node.getNodeTp()))
		continue ;
	dc_names+=",'"+dc.getClassName()+"'" ;
	boolean bmulti = dc.isBindMulti() ;
	String tp = "radio";
	if(bmulti)
		tp = "checkbox" ;
	
%>

<blockquote class="layui-elem-quote ">
<div><%=dc.getClassTitle() %></div>
</blockquote>
<div class="layui-form-item" id="dc_<%=dc.getClassName() %>" dc_multi="<%=bmulti %>" >
  <div class="layui-input-block"  >
<%
	for(DataNode dn:dc.getRootNodes())
	{
%>
<span style="border:1;white-space: nowrap;">
<input dc_name="<%=dc.getClassName() %>" dn_name="<%=dn.getName() %>"  lay-skin="primary" type="<%=tp %>"  id="dn_<%=dc.getClassName() %>_<%=dn.getName() %>" 
	name="dn_<%=dc.getClassName() %>" title="<%=dn.getTitle() %>"  value="<%=dn.getName()%>">
</span>
<%
	}
%>
    </div>
  </div>
<%
}

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
	var ob = $('#dc_'+dc);
	var bmulti = ("true"==ob.attr("dc_multi")) ;
	var v = null ;
	if(jstr!=null)
		v = jstr[dc] ;
	if(v==null||v=="")
		return ;

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
});


function get_val_in_dc(dc)
{
	var ob = $('#dc_'+dc);
	var bmulti = ("true"==ob.attr("dc_multi")) ;
	var ret = null ;
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
	return ret ;
}

function do_submit(cb)
{
	var ret={} ;
	for(var dcn of dc_names)
	{
		var v = get_val_in_dc(dcn) ;
		if(v==null)
			continue ;
		ret[dcn] = v ;
	}
	
	//console.log(ret) ;
	cb(true,{jstr:JSON.stringify(ret)});
	
}
</script>
</html>


