<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,org.json.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	org.iottree.driver.zigbee.ember.*,
	org.iottree.driver.zigbee.ember.ezsp.*,
	org.iottree.driver.zigbee.*,
	org.iottree.driver.zigbee.zspec.*,
	org.iottree.driver.zigbee.ember.req_rsp.*,
	org.iottree.driver.zigbee.ember.*,
	java.util.*"%><%!
	
	
	static class PkASH
	{
		String src ;
		
		ASHFrame frm = null;
		
		String res = "" ;
		
		String dir = "" ;
		
		public String toHtml()
		{
			return "<tr><td>"+dir+":"+src+"</td><td>"+dir+": "+(frm!=null?frm.toShortString():"")+"</td><td>"+res+"</td></tr>" ;
		}
	}
	
	static class PkEZSP
	{
		String src ;
		
		EZSPFrame frm = null;
		
		String res = "" ;
		
		String dir = "" ;
		
		public String toHtml()
		{
			return "<tr><td>"+dir+":"+src+"</td><td>"+dir+": "+frm+"</td><td>"+res+"</td></tr>" ;
		}
	}
	
	static PkASH packEmberFrm(EmberManager em_drv,String dir,String ln) throws Exception
	{
		ln = ln.substring(5) ;
		ln = ln.replaceAll("-", "") ;
		
		byte[] bs = Convert.hexStr2ByteArray(ln) ;
		//String pres = TestParser.parseAshToEzspFrameId(bs) ;
		//System.out.println("***="+pres) ;
		StringBuilder failedr = new StringBuilder() ;
		PkASH r = new PkASH() ;
		r.frm = ASHFrame.parseFrom(em_drv,bs,bs.length,true,failedr);
		if(r.frm==null)
			r.res="??? "+failedr ;
		r.src = Convert.byteArray2HexStr(bs, "-");
		r.dir = dir;
		return r;
	}
	
	static List<PkASH> packEmber(EmberManager em_drv,String input_txt) throws Exception
	{//System.out.println(input_txt) ;
		ArrayList<PkASH> rets = new ArrayList<>() ;
		if(Convert.isNullOrEmpty(input_txt))
			return rets ;
		BufferedReader br = new BufferedReader(new StringReader(input_txt)) ;
		String ln ;
		String cur_dir = "" ;
		while((ln=br.readLine())!=null)
		{
			if(Convert.isNullOrEmpty(ln))
				continue ;
			ln = ln.trim() ;
			if(ln.startsWith("HEX: "))
			{
				PkASH pm = packEmberFrm(em_drv,cur_dir,ln) ;
				cur_dir = "" ;
				if(pm==null)
					continue ;
				rets.add(pm) ;
			}
			else
			{
				int k = ln.indexOf("->") ;
				if(k>0)
				{
					String src = ln.substring(0,k) ;
					String tar = ln.substring(k+2) ;
					if(src.indexOf("TCP")>=0)
						src = "HOST" ;
					else if(src.indexOf("串口")>=0)
						src = "NCP";
					else
						src="" ;
					if(tar.indexOf("TCP")>=0)
						tar = "HOST" ;
					else if(tar.indexOf("串口")>=0)
						tar = "NCP";
					else
						tar="" ;
					cur_dir = src+"->"+tar ;
				}
			}
		}
		return rets ;
	}
	
	
	static PkEZSP packEZSPFrm(EmberManager em_drv,boolean b_legacy,String dir,String ln) throws Exception
	{
		ln = ln.replaceAll("-", "") ;
		
		byte[] bs = Convert.hexStr2ByteArray(ln) ;
		//String pres = TestParser.parseAshToEzspFrameId(bs) ;
		//System.out.println("***="+pres) ;
		StringBuilder failedr = new StringBuilder() ;
		PkEZSP r = new PkEZSP() ;
		if(b_legacy)
			r.frm = EZSPFrame.unpackLegacy(em_drv,bs,0,bs.length,failedr) ;
		else
			r.frm = EZSPFrame.unpack(em_drv,bs,0,bs.length,failedr) ;
		if(r.frm==null)
			r.res="??? "+failedr ;
		r.src = Convert.byteArray2HexStr(bs, "-");
		r.dir = dir;
		return r;
	}
	
	static List<PkEZSP> packEzsp(EmberManager em_drv,boolean b_legacy,String input_txt) throws Exception
	{//System.out.println(input_txt) ;
		ArrayList<PkEZSP> rets = new ArrayList<>() ;
		if(Convert.isNullOrEmpty(input_txt))
			return rets ;
		BufferedReader br = new BufferedReader(new StringReader(input_txt)) ;
		String ln ;
		String cur_dir = "" ;
		while((ln=br.readLine())!=null)
		{
			if(Convert.isNullOrEmpty(ln))
				continue ;
			ln = ln.trim() ;
			if(ln.startsWith("#"))
				continue;
			{
				PkEZSP pm = packEZSPFrm(em_drv,b_legacy,cur_dir,ln) ;
				cur_dir = "" ;
				if(pm==null)
					continue ;
				rets.add(pm) ;
			}
			
		}
		return rets ;
	}
	
	static boolean ember_send(ZSDriver dd,String req,JSONObject pm,StringBuilder failedr) throws Exception
	{
		EmberDriver zsd = (EmberDriver)dd;
		if(pm==null)
			pm = new JSONObject() ;
		EmberManager zsm = zsd.getManager() ;
		switch(req)
		{
		case "version":
			zsm.RT_sendVersionCmd();
			return true;
		case "reset":
			zsm.RT_sendReset() ;
			return true ;
		case "get_value":
			int val_id = pm.optInt("value_id",-1) ;
			if(val_id<0)
			{
				failedr.append("no value_id input") ;
				return false;
			}
			GetValueCmd gvc = new GetValueCmd(zsm,val_id) ;
			ASHFrmData fd = ASHFrmData.createFrmDataCmd(zsm, gvc) ;
			zsm.RT_sendFrmData(fd) ;
			return true ;
		case "set_config_val":
			int conf_id = pm.optInt("conf_id",-1) ;
			int conf_val = pm.optInt("conf_val",-1) ;
			if(conf_id<0)
			{
				failedr.append("no conf_id input") ;
				return false;
			}
			SetConfigValCmd cvc = new SetConfigValCmd(zsm,conf_id,conf_val) ;
			fd = ASHFrmData.createFrmDataCmd(zsm, cvc) ;
			zsm.RT_sendFrmData(fd) ;
			return true;
		}
		
		failedr.append("unknown req="+req) ;
		return false;
	}
	
	static Object ember_syn(ZSDriver dd,String req,JSONObject pm,StringBuilder failedr) throws Exception
	{
		EmberDriver zsd = (EmberDriver)dd;
		if(pm==null)
			pm = new JSONObject() ;
		EmberManager zsm = zsd.getManager() ;
		switch(req)
		{
		case "version":
			return GetValueRR.ezspGetVersion(zsm, failedr) ;
		}
		failedr.append("unknown req") ;
		return null ;
	}
	
	
	static String print_jo(String input_txt) throws Exception
	{//System.out.println(input_txt) ;
		if(Convert.isNullOrEmpty(input_txt))
			return "" ;
		StringWriter sw = new StringWriter() ;
		if(input_txt.startsWith("["))
		{
			JSONArray tmpjo = new JSONArray(input_txt);
			Convert.printJSON(sw,tmpjo,"ID") ;
		}
		else
		{
			JSONObject tmpjo = new JSONObject(input_txt);
			Convert.printJSON(sw,tmpjo,"ID") ;
		}
		
		return sw.toString() ;
	}
