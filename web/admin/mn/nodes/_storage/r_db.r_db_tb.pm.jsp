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
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Table Name</span></div>
  <div class="nor_sel" style=""> 
   <input type="text" id="table" class="layui-input" />
  </div>
  </div>
   <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Create Table SQL</span></div>
  <div class="nor_sel" style=""> 
   <textarea id="create_sql" class="layui-input" style="width:520px;height:200px;" >
   </textarea>
  </div>
  </div>
<script>


function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let table = $("#table").val();
	let csql = $("#create_sql").val() ;
	return {table:table,create_sql:csql} ;
}

function set_pm_jo(jo)
{
	$("#table").val(jo.table||"") ;
	$("#create_sql").val(jo.create_sql||"") ;
}

function get_pm_size()
{
	return {w:700,h:350} ;
}

//on_init_pm_ok() ;
</script>