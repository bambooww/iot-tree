package org.iottree.core.store;

import org.iottree.core.UAPrj;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONArray;
import org.json.JSONObject;

@data_class
public abstract class StoreOut
{
//	public static final String[] TPS = new String[] {"jdbc_tb"};//{"ui","js"} ;
//	public static final String[] TP_TITLES = new String[] {"Table"}; // {"UI","JS"} ;
	
	static StoreOut newInsByTp(String tp)
	{
		StoreOut ao = null ;
		switch(tp)
		{
		case StoreOutTb.TP:
			ao = new StoreOutTb() ;
			break ;
		case StoreOutTbHis.TP:
			ao = new StoreOutTbHis() ;
			break ;
//		case AlertOutUI.TP:
//			ao = new AlertOutUI() ;
//			break ;
		}
		return ao ;
	}
	
	public static StoreOut[] OUTS = new StoreOut[] {new StoreOutTb(),new StoreOutTbHis()};
	
	@data_val
	String id = null ;
	
	@data_val(param_name = "n")
	String name = null ;
	
	@data_val(param_name = "t")
	String title = null ;
	
	@data_val(param_name = "en")
	boolean bEnable = true ;
	
	transient StoreHandler belongTo = null ;
	
	public StoreOut()
	{
		this.id = CompressUUID.createNewId() ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public StoreHandler getBelongTo()
	{
		return this.belongTo ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public abstract String getOutTp() ;
	
	public abstract String getOutTpTitle() ;
	
	public abstract boolean checkValid(StringBuilder failedr) ;
	
	protected abstract boolean initOutInner(StringBuilder failedr) ;
	
	public abstract boolean isStoreHistory() ;
	
	boolean initOk = false;
	
	public boolean initOut(StringBuilder failedr)
	{
		if(!checkValid(failedr))
			return false;
		
		if(initOutInner(failedr))
		{
			initOk = true ;
			return true ;
		}
		return false ;
	}
	
	public boolean checkOrInitOk(StringBuilder failedr)
	{
		if(initOk)
			return true ;
		return initOut(failedr) ;
	}
	
	public boolean isInitOk()
	{
		return this.initOk ;
	}
	
	//will be call before run in loop
	protected abstract boolean RT_initInner(StringBuilder failedr) throws Exception;
	
	protected abstract void RT_runInLoop() throws Exception ;
	
	boolean rtInitOk = false;
	boolean rtRunOk = false;
	String rtErrorInfo = null ;
	
	boolean RT_init(StringBuilder failedr) throws Exception
	{
		if(RT_initInner(failedr))
		{
			rtInitOk = true ;
			return true ;
		}
		
		return false;
	}
	
	public boolean RT_isInitOk()
	{
		return this.rtInitOk ;
	}
	
	public boolean RT_isRunOk()
	{
		return this.rtRunOk ;
	}
	
	public String RT_getErrorInfo()
	{
		return this.rtErrorInfo ;
	}
	
	public JSONObject toJO() // throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		jo.put("tp", this.getOutTp()) ;
		jo.put("tpt", this.getOutTpTitle()) ;
		return jo ;
	}
	
	JSONObject RT_toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id);
		jo.putOpt("n", this.name) ;
		jo.put("init_ok",this.RT_isInitOk()) ;
		jo.put("run_ok",this.RT_isRunOk()) ;
		jo.putOpt("err", Convert.plainToHtml(this.RT_getErrorInfo(),true,false));
		return jo ;
	}
}
