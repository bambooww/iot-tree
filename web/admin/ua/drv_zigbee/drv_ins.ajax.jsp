<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,org.json.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	org.iottree.driver.zigbee.zstack.*,
	org.iottree.driver.zigbee.zstack.msg_sys.*,
	org.iottree.driver.zigbee.zstack.msg_zdo.*,
	org.iottree.driver.zigbee.zstack.msg_util.*,
	org.iottree.driver.zigbee.zstack.req_rsp.*,
	org.iottree.driver.zigbee.*,
	org.iottree.driver.zigbee.zspec.*,
	
	org.iottree.driver.zigbee.ember.*,
	java.util.*"%><%!
	
	static boolean send_req(ZSDriver dd,String req,JSONObject pm,StringBuilder failedr) throws Exception
	{
		ZStackDriver zsd = (ZStackDriver)dd;
		switch(req)
		{
		case "ping":
			return zsd.RT_sendReq(new PingSREQ(), failedr) ;
		case "version":
			return zsd.RT_sendReq(new VersionSREQ(), failedr) ;
		case "nv_length":
			return zsd.RT_sendReq(new OsalNVLengthSREQ(0x82), failedr) ;
		case "mgmt_lqi":
			return zsd.RT_sendReq(new MgmtLQISREQ(), failedr);
		case "reg_cluster_id":
			if(pm==null||!pm.has("cluster_id"))
			{
				failedr.append("no cluster_id input");return false;
			}
			int cluster_id = pm.getInt("cluster_id") ;
			return zsd.RT_sendReq(new MsgCBRegisterSREQ(cluster_id), failedr);
		case "get_device_info":
			return zsd.RT_sendReq(new GetDeviceInfoSREQ(), failedr);
		case "startup_from_app":
			return zsd.RT_sendReq(new StartupFromAppSREQ(), failedr) ;
		}
		
		failedr.append("unknown req="+req) ;
		return false;
	}
	
	
	static MsgReqRsp req_rsp(ZSDriver dd,String req,JSONObject pm,StringBuilder failedr) throws Exception
	{
		ZStackDriver zsd = (ZStackDriver)dd;
		if(pm==null)
			pm = new JSONObject() ;
		ZStackManager zsm = zsd.getZStackManager() ;
		MsgReqRsp reqrsp = null ;
		switch(req)
		{
		case "nwk_key":
			reqrsp = new ReadNVNwkKey() ; 
			break;
		case "get_len_nv":
			reqrsp = new GetLenNV(0x002D);
			break ;
		case "read_ext_pan_id":
			reqrsp = new ReadNV_EXT_PAN_ID() ;
			break ;
		case "read_pan_id":
			reqrsp = new ReadNV_PANID() ;
			break ;
		case "read_ieee_addr":
			reqrsp = new ReadNV_IEEEAddr() ;
			break;
		case "nv_read":
			int nv_item_id = pm.optInt("nv_item_id",-1) ;
			if(nv_item_id<0)
			{
				failedr.append("nv_item_id=-1") ;
				return null;
			}
			reqrsp = new ReadNV_Common(nv_item_id) ;
			break ;
		}
		
		if(reqrsp!=null)
		{
			if(zsm.RT_synReqRsp(reqrsp, failedr))
				return reqrsp ;
			else
				return null ;
		}
		
		failedr.append("unknown req="+req) ;
		return null;
	}
	
	
	static class PkMsg
	{
		String src ;
		
		Msg msg = null;
		
		String res = null ;
		
		public String toHtml()
		{
			return "<tr><td>"+src+"</td><td>"+msg+"</td><td>"+res+"</td></tr>" ;
		}
	}
	
	static class PkASH
	{
		String src ;
		
		ASHFrame frm = null;
		
		String res = "" ;
		
		String dir = "" ;
		
		public String toHtml()
		{
			return "<tr><td>"+dir+":"+src+"</td><td>"+dir+": "+frm+"</td><td>"+res+"</td></tr>" ;
		}
	}
	
	static PkMsg packAnaMsg(String ln) throws Exception
	{
		if(Convert.isNullOrEmpty(ln))
			return null ;
		ln = ln.trim() ;
		if(!ln.startsWith("FE-"))
			return null ;
		PkMsg r = new PkMsg() ;
		r.src = ln ;
		//System.out.println("ana=="+ln) ;
		ln = ln.replaceAll("-", "") ;
		byte[] bs = Convert.hexStr2ByteArray(ln) ;
		StringBuilder failedr = new StringBuilder() ;
		r.msg = MsgManager.unpackedMsg(bs, failedr) ;
		//System.out.println(r.src+"  msg=="+r.msg) ;
		r.res = failedr.toString() ;
		return r ;
	}
	
	static List<PkMsg> packAna(String input_txt) throws Exception
	{//System.out.println(input_txt) ;
		ArrayList<PkMsg> rets = new ArrayList<>() ;
		if(Convert.isNullOrEmpty(input_txt))
			return rets ;
		BufferedReader br = new BufferedReader(new StringReader(input_txt)) ;
		String ln ;
		while((ln=br.readLine())!=null)
		{
			PkMsg pm = packAnaMsg(ln) ;
			if(pm==null)
				continue ;
			rets.add(pm) ;
		}
		return rets ;
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
		input_ed = z_coor.getDeviceByIEEEAddr(ieee) ;
		if(input_ed==null)
		{
			out.print("no ed found") ;
			return ;
		}
	}
	
