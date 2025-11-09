<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.json.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
	private static List<String> getRecentUnit(HttpServletRequest req)
	{
		HttpSession hs = req.getSession() ;
		ArrayList<String> units=  (ArrayList<String>)hs.getAttribute("recent_units");
		if(units==null)
		{
			return Arrays.asList();
		}
		return units ;
	}
	
	public static String html_str(Object o)
	{
		if(o==null)
			return "" ;
		return Convert.plainToJsStr(""+o) ;
	}
	 %><%
	 boolean b_batch = "true".equals(request.getParameter("batch")) ;
	 if(!b_batch)
	 {
		 if(!Convert.checkReqEmpty(request, out, "path"))
				return ;
	 }
	boolean bmid = "true".equalsIgnoreCase(request.getParameter("mid")) ;
	//boolean blocal= "true".equalsIgnoreCase(request.getParameter("local")) ;
	String path = request.getParameter("path") ;
	String id = request.getParameter("id") ;
	UATag tag = null ;
	String name= "" ;
	String title = "" ;
	String addr = "" ;
	boolean blocal=false;
	String local_defval = "" ;
	boolean local_autosave = false;
	UAVal.ValTP valtp = null ;
	String valtp_str = "" ;
	long srate = 200;
	int dec_digits = -1 ;
	String unit="" ;
	String indicator = "" ;
	String canw = "";
	String desc = "" ;
	String trans = null ;
	boolean b_val_filter=false;
	String min_val_str="" ;
	String max_val_str="" ;
	//String alert_low="" ;
	//String alert_high="" ;
	String alerts = null ;
	String mid_w_js = "" ;
	
	if(id==null)
		id = "" ;
	
	UAVal.ValTP[] vtps = null;
	UANodeOCTags n = null;
	if(Convert.isNotNullEmpty(path))
	{
		UANode tmpn = UAUtil.findNodeByPath(path);
		if(tmpn instanceof UAHmi)
		{
			tmpn = tmpn.getParentNode();
			path = tmpn.getNodePath() ;
		}
		n = (UANodeOCTags)tmpn;
		if(n==null)
		{
			out.print("no node with path="+path) ;
			return ;
		}
		
		UADev dev = n.getBelongToDev() ;
		UACh ch = n.getBelongToCh() ;
		
		if(ch!=null)
		{
			DevDriver dd = ch.getDriver() ;
			if(dd!=null)
			{
				vtps = dd.getLimitValTPs(dev);
			}
		}
	}
	if(vtps==null)
		vtps = UAVal.ValTP.values() ;
	
	if(n!=null && Convert.isNotNullEmpty(id))
	{
 		tag = n.getTagById(id) ;
 		if(tag==null)
 		{
 			out.print("no edit tag found") ;
 			return ;
 		}
	}
	
	if(tag!=null)
	{
 		name = tag.getName() ;
 		title = tag.getTitle() ;
 		desc = tag.getDesc() ;
 		bmid = tag.isMidExpress();
 		addr = tag.getAddress() ;
 		blocal = tag.isLocalTag() ;
 		local_defval = tag.getLocalDefaultVal() ;
 		local_autosave = tag.isLocalAutoSave() ;
 		valtp = tag.getValTpRaw() ;
 		if(valtp!=null)
 			valtp_str = ""+valtp.getInt() ;
 		dec_digits = tag.getDecDigits() ;
 		srate = tag.getScanRate() ;
 		canw = ""+tag.isCanWrite();
 		unit = tag.getUnit() ;
 		indicator = tag.getIndicator() ;
 		trans = tag.getValTranser() ;
 		if(Convert.isNullOrEmpty(trans))
 			trans = null ;
 		b_val_filter = tag.isValFilter() ;
 		min_val_str = tag.getMinValStr() ;
 		max_val_str = tag.getMaxValStr() ;
 		mid_w_js = tag.getMidWriterJS() ;
 		//alert_low = tag.getAlertLowValStr() ;
 		//alert_high = tag.getAlertHighValStr() ;
 		JSONArray jarr = tag.getValAlertsJArr() ;
 		if(jarr!=null)
 		{
 			alerts = jarr.toString();
 		}
	}
