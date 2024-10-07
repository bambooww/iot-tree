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
import org.json.JSONArray;
import org.json.JSONObject;

public class MNManager
{
	private static ILogger log = LoggerManager.getLogger(MNManager.class) ;
	
	private static IMNContProvider contProvider = null ;
	
	public static boolean setContProvider(IMNContProvider contp)
	{
		if(contProvider!=null) //系统必须一开始就要设置
			return false;
		contProvider = contp ;
		return true ;
	}
	
	public static IMNContProvider getContProvider()
	{
		return contProvider ;
	}
	
	private static HashMap<String,MNManager> prjid2mgr = new HashMap<>() ;
	
	public static MNManager getInstance(IMNContainer prj)
	{
		MNManager instance = prjid2mgr.get(prj.getMsgNetContainerId()) ;
		if(instance!=null)
			return instance ;
		
		synchronized(MNManager.class)
		{
			instance = prjid2mgr.get(prj.getMsgNetContainerId()) ;
			if(instance!=null)
				return instance ;
			
			instance = new MNManager(prj) ;
			prjid2mgr.put(prj.getMsgNetContainerId(),instance) ;
			return instance ;
		}
	}
	
	public static MNManager getInstanceByContainerId(String container_id)
	{
		if(contProvider==null)
			return null ;
		IMNContainer cont = contProvider.getMsgNetContainer(container_id) ;
		if(cont==null)
			return null ;
		
		return getInstance(cont) ;
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
	
	public static MNBase registerItem(String classname,MNCat cat)
	{
		try
		{
			Class<?> c = Class.forName(classname) ;
			MNBase m = (MNBase)c.newInstance() ;
			registerItem(m,cat);
			return m ;
		}
		catch(Throwable ee)
		{
			if(log.isDebugEnabled())
				log.error(ee.getMessage(), ee);
			log.warn("registerItem "+classname+" "+ee.getMessage());
			return null ;
		}
	}
	
	public static MNBase registerNodeToModule(String classname,MNModule m)
	{
		try
		{
			Class<?> c = Class.forName(classname) ;
			MNNode n = (MNNode)c.newInstance() ;
			m.registerSubTpNode(n);
			return n ;
		}
		catch(Throwable ee)
		{
			if(log.isDebugEnabled())
				log.error(ee.getMessage(), ee);
			log.warn("registerItem "+classname+" "+ee.getMessage());
			return null ;
		}
	}
	
//	private void loadNodesDefault()
//	{
//		MNCat cat = registerCat(new MNCat("_com")) ;
//		registerItem(new org.iottree.core.msgnet.nodes.ManualTrigger(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NS_TimerTrigger(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NE_Debug(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_MemQueue(),cat) ;
//		registerItem(new org.iottree.core.msgnet.modules.MemMultiQueue(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NS_OuterTrigger(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NS_OnFlowEvt(),cat) ;
//		
//		cat = registerCat(new MNCat("_func")) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_JsFunc(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_Template(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_Change(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_Switch(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_OnOff(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_PID(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_Exec(),cat) ;
//		
//		cat = registerCat(new MNCat("_dev")) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_TagReader(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_TagWriter(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_TagFilter(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_TagFilterW(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NS_TagValChgTrigger(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NS_TagEvtTrigger(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NS_ConnInMsgTrigger(),cat) ;
//		//registerItem(new NS_TagAlertTrigger(),cat) ;
//		
//		
//		cat = registerCat(new MNCat("_net")) ;
//		registerItem("org.iottree.ext.msg_net.Kafka_M",cat) ;
//		registerItem("org.iottree.ext.msg_net.Mqtt_M",cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_HttpClient(),cat) ;
//		registerItem("org.iottree.ext.msg_net.BACnet_M",cat) ;
//		
//		
//		cat = registerCat(new MNCat("_storage")) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_FileReader(),cat) ;
//		registerItem(new org.iottree.core.msgnet.nodes.NM_FileWriter(),cat) ;
//		registerItem(new org.iottree.core.msgnet.modules.DBSql(),cat) ;
//		registerItem("org.iottree.ext.msg_net.InfluxDB_M",cat) ;
//		
//		cat = registerCat(new MNCat("_sim")) ;
//		registerItem("org.iottree.pro.modbuss.MSBus_M",cat) ;
//
//	}
	
	
	private static void loadNodesFile(File f) throws IOException
	{
		JSONObject jo = Convert.readFileJO(f) ;
		JSONArray catjarr = jo.optJSONArray("cats") ;
		int n = catjarr.length() ;
		for(int i =0 ; i < n ; i ++)
		{
			JSONObject catjo = catjarr.getJSONObject(i) ;
			String cat = catjo.getString("cat") ;
			String catt = catjo.optString("catt") ;
			MNCat mncat = registerCat(new MNCat(cat,catt)) ;
			JSONArray nodes = catjo.optJSONArray("nodes") ;
			if(nodes!=null)
			{
				for(Object ob:nodes.toList())
				{
					String cn = (String)ob ;
					registerItem(cn,mncat) ;
				}
			}
			JSONArray modules =  catjo.optJSONArray("modules") ;
			if(modules!=null)
			{
				int nn = modules.length() ;
				for(int j = 0 ; j < nn ; j ++)
				{
					JSONObject mjo = modules.getJSONObject(j) ;
					String mcn = mjo.getString("cn") ;
					MNModule md = (MNModule)registerItem(mcn,mncat) ;
					if(md==null)
						continue ;
					JSONArray m_nodes = mjo.getJSONArray("nodes") ;
					for(Object ob:m_nodes.toList())
					{
						String cn = (String)ob ;
						registerNodeToModule(cn,md) ;
					}
				}
				
			}
		}
	}

	static
	{
		String msgnetdir = System.getProperty("iottree.msg_net") ;
		if(Convert.isNullOrEmpty(msgnetdir))
			throw new RuntimeException("no [iottree.msg_net] env property found") ;
		File nodesf = new File(msgnetdir+"nodes.json") ;
		if(!nodesf.exists())
			throw new RuntimeException("no nodes file found = "+nodesf.getAbsolutePath()) ;
		
		try
		{
			loadNodesFile(nodesf) ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		
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
	
	public static MNNode getNodeByClass(Class<?> c)
	{
		//System.out.println("find c ="+ c.getCanonicalName()) ;
		for(MNNode n:TP2NODE.values())
		{
			//System.out.println(n.getClass().getCanonicalName()) ;
			if(n.getClass().equals(c))
				return n ;
		}
		return null ;
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
	
	IMNContainer belongTo = null ;
	
	private ArrayList<MNNet> nets = null ; 
	
	
	
	private MNManager(IMNContainer prj)
	{
		this.belongTo = prj ;
	}
	
	public IMNContainer getBelongTo()
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
		
		File prjdir = this.belongTo.getMsgNetDir();//.getPrjSubDir() ;
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
		File prjdir = this.belongTo.getMsgNetDir();//.getPrjSubDir() ;
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
	
	/**
	 * 在Manager范围内，根据资源名称定位对应的节点
	 * @param res_name
	 * @return
	 */
	public MNBase findNodeByResName(String full_tp,String res_name)
	{
		List<MNNet> nets = this.listNets() ;
		if(nets==null)
			return null ;
		for(MNNet net:nets)
		{
			Map<String,MNModule> n2m = net.getModuleMapAll() ;
			if(n2m!=null)
			{
				for(MNModule m:n2m.values())
				{
					if(!full_tp.equals(m.getTPFull()))
						continue ;
					
					if(m instanceof IMNNodeRes)
					{
						if(res_name.equals(m.getMNResName()))
							return m ;
					}
				}
			}
			
			Map<String,MNNode> n2n = net.getNodeMapAll() ;
			if(n2n!=null)
			{
				for(MNNode m:n2n.values())
				{
					if(!full_tp.equals(m.getTPFull()))
						continue ;
					
					if(m instanceof IMNNodeRes)
					{
						if(res_name.equals(m.getMNResName()))
							return m ;
					}
				}
			}
		}
		
		return null ;
	}
	
	public List<String> listResNames(String full_tp)
	{
		ArrayList<String> rets = new ArrayList<>() ;
		List<MNNet> nets = this.listNets() ;
		if(nets==null)
			return rets ;
		for(MNNet net:nets)
		{
			Map<String,MNModule> n2m = net.getModuleMapAll() ;
			for(MNModule m:n2m.values())
			{
				if(!full_tp.equals(m.getTPFull()))
					continue ;
				
				if(m instanceof IMNNodeRes)
				{
					rets.add(m.getMNResName()) ;
				}
			}
			
			Map<String,MNNode> n2n = net.getNodeMapAll() ;
			for(MNNode m:n2n.values())
			{
				if(!full_tp.equals(m.getTPFull()))
					continue ;
				
				if(m instanceof IMNNodeRes)
				{
					rets.add(m.getMNResName()) ;
				}
			}
		}
		return rets ;
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
		return new File(this.belongTo.getMsgNetDir(), "./msg_net/prj.json"); //.getPrjSubDir()
	}

	private File getRTCxtFileNet(MNNet net)
	{
		return new File(this.belongTo.getMsgNetDir(), "./msg_net/net_" + net.getId() + ".json");
	}

	boolean RT_CXT_load() throws IOException
	{
		JSONObject jo = Convert.readFileJO(getRTCxtFilePrj());
		rtCxtPrj.RT_CXT_injectSavedVals(jo);

		for (MNNet net : this.listNets())
		{
			File cxtf = getRTCxtFileNet(net);
			try
			{
				jo = Convert.readFileJO(cxtf);
				net.RT_CXT_fromSavedJO(jo);
			}
			catch(Exception ee)
			{
				log.warn("MNManager in ["+belongTo.getMsgNetContainerId()+"] load net failed:"+cxtf.getCanonicalPath());
				//ee.printStackTrace();
			}
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
	
	
	public OutCallFunc getOutCallFunc(String net_name,String func)
	{
		MNNet net = this.getNetByName(net_name) ;
		if(net==null)
			return null ;
		
		for(MNNode n : net.getNodeMapAll().values())
		{
			if(n instanceof OutCallFunc)
			{
				OutCallFunc ocf = (OutCallFunc)n ;
				if(func.equals(ocf.getFuncName()))
					return ocf ;
			}
		}
		return null ;
	}
	
	public MNMsg RT_callNetFunc(String net_name,String func,MNMsg input)
	{
		OutCallFunc ocf = getOutCallFunc(net_name,func) ;
		//MNNet net = this.getNetByName(net_name) ;
		if(ocf==null)
			throw new IllegalArgumentException("no OutCallFunc node with name="+net_name+" found in net="+net_name) ;
		
		return ocf.RT_callByOutter(input) ;
	}
}
