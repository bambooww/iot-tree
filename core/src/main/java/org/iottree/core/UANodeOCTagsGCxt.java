package org.iottree.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;

@data_class
public abstract class UANodeOCTagsGCxt extends UANodeOCTagsCxt
{
	public static final int REF_LOCKED_UNKNOWN = 0 ;
	public static final int REF_LOCKED = 1 ;
	public static final int REF_LOCKED_NOT = -1 ;
	/**
	 * 0-unknown and inheret from parent
	 * 1 - locked loc ,will override parent
	 * -1 - not locked loc,will override parent
	 */
	@data_val(param_name="ref_locked_loc")
	int refLockedLoc = REF_LOCKED_UNKNOWN;
	
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
	protected void copyTreeWithNewSelf(IRoot root,UANode new_self,String ownerid,
			boolean copy_id,boolean root_subnode_id,HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		super.copyTreeWithNewSelf(root,new_self,ownerid, copy_id,root_subnode_id,rf2new) ;
		UANodeOCTagsGCxt self = (UANodeOCTagsGCxt)new_self ;
		//
		self.taggs.clear();
		for(UATagG tagg:taggs)
		{
			UATagG ntg = new UATagG() ;
			if(root_subnode_id)
			{
				if(root!=null)
					ntg.id = root.getRootNextId();
				else
					ntg.id = this.getNextIdByRoot();
			}
			tagg.copyTreeWithNewSelf(root,ntg,ownerid, copy_id, root_subnode_id,rf2new);
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
	
//	/**
//	 * check this node can edit tag or node in project.
//	 * 1)node is UADev or UATagG
//	 * 2) UADev has no refdev id
//	 * 
//	 * @return
//	 */
//	public boolean canEditInPrj()
//	{
//		UADev d = getBelongToDev();
//		if(d==null)
//			return false;
//		return !d.hasDevRefId();
//	}
	
	/**
	 * UADev ref from DevDef or other IOTTree Node will lock sub tree structure
	 * so it's tags cannot be edited. 
	 * @return
	 */
	public final boolean isRefLocked()
	{
		int rl = getRefLockedLoc() ;
		if(rl>0)
			return true ;
		if(rl<0)
			return false;
		
		//check parent
		UANode curn = this ;
		while((curn=curn.getParentNode())!=null)
		{
			if(curn instanceof UANodeOCTagsGCxt)
			{
				UANodeOCTagsGCxt pn = (UANodeOCTagsGCxt)curn ;
				rl = pn.getRefLockedLoc() ;
				if(rl>0)
					return true ;
				if(rl<0)
					return false;
			}
		}
		return false;
	}
	
	
	protected int getRefLockedLoc()
	{
		return refLockedLoc ;
	}
	
	public UANodeOCTagsGCxt withRefLockedLoc(int rl)
	{
		this.refLockedLoc = rl ;
		return this;
	}
	
	
	static ArrayList<PropGroup> cxt_tags = null;//new ArrayList<>();
	static
	{
//		PropGroup pg = new PropGroup("cxt_tags","Context Tags") ;
//		pg.addPropItem(new PropItem("ref_locked","Ref Locked","Can edit tags context or not",PValTP.vt_bool,true,
//				null,null,0));
//		//ddPropItem(new PropItem("desc","Description","Object's Description",PValTP.vt_str,false,null,null,""));
//		
//		cxt_tags.add(pg) ;
	}
	
	
	@Override
	public List<PropGroup> listPropGroups()
	{
		if(cxt_tags!=null)
			return cxt_tags;
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		List<PropGroup> lpgs = super.listPropGroups() ;
		if(lpgs!=null)
			pgs.addAll(lpgs) ;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		PropGroup pg = new PropGroup("cxt_tags",lan);//"Context Tags") ;
		pg.addPropItem(new PropItem("ref_locked",lan,PValTP.vt_bool,true,
				null,null,0)); //"Ref Locked","Can edit tags context or not"
		pgs.add(pg) ;
		//
		cxt_tags = pgs;
		return pgs;
	}
	
	public Object getPropValue(String groupn,String itemn)
	{
		if("cxt_tags".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "ref_locked":
				return this.isRefLocked();
			
			}
		}
		return super.getPropValue(groupn, itemn);
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
		return addTagG(name,title,desc,true);
	}
	
	public UATagG addTagG(String name,String title,String desc,boolean bsave)
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
		if(bsave)
			this.save();
		return d ;
	}
	
	public UATagG updateTagG(UATagG tagg,String name,String title,String desc) throws Exception
	{
		UAUtil.assertUAName(name);

		UATagG subtg = this.getSubTagGByName(name);
		if (subtg != null&&subtg!=tagg)
		{
			throw new IllegalArgumentException("tagg with name=" + name + " existed");
		}
		
		//ch = new UAHmi(name, title, desc, "");
		if(!tagg.setNameTitle(name, title, desc))
			return tagg; //not chg
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


	public UATag addTagWithGroupByPath(List<String> path,UAVal.ValTP vt,boolean bsave) throws Exception
	{
		int s = path.size() ;
		if(s<=0)
			return null ;
		String n = path.get(0) ;
		if(s==1)
		{
			return this.addTag(n, n, "", vt, bsave) ;
		}
		
		UATagG tg = this.getSubTagGByName(n) ;
		if(tg==null)
			tg = this.addTagG(n, n, "") ;
		for(int i = 1 ; i < s - 1 ; i ++)
		{
			n = path.get(i) ;
			UATagG tg0 = tg.getSubTagGByName(n) ;
			if(tg0==null)
				tg0 = tg.addTagG(n, n, "") ;
			tg = tg0 ;
		}
		n = path.get(s-1) ;//last
		return tg.addTag(n, n, "", vt, bsave) ;
	}
	
	public UATag addTagWithGroupByPath(String path,String vtstr,boolean bsave) throws Exception
	{
		List<String> ps = Convert.splitStrWith(path, ".") ;
		UAVal.ValTP vt = UAVal.getValTp(vtstr) ;
		if(vt==null)
			throw new Exception("no valtp with "+vtstr) ;
		return addTagWithGroupByPath(ps, vt,bsave) ;
	}
	
	public UATag getTagByPath(String pathstr)
	{
		UANode tn = this.getDescendantNodeByPath(pathstr) ;
		if(tn==null||!(tn instanceof UATag))
			return null ;
		return (UATag)tn ;
	}
	
//	protected void listTagsAll(List<UATag> tgs,boolean bmid)
//	{
//		if(bmid)
//		{
//			for(UATag tg:listTags())
//			{
//				if(tg.isMidExpress())
//					tgs.add(tg) ;
//			}
//		}
//		else
//			tgs.addAll(this.listTags());
//		for(UATagG tg:taggs)
//		{
//			tg.listTagsAll(tgs,bmid) ;
//		}
//	}
	
	/**
	 * when recved an data package which has multi tags in this GGxt will be updated
	 * @param subpath2val
	 */
	public void RT_updateSubTags(HashMap<String,UAVal> subpath2val)
	{
	// TODO 	
	}
}
