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
	if(!Convert.checkReqEmpty(request, out, "container_id"))
		return ;
	
	String container_id = request.getParameter("container_id") ;
UAPrj prj = UAManager.getInstance().getPrjById(container_id) ;
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
	left:257px;top:20px;
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
	left:290px;top:10px;
	width:100px;
}
.row .rrr
{
	position:absolute;
	left:405px;top:20px;
	width:150px;
}
</style>
<button onclick="add_rule()" style="border-color:#dddddd">+Add</button>
<div id="rules">
</div>
 <div class="rule" id="rule_temp" style="display:none">
  <button class="del" onclick="del_rule(this)">X</button>
  
  <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Tag:</span></div>
  <div class="act" style="width:200px;">
	<input type="text" class="layui-input" id="tag" style="border-left:0px;" onclick="sel_tag(this,'r')"/>
  </div>
  <div class="mid">Var</div>
  <div class="tar_subn">
    <input type="text" id="varn" class="layui-input" style="left:2px;"/>
  </div>
  <div class="rrr">
    <input type="checkbox" id="must_ok" class="layui-input" lay-skin="primary" /><w:g>must_ok</w:g>
  </div>
  </div>
  
 </div>
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

	if(jo)
	{
		ele.find("#tag").val(jo.tag||"") ;
		ele.find("#varn").val(jo.varn||"") ;
		ele.find("#must_ok").prop("checked",jo.must_ok||false) ;
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
	ret.tag = ele.find("#tag").val()||"" ;
	ret.varn = ele.find("#varn").val() ;
	ret.must_ok = ele.find("#must_ok").prop("checked") ;
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
	
	//console.log("set pm",jo) ;
	for(let rule of jo.tags)
	{
		let ele = add_rule(rule) ;
		//update_rule(ele) ;
	}
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>