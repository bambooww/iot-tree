package org.iottree.core.msgnet;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAPrj;
import org.iottree.core.UAServer;
import org.iottree.core.UATag;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.conn.ConnPtMSG;
import org.iottree.core.msgnet.nodes.*;
import org.iottree.core.msgnet.modules.*;
import org.iottree.core.msgnet.util.ConfItem;
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
	
	public static void registerItem(MNBase mnn,MNCat cat)
	{
		//mnn.setNodeTP(tp, tpt);
		//String tp = mnn.getNodeTP() ;
		mnn.setCat(cat);
		if(mnn instanceof MNNode)
		{
			TP2NODE.put(mnn.getTPFull(),(MNNode)mnn) ;
			cat.nodes.add((MNNode)mnn) ;
		}
		else
		{
			TP2Module.put(mnn.getTPFull(),(MNModule)mnn) ;
			cat.modules.add((MNModule)mnn) ;
		}
	}
	
	public static void registerByWebItem(UAServer.WebItem wi,JSONObject msg_net_jo)
	{
		String catn = wi.getAppName() ;
		catn = msg_net_jo.optString("cat_name",catn) ;
		String title = msg_net_jo.optString("cat_title") ;
		MNCat cat = registerCat(catn,title).asWebItem(wi) ;
		
		List<ConfItem> cis = ConfItem.parseConfItems(msg_net_jo) ;
		 for(ConfItem ci:cis)
		 {
			 try
			{
					Class<?> c = wi.getAppClassLoader().loadClass(ci.getClassName()) ;
					MNBase mnn = (MNBase)c.newInstance() ;
					mnn.setCat(cat);
					cat.item2conf.put(mnn.getTPFull(), ci) ;
					registerItem(mnn,cat);
				}
				catch(Exception ee)
				{
					//if(log.isDebugEnabled())
					//	log.error(ee.getMessage(), ee);
					ee.printStackTrace();
					//log.warn(ee.getMessage());
				}
		 }
	}
	
