package org.iottree.core.util.web;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.iottree.core.Config;
import org.iottree.core.node.PlatNode;
import org.iottree.core.node.PlatNodeManager;
import org.iottree.core.plugin.PlugAuth;
import org.iottree.core.plugin.PlugAuthUser;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.SecureUtil;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;
import org.w3c.dom.Element;

/**
 * 1,admin user login support
 * 2, TODO access
 * @author jason.zhu
 *
 */
public class LoginUtil
{
	static ILogger log = LoggerManager.getLogger(LoginUtil.class) ;
	
	public static class UserAuthItem
	{
		String username ;
		
		String disname ;//显示名称
		
		String salt ;
		
		String encPsw ;
		
		List<String> roles = null ;
		
		private UserAuthItem() {}
		
		UserAuthItem(String usern,String disname,String salt,String encpsw)
		{
			this.username = usern ;
			this.disname = disname ;
			this.salt = salt ;
			this.encPsw = encpsw ;
		}
		
		public String getUserName()
		{
			return this.username ;
		}
		
		public String getDisName()
		{
			return this.disname ;
		}
		
		public List<String> getRoleNames()
		{
			return this.roles ;
		}
		
		public JSONObject toListJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("usern",this.username) ;
			jo.putOpt("disn", this.disname) ;
			return jo ;
		}
		
		JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("usern",this.username) ;
			jo.putOpt("disn", this.disname) ;
			jo.putOpt("salt", this.salt) ;
			jo.putOpt("enc_psw", this.encPsw) ;
			jo.putOpt("roles", Convert.combineStrWith(this.roles, ',')) ;
			return jo ;
		}
		
		static UserAuthItem fromJO(JSONObject jo)
		{
			UserAuthItem ret = new UserAuthItem() ;
			ret.username = jo.optString("usern") ;
			if(Convert.isNullOrEmpty(ret.username))
				return null ;
			ret.disname = jo.optString("disn") ;
			ret.salt = jo.optString("salt") ;
			ret.encPsw = jo.optString("enc_psw") ;
			ret.roles = Convert.splitStrWith(jo.optString("roles"), ",|") ;
			return ret;
		}
	}
	
	public static class Role
	{
		String role_n ;
		
		String role_t;
		
		public String getRoleName()
		{
			return this.role_n ;
		}
		
		public String getRoleTitle()
		{
			return this.role_t ;
		}
		
		JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("role_n",this.role_n) ;
			jo.putOpt("role_t", this.role_t) ;
			return jo ;
		}
		
		static Role fromJO(JSONObject jo)
		{
			Role ret = new Role() ;
			ret.role_n = jo.optString("role_n") ;
			if(Convert.isNullOrEmpty(ret.role_n))
				return null ;
			ret.role_t = jo.optString("role_t") ;
			return ret;
		}
	}

	public static class SessionItem
	{
		public String sess_id ;
		public String usern = null ;
		public String disn = null ;
		public String lan = null ;
		
		public long loginDT  =System.currentTimeMillis();
		
		transient long lastAcc = System.currentTimeMillis() ;
		
		public SessionItem(String sess_id,String usern,String disn,String lang)
		{
			this.sess_id = sess_id;
			this.usern = usern ;
			this.disn = disn ;
			this.lan = lang ;
		}
		
		public boolean isAdmin()
		{
			return "admin".equals(usern) ;
		}
	}
	
	private static final String LOGIN_SK = "_login_sk" ;
	
	public static final String ATTRN_LOGIN_SESSION_TIMEOUT= "login_session_timeout";
	
	public static final String ATTRN_DEBUG_COOKIE_ONLY = "debug_cookie_only";
	
	public static final String ATTRN_CLIENT_COOKIE_ONLY = "client_cookie_only";
	
	public static final String ATTRN_LOGIN_PAGE = "login_page";
	
	public static final String ATTRN_IS_FORCE_REDIRECT = "is_force_redirect" ;
	
	public static final String ATTRN_LOGIN_REDIRECT_PAGE = "login_redirect_page";
	
	
	private static HashMap<String,SessionItem> SESSIONID2LAST_ACCESS = new HashMap<>() ;
	
	private static boolean debugCookieOnly = false;
	
	private static boolean clientCookieOnly = false;
	
	private static long SESSION_TIMEOUT = 30*60000 ;
	
	private static String loginPage = null ;
	
	/**
	 * 是否强制重定向-true表示登录重定向不考虑r参数指定的内容
	 */
	private static boolean bForceRedirect = false ;
	
	private static String loginRedirectPage = null ;
	
	static
	{
		try
		{
			Element ele = Config.getConfElement("auth");
			if(ele!=null)
			{
				String lst = ele.getAttribute(ATTRN_LOGIN_SESSION_TIMEOUT) ;
				
				if(lst!=null&&!lst.equals(""))
				{
					long t = Long.parseLong(lst);
					if(t>0)
						SESSION_TIMEOUT = t * 60000 ;
				}
				
				debugCookieOnly = "true".equalsIgnoreCase(ele.getAttribute(ATTRN_DEBUG_COOKIE_ONLY));
				clientCookieOnly = "true".equalsIgnoreCase(ele.getAttribute(ATTRN_CLIENT_COOKIE_ONLY));
				
				loginPage = ele.getAttribute(ATTRN_LOGIN_PAGE) ;
				
				loginRedirectPage = ele.getAttribute(ATTRN_LOGIN_REDIRECT_PAGE);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean isCookieOnly()
	{
		return debugCookieOnly||clientCookieOnly ;
	}
	
	private static LinkedHashMap<String,UserAuthItem> user2items = null ;
	

	private static File calcAuthFile(String username)
	{
		String authf = Config.getDataDirBase()+"/auth/"+username+".json" ;
		return new File(authf) ;
	}
	
	
	private static UserAuthItem loadUserAuthItem(String username) throws IOException
	{
		//String authf = Config.getDataDirBase()+"/auth/"+username+".json" ;
		File f = calcAuthFile(username) ;//new File(authf) ;
		if(!f.exists())
			return null ;
		
		JSONObject jo = Convert.readFileJO(f) ;
		if(jo==null)
			return null ;
		
		return UserAuthItem.fromJO(jo) ;
	}
	
	public static LinkedHashMap<String,UserAuthItem> listUserAll()
	{
		if(user2items!=null)
			return user2items;
		
		synchronized(LoginUtil.class)
		{
			if(user2items!=null)
				return user2items;
			
			File dir = new File(Config.getDataDirBase()+"/auth/");
			File[] fs = dir.listFiles(new FileFilter() {
	
				@Override
				public boolean accept(File f)
				{
					if(!f.isFile())
						return false;
					String fn = f.getName() ;
					return fn.endsWith(".json");
				}}) ;
			
			LinkedHashMap<String,UserAuthItem> n2i = new LinkedHashMap<>() ;
			if(fs!=null)
			{
				for(File f:fs)
				{
					String fn = f.getName();
					fn =fn.substring(0,fn.length()-5) ;
					try
					{
						UserAuthItem uai = loadUserAuthItem(fn) ;
						if(uai==null)
							continue ;
						n2i.put(fn,uai) ;
					}
					catch(Exception ee)
					{
						ee.printStackTrace();
					}
				}
			}
			return user2items = n2i ;
		}
	}
	
	public static UserAuthItem getUserItem(String username)
	{
		return listUserAll().get(username);
	}
	
	private synchronized static boolean saveUserAuthItem(UserAuthItem uai) throws Exception
	{
		File f = calcAuthFile(uai.username) ;
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs() ;
		Convert.writeFileJO(f, uai.toJO());
		return true ;
	}
	

	public static UserAuthItem addUser(String username,String disname,String psw,StringBuilder failedr) throws Exception
	{
		if("admin".equals(username))
			return null ;
		
		UserAuthItem uai = getUserItem(username) ;
		if(uai!=null)
		{
			failedr.append(username+" is existed") ;
			return null ;
		}
		uai = saveUserAuthItem(username,disname,psw);
		if(uai==null)
			return null ;
		listUserAll().put(uai.username,uai);
		return uai ;
	}
	

	private synchronized static UserAuthItem saveUserAuthItem(String username,String disname,String psw) throws Exception
	{
		String salt = SecureUtil.generateSalt() ;
		String encpsw = SecureUtil.encryptPsw(psw, salt) ;
		UserAuthItem uai = new UserAuthItem(username,disname,salt,encpsw);
		//String authf = Config.getDataDirBase()+"/auth/"+username+".json" ;
		File f = calcAuthFile(username) ;
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs() ;
		Convert.writeFileJO(f, uai.toJO());
		return uai ;
	}
	
	
	public static UserAuthItem updateUser(String username,String disname,StringBuilder failedr) throws Exception
	{
		if("admin".equals(username))
			return null ;
		
		if(Convert.isNullOrEmpty(disname))
		{
			failedr.append("disname cannot be null or empty") ;
			return null ;
		}
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
		{
			failedr.append(username+" is not existed") ;
			return null ;
		}
		
		uai.disname = disname ;
		if(saveUserAuthItem(uai))
			return uai ;
		return null ;
	}
	
	public static boolean delUser(String username)
	{
		if("admin".equals(username))
			return false ;
		
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
		{
			return false;
		}
		File f = calcAuthFile(username) ;
		if(f.exists())
		{
			if(f.delete())
			{
				listUserAll().remove(username);
			}
		}
		
		return false ;
	}
	
	public static UserAuthItem changeUserPsw(String username,String newpsw,String oldpsw,StringBuilder failedr) throws Exception
	{
		if(Convert.isNullOrEmpty(newpsw))
		{
			failedr.append("password cannot be null or empty") ;
			return null ;
		}
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
		{
			failedr.append(username+" is not existed") ;
			return null ;
		}
		
		if(!checkUserPsw(username,oldpsw))
		{
			failedr.append("old psw check failed") ;
			return null;
		}
		
		String salt = SecureUtil.generateSalt() ;
		String encpsw = SecureUtil.encryptPsw(newpsw, salt) ;
		uai.salt = salt;
		uai.encPsw = encpsw ;
		if(saveUserAuthItem(uai))
			return uai ;
		return null ;
	}
	
	public static UserAuthItem changeUserPswAdmin(String username,String psw,StringBuilder failedr) throws Exception
	{
		if(Convert.isNullOrEmpty(psw))
		{
			failedr.append("password cannot be null or empty") ;
			return null ;
		}
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
		{
			failedr.append(username+" is not existed") ;
			return null ;
		}
		String salt = SecureUtil.generateSalt() ;
		String encpsw = SecureUtil.encryptPsw(psw, salt) ;
		uai.salt = salt;
		uai.encPsw = encpsw ;
		if(saveUserAuthItem(uai))
			return uai ;
		return null ;
	}
	
	public static boolean checkUserPsw(String username,String password)  throws Exception
	{
		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
		if(pa!=null && pa.canCheckAdminUser())
		{
			PlugAuthUser u = pa.checkAdminUser(username, password);
			if(u==null)
				return false;
			return true ;
		}
		
		
		if(!"admin".equals(username))
			return false;
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
			return false;

		return SecureUtil.checkPsw(password, uai.encPsw, uai.salt) ;
	}
	
	public static boolean doLogin(HttpServletRequest req,HttpServletResponse resp,String username,String password,String lang) throws Exception
	{
		SessionItem si = validateLogin(req,username,password,lang);
		if(si==null)
			return false;
		processSession(req,resp,si) ;
		return true;
	}
	
	private static SessionItem validateLogin(HttpServletRequest req,String username,String password,String lang) throws Exception
	{
		String sessid = UUID.randomUUID().toString().replaceAll("-", "") ;
		
		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
		if(pa!=null && pa.canCheckAdminUser())
		{
			PlugAuthUser u = pa.checkAdminUser(username, password);
			if(u==null)
				return null;
			return new SessionItem(sessid,username,u.getFullName(),lang);
		}
		
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
		{//first
			if("admin".equals(username))
			{
				uai = saveUserAuthItem(username,"Admin",password) ;
			}
			else
			{
				return null;
			}
		}
		else
		{
			boolean r = SecureUtil.checkPsw(password, uai.encPsw, uai.salt) ;
			if(!r)
				return null;
		}
		
		return new SessionItem(sessid,username,uai.disname,lang);
	}
	
	public static final String IOTTREE_COOKIE = "__iottree__";
	public static final String COOKIE_NAME_SESSION = "sessionid";
	
	private static void processSession(HttpServletRequest req,HttpServletResponse resp,SessionItem si) throws UnsupportedEncodingException
	{
		req.getSession().setAttribute(LOGIN_SK, si.sess_id);
		//set cookie
		HttpCookie wb_cookie = new HttpCookie(IOTTREE_COOKIE);
		wb_cookie.setPath("/");
		wb_cookie.setValue(COOKIE_NAME_SESSION, si.sess_id);
		HttpCookie.addResponseCookie(wb_cookie, resp);
		
		SESSIONID2LAST_ACCESS.put(si.sess_id,si);
	}
	
	public static void doLogout(HttpServletRequest req,HttpServletResponse resp) throws UnsupportedEncodingException
	{
		HttpSession hs = req.getSession();
		String sess_id = (String)hs.getAttribute(LOGIN_SK);
		hs.removeAttribute(LOGIN_SK) ;//, false);
		
		HttpCookie wb_cookie = new HttpCookie(IOTTREE_COOKIE);
		wb_cookie.setPath("/");
		HttpCookie.addResponseCookie(wb_cookie, resp);
		if(Convert.isNotNullEmpty(sess_id))
			SESSIONID2LAST_ACCESS.remove(sess_id) ;
	}
	/**
	 * check admin has set his psw or not
	 * @return
	 * @throws IOException 
	 */
	public static boolean checkAdminSetPsw() throws IOException
	{
		return null!= getUserItem("admin") ;
	}
	
	public static boolean chgPsw(String username,String oldpsw,String newpsw,StringBuilder failedr) throws Exception
	{
		//if(!"admin".equals(username))
		//	return false;
		
		if(Convert.isNullOrEmpty(oldpsw) || Convert.isNullOrEmpty(newpsw))
		{
			failedr.append("illegal input") ;
			return false;
		}
		UserAuthItem uai = getUserItem(username) ;
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
		
		saveUserAuthItem(username,uai.disname,newpsw) ;
		return true;
	}
	/**
	 * check admin login or not 
	 * @param req
	 * @param username
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static boolean checkAdminLogin(HttpServletRequest req,HttpServletResponse resp) throws UnsupportedEncodingException
	{
		SessionItem si = getUserLoginSession(req) ;
		if(si!=null&&si.usern.equals("admin"))
			return true ;
		
		return checkPlatAdmin(req) ;
	}
	
	public static boolean checkUserLogin(HttpServletRequest req) throws UnsupportedEncodingException
	{
		SessionItem si = getUserLoginSession(req) ;
		if(si!=null)
			return true ;
		
		return checkPlatAdmin(req);
	}
	
	public static boolean checkUserLogin(HttpSession hs)// throws UnsupportedEncodingException
	{
		if(hs==null)
			return false;
		String sess_id = (String)hs.getAttribute(LOGIN_SK);
		if(Convert.isNullOrEmpty(sess_id))
			return false;
		
		SessionItem si = accessSession(sess_id) ;
		return si!=null;
	}
	
	public static SessionItem getUserLoginSession(HttpServletRequest req) throws UnsupportedEncodingException
	{
		HttpSession hs = req.getSession() ;
		//if(hs==null)
		//	return null;
		String sess_id = null;// (String)hs.getAttribute(LOGIN_SK);
		if(Convert.isNullOrEmpty(sess_id))
		{
			//read from cookie
			HttpCookie wb_cookie = HttpCookie.getRequestCookie(IOTTREE_COOKIE,req);
			if (wb_cookie == null)
			{
				return null ;
			}
			sess_id = wb_cookie.getValue(COOKIE_NAME_SESSION);
		}
		if(Convert.isNullOrEmpty(sess_id))
			return null ;
		SessionItem si = accessSession(sess_id);
		if(si!=null)
		{
			hs.setAttribute(LOGIN_SK, sess_id);
		}
		return si;
	}
	
	private static SessionItem accessSession(String sessionid)
	{
		if(sessionid==null)
			return null;
		
		SessionItem ls = SESSIONID2LAST_ACCESS.get(sessionid);
		if(ls==null)
			return null ;
		
		if(isCookieOnly())
			return ls;
		
		//check timeout
		
		long curtm = System.currentTimeMillis();
		if((curtm-ls.lastAcc)>SESSION_TIMEOUT)
		{
			synchronized(SESSIONID2LAST_ACCESS)
			{
				clearTimeOutSession();
			}
			return null ;
		}

		ls.lastAcc = curtm ;
		return ls ;
	}
	
	private static void clearTimeOutSession()
	{
		ArrayList<String> tobem = new ArrayList<String>();
		long curtm = System.currentTimeMillis();
		for(Map.Entry<String, SessionItem> s2la:SESSIONID2LAST_ACCESS.entrySet())
		{
			if((curtm-s2la.getValue().lastAcc)>SESSION_TIMEOUT)
				tobem.add(s2la.getKey());
		}
		
		for(String s:tobem)
		{
			SESSIONID2LAST_ACCESS.remove(s);
		}
	}
	
	// get pm
	
	public static String getLoginPage()
	{
		return loginPage ;
	}
	
	/**
	 * 得到登陆后跳转页面
	 * @return
	 */
	public static String getLoginRedirectPage()
	{
		return loginRedirectPage;
	}
	
	/**
	 * 判断是否每次登陆后都强制使用重定向处理
	 * @return
	 */
	public static boolean isForceRedirect()
	{
		return bForceRedirect;
	}
	
	public static boolean isClientCookieOnly()
	{
		return clientCookieOnly;
	}
	
	// ---------------- plat access support
	/**
	 * 判断
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static boolean checkPlatAdmin(HttpServletRequest request) throws UnsupportedEncodingException
	{
		if(!PlatNodeManager.isPlatNode())
			return false;
		
		PlatNode pn = PlatNodeManager.getInstance().getNode() ;
		if(pn==null)
			return false;
		
		String _plat_token_ = request.getParameter(PlatNode.PN_TOKEN) ;
		if(Convert.isNotNullEmpty(_plat_token_))
		{//
			PlatNode.UserRight ur = pn.getRightByToken(_plat_token_) ;
			if(log.isDebugEnabled())
				log.debug("token="+_plat_token_);
			//System.out.println("token="+_plat_token_+" ur="+ur) ;
			if(ur!=null)
			{
				HttpSession hs = request.getSession();
				//String sessid = UUID.randomUUID().toString().replaceAll("-", "") ;
				//SessionItem si = new SessionItem(sessid,ur.userName,ur.userDisName,ur.lan) ;
				//processSession(request, resp,si) ;
				
				hs.setAttribute(PlatNode.PN_TOKEN, _plat_token_);
				if(log.isDebugEnabled())
					log.debug(" set session id="+hs.getId()+" token="+_plat_token_) ;
				//System.out.println("set session="+hs+"   ssid="+hs.getId()  );
				return true ;
			}
			else
			{
				return false;
			}
		}
		
		HttpSession hs = request.getSession() ;
		
		String tk = (String)hs.getAttribute(PlatNode.PN_TOKEN);
		//System.out.println("si=="+si) ;
		if(log.isDebugEnabled())
			log.debug("chk session id="+hs.getId());//+" si="+si);
		return Convert.isNotNullEmpty(tk);
		//_plat_token_ = (String)request.getSession().getAttribute(PlatNode.PN_TOKEN) ;
//		if(Convert.isNullOrEmpty(_plat_token_))
//			return false;
//		
		//PlatNode.UserRight ur = pn.getRightByToken(_plat_token_) ;
		//return ur!=null ;
	}
}
