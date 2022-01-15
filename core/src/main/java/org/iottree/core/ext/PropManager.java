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

public class PropManager
{
	private static PropManager instance = null ;
	
	public static PropManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(PropManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new PropManager() ;
			return instance ;
		}
	}
	

	private HashMap<String,List<PropItem>> prj2props = new HashMap<>();
	
	
	
	private PropManager()
	{}
	
	public List<PropItem> getPropItems(String prjid)
	{
		List<PropItem> ts = prj2props.get(prjid) ;
		if(ts!=null)
			return ts ;
		
		synchronized(PropItem.class)
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
	
	
	private List<PropItem> loadPropItems(String prjid) throws Exception
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
		 
		 ArrayList<PropItem> rets = new ArrayList<>() ;
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
