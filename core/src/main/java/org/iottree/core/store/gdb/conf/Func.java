package org.iottree.core.store.gdb.conf;

import java.util.*;

import org.iottree.core.util.Convert;
import org.w3c.dom.Element;

public class Func implements Comparable
{
    private static final String SQL_DELIM = ";";

//    public static final String CONST_PROJECT_NAME = "prj_name_dataaccess" ;
//
//    public static String PRO_PREFIX = null ;

    static Func parseFunc(Module m,Element funcele) throws Exception
	{
		Func fi = new Func() ;
		
		fi.belongTo = m ;
		
		fi.name = funcele.getAttribute(Gdb.ATTR_NAME);
		if(fi.name==null||fi.equals(""))
			return null ;
		
		fi.usingDBName = funcele.getAttribute(Gdb.ATTR_DBNAME) ;
		if(Convert.isNullOrEmpty(fi.usingDBName))
			fi.usingDBName = funcele.getAttribute(Gdb.ATTR_DB_NAME) ;
		
		fi.timeOut = m.timeOut;
		String func_time_str = funcele.getAttribute(Gdb.ATTR_TIMEOUT);
		if(func_time_str!=null&&!func_time_str.equals(""))
			fi.timeOut = Integer.parseInt(func_time_str);
		
		String str_exe_type = funcele.getAttribute(Gdb.ATTR_EXE_TYPE);
		if(str_exe_type!=null&&!str_exe_type.equals(""))
			fi.exeType = ExeType.valueOf(str_exe_type);
        
		String str_functype = funcele.getAttribute(Gdb.ATTR_TYPE);
		if(str_functype!=null&&!str_functype.equals(""))
			fi.funcType = CallType.valueOf(str_functype); 
        //if(gmf.exe_type!=ExeType.dataset)
        //    gmf.exe_type = ExeType.unknown ;
        
		//处理输入参数
		Element[] inp_eles = XDataHelper.getCurChildElement(funcele, Gdb.TAG_INPARAM);
		if(inp_eles!=null)
		{
			for(Element e0:inp_eles)
			{
				InParam inp = InParam.parseInParam(e0);
				fi.name2InParam.put(inp.getName(), inp);
			}
		}

		Element contele = XDataHelper.getFirstChildElement(funcele, Gdb.TAG_CONTENT);
		if(contele!=null)
		{
			fi.defaultFuncContFDB = FuncContentForDB.parseContent(fi, contele);
		}
		
		
		Element[] cont_fordb_eles = XDataHelper.getCurChildElement(funcele, Gdb.TAG_CONTENT_FOR_DB);
		if(cont_fordb_eles!=null)
		{
			for(Element fordb_ele:cont_fordb_eles)
			{
				FuncContentForDB fcdb = FuncContentForDB.parseContent(fi, fordb_ele);
				if(fcdb==null)
					continue ;
				
				if(fi.defaultFuncContFDB==null)
					fi.defaultFuncContFDB = fcdb;//第一个可能作为缺省的使用
				
				fi.dbType2Content.put(fcdb.getDBType(), fcdb);
			}
		}
		
		if(fi.defaultFuncContFDB==null&&fi.dbType2Content.size()<=0)
			return null ;
		
        return fi ;
	}
	
	

    private static int GetSqlSelectNum(String s)
    {
        if(s==null)
            return 0 ;

        String[] ss = s.split(SQL_DELIM) ;
        int c = 0 ;
        for(int i = 0 ; i < ss.length ; i ++)
        {
            //Console.WriteLine("["+ss[i]+"]") ;
            if(ss[i].trim().toLowerCase().startsWith("select"))
                c ++ ;
        }

        return c ;
    }


	

    
    transient Module belongTo = null ;
    
    private String name = null;
    
    private String usingDBName = null ;
    
	private HashMap<String,InParam> name2InParam = new HashMap<String,InParam>() ;
	
	private FuncContentForDB defaultFuncContFDB = null ;
	/**
	 * 存放数据库类型到运行内容的映射
	 */
	private HashMap<DBType,FuncContentForDB> dbType2Content = new HashMap<DBType,FuncContentForDB>();
	
    ExeType exeType = ExeType.dataset;
    
    CallType funcType = CallType.sql;
    
    private int selectSqlNum = 0 ;


    private int timeOut = -1;
    

    public Module getBelongTo()
    {
    	return belongTo ;
    }
    
    public String getName()
    {
    	return name ;
    }
    
    private transient String realDBName = null ;
    
    public String getRealUsingDBName()
    {
    	if(realDBName!=null)
    		return realDBName ;
    	
    	if(Convert.isNotNullEmpty(usingDBName))
    	{
    		realDBName = usingDBName ;
    		return realDBName ;
    	}
    	
    	realDBName = belongTo.getUsingDBName() ;
    	if(realDBName==null)
    		realDBName = "" ;
    	
    	return realDBName ;
    }
    
    /**
     * 获得必须填写的参数
     * @return
     */
    public List<InParam> getNeedParams()
    {
    	ArrayList<InParam> rets = new ArrayList<InParam>() ;
    	for(InParam ip:name2InParam.values())
    	{
    		if(!ip.isNullable())
    			rets.add(ip);
    	}
    	return rets;
    }
    
    public InParam getInParam(String pn)
    {
    	return name2InParam.get(pn);
    }
    
    public String getUniqueKey()
    {
        return belongTo.getName()+"."+name ;
    }
    
    
    public FuncContentForDB getFuncContent(DBType dbt)
    {
    	FuncContentForDB fcfdb = dbType2Content.get(dbt);
    	if(fcfdb!=null)
    		return fcfdb ;
    	
    	return defaultFuncContFDB ;
    }
    
    
    public FuncContentForDB getFuncContentDefault()
    {
    	return defaultFuncContFDB ;
    }
    
    public List<FuncContentForDB> getFuncContents()
    {
    	ArrayList<FuncContentForDB> r = new ArrayList<FuncContentForDB>() ;
    	
    	r.addAll(dbType2Content.values()) ;
    	
    	return r ;
    }

    /// <summary>
    /// 
    /// </summary>
    public int getTimeOut()
    {
        return timeOut;
    }


    public String ToDocContent()
    {
        return "" ;

    }

    public String toEnvStr()
    {
    	
    	return belongTo.getBelongTo().getAbsResPath()+"-"+belongTo.getName()+"."+this.name;
    }
    
    public int compareTo(Object obj)
    {
        Func ofi = (Func)obj ;
        
        
        return toEnvStr().compareTo(ofi.toEnvStr()) ;
    }

	}
