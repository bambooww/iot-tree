package org.iottree.core.util.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.iottree.core.Config;
import org.iottree.core.plugin.PlugAuth;
import org.iottree.core.plugin.PlugAuthUser;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.SecureUtil;

/**
 * 1,admin user login support
 * 2, TODO access
 * @author jason.zhu
 *
 */
public class LoginUtil
{
	static class UserAuthItem
	{
		String username ;
		
		String salt ;
		
		String encPsw ;
		
		UserAuthItem(String usern,String salt,String encpsw)
		{
			this.username = usern ;
			this.salt = salt ;
			this.encPsw = encpsw ;
		}
	}
	
	public static class SessionItem
	{
		public String lan = null ;
		
		public long loginDT  =System.currentTimeMillis();
		
		public SessionItem(String lang)
		{
			this.lan = lang ;
		}
	}
	
	private static final String ADMIN_LOGIN = "_admin" ;
	
	
	
	private static UserAuthItem loadUserAuthItem(String username) throws IOException
	{
		String authf = Config.getDataDirBase()+"/auth/"+username+".txt" ;
		File f = new File(authf) ;
		if(!f.exists())
			return null ;
		
		List<String> ss = Convert.readFileTxtLines(f, "UTF-8") ;
		if(ss==null)
			return null ;
		if(ss.size()<2)
			return null ;
		
		return new UserAuthItem(username,ss.get(0),ss.get(1)) ;
	}
	
	private synchronized static UserAuthItem saveUserAuthItem(String username,String psw) throws Exception
	{
		String salt = SecureUtil.generateSalt() ;
		String encpsw = SecureUtil.encryptPsw(psw, salt) ;
		String authf = Config.getDataDirBase()+"/auth/"+username+".txt" ;
		File f = new File(authf) ;
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs() ;
		try(FileOutputStream fos = new FileOutputStream(f);)
		{
			fos.write((salt+"\r\n").getBytes()) ;
			fos.write(encpsw.getBytes()) ;
		}
		return new UserAuthItem(username,salt,encpsw) ;
	}
	
	
	
	public static boolean doLogin(HttpServletRequest req,String username,String password,String lang) throws Exception
	{
		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
		if(pa!=null && pa.canCheckAdminUser())
		{
			PlugAuthUser u = pa.checkAdminUser(username, password);
			if(u==null)
				return false;
			req.getSession().setAttribute(ADMIN_LOGIN, new SessionItem(lang));
			return true ;
		}
		
		
		if(!"admin".equals(username))
			return false;
		UserAuthItem uai = loadUserAuthItem(username) ;
		if(uai==null)
		{//first
			uai = saveUserAuthItem(username,password) ;
		}
		else
		{
			boolean r = SecureUtil.checkPsw(password, uai.encPsw, uai.salt) ;
			if(!r)
				return false;
		}
		
		req.getSession().setAttribute(ADMIN_LOGIN, new SessionItem(lang));
		return true ;
	}
	
	public static void doLogout(HttpServletRequest req)
	{
		req.getSession().removeAttribute(ADMIN_LOGIN) ;//, false);
	}
	/**
	 * check admin has set his psw or not
	 * @return
	 * @throws IOException 
	 */
	public static boolean checkAdminSetPsw() throws IOException
	{
		return null!= loadUserAuthItem("admin") ;
	}
	
	public static boolean chgPsw(String username,String oldpsw,String newpsw,StringBuilder failedr) throws Exception
	{
		if(!"admin".equals(username))
			return false;
		
		if(Convert.isNullOrEmpty(oldpsw) || Convert.isNullOrEmpty(newpsw))
		{
			failedr.append("illegal input") ;
			return false;
		}
		UserAuthItem uai = loadUserAuthItem(username) ;
		if(uai==null)
		{
			failedr.append("no auth inf") ;
			return false;
		}
		
		boolean r = SecureUtil.checkPsw(oldpsw, uai.encPsw, uai.salt) ;
		if(!r)
		{
			failedr.append("check old failed") ;
			return false;
		}
		
		saveUserAuthItem(username,newpsw) ;
		return true;
	}
	/**
	 * check admin login or not 
	 * @param req
	 * @param username
	 * @return
	 */
	public static boolean checkAdminLogin(HttpServletRequest req)
	{
		return checkAdminLogin(req.getSession());
	}
	
	public static boolean checkAdminLogin(HttpSession hs)
	{
		SessionItem si = getAdminLoginSession(hs) ;
		return si!=null;
	}
	
	public static SessionItem getAdminLoginSession(HttpSession hs)
	{
		if(hs==null)
			return null;
		return (SessionItem)hs.getAttribute(ADMIN_LOGIN);
	}
}
