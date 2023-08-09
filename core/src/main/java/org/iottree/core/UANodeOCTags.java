package org.iottree.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;

@data_class
public abstract class UANodeOCTags extends UANodeOC
{
	@data_obj(obj_c = UATag.class)
	List<UATag> tags = new ArrayList<>();
	
	//@data_obj(param_name = "local_tags",obj_c = UATag.class)
	//List<UATag> localTags = new ArrayList<>();

	// - for system tags
	@data_obj(param_name = "sys_tags", obj_c = UATag.class)
	ArrayList<UATag> sysTags = new ArrayList<>();

	public UANodeOCTags()
	{
		super();
	}

	public UANodeOCTags(String name, String title, String desc)
	{
		super(name, title, desc);
	}

	/**
	 * 
	 * @param new_self
	 *            create by copySelfWithNewId
	 */
	@Override
	protected void copyTreeWithNewSelf(IRoot root,UANode new_self, String ownerid, 
			boolean copy_id, boolean root_subnode_id,HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		super.copyTreeWithNewSelf(root,new_self, ownerid, copy_id, root_subnode_id,rf2new);
		UANodeOCTags self = (UANodeOCTags) new_self;
		self.tags.clear();
		for (UATag t : tags)
		{
			UATag nt = new UATag();
			if (root_subnode_id)
			{
				if(root!=null)
					nt.id = root.getRootNextId();
				else
					nt.id = this.getNextIdByRoot();
			}
				
			t.copyTreeWithNewSelf(root,nt, ownerid, copy_id, root_subnode_id,rf2new);
			self.tags.add(nt);
		}
	}

	// public UATagList getTagList()
	// {
	// return tagList ;
	// }

	public List<UANode> getSubNodes()
	{
		ArrayList<UANode> rets = new ArrayList<>();
		rets.addAll(tags);
		if (sysTags != null)
			rets.addAll(sysTags);
//		if(localTags!=null)
//			rets.addAll(localTags);
		return rets;
	}

	// @Override
	// public List<UAMember> getMembers()
	// {
	// List<UAMember> rets = super.getMembers();
	// rets.add(tagList) ;
	// return rets ;
	// }

	// public UARep getTopUARep()
	// {
	// UANode tn = this.getTopNode() ;
	// if(tn instanceof UARep)
	// return (UARep)tn ;
	// return null ;
	// }

	public void save() throws Exception
	{
		// UARep rep = getTopUARep() ;
		// if(rep==null)
		// throw new Exception("no rep found") ;
		UANode tn = this.getTopNode();
		if (!(tn instanceof ISaver))
			throw new Exception("top node is not saver");
		((ISaver) tn).save();
	}

	void constructNodeTree()
	{
		for (UATag tg : tags)
		{
			tg.belongToNode = this;
			// tg.belongToNode = this ;
		}

		if (sysTags != null)
		{
			for (UATag tg : sysTags)
			{
				tg.belongToNode = this;
				// tg.belongToNode = this ;
			}
		}
		super.constructNodeTree();
	}

	public List<UANodeOCTags> listSelfAndSubTagsNode()
	{
		ArrayList<UANodeOCTags> rets = new ArrayList<>();
		listSelfAndSubTagsNode(rets);
		return rets;
	}

	private void listSelfAndSubTagsNode(List<UANodeOCTags> tags)
	{
		tags.add(this);
		List<UANode> ns = this.getSubNodes();
		if (ns == null)
			return;
		for (UANode n : ns)
		{
			if (n instanceof UANodeOCTags)
			{
				((UANodeOCTags) n).listSelfAndSubTagsNode(tags);
			}
		}
	}
	
	/**
	 * get related DevDriverable
	 * @return
	 */
	public IDevDriverable getDevDriverable()
	{
		UANode curn = this ;
		while(curn!=null)
		{
			if(curn instanceof IDevDriverable)
			{
				return (IDevDriverable)curn ;
			}
			
			curn = curn.getParentNode();
		}
		return null;
	}
	
