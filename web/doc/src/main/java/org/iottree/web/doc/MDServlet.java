package org.iottree.web.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.iottree.core.Config;
import org.iottree.core.UAHmi;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UAUtil;
import org.iottree.core.util.Convert;

public class MDServlet extends HttpServlet
{
	static HashMap<String,String> path2txt = new HashMap<>() ;
	
	public MDServlet()
	{
	}
	
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
//	{
//		super.doGet(req, resp);
//	}
	
	private String webappBase = null ;
	
	private String getWebappBase(HttpServletRequest req)
	{
		if(webappBase!=null)
			return webappBase;
		try
		{
			return Config.getWebappBase();
		}
		catch(Throwable e)
		{
			webappBase = req.getSession().getServletContext().getRealPath("/")+"/../";
			
			return webappBase;
		}
	}
	
	private File path2file(HttpServletRequest req,String path) throws IOException
	{
		String webappbase = getWebappBase(req) ;
		File tmpf = new File(webappbase+path) ;
		//System.out.println(" path2file="+path+" " +tmpf.getCanonicalPath());
		return tmpf ;
	}

	public String getMdHtml(HttpServletRequest req,String path) throws IOException
	{
		String txt = null;//path2txt.get(path) ;
		if(txt!=null)
			return txt;
		
		File f = path2file(req,path) ;
		if(!f.exists())
			return null ;
		
		Parser parser = Parser.builder().build();
		try(FileInputStream fis = new FileInputStream(f);InputStreamReader isr = new InputStreamReader(fis,"UTF-8"))
		{
	        Node document = parser.parseReader(isr);
	        HtmlRenderer renderer = HtmlRenderer.builder().build();
	        txt = renderer.render(document);  // <h1>My name is <em>huhx</em></h1>
		}
		if(txt!=null)
			path2txt.put(path, txt) ;
		
		return txt ;
	}
	
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException
	{
		//HttpSession session = req.getSession();
		//this.getServletContext()..getRequestDispatcher(getServletInfo())
		resp.setContentType("text/html;charset=UTF-8");
		String uri = req.getRequestURI();
		//String qs = req.getQueryString();
		//System.out.println("uri="+uri +"  qs="+qs);
		if(uri.startsWith("/iottree"))
			uri = uri.substring(8) ;
		
		if(uri.endsWith(".md"))
		{
			boolean boutline = !"false".equals(req.getParameter("outline")) ;
			if(uri.endsWith("/nav.md"))
				boutline=false;
			String txt = getMdHtml(req,uri) ;
			if(txt==null)
				return ;
			PrintWriter w = resp.getWriter() ;
			if(boutline)
			{
				w.write("<html><head><script src=\"/_js/jquery-1.12.0.min.js\"></script>"
						+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"/_js/layui/css/layui.css\" />\r\n" + 
					"<script src=\"/_js/layui/layui.all.js\"></script>"+
					"<script src=\"/_js/layui/layui.all.js\"></script>"+
					"<link  href=\"/_js/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\" >"+
					"</head><body>"
					+ "<div style='position:absolute;left:10px;right:200px'>") ;
			}
			
			w.write(txt);
			if(boutline)
			{
				w.write("</div>"
						+ "<div id='outline_div' class=\"layui-layer layui-layer-page layui-layer-dir\" type=\"page\" times=\"1\" showtime=\"0\" contype=\"object\" style=\"z-index: 19891015; position: fixed; top: 33px; left: 994px; margin-left: -15px;\"><div class=\"layui-layer-title\" style=\"cursor: move;\">Outline</div><div id=\"\" class=\"layui-layer-content\">"
						+ "<ul id=\"outline_list\" class=\"site-dir layui-layer-wrap\" style=\"display: block;\">\r\n" + 
						"</ul></div><span class=\"layui-layer-setwin\"></span><span class=\"layui-layer-resize\"></span></div>"+
						"</body><script src=\"/doc/doc.js\"></script></html>");
			}
			return ;
		}

		//req.getRequestDispatcher("/node_list.jsp?path="+uri).forward(req, resp);
		return ;
	}
	
}  