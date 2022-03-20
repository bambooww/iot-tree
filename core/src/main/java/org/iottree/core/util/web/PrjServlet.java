package org.iottree.core.util.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.iottree.core.UAHmi;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAUtil;
import org.iottree.core.util.Convert;

public class PrjServlet extends HttpServlet
{
	public PrjServlet()
	{
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		super.doGet(req, resp);
		
		doGetPost(req,resp) ;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		super.doPost(req, resp);
		
		doGetPost(req,resp) ;
	}

	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		super.doPut(req, resp);
		//update restful api
		resp.setContentType("text/html;charset=UTF-8");
		String uri = req.getRequestURI();
		//String qs = req.getQueryString();
		
		if(uri.startsWith("/iottree"))
			uri = uri.substring(8) ;
		
		UANode node = UAUtil.findNodeByPath(uri) ;
		if(node==null)
			return ;
		
		if(!(node instanceof UATag))
			return ;
		
		UATag tag = (UATag)node ;
		for(Enumeration<String> ens = req.getParameterNames() ;ens.hasMoreElements();)
		{
			String pn = ens.nextElement() ;
			String pv = req.getParameter(pn) ;
			tag.JS_set(pn, pv);
		}
		
	}
	
	private void doGetPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException
	{
		String m = req.getMethod() ;
		
		HttpSession session = req.getSession();
		//this.getServletContext()..getRequestDispatcher(getServletInfo())
		resp.setContentType("text/html;charset=UTF-8");
		String uri = req.getRequestURI();
		String qs = req.getQueryString();
		//System.out.println("uri="+uri +"  qs="+qs);
		if(uri.startsWith("/iottree"))
			uri = uri.substring(8) ;
		
		if(uri.endsWith(".jsp"))
		{
			String u = uri ;
			if(qs!=null)
				u += "?"+qs ;
			//req.getRequestDispatcher(u).forward(req, resp);
			super.service(req, resp);
			return ;
		}
		
		UANode node = UAUtil.findNodeByPath(uri) ;
		if(node==null)
		{
			return ;
		}
		
		if(node instanceof UAHmi)
		{
			//UAHmi hmi = (UAHmi)node ;
			req.getRequestDispatcher("/hmi.jsp?path="+uri).forward(req, resp);
			return ;
		}
		else
		{
			req.getRequestDispatcher("/node_list.jsp?path="+uri).forward(req, resp);
			return ;
		}
		
	}
	
	private PathItem parsePath(String path)
	{
		LinkedList<String> ss = Convert.splitStrWithLinkedList(path, "/") ;
		PathItem pi = new PathItem() ;
		
		pi.nodePath = new LinkedList<>() ;
		while(ss.size()>0)
		{
			String n = ss.removeFirst() ;
			if(n.startsWith("_"))
			{
				pi.tp = n ;
				break ;
			}
			else
			{
				pi.nodePath.addLast(n);
			}
		}
		
		if(ss.size()>0)
			pi.tpPath = ss ;
		
		return pi ;
	}
	
	public static class PathItem
	{
		LinkedList<String> nodePath = null ;
		
		String tp = null ;
		
		LinkedList<String> tpPath = null ;
	}
	

	protected void service0(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException
	{
		HttpSession session = req.getSession();
		//this.getServletContext()..getRequestDispatcher(getServletInfo())
		resp.setContentType("text/html;charset=UTF-8");
		String uri = req.getRequestURI();
		String qs = req.getQueryString();
		System.out.println("uri="+uri +"  qs="+qs);
		if(uri.startsWith("/iottree"))
			uri = uri.substring(8) ;
		
		if(uri.endsWith(".jsp"))
		{
			String u = uri ;
			if(qs!=null)
				u += "?"+qs ;
			req.getRequestDispatcher(u).forward(req, resp);
			return ;
		}
		
		UANode node = null ;
		
		UAManager uamgr = UAManager.getInstance() ;
		UAPrj rep = null ;
		PathItem pi = null ;
		if(uri.equals("/"))
		{//using default repository
			node = rep = uamgr.getPrjDefault() ;
			if(rep==null)
				return ;
			pi = new PathItem() ;
			pi.nodePath = new LinkedList<>(Arrays.asList(node.getName())) ;
		}
		else
		{
			pi = parsePath(uri);
			String firstn = pi.nodePath.removeFirst() ;
			rep = uamgr.getPrjByName(firstn) ;
			if(rep==null)
				return ;
			if(pi.nodePath.size()<=0)
				node= rep ;
			else
				node = rep.getDescendantNodeByPath(pi.nodePath) ;
			if(node==null)
				return ;
		}
		
		if(pi.tp==null)
		{
			req.getRequestDispatcher("/node_list.jsp?repid="+rep.getId()+"&id="+node.getId()).forward(req, resp);
			return ;
		}
		
		switch(pi.tp)
		{
		case "_hmi":
			if(!(node instanceof UANodeOCTagsCxt))
			{
				return ;
			}
			UANodeOCTagsCxt ncxt = (UANodeOCTagsCxt)node ;
			if(pi.tpPath==null||pi.tpPath.size()<=0)
				return ;
			UAHmi hmi = ncxt.getHmiByName(pi.tpPath.get(0)) ;
			if(hmi==null)
				return ;
			req.getRequestDispatcher("/hmi.jsp?repid="+rep.getId()+"&id="+hmi.getId()).forward(req, resp);
			break ;
		}
	}
}  
