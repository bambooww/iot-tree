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
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
	
	String prjid = request.getParameter("prjid") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}

String prjpath = prj.getNodePath() ;
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
	position:absolute;left:200px;top:10px;
	width:40px;height:36px;
	border:1px solid #dddddd;
	vertical-align: middle;
}
.row .act
{
	position:absolute;
	left:230px;top:10px;
}
.row .mid
{
	position:absolute;
	left:450px;top:20px;
}
.row .tar_pktp
{
	position:absolute;
	left:470px;top:10px;
	width:100px
}
.row .tar_subn
{
	position:absolute;
	left:550px;top:10px;
	width:140px;
}

.row .l1
{
	position:absolute;
	left:20px;top:20px;
	width:30px;white-space: nowrap;
}
.row .l2
{
	position:absolute;
	left:60px;top:10px;
	width:105px;white-space: nowrap;
}
.row .l3
{
	position:absolute;
	left:170px;top:20px;
	width:30px;white-space: nowrap;
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
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:20%;">
      <input type="checkbox" class="layui-input" lay-skin="primary" id="asyn" /> <w:g>asyn_run</w:g>
    </div>
  </div>
<div id="rules">
</div>
 <div class="rule" id="rule_temp" style="display:none">
  <button class="del" onclick="del_rule(this)">X</button>
  
  <div class="row" >
  <div class="l1">
    Delay
  </div>
  <div class="l2">
    <input type="number" id="w_delay" class="layui-input" value="0"/>
  </div>
  <div class="l3">MS</div>
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Tag:</span></div>
  <div class="act" style="width:220px;">
	<input type="text" class="layui-input" id="tag" style="border-left:0px;" onclick="sel_tag(this,'w')"/>
  </div>
  <div class="mid">=</div>
  <div class="tar_pktp" style=""> 
    <select id="w_valsty"  class="layui-input" lay-filter="w_valsty" style="width:100px;border-right: 0px;">
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
    <input type="text" id="w_subn" class="layui-input" style="border-left: 0px;left:2px;"/>
  </div>
  
  </div>
  
 </div>
 <button onclick="add_rule()" style="border-color:#dddddd">+Add</button>
<script>
var prjpath = "<%=prjpath%>" ;

function sel_tag(ele,rw)
{
	let seltagids = [] ;
	let w_only = "" ;
	if(rw=='r')
	{
//		seltagids = out_tagids ;
	}
	else if(rw=='w')
	{
		//seltagids = in_tagids ;
		w_only = "true" ;
	}
		
	else
		return ;
	
	dlg.open("../ua_cxt/cxt_tag_selector.jsp?w_only="+w_only+"&multi=false&path="+prjpath,//+"&val="+tmpv,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagids:seltagids},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tagpaths();
					if(!ret && ret.length<=0)
						$(ele).val("") ;
					else
						$(ele).val(ret[0]) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function add_rule(jo)
{
	let html = $("#rule_temp")[0].outerHTML ;
	let ele = $(html) ;
	ele.css("display","") ;
	ele.attr("id","rulex") ;
	$("#rules").append(ele) ;
	
	ele.find("#w_subn").val("") ;
	ele.find("#w_valsty").val("vt_bool") ;
	
	if(jo)
	{
		ele.find("#w_delay").val(jo.w_delay||0) ;
		ele.find("#tag").val(jo.tag||"") ;
		if(jo.w_valsty)
			ele.find("#w_valsty").val(jo.w_valsty) ;
		ele.find("#w_subn").val(jo.w_subn||"") ;
	}
	
	form.render() ;
	
	return ele ;
}

function del_rule(ele)
{
	//console.log(ele) ;
	$(ele).parent().remove() ;
	update_rules();
}


function extract_rule_jo(ele)
{
	let ret={} ;
	ret.w_delay = get_input_val("w_delay",true,0) ;
	ret.tag = ele.find("#tag").val() ;
	ret.w_valsty = ele.find("#w_valsty").val() ;
	ret.w_subn = ele.find("#w_subn").val()||"" ;
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
	jo.asyn = $("#asyn").prop("checked") ;
	
	let rule_jos = [] ;
	jo.tags = rule_jos ;
	$("#rules").find(".rule").each(function(){
		let ruleele = $(this) ;
		let tmpjo = extract_rule_jo(ruleele) ;
		rule_jos.push(tmpjo) ;
	}) ;
	return jo ;
}

function set_pm_jo(jo)
{
	if(!jo || !jo.tags || jo.tags.length<=0)
	{
		add_rule(null) ;
		return ;
	}
	
	$("#asyn").prop("checked",jo.asyn||false) ;
	
	for(let rule of jo.tags)
	{
		add_rule(rule) ;
	}
	
}

function get_pm_size()
{
	return {w:750,h:550} ;
}

//on_init_pm_ok() ;
</script>