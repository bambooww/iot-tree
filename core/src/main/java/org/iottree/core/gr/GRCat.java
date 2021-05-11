package org.iottree.core.gr;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.w3c.dom.Element;

public class GRCat
{
	public static final String PN_TITLE_CN = "title_cn" ;
	public static final String PN_TITLE_EN = "title_en" ;
	
	String name = null ;
	String title_cn = null;
	String title_en = null ;

	transient ArrayList<GRItem> subItems = new ArrayList<GRItem>() ;
	
	GRCat(String n,String cn,String en)
	{
		this.name = n ;
		this.title_cn = cn ;
		this.title_en = en ;
	}
	
	public GRCat(String pdir,String n,Element gisele)
	{
		this.name = n ;
		title_cn = gisele.getAttribute(PN_TITLE_CN) ;
		title_en = gisele.getAttribute(PN_TITLE_EN) ;
		
		for(Element giele:Convert.getSubChildElement(gisele,GRItem.TAG_GR))
		{
			GRItem gii = new GRItem(pdir+n,giele) ;
			gii.belongCat = this ;
			subItems.add(gii) ;
		}
	}
	
	public List<GRItem> getGRItems()
	{
		return subItems ;
	}
	/**
	 * 
	 * @return
	 */
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
}
