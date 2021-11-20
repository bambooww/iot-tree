package org.iottree.core;

import java.io.File;
import java.io.Writer;
import java.util.*;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.*;
import org.graalvm.polyglot.Value;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtMSG;
import org.iottree.core.conn.ConnPtVirtual;
import org.json.JSONObject;

@data_class
public class UACh extends UANodeOCTagsGCxt implements IOCUnit,IOCDyn
{
	@data_val(param_name = "drv")
	String drvName = null ;
	
	@data_val(param_name = "drv_int")
	long devIntMS = 1000 ;
//	/**
//	 * javascript run with interval
//	 */
//	@data_val(param_name = "script")
//	String script = null ;
//	
//	/**
//	 * js run interval with ms
//	 */
//	@data_val(param_name = "script_int")
//	long scriptInt = 10000 ;
//	
//	/**
//	 * last script date time
//	 */
//	transient long scriptRunDT = System.currentTimeMillis() ;
//	
//	/**
//	 * check js script ok or not
//	 */
//	private transient boolean jsSetOk = false;
//	
//	/**
//	 * script set error when setup js script
//	 */
//	private transient String jsSetError  = null ;
//	
//	/**
//	 * script run error 
//	 */
//	private transient String jsRunError  = null ;
	
	//UARep belongTo = null ;
	
	private transient DevDriver devDrv = null ;
	
	@data_obj(obj_c=UADev.class)
	List<UADev> devs = new ArrayList<>();
	
	public UACh()
	{}
	
	public UACh(String name,String title,String desc,String drvname)
	{
		super(name,title,desc);
		this.drvName = drvname ;
	}
	
	@Override
	protected void copyTreeWithNewSelf(UANode new_self,String ownerid,boolean copy_id,boolean root_subnode_id)
	{
		super.copyTreeWithNewSelf(new_self,ownerid,copy_id,root_subnode_id);
		UACh self = (UACh)new_self ;
		self.drvName = this.drvName ;
		self.devs.clear();
		for(UADev dev:devs)
		{
			UADev ndev = new UADev() ;
			if(root_subnode_id)
				ndev.id = this.getNextIdByRoot();
			dev.copyTreeWithNewSelf(ndev,ownerid,copy_id,root_subnode_id);
			self.devs.add(ndev) ;
		}
	}
	
//	void constructNodeTree()
//	{
//		super.constructNodeTree();
//	}
//	
//	void constructTree()
//	{
//		for(UADev dev:devs)
//		{
//			dev.belongTo = this ;
//			dev.constructTree();
//		}
//	}
	
	public List<UANode> getSubNodes()
	{
		List<UANode> rets = super.getSubNodes();
		rets.addAll(devs);
		
		return rets;
	}
	
	public UAPrj getBelongTo()
	{
		return (UAPrj)this.getParentNode() ;
	}
	

	protected int getRefLockedLoc()
	{
		return 1 ;
	}
	
	
	public boolean chkValid()
	{
		if(Convert.isNullOrEmpty(drvName))
			return false;
		return true;
	}
	
	public String getDriverName()
	{
		return drvName ;
	}
	
	public long getDriverIntMS()
	{
		return this.devIntMS;
	}
	
	public DevDriver getDriver()
	{
		if(Convert.isNullOrEmpty(this.drvName))
			return null ;
		
		if(devDrv!=null)
			return devDrv;
		if(Convert.isNullOrEmpty(this.drvName))
			return null ;
		devDrv = DevManager.getInstance().createDriverIns(this.drvName);
		if(devDrv==null)
			return null ;
		devDrv.belongToCh = this ;
		// init connpt if existed
		
		return devDrv;
	}
	
