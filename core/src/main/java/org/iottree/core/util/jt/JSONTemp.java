package org.iottree.core.util.jt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.dict.DataClass;
import org.iottree.core.dict.DataNode;
import org.iottree.core.dict.DictManager;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 数据定义模板——一般针对json 由Adapter管理维护，用来定义Adapter输出数据结构
 * 
 * 1，最外层必须是Object 2，内部可以有属性 3，属性可以是基本类型，对象数组，层次只允许2层
 * 
 * @author zzj
 *
 */
public class JSONTemp implements Comparable<JSONTemp>
{
	private static final String[] TT_LANGS = new String[] {"en","cn"} ;
	/**
	 * 其中file类型的值仅仅是文件名称（含扩展名）,如 aaa.pdf.具体的文件File对象，由使用方提供
	 * @author zzj
	 *
	 */
	public static enum ValTp
	{
		number(1), bool(2), string(3),file(4);

		private final int val;

		ValTp(int v)
		{
			val = v;
		}

		public int getValue()
		{
			return val;
		}

		public Object transStrValToObj(String strv)
		{
			switch(val)
			{
			case 1:
				if(Convert.isNullOrEmpty(strv))
					return null ;
				int k = strv.indexOf('.');
				if(k>=0)
					return Double.parseDouble(strv) ;
				else
					return Long.parseLong(strv) ;
			case 2:
				if(Convert.isNullOrEmpty(strv))
					return null ;
				return "true".equalsIgnoreCase(strv) ;
			default:
				return strv ;
			}
		}
		
		public Object tranObjVal(Object objv)
		{
			if(objv==null)
				return objv ;
			switch(val)
			{
			case 1: //number
				if(objv instanceof Number)
					return objv ;
				if(objv instanceof Boolean)
					return ((Boolean)objv)?1:0 ;
				String strv = objv.toString() ;
				int k = strv.indexOf('.');
				if(k>=0)
					return Double.parseDouble(strv) ;
				else
					return Long.parseLong(strv) ;
			case 2: //bool
				if(objv instanceof Boolean)
					return objv ;
				if(objv instanceof Number)
					return ((Number)objv).intValue()>0 ;
					
				return "true".equalsIgnoreCase(objv.toString()) ;
			default:
				return objv.toString() ;
			}
		}
		
		public static ValTp valueOfInt(int i)
		{
			return JSONTemp.valueOfInt(i) ;
		}
	}

	public static ValTp valueOfInt(int i)
	{
		switch (i)
		{
		case 1:
			return ValTp.number;
		case 2:
			return ValTp.bool;
		case 4:
			return ValTp.file;
		default:
			return ValTp.string;
		}
	}

	static ValTp valueOfStr(String i)
	{
		switch (i)
		{
		case "num":
		case "number":
		case "int":
		case "int32":
		case "integer":
		case "long":
		case "double":
		case "float":
			return ValTp.number;
		case "bool":
		case "boolean":
			return ValTp.bool;
		case "file":
			return ValTp.file;
		default:
			return ValTp.string;
		}
	}
	
	/**
	 * 只针对字符串和数值两种类型
	 * @author zzj
	 *
	 * @param <T>
	 */
	public static class ValOpt
	{
		String val = null ;
		
		String title ;
		
		ValOpt()
		{}
		
		public ValOpt(String v,String t)
		{
			this.val = v ;
			this.title = t ;
		}
		
		public String getValStr()
		{
			return val ;
		}
		
		public int getValInt32()
		{
			return Integer.parseInt(this.val) ;
		}
		
		public String getTitle()
		{
			return this.title ;
		}
		
		public void fromJSON(JSONObject jo)
		{
			this.val = jo.getString("v") ;
			this.title = jo.getString("t") ;
		}
		
		public JSONObject toJSON()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("v", this.val);
			jo.put("t", this.title);
			return jo ;
		}
		
		public String toJSONStr()
		{
			return "{\"v\":\""+this.val+"\",\"t\":\""+this.title+"\"}" ;
		}
		
