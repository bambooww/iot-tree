<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.filter.*,
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



List<SubFilteredTree.PropDef> pds_cont = SubFilteredTree.getExtPropDefsCont(prj) ;
List<SubFilteredTree.PropDef> pds_tag = SubFilteredTree.getExtPropDefsTag(prj) ;

SubFilteredTree sft = new SubFilteredTree(prj) ;
String sub_root_path = sft.getSubRootPath() ;

boolean c_tps_en = sft.isContainerTPSetEn() ;
HashSet<String> c_tps = sft.getContainerTPSet() ;
boolean c_exts_en = sft.isContainerExtSetEn() ;
HashSet<String> c_exts = sft.getContainerExtSet() ;
boolean tag_inc_sys = sft.isTagIncSys() ;
boolean tag_exts_en = sft.isTagExtSetEn() ;
HashSet<String> tag_exts = sft.getTagExtSet() ;
%>

   <div class="layui-form-item">
    <label class="layui-form-label"><w:g>rootp</w:g>:</label>
    <div class="layui-input-inline " id="filter_list" style="width:250px;">
    	<input type="text" id="sub_root_path" name="sub_root_path" value="" readonly  autocomplete="off" class="layui-input" onclick="sel_sub_root(this)">
    </div>
    <div class="layui-form-mid"><w:g>tag_inc_sys</w:g></div>
    <div class="layui-input-inline" style="width:50px;">
    <input type="checkbox" id="tag_inc_sys" name="tag_inc_sys" lay-text="" lay-skin="primary" <%=(tag_inc_sys?"checked":"") %>> 
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>cont_n</w:g>:</label>
    <div class="layui-form-mid"><w:g>en_filter</w:g>:</div>
	  <div class="layui-input-inline" style="width:50px;">
	    <input type="checkbox" id="c_tps_en" name="c_tps_en"  lay-skin="switch"  lay-filter="c_tps_en" class="layui-input" <%=(c_tps_en ?"checked":"") %> >
	  </div>
    <div class="layui-input-inline" style="width:400px;">
    	<%
	for(String tp:UANodeOCTagsCxt.CONTAINER_NODE_TPS)
	{
		String tpt = UANodeOCTagsCxt.getContainerNodeTitle(tp) ;
		String disabled=c_tps_en?"":"disabled" ;
%><%=tpt %><input type="checkbox" id="c_tps_<%=tp %>" name="c_tps" value="<%=tp %>" lay-skin="primary"  <%=(c_tps.contains(tp) ?"checked":"") %> <%=disabled %>>
<%
	}
%>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>cont_n,props</w:g>:</label>
    <div class="layui-form-mid"><w:g>en_filter</w:g>:</div>
	  <div class="layui-input-inline" style="width:50px;">
	    <input type="checkbox" id="c_exts_en" name="c_exts_en"  lay-skin="switch"  lay-filter="c_exts_en" class="layui-input" <%=(c_exts_en?"checked":"") %>>
	  </div>
    <div class="layui-input-inline" style="width:400px;border:0px solid;">
<%
if(pds_cont != null)
{
	for(SubFilteredTree.PropDef pd:pds_cont)
	{
		String nn = pd.getPropName() ;
%><%=pd.getPropTitle() %><input type="checkbox" id="c_exts_<%=pd.getPropName() %>" name="c_exts" lay-skin="primary"  value="<%=nn %>" <%=(c_exts.contains(nn) ?"checked":"") %>><%
	}
}
%>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>tags,props</w:g>:</label>
    <div class="layui-form-mid"><w:g>en_filter</w:g>:</div>
    <div class="layui-input-inline" style="width:50px;">
    <input type="checkbox" id="tag_exts_en" name="tag_exts_en"  lay-skin="switch"  lay-filter="tag_exts_en" class="layui-input" <%=(tag_exts_en?"checked":"") %>>
    </div>
    <div class="layui-input-inline" style="width:400px;">
