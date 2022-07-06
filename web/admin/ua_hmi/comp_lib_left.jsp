<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
String libid = request.getParameter("libid") ;
if(libid==null)
	libid= "" ;
String catid =request.getParameter("catid") ;
if(catid==null)
	catid= "" ; 
String libtitle ="" ;
String cattitle ="" ;
if(Convert.isNotNullEmpty(libid))
{
	CompLib lib = CompManager.getInstance().getCompLibById(libid) ;
	if(lib==null)
	{
		out.print("no lib found") ;
		return ;
	}
	libtitle = lib.getTitle() ;
	
	if(Convert.isNotNullEmpty(catid))
	{
		CompCat cat = lib.getCatById(catid) ;
		if(cat==null)
		{
			out.print("no cat found") ;
			return ;
		}
		cattitle = cat.getTitle() ;
	}
}
boolean bedit = "true".equalsIgnoreCase(request.getParameter("edit")) ;
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
<link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
<script src="/_js/selectmenu/selectmenu.min.js"></script>
    <style>
        .layui-form-label{
            width: 120px;
        }
        .layui-input-block {
            margin-left: 140px;
            min-height: 36px;
            width:240px;
        }
        .layui-table-view
        {
        	margin-top: 45px;
        }
          .layui-table-cell {
            height: auto;
            line-height: 18px;
        }
    </style>
</head>
<body>
    <div style="float:left;margin-left:8px;margin-top:4px;">
      <table id="top_oper">
          <tr>
          <td style="padding-left: 5px;">
           Lib: 
              </td>
              <td>
              <form class="layui-form" action="">
                 <select id="lib_ids" name="lib_ids" lay-filter="lib_ids" style="width:50px">
        <%
 for(CompLib dl:CompManager.getInstance().getCompLibs())
 {
	 String sel = (libid.equals(dl.getId())?"selected=selected":"");
	 
%>
<option value="<%=dl.getId()%>" <%=sel %>><%=dl.getTitle() %></option>
<%
 }
%>
      </select>
      </form>
              </td>
              
              <td style="padding-left: 1px;">
<%
if(false) //(bedit)
{
%>
<div class="btn-group open"  id="btn_menu_lib" style="border:1px solid;border-color: #c9c9c9">
				  <a class="btn" href="#">---</a>
				  <a class="btn" href="#">
				    <span class="fa fa-caret-down" title="Toggle dropdown menu"></span>
				  </a>
				 </div>
<%
}
%>
              </td>
              <td style="padding-left: 15px;">
              <button id="top_oper_add_cat" type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="add_cat"><i class="fa fa-plus"></i>Category</button>
              </td>
          </tr>
      </table>
  </div>
<table id="lib_list"  lay-filter="lib_list"  lay-size="sm" lay-even="true">

</table>
  <script type="text/html" id="row_toolbar">
<div class="layui-btn-group">
{{ if(true){  }}
  <a class="layui-btn layui-btn-xs " lay-event0="show" title=show devices"><i class="fa fa-archive" ></i></a>
{{ } }}
  <button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="edit"><i class="fa fa-pencil"></i></button>
  
<button type="button" class="layui-btn layui-btn-xs layui-btn-danger"  lay-event="del" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
  </div>
</script>
<script>

var bedit = <%=bedit%>
var table ;
var table_cur_page = 1 ;

var libid="<%=libid%>";
var libtitle="<%=libtitle%>";
var catid = "<%=catid%>" ;
var cattitle = "<%=cattitle%>" ;

$('#btn_menu_lib').click(function(){
	$(this).selectMenu({
		
		regular : true,
		data :  [
			{content:'Library',header: true},
			{content:'Edit Current Lib',callback:function(){lib_edit();}},
			{content:'Add Library',callback : function(){lib_add();}},
			{content:'Category',header: true},
			{content:'Add Category',callback : function(){lib_add_edit_cat();}}
		]
	});
});

$("#top_oper").on("click","#top_oper_add_cat",lib_add_cat);

layui.use('form', function(){
	  var form = layui.form;
	  
	  form.on('select(lib_ids)', function(obj){
		       refresh_table()
		  });
	  
	  form.render();
	  
	  
	});
