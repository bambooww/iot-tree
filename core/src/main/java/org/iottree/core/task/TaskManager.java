package org.iottree.core.task;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;

public class TaskManager
{
	private static TaskManager instance = null ;
	
	public static TaskManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(TaskManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new TaskManager() ;
			return instance ;
		}
	}
	
	
	
	private HashMap<String,List<Task>> prj2tasks = new HashMap<>();
	
	private TaskManager()
	{}
	
	
	public List<Task> getTasks(String prjid)
	{
		List<Task> ts = prj2tasks.get(prjid) ;
		if(ts!=null)
			return ts ;
		
		synchronized(Task.class)
		{
			ts = prj2tasks.get(prjid) ;
			if(ts!=null)
				return ts ;
			
			try
			{
				ts =  loadJsTasks(prjid) ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if(ts==null)
				ts = new ArrayList<>(0);
			prj2tasks.put(prjid, ts);
			return ts ;
		}
		
	}
	
	public Task getTask(String prjid,String id)
	{
		List<Task> ts = getTasks(prjid);
		if(ts==null)
			return null ;
		for(Task t:ts)
		{
			if(t.getId().equals(id))
				return t ;
		}
		return null ;
	}
	
	private File getTaskFile(String prjid,String taskid)
	{
		File  prjdir = UAManager.getPrjFileSubDir(prjid);
		return new File(prjdir,"task_"+taskid+".xml") ;
	}
	
	private List<Task> loadJsTasks(String prjid) throws Exception
	{
		UAPrj p = UAManager.getInstance().getPrjById(prjid) ;
		if(p==null)
			throw new Exception("no prj found") ;
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
		 
		 ArrayList<Task> rets = new ArrayList<>() ;
		 for(File tf:tfs)
		 {
			 XmlData xd = XmlData.readFromFile(tf);
			 if(xd==null)
				 continue;
			 Task jst=new Task(p);
			 if(!DataTranserXml.injectXmDataToObj(jst, xd))
				 continue;
			 rets.add(jst);
		 }
		 return rets;
	}
	
	private void saveTask(String prjid,Task t) throws Exception
	{
		XmlData xd = DataTranserXml.extractXmlDataFromObj(t) ;
		//XmlData xd = rep.toUAXmlData();
		File tf = getTaskFile(prjid,t.getId()) ;
		XmlData.writeToFile(xd, tf);
	}
	
	synchronized public Task setTask(String prjid,Task jst) throws Exception
	{
		saveTask(prjid,jst);
		List<Task> ts = getTasks(prjid);
		int s = ts.size() ;
		for(int i = 0 ; i < s ; i ++)
		{
			Task t = ts.get(i);
			if(t.getId().equals(jst.getId()))
			{
				jst.actions = t.actions ;
				ts.set(i,jst) ;
				return jst ;
			}
		}
		
		ts.add(jst) ;
		return jst ;
	}
	
	synchronized public boolean delTask(String prjid,String id)
	{
		Task t = getTask(prjid,id) ;
		if(t==null)
			return false;
		File tf = getTaskFile(prjid,id) ;
		if(!tf.delete())
			return false;
		List<Task> ts = getTasks(prjid);
		ts.remove(t);
		return true ;
	}
	
	public void setTaskAction(String prjid,String taskid,TaskAction ta) throws Exception
	{
		Task t = getTask(prjid,taskid) ;
		if(t==null)
			throw new Exception("no task found") ;
		t.setAction(ta) ;
		
		this.saveTask(prjid, t);
	}
	
	public boolean delTaskAction(String prjid,String taskid,String actid) throws Exception
	{
		Task t = getTask(prjid,taskid) ;
		if(t==null)
			return false;
		return t.delAction(actid)!=null ;
	}
}