%><html>
<head>
<title>Tag Editor </title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>

<style type="text/css">
.btn-group button {
    left:10px;
    padding: 10px 24px;
    cursor: pointer;
    width: 90%; 
    display: block; 
}
.alert_item
{
	position: relative;
	border:1px solid;
	height:30px;
	min-width:70px;
	margin-left:5px;
	width:fit-content;
	float: left;
}

.alert_item .oper
{
left:5px;
	color: red;
}

.alert_item .tt
{
top:5px;
	cursor:pointer;
}
.batch {color:green;}
.recent_p {}
.recent_p .item {margin:1px;border:1px solid blue;font-size:11px;cursor: pointer;}
.recent_p .item:hover {background-color: grey;}
</style>
<script>
dlg.resize_to(1050,600);
</script>

</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
 <table style="width:100%	">
   <tr>
     <td width="90%">
	<input type="hidden" id="id" name="name" value="<%=html_str(id)%>">
	  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>name</wbt:g>:</label>
    <div class="layui-input-inline" style="width: 200px;">
      <input type="text" id="name" name="name" lay-verify="required" <%=(b_batch?"readonly":"") %> autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>title</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 300px;">
	    <input type="text" id="title" name="title" lay-verify="required" <%=(b_batch?"readonly":"") %> size="0" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid <%=(b_batch?"batch":"")%>"><wbt:g>data,type</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 100px;">
      <select  id="vt"  name="vt"  class="layui-input" lay-filter="vt" >
        <option value="">---</option>
<%
for(UAVal.ValTP vt:vtps)
{
	boolean bnum = vt.isNumberVT() ;
	 %><option value="<%=vt.getInt()%>" b_num="<%=bnum%>"><%=vt.getStr() %></option><%
}
%>
      </select>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label <%=(b_batch?"batch":"")%>"><wbt:g>dec_digit</wbt:g>:</label>
    <div class="layui-input-inline" style="width: 50px;">
      <input type="text" id="dec_digits" name="dec_digits" class="layui-input">
    </div>
    <div class="layui-form-mid <%=(b_batch?"batch":"")%>"><wbt:g>r_or_w</wbt:g>:</div>
    <div class="layui-input-inline" style="width: 120px;">
      <select id="canw"  name="canw" class="layui-input" lay-filter="canw">
        <option value="">---</option>
        <option value="false"><wbt:g>r</wbt:g></option>
        <option value="true"><wbt:g>rw</wbt:g></option>
      </select>
    </div>
    
    <div class="layui-form-mid <%=(b_batch?"batch":"")%>"><wbt:g>indicator</wbt:g>:</div>
    <div class="layui-input-inline" style="width:180px;">
      <select id="indicator"  name="indicator" class="layui-input" lay-filter="indicator">
        <option value="">---</option>
<%
	for(ValIndicator vi:ValIndicator.values())
	{
		List<ValUnit> vus = vi.getUnits() ;
		String vusstr = "";
		if(vus!=null && vus.size()>0)
		{
			StringBuilder vussb = new StringBuilder() ;
			boolean bfirst = true;
			for(ValUnit vu:vus)
			{
				if(bfirst) bfirst=false;
				else vussb.append(",") ;
				vussb.append(vu.name()) ;
			}
			vusstr = vussb.toString() ;
		}
%>
        <option value="<%=vi.name()%>" units="<%=vusstr%>"><%=vi.getTitle() %></option>
<%
	}
%>
      </select>
    </div>
    <div class="layui-form-mid <%=(b_batch?"batch":"")%>"><wbt:g>unit</wbt:g>:</div>
    <div class="layui-input-inline" style="width: 180px;">
      <select id="unit"  name="unit" class="layui-input" lay-filter="unit">
      </select>
      <span style="display:none" >
      <select id="unit_hid"  name="unit_hid" lay-ignore>
        <option value="">---</option>
<%
	for(ValUnit vu:ValUnit.values())
	{
%>
        <option value="<%=vu.name()%>">[<%=vu.getUnit() %>] <%=vu.getTitle() %></option>
<%
	}
