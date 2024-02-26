package org.iottree.core.store;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.store.StoreHandler.TagsFilter;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.json.JSONArray;
import org.json.JSONObject;

public class StoreHandlerRT extends StoreHandler
{
	public static final String TP = "rt" ;
	
	boolean filterAll = true ;
	
	List<String> prefixs = new ArrayList<>() ;
	
	//Time Based Limit TODO
	
	//Trigger condition  TODO
	
	@Override
	public String getTp()
	{
		return TP;
	}

	@Override
	public String getTpTitle()
	{
		return "Tag Runtime Data Handler";
	}
	
	public boolean isFilterAll()
	{
		return this.filterAll ;
	}
	
	public void setFilter(boolean b_all,List<String> prefix)
	{
		this.filterAll = b_all ;
		this.prefixs = new ArrayList<>() ;
		if(prefix!=null)
			this.prefixs.addAll(prefix) ;
	}

	public void setFilterPrefixs(List<String> prefixs)
	{
		this.prefixs.addAll(prefixs);
	}
	
	public List<String> getFilterPrefixs()
	{
		return this.prefixs ;
	}
	
	public String getFilterPrefixStr()
	{
		if(this.prefixs==null)
			return "" ;
		return Convert.combineStrWith(this.prefixs, "\r\n") ;
	}

	@Override
	public boolean checkFilterFit(UATag tag)
	{
		if(filterAll)
			return true;
		
		if(prefixs==null||prefixs.size()<=0)
			return false;
		UAPrj prj = tag.getBelongToPrj() ;
		String np = tag.getNodeCxtPathTitleIn(prj) ;
		for(String p:prefixs)
		{
			if(np.startsWith(p))
				return true ;
		}
		return false;
	}
	
	public JSONObject toJO() //throws Exception
	{
		JSONObject jo = super.toJO() ;
		jo.put("filter_all", this.filterAll) ;
		JSONArray jarr = new JSONArray(this.prefixs) ;
		jo.put("filter_prefixs", jarr) ;
		return jo ;
	}
	
	public void fromJO(JSONObject jo,boolean include_sel_tagid,boolean include_out) throws Exception
	{
		super.fromJO(jo,include_sel_tagid,include_out);
		
		this.filterAll = jo.optBoolean("filter_all",true) ;
		Object fps = jo.opt("filter_prefixs");
		if(fps instanceof String)
		{
			String str = (String)fps ;
			this.prefixs = Convert.splitStrWith(str, ",\r\n") ;
		}
		else if(fps instanceof JSONArray)
		{
			this.prefixs = new ArrayList<>();
			JSONArray jarr = (JSONArray) fps ;
			this.prefixs = new ArrayList<>();
			int c = jarr.length() ;
			for(int i = 0 ; i < c ; i ++)
				this.prefixs.add(jarr.getString(i)) ;
		}
	}
	
	//DataTable rtDataTb = null ;
	
	@Override
	protected void RT_runInLoop()
	{
		
		//this.rtDataTb = dt ;
		
		super.RT_runInLoop();
		//dt.synToDBTable(conn, tablename, uniquecol, syncols, bdel)
	}
}
