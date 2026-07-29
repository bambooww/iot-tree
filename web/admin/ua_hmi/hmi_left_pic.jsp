<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.util.gr.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%!

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
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="oc"/>
</jsp:include>
</head>
<script type="text/javascript">
function drag(ev)
{
	var tar = ev.target;
	
	var cn = tar.getAttribute("pic_path");
	
	console.log("ss="+cn);
	oc.util.setDragEventData(ev,{_val:cn,_tp:"pic"})
}
</script>
<body marginwidth="0" marginheight="0">
<table width='100%' height='100%'>
 <tr width="20">
 	<td colspan='2'></td>
 </tr>
 <tr>
 <td valign="top" width="25%"><w:g>cat</w:g>
   <select id='var_cat' multiple="multiple" style="width: 100%;height: 100%" onchange="single_sel_chg_cat('var_cat')">
<%
	for(GRCat grc:GRManager.getInstance().getGRCatAll())
	{
		if(grc.getName().equals(cat))
		{
%><option value="<%=grc.getName() %>" selected="selected"><%=grc.getTitle() %></option><%
		}
		else
		{
%><option value="<%=grc.getName() %>"><%=grc.getTitle() %></option><%
		}
	}
%>
   </select>
 </td>
 <td valign="top" width="25%"><w:g>pics</w:g>
 	<select id='var_item' multiple="multiple" style="width: 100%;height: 100%" onchange="single_sel_chg('var_item')">
<%
	if(curCat!=null)
	{
		for(GRItem gri:curCat.getGRItems())
		{
%><option value="<%=gri.getRefPath() %>"><%=gri.getTitle() %></option><%
		}
	}
%>
 	</select>
 </td>
 <td width="50%" height='90%' valign="top" >
 	<img id='pic_demo' src="" width='90%' height='90%' draggable="true" ondragstart="drag(event)"/><br/>
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
	console.log(v) ;
	var pd = document.getElementById('pic_demo') ;
	pd.src=v ;
	pd.setAttribute("pic_path",v) ;
}

var indlg = false;//
if(indlg)
	dlg.resize_to(500,400);

function sel()
{
	var o = document.getElementById('var_cat');
	if(o.value==null||o.value=='')
	{
		dlg.msg('<w:g>pls,select,cat</w:g>') ;
		return ;
	}
	var tmps = o.value +'.';
	o = document.getElementById('var_item');
	if(o.value==null||o.value=='')
	{
		dlg.msg('<w:g>pls,select,pic</w:g>') ;
		return ;
	}
	
	//alert(o.value) ;
	dlg.close(o.value) ;
}

</script>

</body>
</html>