%>
      </select>
      </span>
      
    </div>
    <div class="layui-form-mid"><button style="border:1px solid #ccc;width:25px;height:25px;" title="<wbt:g>recent_units</wbt:g>" onclick="sel_recent_unit()">...</button>
    	<div id="recent_unit_p"  class="recent_p" style="position:absolute;display:none;border:1px solid green;min-width:180px;min-height:80px;background-color: #fff;">
    	</div>
    </div>
  </div>
<%

String loc_set="display:none" ;
	String loc_autosave_chk="" ;
	String loc_chked = "" ;
	String b_val_filter_chked="" ;
	if(blocal)
	{
		loc_chked= "checked=checked" ;
		loc_set="" ;
	}
	if(local_autosave)
		loc_autosave_chk = "checked=checked" ;
	if(b_val_filter)
		b_val_filter_chked= "checked=checked" ;

if(!bmid)
{
%>
    <div class="layui-form-item" id="fi_local">
    <div class="layui-form-label"><wbt:g>local</wbt:g></div>
	  <div class="layui-input-inline" style="width: 100px;" title="<wbt:g>local_ptt</wbt:g>">
	   <input type="checkbox" id="local" name="local" <%=loc_chked%> lay-skin="switch" <%=(b_batch?"readonly disabled":"") %>  lay-filter="local" class="layui-input">
	  </div>
	  <div id="local_setting" style="<%=loc_set%>">
    <label class="layui-form-mid"><wbt:g>default_val</wbt:g></label>
    <div class="layui-input-inline">
      <input type="text"  id="local_defval"  name="local_defval"  class="layui-input" style="width: 150px;">
    </div>
    <div class="layui-form-mid"><wbt:g>auto_save</wbt:g></div>
	  <div class="layui-input-inline" style="width: 80px;">
	   <input type="checkbox" id="local_autosave" name="local_autosave" <%=loc_autosave_chk%> lay-skin="switch"  lay-filter="local_autosave" class="layui-input">
	  </div>
	</div>
  </div>
<%
}
%>
    <div class="layui-form-item" id="addr_setting">
    <label class="layui-form-label"><wbt:g><%=(bmid?"js_exp":"addr") %></wbt:g>:</label>
    <div class="layui-input-inline" style="width:400px">
<%
if(bmid)
{
%><textarea style="width:100%;height:100px;overflow:auto;white-space: nowrap;font-size: 12px"  id="addr"  name="addr"   class="layui-input" ondblclick="on_js_edit()" title="<wbt:g>dbclk_open_jse</wbt:g>"></textarea>
<%
}
else
{
%>
<input type="text"  id="addr"  name="addr" autocomplete="off" <%=(b_batch?"readonly":"") %> class="layui-input" value="<%=addr%>"/>
<%
}
%>      
    </div>
    <div class="layui-input-inline" >
<%
if(!bmid)
{
%>
    	<button class="layui-btn layui-btn-primary" title="Check Address" onclick="chk_addr()" <%=(b_batch?"readonly disabled":"") %>><i class="fa-solid fa-check"></i></button>
    	<button class="layui-btn layui-btn-primary" title="Address Help" onclick="help_addr()" <%=(b_batch?"readonly disabled":"") %>><i class="fa-solid fa-question"></i></button>
<%
}
%>
    </div>
  </div>
<%
if(bmid)
{
%>
  <div class="layui-form-item" id="w_js_setting">
    <label class="layui-form-label"><wbt:g>exp_wjs</wbt:g>:</label>
    <div class="layui-input-inline" style="width:800px;color:#c1c1c1;font-size: 12px;">
    ($tag,$input)=>{
	<textarea style="width:100%;height:100px;overflow:auto;white-space: nowrap;"  id="mid_w_js"  name="mid_w_js"   class="layui-input" ondblclick="on_wjs_edit()" title="<wbt:g>dbclk_open_jse</wbt:g>"></textarea>
	}
    </div>
    <div class="layui-input-inline" >

    </div>
  </div>
<%
}
%>
  <%--
   <div class="layui-form-item">
    <label class="layui-form-label">Scan rate:</label>
    <div class="layui-input-block">
      <input type="text" id="srate" name="srate" required  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
   --%>
    
