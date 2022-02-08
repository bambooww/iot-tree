<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.task.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.sim.*,
				org.iottree.driver.common.modbus.sim.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out,"insid","chid","devid"))
	return ;
String op = request.getParameter("op") ;
if(op==null)
	op="" ;

	String insid=request.getParameter("insid");
	SimInstance ins = SimManager.getInstance().getInstance(insid) ;
	if(ins==null)
	{
		out.print("no instance found") ;
		return ;
	}
	
	String chid=request.getParameter("chid");
	String devid=request.getParameter("devid");
	String segid = request.getParameter("segid");
	int regidx= Convert.parseToInt32(request.getParameter("regidx"), -1) ;
	SimChannel sch = ins.getChannel(chid);
	if(sch==null)
	{
		out.print("no channel found") ;
		return ;
	}
	SlaveDev dev = (SlaveDev)sch.getDev(devid) ;
		if(dev==null)
		{
	out.print("no device found") ;
	return ;
		}
	int addr = dev.getDevAddr() ;
	
switch(op)
{
case "input_v":
	if(!Convert.checkReqEmpty(request, out,"segid","regidx","v"))
		return ;
	if(regidx<0)
	{
		out.print("invalid regidx input") ;
		return ;
	}
	SlaveDevSeg seg = dev.getSegById(segid) ;
	if(seg==null)
	{
		out.print("no seg found");
		return ;
	}
	String strv = request.getParameter("v") ;
	seg.setSlaveDataStr(regidx, strv);
	out.print("succ") ;
	break ;
default:

	out.print("{ \"segs\":[");
	List<SlaveDevSeg> segs = dev.getSegs() ;

	boolean bfirst = true;
	for(SlaveDevSeg seg0:segs)
	{
		if(bfirst) bfirst=false;
		else out.print(",");
		
		String tmpsegid = seg0.getId() ;
		int fc = seg0.getFC() ;
		//String fct = seg.getFCTitle() ;
		//int regidx = seg.getRegIdx() ;
		//int regnum = seg.getRegNum() ;
		String segdatastr = seg0.RT_getDataJsonArrStr();
		out.print("{\"id\":\""+tmpsegid+"\",\"datas\":") ;
		out.print(segdatastr);
		out.print("}") ;
	}
	out.print("]}");
}
%>
