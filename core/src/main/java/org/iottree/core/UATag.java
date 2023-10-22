package org.iottree.core;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptException;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.*;
import org.iottree.core.util.xmldata.XmlVal.*;
import org.graalvm.polyglot.*;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.basic.ValTranser;
import org.iottree.core.conn.ConnPtBinder;
import org.iottree.core.conn.ConnPtMSG;
import org.iottree.core.conn.ConnPtVirtual;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
@data_class
public class UATag extends UANode implements IOCDyn //UANode UABox
{
	public static final String NODE_TP  ="tag" ;
	
	transient UANodeOCTags belongToNode = null ;
	
	//transient  UATagG parentTagG = null ;
	//transient UATagList belongToTL = null ;
	
	@data_val
	String rename = null ;
	
	@data_val
	String retitle = null ;
	
	@data_val
	String redesc = null ;
	
	/**
	 * is middle express tqg
	 */
	@data_val(param_name = "mid")
	boolean bMidExp = false;
	
	@data_val(param_name = "midt")
	private String get_MidT()
	{
		return bMidExp?"M":"" ;
	}
	/**
	 * addr or express
	 */
	@data_val
	String addr = "" ;
	
	private transient DevAddr devAddr = null ;
	
	/**
	 * node local var tag or not
	 * if true. Tag addr is ignore and this tag value will not related driver
	 * it can be set default value,and it can be set writable, and be auto saved
	 */
	@data_val(param_name = "local")
	boolean bLocal = false;
	
	/**
	 * tag default value. it can be set for readonly tag.
	 */
	@data_val(param_name = "local_default")
	String localDefaultVal = null ;
	
	/**
	 * true will make this tag's value auto saved when value is set to changed
	 * so,it will not lost last value when server is reboot.
	 * 
	 * it can be used for setup control parameter by user in hmi etc.
	 */
	@data_val(param_name = "local_auto_save")
	boolean bLocalAutoSave = false;

	
	private UAVal.ValTP valTp = null;
	
	@data_val(param_name = "vt")
	private int get_ValTP()
	{
		if(valTp==null)
			return 0 ;
		return valTp.getInt() ;
	}
	@data_val(param_name = "vt")
	private void set_ValTP(int v)
	{
		valTp = UAVal.getValTp(v) ;
	}
	
	
	
	@data_val(param_name = "vtt",extract_only = true)
	private String getValTpTitle()
	{
		if(valTp==null)
			return "" ;
		return valTp.getStr();
	}
	
	@data_val(param_name = "dec_digits")
	int decDigits = -1 ;

	@data_val(param_name = "w")
	boolean bCanWrite = true ;
	
	/**
	 * millisecond
	 */
	@data_val(param_name="sr")
	long scanRate = 100 ;
	
	@data_val(param_name="transer")
	String valTranser=null;
	
	
	
	@data_val(param_name="val_cache_len")
	int valChgedCacheLen = 100 ;
	
	@data_val(param_name="b_val_filter")
	boolean bValFilter = false;
	
	@data_val(param_name="val_filter")
	String valFilter = null;
	
	/**
	 * save val his, to support 
	 */
	private transient UAValList valsCacheList = null ;
	
	private transient UAVal curVal = new UAVal(false,null,-1,-1) ;
	
	private transient Object curRawVal = null ;
	
	private transient ValTranser valTransObj = null ;
	
	//for value not chg check,when tag is inited or 
	// reload,not chg check will ignore
	//private transient boolean bInitLoaded = true ;
	
	public UATag()
	{
		super();
	}
	
	public UATag(String name,String title,String desc,String addr,UAVal.ValTP vt,int dec_digits,boolean canwrite,long srate)
	{
		super(name,title,desc) ;
		this.addr = addr ;
		this.valTp = vt ;
		this.bCanWrite = canwrite ;
		this.scanRate = srate;
		this.decDigits = dec_digits ;
	}
	
	UATag(UATag t,String newname,String newtitle,String newaddr)
	{
		super(newname,newtitle,t.getDesc()) ;
		
		this.rename = null ;
		this.retitle = null;
		this.redesc = null ;
		
		bMidExp = t.bMidExp;
		if(Convert.isNotNullEmpty(newaddr))
			this.addr = newaddr ;
		else
			this.addr = t.addr;
		this.valTp = t.valTp;
		this.decDigits = t.decDigits ;

		this.bCanWrite = t.bCanWrite ;
		this.scanRate = t.scanRate ;
		
		this.valTranser=t.valTranser;
		this.valChgedCacheLen = t.valChgedCacheLen ;
	}
	
