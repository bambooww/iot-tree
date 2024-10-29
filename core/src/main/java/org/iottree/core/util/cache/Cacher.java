package org.iottree.core.util.cache;

import java.io.*;
import java.util.*;



/**
 * In one java vm,running a application like web server may has many bottleneck.
 * Such as access db,read file and create a complex object etc.
 * <p>
 * The <code>Cacher</code> class is a main class that in a java vm.<br/>
 * 1,all running procedure can use a general cacher.<br/> 2,in general
 * cacher,there are many sub cacher,and every one has a name. So,different
 * program mode can use different sub cacher and it can use any name to cache
 * its data withou conflict.<br/> 3,cache type:<br/> (1),CACHER_FOREVER:every
 * data object can in cache for ever until the data with the same name replace
 * it. (2),CACHER_TIMEOUT:every data object has a timeout value to be set before
 * being placed into cache.<br/> <b>in general cacher,a moniter thread is
 * always runing to detect data which is timeout and remove it from cache.</b><br/>
 * (3),CACHER_LENGTH: The cache has a limited length. For it is difficult to get
 * a object's memory usage.so the limited length is only for object number. And
 * the cache use LRU algorithm to to remove old data when a new data come into a
 * full cache. 3,The general cacher may be can automatically check java vm
 * runtime status,and can use some strategy to adjust self. For example.when
 * moniter find that java vm freeMemory/totlaMemory is small than a special
 * value,it can walk through the whole cacher and remove some data which is not
 * used for a long time. 
 * 
 * @author Jason.zhu
 * @since
 */
public class Cacher<K,V>
{
	public static boolean _DEBUG = false;

	public static final short UNKNOW_INIT = 0;

	public static final short BEFORE_INIT = 1;

	public static final short DURING_INIT = 2;

	public static final short AFTER_INIT = 3;

	// (("true").equals(Configuration.getProperty("platform.release")))?false:true
	// ;
	/***************************************************************************
	 * ������
	 **************************************************************************/
	public static final int DEFAULT_CACHE_LENGTH = Integer.MAX_VALUE;

	/***************************************************************************
	 * ������
	 **************************************************************************/
	static String SYNER_CONF_FILE = null;

	static Properties SYNER_PROP = new Properties();

	static Properties SYNER_PREFIX_PROP = new Properties();

	// static
	// {
	// File f = null;
	// FileInputStream fis = null;
	// CacherLogger.startLog();
	// try
	// {
	// SYNER_CONF_FILE = Configuration.getProperty("conf.directory") +
	// "cachersyner.conf";
	// f = new File(SYNER_CONF_FILE);
	// if (f.exists())
	// {
	// fis = new FileInputStream(f);
	// SYNER_PROP.load(fis);
	//
	// for (Enumeration en = SYNER_PROP.propertyNames();
	// en.hasMoreElements(); )
	// {
	// String cn = (String) en.nextElement();
	// if (!cn.endsWith("*"))
	// {
	// continue;
	// }
	// String val = SYNER_PROP.getProperty(cn);
	// SYNER_PREFIX_PROP.setProperty(cn.substring(0,
	// cn.length() - 1), val);
	// }
	//
	// } //end of if(f.exists())
	// }
	// catch (Throwable e)
	// {
	// //e.printStackTrace();
	// }
	// finally
	// {
	// try
	// {
	// if (fis != null)
	// {
	// fis.close();
	// }
	// }
	// catch (IOException ioe)
	// {
	// ioe.printStackTrace();
	// }
	// }
	// }

	static Hashtable<String,Cacher<?,?>> allCachers = new Hashtable<>();

	static String DEFAULT_CACHER = "DEFAULT_CACHER";

	// static CacherSyner cacherSyn = CacherSyner.getInstance();

	public static Cacher<?,?> getCacher()
	{
		return getCacher(DEFAULT_CACHER);
	}

