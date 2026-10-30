package org.iottree.driver.zigbee.zspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.driver.zigbee.zcl.ZCLAttr;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.ZCLCmd;
import org.iottree.driver.zigbee.zcl.ZCLParam;
import org.iottree.driver.zigbee.zcl.cmd_frm.ReportAttr;
import org.iottree.driver.zigbee.zcl.util.AttrVal;
import org.iottree.driver.zigbee.zcl.util.DataType;
import org.iottree.driver.zigbee.zcl.util.DataVal;
import org.iottree.driver.zigbee.zcl.util.TagDef;
import org.json.JSONArray;
import org.json.JSONObject;

public class ZSCluster
{
	ZSEndPoint owner ;
	ZCLCluster clusterDef ;
	
	//运行过程中才会加入的数据
	HashMap<Integer,ZSAttrVal> attrId2Val = new HashMap<>() ;
	
	HashMap<Integer,ZSCmd> id2cmd = new HashMap<>() ;
	
	public ZSCluster(ZSEndPoint owner,ZCLCluster c_def)
	{
		this.clusterDef = c_def;//ZCLClusterManager.getInstance().getCluserByID(id) ;
//		if(this.clusterDef==null)
//			throw new IllegalArgumentException("no cluster def found with id="+id) ;
		this.owner = owner ;
		List<ZCLAttr> attrdefs = this.clusterDef.listTagDefAttributes() ;
		for(ZCLAttr attr:attrdefs)
		{
			ZSAttrVal av = new ZSAttrVal(this,attr) ;
			attrId2Val.put(av.getAttrId(),av) ;
		}
		for(ZCLCmd cmd:this.clusterDef.getIdCmds().values())
		{
			ZSCmd zsc = new ZSCmd(this,cmd) ;
			id2cmd.put(zsc.getCmdId(),zsc) ;
		}
	}
	
	public ZSEndPoint getOwner()
	{
		return this.owner ;
	}
	
	public int getClusterId()
	{
		return this.clusterDef.getID() ;
	}
	
	public ZCLCluster getClusterDef()
	{
		return this.clusterDef ;
	}
	
	public ZCLAttr getAttributeDef(int attr_id)
	{
		ZCLCluster c = getClusterDef() ;
		if(c==null)
			return null ;
		return c.getAttributeById(attr_id) ;
	}
	
	public ZSAttrVal getAttrVal(int attr_id)
	{
		ZSAttrVal av = this.attrId2Val.get(attr_id) ;
		if(av==null)
			return null ;
		
		List<UATag> tags = getRelatedTagByAttrId(attr_id) ;
		if(tags==null||tags.size()<=0)
			return null ;
		UAVal uav = tags.get(0).RT_getVal() ;
		if(uav==null||!uav.isValid())
			return null ;
		Object objv = uav.getObjVal() ;
		long updt = uav.getValDT() ;
		//DataVal dv = new DataVal(attrd.getDataType(),objv) ;
		av.RT_setObjVal(updt, objv);
		return av ;
	}
	
	private transient List<ZSAttrVal> attr_vals_list = null ;
	
	public List<ZSAttrVal> listAttrVals()
	{
		if(attr_vals_list!=null)
			return attr_vals_list;
		
		ArrayList<ZSAttrVal> rets = new ArrayList<>() ;
		rets.addAll(this.attrId2Val.values()) ;
		Collections.sort(rets);
		return attr_vals_list = rets ;
	}
	
	public ZSCmd getCmd(int cmd_id)
	{
		return this.id2cmd.get(cmd_id) ;
	}
	
	public String getTagAddrPrefix()
	{
		return "ep:"+owner.getEndPointId()+";cluster:"+this.getClusterId() ;
	}
	
	String calcTagAddr(int attr_id)
	{
		return this.getTagAddrPrefix()+";attr:"+attr_id ;
	}
	

	public List<UATag> getRelatedTagByAttrId(int attr_id)
	{
		String addr = this.calcTagAddr(attr_id);
		UADev dev = this.owner.getOwner().getRelatedUADev() ;
		if(dev==null)
			return null ;
		return dev.filterTagsAllByAddr(addr) ;
	}
	
	
	//for syn tags in UADev
	int setupTagsJArr(JSONArray ret)
	{
		ZCLCluster c = getClusterDef() ;
		if(c==null)
			return 0 ;
		int r = 0 ;
		for(ZCLAttr attrdef:c.getIdAttributes().values())
		{
			JSONObject tagjo = toTagJO(attrdef) ;
			if(tagjo==null)
				continue ;
			ret.put(tagjo) ;
			r ++ ;
		}
		
		for(ZCLCmd cmd:c.getIdCmds().values())
		{
			List<JSONObject> tagjos = toTagJO(cmd) ;
			if(tagjos==null || tagjos.size()<=0)
				continue ;
			for(JSONObject tagjo:tagjos)
				ret.put(tagjo) ;
			r +=tagjos.size() ;
		}
		
		return r;
	}
	

