package org.iottree.core.dict;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.iottree.core.Config;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UAHmi;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UATagG;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * related a xml ddfile
 * 
 * @author Jason Zhu
 */
public class DataClass extends JSObMap
{
	static Lan lan = Lan.getLangInPk(DataClass.class) ;
	
	public static final String ATTRN_EXT_ATTR_NAMES = "ext_attr_names" ;
	public static final String ATTRN_EXT_ATTR_TITLES = "ext_attr_titles" ;
	public static final String ATTRN_EXT_ATTR_DESCS = "ext_attr_descs" ;
	
	public static final String[] BIND_FOR = {"all",UAPrj.NODE_TP,UACh.NODE_TP,UADev.NODE_TP,UATagG.NODE_TP,UATag.NODE_TP,UAHmi.NODE_TP} ;
	public static final String[] BIND_FOR_TITLE = {"All","Project","Channel","Device","TagGroup","Tag","Hmi"} ;
	
	public static enum BindStyle
	{
		s, //single
		m,
		i_b,
		i_i,
		i_f,
		i_s;
		
		
		public String getTitle()
		{
			return lan.g("bs_"+this.name()) ;
		}
		
		public boolean isInput()
		{
			return this.name().startsWith("i_") ;
		}
		
		
	}
	/**
	 * id
	 */
	String classId = null;

	/**
	 * unique name
	 */
	String name = null ;

	/**
	 * default title
	 */
	String title = null ;
	
	
	boolean bEnable=true;
		/**
	 * 
	 */
	String version = null;

	/**
	 * 
	 */
	Date createTime = null;

	/**
	 * 
	 */
	Date lastUpdateTime = null;

	/**
	 * 
	 */
	LinkedHashMap<String,DataNode> rootDataNodes = new LinkedHashMap<>();

	List<DataNode> validRootNodeList= null ;
	/**
	 * when dd is impl by db,it can be used to ext attr definition,
	 * so,when add node ,it can show some input
	 */
	ArrayList<String[]> extNameTitles = new ArrayList<String[]>() ;


	transient DataNode defaultNode = null ;
	
	transient HashMap<String,String> extNameVals = new HashMap<String,String>() ;
	
	/**
	 * default lang
	 */
	transient String defaultLangStr = null ;
	
	
	transient Element relatedEle = null ;
	
	/**
	 * 
	 */
	transient boolean ignoreIntId = false;
	
	
	private DataClass()
	{}
	
	
	static DataClass createNewClass(String name,String title)
	{
		DataClass dc = new DataClass() ;
		dc.classId = CompressUUID.createNewId() ;
		dc.name = name ;
		dc.title = title ;
		return dc ;
	}
	
	
	
	static DataClass loadFromXml(InputStream inputs) throws Exception
	{
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;

		// parse XML XDATA File
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		docBuilder = docBuilderFactory.newDocumentBuilder();

		//docBuilder.p
		doc = docBuilder.parse(inputs);

		Element rootele = doc.getDocumentElement();

		return loadFromEle(rootele);
	}
	
	static DataClass loadFromEle(Element ele) throws Exception
	{
		DataClass dc = new DataClass();
		if(!dc.loadMeEle(ele))
			return null ;
		
		return dc ;
	}
	

