<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

	 %><%
	
%>
<html>
<head>
<title>Tag Transfer Editor </title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(590,600);
</script>

</head>
<body>
<form class="layui-form" action="">
	<div class="layui-form-item">
    <label class="layui-form-label">&nbsp;</label>
    <div class="layui-input-block">
      <input type="radio" name="name" value="none" title="<wbt:g>none</wbt:g>" checked="checked" lay-filter="name">
<%
for(ValTranser vt: ValTranser.listValTransers())
{
%> <input lay-filter="name" type="radio" name="name" value="<%=vt.getName() %>" title="<%=vt.getTitle() %>"  onchange="show_card()"/><%
}
%>
    </div>
   </div>
   
  <div class="layui-card" id="card_none" style="display:none">
  
  <div class="layui-card-body">
  	<wbt:g>no_transfer</wbt:g>
  </div>
  </div>
 <div class="layui-form-item" style="display:none" id="dt_tp">
    <label class="layui-form-label"><wbt:g>data,type</wbt:g></label>
    <div class="layui-input-inline" style="width: 120px;">
      <select  id="_vt"  name="_vt"  class="layui-input" placeholder="">
        <option value="">-</option>
<%
for(UAVal.ValTP vt:UAVal.ValTP.values())
{
	String sel="" ;
	if(vt==UAVal.ValTP.vt_float)
		sel = "selected=\'selected\'";
	 %><option value="<%=vt.getInt()%>" <%=sel %>><%=vt.getStr() %></option><%
}
%>
      </select>
    </div>
    
</div>

  <div class="layui-card" id="card_scaling" style="display:none">
<!--   
  <div class="layui-card-header">Scaling</div>
  <div class="layui-card-body">
 -->
    <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>scaling,type</wbt:g></label>
    <div class="layui-input-block">
      <input type="radio" name="tp" value="1" title="<wbt:g>linear</wbt:g>" checked="checked" lay-filter="tp">
  	<input type="radio" name="tp" value="2" title="<wbt:g>squ_root</wbt:g>"  lay-filter="tp">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>row_val_r</wbt:g></label>
    <div class="layui-input-block">
      <div class="layui-form-mid"><wbt:g>high_v</wbt:g>:</div>
<div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="raw_high" name="raw_high" value="" class="layui-input"/>
	  </div>
      <div class="layui-form-mid"><wbt:g>low_v</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="raw_low" name="raw_low" value="" class="layui-input"/>
	  </div>
	  
    </div>
  </div>
     

    
    <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>scaled_val_r</wbt:g></label>
    <div class="layui-input-block">
      <div class="layui-form-mid"><wbt:g>high_v</wbt:g>:</div>
<div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="scaled_high" name="scaled_high" value="" class="layui-input"/> 
	  </div>
      <div class="layui-form-mid"><wbt:g>clamp</wbt:g></div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="scaled_high_c" name="scaled_high_c" title="Clamp" lay-skin="switch" >
	  </div>
	  
    </div>
  </div>
  
  
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-block">
      <div class="layui-form-mid"><wbt:g>low_v</wbt:g>:</div>
<div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="scaled_low" name="scaled_low" value="" class="layui-input"/> 
	  </div>
      <div class="layui-form-mid"><wbt:g>clamp</wbt:g></div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="scaled_low_c" name="scaled_low_c"  title="Clamp" lay-skin="switch"> 
	  </div>
	  
    </div>
  </div>
</div>

<div class="layui-card" id="card_calc" style="display:none">
    <div class="layui-form-item">
    <label class="layui-form-label">OP<wbt:g>type</wbt:g></label>
    <div class="layui-input-block">
      <input type="radio" name="op" value="1" title="<wbt:g>op_add</wbt:g>"  >
      <input type="radio" name="op" value="2" title="<wbt:g>op_subtract</wbt:g>" >
      <input type="radio" name="op" value="3" title="<wbt:g>op_multiplay</wbt:g>" checked="checked" >
      <input type="radio" name="op" value="4" title="<wbt:g>op_divide</wbt:g>" >
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">op<wbt:g>val</wbt:g></label>
    <div class="layui-input-block">
      
	<div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="op_v" name="op_v" value="" class="layui-input"/>
	  </div>
    </div>
  </div>
  </div>
  
  <div class="layui-card" id="card_js" style="display:none">
