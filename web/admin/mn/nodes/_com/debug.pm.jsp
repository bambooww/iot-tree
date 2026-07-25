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
.ln_items {width:95%;height:400px;border:0px solid #e6e6e6;margin-left:10px;overflow-y:auto;}
.ln_item {position: relative;border:1px solid #cccccc;height:60px;width:96%;left:2%;margin:3px;}
.ln_item .subn
{
	position:absolute;background-color:#ccc;
	left:1px;top:1px;font:15px;
}

.ln_item .tt
{
	position:absolute;
	left:7px;top:20px;font:12px;
}

.ln_item .tt2
{
	position:absolute;
	left:7px;top:40px;font:12px;
}

.ln_item .tt input {width:100px;}

.ln_item .cc
{
	position:absolute;
	left:310px;bottom:30px;font:12px;top:10px;
}

.ln_item .op
{
	position:absolute;
	right:3px;top:4px;
}

.sub_item {border:1px solid;cursor:pointer;position: relative;margin-bottom: 3px;}
.sub_item:hover {
	background-color: #ccc;
}
</style>
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>buf_len</w:g>:</label>
    <div class="layui-input-inline" style="width:150px;">
    	<input type="number" class="layui-input" id="buf_len"/>
    </div>
    <div class="layui-form-mid" style="width:20px;"></div>
    <div class="layui-input-inline" style="width:10px;">
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="sup_ana"  checked /> 
    </div>
    <div class="layui-form-mid" ><w:g>sup_ana</w:g></div>
  </div>
  
<div id="ana_cc" style="position: relative;font-size:12px;">
	<div style="position: absolute;right:2px;top:2px;width:130px;height:300px;">
		<div class="sub_item" onclick="set_ln_item('payload','pld','')">payload</div>
		<div class="sub_item" style="border:0px;">
			<input id="new_subn" type="text" value="payload." style="left:0px;right:25px;height:22px;position:absolute;"/>
			<button  class="layui-btn layui-btn-xs layui-btn-primary" onclick="add_ana_item()" style="width:22px;right:1px;position:absolute;"><i class="fa fa-plus"></i></button>
		</div>
	</div>
	
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:450px;height:300;border:1px solid #ccc;overflow-y:auto;" id="ana_list">
    	
    </div>
</div>
</div>
  <%--
  <div class="layui-form-item">
    <label class="layui-form-label">To</label>
    <div class="layui-input-inline" style="width:250px;">
    	<input type="checkbox" class="layui-input" id="out_win"/>debug window
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:250px;">
    	<input type="checkbox" class="layui-input" id="out_console"/>system console
    </div>
  </div>
   --%>
<script>

var ana_items = [] ;

function update_ui()
{
	let tmps ="" ;
	for(let lni of ana_items)
	{
		//console.log(lni) ;
		tmps += `<div class="ln_item">
			<span class="subn">\${lni.subn}</span>
			<span class="cc"><input  type="color" value="\${lni.color}" onchange="on_ln_chg('\${lni.subn}','color',this)"/></span>
			<span class="tt">Title <input value="\${lni.title}" onchange="on_ln_chg('\${lni.subn}','title',this)" />
				Unit <input style="width:50px;" value="\${lni.unit}" onchange="on_ln_chg('\${lni.subn}','unit',this)" />				
			</span>
			<span class="tt2">Y Min<input style="width:50px;"  type="number" value="\${lni.min}" onchange="on_ln_chg('\${lni.subn}','min',this)" />
				Max<input style="width:50px;"  type="number" value="\${lni.max}" onchange="on_ln_chg('\${lni.subn}','max',this)" />
					&nbsp;<input type="checkbox" \${(lni.yaxis_right?"checked":"")} lay-ignore  onchange="on_ln_chg('\${lni.subn}','yaxis_right',this)" />Right
			</span>
			<span class="op">
			<button class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_ln_item('\${lni.subn}')"><i class="fa fa-times"></i></button>
			</span>
			</div>` ;
	}
	$("#ana_list").html(tmps) ;
	form.render();
}

function on_ln_chg(subn,inp,ele)
{
	let item = get_ln_item(subn) ;
	if(!item) return ;
	let v = $(ele).val() ;
	if(inp=="yaxis_right")
		v = $(ele).prop("checked") ;
	//console.log(tagp,inp,v) ;
	item[inp] = v ;
}

function get_ln_item(subn)
{
	for(let lni of ana_items)
	{
		if(lni.subn==subn)
			return lni ;
	}
	return null ;
}

function set_ln_item(subn,tt,unit)
{
	let oldi = get_ln_item(subn) ;
	if(oldi) {dlg.msg("sub name ="+nn+" is already existed!");return;}
	
	ana_items.push({subn:subn,color:"#888888",title:tt,unit:unit,min:0,max:100}) ;
	update_ui();
}

function add_ana_item()
{
	let nn = $("#new_subn").val() ;
	if(!nn) {dlg.msg("empty sub name input");return;}
	if(nn.endsWith("."))
	{
		dlg.msg("sub name must be like xxx  xxx.yy ");return;
	}
	let oldi = get_ln_item(nn) ;
	if(oldi) {dlg.msg("sub name ="+nn+" is already existed!");return;}
	set_ln_item(nn,nn,'');
}

function del_ln_item(subn)
{
	let n = ana_items.length ;
	for(let i = 0 ; i < n ; i ++)
	{
		let lni = ana_items[i] ;
		if(lni.subn==subn)
		{
			ana_items.splice(i,1) ;
			update_ui();
			return lni ;
		}
	}
	return null ;
}

function get_pm_jo()
{
	let out_win = $("#out_win").prop("checked") ;
	let out_console = $("#out_console").prop("checked") ;
	let sup_ana = $("#sup_ana").prop("checked") ;
	let buf_len = get_input_val("buf_len",true,100) ;
	return {out_win:out_win,out_console:out_console,buf_len:buf_len,sup_ana:sup_ana,ana_items:ana_items} ;
}

function set_pm_jo(jo)
{
	$("#out_win").prop("checked",!(jo.out_win===false)) ;
	$("#out_console").prop("checked",jo.out_console) ;
	$("#sup_ana").prop("checked",jo.sup_ana===true) ;
	$("#buf_len").val(jo.buf_len||100) ;
	ana_items = jo.ana_items||[] ;
	update_ui()
}

function get_pm_size()
{
	return {w:700,h:550} ;
}

</script>