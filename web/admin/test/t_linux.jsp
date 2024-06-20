<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
		static void set_led_onoff(boolean bon) throws IOException
		{
				try(FileOutputStream fos = new FileOutputStream("/sys/class/leds/work/brightness");
						)
				{
					if(bon)
						fos.write("1".getBytes()) ;
					else
						fos.write("0".getBytes()) ;
				}
		}
%><%
	boolean bon = "true".equals(request.getParameter("on")) ;
set_led_onoff(bon) ;
%>