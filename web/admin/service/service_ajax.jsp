<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.service.*,
				org.iottree.core.basic.*,org.iottree.driver.opc.opcua.server.*,
	java.io.*,org.json.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%

if(!Convert.checkReqEmpty(request, out, "op"))
	return;
String op = request.getParameter("op") ;
String fn = request.getParameter("fn") ;
boolean b_trusted = "true".equals(request.getParameter("trusted")) ;
StringBuilder failedr = new StringBuilder() ;

try
{
switch(op)
{
case "start":
case "stop":
	if(!Convert.checkReqEmpty(request, out,"n"))
		return;
	String n = request.getParameter("n") ;
	
	AbstractService as = ServiceManager.getInstance().getService(n) ;
	System.out.println("nn==="+n+" as="+as) ;
	if(as==null)
	{
		out.print("no service found") ;
		return ;
	}
	if("start".equals(op))
		as.startService();
	else
		as.stopService() ;
	out.print("ok") ;
	break;
case "setup":
	if(!Convert.checkReqEmpty(request, out,"n"))
		return;
	n = request.getParameter("n") ;
	as = ServiceManager.getInstance().getService(n) ;
	if(as==null)
	{
		out.print("no service found") ;
		return ;
	}
	HashMap<String,String> pms = Convert.parseFromRequest(request, null);
	as.setService(pms);
	out.print("ok") ;
	break ;
case "opcua_cer_list":
	List<KeyStoreLoader.CertItem> trusted_cis = KeyStoreLoader.listTrustedCers() ;
	List<KeyStoreLoader.CertItem> rejected_cis = KeyStoreLoader.listRejectedCers() ;
	JSONObject retjo = new JSONObject() ;
	JSONArray trusted_jarr = new JSONArray() ;
	retjo.put("trusted",trusted_jarr) ;
	JSONArray rejected_jarr = new JSONArray() ;
	retjo.put("rejected",rejected_jarr) ;
	for(KeyStoreLoader.CertItem ci:trusted_cis)
	{
		trusted_jarr.put(ci.toJO()) ;
	}
	for(KeyStoreLoader.CertItem ci:rejected_cis)
	{
		rejected_jarr.put(ci.toJO()) ;
	}
	retjo.write(out) ;
	break ;
case "opcua_trust_cert":
	if(!Convert.checkReqEmpty(request, out,"fn"))
		return;
	if(KeyStoreLoader.trustCer(fn, failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	break;
case "opcua_reject_cert":
	if(!Convert.checkReqEmpty(request, out,"fn"))
		return;
	if(KeyStoreLoader.rejectCer(fn, failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	break;
case "opcua_del_cert":
	if(!Convert.checkReqEmpty(request, out,"fn","trusted"))
		return;
	if(KeyStoreLoader.deleteCer(b_trusted, fn))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	break;
}
}
catch(Exception eee)
{
	eee.printStackTrace();
	out.print(eee.getMessage()) ;
}
%>