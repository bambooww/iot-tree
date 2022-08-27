package org.iottree.core;

import java.io.File;
import java.util.*;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

/**
 * UADev is key node under ch
 * this node reference driver devices (defined or remoted)
 *   then,it can has TagGroup,Tags and other nodes. e.g hmi etc.
 *   
 * @author jason.zhu
 */
@data_class
public class UADev extends UANodeOCTagsGCxt  implements IOCUnit,IOCDyn,IRefOwner,IResNode,IDevDriverable,IJoinedNode // extends UANodeOC
{
	public static final String NODE_TP = "dev" ;
	/**
	 * null or empty means device is not limited by any model
	 * so,tags used by this device must be carefully used and
	 * must be set manually.
	 */
	//@data_val(param_name="model")
	//String modelUid = "" ;
	
	//private transient DevModel model = null ;
	
	@data_val(param_name = "ref_lib_id")
	String devRefLibId = null;
	
	@data_val(param_name = "ref_id")
	String devRefId = null ;

	@data_val(param_name = "dev_model")
	String devModel = null ;
	
	/**
	 * is device set or not
	 * dev set may use this device struct,and has multi device instance.
	 * every instance is stored in a list
	 */
	@data_val(param_name = "is_set")
	boolean bSet = false;
	
	
	public UADev()
	{}
	
	public UADev(String name,String title,String desc)
	{
		super(name,title,desc) ;
	}
	
//	@Override
//	protected void copyTreeWithNewSelf(UANode new_self)
//	{
//		super.copyTreeWithNewSelf(new_self);
//		UADev self = (UADev)new_self ;
//	}
	
	public UACh getBelongTo()
	{
		return (UACh)this.getParentNode() ;
	}
	
	public String getNodeTp()
	{
		return NODE_TP;
	}
	
	public String getDevModel()
	{
		if(devModel==null)
			return "" ;
		return devModel ;
	}
	
	public DevDriver.Model getDrvDevModel()
	{
		if(Convert.isNullOrEmpty(devModel))
			return null ;
		DevDriver drv = this.getBelongTo().getDriver() ;
		if(drv==null)
			return null ;
		return drv.getDevModel(this.devModel) ;
	}
	
	public void setDevModel(String m)
	{
		this.devModel = m ;
	}
	
	public ConnPt getConnPt() throws Exception
	{
		UACh ch = this.getBelongTo() ;
		UAPrj prj = ch.getBelongTo() ;
		return ConnManager.getInstance().getConnPtByNode(prj.getId(), ch.getId(),this.getId()) ;
	}
	
	public boolean isDevSet()
	{
		return this.bSet ;
	}
	@Override
	protected void copyTreeWithNewSelf(IRoot root,UANode new_self,String ownerid,
			boolean copy_id,boolean root_subnode_id,HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		super.copyTreeWithNewSelf(root,new_self,ownerid, copy_id,root_subnode_id,rf2new) ;
		if(new_self instanceof UADev)
		{
			UADev self = (UADev)new_self ;
			self.devRefId = this.devRefId;
		}
	}
	
	UADev deepCopyMe() throws Exception
	{
		XmlData xd = DataTranserXml.extractXmlDataFromObj(this) ;
		UADev newd = new UADev() ;
		DataTranserXml.injectXmDataToObj(newd, xd);
		return newd;
	}
	
	public UAPrj getPrj()
	{
		UACh ch = getBelongTo();
		if(ch==null)
			return null ;
		return ch.getBelongTo(); 
	}
	

	public boolean delFromParent() throws Exception
	{
		UACh ch = this.getBelongTo();
		if(ch==null)
			return false;
		ch.delDev(this) ;
		return true;
	}
	

	public String getDevRefId()
	{
		return this.devRefId ;
	}
	
	public boolean hasDevRefId()
	{
		return Convert.isNotNullEmpty(this.devRefId) ;
	}
	
	void setDevRef(String def_lib_id,String defid)
	{
		this.devRefLibId = def_lib_id ;
		this.devRefId = defid ;
	}
	

