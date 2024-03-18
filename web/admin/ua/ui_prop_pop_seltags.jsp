<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "nodepath"))
		return ;
	String nodep = request.getParameter("nodepath") ;
	UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
	if(node==null)
	{
		out.print("no node found with path="+nodep) ;
		return ;
	}
	
	UANodeOCTagsCxt cxtNode = null ;
	
	if(node instanceof UAHmi)
	{
		UAHmi uhmi = (UAHmi)node ;
		cxtNode = uhmi.getBelongTo() ;
	}
	else
	{
		cxtNode = (UANodeOCTagsCxt)node ;
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,550);
</script>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}

table, th, td
{
border:1px solid;
}
th
{
	font-size: 12px;
	font-weight: bold;
}
td
{font-size: 12px;
}


.prop_table
{
width:99%;
border: 0px;
margin: 0 auto;
}

.prop_table tr>td
{
	border: 1;
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
height:200px;
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

li {
    list-style: none;
}


.pi_sel
{
background-color: #0078d7;
color:#ffffff;
}

.pi_edit_unit
{
border: 0px;
width:100%;
}

.tag_sel
{
 background-color: #1e90ff;
}

.left_tb tr:hover {
	background-color: #979797;
}

#tb_right_seled input
{
	width:100%;
}
</style>
</head>
<body>

<table class="prop_table" style="border:solid 1px" >
  <tr>
    <td style="width:45%" >
    <div id="prop_edit_path" class="prop_edit_path">[<%=nodep %>]&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

    </div>
       <div id="list_table" class="prop_edit_cat" style="height:420px;width:400px">
    	<table style="width:100%;border:0px" class='left_tb' id="tb_left_tags">
    		<thead>
    			<tr style="background-color: #f0f0f0">
    				<td width="50%"><wbt:g>tag</wbt:g></td>
      				<td><wbt:g>title</wbt:g></td>
    				<td width="20%"><wbt:g>val,type</wbt:g></td>
    			</tr>
    		</thead>
    		<tbody id="bind_tb_body" >
    			<%
for(UATag tag:cxtNode.listTagsAll())
{
	String tagp = tag.getNodeCxtPathIn(cxtNode) ;
%>
<tr tagp="<%=tagp%>" tagt="<%=tag.getTitle() %>">
  <td><%=tagp%></td>
  <td><%=tag.getTitle() %></td>
  
  <td><%=tag.getValTp() %></td>
</tr>
<%
}
%>
    		</tbody>
		</table>
	</div>


    </td>
    <td style="width:55%" >
      <table style="border:0px;height:100%">
      
       <tr style="height:100%;border:solid 0px">
         <td style="width:5%;vertical-align:middle;"  >
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="sel_or_not(true)" title="bind to tag"><i class="fa-solid fa-arrow-right"></i></button><br><br>
	     	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="sel_or_not(false)" title="unbind from tag"><i class="fa-solid fa-arrow-left"></i></button>
	    </td>
	    <td style="width:95%;vertical-align: top;height:100%"  >
	    <div id="prop_edit_path" class="prop_edit_path"><wbt:g>selected,tags</wbt:g> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     
    </div>
	    <div id=""  class="prop_edit_panel" style="height:420px">
	       <table style="width:100%;overflow: auto;" >
	       	 <thead>
	       	   <tr>
	       	    <th style="width:50%"><wbt:g>tag</wbt:g></th>
	       	    <th style="width:50%"><wbt:g>show,title</wbt:g></th>
	       	   </tr>
	       	 </thead>
	       	 <tbody id="tb_right_seled">

			</tbody>
	       </table>
		 </div>
	    </td>
       </tr>
      </table>
    </td>
  </tr>
</table>


</body>
<script type="text/javascript">

var input_txt = dlg.get_opener_opt("inputv") ;

var sel_obs = [] ;


var cur_tag_tr = null ;
var cur_sel_tr = null ;

$("#tb_left_tags tr").click(function(){
	cur_tag_tr = $(this) ;
	refresh_tag_list();
});

//function on_right(ob)
//{
//	cur_bind_map_tr = $(ob) ;
//	refresh_bind_map();
//}

function refresh_tag_list()
{
	$("#tb_left_tags tr").each(function(){
		$(this).removeClass("tag_sel") ;
	});
	if(cur_tag_tr!=null)
		cur_tag_tr.addClass("tag_sel") ;
}

function sel_or_not(b)
{
	if(b)
	{
		if(!cur_tag_tr)
		{
			dlg.msg("<wbt:g>pls,select,tag,left</wbt:g>") ;
			return ;
		}
		let tagp = cur_tag_tr.attr("tagp") ;
		let tt = cur_tag_tr.attr("tagt") ;
		set_right_tag(tagp,tt)
	}
	else
	{
		if(!cur_sel_tr)
		{
			dlg.msg("<wbt:g>pls,select,item,right</wbt:g>") ;
			return ;
		}
		cur_sel_tr.remove() ;
		cur_sel_tr=null ;
	}
}

function on_right_sel(tr)
{
	cur_sel_tr = $(tr) ;
	refresh_sel_list() ;
}

function refresh_sel_list()
{
	$("#tb_right_seled tr").each(function(){
		$(this).removeClass("tag_sel") ;
	});
	if(cur_sel_tr!=null)
		cur_sel_tr.addClass("tag_sel") ;
}

function set_right_tag(tagp,title)
{
	let old = get_right_tag(tagp) ;
	if(old)
	{
		dlg.msg("<wbt:g>this,tag,be_selected</wbt:g>");
		return ;
	}
	
	$("#tb_right_seled").append(`<tr onclick="on_right_sel(this)">
		<td>\${tagp}</td>
		<td><input type="text" value="\${title}"  /></td>
		</tr>`);
}

function set_right_tags(obs)
{
	for(let ob of obs)
	{
		set_right_tag(ob.tag,ob.title) ;
	}
}

function get_right_tags()
{
	let obs = [] ;
	$("#tb_right_seled").find("tr").each(function(){
		let curtr = $(this) ;
		let tag = curtr.find("td").eq(0).text() ;
		let tt = curtr.find("input").eq(0).val() ;
		obs.push({tag:tag,title:tt}) ;
	}) ;
	return obs ;
}

function get_right_tag(tagp)
{
	let ret = null ;
	$("#tb_right_seled").find("tr").each(function(){
		let curtr = $(this) ;
		let tag = curtr.find("td").eq(0).text() ;
		let tt = curtr.find("input").eq(0).val() ;
		if(tag==tagp)
			ret = {tag:tag,title:tt} ;
	}) ;
	return ret ;
}

function init_sel()
{
	if(!input_txt)
	{
		return ;
	}
	eval("sel_obs="+input_txt) ;
	set_right_tags(sel_obs) ;
}

init_sel() ;

function win_close()
{
	dlg.close(0);
}



function do_submit(cb)
{
	let obs = get_right_tags();
	console.log(obs) ;
	
	cb(true,{txt:JSON.stringify(obs)});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>