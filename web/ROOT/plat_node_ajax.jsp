<%@ page contentType="text/json;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.net.*,
				java.util.*,
				org.json.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.node.*,
				org.iottree.core.station.*,
				org.iottree.core.msgnet.*,
				org.iottree.core.store.*,
				java.net.*"%><%!
				
%><%
	if(!PlatNodeManager.isPlatNode())
		return ;

	PlatNode pn = PlatNodeManager.getInstance().getNode() ;
	
	if(!Convert.checkReqEmpty(request, out, "op"))
		return;
	
	List<String> opns = pn.getOpenPrjNames() ;
	
	ArrayList<String> prjns = new ArrayList<>() ;
	for(UAPrj prj:UAManager.getInstance().listPrjs())
	{
		String tmpn = prj.getName() ;
		if(opns==null||opns.size()<=0||opns.contains(tmpn))
			prjns.add(tmpn) ;
	}
	pn.setOpenPrjNames(prjns) ;
	
	String op = request.getParameter("op") ;
	
	String prjname = request.getParameter("prjn") ;
	UAPrj prj = null ;
	String prjid = null ;
	if(Convert.isNotNullEmpty(prjname))
	{
		prj = UAManager.getInstance().getPrjByName(prjname) ;
		if(prj==null)
		{
			out.print("no prj found with name="+prjname) ;
			return ;
		}
		prjid = prj.getId() ;
	}
	
	PlatInsManager pmgr= PlatInsManager.getInstance() ;
	String txt = null ;
	switch(op)
	{
	case "plat_node": // out plat node jo
		JSONObject tmpjo = pn.toJO();
		tmpjo.write(out) ;
		return ;
	case "store_sor":
		File sorf = StoreManager.getStoreSourcesFile() ;
		txt=  Convert.readFileTxt(sorf) ;
		out.print(txt) ;
		return ;
	case "prj_xml": //read prj xml
		if(Convert.isNullOrEmpty(prjname))
			return ;
		File prjf = UAManager.getPrjFile(prjid) ;
		txt=  Convert.readFileTxt(prjf) ;
		out.print(txt) ;
		return ;
	case "prj_msg_nets":
		if(!Convert.checkReqEmpty(request, out, "prjn"))
			return;
		MNManager mng = MNManager.getInstance(prj) ;
		List<MNNet> nets = mng.listNets() ;
		JSONArray jarr = new JSONArray() ;
		if(nets!=null)
		{
			for(MNNet net:nets)
			{
				File netf = net.getNetFile() ;
				if(!netf.exists())
					continue ;
				JSONObject jo = Convert.readFileJO(netf) ;
				if(jo==null)
					continue ;
				jarr.put(jo) ;
			}
		}
		jarr.write(out) ;
		return ;
	case "stations_rt_st": //station实时状态
		tmpjo = pmgr.RT_toStatusJO() ;
		tmpjo.write(out) ;
		return ;
	default:
		out.print("unknown op="+op) ;
	}%>