	synchronized public static <K,V> Cacher<K,V> getCacher(String name)
	{
		if (name.indexOf('*') >= 0)
		{
			throw new RuntimeException("Cacher Error:Wrong name [" + name
					+ "] Cache Name Cannot Contain [*]!");
		}

		Cacher<K,V> tmpc = (Cacher<K,V>) allCachers.get(name);
		if (tmpc == null)
		{
			tmpc = new Cacher<K,V>(name);

			String syntype = SYNER_PROP.getProperty(name);
			if (syntype == null)
			{ // may be cachename*
				for (Enumeration<?> en = SYNER_PREFIX_PROP.propertyNames(); en
						.hasMoreElements();)
				{
					String tmps = (String) en.nextElement();
					if (name.startsWith(tmps))
					{
						syntype = SYNER_PREFIX_PROP.getProperty(tmps);
						break;
					}
				}
			}

			// if (syntype != null)
			// {
			// if ("syn_dirty".equals(syntype.toLowerCase()))
			// {
			// tmpc.setSynType(CacherSyner.SYN_DIRTY);
			// }
			// else if ("syn_change".equals(syntype.toLowerCase()))
			// {
			// tmpc.setSynType(CacherSyner.SYN_CHANGE);
			// }
			// else if ("syn_same".equals(syntype.toLowerCase()))
			// {
			// tmpc.setSynType(CacherSyner.SYN_SAME);
			// }
			// else if ("syn_same_mem".equals(syntype.toLowerCase()))
			// {
			// tmpc.setSynType(CacherSyner.SYN_SAME_MEM);
			// tmpc.sameMemInit();
			//
			// }
			// }
			allCachers.put(name, tmpc);
		}
		return tmpc;
	}

	// public static Cacher getCacher(String name, short syn)
	// {
	// Cacher tmpc = (Cacher) allCachers.get(name);
	// if (tmpc == null)
	// {
	// tmpc = new Cacher(name, syn);
	// allCachers.put(name, tmpc);
	// }
	// tmpc.setSynType(syn);
	// return tmpc;
	// }

	public static boolean isExistedCacher(String name)
	{
		return (allCachers.get(name) != null);
	}

	/**
	 * ����ǰ׺���õ�������ص�Cacher����
	 * 
	 * @param pname
	 *            ������ͨ���
	 */
	synchronized public static <K,V> Cacher<K,V>[] getCachersByPrefixName(String pname)
	{
		Vector v = new Vector();

		Enumeration en = allCachers.elements();
		Cacher tmpc = null;
		while (en.hasMoreElements())
		{
			tmpc = (Cacher) en.nextElement();
			if (tmpc.getName().startsWith(pname))
			{
				v.addElement(tmpc);
			}
		}

		int s = v.size();
		Cacher[] retcs = new Cacher[s];
		for (int i = 0; i < s; i++)
		{
			retcs[i] = (Cacher) v.elementAt(i);
		}

		return retcs;
	}

	/**
	 * @return String[] impossible to be null.
	 */
	public static String[] getAllCacherNames()
	{
		int s = allCachers.size();
		Enumeration en = allCachers.keys();
		String[] rets = new String[s];
		for (int i = 0; i < s; i++)
		{
			rets[i] = (String) en.nextElement();
		}
		return rets;
	}

	/***************************************************************************
	 * ������
	 **************************************************************************/
	
	static long checkInterval = 60000;
	
//	/**
//	 * ����̶߳���
//	 */
//	static SoleThread monThread = null;
//
//	static MonRunner monRunner = null;
//
//	
//	static
//	{
//		// monRunner = new MonRunner();
//		// monThread = new SoleThread(monRunner);
//		// monThread.start();
//	}
//
//	static class MonRunner implements SoleRunnable
//	{
//		public void release()
//		{
//			System.out
//					.println(">>>>>Cacher MonRunner to be released because of SoleThread!!");
//		}
//
//		public void run(SoleThread st)
//		{
//			while (st.isSole())
//			{
//				try
//				{
//					cleanAllCacher();
//
//					Thread.sleep(checkInterval);
//				}
//				catch (Exception e)
//				{
//					if (_DEBUG)
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//
//	}
	
	
	static class CleanRunner implements Runnable
	{
		public void run()
		{
			cleanAllCacher();
		}

	}

