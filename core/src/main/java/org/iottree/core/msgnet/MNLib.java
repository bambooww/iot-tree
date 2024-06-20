package org.iottree.core.msgnet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public class MNLib
{
	public static class Item implements Comparable<Item>
	{
		String id ;
		
		String title ;
		
		JSONObject pmjo ;
		
		long dt = -1;
		
		public Item(String id,String tt,JSONObject pmjo)
		{
			this.id = id ;
			this.title = tt ;
			this.pmjo = pmjo ;
		}
		
		private Item()
		{}
		
		public String getId()
		{
			return this.id ;
		}
		
		public String getTitle()
		{
			return this.title ;
		}
		
		public JSONObject getPmJO()
		{
			return this.pmjo ;
		}
		
		public long getDT()
		{
			return this.dt ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("id", this.id) ;
			jo.putOpt("title", this.title) ;
			jo.putOpt("pmjo", this.pmjo) ;
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			this.id = jo.optString("id") ;
			this.title = jo.optString("title") ;
			this.pmjo = jo.optJSONObject("pmjo") ;
		}

		@Override
		public int compareTo(Item o)
		{
			return (int)(o.dt-this.dt);
		}
	}
	
	private static File libDir = null ;
	
	static
	{
		libDir = new File(Config.getDataDirBase()+"/msg_net/lib/") ;
		if(!libDir.exists())
			libDir.mkdirs() ;
	}
	
	private static HashMap<String,List<Item>> dname2items = new HashMap<>() ;
	
	public static void saveTo(String mn,String tp,String title,JSONObject pmjo) throws IOException
	{
		MNBase ib = MNManager.getItemByFullTP(mn,tp) ;
		if(ib==null)
		{
			throw new IOException("no item found with mn="+mn+" tp="+tp) ;
		}
		String dname = mn+"_"+tp ;
		
		File tpdir = new File(libDir,dname+"/") ;
		if(!tpdir.exists())
			tpdir.mkdirs() ;
		
		String newid = IdCreator.newSeqId() ;
		File f = new File(tpdir,newid+".json") ;
		Item item = new Item(newid,title,pmjo) ;
		Convert.writeFileJO(f, item.toJO());
		
		List<Item> items = dname2items.get(dname) ;
		if(items!=null)
			items.add(0, item);
	}
	
	
	
	public static List<Item> listItems(String mn,String tp) 
	{
		String dname = mn+"_"+tp ;
		List<Item> items = dname2items.get(dname) ;
		if(items!=null)
			return items ;
		
		File tpdir = new File(libDir,dname+"/") ;
		if(!tpdir.exists())
			return null ;
		
		ArrayList<Item> rets  = new ArrayList<>() ;
		for(File f:tpdir.listFiles())
		{
			try
			{
				JSONObject tmpjo = Convert.readFileJO(f) ;
				if(tmpjo==null)
					continue ;
				Item item = new Item();
				item.fromJO(tmpjo);
				item.dt = f.lastModified() ;
				rets.add(item) ;
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		Collections.sort(rets);
		dname2items.put(dname,rets) ;
		return rets ;
	}
	
	public static Item getItemById(String mn,String tp,String id)
	{
		List<Item> items = listItems(mn,tp) ;
		if(items==null)
			return null ;
		for(Item item:items)
		{
			if(item.getId().equals(id))
				return item ;
		}
		return null ;
	}
}
