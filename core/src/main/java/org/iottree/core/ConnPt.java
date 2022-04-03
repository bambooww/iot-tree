package org.iottree.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.ConnPt.MonItem;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.IXmlDataValidator;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

/**
 * connection point
 * 
 * @author jason.zhu
 *
 */
public abstract class ConnPt implements IXmlDataValidator
{
	/**
	 * 存放上下行数据
	 */
	public static class MonItem
	{
		/**
		 * true-input  false=output
		 */
		boolean bInput = true; 
		/**
		 * 第一个字节时间
		 */
		long stDT = -1 ;
		
		/**
		 * 最后一字节时间
		 */
		long endDT = -1 ;
		
		byte[] monBuf = null;//new byte[MAX_MON_BLOCKLEN] ;
		
		//ByteArrayOutputStream baso = null ;
		
		int monLen = 0 ;
		
//		MonItem(boolean binput,int b)
//		{
//			bInput = binput ;
//			endDT = stDT = System.currentTimeMillis() ;
//			monBuf[0] = (byte)b ;
//			monLen = 1 ;
//		}
		
		MonItem(boolean binput,byte[] bs)
		{
			bInput = binput ;
			endDT = stDT = System.currentTimeMillis() ;
			//monBuf[0] = (byte)b ;
			monBuf = bs ;
			monLen = bs.length ;
		}
		
		public boolean isInput()
		{
			return bInput ;
		}
		
		public boolean isOutput()
		{
			return !bInput ;
		}
		
		public long getStartDT()
		{
			return stDT ;
		}
		
		public long getEndDT()
		{
			return endDT ;
		}
		
		/**
		 * 获得监视数据长度
		 * @return
		 */
		public int getMonDataLen()
		{
			return monLen ;
		}
		
		public byte[] getMonData()
		{
			return monBuf ;
		}
		
	}
	
	public static final int MAX_MON_BLOCKLEN = 1024 ;
	
	
	
	private transient int monListMaxLen = 100 ;
	
	
	/**
	 * 监视列表
	 */
	private transient LinkedList<MonItem> monItemList = new LinkedList<MonItem>() ;
	
	
	/**
	 * autoid
	 */
	private String id = null ;
	
	/**
	 * name to fit connection like socket or others
	 */
	private String name = null ;
	
	private String title = null ;
	
	private String desc = null ;
	
	private boolean bEnable = true ;
	
	//private String staticTxt = "" ;

	transient ConnProvider belongTo = null ;
	
	private transient long createDT = -1 ;
	
	private transient long lastUsedDT = 0 ;
	
	transient private DevDriver bindedDrv = null ;
	
	/**
	 * create a empty connpt,it will be injected to old data
	 */
	public ConnPt()
	{
		this.id = CompressUUID.createNewId();
	}
	
	/**
	 * create a new ConnPt
	 * @param cp
	 * @param name
	 * @param title
	 */
	public ConnPt(ConnProvider cp,String name,String title,String desc)
	{
		this.belongTo = cp ;
		this.createDT = System.currentTimeMillis() ;
		this.id = CompressUUID.createNewId();
		this.name = name ;
		this.title = title ;
		this.desc = desc ;
	}
	
	public abstract String getConnType() ;
	
	@Override
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		xd.setParamValue("id",id);
		xd.setParamValue("name", name);
		xd.setParamValue("title", title);
		if(desc!=null)
			xd.setParamValue("desc", desc);
		xd.setParamValue("enable", bEnable);
		return xd ;
	}
	
	public boolean fromXmlData(XmlData xd,StringBuilder failedr)
	{
		this.id = xd.getParamValueStr("id") ;
		this.name = xd.getParamValueStr("name") ;
		this.title = xd.getParamValueStr("title") ;
		this.desc = xd.getParamValueStr("desc") ;
		this.bEnable = xd.getParamValueBool("enable", true) ;
		if(Convert.isNullOrEmpty(this.id)||Convert.isNullOrEmpty(this.name))
		{
			failedr.append("no id name") ;
			return false;
		}
		return true ;
	}
	
	protected void injectByJson(JSONObject jo) throws Exception
	{
		if(this.isConnReady())
			throw new Exception("Connection is ready") ;
		String id = jo.getString("id") ;
		if(Convert.isNotNullEmpty(id))
			this.id = id ; 
		this.name = jo.getString("name") ;
		this.title = jo.optString("title") ;
		if(Convert.isNullOrEmpty(this.title))
			this.title = this.name;
		this.desc = jo.optString("desc") ;
		this.bEnable = jo.optBoolean("enable") ;
		if(Convert.isNullOrEmpty(name))
			throw new Exception("input json must has name param") ;
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(name, true, sb))
			throw new Exception(sb.toString()) ;
	}
	