<%
if(!bmid)
{
%>
  <div class="layui-form-item" id="transfer_setting">
    <label class="layui-form-label <%=(b_batch?"batch":"")%>"><wbt:g>transfer</wbt:g></label>
    <div class="layui-input-inline" style="width:370px">
      <input id="transfer_s" name="transfer_s" class="layui-input" readonly="readonly" onclick="edit_trans()"/>
    </div>
    <div class="layui-form-mid"><button style="border:1px solid #ccc;width:25px;height:25px;" title="<wbt:g>recent_trans</wbt:g>" onclick="sel_recent_trans()">...</button>
    	<div id="recent_trans_p"  class="recent_p" style="position:absolute;display:none;border:1px solid green;min-width:320px;min-height:80px;background-color: #fff;">
    	</div>
    </div>
  </div>
  
  <div class="layui-form-item" id="val_filter_setting">
    <label class="layui-form-label"><wbt:g>filter</wbt:g></label>
    <div class="layui-input-block" style="width:370px">
      <input type="checkbox" id="b_val_filter" name="b_val_filter" <%=(b_batch?"readonly disabled":"") %> <%=b_val_filter_chked%> lay-skin="primary"  lay-filter="b_val_filter" class="layui-input" title="<wbt:g>en_anti_interf</wbt:g>">
      
    </div>
  </div>
<%
}
%>
  <div class="layui-form-item" id="max_min_val" style="display:none;">
    <label class="layui-form-label"><wbt:g>min,val</wbt:g></label>
    <div class="layui-input-inline" style="width: 80px;">
     <input type="number" id="min_val_str" name="min_val_str" class="layui-input">
    </div>
     
    <div class="layui-form-mid"><wbt:g>max,val</wbt:g></div>
    <div class="layui-input-inline" style="width: 80px;">
      <input type="number" id="max_val_str" name="max_val_str" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>evt</wbt:g>/<wbt:g>alert</wbt:g></label>
    <div class="layui-input-inline"  style="width:500px;">
      <div id="alert_list" style="width:100%;white-space: nowrap;"></div>
    </div>
    <div class="layui-input-inline"  style="width:50px;">
    <button class="layui-btn layui-btn-primary" title="Add Alert Source" onclick="edit_alert()" <%=(b_batch?"readonly disabled":"") %>><i class="fa-solid fa-plus"></i></button>
    </div>
  </div>
<%
if(!b_batch)
{
%>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>desc</wbt:g>:</label>
    <div class="layui-input-block">
      <input type="text"  id="desc"  name="desc"  lay-verify="required" <%=(b_batch?"readonly":"") %> autocomplete="off" class="layui-input">
    </div>
  </div>
<%
}
else
{
%>
  <div class="layui-form-item">
    <label class="layui-form-label batch"><wbt:g>batch,clear</wbt:g>:</label>
    <div class="layui-input-block" id="batch_clear_p">
<%--
      <input type="checkbox"  class="batch_clear"  batch_clear="vt" class="layui-input" lay-skin="primary" title="<wbt:g>clear,data,type</wbt:g>"/>
 --%>
      <input type="checkbox"  class="batch_clear" batch_clear="dec"   class="layui-input" lay-skin="primary" title="<wbt:g>clear,dec_digit</wbt:g>"/>
      <input type="checkbox"  class="batch_clear"  batch_clear="rw"  class="layui-input" lay-skin="primary" title="<wbt:g>clear,r_or_w</wbt:g>"/>
      <input type="checkbox"  class="batch_clear" batch_clear="ind"   class="layui-input" lay-skin="primary" title="<wbt:g>clear,indicator</wbt:g>"/>
      <input type="checkbox"  class="batch_clear" batch_clear="unit"  class="layui-input" lay-skin="primary" title="<wbt:g>clear,unit</wbt:g>"/>
      <input type="checkbox"  class="batch_clear" batch_clear="trans"  class="layui-input" lay-skin="primary" title="<wbt:g>clear,transfer</wbt:g>"/>
    </div>
  </div>
<%
}
%>
  </td>
  <td>
 <div class="layui-btn-container">
    <button onclick="on_new_tag()"  class="layui-btn layui-btn-primary" style="width:90px;"><wbt:g>add</wbt:g></button>
    <button onclick="on_copy_tag()" class="layui-btn layui-btn-primary" style="width:90px;"><wbt:g>copy</wbt:g></button>
    <button  class="layui-btn layui-btn-primary" style="width:90px;"><wbt:g>del</wbt:g></button>
    </div>
    
  </td>
  </tr>
  </table>
 </form>
