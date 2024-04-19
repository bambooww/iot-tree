<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.router.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%

%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
    <style>
.list_ln
{
	display: flex;
 flex-wrap: wrap;
 float:left;
 border:0px solid;
}


.li_item
{
	width:80px;
	height:80px;
	margin: 5px;
	margin-left:5px;
	align-content: center;
	text-align: center;
	border:2px solid #bbbbbb;
}

.sel_item
{
 cursor:pointer;
 border:2px solid green;
}

.sel_item:hover
{
	border:2px solid blue;
}

.li_item img
{
	max-width:60px;
	max-height:50px;
}

.li_item .tt
{
}
    </style>
    <script type="text/javascript">
    dlg.resize_to(650,550);
    </script>
</head>
<body>
<table>

<%
for(RouterOuterAdpCat cat :RouterOuterAdpCat.listCatsAll())
{
	String catn = cat.getName() ;
	String catt = cat.getTitle() ;
	String tmpt = catt ;
	if(tmpt.length()>8)
		tmpt = tmpt.substring(0,8)+".." ;
%>
  <tr>
    <td style="width:90px;">
<div class="li_item"  style="border-color:#00000000;" title="<%=catt %>">
	<img src="./roa/img/cat_<%=catn%>.png"/><br>
	<span class="tt"><%=tmpt %></span>
</div>
	</td>
	<td style="width:1px;border:1px solid;border-color:green;">
	</td>
	<td class="list_ln" >
<%
	for(RouterOuterAdpCat.AdpDef ad:cat.getAdpDefs())
	{
		String tp = ad.getAdpTP() ;
		String tpt = ad.getAdpTPTitle() ;
		boolean bok = ad.isOk() ;
		String bc = bok?"sel_item":"";
%><span class="li_item <%=bc %>" onclick="go_to('<%=tp %>','<%=tpt%>',<%=bok%>)">
		<img src="./roa/img/<%=tp%>.png"/><br>
	<span class="tt"><%=tpt %></span>
	</span>
<%
	}
%>
	</td>
	</tr>
<%
}
%>
</table>
<script>
function go_to(tp,tt,bok)
{
	if(!bok)
	{
		dlg.msg("<wbt:g>will_impl_later</wbt:g>");
		return ;
	}
	dlg.close({tp:tp,tt:tt}) ;
}
</script>
</body>
</html>
