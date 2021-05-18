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
	protected void copyTreeWithNewSelf(UANode new_self,String ownerid,boolean copy_id)
	{
		super.copyTreeWithNewSelf(new_self,ownerid,copy_id);
		UANodeOCTags self = (UANodeOCTags)new_self ;
		self.tags.clear();
		for(UATag t:tags)
		{
			UATag nt = new UATag() ;
			t.copyTreeWithNewSelf(nt,ownerid,copy_id);
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
		super.constructNodeTree();
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
		return tags;//tagList.listTags();
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
		List<UATag> tags = listTags() ;
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
