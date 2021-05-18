<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.gr.*,
	org.iottree.core.comp.*
	"%><%!

%><%
boolean bdlg = !"false".equalsIgnoreCase(request.getParameter("dlg")) ;
String cat = request.getParameter("cat") ;

GRCat curCat = null ;
if(Convert.isNotNullEmpty(cat))
{
	curCat = GRManager.getInstance().getGRCatByName(cat) ;
}
%><html>
<head>
<title></title>
<script src="/_js/ajax.js" ></script>
<script src="/_js/dlg.js" ></script>
<script src="../scada_common.js" charset="utf-8" ></script>
<link type="text/css" href="/_js/jquery_ui/css/ui-lightness/jquery-ui-1.8.10.custom.css" rel="stylesheet" />	
<script type="text/javascript" src="/_js/jquery_ui/js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="/_js/jquery_ui/js/jquery-ui-1.8.10.custom.min.js"></script>

</head>
<body marginwidth="0" marginheight="0">
<table width='100%' height='100%'>
 <tr width="20">
 	<td colspan='2'></td>
 </tr>
 <tr>
 <td valign="top" width="25%">分类
   <select id='var_cat' multiple="multiple" style="width: 100%;height: 100%" onchange="single_sel_chg_cat('var_cat')">
<%
	for(GRCat grc:GRManager.getInstance().getGRCatAll())
	{
		if(grc.getName().equals(cat))
		{
%><option value="<%=grc.getName() %>" selected="selected"><%=grc.getTitleCN() %></option><%
		}
		else
		{
%><option value="<%=grc.getName() %>"><%=grc.getTitleCN() %></option><%
		}
	}
%>
   </select>
 </td>
 <td valign="top" width="25%">图元
 	<select id='var_item' multiple="multiple" style="width: 100%;height: 100%" onchange="single_sel_chg('var_item')">
<%
	if(curCat!=null)
	{
		for(GRItem gri:curCat.getGRItems())
		{
%><option value="<%=gri.getRefPath() %>"><%=gri.getTitleCN() %></option><%
		}
	}
%>
 	</select>
 </td>
 <td width="50%" height='90%' valign="top" >示意
 	<img id='pic_demo' src="" width='90%' height='90%'/><br/>
 	<input type='button' value="OK" onclick="sel()"/> &nbsp; 
  <input type='button' value="Cancel" onclick="dlg.close()"/>
 </td>
 </tr>
 <tr height="30">
  <td colspan='2'></td>
 </tr>
</table>
<script>
function single_sel_chg_cat(id)
{
	var ss = document.getElementById(id);
	var v = ss.value ;
	if(v!=null)
		ss.value = v ;
	
	document.location.href="hmi_left_pic.jsp?cat="+v ;
}

function single_sel_chg(id)
{
	var ss = document.getElementById(id);
	var v = ss.value ;
	if(v!=null)
		ss.value = v ;
	
	document.getElementById('pic_demo').src=v ;
}

var indlg = false;//
if(indlg)
	dlg.resize_to(500,400);

function sel()
{
	var o = document.getElementById('var_cat');
	if(o.value==null||o.value=='')
	{
		alert('请选择分类') ;
		return ;
	}
	var tmps = o.value +'.';
	o = document.getElementById('var_item');
	if(o.value==null||o.value=='')
	{
		alert('请选择图元') ;
		return ;
	}
	
	//alert(o.value) ;
	dlg.close(o.value) ;
}

</script>

</body>
</html>