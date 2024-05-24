package org.iottree.core.msgnet;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UAServer;
import org.iottree.core.msgnet.nodes.*;
import org.iottree.core.msgnet.modules.*;
import org.iottree.core.msgnet.util.ConfNode;
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
	
	private static LinkedHashMap<String,MNCat> NAME2CATS = new LinkedHashMap<>() ;
	
	private static LinkedHashMap<String,MNNode> TP2NODE = new LinkedHashMap<>() ;
	private static LinkedHashMap<String,MNModule> TP2Module = new LinkedHashMap<>() ;
	
	public static MNCat registerCat(MNCat cat)
	{
		NAME2CATS.put(cat.getName(),cat) ;
		return cat ;
	}
	
	public static MNCat registerCat(String name,String title)
	{
		MNCat cat = new MNCat(name,title) ;
		return registerCat(cat) ;
	}
	
	public static void registerNode(MNNode mnn,MNCat cat)
	{
		//mnn.setNodeTP(tp, tpt);
		//String tp = mnn.getNodeTP() ;
		mnn.setCat(cat);
		TP2NODE.put(mnn.getTPFull(),mnn) ;
		cat.nodes.add(mnn) ;
	}
	
	private static void registerNode(ConfNode confn,MNCat cat)
	{
		try
		{
			UAServer.WebItem wi = cat.getWebItem() ;
			Class<?> c = wi.getAppClassLoader().loadClass(confn.getClassName()) ;
			MNNode mnn = (MNNode)c.newInstance() ;
			registerNode(mnn,cat);
		}
		catch(Exception ee)
		{
			if(log.isDebugEnabled())
				log.error(ee.getMessage(), ee);
			log.warn(ee.getMessage());
		}
	}
	
	public static void registerNode(UAServer.WebItem wi,JSONObject msg_net_jo)
	{
		String catn = wi.getAppName() ;
		catn = msg_net_jo.optString("cat_name",catn) ;
		String title = msg_net_jo.optString("cat_title") ;
		MNCat cat = registerCat(catn,title).asWebItem(wi) ;
		
		List<ConfNode> nodes = ConfNode.parseConfNodes(msg_net_jo) ;
		 for(ConfNode nn:nodes)
		 {
			 registerNode(nn,cat) ;
		 }
	}
	
	public static void registerModule(MNModule mnn,MNCat cat)
	{
		//mnn.setNodeTP(tp, tpt);
		//String tp = mnn.getNodeTP() ;
		mnn.setCat(cat);
		TP2Module.put(mnn.getTPFull(),mnn) ;
		cat.modules.add(mnn) ;
	}
	
	public static void registerModule(String classname,MNCat cat)
	{
		try
		{
			Class<?> c = Class.forName(classname) ;
			MNModule m = (MNModule)c.newInstance() ;
			registerModule(m,cat);
		}
		catch(Exception ee)
		{
			if(log.isDebugEnabled())
				log.error(ee.getMessage(), ee);
			log.warn(ee.getMessage());
		}
	}

	static
	{
		MNCat cat = registerCat(new MNCat("_com")) ;
		registerNode(new NS_Inject(),cat) ;
		registerNode(new NE_Debug(),cat) ;
		registerNode(new NM_JsFunc(),cat) ;
		
		cat = registerCat(new MNCat("_func")) ;
		registerNode(new NM_Switch(),cat) ;
		registerNode(new NM_Change(),cat) ;
		
		cat = registerCat(new MNCat("_net")) ;
		registerModule("org.iottree.ext.msg_net.Kafka_M",cat) ;
		
		cat = registerCat(new MNCat("_storage")) ;
		registerModule(new M_DBSql(),cat) ;
		
//		registerCat(new MNNodeCat("network")) ;
//		registerCat(new MNNodeCat("seq")) ;
//		registerCat(new MNNodeCat("parser")) ;
//		registerCat(new MNNodeCat("storage")) ;
		
		
		
	}
	
	public static MNCat getCatByName(String name)
	{
		return NAME2CATS.get(name) ;
	}
	
	public static MNNode getNodeByFullTP(String full_tp)
	{
		if(Convert.isNullOrEmpty(full_tp))
			return null ;
		List<String> ss = Convert.splitStrWith(full_tp, ".") ;
		int sz = ss.size() ;
		if(sz<=1) return null ;
		
		if(sz==2)
		{
			MNCat cat = getCatByName(ss.get(0)) ;
			if(cat==null)
				return null ;
			return cat.getNodeByTP(ss.get(1)) ;
		}
		
		MNCat cat = getCatByName(ss.get(0)) ;
		if(cat==null)
			return null ;
		MNModule m = cat.getModuleByTP(ss.get(1)) ;
		if(m==null)
			return null ;
		
		return m.getSupportedNodeByTP(ss.get(2)) ;
	}
	
	public static MNModule getModuleByFullTP(String full_tp)
	{
		return TP2Module.get(full_tp) ;
	}
	
	public static List<MNCat> listRegisteredCats()
	{
		ArrayList<MNCat> rets = new ArrayList<>() ;
		rets.addAll(NAME2CATS.values()) ;
		return rets ;
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
