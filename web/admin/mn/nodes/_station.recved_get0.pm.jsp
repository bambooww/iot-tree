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

%>

   <div class="layui-form-item">
    <label class="layui-form-label">DB Name:</label>
    <div class="layui-input-inline " id="filter_list" style="width:250px;">
    	<input type="text" id="db_name" name="db_name" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>
 
  <div class="layui-form-item">
    <label class="layui-form-label">Station Prjs:</label>
    <div class="layui-input-inline" style="width:350px;">
<%
	for(UAPrj tprj:UAManager.getInstance().listPrjs())
	{
		String uid = tprj.getName() ;
%><input type="checkbox" id="sp_<%=uid %>" name="station_prjs"  lay-skin="primary"  lay-filter="" class="layui-input" value="<%=uid%>"><%=uid%><br><%
	}
%>
    </div>
  </div>
<script>


function on_after_pm_show(form)
{
	
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


function get_pm_jo()
{
	let db_name = $("#db_name").val() ;
	let station_prjs = get_chk_vals("station_prjs") ;
	return {db_name:db_name,station_prjs:station_prjs} ;
}



function set_pm_jo(jo)
{
	$("#db_name").val(jo.db_name||"") ; 
	let station_prjs = jo.station_prjs||[] ;
	
	$("input[name=station_prjs]").each( function () {
	       let ob = $(this) ;
	       let val = ob.attr("value") ;
	       let b_chk = station_prjs.indexOf(val)>=0 ;
	       ob.prop("checked",b_chk);
	       form.render();
	});
}

function get_pm_size()
{
	return {w:750,h:400} ;
}

//on_init_pm_ok() ;
</script>