<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,org.iottree.core.devtree.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
	LinkedHashMap<String,DTRunBlkCat> blkcats = DTRunBlkManager.getInstance().getRunBlkCatMap();
	boolean bdlg = "true".equalsIgnoreCase(request.getParameter("dlg"));
	
	JSONArray cat_jarr = new JSONArray() ;
	for(DTRunBlkCat cat:blkcats.values())
	{
		cat_jarr.put(cat.toJO()) ;
	}
%>
<html>
<head>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
</head>
<style>
table{border:0px solid skyblue;}

.oc-toolbar .toolbarbtn
{
position:relative;
margin: 5px;
font-size: 13px;

width:100px;height:120px;

}

select option
{
font-size: 12px;
}

.btns
{
overflow: auto;
}

.toolbarbtn div
{
background-color: #2f2f2f;width:100px;height:100px;float:left; 
}

.toolbarbtn span
{
position:absolute;
bottom:10px;
left:0px;
}


.rmenu_item:hover {
	background-color: #373737;
}

.toolbarbtn:hover .item_edit{
visibility: visible;
}

.item_edit
{
	position:relative;
	top:70px;
	margin:0px;
	left:0px;
	padding-top:3px;
	width:100%;
	height:30px;
	visibility:hidden;
	background-color: #f2f6fb;
}

.item_title
{
	position:relative;
	top:80px;
	font-size:15px;
	font-weight:bold;
	margin:0px;
	left:0px;
	opacity: 0.9;
	padding-top:8px;
	width:100%;
	height:20px;
	background-color: #b8dbfe;
}
.seled {color:green;font-weight: bold;}
</style>
<script type="text/javascript">

</script>
<body style="overflow: hidden">
<table style="width:100%;height:100%;border:0px solid red;">
	<tr >
		<td style="width:40%;height:100%;vertical-align: top;">
			<table id="cat_list"  lay-filter="cat_list"  lay-size="sm" lay-even="true" style="height:100%;">
				
			</table>
		</td>
		<td style="width:60%;height:100%;vertical-align: top;" >

			<table id="runblk_items"  lay-filter="runblk_items"  lay-size="sm" lay-even="true" style="height:100%;">
				
			</table>
		</td>
	</tr>
</table>
</body>
<script type="text/html" id="rb_toolbar">
<div class="layui-btn-group">
  <button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="select"><i class="fa-solid fa-check"></i></button>
  </div>
</script>
<script type="text/javascript">
var bdlg = <%=bdlg%>
dlg.resize_to(700,500) ;
var cat_jarr = <%=cat_jarr%> ;

layui.use('table', function()
{
		  table = layui.table;
		  
let cols = [];
	 cols.push({field: 't', title: '<wbt:g>cat,title</wbt:g>', width:'80%'});
	 cols.push({field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"20%" ,toolbar: '#row_toolbar'}) ;

table.render({
    elem: '#cat_list'
    ,height: "full-20"
   // ,url: 'lib_ajax.jsp?op=list&libid=' //data ajax
    ,page: false,even: false,limit:100000
    ,cols: [cols]
  ,data:cat_jarr
  });
  
  table.on('tool(cat_list)', function(obj){ // lay-filter="mc_acc_list"
	  var data = obj.data; //cur d
	  var lay_evt = obj.event; // lay-event
	  var tr = obj.tr; //tr DOM
	 
	  if(lay_evt === 'detail'){ //
	    //do somehing
	    
	  }
	  else if(lay_evt === 'del')
	  {
		  lib_del_cat(data.id);
	  }
	  else if(lay_evt === 'edit')
	  { 
		  lib_add_edit_cat(data.id) ;
	  }
	  
	});
  
  table.on('row(cat_list)', function(obj)
		  {
	  var trs = $(".layui-table-body.layui-table-main tr");
	  trs.each(function(){
		  $(this).removeClass("seled") ;
	  })
	  obj.tr.addClass("seled");
			  var data = obj.data; //cur d
			  on_sel_cat(data.n,data.t)
		  });


let runblk_cols = [{field: 't', title: '<wbt:g>runblk,title</wbt:g>', width:'80%'},
	{field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"20%" ,toolbar: '#rb_toolbar0'}];

table.render({
    elem: '#runblk_items'
    ,height: "full-20"
   // ,url: 'lib_ajax.jsp?op=list&libid=' //data ajax
    ,page: false,even: true,limit:100000
    ,cols: [runblk_cols]
	  ,data:[]
	  ,text: {
	      none: 'No Data'
	  }
  });
  
  table.on('tool(runblk_items)', function(obj){ // lay-filter="mc_acc_list"
	  var data = obj.data; //cur d
	  var lay_evt = obj.event; // lay-event
	  var tr = obj.tr; //tr DOM
	 
	  if(lay_evt === 'detail'){ //
	    //do somehing
	    
	  }
	  else if(lay_evt === 'select')
	  {
		  sel_runblk(data);
	  }
	  else if(lay_evt === 'edit')
	  { 
		  lib_add_edit_cat(data.id) ;
	  }
	  
	});
  
  table.on('row(runblk_items)', function(obj)
		  {
			  var data = obj.data; //cur d
			  on_sel_runblk(data)
		  });
});

function tb_reload_blks(data)
{
	table.reload("runblk_items",{data:data});
}

var cur_cat = null ;

function on_sel_cat(n,t)
{
	//console.log(n,t) ;
	cur_cat = {n:n,t:t}
	send_ajax("runblk_ajax.jsp",{op:"list_runblks_cat",cat:n},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret);return;
		}
		let obs = null;
		eval("obs="+ret) ;
		console.log(obs)
		//obs=[{t:"aaaa"},{t:"aaaaaa3"}]
		tb_reload_blks(obs)
	});
}

function on_sel_runblk(d)
{
	if(!cur_cat) return;
	dlg.close({catn:cur_cat.n,catt:cur_cat.t,...d}) ;
}

</script>
</html>