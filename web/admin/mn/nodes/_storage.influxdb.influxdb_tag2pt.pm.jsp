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
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
	String prjid = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	//IMNContainer
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager mnm= MNManager.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	MNBase item =net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	String prj_path = prj.getNodePath() ;
	MNMsg msg = null;
	if(item instanceof MNNode)
		msg = ((MNNode)item).RT_getLastMsgIn() ;
	if(msg==null)
		msg = new MNMsg() ;
	JSONObject pld = msg.getPayloadJO(null) ;
	LinkedHashMap<String,Object> name2v = new LinkedHashMap<>() ;
	if(pld!=null)
	{
		for(String n:pld.keySet())
		{
			Object obj = pld.get(n) ;
			name2v.put(n,obj) ;
		}
	}
	
%>
<div class="layui-form-item">
    <label class="layui-form-label">Measurement:</label>
    <div class="layui-input-inline" style="width: 250px;">
      <input type="text" id="measurement" name="measurement" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Tag1</label>
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
    <label class="layui-form-label">Tag2</label>
    <div class="layui-form-mid"> Name:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag2_name" name="tag2_name" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"> Value:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag2_val" name="tag2_val" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Tag3</label>
    <div class="layui-form-mid"> Name:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag3_name" name="tag3_name" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"> Value:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="tag3_val" name="tag3_val" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
 <div class="layui-form-item">
    <div class="layui-form-label"><w:g>tag,filter</w:g>:</div>
	  <div class="layui-input-inline" style="width: 350px;">
	    <textarea type="text" id="filter_tags" name="filter_tags" style="height:60px;" autocomplete="off" class="layui-input"
	    	onclick="sel_tags()"
	    	></textarea>
	  </div>
 </div>
<script>
var prjid="<%=prjid%>";
var prj_path="<%=prj_path%>";

var filter_tags=[];

function sel_tags()
{	
	dlg.open("../ua_cxt/cxt_tag_selector.jsp?w_only="+false+"&multi=true&path="+prj_path,//+"&val="+tmpv,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:filter_tags},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tagpaths();
					//let txt = dlgw.get_selected_tagtxt();
					filter_tags = ret ;
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
	$("#filter_tags").val(filter_tags.join("\r\n"));
}

function on_after_pm_show(form)
{
	update_ui();
}


function get_pm_jo()
{
	var measurement = $('#measurement').val();
	if(!measurement)
	{
		return '<w:g>pls,input,</w:g> Measurement';
	}
	
	let ret = {measurement:measurement} ;
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
	ret.filter_tags=filter_tags;
	return ret ;
}

function set_pm_jo(jo)
{
	$('#measurement').val(jo.measurement||"");
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
	
	filter_tags = jo.filter_tags||[] ;
}

function get_pm_size()
{
	return {w:500,h:450} ;
}

//on_init_pm_ok() ;
</script>