	public boolean setDriverName(String drvname) throws Exception
	{
		if(Convert.isNotNullEmpty(drvname)&&!chkDriverFit(drvname))
			return false;
		
		if(drvname.equals(this.drvName))
			return true ;
		
		this.drvName = drvname ;
		
		if(devDrv!=null)
		{
			devDrv.RT_stop(true);
			devDrv = null ;
		}
		
		getDriver();
		
		//save
		this.getBelongTo().save();
		return true;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<DevDriver> getSupportedDrivers() throws Exception
	{
		UAPrj rep = this.getBelongTo() ;
		
		//
		ConnProvider cp =getConnJoinedProvider();
		if(cp==null)
		{// no conn
			return DevManager.getInstance().listDriversNotNeedConn() ;
		}
		
		return cp.supportDrivers() ;	
	}
	
	
	public ConnProvider getConnJoinedProvider() throws Exception
	{
		UAPrj rep = this.getBelongTo() ;
		
		//ConnJoin cj = ConnManager.getInstance().getConnJoinByChId(rep.getId(), this.getId()) ;
		return ConnManager.getInstance().getConnJoinedProvider(rep.getId(), this.getId()) ;
	}
//	
//	public ConnJoin getConnJoin() throws Exception
//	{
//		UAPrj rep = this.getBelongTo() ;
//		return ConnManager.getInstance().getConnJoinByChId(rep.getId(), this.getId()) ;
//	}
	
	public ConnPt getConnPt() throws Exception
	{
		UAPrj rep = this.getBelongTo() ;
		return ConnManager.getInstance().getConnPtByCh(rep.getId(), this.getId()) ;
	}
	
	/**
	 * check channel is connected virtual or not
	 * @return
	 * @throws Exception
	 */
	public boolean isConnVirtual() throws Exception
	{
		ConnPt cpt = this.getConnPt() ;
		return cpt!=null&&cpt instanceof ConnPtVirtual ;
	}
	
	public boolean isConnMsg() throws Exception
	{
		ConnPt cpt = this.getConnPt() ;
		return cpt!=null&&cpt instanceof ConnPtMSG ;
	}
	/**
	 * check driver is fit for this ch or not
	 * @return
	 * @throws Exception
	 */
	public boolean chkDriverFit(String drvname) throws Exception
	{
		if(isConnVirtual())
			return true;
		if(Convert.isNullOrEmpty(drvname))
			return false;
		List<DevDriver> drvs = getSupportedDrivers();
		for(DevDriver drv:drvs)
		{
			if(drvname.equals(drv.getName()))
				return true ;
		}
		return false;
	}
	
	/**
	 * will used by tree error prompt
	 * @return
	 * @throws Exception
	 */
	public boolean isDriverFit() throws Exception
	{
		return chkDriverFit(drvName) ;
	}
	
	public boolean hasDriver()
	{
		return Convert.isNotNullEmpty(drvName);
	}
	
	public UACh deepCopyMe() throws Exception
	{
		XmlData xd = DataTranserXml.extractXmlDataFromObj(this) ;
		UACh newch = new UACh() ;
		DataTranserXml.injectXmDataToObj(newch, xd);
		return newch;
	}
	
	
	public UADev deepPasteDev(UADev dev) throws Exception
	{
		String newn = dev.getName();
		newn = this.calNextSubNameAuto(newn);
		return deepPasteDev(dev, newn,dev.getTitle());
	}
	
	public UADev deepPasteDev(UADev dev,String newname,String newtitle) throws Exception
	{
		UANode oldn = this.getSubNodeByName(newname);
		if(oldn!=null)
		{
			throw new Exception("device name ["+newname+"] already existed");
		}
		UADev newdev = dev.deepCopyMe();
		newdev.id=this.getNextIdByRoot();
//		newdev.name=newname;
//		newdev.title=newtitle;
		newdev.setNameTitle(newname, newtitle,null) ;
		this.devs.add(newdev);
		this.constructNodeTree();
		
		newdev.updateByDevDef();
		this.constructNodeTree();
		this.save();
		return newdev;
	}
	
	public List<UADev> getDevs()
	{
		return devs ;
	}
	
	public UADev getDevById(String id)
	{
		for(UADev d:devs)
		{
			if(id.contentEquals(d.getId()))
				return d ;
		}
		return null ;
	}
	
	public UADev getDevByName(String n)
	{
		for(UADev d:devs)
		{
			if(n.contentEquals(d.getName()))
				return d ;
		}
		return null ;
	}
	
	public UADev addDev(String name,String title,String desc,String devdef_id) throws Exception
	{
		UAUtil.assertUAName(name);
		
		UADev d = getDevByName(name);
		if(d!=null)
		{
			throw new IllegalArgumentException("dev with name="+name+" existed") ;
		}
		
		DevDriver drv = this.getDriver() ;
		DevDef dd = null;
		if(Convert.isNotNullEmpty(devdef_id))
		{
			dd = drv.getDevDefById(devdef_id) ;
			if(dd==null)
				throw new Exception("no device definition found") ;
			d = dd.createNewUADev(this.getNextIdByRoot() ,name, title, desc) ;
		}
		else
		{
			d = new UADev();
			d.id = this.getNextIdByRoot() ;
			d.setNameTitle(name, title, desc);
		}
		
		devs.add(d);
		constructNodeTree();
		this.getBelongTo().save();
		d.RT_init(true, false);
		return d ;
	}
	
	public UADev updateDev(UADev dev,String name,String title,String desc,String devdef_id) throws Exception
	{
		UAUtil.assertUAName(name);

		if(Convert.isNotNullEmpty(devdef_id) && !devdef_id.equals(dev.getDevRefId()))
		{
			DevDriver drv = this.getDriver() ;
			DevDef dd = drv.getDevDefById(devdef_id) ;
			if(dd==null)
				throw new Exception("no device definition found") ;
			
			UADev d = getDevByName(name);
			if(d!=null && d!=dev)
			{
				throw new IllegalArgumentException("dev with name="+name+" existed") ;
			}
			d = dd.updateUADev(dev,name, title, desc) ;
		}
		else
		{
			dev.setNameTitle(name, title, desc);
		}
		
		this.RT_init(false, true);
		constructNodeTree();
		this.getBelongTo().save();
		return dev ;
	}
	
	public UADev refreshDev(UADev dev) throws Exception
	{
		DevDef dd = dev.getDevDef() ;
		if(dd==null)
			throw new Exception("no device definition found") ;
		
		dd.updateUADev(dev) ;
		constructNodeTree();
		this.getBelongTo().save();
		return dev ;
	}

	public UADev delDevById(String id) throws Exception
	{
		UADev d = getDevById(id);
		if(d==null)
			return null ;
		delDev(d);
		return d ;
	}
	
	void delDev(UADev d) throws Exception
	{
		devs.remove(d) ;
		this.getBelongTo().save();
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
//		
//		 for(UADev d:devs)
//		{
//			d.listTagsAll(tgs,false,bmid);
//		}
//	}
	
	public boolean delFromParent() throws Exception
	{
		UAPrj r = this.getBelongTo();
		if(r==null)
			return false;
		r.delCh(this) ;
		return true;
	}
	
	
	private List<PropGroup> chPGS = null ;
	/**
	 * node prop groups
	 * 0) basic prop group
	 * 1��node��s self prop group
	 * 2) driver's group
	 */
	@Override
	public List<PropGroup> listPropGroups()
	{
		if(chPGS!=null)
			return chPGS;
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		List<PropGroup> lpgs = super.listPropGroups() ;
		if(lpgs!=null)
			pgs.addAll(lpgs) ;
		pgs.add(this.getChPropGroup()) ;
		//
		DevDriver uad = this.getDriver() ;
		if(uad!=null)
		{//add driver prop used in this channel
			List<PropGroup> drvpgs = uad.getPropGroupsForCh() ;
			if(drvpgs!=null)
				pgs.addAll(drvpgs);
		}
		chPGS = pgs;
		return pgs;
	}
	
	private PropGroup getChPropGroup()
	{
		PropGroup r = new PropGroup("ch","Channel");
		DevDriver dd = this.getDriver() ;
		if(dd==null)
		{
			List<DevDriver> dds = DevManager.getInstance().getDrivers() ;
			String[] ts = new String[dds.size()] ;
			Object[] vs = new Object[dds.size()] ;
			for(int i = 0 ; i < ts.length ; i ++)
			{
				ts[i] = dds.get(i).getTitle() ;
				vs[i] = dds.get(i).getName() ;
			}
			r.addPropItem(new PropItem("drv","Driver","Device Driver used by Channel",PValTP.vt_str,false,ts,vs,""));
		}
		else
			r.addPropItem(new PropItem("drv","Driver","Device Driver used by Channel",PValTP.vt_str,true,null,null,""));
		
		r.addPropItem(new PropItem("drv_intv","Driver scan interval","Driver scan interval on every loop",PValTP.vt_int,
				false,null,null,1000));
				
		//r.addPropItem(new PropItem("script","JavaScript","JavaScript run interval by Channel",PValTP.vt_str,false,null,null,"")
		//		.withTxtMultiLine(true));
		
		//r.addPropItem(new PropItem("script_int","JavaScript Interval","JavaScript run interval(ms)",PValTP.vt_int,false,null,null,"10000"));
		
		return r ;
	}
	
	public Object getPropValue(String groupn,String itemn)
	{
		if("ch".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "drv":
				return this.drvName;
			case "drv_intv":
				return devIntMS;
//			case "script":
//				return this.script ;
//			case "script_int":
//				return this.scriptInt ;
			}
		}
		return super.getPropValue(groupn, itemn);
	}
	
	public boolean setPropValue(String groupn,String itemn,String strv)
	{
		if("ch".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "drv":
				DevDriver dd = this.getDriver() ;
				if(dd==null)
				{
					this.drvName = strv ;
					devDrv = DevManager.getInstance().createDriverIns(this.drvName);
					for(UADev d:this.devs)
						d.onChDriverChged();
					chPGS=null;//
				}
				return true;//do nothing
			case "drv_intv":
				devIntMS = Convert.parseToInt64(strv, 1000);
				return true;
//			case "script":
//				this.script = strv ;
//				return true ;
//			case "script_int":
//				this.scriptInt = Long.parseLong(strv) ;
//				return true ;
			}
			
		}
		return super.setPropValue(groupn, itemn,strv);
	}
	
