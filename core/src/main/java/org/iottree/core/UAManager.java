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
	
	private ArrayList<UAPrj> reps = null;
	
	private EventBus eventBus = new EventBus("ua");
	
	private UAManager()
	{
		reps = loadPrjs();
		if(reps==null)
			reps = new ArrayList<>() ;
		
		constrcuctTree();
	}
	
	private void constrcuctTree()
	{
		for(UAPrj r:reps)
			r.constructNodeTree();
	}
	
	public EventBus getEventBus()
	{
		return eventBus;
	}
	
	private ArrayList<UAPrj> loadPrjs()
	{
		String dirf = Config.getDataDirBase() + "/prjs/";
		File df = new File(dirf);
		if (!df.exists())
			return null;

		File[] fs = df.listFiles(new FileFilter()
		{
			public boolean accept(File f)
			{
				String fn = f.getName();
				return fn.startsWith("prj_") && (fn.endsWith(".xml"));
			}
		});
		
		ArrayList<UAPrj> reps = new ArrayList<>();
		if (fs != null)
		{
			for (File tmpf : fs)
			{
				String tmpfn = tmpf.getName() ;
				String id = tmpfn.substring(4);
				id = id.substring(0,id.length()-4) ;
				
				try
				{
					UAPrj dc = loadRep(id);
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
		
		Collections.sort(reps, new Comparator<UAPrj>() {

		    @Override
		    public int compare(UAPrj o1, UAPrj o2) {
		        long v = o1.getSavedDT()-o2.getSavedDT() ;
		        if(v>0)
		        	return -1 ;
		        else if(v<0)
		        	return 1 ;
		        return 0 ;
		    }
		});
		return reps;
	}
	
	public List<UAPrj> listPrjs()
	{
		return reps;
	}
	
	public UAPrj getPrjById(String id)
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
		
		for(UAPrj r:reps)
		{
			if(r.getId().contentEquals(id))
				return r ;
		}
		return null ;
	}
	
	public UAPrj getPrjByName(String name)
	{
		for(UAPrj r:reps)
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
	public UAPrj getPrjDefault()
	{
		if(reps.size()<=0)
			return null ;
		return reps.get(0) ;
	}
	
	static File getPrjFileSubDir(String id)
	{
		String fp = Config.getDataDirBase() + "/prjs/prj_"+id+"/";
		return new File(fp) ;
	}
	
	static File getPrjFile(String id)
	{
		String fp = Config.getDataDirBase() + "/prjs/prj_"+id+".xml";
		return new File(fp);
	}
	
	UAPrj loadRep(String id) throws Exception
	{
		File tmpf = getPrjFile(id);
		if(!tmpf.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(tmpf);
		UAPrj r = new UAPrj() ;
		DataTranserXml.injectXmDataToObj(r, tmpxd);
		//r.fromUAXmlData(tmpxd);
		r.onLoaded();
		return r ;
	}
	
	void saveRep(UAPrj rep) throws Exception
	{
		XmlData xd = DataTranserXml.extractXmlDataFromObj(rep) ;
		//XmlData xd = rep.toUAXmlData();
		XmlData.writeToFile(xd, getPrjFile(rep.getId()));
	}
	
	public void delRep(String id)
	{
		UAPrj prj = getPrjById(id);
		if(prj==null)
			return ;
		
		File f = getPrjFile(id) ;
		if(f.exists())
		{
			f.delete();
		}
		File df = getPrjFileSubDir(id) ;
		if(df.exists())
		{
			deleteDir(df) ;
		}
		
		this.reps.remove(prj) ;
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
	
	public UAPrj addRep(String name,String title,String desc) throws Exception
	{
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(name,sb))
			throw new IllegalArgumentException(sb.toString()) ;
		if("admin".equals(name))
		{
			throw new IllegalArgumentException("admin is reserved word.Please use another name") ;
		}
		UAPrj r = this.getPrjByName(name) ;
		if(r!=null)
		{
			throw new Exception("name="+name+" is existed!") ;
		}
		r = new UAPrj(name,title,desc) ;
		saveRep(r);
		reps.add(r);
		return r ;
	}
	
	
	public UANode findNodeByPath(String path)
	{
		LinkedList<String> ss = Convert.splitStrWithLinkedList(path, "/\\.") ;
		String n = ss.removeFirst() ;
		UAPrj rep = this.getPrjByName(n) ;
		if(rep==null)
			return null ;
		return rep.getDescendantNodeByPath(ss) ;
	}
	
	public UANode findNodeById(String id)
	{
		for(UAPrj rep:this.listPrjs())
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
		UAPrj rep = this.getPrjById(repid) ;
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
