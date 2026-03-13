<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.av.*,
	org.iottree.core.msgnet.nodes.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "op"))
		return ;
	String op = request.getParameter("op") ;
	int idx = Convert.parseToInt32(request.getParameter("idx"),-1) ;
	

	for(CameraCap_NM.CamItem cam:CameraCap_NM.listCameras())
	{

	}
	switch(op)
	{
	case "cam_pm":
		if(!Convert.checkReqEmpty(request, out, "idx"))
			return ;
		if(idx<0)
		{
			out.print("invalid idx="+idx) ;return ;
		}
		List<CameraCap_NM.CamPM> cpms = CameraCap_NM.getCameraPM(idx) ;
		JSONArray jarr = new JSONArray() ;
		for(CameraCap_NM.CamPM cpm : cpms)
		{
			jarr.put(cpm.toJO()) ;
		}
		jarr.write(out) ;
		return ;
	default:
		return ;
	}
%>