package org.iottree.core.conn.ext;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.basic.NameTitleVal;
import org.iottree.core.conn.ConnProTcpServer;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;

/**
 * accept socket with remote ip addr map to socket connid
 * @author jason.zhu
 *
 */
public class ASHFixIP  implements ConnProTcpServer.AcceptedSockHandler
{
	boolean chkIP = true ;
	
	boolean chkPort = false;


	List<String> limitIds = null ;
	
	@Override
	public String getName()
	{
		return "fix_ip";
	}
	

	public String getTitle()
	{
		return "Fix IP" ;
	}

	@Override
	public String checkSockConnId(Socket sock) throws Exception
	{
		String tmps = "" ;
		if(chkIP)
			tmps += sock.getInetAddress().getHostAddress() ;
		if(chkPort)
			tmps += ":"+sock.getPort();
		return tmps;
	}

	@Override
	public int getRecvTimeout()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRecvEndTimeout()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	private static NameTitleVal[] PDS = new NameTitleVal[] {
			new NameTitleVal("chk_client_ip","Check Client IP",new String[] {"check","ignore"}),
			new NameTitleVal("chk_client_port","Check Client Port",new String[] {"check","ignore"}),
			new NameTitleVal("limit_ids","Limit IDs",true).setDesc("ip1:port1,ip2:port2 or ip1,ip2 ,etc"),
			} ;
	private static String[] PTS = new String[] {"Client IP","Client Port"} ;

	@Override
	public NameTitleVal[] getParamDefs()
	{
		return PDS;
	}

	public String[] getParamTitles()
	{
		return PTS;
	}

	@Override
	public XmlData chkAndCreateParams(HashMap<String, String> pn2strv, StringBuilder failedr)
	{
		boolean chk_ip = "check".equals(pn2strv.get("chk_client_ip")) ;
		
		XmlData xd = new XmlData() ;
		xd.setParamValue("chk_client_ip", chk_ip);
		
		boolean chk_port = "check".equals(pn2strv.get("chk_client_port")) ;
		xd.setParamValue("chk_client_port", chk_port);
		
		String limit_ids = pn2strv.get("limit_ids") ;
		if(limit_ids!=null)
		{
			List<String> ids = Convert.splitStrWith(limit_ids, ",|\r\n") ;
			xd.setParamValues("limit_ids", ids);
		}
		return xd;//succ
	}


	@Override
	public void setParams(XmlData xd)
	{
		this.chkIP = xd.getParamValueBool("chk_client_ip",true) ;
		this.chkPort = xd.getParamValueBool("chk_client_port", false) ;
		this.limitIds = xd.getParamXmlValStrs("limit_ids") ;
	}

}
