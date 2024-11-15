package org.iottree.core.msgnet.modules;

import java.util.List;

import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.store.Source;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class RelationalDB_M extends MNModule
{
	String sourceName = null ;
	
	@Override
	public String getTP()
	{
		return "r_db";
	}

	@Override
	public String getTPTitle()
	{
		return g("r_db");
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "\\uf1c0";
	}

	@Override
	public String getPmTitle()
	{
		SourceJDBC sj = getSourceJDBC() ;
		if(sj==null)
			return "no source jdbc";
		return sj.getDBInf() ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.sourceName))
		{
			failedr.append("no jdbc source name") ;
			return false;
		}
		SourceJDBC sorjdbc = getSourceJDBC() ;
		if(sorjdbc==null)
		{
			failedr.append("no jdbc source found with name="+this.sourceName) ;
			return false;
		}
		return true ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("sor_name",this.sourceName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.sourceName = jo.optString("sor_name") ;
	}
	
	public String getSourceName()
	{
		return this.sourceName ;
	}

	public SourceJDBC getSourceJDBC()
	{
		if(Convert.isNullOrEmpty(this.sourceName))
			return null ;
		Source sor = StoreManager.getSourceByName(this.sourceName) ;
		if(sor==null || !(sor instanceof SourceJDBC))
			return null ;
		return (SourceJDBC)sor;
	}
	
}
