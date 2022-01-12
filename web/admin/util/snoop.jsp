<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import = "java.io.PrintStream,java.util.* , java.io.* , java.net.*,org.iottree.core.* " %>
<%
  StringBuffer u = request.getRequestURL() ;
  URL tmpu = new URL(u.toString()) ;

%>
tbs_data=<%=Config.getDataDirBase() %><br/>
conf_dir=<%=Config.getConfigFileBase()%><br/>
webapp_dir=<%=Config.getWebappBase() %><br/>
<br/>
<%

	String lf=  request.getParameter("lf") ;
	if(lf!=null&&!lf.equals(""))
	{
		//lf = WebUtil.decodeHexUrl(lf) ;
%>
list file at=<%=lf %><br/>
<%
		File f = new File(lf);
		String addfn = request.getParameter("addfn") ;
		if(f.exists()&&addfn!=null&&!"".equals(addfn))
		{
			String txt = request.getParameter("addfn_txt") ;
			if(txt==null)
				txt = "" ;
			File f0 = new File(f,addfn) ;
			if(!f0.exists())
			{
				FileOutputStream fos = null ;
				try
				{
					fos = new FileOutputStream(f0) ;
					fos.write(txt.getBytes());
				}
				finally
				{
					if(fos!=null)
						fos.close() ;
				}
			}
		}
		File pf = f.getParentFile() ;
		if(pf!=null&&pf.exists())
		{
%>
&nbsp;&nbsp;&nbsp;<a href="snoop.jsp?lf=<%=pf.getAbsolutePath() %>">..</a><br/>
<%			
		}

		File[] subfs = f.listFiles() ;
		if(subfs!=null)
		for(int k = 0 ; k < subfs.length ; k ++)
		{
			File tmpf = subfs[k];
			if(tmpf.isDirectory())
			{
%>
&nbsp;&nbsp;&nbsp;<a href="snoop.jsp?lf=<%=tmpf.getAbsolutePath() %>"><%=tmpf.getAbsolutePath() %></a><br/>
<%
			}
			else
			{
%>
&nbsp;&nbsp;&nbsp;<%=tmpf.getAbsolutePath() %> len=<%=tmpf.length()/1024 %>K <a target="_blank" href="snoop_download.jsp?f=<%=tmpf.getAbsolutePath() %>&ln=1024">download</a><br/>
<%
			}
		}
	}
%>
GetRequest Dispatcher<%=request.getRequestDispatcher("snoop.jsp") %><br/>
cur run dir=<%=new File(".").getCanonicalPath() %><br/>
Is JspPage=<%=(this instanceof JspPage) %><br/>
My Page Java Class=<%="" %><br/>
Is ServletConfig=<%=(this instanceof ServletConfig) %><br/>
Your IP=<%=request.getRemoteAddr()%><br/>
Domain=<%=request.getRequestURL()%><br/>
ServerName=<%=request.getServerName() %><br/>
Host = <%=tmpu.getHost()%><br/>
Port = <%=tmpu.getPort()%><br/>
Path = <%=tmpu.getPath()%><br/>
Path Info = <%=request.getPathInfo() %><br/>
Real Path=<%= this.getServletContext().getRealPath(request.getServletPath()) %><br/>
Path Translated=<%=request.getPathTranslated() %><br/>
<%
	InetAddress addr = InetAddress.getLocalHost() ;
%>
request real path = <%=request.getRealPath(request.getRequestURI()) %>
Server IP=<%=addr.getHostAddress()%><br>
request uri=<%=request.getRequestURI()%><br>
request pathinfo=<%=request.getPathInfo()%><br>
request contextpath=<%=request.getContextPath()%><br>
request querystring=<%=request.getQueryString()%><br>
servlet path=<%=request.getServletPath() %><br>

<%
 ServletContext context = config.getServletContext()  ;
 for (Enumeration enums = context.getAttributeNames()  ; enums.hasMoreElements();)
 {
     String name = (String)enums.nextElement () ;
     Object val = context.getAttribute (name) ;
%>
     <%=name%>=<%=val%><br><br>
<%
 }
%>

<br/>----req param------------<br/>
<%

 for (Enumeration en = request.getParameterNames()  ; en.hasMoreElements();)
 {
     String name = (String)en.nextElement () ;
     Object val = request.getParameter(name) ;
%>
     <%=name%>=<%=val%><br><br>
<%
 }

 RequestDispatcher rd = request.getRequestDispatcher("abc.jsp") ;
 if(rd==null)
 {
   out.println("dispatcher is null!") ;
   return ;
 }
 //rd.include(request, response);
 String rp = context.getRealPath("/") ;
%>
<br>
<%=rp%>
<br>
servletname=<%=config.getServletName()%>
<br>
contextname=<%=context.getServletContextName()%>
<br>
serverinfo=<%=context.getServerInfo()%>
<br>
------------------------------------init parameter of context<br>
<%
	for(Enumeration ee = context.getInitParameterNames() ;ee.hasMoreElements();)
	{
		String n = (String)ee.nextElement() ;
		String v = context.getInitParameter(n);
%>
		<%=n%>=<%=v%><br>
<%
	}
%>
<br>--------------------------------<br>
<%
	ServletContext ocontext = context.getContext("/tomcat-docs") ;
	String ocontInfo = null ;

	if(ocontext!=null)
	{
	/*
	System.out.println("ocontext is not null!") ;
		ocontInfo = ocontext.getServletContextName() ;
		RequestDispatcher rd0 = ocontext.getRequestDispatcher("/mm.jsp") ;
		 if(rd0==null)
		 {
		   System.out.println("dispatcher is null!") ;
		 }
		 else
		 {
		 System.out.println("dispatcher is not null!") ;
		 	rd0.include(request, response);
		 }
		 */
	}
	else
		ocontInfo = "no other context gotten" ;
%>
<%=ocontInfo%><br>
<p>
<b>syste props</b>
<br>
<%
  Properties ps = java.lang.System.getProperties() ;
  for(Enumeration en = ps.propertyNames();en.hasMoreElements();)
  {
    String n = (String)en.nextElement() ;
%>
 <%=n%>=<%=ps.getProperty(n)%><br>
<%
  }
%>