<%
if(pds_tag != null)
{
	for(SubFilteredTree.PropDef pd:pds_tag)
	{
		String nn = pd.getPropName() ;
%><%=pd.getPropTitle() %><input type="checkbox" id="tag_exts_<%=pd.getPropName() %>" name="tag_exts" value="<%=nn %>" lay-skin="primary" <%=(tag_exts.contains(nn) ?"checked":"") %>><%
	}
}
%>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>output</w:g>:</label>
    <div class="layui-input-inline" style="width:150px;">
    <input type="checkbox" id="_b_flat_out" name="_b_flat_out"  lay-skin="primary"  lay-filter="_b_flat_out" class="layui-input" >List out
    </div>
  </div>
<script>

var prjid = "<%=prjid%>" ;

function on_after_pm_show(form)
{
	form.on('switch(c_tps_en)', function (data) {
		  set_chk_all("c_tps",data.elem.checked);
		});
	  form.on('switch(c_exts_en)', function (data) {
		  set_chk_all("c_exts",data.elem.checked);
		});
	  form.on('switch(tag_exts_en)', function (data) {
		  set_chk_all("tag_exts",data.elem.checked);
		});
}

function set_chk_all(name,b_en)
{
	$("input[name="+name+"]").each( function () {
	       let ob = $(this) ;
	       if(b_en)
	       {
	    	   ob.prop("disabled",false);
	    	   ob.prop("checked",true);
	       }
	       else
	       {
	    	   ob.prop("disabled",true);
	    	   ob.prop("checked",false);
	       }
	       form.render();
	});
}

function get_chk_vals(name)
{
	let ret=[] ;
	$("input[name="+name+"]").each( function () {
	       let ob = $(this) ;
	       if(ob.prop("checked"))
	    	   ret.push(ob.val()) ;
	   });
	return ret ;
}


function sel_sub_root(ele)
{
	let oldv = $(ele).val() ;
	dlg.open("../util/prj_tree_node_sel.jsp?prjid="+prjid,{title:"<wbt:g>select,move,tar</wbt:g>",w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>close</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.get_select((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret);
							return ;
						}
						
						//dlg.msg(ret) ;
						 $(ele).val(ret) ;
						 dlg.close() ;
					});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}



function set_chk_vals(name,vals)
{
	if(!vals) vals=[];
	$("input[name="+name+"]").each( function () {
	       let ob = $(this) ;
	       let v = ob.val();
	       ob.prop("checked",vals.indexOf(v)>=0);
	   });
}

function get_pm_jo()
{
	let sub_root_path = $("#sub_root_path").val() ;
	let tag_inc_sys = $("#tag_inc_sys").prop("checked") ;
	let c_tps_en = $("#c_tps_en").prop("checked") ;
	let c_tps = get_chk_vals("c_tps") ;
	let c_exts_en = $("#c_exts_en").prop("checked") ;
	let c_exts = get_chk_vals("c_exts") ;
	let tag_exts_en = $("#tag_exts_en").prop("checked") ;
	let tag_exts = get_chk_vals("tag_exts") ;
	let _b_flat_out = $("#_b_flat_out").prop("checked");
	return {sub_root_path:sub_root_path,c_tps_en:c_tps_en,c_tps:c_tps,c_exts_en:c_exts_en,c_exts:c_exts,
			tag_exts_en:tag_exts_en,tag_exts:tag_exts,tag_inc_sys:tag_inc_sys,_b_flat_out:_b_flat_out} ;
}



function set_pm_jo(jo)
{
	$("#sub_root_path").val(jo.sub_root_path||"") ; 
	$("#tag_inc_sys").prop("checked",jo.tag_inc_sys||false) ; 
	$("#c_tps_en").prop("checked",jo.c_tps_en||false) ;
	set_chk_vals("c_tps",jo.c_tps||[]) ;
	$("#c_exts_en").prop("checked",jo.c_exts_en||false) ; 
	set_chk_vals("c_exts",jo.c_exts||[]) ;
	$("#tag_exts_en").prop("checked",jo.tag_exts_en||false) ;
	set_chk_vals("tag_exts",jo.tag_exts||[]) ;
	
	$("#_b_flat_out").prop("checked",jo._b_flat_out||false) ;
}

function get_pm_size()
{
	return {w:750,h:400} ;
}

//on_init_pm_ok() ;
</script>