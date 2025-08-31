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
	if(item==null || !(item instanceof NS_TagChgTrigger))
	{
		out.print("no item found") ;
		return ;
	}
	
	NS_TagChgTrigger stb_node= (NS_TagChgTrigger)item ;
	
	boolean ignore_invalid = stb_node.isIgnoreInvalid() ;
	List<String> tagpaths = stb_node.getTagPaths() ;
	
	JSONArray tags_jarr = new JSONArray() ;
	if(tagpaths!=null)
	{
		for(String tagp:tagpaths)
		{
			UATag tag = prj.getTagByPath(tagp) ;
			if(tag==null)
				continue ;
			JSONObject tmpjo = new JSONObject() ;
			tmpjo.put("tagid",tag.getId()) ;
			tmpjo.put("tagp",tagp) ;
			tmpjo.put("tagt",tag.getNodeCxtPathTitleIn(prj)) ;
			tags_jarr.put(tmpjo) ;
		}
	}
%>
 <div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Ignore Invalid:</span>
    </div>
	  <div class="layui-input-inline" style="width: 20px;">
	    <input type="checkbox" id="ignore_invalid" class="layui-input" lay-skin="primary" />
	  </div>
	  <div class="layui-form-mid"><span style="white-space: nowrap;">Ignore Update (Value Not Changed):</span>
    </div>
	  <div class="layui-input-inline" style="width: 20px;">
	    <input type="checkbox" id="ignore_update" class="layui-input" lay-skin="primary" />
	  </div>
	  <div class="layui-form-mid">Change TP</span>
    </div>
	  <div class="layui-input-inline" style="width: 170px;">
	    <select id="chg_tp"  lay-skin="primary" >
<%
	for(NS_TagChgTrigger.ChgTP chgtp:NS_TagChgTrigger.ChgTP.values())
	{
%><option value="<%=chgtp.getIntVal()%>"><%=chgtp.getTitle() %></option><%
	}
%>
	    </select>
	  </div>
</div>

<div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Trigger Delay:</span>
    </div>
	  <div class="layui-input-inline" style="width: 20px;">
	    <input type="checkbox" id="bdelay" class="layui-input" lay-skin="primary" />
	  </div>
	  <div class="layui-form-mid"><span style="white-space: nowrap;">After</span>
    </div>
	  <div class="layui-input-inline" style="width: 100px;">
	    <input type="number" id="delay_ms" class="layui-input" lay-skin="primary" />
	  </div>
	  <div class="layui-form-mid">(MS)  Enable Log</div>
	  <div class="layui-input-inline" style="width: 20px;">
	    <input type="checkbox" id="blog" class="layui-input" lay-skin="primary" />
	  </div>
</div>
 
 <div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Project Tags:</span>
    </div>
	  <div class="layui-input-inline" style="width: 75%;">
	    <div id="tag_paths"  ondblclick="sel_tags()" style="border:1px solid #ececec;width:100%;height:220px;overflow:auto">
	    </div>
	  </div>
	  <div class="layui-form-mid"><button class="layui-btn layui-btn-xs layui-btn-primary" onclick="add_tag()">+</button></div>
 </div>
 
<script>
var prj_path = "<%=prj_path%>";
var container_id="<%=container_id%>";
var netid="<%=netid%>";

var ignore_invalid = <%=ignore_invalid%>;

var tags_jarr = <%=tags_jarr%>;

function get_tag_paths()
{
	let ret=[];
	for(let t of tags_jarr)
	{
		ret.push(t.tagp) ;
	}
	return ret;
}

function get_tag_by_id(id)
{
	for(let t of tags_jarr)
	{
		if(t.tagid==id)
			return t;
	}
	return null;
}

function get_tag_by_path(tagp)
{
	for(let t of tags_jarr)
	{
		if(t.tagp==tagp)
			return t;
	}
	return null;
}

function set_sel_tag(ob)
{
	let old = get_tag_by_id(ob.tagid)
	if(old) return false;
	tags_jarr.push(ob);
	return true;
}

function sel_tags()
{
	dlg.open(`\${PM_URL_BASE}/../../ua_cxt/cxt_tag_selector.jsp?path=\${prj_path}&multi=true&bind_tag_only=true`,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:get_tag_paths()},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_selected_tags() ; //{tagid:tagid,tagp:tagp,tagt:patht}
					if(!ret || ret.length<=0)
					{
						dlg.msg("please select tags");return ;
					}
					tags_jarr = ret ;
					update_ui();
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_tag(tagp)
{
	for(let i = 0 ; i < tags_jarr.length ; i ++)
	{
		let t = tags_jarr[i] ;
		if(t.tagp==tagp)
		{
			tags_jarr.splice(i,1) ;
			update_ui();
			return ;
		}
	}
}

function  add_tag()
{
	dlg.open(`\${PM_URL_BASE}/../../ua_cxt/di_cxt_tag_selector.jsp?path=\${prj_path}&multi=false&bind_tag_only=true`,
			{title:"<w:g>select,tags</w:g>",w:'500px',h:'400px',sel_tagpaths:get_tag_paths()},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let ret = dlgw.get_select_tag() ; //{tagid:tagid,tagp:tagp,tagt:patht}
					if(!ret)
					{
						dlg.msg("please select tag");return ;
					}
					if(set_sel_tag(ret))
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
	let tmps = "<table style='font-size:12px;' class='layui-table' lay-size='sm'>" ;
	
	//if(store_tb.b_all_subt)
	//	tmps += `<span style="color:green">Using all sub tags</span>`;
	if(tags_jarr)
	{
		for(let tag of tags_jarr)
		{
			tmps += `<tr><td>\${tag.tagp}</td><td>\${tag.tagt}</td><td><button onclick="del_tag('\${tag.tagp}')" class="layui-btn layui-btn-xs layui-btn-primary">X</button></td></tr>` ;
		}
	}
	tmps += "</table>"
	$("#tag_paths").html(tmps) ;
}

function on_after_pm_show(form)
{
	update_ui();
}


function get_pm_jo()
{
	let ret = {tag_paths:get_tag_paths()} ;
	ret.ignore_invalid = $("#ignore_invalid").prop("checked") ;
	ret.ignore_update = $("#ignore_update").prop("checked") ;
	ret.chg_tp = parseInt($("#chg_tp").val());
	ret.bdelay = $("#bdelay").prop("checked") ;
	ret.delay_ms = parseInt($("#delay_ms").val());
	ret.blog= $("#blog").prop("checked") ;
	return ret ;
}

function set_pm_jo(jo)
{//console.log(jo) ;
	//tag_paths = jo.tag_paths;
	$("#ignore_invalid").prop("checked",jo.ignore_invalid||true) ;
	$("#ignore_update").prop("checked",jo.ignore_update||false) ;
	$("#chg_tp").val(jo.chg_tp||0) ;
	$("#bdelay").prop("checked",jo.bdelay||false) ;
	$("#delay_ms").val(jo.delay_ms||-1) ;
	$("#blog").prop("checked",jo.blog||false) ;
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>