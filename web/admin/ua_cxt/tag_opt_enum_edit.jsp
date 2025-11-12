<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

	 %><%
	String lang = "en" ;
%>
<html>
<head>
<title>Tag Value Option Editor </title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(650,500);
</script>
<style type="text/css">
.layui-form-label
{
	width:120px;
}
.prompt
{
	border:1px solid;
	margin:10px;
}
table {width:100%;}
thead {background-color: #eeeeee;}
th,td {border:1px solid #ccc;font-size:12px;}
</style>
</head>
<body>
<table >
	<thead>
		<tr>
			<td>Value</td>
			<td>Title</td>
			<td><button onclick="add_item()"><i class="fa fa-plus"></i></button></td>
		</tr>
	</thead>
	<tbody id="tbd">
	</tbody>
</table>
</body>
<script type="text/javascript">
var form ;
var ow =dlg.get_opener_w();
var val_opt_dd = dlg.get_opener_opt("dd");

layui.use('form', function(){
	  form = layui.form;
	  
	  form.on('select(tp)', function (data) {
		　//console.log(data);　
		　//var n = data.value;
		
			
		});
	  update_ui();
	  form.render();
});

function add_item()
{
	let tmps = `<tr><td><input type="number" class="v_str"  value=""/></td><td><input class="v_tt"  value=""/></td><td><button onclick="del_item(this)"><i class="fa fa-times"></i></button></td></tr>` ;
	$("#tbd").append(tmps) ;
}

function update_ui()
{
	let tmps ="" ;
	
	if(val_opt_dd && val_opt_dd._tp=='enum')
	{
		for(let item of val_opt_dd.items)
		{
			tmps += `<tr><td><input type="number" class="v_str" value="\${item.v_str}"/></td><td><input class="v_tt"  value="\${item.v_tt}"/></td><td><button onclick="del_item(this)"><i class="fa fa-times"></i></button></td></tr>` ;
		}
	}

	$("#tbd").html(tmps) ;
}

function del_item(btn)
{
	$(btn).parent().parent().remove();
}

function convertHTML(str)
{
	  var characters = [/&/g, /</g, />/g, /\"/g, /\'/g];
	  var entities = ["&amp;", "&lt;", "&gt;", "&quot;", "&apos;"];
	  for(var i = 0; i < characters.length; i++)
	  {
	    str = str.replace(characters[i], entities[i]);
	  }
	  
	  return str;
}

function get_input_val(id,defv,bfloat)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bfloat)
		return parseFloat(n);
	else
		return parseInt(n);
	return n;
}

function do_submit(cb)
{
	let ret={_tp:"enum"};
	ret.items=[];
	$("#tbd").find('tr').each(function(){
		let tr = $(this) ;
		let v_str = tr.find(".v_str").val();
		if(!v_str)
			return ;
		let v_tt = tr.find(".v_tt").val();
		ret.items.push({v_str:v_str,v_tt:v_tt}) ;
	});
	if(ret.items.length<=0)
	{
		cb(false,"no item set") ;
		return ;
	}
	cb(true,ret);
}

</script>
</html>