package org.iottree.ext.msg_net;

import java.util.*;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class MailSender_NM extends MNNodeMid
{
	static ILogger log = LoggerManager.getLogger(MailSender_NM.class) ;
	
	static Lan lan = Lan.getLangInPk(TCPClient_NM.class) ;
	
	public static class MailAddr
	{
		String mail ;
		
		String name ;
		
		String encod ;
		
		MailAddr(String m,String n,String encod)
		{
			this.mail = m ;
			this.name = n ;
			this.encod = encod ;
		}
		
		public String getMail()
		{
			return this.mail ;
		}
		
		public String getName()
		{
			return this.name ;
		}
		
		public String getEncod()
		{
			return this.encod;
		}
		
//		public void setEmailFrom(MultiPartEmail email)
//		{
//	        if(Convert.isNotNullEmpty(name))
//	        	email.setFrom(mail, this.name,this.encod) ;
//	        else
//	        	email.setFrom(this.fromAddr.mail) ;
//		}
		
		public JSONObject toJO()
		{
			return new JSONObject().putOpt("mail", this.mail).putOpt("name", this.name).putOpt("encod",this.encod) ;
		}
		
		public static MailAddr fromJO(JSONObject jo)
		{
			if(jo==null)
				return null ;
			String m = jo.optString("mail") ;
			if(Convert.isNullOrEmpty(m))
				return null ;
			String n = jo.optString("name") ;
			String enc = jo.optString("encod") ;
			return new MailAddr(m,n,enc) ;
		}
		

		public static JSONArray toJArr(List<MailAddr> mas)
		{
			JSONArray ret = new JSONArray() ;
			if(mas==null)
				return ret;
			for(MailAddr ma:mas)
			{
				ret.put(ma.toJO());
			}
			return ret;
		}
		
		public static ArrayList<MailAddr> fromJArr(JSONArray jarr)
		{
			if(jarr==null)
				return null ;
			ArrayList<MailAddr> rets = new ArrayList<>();
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				MailAddr ma = MailAddr.fromJO(tmpjo) ;
				if(ma==null)
					continue ;
				rets.add(ma);
			}
			return rets;
		}
	}

	String smtpHost = null ;
	
	int smtpPort = 465;
	
	boolean bSsl = false;
	
	String authUser = null ;
	
	String authPsw = null ;
	
	MailAddr fromAddr = null ;
	
	/**
	 * fix to addr
	 */
	ArrayList<MailAddr> fixToAddrs = null ;
	
	ArrayList<MailAddr> fixCCAddrs = null ;
	
	ArrayList<MailAddr> fixBccAddrs = null ;
	
	
	public MailSender_NM()
	{
		
	}

	@Override
	public int getOutNum()
	{
		return 2;
	}


	@Override
	public String getTP()
	{
		return "mail_sender";
	}


	@Override
	public String getTPTitle()
	{
		return "Mail Sender";
	}


	@Override
	public String getColor()
	{
		return "#00a8f1";
	}


	@Override
	public String getIcon()
	{
		return "\\uf0e0";
	}
	
	public String getSmtpHost()
	{
		if(this.smtpHost==null)
			return "";
		return this.smtpHost ;
	}
	
	public int getSmtpPort()
	{
		return this.smtpPort ;
	}
	
	public boolean isSmtpSsl()
	{
		return bSsl;
	}

	public String getAuthUser()
	{
		if(this.authUser==null)
			return "" ;
		return this.authUser ;
	}
	
	public String getAuthPsw()
	{
		if(this.authPsw==null)
			return "";
		return this.authPsw ;
	}
	
	public MailAddr getFromAddr()
	{
		return this.fromAddr ;
	}
	
	public List<MailAddr> getFixToAddrs()
	{
		return this.fixToAddrs ;
	}
	
	public List<MailAddr> getFixCCAddrs()
	{
		return this.fixCCAddrs ;
	}
	
	public List<MailAddr> getFixBccAddrs()
	{
		return this.fixBccAddrs ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.smtpHost)||this.smtpPort<=0)
		{
			failedr.append("not smtp host or port set") ;
			return false;
		}
		if(fromAddr==null)
		{
			failedr.append("not from mail address set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("smtp_host", this.smtpHost) ;
		jo.putOpt("smtp_port", this.smtpPort) ;
		jo.put("smtp_ssl",this.bSsl);
		if(fromAddr!=null)
			jo.put("from", fromAddr.toJO()) ;
		jo.putOpt("auth_user", this.authUser) ;
		jo.putOpt("auth_psw", this.authPsw) ;
		
		JSONArray jarr = MailAddr.toJArr(this.fixToAddrs) ;
		jo.putOpt("fix_to", jarr) ;
		jarr = MailAddr.toJArr(this.fixCCAddrs) ;
		jo.putOpt("fix_cc", jarr) ;
		jarr = MailAddr.toJArr(this.fixBccAddrs) ;
		jo.putOpt("fix_bcc", jarr) ;
		return jo;
	}
	

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.smtpHost = jo.optString("smtp_host","") ;
		this.smtpPort = jo.optInt("smtp_port",465);
		this.bSsl = jo.optBoolean("smtp_ssl",false);
		JSONObject tmpjo = jo.optJSONObject("from") ;
		this.fromAddr = MailAddr.fromJO(tmpjo) ;
		this.authUser = jo.optString("auth_user","") ;
		this.authPsw = jo.optString("auth_psw","") ;
		JSONArray jarr = jo.optJSONArray("fix_to") ;
		this.fixToAddrs = MailAddr.fromJArr(jarr);
		jarr = jo.optJSONArray("fix_cc") ;
		this.fixCCAddrs = MailAddr.fromJArr(jarr);
		jarr = jo.optJSONArray("fix_bcc") ;
		this.fixBccAddrs = MailAddr.fromJArr(jarr);
	}
	
	private MultiPartEmail createEmail(StringBuilder failedr) throws EmailException
	{
		if(!isParamReady(failedr))
			return null ;
		MultiPartEmail email = new MultiPartEmail();
        email.setHostName(this.smtpHost);
        email.setSmtpPort(this.smtpPort);
        if(this.bSsl)
        	email.setSSLOnConnect(true);
        
        if(Convert.isNotNullEmpty(this.authUser))
        	email.setAuthenticator(new DefaultAuthenticator(this.authUser, this.authPsw));
        
        email.setFrom(this.fromAddr.mail, this.fromAddr.name,this.fromAddr.encod) ;
        
        return email ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		JSONObject pld = msg.getPayloadJO(null) ;
		if(pld==null)
		{
			RT_DEBUG_ERR.fire("mail", "no payload in");
			return null;
		}
		
		JSONArray to_jarr = pld.optJSONArray("to") ;
		String subject = pld.optString("subject") ;
		if(Convert.isNullOrEmpty(subject))
		{
			RT_DEBUG_ERR.fire("mail", "no mail subject in payload");
			return null ;
		}
		String content = pld.optString("content","") ;
		
		StringBuilder failedr = new StringBuilder() ;
		MultiPartEmail mpe = createEmail(failedr)  ;
        if(mpe==null)
        {
        	RT_DEBUG_ERR.fire("mail", failedr.toString());
        	return null;
        }
       
        boolean has_to = false;
        if(to_jarr!=null&&to_jarr.length()>0)
        {
        	for(int i = 0 ; i < to_jarr.length() ; i ++)
        	{
        		JSONObject tmpjo = to_jarr.getJSONObject(i) ;
        		MailAddr ma = MailAddr.fromJO(tmpjo) ;
        		if(ma==null)
        			continue ;
        		 mpe.addTo(ma.mail,ma.name);
        		 has_to = true ;
        	}
        }
        if(fixToAddrs!=null&&fixToAddrs.size()>0)
        {
        	for(MailAddr ma:this.fixToAddrs)
        	{
        		mpe.addTo(ma.mail,ma.name);
       		 	has_to = true ;
        	}
        }
        
        if(!has_to)
        {
        	RT_DEBUG_ERR.fire("mail", "no to set");
        	return null;
        }
        
        RT_DEBUG_ERR.clear("mail");
        
        mpe.setSubject(subject);
        mpe.setMsg(content);
        //email.attach(new ByteArrayDataSource("xxxxx", "text/csv"), "data.csv", "");
        mpe.send();
        
		return RTOut.createOutIdx().asIdxMsg(0, new MNMsg().asPayload(pld));
	}


}
