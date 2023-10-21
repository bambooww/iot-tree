<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,org.iottree.core.*,org.iottree.core.util.*,
	java.io.*,
	org.iottree.core.res.*,
	org.iottree.core.comp.*,
	org.iottree.core.gr.*
	"%><%!
static HashMap<String,String> NAME2Path = new HashMap<>() ;
	
static
{
	NAME2Path.put("event_bind","");
}
%><%
if(!Convert.checkReqEmpty(request, out, "name"))
	return ;
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Doc Helper</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}

.prop_table
{
width:99%;
border: 0px;
margin: 0 auto;
}

.prop_table tr>td
{
	border: 0;
	height:100%
}

.prop_table tr>div
{
	border: 0;

}

.prop_edit_cat
{
border: 1px solid #cccccc;
height:400px;
padding: 3px;
margin: 2px;
overflow: auto;
}

.prop_edit_panel
{
border: 1px solid #cccccc;
height:350px;
padding: 0px;
margin: 2px;
overflow: auto;
}

.prop_edit_path
{
font-weight:bold;
border: 1px solid #cccccc;
background-color:#f0f0f0;
padding: 3px;
margin: 2px;
overflow: hidden;
}

.prop_edit_desc
{
border: 1px solid #cccccc;
background-color:#f0f0f0;
height:48px;
padding-left:3px;
padding-right:3px;
padding-bottom: 0px;
padding-top: 0px;
margin-left: 2px;
margin-right: 2px;
margin-top: 0px;
margin-bottom: 0px;
overflow: hidden;
}

.site-dir li {
    line-height: 26px;
    margin-left: 20px;
    overflow: visible;
    list-style-type: square;
}
li {
    list-style: none;
}

.site-dir li a {
    display: block;
    color: #333;
    cursor:pointer;
    text-decoration: none;
}


.site-dir li a.layui-this {
    color: #01AAED;
}

.pi_edit_table
{
width:100%;
border: 0px solid #b4b4b4;
margin: 0 auto;
}


.pi_edit_table tr>td
{
	border: 1px solid #b4b4b4;
	height:100%;
	
	
}

.pi_edit_table .td_left
{
	padding-left: 20px;
}

.pi_edit_table tr>div
{
	border: 0;

}

.pi_sel
{
background-color: #0078d7;
}

.pi_edit_unit
{
border: 0px;
width:100%;
}


.oc-toolbar .toolbarbtn
{
width:80px;height:80px;margin: 10px;
}

.resitem
{
border-width:2px; border-style:solid; background-color0: #515658;
}

.res_sel
{
	background-color: #1173ec;
	color: #eeeeee;
	margin:2px;
	border-color: red;
	border: 3px solid;
}

.res_node
{

}

.res_node:hover {
	background-color: #1173ec;
	color: #eeeeee;
}
</style>
</head>
<script type="text/javascript">
</script>
<body>
<table class="prop_table">
  <tr>
    <td colspan="2"><div id="prop_edit_path" class="prop_edit_path">Helper : <%="" %></div></td>
  </tr>
  <tr>
    <td style="width:20%;height:90%" valign="top" >
     <div id="rescxt_list" class="prop_edit_cat" style="overflow: hidden;">

    	</div>
    </td>
    <td style="width:80%;vertical-align: top;"  >
    <div id="editpanel"  class="prop_edit_panel oc-toolbar" >
       <div id="res_list" class="btns" style="width:100%;height:100%;overflow: auto;">
			  
    </div>
	 </div>
	  <div id="editop"  class="prop_edit_desc">
	  	<input type="text" id="add_name" placeholder="input name" onkeyup="on_name_chged()" />
	  	<input type="file" id='add_file' onchange="add_file_onchg()" name="file" style="left:-9999px;position:absolute;"/>
			<button id="btn_add_file" onclick="add_res()">Add</button>
			<button id="btn_del_file" onclick="del_res()" style="display:none">Delete</button>
	  </div>
    </td>
  </tr>
</table>
</body>
<script type="text/javascript">
dlg.resize_to(800,550);

var cur_cxtid=null ;
var cur_resitems = null;
var cur_resitem = null ;

function win_close()
{
	dlg.close(0);
}

</script>
</html>