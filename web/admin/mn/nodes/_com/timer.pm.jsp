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
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid"><input type="checkbox" class="layui-input" id="b_delay" lay-skin="primary"/>trigger once after</div>
    <div class="layui-input-inline" style="width:150px;"><input type="number" class="layui-input" id="delay_ms" /></div>
    <div class="layui-form-mid">mills seconds, then</div>
    
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Repeat:</label>
    <div class="layui-input-inline" style="width:250px;">
		<select id="repeat_tp"  name="repeat_tp" class="layui-input" lay-filter="repeat_tp">
<%
	for(RepeatTP rt:RepeatTP.values())
	{
%>
        <option value="<%=rt.getInt()%>"><%=rt.getTitle() %></option>
<%
	}
%>
      </select>
    </div>
    <div class="layui-form-mid"> at interval:</div>
     <div class="layui-input-inline" style="width:100px;">
     	<input type="number" class="layui-input" id="interval_ms" />
     </div>
     <div class="layui-form-mid">MS</div>
  </div>
  <div id="bt_cont">
  <div class="layui-form-item">
    <label class="layui-form-label">Between:</label>
    <div class="layui-input-inline" style="width:100px;">
    	<input type="time" class="layui-input" id="between_s" />
    </div>
    <div class="layui-form-mid"> -- </div>
    <div class="layui-input-inline" style="width:100px;">
    	<input type="time" class="layui-input" id="between_e" />
    </div>
</div>
  <div class="layui-form-item">
    <label class="layui-form-label">On:</label>
    
    <div class="layui-input-inline" style="width:450px;">
    	<input type="checkbox" class="layui-input" lay-skin="primary" value="" id="b_on_week"/>Filter by week
    </div>
</div>
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:450px;">
    	
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="on_week_<%=Calendar.MONDAY %>"    value="<%=Calendar.MONDAY %>"     name="on_week"/><w:g>monday</w:g>
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="on_week_<%=Calendar.TUESDAY %>"   value="<%=Calendar.TUESDAY %>"    name="on_week"/><w:g>tuesday</w:g>
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="on_week_<%=Calendar.WEDNESDAY %>" value="<%=Calendar.WEDNESDAY %>"  name="on_week"/><w:g>wednesday</w:g>
    	<br>
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="on_week_<%=Calendar.THURSDAY %>"  value="<%=Calendar.THURSDAY %>"   name="on_week"/><w:g>thursday</w:g>
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="on_week_<%=Calendar.FRIDAY %>"    value="<%=Calendar.FRIDAY %>"     name="on_week"/><w:g>friday</w:g>
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="on_week_<%=Calendar.SATURDAY %>"  value="<%=Calendar.SATURDAY %>"   name="on_week"/><w:g>saturday</w:g>\
    	<br>
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="on_week_<%=Calendar.SUNDAY %>"    value="<%=Calendar.SUNDAY %>"     name="on_week"/><w:g>sunday</w:g>
    </div>
</div>
</div>
<script>

function on_after_pm_show(form)
{
	form.on('switch(b_on_week)', function (data) {
		  set_chk_all("on_week",data.elem.checked);
		});
	  form.on('select(repeat_tp)', function (data) {
		  update_bt();
		});
	  update_bt();
}

function update_bt()
{
	if($("#repeat_tp").val()==2)
		$("#bt_cont").css("display","") ;
	else
		$("#bt_cont").css("display","none") ;
}

function set_chk_all(name,b_en)
{
	$("input[name="+name+"]").each( function () {
	       let ob = $(this) ;
	       if(b_en)
	       {
	    	   ob.prop("disabled",false);
	    	   ob.prop("checked",true);
	       }
	       else
	       {
	    	   ob.prop("disabled",true);
	    	   ob.prop("checked",false);
	       }
	       form.render();
	});
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
	let b_delay = $("#b_delay").prop("checked") ;
	let delay_ms = get_input_val("delay_ms",-1,true) ;
	if(b_delay && delay_ms<=0)
	{
		return "<w:g>pls,input,delay</w:g>" ;
	}
	
	let repeat_tp = get_input_val("repeat_tp",0,true);
	let interval_ms = get_input_val("interval_ms",10000,true);
	
	let between_s= $("#between_s").val() ;
	let between_e= $("#between_e").val() ;
	
	let b_on_week = $("#b_on_week").prop("checked") ;
	let on_week=0;
	for(let tmpv of get_chk_vals("on_week"))
	{
		let iv = parseInt(tmpv) ;
		on_week += (1<<iv) ;
	}
	return {b_delay:b_delay,delay_ms:delay_ms,repeat_tp:repeat_tp,interval_ms:interval_ms,
		between_s:between_s,between_e:between_e,b_on_week:b_on_week,on_week:on_week} ;
}

function set_pm_jo(jo)
{
	$("#b_delay").prop("checked",jo.b_delay||false) ;
	$("#delay_ms").val(jo.delay_ms||1000) ;
	$("#repeat_tp").val(jo.repeat_tp||0) ;
	$("#interval_ms").val(jo.interval_ms||10000) ;
	$("#between_s").val(jo.between_s||"") ;
	$("#between_e").val(jo.between_e||"") ;
	$("#b_on_week").prop("checked",jo.b_on_week||false) ;
	let iv = jo.on_week ;
	if(iv>0)
	{
		for(let w=1;w<=7;w++)
		{
			if((iv & (1<<w))>0)
			{
				$("#on_week_"+w).prop("checked",true) ;
			}
		}
	}
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>