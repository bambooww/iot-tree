package org.iottree.core.store.gdb.conf;

import java.io.InputStream;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.iottree.core.util.Convert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 对应一个gdb配置文件
 * @author Jason Zhu
 */
public class Gdb implements Comparable<Gdb>
{
	public static final String TAG_GDB = "Gdb";
	public static final String TAG_INSTALL_FOR_DB = "Install_For_DB";
	public static final String TAG_XORM = "XORM";
	
	public static final String TAG_VAR = "Var";
	public static final String TAG_MODULE = "Module";
	public static final String TAG_FUNC = "Func";
	public static final String TAG_INPARAM = "InParam";
	public static final String TAG_CONTENT = "Content";
	public static final String TAG_CONTENT_FOR_DB = "Content_For_DB";
	public static final String TAG_OR_MAP = "ORMap";
	public static final String TAG_MAP = "map";
	public static final String TAG_EMBED = "embed";
	public static final String TAG_SQL = "Sql";
	public static final String TAG_FILE = "File";
	
	public static final String ATTR_ID = "id";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_TABLE_NAME = "table_name";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_DESC = "desc";
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_DEFAULT_VAL = "default_val";
	public static final String ATTR_PURE_SELECT = "pure_select";
	public static final String ATTR_EXE_TYPE = "exe_type";
	public static final String ATTR_NULLABLE = "nullable";
	public static final String ATTR_TIMEOUT = "timeout";
	public static final String ATTR_DBNAME = "dbname";
	public static final String ATTR_DB_NAME = "db_name";
	public static final String ATTR_RES_TABLE_NAME = "result_tablename";
	
	public static final String ATTR_DBTYPE = "db_type";
	public static final String ATTR_BUFFER_LEN = "bufferlen";
	public static final String ATTR_COLUMN = "column";
	public static final String ATTR_CLASS = "class";
	public static final String ATTR_PROPERTY = "property";
	public static final String ATTR_CC_TAR = "cc_tar";
	public static final String ATTR_VALUE_GEN = "value_generator";
	public static final String ATTR_AUTO_TRUNCATE = "auto_truncate";
	public static final String ATTR_MAX_SIZE = "max_size";
	public static final String ATTR_SET_PARAM = "set_param";
	public static final String ATTR_IF = "if";
	public static final String ATTR_BASE = "base";
	public static final String ATTR_NOINSTALL = "noinstall";
	
	
	
	/**
	 * 对应的使用数据库名称
	 */
	public transient String usingDBName = "" ;
	
	private ClassLoader relatedClassLoader = null ;
	/**
	 * xml文件所在的资源路径 形如：/com/xx/xx/xdata_xx.xml
	 */
	private String absResPath = null ;
	private String fullPath = null ;
	
	private int timeOut = -1 ;
	
	private HashMap<String,String> varName2Value = new HashMap<String,String>() ;
	
	/**
	 * 从整体上存放XORM类到定义的映射,该变量为直接通过类,做一些数据库的操作提供了依据
	 */
	private static HashMap<Class,XORM> globalClass2XORM = new HashMap<Class,XORM>();
	private static HashMap<String,XORM> globalClassN2XORM = new HashMap<String,XORM>();
	
	/**
	 * 该方法用来支持,不直接通过配置文件的Func的唯一id来访问数据库
	 * 而是通过XORM类来访问
	 * 
	 * 通过XORM类,可以定位对应的数据库连接信息,为XORM特殊方法提供支持
	 * @param c
	 * @return
	 */
	public static XORM getXORMByGlobal(Class<?> c)
	{
		XORM xorm =  globalClass2XORM.get(c);
		if(xorm!=null)
			return xorm;
		
		XORM r = XORM.fromClass(c) ;
		globalClass2XORM.put(c, r);
		return r;
	}
	
	
	public static XORM getXORMByGlobal(String classn)
	{
		return globalClassN2XORM.get(classn) ;
	}
	
	private static transient List<XORM> allXORMs = null ;
	
