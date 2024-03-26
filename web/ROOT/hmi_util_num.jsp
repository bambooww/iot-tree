<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="lan"%><%
	String user = request.getParameter("user") ;
	if(user==null)
		user = "" ;
	%>
<html>
<head>
<title>auth</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<style>
#kp_vals input
{
width:100%;height:100%;
font-size:5em;
}

#input_psw
{
width:100%;height:100%;
font-size:5em;
}
</style>
<script>
dlg.resize_to(720,790);
</script>
</head>
<body>
<DIV align=center id="softkeyboard" style="width:670px;height:680px">
	  <table  style="width:100%;height:100%" border="0" align="center" cellpadding="0" cellspacing="0" >
	      <tr>
	        <td align="left" style="height:80px;">
	        <INPUT type=password readonly=readonly id="input_psw">
	      </tr>
	      <tr>
	        <td align="center" bgcolor="#FFFFFF" align="center">
	           <table id="kp_vals" align="center" style="width:100%;height:100%" border="0" cellspacing="0" cellpadding="0">
	            <tr align="left" valign="middle">
	              <td><input type=button onclick="addValue('1')" value=" 1 "></td>
	              <td><input type=button onclick="addValue('2')" value=" 2 "></td>
	              <td><input type=button onclick="addValue('3')" value=" 3 "></td>
	              <td><input name="button10" type=button value="<lan:g>back</lan:g>" onclick="bkValue()"></td>
	            </tr>
	            <tr align="left" valign="middle">
	              <td><input type=button onclick="addValue('4')" value=" 4 "></td>
	              <td><input type=button onclick="addValue('5')" value=" 5 "></td>
	              <td><input type=button onclick="addValue('6')" value=" 6 "></td>
	              <td><INPUT type=button value="<lan:g>reset</lan:g>"  onclick="reset_psw()"></td>
	            </tr>
	            <tr align="left" valign="middle">
	              <td><input type=button onclick="addValue('7')" value=" 7 "></td>
	              <td><input type=button onclick="addValue('8')" value=" 8 "></td>
	              <td><input type=button onclick="addValue('9')" value=" 9 "></td>
	              <td><input class=button type=button value="<lan:g>close</lan:g>" name="Submit222" onclick="win_close()"></td>
	            </tr>
	            <tr align="left" valign="middle">
	              <td><input type=button onclick="addValue('7')" value="&lt;"></td>
	              <td><input type=button onclick="addValue('8')" value=" 0 "></td>
	              <td><input type=button onclick="addValue('9')" value="&gt;"></td>
				  <td> <input type=button onclick="do_ok()" value="<lan:g>ok</lan:g>" style="background-color:blue;color:white "></td>
	            </tr>
	          </table>
	          </td>
	      </tr>
	  </table>
	</DIV>
</body>
<script type="text/javascript">
var user="<%=user%>";
function win_close()
{
	dlg.close(0);
}

function addValue(c)
{
	var inp = $("#input_psw")
	inp.val(inp.val()+c) ;
}

function bkValue()
{
	var inp = $("#input_psw");
	var vv = inp.val() ;
	if(vv==null||vv=="")
		return ;
	inp.val(vv.substr(0,vv.length-1)) ;
}

function reset_psw()
{
	$("#input_psw").val("");
}

function do_ok()
{
	var psw = document.getElementById('input_psw').value;
	if(psw==null||psw=='')
	{
		dlg.msg("<lan:g>pls,input,psw</lan:g>");
		return ;
	}
	
	dlg.close({user:user,psw:psw});
}

function do_submit(cb)
{
	var psw = document.getElementById('psw').value;
	if(psw==null||psw=='')
	{
		cb(false,'<wbt:lang>pls_input</wbt:lang><wbt:lang>psw</wbt:lang>') ;
		return ;
	}
	
	cb(true,{user:user,psw:psw});
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>