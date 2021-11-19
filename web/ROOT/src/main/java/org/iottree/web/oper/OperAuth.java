package org.iottree.web.oper;

import javax.servlet.http.HttpSession;

import org.iottree.core.UAPrj;

public class OperAuth
{
	private static final String OPER_AUTH = "_OPER_AUTH_" ;
	
	public static boolean checkSessionAuthOk(HttpSession hs,UAPrj prj)
	{
		Long authdt = (Long)hs.getAttribute(OPER_AUTH+prj.getId()) ;
		if(authdt==null)
			return false ;
		return System.currentTimeMillis() - authdt < prj.getOperPermDurSec()*1000 ;
	}
	
	public static boolean checkSessionAuth(HttpSession hs,UAPrj prj,String user,String psw)
	{
		if(!prj.checkOperator(user, psw))
			return false;
		
		hs.setAttribute(OPER_AUTH+prj.getId(), System.currentTimeMillis());
		return true ;
	}
}
