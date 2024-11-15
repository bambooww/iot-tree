<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.core.msgnet.store.*,
	org.iottree.core.station.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	MNManager mnm = MNManager.getInstanceByContainerId(container_id) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	IMNContainer mnc = net.getBelongTo().getBelongTo() ;
	if(mnc==null || !(mnc instanceof UAPrj))
	{
		out.print("no in prj") ;
		return ;
	}
	UAPrj prj = (UAPrj)mnc ;
	String prj_path = "/"+prj.getName() ;
	MNBase item =net.getItemById(itemid) ;
	if(item==null || !(item instanceof PlatRecvedDataFilterSave_NM))
	{
		out.print("no item found") ;
		return ;
	}
	
	PlatRecvedDataFilterSave_NM stb_node= (PlatRecvedDataFilterSave_NM)item ;
	
	boolean b_alltagpath = stb_node.isAllTagPaths();
	List<String> tagpaths = stb_node.getTagPaths() ;
	JSONArray tagsubpaths_jarr = new JSONArray(tagpaths) ;
%>

 <div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Project Tags:</span>
    <br><br> <input type="checkbox" id="b_all_subt" class="layui-input" lay-skin="primary" /><br>All Sub Tags
    </div>
	  <div class="layui-input-inline" style="width: 75%;">
	    <div id="tag_paths" onclick="sel_tags()" style="border:1px solid #ececec;width:100%;height:220px;overflow:auto">
	    </div>
	  </div>
 </div>
 
 <div class="layui-form-item">
    <label class="layui-form-label">InfluxDB Tag1</label>
    <div class="layui-form-mid"> Name:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag1_name" name="tag1_name" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"> Value:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag1_val" name="tag1_val" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">InfluxDB Tag2</label>
    <div class="layui-form-mid"> Name:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag2_name" name="tag2_name" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"> Value:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag2_val" name="tag2_val" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
<script>
var prj_path = "<%=prj_path%>";
var container_id="<%=container_id%>";
var netid="<%=netid%>";

var b_alltagpath = <%=b_alltagpath%>;
var tag_paths = <%=tagsubpaths_jarr%>;

function sel_tags()
{	
	dlg.open(`\${PM_URL_BASE}/../../ua_cxt/cxt_tag_selector.jsp?path=\${prj_path}&multi=true&bind_tag_only=true`,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:tag_paths},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tagpaths() ;
					if(!ret || ret.length<=0)
					{
						dlg.msg("please select tags");return ;
					}
					tag_paths = ret ;
					update_ui();
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function update_ui()
{
	let tmps = "" ;
	
	//if(store_tb.b_all_subt)
	//	tmps += `<span style="color:green">Using all sub tags</span>`;
	
	if(tag_paths)
	{
		for(let subt of tag_paths)
		{
			tmps += `<br>&nbsp;&nbsp;\${subt}` ;
		}
	}
	$("#tag_paths").html(tmps) ;
}

function on_after_pm_show(form)
{
	update_ui();
}


function get_pm_jo()
{
	//store_tb.tablen = $('#tablen').val();
	//store_tb.tablet = $('#tablet').val();
	
	let ret = {tag_paths:tag_paths} ;
	let n = $("#tag1_name").val() ;
	let v = $("#tag1_val").val() ;
	if(n && v)
	{
		ret.tag1 = {n:n,v:v} ;
	}
	
	n = $("#tag2_name").val() ;
	v = $("#tag2_val").val() ;
	if(n && v)
	{
		ret.tag2 = {n:n,v:v} ;
	}
	
	n = $("#tag3_name").val() ;
	v = $("#tag3_val").val() ;
	if(n && v)
	{
		ret.tag3 = {n:n,v:v} ;
	}
	ret.all_tag_paths = $("#b_all_subt").prop("checked") ;
	return ret ;
}

function set_pm_jo(jo)
{
	
	let tag = jo.tag1 ;
	if(tag)
	{
		$("#tag1_name").val(tag.n) ;
		$("#tag1_val").val(tag.v) ;
	}
	tag = jo.tag2 ;
	if(tag)
	{
		$("#tag2_name").val(tag.n) ;
		$("#tag2_val").val(tag.v) ;
	}
	tag = jo.tag3 ;
	if(tag)
	{
		$("#tag3_name").val(tag.n) ;
		$("#tag3_val").val(tag.v) ;
	}
	
	b_alltagpath = jo.all_tag_paths ;
	tag_paths = jo.tag_paths;
	$("#b_all_subt").prop("checked",b_alltagpath) ;
}

function get_pm_size()
{
	return {w:600,h:450} ;
}

//on_init_pm_ok() ;
</script>