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
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Output</span></div>
  <div class="nor_sel" style="width:250px;"> 
   <select id="out_tp"  class="layui-input" lay-filter="out_tp" style="border-right: 0px;">
<%
	for(RelationalDB_SQL_Query.OutTP pktp:RelationalDB_SQL_Query.OutTP.values())
	{
%>
<option value="<%=pktp.name()%>"><%=pktp.getTitle() %></option>
<%
	}
%>
    </select>
  </div>
  </div>

 <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Max Row Num</span></div>
  <div class="nor_sel" style=""> 
   <input type="number" id="max_row" class="layui-input" />
  </div>
 </div>
 
 <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;JO Format</span></div>
  <div class="nor_sel" style="width:400px"> 
   time2ms <input type="checkbox" id="jo_time2ms" lay-ignore/>
   column low case<input type="checkbox" id="jo_col2lowcase"  lay-ignore/>
   ignore null <input type="checkbox" id="jo_ignorenull"  lay-ignore/>
  </div>
 </div>
 
<script>


function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let out_tp = $("#out_tp").val();
	let max_row = get_input_val("max_row",100,true);
	let jo_time2ms = $("#jo_time2ms").prop("checked");
	let jo_col2lowcase = $("#jo_col2lowcase").prop("checked");
	let jo_ignorenull = $("#jo_ignorenull").prop("checked");
	
	return {out_tp:out_tp,max_row:max_row,jo_time2ms:jo_time2ms,jo_col2lowcase:jo_col2lowcase,jo_ignorenull:jo_ignorenull} ;
}

function set_pm_jo(jo)
{
	$("#out_tp").val(jo.out_tp||"msg_per_row_jo") ;
	$("#max_row").val(jo.max_row||100) ;
	$("#jo_time2ms").prop("checked",jo.jo_time2ms||false) ;
	$("#jo_col2lowcase").prop("checked",jo.jo_col2lowcase||false) ;
	$("#jo_ignorenull").prop("checked",jo.jo_ignorenull||true) ;
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>