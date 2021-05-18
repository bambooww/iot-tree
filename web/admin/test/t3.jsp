<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.ua.*,
				org.iottree.system.xmldata.*,
				org.json.*,
				java.net.*"%>
<%
	String rid = request.getParameter("rid");

UARep rep = new UARep() ;

UACh ch = new UACh("modbus_serial");
ch.addDev(new UADev());
rep.addCh(ch);

XmlData xd = rep.toUAXmlData();//XmlDataUtil.extractXmlDataFromObj(rep) ;
System.out.println(xd.toXmlString()) ;
JSONObject jo = DataTranserJSON.extractJSONFromObj(rep) ;
System.out.println(jo.toString(2)) ;

UARep repn = new UARep() ;
repn.fromUAXmlData(xd);
//XmlDataUtil.injectXmDataToObj(repn, xd) ;
xd = repn.toUAXmlData();
System.out.println("--------------");
System.out.println(xd.toXmlString()) ;

repn = new UARep() ;
DataTranserJSON.injectJSONToObj(repn, jo) ;
jo = DataTranserJSON.extractJSONFromObj(rep) ;
System.out.println(jo.toString(2)) ;
%>