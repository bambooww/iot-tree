package org.iottree.portal;

import java.io.File;
import java.util.LinkedHashMap;

import org.iottree.core.Config;


/**
 * 模板分类 //，对应具体的页面中，可以放置1个或多个PageBlk
 * @author zzj
 */
public class TempletCat
{
	public static File getTempletBaseDir()
	{
		return new File(Config.getWebappBase() + "/_templet/");
	}
	
	String name ;
	
	String title ;
	
	LinkedHashMap<String,Templet> name2tmp = new LinkedHashMap<>() ;
	
	public TempletCat(String name,String title)
	{
		this.name = name ;
		this.title = title ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public File getCatDir()
	{
		return new File(getTempletBaseDir(),this.name+"/") ;
	}

	public LinkedHashMap<String,Templet> listTempletAll()
	{
		return this.name2tmp ;
	}
	
	public Templet getTemplet(String name)
	{
		return name2tmp.get(name) ;
	}
}
