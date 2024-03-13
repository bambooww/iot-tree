<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.store.record.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%!

	 %><%
	if(!Convert.checkReqEmpty(request, out,"prjid", "tag"))
		return ;
	 String prjid = request.getParameter("prjid") ;
	 String tagp = request.getParameter("tag") ;
	 
	 UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	 if(prj==null)
	 {
		 out.print("no prj found") ;
		 return ;
	 }
	 
	 UATag tag = (UATag)prj.getDescendantNodeByPath(tagp) ;
	 if(tag==null)
	 {
		 out.print("no tag found in prj") ;
		 return ;
	 }
	 String tagid = tag.getId() ;
	 RecManager recmgr = RecManager.getInstance(prj) ;
	 if(!recmgr.checkTagCanRecord(tag))
	 {
		 out.print("the tag is cannot be recorded") ;
		 return ;
	 }
	
	 RecTagParam tagparam = recmgr.getRecTagParam(tag) ;
	 long gather_intv = 1000 ;
	 int keep_days = -1;
	 String chked = "checked" ;
	 long min_rec_intv = -1 ;
	 RecValStyle val_style = RecValStyle.successive_normal ;
	 if(tag.getValTp()==UAVal.ValTP.vt_bool)
		 val_style = RecValStyle.discrete ;
	 
	 JSONArray usingproids = new JSONArray() ;
	 if(tagparam!=null)
	 {
		 gather_intv = tagparam.getGatherIntv() ;
		 val_style = tagparam.getValStyle() ;
		 keep_days = tagparam.getKeepDays() ;
		 min_rec_intv = tagparam.getMinRecordIntv() ;
		 if(!tagparam.isEnable())
				chked = "" ;
		 List<String> ids = tagparam.getUsingProIds() ;
		 if(ids!=null)
			 usingproids = new JSONArray(ids) ;
	 }
	 int val_style_i = val_style.getVal() ;
	 
%>
<html>
<head>
<title>Tag Record Param Editor </title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(690,500);
</script>
<style type="text/css">
.layui-form-label
{
	width:120px;
}

.layui-form-checkbox i
{
border-style:solid solid solid solid;
border-left: 1px;
border-color:blue;
}

.prompt
{
	border:1px solid;
	margin:10px;
}
.pro_list li
{
  border:1px solid;
  width:100%;
  height:40px;
  margin-bottom:5px;
  position: relative;
  border-color: #e6e6e6;
}

.t
{
	position:absolute;
	font-size: 18px;
	top:2px;
	left:45px;
}

.f
{
	position:absolute;
	font-size: 15px;
	top:20px;
	left:55px;
	color:#00988b;
	cursor:pointer;
	bottom:7px;
}

.psel
{
  position:absolute;
	font-size: 15px;
	width:35px;
	top:2px;
	left:10px;
}

.enable_c
{
	font-size: 15px;
}

</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
 <div class="layui-form-item"  id="dt_tp">
    <label class="layui-form-label">Value Style</label>
    <div class="layui-input-inline" style="width: 210px;">
      <select  id="val_style"  name="val_style"  class="layui-input" placeholder="" lay-filter="val_style">
<%
boolean bfirst = true ;
for(RecValStyle tp:RecValStyle.values())
{
	int tpv = tp.getVal() ;
	 %><option value="<%=tpv%>"><%=tp.getTitle() %></option><%
}
%>
      </select>
    </div>
    <div class="layui-form-mid">Enable</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="en" name="en"  title="Enable or Not" lay-skin="switch" <%=chked %>> 
	  </div>
</div>
<div class="layui-form-item">
    <label class="layui-form-label">Gather Interval MS</label>
		<div class="layui-input-inline" style="width: 100px;">
	    <input type="number" id="gather_intv" name="gather_intv" class="layui-input" value=""/>
	  </div>
	  <div class="layui-form-mid">Keep Days</div>
	  <div class="layui-input-inline" style="width: 80px;">
	    <input type="number" id="keep_days" name="keep_days"  class="layui-input"  title=""  value=""> 
	  </div>
	   <div class="layui-form-mid">Min Record Interval</div>
	  <div class="layui-input-inline" style="width: 80px;">
	    <input type="number" id="min_rec_intv" name="min_rec_intv"  class="layui-input"  title=""  value=""> 
	  </div>
	  
</div>
<div class="layui-form-item">
    <label class="layui-form-label">Process</label>
	<div class="layui-input-inline" style="width: 350px;">
	    <ul class="pro_list" id="pro_list">

	     <li ></li>
	    </ul>
  </div>
</div>

  <div class="layui-card" id="card_scaling" >

  
   
</div>

 </form>
</body>
<script type="text/javascript">
var form ;
var ow =dlg.get_opener_w();
var dd = dlg.get_opener_opt("dd");
var prjid = "<%=prjid%>" ;
var tag = "<%=tagp%>" ;
var tagid="<%=tagid%>" ;
var gather_intv = <%=gather_intv%> ;
var val_style = <%=val_style_i%> ;
var keep_days= <%=keep_days%>;
var min_rec_intv = <%=min_rec_intv%>;
var usingproids = <%=usingproids.toString()%>;

layui.use('form', function(){
	  form = layui.form;
	  
	  form.on('select(val_style)', function (data) {
		　//console.log(data);　
		　//var n = data.value;
		
			update_prolist();
		});

	  $("#val_style").val(val_style) ;
	  $("#gather_intv").val(gather_intv);
	  $("#keep_days").val(keep_days) ;
	  $("#min_rec_intv").val(min_rec_intv) ;
	  
	  update_prolist() ;
	  form.render();
});

function update_prolist()
{
	let val_style = $("#val_style").val() ;
	send_ajax("./rec_tag_param_ajax.jsp",{op:"list_pros",prjid:prjid,tag:tag,val_style:val_style},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			msg.dlg(ret) ;
			return ;
		}
		let pros = null;
		eval("pros="+ret) ;
		let tmps="" ;
		for(let pro of pros)
		{
			tmps += trans_pro2html(pro);
		}
		$("#pro_list").html(tmps) ;
		form.render();
	}) ;
}

function trans_pro2html(ob)
{
	let en_c = ob.en?"green":"gray" ;
	let en_t = ob.en?"Enabled":"Disabled" ;
	let id = ob.id ;
	let bsel = usingproids.indexOf(id)>=0 ;
	let chked = bsel?"checked":"" ;
	return `<li>
	 <span class="psel"><input proid="\${id}" class="pro_chk" type="checkbox" \${chked} lay-skin="primary" /></span>
	<span class="t"><span class="enable_c"><i class="fa fa-square en" style="color:\${en_c}" title="\${en_t}"></i></span>\${ob.t} [\${ob.n}]</span>
	<span class="f" ><i class="fa fa-gear" style="font-size:16px;"></i>\${ob.tpt}</span>
	</li>`;
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
	var valsty = $("#val_style").val() ;
	let ret = {val_style:valsty} ;
	ret.tagid=tagid ;
	ret.en = $("#en").prop("checked") ;
    ret.gather_intv = get_input_val("gather_intv",-1,true) ;
    ret.keep_days = get_input_val("keep_days",-1,true) ;
    ret.min_rec_intv = get_input_val("min_rec_intv",-1,true) ;
    
    let proids = [] ;
    $(".pro_chk").each(function(){
    	let ob = $(this) ;
    	if(ob.prop("checked"))
    		proids.push(ob.attr("proid")) ;
    });
    ret.using_pros=proids ;
	cb(true,{jstr:JSON.stringify(ret),en:ret.en});
	return ;
}

</script>
</html>