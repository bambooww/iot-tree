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

.seg .del
{
	right:2px;
	top:2px;z-index:10;
	width:20px;
	height:20px;
	color:#dddddd;
	border-color:#dddddd;
}
.seg .del:hover
{
	background-color: red;
	
}
</style>

  <div class="layui-form-item">
    <label class="layui-form-label">Work Dir:</label>

    <div class="layui-input-inline" style="width:350px;">
    	<input type="text" class="layui-input" id="work_dir" />
    </div>
    <div class="layui-form-mid">
    <button onclick="on_dir_sel()" style="border-color:#dddddd">...</button>
    </div>
     <div class="layui-form-mid">&nbsp;</div>
</div>
 	
 <div class="layui-form-item">
    <label class="layui-form-label">Cmd:</label>

    <div class="layui-input-inline" style="width:350px;">
    	<input type="text" class="layui-input" id="cmd" onchange="update_bt()"/>
    </div>
    <div class="layui-form-mid">
    <button onclick="add_rule()" style="border-color:#dddddd">+Add Cmd Segment</button>
    </div>
     <div class="layui-form-mid">&nbsp;</div>
</div>
<div id="cmd_segs">
</div>


 <div class="layui-form-item" id="">
    <label class="layui-form-label">Timeout:</label>
    <div class="layui-input-inline" style="width:150px;">
    	<input type="number" class="layui-input" id="timeout" />
    </div>
    <div class="layui-form-mid">MS  Output Encoding</div>
    <div class="layui-input-inline" style="width:150px;">
    	<input type="text" class="layui-input" id="out_enc" />
    </div>
</div>
 <div class="layui-form-item">
    <label class="layui-form-label">Preview:</label>
    <div class="layui-form-mid"  id="prev">
    	
    </div>
</div>

<div class="layui-form-item seg" style="display:none" id="rule_temp">

    <label class="layui-form-label"></label>
    <div class="layui-form-mid">+</div>
    <div class="layui-input-inline" style="width:100px;">
        <select id=valsty  class="layui-input" lay-filter="valsty" style="width:80px;border-right: 0px;">
<%
	for(MNCxtValSty vs:MNCxtValSty.values())
	{
%>
        <option value="<%=vs.name()%>"><%=vs.getTitle() %>.</option>
<%
	}
%>
    </select>
    </div>
    <div class="layui-form-mid">.</div>
    <div class="layui-input-inline" style="width:200px;">
    	<input type="text" class="layui-input" id="subn"  onchange="update_bt()"/>
    </div>
    <div class="layui-form-mid" >
    	<input type="checkbox" class="layui-input" id="qm" lay-skin="primary" lay-filter="qm" />
    </div>
    <div class="layui-form-mid" >
    	quotation mark
    </div>
    <div class="layui-form-mid" >
    	<button class="del" onclick="del_rule(this)">X</button>
    </div>
    
</div>
<script>


function on_dir_sel()
{
	dlg.open("/admin/util/dir_selector.jsp",{title:"Select Dir",w:'500px',h:'400px'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					let seldir = dlgw.get_sel_dir();
					if(!seldir)
					{
						dlg.msg("no dir selected");return;
					}
					$("#work_dir").val(seldir) ;
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
	$("#cmd_segs").append(ele) ;
	
	if(jo)
	{
		if(jo.valsty)
			ele.find("#valsty").val(jo.valsty) ;
		ele.find("#subn").val(jo.subn||"") ;
		ele.find("#qm").prop("checked",jo.qm) ;
	}
	update_bt();
	form.render() ;
	return ele ;
}

function del_rule(ele)
{
	$(ele).parent().parent().remove() ;
	update_bt();
}

function extract_rule_jo(ele)
{
	let ret={} ;
	ret.valsty = ele.find("#valsty").val() ;
	ret.subn = ele.find("#subn").val() ;
	ret.qm = ele.find("#qm").prop("checked");
	
	return ret ;
}

function on_after_pm_show(form)
{
	form.on('select(valsty)', function (data) {
		update_bt();
	});
	form.on('checkbox(qm)', function (data) {
		update_bt();
	});

	update_bt();
}
var PKS = ["msg","node","flow","prj"];
function update_bt()
{
	let cmd = $("#cmd").val()||"";
	$("#cmd_segs").find(".seg").each(function(){
		let ruleele = $(this) ;
		let tmpjo = extract_rule_jo(ruleele) ;
		cmd += " " ;
		if(tmpjo.qm)
			cmd +="\"";
		if(PKS.indexOf(tmpjo.valsty)>=0)
		{
			cmd+="{{ "+(tmpjo.valsty+"."+tmpjo.subn||"")+" }}" ;
		}
		else
			cmd +=tmpjo.subn||"" ;
		if(tmpjo.qm)
			cmd +="\"";
	}) ;
	
	$("#prev").text(cmd) ;
}

function get_pm_jo()
{
	let cmd = $("#cmd").val();
	let work_dir= $("#work_dir").val();
	let mid_sty  = $("#mid_sty").val();
	let mid_sub = $("#mid_sub").val();
	let cmd_tail = $("#cmd_tail").val();
	let timeout = get_input_val("timeout",-1,true) ;
	let out_enc =  $("#out_enc").val();
	let segs = [] ;
	
	$("#cmd_segs").find(".seg").each(function(){
		let ruleele = $(this) ;
		let tmpjo = extract_rule_jo(ruleele) ;
		segs.push(tmpjo) ;
	}) ;
	
	return {cmd:cmd,work_dir:work_dir,timeout:timeout,out_enc:out_enc,segs:segs} ;
}

function set_pm_jo(jo)
{
	$("#cmd").val(jo.cmd||"") ;
	$("#work_dir").val(jo.work_dir||"");
	$("#timeout").val(jo.timeout||"") ;
	$("#out_enc").val(jo.out_enc||"");
	if(jo.mid_sty)
		$("#mid_sty").val(jo.mid_sty) ;
	$("#mid_sub").val(jo.mid_sub||"") ;
	
	if(jo.segs)
	{
		for(let seg of jo.segs)
			add_rule(seg) ;
	}
	else
	{
		add_rule({}) ;
	}
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>