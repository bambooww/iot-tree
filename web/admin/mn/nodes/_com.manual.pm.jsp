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
	border:1px solid;
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
	position:absolute;left:10px;top:10px;
	width:40px;height:36px;
	border:1px solid #dddddd;
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
	left:220px;top:10px;
	width:100px
}
.row .tar_subn
{
	position:absolute;
	left:300px;top:10px;
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
<button onclick="add_rule()" style="border-color:#dddddd">+Add</button>
<div id="rules">
</div>
 <div class="rule" id="rule_temp" style="display:none">
  <button class="del" onclick="del_rule(this)">X</button>
  
  <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;$msg.</span></div>
  <div class="act" style="width:150px;">
	<input type="text" class="layui-input" id="msg_subn" style="border-left:0px;"/>
  </div>
  <div class="mid">=</div>
  <div class="tar_pktp" style=""> 
    <select id="sor_valsty"  class="layui-input" lay-filter="sor_valsty" style="width:100px;border-right: 0px;">
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
    <input type="text" id="sor_subn" class="layui-input" style="border-left: 0px;left:2px;"/>
  </div>
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
	
	ele.find("#msg_subn").val("payload") ;
	ele.find("#sor_valsty").val("vt_str") ;
	
	if(jo)
	{
		ele.find("#msg_subn").val(jo.msg_subn||"") ;
		if(jo.sor_valsty)
			ele.find("#sor_valsty").val(jo.sor_valsty) ;
		ele.find("#sor_subn").val(jo.sor_subn||"") ;
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
	ret.msg_subn = ele.find("#msg_subn").val()||"" ;
	ret.sor_valsty = ele.find("#sor_valsty").val() ;
	ret.sor_subn = ele.find("#sor_subn").val()||"" ;
	return ret ;
}

function on_after_pm_show(form)
{
	 
}


function get_chk_vals(name)
{
	let ret=[] ;
	$("input[name="+name+"]").each( function () {
	       let ob = $(this) ;
	       if(ob.prop("checked"))
	    	   ret.push(ob.val()) ;
	   });
	return ret ;
}

function get_pm_jo()
{
	let jo = {} ;
	let rule_jos = [] ;
	jo.rules = rule_jos ;
	
	$("#rules").find(".rule").each(function(){
		let ruleele = $(this) ;
		let tmpjo = extract_rule_jo(ruleele) ;
		rule_jos.push(tmpjo) ;
	}) ;
	
	//console.log(jo) ;
	return jo ;
}

function set_pm_jo(jo)
{
	if(!jo || !jo.rules || jo.rules.length<=0)
	{
		add_rule(null) ;
		return ;
	}
	
	//console.log("set pm",jo) ;
	for(let rule of jo.rules)
	{
		let ele = add_rule(rule) ;
		update_rule(ele) ;
	}
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>