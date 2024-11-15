package org.iottree.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.jsp.JspWriter;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;


/**
 * Connector and connections manager
 * it may support  tcp client,tcp server,com,http,UDP,MQTT and Sub-IOT-Tree
 * 
 * @author jason.zhu
 */
public class ConnManager
{
	protected static ILogger log = LoggerManager.getLogger(ConnManager.class) ;
	
	private static ConnManager instance = null ;
	
	public static ConnManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(ConnManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new ConnManager() ;
			return instance ;
		}
	}
	
	private HashMap<String,List<ConnProvider>> repid2cps = new HashMap<>() ;
	private HashMap<String,List<ConnJoin>> repid2cjs = new HashMap<>() ;
	
	private ConnManager()
	{}
	
	public List<ConnProvider> getConnProviders(String repid) // throws Exception
	{
		List<ConnProvider> cps = repid2cps.get(repid) ;
		if(cps!=null)
			return cps ;
		
		synchronized(this)
		{
			UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
			if(rep==null)
				return null ;//throw new Exception("no rep found") ;
			
			try
			{
				File cf = getConnFile(rep) ;
				cps = loadConnProviders(rep,cf) ;
				repid2cps.put(repid,cps) ;
				return cps ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return null ;
			}
		}
	}
	
	public ConnProvider getOrCreateConnProviderSingle(String prjid,String tp) throws Exception
	{
		List<ConnProvider> cps = getConnProviders(prjid) ;
		ConnProvider rcp = null ; 
		for(ConnProvider cp:cps)
		{
			if(cp.getProviderType().equals(tp))
			{
				rcp = cp;
				break;
			}
		}
		
		if(rcp!=null)
		{
			if(!rcp.isSingleProvider())
				return null ;
			return rcp ;
		}
		//create
		rcp = ConnProvider.newInstance(tp);
		if(!rcp.isSingleProvider())
			return null ;
		rcp.setName(tp);
		rcp = setConnProvider(prjid,rcp);
		return rcp ;
	}
	
	public ConnProvider getConnProviderById(String repid,String cpid) throws Exception
	{
		List<ConnProvider> cps = getConnProviders(repid) ;
		for(ConnProvider cp:cps)
		{
			if(cp.getId().equals(cpid))
				return cp ;
		}
		return null ;
	}
	
	public ConnProvider getConnProviderByName(String repid,String name) throws Exception
	{
		List<ConnProvider> cps = getConnProviders(repid) ;
		for(ConnProvider cp:cps)
		{
			if(name.equals(cp.getName()))
				return cp ;
		}
		return null ;
		
	}
	
	public ConnPt getConnPtById(String repid,String connid) throws Exception
	{
		List<ConnProvider> cps = getConnProviders(repid) ;
		for(ConnProvider cp:cps)
		{
			for(ConnPt conn:cp.listConns())
			{
				if(connid.equals(conn.getId()))
					return conn ;
			}
		}
		return null ;
	}
	
	/**
	 * get connproviders config file
	 * @return
	 */
	private File getConnFile(UAPrj rep)
	{
		File subdir = rep.getPrjSubDir() ;
		if(!subdir.exists())
			subdir.mkdirs() ;
		return new File(subdir,"conns.xml") ;
	}
	
	
	private List<ConnProvider> loadConnProviders(UAPrj rep,File cf) throws Exception
	{
		ArrayList<ConnProvider> rets = new ArrayList<>() ;
		XmlData xd = XmlData.readFromFile(cf) ;
		if(xd==null)
		{
			//add tcpclient
			//rets.add(new ConnProviderTcpClient()) ;
			return rets;
		}
		
		List<XmlData> cpxds = xd.getSubDataArray("cps") ;
		if(cpxds==null)
			return rets ;
		for(XmlData cpxd:cpxds)
		{
			StringBuilder failedr = new StringBuilder() ;
			ConnProvider cp = ConnProvider.parseFromXmlData(cpxd, failedr) ;
			if(cp==null)
			{
				log.warn(" warning: load ConnProvider failedr") ;
				continue ;
			}
			cp.belongTo = rep ;
			rets.add(cp) ;
		}
		return rets;
	}
	
