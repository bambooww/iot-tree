package org.iottree.core.ws;

import java.util.List;
import java.util.Map;

import javax.websocket.Session;


public abstract class WSServer// extends ConnServer
{

	protected String getSessionHead(Session session,String name)
	{
		Map<String, List<String>> heads = (Map<String, List<String>>) session.getUserProperties().get("req_head");
		
		List<String> vvs = heads.get(name);
		if (vvs == null || vvs.size() <= 0)
		{
			return null;
		}
		return vvs.get(0);
	}
	
//	public abstract ConnServer getConnServer() ;
//
//	ConnAuth getAuthFromSession(String connid, Session session)
//	{
//		// get apnid and apnauth
//		
//		//Map<String, List<String>> heads = (Map<String, List<String>>) session.getUserProperties().get("req_head");
//
//		String r_enc = getSessionHead(session,ConnAuth.PN_AUTH_R_KEY);
//		String id_enc = getSessionHead(session,ConnAuth.PN_AUTH_ID_KEY);
//		
//		String bdef = getSessionHead(session,ConnAuth.PN_AUTH_DEFAULT);
//		boolean bdefault_key = "1".equals(bdef) ;
//				
//		
//		String apnkey = null;
//		if(bdefault_key)
//			apnkey = ConnAuth.DEFAULT_KEY ;
//		else
//			apnkey = getConnServer().getSupporter().getConnKey(connid);
//		
//		ConnAuth serverauth = new ConnAuth(connid, apnkey);
//
//		try
//		{
//			// 使用client提供的随机密钥
//			serverauth.setRandomDesKeyEncoded(r_enc);
//			if (!serverauth.checkConnIdEncoded(id_enc))
//			{
//				return null;// 验证失败
//			}
//		}
//		catch (Exception ee)
//		{
//			ee.printStackTrace();
//			return null;
//		}
//		return serverauth;
//	}
	
}