<!-- 
  <div class="layui-card-header">JS</div>
  <div class="layui-card-body">
 -->
    <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>transfer</wbt:g></label>
    <div class="layui-input-block">
    ($tag,$input)=>{
      <textarea rows="9" cols="60" id="js_txt" style="border-color: #e6e6e6" ondblclick="on_js_edit()" title="double click to open edit JS dialog"></textarea>
    <br>}
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>inverser</wbt:g></label>
    <div class="layui-input-block">
     ($tag,$input)=>{
      <textarea rows="9" cols="60" id="inverse_js_txt" style="border-color: #e6e6e6"  ondblclick="on_js_edit_inv()" title="double click to open edit JS dialog"></textarea>
    <br>}
    </div>
  </div>
<!-- 
  </div>
   -->
  </div>
 </form>
</body>
<script type="text/javascript">
var form ;
var ow =dlg.get_opener_w();
var node_path=ow.node_path;
var transdd = ow.trans_dd ;
if(transdd==null)
	transdd={} ;
var path=node_path ;
//console.log(transdd);
	
layui.use('form', function(){
	  form = layui.form;
	  
	  $("input[type=radio][name=name][value="+transdd._n+"]").attr("checked",true);
	  
	  form.on('radio(name)', function (data) {
		　　
		　var n = data.value;
			show_card();
		});
		
	  form.render();
});

function get_val(n,defv)
{
	var v = transdd[n] ;
	if(v)
		return v ;
	return ""+defv ;
}

function show_card()
{
	$("#dt_tp").css("display","none") ;
	$("#card_none").css("display","none") ;
	$("#card_scaling").css("display","none") ;
	$("#card_calc").css("display","none") ;
	$("#card_js").css("display","none") ;
	
	var n = $("input[name='name']:checked").val();
	if(n==null||n=="")
		return ;
	
	if(transdd._vt)
		$("#_vt").val(transdd._vt) ;
	
	$("#card_"+n).css("display","") ;
	switch(n)
	{
	case "scaling":
		//console.log(transdd) ;
		$("#dt_tp").css("display","") ;
		$("input[type=radio][name=tp][value="+transdd.tp+"]").attr("checked",true);
		$("#raw_high").val(get_val("raw_high",1000)) ;
		$("#raw_low").val(get_val("raw_low",0)) ;
		$("#scaled_high").val(get_val("scaled_high",1000)) ;
		$("#scaled_low").val(get_val("scaled_low",0)) ;
		if(transdd.scaled_high_c)
			$("#scaled_high_c").attr("checked",true);
		if(transdd.scaled_low_c)
			$("#scaled_low_c").attr("checked",true);
		break;
	case "js":
		$("#dt_tp").css("display","") ;
		$("#js_txt").val(get_val("js",""));
		$("#inverse_js_txt").val(get_val("inverse_js",""));
		break;
	case "calc":
		$("#dt_tp").css("display","") ;
		$("input[type=radio][name=op][value="+transdd.op+"]").attr("checked",true);
		$("#op_v").val(get_val("op_v",1.0)) ;
		break ;
	}
	form.render();
}

show_card();

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseFloat(n);
	return n;
}

function do_submit(cb)
{
	var n = $("input[name='name']:checked").val();
	if(n==null||n=="")
	{
		cb(false,"please select one transfer ");
		return ;
	}
	
	var vt = $("#_vt").val() ;
	if(vt==null||vt=="")
	{
		cb(false,"<wbt:g>pls,select,data,type</wbt:g>") ;
		return false;
	}
	
	switch(n)
	{
	case 'scaling':
		if(!get_scaling(n,vt,cb))
			return ;
		break;
	case "js":
		if(!get_js(n,vt,cb))
			return ;
		break;
	case "calc":
		if(!get_calc(n,vt,cb))
			return ;
		break;
	case "none":
		transdd._n="none" ;
		transdd._t = "";
		break ;
	default:
		cb(false,"unknown transfer name="+n);
		return ;
	}
	
	
	cb(true,transdd);
	return ;
}

