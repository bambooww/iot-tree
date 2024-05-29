<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
	
	String prjid = request.getParameter("prjid") ;
	String id = request.getParameter("id") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}
String prj_path = prj.getNodePath() ;
%>
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>write,tags</w:g></label>
    <div class="layui-input-inline" style="overflow: auto;width:350px;">
      <textarea style="width:100%;height:50px;" id="w_tags"  readonly="readonly" onclick="sel_tags('w')" class="layui-input"></textarea>
    </div>
    <div class="layui-form-mid"><button onclick="sel_tags('w')">...</button></div>
  </div>

<script>

var prjid="<%=prjid%>";
var prj_path="<%=prj_path%>";

var w_tagids = [];

function sel_tags(rw)
{
	let seltagids = [] ;
	let w_only = "" ;
	if(rw=='r')
		seltagids = out_tagids ;
	else if(rw=='w')
	{
		seltagids = in_tagids ;
		w_only = "true" ;
	}
		
	else
		return ;
	
	dlg.open("../ua_cxt/cxt_tag_selector.jsp?w_only="+w_only+"&multi=true&path="+prj_path,//+"&val="+tmpv,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagids:seltagids},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tagids();
					let txt = dlgw.get_selected_tagtxt();
					if(rw=='r')
					{
						out_tagids= ret ;
						$("#r_tags").val(txt);
					}
					else if(rw=='w')
					{
						in_tagids = ret ;
						$("#w_tags").val(txt);
					}
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_after_pm_show(form)
{
	
}


function get_pm_jo()
{
	var topic = $('#topic').val();
	if(!topic)
	{
		return '<w:g>pls,input,topic</w:g>';
	}
	
	return {topic:topic} ;
}

function set_pm_jo(jo)
{
	$('#topic').val(jo.topic||"");
}

function get_pm_size()
{
	return {w:500,h:350} ;
}

//on_init_pm_ok() ;
</script>