</body>
<script type="text/javascript">

var node_path="<%=path%>";
var tag_id = "<%=id%>"
var bmid = <%=bmid%>;

var name= "<%=html_str(name) %>" ;
var title = "<%=html_str(title)%>" ;
var desc = "<%=html_str(desc)%>";
var addr = "<%=html_str(addr)%>";
var vt = "<%=valtp_str%>" ;
var srate = "<%=srate%>";
var dec_digits = <%=dec_digits%> ;
var canw = "<%=canw%>";
var unit = "<%=unit%>" ;
var indicator = "<%=indicator%>" ;
var mid_w_js = "<%=html_str(mid_w_js)%>" ;
var trans_dd = <%=trans%>;
var bloc=<%=blocal%>
var loc_devf="<%=html_str(local_defval)%>" ;
var bloc_autosave = <%=local_autosave%> ;
var min_val_str = "<%=min_val_str%>";
var max_val_str = "<%=max_val_str%>";
var alerts_dd = <%=alerts%>;
if(!alerts_dd)
	alerts_dd=[];

function update_form()
{
	var bloc = $("#local").prop("checked") ;
	if(bloc)
	{
		$("#local_setting").css("display","") ;
		$("#addr_setting").css("display","none") ;
		$("#transfer_setting").css("display","none") ;
		$("#val_filter_setting").css("display","none") ;
		
	}
	else
	{
		$("#local_setting").css("display","none") ;
		$("#addr_setting").css("display","") ;
		$("#transfer_setting").css("display","") ;
		$("#val_filter_setting").css("display","") ;
	}
	
	if($("#vt").find("option:selected").attr("b_num")=="true")
		$("#max_min_val").css("display","") ;
	else
		$("#max_min_val").css("display","none") ;
	
	if(bmid)
	{
		//console.log(canw) ;
		$("#w_js_setting").css("display",(canw=='true')?"":"none");
	}
	
}

var form ;

layui.use('form', function(){
	  form = layui.form;
	  $("#name").val(name) ;
	  $("#title").val(title) ;
	  $("#addr").val(addr) ;
	  $("#mid_w_js").val(mid_w_js) ;
	  $("#desc").val(desc) ;
	  if(dec_digits>0)
	  	$("#dec_digits").val(dec_digits);
	  else
		$("#dec_digits").val("");
	  $("#vt").val(vt) ;
	  $("#srate").val(srate) ;
	  $("#canw").val(canw) ;
	  
	  $("#local_defval").val(loc_devf) ;
	  $("#min_val_str").val(min_val_str) ;
	  $("#max_val_str").val(max_val_str) ;
	  //$("#alert_low").val(alert_low) ;
	 // $("#alert_high").val(alert_high) ;
	  
	  form.on('switch(local)', function(obj){
		        var b = obj.elem.checked ;
		  update_form();
		  });
	  form.on('select(canw)', function(obj){
		  canw =$("#canw").val() ;
		  update_form();
		  });
	  form.on("select(vt)",function(obj){
		  //      var b = obj.elem.checked ;
		  update_form();
		  });
	  form.on("select(indicator)",function(obj){
		  //      var b = obj.elem.checked ;
		  update_indicator();
		  });
	  update_form();
	  
	  $("#indicator").val(indicator) ;
	  update_indicator();
	  $("#unit").val(unit) ;
	  
	  form.render();
	  
	  if(!tag_id)
		  $("#name").focus() ;
	  else
	  	$("#title").focus() ;
});
	
function win_close()
{
	dlg.close(0);
}

