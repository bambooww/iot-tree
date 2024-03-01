package org.iottree.core.basic;

import java.lang.reflect.Method;
import java.util.*;

import org.iottree.core.util.xmldata.*;
import org.json.JSONArray;

@data_class
public class PropItem
{
	public static final String VT_BOOL = "bool" ;
	public static final String VT_STR = "str" ;
	public static final String VT_INT = "int" ;
	public static final String VT_FLOAT = "float" ;
	
	public static enum PValTP
	{
		vt_bool(1),
		vt_int(2),
		vt_float(3),
		vt_str(4);
		
		private final int val ;
		
		PValTP(int v)
		{
			val = v ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public String getStr()
		{
			switch(val)
			{
			case 1:
				return VT_BOOL;
			case 2:
				return VT_INT;
			case 3:
				return VT_FLOAT;
			case 4:
				return VT_STR;
			default:
				return null;
			}
		}
	}

	
	
	public static class ValOpt implements IJSONArr
	{
		Object val ;
		String title;
		
		public ValOpt()
		{
//			val = v ;
//			title = t;
		}
		
		public ValOpt(String t,Object val)
		{
			this.val = val ;
			this.title = t ;
		}
		
		public Object getVal()
		{
			return val;
		}
		
		public String getTitle()
		{
			return title ;
		}
		
		public String toJSONStr()
		{
			if(val instanceof String)
				return "[\""+val+"\",\""+title+"\"]";
			else
				return "["+val+",\""+title+"\"]";
		}

		@Override
		public JSONArray toJSONArr()
		{
			JSONArray r = new JSONArray() ;
			r.put(val);
			r.put(title) ;
			return r;
		}

		@Override
		public boolean fromJSONArr(JSONArray job)
		{
			val = job.get(0) ;
			title = job.getString(1) ;
			return true;
		}
	}
	
	public static class ValOpts implements IJSONArr
	{
		List<ValOpt> valOpts = null;

		public ValOpts()
		{
			valOpts = new ArrayList<>() ;
		}
		
		public ValOpts(List<ValOpt> valops)
		{
			valOpts = valops ;
		}
		
		public List<ValOpt> getOpts()
		{
			return valOpts;
		}
		@Override
		public JSONArray toJSONArr()
		{
			JSONArray r = new JSONArray() ;
			for(ValOpt vo:valOpts)
			{
				r.put(vo.toJSONArr());
			}
			return r;
		}

		@Override
		public boolean fromJSONArr(JSONArray job)
		{
			int n = job.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONArray ja = job.getJSONArray(i);
				ValOpt vo = new ValOpt() ;
				vo.fromJSONArr(ja);
				valOpts.add(vo);
			}
			return true;
		}
	}
	
	static ValOpts BOOL_DEF_OPTS = new ValOpts();
	static
	{
		BOOL_DEF_OPTS.valOpts.add(new ValOpt("Enabled",true));
		BOOL_DEF_OPTS.valOpts.add(new ValOpt("Disabled",false));
	}
	
	private static ValOpts transValOpts(PValTP vt,String[] valopt_ts,Object[] valopt_vals)
	{
		if(valopt_ts==null||valopt_ts.length<=0)
		{
			if(vt==PValTP.vt_bool)
			{
				return BOOL_DEF_OPTS;
			}
			return null ;
		}

		ArrayList<ValOpt> vos = new ArrayList<>();
		for(int i = 0 ; i < valopt_ts.length ; i ++)
		{
			String t = valopt_ts[i];
			Object v = valopt_vals[i] ;
			ValOpt vop = new ValOpt(t,v) ;
			vos.add(vop);
		}
		return new ValOpts(vos);
	}
	
	private static ValOpts transValOpts(PValTP vt,List<?> nts,Class<?> objc,String title_m,String val_m)
		throws Exception
	{
		if(nts==null||nts.size()<=0)
		{
			if(vt==PValTP.vt_bool)
			{
				return BOOL_DEF_OPTS;
			}
			return null ;
		}

		Method tm = objc.getDeclaredMethod(title_m) ;
		Method vm = objc.getDeclaredMethod(val_m) ;
		if(tm==null||vm==null)
			return null ;
		ArrayList<ValOpt> vos = new ArrayList<>();
		for(Object nt:nts)
		{
			String t = (String)tm.invoke(nt) ;
			Object v = vm.invoke(nt) ;
			ValOpt vop = new ValOpt(t,v) ;
			vos.add(vop);
		}
		return new ValOpts(vos);
	}
	
	/**
	 * 
	 * @param tp
	 * @param strv
	 * @return
	 */
	public static Object transStrToVal(String tp,String strv)
	{
		if(strv==null)
			return null ;
		
		switch(tp)
		{
		case VT_BOOL:
			return "true".equalsIgnoreCase(strv) || "1".contentEquals(strv) ;
		case VT_INT:
			return Long.parseLong(strv) ;
		case VT_FLOAT:
			return Double.parseDouble(strv);
		case VT_STR:
		default:
			return strv ;
		}
	}
	
