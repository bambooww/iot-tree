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

td {border:1px solid #ccc;}
</style>
<button onclick="add_rule()" style="border-color:#dddddd">+Add</button>
&nbsp;&nbsp;&nbsp;<button onclick="add_rule_multi()" style="border-color:#dddddd">+Add Multi</button>
&nbsp;&nbsp;&nbsp;<input type="checkbox" id="tag_val_detail" lay-skin="primary" />Output Detail
<table style="max-height: 480px;width:100%;">
	<thead>
		<tr>
			<td>Tag</td>
			<td>Var</td>
			<td>Title</td>
			<td>Unit</td>
			<td><w:g>must_ok</w:g></td>
			<td>Oper</td>
		</tr>
	</thead>
	<tbody id="rules" style="overflow-y:auto;">
		
	</tbody>
</table>
<table style="display:none">
  <tr id="rule_temp" >
    <td><input type="text" id="tag" style="width:200px;" onclick="sel_tag(this,'r')"/></td>
	<td><input type="text" id="varn" style="width:120px;"/></td>
	<td><input type="text" id="t" style="width:150px;"/></td>
	<td><input type="text" id="unit" style="width:80px;"/></td>
	<td><input type="checkbox" id="must_ok" class="layui-input" lay-skin="primary" /></td>
	<td>
		<button class="del" onclick="up_or_down(this,true)"><i class="fa-solid fa-arrow-up"></i></button>
		<button class="del" onclick="up_or_down(this,false)"><i class="fa-solid fa-arrow-down"></i></button>
		<button class="del" onclick="del_rule(this)"><i class="fa fa-times"></i></button>
	</td>
  </tr>
</table>

<%--
<div id="rules" style="overflow-y:auto;max-height: 480px;">
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
  <div class="mid">Title</div>
  <div class="tar_subn">
    <input type="text" id="t" class="layui-input" style="left:2px;"/>
  </div>
  <div class="mid">Unit</div>
  <div class="tar_subn">
    <input type="text" id="unit" class="layui-input" style="left:2px;"/>
  </div>
  <div class="rrr">
    <input type="checkbox" id="must_ok" class="layui-input" lay-skin="primary" /><w:g>must_ok</w:g>
  </div>
  </div>
  
 </div>
  --%>
<script>

var prjpath = "<%=prjpath%>" ;

function up_or_down(ele, b_up_or_down) {
	let $tr = $(ele).parent().parent();
    if (b_up_or_down) {
        var $prev = $tr.prev('tr');
        if ($prev.length) {
            $tr.insertBefore($prev);
            return true;
        }
        return false;
    } else {
        var $next = $tr.next('tr');
        if ($next.length) {
            $tr.insertAfter($next);
            return true;
        }
        return false;
    }
}

function sel_tag(ele,rw)
{
	let seltagids = [] ;
	let w_only = "" ;
	if(rw=='r')
	{
	}
	else if(rw=='w')
	{
		w_only = "true" ;
	}
	else
		return ;
	
	dlg.open("../ua_cxt/di_cxt_tag_selector.jsp?w_only="+w_only+"&multi=false&path="+prjpath,//+"&val="+tmpv,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagids:seltagids},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_select_tag();
					let tr = $(ele).parent().parent();
					update_row(tr,ret);
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function update_row(tr,ret)
{
	if(!ret)
	{
		tr.find("#tag").val("") ;return;
	}
		
	tr.find("#tag").val(ret.tagp) ;
	let v = tr.find("#varn").val();
	if(!v && ret.tagp)
	{
		let ss = ret.tagp.split('.') ;
		v = ss[ss.length-1] ;
		tr.find("#varn").val(v);
	}
	let t = tr.find("#t").val();
	if(!t && ret.tagt)
	{
		let ss = ret.tagt.split('/') ;
		v = ss[ss.length-1] ;
		tr.find("#t").val(v);
	}
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
		ele.find("#t").val(jo.t||"") ;
		ele.find("#unit").val(jo.unit||"") ;
		ele.find("#must_ok").prop("checked",jo.must_ok||false) ;
	}
	
	form.render() ;
	return ele ;
}

function del_rule(ele)
{
	//console.log(ele) ;
	$(ele).parent().parent().remove() ;
}

function add_rule_multi()
{
	dlg.open(`\${PM_URL_BASE}/../../ua_cxt/cxt_tag_selector.jsp?path=\${prjpath}&multi=true&bind_tag_only=true`,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:[]},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tags() ; //{tagid:tagid,tagp:tagp,tagt:patht}
					if(!ret || ret.length<=0)
					{
						dlg.msg("please select tags");return ;
					}
					tags_jarr = ret ;
					if(tags_jarr&&tags_jarr.length>0)
					{
						for(let tag of tags_jarr)
						{
							let tr = add_rule(null) ;
							update_row(tr,tag);
						}
					}
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function extract_rule_jo(ele)
{
	let ret={} ;
	ret.tag = ele.find("#tag").val()||"" ;
	ret.varn = ele.find("#varn").val() ;
	ret.must_ok = ele.find("#must_ok").prop("checked") ;
	ret.t=ele.find("#t").val();
	ret.unit=ele.find("#unit").val();
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
	
	$("#rules").find("tr").each(function(){
		let ruleele = $(this) ;
		let tmpjo = extract_rule_jo(ruleele) ;
		rule_jos.push(tmpjo) ;
	}) ;
	jo.tag_val_detail = $("#tag_val_detail").prop("checked") ;
	
	return jo ;
}

function set_pm_jo(jo)
{
	$("#tag_val_detail").prop("checked",jo.tag_val_detail===true) ;
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