	public String getNodeTp()
	{
		return NODE_TP;
	}
	
	void setTagNor(String name,String title,String desc,String addr,UAVal.ValTP vt,int dec_digits,boolean canwrite,long srate)
	{
		setNameTitle(name,title,desc) ;
		this.addr = addr ;
		this.valTp = vt ;
		this.bCanWrite = canwrite ;
		this.scanRate = srate;
		this.decDigits = dec_digits ;
	}
	
	void setTagSys(String name,String title,String desc,String addr,UAVal.ValTP vt,int dec_digits,boolean canwrite,long srate)
	{
		setNameTitleSys(name,title,desc) ;
		this.addr = addr ;
		this.valTp = vt ;
		this.bCanWrite = canwrite ;
		this.scanRate = srate;
		this.decDigits = dec_digits ;
	}
	
	public UATag(DevItem item)
	{
		super(item.name,item.title,item.desc) ;
		this.addr = item.addr ;
		this.valTp = item.valTp ;
		this.bCanWrite = item.canW;
		this.scanRate = item.scanRate;
	}
	
	/**
	 * create middle express tag
	 * @param name
	 * @param title
	 * @param desc
	 * @param addrexp
	 * @param vt
	 */
	UATag(String name,String title,String desc,String addrexp,UAVal.ValTP vt,int dec_digits)
	{
		super(name,title,desc) ;
		this.bMidExp = true ;
		this.addr = addrexp ;
		this.valTp = vt ;
		this.bCanWrite = false ;
		this.scanRate = -1;
		this.decDigits = dec_digits ;
	}
	
	void setTagMid(String name,String title,String desc,String addrexp,UAVal.ValTP vt,int dec_digits)
	{
		setNameTitle(name,title,desc) ;
		this.bMidExp = true ;
		this.addr = addrexp ;
		this.valTp = vt ;
		this.bCanWrite = false ;
		this.scanRate = -1;
		this.decDigits = dec_digits ;
	}
	
	/**
	 * tag in project may has it's own name title and desc
	 * so there are defined by rename retitle redesc
	 * 
	 * @param n
	 * @param t
	 * @param d
	 */
	public boolean setReNameTitle(String name,String title,String desc)
	{
		boolean b = false;
		Convert.checkVarName(name);
		if(name.startsWith("_"))
			throw new IllegalArgumentException("name cannot start with _") ;
		if(!name.equals(this.rename))
		{
			this.rename = name ;
			b = true ;
		}
		
		if(!title.equals(this.retitle))
		{
			this.retitle = title ;
			b = true ;
		}
		if((desc==null&&this.redesc!=null)||!desc.equals(this.redesc))
		{
			this.redesc = desc ;
			b = true ;
		}
		return b;
	}
	
	public String getReName()
	{
		return this.rename ;
	}
	
	public String getReTitle()
	{
		return this.retitle ;
	}
	
	public String getReDesc()
	{
		return this.redesc;
	}
//	public static UATag newMidExpTag(String name,String title,String desc,String addrexp,UAVal.ValTP vt)
//	{
//		
//	}
	
	@Override
	public String getName()
	{
		if(Convert.isNullOrEmpty(this.rename))
			return super.getName() ;
		return this.rename ;
	}
	
	
	public String getNameSor()
	{
		return super.getName();
	}
	
	@Override
	public String getTitle()
	{
		if(Convert.isNullOrEmpty(this.retitle))
			return super.getTitle() ;
		return this.retitle ;
	}
	
	public String getTitleSor()
	{
		return super.getTitle() ;
	}
	
	@Override
	public String getDesc()
	{
		if(Convert.isNullOrEmpty(this.redesc))
			return super.getDesc() ;
		return this.redesc ;
	}
	
	public String getDescSor()
	{
		return super.getDesc() ;
	}
	/**
	 * 
	 * @param new_self create by copySelfWithNewId
	 */
	@Override
	protected void copyTreeWithNewSelf(IRoot root,UANode new_self,String ownerid,
			boolean copy_id,boolean root_subnode_id,HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		super.copyTreeWithNewSelf(root,new_self,ownerid, copy_id, root_subnode_id,rf2new);
		UATag nt = (UATag)new_self ;
		nt.rename = this.rename ;
		nt.retitle = this.retitle ;
		nt.redesc = this.redesc;
		nt.bMidExp = this.bMidExp;
		
		nt.addr = this.addr ;
		nt.valTp = this.valTp;
		nt.decDigits = this.decDigits ;
		nt.bCanWrite = this.bCanWrite ;
		nt.scanRate = this.scanRate ;
		
		nt.valTranser=this.valTranser;
		
		nt.valChgedCacheLen = this.valChgedCacheLen ;
	}
	