	/**
	 * ���Cache�����еĹ�������,���ڳ��ڴ�
	 *
	 */
	private static void cleanAllCacher()
	{

		for (Enumeration en = allCachers.elements(); en.hasMoreElements();)
		{
			Cacher tmpch = (Cacher) en.nextElement();
			tmpch.detectExpired();
		}

	}

	static Object cleanLocker = new Object() ;
	static long lastClean = -1 ;
	/**
	 * ÿ�δ�cache��ȡ����,���������,��Ӧ�õ��øķ���
	 * �÷�������һ���Ĳ���,������ʱ�����������
	 */
	private static void pulseClean()
	{
		if(System.currentTimeMillis()-lastClean<checkInterval)
			return ;
		
		synchronized(cleanLocker)
		{
			long cst = System.currentTimeMillis() ;
			if(cst-lastClean<checkInterval)//�ٴ�ȷ��
				return ;
			
			lastClean = cst ;
			
			CleanRunner cr = new CleanRunner();
			Thread t = new Thread(cr,"cache-clean_runner");
			t.start();
		}
	}
	/**
	 * ���ü���̼߳��ʱ����
	 */
	public static void setCheckInterval(long t)
	{
		checkInterval = t;
	}

	public static long getCheckInterval()
	{
		return checkInterval;
	}

	// //////////////////////////////////////////////////

	// ////////////////////
	/***************************************************************************
	 * ������
	 **************************************************************************/
	/**
	 * Cache������
	 */
	String strCacherName;

	/**
	 * ��Cache�Ƿ�ͬ��
	 */
	// short synType = CacherSyner.SYN_NO;
	/**
	 * 
	 */
	CacherBuffer cacherBuffer = null;

	/**
	 * 
	 */
	short initState = UNKNOW_INIT;

	/**
	 * for object implements AutoCloseable interface,
	 * when destroy from cache,close() will be called.
	 */
	boolean bAutoClose = false;
	/**
	 * 
	 */
	Vector duringInitBuf = new Vector();

	private Cacher(String name)
	{
		strCacherName = name;
		cacherBuffer = new CacherBuffer(this);
		// initer = new CacherIniter() ;
	}

	public Cacher<K,V> asAutoCloseSupport(boolean b)
	{
		bAutoClose = b ;
		return this ;
	}
	// private Cacher(String name, short syn)
	// {
	// this(name);
	// synType = syn;
	// }

	// private void sameMemInit()
	// {
	// String tip = cacherSyn.udp.getActiveMasterIP();
	// boolean bf = cacherSyn.checkFreezen(tip, this.strCacherName);
	// if (bf)
	// {
	// throw new RuntimeException("The Master Server[" + tip +
	// "] is in freezen state,please reboot this web server after a while!");
	// }
	//
	// }

