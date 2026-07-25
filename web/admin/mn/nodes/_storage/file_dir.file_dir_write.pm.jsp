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
	org.iottree.core.msgnet.util.*,
	org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.modules.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<style>
.rule
{
	position: relative;
	width:98%;
	left:1%;
	border:0px solid;
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
	position:absolute;left:30px;top:10px;
	width:140px;height:36px;
	border:0px solid #dddddd;
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
	left:207px;top:20px;
}
.row .tar_pktp
{
	position:absolute;
	left:150px;top:10px;
	width:100px
}

.row .nor_sel
{
	position:absolute;
	left:150px;top:10px;
	width:200px
}

.row .tar_subn
{
	position:absolute;
	left:230px;top:10px;
	width:260px;
}

.row .tar_pktp .layui-edge
{
	right:80px;
}
.row .tar_pktp .layui-input
{
	padding-left: 20px;
	padding-right: 20px;
	text-align: right;
	border-right: 0px;
}
</style>

  <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Write Type</span></div>
  <div class="nor_sel" style="width:300px"> 
   <select id="write_tp"  class="layui-input" lay-filter="write_tp">
<%
	for(FileDir_Write.WriteTP tp:FileDir_Write.WriteTP.values())
	{
%>
<option value="<%=tp.getVal()%>"><%=tp.getTitle() %></option>
<%
	}
%>
    </select>
  </div>
  </div>
  
  
 <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Append Hex</span></div>
  <div class="nor_sel" style=""> 
   <input type="text" id="append_hex" class="layui-input" />
  </div>
  
  </div>
 <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Payload Encoding</span></div>
  <div class="nor_sel" style=""> 
   <select id="pld_enc"  class="layui-input" lay-filter="pld_enc" style="width:100px;border-right: 0px;">
<%
for(String chartset:java.nio.charset.Charset.availableCharsets().keySet())
{
%><option value="<%=chartset%>"><%=chartset %></option><%
}
%>
    </select>
  </div>
  </div>
<script>


function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let write_tp = $("#write_tp").val();
	let pld_enc = $("#pld_enc").val() ;
	let append_hex = $("#append_hex").val() ;
	return {write_tp:write_tp,pld_enc:pld_enc,append_hex:append_hex} ;
}

function set_pm_jo(jo)
{
	$("#write_tp").val(jo.write_tp||0) ;
	$("#pld_enc").val(jo.pld_enc||"UTF-8") ;
	$("#append_hex").val(jo.append_hex||"") ;
}

function get_pm_size()
{
	return {w:600,h:350} ;
}

//on_init_pm_ok() ;
</script>