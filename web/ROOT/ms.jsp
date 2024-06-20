<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="org.json.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.res.*,
	org.iottree.web.oper.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
	JSONObject jo = new JSONObject() ;
	Date dt = new Date() ;
	jo.put("ms",dt.getTime()) ;
	jo.put("dt",Convert.toFullYMDHMS(dt)) ;
%><%=jo%>