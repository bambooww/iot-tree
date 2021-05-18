package org.iottree.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.util.xmldata.data_obj;

public class StoreManager
{

	@data_obj(obj_c = Store.class)
	List<Store> stores = new ArrayList<>();
	
	private HashMap<String,List<Store>> repid2cps = new HashMap<>() ;

	public List<Store> getStores(String repid)
	{
		return repid2cps.get(repid);
	}

	public Store getStoreById(String repid,String id)
	{
		List<Store> sts = getStores(repid) ;
		if(sts==null)
			return null ;
		for (Store ch : sts)
		{
			if (id.contentEquals(ch.getId()))
				return ch;
		}
		return null;
	}

	public Store getStoreByName(String n)
	{
		for (Store ch : stores)
		{
			if (n.contentEquals(ch.getName()))
				return ch;
		}
		return null;
	}

	public Store addStore(String tp, String name, String title, String desc, HashMap<String, Object> uiprops)
			throws Exception
	{
		UAUtil.assertUAName(name);

		Store ch = getStoreByName(name);
		if (ch != null)
		{
			throw new IllegalArgumentException("store with name=" + name + " existed");
		}
		ch = new Store(name, title, desc, tp);
		
		// ch.belongTo = this;
		stores.add(ch);
		
		save();
		return ch;
	}

	void delStore(Store ch) throws Exception
	{
		stores.remove(ch);
		save();
	}

	
	private void save()
	{
		
	}
}
