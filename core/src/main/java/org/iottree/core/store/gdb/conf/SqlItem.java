package org.iottree.core.store.gdb.conf;

import java.io.*;
import java.util.*;

import org.iottree.core.store.gdb.GdbException;
import org.iottree.core.util.Convert;
import org.w3c.dom.Element;

/**
 * 支持一次sql运行的配置项
 * 
 * 必然：Content，Step中的sql语句会被处理成SqlItem对象。以支持运行时的Sql语句装配
 * 和参数的设置
 * 
 * sql语句中的，参数定制形如[@xxx] [$xxx] {@xxx} {$xxx}
 * 其中使用 {}表示如果参数值为null，则本语句段会被忽略
 * @author Jason Zhu
 */
public class SqlItem extends IOperItem
{
	/**
	 * 监测运行时间
	 * @author jasonzhu
	 *
	 */
	public static class RunCost
	{
		public long runDT =-1 ;
		
		public long costMS = -1 ;
		
		public String sql = null ;
		
		public String paramStr = null ;
	}
	
	public static class AutoFit
	{
		String name = null ;
		ExeType exeType = null ;
		HashMap<DBType,String> dbtype2cont = new HashMap<DBType,String>() ;
		
		public AutoFit(String n)
		{
			name = n ;
			int p = name.indexOf('_');
			exeType = ExeType.valueOf(name.substring(0,p));
		}
		
		public String getName()
		{
			return name ;
		}
		
		public ExeType getExeType()
		{
			return exeType ;
		}
		
		public String getFitContent(DBType n)
		{
			return dbtype2cont.get(n) ;
		}
	}
	//装载autofit信息
	private static HashMap<String,AutoFit> name2AutoFit = new HashMap<String,AutoFit>() ;
	
	
	
	static
	{
		//ClassLoader cl = Thread.currentThread().getContextClassLoader();//.getResourceAsStream(name)
		//Class c = new Object().getClass();
		Class c = new SqlItem().getClass();
		InputStream ips = null ;
		for(DBType dbt:DBType.values())
		{
			try
			{
				ips = c.getResourceAsStream("/com/dw/system/gdb/conf/autofit/"+dbt.toString()+".prop");
				if(ips==null)
					continue ;
				
				System.out.println("load >>"+"autofit/"+dbt.toString()+".prop");
				
				
				Properties p = new Properties();
				p.load(ips);
				for(Enumeration en = p.propertyNames();en.hasMoreElements();)
				{
					String pn = (String)en.nextElement() ;
					String pv = p.getProperty(pn);
					
					
					AutoFit af = name2AutoFit.get(pn);
					if(af==null)
					{
						af = new AutoFit(pn);
						name2AutoFit.put(pn,af);
					}
					
					af.dbtype2cont.put(dbt,pv);
				}
			}
			catch(IOException ioe)
			{
				
			}
			finally
			{
				if(ips!=null)
				{
					try
					{
					ips.close();
					}
					catch(IOException ioe)
					{}
					ips = null ;
				}
			}
		}
	}
	
	private static final int ST_NORMAL = 0 ;
	private static final int ST_IN_RT_PARAM = 1 ;
	private static final int ST_IN_CAT_PARAM = 2 ;
	