function get_scaling(n,vt,cb)
{
	var tp = $("input[name='tp']:checked").val();
	if(!tp)
	{
		cb(false,"<wbt:g>pls,select,scaling,type</wbt:g>") ;
		return false;
	}
	tp = parseInt(tp) ;
	var tp_tt = $("input[name='tp']:checked").attr("title") ;
	var raw_h = get_input_val("raw_high",null,true) ;
	var raw_l = get_input_val("raw_low",null,true) ;
	if(raw_h==null||raw_l==null)
	{
		cb(false,"<wbt:g>pls,input,row_val</wbt:g>") ;
		return false;
	}
	
	var scaled_h = get_input_val("scaled_high",null,true) ;
	var scaled_l = get_input_val("scaled_low",null,true) ;
	if(scaled_h==null||scaled_l==null)
	{
		cb(false,"<wbt:g>pls,input,scaled_val</wbt:g>") ;
		return false;
	}
	
	
	var vttt = $("#_vt").find("option:selected").text(); 
	
	transdd._n = n ;
	transdd._t = n +" - "+vttt;
	transdd._vt = vt ;
	transdd.tp = tp;
	transdd.raw_high = raw_h ;
	transdd.raw_low = raw_l ;
	transdd.scaled_high = scaled_h ;
	transdd.scaled_low = scaled_l ;
	transdd.scaled_high_c = $('#scaled_high_c').is(':checked')
	transdd.scaled_low_c = $('#scaled_low_c').is(':checked')
	
	return true;
}

function get_js(n,vt,cb)
{
	var jstxt = $("#js_txt").val() ;
	var inverse_jstxt = $("#inverse_js_txt").val() ;
	
	if(jstxt==null||(jstxt=trim(jstxt))=="")
	{
		cb(false,"<wbt:g>pls,input,js,code</wbt:g>") ;
		return false;
	}
	
	transdd._n = n ;
	transdd._t = "js";
	transdd._vt = vt ;
	transdd.js = jstxt ;
	transdd.inverse_js = inverse_jstxt;
	return true;
}

function get_calc(n,vt,cb)
{
	var op = $("input[name='op']:checked").val();
	if(!op)
	{
		cb(false,"<wbt:g>pls,select</wbt:g>,<wbt:g>type</wbt:g>") ;
		return false;
	}
	op = parseInt(op) ;
	var op_tt = $("input[name='op']:checked").attr("title") ;
	var op_v = get_input_val("op_v",null,true) ;
	//console.log(op_v) ;
	if(op==4 && (op_v==0||op_v==0.0))
	{
		cb(false,"cannot divide zero!") ;
		return false;
	}
	transdd._n = n ;
	transdd._t = op_tt +" "+op_v;
	transdd._vt = vt ;
	transdd.op = op ;
	transdd.op_v = op_v;
	return true ;
}

function on_js_edit()
{
	let txt = $("#js_txt").val() ;
	dlg.open("../ua_cxt/cxt_script.jsp?dlg=true&no_parent=true&no_this=true&opener_txt_id=js_txt&path="+path,
			{title:"<wbt:g>edit</wbt:g> JS - <wbt:g>transfer</wbt:g>",w:'600px',h:'400px',},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#js_txt").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_js_edit_inv()
{
	let txt = $("#inverse_js_txt").val() ;
	dlg.open("../ua_cxt/cxt_script.jsp?dlg=true&no_parent=true&no_this=true&opener_txt_id=inverse_js_txt&path="+path,
			{title:"<wbt:g>edit</wbt:g> JS - <wbt:g>inverser</wbt:g>",w:'600px',h:'400px',},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#inverse_js_txt").val(jstxt) ;
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