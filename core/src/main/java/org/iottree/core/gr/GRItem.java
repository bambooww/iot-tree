package org.iottree.core.gr;

import org.w3c.dom.Element;

public class GRItem
{
public static final String PN_JRF = "jrf";
	
	public static final String TAG_GR = "gr" ;
	
	String name=null ;
	String title_cn = null ;
	String title_en = null ;
	String jrfn = null ;
	
	GRCat belongCat = null ;
	
	GRItem(String abspath,Element giele)
	{
		//name = giele.getAttribute(GRCat.PN_NAME) ;
		title_cn = giele.getAttribute(GRCat.PN_TITLE_CN) ;
		title_en = giele.getAttribute(GRCat.PN_TITLE_EN) ;
		name = giele.getAttribute(GRItem.PN_JRF) ;
		jrfn = abspath+name ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitleCN()
	{
		return title_cn ;
	}
	
	public String getTitleEN()
	{
		return title_en ;
	}
	
	public String getJRFPath()
	{
		return jrfn ;
	}
	
	/**
	 * 获得引用路径
	 * @return
	 */
	public String getRefPath()
	{
		return GRManager.REF_HEAD+belongCat.name+"/"+name ;
	}
}
