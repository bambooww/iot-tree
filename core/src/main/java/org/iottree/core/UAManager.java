package org.iottree.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;

import org.iottree.core.basic.IdName;
import org.iottree.core.msgnet.IMNContProvider;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ZipUtil;
import org.iottree.core.util.web.Mime;
import org.iottree.core.util.xmldata.*;

import com.google.common.eventbus.EventBus;

public class UAManager implements IMNContProvider
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
			MNManager.setContProvider(instance);
			return instance ;
		}
	}
	

	
	private ArrayList<UAPrj> prjs = null;
	
	private EventBus eventBus = new EventBus("ua");
	
	private UAManager()
	{
		
	}
	
	public ArrayList<UAPrj> listPrjs()
	{
		if(prjs!=null)
			return prjs ;
		
		synchronized(this)
		{
			if(prjs!=null)
				return prjs ;
			
			ArrayList<UAPrj> prjs00 = loadPrjs();
			if(prjs00==null)
				prjs00 = new ArrayList<>() ;
			updatePrjList(prjs00);
			//constrcuctTree();
			for(UAPrj r:prjs00)
				r.constructNodeTree();
			
			return prjs = prjs00 ;
		}
	}
	
//	private void constrcuctTree()
//	{
//		for(UAPrj r:prjs)
//			r.constructNodeTree();
//	}
	
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
					//dc.onLoaded();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return reps;
	}
	
	private static void updatePrjList(List<UAPrj> prjss)
	{
		Collections.sort(prjss, new Comparator<UAPrj>() {
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
	
	private void updatePrjList()
	{
		updatePrjList(this.listPrjs());
	}
	
	transient String defaultPrjId = null ;
	/**
	 * get default project
	 * @return
	 * @throws IOException 
	 */
	public UAPrj getPrjDefault()// throws IOException
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
		
		try
		{
			String defid = Convert.readFileTxt(f);
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
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
		
		
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
		
		for(UAPrj r:listPrjs())
		{
			if(r.getId().contentEquals(id))
				return r ;
		}
		return null ;
	}
	
	public UAPrj getPrjByName(String name)
	{
		for(UAPrj r:listPrjs())
		{
			if(name.contentEquals(r.getName()))
				return r ;
		}
		return null ;
	}
	
//	public UAPrj getPrjMain()
//	{
//		for(UAPrj r:listPrjs())
//		{
//			if(r.isMainPrj())
//				return r ;
//		}
//		return null ;
//	}
	
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
		
		updatePrjList();
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
		
		this.listPrjs().remove(prj) ;
	}
	
	
	public UAPrj addPrj(String name,String title,String desc) throws Exception
	{
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(name,sb))
			throw new IllegalArgumentException(sb.toString()) ;
		if("admin".equals(name)||"doc".equals(name))
		{
			throw new IllegalArgumentException(name+" is reserved word.Please use another name") ;
		}
		UAPrj r = this.getPrjByName(name) ;
		if(r!=null)
		{
			throw new Exception("name="+name+" is existed!") ;
		}
		r = new UAPrj(name,title,desc) ;
		savePrj(r);
		r.RT_init(true, false);
		listPrjs().add(r);
		
		this.updatePrjList();
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
	
	public boolean exportPrj(HttpServletResponse resp, String prjid) throws IOException
	{
		UAPrj p = this.getPrjById(prjid) ;
		if(p==null)
			return false;
		
		String filename = "prj_"+p.getName()+".zip";
		List<File> fs = Arrays.asList(UAManager.getPrjFile(prjid),UAManager.getPrjFileSubDir(prjid)) ;
		HashMap<String,String> metam = new HashMap<>() ;
		metam.put("tp", "prj") ;
		metam.put("prjid", prjid) ;
		String metatxt=  Convert.transMapToPropStr(metam) ;
		
		resp.addHeader("Content-Disposition", "attachment; filename="
				+ new String(filename.getBytes(), "iso8859-1"));

		resp.setContentType(Mime.getContentType(filename));
		
		ServletOutputStream os = resp.getOutputStream();
		ZipUtil.zipOut(metatxt,fs,os) ;
		os.flush();
	
		return true;
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
	
	public boolean exportPrj(String prjid,OutputStream out) throws IOException
	{
		UAPrj p = this.getPrjById(prjid) ;
		if(p==null)
			return false;
		
		List<File> fs = Arrays.asList(UAManager.getPrjFile(prjid),UAManager.getPrjFileSubDir(prjid)) ;
		HashMap<String,String> metam = new HashMap<>() ;
		metam.put("tp", "prj") ;
		metam.put("prjid", prjid) ;
		String metatxt=  Convert.transMapToPropStr(metam) ;
		
		ZipUtil.zipOut(metatxt,fs,out) ;
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
//			StringBuilder failedr = new StringBuilder() ;
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
	
	private List<IdName> parsePrjZipFile(byte[] zipbs,String id) throws Exception
	{
		ArrayList<IdName> rets =new ArrayList<>() ;
		
		List<String> ens = ZipUtil.readZipEntrys(zipbs) ;
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
//			StringBuilder failedr = new StringBuilder() ;
//			if(!Convert.checkVarName(prjid, true,failedr))
//			{
//				continue ;
//			}
			
			String txt = ZipUtil.readZipTxt(new ByteArrayInputStream(zipbs), en, "UTF-8");
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
			
			if("admin".equals(newname)||"doc".equals(newname))
			{
				throw new IllegalArgumentException(newname+" is reserved word.Please use another name") ;
			}
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
			//System.out.println(" entry :"+en) ;
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
			this.listPrjs().add(p) ;
		}
		this.updatePrjList();	
		p.constructNodeTree();
		return true;
	}
	
	public boolean updateOrAddPrj(byte[] zipbs,String prjname,StringBuilder failedr) throws Exception
	{
		UAPrj np = this.getPrjByName(prjname) ;
		String prjid =null;
		if(np!=null)
		{
			if(np.RT_isRunning())
			{
				failedr.append("prj "+prjname+" is running") ;
				return false;
			}
			this.backupPrj(np.getId()) ; //may do recover
			this.delPrj(np.getId());
			prjid = np.getId() ;
		}
		
		List<IdName> tmpidns = parsePrjZipFile(zipbs,null) ;
		if(tmpidns==null||tmpidns.size()<=0)
		{
			failedr.append("invalid zip fmt") ;
			return false;
		}
		
		String id = tmpidns.get(0).getId() ;
		
		if(Convert.isNullOrEmpty(prjid))
			prjid = CompressUUID.createNewId(); 
		
		List<String> ens = ZipUtil.readZipEntrys(zipbs) ;
		HashMap<String,String> outens = new HashMap<>() ;
		boolean bvalid =false;
		String prefix = "prj_"+id ;
		for(String en:ens)
		{
			//System.out.println(" entry :"+en) ;
			if(en.startsWith("prj_") && en.endsWith(".xml")&&en.indexOf('/')<0&&en.indexOf('\\')<0)
			{
				if(Convert.isNotNullEmpty(prjid))
					outens.put(en,"prj_"+prjid+".xml") ;
				else
					outens.put("en","");
				bvalid= true;
				continue ;
			}
			
			if(en.startsWith(prefix))
			{
				if(Convert.isNotNullEmpty(prjid))
				{
					String taren = "prj_"+prjid+en.substring(prefix.length()) ;
					outens.put(en, taren) ;
				}
				else
					outens.put(en,"") ;
			}
			//find prj
		}
		if(!bvalid)
			return false;
		
		ZipUtil.readZipOut(new ByteArrayInputStream(zipbs), outens, UAManager.getPrjDataDir());
		UAPrj p = loadPrj(prjid) ;
		if(p==null)
			return false;
			
		p.id = prjid ;
		savePrj(p) ;
		
		p.RT_init(true, true);
		this.listPrjs().add(p) ;
		
		this.updatePrjList();	
		p.constructNodeTree();
		return true;
	}
	
	
	public UANode findNodeByPath(String path)
	{
		if("/".equals(path))
			return null ;
		
		LinkedList<String> ss = Convert.splitStrWithLinkedList(path, "/\\.") ;
		String n = ss.removeFirst() ;
		UAPrj rep = this.getPrjByName(n) ;
		if(rep==null)
			return null ;
		return rep.getDescendantNodeByPath(ss) ;
	}
	
//	public UANode findNodeById(String id)
//	{ // tag id may same in diff prjs
//		for(UAPrj rep:this.listPrjs())
//		{
//			UANode n = rep.findNodeById(id) ;
//			if(n!=null)
//				return n ;
//		}
//		return null ;
//	}
	
	Thread uaMonTh = null;
	
	Runnable uaMonRunner = new Runnable()
			{
				@Override
				public void run()
				{
					ArrayList<UAPrj> ppps = new ArrayList<>() ;
					ppps.addAll(UAManager.this.listPrjs()) ;
					
					for(UAPrj prj:ppps)
					{
						prj.RT_onMonInit() ;
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
			//if(prj.RT_isRunning())
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
	
//	@Override
//	public String getMsgNetContName()
//	{
//		return "uamanager" ;
//	}

	@Override
	public IMNContainer getMsgNetContainer(String container_id)
	{
		return this.getPrjById(container_id);
	}

	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run()
			{
				UAManager.getInstance().stop() ;
			}
		});
	}

}
