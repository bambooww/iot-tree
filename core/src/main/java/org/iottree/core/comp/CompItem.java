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
import org.iottree.core.UAManager;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResCxtRelated;
import org.iottree.core.res.ResCxt;
import org.iottree.core.res.ResCxtManager;
import org.iottree.core.util.CompressUUID;
import org.w3c.dom.Element;

public class CompItem implements IXmlDataable,IResCxt,IResCxtRelated
{
	public static final String TAG = "item" ;
	
	String id = null ;
	
	String title = null ;
	
	String desc = null ;
	
	transient String dataTxt = null ; 
	
	transient CompCat belongTo = null ;
	
	public CompItem()
	{}
	
	/**
	 * add and create new comp
	 * @param title
	 */
	public CompItem(CompCat cc,String title)
	{
		belongTo = cc ;
		id = CompressUUID.createNewId();//.randomUUID().toString() ;
		this.title = title ;
	}
	
	static CompItem createByEle(Element ele)
	{
		CompItem ret = new CompItem() ;
		ret.id = ele.getAttribute("id") ;
		if(Convert.isNullOrEmpty(ret.id))
			return null ;
		ret.title = ele.getAttribute("title") ;
		if(Convert.isNullOrEmpty(ret.title))
			ret.title = "noname" ;
		ret.desc = ele.getAttribute("desc") ;
		return ret ;
	}
	
	
	
	public CompCat getBelongTo()
	{
		return belongTo ;
	}
	
	void onLoaded()
	{
		getResCxt();
		ResCxtManager.getInstance().setResCxtRelated(this);
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
	
	public File getDataFile()
	{
		File catdir = belongTo.getCatDirFile() ;
		return new File(catdir,this.id+".data.txt") ;
	}
	
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

	ResCxt resCxt = null ;
	
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
	public ResCxt getResCxt()
	{
		if(resCxt!=null)
			return resCxt ;
		File catdir = belongTo.getCatDirFile() ;
		File dir = new File(catdir,"_res_"+this.getId()+"/") ;
		if(!dir.exists())
			dir.mkdirs();
		resCxt=new ResCxt("comp",this.getId(),this.getTitle(),dir);
		return resCxt;
	}

	@Override
	public List<ResCxt> getResCxts()
	{
		ArrayList<ResCxt> rets = new ArrayList<>(2) ;
		ResCxt rc = this.getBelongTo().getResCxt() ;
		rets.add(getResCxt()) ;
		rets.add(rc) ;
		return rets;
	}
	
}