layui.use('table', function()
{
  table = layui.table;
  
  let cols = [];
 if(bedit)
 {
	 //cols.push({field: 'n', title: 'Name', width:'25%'}) ;
	 cols.push({field: 't', title: 'Title', width:'65%'});
	 cols.push({field: 'Oper', title: 'Oper', width:"35%" ,toolbar: '#row_toolbar'}) ;
 }
 else
 {
	 //cols.push({field: 'n', title: 'Name', width:'35%'}) ;
	 cols.push({field: 't', title: 'Title', width:'95%'});
 }
  table.render({
    elem: '#lib_list'
    ,height: "full-60"
   // ,url: 'lib_ajax.jsp?op=list&libid=' //data ajax
    //,page: {layout:['prev', 'page', 'next'],limit:18,theme:"#c00"} //open page
    ,cols: [cols]
  ,parseData:function(res){
		if(res.data.length==0){
			return{
				'code':'201',
				'msg':'no category list'
			};
		};
	}
    ,done:function(res, curr, count){
   	 table_cur_page = curr ;
   	 var trs = $(".layui-table-body.layui-table-main tr");
   	 if(res && res.data)
   	 {
   		for(var i = 0 ; i < res.data.length;i++)
  		 {
  		    if(i%2==1)
	    		 trs.eq(i).css("background-color","#f2f2f2");
	     }
   	 }
   	 
   	 
   	 }
  });
  
  table.on('tool(lib_list)', function(obj){ // lay-filter="mc_acc_list"
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
  
  table.on('row(lib_list)', function(obj)
		  {
	  var libid = $("#lib_ids").val() ;
			  var data = obj.data; //cur d
			  on_sel_cat(data.id,data.t)
		  });
});

function on_sel_cat(id,tt)
{
	catid = id ;
	cattitle = tt ;
	let fwin = FindFrameWin('comp_right');
	  if(fwin&&catid)
	  	fwin.location.href="comp_cat_list.jsp?libid="+libid+"&catid="+catid+"&edit="+bedit;
	  
	  if(parent && parent.on_selected_libcat)
		  parent.on_selected_libcat(libid,catid,libtitle+" - "+cattitle)
	
}

function refresh_table(bfirst)
{
	libid = $("#lib_ids").val() ;
	libtitle =$('#lib_ids option:selected').html();
	table.reload("lib_list",{url:"comp_lib_ajax.jsp?op=list&libid="+libid});
	if(!bfirst)
	{
		if(parent && parent.on_selected_libcat)
			  parent.on_selected_libcat("","","")
		if(parent && parent.on_selected_comp)
			  parent.on_selected_comp("","","")
	}
}

refresh_table(true);
if(catid)
	on_sel_cat(catid,cattitle);

/*
function lib_add()
{
	lib_add_or_edit_cat(false)
}

function lib_edit()
{
	event.stopPropagation();
	lib_add_or_edit_cat(true)
}
*/



function lib_add_cat()
{
	lib_add_edit_cat("")
}

function lib_del_cat(cid)
{
	var libid = $("#lib_ids").val() ;
	console.log(libid) ;
	if(!libid)
	{
		dlg.msg("no lib selected") ;
	}
	dlg.confirm('delete this category?',{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
		    {
					send_ajax("comp_lib_ajax.jsp","op=del_cat&libid="+libid+"&catid="+cid,function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("del err:"+ret) ;
			    			return ;
			    		}
			    		//
						refresh_table();
			    	}) ;
				});
}

function lib_add_edit_cat(catid)
{
	if(!catid)
		catid ="" ;
	var libid = $("#lib_ids").val() ;
	dlg.open("comp_cat_edit.jsp?libid="+libid+"&catid="+catid,
			{title:"Edit Category"},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="edit_cat" ;
						 ret.libid=libid;
						 var pm = {
									type : 'post',
									url : "./comp_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if(ret.indexOf("{")!=0)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								refresh_table();
							}).fail(function(req, st, err) {
								dlg.msg(err);
							});
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

//$("#top_oper").on("click","#top_oper_edit_lib",lib_edit);
//

$("#top_oper").on("keydown","#search_txt",function(evt){
		if(evt.keyCode==13) do_search() ;
	});

</script>
</body>
</html>