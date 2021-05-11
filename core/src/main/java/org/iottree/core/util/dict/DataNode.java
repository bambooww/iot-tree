package org.iottree.core.util.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.*;

import javax.servlet.jsp.PageContext;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.w3c.dom.Element;

/**
 * 数据字典的节点
 * 
 * @author Jason Zhu
 */
public class DataNode implements Comparable<DataNode>
{
	/**
	 * 节点唯一id
	 */
	int id = -1;

	/**
	 * 唯一名称
	 */
	String name = null ;
	
	/**
	 * 父节点
	 */
	int parentNodeId = -1;

	transient int belongToClassId = -1 ;

	transient String belongToModule = null ;
	
	
	transient Element relatedEle = null ;
	/**
	 * 中文名称
	 */
	String nameCn = null;

	/**
	 * 英文名称
	 */
	String nameEn = null;

	/**
	 * 语言字符串到对应语言的名称,如cn-客户管理 en=Custom Mgr
	 */
	HashMap<String,String> lang2name = new HashMap<String,String>() ;

	/**
	 * 排序号
	 */
	int orderNo = -1;

	/**
	 * 是否可见
	 */
	boolean bVisiable = true;

	/**
	 * 是否禁止
	 */
	boolean bForbidden = false;

	/**
	 * 是否是缺省的节点--如果选择时,输入的值没有其他值进行匹配.则使用该节点
	 */
	boolean bDefault = false;

	/**
	 * 创建时间
	 */
	Date createTime = new Date();

	/**
	 * 上次更新时间
	 */
	Date lastUpdateTime = new Date();

	/**
	 * 
	 */
	ArrayList<DataNode> childNodes = new ArrayList<DataNode>();

	DataNode parentNode = null;

	DataNode[] validChildNodes = null;

	DataNode[] validOffspringNodes = null;

	HashMap<String,String> extendAttrMap = new HashMap<String,String>();
	