	// synchronized public void setInitState(short st)
	// {
	// if (st < UNKNOW_INIT || st > AFTER_INIT)
	// {
	// initState = UNKNOW_INIT;
	// return;
	// }
	//
	// initState = st;
	// }
	//
	// public short getInitState()
	// {
	// return initState;
	// }
	//
	// synchronized public void beforeInit(boolean clearexisted)
	// {
	// //if(synType!=CacherSyner.SYN_SAME)
	// //throw new RuntimeException("Only SYN_SAME can using beforeInit()") ;
	//
	// initState = BEFORE_INIT;
	// if (clearexisted)
	// {
	// clear();
	// }
	// }
	//
	// synchronized public void beginInit()
	// {
	// //if(synType!=CacherSyner.SYN_SAME)
	// //throw new RuntimeException("Only SYN_SAME can using beginInit()") ;
	//
	// initState = this.DURING_INIT;
	// }
	//
	// synchronized public void endInit()
	// {
	// //if(synType!=CacherSyner.SYN_SAME)
	// //throw new RuntimeException("Only SYN_SAME can using endInit()") ;
	// Object o = null;
	// while (duringInitBuf.size() > 0)
	// {
	// o = duringInitBuf.remove(0);
	// //
	// if (o instanceof Shell)
	// {
	// Shell sh = (Shell) o;
	// Shell tmpsh = cacherBuffer.accessShell(sh.getKey());
	// if (tmpsh == null)
	// {
	// cacherBuffer.addShell(sh);
	// }
	// else
	// {
	// tmpsh.setContent(sh.getContent());
	// tmpsh.setRefresh(sh.isRefresh());
	// tmpsh.setLiveTime(sh.getLiveTime());
	// }
	// }
	// else if (o instanceof Object[])
	// {
	// Object[] os = (Object[]) o;
	// removeNoSyn(os[0]);
	// }
	// else if (o instanceof String)
	// {
	// if ("clear".equals( (String) o))
	// {
	// clearNoSyn();
	// }
	// }
	// }
	// initState = AFTER_INIT;
	// }

	/*
	 * synchronized public CacherIniter obtainIniter() {
	 * initer.setInitState(CacherIniter.BEFORE_INIT) ; return initer ; }
	 */
	/*
	 * synchronized public void freeIniter(CacherIniter ci) { if(ci!=initer)
	 * throw new RuntimeException("The Initer is not same to be freed!") ; }
	 */

	/**
	 * �õ�������
	 */
	public String getName()
	{
		return strCacherName;
	}

	// synchronized public void setSynType(short syn)
	// { //System.out.println ("SetSynType-------"+syn+" ["+strCacherName+"]") ;
	// if (syn == CacherSyner.SYN_UNIQUE)
	// {
	// throw new RuntimeException("Not Support Unique Syner now!");
	// }
	//
	// if (syn == CacherSyner.SYN_SAME_MEM)
	// {
	// throw new RuntimeException(
	// "The method setSynType(short) cannot be used in SAME_MEM model!");
	// }
	//
	// synType = syn;
	// /*
	// if(synType==CacherSyner.SYN_SAME)
	// {
	// beforeInit(boolean clearexisted)
	// }*/
	//
	// cacherSyn.myTypeChange(strCacherName, syn);
	// }
	//
	// void setSynTypeNoSyn(short syn)
	// {
	// if (syn == CacherSyner.SYN_UNIQUE)
	// {
	// throw new RuntimeException("Not Support Unique Syner now!");
	// }
	// synType = syn;
	// }
	//
	// public short getSynType()
	// {
	// return synType;
	// }

	public void setCacheLength(int len)
	{
		// if (synType == CacherSyner.SYN_SAME_MEM)
		// { //�ж��Ƿ��ڱ���״̬������Ǿ�����
		// if (bFreezen)
		// {
		// try
		// {
		// wait();
		// setCacheLength(len);
		// }
		// catch (Exception e)
		// {}
		// }
		// }

		setCacheLengthNoSyn(len);

		// cacherSyn.mySetCacheLength(strCacherName, len);
	}

	void setCacheLengthNoSyn(int len)
	{
		cacherBuffer.setMaxBufferLen(len);
	}

	public int getCacheLength()
	{
		return cacherBuffer.getMaxBufferLen();
	}

	public int getContentLength()
	{
		return cacherBuffer.getBufferLen();
	}

	// void addToBufferDuringInit(Object o)
	// {
	// if (synType != CacherSyner.SYN_SAME)
	// {
	// throw new RuntimeException("Error Cache[" + strCacherName +
	// "],Only SYN_SAME cannot do During Init buffering!");
	// }
	// if (initState != DURING_INIT)
	// {
	// throw new RuntimeException("Error Cache[" + strCacherName +
	// "],Only During Init can buffering!");
	// }
	// duringInitBuf.addElement(o);
	// }

