package org.iottree.core;

import java.util.*;

import org.iottree.core.util.Lan;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.cxt.JsDef;

/**
 * Tag Group
 * @author zzj
 *
 */
@data_class
@JsDef(name="tagg",title="TagG",desc="Tag Group Node",icon="icon_tagg")
public class UATagG extends UANodeOCTagsGCxt implements IOCUnit
{
	public static final String NODE_TP = "tagg" ;
	
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
	public String getNodeTp()
	{
		return NODE_TP;
	}
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
	
	protected int getRefLockedLoc()
	{
		if(super.getRefLockedLoc()!=0)
			return super.getRefLockedLoc();

		UANode pn = this.getParentNode() ;
		if(pn!=null&&pn instanceof UACh)
			return REF_LOCKED_NOT;
		return REF_LOCKED_UNKNOWN ;
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
	
	private List<PropGroup> tagGPGS = null ;
	
	@Override
	protected void onPropNodeValueChged()
	{
		tagGPGS = null ;
	}
	

	@Override
	public List<PropGroup> listPropGroups()
	{
		UANode pnode = this.getParentNode() ;
		if(!(pnode instanceof UADev))
		{
			return super.listPropGroups() ;
		}

		if(tagGPGS!=null)
			return tagGPGS;
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		List<PropGroup> lpgs = super.listPropGroups() ;
		if(lpgs!=null)
			pgs.addAll(lpgs) ;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		PropGroup pg = new PropGroup("tagg_tags_pm",lan);//,"Device run parameters") ;
		pg.addPropItem(new PropItem("read_intv_en",lan,PValTP.vt_bool,false,null,null,false)); 
		pg.addPropItem(new PropItem("read_intv",lan,PValTP.vt_int,false,null,null,100)); //"Read Interval MS",""
		pgs.add(pg) ;
		//pgs.add(this.getDevPropGroup()) ;
		//
		
		tagGPGS = pgs;
		return pgs;
	}
	
	public boolean isReadIntvEnabled()
	{
		return this.getOrDefaultPropValueBool("tagg_tags_pm", "read_intv_en", false) ;
	}
	
	public int getReadIntvMS()
	{
		return this.getOrDefaultPropValueInt("tagg_tags_pm", "read_intv", 100) ;
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
