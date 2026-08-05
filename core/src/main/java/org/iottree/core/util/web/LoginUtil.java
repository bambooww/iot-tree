package org.iottree.core.util.web;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;

import org.iottree.core.Config;
import org.iottree.core.node.PlatNode;
import org.iottree.core.node.PlatNodeManager;
import org.iottree.core.plugin.PlugAuth;
import org.iottree.core.plugin.PlugAuthUser;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.SecureUtil;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.ws.WebSocketConfig;
import org.json.JSONArray;
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
	
	public enum UserState
	{
		Normal(0,"正常","normal"), Invalid(1,"无效","invalid"), Delete(2,"删除","deleted")
			,ResetPsw(3,"重置密码","reset password"),New(4,"新建","new");

		private final int stVal;
		private final String title_cn;
		private final String title_en;

		UserState(int v,String t_cn,String t_en)
		{
			stVal = v;
			title_cn = t_cn ;
			title_en = t_en ;
		}

		public int getIntValue()
		{
			return stVal;
		}
		
		public String getTitle()
		{
			if("cn".equals(Lan.getUsingLang()))
				return title_cn ;
			return title_en ;
		}
		
		public String getTitleColor()
		{
			String t = this.getTitle() ;
			String c ;
			switch(stVal)
			{
			case 1:
				c = "red";break;
			case 2:
				c = "red";break;
			case 3:
				c = "blue";break;
			case 4:
				c = "blue";break;
			default:
				c = "green";break;
			}
			return "<font color='"+c+"'>"+t+"</font>";
		}
		
		public static UserState getByIntValue(int v)
		{
			switch(v)
			{
			case 1:
				return Invalid;
			case 2:
				return Delete;
			case 3:
				return ResetPsw;
			case 4:
				return New;
			default:
				return Normal;
			}
		}
	}
	
	
	public static class UserAuthItem
	{
		String username ;
		
		String disname ;//显示名称
		
		UserState state = UserState.Normal ;
		
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
		
		public UserState getState()
		{
			return this.state ;
		}
		
		public List<String> getRoleNames()
		{
			return this.roles ;
		}
		
		public boolean hasRole(String rolen)
		{
			if(this.roles==null)
				return false;
			return this.roles.contains(rolen) ;
		}
		
		public JSONObject toListJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("usern",this.username) ;
			jo.putOpt("disn", this.disname) ;
			jo.put("state",this.state.stVal) ;
			jo.put("state_t",this.state.getTitle()) ;
			jo.put("state_t_c",this.state.getTitleColor()) ;
			if(this.roles!=null && this.roles.size()>0)
			{
				ArrayList<String> rrs = new ArrayList<>() ;
				ArrayList<String> roles_t = new ArrayList<>() ;
				for(String r:this.roles)
				{
					Role rr = getRole(r) ;
					if(rr==null)
						continue ;
					rrs.add(rr.getRoleName()) ;
					roles_t.add(rr.getRoleTitle()) ;
				}
				jo.put("roles", Convert.combineWith(rrs, ',')) ;
				jo.put("roles_t", Convert.combineWith(roles_t, ',')) ;
			}
			return jo ;
		}
		
		JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("usern",this.username) ;
			jo.putOpt("disn", this.disname) ;
			jo.put("state",this.state.stVal) ;
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
			ret.state = UserState.getByIntValue(jo.optInt("state",0)) ;
			ret.salt = jo.optString("salt") ;
			ret.encPsw = jo.optString("enc_psw") ;
			ret.roles = Convert.splitStrWith(jo.optString("roles"), ",|") ;
			if("admin".equals(ret.username))
			{
				if(ret.roles==null)
					ret.roles = new ArrayList<>();
				if(!ret.roles.contains("admin"))
					ret.roles.add("admin") ;
			}
			return ret;
		}
	}
	
	public static class Role
	{
		String role_n ;
		
		String role_t;
		
		Role(){}
		
		Role(String n,String t)
		{
			this.role_n = n ;
			this.role_t = t ;
		}
		
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
		
		public HashSet<String> roles = null ;
		
		public long loginDT  =System.currentTimeMillis();
		
		transient long lastAcc = System.currentTimeMillis() ;
		
		public SessionItem(String sess_id,String usern,String disn,List<String> roles,String lang)
		{
			this.sess_id = sess_id;
			this.usern = usern ;
			this.disn = disn ;
			this.lan = lang ;
			if(roles!=null)
			{
				this.roles = new HashSet<>();
				this.roles.addAll(roles) ;
			}
		}
		
		public SessionItem(PlatNode.UserRight ur)
		{
			//this.sess_id = sess_id;
			this.usern = ur.userName ;
			this.disn = ur.userDisName ;
			this.lan = ur.lan ;
			if(ur.bAdmin)
			{
				this.roles = new HashSet<>();
				this.roles.add("admin") ;
			}
		}
		
		public boolean isAdmin()
		{
			if("admin".equals(usern))
				return true ;
//			UserAuthItem uai = LoginUtil.getUserItem(usern) ;
//			if(uai==null)
//				return false;
//			return uai.hasRole("admin") ;
			if(this.roles==null)
				return false;
			return this.roles.contains("admin") ;
		}
		
		public boolean hasRole(String rolen)
		{
			if("admin".equals(usern))
				return true ;
			if(this.roles==null)
				return false;
			if(this.roles.contains("admin"))
				return true;
			return this.roles.contains(rolen) ;
		}
		
		@Override
		public String toString()
		{
			return "usern:"+this.usern ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("n", this.usern).putOpt("disn", this.disn).putOpt("lan", this.lan)
					.putOpt("login_dt", this.loginDT).put("admin", this.isAdmin()).putOpt("roles", this.roles) ;
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
	
	private static LinkedHashMap<String,Role> role2items = null ;
	
	public static LinkedHashMap<String,Role> listRoleAll()
	{
		if(role2items!=null)
			return role2items;
		
		synchronized(LoginUtil.class)
		{
			if(role2items!=null)
				return role2items;
			
			try
			{
				return reloadRoles();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	public static Role getRole(String name)
	{
		return listRoleAll().get(name) ;
	}
	
	private static LinkedHashMap<String,Role> reloadRoles() throws IOException
	{
		File rf = new File(Config.getDataDirBase()+"/auth/_roles.json");
		String txt =  Convert.readFileTxt(rf) ;
		JSONArray jarr = null;
		if(Convert.isNotNullEmpty(txt))
			jarr =new JSONArray(txt) ;
		LinkedHashMap<String,Role> n2i = new LinkedHashMap<>() ;
		n2i.put("admin",new Role("admin","Admin")) ;
		if(jarr==null)
			return n2i ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			Role r = Role.fromJO(tmpjo) ;
			if(r==null)
				continue ;
			n2i.put(r.role_n,r) ;
		}
		return role2items = n2i ;
	}
	
	public static JSONArray listRoleAllJArr()
	{
		JSONArray jarr = new JSONArray() ;
		LinkedHashMap<String,Role> n2r = listRoleAll() ;
		for(Role r:n2r.values())
		{
			jarr.put(r.toJO()) ;
		}
		return jarr ;
	}
	
	private static void saveRoles() throws IOException
	{
		JSONArray jarr = listRoleAllJArr() ;
		File rf = new File(Config.getDataDirBase()+"/auth/_roles.json");
		if(!rf.getParentFile().exists())
			rf.getParentFile().mkdirs() ;
		Convert.writeFileTxt(rf, jarr.toString());
	}
	
	public static Role setRole(String name,String title,StringBuilder failedr) throws IOException
	{
		if(!Convert.checkVarName(name, failedr))
			return null ;
		if("admin".equals(name))
		{
			failedr.append("admin cannot be modified") ;
			return null ;
		}
		Role r = getRole(name) ;
		if(r==null)
		{
			r = new Role(name,title) ;
			listRoleAll().put(name,r) ;
		}
		else
		{
			r.role_t = title ;
		}
		saveRoles() ;
		return r ;
	}
	
	public static Role delRole(String name) throws IOException
	{
		if("admin".equals(name))
			return null ;
		Role r = listRoleAll().remove(name) ;
		if(r==null)
			return null ;
		saveRoles() ;
		return r ;
	}

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
			
			return reloadUsers();
		}
	}
	
	private static LinkedHashMap<String,UserAuthItem> reloadUsers()
	{
		File dir = new File(Config.getDataDirBase()+"/auth/");
		File[] fs = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isFile())
					return false;
				String fn = f.getName() ;
				if(fn.startsWith("_"))
					return false;
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
		if(username.startsWith("_"))
		{
			failedr.append("username cannot start with _") ;
			return null ;
		}
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
		reloadUsers();
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
	
	public static UserAuthItem setUserRoles(String username,List<String> roles,StringBuilder failedr) throws Exception
	{
		if("admin".equals(username))
		{
			failedr.append("admin's role cannot be changed") ;
			return null ;
		}
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
		{
			failedr.append("no user found") ;
			return null ;
		}
		
		uai.roles = roles ;
		if(saveUserAuthItem(uai))
			return uai ;
		failedr.append("save user failed");
		return null ;
	}
	
	public static List<UserAuthItem> listUsersByRoles(Collection<String> roles)
	{
		ArrayList<UserAuthItem> rets = new ArrayList<>() ;
		if(roles==null||roles.size()<=0)
			return rets ;
		for(UserAuthItem uai:listUserAll().values())
		{
			for(String role:roles)
			{
				if(uai.hasRole(role))
				{
					rets.add(uai) ;
					break ;
				}
			}
		}
		return rets;
	}
	
	public static List<UserAuthItem> listUsersByRole(String role)
	{
		return listUsersByRoles(Arrays.asList(role)) ;
	}
	
	public static boolean delUser(String username,boolean deleted,StringBuilder failedr) throws Exception
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
			if(deleted)
			{
				if(f.delete())
				{
					listUserAll().remove(username);
					return true;
				}
			}
			else
			{
				if(changeUserStateAdmin(username,UserState.Invalid,failedr)!=null)
					return true;
			}
		}
		
		return false ;
	}
	
	public static UserAuthItem changeUserStateAdmin(String username,UserState ust,StringBuilder failedr) throws Exception
	{
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
		{
			failedr.append(username+" is not existed") ;
			return null ;
		}
		uai.state= ust ;
		if(saveUserAuthItem(uai))
			return uai ;
		return null ;
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
		
		if(checkUserPsw(username,oldpsw)==null)
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
	
	
	
	public static UserAuthItem checkUserPsw(String username,String password)  throws Exception
	{
//		PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
//		if(pa!=null && pa.canCheckAdminUser())
//		{
//			PlugAuthUser u = pa.checkAdminUser(username, password);
//			if(u==null)
//				return null;
//			return true ;
//		}
		
		
		//if(!"admin".equals(username))
		//	return false;
		UserAuthItem uai = getUserItem(username) ;
		if(uai==null)
			return null;

		if(SecureUtil.checkPsw(password, uai.encPsw, uai.salt))
			return uai ;
		else
			return null ;
	}
	
	public static SessionItem doLogin(HttpServletRequest req,HttpServletResponse resp,String username,String password,String lang) throws Exception
	{
		SessionItem si = validateLogin(req,username,password,lang);
		if(si==null)
			return null;
		processSession(req,resp,si) ;
		return si;
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
			return new SessionItem(sessid,username,u.getFullName(),null,lang);
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
		
		return new SessionItem(sessid,username,uai.disname,uai.getRoleNames(),lang);
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
		
		return checkPlatAdmin(req)!=null ;
	}
	
	public static boolean checkUserLogin(HttpServletRequest req) throws UnsupportedEncodingException
	{
		SessionItem si = getUserLoginSession(req) ;
		if(si!=null)
		{
			req.getSession().setAttribute(LOGIN_SK, si.sess_id);
			return true ;
		}
		
		return checkPlatAdmin(req)!=null;
	}
	
	public static boolean checkUserLogin(HttpSession hs)// throws UnsupportedEncodingException
	{
		return checkUserLogin(hs,false) ;
	}
	
	public static boolean checkUserLogin(HttpSession hs,boolean badmin)// throws UnsupportedEncodingException
	{
		if(hs==null)
			return false;
		
		PlatNode.UserRight ur = (PlatNode.UserRight )hs.getAttribute(PlatNode.PN_USER_RIGHT);
		if(ur!=null)
		{
			if(badmin && !ur.bAdmin)
				return false;
			return true ;
		}
		//
		//if(Convert.isNullOrEmpty(sess_id))
		//	return false;
		String sess_id = (String)hs.getAttribute(LOGIN_SK);
		SessionItem si = accessSession(sess_id) ;
		return si!=null;
	}
	
	public static SessionItem getUserLoginSession(HttpServletRequest req) throws UnsupportedEncodingException
	{
		HttpSession hs = req.getSession() ;
		return getUserLoginSession(hs,req);
	}
	
	public static SessionItem getUserLoginSession(javax.websocket.EndpointConfig config) throws UnsupportedEncodingException
	{
		HttpSession hs = WebSocketConfig.getHttpSession(config) ;
		return getUserLoginSession(hs,null);
	}
	
	public static SessionItem getUserLoginSession(HttpSession hs,HttpServletRequest req) throws UnsupportedEncodingException
	{
		String sess_id = (String)hs.getAttribute(LOGIN_SK);
		if(Convert.isNullOrEmpty(sess_id))
		{
			if(req==null)
				return null ;
			
			//read from cookie
			HttpCookie wb_cookie = HttpCookie.getRequestCookie(IOTTREE_COOKIE,req);
			if (wb_cookie != null)
				sess_id = wb_cookie.getValue(COOKIE_NAME_SESSION);
		}
		
		if(Convert.isNullOrEmpty(sess_id))
		{
			PlatNode.UserRight ur = checkPlatAdmin(req) ;
			if(ur!=null)
			{
				return new SessionItem(ur) ;
			}
			return null ;
		}
		
		SessionItem si = accessSession(sess_id);
		if(si!=null)
		{
			hs.setAttribute(LOGIN_SK, sess_id);
			return si ;
		}

		PlatNode.UserRight ur = checkPlatAdmin(req) ;
		if(ur!=null)
		{
			return new SessionItem(ur) ;
		}
		return null ;
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
	private static PlatNode.UserRight checkPlatAdmin(HttpServletRequest request) throws UnsupportedEncodingException
	{
		if(!PlatNodeManager.isPlatNode())
			return null;
		
		PlatNode pn = PlatNodeManager.getInstance().getNode() ;
		if(pn==null)
			return null;
		
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
				hs.setAttribute(LOGIN_SK, hs.getId());
				hs.setAttribute(PlatNode.PN_USER_RIGHT, ur);
				return ur ;
			}
			else
			{
				return null;
			}
		}
		
		HttpSession hs = request.getSession() ;
		
		String tk = (String)hs.getAttribute(PlatNode.PN_TOKEN);
		//System.out.println("si=="+si) ;
		if(log.isDebugEnabled())
			log.debug("chk session id="+hs.getId());//+" si="+si);
		//return Convert.isNotNullEmpty(tk);
		return (PlatNode.UserRight)hs.getAttribute(PlatNode.PN_USER_RIGHT) ;
	}
}
