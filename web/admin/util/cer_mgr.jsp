<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.service.*,
				org.iottree.core.util.web.*,
	java.io.*,org.iottree.core.util.cer.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	String cat_name = request.getParameter("cat") ;
	CerCat cat = null;
	if(Convert.isNotNullEmpty(cat_name))
		cat = CerManager.getInstance().getCat(cat_name) ;
	else
	{
		cat = CerManager.getInstance().getDefaultCerCat() ;
		cat_name = cat.getName() ;
	}
	
	if(cat==null)
	{
		out.print("no cat found") ;
		return ;
	}
	
	List<CerItem> items = cat.getCerItems() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,500);
</script>
<style>
.left {position:absolute;left: 0px;top:0px;width:200px;bottom: 0px;border:1px solid #ccc;}
.right {position:absolute;left:200px;top:0px;right:0px;bottom: 0px;}
.cat {position: relative;border:1px solid #aaa;width:98%;left:1px;margin-top:3px;}
.cat:hover {background-color: #ccc;}
.item {position: relative;float: left;margin:5px;height:30px;line-height:30px;height:50px;min-width:200px;border:1px solid #ccc;padding-left:5px;padding-right:5px;}
.item .t {}
.item .op {position:absolute;right:3px;top:3px;border:1px solid #ccc;width:18px;height:18px;text-align:center;color:red;}
.item .org {position:absolute;left:10px;bottom:2px;}
.sel  {background-color: #ccc;}
</style>
</head>
<body>
<div class="left">
<%
for(CerCat cc:CerManager.getInstance().listCerCat())
{
	String sel = (cat==cc)?"sel":"";
%><div class="cat <%=sel %>" onclick="on_cat('<%=cc.getName()%>')">&nbsp;&nbsp;&nbsp;<%=cc.getTitle() %></div>
<%
}
%>
</div>
<div class="right">
<%
for(CerItem item:items)
{
	String run_c = "grey" ;
	String run_t = "disabled" ;
	String id = item.getAutoId() ;
	String org = item.getOrg() ;
	if(org==null)
		org = "" ;
%><div class="item">
	<span class="t"><%=item.getTitle() %></span>
	<span class="org"><%=org %></span>
	<span class="op" ><span onclick="on_item_del('<%=id%>')"><i class="fa fa-times"></i></span></span>
</div>
<%
}
%>

<div class="item" style="border:0px"><button class="layui-btn layui-btn-sm layui-btn-primary" onclick="on_item_edit()"><i class="fa fa-plus"></i></button></div>
</div>
</body>
<script type="text/javascript">

var cat_name  = "<%=cat_name%>" ;
var table = null ;

var cur_selected = null ;

//var on_devdef_selected = null ;

layui.use('table', function()
{
  table = layui.table;
});

function refresh_ui()
{
	location.reload();
}

function on_cat(name)
{
	location.href="cer_mgr.jsp?cat="+name ;
}

function on_item_del(id)
{
	dlg.confirm('<wbt:g>del,this,item</wbt:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
    {
		let pm={op:"del_item",cat:cat_name,item_id:id} ;
			send_ajax("cer_ajax.jsp",pm,function(bsucc,ret){
	    		if(!bsucc || ret!='succ')
	    		{
	    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
	    			return ;
	    		}
	    		//
				location.reload();
	    	}) ;
		});
}

function on_item_edit(id)
{
	let tt = "Add Cer Item";
	if(id)
		tt = "Edit Cer Item" ;
	else
		id = "" ;
	
	dlg.open("cer_item_edit.jsp?id="+id,
			{title:tt,w:'500px',h:'400px'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						ret.cat = cat_name ;
						ret.op='add_item';
						dlg.loading(true);
						send_ajax('cer_ajax.jsp',ret,function(bsucc,ret)
						{
							dlg.loading(false);
							if(!bsucc || ret.indexOf('succ')<0)
							{
								dlg.msg(""+ret);
								return ;
							}
							dlg.close();
							refresh_ui();
						},false);
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

</script>
</html>