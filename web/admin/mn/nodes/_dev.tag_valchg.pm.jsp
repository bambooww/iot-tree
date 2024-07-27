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
	if(!Convert.checkReqEmpty(request, out, "container_id"))
		return ;
	
	String prjid = request.getParameter("container_id") ;
	String id = request.getParameter("id") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}
String prj_path = prj.getNodePath() ;
String pm_jo_str = request.getParameter("pm_jo") ;
JSONObject pm_jo = new JSONObject(pm_jo_str) ; //{"tagids":["r5","r6"]}
//System.out.println(pm_jo) ;
JSONArray tagids_jarr = pm_jo.optJSONArray("tagids") ;
// get path to show
StringBuilder pss = new StringBuilder() ;
if(tagids_jarr!=null)
{
	int n = tagids_jarr.length() ;
	for(int i = 0 ; i < n ; i ++)
	{
		String tagid = tagids_jarr.getString(i) ;
		UATag tag= prj.findTagById(tagid) ;
		if(tag==null)
			continue ;
		pss.append(tag.getNodeCxtPathInPrj()).append("\r\n") ;
	}
}
%>
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:60%;">
      <input type="checkbox" class="layui-input" lay-skin="primary" id="asyn" /> <w:g>asyn_run</w:g>
    </div>
  </div>
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>write,tags</w:g></label>
    <div class="layui-input-inline" style="overflow: auto;width:350px;">
      <textarea style="width:100%;height:200px;" id="tags"  readonly="readonly" onclick="sel_tags()" class="layui-input"><%=pss %></textarea>
    </div>
    <div class="layui-form-mid"><button onclick="sel_tags()">...</button></div>
  </div>

<script>

var prjid="<%=prjid%>";
var prj_path="<%=prj_path%>";

var tagids = [];

function sel_tags()
{	
	dlg.open("../ua_cxt/cxt_tag_selector.jsp?w_only="+true+"&multi=true&path="+prj_path,//+"&val="+tmpv,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagids:tagids},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tagids();
					let txt = dlgw.get_selected_tagtxt();

					tagids = ret ;
					$("#tags").val(txt);
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
	let jo = {} ;
	jo.asyn = $("#asyn").prop("checked") ;
	jo.tagids = tagids ;
	return jo ;
}

function set_pm_jo(jo)
{// no path show
	tagids = jo.tagids||[];
	
	$("#asyn").prop("checked",jo.asyn||false) ;
}

function get_pm_size()
{
	return {w:500,h:350} ;
}

//on_init_pm_ok() ;
</script>