package org.iottree.core.devtree;

import java.util.Date;

import javax.script.ScriptException;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.devtree.cxt.DevContext;
import org.iottree.core.devtree.cxt.DevNodeCodeItem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IPack;
import org.json.JSONObject;

/**
 * Dev Tree or node prop,it's provider by RunBlk
 * 
 * @author jason.zhu
 *
 */
public class DTRunProp extends JSObMap implements IPack
{
	public static enum ValSty
	{
		normal(0,false),
		set_in_root(1,false), //
		js_func(2,true),
		bind_tag(3,true);
		
		private final int val ;
		private final boolean need_pm ;
		
		ValSty(int v,boolean needpm)
		{
			val = v ;
			need_pm = needpm ;
		}
		
		public int getValue()
		{
			return val ;
		}
		
		public boolean isNeedPM()
		{
			return need_pm;
		}
		
		public String getTitle()
		{
			switch(val)
			{
			case 0:
				return "普通";
			case 1:
				return "外部提供";
			case 2:
				return "JS Func";
			case 3:
				return "绑定标签" ;
			default:
				return null ;
			}
		}
		
		public static ValSty valueOf(int v)
		{
			switch(v)
			{
			case 0:
				return normal;
			case 1:
				return set_in_root;
			case 2:
				return js_func ;
			case 3:
				return bind_tag ;
			default:
				return normal ;
			}
		}
	}


	DTNode belongTo ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	UAVal.ValTP valTP = UAVal.ValTP.vt_str ;
	
	ValSty valSty = ValSty.normal ;
	
	/**
	 * 缺省字符串值
	 */
	String defStrV = null ;
	
	private String propPM = null ;
	
	/**
	 * 是否是静态属性
	 * 静态属性只在DevPart中有效，并且只能在DevPart中设置
	 * 在设备引用时，此属性值都一样。在DevPart中修改之后，所有设备引用都会起作用
	 */
	private boolean bPartStatic =  false;
	
	/**
	 * 在Part中设置的值——此值在Part中单独配置存储
	 */
	String strValInPart = null ;
	//UAVal RT_val = null ;
	
	/**
	 * 判断是否是PartCls引用过来的
	 */
	public transient boolean bPartClsRefed = false;

	public DTRunProp(DTNode dn)
	{
		belongTo = dn ;
	}
	
	public DTRunProp(DTNode dn,String newname,String title,String desc,UAVal.ValTP vtp,ValSty vsty,String def_strv,boolean b_partstatic)
	{
		StringBuilder failedr = new StringBuilder() ;
		if(Convert.isNullOrEmpty(newname) || !Convert.checkVarName(newname, true, failedr))
		{
			throw new IllegalArgumentException("invalid name "+newname+" "+failedr) ;
		}
		belongTo = dn ;
		this.name = newname ;
		this.title = title ;
		this.desc = desc ;
		this.valTP = vtp ;
		this.valSty = vsty; 
		this.defStrV = def_strv ;
		this.bPartStatic = b_partstatic ;
	}
	
	DTRunProp copyWith(DTNode bt)
	{
		DTRunProp dp = new DTRunProp(bt) ;
		dp.belongTo = bt ;
		dp.name = this.name ;
		dp.title = this.title ;
		dp.desc = this.desc ;
		dp.valTP = this.valTP ;
		dp.valSty = this.valSty ;
		dp.defStrV = this.defStrV ;
		dp.propPM = this.propPM ;
		dp.bPartStatic = this.bPartStatic ;
		return dp ;
	}
	