//	public static void registerModule(MNModule mnn,MNCat cat)
//	{
//		//mnn.setNodeTP(tp, tpt);
//		//String tp = mnn.getNodeTP() ;
//		mnn.setCat(cat);
//		TP2Module.put(mnn.getTPFull(),mnn) ;
//		cat.modules.add(mnn) ;
//	}
	
	public static void registerItem(String classname,MNCat cat)
	{
		try
		{
			Class<?> c = Class.forName(classname) ;
			MNModule m = (MNModule)c.newInstance() ;
			registerItem(m,cat);
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
		registerItem(new ManualTrigger(),cat) ;
		registerItem(new NS_TimerTrigger(),cat) ;
		registerItem(new NE_Debug(),cat) ;
		registerItem(new NM_MemQueue(),cat) ;
		registerItem(new MemMultiQueue(),cat) ;
		registerItem(new NS_OuterTrigger(),cat) ;
		
		cat = registerCat(new MNCat("_func")) ;
		registerItem(new NM_JsFunc(),cat) ;
		registerItem(new NM_Template(),cat) ;
		registerItem(new NM_Change(),cat) ;
		registerItem(new NM_Switch(),cat) ;
		registerItem(new NM_OnOff(),cat) ;
		registerItem(new NM_PID(),cat) ;
		registerItem(new NM_Exec(),cat) ;
		
		cat = registerCat(new MNCat("_dev")) ;
		registerItem(new NM_TagReader(),cat) ;
		registerItem(new NM_TagWriter(),cat) ;
		registerItem(new NM_TagFilter(),cat) ;
		registerItem(new NM_TagFilterW(),cat) ;
		registerItem(new NS_TagValChgTrigger(),cat) ;
		registerItem(new NS_TagEvtTrigger(),cat) ;
		registerItem(new NS_ConnInMsgTrigger(),cat) ;
		//registerItem(new NS_TagAlertTrigger(),cat) ;
		
		
		cat = registerCat(new MNCat("_net")) ;
		registerItem("org.iottree.ext.msg_net.Kafka_M",cat) ;
		registerItem("org.iottree.ext.msg_net.Mqtt_M",cat) ;
		registerItem(new NM_HttpClient(),cat) ;
		registerItem("org.iottree.ext.msg_net.BACnet_M",cat) ;
		
		cat = registerCat(new MNCat("_storage")) ;
		registerItem(new DBSql(),cat) ;
		
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
	
	public static MNBase getItemByFullTP(String mn,String full_tp)
	{
		if("n".equals(mn))
			return getNodeByFullTP(full_tp) ;
		else
			return getModuleByFullTP(full_tp) ;
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
	
	public MNNet createNewNet(String name,String title,String desc,boolean benable) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(name, failedr))
			throw new Exception(failedr.toString()) ;
		
		MNNet old_n = this.getNetByName(name) ;
		if(old_n!=null)
			throw new Exception("net with name "+name+ "existed") ;
		
		MNNet rnn = new MNNet(this,name,title,desc) ;
		rnn.bEnable = benable ;
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
	
	public MNNet updateNet(String id,String name,String title,String desc,boolean benable) throws Exception
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
		rnn.bEnable = benable ;
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
	
	
	// rt
	
	Thread rtTH = null ;
	boolean rtRun = false;
	
	MNCxtPk rtCxtPrj = new MNCxtPk() ;
	
	public MNCxtPk RT_getCxtPk()
	{
		return rtCxtPrj ;
	}

	private Runnable rtCxtPrjRunner = new Runnable() {

		@Override
		public void run()
		{
			RT_run() ;
		}
	};
	
	private void RT_run()// throws Exception
	{
		try
		{
			RT_CXT_load() ;
			
			StringBuilder failedr = new StringBuilder() ;
			for(MNNet net:this.listNets())
			{
				if(!net.isEnable())
					continue ;
				
				net.RT_startNetFlow(failedr);
			}
		
			while(rtRun)
			{
				Thread.sleep(300);
				
				synchronized(this) //cannot be stopped
				{
					RT_CXT_save(false);
				}
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
			//RT_stop() ;
			rtTH = null ;
			rtRun=false;
		}
	}
	
	public synchronized void RT_start()
	{
		if(rtRun)
			return ;
		
		rtRun = true ;
		rtTH  =new Thread(rtCxtPrjRunner);
		rtTH.start();
	}
	
	public synchronized void RT_stop()
	{
		if(!rtRun)
			return ;

		for(MNNet net:this.listNets())
		{
			if(!net.isEnable())
				continue ;
			net.RT_stopNetFlow();
		}
		
		rtRun = false;
	}
	
	public boolean RT_isRunning()
	{
		return rtRun ;
	}
	
	// rt cxt

	private File getRTCxtFilePrj()
	{
		return new File(this.belongTo.getPrjSubDir(), "./msg_net/prj.json");
	}

	private File getRTCxtFileNet(MNNet net)
	{
		return new File(this.belongTo.getPrjSubDir(), "./msg_net/net_" + net.getId() + ".json");
	}

	boolean RT_CXT_load() throws IOException
	{
		JSONObject jo = Convert.readFileJO(getRTCxtFilePrj());
		rtCxtPrj.RT_CXT_injectSavedVals(jo);

		for (MNNet net : this.listNets())
		{
			File cxtf = getRTCxtFileNet(net);
			jo = Convert.readFileJO(cxtf);
			net.RT_CXT_fromSavedJO(jo);
		}
		return true;
	}

	void RT_CXT_save(boolean b_all) throws IOException
	{
		if (b_all || rtCxtPrj.RT_CXT_isDirty())
		{
			JSONObject jo = rtCxtPrj.RT_CXT_extractValsForSave();
			Convert.writeFileJO(this.getRTCxtFilePrj(), jo);
			rtCxtPrj.RT_CXT_clearDirty();
		}

		for (MNNet net : this.listNets())
		{
			if (b_all || net.RT_CXT_isSelfOrSubDirty())
			{
				JSONObject jo = net.RT_CXT_toSaveJO();
				File cxtf = getRTCxtFileNet(net);
				Convert.writeFileJO(cxtf, jo);
				net.RT_CXT_clearSelfSubDirty();
			}
		}
	}
	
	
	public void RT_TAG_triggerEvt(ValAlert va,Object curval)
	{
		for(MNNet net :this.listNets())
		{
			if(!net.isEnable())
				continue ;
			for(MNNode node:net.getNodeMapAll().values())
			{
				if(node instanceof NS_TagEvtTrigger && node.isEnable())
				{
					((NS_TagEvtTrigger)node).RT_fireByEventTrigger(va,curval) ;
				}
			}
		}
	}
	
	public void RT_TAG_releaseEvt(ValAlert va,Object curval)
	{
		for(MNNet net :this.listNets())
		{
			if(!net.isEnable())
				continue ;
			for(MNNode node:net.getNodeMapAll().values())
			{
				if(node instanceof NS_TagEvtTrigger && node.isEnable())
				{
					((NS_TagEvtTrigger)node).RT_fireByEventRelease(va,curval) ;
				}
			}
		}
	}
	
	public void RT_CONN_triggerConnMsg(ConnPtMSG cpt_msg,String json_xml_str,Map<UATag,Object> tag2obj)
	{
		for(MNNet net :this.listNets())
		{
			if(!net.isEnable())
				continue ;
			for(MNNode node:net.getNodeMapAll().values())
			{
				if(node instanceof NS_ConnInMsgTrigger && node.isEnable())
					((NS_ConnInMsgTrigger)node).RT_fireByConnInMsg(cpt_msg,json_xml_str,tag2obj) ;
			}
		}
	}
}
