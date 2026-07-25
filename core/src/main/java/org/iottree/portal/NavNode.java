package org.iottree.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class NavNode
{
	boolean bSep = false;


	private String id_name = null;

	private String title = null;

	private String url = null;


	private String icon = null;
	
	/**
	 *
	 */
	private String img = null;

	
	private String target = null;

	String tips = null;
	String roles = null;

	
	private int order=-1;	

	String notroles=null;
	
	private int x = -1;

	private int y = -1;

	private ArrayList<NavNode> childs = new ArrayList<>();
	/**
	 *
	 */
	private int extType = -1;
	/**
	 *
	 */
	private String extInfo = null;

	private HashSet<String> relatedLink = new HashSet<String>();
	
	HashMap<String,String> extAttr = null;

	transient NavNode parentTN = null;
	
	transient int navIdx = -1 ; //nav1=1 nav2=2

	public NavNode()
	{

	}

	public NavNode(boolean bsep)
	{
		bSep = bsep;
	}
	/**
	 * name,title,icon,url,img,order
	 * */
	public NavNode(String id_n, String title,String icon, String url,  String img,int ordernum)
	{
		this.id_name = id_n;
		this.title = title;
		this.url = url;
		this.icon = icon;
		this.img = img;
		this.order=ordernum;
	}
	/**
	 * name,title,icon,url,target,roles,order
	 * */
	public NavNode(String id_n, String title,String icon,String url, String tar,String roles,int ordernum)
	{
		this.id_name = id_n;
		this.title = title;
		this.icon = icon;
		this.url = url;
		this.target = tar;
		this.roles=roles;
		this.order=ordernum;
	}
	
	public NavNode(String id_n, String title, String url)
	{
		this(id_n, title, url, (String[]) null);
	}

	public NavNode(String id_n, String title, String url,
			String[] relatedurl)
	{
		this.id_name = id_n;
		this.title = title;
		this.url = url;

		relatedLink.add(url);

		if (relatedurl != null)
		{
			for (String rul : relatedurl)
				relatedLink.add(rul);
		}
	}

	public NavNode(String id_n, String title, String url, String icon)
	{
		this.id_name = id_n;
		this.title = title;
		this.url = url;
		this.icon = icon;
	}

	public NavNode(String id_n, String title, String url, String icon,
			String roles, String tar, String tips)
	{
		this(id_n, title, url, icon);
		this.tips = tips;
		this.roles = roles;
		this.target = tar;
	}

	public NavNode copyMe()
	{
		NavNode wtn = new NavNode();
		wtn.bSep = this.bSep;
		wtn.id_name = this.id_name;
		wtn.title = this.title;
		wtn.url = this.url;
		wtn.icon = this.icon;
		wtn.target = this.target;
		wtn.tips = this.tips;
		wtn.roles = this.roles;
		wtn.notroles=this.notroles;
		wtn.x = this.x;
		wtn.y = this.y;
		wtn.extInfo = this.extInfo;
		wtn.extType = this.extType;
		// wtn.childs = this.childs;
		wtn.relatedLink = this.relatedLink;
		wtn.extAttr = this.extAttr ;
		wtn.navIdx = this.navIdx ;
		// wtn.parentTN = this.parentTN;

		return wtn;
	}
	
	NavNode asUrl(String url)
	{
		this.url = url ;
		return this ;
	}
	
	public String getNavUID()
	{
		if(this.navIdx<1)
			return null ;
		if(this.navIdx==1)
			return this.id_name ;
		return this.parentTN.getNavUID() +"."+this.id_name ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("id_name", this.id_name) ;
		jo.putOpt("title", this.title) ;
		jo.putOpt("url", this.url) ;
		jo.putOpt("icon", this.icon) ;
		jo.putOpt("target", this.target) ;
		jo.putOpt("tips", this.tips) ;
		if(extAttr!=null)
		{
			for(Map.Entry<String, String> n2v:this.extAttr.entrySet())
			{
				jo.putOpt(n2v.getKey(), n2v.getValue()) ;
			}
		}
		return jo ;
	}

	public boolean isSeperator()
	{
		return bSep;
	}

	public String getIdName()
	{
		return id_name;
	}
	
	public String getUID()
	{
		if(this.parentTN==null)
			return this.getIdName() ;
		return this.parentTN.getIdName()+"."+this.getIdName() ;
	}

	public int getLevel()
	{
		if (parentTN == null)
			return 0;

		return parentTN.getLevel() + 1;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String t)
	{
		title = t;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String u)
	{
		url = u;
	}
	
	public String getUrlPath()
	{
		if(Convert.isNullOrEmpty(this.url))
			return null ;
		int k = this.url.indexOf('?') ;
		if(k<=0)
			return this.url ;
		return this.url.substring(0,k) ;
	}
	
	public HashMap<String,String> getUrlPM()
	{
		if(Convert.isNullOrEmpty(this.url))
			return null ;
		int k = this.url.indexOf('?') ;
		if(k<=0)
			return null ;
		String ss = this.url.substring(k+1) ;
		return Convert.parseUrlParamStr(ss) ;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public NavNode setXY(int x, int y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public String getIcon()
	{
		return icon;
	}

	public NavNode setIcon(String icon)
	{
		this.icon = icon;
		return this;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String t)
	{
		target = t;
	}

	public String getTips()
	{
		return tips;
	}

	public void setTips(String t)
	{
		tips = t;
	}

	public String getRoles()
	{
		return roles;
	}

	public void setRoles(String r)
	{
		roles = r;
	}
	
	/**��ֹ�Ľ�ɫ**/
	public String getNotRoles()
	{
		return notroles;
	}
	/**��ֹ�Ľ�ɫ**/
	public void setNotRoles(String notRole)
	{
		notroles = notRole;
	}
	
	public String getExtInfo()
	{
		return extInfo;
	}

	public int getExtType()
	{
		return extType;
	}
	
	public String getAttrVal(String attrn)
	{
		if(extAttr==null)
			return null ;
		return extAttr.get(attrn) ;
	}

	public String getAttrVal(String attrn,String defv)
	{
		if(extAttr==null)
			return defv ;
		String r = extAttr.get(attrn) ;
		if(Convert.isNullOrEmpty(r))
			return defv;
		return r ;
	}
	/**
	 * 
	 * @param rl
	 * @return
	 */
	public boolean checkRelatedLink(String rl)
	{
		return relatedLink.contains(rl);
	}

	public void setExtInfo(int ext_type, String ext_info)
	{
		extType = ext_type;
		extInfo = ext_info;
	}

	public NavNode getParent()
	{
		return parentTN;
	}

	public NavNode appendChild(NavNode wtn)
	{
		wtn.removeFromParent();

		wtn.parentTN = this;
		childs.add(wtn);

		return this;
	}

	public NavNode getFirstChild()
	{
		if (childs == null || childs.size() <= 0)
			return null;

		return childs.get(0);
	}

	public NavNode getLastChild()
	{
		if (childs == null || childs.size() <= 0)
			return null;

		return childs.get(childs.size() - 1);
	}

	public void removeFromParent()
	{
		if (parentTN == null)
			return;

		parentTN.childs.remove(this);
		parentTN = null;
	}

	public ArrayList<NavNode> getChildNodes()
	{
		return childs;
	}

	public boolean hasChild()
	{
		return childs.size() > 0;
	}

	public NavNode getChildNodeById(String id)
	{
		if (id == null)
			return null;

		for (NavNode wtn : childs)
		{
			if (wtn.getIdName().equals(id))
				return wtn;
		}
		return null;
	}

	public int getIdxInParent()
	{
		if (parentTN == null)
			return -1;

		return parentTN.childs.indexOf(this);
	}

	public boolean isLastInParent()
	{
		if (parentTN == null)
			return true;

		return parentTN.getLastChild() == this;
	}

	/**
	 * ��ôӸ���ʼ�����ڵ�ĸ��ڵ������������нڵ�
	 * 
	 * @return
	 */
	public ArrayList<NavNode> getAncestors()
	{
		if (parentTN == null)
			return null;

		return parentTN.getNodePath();
	}

	/**
	 * ��ôӸ���ʼ�����ڵ������������нڵ�
	 * 
	 * @return
	 */
	public ArrayList<NavNode> getNodePath()
	{
		ArrayList<NavNode> wtns = new ArrayList<NavNode>();
		getNodePaths(wtns);
		return wtns;
	}

	private void getNodePaths(ArrayList<NavNode> ans)
	{
		if (parentTN != null)
			parentTN.getNodePaths(ans);

		ans.add(this);
	}

	public ArrayList<String> getPathIdNames()
	{
		ArrayList<String> rets = new ArrayList<String>();
		getPathIdNames(rets);
		return rets;
	}

	private void getPathIdNames(ArrayList<String> pins)
	{
		if (parentTN != null)
			parentTN.getPathIdNames(pins);

		pins.add(this.id_name);
	}

	// 
//	public boolean checkUser(UserProfile up)
//	{
//		if (roles == null)
//			return false;
//
//		StringTokenizer st = new StringTokenizer(roles, ",|");
//		while (st.hasMoreTokens())
//		{
//			String tmps = st.nextToken().trim();
//			if (up.containsRoleName(tmps))
//			{
//				// up.checkMatchOneRoleName();
//				return true;
//			}
//		}
//		return false;
//	}
	
	
	public boolean checkRoles(List<String> rrs)
	{
		if (roles == null)
			return false;

		StringTokenizer st = new StringTokenizer(roles, "\\,|");
		while (st.hasMoreTokens())
		{
			String tmps = st.nextToken().trim();
			if (rrs.contains(tmps))
				return true;
		}
		return false;
	}
	
//	public boolean checkUserIsNotAllow(UserProfile up)
//	{
//		if (notroles == null||notroles=="")
//			return false;
//
//		StringTokenizer st = new StringTokenizer(notroles, "\\|,");
//		while (st.hasMoreTokens())
//		{
//			String tmps = st.nextToken().trim();
//			if (up.containsRoleName(tmps))
//			{
//				// up.checkMatchOneRoleName();
//				return true;
//			}
//		}
//		return false;
//	}
	
	
	public boolean checkRolesIsNotAllow(String role)
	{
		if (notroles == null)
			return false;
		StringTokenizer st = new StringTokenizer(notroles, "\\|,");
		while (st.hasMoreTokens())
		{
			String tmps = st.nextToken().trim();
			if (tmps.equals(role))
				return true;
		}
		return false;
	}
	
}