	public UANodeOCTags getBelongToNode()
	{
		return belongToNode;
	}
	
	public boolean isSysTag()
	{
		return this.getName().startsWith("_") ;
	}
	
	public boolean isLocalTag()
	{
		return this.bLocal ;
	}
	
	public String getLocalDefaultVal()
	{
		return this.localDefaultVal;
	}
	
	public boolean isLocalAutoSave()
	{
		return this.bLocalAutoSave ;
	}
	
	public UATag asLocal(boolean blocal,String defval,boolean autosave)
	{
		this.bLocal = blocal ;
		this.localDefaultVal = defval ;
		this.bLocalAutoSave = autosave ;
		return this ;
	}
	
	public UATag asFilter(boolean b_val_filter)
	{
		this.bValFilter = b_val_filter; 
		return this ;
	}
	
	@Override
	public List<UANode> getSubNodes()
	{
		return null;
	}

	public boolean isMidExpress()
	{
		return bMidExp;
	}
	
	public UADev getBelongToDev()
	{
		UANode p = belongToNode ;
		do
		{
			if(p instanceof UADev)
				return (UADev)p ;
			p = p.getParentNode() ;
		}while(p!=null);
		return null ;
	}
	
	public UACh getBelongToCh()
	{
		UANode p = belongToNode ;
		do
		{
			if(p instanceof UACh)
				return (UACh)p ;
			p = p.getParentNode() ;
		}while(p!=null);
		return null ;
	}
	
	
	DevDriver getRelatedDriver()
	{
		UADev dev =getBelongToDev();
		if(dev==null)
			return null ;
		UACh ch= dev.getBelongTo() ;
		if(ch==null)
			return null;
		return ch.getDriver() ;
	}
	
//	public List<UANode> getSubNodes()
//	{
//		return null;
//	}
	
//	public UATagG getParent()
//	{
//		return parentTagG ;
//	}
	
	protected boolean chkValid()
	{
		if(Convert.isNullOrEmpty(addr))
			return false;
		if(valTp==null)
			return false;
		return true;
	}
	
	public String getAddress()
	{
		if(addr==null)
			return "" ;
		return addr ;
	}
	