	private boolean bFreezen = false;

	void setFreezen(boolean bfreezen)
	{
		bFreezen = bfreezen;
		if (!bFreezen)
		{
			notify();
		}
	}

	boolean isFreezen()
	{
		return bFreezen;
	}

	/**
	 * ��ĳ��������б��� 1�����û����ͬ����ֱ�ӱ��沢���� 2���������ͬ��������һ��myChange�¼�
	 * 3�������Ψһͬ���������ж��Լ��Ƿ���������������IP��ַС��Ϊ������ ������ǣ����ö����͵����� ����ǣ�ֱ�ӱ���
	 * 
	 * @param id
	 *            �ö���ʹ�õ�id
	 * @param ob
	 *            ����ָ��
	 * @param lifttime
	 *            �ö�����cache�б�����ʱ��
	 */
	public void cache(Object key, Object ob, long livetime, boolean refresh)
	{ // System.out.println
		// ("cache---------["+this.strCacherName+"]["+key+"]["+ob+"]>>>>>") ;
	// if (synType == CacherSyner.SYN_SAME)
	// {
	// switch (initState)
	// {
	// case UNKNOW_INIT:
	// throw new RuntimeException(
	// "Not set beforeInit for this SYN_SAME cache[" +
	// strCacherName + "]!");
	// case BEFORE_INIT:
	// throw new RuntimeException(
	// "Not set beginInit for this SYN_SAME cache[" +
	// strCacherName + "]!");
	// }
	// }
	// else if (synType == CacherSyner.SYN_SAME_MEM)
	// { //�ж��Ƿ��ڱ���״̬������Ǿ�����
	// if (bFreezen)
	// {
	// try
	// {
	// wait();
	// cache(key, ob, livetime, refresh);
	// }
	// catch (Exception e)
	// {}
	// }
	// }

		Shell tmpsh = null;
		boolean bnew = false;
		synchronized (this)
		{
			tmpsh = cacherBuffer.accessShell(key);

			if (tmpsh == null)
			{
				tmpsh = new Shell(key, ob, livetime, refresh);
				cacherBuffer.addShell(tmpsh);
				bnew = true;
			}
			else
			{
				tmpsh.setContent(ob);
				tmpsh.setRefresh(refresh);
				tmpsh.setLiveTime(livetime);
				bnew = false;
			}
		}

		pulseClean();
		
		// switch (synType)
		// {
		// case CacherSyner.SYN_NO:
		// break;
		// case CacherSyner.SYN_DIRTY:
		// if (!bnew)
		// {
		// cacherSyn.myRemove(strCacherName, key);
		// }
		// break;
		// case CacherSyner.SYN_CHANGE:
		// cacherSyn.myAddOrChange(strCacherName, tmpsh);
		// break;
		// case CacherSyner.SYN_SAME:
		// if (initState == AFTER_INIT)
		// {
		// cacherSyn.myAddOrChange(strCacherName, tmpsh);
		// }
		// break;
		// case CacherSyner.SYN_UNIQUE:
		//
		// //cacherSyn.myAddOrChange(strCacherName,tmpsh) ;
		// //throw new RuntimeException("") ;
		// break;
		// }
	}

	/*
	 * public void update (Object key,Object ob,long livetime,boolean refresh) {
	 * Shell tmpsh = null ; synchronized (this) { tmpsh =
	 * cacherBuffer.accessShell (key) ; if (tmpsh==null) { tmpsh = new Shell
	 * (key,ob,livetime,refresh) ; cacherBuffer.addShell (tmpsh) ; } else {
	 * tmpsh.setContent (ob) ; tmpsh.setRefresh (refresh) ; tmpsh.setLiveTime
	 * (livetime) ; } } switch (synType) { case CacherSyner.SYN_NO: break ; case
	 * CacherSyner.SYN_DIRTY: cacherSyn.myRemove(strCacherName,key) ; break ;
	 * case CacherSyner.SYN_SAME: cacherSyn.myAddOrChange(strCacherName,tmpsh) ;
	 * break ; case CacherSyner.SYN_UNIQUE:
	 * //cacherSyn.myAddOrChange(strCacherName,tmpsh) ; //throw new
	 * RuntimeException("") ; break ; } }
	 */
	// public void cacheNoSyn (Object key,
	public void cache(Object key, Object ob, long livetime)
	{
		cache(key, ob, livetime, true);
	}