	@Override
	protected void onPropNodeValueChged()
	{
		
	}
	
	public String OCUnit_getUnitTemp()
	{
		return "ch";
	}
	
	public void OCUnit_setBaseVal(String name,String title)
	{
		
	}
	/**
	 * true node may has sub unit
	 * @return
	 */
	public boolean OC_supportSub()
	{
		return true;
	}
	
	public List<IOCBox> OC_getSubs()
	{
		List<IOCBox> rets = super.OC_getSubs();
		rets.addAll(devs);
		return rets;
	}
	
	public void OC_renderDyn(Writer w) throws Exception
	{
		w.write("{id:\"\",name:\"\"");
		w.write("}");
	}
	
	
	@Override
	void RT_init(boolean breset, boolean b_sub)
	{
		super.RT_init(breset, b_sub);
		this.setSysTag("_name", "channel name", "", ValTP.vt_str);
		this.setSysTag("_title", "channle", "", ValTP.vt_str);
		this.setSysTag("_driver_name", "", "", ValTP.vt_str);
		this.setSysTag("_driver_run", "", "", ValTP.vt_bool);
		
		this.RT_setSysTagVal("_name", this.getName()) ;
		this.RT_setSysTagVal("_title", this.getTitle()) ;
		this.RT_setSysTagVal("_driver_name", this.getDriverName()) ;
		
		this.setSysTag("_conn_ready", "","",ValTP.vt_bool) ;
		this.setSysTag("_conn_name", "","",ValTP.vt_str) ;
		this.setSysTag("_conn_tp", "","",ValTP.vt_str) ;
	}
	
