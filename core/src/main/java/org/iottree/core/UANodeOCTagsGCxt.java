package org.iottree.core;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;

@data_class
public abstract class UANodeOCTagsGCxt extends UANodeOCTagsCxt
{

	@data_obj(obj_c=UATagG.class)
	List<UATagG> taggs = new ArrayList<>() ;
	
	public UANodeOCTagsGCxt()
	{}
	
	public UANodeOCTagsGCxt(String name,String title,String desc)
	{
		super(name,title,desc) ;
	}
	

	/**
	 * 
	 * @param new_self create by copySelfWithNewId
	 */
	@Override
	protected void copyTreeWithNewSelf(UANode new_self,String ownerid,boolean copy_id)
	{
		super.copyTreeWithNewSelf(new_self,ownerid, copy_id) ;
		UANodeOCTagsGCxt self = (UANodeOCTagsGCxt)new_self ;
		//
		self.taggs.clear();
		for(UATagG tagg:taggs)
		{
			UATagG ntg = new UATagG() ;
			tagg.copyTreeWithNewSelf(ntg,ownerid, copy_id);
			self.taggs.add(ntg) ;
		}
	}
	
	
//	void constructNodeTree()
//	{
//		for(UATagG tgg:taggs)
//		{
//			//tgg.belongToDev = this ;
//			tgg.parentNode = this ;
//		}
//		
//		super.constructNodeTree();
//	}

	public List<UANode> getSubNodes()
	{
		List<UANode> rets = super.getSubNodes();
		rets.addAll(taggs);
		//rets.add(tagList);
		return rets;
	}
	

	public List<UATagG> getSubTagGs()
	{
		return taggs ;
	}
	

	public List<IOCBox> OC_getSubs()
	{
		List<IOCBox> rets = super.OC_getSubs();
		rets.addAll(taggs);
		return rets;
	}
	
	
	public UATagG getSubTagGByName(String n)
	{
		for(UATagG tg:taggs)
		{
			if(n.contentEquals(tg.getName()))
				return tg;
		}
		return null;
	}
	
	public UATagG getSubTagGById(String id)
	{
		for(UATagG tg:taggs)
		{
			if(id.contentEquals(tg.getId()))
				return tg;
		}
		return null;
	}
	
	public UATagG addTagG(String name,String title,String desc)
			 throws Exception
	{
		UAUtil.assertUAName(name);
		
//		UATagG d = this.getSubTagGByName(name) ;
//		UATag tg = getTagByName(name);
		UATagG d = this.getSubTagGByName(name) ;
		UATag tg = this.getTagByName(name) ;
		if(d!=null||tg!=null)
			throw new IllegalArgumentException("tag with name="+name+" existed") ;
		d = new UATagG(name,title,desc) ;
		d.id = this.getNextIdByRoot() ;
		taggs.add(d);
		constructNodeTree();
		this.save();
		return d ;
	}
	
	public UATagG updateTagG(UATagG tagg,String name,String title,String desc) throws Exception
	{
		UAUtil.assertUAName(name);

		UATagG ch = this.getSubTagGByName(name);
		if (ch != null&&ch!=tagg)
		{
			throw new IllegalArgumentException("tagg with name=" + name + " existed");
		}
		//ch = new UAHmi(name, title, desc, "");
		tagg.setNameTitle(name, title, desc);
		// ch.belongTo = this;
		//hmis.add(ch);
		this.constructNodeTree();

		save();
		return tagg;
	} 
	

	public boolean delSubTagG(UATagG tg) throws Exception
	{
		if(taggs.remove(tg))
		{
			UANode topn = this.getTopNode() ;
			if(!(topn instanceof ISaver))
				throw new Exception("top node cannot save") ;
			((ISaver)topn).save();
			return true;
		}
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
			tg.listTagsAll(tgs,bmid) ;
		}
	}
	
}
