<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.util.*
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
	if(item==null || !(item instanceof NRES_Common))
	{
		out.print("no item found") ;
		return ;
	}
	
	NRES_Common node= (NRES_Common)item ;
%>
<style>

</style>
<%

	String caller_uid = node.getResCallerUID() ;
	if(caller_uid==null)
		caller_uid = "" ;
	ResCaller res_caller = node.getResCaller() ;
	String tt = "" ;
	if(res_caller!=null)
		tt = res_caller.getCallerTitle() ;
%>
<div class="layui-form-item">
    <label class="layui-form-label">Caller:</label>
    <div class="layui-input-inline" style="width:46%;" >
    	<input type="hidden" id="caller_uid" name="caller_uid" value="<%=caller_uid %>" />
      <input type="text" id="caller_tt" name="caller_tt" value="<%=tt %> - [<%=caller_uid %>]"  class="layui-input" readonly>
    </div>
    <label class="layui-form-mid"><button onclick="sel_caller()" class="layui-btn layui-btn-sm layui-btn-primary">...</button></label>
  </div>

<script>

function on_after_pm_show(form)
{
	 
}


function sel_caller(uid)
{
	dlg.open("./util/res_caller_sel.jsp",
			{title:'Res Caller Selector',w:'500px',h:'400px',input:{uid:uid||""}},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>clear</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					let sel = dlgw.get_selected();
					if(typeof(sel)=='string')
					{
						dlg.msg(sel) ;
						return ;
					}
					$("#caller_uid").val(sel.uid);
					$("#caller_tt").val(sel.title);
					dlg.close() ;
				},
				function(dlgw)
				{
					$("#caller_uid").val("");
					$("#caller_tt").val("");
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function get_pm_jo()
{
	let caller_uid = $("#caller_uid").val()||"";
	return {caller_uid:caller_uid} ;
}

function set_pm_jo(jo)
{
	
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>