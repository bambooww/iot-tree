package org.iottree.core.ext;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.task.Task;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;

public class AttrManager
{
	private static AttrManager instance = null ;
	
	public static AttrManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(AttrManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new AttrManager() ;
			return instance ;
		}
	}
	

	private HashMap<String,List<AttrItem>> prj2props = new HashMap<>();
	
	
	
	private AttrManager()
	{}
	
	public List<AttrItem> getPropItems(String prjid)
	{
		List<AttrItem> ts = prj2props.get(prjid) ;
		if(ts!=null)
			return ts ;
		
		synchronized(AttrItem.class)
		{
			ts = prj2props.get(prjid) ;
			if(ts!=null)
				return ts ;
			
			try
			{
				ts =  loadPropItems(prjid) ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if(ts==null)
				ts = new ArrayList<>(0);
			prj2props.put(prjid, ts);
			return ts ;
		}
		
	}
	
//	public Task getTask(String prjid,String id)
//	{
//		List<Task> ts = getTasks(prjid);
//		if(ts==null)
//			return null ;
//		for(Task t:ts)
//		{
//			if(t.getId().equals(id))
//				return t ;
//		}
//		return null ;
//	}
	
	
	private List<AttrItem> loadPropItems(String prjid) throws Exception
	{
		 File  prjdir = UAManager.getPrjFileSubDir(prjid);
		 if(!prjdir.exists())
			 return null ;
		 File[] tfs = prjdir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(f.isDirectory())
					return false;
				String fn = f.getName().toLowerCase() ;
				return fn.startsWith("task_")&&fn.endsWith(".xml") ;
			}}) ;
		 if(tfs==null||tfs.length<=0)
			 return null ;
		 
		 ArrayList<AttrItem> rets = new ArrayList<>() ;
//		 for(File tf:tfs)
//		 {
//			 XmlData xd = XmlData.readFromFile(tf);
//			 if(xd==null)
//				 continue;
//			 Task jst=new Task(prjid);
//			 if(!DataTranserXml.injectXmDataToObj(jst, xd))
//				 continue;
//			 rets.add(jst);
//		 }
		 return rets;
	}
}
