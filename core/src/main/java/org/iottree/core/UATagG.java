package org.iottree.core;

import java.util.*;

import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.basic.PropGroup;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Tag Group
 * @author zzj
 *
 */
@data_class
public class UATagG extends UANodeOCTagsGCxt implements IOCUnit
{
	//transient UADev belongToDev = null;
	
	//transient UATagG parentTagG = null ;
	
	
//	@data_obj(obj_c=UATag.class)
//	List<UATag> tags = new ArrayList<>() ;
	
	//@data_obj(obj_c=UATagList.class,param_name = "taglist")
	//UATagList tagList = new UATagList();
	
	public UATagG()
	{}
	
	public UATagG(String name,String title,String desc)
	{
		super(name,title,desc);
	}
	
	
	UATagG(UANodeOCTagsGCxt tg)
	{
		super(tg.getName(),tg.getTitle(),tg.getDesc());
	}
//	
//	void constructNodeTree()
//	{
//		for(UATagG tgg:taggs)
//		{
//			tgg.belongToDev = belongToDev ;
//		}
//		
////		for(UATag tg:tags)
////		{
////			tg.belongToDev = belongToDev ;
////		}
//		//tagList.belongToNode = this;
//		
//		super.constructNodeTree();
//	}
	
//	public UADev getBelongToDev()
//	{
//		return belongToDev;
//	}
	
	public boolean isInDev()
	{
		UANode pn = this ;
		do
		{
			pn = pn.getParentNode();
			if(pn==null)
				return false;
			if(pn instanceof UADev)
				return true ;
			if(pn instanceof DevDef)
				return true;
		}while(pn!=null);
		return false;
	}
	
	public boolean chkValid()
	{
		return true;
	}
	
//	
//	public List<UATagG> getSubTagGs()
//	{
//		return taggs ;
//	}
//	
//	public UATagG getSubTagGByName(String n)
//	{
//		for(UATagG tgg:taggs)
//		{
//			if(n.contentEquals(tgg.getName()))
//				return tgg ;
//		}
//		return null ;
//	}
//	
//	public boolean delSubTagG(UATagG tg) throws Exception
//	{
//		if(this.taggs.remove(tg))
//		{
//			this.getBelongToDev().getRep().save();
//			return true;
//		}
//		return false;
//	}

	public UANodeOCTagsGCxt getBelongTo()
	{
		UANode pn = this.getParentNode() ;
		if(pn==null)
			return null;
		if(pn instanceof UANodeOCTagsGCxt)
			return (UANodeOCTagsGCxt)pn ;
		return null ;
	}

	public boolean delFromParent() throws Exception
	{
		UANode pn = this.getParentNode() ;
		if(pn==null)
			return false;
		
		if(pn instanceof UANodeOCTagsGCxt)
		{
			if(((UANodeOCTagsGCxt)pn).delSubTagG(this))
				return true;
		}
//		else if(pn instanceof UADev)
//		{
//			if(((UADev)pn).delSubTagG(this))
//				return true;
//		}
		return false;
	}
	
	
	
	protected void listTagsAll(List<UATag> tgs,boolean bmid)
	{
		if(bmid)
		{
			for(UATag tg:listTags())
			{
				if(tg.isMidExpress())
					tgs.add(tg) ;
			}
		}
		else
			tgs.addAll(this.listTags());
		for(UATagG tg:taggs)
		{
			tg.listTagsAll(tgs,bmid);
		}
	}
	
//	 public List<UATag> listTagsAll()
//	 {
//		 ArrayList<UATag> rets = new ArrayList<>() ;
//		 listTagsAll(rets) ;
//		 return rets ;
//	 }


	
	@Override
	protected void onPropNodeValueChged()
	{
		
	}

	@Override
	public String OCUnit_getUnitTemp()
	{
		return "tagg";
	}
	
	public List<IOCBox> OC_getSubs()
	{
		List<IOCBox> rets = super.OC_getSubs();
		rets.addAll(taggs);
		//rets.add(tagList);
		return rets;
	}
	
	public boolean OC_supportSub()
	{
		return true;
	}
//	
//	protected Object JS_get(String  key)
//	{
//		UATag tg = this.getTagByName(key) ;
//		if(tg!=null)
//			return tg ;
//		UATagG  tgg = this.getSubTagGByName(key) ;
//		if(tgg!=null)
//			return tgg ;
//
//		return null ;
//	}

	@Override
	public boolean CXT_containsKey(String jsk)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object CXT_getByKey(String jsk)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
