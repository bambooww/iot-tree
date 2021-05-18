package org.iottree.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.script.ScriptException;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.*;
import org.iottree.core.util.xmldata.XmlVal.*;
import org.graalvm.polyglot.Value;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;
import org.json.JSONObject;

@data_class
public class UATag extends UANode implements IOCDyn //UANode UABox
{
	transient UANodeOCTags belongToNode = null ;
	
	//transient  UATagG parentTagG = null ;
	//transient UATagList belongToTL = null ;
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
	
	/**
	 * save val his, to support 
	 */
	private transient UAValList valsCacheList = null ;
	
	public UATag()
	{
		super();
	}
	
	public UATag(String name,String title,String desc,String addr,UAVal.ValTP vt,boolean canwrite,long srate)
	{
		super(name,title,desc) ;
		this.addr = addr ;
		this.valTp = vt ;
		this.bCanWrite = canwrite ;
		this.scanRate = srate;
	}
	
	void setTagNor(String name,String title,String desc,String addr,UAVal.ValTP vt,boolean canwrite,long srate)
	{
		setNameTitle(name,title,desc) ;
		this.addr = addr ;
		this.valTp = vt ;
		this.bCanWrite = canwrite ;
		this.scanRate = srate;
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
	UATag(String name,String title,String desc,String addrexp,UAVal.ValTP vt)
	{
		super(name,title,desc) ;
		this.bMidExp = true ;
		this.addr = addrexp ;
		this.valTp = vt ;
		this.bCanWrite = false ;
		this.scanRate = -1;
	}
	
	void setTagMid(String name,String title,String desc,String addrexp,UAVal.ValTP vt)
	{
		setNameTitle(name,title,desc) ;
		this.bMidExp = true ;
		this.addr = addrexp ;
		this.valTp = vt ;
		this.bCanWrite = false ;
		this.scanRate = -1;
	}
	
//	public static UATag newMidExpTag(String name,String title,String desc,String addrexp,UAVal.ValTP vt)
//	{
//		
//	}
	
	/**
	 * 
	 * @param new_self create by copySelfWithNewId
	 */
	protected void copyTreeWithNewSelf(UANode new_self,String ownerid,boolean copy_id)
	{
		super.copyTreeWithNewSelf(new_self,ownerid, copy_id);
		UATag nt = (UATag)new_self ;
		nt.bMidExp = this.bMidExp;
		
		nt.addr = this.addr ;
		nt.valTp = this.valTp;
		nt.bCanWrite = this.bCanWrite ;
		nt.scanRate = this.scanRate ;
		
		nt.valTranser=this.valTranser;
		
		nt.valChgedCacheLen = this.valChgedCacheLen ;
	}
	
	public UANodeOCTags getBelongToNode()
	{
		return belongToNode;
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
		if(belongToNode instanceof UADev)
			return (UADev)belongToNode ;
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
		return addr ;
	}
	
	public DevAddr getDevAddr(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(addr))
			return null ;
		if(devAddr!=null)
			return devAddr;
		UAVal.ValTP vt = getValTp() ;
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
	
	
	
	public UAVal.ValTP getValTp()
	{
		return valTp ;
	}
	
	public long getScanRate()
	{
		return this.scanRate ;
	}
	
	public boolean isCanWrite()
	{
		return bCanWrite;
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
	
	private transient UAVal midVal = null ;
	
	
	
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
	public void CXT_calMidVal()
	{
		if(!this.isMidExpress())
			return ;
		UAVal v = midVal ;
		if(v==null)
			v = new UAVal() ;
		try
		{
			UACodeItem ci =  CXT_getMidCodeItem();
			if(ci==null)
			{
				v.setValErr("no express code item");
				return ;
			}
			Object ob = ci.runCode() ;
			v.setVal(true, ob, System.currentTimeMillis());
		}
		catch(Exception e)
		{
			v.setValException(null, e);
			e.printStackTrace();
		}
		finally
		{
			if(midVal==null)
				midVal = v ;
		}
	}
	
	/**
	 * 不考虑各种驱动、地址和型号，也不考虑mid
	 * 直接设置对应的值
	 * @param v
	 */
	public void RT_setVal(Object v)
	{
		midVal = new UAVal(true,v,System.currentTimeMillis()) ;
		HIS_setVal(midVal) ;
	}
	
	void HIS_setVal(UAVal v)
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
	
	public boolean RT_writeVal(Object v)
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
		
		
//		DevModel m = dev.getModel();
//		if(m==null)
//			return false;
//		if(v instanceof String)
//			return m.RT_writeValStr(da, (String)v);
//		
//		return m.RT_writeVal(da, v) ;
		// TODO call driver to write  
		return false;
	}
	
	
	public UAVal RT_getVal()
	{
		if(this.isMidExpress())
		{
			return midVal ;
		}
		else
		{
			StringBuilder sb = new StringBuilder() ;
			DevAddr da = this.getDevAddr(sb);
			UAVal r = null ;
			if(da!=null)
			{
				r = da.RT_getVal() ;
			}
			
			if(r==null)
				r = midVal ;
			return r ;
		}
	}
	
	
	public Object JS_get(String  key)
	{
		Object obj  = super.JS_get(key) ;
		if(obj!=null)
			return obj ;
		
		UAVal uav = this.RT_getVal() ;
		if(uav==null)
			return null ;
		
		switch(key.toLowerCase())
		{
		case "_pv":
			return uav.getObjVal() ;
		case "_valid":
			return uav.isValid() ;
		case "_updt":
			return uav.getValDT() ;
		}
		return null ;
	}
	
	
	static List<String> js_names = Arrays.asList("_pv","_valid","_updt") ;
	
	
	public List<Object> JS_names()
	{
		List<Object> rets = super.JS_names() ;
		rets.addAll(js_names);
		return rets ;
	}
	
	public Class<?> JS_type(String key)
	{
		switch(key.toLowerCase())
		{
		case "_pv":
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
			RT_setVal(v) ;
			break ;
		default:
			break ;//do nothing
		}
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
	
}
