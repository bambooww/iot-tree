package org.iottree.core.store.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.store.Source;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.tssdb.TSSTagSegs;
import org.iottree.core.store.tssdb.TSSValPt;
import org.iottree.core.store.tssdb.TSSValSeg;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 包含存储支持的数据处理器
 * 
 * @author jason.zhu
 *
 */
public abstract class RecPro
{
	RecManager belongTo = null ;
	
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	
	boolean bEnable = true ;
	
	/**
	 * using data source name
	 * if null then using inner default
	 */
	String sorName = null ;
	
	private static Source dataSor = null ;
	
	public RecPro()
	{
		this.id = IdCreator.newSeqId() ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		if(Convert.isNullOrEmpty(this.title))
			return name ;
		return title ;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public String getSorName()
	{
		if(sorName==null)
			return "" ;
		return sorName ;
	}
	
	public abstract String getTp() ;
	
	public abstract String getTpTitle() ;
	
	public abstract String getTpDesc() ;
	
	
	//public abstract List<String> getSupportedSavers() ;
	
	public abstract List<RecShower>  getSupportedShowers() ;
	
	protected Source getSaverSource()
	{
		//return StoreManager.getSourceByName(this.sorName) ;// TODO 使用外部数据源，可能会使项目启动慢
		if(Convert.isNullOrEmpty(sorName) || "_".equals(sorName))
		{// inner source
			
		}
		return StoreManager.getInnerSource(this.belongTo.prj.getName()+".recdb") ;
	}
	
	
	protected final String calTableName(String suffix)
	{
		if(Convert.isNotNullEmpty(suffix))
			return "recpro_"+getTp()+"_"+this.getName()+"_"+suffix ;
		else
			return "recpro_"+getTp()+"_"+this.getName() ;
	}
	
	protected abstract RecPro newInstance() ; 

	abstract void clearCache() ;
	//-- rt
	
	private transient boolean rtOk = false;
	private transient long rtErrDT = -1 ;
	private transient String rtErr = null ;
	
	
	public final boolean RT_init(StringBuilder failedr)
	{
		this.rtOk=  false;
		
		if(!RT_initSaver(failedr))
		{
			rtErr = failedr.toString() ;
			rtErrDT = System.currentTimeMillis() ;
			return false;
		}
		
		if(!RT_initPro(failedr))
		{
			rtErr = failedr.toString() ;
			rtErrDT = System.currentTimeMillis() ;
			return false;
		}
		
		this.rtOk=  true;
		rtErrDT = -1 ;
		rtErr = null ;
		
		return true ;
	}
	
	public final boolean RT_isOk()
	{
		return this.rtOk ;
	}
	
	public final long RT_getErrDT()
	{
		return this.rtErrDT ;
	}
	
	public final String RT_getErrInf()
	{
		return this.rtErr ;
	}

	protected abstract boolean RT_initSaver(StringBuilder failedr) ;
	
	protected abstract boolean RT_initPro(StringBuilder failedr) ;
	
	public JSONObject RT_getInf()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("b_ok", this.rtOk) ;
		jo.put("err_dt", this.rtErrDT) ;
		jo.putOpt("err", this.rtErr) ;
		return jo ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id) ;
		jo.put("n", this.name);
		jo.putOpt("t", this.title) ;
		jo.put("en", bEnable) ;
		jo.put("tp", this.getTp()) ;
		jo.putOpt("sor", sorName) ;
		//JSONArray jarr = new JSONArray(this.selTagIds) ;
		
		return jo ;
	}
	
	
	protected boolean fromJO(JSONObject jo,StringBuilder failed)
	{
		this.id = jo.getString("id") ;
		this.name = jo.getString("n") ;
		this.title = jo.optString("t") ;
		this.bEnable = jo.optBoolean("en",true) ;
		this.sorName = jo.optString("sor") ;
		
		return true ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = toJO() ;
		jo.put("tpt", this.getTpTitle()) ;
		jo.putOpt("tpd", this.getTpDesc()) ;
		//this.belongTo.getRecTagParam(tag)
		//jo.put("sel_tags", value)
		return jo ;
	}
	
	
	private static HashMap<String,RecPro> TP2PRO = new HashMap<>() ;
	
	private static List<RecProL1> ALL_L1 = new ArrayList<>() ;
	
	private static List<RecProL2> ALL_L2 = new ArrayList<>() ;
	
	private static void regRecPro(RecPro rp)
	{
		TP2PRO.put(rp.getTp(), rp) ;
		if(rp instanceof RecProL1)
			ALL_L1.add((RecProL1)rp) ;
		
		if(rp instanceof RecProL2)
			ALL_L2.add((RecProL2)rp) ;
	}
	
	static
	{
		regRecPro(new RecProL1DValue()) ;
		regRecPro(new RecProL1JmpChg()) ;
		regRecPro(new RecProL1MutPts()) ;
		regRecPro(new RecProL1StablePts()) ;
	}
	
	public static RecPro fromJO(RecManager recm,JSONObject jo,StringBuilder failedr)
	{
		String tp = jo.getString("tp") ;
		RecPro rp = TP2PRO.get(tp) ;
		if(rp==null)
		{
			failedr.append("unknown pro tp="+tp) ;
			return null ;
		}
		
		rp = rp.newInstance() ;
		if(!rp.fromJO(jo, failedr))
			return null ;
		rp.belongTo = recm ;
		return rp ;
	}
	
	public static List<RecProL1> listProL1()
	{
		return ALL_L1;
	}
	
	public static List<RecProL2> listProL2()
	{
		return ALL_L2;
	}
}