	public DevAddr getDevAddr(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(addr))
			return null ;
		if(devAddr!=null)
			return devAddr;
		UAVal.ValTP vt = getValTpRaw() ;
		if(vt==null)
			return null ;
		DevDriver dd =getRelatedDriver();
		if(dd==null)
			return null;
		devAddr = dd.getSupportAddr().parseAddr(this.addr,vt, failedr);
		if(devAddr!=null)
			devAddr.belongTo = this ;
		return devAddr ;
	}
	
	
	
	public UAVal.ValTP getValTpRaw()
	{
		return valTp ;
	}
	
	public UAVal.ValTP getValTp()
	{
		ValTranser vt = this.getValTranserObj() ;
		if(vt==null)
			return valTp ;
		return vt.getTransValTP() ;
	}
	
	
	public int getDecDigits()
	{
		return decDigits ;
	}
	
	public long getScanRate()
	{
		return this.scanRate ;
	}
	
	public String getValTranser()
	{
		return this.valTranser;
	}
	
	public void setValTranser(String valtstr)
	{
		this.valTranser = valtstr ;
		valTransObj = null ;
	}
	
	public ValTranser getValTranserObj()
	{
		if(this.valTransObj!=null)
			return this.valTransObj;
		if(Convert.isNullOrEmpty(this.valTranser)||"null".equals(this.valTranser))
			return null ;
		
		this.valTransObj = ValTranser.parseValTranser(this,this.valTranser) ;
		return this.valTransObj;
	}
	
	public void setValTranserObj(ValTranser vt)
	{
		this.valTransObj = vt ;
		if(vt==null)
			this.valTranser = null ;
		else
			this.valTranser = vt.toString() ;
	}
	
	public boolean isCanWrite()
	{
		return bCanWrite;
	}
	
	public boolean isValFilter()
	{
		return bValFilter;
	}
	
	public boolean delFromParent() throws Exception
	{
		if( this.belongToNode==null)
			return false;
		if( this.belongToNode.delTag(this))
				return true;
		
		return false;
	}
	
	
	private transient List<PropGroup> tagPGS = null ;

	@Override
	public List<PropGroup> listPropGroups()
	{
		if(tagPGS!=null)
			return tagPGS;
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		List<PropGroup> lpgs = super.listPropGroups() ;
		if(lpgs!=null)
			pgs.addAll(lpgs) ;
		
		PropGroup pg = new PropGroup("tag","Tag Properties");
		pg.addPropItem(new PropItem("mid","Is Middle Tag","",PValTP.vt_bool,true,null,null,false));
		pg.addPropItem(new PropItem("addr","Address Or Script Expression","",PValTP.vt_str,false,null,null,""));
		pg.addPropItem(new PropItem("vt","Data type","",PValTP.vt_int,false,UAVal.ValTPTitles,UAVal.ValTPVals,1));
		pg.addPropItem(new PropItem("w","Client Access","",PValTP.vt_bool,false,new String[] {"Read Only","Read/Write"},new Object[] {false,true},false));
		pg.addPropItem(new PropItem("sc","ScanRate","",PValTP.vt_int,false,null,null,100));
		pgs.add(pg) ;
		
		tagPGS = pgs;
		return pgs;
	}
	
	public Object getPropValue(String groupn,String itemn)
	{
		if("tag".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "mid":
				return this.isMidExpress();
			case "addr":
				return this.getAddress();
			case "vt":
				if(this.getValTp()==null)
					return 0 ;
				return this.getValTp().getInt();
			case "w":
				return this.bCanWrite ;
			case "sc":
				return this.scanRate ;
			}
		}
		return super.getPropValue(groupn, itemn);
	}
	
	public boolean setPropValue(String groupn,String itemn,String strv)
	{
		if("tag".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "mid":
				this.bMidExp = "true".contentEquals(strv) ;
				return true;
			case "addr":
				this.addr = strv ;
				if(this.addr==null)
					this.addr = "" ;
				devAddr=null;
				return true;
			case "vt":
				try
				{
					int v = Integer.parseInt(strv) ;
					valTp = UAVal.getValTp(v);
					devAddr=null;
					return true;
				}
				catch(Exception e)
				{
					return false;
				}
			case "w":
				this.bCanWrite = "true".contentEquals(strv) ;
				return true ;
			case "sc":
				this.scanRate = Long.parseLong(strv) ;
				return true ;
			}
		}
		return super.setPropValue(groupn, itemn,strv);
	}
	
	@Override
	protected void onPropNodeValueChged()
	{
		tagPGS = null ;
		devAddr = null ;
	}
	
	private long lastValEmptyDT = -1 ;
	@Override
	public JSONObject OC_getDynJSON(long lastdt)
	{
		JSONObject r = new JSONObject() ;
		UAVal v = RT_getVal() ;
		if(v==null)
		{
			if(lastdt>0&&lastdt>=lastValEmptyDT)
				return null ;
			
			lastValEmptyDT = System.currentTimeMillis() ;
			r.put("qt", false);//quality
			return r;
		}
		
		lastValEmptyDT = -1 ;
		
		long vdt = v.getValDT() ;
		if(lastdt>0&&lastdt>=vdt)
			return null ;
		//StringBuilder sb = new StringBuilder() ;
		
		r.put("ts", Convert.toFullYMDHMS(new Date(vdt)));
		r.put("qt", v.isValid());//quality
		r.put("val", v.getObjVal()) ;
//		DevAddr da = this.getDevAddr(sb);
//		if(da==null)
//		{
//			r.put("qt", false);//quality
//			return r;
//		}
//		boolean qt = da.RT_getValQT();
//		r.put("qt", qt);
//		r.put("ts", Convert.toFullYMDHMS(new Date(da.RT_getValDT())));
//		if(!qt)
//		{
//			r.put("val", "") ;
//		}
//		else
//		{
//			Object v = da.RT_getValObj();
//			if(v!=null)
//			{
//				r.put("val", v) ;
//			}
//		}
		return r;
	}
	
	public UANodeOCTagsCxt CXT_getBelongToCxtNode()
	{
		UANode pn = this.belongToNode ;
		if(pn==null)
			return null ;
		if(pn instanceof UANodeOCTagsCxt)
			return (UANodeOCTagsCxt)pn ;
		do
		{
			pn = pn.getParentNode() ;
			if(pn instanceof UANodeOCTagsCxt)
				return (UANodeOCTagsCxt)pn ;
		}
		while(pn!=null) ;
		return null ;
	}
	
	public UAContext CXT_getBelongToCxt()
	{
		UANodeOCTagsCxt n = CXT_getBelongToCxtNode();
		if(n==null)
			return null ;
		return n.RT_getContext();
	}
	
	private transient UACodeItem codeItem = null ;
	
	//private transient UAVal midVal = null ;
	
	
	
	public UACodeItem CXT_getMidCodeItem() throws ScriptException
	{
		if(!this.isMidExpress())
			return null ;
		if(codeItem!=null)
			return codeItem ;
		
		if(Convert.isNullOrEmpty(this.addr))
			return null ;
		UAContext cxt = CXT_getBelongToCxt();
		if(cxt== null)
			return null;
		codeItem = new UACodeItem("",this.addr,cxt) ;
		return codeItem;
	}
	
	/**
	 * outter thread call this method to calculate mid value
	 */
	UAVal CXT_calMidVal()
	{
		if(!this.isMidExpress())
			return null;
		//UAVal v = new UAVal() ;
		try
		{
			UACodeItem ci =  CXT_getMidCodeItem();
			if(ci==null)
			{
				//v.setValErr("no express code item");
				//this.RT_set
				return null;
			}
			Object ob = ci.runCode() ;
			return this.RT_setValRaw(ob);
			//v.setVal(true, ob, System.currentTimeMillis());
			
		}
		catch(Exception e)
		{
			//v.setValException(e.getMessage(), e);
			//e.printStackTrace();
			return RT_setValErr(e.getMessage(),e) ;
		}
		
		//this.RT_setUAVal(v);
		//return v;
	}
	
	UAVal RT_readValFromDriver()
	{
		if(this.isMidExpress()||this.isSysTag())
		{
			return null ;
		}
		
		StringBuilder sb = new StringBuilder() ;
		DevAddr da = this.getDevAddr(sb);
		UAVal r = null ;
		if(da!=null)
		{
			r = da.RT_getVal() ;
		}
		return r ;
	}