function sel_recent_unit()
{
	let tdds = read_units_dd() ;
	let tmps ="" ;
	for(let i = 0 ; i < tdds.length ; i ++)
	{
		let tdd = tdds[i] ;
		tmps += `<div class="item" onclick="set_recent_unit('\${tdd.u}')">\${tdd.t}</div>` ;
	}
	
    tmps += `<span class="item" onclick="set_recent_unit('')"><wbt:g>clear</wbt:g></span>
    	<span class="item" onclick='$("#recent_unit_p").css("display","none")'><wbt:g>cancel</wbt:g></span>`;
    $("#recent_unit_p").html(tmps) ;
	$("#recent_unit_p").css("display","") ;
}
function set_recent_unit(u)
{
	$("#unit").val(u) ;
	$("#recent_unit_p").css("display","none") ;
	form.render();
}

function sel_recent_trans()
{
	let tdds = read_trans_dd() ;
	let tmps ="" ;
	for(let i = 0 ; i < tdds.length ; i ++)
	{
		let tdd = tdds[i] ;
		tmps += `<div class="item" onclick="set_recent_trans(\${i})">\${tdd._tt}</div>` ;
	}
	
    tmps += `<span class="item" onclick="set_recent_trans(-1)"><wbt:g>clear</wbt:g></span>
    	<span class="item" onclick='$("#recent_trans_p").css("display","none")'><wbt:g>cancel</wbt:g></span>`;
    $("#recent_trans_p").html(tmps) ;
	$("#recent_trans_p").css("display","") ;
}
function set_recent_trans(idx)
{
	$("#recent_trans_p").css("display","none") ;
	if(idx<0)
	{
		trans_dd=null;
		update_transfer_s();
		return ;
	}
	let tdds = read_trans_dd() ;
	if(tdds.length<=idx) return ;
	
	trans_dd = tdds[idx] ;
	update_transfer_s();
}


function update_transfer_s()
{
	if(trans_dd==null||trans_dd._n=='none')
	{
		$("#transfer_s").val("") ;
		return ;
	}
	$("#transfer_s").val(trans_dd._t) ;
}

function update_indicator()
{
	let indoptele = $("#indicator").find("option:selected") ;
	let units = indoptele.attr("units") ;
	let uss = null ;
	if(units)
	{
		uss = units.split(",") ;
	}
	
	let unitele =$("#unit");
	let oldv = unitele.val() ;
	unitele.find('option').remove();
	
	let opts = $("#unit_hid").children('option') ;
	
	let first_unit_v = null ;
	for(let i = 0 ; i < opts.length; i ++)
	{
		let opt = $(opts[i]) ;
		if(i==0 || uss==null)
		{
			unitele.append(opt.clone()) ;
			continue ;
		}
		
		let v = opt.attr("value") ;
		if(uss.indexOf(v)<0)
			continue ;
		
		if(!first_unit_v)
			first_unit_v = v ;
		unitele.append(opt.clone()) ;
	}
	unitele.val(oldv) ;
	
	let ind_curv = $("#indicator").val() ;
	let unit_curv = unitele.val() ;
	if(ind_curv && !unit_curv && first_unit_v)
	{
		unitele.val(first_unit_v)
	}
	form.render();
}

function update_alert_s()
{
	let tmps = "" ;
	for(let i=0 ; i<alerts_dd.length ; i ++)
	{
		let d = alerts_dd[i];
		tmps += `<div id="alert_\${i}" class="alert_item" >
			<span onclick="edit_alert(\${i})" class="tt">L\${d.lvl} \${d.tpt} \${d.pm_tt}(\${d.prompt||""})</span><span class="oper">&nbsp;&nbsp;<i class="fa fa-times fa-lg" onclick="del_alert(\${i})"></i></span>
			</div>`;
	}
	
	$("#alert_list").html(tmps) ;
}

update_transfer_s();
update_alert_s();

