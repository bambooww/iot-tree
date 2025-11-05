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
	org.iottree.core.msgnet.nodes.*,
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
	if(item==null || !(item instanceof NM_TagTSRec2RDB))
	{
		out.print("no item found") ;
		return ;
	}
	
	NM_TagTSRec2RDB node = (NM_TagTSRec2RDB)item ;
	
	UATag tag = node.getTag() ;
	String tag_id = "" ;
	String tag_path = "" ;
	String tag_t = "" ;
	int tag_val_maxlen = -1 ;
	long min_rec_intv = -1 ;
	if(tag!=null)
	{
		tag_id = tag.getId() ;
		tag_path = tag.getNodeCxtPathInPrj() ;
		tag_t = tag.getNodeCxtPathTitleIn(prj) +" ["+tag.getValTp()+"]";
		
		tag_val_maxlen = node.getTagValMaxLen() ;
		min_rec_intv = node.getMinRecIntvMS() ;
	}
%>
<div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Project Tag:</span>
    </div>
	  <div class="layui-input-inline" style="width: 65%;">
	    <input id="tag"  ondblclick="set_tag()" tag_id="<%=tag_id %>" tag_path="<%=tag_path %>" value="<%=tag_t %>"  class="layui-input"/>
	    </div>
	  <div class="layui-form-mid"><button class="layui-btn layui-btn-sm layui-btn-primary" onclick="set_tag()">...</button></div>
 </div>
 
 <div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;"></span>
    </div>
    <div class="layui-form-mid">Value Max Len:</div>
	  <div class="layui-input-inline" style="width:80px;">
	    <input id="tag_val_maxlen"  type="number" step="1" value="<%=tag_val_maxlen %>"  class="layui-input"/>
	  </div>
	  <%--
	  <div class="layui-form-mid">Min Record Interval(MS)</div>
	  <div class="layui-input-inline" style="width:80px;">
	    <input id="min_rec_intv"  type="number" step="1" value="<%=min_rec_intv %>"  class="layui-input"/>
	  </div>
	   --%>
 </div>
 
<script>
var prj_path = "<%=prj_path%>";
var container_id="<%=container_id%>";
var netid="<%=netid%>";

function get_tag_id()
{
	return $("#tag").attr("tag_id");
}

function get_tag_path()
{
	return $("#tag").attr("tag_path");
}

function set_sel_tag(t)
{
	let tag = $("#tag");
	tag.attr("tag_id",t.tagid||"") ;
	tag.attr("tag_path",t.tagp||"") ;
	tag.attr("vt",t.vt||"") ;
	tag.val(t.tagt||tag.tagp+" ["+t.vt||""+"]") ;
}

function  set_tag()
{
	dlg.open(`\${PM_URL_BASE}/../../ua_cxt/di_cxt_tag_selector.jsp?path=\${prj_path}&multi=false&bind_tag_only=true`,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:get_tag_path()},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_select_tag() ; //{tagid:tagid,tagp:tagp,tagt:patht}
					if(!ret)
					{
						dlg.msg("please select tag");return ;
					}
					set_sel_tag(ret);
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
	let ret = {} ;
	//ret.ignore_invalid = $("#ignore_invalid").prop("checked") ;
	ret.tag_id = $("#tag").attr("tag_id");
	ret.tag_val_maxlen = parseInt($("#tag_val_maxlen").val());
	if(isNaN(ret.tag_val_maxlen))
		ret.tag_val_maxlen = -1 ;
	if(ret.tag_val_maxlen<=0)
	{
		if($("#tag").attr("vt")=="vt_str")
		{
			return "string tag must has value max length" ;
		}
	}
	ret.min_rec_intv = parseInt($("#min_rec_intv").val());
	return ret ;
}

function set_pm_jo(jo)
{//console.log(jo) ;
	
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>