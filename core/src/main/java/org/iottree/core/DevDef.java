package org.iottree.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropNode;
import org.iottree.core.util.CompressUUID;
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
public class DevDef extends UANodeOCTagsGCxt implements IRoot,ISaver,IRefBranch
{
	transient DevCat belongToCat = null;
	
	//for mem syn purpose
	transient long memUpDT = System.currentTimeMillis() ;
	
	@data_val(param_name = "max_id")
	int maxIdVal = 0 ;
	
	public DevDef(DevCat dc)
	{
		belongToCat = dc ;
	}
	
	public DevDef(DevCat dc,String name,String title,String desc)
	{
		super(name,title,desc);
		belongToCat = dc ;
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
	
	public DevDriver getBelongToDrv()
	{
		return belongToCat.getDriver();
	}

	public String getNodePath()
	{
		return "/"+getBelongToDrv().getName()+"-"+this.belongToCat.getName()+"-"+this.getName() ;
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
		return new File(this.getBelongToCat().getDevCatDir(),"dd_"+getId()+".xml");
	}
	public void save() throws Exception
	{
		this.getBelongToCat().saveDevDef(this);
		memUpDT=  System.currentTimeMillis() ;
	}
	
	public File getSaverDir()
	{
		return new File(this.getBelongToCat().getDevCatDir(),"dd_"+getId()+"/");
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
		String id = dev.getId() ;
		super.copyTreeWithNewSelf(dev,id,true); //recreate tree
		dev.id = id ;
		dev.setNameTitle(name, title, desc);
		dev.setDevRefId(this.getId());
		return dev;
	}
	
	/**
	 * update dev sub tree node only
	 * @param dev
	 * @return
	 */
	UADev updateUADev(UADev dev)
	{
		String n = dev.getName() ;
		String t = dev.getTitle() ;
		String d = dev.getDesc() ;
		return updateUADev(dev,n,t,d) ;
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
		return this.getBelongToDrv().getPropGroupsForDevDef();
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

}