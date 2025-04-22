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
	height:55px;border:0px solid;
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

.row .tar_right
{
	position:absolute;
	right:30px;top:10px;
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
    <input type="text" id="dir_path" class="layui-input" style="border-left: 0px;left:2px;"/>
  </div>
  <div class="tar_right" >
  	<button onclick="on_dir_sel()" class="layui-btn layui-btn-primary" >...</button>
  </div>
  </div>
<script>

function on_dir_sel()
{
	dlg.open("/admin/util/dir_selector.jsp",{title:"Select Dir",w:'500px',h:'400px'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					let seldir = dlgw.get_sel_dir();
					if(!seldir)
					{
						dlg.msg("no dir selected");return;
					}
					$("#dir_path").val(seldir) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let path_valsty = $("#path_valsty").val();
	let dir_path =  $("#dir_path").val();
	
	return {path_valsty:path_valsty,dir_path:dir_path} ;
}

function set_pm_jo(jo)
{
	if(jo.path_valsty)
		$("#path_valsty").val(jo.path_valsty) ;
	$("#dir_path").val(jo.dir_path||"") ;
}

function get_pm_size()
{
	return {w:600,h:350} ;
}

//on_init_pm_ok() ;
</script>