//	/**
//	 * ConnPt implements or extender ,may has multi types name
//	 */
//	private transient HashMap<Class<?>,List<String>> connc2tps = new HashMap<>() ;
//	/**
//	 * get conn types that this connpt can have.
//	 * extends implements may make mulit types
//	 * @return
//	 * @throws SecurityException 
//	 * @throws NoSuchFieldException 
//	 * @throws IllegalAccessException 
//	 * @throws IllegalArgumentException 
//	 */
//	private static List<String> getConnTPs(Class<?> c) throws Exception
//	{
//		if(connTPS!=null)
//			return connTPS ;
//		
//		ArrayList<String> rets = new ArrayList<>() ;
//		
//		ArrayList<Class<?>> dcs = new ArrayList<>() ;
//		Class<?> curc = this.getClass() ;
//		do
//		{
//			dcs.add(curc) ;
//			curc = curc.getSuperclass() ;
//		}while(curc!=java.lang.Object.class) ;
//		
//		//Class<?> dcs[] = this.getClass().getDeclaredClasses();
//		for(Class<?> dc:dcs)
//		{
//			try
//			{
//				Field f = dc.getDeclaredField("TP") ;
//				if(f==null)
//					continue ;
//				String v = (String)f.get(dc) ;
//				rets.add(v) ;
//			}
//			catch(NoSuchFieldException ee)
//			{}
//		}
//		connTPS = rets ;
//		return rets ;
//	}
	/**
	 * unique id
	 * @return
	 */
	public String getId()
	{
		return id ;
	}
	
	/**
	 * unique conn id
	 * @return
	 */
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
		if(desc==null)
			return "" ;
		return desc ;
	}
	

	public boolean isEnable()
	{
		return bEnable;
	}
	
	
	
	public boolean isValid()
	{
		return Convert.isNotNullEmpty(this.name);
	}
	
	/**
	 * show static config or param brief information in ui
	 * @return
	 */
	public abstract String getStaticTxt();
	
	/**
	 * dynamic info
	 * @return
	 */
	public String getDynTxt()
	{
		return "" ;
	}
	//public abstract String getConnTp() ;
	
	public ConnProvider getConnProvider()
	{
		return belongTo ;
	}
	

	public long getLastUsedDT()
	{
		return lastUsedDT ;
	}
	
	protected void setUsed()
	{
		this.lastUsedDT = System.currentTimeMillis();
	}
	
	public long getCreatedDT()
	{
		return createDT ;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public UACh getJoinedCh() throws Exception
	{
		UAPrj rep = this.belongTo.getBelongTo();
		ConnJoin cj = ConnManager.getInstance().getConnJoinByConnId(rep.getId(),this.id) ;
		if(cj==null)
			return null ;
		String chid = cj.getChId() ;
		return rep.getChById(chid) ;
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
	
	protected void RT_connInit()
	{}
	
	public void onJoinedChanged(ConnJoin cj)
	{}
	
	public abstract boolean isConnReady() ;
	
	public abstract String getConnErrInfo() ;
	
	public boolean hasJoinedCh() throws Exception
	{
		return getJoinedCh()!=null ;
	}
	
	/**
	 * ConnPt is binded by Driver
	 * @param drv
	 */
	protected void onDriverBinded(DevDriver drv)
	{
		this.bindedDrv = drv ;
		if(this.isConnReady())
			this.fireConnReady();
	}
	/**
	 * ConnPt is unbinded by Driver
	 * @param drv
	 */
	protected void onDriverUnbinded()
	{
		this.bindedDrv = null ;
	}
	
	public DevDriver getBindedDriver()
	{
		return this.bindedDrv ;
	}
	
	protected void fireConnReady()
	{
		onConnReadyOrNot(true) ;
		
		if(this.bindedDrv==null)
			return ;
		this.bindedDrv.RT_onConnReady(this);
	}
	
	protected void fireConnInvalid()
	{
		onConnReadyOrNot(false) ;
		if(this.bindedDrv==null)
			return ;
		this.bindedDrv.RT_onConnInvalid(this);
	}
	
	/**
	 * override it will do something
	 * @param r
	 */
	protected void onConnReadyOrNot(boolean r)
	{
		
	}

	protected  void onMonDataRecv(boolean binputs,byte[] bs)
	{
		MonItem curmi = new MonItem(binputs,bs) ; ;
		synchronized(this)
		{
			monItemList.add(curmi) ;
			if(monItemList.size()>=monListMaxLen)
				monItemList.removeFirst() ;
		}
	}
	
	public boolean setMonListMaxLen(int len)
	{
		if(len<=0)
			return false ;
		
		if(len<monListMaxLen)
		{
			int s = monItemList.size() - len ;
			if(s>0)
			{
				for(int i = 0 ; i < s ; i ++)
					monItemList.removeFirst();
			}
		}
		monListMaxLen = len ;
		return true ;
	}
	
	/**
	 * 获得所有的监视列表
	 * @return
	 */
	public synchronized List<MonItem> getMonitorList()
	{
		int s = monItemList.size() ;
		ArrayList<MonItem> rets = new ArrayList<>(s) ;
		rets.addAll(monItemList) ;
		return rets ;
	}

	public String toString()
	{
		return this.id+" "+Convert.toFullYMDHMS(new Date(createDT))+"-"+Convert.toFullYMDHMS(new Date(lastUsedDT)) ;
	}
	
}