switch(op)
{
case "req":
	if(!Convert.checkReqEmpty(request, out, "req"))
		return;
	if(send_req(z_dd,req,pm_jo,failedr))
		out.print("succ");
	else
		out.print(failedr.toString()) ;
	return ;
case "req_rsp":
	if(!Convert.checkReqEmpty(request, out, "req"))
		return;
	
	MsgReqRsp rr = req_rsp(z_dd,req,pm_jo,failedr) ;
	if(rr!=null)
		out.print("succ="+rr);
	else
		out.print(failedr.toString()) ;
	return ;
case "ember_send":
	if(!Convert.checkReqEmpty(request, out, "req"))
		return;
	
	EmberDriver em_drv = (EmberDriver)z_dd ;
	//em_drv.getManager().RT_sendFrmData(fd) ;
	return ;
case "pack_ana":
	out.print("<table style='width:100%'>");
	for(PkMsg pkm: packAna(input_txt))
	{
		out.print(pkm.toHtml()) ;
	}
	out.print("</table>");
	return ;
case "pack_ember":
	
	return ;
case "zstack_init":
	if(z_coor.RT_init(failedr))
		out.print(" init end ok") ;
	else
		out.print(" init end err:"+failedr) ;
	return ;
case "coor_info":
	JSONObject coor_jo = z_coor.toJO() ;
	coor_jo.write(out, 4, 4);
	return ;
case "print_jo":
	String out_txt = print_jo(input_txt) ;
	out.print(out_txt) ;
	return ;
case "cache_mrr_uid":
	if(!Convert.checkReqEmpty(request, out, "uid"))
		return;
	MsgReqRsp mrr = ((ZStackCoor)z_coor).getZStackManager().getCachedMRR(uid) ;
	JSONObject c_mrr_jo = mrr.toDetailJO() ;
	c_mrr_jo.write(out,4,4) ;
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
case "ed_leave":
	if(!Convert.checkReqEmpty(request, out, "ieee"))
		return;
	boolean force = "true".equals(request.getParameter("force")) ;
	boolean rejoin = "true".equals(request.getParameter("rejoin")) ;
	boolean del_uadev = "true".equals(request.getParameter("del_uadev")) ;
	force = true ;
	rejoin = false;
	int del_res = z_coor.MGR_delEndDevice(ieee,force,rejoin,del_uadev,failedr) ;
	if(del_res<0)
		out.print(failedr.toString()) ;
	else if(del_res==0)
		out.print("succ="+failedr.toString()) ;
	else
		out.print("succ") ;
	return ;
case "eds_rt_list":
	List<ZSDevice> eds = z_coor.listDevices() ;
	JSONArray eds_rt_jarr = new JSONArray() ;
	if(eds!=null)
	{
		for(ZSDevice ed:eds)
		{
			eds_rt_jarr.put(ed.RT_toJO()) ;
		}
	}
	eds_rt_jarr.write(out) ;
	return ;
case "get_ep_addrs":
	eds = z_coor.listDevices() ;
	JSONArray eds_jarr = new JSONArray() ;
	if(eds!=null)
	{
		for(ZSDevice ed:eds)
			eds_jarr.put(ed.getIEEEAddr()) ;
	}
	eds_jarr.write(out) ;
	return ;
case "ed_set_title":
	if(!Convert.checkReqEmpty(request, out, "ieee","title"))
		return;
	if(z_coor.MGR_setEndDeviceTitle(ieee, title))
		out.print("succ") ;
	else
		out.print("set title failed") ;
	return ;
case "ed_syn_dev":
	if(!Convert.checkReqEmpty(request, out, "ieee"))
		return ;
	if(z_coor.MGR_synEndDeviceToUADev(ieee,failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	return ;
case "refresh_ed":
	if(!Convert.checkReqEmpty(request, out, "ieee"))
		return ;
	if(z_coor.MGR_putQueueEndDevice(ieee, failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	return ;
}
%>