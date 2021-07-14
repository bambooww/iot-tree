package org.iottree.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;

@data_class
public abstract class UANodeOCTags extends UANodeOC
{
	@data_obj(obj_c=UATag.class)
	List<UATag> tags = new ArrayList<>();
	
	// -  for system tags
	
	private transient ArrayList<UATag> sysTags = null;
	
	public UANodeOCTags()
	{
		super();
	}
	
	public UANodeOCTags(String name,String title,String desc)
	{
		super(name,title,desc) ;
	}
	
	/**
	 * 
	 * @param new_self create by copySelfWithNewId
	 */
	@Override
	protected void copyTreeWithNewSelf(UANode new_self,String ownerid,boolean copy_id,boolean root_subnode_id)
	{
		super.copyTreeWithNewSelf(new_self,ownerid,copy_id,root_subnode_id);
		UANodeOCTags self = (UANodeOCTags)new_self ;
		self.tags.clear();
		for(UATag t:tags)
		{
			UATag nt = new UATag() ;
			if(root_subnode_id)
				nt.id  = this.getNextIdByRoot();
			t.copyTreeWithNewSelf(nt,ownerid,copy_id,root_subnode_id);
			self.tags.add(nt) ;
		}
	}

//	public UATagList getTagList()
//	{
//		return tagList ;
//	}
	
	public List<UANode> getSubNodes()
	{
		ArrayList<UANode> rets = new ArrayList<>() ;
		rets.addAll(tags);
		if(sysTags!=null)
			rets.addAll(sysTags) ;
		return rets;
	}
	
	
//	@Override
//	public List<UAMember> getMembers()
//	{
//		List<UAMember> rets = super.getMembers();
//		rets.add(tagList) ;
//		return rets ;
//	}
	
//	public UARep getTopUARep()
//	{
//		UANode tn = this.getTopNode() ;
//		if(tn instanceof UARep)
//			return (UARep)tn ;
//		return null ;
//	}
	
	public void save() throws Exception
	{
//		UARep rep = getTopUARep() ;
//		if(rep==null)
//			throw new Exception("no rep found") ;
		UANode tn = this.getTopNode() ;
		if(!(tn instanceof ISaver))
			throw new Exception("top node is not saver") ;
		((ISaver)tn).save(); 
	}
	
	void constructNodeTree()
	{
		for(UATag tg:tags)
		{
			tg.belongToNode = this ;
			//tg.belongToNode = this ;
		}
		
		if(sysTags!=null)
		{
			for(UATag tg:sysTags)
			{
				tg.belongToNode = this ;
				//tg.belongToNode = this ;
			}
		}
		super.constructNodeTree();
	}
	
	
	public List<UANodeOCTags> listSelfAndSubTagsNode()
	{
		ArrayList<UANodeOCTags> rets = new ArrayList<>() ;
		listSelfAndSubTagsNode(rets) ;
		return rets ;
	}
	
	
	private void listSelfAndSubTagsNode(List<UANodeOCTags> tags)
	{
		tags.add(this) ;
		List<UANode> ns = this.getSubNodes() ;
		if(ns==null)
			return ;
		for(UANode n:ns)
		{
			if(n instanceof UANodeOCTags)
			{
				((UANodeOCTags)n).listSelfAndSubTagsNode(tags) ;
			}
		}
	}

	public UATag addOrUpdateTag(String tagid,
			boolean bmid,String name,String title,String desc,
			String addr,UAVal.ValTP vt,boolean canw,long srate) throws Exception
	{
		UAUtil.assertUAName(name);
		UATag d = null ;
		if(Convert.isNotNullEmpty(tagid))
		{
			d = this.getTagById(tagid) ;
			if(d==null)
				throw new Exception("no tag with id="+tagid+" found!") ;
		}
		
		UANode tmpn = getSubNodeByName(name) ;
		if(d==null&&tmpn!=null)
			throw new IllegalArgumentException("tag with name="+name+" existed") ;
		if(d!=null&&tmpn!=null&&d!=tmpn)
			throw new IllegalArgumentException("tag with name="+name+" existed") ;
		
		if(d==null)
		{
			if(bmid)
				d = new UATag(name,title,desc,addr,vt) ;
			else
				d = new UATag(name,title,desc,addr,vt,canw,srate) ;
			d.id = this.getNextIdByRoot() ;
			tags.add(d) ;
			constructNodeTree();
		}
		else
		{
			if(bmid)
				d.setTagMid(name,title,desc,addr,vt) ;
			else
				d.setTagNor(name,title,desc,addr,vt,canw,srate) ;
		}
		save();
		return d ;
	}
	
	public UATag addTag(DevItem item) throws Exception
	{
		String name =item.getName() ;
		UAUtil.assertUAName(name);
		UANode tmpn = getSubNodeByName(name) ;
		//UATagG tgg = this.getSubTagGByName(name) ;
		//UATag d = getTagByName(name);
		//if(tgg!=null||d!=null)
		if(tmpn!=null)
		{
			throw new IllegalArgumentException("tag with name="+name+" existed") ;
		}
		UATag d = new UATag(item) ;
		d.id = this.getNextIdByRoot() ;
		tags.add(d) ;
		constructNodeTree();
		save();
		return d ;
	}
	
	public boolean delTag(UATag t) throws Exception
	{
		if(this.tags.remove(t))
		{
			this.save();
			return true ;
		}
		return false;
	}
	/**
	 * list all tags in sub tree node and decedent node
	 * @return
	 */
	public List<UATag> listTags()
	{
		if(this.sysTags==null)
		{
			return tags;//tagList.listTags();
		}
		ArrayList<UATag> rets = new ArrayList<>(tags.size()+sysTags.size()) ;
		rets.addAll(this.tags) ;
		rets.addAll(this.sysTags) ;
		return rets ;
	}
	