	@Override
	protected void RT_flush()
	{
		super.RT_flush();
		
		try
		{
			ConnPt cpt = getConnPt() ;
			this.RT_setSysTagVal("_conn_ready", cpt!=null&&cpt.isConnReady());
			
			if(cpt!=null)
			{
				this.RT_setSysTagVal("_conn_name", cpt.getName());
				this.RT_setSysTagVal("_conn_tp", cpt.getConnType());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		this.RT_setSysTagVal("_driver_run",  this.RT_getState().isRunning());
	}
	
	
	/**
	 * start driver
	 * @param failedr
	 * @return
	 * @throws Exception 
	 */
	public boolean RT_startDriver(StringBuilder failedr) throws Exception
	{
		if(isConnVirtual())
		{
			failedr.append("channel is connected to virtual") ;
			return false ;
		}
		
		//this.getConnJoinedProvider()
		DevDriver drv = this.getDriver() ;
		if(drv==null)
		{
			failedr.append("no driver found") ;
			return false;
		}
		if(drv.RT_isRunning())
		{
			failedr.append("driver is running") ;
			return false;
		}
		
		//finally
		return drv.RT_start(failedr) ;
	}
	
	public boolean RT_stopDriver(boolean bforce)
	{
		DevDriver drv = this.getDriver() ;
		if(drv==null)
			return false;
		drv.RT_stop(bforce);
		return true;
	}
	
	public DevDriver.State RT_getState()
	{
		DevDriver drv = this.getDriver() ;
		if(drv==null)
		{
			return DevDriver.State.not_run;
		}
		return drv.getDriverState() ;
	}

	@Override
	public JSONObject OC_getDynJSON(long lastdt)
	{
		JSONObject r = new JSONObject() ;
		r.put("state", RT_getState().getInt()) ;
		return r;
	}
	

//	protected Object JS_get(String  key)
//	{
//		UADev dev = this.getDevByName(key) ;
//		if(dev!=null)
//			return dev ;
//		
//
//		return null ;
//	}
	
	
	//--cxt support

	@Override
	public boolean CXT_containsKey(String jsk)
	{
		UADev dev = this.getDevByName(jsk) ;
		if(dev!=null)
			return true;
		return false;
	}

	@Override
	public Object CXT_getByKey(String jsk)
	{
		UADev dev = this.getDevByName(jsk) ;
		if(dev!=null)
			return dev ;
		return null;
	}

	/**
	 * IOTTree Node support
	 * 
	 * @param xd
	 * @throws Exception
	 */
	public void Node_refreshByPrjXmlData(XmlData xd) throws Exception
	{
		UAPrj r = new UAPrj() ;
		DataTranserXml.injectXmDataToObj(r, xd);
		//other prj make all as sub node
		List<UANode> subnodes = r.getSubNodes() ;
		for(UANode subn:subnodes)
		{
			Node_refreshBySubNode(this,subn,true) ;
			
		}
		this.save();
	}
	
	private void Node_refreshBySubNode(UANodeOCTagsGCxt gcxt,UANode uan,boolean firstlvl) throws Exception
	{
		if(uan instanceof UANodeOCTagsGCxt)
		{
			UANodeOCTagsGCxt tmptg = (UANodeOCTagsGCxt)uan ;
			String tmpn = tmptg.getName();
			UATagG nn = (UATagG)gcxt.getSubNodeByName(tmpn) ;
			UATagG tg = null;
			if(nn!=null)
				tg = gcxt.updateTagG(nn, uan.getName(), uan.getTitle(), uan.getDesc()) ;
			else
				tg = gcxt.addTagG(uan.getName(), uan.getTitle(), uan.getDesc(),false) ;
			if(firstlvl)
				tg.withRefLockedLoc(REF_LOCKED);
			List<UANode> tmpsubs = tmptg.getSubNodes();
			if(tmpsubs!=null)
			{
				for(UANode tmpsub:tmpsubs)
				{
					Node_refreshBySubNode(tg,tmpsub,false) ;
				}
			}
		}
		else if(uan instanceof UATag)
		{
			UATag t = (UATag)uan ;
			UANode tmpn = gcxt.getSubNodeByName(t.getName()) ;
			if(tmpn==null)
			{
				gcxt.addOrUpdateTagSys(null, t.bMidExp, t.getName(), t.getTitle(), t.getDesc(), "", 
						t.getValTp(),t.getDecDigits(), t.bCanWrite, t.scanRate,false) ;
			}
		}
		else if(uan instanceof UAHmi)
		{
			UAHmi hmi = (UAHmi)uan ;
			//hmi.
			//gcxt.addHmi("",  hmi.getName(), hmi.getTitle(), hmi.getDesc(), null);
			// TODO: 1,up node must has sub node's lib comp
			//            2 hmi may use sub node project res,it must syn too
			//            it's must be designed carefully
		}
		
	}
}