	public static List<XORM> getXORMAll()
	{
		if(allXORMs!=null)
			return allXORMs; 
		
		final Comparator<XORM> cp = new Comparator<XORM>()
		{

			public int compare(XORM o1, XORM o2)
			{
				return o1.getXORMClassStr().compareTo(o2.getXORMClassStr());
			}
			
		};
		ArrayList<XORM> rets = new ArrayList<XORM>() ;
		rets.addAll(globalClass2XORM.values()) ;
		
		Collections.sort(rets,cp);
		allXORMs = rets ;
		return rets ;
	}
	
	//private AppInfo appInfo = null ;
	//String compName = null ;
	/**
	 * 本Gdb文件内部的XORM类信息
	 */
	private ArrayList<XORM> xorms = new ArrayList<XORM>() ;
	private HashMap<DBType,ArrayList<InstallForDB>> dbt2install = new HashMap<DBType,ArrayList<InstallForDB>>() ;
	private ArrayList<Module> modules = new ArrayList<Module>() ;
	private HashMap<String,ORMap> clazz2orm = new HashMap<String,ORMap>();
	
	//AppInfo ai,
	public Gdb(String absrespath,String fullpath,InputStream inputs) throws Exception
	{
//		if(ai!=null)
//		{
//			appInfo = ai ;
//			relatedClassLoader = ai.getRelatedClassLoader() ;
//		}
		absResPath = absrespath;
		fullPath = fullpath ;
		
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;

		// parse XML XDATA File
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		docBuilder = docBuilderFactory.newDocumentBuilder();

		doc = docBuilder.parse(inputs);

		Element root = doc.getDocumentElement();
		
		parseGdb(root);
	}
	
	private void parseGdb(Element gdbele) throws Exception
	{
		if(!gdbele.getTagName().equals(TAG_GDB))
			throw new Exception("root node tag is not "+TAG_GDB);
		
		usingDBName = gdbele.getAttribute(ATTR_DBNAME) ;
		if(Convert.isNullOrEmpty(usingDBName))
			usingDBName = gdbele.getAttribute(ATTR_DB_NAME) ;
		
		timeOut = -1 ;
		String gdb_time_str = gdbele.getAttribute(ATTR_TIMEOUT);
		if(gdb_time_str!=null&&!gdb_time_str.equals(""))
			timeOut = Integer.parseInt(gdb_time_str);
		
		//parse var element
		Element[] vareles = XDataHelper.getCurChildElement(gdbele, TAG_VAR);
		if(vareles!=null)
		{
			for(Element tmpe:vareles)
			{
				String n = tmpe.getAttribute(ATTR_NAME);
				if(n==null||n.equals(""))
					continue ;
				String v = tmpe.getAttribute(ATTR_VALUE);
				if(v==null||v.equals(""))
					continue ;
				varName2Value.put(n, v);
			}
		}
		
		
		Element[] installforxorm_eles = XDataHelper.getCurChildElement(gdbele, TAG_XORM);
		if(installforxorm_eles!=null)
		{
			for(Element tmpe0:installforxorm_eles)
			{
				XORM ifdb = XORM.parseContent(this,tmpe0);
				if(ifdb==null)
					continue ;
				//this.class2XORM.put(ifdb.getXORMClass(), ifdb);
				xorms.add(ifdb);
				globalClass2XORM.put(ifdb.getXORMClass(), ifdb);
				globalClassN2XORM.put(ifdb.getXORMClass().getCanonicalName(), ifdb);
			}
		}
		
		Element[] installfordb_eles = XDataHelper.getCurChildElement(gdbele, TAG_INSTALL_FOR_DB);
		if(installfordb_eles!=null)
		{
			for(Element tmpe0:installfordb_eles)
			{
				InstallForDB ifdb = InstallForDB.parseContent(this,tmpe0);
				if(ifdb==null)
					continue ;
				
				ArrayList<InstallForDB> ss = dbt2install.get(ifdb.getDBType());
				if(ss==null)
				{
					ss = new ArrayList<InstallForDB>() ;
					dbt2install.put(ifdb.getDBType(),ss);
				}
				ss.add(ifdb);
			}
		}
		
		//parse module element
		Element[] module_eles = XDataHelper.getCurChildElement(gdbele, TAG_MODULE);
		if(module_eles!=null)
		{
			for(Element tmpe:module_eles)
			{
				Module m = Module.parseModule(this,tmpe);
				if(m==null)
					continue ;
				
				modules.add(m);
			}
		}
		
		Element[] orm_eles = XDataHelper.getCurChildElement(gdbele, TAG_OR_MAP);
		if(orm_eles!=null)
		{
			for(Element tmpe:orm_eles)
			{
				ORMap orm = ORMap.parseORMap(this, tmpe);
				if(orm==null)
					continue ;
				
				clazz2orm.put(orm.getClazz(), orm);
			}
		}
	}
	
//	public AppInfo getAppInfo()
//	{
//		return appInfo ;
//	}
//	
//	public String getCompName()
//	{
//		return appInfo.getContextName() ;
//	}
	/**
	 * 获得装载了该Xml文件的类装载器
	 * @return
	 */
	public ClassLoader getRelatedClassLoader()
	{
		return relatedClassLoader;
	}
	
