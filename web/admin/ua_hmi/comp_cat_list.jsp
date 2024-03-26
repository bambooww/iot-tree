<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
String libid = request.getParameter("libid") ;
if(libid==null)
	libid= "" ;
String catid = request.getParameter("catid") ;
CompLib lib = CompManager.getInstance().getCompLibById(libid);
if(lib==null)
{
	out.print("no lib found") ;
	return ;
}
CompCat cat = lib.getCatById(catid) ;
if(cat==null)
{
	out.print("no cat found") ;
	return ;
}
boolean bedit = "true".equalsIgnoreCase(request.getParameter("edit")) ;
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
  <jsp:include page="../head.jsp"></jsp:include>
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
        	margin-top: 0px;
        }
          .layui-table-cell {
            height: auto;
            line-height: 18px;
        }
    </style>
</head>
<body>
Comp in [<%=lib.getTitle() %>/<%=cat.getTitle() %>]<br>
 <%
 if(bedit)
 {
 %>
 <div style="float:left;margin-left:8px;margin-top:4px;">
      <table id="top_oper">
          <tr>
          <td style="padding-left: 5px;">
           <a id="top_oper_add_dev" class="layui-btn  layui-btn-sm layui-btn-primary " ><i class="fa-regular fa-square-plus"></i>&nbsp;Add Device</a>
              </td>
              <td>
              <form class="layui-form" action="">

      </form>
              </td>
              <td style="padding-left: 5px;">
              </td>
              <td style="padding-left: 25px;">
	         	
              </td>
          </tr>
      </table>
  </div>
  <%
 }
  %>
<table id="view_colorval"  class="layui-table" lay-filter="dl_list"  lay-size="sm" lay-even="true" style="width:100%" border="1">
   <thead style="background-color: #cccccc">
     <tr>
	  <td style="width:40%"><wbt:g>title</wbt:g></td>
	  <td style="width:35%"><wbt:g>oper</wbt:g></td>
	</tr>
  </thead>
  <tbody id="color_list">
<%
	List<CompItem> dds = cat.getItems();
	for(CompItem dd:dds)
	{
		String ddid = dd.getId() ;
%>
	<tr id="devdef_" onclick="on_clk_def('<%=ddid%>','<%=dd.getTitle() %>')">
		<td><%=dd.getTitle() %></td>
		
		<td>
<%
	if(bedit)
	{
%>
<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="edit basic" onclick="edit_devdef('<%=ddid%>')"><i class="fa fa-pencil"></i></button>

<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="edit detail" onclick="edit_devdef_detail('<%=ddid%>')"><i class="fa-regular fa-pen-to-square"></i></button>

  <button type="button" class="layui-btn layui-btn-xs layui-btn-danger"  lay-event="del" title="delete" onclick="del_devdef('<%=ddid%>')"><i class="fa-regular fa-rectangle-xmark"></i></button><%
	}
	else
	{
%>
<button type="button" class="layui-btn layui-btn-xs"  lay-event="del" title="select device"><i class="fa-regular fa-hand-pointer"></i></button>
<%
	}
%>
		</td>
	</tr>
<%
	}
%>
  </tbody>
</table>
</body>
<script type="text/javascript">

var libid = "<%=libid%>";
var catid = "<%=catid%>";
var bedit = <%=bedit%>;

function on_clk_def(id,n,tt)
{
	if(parent && parent.on_selected_dev)
		parent.on_selected_dev(id,n,tt) ;
}

$("#top_oper").on("click","#top_oper_add_dev",add_devdef);

var _tmpid = 0 ;

function get_new_id()
{
	_tmpid++ ;
	var d = new Date() ;
	var tmps = 'id_' ;
	tmps += d.getFullYear() ;
	var i = d.getMonth() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i = d.getDay() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getHours();
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getMinutes();
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getSeconds() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	tmps += "_"+_tmpid ;
	return tmps ;
	//return "id_"+scada_tmpid ;
}

function add_devdef()
{
	edit_devdef("")
}

function edit_devdef(id)
{
	
	dlg.open("devdef_edit.jsp?libid="+libid+"&catid="+catid+"&devid="+id,
			{title:"Add Device"},
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
						 
						 ret.op="dev_add" ;
						 ret.libid=libid;
						 ret.catid = catid ;
						 var pm = {
									type : 'post',
									url : "./cat_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								location.reload();
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

function del_devdef(id)
{
	dlg.confirm('delete this device?',{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
		    {
					send_ajax("cat_ajax.jsp","op=dev_del&libid="+libid+"&catid="+catid+"&devid="+id,function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("del err:"+ret) ;
			    			return ;
			    		}
			    		//
						location.reload();
			    	}) ;
				});
}

function edit_devdef_detail(devid)
{
	window.open("devdef_editor.jsp?libid="+libid+"&catid="+catid+"&devid="+devid) ;
}

</script>
</html>