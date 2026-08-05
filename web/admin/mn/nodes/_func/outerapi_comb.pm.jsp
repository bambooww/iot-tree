<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<%--
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>gain</w:g> (P)</label>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="uri_path" name="uri_path"  lay-filter="uri_path"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>integral</w:g> (I)</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="ki" name="ki"  lay-filter="ki" min="0.0"  autocomplete="off" class="layui-input" />
    </div>
    <div class="layui-form-mid"><w:g>derivative</w:g> (D)</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="kd" name="kd"  lay-filter="kd" min="0.0"  autocomplete="off" class="layui-input" />
    </div>
</div>
 --%>
<div style="width:100%;height:700px;">
	<iframe id="if_api_doc" src="" style="width:100%;height:100%;border:0px;"></iframe>
</div>
<div style="position: absolute;width:50%;height:300px;bottom:30px;right:0px;border:1px solid #ccc">
	<div style="background-color: #aaa;width:100%;height:20px;font-weight:bold;line-height: 20px;">request json (when in msg has no payload)</div>
	<textarea style="width:100%;height:270px;color:green;" id="req_txt"></textarea>
</div>
<script>
function on_after_pm_show(form)
{
	form.on("checkbox(using_sor)",function(obj){
		update_ui() ;
	}) ;
}

function update_ui()
{
	
}

function get_input_fval(id,defv,bnum)
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

function check_no_input(ids,tts)
{
	for(let i = 0 ; i < ids.length ; i ++)
	{
		let id = ids[i] ;
		let v = $("#"+id).val() ;
		if(!v)
		{
			return "<w:g>pls_input</w:g>  "+tts[i];
		}
	}
	return true;
}

function get_pm_jo()
{
	let w = $("#if_api_doc")[0].contentWindow ;
	let uri_path = w.get_cur_uri_path() ;
	let uids = w.get_cur_sub_uids();
	let req_txt = $("#req_txt").val() ;
	
	let ret ={uri_path:uri_path,sub_api_uids:uids.join(','),req_txt:req_txt||""};
	//console.log(ret) ;
	return ret ;
}

function set_pm_jo(jo)
{
	let path = jo.uri_path||"" ;
	let sub_api_uids = jo.sub_api_uids||"";
	$("#req_txt").val(jo.req_txt||"") ;
	//console.log(path);
	$("#if_api_doc").attr("src",`/mn_outer_api_doc.jsp?dlg=true&path=\${path}&sub_api_uids=\${sub_api_uids}`) ;
	update_ui() ;
}

function get_jo_val(jo,n,defv)
{
	let vv = jo[n];
	if(vv===null||vv===undefined||vv==="")
		return defv ;
	return vv ;
}

function get_pm_size()
{
	return {w:1000,h:800} ;
}

//on_init_pm_ok() ;
</script>