	private transient HashMap<String,String> _allVarName2Val = null ;
	
	/**
	 * 把之间定义的变量和XORM中隐含的变量进行组合,生成全部的变量集合
	 * 以便于后续Sql语句的并接处理
	 * @return
	 * @throws ClassNotFoundException
	 */
	public HashMap<String,String> getAllVarName2Value() throws ClassNotFoundException
	{
		if(_allVarName2Val!=null)
			return _allVarName2Val;
		//if(xorm)
		if(xorms==null||xorms.size()<=0)
		{
			_allVarName2Val = varName2Value ;
			return _allVarName2Val;
		}
		
		HashMap<String,String> ret = new HashMap<String,String>() ;
		for(Map.Entry<String,String> n2v:varName2Value.entrySet())
		{
			ret.put(n2v.getKey(), n2v.getValue());
		}
		for(XORM x:xorms)
		{
			HashMap<String,String> tmpm = x.getSqlCatParamValues() ;
			for(Map.Entry<String,String> n2v:tmpm.entrySet())
			{
				ret.put(n2v.getKey(), n2v.getValue());
			}
		}
		
		_allVarName2Val = ret ;
		return _allVarName2Val;
	}
	
	public int getTimeOut()
	{
		return timeOut ;
	}
	
	public String getAbsResPath()
	{
		return absResPath;
	}
	
	public ArrayList<Module> getModules()
	{
		return modules ;
	}
	
	public ArrayList<XORM> getXORMs()
	{
		return xorms;
	}
	
	public ArrayList<XORM> getInstallForXORMs()
	{
//		XORM[] rets = new XORM[class2XORM.size()];
//		class2XORM.values().toArray(rets);
//		return rets ;
		ArrayList<XORM> rets = new ArrayList<XORM>() ;
		for(XORM xorm:xorms)
		{
			if(xorm.IsNoInstall())
				continue ;
			
			rets.add(xorm);
		}
		return rets;
	}
	
	/**
	 * 根据一个XORM类获得对应的配置对象
	 * 从该对象中,可以获得Gdb信息,进而可以获得运行时使用的数据库连接对象
	 * @param c
	 * @return
	 */
	public XORM getInstallForXORMByClass (Class c)
	{
		for(XORM x:xorms)
		{
			if(x.getXORMClass()==c)
				return x;
		}
		return null;
	}
	
	public List<InstallForDB> getInstallForDB(DBType dbt)
	{
		ArrayList<InstallForDB> fds = dbt2install.get(dbt);
		if(fds!=null)
			return fds ;
		
		return dbt2install.get(DBType.all);
	}
	
	public ORMap getORMap(String clazz_name)
	{
		return clazz2orm.get(clazz_name);
	}

	public int compareTo(Gdb o)
	{
		return toString().compareTo(o.toString());
	}
	
	public String toString()
	{
		return "["+usingDBName +"]"+ absResPath +" @ "+fullPath;
	}
}
