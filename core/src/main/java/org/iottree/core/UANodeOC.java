package org.iottree.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.web.LoginUtil;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public abstract class UANodeOC extends UANode implements IOCBox
{
	JSONObject ocUnitJSONObj = null ;
	
//	@data_val(param_name = "r_roles")
//	String readRoles = null;
	
	@data_val(param_name = "w_roles")
	String writeRoles = null;
	
	public UANodeOC()
	{
		super();
	}
	
	public UANodeOC(String name,String title,String desc)
	{
		super(name,title,desc) ;
	}
	
	/**
	 * true node may has sub unit
	 * @return
	 */
	public boolean OC_supportSub()
	{
		return false;
	}
	
	public List<IOCBox> OC_getSubs()
	{
		return new ArrayList<>();
	}
	
	public JSONObject OC_getPropsJSON()
	{
		return ocUnitJSONObj;
	}
	
	public void OC_setPropsJSON(JSONObject jo)
	{
		ocUnitJSONObj = jo;
	}
	
	public void OCUnit_setProp(String pn,Object pv)
	{
		if(ocUnitJSONObj==null)
			ocUnitJSONObj = new JSONObject() ;
		ocUnitJSONObj.put(pn, pv);
	}
	
	
	
	private List<PropGroup> nodeOcPGS = null;

	@Override
	public List<PropGroup> listPropGroups()
	{
		if (nodeOcPGS != null)
			return nodeOcPGS;
		ArrayList<PropGroup> pgs = new ArrayList<>();
		List<PropGroup> lpgs = super.listPropGroups();
		if (lpgs != null)
			pgs.addAll(lpgs);
		pgs.add(this.getAuthPropGroup());
		nodeOcPGS = pgs;
		return pgs;
	}
	
	private PropGroup getAuthPropGroup()
	{
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		PropGroup r = new PropGroup("auth", lan);//"Project");

//		r.addPropItem(new PropItem("r_roles",lan, PValTP.vt_str, false, null, null,
//				"").withPop(PropItem.POP_N_ROLES));
		
		r.addPropItem(new PropItem("w_roles",lan, PValTP.vt_str, false, null, null,
				"").withPop(PropItem.POP_N_ROLES));
		
		return r;
	}

	public Object getPropValue(String groupn, String itemn)
	{
		if ("auth".contentEquals(groupn))
		{
			switch (itemn)
			{
//			case "r_roles":
//				return this.readRoles;
			case "w_roles":
				return this.writeRoles;
			}
		}
		return super.getPropValue(groupn, itemn);
	}

	public boolean setPropValue(String groupn, String itemn, String strv)
	{
		if ("auth".contentEquals(groupn))
		{
			switch (itemn)
			{
//			case "r_roles":
//				this.readRoles = strv;
//				clearCache();
//				return true;
			case "w_roles":
				this.writeRoles = strv;
				clearCache();
				return true;
			}
		}
		return super.setPropValue(groupn, itemn, strv);
	}
	
	//private transient HashSet<String> readRoleSet = null ;
	private transient HashSet<String> writeRoleSet = null ;
	private synchronized void clearCache()
	{
//		readRoleSet = null ;
		writeRoleSet = null ;
	}
	
//	public HashSet<String> getReadRolesLocal()
//	{
//		if(readRoleSet != null)
//			return readRoleSet ;
//		if(Convert.isNullOrEmpty(this.readRoles))
//			return null ;
//		readRoleSet = new HashSet<>() ;
//		readRoleSet.addAll(Convert.splitStrWith(this.readRoles, ",|")) ;
//		return readRoleSet ;
//	}
	
	public HashSet<String> getWriteRolesLocal()
	{
		if(writeRoleSet != null)
			return writeRoleSet ;
		if(Convert.isNullOrEmpty(this.writeRoles))
			return null ;
		writeRoleSet = new HashSet<>() ;
		writeRoleSet.addAll(Convert.splitStrWith(this.writeRoles, ",|")) ;
		return writeRoleSet ;
	}
	
	public boolean hasAuthRoleLocal()
	{
		HashSet<String> rr = getWriteRolesLocal();
		if(rr!=null&&rr.size()>0)
			return true ;
//		rr = getReadRolesLocal();
//		if(rr!=null&&rr.size()>0)
//			return true ;
		return false;
	}
//	/**
//	 * self or inherient 
//	 * @return
//	 */
//	public HashSet<String> getReadRolesUsed()
//	{
//		HashSet<String> rs = getReadRolesLocal() ;
//		if(rs!=null&& rs.size()>0)
//			return rs ;
//		UANodeOC pn = (UANodeOC)this.getParentNode() ;
//		if(pn==null)
//			return null ;
//		return pn.getReadRolesUsed() ;
//	}
	
	public HashSet<String> getWriteRolesUsed()
	{
		HashSet<String> rs = getWriteRolesLocal() ;
		if(rs!=null&& rs.size()>0)
			return rs ;
		UANodeOC pn = (UANodeOC)this.getParentNode() ;
		if(pn==null)
			return null ;
		return pn.getWriteRolesUsed() ;
	}
	
	public boolean hasWriteRolesUsed()
	{
		HashSet<String> rs = getWriteRolesUsed() ;
		if(rs==null)
			return false;
		return rs.size()>0 ;
	}
	
//	public boolean RT_checkReadUserRight(LoginUtil.SessionItem login_si)
//	{
//		HashSet<String> rs = getReadRolesUsed() ;
//		if(rs==null||rs.size()<=0)
//			return true ;
//		
//		if(login_si==null)
//			return false;//
//		for(String r:rs)
//		{
//			if(login_si.hasRole(r))
//				return true;
//		}
//		
//		return false;
//	}
	
	public boolean RT_checkWriteUserRight(LoginUtil.SessionItem login_si)
	{
		HashSet<String> rs = getWriteRolesUsed() ;
		if(rs==null||rs.size()<=0)
			return true ;
		
		if(login_si==null)
			return false;//
		for(String r:rs)
		{
			if(login_si.hasRole(r))
				return true;
		}
		return false;
	}
	
	
	public void afterXmlDataExtract(XmlData xd)
	{
		super.afterXmlDataExtract(xd);
		IOCBox.injectToXmlData(xd, this);
	}
	
	public void afterXmlDataInject(XmlData xd)
	{
		super.afterXmlDataInject(xd);
		IOCBox.extractFromXmlData(xd, this);
	}
}
