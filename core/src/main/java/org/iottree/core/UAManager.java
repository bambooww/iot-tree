package org.iottree.core;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import javax.servlet.jsp.JspWriter;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.*;

import com.google.common.eventbus.EventBus;
import static com.google.common.base.Preconditions.*;

public class UAManager
{
	private static UAManager instance = null ;
	
	public static UAManager getInstance()
	{
		if(instance!=null)
			return instance ;
		synchronized(UAManager.class)
		{
			if(instance!=null)
				return instance ;
			instance = new UAManager() ;
			return instance ;
		}
	}
	
	private ArrayList<UARep> reps = null;
	
	private EventBus eventBus = new EventBus("ua");
	
	private UAManager()
	{
		reps = loadReps();
		if(reps==null)
			reps = new ArrayList<>() ;
		
		constrcuctTree();
	}
	
	private void constrcuctTree()
	{
		for(UARep r:reps)
			r.constructNodeTree();
	}
	
	public EventBus getEventBus()
	{
		return eventBus;
	}
	
	private ArrayList<UARep> loadReps()
	{
		String dirf = Config.getDataDirBase() + "/ua/reps/";
		File df = new File(dirf);
		if (!df.exists())
			return null;

		File[] fs = df.listFiles(new FileFilter()
		{
			public boolean accept(File f)
			{
				String fn = f.getName();
				return fn.startsWith("rep_") && (fn.endsWith(".xml"));
			}
		});
		
		ArrayList<UARep> reps = new ArrayList<>();
		if (fs != null)
		{
			for (File tmpf : fs)
			{
				String tmpfn = tmpf.getName() ;
				String id = tmpfn.substring(4);
				id = id.substring(0,id.length()-4) ;
				
				try
				{
					UARep dc = loadRep(id);
					if(dc==null)
						continue ;
					reps.add(dc);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return reps;
	}
	
	public List<UARep> listReps()
	{
		return reps;
	}
	
	public UARep getRepById(String id)
	{
//		if(true)
//		{
//			try
//			{
//				UARep rep =  loadRep(id);
//				if(rep!=null)
//					rep.constructNodeTree();
//				return rep;
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				return null ;
//			}
//		}
		
		for(UARep r:reps)
		{
			if(r.getId().contentEquals(id))
				return r ;
		}
		return null ;
	}
	
	public UARep getRepByName(String name)
	{
		for(UARep r:reps)
		{
			if(name.contentEquals(r.getName()))
				return r ;
		}
		return null ;
	}
	
	/**
	 * get default repository
	 * @return
	 */
	public UARep getRepDefault()
	{
		if(reps.size()<=0)
			return null ;
		return reps.get(0) ;
	}
	
	static File getRepFileSubDir(String id)
	{
		String fp = Config.getDataDirBase() + "/ua/reps/rep_"+id+"/";
		return new File(fp) ;
	}
	
	static File getRepFile(String id)
	{
		String fp = Config.getDataDirBase() + "/ua/reps/rep_"+id+".xml";
		return new File(fp);
	}
	
	UARep loadRep(String id) throws Exception
	{
		File tmpf = getRepFile(id);
		if(!tmpf.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(tmpf);
		UARep r = new UARep() ;
		DataTranserXml.injectXmDataToObj(r, tmpxd);
		//r.fromUAXmlData(tmpxd);
		r.onLoaded();
		return r ;
	}
	
	void saveRep(UARep rep) throws Exception
	{
		XmlData xd = DataTranserXml.extractXmlDataFromObj(rep) ;
		//XmlData xd = rep.toUAXmlData();
		XmlData.writeToFile(xd, getRepFile(rep.getId()));
	}
	
	public void delRep(String id)
	{
		File f = getRepFile(id) ;
		if(f.exists())
		{
			f.delete();
		}
		File df = getRepFileSubDir(id) ;
		if(df.exists())
		{
			deleteDir(df) ;
		}
	}
	private static boolean deleteDir(File dir) {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
	
	public UARep addRep(String name,String title,String desc) throws Exception
	{
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(name,sb))
			throw new IllegalArgumentException(sb.toString()) ;
		
		UARep r = this.getRepByName(name) ;
		if(r!=null)
		{
			throw new Exception("name="+name+" is existed!") ;
		}
		r = new UARep(name,title,desc) ;
		saveRep(r);
		reps.add(r);
		return r ;
	}
	
	
	public UANode findNodeByPath(String path)
	{
		LinkedList<String> ss = Convert.splitStrWithLinkedList(path, "/\\.") ;
		String n = ss.removeFirst() ;
		UARep rep = this.getRepByName(n) ;
		if(rep==null)
			return null ;
		return rep.getDescendantNodeByPath(ss) ;
	}
	
	public UANode findNodeById(String id)
	{
		for(UARep rep:this.listReps())
		{
			UANode n = rep.findNodeById(id) ;
			if(n!=null)
				return n ;
		}
		return null ;
	}
	
	Thread uaMonTh = null;
	
	Runnable uaMonRunner = new Runnable()
			{

				@Override
				public void run()
				{
					while(uaMonTh!=null)
					{
						try
						{
							Thread.sleep(3000);
						}
						catch(Exception e)
						{
							
						}
						finally
						{
							uaMonTh = null ;
						}
					}
				}
		
			};
	
	
	/**
	 * auto start when server is starting
	 */
	synchronized public void start()
	{
		if(uaMonTh!=null)
			return ;
		
		uaMonTh = new Thread(uaMonRunner) ;
		uaMonTh.start();
	}
	
	synchronized public void stop()
	{
		uaMonTh=null;
	}
	

	public void renderRTJson(String repid,JspWriter out) throws Exception
	{
		UARep rep = this.getRepById(repid) ;
		if(rep==null)
			throw new Exception("no rep found") ;
		List<UACh> chs = rep.getChs() ;
		out.print("[") ;
		boolean bf = true;
		for(UACh ch:chs)
		{
			if(bf) bf=false;
			else out.print(",") ;
			boolean br = ch.RT_getState().isRunning();
			out.print("{\"ch_id\":\""+ch.getId()+"\",\"run\":"+br+",\"devs\":[");
			
			List<UADev> devs = ch.getDevs() ;
			boolean bf1=true;
			for(UADev dev:devs)
			{
				if(bf1) bf1=false;
				else out.print(",") ;
				
				out.print("{\"dev_id\":\""+dev.getId()+"\",\"run_ok\":"+dev.RT_runOk()+"}");
			}
			
			out.print("]}");
		}
		out.print("]");
	}
}
