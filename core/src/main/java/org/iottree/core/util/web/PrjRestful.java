package org.iottree.core.util.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.cache.Cacher;

public class PrjRestful
{
	static class TkItem
	{
		String user ;
		
		String uuid ;
	}
	
	static class TkBuf
	{
		int maxLen ;
		
		private HashSet<String> tkset = new HashSet<>() ;
		private LinkedList<String> tklist = new LinkedList<>() ;
		
		public TkBuf(int maxlen)
		{
			maxLen = maxlen ;
		}
		
		public synchronized void addToken(String tk)
		{
			tklist.addLast(tk);
			tkset.add(tk) ;
			
			if(tklist.size()>maxLen)
			{
				String tk0 = tklist.removeFirst();
				tkset.remove(tk0) ;
			}
		}
		
		public boolean checkHasToken(String tk)
		{
			return tkset.contains(tk) ;
		}
	}
	
	UAPrj belongTo ;
	
	private HashMap<String,String> user2psw ;
	
	private String httpHead = "token" ;
	
	private transient HashMap<String,Token> user2tk = new HashMap<>() ;
	
	private transient TkBuf tkBuf = new TkBuf(100) ;
	
	public PrjRestful(UAPrj prj)
	{
		this.belongTo = prj ;
		String users = prj.getOrDefaultPropValueStr("prj_restful", "token_users", "") ;
		httpHead = prj.getOrDefaultPropValueStr("prj_restful", "token_http_head", "token") ;
		user2psw = Convert.transPropStrToMap(users) ;
		
	}
	
	public boolean checkRequest(HttpServletRequest req) throws Exception
	{
		String tk = req.getHeader(httpHead) ;
		if(Convert.isNullOrEmpty(tk))
			return false;
		
		int k = tk.indexOf('|') ;
		if(k<=0)
			return false;
		String usr = tk.substring(0,k) ;
		String psw = user2psw.get(usr) ;
		if(Convert.isNullOrEmpty(psw))
			return false;
		
		Token token = user2tk.get(usr) ;
		if(token==null)
		{
			token = new Token(usr,psw) ;
			user2tk.put(usr, token) ;
		}
		
		String[] usr_uuid = token.parseToken(tk);
		if(usr_uuid==null)
			return false;
		
		if(tkBuf.checkHasToken(usr_uuid[1]))
			return false; //
		tkBuf.addToken(usr_uuid[1]);
		return true ;
	}
}
