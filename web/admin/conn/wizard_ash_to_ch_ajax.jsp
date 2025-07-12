<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.conn.*,
				org.iottree.core.basic.*,
				org.json.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%!
%><%
if(!Convert.checkReqEmpty(request, out, "jsontxt"))
	return;

String jsontxt = request.getParameter("jsontxt") ;
//{"drv":"modbus_rtu","devids":["5hfkm6JaccZuKXxZjgk","CDwSLRBNDoznP9uSE6J"],
	// "conn_nts":[{"connid":"a","name":"m1","title":"啊啊"},{"connid":"aa","name":"m2","title":"机器1"}]}
//System.out.println(jsontxt);
JSONObject jobj = new JSONObject(jsontxt) ;
String prjid = jobj.getString("prjid") ;
String cpid = jobj.getString("cpid") ;
if(Convert.isNullOrEmpty(prjid) || Convert.isNullOrEmpty(cpid))
{
	out.print("no prj or cpid input") ;
	return;
}
String drv = jobj.getString("drv") ;
if(Convert.isNullOrEmpty(drv))
{
	out.print("no driver input") ;
	return ;
}

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
ConnProvider cp = ConnManager.getInstance().getConnProviderById(prjid, cpid) ;
DevDriver dd = DevManager.getInstance().getDriver(drv) ;
if(prj==null || cp==null || dd==null)
{
	out.print("no prj or cp or driver found") ;
	return ;
}
if(!(cp instanceof ConnProTcpServer))
{
	out.print("not conn tcp server provider") ;
	return ;
}
JSONArray devs = jobj.getJSONArray("devs") ;
JSONArray conn_nts = jobj.getJSONArray("conn_nts") ;
int dev_num = devs.length();
int nts_num = conn_nts.length() ;
if(nts_num<=0||dev_num<=0)
{
	out.print("no conntions or devices input") ;
	return ;
}

for(int i = 0 ; i < nts_num ; i ++)
{
	JSONObject nt = conn_nts.getJSONObject(i) ;
	String connid = nt.getString("connid") ;
	String name = nt.getString("name") ;
	String title = nt.getString("title") ;
	
	JSONObject tmpjo = new JSONObject() ;
	tmpjo.put("id", connid) ;
	tmpjo.put("name", name) ;
	//tmpjo.put("t", connid) ;
	tmpjo.put("sock_connid",connid) ;
	ConnPt cpt = cp.setConnPtByJson(tmpjo) ;
	
	//add ch
	UACh ch = prj.addCh(drv,true, name, title, "", null) ;
	//add dev
	for(int j = 0 ; j < dev_num ; j ++)
	{
		JSONObject devob = devs.getJSONObject(j);
		String devid = devob.getString("id") ;
		String devname = devob.getString("dev_name") ;
		JSONArray devprops = devob.optJSONArray("dev_props");
		//JSONArray chprops = devob.optJSONArray("ch_props");
		UADev tmpdev = ch.addDev(devname, devname, "", devid);
		if(devprops!=null)
		{
			int pnum = devprops.length();
			for(int k = 0 ; k < pnum ; k ++)
			{
				JSONObject dp = devprops.getJSONObject(k) ;
				String pg = dp.getString("pg") ;
				String pn = dp.getString("pn") ;
				String strv = dp.getString("pv") ;
				tmpdev.setPropValue(pg, pn, strv) ;
			}
		}
		
	}
	
	ConnManager.getInstance().setConnJoin(prjid, connid, ch.getId()) ;
}


prj.save(true);

%>succ