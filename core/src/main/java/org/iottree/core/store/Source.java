package org.iottree.core.store;

import org.iottree.core.basic.ce.ExchgObj;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
@data_class
public abstract class Source extends ExchgObj
{
	@data_val(param_name = "id")
	String id = null ;
	
	@data_val(param_name = "name")
	String name = null ;
	
	@data_val
	String title = null ;
	
	@data_val(param_name = "enable")
	boolean bEnable = true ;
	
	@data_val(param_name = "desc")
	String desc = "" ;
	
	public Source()
	{
		this.id = CompressUUID.createNewId();
	}
	
//	public Store(String n,String t)
//	{
//		this.id = CompressUUID.createNewId();
//		this.name = n ;
//		this.title = t ;
//	}
	
	public String getId()
	{
		return id ;
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

	public String getDesc()
	{
		return this.desc ;
	}
	
	public abstract String getSorTp() ;
	
	public abstract String getSorTpTitle() ;
	
	
	public abstract boolean checkValid(StringBuilder failedr) ;
	
	public abstract boolean checkConn(StringBuilder failedr) ;
	
	public JSONObject toListJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id);
		jo.put("n", this.name);
		jo.putOpt("t", this.title);
		jo.putOpt("d", this.desc);
		jo.put("en", this.bEnable) ;
		jo.put("tp", this.getSorTp()) ;
		jo.put("tpt", this.getSorTpTitle()) ;
		return jo ;
	}
	

	static Source newInsByTp(String tp)
	{
		switch(tp)
		{
		case "jdbc":
			return new SourceJDBC() ;
		case "influxdb":
			return new SourceInfluxDB();
		case "iotdb":
			return new SourceIoTDB() ;
		default:
			return null ;
		}
	}
	

	@Override
	public String getExchgName()
	{
		return this.getName();
	}
	

	@Override
	public String getExchgTitle()
	{
		return this.getTitle();
	}

	@Override
	protected void setExchgBasic(String tp,String name,String title)
	{
		this.name = name ;
		this.title = title ;
	}
}
