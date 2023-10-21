package org.iottree.core.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.*;

import javax.servlet.jsp.PageContext;

import org.iottree.core.Config;
import org.iottree.core.UANode;
import org.iottree.core.basic.JSObMap;
import org.iottree.core.util.Convert;
import org.w3c.dom.Element;

/**
 * dict data ndoe
 * 
 * @author Jason Zhu
 */
public class DataNode extends JSObMap implements Comparable<DataNode>
{
	/**
	 * name
	 * xxx.xxx.xxx
	 * xxx rootnode lvl=1
	 * xxx.xxx lvl=2
	 */
	String name = null ;
	
	String title = null ;
	
	
	transient DataClass belongTo = null ;

	
	/**
	 * 
	 */
	int orderNo = -1;

	/**
	 * 
	 */
	//boolean bDefault = false;

	/**
	 * 
	 */
	HashMap<String,String> extendAttrMap = new HashMap<String,String>();
	
	
	public DataNode(DataClass dc,String name,String title,	int ordern,	HashMap<String,String> extinfo) //,boolean bdefault)
	{
		this.belongTo = dc ;
		
		this.name = name ;
		updateNode(title,	ordern,	extinfo);
	}
	
	void updateNode(String title,	int ordern,	HashMap<String,String> extinfo)
	{
		this.title = title;
		
		this.orderNo = ordern;
		
		if(extinfo!=null)
		{
			for(Map.Entry<String,String> n2v:extinfo.entrySet())
			{
				String n = n2v.getKey() ;
				String v = n2v.getValue() ;
				
				extendAttrMap.put(n, v) ;
			}
		}
	}

	static DataNode createFromEle(DataClass dc,DataNode pnode,Element ele)
	{
		String name = ele.getAttribute("name");
		if(Convert.isNullOrEmpty(name))
			return null ;
		
		String title = ele.getAttribute("title") ;
		if(Convert.isNullOrEmpty(title))
			title = name ;
		
		HashMap<String,String> attrm = Convert.getElementAttrMap(ele) ;
		int ordn = Convert.parseToInt32(ele.getAttribute("order_no"), 100) ;
		//bDefault = "true".equalsIgnoreCase(ele.getAttribute("default")) ;
		
		if(pnode!=null)
			name = pnode.getName()+"."+name ;
		
		return new DataNode(dc,name,title,ordn,attrm);
	}
	
	
	/**
	 * get unique name in DataClass
	 * @return
	 */
	public String getName()
	{
		return name ;
	}
	
	public String getNameLocal()
	{
		int i = name.lastIndexOf('.') ;
		if(i<=0)
			return name ;
		return name.substring(i+1) ;
	}
	
	public String getNameTop()
	{
		int i = name.indexOf('.') ;
		if(i<=0)
			return name ;
		return name.substring(0,i) ;
	}
	
	public String getNameParent()
	{
		int i = name.lastIndexOf('.') ;
		if(i<=0)
			return null ;
		return name.substring(0,i) ;
	}
	
	public boolean isRootNode()
	{
		return name.indexOf('.')<0;
	}
	
	public String getTitle()
	{
		if(Convert.isNotNullEmpty(this.title))
			return title ;
		
		return this.getNameBySysLan(this.name) ;
	}
	