	private void set_ExtAttrMapStr(String s) throws IOException
	{
		if(Convert.isNullOrTrimEmpty(s))
			return ;
		
		s = s.trim() ;
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		String l = null ;
		while((l=br.readLine())!=null)
		{
			l = l.trim() ;
			if("".equals(l))
				continue ;
			
			int p = l.indexOf('=') ;
			if(p<0)
			{
				extendAttrMap.put(l, "") ;
			}
			else
			{
				extendAttrMap.put(l.substring(0,p).trim(), l.substring(p+1).trim()) ;
			}
		}
	}
	private String get_ExtAttrMapStr() throws IOException
	{
		if(extendAttrMap==null)
			return "" ;
		
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, String> n2v:extendAttrMap.entrySet())
		{
			sb.append(n2v.getKey()).append('=').append(n2v.getValue()).append("\r\n") ;
		}
		return sb.toString() ;
		
	}
	
	transient long autoId = -1 ;
	
	public DataNode()
	{}
	
	public DataNode(long autoid,int id,int pid,String name,
			String namecn,String nameen,
			int ordern,boolean bvis,boolean bforbid,
			Date createt,Date lastup_date,
			HashMap<String,String> extinfo,boolean bdefault)
	{
		this.autoId = autoid ;
		this.id = id ;
		this.parentNodeId = pid ;
		this.name = name ;
		
		this.nameCn = namecn;
		if(namecn!=null)
			lang2name.put("cn", namecn) ;
		
		this.nameEn = nameen;
		if(nameen!=null)
			lang2name.put("en", nameen);
		
		this.orderNo = ordern;
		bVisiable = bvis;
		bForbidden = bforbid;
		createTime = createt;
		lastUpdateTime = lastup_date;
		if(extinfo!=null)
		{
			for(Map.Entry<String,String> n2v:extinfo.entrySet())
			{
				String n = n2v.getKey() ;
				String v = n2v.getValue() ;
				
				extendAttrMap.put(n, v) ;
				
				if(n.startsWith("name_"))
				{
					String langn = n.substring(5);
					lang2name.put(langn, v);
				}
			}
		}
		
		bDefault = bdefault ;
	}
	
	public DataNode(int id,int pid,String name,
			String namecn,String nameen,
			int ordern,
			HashMap<String,String> extinfo)
	{
		this(-1,id,pid,name,namecn,nameen,ordern,true,false,new Date(),new Date(),extinfo,false) ;
	}
	
	public DataNode(int id,int pid,String name,
			String namecn,String nameen,
			int ordern,boolean bvis,boolean bforbid,
			Date createt,Date lastup_date,
			HashMap<String,String> extinfo)
	{
		this(-1,id,pid,name,namecn,nameen,ordern,bvis,bforbid,new Date(),new Date(),extinfo,false) ;
	}

	public DataNode(Element ele,DataNode pn)
	{
		relatedEle = ele ;
		
		String strid = ele.getAttribute("id") ;
		if(strid!=null&&!strid.equals(""))
			id = Integer.parseInt(strid);
		else
			id = 1 ;
		
		name = ele.getAttribute("name");
		
		nameCn = ele.getAttribute("name_cn");
		nameEn = ele.getAttribute("name_en");
		
		HashMap<String,String> ps = Convert.getElementAttrMap(ele) ;
		for(Map.Entry<String, String> n2v:ps.entrySet())
		{
			String n0 = n2v.getKey() ;
			String v0 = n2v.getValue() ;
			if(n0.startsWith("name_"))
			{
				String langn = n0.substring(5);
				lang2name.put(langn, v0);
			}
			
			extendAttrMap.put(n0, v0);
		}
		
		bVisiable = !("0".equals(ele.getAttribute("visible"))||"false".equals(ele.getAttribute("visible")));
		bForbidden = "1".equals(ele.getAttribute("forbidden"))||"true".equals(ele.getAttribute("forbidden"));
		parentNode = pn ;
		if(parentNode!=null)
			parentNodeId = pn.id;
		
		
		Element[] eles = Convert.getSubChildElement(ele,"dd_node");
		if(eles==null)
			return ;
		
		for(Element tmpe:eles)
		{
			childNodes.add(new DataNode(tmpe,this));
		}
		
		bDefault = "true".equalsIgnoreCase(ele.getAttribute("default")) ;
	}
	
	//public DataNode copy
	
	public Element getRelatedEle()
	{
		return relatedEle ;
	}
	
	public long getAutoId()
	{
		return autoId ;
	}

	void appendChildNode(DataNode dn)
	{
		if (dn == this)
			throw new IllegalArgumentException();

		if (dn.parentNode == this)
			return;

		dn.RemoveFromParent();

		dn.parentNode = this;
		dn.parentNodeId = this.id;

		childNodes.add(dn);

		Collections.sort(childNodes);
	}

	void RemoveChildNode(DataNode dn)
	{
		if (dn == this)
			throw new IllegalArgumentException();

		if (dn.parentNode != this)
			throw new IllegalArgumentException();

		this.childNodes.remove(dn);

		dn.parentNode = null;
		dn.parentNodeId = -1;
	}

	void RemoveFromParent()
	{
		if (this.parentNode != null)
			parentNode.RemoveChildNode(this);
	}

	public int getId()
	{
		return id;
	}
	
	

	/**
	 * 得到在本类中的唯一名称
	 * @return
	 */
	public String getName()
	{
		return name ;
	}
	// / <summary>
	// / 中文名称
	// / </summary>
	public String getNameCN()
	{
		return nameCn;
	}

	// / <summary>
	// / 英文名称
	// / </summary>
	public String getNameEn()
	{
		return nameEn;
	}
	
	/**
	 * 根据系统环境获得对应的语言名称
	 * @return
	 */
	public String getNameBySysLan(String defv)
	{
		String v = getNameByLang(Config.getAppLang()) ;
		if(Convert.isNullOrEmpty(v))
			return defv ;
		return v ;
	}

	// / <summary>
	// / 根据语言类型获得对应的名称
	// / </summary>
	// / <param name="lang"></param>
	// / <returns></returns>
	public String getNameByLang(String lang)
	{
		if(Convert.isNullOrEmpty(lang))
			return getName() ;
		
		return lang2name.get(lang);
	}
	
	
