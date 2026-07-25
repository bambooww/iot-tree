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
	org.iottree.core.msgnet.nodes.*
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
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Input</span></div>
  <div class="nor_sel" style=""> 
   <input type="checkbox" id="parse_num" class="layui-input" style="border-left: 0px;left0:2px;"/>
   parse numerical values
  </div>
  
  </div>
  <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Output</span></div>
  <div class="nor_sel" style="width:250px;"> 
   <select id="out_tp"  class="layui-input" lay-filter="out_tp" style="border-right: 0px;">
<%
	for(NM_Csv.OutTP pktp:NM_Csv.OutTP.values())
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
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Skip first Lines</span></div>
  <div class="nor_sel" style=""> 
   <input type="number" id="skip_first_lns" class="layui-input" style="border-left: 0px;left0:2px;"/>
  </div>
  
  </div>
  
   <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Columns</span></div>
  <div class="nor_sel" style=""> 
   <input type="text" id="col_names" class="layui-input" style="width:380px;border-left: 0px;left0:2px;" title="like col1,col2,xx.yy,col4"/>
  </div>
  
  </div>
  
<script>


function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let parse_num = $("#parse_num").prop("checked") ;
	let out_tp = $("#out_tp").val() ;
	let skip_first_lns = get_input_val("skip_first_lns",true,0) ;
	let col_names = $("#col_names").val() ;
	return {parse_num:parse_num,out_tp:out_tp,skip_first_lns:skip_first_lns,col_names:col_names} ;
}

function set_pm_jo(jo)
{
	$("#out_tp").val(jo.out_tp||"txt") ;
	$("#parse_num").prop("checked",jo.parse_num||false) ;
	$("#skip_first_lns").val(jo.skip_first_lns||'0') ;
	$("#col_names").val(jo.col_names||"") ;
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>