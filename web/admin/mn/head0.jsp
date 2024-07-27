<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page
	import="java.util.*,
				java.io.*,
				java.net.*
				"%><%
				String ver="1.0";
				
				boolean selectmenu= "true".equals(request.getParameter("selectmenu"));
				boolean tree  ="true".equals(request.getParameter("tree"));
				boolean reco = "true".equals(request.getParameter("reco"));
				boolean tab = "true".equals(request.getParameter("tab"));
				boolean rnn = "true".equals(request.getParameter("rnn"));

%> <script src="/_jquery/jquery.min.js"></script>
    <link href="/_jquery/font6/css/all.css" rel="stylesheet">
      
      <link rel="stylesheet" type="text/css" href="/_jquery/layui/css/layui.css" />
<script src="/_jquery/layui/layui.all.js"></script>
<script src="/system/ui/dlg_layer.js"></script>
<link rel="stylesheet" href="/_jquery/api_doc/global.css" media="all">
    <script src="/system/ui/ajax.js"></script>
        <script src="/system/jt/jt_util.js"></script>
        <link rel="stylesheet" href="./px.css" />
<%
if(selectmenu)
{
%>
        <link rel="stylesheet" href="/_jquery/selectmenu/selectmenu.css" />
<script src="/_jquery/selectmenu/selectmenu.min.js"></script>
<%
}
if(tree)
{
%>
<script src="/_jquery/jstree/jstree.min.js"></script>
<link rel="stylesheet" href="/_jquery/jstree/themes/default/style.min.css" />
<%
}
if(rnn)
{
%>
<script src="/_jquery/reco_net/rn.js"></script>
<%
}
if(reco)
{
%>
<script src="/system/prox/reco/reco.js"></script>
<link rel="stylesheet" href="/system/prox/reco/reco.css" />
<%
}
if(tab)
{
%>
	<script type="text/javascript" src="/system/ui/tab/tab.js" ></script>
    <link rel="stylesheet" href="/system/ui/tab/tab.css" />
<%
}
%>