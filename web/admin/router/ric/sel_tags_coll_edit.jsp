<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.node.*,
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

RouterManager rmgr = RouterManager.getInstance(prj) ;
RICSelTags ric = null ;
if(Convert.isNotNullEmpty(id))
{
	ric = (RICSelTags)rmgr.getInnerCollatorById(id) ;
	if(ric==null)
	{
		out.print("no RICSelTags found with id="+id) ;
		return ;
	}
}
else
{
	ric = new RICSelTags(rmgr) ;
}

boolean benable = ric.isEnable();
String chk_en = "" ;
if(benable)
	chk_en = "checked" ;

String name =ric.getName() ;
String title = ric.getTitle() ;
String desc = ric.getDesc() ;
long out_intv = ric.getOutIntervalMS() ;
RICSelTags.OutStyle os = ric.getOutStyle() ;
List<UATag> out_tags = ric.getRTOutTags() ;
String out_tagtxt = "" ;
JSONArray out_tagids = new JSONArray() ;
if(out_tags!=null)
{
	for(UATag tag:out_tags)
	{
		out_tagids.put(tag.getId()) ;
		out_tagtxt += tag.getNodePath() +"\r\n";
	}
}
List<UATag> in_tags = ric.getRTInWriteTags() ;
String in_tagtxt = "" ;
JSONArray in_tagids = new JSONArray() ;
if(in_tagids!=null)
{
	for(UATag tag:in_tags)
	{
		in_tagids.put(tag.getId()) ;
		in_tagtxt += tag.getNodeCxtPathIn(prj) +"\r\n";
	}
}
 
JSONObject js_ob = ric.toJO() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(700,500);
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
    <label class="layui-form-label"><w:g>read,tags</w:g></label>
    <div class="layui-input-inline"  style="overflow: auto;width:350px;">
      <textarea  style="width:100%;height:50px;" id="r_tags" readonly="readonly" onclick="sel_tags('r')" class="layui-input"><%=out_tagtxt %></textarea>
    </div>
    <div class="layui-form-mid"><button onclick="">...</button></div>
  </div>
  
  <div class="layui-form-item">
  	<label class="layui-form-label"><w:g>out,style</w:g>:</label>
    <div class="layui-input-inline" style="width: 250px;">
      <select id="out_sty" name="out_sty" lay-filter="out_sty">
<%
	for(RouterInnCollator.OutStyle outs:RouterInnCollator.OutStyle.values())
	{
		int v = outs.getInt() ;
		String chk = "" ;
		if(os!=null&&os.getInt()==v)
			chk="selected";
%><option value="<%=v%>" <%=chk %>><%=outs.getTitle() %></option>
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
    <label class="layui-form-label"><w:g>write,tags</w:g></label>
    <div class="layui-input-inline" style="overflow: auto;width:350px;">
      <textarea style="width:100%;height:50px;" id="w_tags"  readonly="readonly" onclick="sel_tags('w')" class="layui-input"><%=in_tagtxt %></textarea>
    </div>
    <div class="layui-form-mid"><button onclick="sel_tags('w')">...</button></div>
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

var prjid="<%=prjid%>";
var prj_path = "<%=prj.getNodePath()%>" ;
	var js_ob = <%=js_ob%> ;

var in_tagids = <%=in_tagids%>;
var out_tagids = <%=out_tagids%>;

var cur_prod_ob = null ;

layui.use('form', function(){
	  var form = layui.form;
	  
	  
	  form.render();
	});

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
	
	dlg.open("../../ua_cxt/cxt_tag_selector.jsp?w_only="+w_only+"&multi=true&path="+prj_path,//+"&val="+tmpv,
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

function add_or_edit_prod(id)
{
	
	cur_prod_ob = get_send_conf(id) ;
	console.log(id,cur_prod_ob) ;
	let tt = '<wbt:g>add</wbt:g> Producer Parameter';
	if(cur_prod_ob)
		tt = '<wbt:g>edit</wbt:g> Producer Parameter';
	dlg.open("kafka_adp_prod_edit.jsp",
			{title:tt,pm:cur_prod_ob},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 if(!js_ob.send_confs)
							 js_ob.send_confs = [] ;
						 js_ob.send_confs.push(ret) ;
						 update_ui();
						 dlg.close() ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
	
function win_close()
{
	dlg.close(0);
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

function do_submit(cb)
{
	let n = $("#name").val() ;
	if(!n)
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	if(in_tagids.length==0 && out_tagids.length==0)
	{
		cb(false,'<w:g>read,write,tags,cannot_empty</w:g>') ;
		return ;
	}
	var ben = $("#en").prop("checked") ;
	let t =  $("#title").val() ;
	if(!t) t = "" ;
	let d = $("#desc").val() ;
	let out_sty = $("#out_sty").val() ;
	let out_intv = get_input_val("out_intv",30000,true) ;
	let pm={n:n,t:t,d:d,out_tagids:out_tagids,in_tagids:in_tagids,en:ben,out_sty:out_sty,out_intv:out_intv} ;
	cb(true,pm) ;
}

</script>
</html>