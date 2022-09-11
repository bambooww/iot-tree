package org.iottree.core.comp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.apache.commons.io.FileUtils;
import org.iottree.core.UAManager;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.res.ResDir;
import org.iottree.core.res.ResManager;
import org.iottree.core.util.CompressUUID;
import org.w3c.dom.Element;

public class CompItem implements IXmlDataable ,IResNode
{
	public static final String TAG = "item" ;
	
	String id = null ;
	
	String title = null ;
	
	String desc = null ;
	
	transient String dataTxt = null ; 
	
	//transient CompCat belongTo = null ;
	
	private transient String resLibId = null ;
	
	private transient File compDir = null ;
	
	CompItem(String reslibid,File dir)
	{
		this.resLibId = reslibid ;
		compDir = dir ;
	}
	
	/**
	 * add and create new comp
	 * @param title
	 */
	CompItem(String reslibid,File parentdir,String title)
	{
		//belongTo = cc ;
		this.resLibId = reslibid ;
		id = CompressUUID.createNewId();//.randomUUID().toString() ;
		this.title = title ;
		compDir = new File(parentdir,id+"/") ;
	}
	
	public static CompItem loadFromDir(String reslibid,String id,File dir) throws Exception
	{
		if(!dir.exists())
			return null;
		File cif = new File(dir,"ci.xml") ;
		if(!cif.exists())
			return null ;
		
		XmlData xd = XmlData.readFromFile(cif) ;
		CompItem ci = new CompItem(reslibid,dir) ;
		ci.fromXmlData(xd);
		ci.id = id ;
		
		return ci ;
	}
	
//	static CompItem createByEle(Element ele)
//	{
//		CompItem ret = new CompItem() ;
//		ret.id = ele.getAttribute("id") ;
//		if(Convert.isNullOrEmpty(ret.id))
//			return null ;
//		ret.title = ele.getAttribute("title") ;
//		if(Convert.isNullOrEmpty(ret.title))
//			ret.title = "noname" ;
//		ret.desc = ele.getAttribute("desc") ;
//		return ret ;
//	}
	
	public File getCompItemDir()
	{
		return compDir ;
	}
	
//	public CompCat getBelongTo()
//	{
//		return belongTo ;
//	}
	
	void onLoaded()
	{
		//getResDir();
		//ResManager.getInstance().setResCxtRelated(this);
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public String getDesc()
	{
		return desc ;
	}
	
//	public File getCompItemDir()
//	{
//		File catdir = belongTo.getCatDirFile() ;
//		return new File(catdir,this.getId()+"/") ;
//	}
	
	public File getDataFile()
	{
		File f = new File(getCompItemDir(),"data.txt") ;
		return f ;
	}
	
//	public File getDataDir()
//	{
//		File catdir = belongTo.getCatDirFile() ;
//		return new File(catdir,this.id+"/") ;
//	}
	
	public void saveCompData(String txt) throws Exception
	{
		if(txt==null)
			return ;
		
		File f = getDataFile() ;
		try(FileOutputStream fos = new FileOutputStream(f);)
		{
			fos.write(txt.getBytes("UTF-8"));
		}
		dataTxt = txt;
	}
	
	private String loadCompData() throws IOException
	{
		File f = getDataFile() ;
		if(!f.exists())
			return "" ;
		return Convert.readFileTxt(f, "UTF-8") ;
	}
	
	public String getOrLoadCompData() throws IOException
	{
		if(dataTxt!=null)
			return dataTxt;
		dataTxt = loadCompData() ;
		return dataTxt ;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();
		xd.setParamValue("id", this.id);
		if(this.title!=null)
			xd.setParamValue("title", this.title);
		if(this.desc!=null)
			xd.setParamValue("desc", this.desc);
		return xd;
	}

	@Override
	public void fromXmlData(XmlData xd)
	{
		this.id = xd.getParamValueStr("id") ;
		this.title = xd.getParamValueStr("title") ;
		this.desc = xd.getParamValueStr("desc") ;
	}

	//ResDir resCxt = null ;
	
	/**
	 * name of editor which will use res
	 * @return
	 */
	public String getEditorName()
	{
		return "comp" ;
	}
	
	public String getEditorId()
	{
		return this.id ;
	}


	@Override
	public File getResNodeDir()
	{
		return this.getCompItemDir();
	}
	
//	@Override
//	public IResCxt getResCxt()
//	{
//		if(this.belongTo==null)
//			return null ;
//		return this.belongTo.getBelongTo();
//	}

	
//	@Override
//	public ResDir getResDir()
//	{
//		if(resCxt!=null)
//			return resCxt ;
//		File datadir = getDataDir() ;
//		File dir = new File(datadir,"_res/") ;
//		if(!dir.exists())
//			dir.mkdirs();
//		resCxt=new ResDir(this,this.getId(),this.getTitle(),dir);
//		return resCxt;
//	}
//
//	@Override
//	public IResNode getResNodeSub(String subid)
//	{
//		return null;
//	}
//
	@Override
	public String getResNodeId()
	{
		return this.getId();
	}
	
	@Override
	public String getResLibId()
	{
		return resLibId;
	}
//
//	
//	
	@Override
	public String getResNodeTitle()
	{
		return this.getTitle() ;
	}
//
//	@Override
//	public IResNode getResNodeParent()
//	{
//		return this.getBelongTo();
//	}

	
}
