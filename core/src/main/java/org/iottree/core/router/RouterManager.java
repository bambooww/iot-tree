package org.iottree.core.router;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.alert.AlertHandler;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.json.JSONArray;
import org.json.JSONObject;

public class RouterManager
{
	private static HashMap<String,RouterManager> prjid2mgr = new HashMap<>() ;
	
	public static RouterManager getInstance(UAPrj prj)
	{
		RouterManager instance = prjid2mgr.get(prj.getId()) ;
		if(instance!=null)
			return instance ;
		
		synchronized(RouterManager.class)
		{
			instance = prjid2mgr.get(prj.getId()) ;
			if(instance!=null)
				return instance ;
			
			instance = new RouterManager(prj) ;
			prjid2mgr.put(prj.getId(),instance) ;
			return instance ;
		}
	}
	
	UAPrj belongTo = null ;
	
//	ArrayList<RouterInnerCollator> sysInns = new ArrayList<>() ;
	
	LinkedHashMap<String,RouterInnCollator> id2inns = null;// new ArrayList<>() ;
	
	LinkedHashMap<String,RouterOuterAdp> id2outers = null ;
	
	LinkedHashMap<String,JoinConn> ric2roaConns = null;// new LinkedHashMap<>() ;
	
	LinkedHashMap<String,JoinConn> roa2ricConns = null;//new LinkedHashMap<>() ;
	
	private RouterManager(UAPrj prj)
	{
		this.belongTo = prj ;
		
//		sysInns.add(new RICDef(this)) ;
//		sysInns.add(new RICRunTime(this)) ;
	}
	
//	public List<RouterInnerCollator> getSysInnerCollators()
//	{
//		return sysInns;
//	}
	
	public List<RouterInnCollator> getInnerCollators()
	{
		ArrayList<RouterInnCollator> rets  = new ArrayList<>() ;
		rets.addAll(getInnerCollatorsMap().values()) ;
		return rets ;
	}
	