	public static SqlItem parseSqlItem(Gdb g,Func fi,Element sqlele) throws Exception
	{
		SqlItem si = new SqlItem();
		
		si.gdb = g ;
		si.func = fi ;
		
		String exet = sqlele.getAttribute(Gdb.ATTR_EXE_TYPE);
		if(exet!=null&&!exet.equals(""))
			si.exeType = ExeType.valueOf(exet);
		
		si.resultTableName = sqlele.getAttribute(Gdb.ATTR_RES_TABLE_NAME) ;
		
		String callt = sqlele.getAttribute(Gdb.ATTR_TYPE);
		if(callt!=null&&!callt.equals(""))
			si.callType = CallType.valueOf(callt);
		
		si.parseEle(sqlele);
		
		String str_cont = XDataHelper.getElementFirstTxt(sqlele);
		if(str_cont==null||(str_cont=str_cont.trim()).equals(""))
			return null ;
		
		if(Convert.isNullOrTrimEmpty(exet))
		{//guess exe type
			StringTokenizer st = new StringTokenizer(str_cont," \t\r\n") ;
			String firstword = st.nextToken() ;
			if("select".equalsIgnoreCase(firstword))
			{
				si.exeType = ExeType.select ;
			}
			else if("update".equalsIgnoreCase(firstword))
			{
				si.exeType = ExeType.update ;
			}
			else if("delete".equalsIgnoreCase(firstword))
			{
				si.exeType = ExeType.delete ;
			}
		}
		
		if(si.callType==CallType.auto_fit)
		{
			si.autoFit = name2AutoFit.get(str_cont);
			if(si.autoFit==null)
				throw new RuntimeException("cannot get auto fit with name="+str_cont);
			return si ;
		}
		//设置#var 变量到对应的sql语句字符串中
		for(Map.Entry<String, String> vn2vv:si.gdb.getAllVarName2Value().entrySet())
		{
			str_cont = str_cont.replaceAll(vn2vv.getKey(), vn2vv.getValue());
		}
		
		parseSqlItem(si, str_cont);
		
		return si ;
	}

	/**
	 * 根据字符串sql直接生成SqlItem对象
	 * @param sqlstr
	 * @return
	 * @throws GdbException
	 */
	public static SqlItem parseSqlItem(String sqlstr,HashMap<String,String> inparam_n2t) throws GdbException
	{
		SqlItem si = new SqlItem();
		HashMap<String,InParam> n2ip=new HashMap<String,InParam>() ;
		if(inparam_n2t!=null && inparam_n2t.size()>0)
		{
			for(Map.Entry<String, String> n2t : inparam_n2t.entrySet())
			{
				String n = n2t.getKey() ;
				String t = n2t.getValue() ;
				InParam ip = InParam.createInParam(n, t) ;
				if(ip!=null)
					n2ip.put(n, ip) ;
			}
		}
		si.outterInParam = n2ip ;
		
		
		StringTokenizer st = new StringTokenizer(sqlstr," \t\r\n") ;
		String firstword = st.nextToken() ;
		if("select".equalsIgnoreCase(firstword))
		{
			si.exeType = ExeType.select ;
		}
		else if("update".equalsIgnoreCase(firstword))
		{
			si.exeType = ExeType.update ;
		}
		else if("delete".equalsIgnoreCase(firstword))
		{
			si.exeType = ExeType.delete ;
		}
		
		parseSqlItem(si, sqlstr) ;
		return si ;
	}