	public DTNode getBelongTo()
	{
		return this.belongTo ;
	}

	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		if(this.title==null)
			return "" ;
		return this.title ;
	}
	
	public UAVal.ValTP getValTP()
	{
		return valTP ;
	}
	
	public ValSty getValSty()
	{
		return this.valSty ;
	}
	
	public boolean isPartStatic()
	{
		return this.bPartStatic ;
	}
	
	public String getDefStrVal()
	{
		if(this.defStrV==null)
			return "" ;
		return this.defStrV ;
	}
	
	public String getStrValInPart()
	{
		if(this.strValInPart==null)
			return "" ;
		return this.strValInPart ;
	}
	
	private transient Object defVal = null ;
	
	//@JsDef
	public Object getDefVal()
	{
		if(defVal!=null)
			return defVal ;
//		if(this.defStrV==null)
//			return null ;
		defVal = transStrToObjVal(this.valTP,defStrV) ;
		return defVal;
	}
	
	public void setDefStrVal(String strv)
	{
		if(this.bPartStatic)
		{
			if(Convert.isNullOrEmpty(strv))
				throw new IllegalArgumentException("part static cannot set null value") ;
		}
		defVal = transStrToObjVal(this.valTP,strv) ;
		this.defStrV = strv ;
	}
	
	public String getValStrInPart()
	{
		return this.strValInPart ;
	}
	/**
	 * 如果此属性是属于DevPart，那么可以获取在Part中设定的值
	 * @return
	 */
	public Object getValInPart()
	{
		if(Convert.isNullOrEmpty(this.strValInPart))
			return null ;
		return transStrToObjVal(this.valTP,strValInPart) ;
	}
	
	public String getPropPM()
	{
		return propPM ;
	}
	
	public void setPropPM(String pm)
	{
		this.propPM = pm ;
		clearCache();
	}
	
	public String getPropPMTitle()
	{
		if(propPM==null)
			return null ;
		
		int len = this.propPM.length() ;
		if(len>20)
			return this.propPM.substring(0,20)+"..." ;
		return this.propPM ;
	}
	
	public String getDesc()
	{
		if(this.desc==null)
			return "" ;
		return this.desc ;
	}
	

	public static String transToStrVal(ValTP vtp,Object obj)
	{
		if(obj==null)
			return null ;
		if(vtp==ValTP.vt_date)
		{
			if(obj instanceof Date) //时间类型字符串输出，转成标准格式
				return Convert.toFullYMDHMS((Date)obj) ;
			else if(obj instanceof Number)
				return Convert.toFullYMDHMS(new Date(((Number)obj).longValue())) ;
		}
		return obj.toString() ;
	}
	
	
	public static Object transStrToObjVal(ValTP vtp,String str)
	{
		Object  objv = UAVal.transStr2ObjVal(vtp, str) ;
		if(objv==null)
			return null ;
		if(vtp==ValTP.vt_date)
		{//js和对象内部的时间都必须使用int64，转换成long
			return ((Date)objv).getTime() ;
		}
		return objv ;
	}

	
	public static Object transObjToObjVal(ValTP vtp,Object objv)
	{
		if(objv==null)
			return null ;
		
		if(objv instanceof String)
			return transStrToObjVal(vtp,(String)objv) ;
		if(vtp==ValTP.vt_date)
		{
			if(objv instanceof Date)
				return ((Date)objv).getTime() ;
			if(objv instanceof Number)
				return ((Number)objv).longValue() ;
			throw new IllegalArgumentException("no date obj val") ;
		}
		
		if(vtp==ValTP.vt_bool)
		{
			if(objv instanceof Boolean)
				return (Boolean)objv ;
			if(objv instanceof Number)
				return ((Number)objv).doubleValue()>0 ;
		}
		
		if(objv instanceof Date)
		{
			if(vtp==ValTP.vt_int64)
				return ((Date)objv).getTime() ;
		}
		
		if(objv instanceof Number)
		{
			switch(vtp)
			{
				case vt_int64:
					return ((Number)objv).longValue();
				case vt_int32:
					return ((Number)objv).intValue();
				case vt_int16:
					return ((Number)objv).shortValue();
				case vt_byte:
					return ((Number)objv).byteValue();
				case vt_float:
					return ((Number)objv).floatValue() ;
				case vt_double:
					return ((Number)objv).doubleValue();
				case vt_char:
					return (char)((Number)objv).shortValue();
				default:
			}
		}
		
		return objv.toString() ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", this.name) ;
		jo.putOpt("t", this.title) ;
		jo.putOpt("d", this.desc) ;
		jo.put("vtp",this.valTP.getInt()) ;
		jo.put("vsty", this.valSty.val) ;
		jo.putOpt("defv",this.defStrV) ;
		jo.putOpt("pm", this.propPM) ;
		if(this.bPartStatic)
			jo.put("part_static",true) ;
		
		return jo ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", this.name) ;
		jo.putOpt("t", this.title) ;
		jo.putOpt("d", this.desc) ;
		jo.put("vtp",this.valTP.getInt()) ;
		jo.put("vtp_t",this.valTP.getStr()) ;
		jo.put("vsty", this.valSty.val) ;
		jo.putOpt("defv",this.defStrV) ;
		//jo.putOpt("pm", this.propPM) ;
		//jo.putOpt("tree_nid", this.belongTo.getTreeNodeFullID()) ;
		jo.putOpt("tree_ntt", this.belongTo.getPathTitle()) ;
		jo.put("vsty_t", this.valSty.getTitle()) ;
		jo.put("part_static",this.bPartStatic) ;
		jo.putOpt("val_in_part",this.strValInPart) ;
		return jo ;
	}
	
	public static DTRunProp fromJO(DTNode dn,JSONObject jo)
	{
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
			return null ;
		DTRunProp dp = new DTRunProp(dn) ;
		dp.name = n ;
		dp.title = jo.optString("t") ;
		dp.desc = jo.optString("d") ;
		dp.valTP = UAVal.getValTp(jo.optInt("vtp")) ;
		dp.valSty = ValSty.valueOf(jo.optInt("vsty")) ;
		dp.defStrV = jo.optString("defv") ;
		dp.propPM = jo.optString("pm") ;
		dp.bPartStatic = jo.optBoolean("part_static",false) ;
		return dp ;
	}
	
	private DevNodeCodeItem jsfuncCI = null ;
	
	public DevNodeCodeItem JS_getJsFuncCodeItem() throws ScriptException
	{
		if(this.valSty!=ValSty.js_func)
			throw new RuntimeException("no js func prop") ;
		
		DevContext cxt = this.belongTo.JS_getContext() ;
		if(jsfuncCI!=null)
			return jsfuncCI ;
		
		jsfuncCI = new DevNodeCodeItem("",this.propPM,cxt) ;
		return jsfuncCI ;
	}
	
	void clearCache()
	{
		jsfuncCI = null ;
	}
	
}