	private boolean loadMeEle(Element ele) throws Exception
	{
		relatedEle = ele ;
		
		classId = ele.getAttribute("id");
		name = ele.getAttribute("name");
		if(Convert.isNullOrEmpty(name))
			return false;
		title = ele.getAttribute("title") ;
		bEnable = !"false".equals(ele.getAttribute("__enable")) ;
		
		version = ele.getAttribute("version");
		
		String io = ele.getAttribute("io") ;
		
		extNameVals = Convert.getElementAttrMap(ele);
		
		String ext_attr_names = ele.getAttribute(ATTRN_EXT_ATTR_NAMES);
		if(Convert.isNotNullEmpty(ext_attr_names))
		{
			StringTokenizer nst = new StringTokenizer(ext_attr_names,"|") ;
			String ext_attr_titles = ele.getAttribute(ATTRN_EXT_ATTR_TITLES);
			String ext_attr_descs = ele.getAttribute(ATTRN_EXT_ATTR_DESCS);
			StringTokenizer tst = null ;
			StringTokenizer dst = null ;
			if(ext_attr_titles!=null)
				tst = new StringTokenizer(ext_attr_titles,"|") ;
			if(ext_attr_descs!=null)
				dst = new StringTokenizer(ext_attr_descs,"|") ;
			
			while(nst.hasMoreTokens())
			{
				String n = nst.nextToken().trim() ;
				String t = n ;
				String d = n ;
				if(tst!=null&&tst.hasMoreTokens())
					t = tst.nextToken().trim() ;
				
				if(dst!=null&&dst.hasMoreTokens())
					d = dst.nextToken().trim() ;
				
				extNameTitles.add(new String[]{n,t,d}) ;
			}
		}
		
		Element[] eles = Convert.getSubChildElement(ele,"dd_node");
		if(eles!=null)
		{
			for(Element tmpe:eles)
				constructFromEle(null,tmpe);
		}
		return true;
	}

	private void constructFromEle(DataNode pnode,Element ele)
	{
		DataNode tmpdn = DataNode.createFromEle(this,pnode,ele);
		if(tmpdn==null)
			return ;
		
		this.rootDataNodes.put(tmpdn.getName(), tmpdn) ;
		
		Element[] eles = Convert.getSubChildElement(ele,"dd_node");
		if(eles==null||eles.length<=0)
			return ;
		for(Element tmpe:eles)
		{
			constructFromEle(tmpdn,tmpe) ;
		}
	}
	
	
	
//	public void reloadClass() throws Exception
//	{
//		reloadFile() ;
//	}
//	
	