	private static void parseSqlItem(SqlItem si, String str_cont) throws GdbException
	{
		//对sql语句进行分解，并生成对应的片段和参数信息
		//语句中的参数信息形如[@xxx]-运行时转换为? [$xxx]-运行时被自动拼装
		int p = 0 ;
		int st = 0 ;
		int len = str_cont.length();
		char c;
		StringBuilder tmpsb = new StringBuilder();
		//如果null，表是当前不在忽略段之内
		SqlSegIgnore curSegIngore = null ;
		while(p<len)
		{
			c = str_cont.charAt(p);
			switch(st)
			{
			case ST_NORMAL: //-normal state
				if(c=='['&&p+1<len)
				{
					char tmpc = str_cont.charAt(p+1) ;
					if(tmpc=='@')
					{
						if(tmpsb.length()>0)
						{
							SqlSeg ss = si.new SqlSeg(tmpsb.toString());
							if(curSegIngore!=null)
								curSegIngore.segs.add(ss);
							else
								si.sqlSegs.add(ss);
							tmpsb.delete(0, tmpsb.length());
						}
						
						p +=2 ;
						st = ST_IN_RT_PARAM;
						break;
					}
					else if(tmpc=='$')
					{
						if(tmpsb.length()>0)
						{
							SqlSeg ss = si.new SqlSeg(tmpsb.toString());
							if(curSegIngore!=null)
								curSegIngore.segs.add(ss);
							else
								si.sqlSegs.add(ss);
							tmpsb.delete(0, tmpsb.length());
						}
						
						p +=2 ;
						st = ST_IN_CAT_PARAM;
						break;
					}
				}
				
				if(c=='{')
				{
					if(curSegIngore!=null)
						throw new GdbException("ignore segment cannot contain segment!");
					
					SqlSeg ss = si.new SqlSeg(tmpsb.toString());
					si.sqlSegs.add(ss);
					tmpsb.delete(0, tmpsb.length());
					
					curSegIngore = si.new SqlSegIgnore();
					p++;
					break;
				}
				
				if(c=='}'&&curSegIngore!=null)
				{
					if(tmpsb.length()>0)
					{
						SqlSeg ss = si.new SqlSeg(tmpsb.toString());
						curSegIngore.segs.add(ss);
						tmpsb.delete(0, tmpsb.length());
					}
					si.sqlSegs.add(curSegIngore);
					curSegIngore = null ;
					p++ ;
					break;
				}
				
				
				tmpsb.append(c);
				p ++ ;
				break;
			case ST_IN_RT_PARAM: //-处理@参数中状态
				if(c==']')
				{//结束
					if(tmpsb.length()<=0)
						throw new RuntimeException("illegal runtime param [@] ,no name found");
					
					SqlSeg ss = si.new SqlSegRtParam("@"+tmpsb.toString().trim());
					if(curSegIngore!=null)
						curSegIngore.segs.add(ss);
					else
						si.sqlSegs.add(ss);
					tmpsb.delete(0, tmpsb.length());
					p ++ ;
					st = ST_NORMAL ;
					break;
				}
				
				tmpsb.append(c);
				p ++ ;
				break;
			case ST_IN_CAT_PARAM: //-处理$参数中状态
				if(c==']')
				{//结束
					if(tmpsb.length()<=0)
						throw new RuntimeException("illegal runtime param [$] ,no name found");
					
					SqlSeg ss = si.new SqlSegCatParam("$"+tmpsb.toString().trim());
					if(curSegIngore!=null)
						curSegIngore.segs.add(ss);
					else
						si.sqlSegs.add(ss);
					tmpsb.delete(0, tmpsb.length());
					p ++ ;
					st = ST_NORMAL ;
					break;
				}
				
				tmpsb.append(c);
				p ++ ;
				break;
			}
		}//end of while
		
		if(tmpsb.length()>0)
		{//最后一段
			SqlSeg ss = si.new SqlSeg(tmpsb.toString());
			si.sqlSegs.add(ss);
		}
	}
	
	Gdb gdb = null ;
	
	Func func = null ;
	//外部直接调用，没有func的情况下使用
	HashMap<String,InParam> outterInParam = null ;
	
	ExeType exeType = ExeType.nonquery;
	CallType callType = CallType.sql;
	
	
	
	private transient AutoFit autoFit = null ;
	private String resultTableName = null ;
	private ArrayList<SqlSeg> sqlSegs = new ArrayList<SqlSeg>();
	
	/**
	 * 存放运行消耗时间最大个数
	 */
	int RUNCOST_MAX = 10 ; 
	
	transient LinkedList<RunCost> runCosts = new LinkedList<RunCost>() ;
	
	private SqlItem()
	{}
	
	
	public synchronized void setRunCostStEt(long st,long et,String sql,String paramstr)
	{
		RunCost rc = new RunCost() ;
		rc.runDT = st ;
		rc.costMS = et-st ;
		rc.sql = sql ;
		rc.paramStr = paramstr ;
		runCosts.addLast(rc) ;
		
		if(runCosts.size()>RUNCOST_MAX)
		{
			runCosts.removeFirst() ;
		}
	}
	
	public List<RunCost> getRunCostList()
	{
		return runCosts ;
	}
	
	public ExeType getExeType()
	{
		return exeType ;
	}
	
	
	/**
	 * 获得本sql查询结果集中的DataTable名称
	 * @return
	 */
	public String getResultTableName()
	{
		return resultTableName ;
	}
	
