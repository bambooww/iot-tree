<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,org.iottree.core.store.gdb.autofit.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,org.iottree.core.msgnet.modules.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "op"))
		return ;
	String op = request.getParameter("op");
	String prjid = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	String name = request.getParameter("name") ;
	//IMNContainer
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager mnm= MNManager.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	RelationalDB_CRUD item =(RelationalDB_CRUD)net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	RelationalDB_M m = item.getOwnerRDB_M() ;
	
	switch(op)
	{
	case "read_cols_from_db":
		if(!Convert.checkReqEmpty(request, out, "op","container_id","netid","itemid"))
			return ;
		String tablen = item.getRDBTableName() ;
		if(Convert.isNullOrEmpty(tablen))
		{
			out.print("no table related or found") ;
			return ;
		}
		List<JavaColumnInfo> cols = m.readColsFromDB(tablen) ;
		if(cols==null)
		{
			out.print("no columns gotten") ;
			return ;
		}
		JSONArray tmpjarr = new JSONArray() ; 
		for(JavaColumnInfo col:cols)
		{
			tmpjarr.put(col.toJO()) ;
		}
		tmpjarr.write(out) ;
		return ;
	case "read_cols_from_reg":
		if(!Convert.checkReqEmpty(request, out, "name"))
			return ;
		JavaTableInfo jti = RelationalDB_CRUD.getRegisteredName2JTI().get(name) ;
		if(jti==null)
		{
			out.print("no register found with name="+name) ;
			return ;
		}
		tmpjarr = new JSONArray() ; 
		JavaColumnInfo pkcol = jti.getPkColumnInfo() ;
		if(pkcol!=null)
			tmpjarr.put(pkcol.toJO()) ;
		for(JavaColumnInfo col:jti.getNorColumnInfos())
		{
			tmpjarr.put(col.toJO()) ;
		}
		tmpjarr.write(out) ;
		return ;
	default:
		out.print("unknow op") ;
		return ;
	}
%>