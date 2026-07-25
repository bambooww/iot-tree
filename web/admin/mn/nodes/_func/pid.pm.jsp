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
    <label class="layui-form-label"><w:g>gain</w:g> (P)</label>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="kp" name="kp"  lay-filter="kp"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>integral</w:g> (I)</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="ki" name="ki"  lay-filter="ki" min="0.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>derivative</w:g> (D)</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="kd" name="kd"  lay-filter="kd" min="0.0"  autocomplete="off" class="layui-input" />
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"><w:g>sample_t</w:g>:</label>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="st" name="st"  lay-filter="st" min="0.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid">(<w:g>second</w:g>)</div>
    <div class="layui-form-mid" style="color:blue"><w:g>st_desc</w:g></div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"><w:g>in_rg</w:g>:</label>
    <div class="layui-form-mid"><w:g>low</w:g>:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="input_min" name="input_min"  lay-filter="input_min" min="0.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>high</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="input_max" name="input_max"  lay-filter="input_max" min="100.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid" style="color:blue"><w:g>in_range</w:g></div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"><w:g>ctrl,out</w:g>:</label>
    <div class="layui-form-mid"><w:g>low</w:g>:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="output_min" name="output_min"  lay-filter="output_min" min="200.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>high</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="output_max" name="output_max"  lay-filter="output_max" min="10000.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid" style="color:blue"><w:g>out_range</w:g></div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid"><w:g>output_stop_v</w:g>:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="output_stop_v" name="output_stop_v"  lay-filter="output_stop_v" min="200.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid" style="color:#e3b267"><w:g>output_stop_v_d</w:g></div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid"><w:g>output_err_v</w:g></div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="output_err_v" name="output_err_v"  lay-filter="output_err_v" min="10000.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid" style="color:red"><w:g>output_err_v_d</w:g></div>
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
	var n = $('#'+id).val();
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
	let chkres = check_no_input(["kp","ki","kd","st","input_min","input_max","output_min","output_max"],
			["<w:g>gain</w:g>","<w:g>integral</w:g>","<w:g>derivative</w:g>","<w:g>sample_t</w:g>",
				"<w:g>in_rg</w:g><w:g>low</w:g>","<w:g>in_rg</w:g><w:g>high</w:g>",
				"<w:g>out_rg</w:g><w:g>low</w:g>","<w:g>out_rg</w:g><w:g>high</w:g>",
			]);
	if(chkres!=true)
		return chkres ;
	
	let js_ob={} ;
	js_ob.kp = get_input_fval("kp",1.0,true) ;
	js_ob.ki = get_input_fval("ki",10.0,true) ;
	js_ob.kd = get_input_fval("kd",0.0,true) ;
	js_ob.st = get_input_fval("st",1.0,true) ;
	js_ob.input_min = get_input_fval("input_min",0.0,true) ;
	js_ob.input_max = get_input_fval("input_max",100.0,true) ;
	if(js_ob.input_min>=js_ob.input_max)
		return "<w:g>low</w:g> &gt;= <w:g>high</w:g>" ;
	js_ob.output_min = get_input_fval("output_min",0.0,true) ;
	js_ob.output_max = get_input_fval("output_max",100.0,true) ;
	//console.log(js_ob);
	if(js_ob.output_min>=js_ob.output_max)
		return "<w:g>low</w:g> &gt;= <w:g>high</w:g>" ;
	
	js_ob.output_stop_v = get_input_fval("output_stop_v",0.0,true) ;
	js_ob.output_err_v = get_input_fval("output_err_v",0.0,true) ;
	
	return js_ob ;
}

function set_pm_jo(jo)
{
	$('#kp').val(get_jo_val(jo,'kp',1.0)) ;//jo.kp||1.0);
	$('#ki').val(get_jo_val(jo,'ki',10.0)) ;//.val(jo.ki||10.0);
	$('#kd').val(get_jo_val(jo,'kd',0.0)) ;//.val(jo.kd||0.0);
	$('#st').val(get_jo_val(jo,'st',1.0)) ;//.val(jo.st||1.0);
	$("#input_min").val(get_jo_val(jo,'input_min',0.0)) ;//.val(jo.input_min||0.0) ;
	$("#input_max").val(get_jo_val(jo,'input_max',100.0)) ;//.val(jo.input_max||100.0) ;
	$("#output_min").val(get_jo_val(jo,'output_min',0.0)) ;//.val(jo.output_min||0.0) ;
	$("#output_max").val(get_jo_val(jo,'output_max',10.0)) ;//.val(jo.output_max||10.0) ;
	$("#output_stop_v").val(get_jo_val(jo,'output_stop_v',0.0)) ;
	$("#output_err_v").val(get_jo_val(jo,'output_err_v',0.0)) ;
	update_ui() ;
}

function get_jo_val(jo,n,defv)
{
	let vv = jo[n];
	if(vv===null||vv===undefined||vv==="")
		return defv ;
	return vv ;
}

function get_pm_size()
{
	return {w:800,h:450} ;
}

//on_init_pm_ok() ;
</script>