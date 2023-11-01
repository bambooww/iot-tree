package org.iottree.core.cxt;

import java.io.Writer;

import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.util.Convert;

import com.sun.xml.bind.v2.schemagen.xmlschema.List;

/**
 * js sub abstract def
 * 
 * @author jason.zhu
 *
 */
public abstract class JsSub
{
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	public JsSub(String name,String title,String desc)
	{
		//this.cxtNode = cxtn;
		this.name = name ;
		
		this.title = title ;
		this.desc = desc ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		if(title==null)
			return "" ;
		return this.title ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
	public abstract boolean hasSub() ;
	
	public abstract String getSubTitle() ;
	
	public String getSubIcon()
	{
		return null ;
	}
	
	public void writeTree(String pid,Writer w) throws Exception
	{
		String nid = Convert.isNotNullEmpty(pid)?(pid+"."+this.name):this.name ;
		w.write("{\"id\":\"" + nid + "\"");
		
		String icon = getSubIcon() ;
		if(Convert.isNotNullEmpty(icon))
			w.write(",\"icon\": \""+icon+"\"");
		else
		{
			if(hasSub())
				w.write(",\"icon\": \"fa-solid fa-folder fa-lg\"");
			else
				w.write(",\"icon\": \"fa-solid fa-file fa-lg\"");
		}
			
		w.write(",\"text\":\""+this.getSubTitle()+"\"");
		w.write(",\"tt\":\""+this.title+"\"");
		w.write(",\"state\": {\"opened\": false}");

		w.write(",\"children\":"+this.hasSub());

		w.write("}");
	}
	
	
	public final static String getClassJsTitle(Class<?> c)
	{
		if(c==null)
			return "" ;
		
		JsDef jsd = c.getAnnotation(JsDef.class) ;
		if(jsd!=null)
			return jsd.title() ;
		
		if(c==String.class)
			return "str" ;
		if(Number.class.isAssignableFrom(c))
			return "number" ;
		if(c==Boolean.class)
			return "bool";
		
		return c.getTypeName() ;
	}
}
