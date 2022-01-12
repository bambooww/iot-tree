package org.iottree.core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.*;
import java.util.function.Predicate;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.jsp.JspWriter;

import org.iottree.core.basic.IdName;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.res.IResCxt;
import org.iottree.core.res.IResNode;
import org.iottree.core.res.ResDir;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ZipUtil;
import org.iottree.core.util.js.Debug;
import org.iottree.core.util.js.GSys;
import org.iottree.core.util.xmldata.*;

import com.google.common.eventbus.EventBus;
import static com.google.common.base.Preconditions.*;

public class UAManager implements IResCxt
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
	

	public static final String JS_NAME="graal.js";//"nashorn"; //

	public static ScriptEngine createJSEngine(UAPrj prj)
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(JS_NAME);
		engine.put("polyglot.js.allowHostAccess", true);
		engine.put("polyglot.js.allowAllAccess",true);
		engine.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
		
		engine.put("$sys", new GSys(prj));
		engine.put("$debug", new Debug());
		
		HashMap<String,Object> gvar2obj = PlugManager.getInstance().getJsApiAll();
		if(gvar2obj!=null)
		{
			for(Map.Entry<String, Object> n2o:gvar2obj.entrySet())
			{
				engine.put("$$"+n2o.getKey(), n2o.getValue());
			}
		}
		
		return engine ;
	}
	
	
	
	private ArrayList<UAPrj> reps = null;
	
	private EventBus eventBus = new EventBus("ua");
	
	private UAManager()
	{
		reps = loadPrjs();
		if(reps==null)
			reps = new ArrayList<>() ;
		updatePrjList();
		
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
					UAPrj dc = loadPrj(id);
					if(dc==null)
						continue ;
					reps.add(dc);
					dc.onLoaded();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return reps;
	}
	
	private void updatePrjList()
	{
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
	}
	
	public List<UAPrj> listPrjs()
	{
		return reps;
	}
	
	
	transient String defaultPrjId = null ;
	/**
	 * get default repository
	 * @return
	 * @throws IOException 
	 */
	public UAPrj getPrjDefault() throws IOException
	{
		if(defaultPrjId!=null)
		{
			if(Convert.isNullOrEmpty(defaultPrjId))
				return null ;
			return this.getPrjById(defaultPrjId);
		}
		
		File f = getPrjDefaultFile();
		if(!f.exists())
			return null ;
		
		String defid = Convert.readFileTxt(f, "utf-8");
		if(Convert.isNullOrEmpty(defid))
		{
			defaultPrjId = "" ;
		}
		else
		{
			defaultPrjId = defid ;
		}
		return this.getPrjById(defaultPrjId);
	}
	
	public void setPrjDefault(UAPrj prj) throws FileNotFoundException, IOException
	{
		String id = "";
		if(prj!=null)
		{
			id = prj.getId();
		}
		File f = getPrjDefaultFile();
		try(FileOutputStream fos = new FileOutputStream(f);)
		{
			fos.write(prj.getId().getBytes());
		}
		defaultPrjId = id ;
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
	
	
	
	static File getPrjDataDir()
	{
		return new File(Config.getDataDirBase() + "/prjs/") ;
	}
	
	public static File getPrjFileSubDir(String id)
	{
		String fp = Config.getDataDirBase() + "/prjs/prj_"+id+"/";
		return new File(fp) ;
	}
	
	public static File getPrjFile(String id)
	{
		String fp = Config.getDataDirBase() + "/prjs/prj_"+id+".xml";
		return new File(fp);
	}
	
	static File getPrjDefaultFile()
	{
		String fp = Config.getDataDirBase() + "/prjs/default.txt";
		return new File(fp);
	}
	
	UAPrj loadPrj(String id) throws Exception
	{
		File tmpf = getPrjFile(id);
		if(!tmpf.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(tmpf);
		UAPrj r = new UAPrj() ;
		DataTranserXml.injectXmDataToObj(r, tmpxd);
		//r.fromUAXmlData(tmpxd);
		
		return r ;
	}
	
	void savePrj(UAPrj prj) throws Exception
	{
		XmlData xd = DataTranserXml.extractXmlDataFromObj(prj) ;
		//XmlData xd = rep.toUAXmlData();
		XmlData.writeToFile(xd, getPrjFile(prj.getId()));
	}
	
	public void delPrj(String id)
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
			Convert.deleteDir(df) ;
		}
		
		this.reps.remove(prj) ;
	}
	
	
	public UAPrj addPrj(String name,String title,String desc) throws Exception
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
		savePrj(r);
		r.RT_init(true, false);
		reps.add(r);
		return r ;
	}
	
	public UAPrj updatePrj(String id,String name,String title,String desc) throws Exception
	{
		UAUtil.assertUAName(name);
		
		UAPrj prj = this.getPrjById(id) ;
		if(prj==null)
			throw new IllegalArgumentException("prj no existed") ;
		UAPrj tmpprj = this.getPrjByName(name) ;
		if(tmpprj!=null&&tmpprj!=prj)
			throw new IllegalArgumentException("prj with name="+name+" existed") ;
		prj.setNameTitle(name, title, desc);
		savePrj(prj) ;
		return prj ;
	}
	
	
	public boolean exportPrj(String prjid,File fout) throws IOException
	{
		UAPrj p = this.getPrjById(prjid) ;
		if(p==null)
			return false;
		
		List<File> fs = Arrays.asList(UAManager.getPrjFile(prjid),UAManager.getPrjFileSubDir(prjid)) ;
		HashMap<String,String> metam = new HashMap<>() ;
		metam.put("tp", "prj") ;
		metam.put("prjid", prjid) ;
		String metatxt=  Convert.transMapToPropStr(metam) ;
		
		ZipUtil.zipFileOut(metatxt,fs,fout) ;
		return true;
	}
	
	
	public boolean backupPrj(String prjid) throws IOException
	{
		String bkdir = Config.getDataDirBase()+"/backup/" ;
		return exportPrj(prjid,new File(bkdir,"prj_"+prjid+"_"+System.currentTimeMillis()+".zip")) ;
	}
	
	
	public File exportPrjToTmp(String prjid) throws IOException
	{
		String fn = prjid+".zip";
		File fout = new File(Config.getDataDirBase()+"/tmp/"+fn) ;
		if(exportPrj(prjid,fout))
			return fout ;
		return null;
	}
	
	public List<IdName> parsePrjZipFile(File zipf) throws Exception
	{
		return parsePrjZipFile(zipf,null);
	}
	
	private List<IdName> parsePrjZipFile(File zipf,String id) throws Exception
	{
		ArrayList<IdName> rets =new ArrayList<>() ;
		
		List<String> ens = ZipUtil.readZipEntrys(zipf) ;
		for(String en:ens)
		{
			if(!en.startsWith("prj_"))
				continue ;
			if(!en.endsWith(".xml"))
				continue ;
			if(en.indexOf("/")>0)
				continue;
			if(en.indexOf("\\")>0)
				continue;
			//find prj
			String prjid = en.substring(4,en.length()-4) ;
			if(Convert.isNotNullEmpty(id)&&!id.equals(prjid))
				continue;
			//chk id
			StringBuilder failedr = new StringBuilder() ;
//			if(!Convert.checkVarName(prjid, true,failedr))
//			{
//				continue ;
//			}
			
			String txt = ZipUtil.readZipTxt(zipf, en, "UTF-8");
			if(txt==null)
				continue ;
			
			XmlData tmpxd = XmlData.parseFromReader(new StringReader(txt)) ;
			UAPrj r = new UAPrj() ;
			if(!DataTranserXml.injectXmDataToObj(r, tmpxd))
				continue ;
			String n = r.getName() ;
			rets.add(new IdName(prjid,n).withTitle(r.getTitle())) ;
		}
		
		return rets ;
	}
	
	public boolean importPrjZipFile(File zipf,String id,String newid,String newname,String newtitle) throws Exception
	{
		if(Convert.isNotNullEmpty(newname))
		{
			UAPrj np = this.getPrjByName(newname) ;
			if(np!=null)
				return false;
		}
		
		List<IdName> tmpidns = parsePrjZipFile(zipf,id) ;
		if(tmpidns!=null&&tmpidns.size()==1)
		{
			if(Convert.isNullOrEmpty(newname))
			{
				UAPrj np = this.getPrjByName(tmpidns.get(0).getName()) ;
				if(np!=null)
					return false;
			}
		}
		
		List<String> ens = ZipUtil.readZipEntrys(zipf) ;
		HashMap<String,String> outens = new HashMap<>() ;
		boolean bvalid =false;
		String prefix = "prj_"+id ;
		for(String en:ens)
		{
			if(en.startsWith("prj_") && en.endsWith(".xml")&&en.indexOf('/')<0&&en.indexOf('\\')<0)
			{
				if(Convert.isNotNullEmpty(newid))
					outens.put(en,"prj_"+newid+".xml") ;
				else
					outens.put("en","");
				bvalid= true;
				continue ;
			}
			
			if(en.startsWith(prefix))
			{
				if(Convert.isNotNullEmpty(newid))
				{
					String taren = "prj_"+newid+en.substring(prefix.length()) ;
					outens.put(en, taren) ;
				}
				else
					outens.put(en,"") ;
			}
			//find prj
		}
		if(!bvalid)
			return false;
		
		ZipUtil.readZipOut(zipf, outens, UAManager.getPrjDataDir());
		String tmpid = Convert.isNotNullEmpty(newid)?newid:id ;
		UAPrj p = loadPrj(tmpid) ;
		if(p==null)
			return false;
		
		if(Convert.isNotNullEmpty(newid)||Convert.isNotNullEmpty(newname))
		{
			p.id = tmpid ;
			if(Convert.isNotNullEmpty(newname))
				p.setNameTitle(newname,null,null) ;
			if(Convert.isNotNullEmpty(newtitle))
				p.setNameTitle(null,newtitle,null);
			savePrj(p) ;
		}
		
		if(Convert.isNotNullEmpty(newid))
		{
			p.RT_init(true, true);
			this.reps.add(p) ;
		}
		this.updatePrjList();	
		p.constructNodeTree();
		return true;
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
					for(UAPrj prj:UAManager.this.listPrjs())
					{
						if(prj.isAutoStart())
							prj.RT_start();
					}
					
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
		
		uaMonTh = new Thread(uaMonRunner,"iottree-ua_mon") ;
		uaMonTh.start();
	}
	
	synchronized public void stop()
	{
		for(UAPrj prj:UAManager.this.listPrjs())
		{
			if(prj.RT_isRunning())
				prj.RT_stop();
		}
		
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
	
	//--res
	@Override
	public String getResNodeId()
	{
		return null ;
	}
	
	@Override
	public String getResNodeTitle()
	{
		return null;
	}

	@Override
	public IResNode getResNodeParent()
	{
		return null;
	}

	@Override
	public String getResPrefix()
	{
		return IResCxt.PRE_PRJ;
	}

	@Override
	public boolean isResReadOnly()
	{
		return false;
	}

	@Override
	public ResDir getResDir()
	{
		return null;
	}

	@Override
	public IResNode getResNodeSub(String subid)
	{
		return this.getPrjById(subid) ;
	}

}