	/**
	 * ��ĳ��������б��棬��cache�б�����ʱ��Ϊȱʡֵ
	 * 
	 * @param id
	 *            �ö���ʹ�õ�id
	 * @param ob
	 *            ����ָ��
	 */
	public void cache(Object key, Object ob)
	{
		cache(key, ob, -1);
	}

	/**
	 * ���һ���������ݵĴ��ʱ�䡣
	 * 
	 * @return long 0��ʾ�����ڸ����ݣ���1��ʾ���ã�&gt;0��ʾ����ʱ��
	 */
	public long getLiveTime(Object key)
	{
		Shell tmpsh = cacherBuffer.getShell(key);
		if (tmpsh == null)
		{
			return 0;
		}
		if (tmpsh.isTimeOut())
		{
			cacherBuffer.removeShell(tmpsh);
			return 0;
		}
		return tmpsh.getLiveTime();
	}

	/**
	 * ɾ��һ����������
	 */
	public Object remove(Object key)
	{
		// if (synType == CacherSyner.SYN_SAME_MEM)
		// { //�ж��Ƿ��ڱ���״̬������Ǿ�����
		// if (bFreezen)
		// {
		// try
		// {
		// wait();
		// remove(key);
		// }
		// catch (Exception e)
		// {}
		// }
		// }

		Shell tmpsh = cacherBuffer.removeShell(key);
		if (tmpsh == null)
		{
			return null;
		}

		// if (synType == CacherSyner.SYN_NO)
		// {
		// return tmpsh.getContent();
		// }
		//
		// if ( (synType == CacherSyner.SYN_SAME) && (initState != AFTER_INIT))
		// {
		// return tmpsh.getContent();
		// }
		//
		// cacherSyn.myRemove(strCacherName, key);

		return tmpsh.getContent();
	}

	void removeNoSyn(Object key)
	{
		cacherBuffer.removeShell(key);
	}

	/**
	 * ��ȡ�����е��������ݵ�key,�����ܱ�֤���е�key�����Ժ�� ʹ�ù����ж����Ի�����ݡ�
	 */
	public Object[] getAllKeys()
	{
		Shell[] tmpsh = cacherBuffer.getAllShell();
		Object[] keys = new Object[tmpsh.length];
		for (int i = 0; i < tmpsh.length; i++)
		{
			keys[i] = tmpsh[i].getKey();
		}
		return keys;
	}

	/**
	 * ��ȡ�����е��������ݵ�key,�����ܱ�֤���е�key�����Ժ�� ʹ�ù����ж����Ի�����ݡ�
	 */
	public Enumeration getAllKeysEnum()
	{
		return cacherBuffer.getAllKeys();
	}

	/**
	 * ��ȡ�����е���������
	 */
	public Object[] getAllContents()
	{
		Shell[] tmpsh = cacherBuffer.getAllShell();
		Object[] conts = new Object[tmpsh.length];
		for (int i = 0; i < tmpsh.length; i++)
		{
			conts[i] = tmpsh[i].peekContent();
		}
		return conts;
	}

	/**
	 * ��ջ����е���������
	 */
	public void clear()
	{
		// if (synType == CacherSyner.SYN_SAME_MEM)
		// { //�ж��Ƿ��ڱ���״̬������Ǿ�����
		// if (bFreezen)
		// {
		// try
		// {
		// wait();
		// clear();
		// }
		// catch (Exception e)
		// {}
		// }
		// }

		cacherBuffer.emptyBuffer();

		// if (synType == CacherSyner.SYN_NO)
		// {
		// return;
		// }
		//
		// if ( (synType == CacherSyner.SYN_SAME) && (initState != AFTER_INIT))
		// {
		// return;
		// }
		//
		// cacherSyn.myClear(strCacherName);
	}

