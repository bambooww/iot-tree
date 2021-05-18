<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "ashn"))
		return;
	String ashn = request.getParameter("ashn") ;
	ConnProTcpServer.AcceptedSockHandler ash = ConnProTcpServer.getAcceptedSockHandler(ashn) ;
	if(ash==null)
	{
		return ;
	}
	
	NameTitleVal[] ntvs = ash.getParamDefs() ;
	if(ntvs==null||ntvs.length<=0)
		return ;
	for(NameTitleVal ntv:ntvs)
	{
%>


<%
		String[] vopts = ntv.getValOpts() ;
		if(vopts!=null&&vopts.length>0)
		{
%>
<%=ntv.getTitle() %>:
<select id="param_<%=ntv.getName() %> name="param_<%=ntv.getName() %>" lay-verify="" lay-ignore>

<%
			for(String vopt:vopts)
			{
%>
  <option value="<%=vopt%>"><%=vopt%></option>
<%
			}
%>
</select>
<%
		}
		else if(ntv.isValMultiLines())
		{
%><%=ntv.getTitle() %>:
<textarea id="param_<%=ntv.getName() %> name="param_<%=ntv.getName() %>" lay-ignore></textarea>
<%
		}
		else
		{
%><%=ntv.getTitle() %>:
<input id="param_<%=ntv.getName() %> name="param_<%=ntv.getName() %>" lay-ignore/>
<%
		}
%><br><%
	}
%>