	public Element getRelatedEle()
	{
		return relatedEle ;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getClassId()
	{
		return classId;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getClassName()
	{
		return name ;
	}
	
	public String getClassTitle()
	{
		if(Convert.isNotNullEmpty(this.title))
			return title ;
		
		return this.getNameBySysLan(this.name) ;
	}

	public boolean isClassEnable()
	{
		return this.bEnable ;
	}
	
	public void setClassEnable(boolean b)
	{
		this.bEnable = b ;
	}
	
	public String getNameBySysLan(String defv)
	{
		String v = getNameByLang(Config.getAppLang()) ;
		if(Convert.isNullOrEmpty(v))
			return defv ;
		return v ;
	}

	public String getNameByLang(String lang)
	{
		if(Convert.isNullOrEmpty(lang))
			return getClassName() ;
		return this.getExtAttrValue("name_"+lang) ;
	}
	
	public void setBindFor(String bf)
	{
		if(Convert.isNullOrEmpty(bf))
			bf="" ;
		this.setExtAttr("bind_for",bf) ;
	}
	
	public List<String> getBindForList()
	{
		String tmps = this.getExtAttrValue("bind_for") ;
		if(Convert.isNullOrEmpty(tmps))
			return null;
		return Convert.splitStrWith(tmps, " ,|") ;
	}
	
	public boolean isBindFor(String bf)
	{
		String tmps = this.getExtAttrValue("bind_for") ;
		if(Convert.isNullOrEmpty(tmps))
			return false;
		List<String> ss = Convert.splitStrWith(tmps, " ,|") ;
		if(ss.contains(bf))
			return true ;
		if(ss.contains("all"))
			return true;
		return false;
	}
	
	public boolean hasBindFor(String bf)
	{
		String tmps = this.getExtAttrValue("bind_for") ;
		if(Convert.isNullOrEmpty(tmps))
			return false;
		List<String> ss = Convert.splitStrWith(tmps, " ,|") ;
		return ss.contains(bf) ;
	}
	
//	public void setBindMulti(boolean b)
//	{
//		if(b)
//			this.setExtAttr("bind_style", "m");
//		else
//			this.setExtAttr("bind_style", "s");
//	}
	
	public void setBindStyle(BindStyle bs)
	{
		this.setExtAttr("bind_style", bs.name());
	}
	
//	public boolean isBindMulti()
//	{
//		return "m".equals() ;
//	}
	
	public BindStyle getBindStyle()
	{
		String n = this.getExtAttrValue("bind_style") ;
		if(Convert.isNullOrEmpty(n))
			return BindStyle.s ;
		return BindStyle.valueOf(n) ;
	}

	public List<String[]> getExtNameTitles()
	{
		return this.extNameTitles ;
	}
	
	public boolean isContainDefExtName(String n)
	{
		if(extNameTitles==null)
			return false;
		
		for(String[] nt:extNameTitles)
		{
			if(nt[0].equals(n))
				return true ;
		}
		return false;
	}
	
	public String getExtAttrValue(String pn)
	{
		return extNameVals.get(pn) ;
	}
	
	public Set<String> getExtAttrNames()
	{
		return this.extNameVals.keySet() ;
	}
	
	public boolean hasExtAttr(String pn)
	{
		return extNameVals.containsKey(pn) ;
	}
	
	public void setExtAttr(String pn,String pv)
	{
		extNameVals.put(pn, pv) ;
	}
	
	public void setExtAttrs(Map<String,String> mp)
	{
		if(mp==null)
			return ;
		
		for(Map.Entry<String,String> n2v:mp.entrySet())
		{
			setExtAttr(n2v.getKey(),n2v.getValue()) ;
		}
	}
	
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String ver)
	{
		version = ver ;
	}

	public Date getCreateTime()
	{
		return createTime;
	}

	public Date getLastUpdateTime()
	{
		return lastUpdateTime;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public DataNode getNodeByName(String name)
	{
		return this.rootDataNodes.get(name) ;
	}
	
	public List<DataNode> listChildNodes(String name)
	{
		ArrayList<DataNode> rets = new ArrayList<>() ;
		String pre = name+".";
		int plen = pre.length() ;
		for(DataNode dn:this.rootDataNodes.values())
		{
			String tmpn = dn.getName() ;
			if(tmpn.startsWith(pre) && tmpn.indexOf('.',plen)<0)
			{
				rets.add(dn);
				continue ;
			}
		}
		return rets;
	}
	
	public List<DataNode> listSelfDescendantsNodes(String name)
	{
		ArrayList<DataNode> rets = new ArrayList<>() ;
		String pre = name+".";
		for(DataNode dn:this.rootDataNodes.values())
		{
			String tmpn = dn.getName() ;
			if(tmpn.equals(name) || tmpn.startsWith(pre))
			{
				rets.add(dn);
				continue ;
			}
			
		}
		return rets;
	}
	/**
	 * 
	 * @param propname
	 * @param propval
	 * @return
	 */
	public List<DataNode> findDataNodesByProp(String propname,String propval,boolean b_ignorecase)
	{
		List<DataNode> r =new ArrayList<>() ;
		for(DataNode dn:rootDataNodes.values())
		{
			String pv =dn.getAttr(propname) ;
			if(pv==null)
				continue ;
			if(b_ignorecase&&pv.equalsIgnoreCase(propval))
				r.add(dn) ;
			else if(pv.equals(propval))
				r.add(dn) ;
		}
		return r ;
	}
	
	public void setDefaultLang(String lang)
	{
		defaultLangStr = lang ;
	}
	
	
	public String getDataNodeLangByName(String name)
	{
		DataNode dn = getNodeByName(name) ;
		if(dn==null)
			return "[x"+name+"x]" ;
		
		return dn.getNameByLang(defaultLangStr) ;
	}

	

	public List<DataNode> getRootNodes()
	{
		ArrayList<DataNode> rets =new ArrayList<>() ;
		for(DataNode dn:this.rootDataNodes.values())
		{
			if(dn.isRootNode())
				rets.add(dn) ;
		}
		return rets;
	}

	public List<DataNode> listValidRootNodes()
	{
		if (validRootNodeList != null)
			return validRootNodeList;

		ArrayList<DataNode> tmpal = new ArrayList<DataNode>();
		tmpal.addAll(rootDataNodes.values());
		validRootNodeList = tmpal ;
		return tmpal;
	}

	ArrayList<DataNode> getAllExpandedNodes()
	{
		ArrayList<DataNode> tmpal = new ArrayList<DataNode>();
		
		tmpal.addAll(this.rootDataNodes.values());

		return tmpal;
	}


	public ArrayList<DataNode> getAllValidExpandedLeafNodes()
	{
		return null ;
	}
	
	
	////////////////////////////
	
	
	public DataNode addOrUpdateDataNode(String name,String title,int ordern,HashMap<String,String> props)
			throws Exception
	{
		DataNode dn = this.getNodeByName(name);
		if(dn!=null)
		{
			dn.updateNode(title, ordern, props);
			return dn ;
		}
		
		dn = new DataNode(this,name,title,ordern,props) ;
		this.rootDataNodes.put(dn.getName(), dn) ;
		
		this.reorder() ;
		
		return dn;
	}
	
	public void delDataNode(String name) throws Exception
	{
		List<DataNode> dns = listSelfDescendantsNodes(name) ;
		if(dns==null||dns.size()<=0)
			return ;
		for(DataNode dn:dns)
		{
			this.rootDataNodes.remove(dn.getName()) ;
		}
	}
	
	void reorder()
	{
		Collection<DataNode> dns = this.rootDataNodes.values();
		ArrayList<DataNode> nnn = new ArrayList<>() ;
		nnn.addAll(dns) ;
		Collections.sort(nnn);
		LinkedHashMap<String,DataNode> tmplhm = new LinkedHashMap<>();
		for(DataNode dn:nnn)
			tmplhm.put(dn.getName(), dn) ;
	}
	
	public void writeToXml(Writer tw) throws IOException
	{
		tw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n") ;
		tw.write("<dd_class id=\""+this.getClassId()+"\" name=\""+this.getClassName()+"\"") ;
		if(title!=null)
			tw.write(" title=\""+this.title+"\"") ;
		if(!bEnable)
			tw.write(" __enable=\"false\"") ;
		for(Map.Entry<String, String> n2v:extNameVals.entrySet())
		{
			String pn = n2v.getKey() ;
			String pv = n2v.getValue() ;
			if("id".equals(pn)||"name".equals(pn)||"title".equals(pn)||"__enable".equals(pn))
				continue ;
			tw.write(" "+pn+"=\"");
			tw.write(Convert.plainToHtml(pv));
			tw.write("\"");
		}
		tw.write(">\r\n") ;
		List<DataNode> rns = this.getRootNodes() ;
		if(rns!=null)
		{
			for(DataNode rn:rns)
			{
				rn.writeToXml(tw);
			}
		}
		tw.write("</dd_class>") ;
		tw.flush();
	}
	
	
	@Override
	public Object JS_get(String  key)
	{
		switch(key)
		{
		case "_id":
			return this.classId;
		case "_name":
			return this.name ;
		case "_title":
			return this.title ;
		case "_names":
			return this.getExtAttrNames() ;
		}
		DataNode dn = this.getNodeByName(key) ;
		if(dn!=null)
			return dn ;
		return this.getExtAttrValue(key) ;
	}
	
	@Override
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props();
		ss.add(new JsProp("_id",null,String.class,false,"DataClass Id","")) ;
		ss.add(new JsProp("_name",null,String.class,false,"DataClass Name","")) ;
		ss.add(new JsProp("_names",null,Set.class,false,"DataClass Names","")) ;
		ss.add(new JsProp("_title",null,String.class,false,"DataClass Title","")) ;


		for(String pn:getExtAttrNames())
		{
			ss.add(new JsProp(pn,null,String.class,false,pn,"")) ;
		}
		List<DataNode> dns = this.getRootNodes() ;
		if(dns!=null)
		{
			for(DataNode dn:dns)
			{
				ss.add(new JsProp(dn.getName(),dn,DataNode.class,true,dn.getTitle(),"Root Node "+dn.getTitle())) ;
			}
		}
		return ss ;
	}
}