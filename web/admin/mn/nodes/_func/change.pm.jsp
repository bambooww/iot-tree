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

.row .act
{
	position:absolute;
	left:30px;top:10px;
}
.row .tar_pktp
{
	position:absolute;
	left:150px;top:10px;
	width:80px
}
.row .tar_subn
{
	position:absolute;
	left:220px;top:10px;
	width:230px;
}

.row .tar_pktp .layui-edge
{
	right:60px;
}
.row .tar_pktp .layui-input
{
	padding-left: 20px;
	padding-right: 10px;
	text-align: right;
	border-right: 0px;
}
</style>
Rule: <button onclick="add_rule()" style="border-color:#dddddd">+Add Rule</button>
<div id="rules">
</div>
 <div class="rule" id="rule_temp" style="display:none">
  <button class="del" onclick="del_rule(this)">X</button>
  <div class="row">
  <div class="act" style="width:100px;">
	<select id="act"  name="act" class="layui-input" lay-filter="act">
<%
	for(CxtChgRule.Action rt:CxtChgRule.Action.values())
	{
%>
        <option value="<%=rt.name()%>"><%=rt.getTitle() %></option>
<%
	}
%>
    </select>
   </div>
   <div class="tar_pktp" style="">
    <select id="tar_pktp"  class0="layui-input" lay-filter="tar_pktp" style="width:80px;border-right: 0px;">
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
    <input type="text" id="tar_subn" class="layui-input" style="border-left: 0px;left:2px;"/>
  </div>
  </div>
  
  <div class="row sor" >
  <div class="act" style="width:100px;text-align: right;white-space: nowrap;">
	<w:g>to_the_val</w:g><br/>
	<input type="checkbox" id="sor_deep_cp"  class="layui-input" lay-skin="primary" /><w:g>deep_cp</w:g>
  </div>
  <div class="tar_pktp" style="">
    <select id="sor_valsty"  class="layui-input" lay-filter="sor_valsty" style="width:80px;border-right: 0px;">
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
  
  <div class="row to" style="display:none">
  <div class="act" style="width:100px;text-align: right;">
	<w:g>to</w:g>
  </div>
  <div class="tar_pktp" style="">
    <select id="to_pktp"  class="layui-input" lay-filter="to_pktp" style="width:80px;border-right: 0px;">
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
    <input type="text" id="to_subn" class="layui-input" style="border-left: 0px;left:2px;"/>
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
	
	if(jo)
	{
		ele.find("#act").val(jo.act) ;
		ele.find("#tar_pktp").val(jo.tar_pktp) ;
		ele.find("#tar_subn").val(jo.tar_subn) ;
		if(jo.pm)
		{
			if(jo.pm.sor_valsty)
				ele.find("#sor_valsty").val(jo.pm.sor_valsty);
			ele.find("#sor_subn").val(jo.pm.sor_subn||"") ;
			ele.find("#sor_deep_cp").prop("checked",jo.pm.sor_deep_cp||false) ;
			
			if(jo.pm.to_pktp)
				ele.find("#to_pktp").val(jo.pm.to_pktp) ;
			ele.find("#to_subn").val(jo.pm.to_subn||""); 
		}
		
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
	ret.act = ele.find("#act").val() ;
	ret.tar_pktp = ele.find("#tar_pktp").val() ;
	ret.tar_subn = ele.find("#tar_subn").val()||"" ;
	ret.pm={} ;
	ret.pm.sor_valsty = ele.find("#sor_valsty").val();
	ret.pm.sor_subn = ele.find("#sor_subn").val()||"";
	ret.pm.sor_deep_cp =  ele.find("#sor_deep_cp").prop("checked") ;
	
	ret.pm.to_pktp = ele.find("#to_pktp").val() ;
	ret.pm.to_subn = ele.find("#to_subn").val()||"";
	return ret ;
}

function on_after_pm_show(form)
{
	  form.on('select(act)', function (data) {
		  update_rule($(data.elem).parents(".rule")) ;
		});
	  
}

function update_rule(rule_ob)
{
	//console.log(rule_ob) ;
	let actv = rule_ob.find("#act").val() ;
	//console.log(actv) ;
	switch(actv)
	{
	case "del":
		rule_ob.find(".sor").css("display","none") ;
		rule_ob.find(".to").css("display","none") ;
		break ;
	case "set":
		rule_ob.find(".sor").css("display","") ;
		rule_ob.find(".to").css("display","none") ;
		break ;
	case "move":
		rule_ob.find(".sor").css("display","none") ;
		rule_ob.find(".to").css("display","") ;
		break ;
	default:
		
		break ;
	}
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
	if(!jo) return ;
	let rules = jo.rules ;
	if(!rules) return ;
	
	//console.log("set pm",jo) ;
	for(let rule of rules)
	{
		let ele = add_rule(rule) ;
		update_rule(ele) ;
	}
}

function get_pm_size()
{
	return {w:500,h:650} ;
}

//on_init_pm_ok() ;
</script>