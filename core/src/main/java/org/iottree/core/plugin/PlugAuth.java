package org.iottree.core.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;

import org.iottree.core.util.Convert;
import org.iottree.core.ws.WebSocketConfig;

public class PlugAuth
{
	public static final String PN_PLUG_AUTH_USER = "__plug_auth_user_" ;
	
	//public static final String PN_IOTTREE_TOKEN = "iottree_token" ;
	
	Object plugObj = null ;
	
	private Method mCheckAdminUser = null;
	private Method mCheckUserByToken = null ;
	private Method mCheckReadRight = null;
	private Method mCheckWriteRight = null;
	
	private String tokenCookieName = null ;
	
	
	private String noReadRightPrompt = null ;
	
	private String noWriteRightPrompt = null ;
	
	
	public PlugAuth(Object plugobj,String token_cookie_name)
	{
		plugObj = plugobj ;
		this.tokenCookieName = token_cookie_name ;
	}
	
	public PlugAuth asNoRightPrompt(String no_read_p,String n_write_p)
	{
		this.noReadRightPrompt = no_read_p ;
		this.noWriteRightPrompt = n_write_p ;
		return this ;
	}
	
	public String getNoReadRightPrompt()
	{
		if(Convert.isNullOrEmpty(this.noReadRightPrompt))
			return "no read right" ;
		return this.noReadRightPrompt ;
	}
	
	public String getNoWriteRightPrompt()
	{
		if(Convert.isNullOrEmpty(this.noWriteRightPrompt))
			return "no write right" ;
		return this.noWriteRightPrompt ;
	}
	
	boolean initAuth()
	{
		if(plugObj==null)
			return false;
		
		Class<?> c = plugObj.getClass() ;
		
		try
		{
			mCheckAdminUser = c.getDeclaredMethod("checkAdminUser", String.class,String.class);
		}
		catch(NoSuchMethodException nsme) {}
		
		try
		{
			mCheckUserByToken = c.getDeclaredMethod("checkUserByToken", String.class);
		}
		catch(NoSuchMethodException nsme) {}
		
		try
		{
			mCheckReadRight = c.getDeclaredMethod("checkReadRight", String.class,String.class);
		}
		catch(NoSuchMethodException nsme) {}
		
		try
		{
			mCheckWriteRight = c.getDeclaredMethod("checkWriteRight", String.class,String.class);
		}
		catch(NoSuchMethodException nsme) {}
		
		if(mCheckUserByToken==null||mCheckReadRight==null)
			return false;
		return true ;
	}
	
	public boolean canCheckAdminUser()
	{
		return mCheckAdminUser!=null ;
	}
	
	public PlugAuthUser checkAdminUser(String reg_name,String password) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(mCheckAdminUser==null)
			return null ;
		
		Object ob = mCheckAdminUser.invoke(plugObj, reg_name,password);
		if(ob==null)
			return null;
		return PlugAuthUser.createFromObj(ob) ;
	}

	/**
	 * Obtain user information according to the token
	 * 
	 * @param token provided by page cookie or other ways
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public PlugAuthUser checkUserByToken(String token) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(token==null||"".equals(token))
			return null ;
		if(mCheckUserByToken==null)
			return null ;
		
		Object ob = mCheckUserByToken.invoke(plugObj, token);
		if(ob==null)
			return null;
		return PlugAuthUser.createFromObj(ob) ;
	}
	
	public String getTokenByRequest(HttpServletRequest req)
	{
		String tk = req.getHeader(tokenCookieName) ;
		if(Convert.isNullOrEmpty(tk))
		{
			tk = (String)req.getSession().getAttribute(tokenCookieName) ;
		}
		if(Convert.isNotNullEmpty(tk))
			return tk ;
		
		Cookie[] cks = req.getCookies() ;
		for(Cookie ck:cks)
		{
			if(tokenCookieName.equalsIgnoreCase(ck.getName()))
				return ck.getValue() ;
		}
		return null;
	}
	
	public PlugAuthUser checkUserBySessionTk(HttpSession hs,String tk) throws Exception
	{
		String sess_tk = (String)hs.getAttribute(tokenCookieName) ;
		
		PlugAuthUser pau = (PlugAuthUser)hs.getAttribute(PN_PLUG_AUTH_USER);
		if(pau!=null &&tk.equals(sess_tk))
			return pau ;
		
		pau = checkUserByToken(tk) ;
		if(pau==null)
			return null ;
		
		hs.setAttribute(PN_PLUG_AUTH_USER, pau);
		hs.setAttribute(tokenCookieName, tk);
		
		return pau ;
	}
	
	public PlugAuthUser checkUserByRequest(HttpServletRequest req) throws Exception
	{
		String tk = getTokenByRequest(req) ;
		if(Convert.isNullOrEmpty(tk))
			return null ;
		
		HttpSession hs = req.getSession() ;
		
		return checkUserBySessionTk(hs,tk) ;
	}
	
	
	public PlugAuthUser checkUserByWebSocket(EndpointConfig config) throws Exception
	{
		HttpSession hs = WebSocketConfig.getHttpSession(config) ;
		String tk = WebSocketConfig.getCookieValue(config, tokenCookieName) ;
		
		return checkUserBySessionTk(hs,tk) ;
	}
	/**
	 * this method will be called when outer will access node path in iottree like /prjname/n1/u1
	 * 
	 * The node related to this path may be a context node that returns JSON data. 
	 * Or the HMI UI node outputs the UI picture
	 * 
	 * @param node_path  like /prjname/n1   /prjname/n1/u1
	 * @param reg_name registered user name
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public boolean checkReadRight(String node_path,String reg_name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(mCheckReadRight==null)
			return false;
		
		return (Boolean)mCheckReadRight.invoke(plugObj, node_path,reg_name);
	}
	
	
	public boolean checkReadRight(String node_path,HttpServletRequest req) throws Exception
	{
		PlugAuthUser u = checkUserByRequest(req) ;
		if(u==null)
			return false;
		
		return checkReadRight(node_path,u.getRegName()) ;
	}
	
	public boolean checkReadRight(String node_path,EndpointConfig req) throws Exception
	{
		PlugAuthUser u = checkUserByWebSocket(req) ;
		if(u==null)
			return false;
		
		return checkReadRight(node_path,u.getRegName()) ;
	}
	
	
	/**
	 * this method will be called when outer will write some data to node path in iottree like /prjname/n1/u1
	 * 
	 * this may be update some tag value in node or send cmd in hmi
	 * 
	 * @param node_path
	 * @param reg_name
	 * @return
	 */
	public boolean checkWriteRight(String node_path,String reg_name) throws Exception
	{
		if(mCheckWriteRight==null)
			return false;
		
		return (Boolean)mCheckWriteRight.invoke(plugObj, node_path,reg_name);
	}
	
	public boolean checkWriteRight(String node_path,HttpServletRequest req) throws Exception
	{
		PlugAuthUser u = checkUserByRequest(req) ;
		if(u==null)
			return false;
		
		return checkWriteRight(node_path,u.getRegName()) ;
	}
	
	public boolean checkWriteRight(String node_path,EndpointConfig req) throws Exception
	{
		PlugAuthUser u = checkUserByWebSocket(req) ;
		if(u==null)
			return false;
		
		return checkWriteRight(node_path,u.getRegName()) ;
	}
}
