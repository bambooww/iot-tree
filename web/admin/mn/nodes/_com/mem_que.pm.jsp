<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
  <div class="layui-form-item">
    <label class="layui-form-label">Mode:</label>
    <div class="layui-input-inline" style="width:250px;">
		<select id="mode"  name="mode" class="layui-input" lay-filter="mode">
<%
	for(NM_MemQueue.Mode md:NM_MemQueue.Mode.values())
	{
%>
        <option value="<%=md.getInt()%>"><%=md.getTitle() %></option>
<%
	}
%>
      </select>
    </div>
   </div>
    
    <div class="layui-form-item">
    <label class="layui-form-label">Queue:</label>
    <div class="layui-form-mid">Max Length:</div>
     <div class="layui-input-inline" style="width:100px;">
     	<input type="number" class="layui-input" id="max_len" />
     </div>
     <div class="layui-form-mid">Warn Length</div>
      <div class="layui-input-inline" style="width:100px;">
     	<input type="number" class="layui-input" id="warn_len" />
     </div>
  </div>
  
    <div class="layui-form-item">
    <label class="layui-form-label">Exceed</label>
    <div class="layui-form-mid">Max Length Handle</div>
    <div class="layui-input-inline" style="width:250px;">
		<select id="exd_max_h"  name="exd_max_h" class="layui-input" lay-filter="exd_max_h">
<%
	for(NM_MemQueue.ExceedMaxH md:NM_MemQueue.ExceedMaxH.values())
	{
%>
        <option value="<%=md.getInt()%>"><%=md.getTitle() %></option>
<%
	}
%>
      </select>
    </div>
   </div>
  
  <div id="combined_cont">
  <div class="layui-form-item">
    <label class="layui-form-label">Combined:</label>
    <div class="layui-form-mid">Min Length</div>
    <div class="layui-input-inline" style="width:100px;">
    	<input type="number" class="layui-input" id="combined_min_len" />
    </div>
    <div class="layui-form-mid">Max Length</div>
    <div class="layui-input-inline" style="width:100px;">
    	<input type="number" class="layui-input" id="combined_max_len" />
    </div>
</div>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
     <div class="layui-form-mid">Wait Timeout</div>
    <div class="layui-input-inline" style="width:450px;" title="wait ms when queue length not reach min length">
    	<input type="number" class="layui-input" id="combined_wait_to" />
    </div>
</div>

</div>

<div id="latest_pri_cont">
</div>
<script>

function on_after_pm_show(form)
{
	  form.on('select(mode)', function (data) {
		  update_bt();
		});
	  update_bt();
}

function update_bt()
{
	if($("#mode").val()==1)
		$("#combined_cont").css("display","") ;
	else
		$("#combined_cont").css("display","none") ;
	
	if($("#mode").val()==2)
		$("#latest_pri_cont").css("display","") ;
	else
		$("#latest_pri_cont").css("display","none") ;
}

function get_pm_jo()
{
	let ret={} ;
	ret.mode = get_input_val("mode",0,true) ;
	ret.warn_len =  get_input_val("warn_len",-1,true) ;
	ret.max_len =  get_input_val("max_len",-1,true) ;
	
	if(ret.max_len && ret.max_len<=0)
	{
		return "queue max length must >0" ;
	}
	ret.exd_max_h = get_input_val("exd_max_h",0,true);
	
	ret.combined_min_len = get_input_val("combined_min_len",0,true);
	if(ret.mode==1 && ret.combined_min_len<=0)
	{
		return "combined min length must >0" ;
	}
	ret.combined_max_len = get_input_val("combined_max_len",0,true);
	ret.combined_wait_to = get_input_val("combined_wait_to",0,true);
	return ret ;
}

function set_pm_jo(jo)
{
	$("#mode").val(jo.mode||0) ;
	$("#warn_len").val(jo.warn_len||100) ;
	$("#max_len").val(jo.max_len||150) ;
	$("#exd_max_h").val(jo.exd_max_h||0) ;
	
	$("#combined_min_len").val(jo.combined_min_len||1) ;
	$("#combined_max_len").val(jo.between_e||30) ;
	$("#combined_wait_to").val(jo.combined_wait_to||3000) ;
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>