	public UATag addOrUpdateTagInMem(String tagid, boolean bmid, String name, String title, String desc, String addr,
			UAVal.ValTP vt, int dec_digits, boolean canw, long srate, String trans) throws Exception
	{
		UAUtil.assertUAName(name);
		UATag d = null;
		if (Convert.isNotNullEmpty(tagid))
		{
			d = this.getTagById(tagid);
			if (d == null)
				throw new Exception("no tag with id=" + tagid + " found!");
		}
		
		//this.get

		UANode tmpn = getSubNodeByName(name);
		if (d == null && tmpn != null)
			throw new IllegalArgumentException("tag with name=" + name + " existed");
		if (d != null && tmpn != null && d != tmpn)
			throw new IllegalArgumentException("tag with name=" + name + " existed");

		if(Convert.isNotNullEmpty(addr) &&!bmid)
		{
			IDevDriverable ddable = getDevDriverable() ;
			if(ddable!=null)
			{
				DevDriver dd = ddable.getRelatedDrv() ;
				if(dd!=null)
				{
					DevAddr.ChkRes chkres = dd.checkAddr(addr, vt) ;
					if(chkres!=null)
					{
						if(chkres.getChkVal()<=0)
						{
							throw new IllegalArgumentException(chkres.getChkPrompt()) ;
						}
						
					}
				}
			}
		}
		
		if (d == null)
		{
			if (bmid)
				d = new UATag(name, title, desc, addr, vt, dec_digits);
			else
				d = new UATag(name, title, desc, addr, vt, dec_digits, canw, srate);
			d.id = this.getNextIdByRoot();
			tags.add(d);
			constructNodeTree();
		}
		else
		{
			if (bmid)
				d.setTagMid(name, title, desc, addr, vt, dec_digits);
			else
				d.setTagNor(name, title, desc, addr, vt, dec_digits, canw, srate);
		}
		d.setValTranser(trans);
		//save();
		return d;
	}

	public UATag addOrUpdateTag(String tagid, boolean bmid, String name, String title, String desc, String addr,
			UAVal.ValTP vt, int dec_digits, boolean canw, long srate, String trans) throws Exception
	{
		UATag tag = addOrUpdateTagInMem(tagid, bmid, name, title, desc, addr,
				vt, dec_digits, canw, srate, trans) ;
		save();
		return tag;
	}

