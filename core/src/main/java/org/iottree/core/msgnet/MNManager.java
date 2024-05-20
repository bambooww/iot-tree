package org.iottree.core.msgnet;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.nodes.*;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class MNManager
{
	private static ILogger log = LoggerManager.getLogger(MNManager.class) ;
	
	private static HashMap<String,MNManager> prjid2mgr = new HashMap<>() ;
	
	public static MNManager getInstance(UAPrj prj)
	{
		MNManager instance = prjid2mgr.get(prj.getId()) ;
		if(instance!=null)
			return instance ;
		
		synchronized(MNManager.class)
		{
			instance = prjid2mgr.get(prj.getId()) ;
			if(instance!=null)
				return instance ;
			
			instance = new MNManager(prj) ;
			prjid2mgr.put(prj.getId(),instance) ;
			return instance ;
		}
	}
	
	private static LinkedHashMap<String,MNNode> TP2NODE = new LinkedHashMap<>() ;
	
	public static void registerNode(MNNode mnn)
	{
		String tp = mnn.getNodeTP() ;
		TP2NODE.put(tp,mnn) ;
	}

	static
	{
		registerNode(new NSInject()) ;
		registerNode(new NSDebug()) ;
		registerNode(new NSSwitch()) ;
	}
	
	static MNNode getNodeByTP(String tp)
	{
		return TP2NODE.get(tp) ;
	}
	
	public static List<MNNode> listRegisteredNodes()
	{
		ArrayList<MNNode> rets = new ArrayList<>(TP2NODE.size()) ;
		rets.addAll(TP2NODE.values()) ;
		return rets ;
	}
	
	UAPrj belongTo = null ;
	
	private ArrayList<MNNet> nets = null ; 
	
	private MNManager(UAPrj prj)
	{
		this.belongTo = prj ;
	}
	
	public UAPrj getBelongTo()
	{
		return this.belongTo ;
	}
	
	public List<MNNet> listNets()
	{
		if(nets!=null)
			return nets;
		
		synchronized(this)
		{
			if(nets!=null)
				return nets;
			
			try
			{
				nets = this.loadNets() ;
				return nets ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return null ;
			}
		}
	}
	
	private ArrayList<MNNet> loadNets() throws Exception
	{
		final FileFilter ff = new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isFile()) return false;
				
				String fn = f.getName() ;
				return fn.startsWith("mn_") && fn.endsWith(".json") ;
			}};
		ArrayList<MNNet> rets = new ArrayList<>() ;
		
		File prjdir = this.belongTo.getPrjSubDir() ;
		for(File mnf:prjdir.listFiles(ff))
		{
			MNNet mnn = loadNet(mnf) ;
			if(mnn==null)
			{
				log.warn("load MNNet failed :"+mnf.getCanonicalPath());
				continue ;
			}
			rets.add(mnn) ;
		}
		return rets ;
	}
	
	private MNNet loadNet(File f) throws IOException
	{
		String txt = Convert.readFileTxt(f) ;
		if(Convert.isNullOrEmpty(txt))
			return null ;
		JSONObject jo = new JSONObject(txt)  ;
		
		
		MNNet ret = new MNNet(this) ;
		if(!ret.fromJO(jo))
		{
			return null ;
		}
		return ret ;
	}
	
	private File calNetFile(String id)
	{
		File prjdir = this.belongTo.getPrjSubDir() ;
		return new File(prjdir,"mn_"+id+".json") ;
	}
	
	public void saveNet(MNNet mnn) throws IOException
	{
		File f = calNetFile(mnn.getId()) ;
		JSONObject jo = mnn.toJO() ;
		Convert.writeFileTxt(f, jo.toString());
	}
	
	public MNNet getNetById(String id)
	{
		for(MNNet net:this.listNets())
		{
			if(id.equalsIgnoreCase(net.getId()))
				return net ;
		}
		return null ;
	}
	
	public MNNet getNetByName(String name)
	{
		for(MNNet net:this.listNets())
		{
			if(name.equalsIgnoreCase(net.getName()))
				return net ;
		}
		return null ;
	}
	
	public MNNet createNewNet(String name,String title,String desc) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(name, failedr))
			throw new Exception(failedr.toString()) ;
		
		MNNet old_n = this.getNetByName(name) ;
		if(old_n!=null)
			throw new Exception("net with name "+name+ "existed") ;
		
		MNNet rnn = new MNNet(this,name,title,desc) ;
		saveNet(rnn);
		this.listNets().add(rnn) ;
		return rnn ;
	}
	
	public MNNet updateNet(String id,JSONObject jo)  throws Exception
	{
		MNNet rnn = this.getNetById(id) ;
		if(rnn==null)
			throw new Exception("no net found with id="+id) ;
		rnn.fromJO(jo) ;
		this.saveNet(rnn);
		return rnn ;
	}
	
	public MNNet updateNet(String id,String name,String title,String desc) throws Exception
	{
		MNNet rnn = this.getNetById(id) ;
		if(rnn==null)
			throw new Exception("no net found with id="+id) ;
		
		MNNet old_n = this.getNetByName(name) ;
		if(old_n!=null&&old_n!=rnn)
			throw new Exception("net with name "+name+ "existed") ;
		rnn.name = name ;
		rnn.title = title ;
		rnn.desc = desc ;
		this.saveNet(rnn);
		return rnn ;
	}
	
	public MNNet delNet(String id) throws Exception
	{
		MNNet rnn = this.getNetById(id) ;
		if(rnn==null)
			throw new Exception("no net found with id="+id) ;
		File f = calNetFile(id) ;
		f.delete();
		listNets().remove(rnn) ;
		return rnn ;
	}
}