	protected int getRefLockedLoc()
	{
		//if(hasDevRefId())
		//	return REF_LOCKED ;
		return REF_LOCKED_NOT;//super.getRefLockedLoc() ;
	}
	
	
	long defMemUpDT = -1 ;
	
	public DevDef getDevDef()
	{
		if(Convert.isNullOrEmpty(this.devRefLibId) || Convert.isNullOrEmpty(this.devRefId))
			return null ;
//		DevDriver drv = this.getBelongTo().getDriver() ;
//		if(drv==null)
//			return null ;
		DevDef dd = DevManager.getInstance().getDevDefById(devRefLibId, devRefId);// drv.getDevDefById(this.devRefId) ;
		if(dd==null)
			return null ;
		if(dd.memUpDT!=defMemUpDT)
		{
//			refreshByDevDef();
//			defMemUpDT = dd.memUpDT;
		}
		return dd ;
	}
	
	boolean updateByDevDef(HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		DevDef dd = getDevDef() ;
		if(dd==null)
			return false;
		
		//newdev.setNameTitle(newname, newtitle, "");
		
		//dd.updateUADev(this,this.getName(), this.getTitle(), this.getDesc()) ;
		dd.deepCopyUADev(this.getPrj(),this,this.getName(), this.getTitle(), this.getDesc(),rf2new) ;
		return true;
	}
	/**
	 * get branch to be refered
	 * @return
	 */
	@Override
	public IRefBranch getRefBranch()
	{
		return getDevDef() ;
	}
//	
//	public boolean refreshByDevDef()
//	{
//		DevDef dd = getDevDef() ;
//		if(dd==null)
//			return false;
//		
//		dd.updateUADev(this) ;
//		//dd.copyTreeWithNewSelf(this, true);
//		return true ;
//	}

//	@Override
//	public List<UANode> getSubNodes()
//	{
////		DevDef dd = getDevDef() ;
////		if(dd==null)
////			return null;
////		return dd.getSubNodes();
//		return null ;
//	}
	
	public DevDriver getRelatedDrv()
	{
		UACh ch = this.getBelongTo() ;
		if(ch==null)
			return null ;
		return ch.getDriver() ;
	}
	
	public boolean chkValid()
	{
		return true;
	}
	

	private List<PropGroup> devPGS = null ;
	
	void onChDriverChged()
	{
		devPGS = null ;
//		modelUid = "" ;
//		model=null ;
	}
	
	@Override
	protected void onPropNodeValueChged()
	{
		devPGS = null ;
	}
	
	@Override
	public List<PropGroup> listPropGroups()
	{
		if(devPGS!=null)
			return devPGS;
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		List<PropGroup> lpgs = super.listPropGroups() ;
		if(lpgs!=null)
			pgs.addAll(lpgs) ;
		//pgs.add(this.getDevPropGroup()) ;
		//
		DevDriver uad = this.getBelongTo().getDriver() ;
		if(uad!=null)
		{//add driver prop used in this channel
			if(uad.isConnPtToDev())
			{
				PropGroup pg = new PropGroup("dev","Device run parameters") ;
				pg.addPropItem(new PropItem("dev_intv","Read Interval MS","",PValTP.vt_int,false,
						null,null,100));
				pgs.add(pg) ;
			}
			
			List<PropGroup> drvpgs = uad.getPropGroupsForDevInCh(this);//.getPropGroupsForDev() ;
			if(drvpgs!=null)
				pgs.addAll(drvpgs);
			
			drvpgs = uad.getPropGroupsForDevDef();
			if(drvpgs!=null)
				pgs.addAll(drvpgs);
		}
		devPGS = pgs;
		return pgs;
	}
	
//	private PropGroup getDevPropGroup()
//	{
//		PropGroup r = new PropGroup("dev","Device");
//		r.addPropItem(new PropItem("drv","Driver","Device Driver used by Channel",PValTP.vt_str,true,null,null,""));
//		DevDriver dd = this.getBelongTo().getDriver() ;
////		List<DevModel> mds = null; 
////		if(dd!=null)
////			mds = DevDrvManager.getInstance().listDevModelsByDriver(dd);
////		r.addPropItem(new PropItem("model","Model","Device Model",PValTP.vt_str,false,mds,DevModel.class,"getTitle","getUniqueId",""));
//		r.addPropItem(new PropItem("devid","Dev Id","Device ID",PValTP.vt_str,false,null,null,""));
//		return r ;
//	}
	