	/**
	 * order by name
	 * 
	 * @param t
	 * @return
	 */
	public UATag getPrevTag(UATag t)
	{
		this.tags.sort(new Comparator<UATag>() {

			@Override
			public int compare(UATag o1, UATag o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		int i = this.tags.indexOf(t);
		if (i <= 0)
			return null;
		return this.tags.get(i - 1);
	}

	public UATag getNextTag(UATag t)
	{
		this.tags.sort(new Comparator<UATag>() {

			@Override
			public int compare(UATag o1, UATag o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		int i = this.tags.indexOf(t);
		if (i >= this.tags.size() - 1)
			return null;
		return this.tags.get(i + 1);
	}

	private static class NameNum
	{
		String name ;
		
		int num =0 ;
		
		public NameNum(String n,int num)
		{
			this.name = n ;
			this.num = num ;
		}
		
		public boolean isEmpty()
		{
			return Convert.isNullOrEmpty(this.name);
		}
		
		public String toString(String defv)
		{
			if(Convert.isNullOrEmpty(name)&&num<=0)
				return defv ;
			return name+num ;
		}
	}
	private NameNum getNameEndNum(String name)
	{
		if (Convert.isNullOrEmpty(name))
			return new NameNum("",0);
		
		int k = name.length();
		for (; k > 0; k--)
		{
			char c = name.charAt(k-1) ;
			if(c>='0'&&c<='9')
				continue ;
			break ;
		}
		if(k>=name.length())
			return new NameNum(name,0);
		else
			return new NameNum(name.substring(0,k), Integer.parseInt(name.substring(k)));
	}

	public UATag addTagByCopy(UATag cp_tag) throws Exception
	{
		String name = cp_tag.getName();
		NameNum n_name = getNameEndNum(name);
		NameNum n_title = getNameEndNum(cp_tag.getTitle());
		String old_addr = cp_tag.getAddress();
		NameNum n_addr = getNameEndNum(cp_tag.getAddress());
		int step = 1 ;
		if(n_addr.num>0)
		{
			UATag ptag = this.getPrevTag(cp_tag) ;
			if(ptag!=null)
			{
				NameNum n_paddr = getNameEndNum(ptag.getAddress());
				if(n_paddr!=null&&n_paddr.num>0)
				{
					step = n_addr.num - n_paddr.num  ;
					if(step<1||step>4)
						step = 1 ;
				}
			}
		}
		UATag oldtag = null;
		do
		{
			oldtag = this.getTagByName(name);
			if (oldtag != null)
			{
				n_name.num ++ ;
				name = n_name.toString("");
			}
		} while (oldtag != null);

		n_title.num++ ;
		//n_addr.num += step ;
		// may has bug
		//UATag newtag = new UATag(cp_tag, name,n_title.toString(name),n_addr.toString(""));
		
		UATag newtag = new UATag(cp_tag, name,n_title.toString(name),old_addr);
		newtag.id = this.getNextIdByRoot();
		tags.add(newtag);
		constructNodeTree();

		save();
		return newtag;
	}

	public UATag renameRefedTag(String tagid, String name, String title, String desc) throws Exception
	{
		if (!this.isRefedNode())
			throw new Exception("Node is not refered");

		UATag d = this.getTagById(tagid);
		if (d == null)
			throw new Exception("no tag with id=" + tagid + " found!");

		UANode tmpn = getSubNodeByName(name);
		if (tmpn != null && d != tmpn)
			throw new IllegalArgumentException("tag with name=" + name + " existed");
		d.setReNameTitle(name, title, desc);
		save();
		return d;

	}

	UATag addOrUpdateTagSys(String tagid, boolean bmid, String name, String title, String desc, String addr,
			UAVal.ValTP vt, int dec_digits, boolean canw, long srate, boolean bsave) throws Exception
	{
		UAUtil.assertUAName(name);
		UATag d = null;
		if (Convert.isNotNullEmpty(tagid))
		{
			d = this.getTagById(tagid);
			if (d == null)
				throw new Exception("no tag with id=" + tagid + " found!");
		}

		UANode tmpn = getSubNodeByName(name);

		if (d == null && tmpn != null)
			return null;
		// throw new IllegalArgumentException("tag with name="+name+" existed")
		// ;
		if (d != null && tmpn != null && d != tmpn)
			return null;
		// throw new IllegalArgumentException("tag with name="+name+" existed")
		// ;

		if (d == null)
		{
			if (bmid)
				d = new UATag(name, title, desc, addr, vt, dec_digits);
			else
			{
				d = new UATag();
				d.setTagSys(name, title, desc, addr, vt, dec_digits, canw, srate);
			}
			d.id = this.getNextIdByRoot();
			if (d.isSysTag())
				sysTags.add(d);
			else
				tags.add(d);
			constructNodeTree();
		}
		else
		{
			if (bmid)
				d.setTagMid(name, title, desc, addr, vt, dec_digits);
			else
				d.setTagSys(name, title, desc, addr, vt, dec_digits, canw, srate);
		}
		if (bsave)
			save();
		return d;
	}

	public UATag addTag(DevItem item) throws Exception
	{
		String name = item.getName();
		UAUtil.assertUAName(name);
		UANode tmpn = getSubNodeByName(name);
		// UATagG tgg = this.getSubTagGByName(name) ;
		// UATag d = getTagByName(name);
		// if(tgg!=null||d!=null)
		if (tmpn != null)
		{
			throw new IllegalArgumentException("tag with name=" + name + " existed");
		}
		UATag d = new UATag(item);
		d.id = this.getNextIdByRoot();
		tags.add(d);
		constructNodeTree();
		save();
		return d;
	}
	
	public UATag addTag(String name,String title,String desc,UAVal.ValTP vt,boolean bsave) throws Exception
	{
		UAUtil.assertUAName(name);
		UANode tmpn = getSubNodeByName(name);
		// UATagG tgg = this.getSubTagGByName(name) ;
		// UATag d = getTagByName(name);
		// if(tgg!=null||d!=null)
		if (tmpn != null)
		{
			throw new IllegalArgumentException("tag with name=" + name + " existed");
		}
		
		UATag d = new UATag(name,title,desc,null,vt,0,false,200);
		d.id = this.getNextIdByRoot();
		tags.add(d);
		constructNodeTree();
		if(bsave)
			save();
		return d;
	}
	
	

	public boolean delTag(UATag t) throws Exception
	{
		if (this.tags.remove(t))
		{
			this.save();
			return true;
		}
		return false;
	}

	/**
	 * list all tags in sub tree node and decedent node
	 * 
	 * @return
	 */
	public List<UATag> listTags()
	{
		if (this.sysTags == null)
		{
			return tags;// tagList.listTags();
		}
		ArrayList<UATag> rets = new ArrayList<>(tags.size() + sysTags.size());
		rets.addAll(this.tags);
		rets.addAll(this.sysTags);
		return rets;
	}

	public List<UATag> getNorTags()
	{
		return tags;
	}
	
//	public List<UATag> getLocalTags()
//	{
//		return localTags ;
//	}

	/**
	 * list local mid tags
	 * 
	 * @return
	 */
	public List<UATag> listTagsMid()
	{
		ArrayList<UATag> rets = new ArrayList<>();
		for (UATag tg : listTags())
		{
			if (tg.isMidExpress())
				rets.add(tg);
		}
		return rets;
	}

	public final List<UATag> listTagsAll()
	{
		ArrayList<UATag> rets = new ArrayList<>();
		listTagsAll(rets, true, false,false);
		return rets;
	}

	public final List<UATag> listTagsNorAll()
	{
		ArrayList<UATag> rets = new ArrayList<>();
		listTagsAll(rets, false, false,false);
		return rets;
	}
	
	public final List<UATag> listTagsLocalAll()
	{
		ArrayList<UATag> rets = new ArrayList<>();
		listTagsAll(rets, false, false,true);
		return rets;
	}

	public final List<UATag> listTagsMidAll()
	{
		ArrayList<UATag> rets = new ArrayList<>();
		listTagsAll(rets, true, true,false);
		return rets;
	}

	private final void listTagsAll(List<UATag> tgs, boolean include_sys, boolean bmid_only,boolean blocal_only)
	{
		List<UATag> tags = null;

		if(blocal_only)
		{
			for (UATag tg : listTags())
			{
				if (tg.isLocalTag())
					tgs.add(tg);
			}
		}
		else if (bmid_only)
		{
			for (UATag tg : listTags())
			{
				if (tg.isMidExpress())
					tgs.add(tg);
			}
		}
		else
		{
			if (include_sys)
				tags = listTags();
			else
				tags = getNorTags();

			tgs.addAll(tags);
		}

		List<UANode> ns = this.getSubNodes();
		if (ns == null)
			return;
		for (UANode n : ns)
		{
			if (n instanceof UANodeOCTags)
			{
				((UANodeOCTags) n).listTagsAll(tgs, include_sys, bmid_only,blocal_only);
			}
		}
	}

	// {
	// ArrayList<UATag> rets = new ArrayList<>() ;
	// this.getTagList();
	// return rets ;
	// }

	public UATag getTagByName(String n)
	{
		List<UATag> sss = null;
		if (n.startsWith("_"))
		{
			if (this.sysTags == null)
				return null;
			sss = sysTags;
		}
		else
			sss = tags;

		for (UATag t : sss)
		{
			if (n.contentEquals(t.getName()))
				return t;
		}
		return null;
	}

	public UATag getTagById(String id)
	{
		for (UATag t : tags)
		{
			if (id.contentEquals(t.getId()))
				return t;
		}
		if (sysTags == null)
			return null;

		for (UATag t : sysTags)
			if (id.contentEquals(t.getId()))
				return t;
		return null;
	}

//	public UATag getTagByCxtPath(String cxtpath)
//	{
//		// this.fin
//		List<String> pns = Convert.splitStrWith(cxtpath, "/.");
//		return null;
//	}
	// /**
	// * xxx.xxx.xxx
	// * @param path
	// * @return
	// */
	// public UATag getTagByPath(String path)
	// {
	// LinkedList<String> ps = Convert.splitStrWithLinkedList(path, ".") ;
	// if(ps.size()==1)
	// return getTagByName(path) ;
	// String last_tagn = ps.removeLast() ;
	// UANode n = this.getDescendantNodeByPath(ps) ;
	// if(n==null)
	// return null ;
	// if(!(n instanceof UANodeOCTags))
	// return null ;
	// return ((UANodeOCTags)n).getTagByName(last_tagn) ;
	// }

	public List<DevAddr> listTagsAddrAll()
	{
		List<UATag> tags = listTagsAll();
		if (tags == null || tags.size() <= 0)
		{
			return null;
		}
		
		ArrayList<DevAddr> addrs = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (UATag tag : tags)
		{
			if (tag.isMidExpress()||tag.isLocalTag())
				continue;
			DevAddr da = tag.getDevAddr(sb);
			if (da == null)
				continue;
			addrs.add(da);
		}
		
		return addrs;
	}

	protected void onNodeChanged()
	{
		super.onNodeChanged();

		this.RT_init(true, false);
	}

	public List<UATag> getSysTags()
	{
		return sysTags;
	}

	public UATag getSysTagByName(String n)
	{
		List<UATag> ts = getSysTags();
		if (ts == null)
			return null;
		for (UATag t : ts)
		{
			if (n.equals(t.getName()))
				return t;
		}
		return null;
	}

	protected final synchronized void setSysTag(String name, String title, String desc, UAVal.ValTP vt)
	{
		setSysTag(name, title, desc, vt, -1);
	}

	protected final synchronized void setSysTag(String name, String title, String desc, UAVal.ValTP vt, int dec_digits)
	{
		if (!name.startsWith("_"))
			throw new IllegalArgumentException("system tag name must start with _");
		if (sysTags == null)
			sysTags = new ArrayList<>();
		UATag t = this.getSysTagByName(name);
		if (t == null)
		{
			t = new UATag();
			t.setTagSys(name, title, desc, "", vt, dec_digits, false, 200);
			sysTags.add(t);
		}
		else
			t.setTagSys(name, title, desc, "", vt, dec_digits, false, 200);
		
		t.belongToNode = this ;
	}

	/**
	 * node will be init before start RT
	 */
	void RT_init(boolean breset, boolean b_sub)
	{
		// if(breset)
		// sysTags= null ;

		if (b_sub)
		{
			List<UANode> subn = this.getSubNodes();
			if (subn == null)
				return;
			for (UANode sub : subn)
			{
				if (sub instanceof UANodeOCTags)
				{
					((UANodeOCTags) sub).RT_init(breset, b_sub);
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
		for (UANode subn : this.getSubNodes())
		{
			if (subn instanceof UANodeOCTags)
			{
				((UANodeOCTags) subn).RT_runFlush();
			}
		}
	}

	boolean RT_setSysTagVal(String name, Object v)
	{
		return RT_setSysTagVal(name, v, true);
	}

	final boolean RT_setSysTagVal(String name, Object v, boolean ignore_nochg)
	{
		UATag t = this.getSysTagByName(name);
		if (t == null)
			return false;
		t.RT_setVal(v);
		return true;
	}

	public List<IOCBox> OC_getSubs()
	{
		List<IOCBox> rets = super.OC_getSubs();
		// ArrayList<IOCBox> rets = new ArrayList<>() ;
		rets.addAll(tags);
		return rets;
	}

	public Object JS_get(String key)
	{
		Object r = super.JS_get(key);
		if (r != null)
			return r;
		return this.getTagByName(key);
	}

	public List<Object> JS_names()
	{
		List<Object> rets = super.JS_names();

		List<UATag> tags = listTags();
		for (UATag tag : tags)
		{
			rets.add(tag.getName());
		}
		return rets;
	}
	
}