	/**
	 * get lang by system setup
	 * @return
	 */
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
			return getName() ;
		return this.getAttr("name_"+lang) ;
	}
	

	public int getOrderNo()
	{
		return orderNo;
	}

	// 
	// root = 1 sub 2 3 4 
	public int getLevel()
	{
		return Convert.splitStrWith(name, ".").size() ;
	}

	
	public DataNode getRootNode()
	{
		return this.belongTo.getNodeByName(getNameTop()) ;
	}
	
	public DataClass getBelongToClass()
	{
		return this.belongTo ;		
	}

	public DataNode getParentNode()
	{
		String pn = getNameParent() ;
		if(pn==null)
			return null ;
		return this.belongTo.getNodeByName(pn) ;
	}
	
	
	public boolean checkIsAncestor(DataNode dn)
	{
		return name.startsWith(dn.getName()+".") ;
	}
	
	public boolean hasChild()
	{
		return getChildNodes().size() > 0;
	}

	public boolean isLeaf()
	{
		return !hasChild();
	}
	
	public List<DataNode> getChildNodes()
	{
		return this.belongTo.listChildNodes(this.getName()) ;
	}
	
	public int getChildMaxOrderNo()
	{
		int r = 0 ;
		for(DataNode dn:getChildNodes())
		{
			int orn = dn.getOrderNo() ;
			if(orn>r)
				r = orn ;
		}
		return r ;
	}

	public DataNode getChildNodeByName(String n)
	{
		String tmpn = this.getName()+"."+n ;
		return this.belongTo.getNodeByName(tmpn) ;
	}

	// this method can get all attribute in node,it can also get ext attribute
	public String getAttr(String attrname)
	{
		return extendAttrMap.get(attrname);
	}
	
	public Set<String> getAttrNames()
	{
		return extendAttrMap.keySet() ;
	}
	
	public void setAttr(String attrn,String attrv)
	{
		extendAttrMap.put(attrn, attrv);
	}
	
	
	/**
	 * xxx=xxx
	 * yyy=kkk
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getUndefinedExtAttrMapStr() throws IOException
	{
		if(extendAttrMap==null)
			return "" ;
		
		StringBuilder sb = new StringBuilder();
		DataClass dc = getBelongToClass() ;
		for(Map.Entry<String, String> n2v:extendAttrMap.entrySet())
		{
			String n = n2v.getKey() ;
			if(dc.isContainDefExtName(n))
				continue ;//
			sb.append(n).append('=').append(n2v.getValue()).append("\r\n") ;
		}
		return sb.toString() ;
		
	}

	public void writeTo(Writer tw) throws IOException
	{
		int lv = this.getLevel();
		for (int i = 0; i < lv; i++)
		{
			tw.write("  ");
		}

		tw.write("(" + lv + ") ");
		tw.write("[" + name + "]");
		if (title != null)
			tw.write(title);
		tw.write("\r\n");

		tw.flush();
		
		for(DataNode tmpdn:this.getChildNodes())
		{
			tmpdn.writeTo(tw);
		}
	}
	
	public void writeToXml(Writer tw) throws IOException
	{
		int lv = this.getLevel();
		for (int i = 0; i < lv; i++)
		{
			tw.write("  ");
		}

		
		tw.write("<dd_node name=\""+this.getName()+"\"");
		if(title!=null)
			tw.write(" title=\""+this.title+"\"") ;
		for(Map.Entry<String, String> n2v:extendAttrMap.entrySet())
		{
			String pn = n2v.getKey() ;
			String pv = n2v.getValue() ;
			if("id".equals(pn)||"name".equals(pn)||"title".equals(pn))
				continue ;
			tw.write(" "+pn+"=\"");
			tw.write(Convert.plainToHtml(pv));
			tw.write("\"");
		}
		List<DataNode> cdns = this.getChildNodes();
		if(cdns==null||cdns.size()<=0)
		{
			tw.write("/>\r\n");
			return ;
		}
		
		tw.write(">\r\n");
		for (DataNode tmpdn:cdns)
		{
			tmpdn.writeToXml(tw);
		}
		
		lv = this.getLevel();
		for (int i = 0; i < lv; i++)
		{
			tw.write("  ");
		}
		tw.write("</dd_node>\r\n");
		tw.flush();
	}

	public String toLvlString(String onelvl_prefix)
	{
		StringBuilder tmpsb = new StringBuilder();

		int lvl = this.getLevel();
		for (int i = 1; i < lvl; i++)
		{
			tmpsb.append(onelvl_prefix);
		}

		tmpsb.append(toString());

		return tmpsb.toString();
	}

	public String toString()
	{
		return "(" + this.getOrderNo() + ")[name=" + this.getName() + "][title="
				+ this.getTitle() + "]";
	}

	public int compareTo(DataNode o)
	{
		int r = this.name.compareTo(o.name) ;
		if(r!=0)
			return r ;
		return this.orderNo - o.orderNo;
	}

	public Object JS_get(String  key)
	{
		switch(key)
		{
		case "_name":
			return this.name ;
		case "_title":
			return this.title ;
		}
		String tmps = this.getAttr(key) ;
		if(tmps!=null)
			return tmps ;
		return this.getChildNodeByName(key) ;
	}
	
	public List<String> JS_names()
	{
		List<String> ss = super.JS_names();
		ss.add("_name") ;
		ss.add("_title") ;
		for(String tmps:this.getAttrNames())
			ss.add(tmps) ;
		
		List<DataNode> subns = this.getChildNodes() ;
		if(subns!=null)
		{
			for(DataNode n:subns)
				ss.add(n.getName()) ;
		}
		return ss ;
	}
}