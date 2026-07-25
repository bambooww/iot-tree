<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<style>
.rule
{
	position: relative;
	width:98%;
	left:1%;
	border:0px solid;
	border-color: #dddddd;
	margin-top: 5px;
}

.rule .del
{
	position: absolute;
	right:2px;
	top:2px;z-index:10;
	width:20px;
	height:20px;
	color:#dddddd;
	border-color:#dddddd;
}
.rule .del:hover
{
	background-color: red;
	
}

.row
{
	position: relative;
	width:100%;
	height:55px;
}
.row .msg
{
	position:absolute;left:30px;top:10px;
	width:40px;height:36px;
	border:0px solid #dddddd;
	vertical-align: middle;
}
.row .act
{
	position:absolute;
	left:50px;top:10px;
}
.row .mid
{
	position:absolute;
	left:207px;top:20px;
}
.row .tar_pktp
{
	position:absolute;
	left:150px;top:10px;
	width:100px
}
.row .tar_subn
{
	position:absolute;
	left:230px;top:10px;
	width:260px;
}

.row .tar_pktp .layui-edge
{
	right:80px;
}
.row .tar_pktp .layui-input
{
	padding-left: 20px;
	padding-right: 20px;
	text-align: right;
	border-right: 0px;
}
</style>

<div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;<w:g>property</w:g></span></div>
  <div class="tar_pktp" style=""> 
    <select id="prop_pktp"  class="layui-input" lay-filter="sor_valsty" style="width:100px;border-right: 0px;">
<%
	for(MNCxtPkTP pktp:MNCxtPkTP.values())
	{
%>
<option value="<%=pktp.name()%>"><%=pktp.getTitle() %>.</option>
<%
	}
%>
    </select>
  </div>
  <div class="tar_subn">
    <input type="text" id="prop_subn" class="layui-input" style="border-left: 0px;left:2px;"/>
  </div>
  </div>
  
<div id="rules">

 </div>
 
 <div class="rule" id="rule_temp" style="display:none">
  <div class="row" >
   <div class="msg"></div>
  <div class="act" style="width:150px;">
	<select id="op"  class="layui-input" lay-filter="op" style="width:100px;border-right: 0px;">
<%
	for(ValOper vo:ValOper.ALL)
	{
%>
<option value="<%=vo.getName()%>"><%=vo.getTitle() %></option>
<%
	}
%>
    </select>
  </div>
  <div class="mid"> </div>
  <div class="tar_pktp" style=""> 
    <select id="pm2_valsty"  class="layui-input" lay-filter="pm2_valsty" style="width:100px;border-right: 0px;">
<%
	for(MNCxtValSty pktp:MNCxtValSty.values())
	{
%>
<option value="<%=pktp.name()%>"><%=pktp.getTitle() %><%=pktp.isCxtPk()?".":"" %></option>
<%
	}
%>
    </select>
  </div>
  <div class="tar_subn">
    <input type="text" id="pm2_subn" class="layui-input" style="border-left: 0px;left:2px;"/>
  </div>
  </div>
  
 </div>
 
	<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid"><w:g>out_min_intv</w:g></div>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="out_min_intv" name="out_min_intv" value="0"  class="layui-input">
    </div>
    
  </div>
 
<script>

function add_rule(jo)
{
	let html = $("#rule_temp")[0].outerHTML ;
	let ele = $(html) ;
	ele.css("display","") ;
	ele.attr("id","rulex") ;
	$("#rules").append(ele) ;
	
	ele.find("#op").val("eq") ;
	ele.find("#pm2_subn").val("") ;
	ele.find("#pm2_valsty").val("vt_str") ;
	
	if(jo)
	{
		ele.find("#op").val(jo.op||"") ;
		if(jo.pm2_valsty)
			ele.find("#pm2_valsty").val(jo.pm2_valsty) ;
		ele.find("#pm2_subn").val(jo.pm2_subn||"") ;
	}
	
	form.render() ;
	return ele ;
}

function del_rule(ele)
{
	//console.log(ele) ;
	$(ele).parent().remove() ;
}

function extract_rule_jo(ele)
{
	let ret={} ;
	ret.op = ele.find("#op").val()||"" ;
	ret.pm2_valsty = ele.find("#pm2_valsty").val() ;
	ret.pm2_subn = ele.find("#pm2_subn").val()||"" ;
	return ret ;
}

function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	jo.prop_subn = $("#prop_subn").val() ;
	jo.prop_pktp = $("#prop_pktp").val() ;
	
	let rule_jos = [] ;
	//jo.rules = rule_jos ;
	$("#rules").find(".rule").each(function(){
		let ruleele = $(this) ;
		let tmpjo = extract_rule_jo(ruleele) ;
		rule_jos.push(tmpjo) ;
	}) ;
	
	if(rule_jos.length>0)
		jo.rule = rule_jos[0] ;
	
	jo.out_min_intv= get_input_val("out_min_intv",true,-1) ;
	
	return jo ;
}

function set_pm_jo(jo)
{
	if(!jo)
	{
		$("#prop_subn").val("payload") ;
		$("#prop_pktp").val("msg") ;
		
		add_rule(null) ;
		return ;
	}
	
	$("#prop_subn").val(jo.prop_subn||"payload") ;
	$("#prop_pktp").val(jo.prop_pktp||"msg") ;
	
	add_rule(jo.rule) ;
	$("#out_min_intv").val(jo.out_min_intv||0) ;
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>