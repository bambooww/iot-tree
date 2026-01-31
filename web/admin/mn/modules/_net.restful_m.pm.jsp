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
	org.iottree.core.msgnet.modules.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid","itemid"))
		return ;
	
	String container_id = request.getParameter("container_id") ;
UAPrj prj = UAManager.getInstance().getPrjById(container_id) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}
MNManager mnm = MNManager.getInstance(prj) ;
String netid = request.getParameter("netid") ;
MNNet net = mnm.getNetById(netid) ;
if(net==null)
{
	out.print("no net found") ;
	return ;
}
String itemid = request.getParameter("itemid") ;
RESTful_M node = (RESTful_M)net.getModuleById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

String uid = node.CXT_getUID() ;

String url = node.getAccessPath() ;
String url_doc = url+"_doc" ;
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
.url_ppt {color:red;}
</style>

 <div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Module Name</span></div>
  <div class="nor_sel" style=""> 
   <input type="text" id="module_name" class="layui-input" />
  </div>
  </div>
<div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;Access Path</span></div>
  <div class="nor_sel" style=""> 
   <div class="url_ppt"><span class="prefix"></span><%=url %></div>
  </div>
  </div>
<div class="row" >
   <div class="msg"><span style="top:10px;position: absolute;">&nbsp;</span></div>
  <div class="nor_sel" style=""> 
   <div class="url_ppt" onclick="open_doc()" style="cursor:pointer">Api Document</div>
  </div>
  </div>
<script>

var uid="<%=uid%>";

function open_doc()
{
	
	window.open('./util/restful_m_doc.jsp?uid='+uid);
}

function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let module_name = $("#module_name").val();
	
	//let batch_w_buflen = get_input_val('batch_w_buflen',true,10);
	
	return {module_name:module_name} ;
}

function set_pm_jo(jo)
{
	$("#module_name").val(jo.module_name||"") ;
	//$('#batch_w_buflen').val(jo.batch_w_buflen||100);
	let pre = location.protocol+"//"+location.host ;
	
	$(".prefix").html(pre)
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>