	@Override
	public boolean isChangedData()
	{
		if(exeType==ExeType.select||exeType==ExeType.dataset||exeType==ExeType.scalar)
			return false;
		return true ;
	}
	
	public CallType getCallType()
	{
		return callType ;
	}
	
//	public List<ParamSetter> getParamSetters()
//	{
//		return paramSetters ;
//	}
//	
//	public BoolExp getIfBoolExp()
//	{
//		return ifBoolExp ;
//	}
	
	public ArrayList<SqlSeg> getSqlSegs()
	{
		return sqlSegs;
	}
	
	public AutoFit getAutoFit()
	{
		return autoFit ;
	}
	
	private transient RuntimeItem _fixRT = null ;
	/**
	 * 根据输入的参数获得jdbc运行时的支持内容
	 * 
	 * @param parms
	 * @return
	 */
	public RuntimeItem getRuntimeItem(DBType dbt,Hashtable parms)
	{
		if(_fixRT!=null)//固定的sql语句
			return _fixRT ;
		
		RuntimeItem ri = new RuntimeItem();
		if(this.callType==CallType.auto_fit)
		{
			ri.jdbcSql = autoFit.getFitContent(dbt);
			if(ri.jdbcSql==null)
				throw new RuntimeException("cannot get auto fit content with name="+autoFit.name+" on db type="+dbt);
			ri.exeType = autoFit.getExeType() ;
			_fixRT = ri ;
			return ri ;
		}
		boolean bfix = true ;
		
		if(func!=null||outterInParam!=null)
			ri.exeType = this.exeType;
		
		StringBuilder sb = new StringBuilder();
		ArrayList<InParam> rtpns = new ArrayList<InParam>() ;
		for(SqlSeg ss:sqlSegs)
		{
			if(ss instanceof SqlSegCatParam || ss instanceof SqlSegIgnore)
			{
				bfix = false;
			}
			
			RuntimeItem tmpri = ss.getRuntimeItem(parms);
			if(tmpri==null)
				continue ;
			
			if(tmpri.jdbcSql!=null)
				sb.append(tmpri.jdbcSql);
			
			if(tmpri.rtParams!=null&&tmpri.rtParams.size()>0)
				rtpns.addAll(tmpri.rtParams);
		}
		
		ri.jdbcSql = sb.toString() ;
		ri.rtParams = rtpns ;
		
		if(bfix)
			_fixRT = ri ;
		
		return ri ;
	}
	
	
	/**
	 * 为运行时提供的jdbc调用信息
	 * 如sql语句，和安装顺序需要填入的参数名称@xxx
	 * @author Jason Zhu
	 *
	 */
	public static class RuntimeItem
	{
		//CallType callType = CallType.sql;
		ExeType exeType = null;
		String jdbcSql = null ;
		ArrayList<InParam> rtParams = null;
		
		RuntimeItem()
		{
			
		}
		
		public ExeType getExeType()
		{
			return exeType ;
		}
		
		public String getJdbcSql()
		{
			return jdbcSql ;
		}
		
		public ArrayList<InParam> getRtParams()
		{
			return rtParams ;
		}
	}
	/**
	 * sql语句的片段：它有可能是字符串，输入参数，拼装参数等
	 */
	public class SqlSeg
	{
		String sorTxt = null ;
		
		public SqlSeg(String str)
		{
			sorTxt = str ;
		}

		public String getSorTxt()
		{
			return sorTxt ;
		}
		
		public String getParamName()
		{
			return null ;
		}
		
		public RuntimeItem getRuntimeItem(Hashtable parms)
		{
			RuntimeItem ri = new RuntimeItem();
			ri.jdbcSql = sorTxt ;
			return ri;
		}
	}
	
	/**
	 * 形如：[@xxx]的参数
	 * @author Jason Zhu
	 *
	 */
	class SqlSegRtParam extends SqlSeg
	{
		String paramName = null ;
		
