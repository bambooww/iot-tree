package org.iottree.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.IXmlDataValidator;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * 
 * @author jason.zhu
 */
public abstract class ConnProvider implements IXmlDataValidator
{
	protected static ILogger log = LoggerManager.getLogger(ConnProvider.class) ;
	
	private static HashMap<String,Class<?>> tp2class = new HashMap<>() ;
	private static HashMap<String,ConnProvider> tp2cp = new HashMap<>() ;
	private static HashMap<String,JSONObject> tp2json_config = new HashMap<>() ;
	
	private static boolean registerProvider(JSONObject jo)
	{
		String classn = jo.getString("class") ;
		if(Convert.isNullOrEmpty(classn))
			return false;
		try
		{
			Class<?> c = Class.forName(classn) ;
			if(c==null)
				return false;
			
			ConnProvider ins = (ConnProvider)c.newInstance() ;
			if(ins==null)
				return false;
			String tp = ins.getProviderType() ;
			tp2cp.put(tp, ins) ;
			tp2class.put(tp, c) ;
			tp2json_config.put(tp, jo) ;
			System.out.println("load conn provider ["+tp+"] ok") ;
			return true ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private static boolean loadFromConfig() throws IOException
	{
		File df = DevManager.getDevDrvBase() ;
		File f = new File(df,"conn_providers.txt") ;
		if(!f.exists())
			return false;
		String jstr = Convert.readFileTxt(f, "utf-8") ;
		
		JSONArray jos= new JSONArray(jstr) ;
		int len = jos.length() ;
		for(int i = 0 ; i < len ; i ++)
		{
			JSONObject jo = jos.getJSONObject(i) ;
			registerProvider(jo) ;
		}
		return true;
	}
	
	static
	{
//		registerProvider("org.iottree.core.conn.ConnProTcpClient");
//		registerProvider("org.iottree.core.conn.ConnProTcpServer");
		
		try
		{
			loadFromConfig();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected static JSONObject getJSONConfigByTP(String cp_tp)
	{
		return tp2json_config.get(cp_tp) ;
	}

	/**
	 * create new ConnProvider with type
	 * @param cp_tp
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	static ConnProvider newInstance(String cp_tp) throws InstantiationException, IllegalAccessException
	{
		Class<?> c = tp2class.get(cp_tp) ;
		
		if(c==null)
		{
			return null ;
		}
		ConnProvider cp =  (ConnProvider)c.newInstance() ;
		//cp.setName(cp_tp);
		return cp ;
	}
	/**
	 * 获得所有的ConnProvider
	 * @return
	 */
	public static List<ConnProvider> getAllConnProviders()
	{
		ArrayList<ConnProvider> rets = new ArrayList<ConnProvider>() ;
		rets.addAll(tp2cp.values()) ;
		return rets ;
	}
	
	public static boolean hasConnProvider(String cptp)
	{
		return tp2cp.get(cptp)!=null;
	}
	
	static ConnProvider parseFromXmlData(XmlData xd,StringBuilder failedreson)
	{
		String tp = xd.getParamValueStr("type") ;
		if(Convert.isNullOrEmpty(tp))
		{
			if(failedreson!=null)
				failedreson.append("no type input!") ;
			return null ;
		}
		Class<?> c = tp2class.get(tp) ;
		
		if(c==null)
		{
			if(failedreson!=null)
				failedreson.append("invalid type="+tp) ;
			return null ;
		}
		
		try
		{
			ConnProvider ins = (ConnProvider)c.newInstance() ;
			if(ins==null)
			{
				if(failedreson!=null)
					failedreson.append("invalid type="+tp) ;
				return null;
			}
			
			//HashMap<String,String> pms = XmlHelper.getEleAttrNameValueMap(ele) ;
			if(!ins.fromXmlData(xd,failedreson))
			{
				return null ;
			}
			return ins ;
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
			return null ;
		}
	}
	
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	boolean bEn = true ;//enable or not
	
	
	private ArrayList<ConnPt> connPts = new ArrayList<>() ;
	
	transient UAPrj belongTo = null ;
	
	private Thread runTh = null;
	
	protected ConnListener connLis = null ;
	
	public ConnProvider()
	{
		id = CompressUUID.createNewId();
	}
	

	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("type", getProviderType()) ;
		xd.setParamValue("id", id) ;
		if(name!=null)
			xd.setParamValue("name",name) ;
		if(title!=null)
			xd.setParamValue("title", title);
		if(desc!=null)
			xd.setParamValue("desc", desc);
		xd.setParamValue("enable", bEn);
		
		List<XmlData> cptxds = xd.getOrCreateSubDataArray("cpts") ;
		for(ConnPt cp:connPts)
		{
			XmlData cpxd = cp.toXmlData() ;
			cptxds.add(cpxd) ;
		}
		return xd ;
	}
	
	void setName(String name)//,String title)
	{
		this.name = name ;
		//this.title = title ;
	}
	
	public boolean fromXmlData(XmlData xd,StringBuilder failedr)
	{
		this.id = xd.getParamValueStr("id") ;
		this.name = xd.getParamValueStr("name") ;
		if(Convert.isNullOrEmpty(this.id)||Convert.isNullOrEmpty(this.name))
		{
			failedr.append("id or name cannot be null empty") ;
			return false;
		}
		this.title = xd.getParamValueStr("title") ;
		this.desc = xd.getParamValueStr("desc") ;
		this.bEn = xd.getParamValueBool("enable", true) ;
		
		List<XmlData> cptxds = xd.getSubDataArray("cpts") ;
		if(cptxds!=null)
		{
			for(XmlData cptxd:cptxds)
			{
				ConnPt cpt = createEmptyConnPt() ;
				if(!cpt.fromXmlData(cptxd, failedr))
					return false;
				cpt.belongTo = this ;
				this.connPts.add(cpt) ;
			}
		}
		return true ;
	}
	
	/**
	 * call by web admin page edit
	 * if
	 * @param jo
	 * @throws Exception
	 */
	protected void injectByJson(JSONObject jo) throws Exception
	{
		if(this.isRunning())
			throw new Exception("ConnProvider is running..") ;
		String id = jo.getString("id") ;
		if(Convert.isNotNullEmpty(id))
			this.id = id ; 
		String tmpn = jo.getString("name") ;
		if(Convert.isNullOrEmpty(tmpn))
			throw new Exception("input json must has name param") ;
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(tmpn, true, sb))
			throw new Exception(sb.toString()) ;
		
		this.name = tmpn ; 
		this.title = jo.getString("title") ;
		this.desc = jo.getString("desc") ;
		this.bEn = jo.getBoolean("enable") ;
		
	}
	
	
	public UAPrj getBelongTo()
	{
		return belongTo ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getName()
	{
		if(name==null)
			return "" ;
		return name ;
	}
	
	public String getTitle()
	{
		if(title==null)
			return "";
		return title ;
		
	}
	
	public String getDesc()
	{
		if(this.desc==null)
			return "" ;
		return desc ;
	}
	
	public String getStaticTxt()
	{
		return "" ;
	}
	
	public boolean isEnable()
	{
		return bEn ;
	}
	
	
	public void save() throws Exception
	{
		ConnManager.getInstance().setConnProvider(this.belongTo.getId(), this) ;
	}
	
	public void setConnPt(ConnPt cp) throws Exception
	{
		if(!this.connPts.contains(cp))
		{
			cp.belongTo = this ;
			this.connPts.add(cp) ;
		}
		this.save();
	}
	
	public void setConnPtByJson(String jsonstr) throws Exception
	{
		JSONObject jo = new JSONObject(jsonstr) ;
		setConnPtByJson(jo);
	}
	
	public ConnPt setConnPtByJson(JSONObject jo) throws Exception
	{
		String id = jo.getString("id") ;
		ConnPt cpt = this.getConnById(id) ;
		boolean badd = false;
		if(cpt==null)
		{
			cpt = this.createEmptyConnPt() ;
			badd = true ;
		}
		//check name
		String name = jo.getString("name") ;
		if(Convert.isNullOrEmpty(name))
			throw new Exception("input json must has name param") ;
		ConnPt oldcp = this.getConnByName(name);
		if(oldcp!=null&&!id.equals(oldcp.getId()))
			throw new Exception("ConnProvider with name="+name+" is already existed!") ;
		
		//inject
		cpt.injectByJson(jo);
		setConnPt(cpt) ;
		return cpt ;
	}
	
	public boolean delConnPt(String connid) throws Exception
	{
		for(ConnPt cp:this.connPts)
		{
			if(cp.getId().equals(connid))
			{
				this.connPts.remove(cp) ;
				this.save();
				return true ;
			}
		}
		return false;
	}
//	public String getTitleId()
//	{
//		return title + "["+id+"]";
//	}


	
	void setConnListener(ConnListener lis)
	{
		connLis = lis ;
	}

	/**
	 * 
	 * @return
	 */
	public abstract String getProviderType() ;
	
	public abstract boolean isSingleProvider() ;
	
	public abstract Class<? extends ConnPt> supportConnPtClass() ;
	
	private ConnPt createEmptyConnPt()
	{
		try
		{
			Class<? extends ConnPt> c = supportConnPtClass() ;
			return c.newInstance() ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null ;
		}
	}
	/**
	 * 
	 * @return
	 */
	public final List<DevDriver> supportDrivers()
	{
		Class<? extends ConnPt> supc = supportConnPtClass();
		
		List<DevDriver> drvs = DevManager.getInstance().getDrivers() ;
		ArrayList<DevDriver> rets =new ArrayList<>() ;
		for(DevDriver drv:drvs)
		{
			Class<? extends ConnPt> c = drv.supportConnPtClass() ;
			if(c==null)
				continue ;
			if(c.isAssignableFrom(supc))
				rets.add(drv);
		}
		return rets ;
	}
	
	public final boolean supportDriverChk(DevDriver drv)
	{
		Class<? extends ConnPt> c = drv.supportConnPtClass() ;
		if(c==null)
			return false ;
		
		Class<? extends ConnPt> supc = supportConnPtClass();
		return supc.isAssignableFrom(c) ;
	}
	
	public List<ConnPt> listConns()
	{
		return this.connPts ;
	}

	public ConnPt getConnById(String id)
	{
		List<ConnPt> conns =  listConns() ;
		if(conns==null)
			return null ;
		for(ConnPt conn:conns)
		{
			if(id.equals(conn.getId()))
				return conn ;
		}
		return null ;
	}
	
	public ConnPt getConnByName(String name)
	{
		List<ConnPt> conns =  listConns() ;
		if(conns==null)
			return null ;
		for(ConnPt conn:conns)
		{
			if(name.equals(conn.getName()))
				return conn ;
		}
		return null ;
	}

	protected void fireConnConnected(ConnPt cp)
	{
		if(connLis==null)
			return ;
		connLis.onConnPtConnected(cp);
	}
	
	public List<ConnMsg> getConnMsgs()
	{
		return null ;
	}
	
	public final ConnMsg getConnMsgById(String msgid)
	{
		List<ConnMsg> ms = getConnMsgs() ;
		if(ms==null)
			return null ;
		for(ConnMsg cm:ms)
		{
			if(cm.getId().equalsIgnoreCase(msgid))
				return cm ;
		}
		return null ;
	}
	
	protected abstract long connpRunInterval() ;
	
	
	protected void RT_connpInit() throws Exception
	{
		
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	protected abstract void connpRunInLoop() throws Exception;

	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				RT_connpInit();
				
				for(ConnPt cp:listConns())
				{
					cp.RT_connInit() ;
				}
				
				while (runTh != null)
				{
					try
					{
						Thread.sleep(connpRunInterval());
					}
					catch (Exception e)
					{
					}
					
					if(runTh==null)
						break ;

					connpRunInLoop();
				}
			}
			catch (Exception e)
			{
				log.error(e);
				//e.printStackTrace();
			}
			finally
			{
				runTh = null;
				//stop();
			}
		}
	};
	
	
	public void start()  throws Exception
	{
		if (runTh != null)
			return;

		synchronized (this)
		{
			if (runTh != null)
				return;
			
			runTh = new Thread(runner,"iottree-connp-"+this.getName());
			runTh.start();
		}
	}

	public void stop()
	{
		if (runTh == null)
			return;

		synchronized (this)
		{
			if (runTh == null)
				return;
			
			//runTh.interrupt();
			runTh = null;
			
//			try
//			{
//				Thread.sleep(connpRunInterval());
//			}
//			catch (Exception e)
//			{
//			}
		}
	}
	
	
	public boolean isRunning()
	{
		return runTh != null ;
	}
	
//	/**
//	 * ConnProvider impl may provider some promp msg to user
//	 * 1)new conn or new device found
//	 * 2)may license not support
//	 * 3) some error info ;
//	 * @return
//	 */
//	public String getPromptMsg()
//	{
//		return null;
//	}

	public void dispose()
	{
		stop();
	}
	
	public void reInit() throws Exception
	{
		
	}
}
