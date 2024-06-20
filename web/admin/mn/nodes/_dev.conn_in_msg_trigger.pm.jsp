<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.conn.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	String prjid = request.getParameter("prjid");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	
	
%>
  <div class="layui-form-item">
    <label class="layui-form-label">Conn In:</label>
    <div class="layui-input-inline" style="width:250px;">
		<select id="conn_pt_id"  name="conn_pt_id" class="layui-input" lay-filter="repeat_tp">
<%
	List<ConnProvider> cps = prj.getConnProviders() ;
	for(ConnProvider cp:cps)
	{
		List<ConnPt> cpts = cp.listConns() ;
		for(ConnPt cpt:cpts)
		{
			if(!(cpt instanceof ConnPtMSG))
				continue ;
			ConnPtMSG cptm = (ConnPtMSG)cpt ;
%>
        <option value="<%=cptm.getId()%>"><%=cp.getTitle() %> - <%=cptm.getTitle() %></option>
<%
		}
	}
%>
      </select>
    </div>

  </div>

<script>

function on_after_pm_show(form)
{

	  form.on('select(conn_pt_id)', function (data) {
		  //update_bt();
		});
	 // update_bt();
}

function update_bt()
{
	
}

function get_pm_jo()
{
	let conn_pt_id = $("#conn_pt_id").val();
	return {conn_pt_id:conn_pt_id} ;
}

function set_pm_jo(jo)
{
	let conn_pt_id = jo.conn_pt_id||"" ;
	if(conn_pt_id)
		$("#conn_pt_id").val(conn_pt_id) ;
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>