//	private void saveConnProviders(File f,List<ConnProvider> cps) throws Exception
//	{
//		
//	}
	
	void saveConnProvidersByPrjId(String prjid) throws Exception
	{
		UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
		if(prj==null)
			throw new Exception("no rep found with id="+prjid) ;
		
		List<ConnProvider> cps = this.getConnProviders(prjid) ;
		XmlData xd = new XmlData() ;
		List<XmlData> xds = xd.getOrCreateSubDataArray("cps") ;
		for(ConnProvider cp:cps)
		{
			xds.add(cp.toXmlData()) ;
		}
		File f = this.getConnFile(prj) ;
		XmlData.writeToFile(xd, f);
	}
	
	public ConnProvider setConnProvider(String prjid,ConnProvider cp) throws Exception
	{
		List<ConnProvider> cps = this.getConnProviders(prjid) ;
		if(!cps.contains(cp))
		{
			cps.add(cp) ;
			UAPrj rep = UAManager.getInstance().getPrjById(prjid) ;
			if(rep==null)
				throw new Exception("no rep found with id="+prjid) ;
			cp.belongTo = rep;
		}
		saveConnProvidersByPrjId(prjid) ;
		return cp ;
	}
	

	/**
	 * set connprovider with connprovider type and input json string
	 * this method is called by admin ui web page
	 * @param repid
	 * @param cp_tp
	 * @param jsonstr
	 * @return
	 */
	public ConnProvider setConnProviderByJson(String repid,String jsonstr) throws Exception
	{
		JSONObject jo = new JSONObject(jsonstr) ;
		String id = jo.getString("id") ;
		String cp_tp = jo.getString("tp") ;
		if(Convert.isNullOrEmpty(cp_tp))
			throw new Exception("no ConnProvider type input") ;
		ConnProvider cp = null ;
		if(Convert.isNullOrEmpty(id))
		{
			cp = ConnProvider.newInstance(cp_tp) ;
			if(cp==null)
				throw new Exception("cannot create ConnProvider instance with type="+cp_tp) ;
		}
		else
		{
			cp = getConnProviderById(repid,id) ;
			if(cp==null)
			{
				cp = ConnProvider.newInstance(cp_tp) ;
			}
			if(cp==null)
				throw new Exception("cannot get ConnProvider instance with id="+id) ;
		}
		
		//check name
		String name = jo.getString("name") ;
		if(Convert.isNullOrEmpty(name))
			throw new Exception("input json must has name param") ;
		ConnProvider oldcp = this.getConnProviderByName(repid, name) ;
		if(oldcp!=null&&oldcp!=cp)
			throw new Exception("ConnProvider with name="+name+" is already existed!") ;
		
		//inject
		cp.injectByJson(jo);
		//save
		List<ConnProvider> cps = getConnProviders(repid) ;
		if(oldcp==null)
			cps.add(cp) ;
		UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
		if(rep==null)
			throw new Exception("no rep found") ;
		cp.belongTo = rep ;
		
		setConnProvider(repid,cp) ;
		return cp;
	}
	
	
	public boolean delConnProvider(String repid,String cpid) throws Exception
	{
		ConnProvider cp = this.getConnProviderById(repid, cpid) ;
		if(cp==null)
			return false;
		if(cp.isRunning())
			throw new Exception("Connector Provider is running") ;
		
		List<ConnProvider> cps = this.getConnProviders(repid) ;
		cps.remove(cp) ;
		saveConnProvidersByPrjId(repid) ;
		return true ;
	}
	
	

	public List<ConnJoin> getConnJoins(String repid) throws Exception
	{
		List<ConnJoin> rets = getOrLoadConnJoins(repid) ;
		
		ArrayList<ConnJoin> invalidcjs = new ArrayList<>() ;
		synchronized(this)
		{
			for(ConnJoin cj:rets)
			{
				if(!this.checkConnJoinValid(repid, cj))
					invalidcjs.add(cj) ;
			}
			if(invalidcjs.size()>0)
			{
				rets.removeAll(invalidcjs) ;
			}
		}
		
		return rets ;
	}
	
	private List<ConnJoin> getOrLoadConnJoins(String repid) throws Exception
	{
		List<ConnJoin> cjs = repid2cjs.get(repid) ;
		if(cjs!=null)
			return cjs ;
		
		synchronized(this)
		{
			UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
			if(rep==null)
				throw new Exception("no rep found") ;
			
			File cf = getConnJoinFile(rep) ;
			cjs = loadConnJoins(cf) ;
			repid2cjs.put(repid,cjs) ;
			return cjs ;
		}
	}
	
	public ConnJoin getConnJoinByConnId(String repid,String connid) throws Exception
	{
		List<ConnJoin> cjs = getConnJoins(repid) ;
		for(ConnJoin cj:cjs)
		{
			if(connid.equals(cj.getConnId()))
				return cj ;
		}
		return null ;
	}
	
	public ConnJoin getConnJoinByNodeId(String repid,String chid) throws Exception
	{
		List<ConnJoin> cjs = getConnJoins(repid) ;
		for(ConnJoin cj:cjs)
		{
			if(chid.equals(cj.getNodeId()))
				return cj ;
		}
		return null ;
	}
	
	/**
	 * check ConnJoin by check ConnPt and UACh exists
	 * @param cj
	 * @return
	 * @throws Exception 
	 */
	public boolean checkConnJoinValid(String repid,ConnJoin cj) throws Exception
	{
		ConnPt conn = getConnPtById(repid,cj.getConnId()) ;
		if(conn==null)
			return false;
		UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
		if(rep==null)
			throw new Exception("no prj ") ;
		UACh ch = rep.getChById(cj.getRelatedChId()) ;
		if(ch==null)
			return false;
		String devid = cj.getRelatedDevId();
		if(Convert.isNotNullEmpty(devid))
		{
			UADev dev = ch.getDevById(devid) ;
			if(dev==null)
				return false;
		}
		return ch!=null ;
	}
	
	/**
	 * get joined connprovider by ch
	 * @param repid
	 * @param chid
	 * @return
	 * @throws Exception
	 */
	public ConnProvider getConnJoinedProvider(String repid,String nodeId) throws Exception
	{
		ConnJoin cj = getConnJoinByNodeId(repid,nodeId) ;
		if(cj==null)
			return null ;
		List<ConnProvider> cps = getConnProviders(repid) ;
		if(cps==null||cps.size()<=0)
			return null ;
		for(ConnProvider cp:cps)
		{
			List<ConnPt> conns = cp.listConns() ;
			if(conns==null)
				continue ;
			for(ConnPt conn:conns)
			{
				if(cj.getConnId().equals(conn.getId()))
					return cp ;
			}
		}
		return null ;
	}
	
	/**
	 * 
	 * @param repid
	 * @param connid
	 * @param nodeid  ch_chid  or dev_chid-devid
	 * @return
	 * @throws Exception
	 */
	public ConnJoin setConnJoin(String repid,String connid,String nodeid) throws Exception
	{
		String chid,devid ;
		if(nodeid.startsWith("ch_"))
		{
			nodeid = chid = nodeid.substring(3) ;
			devid = null ;
		}
		else if(nodeid.startsWith("dev_"))
		{
			nodeid = chid = nodeid.substring(4) ;
			int k = chid.indexOf('-') ;
			if(k<=0)
				throw new Exception("invalid nodeid="+nodeid) ;
			devid = chid.substring(k+1) ;
			chid = chid.substring(0,k) ;
		}
		else
			throw new Exception("invalid nodeid="+nodeid) ;
		
		ConnJoin oldcj = this.getConnJoinByConnId(repid, connid) ;
		ConnJoin oldch = this.getConnJoinByNodeId(repid, nodeid) ;
		if(oldcj!=null||oldch!=null)
		{
			throw new Exception("join is already existed!") ;
		}
		
		ConnPt cp = this.getConnPtById(repid, connid) ;
		UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
		if(rep==null)
			throw new Exception("no project found") ;
		
		UACh ch = rep.getChById(chid) ;
		if(cp==null||ch==null)
			throw new Exception("no Conn or Ch found") ;
		
		if(Convert.isNotNullEmpty(devid))
		{
			UADev dev = ch.getDevById(devid) ;
			if(dev==null)
				throw new Exception("no Device found") ;
		}
		
		List<ConnJoin> cjs = getConnJoins(repid);
		ConnJoin cj = new ConnJoin(connid,nodeid) ;
		cjs.add(cj) ;
		File cjf = getConnJoinFile(rep) ;
		saveConnJoins(cjf,cjs) ;
		
		cp.onJoinedChanged(cj);
		
		return cj ;
	}
	
	public boolean delConnJoin(String repid,String connid) throws Exception
	{
		ConnPt cp = this.getConnPtById(repid, connid) ;
		UAPrj rep = UAManager.getInstance().getPrjById(repid) ;
		if(rep==null)
			throw new Exception("no rep found") ;
		IJoinedNode jnode = cp.getJoinedNode() ;
		if(cp==null||jnode==null)
			return false;
		
		List<ConnJoin> cjs = getConnJoins(repid);
		ConnJoin cj = this.getConnJoinByConnId(repid, connid) ;
		if(cj==null)
			return false;
		cjs.remove(cj) ;
		File cjf = getConnJoinFile(rep) ;
		saveConnJoins(cjf,cjs) ;
		
		cp.onJoinedChanged(null);
		
		return true;
	}
	//public ConnJoin get
	
	private File getConnJoinFile(UAPrj rep)
	{
		File subdir = rep.getPrjSubDir() ;
		if(!subdir.exists())
			subdir.mkdirs() ;
		return new File(subdir,"conn_joins.xml") ;
	}
	
	private List<ConnJoin> loadConnJoins(File cjf) throws Exception
	{
		ArrayList<ConnJoin> rets = new ArrayList<>() ;
		XmlData xd = XmlData.readFromFile(cjf) ;
		if(xd==null)
		{
			return rets;
		}
		
		List<XmlData> cpxds = xd.getSubDataArray("cjs") ;
		if(cpxds==null)
			return rets ;
		for(XmlData cpxd:cpxds)
		{
			ConnJoin cp = new ConnJoin() ;
			cp.fromXmlData(cpxd);
			rets.add(cp) ;
		}
		return rets;
	}
	
	private void saveConnJoins(File cjf,List<ConnJoin> cjs) throws Exception
	{
		XmlData xd = new XmlData() ;
		List<XmlData> xds = xd.getOrCreateSubDataArray("cjs") ;
		for(ConnJoin cj:cjs)
		{
			xds.add(cj.toXmlData()) ;
		}
		
		XmlData.writeToFile(xd, cjf);
	}
	
	public static String getCJNodeId(String chid,String devid)
	{
		if(Convert.isNullOrEmpty(devid))
			return chid ;
		return chid+"-"+devid ;
	}

	public ConnPt getConnPtByNode(String repid,String chid,String devid) throws Exception
	{
		String nid = getCJNodeId(chid,devid);
		ConnJoin cj = getConnJoinByNodeId(repid,nid) ;
		if(cj==null)
			return null ;
		List<ConnProvider> cps = getConnProviders(repid) ;
		for(ConnProvider cp:cps)
		{
			for(ConnPt conn:cp.listConns())
			{
				if(cj.connId.equals(conn.getId()))
					return conn ;
			}
		}
		return null ;
	}
	
	
	public void renderRTJson(String repid,JspWriter out) throws Exception
	{
		List<ConnProvider> cps = getConnProviders(repid) ;
		out.print("[") ;
		boolean bf = true;
		for(ConnProvider cp:cps)
		{
			if(bf) bf=false;
			else out.print(",") ;
			boolean br = cp.isRunning() ;
			List<ConnMsg> cp_msgs = cp.getConnMsgs();
			out.print("{\"cp_id\":\""+cp.getId()+"\",\"run\":"+br+",\"msgs\":[") ;
			if(cp_msgs!=null)
			{
				boolean mbf = true ;
				for(ConnMsg cm:cp_msgs)
				{
					if(mbf) mbf = false;
					else out.print(",");
					out.print(cm.toListJsonStr());
				}
			}
			out.print("],\"connections\":[");
			
			List<ConnPt> conns = cp.listConns() ;
			boolean bf1=true;
			for(ConnPt conn:conns)
			{
				if(bf1) bf1=false;
				else out.print(",") ;
				
				
				String connerr = conn.getConnErrInfo() ;
				if(connerr==null)
					connerr = "" ;
				String conninf = conn.RT_getConnRunInfo() ;
				if(conninf==null)
					conninf = "" ;
//				boolean fnewdev = false;
//				if(conn instanceof ConnPtDevFinder)
//				{
//					Map<String,ConnDev> n2dev = ((ConnDevFindable)conn).getFoundConnDevs() ;
//					fnewdev = (n2dev!=null&&n2dev.size()>0) ;
//				} 
				out.print("{\"conn_id\":\""+conn.getId()+"\",\"enable\":"+conn.isEnable()+",\"ready\":"+conn.isConnReady()+",\"conn_err\":\""+connerr+"\",\"conn_inf\":\""+conninf+"\",\"msgs\":[");
				List<ConnMsg> cpt_msgs = conn.getConnMsgs() ;
				if(cpt_msgs!=null)
				{
					boolean mbf = true ;
					for(ConnMsg cm:cpt_msgs)
					{
						if(mbf) mbf = false;
						else out.print(",");
						out.print(cm.toListJsonStr());
					}
				}
				
				out.print("]}");
			}
			
			out.print("]}");
		}
		out.print("]");
	}
}