		public SqlSegRtParam(String str)
		{
			super(str);
			
			if(str.startsWith("["))
				str = str.substring(1);
			if(str.endsWith("]"))
				str = str.substring(0,str.length()-1);
			
			if(!str.startsWith("@"))
				throw new IllegalArgumentException("Runtime In Param Name must like @xxx");
			
			paramName = str ;
		}
		
		public String getParamName()
		{
			return paramName ;
		}
		
		public RuntimeItem getRuntimeItem(Hashtable parms)
		{
			RuntimeItem ri = new RuntimeItem();
			ri.jdbcSql = "?" ;
			ri.rtParams = new ArrayList<InParam>(0);
			InParam ip = null ;
			if(func!=null)
			{
				ip = func.getInParam(paramName);
				if(ip==null)
					throw new RuntimeException("cannot find InParam ["+paramName+"] definiation in func["+func.getUniqueKey()+"]");
			}
			else if(outterInParam!=null)
			{
				ip = outterInParam.get(paramName);
				if(ip==null)
					throw new RuntimeException("cannot find InParam ["+paramName+"] outter");
			}
			else
			{
				throw new RuntimeException("cannot find InParam ["+paramName+"]");
			}
			
			ri.rtParams.add(ip);
			return ri;
		}
	}
	
	/**
	 * 形如：[$xxx]的参数
	 * @author Jason Zhu
	 *
	 */
	class SqlSegCatParam extends SqlSeg
	{
		String paramName = null ;
		
		public SqlSegCatParam(String str)
		{
			super(str);
			
			if(str.startsWith("["))
				str = str.substring(1);
			if(str.endsWith("]"))
				str = str.substring(0,str.length()-1);
			
			if(!str.startsWith("$"))
				throw new IllegalArgumentException("Cat In Param Name must like $xxx");
			
			paramName = str ;
		}
		
		public String getParamName()
		{
			return paramName ;
		}
		
		public RuntimeItem getRuntimeItem(Hashtable parms)
		{
			RuntimeItem ri = new RuntimeItem();
			
			Object pv = parms.get(paramName);
			if(pv==null)
			{
				InParam ip = func.getInParam(paramName) ;
				pv = ip.getDefaultVal() ;
			}
			
			ri.jdbcSql = "" ;
			if(pv!=null)
				ri.jdbcSql = pv.toString();
			
			return ri;
		}
	}
	
	/**
	 * 形如{xsdf [@xxx] xdfgg [$yyy]}的语句，如果里面的变量只要有一个为null
	 * 则整个数据段都会忽略
	 * @author Jason Zhu
	 */
	class SqlSegIgnore extends SqlSeg
	{
		ArrayList<SqlSeg> segs = new ArrayList<SqlSeg>() ;
		
		public SqlSegIgnore()
		{
			super("");
		}
		
		public ArrayList<SqlSeg> getInnerSegs()
		{
			return segs;
		}
		
		public RuntimeItem getRuntimeItem(Hashtable parms)
		{
			StringBuilder sb = new StringBuilder();
			RuntimeItem ri = new RuntimeItem();
			for(SqlSeg s:segs)
			{
				if((s instanceof SqlSegRtParam)||(s instanceof SqlSegCatParam))
				{
					
					String pn = s.getParamName() ;
					if(parms.get(pn)==null)
						return null ;//该段被忽略
					
					RuntimeItem tmpri = s.getRuntimeItem(parms);
					String js = tmpri.getJdbcSql();
					if(js!=null)
						sb.append(js);
					ArrayList<InParam> pns = tmpri.getRtParams() ;
					if(pns!=null&&pns.size()>0)
					{
						if(ri.rtParams==null)
							ri.rtParams = new ArrayList<InParam>() ;
						
						ri.rtParams.addAll(pns);
					}
				}
				else
				{
					RuntimeItem tmpri = s.getRuntimeItem(parms);
					String js = tmpri.getJdbcSql();
					if(js!=null)
						sb.append(js);
				}
			}
			
			ri.jdbcSql = sb.toString();
			
			return ri;
		} 
	}
}