//	/**
//	 * 不考虑各种驱动、地址和型号，也不考虑mid
//	 * 直接设置对应的值
//	 * @param v
//	 */
//	public void RT_setVal(Object v)
//	{
//		midVal = new UAVal(true,v,System.currentTimeMillis()) ;
//		HIS_setVal(midVal) ;
//	}
	
	private void HIS_setVal(UAVal v)
	{
		if(this.valsCacheList==null)
			this.valsCacheList = new UAValList(this.valChgedCacheLen) ;
		this.valsCacheList.addVal(v.copyMe());
	}
	
	/**
	 * get history values
	 * @param lastdt
	 * @return
	 */
	public List<UAVal> HIS_getVals(long lastdt)
	{
		if(this.valsCacheList==null)
			return null ;
		return this.valsCacheList.getVals(lastdt) ;
	}
	
	
	
	/**
	 * set value in memory
	 * for systag etc
	 * @param v
	 */
	@HostAccess.Export
	public void RT_setVal(Object v)
	{
		long cdt = System.currentTimeMillis() ;
		//UAVal uav = ;//RT_getVal();
		//boolean bchg = true; 
		if(this.curVal!=null)
		{
			//if(uav.isValid() && uav.getObjVal().equals(v))
			//	return ;
			if(this.curVal.isValid()&&v.equals(this.curVal.getObjVal()))
			{
				this.curVal.setValUpDT(cdt);//.setVal(true, v, cdt);
				return ;
			}
		}
		
		UAVal uav = new UAVal(true,v,cdt,cdt) ;
		RT_setUAVal(uav);
	}
	
	public UAVal RT_setValErr(String err,Exception e)
	{
		if(this.curVal!=null)
		{
			if(!this.curVal.isValid() && err.equals(this.curVal.getErr()))
				return this.curVal ;
		}
		UAVal uav = new UAVal(err,e) ;
		//RT_setUAVal(uav);
		this.curVal = uav ; //强制替换
		return uav ;
	}
	
	public synchronized UAVal RT_setValRaw(Object v,boolean ignore_nochg,Long updt,Long chgdt)
	{
		curRawVal = v ;
		
		//if(bval_chg || this.curVal==null)
		if(v!=null)
		{
			ValTranser vt = this.getValTranserObj() ;
			if(vt!=null)
			{
				try
				{
					v = vt.transVal(v) ;
					v = UAVal.transStr2ObjVal(vt.getTransValTP(),v.toString()) ;
					//v = UAVal.transStr2ObjVal(this.getValTp(),v.toString()) ;
				}
				catch(Exception ee)
				{
					return this.RT_setValErr(ee.getMessage(),ee);
					//return ;
				}
			}
		}
		
		boolean bval_chg = true;
		if(v==null)
		{
			if(this.curVal!=null&&!this.curVal.isValid())
				bval_chg= false ;
		}
		else
		{
			if(this.curVal!=null && this.curVal.isValid() && v.equals(this.curVal.getObjVal()))
				bval_chg = false ;
		}
		
//		UAVal uav = this.curVal;//RT_getVal();
//		if(ignore_nochg && uav!=null)
//		{
//			if(uav.isValid() && uav.getObjVal().equals(v))
//				return ;
//		}
		
		long cdt = System.currentTimeMillis() ;
		if(updt==null)
			updt = cdt ;
		
		if(curVal!=null && !bval_chg)
			chgdt = curVal.getValChgDT() ;
		else
			chgdt = cdt ;
//		if(chgdt!=null)
//			cdt = chgdt ;
		
		if(this.curVal!=null&&!bval_chg)
		{
			this.curVal.setValUpDT(updt);//.setVal(true,v,cdt);
			return curVal;
		}
		//uaVal.setVal(false, null, System.currentTimeMillis());
		UAVal uav = null;
		if(v!=null)
			uav = new UAVal(true,v,updt,chgdt) ;
		else
			uav = new UAVal(false,null,updt,chgdt) ;
		RT_setUAVal(uav);
		return uav ;
	}
	
	public void RT_setValStr(String strv)
	{
		Object objv = UAVal.transStr2ObjVal(this.getValTpRaw(), strv);
		RT_setVal(objv);
	}
	
	public void RT_setValRawStr(String strv,boolean ignore_chg,Long chgdt)
	{
		Object objv = UAVal.transStr2ObjVal(this.getValTpRaw(), strv);
		this.RT_setValRaw(objv,ignore_chg,null,chgdt);
	}