function edit_trans()
{
	dlg.open("./tag_trans_edit.jsp",
			{title:"<wbt:g>val,transfer</wbt:g>",w:'600px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 trans_dd = ret ;
						 save_trans_dd(ret) ;
						 update_transfer_s();
						 dlg.close();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function read_trans_dd()
{
	return JSON.parse(localStorage.getItem('trans_list') || '[]') ;
}
function save_trans_dd(tdd)
{
	//console.log(tdd) ;
	let queue = read_trans_dd();
	
	let bsave = false;
	if(tdd._n=='calc')
	{
		tdd._tt = tdd._t ;
		bsave = true;
	}
	else if(tdd._n=='scaling')
	{
		tdd._tt = `\${tdd._t} [\${tdd.raw_low} , \${tdd.raw_high}] - [\${tdd.scaled_low},\${tdd.scaled_high}]`;
		bsave = true;
	}
	if(!bsave) return ;
	
	for(let ob of queue)
	{
		if(ob._tt==tdd._tt)
			return ;
	}
	
	queue.unshift(tdd);
	if (queue.length > 5) queue.pop();
	localStorage.setItem('trans_list', JSON.stringify(queue));
}

function read_units_dd()
{
	return JSON.parse(localStorage.getItem('units_list') || '[]') ;
}
function save_units_dd(unit,unit_t)
{
	if(!unit) return;
	let queue = read_units_dd();
	for(let ob of queue)
	{
		if(ob.u==unit)
			return ;
	}
	let tdd = {u:unit,t:unit_t} ;
	queue.unshift(tdd);
	if (queue.length > 10) queue.pop();
	localStorage.setItem('units_list', JSON.stringify(queue));
}


function del_alert(idx)
{
	if(event)
		event.preventDefault() || (event.returnValue = false);
	if(idx<0||idx>=alerts_dd.length)
		return;
	alerts_dd.splice(idx,1) ;
	update_alert_s();
}

function get_alert_idx_name(n)
{
	for(let i = 0 ; i < alerts_dd.length ; i ++)
	{
		let a = alerts_dd[i] ;
		if(n==a.name)
			return i ;
	}
	return -1 ;
}

function edit_alert(idx)
{
	if(event)
		event.preventDefault() || (event.returnValue = false);
	let tt = "<wbt:g>edit,tag,alt_evt,sor</wbt:g>"
	if(idx==undefined||idx==null)
		tt ="<wbt:g>add,tag,alt_evt,sor</wbt:g>"
	let dd = null;
	if(idx>=0)
		dd = alerts_dd[idx] ;
	
	 let cur_tagt = $("#title").val() ;
	 let cur_tagvt = $("#vt").val() ;
	  
	dlg.open("./tag_evt_edit.jsp?",	{title:tt,w:'600px',h:'400px',dd:dd,tag_v:{t:cur_tagt,vt:cur_tagvt,idx:idx||-1}},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 if(ret.name)
						 {
							 let oldidx = get_alert_idx_name(ret.name) ;
							 if(oldidx>=0 && oldidx!=idx)
							 {
								 dlg.msg("<wbt:g>name</wbt:g> ["+ret.name+"] <wbt:g>is_al_exist</wbt:g>");
								 return ;
							 }
						 }
						 if(idx>=0)
						 	alerts_dd[idx] = ret ;
						 else
							 alerts_dd.push(ret) ;
						 update_alert_s();
						 dlg.close();
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
function do_submit(cb)
{
	let id=$("#id").val() ;
	let n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	let tt = $('#title').val();
	if(tt==null||tt=='')
	{
		//cb(false,'please input title') ;
		//return ;
		tt = n ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	let bloc = $("#local").prop("checked") ;
	let b_val_filter = $("#b_val_filter").prop("checked") ;
	let loc_defv = get_input_val("local_defval") ;
	let bloc_autosave = $("#local_autosave").prop("checked") ;
	let max_val_str = $("#max_val_str").val() ;
	let min_val_str = $("#min_val_str").val() ;
	let mid_w_js = $("#mid_w_js").val() ;
	//let alert_low = $("#alert_low").val();
	//let alert_high = $("#alert_high").val();
	
	let canw = get_input_val("canw","");
	if('true'==canw && bmid)
	{
		if(trim(mid_w_js)=='')
		{
			cb(false,"<wbt:g>pls,input,exp_wjs</wbt:g>")
			return ;
		}
	}
	let unit = $("#unit").val() ;
	let unit_t = $('#unit option:selected').text();
	save_units_dd(unit,unit_t) ;
	let indicator = $("#indicator").val() ;
	cb(true,{id:id,name:n,title:tt,desc:desc,mid:bmid,
		addr:get_input_val("addr",""),
		vt:get_input_val("vt",""),
		dec_digits:get_input_val("dec_digits",-1,true),
		srate:get_input_val("srate",100,true),
		canw:canw,unit:unit,indicator:indicator,
		trans:JSON.stringify(trans_dd),
		b_val_filter:b_val_filter,
		bloc:bloc,loc_defv:loc_defv,bloc_autosave:bloc_autosave,mid_w_js:mid_w_js,
		min_val_str:min_val_str,max_val_str:max_val_str,alerts:JSON.stringify(alerts_dd),
		});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

function do_batch(cb)
{
	let retob={} ;
	
	let vt = get_input_val("vt","");
	if(vt) retob.vt = vt ;
	
	let dec_d = get_input_val("dec_digits",-1,true);
	if(dec_d!=-1) retob.dec_digits = dec_d ;
	
	let canw = get_input_val("canw","");
	if(canw) retob.canw=canw ;
	
	let unit = $("#unit").val() ;
	let unit_t = $('#unit option:selected').text();
	save_units_dd(unit,unit_t) ;
	if(unit) retob.unit = unit ;
		
	let indicator = $("#indicator").val() ;
	if(indicator) retob.indicator=indicator;
	
	if(trans_dd) retob.trans=JSON.stringify(trans_dd);
	
	let clears=[];
	$("#batch_clear_p").find(".batch_clear").each(function(){
		let ob = $(this) ;
		let bc = ob.attr("batch_clear") ;
		if(!bc || !ob.prop("checked"))
			return ;
		clears.push(bc) ;
	});
	if(clears.length>0)
		retob.clears = clears.join(",") ;
	//console.log(retob) ;cb(false,"test")
	cb(true,retob);
}

function on_new_tag()
{
	event.preventDefault() || (event.returnValue = false);
	document.location.href="./tag_edit.jsp?path="+node_path;
}

function on_copy_tag()
{
	event.preventDefault() || (event.returnValue = false);
	if(id=='')
		return ;
	//console.log(dlg.get_opener_w())
	dlg.get_opener_w().copy_paste_tag(node_path,tag_id,(newid)=>{
		document.location.href="./tag_edit.jsp?path="+node_path+"&id="+newid;
	});
}

function chk_addr()
{
	event.preventDefault() || (event.returnValue = false);
	var addr = $("#addr").val() ;
	addr = trim(addr) 
	if(!addr)
		return ;
	
	send_ajax("./tag_ajax.jsp",{op:"chk_addr",
		vt:get_input_val("vt",""),
		addr:get_input_val("addr",""),
		canw:get_input_val("canw",""),
		path:node_path
		},(bsucc,ret)=>{
			if(ret=="{}")
				return ;
			var r ;
			eval("r="+ret) ;
			//console.log(r) ;
			if(r.guess)
			{
				if(r.addr)
					$("#addr").val(r.addr) ;
				var vt = r.vt+"" ;
				if(vt)
				{
					$("#vt").val(vt) ;
				}
				$("#canw").val(""+r.canw) ;
				form.render();
			}
			else
			{
				if(r.res>0)
				{
					dlg.msg("<wbt:g>chk,succ</wbt:g>");
					return ;//
				}
					
				if(r.res<0)
				{
					dlg.msg(r.prompt) ;
					return ;
				}
				if(r.addr)
					$("#addr").val(r.addr) ;
				var vt = r.vt+"" ;
				if(vt)
				{
					$("#vt").val(vt) ;
					form.render();
				}
				dlg.msg(r.prompt) ;
			}
			
	});
}

function help_addr()
{
	window.open("tag_addr_helper.jsp?path="+node_path) ;
}

function on_js_edit()
{
	let txt = $("#addr").val() ;
	dlg.open("./cxt_script.jsp?dlg=true&opener_txt_id=addr&path="+node_path,
			{title:"<wbt:g>edit_mid_tag_js</wbt:g>",w:'600px',h:'400px',},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#addr").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_wjs_edit()
{
	let txt = $("#mid_w_js").val() ;
	dlg.open("./cxt_script.jsp?dlg=true&opener_txt_id=mid_w_js&path="+node_path,
			{title:"<wbt:g>edit_mid_tag_js</wbt:g>",w:'600px',h:'400px',},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#mid_w_js").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


</script>
</html>