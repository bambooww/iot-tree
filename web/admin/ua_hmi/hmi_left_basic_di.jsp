<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				org.json.*,
				org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%><%!
	static class BtnItem
	{
			public String icon ;
			public String title ;
			public String op ;
			public BtnItem(String icon,String title,String op)
			{
				this.icon = icon ;
				this.title = title ;
				this.op = op ;
			}
	}
	static BtnItem[] BTNS=new BtnItem[]{
			new BtnItem("pic/line1.png","Line","parent.do_add_di('oc.di.DILine')"),
			//new BtnItem("pic/square.png","Add Rect","parent.do_add_di('oc.di.DIRect')"),
			new BtnItem("pic/text.png","Txt","parent.do_add_di('oc.di.DITxt')"),
			new BtnItem("pic/img.png","Add Img","parent.do_add_di('oc.di.DIImg')"),
			//new BtnItem("","Add Icon","parent.do_add_di('oc.di.DIIcon')"),
			new BtnItem("pic/arc1.png","Arc","parent.do_add_di('oc.di.DIArc')"),
			new BtnItem("pic/square.png","Square","parent.do_add_di('oc.di.DIBasic',{tp:'rect'})"),
			new BtnItem("pic/diamond.png","Diamond","parent.do_add_di('oc.di.DIBasic',{tp:'diamond'})"),
			new BtnItem("pic/parallelogram.png","Parallelogram","parent.do_add_di('oc.di.DIBasic',{tp:'parallelogram'})"),
			new BtnItem("pic/pentagon.png","Pentagon","parent.do_add_di('oc.di.DIBasic',{tp:'pentagon'})"),
			new BtnItem("pic/circle.png","Circle","parent.do_add_di('oc.di.DIBasic',{tp:'circle'})"),
			new BtnItem("pic/iso_triangle.png","Triangle","parent.do_add_di('oc.di.DIBasic',{tp:'iso_triangle'})"),
			new BtnItem("pic/eclipse.png","Triangle","parent.do_add_di('oc.di.DIBasic',{tp:'eclipse'})"),
			new BtnItem("pic/py.png","Polygon","parent.do_add_pts('py',{})"),
			new BtnItem("pic/ln.png","Line","parent.do_add_pts('ln',{})"),
			new BtnItem("pic/pipe.png","Pipe","parent.do_add_pts('pipe',{})"),
			new BtnItem("pic/dial.png","Dial","parent.do_add_di('oc.di.DIDial')"),
			//new BtnItem("","Add Img","parent.do_add_di('oc.di.DIImg')"),
			//new BtnItem("","Add Img","parent.do_add_di('oc.di.DIImg')"),
	} ;
%><%
%><!DOCTYPE html>
<html>
<head>
<title>IOT Tree</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
</head>
<style>
.oc-toolbar .toolbarbtn
{
width:40px;height:40px;margin: 5px;
font-size: 13px;
background-color: #eeeeee
}

</style>
<body>

  <div id="win_act1"  class="oc-toolbar" style="width:100%;top:0px;bottom: 50px;overflow:auto" >
  <%--
  <div class="titlebar" >
	<span class="i18n">Basic Shape</span><div class="collapse icon-eda-fold"></div>
</div>
 --%>
  <div id="main" class="btns" style="top:70px;width:100%">
<%
for(BtnItem bi:BTNS)
{
%>
    <div class="toolbarbtn" onclick="<%=bi.op %>" title="<%=bi.title %>">
     <img src="<%=bi.icon %>" />
    </div>
<%
}
%>
  </div>
  </div>
</body>