	public Object getPropValue(String groupn,String itemn)
	{
		if("dev".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "drv":
				return this.getBelongTo().getDriverName();
//			case "model":
//				return this.modelUid ;
			}
		}
		Object locv = super.getPropValue(groupn, itemn);
		if(locv==null) //override from devdef
		{
			DevDef ddef = this.getDevDef() ;
			if(ddef!=null)
				locv = ddef.getPropValue(groupn, itemn) ;
		}
		
		return locv;
	}
	
	
	public boolean setPropValue(String groupn,String itemn,String strv)
	{
		if("dev".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "drv":
				return true;//do nothing
//			case "model":
//				this.modelUid = strv ;
			}
		}
		return super.setPropValue(groupn, itemn,strv);
	}
	
	public String OCUnit_getUnitTemp()
	{
		return "dev";
	}
//	
//	public void OCUnit_setBaseVal(String name,String title)
//	{
//		
//	}
	@Override
	public boolean CXT_containsKey(String jsk)
	{
		return JS_get(jsk)!=null;
	}

	@Override
	public Object CXT_getByKey(String jsk)
	{
		return JS_get(jsk);
	}
	
	
	public boolean OC_supportSub()
	{
		return true;
	}
	


	@Override
	public JSONObject OC_getDynJSON(long lastdt)
	{
		//JSONObject r = new JSONObject() ;
		//r.put("brun", this.RT_isRunning()) ;
		return null;
	}
	
	
//	protected Object JS_get(String  key)
//	{
//		UATag tg = this.getTagByName(key) ;
//		if(tg!=null)
//			return tg ;
//		
//		UATagG tgg = this.getSubTagGByName(key) ;
//		if(tgg!=null)
//			return tgg;
//
//		return null ;
//	}
	
	@Override
	void RT_init(boolean breset, boolean b_sub)
	{
		super.RT_init(breset, b_sub);
		this.setSysTag("_name", "device name", "", ValTP.vt_str);
		this.setSysTag("_title", "device title", "", ValTP.vt_str);
		
//		this.setSysTag("_tick_ms", "Milliseconds from 1970-1-1", "", ValTP.vt_int64);
//		this.setSysTag("_date", "yyyy-MM-dd", "", ValTP.vt_str);
//		this.setSysTag("_date_year", "current year int16 value", "", ValTP.vt_int16);
//		this.setSysTag("_date_month", "current month int16 value", "", ValTP.vt_int64);
//		this.setSysTag("_date_day", "current day int16 value", "", ValTP.vt_int64);
		
		this.RT_setSysTagVal("_name", this.getName()) ;
		this.RT_setSysTagVal("_title", this.getTitle()) ;
	}

	/**
	 * driver run ok or not for this device.
	 * @return
	 */
	public boolean RT_runOk()
	{
		return true;
	}
	
	
	// dev set related
	
	public List<UADevSetItem> SET_listItems(String filtern,int pageidx,int pagesize)
	{
		return null ;
	}
	
	public UADevSetItem SET_getItemById(String itemid)
	{
		return null ;
	}
	
//	public UADevSetItem SET_getItemByName(String name)
//	{
//		return null ;
//	}
	
	public UADevSetItem SET_addItem(String id,String title)
	{
		return null ;
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
		File f = new File(this.getPrj().getResNodeDir(),"dev_"+this.getId()+"/") ;
		return f;
	}

	@Override
	public String getResLibId()
	{
		return null;
	}


}