//	public String getNameByJspPageContext(PageContext pc)
//	{
//		String lan = AppConfig.getAppLang(pc);
//		return getNameByLang(lan);
//	}
	
	public String getPathNameCn(String path_sep)
	{
		return getPathNameByLang("cn",path_sep) ;
	}
	
	public String getPathNameEn(String path_sep)
	{
		return getPathNameByLang("en",path_sep) ;
	}
	/**
	 * 根据语言获得本节点从根开始的路径字符串
	 * @param lang
	 * @param path_sep 路径分割串,如 / - 等
	 * @return
	 */
	public String getPathNameByLang(String lang,String path_sep)
	{
		StringBuilder sb = new StringBuilder();
		getPathNameByLang(sb,lang,path_sep) ;
		return sb.toString();
	}
	
	private void getPathNameByLang(StringBuilder sb,String lang,String path_sep)
	{
		if(this.parentNode==null)
		{
			sb.append(getNameByLang(lang));
			return ;
		}
		
		parentNode.getPathNameByLang(sb, lang, path_sep);
		
		sb.append(path_sep).append(getNameByLang(lang));
	}

	// / <summary>
	// / 排序号
	// / </summary>
	public int getOrderNo()
	{
		return orderNo;
	}

	// / <summary>
	// / 在字典树中的层次，比如根为1，根的直接孩子为2
	// / </summary>
	public int getLevel()
	{
		if (this.parentNode == null)
			return 1;

		return parentNode.getLevel() + 1;
	}

	// / <summary>
	// / 是否可见
	// / </summary>
	public boolean isVisiable()
	{
		return bVisiable;
	}

	// / <summary>
	// / 是否禁止
	// / </summary>
	public boolean isForbidden()
	{
		return bForbidden;
	}

	// / <summary>
	// / 创建时间
	// / </summary>
	public Date getCreateTime()
	{
		return createTime;
	}

	// / <summary>
	// / 上次更新时间
	// / </summary>
	public Date getLastUpdateTime()
	{
		return lastUpdateTime;
	}
	
	public boolean isDefaultNode()
	{
		return bDefault ;
	}

	public DataNode getRootNode()
	{
		if (parentNode == null)
			return this;

		return parentNode.getRootNode();
	}
	
	public int getBelongToClassId()
	{
		return getRootNode().belongToClassId ;
	}
	
	public DataClass getBelongToClass()
	{
		if(Convert.isNotNullEmpty(belongToModule))
			return DictManager.getInstance().getDataClass(belongToModule,this.belongToClassId) ;
		else
			return DictManager.getInstance().getDataClass(this.belongToClassId) ;		
	}

	public DataNode getParentNode()
	{
		return parentNode;
	}
	
	
	public boolean checkIsAncestor(DataNode dn)
	{
		if(belongToClassId!=dn.belongToClassId)
			return false;
		
		if(this.id==dn.id)
			return false;
		
		DataNode pdn = this ;
		while((pdn = pdn.getParentNode())!=null)
		{
			if(pdn.id==dn.id)
				return true ;
		}
		
		return false;
	}
	
	public int getParentNodeId()
	{
		return parentNodeId ;
	}

	public boolean hasChild()
	{
		return childNodes.size() > 0;
	}

	public boolean isLeaf()
	{
		return !hasChild();
	}
	// / <summary>
	// / 子孙节点
	// / </summary>
	public DataNode[] getChildNodes()
	{
		DataNode[] rets = new DataNode[childNodes.size()];
		return childNodes.toArray(rets);
	}
	
	public int getChildMaxOrderNo()
	{
		int r = 0 ;
		for(DataNode dn:childNodes)
		{
			int orn = dn.getOrderNo() ;
			if(orn>r)
				r = orn ;
		}
		return r ;
	}

	public DataNode getChildNodeByName(String n)
	{
		for(DataNode dn:childNodes)
		{
			if(dn.getName().equals(n))
				return dn ;
		}
		return null ;
	}
	// / <summary>
	// / 获得有效的子节点
	// / </summary>
	public DataNode[] getValidChildNodes()
	{
		if (validChildNodes != null)
			return validChildNodes;

		ArrayList<DataNode> tmpal = new ArrayList<DataNode>();
		for (DataNode dn : childNodes)
		{
			if (dn.isForbidden())
				continue;

			if (dn.isVisiable())
				tmpal.add(dn);
		}

		// tmpal.sort() ;

		DataNode[] rets = new DataNode[tmpal.size()];
		return tmpal.toArray(rets);
	}

	// / <summary>
	// / 子孙后代节点
	// / </summary>
	private DataNode[] getOffspringNodes()
	{
		throw new RuntimeException("not impl");
	}

	public DataNode[] getValidOffspringNodes()
	{
		if (validOffspringNodes != null)
			return validOffspringNodes;

		ArrayList<DataNode> tmpal = new ArrayList<DataNode>();
		getValidOffspringNodes(this, tmpal);

		DataNode[] rets = new DataNode[tmpal.size()];
		return tmpal.toArray(rets);
	}

	private void getValidOffspringNodes(DataNode dn, ArrayList<DataNode> al)
	{
		for (DataNode tmpdn : dn.childNodes)
		{
			if (tmpdn.isForbidden())
				continue;

			if (tmpdn.isVisiable())
				al.add(tmpdn);

			getValidOffspringNodes(tmpdn, al);
		}
	}

	// / <summary>
	// / 得到搜索相关id串，中间用,分割
	// / </summary>
	// / <returns></returns>
	public String getSearchIdsStr()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.id);
		DataNode[] dns = getValidOffspringNodes();
		for (int i = 0; i < dns.length; i++)
		{
			sb.append(',').append(dns[i].id);
		}

		return sb.toString();
	}

	/**
	 * 得到属性值
	 * @param attrn
	 * @return
	 */
	public String getAttrValue(String attrn)
	{
		if("id".equals(attrn))
			return ""+this.id ;
		
		return getExtendedAttr(attrn);
	}
	// / <summary>
	// / 根据属性名称获得扩展属性值——该方法可以获得xml字典文件中的节点所有属性
	// / 这样字典可以提供更多的扩展属性
	// / </summary>
	// / <param name="attrname"></param>
	// / <returns></returns>
	public String getExtendedAttr(String attrname)
	{
		return extendAttrMap.get(attrname);
	}
	
	public Set<String> getExtendedAttrNames()
	{
		return extendAttrMap.keySet() ;
	}
	
	public void setExtendedAttr(String attrn,String attrv)
	{
		extendAttrMap.put(attrn, attrv);
	}
	/**
	 * 得到未定义的扩展属性文本--用来后台编辑管理之用
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
				continue ;//排除定义的扩展属性
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
		tw.write("[" + id + "]");
		if (nameCn != null)
			tw.write(nameCn);
		if (nameEn != null)
			tw.write(nameEn);
		tw.write(getSearchIdsStr());
		tw.write("\r\n");

		tw.flush();
		
		DataNode[] cdns = this.getChildNodes();
		for (int i = 0; i < cdns.length; i++)
		{
			cdns[i].writeTo(tw);
		}
	}
	
	public void writeToXml(Writer tw) throws IOException
	{
		int lv = this.getLevel();
		for (int i = 0; i < lv; i++)
		{
			tw.write("  ");
		}

		//<dd_node name="gz_gy_by" name_cn="白云区" gis_pn="Name" gis_pv="白云区"/>
		tw.write("<dd_node id=\""+this.getId()+"\" name=\""+this.getName()+"\"");
		for(Map.Entry<String, String> n2v:extendAttrMap.entrySet())
		{
			String pn = n2v.getKey() ;
			String pv = n2v.getValue() ;
			if("id".equals(pn)||"name".equals(pn))
				continue ;
			tw.write(" "+pn+"=\"");
			tw.write(Convert.plainToHtml(pv));
			tw.write("\"");
		}
		DataNode[] cdns = this.getChildNodes();
		if(cdns==null||cdns.length<=0)
		{
			tw.write("/>\r\n");
			return ;
		}
		
		tw.write(">\r\n");
		for (int i = 0; i < cdns.length; i++)
		{
			cdns[i].writeToXml(tw);
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
		return "(" + this.getOrderNo() + ")[id=" + this.getId() + "][namecn="
				+ this.getNameCN() + "][nameen=" + this.getNameEn() + "]";
	}

	public int compareTo(DataNode o)
	{
		return this.orderNo - o.orderNo;
	}

}