	public List<UATag> getNorTags()
	{
		return tags ;
	}
	/**
	 * list local mid tags
	 * @return
	 */
	public List<UATag> listTagsMid()
	{
		ArrayList<UATag> rets = new ArrayList<>() ;
		for(UATag tg:listTags())
		{
			if(tg.isMidExpress())
				rets.add(tg) ;
		}
		return rets ;
	}
	
	public final List<UATag> listTagsAll()
	{
		ArrayList<UATag> rets =new ArrayList<>() ;
		listTagsAll(rets,false);
		return rets ;
	}
	
	public final List<UATag> listTagsMidAll()
	{
		ArrayList<UATag> rets =new ArrayList<>() ;
		listTagsAll(rets,true);
		return rets ;
	}
	
	protected abstract void listTagsAll(List<UATag> tgs,boolean bmid) ;
	
	
//	{
//		ArrayList<UATag> rets = new ArrayList<>() ;
//		this.getTagList();
//		return rets ;
//	}

	
	public UATag getTagByName(String n)
	{
		for(UATag t:tags)
		{
			if(n.contentEquals(t.getName()))
				return t ;
		}
		return null ;
	}
	
	public UATag getTagById(String id)
	{
		for(UATag t:tags)
		{
			if(id.contentEquals(t.getId()))
				return t ;
		}
		return null ;
	}
	
	
	public UATag getTagByCxtPath(String cxtpath)
	{
		//this.fin
		List<String> pns = Convert.splitStrWith(cxtpath, "/.") ;
		return null;
	}
//	/**
//	 *  xxx.xxx.xxx
//	 * @param path
//	 * @return
//	 */
//	public UATag getTagByPath(String path)
//	{
//		LinkedList<String> ps = Convert.splitStrWithLinkedList(path, ".") ;
//		if(ps.size()==1)
//			return getTagByName(path) ;
//		String last_tagn = ps.removeLast() ;
//		UANode n = this.getDescendantNodeByPath(ps) ;
//		if(n==null)
//			return null ;
//		if(!(n instanceof UANodeOCTags))
//			return null ;
//		return ((UANodeOCTags)n).getTagByName(last_tagn) ;
//	}
	

	public List<DevAddr> listTagsAddrAll()
	{
		List<UATag> tags = listTagsAll() ;
		if(tags==null||tags.size()<=0)
		{
			return null ;
		}
		ArrayList<DevAddr> addrs = new ArrayList<>() ;
		StringBuilder sb = new StringBuilder() ;
		for(UATag tag:tags)
		{
			if(tag.isMidExpress())
				continue;
			DevAddr da = tag.getDevAddr(sb);
			if(da==null)
				continue ;
			addrs.add(da);
		}
		return addrs ;
	}
	
	protected void onNodeChanged()
	{
		super.onNodeChanged();
		
		this.RT_init(true,false);
	}
	
	
	
	public List<UATag> getSysTags()
	{
		return sysTags ;
	}
	
	
	public UATag getSysTagByName(String n)
	{
		List<UATag> ts = getSysTags() ;
		if(ts==null)
			return null ;
		for(UATag t:ts)
		{
			if(n.equals(t.getName()))
				return t ;
		}
		return null ;
	}
	
	protected final synchronized void setSysTag(String name,String title,String desc,UAVal.ValTP vt)
	{
		if(!name.startsWith("_"))
			throw new IllegalArgumentException("system tag name must start with _") ;
		if(sysTags==null)
			sysTags = new ArrayList<>() ;
		UATag t = this.getSysTagByName(name) ;
		if(t==null)
		{
			t = new UATag();
			t.setTagSys(name,title,desc,"", vt,false,200);
			sysTags.add(t) ;
		}
		else
			t.setTagSys(name, title, desc, "", vt, false, 200);
	}
	
	/**
	 * node will be init before start RT
	 */
	void RT_init(boolean breset,boolean b_sub)
	{
		if(breset)
			sysTags= null ;
		
		if(b_sub)
		{
			List<UANode> subn = this.getSubNodes() ;
			if(subn==null)
				return ;
			for(UANode sub:subn)
			{
				if(sub instanceof UANodeOCTags)
				{
					((UANodeOCTags)sub).RT_init(breset,b_sub);
				}
			}
		}
	}
	
	/**
	 * will be called interval to update sys tag values
	 */
	protected void RT_flush()
	{
		
	}
	
	final void RT_runFlush()
	{
		this.RT_flush();
		for(UANode subn:this.getSubNodes())
		{
			if(subn instanceof UANodeOCTags)
			{
				((UANodeOCTags)subn).RT_runFlush();
			}
		}
	}
	
	
	boolean RT_setSysTagVal(String name,Object v)
	{
		return RT_setSysTagVal(name, v,true) ;
	}
	
	
	final boolean RT_setSysTagVal(String name,Object v,boolean ignore_nochg)
	{
		UATag t = this.getSysTagByName(name) ;
		if(t==null)
			return false;
		t.RT_setVal(v,ignore_nochg);
		return true;
	}
	
	public List<IOCBox> OC_getSubs()
	{
		List<IOCBox> rets = super.OC_getSubs() ;
		//ArrayList<IOCBox> rets = new ArrayList<>() ;
		rets.addAll(tags);
		return rets;
	}
	
	public Object JS_get(String  key)
	{
		Object r = super.JS_get(key) ;
		if(r!=null)
			return r ;
		return this.getTagByName(key) ;
	}
	
	public List<Object> JS_names()
	{
		List<Object> rets = super.JS_names() ;
		
		List<UATag> tags = listTags() ;
		for(UATag tag:tags)
		{
			rets.add(tag.getName()) ;
		}
		return rets ;
	}
}
