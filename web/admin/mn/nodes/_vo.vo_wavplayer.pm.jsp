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
	org.iottree.ext.vo.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.core.msgnet.nodes.*
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
	if(item==null || !(item instanceof WavPlayer_NM))
	{
		out.print("no item found") ;
		return ;
	}
	
	WavPlayer_NM node= (WavPlayer_NM)item ;
	String wav_dir = node.getWavDir() ;
	List<WavPlayer_NM.KeyFileItem> kfis = node.getKeyFileItems() ;
	if(kfis==null)
		kfis = Arrays.asList();
	String def_fn = node.getDefaultFN() ;
%>
<style>
td {border:1px solid #ccc;font-size:12px;}
</style>
<div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Wav Dir:</span>
    </div>
	  <div class="layui-input-inline" style="width: 600px;">
	    <input type="text" id="wav_dir" class="layui-input" lay-skin="primary" value="<%=wav_dir%>"/>
	  </div>
	  <div class="layui-form-mid"><button onclick="on_dir_sel()" class="layui-btn layui-btn-sm layui-btn-primary" >...</button></div>
</div>
<div class="layui-form-item">
    <div class="layui-form-label"><span style="white-space: nowrap;">Key-File:</span>
    </div>
	  <div class="layui-input-inline" style="width: 300px;">
	    <table style="width:100%;">
	    	<thead>
	    	<tr><td>Key</td><td>File Name</td><td>Default</td></tr>
	    	</thead>
	    	<tbody id="tb_key_file">
<%
for(WavPlayer_NM.KeyFileItem kfi:kfis)
{
	String k = kfi.getKey() ;
	String fn = kfi.getFileName() ;
	String chk = fn.equals(def_fn)?"checked":"";
%><tr fn="<%=fn %>"><td><input class="k" type="text" value="<%=k %>"/></td><td><%=fn %></td><td><input type="radio" name="fn_def" value="<%=fn %>" <%=chk %> /></td></tr><%
}
%>
	    	</tbody>
	    </table>
	  </div>
	  
</div>

<script>

function on_dir_sel()
{
	dlg.open("/admin/util/dir_selector.jsp",{title:"Select Dir",w:'500px',h:'400px'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					let seldir = dlgw.get_sel_dir();
					if(!seldir)
					{
						dlg.msg("no dir selected");return;
					}
					$("#wav_dir").val(seldir) ;
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
	let jo = {} ;
	let wav_dir =  $("#wav_dir").val();
	let def_fn = $('input[name="fn_def"]:checked').val();
	let key_file_items=[];
	$("#tb_key_file").find("tr").each(function(){
		let tr = $(this) ;
		let fn = tr.attr("fn") ;
		let k = tr.find(".k").val()||"" ;
		key_file_items.push({key:k,filen:fn}) ;
	});
	return {wav_dir:wav_dir,default_fn:def_fn,key_file_items:key_file_items} ;
}

function set_pm_jo(jo)
{
	$("#wav_dir").val(jo.wav_dir||"") ;
	let kfis = jo["key_file_items"]||[];
	for(let kfi of kfis)
	{
		let k = kfi.key||"" ;
		let fn = kfi.filen ;
		$("#tb_key_file").find("tr").each(function(){
			let tr = $(this) ;
			let fn0 = tr.attr("fn") ;
			if(fn==fn0)
				tr.find(".k").val(k) ;
		});
	}
}

function get_pm_size()
{
	return {w:600,h:350} ;
}

//on_init_pm_ok() ;
</script>