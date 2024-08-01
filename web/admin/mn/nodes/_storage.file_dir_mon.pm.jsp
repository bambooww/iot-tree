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
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Dir Path</span></div>
  <div class="tar_pktp" style=""> 
   <select id="path_valsty"  class="layui-input" lay-filter="path_valsty" style="width:100px;border-right: 0px;">
<%
	for(MNCxtValSty pktp:MNCxtValSty.FOR_STR_LIST)
	{
%>
<option value="<%=pktp.name()%>"><%=pktp.getTitle() %><%=pktp.isCxtPk()?".":"" %></option>
<%
	}
%>
    </select>
  </div>
  <div class="tar_subn">
    <input type="text" id="path_subv" class="layui-input" style="border-left: 0px;left:2px;"/>
  </div>
  </div>
  <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Filename Prefix</span></div>
  <div class="nor_sel" style=""> 
   <input type="text" id="fn_prefix" class="layui-input" style="border-left: 0px;left0:2px;"/>
  </div>
  
  </div>
  <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Filename Suffix</span></div>
  <div class="nor_sel" style=""> 
   <input type="text" id=fn_suffix class="layui-input" style="border-left: 0px;left0:2px;"/>
  </div>
  </div>
  <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Output</span></div>
  <div class="nor_sel" style=""> 
   <select id="out_tp"  class="layui-input" lay-filter="out_tp" style="width:100px;border-right: 0px;">
<%
	for(NM_FileDirMon.OutTP pktp:NM_FileDirMon.OutTP.values())
	{
%>
<option value="<%=pktp.getInt()%>"><%=pktp.getTitle() %></option>
<%
	}
%>
    </select>
  </div>

  
  </div>
    <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;After Use</span></div>
  <div class="nor_sel" style=""> 
   <select id="after_use"  class="layui-input" lay-filter="after_use" style="width:100px;border-right: 0px;">
<%
	for(NM_FileDirMon.AfterUse pktp:NM_FileDirMon.AfterUse.values())
	{
%>
<option value="<%=pktp.getInt()%>"><%=pktp.getTitle() %></option>
<%
	}
%>
    </select>
  </div>

  
  </div>
 
  <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Move To Dir</span></div>
  <div class="tar_pktp" style=""> 
   <select id="mvtodir_valsty"  class="layui-input" lay-filter="mvtodir_valsty" style="width:100px;border-right: 0px;">
<%
	for(MNCxtValSty pktp:MNCxtValSty.FOR_STR_LIST)
	{
%>
<option value="<%=pktp.name()%>"><%=pktp.getTitle() %><%=pktp.isCxtPk()?".":"" %></option>
<%
	}
%>
    </select>
  </div>
  <div class="tar_subn">
    <input type="text" id="mvtodir_subv" class="layui-input" style="border-left: 0px;left:2px;"/>
  </div>
  </div>
<script>


function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let path_valsty = $("#path_valsty").val();
	let path_subv =  $("#path_subv").val();
	let fn_prefix = $("#fn_prefix").val() ;
	let fn_suffix = $("#fn_suffix").val() ;
	let out_tp = get_input_val("out_tp",true,0) ;
	let after_use = get_input_val("after_use",true,0) ;
	let mvtodir_valsty = $("#mvtodir_valsty").val();
	let mvtodir_subv =  $("#mvtodir_subv").val();
	
	let skip_first_lns = get_input_val("skip_first_lns",true,0) ;
	return {path_valsty:path_valsty,path_subv:path_subv,fn_prefix:fn_prefix,fn_suffix:fn_suffix,
		out_tp:out_tp,after_use:after_use,mvtodir_valsty:mvtodir_valsty,mvtodir_subv:mvtodir_subv} ;
}

function set_pm_jo(jo)
{
	if(jo.path_valsty)
		$("#path_valsty").val(jo.path_valsty) ;
	$("#path_subv").val(jo.path_subv||"") ;
	
	$("#fn_prefix").val(jo.fn_prefix||"") ;
	$("#fn_suffix").val(jo.fn_suffix||"") ;
	$("#out_tp").val(jo.out_tp||0) ;
	$("#after_use").val(jo.after_use||0) ;
	if(jo.mvtodir_valsty)
		$("#mvtodir_valsty").val(jo.mvtodir_valsty) ;
	$("#mvtodir_subv").val(jo.mvtodir_subv||"") ;
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>