package org.iottree.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropNode;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.res.ResDir;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

/**
 * device defination belong to driver
 * 1,devdef can be edited at local
 * 2,devdef can be loaded by some driver.
 * 	   e.g remote driver can provider devdef which represent remote iottree-node)
 * 
 * @author jason.zhu
 *
 */
@data_class
public class DevDef extends UANodeOCTagsGCxt implements IRoot,ISaver,IRefBranch ,IResNode
{
	transient DevCat belongToCat = null;
	
	//for mem syn purpose
	transient long memUpDT = System.currentTimeMillis() ;
	
	@data_val(param_name = "max_id")
	int maxIdVal = 0 ;
	
	@data_val(param_name = "drv")
	String relatedDrv = null;
	
	private transient File devDefDir = null ;
	
	private DevDef(File defdir,DevCat dc)
	{
		this.devDefDir = defdir ;
		belongToCat = dc ;
	}
	
	public DevDef(DevCat dc,String name,String title,String desc)
	{
		super(name,title,desc);
		
		if(dc==null)
			throw new IllegalArgumentException("add new DevDef must in cat") ;
		belongToCat = dc ;
		this.devDefDir =  dc.calDevDefDir(this.getId());
	}
	
	public static DevDef loadFromDir(File ddd,DevCat dc) throws Exception
	{
		File ddf = new File(ddd,"_dd.xml");
		if(!ddf.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(ddf);
		DevDef r = new DevDef(ddd,dc) ;
		DataTranserXml.injectXmDataToObj(r, tmpxd);
		return r;
	}
	
	public String getNodeTp()
	{
		return "";
	}
	
	public String getRootIdPrefix()
	{
		return "d" ;
	}
	
	public int getRootNextIdVal()
	{
		maxIdVal ++ ;
		return maxIdVal;
	}
	
	public DevCat getBelongToCat()
	{
		return this.belongToCat ;
	}
	
	public DevDriver getRelatedDrv()
	{
		if(Convert.isNullOrEmpty(relatedDrv))
				return null ;
		//return belongToCat.getDriver();
		return DevManager.getInstance().getDriver(relatedDrv) ;
	}
	
	public DevDef asDriver(String drv)
	{
		this.relatedDrv = drv ;
		return this ;
	}

	public String getNodePath()
	{
		if(this.belongToCat==null)
			return null ;
		DevLib devlib = this.belongToCat.getDevLib() ;
		return "/"+devlib.getId()+"-"+this.belongToCat.getName()+"-"+this.getName() ;
	}
	
	
	public void setDefNameTitle(String name,String title,String desc,boolean bsave) throws Exception
	{
		this.setNameTitle(name, title, desc);
		if(bsave)
			this.save();
	}
//	
//	/**
//	 * 
//	 * @param new_self create by copySelfWithNewId
//	 */
//	@Override
//	protected void copySubNodesWithNewSelf(UANode new_self)
//	{
//		
//	}
//	
//	@Override
//	protected void copyMembersWithNewSelf(UANode new_self)
//	{
//		
//	}
//	

	File getDevDefFile()
	{
		return new File(getDevDefDir(),"_dd.xml");
	}
	public void save() throws Exception
	{
		this.getBelongToCat().saveDevDef(this);
		memUpDT=  System.currentTimeMillis() ;
	}
	
	public File getDevDefDir()
	{
		return devDefDir ;
	}
	
	public File getSaverDir()
	{
		return this.getDevDefDir() ;
	}
	/**
	 * create new UADev for UACh
	 * @param name
	 * @param tilte
	 * @return
	 */
	UADev createNewUADev(String id,String name,String title,String desc)
	{
		UADev ret = new UADev() ;
		ret.id = id ;
		return updateUADev(ret,name,title,desc);
	}
	
	
	UADev updateUADev(UADev dev,String name,String title,String desc)
	{
		List<UATag> oldtags = dev.listTagsNorAll();
		//keep rename info
		HashMap<String,UATag> path2tag_rename = new HashMap<>() ;
		for(UATag oldt:oldtags)
		{
			String rn = oldt.getReName();
			String rt = oldt.getReTitle() ;
			String rd = oldt.getReDesc() ;
			if(Convert.isNullOrEmpty(rn)&&Convert.isNullOrEmpty(rt)&&Convert.isNullOrEmpty(rd))
				continue ;
			
			UATag oldbt = (UATag)oldt.getRefBranchNode();
			//String pk = oldt.getNodeCxtPathIn(dev) ; // it may err with renameed name
			String pk = oldbt.getNodeCxtPathIn(this) ;
			path2tag_rename.put(pk, oldt) ;
		}
		
		String id = dev.getId() ;
		super.copyTreeWithNewSelf(null,dev,id,true,false,null); //recreate tree
		dev.id = id ;
		dev.setNameTitle(name, title, desc);
		dev.setDevRef(this.getResLibId(),this.getId());
		
		//recover renamed info
		for(Map.Entry<String, UATag> p2t:path2tag_rename.entrySet())
		{
			UANode tmpn = dev.getDescendantNodeByPath(p2t.getKey()) ;
			if(tmpn==null)
				continue ;
			if(!(tmpn instanceof UATag))
				continue ;
			UATag tart = (UATag)tmpn ;
			UATag oldt = p2t.getValue() ;
			tart.setReNameTitle(oldt.getReName(), oldt.getReTitle(), oldt.getReDesc()) ;
		}
		return dev;
	}
	
	/**
	 * all node with new id will be copied by def
	 * 
	 * @param dev
	 * @param name
	 * @param title
	 * @param desc
	 * @return
	 */
	UADev deepCopyUADev(IRoot root,UADev dev,String name,String title,String desc,
			HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		
		//List<UATag> oldtags = dev.listTagsNorAll();
		
		String id = dev.getId() ;
		
		super.copyTreeWithNewSelf(root,dev,"",false,true,rf2new); //recreate tree
		dev.id = id ;
		dev.setNameTitle(name, title, desc);
		dev.setDevRef(this.getResLibId(),this.getId());
		
		return dev;
	}
	
	/**
	 * update dev sub tree node only
	 * @param dev
	 * @return
	 */
	UADev updateUADev(UAPrj prj,UADev dev,HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		String n = dev.getName() ;
		String t = dev.getTitle() ;
		String d = dev.getDesc() ;
		//return updateUADev(dev,n,t,d) ;
		return deepCopyUADev(prj,dev, n, t, d,rf2new) ;
	}
//	
//	public UATagG addTagG(String name,String title,String desc)
//			 throws Exception
//	{
//		UAUtil.assertUAName(name);
//		
////		UATagG d = this.getSubTagGByName(name) ;
////		UATag tg = getTagByName(name);
//		UANode n = this.getSubNodeByName(name) ;
//		if(n!=null)
//		{
//			throw new IllegalArgumentException("tag group with name="+name+" existed") ;
//		}
//		
//		UATagG d = new UATagG(name,title,desc) ;
//		taggs.add(d);
//		constructNodeTree();
//		this.save();
//		return d ;
//	}
//	
//
//	public boolean delSubTagG(UATagG tg) throws Exception
//	{
//		if(taggs.remove(tg))
//		{
//			this.save();
//			return true;
//		}
//		return false;
//	}

	
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
	
	@Override
	public List<PropGroup> listPropGroups()
	{
		DevDriver dd = this.getRelatedDrv() ;
		if(dd==null)
			return null ;
		return dd.getPropGroupsForDevDef();
	}

	@Override
	protected void onPropNodeValueChged()
	{
		
	}
	
	public boolean setPropValue(String groupn,String itemn,String strv)
	{
		if("ch".contentEquals(groupn))
		{
			
		}
		return super.setPropValue(groupn, itemn,strv);
	}

	@Override
	public JSONObject OC_getPropsJSON()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void OC_setPropsJSON(JSONObject jo)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean OC_supportSub()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<IOCBox> OC_getSubs()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean chkValid()
	{
		return false;
	}

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
	
	@Override
	void RT_init(boolean breset, boolean b_sub)
	{
		super.RT_init(breset, b_sub);
		this.setSysTag("_name", "device name", "", ValTP.vt_str);
		this.setSysTag("_title", "device title", "", ValTP.vt_str);
		
	}

	@Override
	public String getResNodeId()
	{
		return this.getId();
	}

	@Override
	public String getResNodeTitle()
	{
		return this.getTitle();
	}

	@Override
	public File getResNodeDir()
	{
		return this.getDevDefDir();
	}

	@Override
	public String getResLibId()
	{
		return this.getBelongToCat().getDevLib().getResLibId();
	}

//	@Override
//	public IResCxt getResCxt()
//	{
//		DevCat dc = this.getBelongToCat();
//		if(dc==null)
//			return null ;
//		return dc.getDevLib();
//	}

//	private ResDir resDir = null ;
//	
//	@Override
//	public ResDir getResDir()
//	{
//		if(resDir!=null)
//			return resDir ;
//		
//		File dir = new File(getDevDefDir(),"_res/") ;
//		if(!dir.exists())
//			dir.mkdirs();
//		resDir=new ResDir(this,this.getId(),this.getTitle(),dir);
//		return resDir;
//	}
//
//	@Override
//	public IResNode getResNodeSub(String subid)
//	{
//		return null;
//	}
//
//	@Override
//	public String getResNodeId()
//	{
//		return getId();
//	}
//	
//	@Override
//	public String getResNodeTitle()
//	{
//		return this.getTitle() ;
//	}
//
//	@Override
//	public IResNode getResNodeParent()
//	{
//		return this.getBelongToCat();
//	}

}