	public LinkedHashMap<String,RouterInnCollator> getInnerCollatorsMap()
	{
		if(id2inns!=null)
			return id2inns;
		
		synchronized(this)
		{
			if(id2inns!=null)
				return id2inns;
			
			try
			{
				id2inns = loadInnerCollators() ;
				return id2inns ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	private File getInnerCollatorsFile()
	{
		return new File(this.belongTo.getPrjSubDir(),"router_inn_collators.json") ;
	}
	
	void saveInnerCollators() throws Exception
	{
		JSONArray jarr = new JSONArray() ;
		for(RouterInnCollator dp:getInnerCollatorsMap().values())
		{
			JSONObject jo = dp.toJO() ;
			jarr.put(jo) ;
		}
		
		Convert.writeFileTxt(getInnerCollatorsFile(), jarr.toString(), "UTF-8");
	}
	
	private LinkedHashMap<String,RouterInnCollator> loadInnerCollators() throws Exception
	{
		LinkedHashMap<String,RouterInnCollator> dps = new LinkedHashMap<>() ;
		File f = this.getInnerCollatorsFile() ;
		if(!f.exists())
			return dps ;
		String txt = Convert.readFileTxt(f, "UTF-8") ;
		JSONArray jarr = new JSONArray(txt) ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			StringBuilder failedr = new StringBuilder() ;
			RouterInnCollator dp = RouterInnCollator.transFromJO(this,jo,failedr) ;
			if(dp==null)
			{
				System.out.println(" Warn: loadInnerCollators failed="+failedr) ;
				continue ;
			}
			dps.put(dp.id,dp) ;
		}
		return dps ;
	}
	
	public RouterInnCollator getInnerCollatorById(String id)
	{
		RouterInnCollator ret = this.getInnerCollatorsMap().get(id) ;
		return ret;
//		if(ret!=null)
//			return ret ;
//		for(RouterInnerCollator ric:this.sysInns)
//		{
//			if(ric.id.equals(id))
//				return ric ;
//		}
//		return null ;
	}
	
	public RouterInnCollator getInnerCollatorByName(String name)
	{
//		if(name.startsWith("_"))
//		{
//			for(RouterInnerCollator ric:this.getSysInnerCollators())
//			{
//				if(ric.getName().equals(name))
//					return ric ;
//			}
//			return null ;
//		}
		
		for(RouterInnCollator ric:this.getInnerCollatorsMap().values())
		{
			if(ric.getName().equals(name))
				return ric ;
		}
		return null ;
	}
	
	public void setInnerCollator(RouterInnCollator ah) throws Exception
	{
		String n = ah.getName() ;
		if(Convert.isNullOrEmpty(n) || n.startsWith("_"))
			throw new IllegalArgumentException("RouterInnerCollator name cannot be null or empty or start with _") ;
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(n, true, sb))
			throw new IllegalArgumentException(sb.toString()) ;
		
		RouterInnCollator old_ah = this.getInnerCollatorByName(n) ;
		if(old_ah!=null)
		{
			if(!old_ah.getId().equals(ah.getId()))
				throw new IllegalArgumentException("RouterInnerCollator with name="+n+" is already existed!") ;
		}
		this.id2inns.put(ah.getId(), ah) ;
		this.saveInnerCollators();
	}
	

	public void setInnerCollatorByJSON(JSONObject jo) throws Exception
	{
		String tp = jo.getString("_tp");
		if(Convert.isNullOrEmpty(tp))
			throw new Exception("no _tp in json") ;
		RouterInnCollator ric = RouterInnCollator.newInstanceByTp(this, tp) ;
		if(ric==null)
			throw new Exception("unknown tp="+tp) ;

		StringBuilder failedr = new StringBuilder() ;
		if(!ric.fromJO(jo, failedr))
			throw new Exception(failedr.toString()) ;
		
		if(Convert.isNullOrEmpty(ric.id))
			ric.id = IdCreator.newSeqId() ;
		this.setInnerCollator(ric);
	}
	
	public boolean delInnerCollatorById(String id) throws Exception
	{	
		RouterInnCollator ao = this.id2inns.remove(id) ;
		if(ao==null)
			return false;
		this.saveInnerCollators();
		return true ;
	}
	
	//  --  outer
	
	
	
	public RouterOuterAdp newOuterAdp(String tp)
	{
		RouterOuterAdp adp = RouterOuterAdpCat.getAdpByTP(tp) ;
		if(adp==null)
			return null ;
		return adp.newInstance(this) ;
	}
	
	public List<RouterOuterAdp> getOuterAdps()
	{
		ArrayList<RouterOuterAdp> rets = new ArrayList<>() ;
		rets.addAll(getOuterAdpsMap().values()) ;
		return rets ;
	}
	
	public LinkedHashMap<String,RouterOuterAdp> getOuterAdpsMap()
	{
		if(id2outers!=null)
			return id2outers;
		
		synchronized(this)
		{
			if(id2outers!=null)
				return id2outers;
			
			try
			{
				id2outers = loadOuterAdps() ;
				return id2outers ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	private File getOuterAdpsFile()
	{
		return new File(this.belongTo.getPrjSubDir(),"router_out_adps.json") ;
	}
	
	void saveOuterAdps() throws Exception
	{
		JSONArray jarr = new JSONArray() ;
		for(RouterOuterAdp dp:getOuterAdpsMap().values())
		{
			JSONObject jo = dp.toJO() ;
			jarr.put(jo) ;
		}
		
		Convert.writeFileTxt(getOuterAdpsFile(), jarr.toString(), "UTF-8");
	}
	
	private LinkedHashMap<String,RouterOuterAdp> loadOuterAdps() throws Exception
	{
		LinkedHashMap<String,RouterOuterAdp> dps = new LinkedHashMap<>() ;
		File f = this.getOuterAdpsFile() ;
		if(!f.exists())
			return dps ;
		String txt = Convert.readFileTxt(f, "UTF-8") ;
		JSONArray jarr = new JSONArray(txt) ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			StringBuilder failedr = new StringBuilder() ;
			RouterOuterAdp dp = RouterOuterAdp.transFromJO(this,jo,failedr) ;
			if(dp==null)
			{
				System.out.println(" Warn: loadOuters failed="+failedr) ;
				continue ;
			}
			dps.put(dp.id,dp) ;
		}
		return dps ;
	}
	
	public RouterOuterAdp getOuterAdpById(String id)
	{
		return getOuterAdpsMap().get(id) ;
	}
	
	public RouterOuterAdp getOuterAdpByName(String name)
	{
		for(RouterOuterAdp ric:getOuterAdpsMap().values())
		{
			if(ric.getName().equals(name))
				return ric ;
		}
		return null ;
	}
	
	public void setOuterAdp(RouterOuterAdp ah) throws Exception
	{
		String n = ah.getName() ;
		if(Convert.isNullOrEmpty(n) || n.startsWith("_"))
			throw new IllegalArgumentException("RouterOuterAdp name cannot be null or empty or start with _") ;
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(n, true, sb))
			throw new IllegalArgumentException(sb.toString()) ;
		
		RouterOuterAdp old_ah = this.getOuterAdpByName(n) ;
		if(old_ah!=null)
		{
			if(!old_ah.getId().equals(ah.getId()))
				throw new IllegalArgumentException("RouterOuterAdp with name="+n+" is already existed!") ;
		}
		this.id2outers.put(ah.getId(), ah) ;
		this.saveOuterAdps();
	}
	

	public void setOuterAdpByJSON(JSONObject jo) throws Exception
	{
		String tp = jo.getString("_tp");
		if(Convert.isNullOrEmpty(tp))
			throw new Exception("no _tp in json") ;
		StringBuilder failedr = new StringBuilder() ;
		RouterOuterAdp adp = RouterOuterAdp.transFromJO(this,jo,failedr) ;
		if(adp==null)
			throw new Exception(failedr.toString()) ;
		
		if(Convert.isNullOrEmpty(adp.id))
			adp.id = IdCreator.newSeqId() ;
		this.setOuterAdp(adp);
	}
	
	public boolean delOuterAdpById(String id) throws Exception
	{	
		RouterOuterAdp ao = this.id2outers.remove(id) ;
		if(ao==null)
			return false;
		this.saveOuterAdps();
		return true ;
	}
	
	// --  conn
	
	public LinkedHashMap<String,JoinConn> CONN_getRIC2ROAMap()
	{
		if(this.ric2roaConns!=null)
			return this.ric2roaConns ;
		
		synchronized(this)
		{
			if(this.ric2roaConns!=null)
				return this.ric2roaConns ;
			
			try
			{
				CONN_load() ;
				return this.ric2roaConns ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	public LinkedHashMap<String,JoinConn> CONN_getROA2RICMap()
	{
		if(this.roa2ricConns!=null)
			return this.roa2ricConns ;
		
		synchronized(this)
		{
			if(this.roa2ricConns!=null)
				return this.roa2ricConns ;
		
			try
			{
				CONN_load() ;
				
				return this.roa2ricConns ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null ;
			}
		}
	}
	
	private File getJoinConnFile()
	{
		return new File(this.belongTo.getPrjSubDir(),"router_conns.json") ;
	}
	
	private void CONN_save() throws Exception
	{
		JSONObject jo = new JSONObject() ;
		
		JSONArray jarr = new JSONArray() ;
		for(JoinConn jc : CONN_getRIC2ROAMap().values())
			jarr.put(jc.toJO()) ;
		jo.put("ric2roa", jarr) ;
		
		jarr = new JSONArray() ;
		for(JoinConn jc : CONN_getROA2RICMap().values())
			jarr.put(jc.toJO()) ;
		jo.put("roa2ric", jarr);
		
		Convert.writeFileTxt(getJoinConnFile(), jo.toString(), "UTF-8");
	}
	
	private boolean CONN_RIC_chkValid(JoinConn jc)
	{
		String fid = jc.fromId ;
		if(Convert.isNullOrEmpty(fid))
			return false;
		JoinOut jo = CONN_RIC_getJoinOutByFromId(fid) ;
		if(jo==null)
			return false;
		String tid = jc.toId ;
		if(Convert.isNullOrEmpty(tid))
			return false;
		JoinIn ji = CONN_ROA_getJoinInByToId(tid) ;
		if(ji==null)
			return false;
		return true ;
	}
	
	private boolean CONN_ROA_chkValid(JoinConn jc)
	{
		String fid = jc.fromId ;
		if(Convert.isNullOrEmpty(fid))
			return false;
		JoinOut jo = CONN_ROA_getJoinOutByFromId(fid) ;
		if(jo==null)
			return false;
		String tid = jc.toId ;
		if(Convert.isNullOrEmpty(tid))
			return false;
		JoinIn ji = CONN_RIC_getJoinInByToId(tid) ;
		if(ji==null)
			return false;
		return true ;
	}
	
	private void CONN_load() throws IOException
	{
		LinkedHashMap<String,JoinConn> ric2roa = new LinkedHashMap<>() ;
		LinkedHashMap<String,JoinConn> roa2ric = new LinkedHashMap<>() ;
		File f = this.getJoinConnFile() ;
		if(!f.exists())
		{
			ric2roaConns = ric2roa ;
			roa2ricConns = roa2ric ;
			return ;
		}
		String txt = Convert.readFileTxt(f, "UTF-8") ;
		JSONObject tjo = new JSONObject(txt) ;
		JSONArray jarr = tjo.optJSONArray("ric2roa") ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			JoinConn jc = JoinConn.RIC_fromJO(this, jo) ;
			if(jc==null)
				continue ;
			ric2roa.put(jc.getKey(),jc) ;
		}
		
		jarr = tjo.optJSONArray("roa2ric") ;
		n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			JoinConn jc = JoinConn.ROA_fromJO(this, jo) ;
			if(jc==null)
				continue ;
			roa2ric.put(jc.getKey(),jc) ;
		}
		
		ric2roaConns = ric2roa ;
		roa2ricConns = roa2ric ;
		return ;
	}
	
	
	public JoinOut CONN_RIC_getJoinOutByFromId(String fromid)
	{
		int k = fromid.indexOf('-') ;
		if(k<=0)
			throw new IllegalArgumentException("invalid fromid") ;
		String nodeid = fromid.substring(0,k) ;
		String j_name = fromid.substring(k+1) ;
		
		RouterInnCollator ric = getInnerCollatorById(nodeid) ;
		if(ric==null)
			return null ;
		return ric.getJoinOutByName(j_name) ;
	}
	
	public JoinIn CONN_RIC_getJoinInByToId(String toid)
	{
		int k = toid.indexOf('-') ;
		if(k<=0)
			throw new IllegalArgumentException("invalid fromid") ;
		String nodeid = toid.substring(0,k) ;
		String j_name = toid.substring(k+1) ;
		
		RouterInnCollator ric = getInnerCollatorById(nodeid) ;
		if(ric==null)
			return null ;
		return ric.getJoinInByName(j_name) ;
	}
	
	public JoinOut CONN_ROA_getJoinOutByFromId(String fromid)
	{
		int k = fromid.indexOf('-') ;
		if(k<=0)
			throw new IllegalArgumentException("invalid fromid") ;
		String nodeid = fromid.substring(0,k) ;
		String j_name = fromid.substring(k+1) ;
		
		RouterOuterAdp roa = this.getOuterAdpById(nodeid) ;
		if(roa==null)
			return null ;
		return roa.getJoinOutByName(j_name) ;
	}
	
	public JoinIn CONN_ROA_getJoinInByToId(String toid)
	{
		int k = toid.indexOf('-') ;
		if(k<=0)
			throw new IllegalArgumentException("invalid fromid") ;
		String nodeid = toid.substring(0,k) ;
		String j_name = toid.substring(k+1) ;
		
		RouterOuterAdp roa = this.getOuterAdpById(nodeid) ;
		if(roa==null)
			return null ;
		return roa.getJoinInByName(j_name) ;
	}
	
	public JoinConn CONN_RIC_setConn2ROA(String fromid,String toid,StringBuilder failedr) throws Exception
	{
		JoinOut jo = CONN_RIC_getJoinOutByFromId(fromid) ;
		if(jo==null)
		{
			failedr.append("no ric join out found with fromid="+fromid) ;
			return null;
		}
		
		JoinIn ji = CONN_ROA_getJoinInByToId(toid) ;
		if(ji==null)
		{
			failedr.append("no roa join in found with toid="+toid) ;
			return null;
		}
		
		JoinConn jc = new JoinConn(this,jo,ji,fromid,toid) ;
		JoinConn oldjc = this.ric2roaConns.get(jc.getKey()) ;
		if(oldjc!=null)
			return oldjc ;
		
		this.ric2roaConns.put(jc.getKey(), jc) ;
		this.CONN_save();
		return jc ;
	}
	
	public JoinConn CONN_RIC_getConn(String fromid,String toid)
	{
		String key = JoinConn.calKey(fromid, toid) ;
		return this.ric2roaConns.get(key) ;
	}
	
	public JoinConn CONN_RIC_delConn(String fromid,String toid) throws Exception
	{
		String key = JoinConn.calKey(fromid,toid) ;
		JoinConn oldjc = this.ric2roaConns.remove(key) ;
		if(oldjc==null)
			return null ;

		this.CONN_save();
		return oldjc ;
	}
	
	public JoinConn CONN_RIC_setConnJS(String fromid,String toid,boolean js_en,String jstxt) throws Exception
	{
		JoinConn jc = CONN_RIC_getConn(fromid,toid) ;
		if(jc==null)
			return null ;
		if(Convert.isNullOrTrimEmpty(jstxt))
			jstxt = null ;
		jc.transJS = jstxt ;
		jc.bEnJS = js_en ;
		this.CONN_save();
		return jc ;
	}
	
	public JoinConn CONN_ROA_setConn2RIC(String fromid,String toid,StringBuilder failedr) throws Exception
	{
		JoinOut jo = CONN_ROA_getJoinOutByFromId(fromid) ;
		if(jo==null)
		{
			failedr.append("no roa join out found with fromid="+fromid) ;
			return null;
		}
		
		JoinIn ji = CONN_RIC_getJoinInByToId(toid) ;
		if(ji==null)
		{
			failedr.append("no ric join in found with toid="+toid) ;
			return null;
		}
		
		JoinConn jc = new JoinConn(this,jo,ji,fromid,toid) ;
		JoinConn oldjc = this.roa2ricConns.get(jc.getKey()) ;
		if(oldjc!=null)
			return oldjc ;
		
		this.roa2ricConns.put(jc.getKey(), jc) ;
		this.CONN_save();
		return jc ;
	}
	
	public JoinConn CONN_ROA_getConn(String fromid,String toid)
	{
		String key = JoinConn.calKey(fromid, toid) ;
		return this.roa2ricConns.get(key) ;
	}

	public JoinConn CONN_ROA_delConn(String fromid,String toid) throws Exception
	{
		String key = JoinConn.calKey(fromid,toid) ;
		JoinConn oldjc = this.roa2ricConns.remove(key) ;
		if(oldjc==null)
			return null ;

		this.CONN_save();
		return oldjc ;
	}
	
	public JoinConn CONN_ROA_setConnJS(String fromid,String toid,boolean js_en,String jstxt) throws Exception
	{
		JoinConn jc = CONN_ROA_getConn(fromid,toid) ;
		if(jc==null)
			return null ;
		if(Convert.isNullOrTrimEmpty(jstxt))
			jstxt = null ;
		jc.bEnJS = js_en ;
		jc.transJS = jstxt ;
		this.CONN_save();
		return jc ;
	}
	//
	
	public JSONArray UTIL_RIC_toJarr()
	{
		JSONArray jarr = new JSONArray() ;
//		for(RouterInnerCollator ao:getSysInnerCollators())
//		{
//			JSONObject tmpjo = ao.toListJO() ;
//			jarr.put(tmpjo) ;
//		}
		for(RouterInnCollator ao:getInnerCollators())
		{
			JSONObject tmpjo = ao.toListJO() ;
			jarr.put(tmpjo) ;
		}
		return jarr ;
	}
	
	public JSONArray UTIL_CONN_RIC_toJarr()
	{
		JSONArray jarr = new JSONArray() ;
		
		for(JoinConn ao:this.CONN_getRIC2ROAMap().values())
		{
			JSONObject tmpjo = ao.toListJO() ;
			jarr.put(tmpjo) ;
		}
		return jarr ;
	}
	
	public JSONArray UTIL_ROA_toJarr()
	{
		JSONArray jarr = new JSONArray() ;
		for(RouterOuterAdp ao:getOuterAdpsMap().values())
		{
			JSONObject tmpjo = ao.toListJO() ;
			jarr.put(tmpjo) ;
		}
		return jarr ;
	}
	
	public JSONArray UTIL_CONN_ROA_toJarr()
	{
		JSONArray jarr = new JSONArray() ;
		
		for(JoinConn ao:this.CONN_getROA2RICMap().values())
		{
			JSONObject tmpjo = ao.toListJO() ;
			jarr.put(tmpjo) ;
		}
		return jarr ;
	}
	
	public JSONObject UTIL_toJOFull()
	{
		JSONObject jo = new JSONObject() ;
		JSONArray jarr = UTIL_RIC_toJarr() ;
		jo.put("ric_list", jarr) ;
		
		jarr = UTIL_ROA_toJarr() ;
		jo.put("roa_list", jarr) ;
		
		jarr =  UTIL_CONN_RIC_toJarr() ;
		jo.put("ric_conns", jarr) ;
		
		jarr =  UTIL_CONN_ROA_toJarr() ;
		jo.put("roa_conns", jarr) ;
		
		return jo ;
	}
	
	// rt ---------
	
	public void RT_start()
	{
		for(RouterOuterAdp roa:this.getOuterAdpsMap().values())
		{
			if(!roa.isEnable())
				continue ;
			
			roa.RT_start() ;
		}
		
		for(RouterInnCollator ric:this.getInnerCollatorsMap().values())
		{
			if(!ric.isEnable())
				continue ;
			
			ric.RT_start() ;
		}
	}
	
	public void RT_stop()
	{
		for(RouterOuterAdp roa:this.getOuterAdpsMap().values())
		{
			roa.RT_stop() ;
		}
		
		for(RouterInnCollator ric:this.getInnerCollatorsMap().values())
		{
			ric.RT_stop();
		}
	}
	
	public JSONObject RT_getRunInf()
	{
		JSONObject jo = new JSONObject() ;
		JSONArray jarr = new JSONArray() ;
		for(RouterInnCollator ric : this.getInnerCollatorsMap().values())
		{
			JSONObject tmpjo = ric.RT_getRunInf() ;
			jarr.put(tmpjo) ;
		}
		jo.put("rics", jarr) ;
		
		jarr = new JSONArray() ;
		for(RouterOuterAdp roa : this.getOuterAdpsMap().values())
		{
			JSONObject tmpjo = roa.RT_getRunInf() ;
			jarr.put(tmpjo) ;
		}
		jo.put("roas", jarr) ;
		
		jarr = new JSONArray() ;
		for(JoinConn jc:this.CONN_getRIC2ROAMap().values())
		{
			JSONObject tmpjo = jc.RT_getRunInf() ;
			jarr.put(tmpjo) ;
		}
		jo.put("ric2roa_conns", jarr) ;
		
		jarr = new JSONArray() ;
		for(JoinConn jc:this.CONN_getROA2RICMap().values())
		{
			JSONObject tmpjo = jc.RT_getRunInf() ;
			jarr.put(tmpjo) ;
		}
		jo.put("roa2ric_conns", jarr) ;
		return jo ;
	}
}