	public boolean isEmpty()
	{
		return cacherBuffer.isEmpty();
	}

	public int size()
	{
		return cacherBuffer.size();
	}

	void clearNoSyn()
	{
		cacherBuffer.emptyBuffer();
	}

	/**
	 * 
	 */
	synchronized public Object get(Object key)
	{
		pulseClean();
		
		if (_DEBUG)
		{
			return null;
		}
		/*
		 * if (synType==CacherSyner.SYN_UNIQUE) {//����Ψһͬ��,ʹ����Сip��Ϊ�����������������
		 * //�򷵻�null if (!CacherSyner.getInstance().isSmallestIP()) return null ; }
		 */
		Shell tmpsh = cacherBuffer.accessShell(key);
		if (tmpsh == null)
		{
			return null;
		}
		if (tmpsh.isTimeOut())
		{
			cacherBuffer.removeShell(tmpsh);
			return null;
		}
		
		return tmpsh.getContent();
	}

	public Object peek(Object key)
	{
		Shell tmpsh = cacherBuffer.getShell(key);
		if (tmpsh == null)
		{
			return null;
		}
		if (tmpsh.isTimeOut())
		{
			cacherBuffer.removeShell(tmpsh);
			return null;
		}
		return tmpsh.getContent();
	}

	synchronized public void detectExpired()
	{
		Shell[] shs = cacherBuffer.getAllShell();
		for (int i = 0; i < shs.length; i++)
		{
			if (shs[i].isTimeOut())
			{
				cacherBuffer.removeShell(shs[i]);
			}
		}
	}

	public void setMaxBufferLength(int len)
	{
		setCacheLength(len);
	}

	public Shell[] getAllShell()
	{
		return cacherBuffer.getAllShell();
	}

	public CacherBuffer getCacherBuffer()
	{
		return cacherBuffer;
	}

	public void list()
	{
		/*
		 * System.out.println ("-----In cache has----------------") ; Shell[]
		 * shs = cacherBuffer.getAllShell () ; for (int i = 0 ; i < shs.length ;
		 * i ++) System.out.println (shs[i].toString()) ; System.out.println
		 * ("---------------------------------") ;
		 */
		cacherBuffer.list();
	}

	// ///////////////////////////////////////////
	public static void main(String args[])
	{
		try
		{
			Cacher cah = Cacher.getCacher();

			String inputLine;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			while ((inputLine = in.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(inputLine);
				int c = st.countTokens();
				String cmd[] = new String[c];
				for (int i = 0; i < c; i++)
				{
					cmd[i] = st.nextToken();
				}
				if (cmd.length == 0)
				{
					continue;
				}

				if (cmd[0].equals("cache"))
				{ //
					int l = cmd.length;
					if (l == 5)
					{
						cah.cache(cmd[1], cmd[2], Integer.parseInt(cmd[3]),
								Boolean.valueOf(cmd[4]).booleanValue());
					}
					else if (l == 4)
					{
						cah.cache(cmd[1], cmd[2], Integer.parseInt(cmd[3]));
					}
					else if (l == 3)
					{
						cah.cache(cmd[1], cmd[2]);
					}
				}
				else if (cmd[0].equals("ls"))
				{
					cah.list();
				}
				else if (cmd[0].equals("get"))
				{
					Object ob = cah.get(cmd[1]);
					System.out.println("" + ob);
				}
				else if (cmd[0].equals("setMaxBufferLength"))
				{
					cah.setMaxBufferLength(Integer.parseInt(cmd[1]));
				}
				else if (cmd[0].equals("remove"))
				{
					System.out.println("" + cah.remove(cmd[1]));
				}
				else if (cmd[0].equals("clear"))
				{
					cah.clear();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
