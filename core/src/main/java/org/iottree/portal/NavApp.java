package org.iottree.portal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.web.AppWebConfig;
import org.w3c.dom.Element;

public class NavApp extends NavNode
{
	AppWebConfig appweb ;
	
	String appn ;
	
	String title_cn ;
	
	String title_en ;
	
	public NavApp(AppWebConfig awc)
	{
		this.appweb = awc ;
		this.appn = awc.getAppName() ;
		this.title_cn = awc.getTitleCn() ;
		this.title_en = awc.getTitleEn() ;
	}
	
	public String getIdName()
	{
		return appn;
	}
	
	public String getAppName()
	{
		return this.appn ;
	}
	
	public String getTitle()
	{
		String t = this.title_en ;
		if("cn".equals(Lan.getUsingLang()))
			t = this.title_cn ;
		if(Convert.isNullOrEmpty(t))
			return this.title_en ;
		return t ;
	}
	
	private static LinkedHashMap<String,NavApp> appn2nav = new LinkedHashMap<>() ;
	
	public static void loadFromWebConfig(AppWebConfig awc)
	{
		Element navele = awc.getConfElement("nav") ;
		if(navele==null)
			return ;
		NavApp na = NavFrame.loadNavApp(awc, navele) ;
		if(na==null)
			return ;
		appn2nav.put(na.appn,na) ;
	}
	
	public static List<NavApp> listNavAppAll()
	{
		ArrayList<NavApp> ret = new ArrayList<>() ;
		ret.addAll(appn2nav.values()) ;
		return ret ;
	}
	

	public static NavNode getNavNodeByUID(String uid)
	{
		int k = uid.indexOf('.') ;
		if(k<=0)
			return null ;
		String appn = uid.substring(0,k) ;
		NavApp na = appn2nav.get(appn) ;
		if(na==null)
			return null ;
		String subn = uid.substring(k+1) ;
		return na.getChildNodeById(subn) ;
	}
	
}