	private JSONObject toTagJO(ZCLAttr attr)
	{
		TagDef tag_def = attr.getTagDef() ;
		if(tag_def==null)
			return null ;
		
		JSONObject ret = tag_def.toTagJO() ;
		if(tag_def.getValTP()==null)
		{
			DataType tp = attr.getDataType() ;
			if(tp!=null)
				ret.putOpt("val_tp",tp.getUAValTP()) ;
		}
		String addr = this.getTagAddrPrefix()+";attr:"+attr.getID() ;
		ret.put("addr", addr) ;
		if(attr.isWrite())
			ret.put("write", true) ; //can write attr
		String n = ret.getString("name") ;
		if(!n.startsWith("attr_"))
			ret.put("name","attr_"+n) ;
		String t = ret.getString("title") ;
		if(!t.startsWith("Attr "))
			ret.put("title","Attr "+t) ;
		return ret ;
	}
	
	private List<JSONObject> toTagJO(ZCLCmd cmd)
	{
		TagDef tag_def = cmd.getTagDef() ;
		if(tag_def==null)
			return null ;
		
		ArrayList<JSONObject> ret = new ArrayList<>() ;
		
		JSONObject jo = tag_def.toTagJO() ;
		String addr = this.getTagAddrPrefix()+";cmd:"+cmd.getID() ;
		jo.put("addr", addr) ;
		jo.put("val_tp", "bool");
		jo.put("default", "false");
		String n = jo.getString("name") ;
		if(!n.startsWith("cmd_"))
			jo.put("name","cmd_"+n) ;
		
		String t = jo.getString("title") ;
		if(!t.startsWith("Cmd "))
			jo.put("title","Cmd "+t) ;
		ret.add(jo) ;
		
		List<ZCLParam> pms = cmd.getParemeters() ;
		if(pms!=null&&pms.size()>0)
		{
			for(ZCLParam pm:pms)
			{
				TagDef tjo = pm.getTagDef() ;
				if(tjo==null)
					continue ;
				JSONObject pm_jo = tjo.toTagJO(tag_def.getName()+"_") ;
				pm_jo.put("addr", addr+";pm:"+pm.getIdx()) ;
				
				n = pm_jo.getString("name") ;
				if(!n.startsWith("cmd_"))
					pm_jo.put("name","cmd_"+n) ;
				
				String tt = pm_jo.getString("title") ;
				if(!tt.startsWith("Cmd "))
					pm_jo.put("title","Cmd "+tt) ;
				
				ret.add(pm_jo) ;
			}
		}
		return ret ;
	}
	
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject() ;
		r.put("cluster_id",this.getClusterId()) ;
		JSONArray jarr = new JSONArray() ;
		r.put("attr_vals",jarr) ;
		for(ZSAttrVal zsav:this.attrId2Val.values())
		{
			jarr.put(zsav.toJO()) ;
		}
		
		jarr = new JSONArray() ;
		r.put("cmds",jarr) ;
		for(ZSCmd zscmd:this.id2cmd.values())
		{
			jarr.put(zscmd.toJO()) ;
		}
			
		return r ;
	}
	
	public static ZSCluster fromJO(ZSEndPoint ep,JSONObject jo)
	{
		int cid = jo.optInt("cluster_id", -1) ;
		if(cid<0)
			return null ;
		ZCLCluster c = ZCLClusterManager.getInstance().getCluserByID(cid) ;
		if(c==null)
		{
			System.out.println("no cluster found with id="+cid);
			return null ;
		}
		ZSCluster r = new ZSCluster(ep,c) ;
		JSONArray jarr = jo.optJSONArray("attr_vals") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i <  n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				ZSAttrVal av = ZSAttrVal.fromJO(r, tmpjo) ;
				if(av==null)
					continue ;
				r.attrId2Val.put(av.getAttrId(),av) ;
			}
		}
		return r ;
	}
	
	public ZSAttrVal RT_onUpdateAttrVal(AttrVal av)
	{
		int attrid = av.getAttrId() ;
		ZSAttrVal zav = this.attrId2Val.get(attrid) ;
		if(zav==null)
			return null ;
			
		zav.RT_onUpdate(av.getAttrObjVal()) ;
		return zav ;
	}
	
	public ZSAttrVal RT_onUpdateAttrVal(int attrid,DataVal dv)
	{
		ZSAttrVal zav = this.attrId2Val.get(attrid) ;
		if(zav==null)
			return null ;
		zav.RT_onUpdate(dv.getObjVal()) ;
		return zav ;
	}
	
	public List<ZSAttrVal> RT_onReportAttr(ReportAttr ra)
	{
		ArrayList<ZSAttrVal> rets = new ArrayList<>() ;
		List<AttrVal> avs = ra.getAttrVals() ;
		for(AttrVal av:avs)
		{
			ZSAttrVal zav = this.RT_onUpdateAttrVal(av);
			if(zav==null)
				continue ;
			rets.add(zav) ;
		}
		return rets ;
	}
}
