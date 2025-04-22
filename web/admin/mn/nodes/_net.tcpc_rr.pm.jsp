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
	org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>

<div class="layui-form-item">
    <label class="layui-form-label">Server</label>
    <div class="layui-input-inline" style="width:260px;">
		<input type="text" id="server" class="layui-input" />
    </div>
    <div class="layui-form-mid" style="width:30px;">Port</div>
    <div class="layui-input-inline" style="width:80px;">
    	<input type="number" id="port" class="layui-input"/>
    </div>
</div>
 <div class="layui-form-item">
    <label class="layui-form-label">Request:</label>
    <div class="layui-input-inline" style="width:150px;">
    	<select id="req_tp" class="layui-input" lay-filter="req_tp" >
<%
	for(NM_TcpClientRR.ReqTP tp:NM_TcpClientRR.ReqTP.values())
	{
%>
        <option value="<%=tp.getVal()%>"><%=tp.getTitle()%></option>
<%
	}
%>
    </select>
    </div>
    <div class="layui-form-mid" style="width:30px;">Hex</div>
    <div class="layui-input-inline" style="width:280px;">
    	<input type="text" id="req_hex" class="layui-input"/>
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label">Response</label>
    <div class="layui-form-mid" style="width:70px;">Pack Type:</div>
    <div class="layui-input-inline" style="width:150px;">
    	<select id="resp_pk_tp"  class="layui-input" lay-filter="resp_pk_tp" >
<%
	for(NM_TcpClientRR.RespPkTP tp:NM_TcpClientRR.RespPkTP.values())
	{
%>
        <option value="<%=tp.getVal()%>"><%=tp.getTitle()%></option>
<%
	}
%>
    </select>
    </div>
    <div class="layui-form-mid" style="width:100px;">Fixed Length</div>
    <div class="layui-input-inline" style="width:80px;">
    	<input type="number" id="resp_pk_fixedlen" class="layui-input" lay-filter="resp_pk_fixedlen" />
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid" style="width:70px;">Start Hex:</div>
    <div class="layui-input-inline" style="width:180px;">
    	<input type="text" id="resp_pk_start_hex" class="layui-input" lay-filter="resp_pk_start_hex" />
    </div>
    <div class="layui-form-mid" style="width:70px;">End Hex</div>
    <div class="layui-input-inline" style="width:180px;">
    	<input type="text" id="resp_pk_end_hex" class="layui-input" lay-filter="resp_pk_end_hex"/>
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid" style="width:80px;">When Error:</div>
    <div class="layui-input-inline" style="width:280px;">
    	<select id="resp_err_tp"  class="layui-input" lay-filter="resp_err_tp" >
<%
	for(NM_TcpClientRR.RespErrTP tp:NM_TcpClientRR.RespErrTP.values())
	{
%>
        <option value="<%=tp.getVal()%>"><%=tp.getTitle()%></option>
<%
	}
%>
    </select>
    </div>
    
</div>

 <div class="layui-form-item">
    <label class="layui-form-label">Link End:</label>
    <div class="layui-input-inline" style="width:250px;">
    	<select id="link_end_tp" class="layui-input" lay-filter="link_end_tp" >
<%
	for(NM_TcpClientRR.LinkEndTP tp:NM_TcpClientRR.LinkEndTP.values())
	{
%>
        <option value="<%=tp.getVal()%>"><%=tp.getTitle()%></option>
<%
	}
%>
    </select>
    </div>
   
</div>
<script>

function on_after_pm_show(form)
{
	
	  update_bt();
}

function update_bt()
{
	
}

function get_pm_jo()
{
	let server = $("#server").val();
	if(!server)
		return "please input server";
	let port = get_input_val("port",-1,true) ;
	if(port<=0)
	{
		return "please input valid port" ;
	}
	
	let req_hex = $("#req_hex").val();
	let req_tp = get_input_val("req_tp",0,true) ;
	if(req_tp!=2 && !req_hex)
		return "please input request hex" ;
	
	let resp_pk_tp = get_input_val("resp_pk_tp",0,true) ;
	let resp_pk_fixedlen = get_input_val("resp_pk_fixedlen",-1,true) ;
	if(resp_pk_tp==0 && resp_pk_fixedlen<=0)
		return "please input response pack fixed length" ;
	
	let resp_err_tp = get_input_val("resp_err_tp",0,true) ;
	let link_end_tp = get_input_val("link_end_tp",0,true) ;
	
	return {server:server,port:port,req_tp:req_tp,req_hex:req_hex,resp_pk_tp:resp_pk_tp,
		resp_pk_fixedlen:resp_pk_fixedlen,
		resp_err_tp:resp_err_tp,link_end_tp:link_end_tp} ;
}

function set_pm_jo(jo)
{
	$("#server").val(jo.server||"") ;
	$("#port").val(jo.port) ;
	$("#req_tp").val(jo.req_tp||0) ;
	$("#req_hex").val(jo.req_hex||"") ;
	
	$("#resp_pk_tp").val(jo.resp_pk_tp||0) ;
	$("#resp_pk_fixedlen").val(jo.resp_pk_fixedlen||"") ;
	
	$("#resp_pk_start_hex").val(jo.resp_pk_start_hex||"") ;
	$("#resp_pk_end_hex").val(jo.resp_pk_end_hex||"") ;
	
	$("#resp_err_tp").val(jo.resp_err_tp||0) ;
	$("#link_end_tp").val(jo.link_end_tp||0) ;
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>