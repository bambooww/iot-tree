<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.conn.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	UAPrj prj = UAManager.getInstance().getPrjById(container_id) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	
	
%>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:10px;">
		<input type="checkbox" id="trans_str"  class="layui-input " lay-skin="primary"/>
		
    </div>
	<label class="layui-form-mid">Transfer To Str &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Encoding:</label>
    <div class="layui-input-inline" style="width:150px;">
		<input type="text" id="trans_str_enc"  value="UTF-8" class="layui-input" lay-skin="primary"/>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:10px;">
		<input type="checkbox" id="b_json" class="layui-input" lay-skin="primary"/>
    </div>
	<label class="layui-form-mid">JSON Format</label>
  </div>
<script>

function on_after_pm_show(form)
{

	  form.on('select(conn_pt_id)', function (data) {
		  //update_bt();
		});
	 // update_bt();
}

function update_bt()
{
	
}

function get_pm_jo()
{
	let trans_str = $("#trans_str").prop("checked") ;
	let trans_str_enc = $("#trans_str_enc").val() ;
	let b_json = $("#b_json").prop("checked") ;
	return {trans_str:trans_str,trans_str_enc:trans_str_enc,b_json:b_json} ;
}

function set_pm_jo(jo)
{
	$("#trans_str").prop("checked",jo.trans_str||false) ;
	$("#trans_str_enc").val(jo.trans_str_enc||"UTF-8") ;
	$("#b_json").prop("checked",jo.b_json||false) ;
	
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>