	public static Object transStrToVal(PValTP tp,String strv)
	{
		if(strv==null)
			return null ;
		
		switch(tp)
		{
		case vt_bool:
			return "true".equalsIgnoreCase(strv) || "1".contentEquals(strv) ;
		case vt_int:
			return Long.parseLong(strv) ;
		case vt_float:
			return Double.parseDouble(strv);
		case vt_str:
		default:
			return strv ;
		}
	}
	
	@data_val
	String name = null ;
	
	@data_val
	String title = null ;
	
	@data_val
	String desc = null ;
	
	/**
	 * string bool int double
	 * only support 4 types
	 */
	@data_val
	String valTp = PValTP.vt_str.getStr();
	
	transient PValTP vt =  PValTP.vt_str;
	
	@data_val
	boolean bReadOnly = false;
	
	@data_obj(param_name = "enum_val",obj_c=ValOpts.class)
	ValOpts valOpts = null ;
	
	@data_val(param_name = "def_val")
	Object defaultVal = null ;
	
	@data_val(param_name = "multi_line")
	boolean bTxtMultiLine = false;
	
	@data_val(param_name = "pop")
	String popName = null ;
	
	@data_val(param_name = "popt")
	String popTitle = null ;
	
	ValChker<?> valChker = null ;
	
	public PropItem()
	{}
	
	public PropItem(String n,String t,String d,PValTP vt,boolean breadonly,
			String[] valopt_ts,Object[] valopt_vals,Object def_val)
	{
		this.name = n ;
		this.title = t ;
		this.desc = d ;
		this.vt = vt ; 
		this.valTp = vt.getStr() ;
		this.bReadOnly = breadonly ;
		valOpts = transValOpts(vt,valopt_ts,valopt_vals);
		defaultVal = def_val ;
	}
	
	public PropItem(String n,String t,String d,PValTP vt,boolean breadonly,
			List<?> nts,Class<?> objc,String title_m,String val_m,Object def_val)
	{
		this.name = n ;
		this.title = t ;
		this.desc = d ;
		this.vt = vt ; 
		this.valTp = vt.getStr() ;
		this.bReadOnly = breadonly ;
		try
		{
			valOpts = transValOpts(vt,nts,objc,title_m,val_m);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		defaultVal = def_val ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return title ;
	}
	
	public String getValTp()
	{
		return valTp ;
	}
	
	public PValTP getVT()
	{
		return vt ;
	}
	
	
	public String getDesc()
	{
		return desc ;
	}
	
	public boolean isReadOnly()
	{
		return bReadOnly;
	}
	
	public ValOpts getValOpts()
	{
		return valOpts; 
	}
	
	public Object getDefaultVal()
	{
		return this.defaultVal;
	}
	
	public void setDefaultVal(Object v)
	{
		this.defaultVal = v ;
	}
	
	public Number getDefaultValNum()
	{
		if(this.defaultVal==null)
			return null ;
		return (Number)this.defaultVal ; 
	}
	
	public Boolean getDefaultValBool()
	{
		if(this.defaultVal==null)
			return null ;
		return (Boolean)this.defaultVal ; 
	}
	
	public String getDefaultValStr()
	{
		if(this.defaultVal==null)
			return null ;
		return this.defaultVal.toString() ; 
	}
	
	public boolean isTxtMultiLine()
	{
		return this.bTxtMultiLine ;
	}
	
	public PropItem withTxtMultiLine(boolean b)
	{
		this.bTxtMultiLine = b ;
		return this ;
	}
	
	public String getPopName()
	{
		return this.popName ;
	}
	
	public String getPopTitle()
	{
		return this.popTitle ;
	}
	
	public PropItem withPop(String pname,String ptitle)
	{
		this.popName = pname ;
		this.popTitle = ptitle ;
		return this ;
	}
	
	public void setValChker(ValChker<?> vc)
	{
		this.valChker = vc ;
	}
	
	public ValChker<?> getValChker()
	{
		return valChker ;
	}
//	/**
//	 * {name:string,title:string,enum_val:[[][]]}
//	 * @return
//	 */
//	public String toJSONStr()
//	{
//		String r = "{name:\""+name+"\", title: \""+title+"\", type: \""+valTp+"\"";
//		if(valOpts!=null&&valOpts.size()>0)
//		{
//			r += ",enum_val: [";
//			boolean bfirst=true;
//			for(ValOpt vo:valOpts)
//			{
//				if(bfirst)bfirst=false;
//				else r +=",";
//				r += vo.toJSONStr();
//			}
//			r += "]"; 
//		}
//		r += "}";
//		return r ;
//	}
}