%><%
	if(!Convert.checkReqEmpty(request, out, "path","op"))
		return ;

	String path = request.getParameter("path") ;
	String op=request.getParameter("op");
	String req=request.getParameter("req");
	String input_txt=request.getParameter("input_txt");
	String uid=request.getParameter("uid");
	String pm=request.getParameter("pm");
	String title=request.getParameter("title");
	String ieee=request.getParameter("ieee");
	
	boolean permit = "true".equals(request.getParameter("permit")) ;
	JSONObject pm_jo = null ;
	if(Convert.isNotNullEmpty(pm) && pm.startsWith("{"))
		pm_jo = new JSONObject(pm) ;
	
	StringBuilder failedr = new StringBuilder() ;
	ZSDriver z_dd = ZigbeeManager.getZSDriverByPath(path, failedr) ;
	if(z_dd==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	ZSCoor z_coor = z_dd.getZSCoor();//.getCoor() ;
	//ZStackDriver z_dd = z_mgr.getDriver() ;
	
	ZSDevice input_ed = null ;
	if(Convert.isNotNullEmpty(ieee))
	{
		input_ed = z_coor.getEndDeviceByIEEEAddr(ieee) ;
		if(input_ed==null)
		{
			out.print("no ed found") ;
			return ;
		}
	}
	
	EmberDriver em_drv = (EmberDriver)z_dd ;
	EmberManager emgr= em_drv.getManager() ;
switch(op)
{

case "ember_send":
	if(!Convert.checkReqEmpty(request, out, "req"))
		return;
	
	
	//em_drv.getManager().RT_sendFrmData(fd) ;
	if(ember_send(em_drv,req,pm_jo,failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	return ;
case "ember_syn":
	if(!Convert.checkReqEmpty(request, out, "req"))
		return;
	Object objv = ember_syn(em_drv,req,pm_jo,failedr);
	if(objv!=null)
		out.print("succ="+objv) ;
	else
		out.print(failedr.toString()) ;
	return ;
case "pack_ember":
	out.print("<table style='width:100%'><tr><td style='min-width:300px;'></td><td style='width:60%;'></td><td style='width:5%;'></td></tr>");
	for(PkASH pkm: packEmber(emgr,input_txt))
	{
		out.print(pkm.toHtml()) ;
	}
	out.print("</table>");
	return ;
case "pack_ezsp":
	boolean b_legacy = "true".equals(request.getParameter("legacy")) ;
	out.print("<table style='width:100%'><thead><tr><td style='width:40%;'></td><td></td><td></td></tr></thead><tbody>");
	for(PkEZSP pkm: packEzsp(emgr,b_legacy,input_txt))
	{
		out.print(pkm.toHtml()) ;
	}
	out.print("</tbody></table>");
	return ;
case "coor_info":
	JSONObject coor_jo = z_coor.toJO() ;
	coor_jo.write(out, 4, 4);
	return ;
case "print_jo":
	String out_txt = print_jo(input_txt) ;
	out.print(out_txt) ;
	return ;

case "permit_join":
	if(!Convert.checkReqEmpty(request, out, "permit"))
		return;
	int sec = z_coor.MGR_setPermitJoin(permit,failedr);
	if(sec<0)
		out.print(failedr.toString()) ;
	else
		out.print("succ="+sec) ;
	return ;

}
%>