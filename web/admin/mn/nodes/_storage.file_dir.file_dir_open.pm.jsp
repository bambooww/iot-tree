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
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Open Type</span></div>
  <div class="nor_sel" style="width:300px"> 
   <select id="open_tp"  class="layui-input" lay-filter="open_tp">
<%
	for(FileDir_Open.TP tp:FileDir_Open.TP.values())
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
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;File Ext</span></div>
  <div class="nor_sel" style=""> 
   <input type="text" id="ext" class="layui-input" />
  </div>
  
  </div>
<script>


function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let ext = $("#ext").val() ;
	let open_tp = get_input_val("open_tp",true,0) ;
	return {open_tp:open_tp,ext:ext} ;
}

function set_pm_jo(jo)
{
	$("#ext").val(jo.ext||"") ;
	$("#open_tp").val(jo.open_tp||0) ;
}

function get_pm_size()
{
	return {w:600,h:350} ;
}

//on_init_pm_ok() ;
</script>