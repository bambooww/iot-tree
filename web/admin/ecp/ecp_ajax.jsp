<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.router.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.ecp.*
	"%><%!

%><%if(!Convert.checkReqEmpty(request, out,"op"))
	return ;

String op = request.getParameter("op");
String mgr_id = request.getParameter("mgr_id");
String net_uid = request.getParameter("net_uid") ;
String netid = request.getParameter("net_id") ;
String itemid = request.getParameter("itemid") ;
String nodeid = request.getParameter("nodeid") ;
String moduleid= request.getParameter("moduleid") ;
String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;
String fulltp = request.getParameter("fulltp") ;

String comp_libid = request.getParameter("comp_libid") ;
String comp_id = request.getParameter("comp_id") ;

String cxt_uid = request.getParameter("cxt_uid") ;

String tp = request.getParameter("tp") ;
float x = Convert.parseToFloat(request.getParameter("x"),0) ;
float y = Convert.parseToFloat(request.getParameter("y"),0) ;

ECPNet net = null;
ECPManager mnm = null ;
ECPNetContainer net_c = null ;
if(Convert.isNotNullEmpty(net_uid))
{
	 net = ECPManager.getNetByUID(net_uid) ;
	 if(net==null)
	 {
	 	out.print("no net found") ;
	 	return ;
	 }
	 net_c = net.getBelongTo() ;
	 if(net_c instanceof ECPManager)
		 mnm = (ECPManager)net_c ;
}

if(Convert.isNotNullEmpty(mgr_id))
	mnm = ECPManager.getInstanceByMgrId(mgr_id) ;

//ECPManager mnm= ECPManager.getInstanceByMgrId(mgr_id);
//if(mnm==null)
//{
//	out.print("no ECPManager with mgr_id="+mgr_id) ;
//	return ;
//}

ECPNode node=  null ;
ECPBase item = null ;
if(Convert.isNotNullEmpty(itemid))
{
	item = net.getNodeById(itemid);
	if(item==null)
	{
		out.print("no item found with id="+itemid) ;
		return ;
	}
}
if(Convert.isNotNullEmpty(nodeid))
{
	node = net.getNodeById(nodeid) ;
	if(node==null)
	{
		out.print("no node found with id="+nodeid) ;
		return ;
	}
}

String jstr = request.getParameter("jstr") ;
JSONObject in_jo = null ;
if(Convert.isNotNullEmpty(jstr))
	in_jo = new JSONObject(jstr) ;

StringBuilder failedr = new StringBuilder() ;
try
{
	switch(op)
	{
	case "add_edit_net":
		if(!Convert.checkReqEmpty(request, out, "name"))
			return ;
		boolean benable = !"false".equals(request.getParameter("enable")) ;
	
			if(Convert.isNullOrEmpty(netid))
				ECPManager.getInstanceUA().createNewNet(name, title, desc,benable) ;
			else
				ECPManager.getInstanceUA().updateNet(netid, name, title, desc,benable);
			out.print("succ") ;
		
		return ;
	case "del_net":
		if(!Convert.checkReqEmpty(request, out, "net_id"))
			return ;
		try
		{
			ECPManager.getInstanceUA().delNet(netid);
			out.print("succ") ;
		}
		catch(Exception e)
		{
			out.print(e.getMessage()) ;
		}
		return ;
	case "load_net":
		if(!Convert.checkReqEmpty(request, out, "net_uid"))
			return ;
		net.renderOut(out);
		return ;
	case "net_save_basic": //只保存布局信息
		if(!Convert.checkReqEmpty(request, out, "net_uid","jstr"))
			return ;
		
		if(!net.updateStructByJO(in_jo,failedr))
		{
			out.print(failedr.toString()) ;
		}
		else
		{
			net.save();
			out.print("succ") ;
		}
		return ;
	case "del_by_ids":
		if(!Convert.checkReqEmpty(request, out,"net_id", "ids"))
			return ;
		String ids = request.getParameter("ids") ;
		List<String> idlist = Convert.splitStrWith(ids, ",") ;
		try
		{
			int r = net.removeItemByIds(idlist) ;
			if(r>0)
				out.print("succ") ;
			else
				out.print("no_item_del") ;
		}
		catch(Exception e)
		{
			out.print(e.getMessage()) ;
		}
		return ;

	default:
		out.print("unknown op="+op) ;
		return ;
	}
}
catch(Exception e)
{
	//e.printStackTrace();
	out.print(e.getMessage()) ;
}%>