<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>

<div class="layui-form-item">
    <label class="layui-form-label"><w:g>heat_pow,input</w:g>:</label>
    <div class="layui-form-mid"><w:g>signal,low</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="in_signal_low" name="in_signal_low"  lay-filter="in_signal_low"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>signal,high</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="in_signal_high" name="in_signal_high"  lay-filter="in_signal_high"  autocomplete="off" class="layui-input" />
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"><w:g>temp</w:g></label>
    <div class="layui-form-mid"><w:g>ambient_t</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="ambient_t" name="ambient_t"  lay-filter="ambient_t"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>init_t</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="init_t" name="init_t"  lay-filter="init_t" autocomplete="off" class="layui-input" />
    </div>
</div>

<div class="layui-form-item">
	<label class="layui-form-label"></label>
    <div class="layui-form-mid"><w:g>temp,low</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="temp_low" name="temp_low"  lay-filter="temp_low"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>temp,high</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="temp_high" name="temp_high"  lay-filter="temp_high"  autocomplete="off" class="layui-input" />
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid"><w:g>signal,low</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="out_signal_low" name="out_signal_low"  lay-filter="out_signal_low"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>signal,high</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="out_signal_high" name="out_signal_high"  lay-filter="out_signal_high"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>out_intv</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="out_intv" name="out_intv"  step="1" lay-filter="out_intv"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>ms</w:g></div>
</div>

<script>
function on_after_pm_show(form)
{
	form.on("checkbox(using_sor)",function(obj){
		update_ui() ;
	}) ;
}

function update_ui()
{
	
}

function get_input_fval(id,defv,bnum)
{
	let n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseFloat(n);
	return n;
}

function check_no_input(ids,tts)
{
	for(let i = 0 ; i < ids.length ; i ++)
	{
		let id = ids[i] ;
		let v = $("#"+id).val() ;
		if(!v)
		{
			return "<w:g>pls_input</w:g>  "+tts[i];
		}
	}
	return true;
}

function get_pm_jo()
{
	let js_ob={} ;
	js_ob.ambient_t = get_input_fval("ambient_t",25.0,true) ;
	js_ob.init_t = get_input_fval("init_t",25.0,true) ;
	js_ob.in_signal_low = get_input_fval("in_signal_low",0.0,true) ;
	js_ob.in_signal_high = get_input_fval("in_signal_high",10.0,true) ;
	js_ob.temp_low = get_input_fval("temp_low",0.0,true) ;
	js_ob.temp_high = get_input_fval("temp_high",150.0,true) ;
	if(js_ob.temp_low>=js_ob.temp_high)
		return "<w:g>low</w:g> &gt;= <w:g>high</w:g>" ;
	js_ob.out_signal_low = get_input_fval("out_signal_low",4000,true) ;
	js_ob.out_signal_high = get_input_fval("out_signal_high",20000,true) ;
	
	if(js_ob.out_signal_low>=js_ob.out_signal_high)
		return "<w:g>low</w:g> &gt;= <w:g>high</w:g>" ;
	js_ob.out_intv = get_input_val("out_intv",1000,true) ;
	if(js_ob.out_intv<=0)
		return "<w:g>pls,input,out_intv</w:g>"
	return js_ob ;
}

function get_jo_val(jo,n,defv)
{
	let vv = jo[n];
	if(vv===null||vv===undefined||vv==="")
		return defv ;
	return vv ;
}

function set_pm_jo(jo)
{//console.log(jo);
	$('#ambient_t').val(get_jo_val(jo,'ambient_t',25.0));
	$('#init_t').val(get_jo_val(jo,'init_t',25.0));
	$('#in_signal_low').val(get_jo_val(jo,'in_signal_low',0.0));
	$('#in_signal_high').val(get_jo_val(jo,'in_signal_high',10.0));
	$("#temp_low").val(get_jo_val(jo,'temp_low',0.0));
	$("#temp_high").val(get_jo_val(jo,'temp_high',150.0));
	$("#out_signal_low").val(get_jo_val(jo,'out_signal_low',4000));
	$("#out_signal_high").val(get_jo_val(jo,'out_signal_high',20000));
	$("#out_intv").val(get_jo_val(jo,'out_intv',1000));
	update_ui() ;
}

function get_pm_size()
{
	return {w:800,h:450} ;
}

//on_init_pm_ok() ;
</script>