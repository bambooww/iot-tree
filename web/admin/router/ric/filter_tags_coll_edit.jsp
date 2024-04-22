<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.node.*,
				org.iottree.core.filter.*,
				org.iottree.core.router.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%
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

RouterManager rmgr = RouterManager.getInstance(prj) ;
RICFilterTags ric = null ;
if(Convert.isNotNullEmpty(id))
{
	ric = (RICFilterTags)rmgr.getInnerCollatorById(id) ;
	if(ric==null)
	{
		out.print("no RICSelTags found with id="+id) ;
		return ;
	}
}
else
{
	ric = new RICFilterTags(rmgr) ;
}

boolean benable = ric.isEnable();
String chk_en = "" ;
if(benable)
	chk_en = "checked" ;

String name =ric.getName() ;
String title = ric.getTitle() ;
String desc = ric.getDesc() ;
long out_intv = ric.getOutIntervalMS() ;
//List<UATag> out_tags = null;//ric.getRTOutTags() ;
//String out_tagtxt = "" ;
//SONArray out_tagids = new JSONArray() ;
SubFilteredTree sft = ric.getSubFilteredTree() ;
if(sft==null)
	sft = new SubFilteredTree(prj) ;

String sub_root_path = sft.getSubRootPath() ;

boolean c_tps_en = sft.isContainerTPSetEn() ;
HashSet<String> c_tps = sft.getContainerTPSet() ;
boolean c_exts_en = sft.isContainerExtSetEn() ;
HashSet<String> c_exts = sft.getContainerExtSet() ;
boolean tag_inc_sys = sft.isTagIncSys() ;
boolean tag_exts_en = sft.isTagExtSetEn() ;
HashSet<String> tag_exts = sft.getTagExtSet() ;

JSONObject js_ob = ric.toJO() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(750,600);
</script>
<style>
.layui-form-label
{
	width:120px;
}
.conf
{
	position: relative;
	width:90%;
	height:30px;
	border:1px solid ;
}

.conf .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:7px;
}

.fclist
{
border:1px solid;border-color:#d2d2d2;overflow: auto;height:150px;
}
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>name</w:g>:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off"  class="layui-input" <%=Convert.isNotNullEmpty(name)?"readonly":"" %>>
    </div>
    <div class="layui-form-mid"><w:g>title</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><w:g>enable</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="en" name="en" <%=chk_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
 </div>
 <div class="layui-form-item">
  	<label class="layui-form-label"><w:g>out,style</w:g>:</label>
    <div class="layui-input-inline" style="width: 250px;">
      <select id="out_sty" name="out_sty" lay-filter="out_sty">
<%
	for(RouterInnCollator.OutStyle outs:RouterInnCollator.OutStyle.values())
	{
		int v = outs.getInt() ;
%><option value="<%=v%>"><%=outs.getTitle() %></option>
<%
	}
%>
      </select>
    </div>
    <div class="layui-form-mid"><w:g>out,interval</w:g>:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="out_intv" name="out_intv" value="<%=out_intv%>"  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid">(MS)</div>
	  <div class="layui-input-inline" style="width: 150px;">
	  </div>
	  
 </div>
   <div class="layui-form-item">
    <label class="layui-form-label"><w:g>rootp</w:g>:</label>
    <div class="layui-input-inline " id="filter_list" style="width:250px;">
    	<input type="text" id="sub_root_path" name="sub_root_path" value="<%=sub_root_path %>" readonly  autocomplete="off" class="layui-input" onclick="sel_sub_root(this)">
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
    <label class="layui-form-label"><w:g>desc</w:g></label>
    <div class="layui-input-inline" style="width:350px;">
      <textarea  style="width:100%;height:50px;" id="desc" class="layui-input"><%=desc %></textarea>
    </div>
  </div>
</form>
</body>
<script type="text/javascript">

var form ;
var prjid="<%=prjid%>";
var prj_path = "<%=prj.getNodePath()%>" ;
var js_ob = <%=js_ob%> ;

var cur_prod_ob = null ;

layui.use('form', function(){
	  form = layui.form;
	  form.on('switch(c_tps_en)', function (data) {
		  set_chk_all("c_tps",data.elem.checked);
		});
	  form.on('switch(c_exts_en)', function (data) {
		  set_chk_all("c_exts",data.elem.checked);
		});
	  form.on('switch(tag_exts_en)', function (data) {
		  set_chk_all("tag_exts",data.elem.checked);
		});
	  form.render();
});

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

function win_close()
{
	dlg.close(0);
}

function sel_sub_root(ele)
{
	let oldv = $(ele).val() ;
	dlg.open("../../util/prj_tree_node_sel.jsp?prjid="+prjid,{title:"<wbt:g>select,move,tar</wbt:g>",w:'500px',h:'400px'},
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

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
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

function do_submit(cb)
{
	let n = $("#name").val() ;
	if(!n)
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	
	var ben = $("#en").prop("checked") ;
	let t =  $("#title").val() ;
	if(!t) t = "" ;
	let d = $("#desc").val() ;
	let out_intv = get_input_val("out_intv",30000,true);
	
	let sub_root_path = $("#sub_root_path").val() ;
	let tag_inc_sys = $("#tag_inc_sys").prop("checked") ;
	let c_tps_en = $("#c_tps_en").prop("checked") ;
	let c_tps = get_chk_vals("c_tps") ;
	let c_exts_en = $("#c_exts_en").prop("checked") ;
	let c_exts = get_chk_vals("c_exts") ;
	let tag_exts_en = $("#tag_exts_en").prop("checked") ;
	let tag_exts = get_chk_vals("tag_exts") ;
	let sub_f_t={sub_root_path:sub_root_path,c_tps_en:c_tps_en,c_tps:c_tps,c_exts_en:c_exts_en,c_exts:c_exts,
			tag_exts_en:tag_exts_en,tag_exts:tag_exts,tag_inc_sys:tag_inc_sys} ;
	let pm={n:n,t:t,d:d,en:ben,out_intv:out_intv,sub_f_t:sub_f_t} ;
	cb(true,pm) ;
}

</script>
</html>