		public String toString()
		{
			return "["+this.val+","+this.title+"]";
		}
	}

	public static class PropItem implements Comparable<PropItem>
	{
		String name = null;

		String title = null;
		
		String desc = null ;
		
		Object example = null ;

		ValTp valTp;
		
		String format = null ;
		
		String pattern = null ;

		boolean bNullable = true;
		
		boolean outerChk = false;

		boolean bArray = false;
		
		ArrayList<ValOpt> valOpts = null ;
		
		HashMap<String,String> lang2title = null ;
		
		Integer minLen = null ;
		
		Integer maxLen = null ;
		
		Double minVal = null;
		
		Double maxVal = null;
		
		public PropItem()
		{}
		
		public PropItem(PropItem pi)
		{
			this.name = pi.name;
			this.title = pi.title;
			this.desc = pi.desc ;
			this.example = pi.example ;
			this.valTp = pi.valTp;
			this.format = pi.format ;
			this.pattern = pi.pattern ;
			this.bNullable = pi.bNullable;
			this.outerChk = pi.outerChk ;
			this.bArray = pi.bArray;
			this.valOpts = pi.valOpts ;
			this.lang2title = pi.lang2title ;
			this.minLen = pi.minLen ;
			this.maxLen = pi.maxLen ;
			this.minVal = pi.minVal;
			this.maxVal = pi.maxVal;
		}

		public String getName()
		{
			return name;
		}

		public String getTitle()
		{
			if (Convert.isNullOrEmpty(title))
				return name;
			return this.title;
		}

		public String getDesc()
		{
			if(desc==null)
				return "" ;
			return desc ;
		}
		
		public Object getExample()
		{
			return this.example ;
		}
		
		public ValTp getValTp()
		{
			return this.valTp;
		}
		
		public String getFormat()
		{
			if(this.format==null)
				return "" ;
			return this.format ;
		}
		
		public String getPattern()
		{
			return this.pattern ;
		}
		
		public String getTypeFormat()
		{
			String r = this.valTp.toString() ;
			if(Convert.isNotNullEmpty(this.format))
				r +="-"+this.format ;
			if(this.bArray)
				r += "[]" ;
			if(this.pattern!=null)
				r += " "+this.pattern ;
			return r;
		}

		public boolean isNullable()
		{
			return this.bNullable;
		}
		
		public boolean isOuterChk()
		{
			return this.outerChk ;
		}

		public boolean isArray()
		{
			return bArray;
		}
		
		public String getLangTitle(String ln,boolean b_def_name)
		{
			String r = null ;
			
			if(this.lang2title!=null)
				r = this.lang2title.get(ln) ;
			
			if(Convert.isNotNullEmpty(r))
				return r ;
			
			if(b_def_name)
				return this.name ;
			return null ;
		}
		
		public Integer getMinLen()
		{
			return this.minLen;
		}
		
		public Integer getMaxLen()
		{
			return this.maxLen ;
		}
		
		public String getMinMaxLenStr()
		{
			String s = "" ;
			if(this.minLen!=null)
				s = "["+this.minLen ;
			if(this.maxLen!=null)
			{
				if(s.length()>0)
					return s+","+this.maxLen+"]" ;
				else
					return "[,"+this.maxLen+"]" ;
			}
			return s ;
		}
		
		public Double getMinVal()
		{
			return this.minVal;
		}
		
		public Double getMaxVal()
		{
			return this.maxVal ;
		}
		
		public String getMinMaxValStr()
		{
			String s = "" ;
			if(this.minVal!=null)
				s = "("+this.minVal ;
			if(this.maxLen!=null)
			{
				if(s.length()>0)
					return s+","+this.maxVal+")" ;
				else
					return "[,"+this.maxVal+"]" ;
			}
			return s ;
		}

		public List<ValOpt> getValOpts()
		{
			return this.valOpts ;
		}
		
		public String getTotalDesc()
		{
			String ddd = this.getMinMaxValStr()+getMinMaxLenStr() ;
		    String desc = getDesc() ;
		    if(Convert.isNotNullEmpty(desc))
		    {
		    	if(ddd.length()>0)
		    		ddd += "\r\n"+desc;
		    	else
		    		ddd = desc;
		    }
		    if(this.valOpts!=null&&valOpts.size()>0)
		    {
		    	String valoptstr = "Value Options " ;
		    	for(JSONTemp.ValOpt vo:valOpts)
		    	{
		    		valoptstr += vo.toString();
		    	}
		    	if(ddd.length()>0)
		    		ddd += "\r\n"+valoptstr;
		    	else
		    		ddd = valoptstr;
		    }
		    
		    String exstr = "" ;
		    if(this.example!=null)
		    	exstr = this.example.toString() ;
		    if(Convert.isNotNullEmpty(exstr))
		    {
		    	if(ddd.length()>0)
		    		ddd += "\r\nExample:"+exstr;
		    	else
		    		ddd = "Example:"+exstr;
		    }
		    return ddd ;
		}
		
//		@Override
//		public XmlData toXmlData()
//		{
//			XmlData xd = new XmlData();
//			xd.setParamValue("n", name);
//			if (Convert.isNotNullEmpty(title))
//				xd.setParamValue("t", title);
//			xd.setParamValue("vt", valTp.getValue());
//			xd.setParamValue("nullable", this.bNullable);
//			xd.setParamValue("array", this.bArray);
//			return xd;
//		}

//		@Override
//		public void fromXmlData(XmlData xd)
//		{
//			this.name = xd.getParamValueStr("n");
//			this.title = xd.getParamValueStr("t");
//			this.valTp = valueOfInt(xd.getParamValueInt32("vt", -1));
//			this.bNullable = xd.getParamValueBool("nullable", true);
//			this.bArray = xd.getParamValueBool("array", false);
//		}
//
//		private static String updateOnlyOrNotStr(boolean update_only,JSONObject jo,String pn,String oldval)
//		{
//			if(!update_only || jo.has(pn))
//				return jo.optString(pn);
//			return oldval ;
//		}
//		
//		private static boolean updateOnlyOrNotBool(boolean update_only,JSONObject jo,String pn,boolean oldval,boolean defaultv)
//		{
//			if(!update_only||jo.has(pn))
//				return jo.optBoolean(pn,defaultv);
//			return oldval ;
//		}
		
		public boolean fromJSON(JSONObject jo)
		{
			return fromJSON(jo,false);
		}
		
		public boolean fromJSON(JSONObject jo,boolean update_only)
		{
			this.name = jo.getString("n");
			if(!update_only || jo.has("t"))
				this.title = jo.optString("t");
			if(!update_only || jo.has("desc"))
				this.desc =  jo.optString("desc") ;
			if(!update_only || jo.has("vt"))
				this.valTp = valueOfStr(jo.optString("vt"));
			//jo.optBoolean("required", defaultValue)
			if(!update_only || jo.has("nullable"))
				this.bNullable = jo.optBoolean("nullable", true);
			if(!update_only || jo.has("outer_chk"))
				this.outerChk =  jo.optBoolean("outer_chk",false) ;
			if(!update_only || jo.has("array"))
				this.bArray = jo.optBoolean("array", false);
			if(!update_only || jo.has("format"))
				this.format = jo.optString("format") ;
			if(!update_only || jo.has("pattern"))
				this.pattern  = jo.optString("pattern") ;
			if(!update_only || jo.has("example"))
				this.example = jo.opt("example") ;
			
			int len;
			if(!update_only || jo.has("min_len"))
			{
				len = jo.optInt("min_len", -1) ;
				if(len>=0)
					this.minLen = len ;
			}
			if(!update_only || jo.has("max_len"))
			{
				len = jo.optInt("max_len",-1) ;
				if(len>0)
					this.maxLen = len ;
			}
			
			if(jo.has("min_val"))
				this.minVal = jo.optDouble("min_val") ;
			if(jo.has("max_val"))
				this.maxVal = jo.optDouble("max_val") ;
			
			for(String ln:TT_LANGS)
			{
				String tmn = "tt_"+ln;
				if(!update_only || jo.has(tmn))
				{
					String tt = jo.optString(tmn) ;
					if(Convert.isNotNullEmpty(tt))
					{
						if(this.lang2title==null)
							this.lang2title = new HashMap<>() ;
						this.lang2title.put(ln, tt) ;
					}
				}
			}
			
			if(!update_only || jo.has("val_opts"))
			{
				Object ob = jo.opt("val_opts") ;
				if(ob!=null)
				{
					if(ob instanceof JSONArray)
					{
						JSONArray jarr = (JSONArray)ob;
						len = jarr.length() ;
						valOpts = new ArrayList<>() ;
						for(int i = 0 ; i < len ; i ++)
						{
							JSONObject tmpjo = jarr.getJSONObject(i) ;
							ValOpt vo = new ValOpt() ;
							vo.fromJSON(tmpjo);
							valOpts.add(vo) ;
						}
					}
					else if(ob instanceof JSONObject)
					{
						JSONObject tmpjo = (JSONObject)ob ;
						String ddname = tmpjo.getString("dd_name") ;
						String ddmode = tmpjo.optString("dd_mode") ;
						String lang = tmpjo.getString("lang") ;
						String val_attr = tmpjo.optString("val_attr") ;
						int k = ddname.indexOf('.') ;
						DataClass dc = null ;
						if(k<0)
							dc = DictManager.getInstance().getDataClass(ddname) ;
						else
							dc = DictManager.getInstance().getDataClass(ddname.substring(0,k),ddname.substring(k+1)) ;
						if(dc==null)
							throw new IllegalArgumentException("no dict found with name="+ddname) ;
						valOpts = new ArrayList<>() ;
						List<DataNode> dns = null ;
//						if(Convert.isNullOrEmpty(ddmode))
//							dns = dc.listValidRootNodes() ;
//						else
//							dns = dc.getNodesByMode(ddmode) ;
						dns = dc.listValidRootNodes() ;
						for(DataNode dn:dns)
						{
							String v = dn.getName() ;
							if(Convert.isNotNullEmpty(val_attr))
								v = dn.getAttr(val_attr) ;
							ValOpt vo = new ValOpt(v,dn.getNameByLang(lang)) ;
							valOpts.add(vo) ;
						}
					}
				}
			}
			return true;
		}

		public JSONObject toJSON()
		{
			JSONObject jo = new JSONObject();
			jo.put("n", this.name);
			if (this.title != null)
				jo.put("t", this.title);
			if(this.desc!=null)
				jo.put("desc", this.desc) ;
			if(this.example!=null)
				jo.put("example", this.example) ;
			if(this.format!=null)
				jo.put("format", this.format) ;
			if(this.minLen!=null)
				jo.put("min_len", this.minLen) ;
			if(this.maxLen!=null)
				jo.put("max_len", this.maxLen) ;
			if(this.minVal!=null)
				jo.put("min_val", this.minVal);
			if(this.maxVal!=null)
				jo.put("max_val", this.maxVal);
			if(this.pattern!=null)
				jo.put("pattern", this.pattern) ;
			jo.put("vt", this.valTp.toString());
			jo.put("nullable", this.bNullable);
			jo.put("outer_chk", this.outerChk) ;
			jo.put("array", this.bArray);
			if(this.example!=null)
				jo.put("example", this.example) ;
			
			if(this.lang2title!=null)
			{
				for(Map.Entry<String,String> n2v:this.lang2title.entrySet())
				{
					jo.put("tt_"+n2v.getKey(), n2v.getValue()) ;
				}
			}
			if(this.valOpts!=null)
			{
				JSONArray jarr = new JSONArray() ;
				for(ValOpt vo:this.valOpts)
				{
					jarr.put(vo.toJSON()) ;
				}
				jo.put("val_opts",jarr) ;
			}
			return jo;
		}
		
		public String toJSONStr()
		{
			String ret =  "{\"n\":\""+this.name+"\",\"t\":\""+this.title+"\",\"vt\":\""+this.valTp.toString()+"\"";
			
			ret += ",\"nullable\":"+this.bNullable +",\"outer_chk\":"+this.outerChk;
			if(this.format!=null)
				ret += ",\"format\":\""+Convert.plainToJsStr(this.format)+"\"" ;
			if(this.pattern!=null)
				ret += ",\"pattern\":\""+Convert.plainToJsStr(this.pattern)+"\"" ;
			if(this.minLen!=null)
				ret += ",\"min_len\":"+minLen ;
			if(this.maxLen!=null)
				ret += ",\"max_len\":"+maxLen ;
			if(this.minVal!=null)
				ret += ",\"min_val\":"+minVal ;
			if(this.maxVal!=null)
				ret += ",\"max_val\":"+maxVal ;
			if(this.desc!=null)
				ret += ",\"desc\":\""+Convert.plainToJsStr(this.desc)+"\"" ;
			
			if(this.bArray)
				ret += ",\"array\":true" ;
			
			if(this.lang2title!=null)
			{
				for(Map.Entry<String,String> n2v:this.lang2title.entrySet())
				{
					ret += ",\"tt_"+n2v.getKey()+"\":\""+n2v.getValue()+"\"" ;
				}
			}
			if(this.valOpts!=null)
			{
				ret += ",\"val_opts\":[";
				boolean bfirst = true;
				for(ValOpt vo:this.valOpts)
				{
					if(bfirst) bfirst = false;
					else ret += "," ;
					ret += vo.toJSONStr() ;
				}
				ret += "]" ;
			}
			ret += "}" ;
			return ret;
		}

		@Override
		public int compareTo(PropItem o)
		{
			return this.name.compareTo(o.name);
		}
	}

	/**
	 * 
	 * @author zzj
	 *
	 */
	public static class SubItem implements Comparable<SubItem>
	{
		String name = null;

		String title = null;
		
		String desc = null ;

		boolean bNullable = true;

		boolean bArray = false;

		LinkedHashMap<String,PropItem> propItems = new LinkedHashMap<>();

		LinkedHashMap<String,SubItem> objItems  = new LinkedHashMap<>();
		/**
		 * 使用TempOb定义的对象结构
		 */
		String tempObName;
		
		SubItem()
		{}
		
		SubItem(SubItem subi)
		{
			this.name = subi.name;
			this.title = subi.title;
			this.desc = subi.desc ;
			this.bNullable = subi.bNullable;
			this.bArray = subi.bArray;
			this.tempObName = subi.tempObName;
		}
		
		SubItem(String name,String title,String desc,JSONTemp other_jt,boolean barray,boolean b_nullable)
		{
			this.name = name ;
			this.title = title ;
			this.desc = desc ;
			this.bNullable = b_nullable ;
			this.bArray = barray ;
			//this.propItems.putAll(other_jt.propItems);
			//this.objItems.putAll(other_jt.subObjItems) ;
			appendOther(null,other_jt.propItems,other_jt.subObjItems);
		}
		
		void appendOther(SubItem subi,Map<String,PropItem> props,Map<String,SubItem> subis)
		{
			if(subi!=null)
			{
				this.name = subi.name;
				this.title = subi.title;
				this.desc = subi.desc ;
				this.bNullable = subi.bNullable;
				this.bArray = subi.bArray;
				this.tempObName = subi.tempObName;
			}
			for(PropItem pi:props.values())
			{
				pi = new PropItem(pi) ;
				this.propItems.put(pi.name,pi);
			}
			for(SubItem tmpss : subis.values())
			{
				SubItem oldsi = this.objItems.get(tmpss.name) ;
				if(oldsi==null)
				{
					oldsi = new SubItem(tmpss) ;
					this.objItems.put(tmpss.name, oldsi) ;
				}
				oldsi.appendOther(tmpss,tmpss.propItems,tmpss.objItems);
			}
		}
		
//		void appendOrUpdate(JSONObject jo)
//		{
//			//SubItem subi,Map<String,PropItem> props,Map<String,SubItem> subis
//			if(subi!=null)
//			{
//				this.name = subi.name;
//				this.title = subi.title;
//				this.desc = subi.desc ;
//				this.bNullable = subi.bNullable;
//				this.bArray = subi.bArray;
//				this.tempObName = subi.tempObName;
//			}
//			for(PropItem pi:props.values())
//			{
//				pi = new PropItem(pi) ;
//				this.propItems.put(pi.name,pi);
//			}
//			for(SubItem tmpss : subis.values())
//			{
//				SubItem oldsi = this.objItems.get(tmpss.name) ;
//				if(oldsi==null)
//				{
//					oldsi = new SubItem(tmpss) ;
//					this.objItems.put(tmpss.name, oldsi) ;
//				}
//				oldsi.appendOther(tmpss,tmpss.propItems,tmpss.objItems);
//			}
//		}

		public String getName()
		{
			return name;
		}

		public String getTitle()
		{
			if (Convert.isNullOrEmpty(title))
				return name;
			return this.title;
		}
		
		public String getDesc()
		{
			if(this.desc==null)
				return "" ;
			return this.desc ;
		}

		public boolean isNullable()
		{
			return this.bNullable;
		}

		public boolean isArray()
		{
			return bArray;
		}

		public List<PropItem> getSubObjPropItems()
		{
			ArrayList<PropItem> ret = new ArrayList<>(this.propItems.size()) ;
			ret.addAll(this.propItems.values()) ;
			return ret;
		}
		
		public PropItem getPropItem(String name)
		{
			for(PropItem p:this.propItems.values())
			{
				if(p.name.equals(name))
					return p;
			}
			return null ;
		} 
		
		public List<SubItem> getSubObjSubItem()
		{
			ArrayList<SubItem> ret = new ArrayList<>(this.objItems.size()) ;
			ret.addAll(this.objItems.values()) ;
			return ret;
		}

		public SubItem getSubObjItem(String name)
		{
			for(SubItem subi:this.objItems.values())
			{
				if(subi.name.equals(name))
					return subi ;
			}
			return null ;
		}
		
//		@Override
//		public XmlData toXmlData()
//		{
//			XmlData xd = new XmlData();
//			xd.setParamValue("n", name);
//			if (Convert.isNotNullEmpty(title))
//				xd.setParamValue("t", title);
//			xd.setParamValue("nullable", this.bNullable);
//			xd.setParamValue("array", this.bArray);
//			if (propItems != null)
//			{
//				List<XmlData> xds = xd.getOrCreateSubDataArray("sub");
//				for (PropItem pi : propItems)
//				{
//					xds.add(pi.toXmlData());
//				}
//			}
//			return xd;
//		}
//
//		@Override
//		public void fromXmlData(XmlData xd)
//		{
//			this.name = xd.getParamValueStr("n");
//			this.title = xd.getParamValueStr("t");
//			this.bNullable = xd.getParamValueBool("nullable", true);
//			this.bArray = xd.getParamValueBool("array", false);
//			List<XmlData> xds = xd.getSubDataArray("sub");
//			if (xds != null)
//			{
//				ArrayList<PropItem> pis = new ArrayList<>();
//				for (XmlData tmpxd : xds)
//				{
//					PropItem pi = new PropItem();
//					pi.fromXmlData(tmpxd);
//					pis.add(pi);
//				}
//				this.propItems = pis;
//			}
//		}
		
		PropItem addOrUpPropItem(JSONObject pi_jo)
		{
			String n = pi_jo.getString("n") ;
			PropItem existpi = this.propItems.get(n) ;
			if(existpi==null)
			{
				PropItem pi = new PropItem() ;
				pi.fromJSON(pi_jo,false);
				this.propItems.put(pi.name, pi) ;
				return pi ;
			}
			
			existpi.fromJSON(pi_jo,true) ;
			return existpi;
		}
		
		public boolean fromJSON(JSONObject jo,JSONTempDir curdir) throws Exception
		{
			return fromJSON(jo,curdir,false) ;
		}

		public boolean fromJSON(JSONObject jo,JSONTempDir curdir,boolean update_only) throws Exception
		{
			this.name = jo.getString("n");
			
			if(!update_only || jo.has("t"))
				this.title = jo.optString("t");
			if(!update_only || jo.has("nullable"))
				this.bNullable = jo.optBoolean("nullable", true);
			if(!update_only || jo.has("array"))
				this.bArray = jo.optBoolean("array", false);
			
			if(!update_only || jo.has("sub"))
			{
				Object subob = jo.opt("sub");
				if (subob instanceof String)
				{
					String sub_tempob = (String) subob;
					JSONTemp tob = null;
					if(curdir!=null)
						tob = curdir.getJSONTemp(sub_tempob);
					if(tob==null)
						tob = JSONTempManager.getJSONTemp(sub_tempob) ;
					if (tob == null)
					{
						throw new Exception("no json temp ob  found with name=" + sub_tempob);
					}
					this.propItems.putAll(tob.propItems);//.getPropItems();
					this.objItems.putAll(tob.subObjItems) ;
				}
				else if(subob instanceof JSONObject)
				{
					JSONObject jsob = (JSONObject)subob ;
					JSONArray arr = jsob.optJSONArray("props") ;
					if(arr!=null)
					{
						//LinkedHashMap<String,PropItem> pis = new LinkedHashMap<>();
						int len = arr.length();
						for (int i = 0; i < len; i++)
						{
							JSONObject tmpjo = arr.getJSONObject(i);
//							PropItem pi = new PropItem();
//							pi.fromJSON(tmpjo);
//							pis.put(pi.name,pi);
							this.addOrUpPropItem(tmpjo);
						}
						//this.propItems.putAll(pis);
					}
					arr = jsob.optJSONArray("subs") ;
					if(arr!=null)
					{
						//LinkedHashMap<String,SubItem> pis = new LinkedHashMap<>();
						int len = arr.length();
						for (int i = 0; i < len; i++)
						{
							JSONObject tmpjo = arr.getJSONObject(i);
							String tmpnn = tmpjo.getString("n") ;
							SubItem oldsi = this.objItems.get(tmpnn) ;
							if(oldsi==null)
							{
								SubItem si = new SubItem();
								si.fromJSON(tmpjo,curdir,false);
								this.objItems.put(si.name,si);
							}
							else
							{
								oldsi.fromJSON(tmpjo, curdir, update_only) ;
							}
						}
					}
				}
				else if (subob instanceof JSONArray)
				{
					JSONArray arr = (JSONArray) subob;
					//LinkedHashMap<String,PropItem> pis = new LinkedHashMap<>();
					int len = arr.length();
					for (int i = 0; i < len; i++)
					{
						JSONObject tmpjo = arr.getJSONObject(i);
						//PropItem pi = new PropItem();
						//pi.fromJSON(tmpjo);
						//pis.put(pi.name,pi);
						this.addOrUpPropItem(tmpjo);
					}
					//this.propItems.putAll(pis);
				}
			}//end of sub
			return true;
		}

		public JSONObject toJSON()
		{
			JSONObject jo = new JSONObject();
			jo.put("n", this.name);
			if (this.title != null)
				jo.put("t", this.title);
			jo.put("nullable", this.bNullable);
			jo.put("array", this.bArray);
			if (this.propItems != null)
			{
				JSONArray jarr = new JSONArray();
				for (PropItem pi : this.propItems.values())
				{
					jarr.put(pi.toJSON());
				}
				jo.put("props", jarr);
			}
			if(this.objItems!=null)
			{
				JSONArray jarr = new JSONArray();
				for (SubItem pi : this.objItems.values())
				{
					jarr.put(pi.toJSON());
				}
				jo.put("sub", jarr);
			}
			return jo;
		}
		
		protected String toJSONStr(String ind)
		{
			String next_ind = ind+"    " ;
			String tri_ind = ind+"        " ;
			String forth_ind = ind+"            " ;
			
			String ret = ind+ "{\"n\":\""+this.name+"\",\"t\":\""+this.title+"\"";
			if(!this.bNullable)
				ret += ",\"nullable\":false" ;
			if(this.bArray)
				ret += ",\"array\":true" ;
			
			ret +=",\"sub\":{\r\n" ;
			boolean has_prop=false;
			if (this.propItems != null)
			{
				has_prop=true;
				ret += next_ind +"\"props\":[\r\n" ;
				boolean bfirst = true;
				for (PropItem pi : this.propItems.values())
				{
					if(bfirst==true)
					{
						bfirst=false;
						ret += tri_ind + pi.toJSONStr() +"\r\n";
					}
					else
						ret += tri_ind +","+ pi.toJSONStr() +"\r\n";
				}
				ret += tri_ind + "]\r\n" ;
			}
			
			if(this.objItems!=null)
			{
				if(has_prop)
					ret += next_ind +",\"subs\":[\r\n" ;
				else
					ret += next_ind +"\"subs\":[\r\n" ;
				boolean bfirst = true;
				for(SubItem suboi:this.objItems.values())
				{
					if(bfirst==true)
					{
						bfirst=false;
						ret += suboi.toJSONStr(tri_ind) +"\r\n";
					}
					else
						ret += ","+ suboi.toJSONStr(tri_ind) +"\r\n";
				}
				ret += tri_ind+"]\r\n" ;
			}
			
			ret += next_ind+"}\r\n" ;
			ret += ind+"}" ;
			
			return ret;
		}

		@Override
		public int compareTo(SubItem o)
		{
			return this.name.compareTo(o.name);
		}
		
//		public final String toJSONStr()
//		{
//			return this.toJSONStr("") ;
//		}
	}

	public static JSONTemp loadFromJSON(JSONObject jo,JSONTempDir curdir) throws Exception
	{
		JSONTemp t = new JSONTemp(curdir);
		t.name = jo.optString("name");
		t.title = jo.optString("title");
		t.sortByName = jo.optBoolean("sort_by_name",false) ;
		
		String _extends = jo.optString("extends") ;
		if(Convert.isNotNullEmpty(_extends))
		{
			JSONTemp basejt = JSONTempManager.getJSONTemp(_extends) ;
			if(basejt==null)
				throw new Exception("no base JSONTemp found with name="+_extends) ;
			t.appendOther(basejt) ;
		}
		
		JSONArray jos = jo.optJSONArray("props");
		if (jos != null)
		{
			int len = jos.length();
			for (int i = 0; i < len; i++)
			{
				JSONObject tmpjo = jos.getJSONObject(i);
				t.addOrUpPropItem(tmpjo);
//				PropItem pi = new PropItem();
//				pi.fromJSON(tmpjo);
//				t.propItems.put(pi.name,pi);
			}
		}
		jos = jo.optJSONArray("subs");
		if (jos != null)
		{
			int len = jos.length();
			for (int i = 0; i < len; i++)
			{
				JSONObject tmpjo = jos.getJSONObject(i);
				String nnn = tmpjo.getString("n") ;
				SubItem oldsi=  t.subObjItems.get(nnn);
				if(oldsi==null)
				{
					SubItem si = new SubItem();
					si.fromJSON(tmpjo,curdir);
					t.subObjItems.put(si.name,si);
				}
				else
					oldsi.fromJSON(tmpjo, curdir, true);
					//oldsi.appendOther(si,si.propItems,si.objItems) ;
			}
		}
		return t;
	}
	
	transient JSONTempDir belongToDir = null ;

	String name = null;

	String title = null;

	LinkedHashMap<String,PropItem> propItems = new LinkedHashMap<>();

	LinkedHashMap<String,SubItem> subObjItems = new LinkedHashMap<>();
	
	boolean sortByName = false;
	
	transient String sampleTxt = null ;

	private JSONTemp(JSONTempDir dir)
	{
		this.belongToDir = dir ;
	}
	
	JSONTemp(JSONTempDir dir,String n,String t)
	{
		this.belongToDir = dir ;
		this.name =n;
		this.title = t ;
	}
	
	void sortInner()
	{
//		Collections.sort(this.propItems);
//		Collections.sort(this.subObjItems);
	}
	
	public String getUid()
	{
		if(belongToDir==null)
			return this.name ;
		String did = this.belongToDir.getId() ;
		if(did==null)
			return this.name ;
		return did+"."+this.name ;
	}

	public String getName()
	{
		return this.name;
	}

	public String getTitle()
	{
		return this.title;
	}

	public List<PropItem> getPropItems()
	{
		ArrayList<PropItem> ret = new ArrayList<>(this.propItems.size()) ;
		ret.addAll(this.propItems.values()) ;
		return ret;
	}
	
	public PropItem getPropItem(String name)
	{
		for(PropItem p:this.propItems.values())
		{
			if(p.name.equals(name))
				return p;
		}
		return null ;
	}
	
	PropItem addOrUpPropItem(JSONObject pi_jo)
	{
		if(pi_jo.optBoolean("ignore",false))
			return null ;
		
		String n = pi_jo.getString("n") ;
		PropItem existpi = this.propItems.get(n) ;
		if(existpi==null)
		{
			PropItem pi = new PropItem() ;
			pi.fromJSON(pi_jo,false);
			this.propItems.put(pi.name, pi) ;
			return pi ;
		}
		
		existpi.fromJSON(pi_jo,true) ;
		return existpi;
	}

	public List<SubItem> getSubObjItems()
	{
		ArrayList<SubItem> ret = new ArrayList<>(this.subObjItems.size()) ;
		ret.addAll(this.subObjItems.values()) ;
		return ret;
	}
	
	public SubItem getSubObjItem(String name)
	{
		for(SubItem subi:this.subObjItems.values())
		{
			if(subi.name.equals(name))
				return subi ;
		}
		return null ;
	}
	
	public void appendOther(JSONTemp jt)
	{
		for(PropItem pi:jt.propItems.values())
		{
			pi = new PropItem(pi) ;
			this.propItems.put(pi.name,pi);
		}
		for(SubItem tmpss : jt.subObjItems.values())
		{
			SubItem oldsi = this.subObjItems.get(tmpss.name) ;
			if(oldsi==null)
			{
				oldsi = new SubItem(tmpss) ;
				this.subObjItems.put(tmpss.name, oldsi) ;
			}
			oldsi.appendOther(tmpss,tmpss.propItems,tmpss.objItems);
		}
	}
	/**
	 * 判断输入的json数据是否满足JSONTemp的有效性要求，
	 * 基本判断JSONTemp内部定义的必填属性是否完成，非空subobj是否为空，subobj下面的每个
	 *     非空属性数据是否有数据
	 * @param jo
	 * @return
	 */
	public boolean RT_chkDataValid(JSONObject jo,StringBuilder failedr)
	{
		for(PropItem pi:this.propItems.values())
		{
			if(!chkPropItemValid(pi,jo, failedr))
				return false;
		}
		
		for(SubItem subo:subObjItems.values())
		{
			if(!chSubObjItemValid(subo,jo, failedr))
				return false;
		}
		return true;
	}
	
	private boolean chSubObjItemValid(SubItem subi,JSONObject jo, StringBuilder failedr )
	{
		Object ob = jo.opt(subi.name) ;
		if(!subi.isNullable())
		{
			if(ob==null)
			{
				failedr.append(subi.name+ " cannot be null") ;
				return false;
			}
			
			if(subi.isArray())
			{
				if(!(ob instanceof JSONArray))
				{
					failedr.append(subi.name+ " is not JSONArray") ;
					return false;
				}
				JSONArray jarr = (JSONArray)ob;
				if(jarr.length()<=0)
				{
					failedr.append(subi.name+ " is is empty JSONArray,which cannot be nullable") ;
				}
			}
		}
		if(ob==null)
			return true;
		
		List<JSONObject> chkjos = null ;
		
		if(subi.isArray())
		{
			if(!(ob instanceof JSONArray))
			{
				failedr.append(subi.name+ " is not JSONArray") ;
				return false;
			}
			JSONArray jarr = (JSONArray)ob;
			int len = jarr.length() ;
			if(len<=0)
				return true;
			chkjos = new ArrayList<>(len) ;
			for(int k = 0 ; k < len ; k ++)
				chkjos.add(jarr.getJSONObject(k)) ;
		}
		else
		{
			chkjos = Arrays.asList((JSONObject)ob) ;
		}
		for(JSONObject tmpjo:chkjos)
		{
			if(subi.propItems!=null)
			{
				for(PropItem tmppi:subi.propItems.values())
				{
					if(!this.chkPropItemValid(tmppi, tmpjo, failedr))
						return false;
				}
			}
			if(subi.objItems!=null)
			{
				for(SubItem tmpsi:subi.objItems.values())
				{
					if(!this.chSubObjItemValid(tmpsi, tmpjo, failedr))
						return false;
				}
			}
		}
		return true;
	}

	private boolean chkPropItemValid(PropItem pi,JSONObject jo, StringBuilder failedr )
	{
		Object ov = jo.opt(pi.name) ;
		if(pi.isOuterChk())
			return true;//
		
		if(!pi.bNullable)
		{
			if(ov==null)
			{
				failedr.append(pi.name+ " cannot be null") ;
				return false;
			}
			if(pi.valTp==ValTp.string && "".equals(ov))
			{
				failedr.append(pi.name+ " cannot be empty") ;
				return false;
			}
		}
		if(ov!=null)
		{//判断ov数据类型是否匹配
			switch(pi.valTp)
			{
			case number:
				if(pi.bArray)
				{
					//TODO
				}
				else
				{
					if(!(ov instanceof Number))
					{
						failedr.append(pi.name+ " value must be number") ;
						return false;
					}
				}
				break ;
			case bool:
				if(!(ov instanceof Boolean))
				{
					failedr.append(pi.name+ " value must be bool") ;
					return false;
				}
				break ;
			default:
			}
		}
		return true;
	}
	
	public String getSampleTxt()
	{
		return this.sampleTxt ;
	}
	
	public JSONObject sampleJO = null ;
	
	public JSONObject getSampleJO()
	{
		//if(sampleJO!=null)
		//	return sampleJO ;
		sampleJO = calExampleJO() ;
		return sampleJO ;
	}
	
	private JSONObject calExampleJO()
	{
		JSONObject jo = new JSONObject() ;
		for(PropItem pi:this.propItems.values())
		{
			Object o = calExampleProp(pi);
			if(o==null)
				continue ;
			jo.put(pi.name, o) ;
		}
		
		for(SubItem subi:this.subObjItems.values())
		{
			Object o = calExampleSub(subi);
			if(o==null)
				continue ;
			jo.put(subi.name, o) ;
		}
		return jo ;
	}
	
	private Object calExampleSub(SubItem subi)
	{
		JSONObject jo = new JSONObject() ; 
		for(PropItem pi:subi.propItems.values())
		{
			Object o = calExampleProp(pi);
			if(o==null)
				continue ;
			jo.put(pi.name, o) ;
		}
		for(SubItem sii:subi.objItems.values())
		{
			Object o = calExampleSub(sii);
			if(o==null)
				continue ;
			jo.put(sii.name, o) ;
		}
		
		if(subi.bArray)
		{
			JSONArray jarr = new JSONArray() ;
			jarr.put(jo) ;
			jarr.put(jo) ;
			return jarr; 
		}
		else
		{
			return jo ;
		}
	}
	
	private Object calExampleProp(PropItem pi)
	{
		Object o = pi.getExample();
		if(o==null)
		{
			List<ValOpt> vopts = pi.getValOpts();
			if(vopts==null||vopts.size()<=0)
				return null ;
			o = vopts.get(0).val ;
		}
		
		if(o instanceof JSONArray)
			return o;
		
		ValTp vt = pi.getValTp();
		o = vt.transStrValToObj(o.toString()) ;
		if(pi.isArray())
		{
			JSONArray jarr = new JSONArray() ;
			jarr.put(o);
			return jarr;
		}
		return o ;
	}

	public JSONObject toJSON()
	{
		JSONObject jo = new JSONObject();
		if (Convert.isNotNullEmpty(this.name))
			jo.put("name", this.name);
		if (Convert.isNotNullEmpty(this.title))
			jo.put("title", this.title);
		JSONArray jarr = new JSONArray();
		for (PropItem pi : this.propItems.values())
		{
			jarr.put(pi.toJSON());
		}
		jo.put("props", jarr);

		jarr = new JSONArray();
		for (SubItem pi : this.subObjItems.values())
		{
			jarr.put(pi.toJSON());
		}
		jo.put("subs", jarr);
		return jo;
	}
	
	public String toJSONStr()
	{
		String ret = "{\r\n" ;
		String ind="" ;
		String next_ind = "    ";
		String tri_ind = "        " ;
		
		ret += next_ind+"\"name\":\""+this.name+"\",\r\n" ;
		if (Convert.isNotNullEmpty(this.title))
			ret += next_ind+"\"title\":\""+this.title+"\",\r\n" ;
		ret += next_ind+"\"props\":[\r\n";
		boolean bfirst=true;
		for (PropItem pi : this.propItems.values())
		{
			if(bfirst)
			{
				bfirst=false;
				ret += tri_ind + pi.toJSONStr()+"\r\n" ;
			}
			else
				ret += tri_ind +","+ pi.toJSONStr()+"\r\n" ;
		}
		ret += next_ind+"],\r\n" ;

		ret += next_ind+"\"subs\":[\r\n";
		bfirst=true;
		for (SubItem pi : this.subObjItems.values())
		{
			if(bfirst)
			{
				bfirst=false;
				ret += ind + pi.toJSONStr(tri_ind)+"\r\n" ;
			}
			else
				ret += ind +","+ pi.toJSONStr(tri_ind)+"\r\n" ;
		}
		ret += next_ind+"]\r\n";
		ret += "}\r\n";
		return ret;
	}

	@Override
	public int compareTo(JSONTemp o)
	{
		return this.name.compareTo(o.name) ;
	}
	
	
}
