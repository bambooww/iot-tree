package org.iottree.core.comp;

import java.io.File;
import java.util.*;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.res.ResDir;
import org.iottree.core.util.CompressUUID;
import org.w3c.dom.Element;


public class CompCat implements IResNode
{
	public static final String TAG = "cat" ;
	
	/**
	 * uuid
	 */
	String id = null ;
	
	String title = null ;
	
	transient boolean bReadonly = false;
	
	transient String dirName= null ;
	
	transient ArrayList<CompItem> items = new ArrayList<>() ;
	
	public CompCat()
	{}
	
	/**
	 * add and create a new cat
	 * @param title
	 */
	public CompCat(String title)
	{
		this.id = CompressUUID.createNewId();//.randomUUID().toString() ;
		this.dirName = this.id ;
		this.title = title ;
	}
	
//	public CompCat(Element gisele)
//	{
//		this.id = id ;
//		this.title = title ;
//		
//		
//	}
	
	public static CompCat createByEle(Element catele)
	{
		if(catele==null)
			return null ;
		CompCat ret = new CompCat() ;
		ret.id = catele.getAttribute("id") ;
		if(Convert.isNullOrEmpty(ret.id))
			return null ;
		ret.title = catele.getAttribute("title") ;
		if(Convert.isNullOrEmpty(ret.title))
			ret.title = "noname" ;
		for(Element ele:Convert.getSubChildElement(catele,CompItem.TAG))
		{
			CompItem ci = CompItem.createByEle(ele) ;
			if(ci==null)
				continue ;
			ci.belongTo = ret ;
			ret.items.add(ci) ;
			
		}
		return ret ;
	}
	
	public static CompCat createByXD(XmlData xd)
	{
		CompCat cc = new CompCat() ;
		cc.fromXmlData(xd);
		return cc ;
	}
	/**
	 * 
	 * @return
	 */
	public String getId()
	{
		return id ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	/**
	 * modify by outer in file system
	 * @return
	 */
	public boolean isReadOnly()
	{
		return bReadonly ;
	}
	
	/**
	 * get cat dir
	 * @return
	 */
	public File getCatDirFile()
	{
		File fb = CompManager.getInstance().fileDirBase ;
		return new File(fb,dirName);
	}
	
	public List<CompItem> getItems()
	{
		return items ;
	}
	
	public CompItem getItemById(String id)
	{
		for(CompItem ci:items)
		{
			if(id.contentEquals(ci.getId()))
				return ci ;
		}
		return null ;
	}
	
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("id", id);
		xd.setParamValue("title", title);
		List<XmlData> xds = xd.getOrCreateSubDataArray("items") ;
		if(items!=null)
		{
			for(CompItem ci:items)
			{
				xds.add(ci.toXmlData()) ;
			}
		}
		return xd ;
	}
	
	public void fromXmlData(XmlData xd)
	{
		this.id = xd.getParamValueStr("id") ;
		this.title = xd.getParamValueStr("title") ;
		List<XmlData> xds = xd.getSubDataArray("items") ;
		if(xds!=null)
		{
			for(XmlData tmpxd :xds)
			{
				CompItem ci = new CompItem() ;
				ci.fromXmlData(tmpxd);
				ci.belongTo = this ;
				this.items.add(ci) ;
			}
		}
	}

	ResDir resCxt = null ;
	
	@Override
	public ResDir getResDir()
	{
		if(resCxt!=null)
			return resCxt ;
		File catdir = getCatDirFile() ;
		File dir = new File(catdir,"_res/") ;
		if(!dir.exists())
			dir.mkdirs();
		resCxt=new ResDir(CompManager.getInstance(),this.getId(),this.getTitle(),dir);
		return resCxt;
	}

	@Override
	public IResNode getResNodeSub(String subid)
	{
		return this.getItemById(subid) ;
	}

	public IResNode getResNodeParent()
	{
		return CompManager.getInstance() ;
	}

	@Override
	public String getResNodeId()
	{
		return this.getId();
	}
	
	@Override
	public String getResNodeTitle()
	{
		return this.getTitle() ;
	}
}
