package org.iottree.core.ecp;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IDirBase;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class ECPManager extends ECPNetContainer
{
	private static ILogger log = LoggerManager.getLogger(ECPManager.class) ;
	
	private static HashMap<String, ECPManager> id2instance = new HashMap<>();
	
	private static ECPManager getInstance(String tp,String mgr_id,File dir_base)
	{
		String did = tp+"-"+mgr_id;//dirb.getDirBaseId() ;
		ECPManager r = id2instance.get(did);
		if (r != null)
			return r;

		synchronized (ECPManager.class)
		{
			r = id2instance.get(did);
			if (r != null)
				return r;

			r = new ECPManager(tp,mgr_id,dir_base);
			id2instance.put(did, r);
			return r;
		}
	}
	
	public static ECPManager getInstanceByPrjId(String prj_id)
	{
		UAPrj prj = UAManager.getInstance().getPrjById(prj_id) ;
		if(prj==null)
			return null ;
		File dirb = prj.getPrjSubDir() ;
		return getInstance("prj",prj_id,dirb) ;
	}
	
	public static ECPManager getInstanceByPrj(UAPrj prj)
	{
		File dirb = prj.getPrjSubDir() ;
		return getInstance("prj",prj.getId(),dirb) ;
	}
	
	public static ECPManager getInstanceUA()
	{
		UAManager uam = UAManager.getInstance() ;
		File dirf = uam.getDirBaseFile() ;
		return getInstance("ua","_",dirf) ; // 
	}
	
	public static ECPManager getInstanceByMgrId(String mgr_id)
	{
		String did = UAManager.getInstance().getDirBaseId() ;
		if(mgr_id.equals(did))
			return getInstanceUA() ;
		
		return getInstanceByPrjId(mgr_id) ;
	}
	
	public static ECPNet getNetByUID(String uid)
	{
		List<String> ss = Convert.splitStrWith(uid, "-") ;
		if(ss==null||ss.size()<3)
			return null ;
		ECPManager mgr =null ;
		switch(ss.get(0))
		{
		case "ua":
			mgr = getInstanceUA();
			break ;
		case "prj":
			mgr = getInstanceByPrjId(ss.get(1));
			break ;
		case "comp":
			ECPCompLib lib = ECPCompManager.getInstance().getLibById(ss.get(1)) ;
			if(lib==null)
				return null ;
			ECPComp comp = lib.getCompById(ss.get(2)) ;
			if(comp==null)
				return null ;
			// temp net wrapper comp
		}
		
		if(mgr==null)
			return null ;
		return mgr.getNetById(ss.get(2)) ;
	}
	
	private String tp ;
	
	private String mgrId ;

	private File dirBase ;
	
	private ArrayList<ECPNet> nets = null ;

	private ECPManager(String tp,String mgr_id,File dirbase)
	{
		this.tp = tp ;
		this.mgrId = mgr_id ;
		this.dirBase = dirbase;
	}
	
	@Override
	public String getContainerTp()
	{
		return this.tp ;
	}
	
	@Override
	protected String getContainerId()
	{// ua  prj_xxxx
		return this.mgrId;
	}
	
	public String getMgrId()
	{
		return this.mgrId ;
	}
	
	public List<ECPNet> listNets()
	{
		if(nets!=null)
			return this.nets;
		
		synchronized(this)
		{
			if(nets!=null)
				return nets;
			
			try
			{
				nets = this.loadNets() ;
				if(nets!=null)
				{
					for(ECPNet net:nets)
						net.onAfterLoaded() ;
				}
				return nets ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return null ;
			}
		}
	}

	private ArrayList<ECPNet> loadNets() throws Exception
	{
		final FileFilter ff = new FileFilter() {
			@Override
			public boolean accept(File f)
			{
				if(!f.isFile()) return false;
				
				String fn = f.getName() ;
				return fn.startsWith("ecp_") && fn.endsWith(".json") ;
			}};
		ArrayList<ECPNet> rets = new ArrayList<>() ;
		
		File dir = this.calNetDir();//.getPrjSubDir() ;
		if(dir.exists())
		{
			for(File mnf:dir.listFiles(ff))
			{
				String fn = mnf.getName() ;
				String id = fn.substring(4,fn.length()-5) ;
				ECPNet mnn = loadNet(mnf,id) ;
				if(mnn==null)
				{
					log.warn("load ECPNet failed :"+mnf.getCanonicalPath());
					continue ;
				}
				rets.add(mnn) ;
			}
		}
		return rets ;
	}
	
	private ECPNet loadNet(File f,String id) throws IOException
	{
		String txt = Convert.readFileTxt(f) ;
		if(Convert.isNullOrEmpty(txt))
			return null ;
		JSONObject jo = new JSONObject(txt)  ;
		
		ECPNet ret = new ECPNet(this,id) ;
		if(!ret.fromJO(jo))
		{
			return null ;
		}
		return ret ;
	}
	
	private File calNetDir()
	{
		return new File(this.dirBase,"ecps/nets/");
	}
	
	
	File calNetFile(String id)
	{
		File dir = calNetDir() ;
		return new File(dir,"ecp_"+id+".json") ;
	}
	
	@Override
	public void saveNet(ECPNet mnn) throws IOException
	{
		File f = calNetFile(mnn.getId()) ;
		JSONObject jo = mnn.toJO() ;
		Convert.writeFileTxt(f, jo.toString());
	}
	
	
	public ECPNet getNetById(String id)
	{
		for(ECPNet net:this.listNets())
		{
			if(id.equalsIgnoreCase(net.getId()))
				return net ;
		}
		return null ;
	}
	
	public ECPNet getNetByName(String name)
	{
		for(ECPNet net:this.listNets())
		{
			if(name.equalsIgnoreCase(net.getName()))
				return net ;
		}
		return null ;
	}
	
	public ECPNet createNewNet(String name,String title,String desc,boolean benable) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(name, failedr))
			throw new Exception(failedr.toString()) ;
		
		ECPNet old_n = this.getNetByName(name) ;
		if(old_n!=null)
			throw new Exception("net with name "+name+ "existed") ;
		
		String id = IdCreator.newSeqId() ;
		ECPNet rnn = new ECPNet(this,id).asBasic(name, title, desc) ;
		rnn.bEnable = benable ;
		saveNet(rnn);
		this.listNets().add(rnn) ;
		clearCache();
		return rnn ;
	}
	
	public ECPNet updateNet(String id,JSONObject jo)  throws Exception
	{
		ECPNet rnn = this.getNetById(id) ;
		if(rnn==null)
			throw new Exception("no net found with id="+id) ;
		rnn.fromJO(jo) ;
		this.saveNet(rnn);
		clearCache();
		return rnn ;
	}
	
	public ECPNet updateNet(String id,String name,String title,String desc,boolean benable) throws Exception
	{
		ECPNet rnn = this.getNetById(id) ;
		if(rnn==null)
			throw new Exception("no net found with id="+id) ;
		
		ECPNet old_n = this.getNetByName(name) ;
		if(old_n!=null&&old_n!=rnn)
			throw new Exception("net with name "+name+ "existed") ;
		rnn.name = name ;
		rnn.title = title ;
		rnn.desc = desc ;
		rnn.bEnable = benable ;
		this.saveNet(rnn);
		clearCache();
		return rnn ;
	}
	
	public ECPNet delNet(String id) throws Exception
	{
		ECPNet rnn = this.getNetById(id) ;
		if(rnn==null)
			throw new Exception("no net found with id="+id) ;
		File f = calNetFile(id) ;
		f.delete();
		listNets().remove(rnn) ;
		
		clearCache();
		return rnn ;
	}
	
	private synchronized void clearCache()
	{
		
	}
	
	public ECPNet impNetByJO(JSONObject net_jo,String newname,String new_title,boolean force_replace,StringBuilder failedr) throws Exception
	{
		String id = IdCreator.newSeqId();//.optString("id") ;
		//if(Convert.isn)
		ECPNet ret = new ECPNet(this,id) ;
		if(!ret.fromJO(net_jo))
		{
			failedr.append("invalid net jo format");
			return null ;
		}
		
		if(!Convert.checkVarName(newname, failedr))
			throw new Exception(failedr.toString()) ;
		
		ECPNet old_n = this.getNetByName(newname) ;
		if(old_n!=null && !force_replace)
			throw new Exception("net with name "+newname+ " is already existed") ;
		
		List<ECPNet> nets = this.listNets();
		
		if(old_n!=null && force_replace)
		{//replace old one
			if(old_n.RT_isRunning())
			{
				failedr.append("cannot replace net ,becuse net is running") ;
				return null;
			}
			ret.id = old_n.id ;
			if(Convert.isNotNullEmpty(new_title))
				ret.title = new_title ;
			int k = nets.indexOf(old_n) ;
			saveNet(ret);
			nets.set(k,ret) ;
			clearCache();
			return ret ;
		}
		
		//add new one
		ret.id = IdCreator.newSeqId() ;
		ret.name = newname ;
		if(Convert.isNotNullEmpty(new_title))
			ret.title = new_title ;
		saveNet(ret);
		nets.add(ret) ;
		clearCache();
		return ret ;
	}

	

	
}