//	
//	
//	public void RT_setVal(Object v)
//	{
//		this.RT_setVal(v,true);
//	}
	
	public void RT_setUAVal(UAVal uav)
	{
		HIS_setVal(uav) ;
		if(bValFilter) //Convert.isNotNullEmpty(this.valFilter))
		{
			UAVal tmpv = this.valsCacheList.filterValByAntiInterference(this) ;
			if(tmpv!=null)
				uav = tmpv ;
		}
		this.curVal = uav ;
	}
	
	/**
	 * driver get value,may has transfer
	 * @param v
	 * @throws Exception 
	 */
	public UAVal RT_setValRaw(Object v)
	{
		return this.RT_setValRaw(v,true,null,null);
	}
	
	public UAVal RT_setValRaw(Object v,Long updt,Long chgdt)
	{
		return this.RT_setValRaw(v,true,updt,chgdt);
	}
	
	private boolean RT_writeValDriver(Object v)
	{
		if(this.isMidExpress())
			return false;
		
		StringBuilder sb = new StringBuilder() ;
		DevAddr da = this.getDevAddr(sb);
		if(da==null)
			return false;
		UADev dev = this.getBelongToDev();
		if(dev==null)
			return false;
		
		DevDriver dd = dev.getBelongTo().getDriver() ;
		if(dd==null)
			return false;

		ValTranser vtrans = this.getValTranserObj() ;
		if(vtrans!=null)
		{
			try
			{
				v = vtrans.inverseTransVal(v) ;
				if(v==null)
					return false;
				v = UAVal.transStr2ObjVal(this.getValTpRaw(),v.toString()) ;
			}
			catch(Exception ee)
			{
				//if(Log.isDebugging())
				ee.printStackTrace();
				return false;
			}
		}
		
		return dd.RT_writeVal(dev, da, v);
	}
	
	private boolean RT_writeValLocal(Object v)
	{
		RT_setVal(v);
		return true;
	}
	
	public boolean RT_writeVal(Object v) //throws Exception
	{
		return RT_writeVal(v,null) ;
	}
	
	public boolean RT_writeVal(Object v,StringBuilder failedr) //throws Exception
	{
		if(this.isLocalTag())
		{
			return RT_writeValLocal(v);
		}
		
		UACh ch = this.getBelongToCh();
		if(ch==null)
			return false;
		try
		{
			if(ch.isConnVirtual())
			{
				ConnPtVirtual cpt = (ConnPtVirtual)ch.getConnPt() ;
				cpt.runOnWrite(this, v);
				return true;
			}
			else if(ch.isConnMsg())
			{
				ConnPtMSG cpt = (ConnPtMSG)ch.getConnPt();
				cpt.runOnWrite(this,v) ;
				return true;
			}
			else if(ch.isConnBind())
			{
				ConnPtBinder cpt = (ConnPtBinder)ch.getConnPt();
				cpt.RT_writeValByBind(this.getNodeCxtPathIn(ch), v.toString());
				return true;
			}
			else
			{
				return RT_writeValDriver(v);
			}
		}
		catch(Exception e)
		{
			if(failedr!=null)
				failedr.append(e.getMessage()) ;
			else
				e.printStackTrace();
			return false;
		}
	}
	
	public boolean RT_writeValStr(String strv) // throws Exception
	{
		return RT_writeValStr(strv,null) ;
	}
	
	public boolean RT_writeValStr(String strv,StringBuilder failedr) // throws Exception
	{
		StringBuilder sb = new StringBuilder() ;
		DevAddr da = this.getDevAddr(sb);
		ValTP tp = null;
		if(da!=null)
			tp = da.getValTP();
		else
			tp = this.getValTpRaw();
		Object v = UAVal.transStr2ObjVal(tp, strv);
		if(v==null)
			return false;
		
		return RT_writeVal(v,failedr) ;
	}
	
	
	public UAVal RT_getVal()
	{
		return this.curVal ;
	}
	
	
	public Object JS_get(String  key)
	{
//		if("kwh_di".equals(this.getName()))
//		{
//			System.out.println("kwh_val");
//		}
		
		Object obj  = super.JS_get(key) ;
		if(obj!=null)
			return obj ;
		
		UAVal uav = this.RT_getVal() ;
		if(uav==null)
			return null ;
		
		
		switch(key.toLowerCase())
		{
		case "_pv":
		case "_value":
			return uav.getObjVal() ;
		case "_valid":
			return uav.isValid() ;
		case "_updt":
			return uav.getValDT() ;
		case "_chgdt":
			return uav.getValChgDT() ;
		}
		return null ;
	}
	
	
	public final static List<String> js_names = Arrays.asList("_pv","_valid","_updt","_chgdt","_value") ;
	
	
	public List<JsProp> JS_props()
	{
		List<JsProp> rets = super.JS_props() ;
		
		UAVal.ValTP vtp = this.getValTp();
		Class<?> vt = Integer.class ;
		if(vtp!=null)
			vt = vtp.getValClass() ;
		
		rets.add(new JsProp("_pv",vt,"Tag Value","Tag Value,you can get or set by using '='"));
		rets.add(new JsProp("_valid",Boolean.class,"Valid","Tag Value is valid or not in running"));
		rets.add(new JsProp("_updt",Long.class,"Update Date","Tag Value last update date with millisseconds,value may not be changed"));
		rets.add(new JsProp("_chgdt",Long.class,"Change Date","Tag Value last changed date with millisseconds"));
		rets.add(new JsProp("_value",vt,"Tag Value","Tag Value,get value is same as _pv,bug set this prop will not trigger device write(only set in memory)"));
		return rets ;
	}
	
	public Class<?> JS_type(String key)
	{
		switch(key.toLowerCase())
		{
		case "_pv":
		case "_value":
			UAVal.ValTP vtp = this.getValTp();
			if(vtp==null)
				return Integer.class ;
			return vtp.getValClass() ;
		default:
			break ;//do nothing
		}
		return null ;
	}
	
	public void JS_set(String key,Object v)
	{
		switch(key.toLowerCase())
		{
		case "_pv":
			if(v instanceof String)
				RT_writeValStr((String)v) ;
			else
				RT_writeVal(v) ;
			return ;
		case "_value":
			if(v instanceof String)
				RT_setValStr((String)v) ;
			else
				this.RT_setVal(v);
			return;
		default:
			break ;//do nothing
		}
		
		super.JS_set(key, v);
	}
	
	@Override
	public JSONObject OC_getPropsJSON()
	{
		return null;
	}
	@Override
	public void OC_setPropsJSON(JSONObject jo)
	{
	}
	@Override
	public boolean OC_supportSub()
	{
		return false;
	}
	@Override
	public List<IOCBox> OC_getSubs()
	{
		return null;
	}
	
	public void CXT_renderTagJson(Writer w) throws IOException
	{
		long dt_chg = -1;
		
		//String cxtpath = this.getNodeCxtPathIn(this);
		boolean bloc = this.getParentNode() == this;

		UAVal val = this.RT_getVal();

		boolean bvalid = false;
		// Object v=null ;
		String strv = "";
		long dt = -1;
		
		String str_err = "";

		if (val != null)
		{
			bvalid = val.isValid();
			// v = val.getObjVal() ;
			strv = val.getStrVal(this.getDecDigits());
			dt = val.getValDT();// Convert.toFullYMDHMS(new
								// Date(val.getValDT())) ;
			dt_chg = val.getValChgDT();// Convert.toFullYMDHMS(new
										// Date(val.getValChgDT())) ;
			str_err = val.getErr();
			if (str_err == null)
				str_err = "";
		}
		else
		{
			dt_chg = System.currentTimeMillis();
		}




		w.write("{\"p\":\"" + this.getName() + "\",\"t\":\"" + this.getTitle() + "\",\"vt\":\"" + this.getValTp() + "\"");

		ValTP vtp = this.getValTp();
		if (bvalid)
		{
			if (vtp.isNumberVT() || vtp == ValTP.vt_bool)
				w.write(",\"valid\":" + bvalid + ",\"v\":" + strv + ",\"strv\":\"" + strv + "\",\"dt\":" + dt
						+ ",\"chgdt\":" + dt_chg + "}");
			else
				w.write(",\"valid\":" + bvalid + ",\"v\":\"" + strv + "\",\"strv\":\"" + strv + "\",\"dt\":"
						+ dt + ",\"chgdt\":" + dt_chg + "}");
		}
		else
		{
			w.write(",\"valid\":" + bvalid + ",\"v\":null,\"dt\":" + dt + ",\"chgdt\":" + dt_chg + ",\"err\":"
					+ JSONObject.quote(str_err) + "}");
//			w.write(",\"valid\":" + bvalid + ",\"v\":null,\"dt\":" + dt + ",\"chgdt\":" + dt_chg + ",\"err\":\""
//					+ Convert.plainToJsStr(str_err) + "\"}");
		}

	}

	
	public void renderJson(UANode innode,Writer w) throws IOException
	{
		UAVal val = RT_getVal();
		// if(val==null)
		// continue ;

		boolean bvalid = false;
		String strv = "";
		long dt = -1;
		long dt_chg = -1;
		String str_err = "";

		if (val != null)
		{
			bvalid = val.isValid();
			//Object v = val.getObjVal();
			strv = val.getStrVal(getDecDigits());
			dt = val.getValDT();// Convert.toFullYMDHMS(new
								// Date(val.getValDT())) ;
			dt_chg = val.getValChgDT();// Convert.toFullYMDHMS(new

			str_err = val.getErr();
			if (str_err == null)
				str_err = "";
		}
		else
		{
			dt_chg = System.currentTimeMillis();
		}

		
		// w.write("\""+tg.getName()+"\":");
		w.write("{\"n\":\"");
		if(innode==null)
			w.write(getName());
		else
			w.write(this.getNodeCxtPathIn(innode, "_"));
		w.write("\",\"t\":\"");
		w.write(getTitle());
		if(innode!=null)
		{
			w.write("\",\"p\":\"");
			w.write(this.getNodeCxtPathIn(innode));
		}
		ValTP vtp = getValTp();
		if (bvalid)
		{
			if (vtp.isNumberVT() || vtp == ValTP.vt_bool)
				w.write("\",\"valid\":" + bvalid + ",\"v\":" + strv + ",\"strv\":\"" + strv + "\",\"dt\":" + dt
						+ ",\"chgdt\":" + dt_chg );
			else
				w.write("\",\"valid\":" + bvalid + ",\"v\":\"" + strv + "\",\"strv\":\"" + strv + "\",\"dt\":" + dt
						+ ",\"chgdt\":" + dt_chg );
		}
		else
		{
			w.write("\",\"valid\":" + bvalid + ",\"v\":null,\"dt\":" + dt + ",\"chgdt\":" + dt_chg + ",\"err\":"
					+JSONObject.quote(str_err) );
//			w.write("\",\"valid\":" + bvalid + ",\"v\":null,\"dt\":" + dt + ",\"chgdt\":" + dt_chg + ",\"err\":\""
//					+ Convert.plainToJsStr(str_err)+"\"" );
		}

		JSONObject jo = getExtAttrJO() ;
		if(jo!=null)
		{
			w.write(",\"ext\":" + jo.toString() );
		}
		
		w.write("}");
		//bchged = true;
	}
}
