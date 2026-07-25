<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,org.iottree.core.util.jt.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid","itemid","dlgn"))
			return ;
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	String dlgn = request.getParameter("dlgn") ;
	
	MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
	if(mnm==null)
	{
		out.print("no MsgNet Manager with container_id="+container_id) ;
		return ;
	}

	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	MNBase item =net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	
	String tp = item.getTPFull() ;
	String title = item.getTitle() ;
	
	String dlg_url = item.getCat().getNodeUrl(item,dlgn);// "./nodes/"+tp+"_pm.jsp" ;
	dlg_url += "?container_id="+container_id+"&netid="+netid+"&itemid="+itemid ;
	response.sendRedirect(dlg_url) ;
%>                                                                                                                                                        