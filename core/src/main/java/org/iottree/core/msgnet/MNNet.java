package org.iottree.core.msgnet;


import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 一个具体的识别网络定义
 * 
 * @author zzj
 *
 */
public class MNNet
{
	static ILogger log = LoggerManager.getLogger(MNNet.class) ;
	
	static final String NET_DIR_PREFIX = "_net_" ;
	static final String NET_JO_FN = "_net_.json" ;
	
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	
	long updateDT = System.currentTimeMillis() ;
	
	// String tarId = null ;
	
	
	LinkedHashMap<String,MNNode> id2node = new LinkedHashMap<>() ;
	
	LinkedHashMap<String,MNModule> id2module = new LinkedHashMap<>() ;
	
//	static MNNet loadFromFile(String id,File f) throws Exception
//	{
//		if(!f.exists())
//			return null ;
//		
//		String txt = Convert.readFileTxt(f, "UTF-8") ;
//		JSONObject jo = new JSONObject(txt) ;
//		MNNet net = new MNNet(name) ;
//		net.updateDT = f.lastModified() ;
//		if(!net.fromJO(jo))
//			throw new Exception("parse json error") ;
//		return net ;
//	}
	
	
	
//	public static String calNameByDirName(String dir_name)
//	{
//		if(!dir_name.startsWith(NET_DIR_PREFIX))
//			return null ;
//		return dir_name.substring(NET_DIR_PREFIX.length()) ;
//	}
	
	MNManager belongTo = null ;
	UAPrj prj = null ;

	MNNet(MNManager mgr)
	{
		belongTo = mgr ;
		this.prj = mgr.getBelongTo() ;
		this.id = IdCreator.newSeqId() ;
	}
	
	/**
	 * 创建新的定义
	 * @param ref_id
	 * @param title
	 * @return
	 */
	MNNet(MNManager mgr,String name,String title,String desc)
	{
		belongTo = mgr ;
		this.prj = mgr.getBelongTo() ;
		
		this.id = IdCreator.newSeqId() ;
		
		StringBuilder failedr=  new StringBuilder() ;
		if(!Convert.checkVarName(name, true, failedr))
			throw new IllegalArgumentException(failedr.toString()) ;
		
		//this.belongTo = cat ;
		this.name = name ;//def.netId = IdCreator.newSeqId() ;
//		this.refId = ref_id ;
//		this.tarId = tar_id ;
		this.title = title ;
		this.desc = desc ;
	}
	
	
	public String getId()
	{
		return this.id ;
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
	
	public String getDesc()
	{
		if(this.desc==null)
			return "" ;
		return this.desc ;
	}

	
	
	public Date getUpdateDT()
	{
		return new Date(this.updateDT) ;
	}
	
//	public String getTarId()
//	{
//		return this.tarId ;
//	}
	
	public List<MNNodeStart> getStartNodes()
	{
		ArrayList<MNNodeStart> rets = new ArrayList<>() ;
		for(MNNode n:id2node.values())
		{
			//if(n.isStart())
			if(n instanceof MNNodeStart)
				rets.add((MNNodeStart)n) ;
		}
		return rets;
	}
	
	
	public MNNode getNodeById(String node_id)
	{
		return id2node.get(node_id) ;
	}
	
	public Map<String,MNNode> getNodeMapAll()
	{
		return this.id2node ;
	}
	
	
	public MNModule getModuleById(String mid)
	{
		return id2module.get(mid) ;
	}
	
	public Map<String,MNModule> getModuleMapAll()
	{
		return this.id2module ;
	}
	
	public MNBase getItemById(String id)
	{
		MNBase n = id2node.get(id) ;
		if(n!=null)
			return n;
		return id2module.get(id) ;
	}
//	/**
//	 * 获得网络内部的Collector节点
//	 * @return
//	 */
//	public MNNodeMid getNodeMids()
//	{
//		for(MNNode n:id2node.values())
//		{
//			if(!(n instanceof MNNodeMid))
//				continue ;
//			if(((MNNodeMid)n).isMultiInputCollector())
//				return (MNNodeMid)n;
//		}
//		return null ;
//	}


	static MNBase parseFromJO(MNNet net,JSONObject jo,boolean node_or_module,StringBuilder failedr) //throws Exception
	{
		String full_tp = jo.optString("_tp") ;
		if(Convert.isNullOrEmpty(full_tp))
		{
			failedr.append("no _tp found in jo") ;
			return null ;
		}
		MNBase n = null;
		if(node_or_module)
			n = MNManager.getNodeByFullTP(full_tp) ;
		else
			n = MNManager.getModuleByFullTP(full_tp) ;
		if(n==null)
		{
			failedr.append("unknown tp="+full_tp) ;
			return null ;
		}
		
		try
		{
			MNBase newn = (MNBase)n.createNewIns(net) ;
			
			if(!newn.fromJO(jo))
			{
				failedr.append("node create failed") ;
				return null ;
			}
			//newn.setNodeTP(n.getNodeTP(),n.getNodeTPTitle());
			return newn ;
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage()) ;
			return null ;
		}
	}
	
	public MNNode createNewNodeByFullTP(String full_tp,float x,float y,String module_id)  throws Exception
	{
		MNModule m = null ;
		if(Convert.isNotNullEmpty(module_id))
		{
			m = this.getModuleById(module_id) ;
			if(m==null)
				throw new MNException("no module found with id="+module_id) ;
		}
		
		//m.getSupportedNodeByTP(tp) ;
		MNNode n = MNManager.getNodeByFullTP(full_tp) ;
		if(n==null)
			return null ;
		MNModule tp_md = n.TP_getRelatedModule() ;
		if(tp_md!=null && m==null)
			throw new MNException("no related module input") ;
		else if(tp_md==null && m!=null)
			throw new MNException("node has no related module,but you set") ;
		MNNode new_n = n.createNewIns(this) ;
		
		new_n.x = x ;
		new_n.y = y ;
		String tpt = n.getTPTitle() ;
		//new_n.setNodeTP(n.getNodeTP(),tpt);
		new_n.title = tpt+" "+calcNextTitleSuffix(tpt) ;
		id2node.put(new_n.id, new_n) ;
		if(m!=null)
			m.nodeIdSet.add(new_n.id) ;
		
		save() ;
		return new_n ;
	}
	
	public MNModule createNewModuleByFullTP(String full_tp,float x,float y)  throws Exception
	{
		MNModule m = MNManager.getModuleByFullTP(full_tp) ;
		if(m==null)
		{
			return null ;
		}
		MNModule new_n = m.createNewIns(this) ;
		
		new_n.x = x ;
		new_n.y = y ;
		String tpt = m.getTPTitle() ;
		//new_n.setNodeTP(m.getNodeTP(),tpt);
		new_n.title = tpt+" "+calcNextTitleSuffix(tpt) ;
		id2module.put(new_n.id, new_n) ;
		
		save() ;
		return new_n ;
	}
	
	private int calcNextTitleSuffix(String tpt)
	{
		int len = tpt.length() ;
		int max_i = 0 ;
		for(MNNode n:this.id2node.values())
		{
			String tt = n.getTitle() ;
			if(!tt.startsWith(tpt))
				continue ;
			String ss = tt.substring(len).trim() ;
			try
			{
				int i = Integer.parseInt(ss) ;
				if(i>max_i)
					max_i = i ;
			}
			catch(Exception ee)
			{
				continue ;
			}
		}
		for(MNModule m:this.id2module.values())
		{
			String tt = m.getTitle() ;
			if(!tt.startsWith(tpt))
				continue ;
			String ss = tt.substring(len).trim() ;
			try
			{
				int i = Integer.parseInt(ss) ;
				if(i>max_i)
					max_i = i ;
			}
			catch(Exception ee)
			{
				continue ;
			}
		}
		return max_i+1 ;
	}
	
//	public MNNode createNewNodeByPaste(String node_uid,float x,float y)  throws Exception
//	{
//		MNNode sor_n = RNManager.getInstance().getNetNodeByUID(node_uid) ;
//		if(sor_n==null)
//			return null ;
//		
//		MNNode n = sor_n.copyMe() ;
//		n.belongTo = this ;
//		String nid = sor_n.getId() ;
//		MNNode oldn = this.getNodeById(nid) ;
//		if(oldn!=null)
//			nid = "n"+nextMaxSubId();
//		else
//		{
//			int nnn = Integer.parseInt(nid.substring(1)) ;
//			if(nnn>maxSubId)
//			maxSubId = nnn ;
//		}
//		n.id = nid ;
//		n.x = x ;
//		n.y = y ;
//		id2node.put(n.id, n) ;
//		
//		JSONObject jo = sor_n.getParamJO() ;
//		n.setParamJO(jo);
//		
//		save() ;
//		return n ;
//	}
	
	
	

	public MNConn getConnByUID(String uid)
	{
		int k = uid.indexOf('-') ;
		if(k<=0)
			return null ;
		String fstr = uid.substring(0,k) ;
		String tid = uid.substring(k+1) ;
		k = fstr.lastIndexOf('_') ;
		if(k<=0)
			return null ;
		String fid = fstr.substring(0,k) ;
		int idx = Integer.parseInt(fstr.substring(k+1)) ;
		return getConn(fid,idx,tid) ;
	}
	
	public MNConn getConn(String from_nid,int from_idx,String to_nid)
	{
		MNNode fromn = this.getNodeById(from_nid);
		if(fromn==null)
			return null ;
		return fromn.getOutConn(from_idx, to_nid) ;
	}
	
	public MNConn addConn(String from_nid,int from_idx,String to_nid) throws Exception
	{
		MNNode fromn = this.getNodeById(from_nid);
		if(fromn==null)
			return null ;
		MNConn ret = fromn.setOutConn(from_idx, to_nid) ;
		if(ret!=null)
			this.save();
		return ret ;
	}
	
	public MNConn addConn(String out_id,String to_nid,StringBuilder failedr) throws IOException
	{
		int k = out_id.lastIndexOf('_') ;
		if(k<=0)
			throw new IllegalArgumentException("invalid out_id") ;
		String from_nid = out_id.substring(0,k) ;
		int from_idx = Integer.parseInt(out_id.substring(k+1)) ;
		MNNode fromn = this.getNodeById(from_nid);
		if(fromn==null)
			return null ;
		try
		{
			MNConn ret = fromn.setOutConn(from_idx, to_nid) ;
			if(ret!=null)
				this.save();
			return ret ;
		}
		catch(MNException mne)
		{
			if(log.isDebugEnabled())
				log.debug(mne.getMessage(), mne);
			failedr.append(mne.getMessage()) ;
			return null ;
		}
	}
	
	public MNBase delNodeModuleByID(String id,boolean bsave) throws Exception
	{
		MNNode n = this.getNodeById(id) ;
		MNModule m = this.getModuleById(id) ;
		if(m==null&&n==null)
			return null ;
		MNBase ret = null ;
		if(m!=null)
		{
			Set<String> nids = m.getRelatedNodeIdSet() ;
			for(String nid:nids)
			{
				this.id2node.remove(nid) ;
			}
			this.id2module.remove(id) ;
			ret = m ;
		}
		if(n!=null)
		{
			this.id2node.remove(id) ;
			ret = n ;
		}
		
		if(bsave)
			this.save();
		return ret;
	}
	
	public MNConn delConnByUID(String conn_uid,boolean bsave)
		throws Exception
	{
		MNConn c = this.getConnByUID(conn_uid) ;
		if(c==null)
			return null ;
		
		MNNode fromn = c.getFromBelongToNode() ;
		c = fromn.unsetOutConn(c.getOutIdx(), c.getToNode().getId()) ;
		if(bsave && c!=null)
			this.save();
		return c;
	}
	
	public int delItemsByIds(List<String> uids) throws Exception
	{
		if(uids==null||uids.size()<=0)
			return 0 ;
		int r = 0 ;
		for(String uid:uids)
		{
			int k = uid.indexOf('-');
			if(k>0)
			{
				MNConn c = delConnByUID(uid,false) ;
				if(c!=null)
					r++;
			}
			else
			{
				MNBase n = delNodeModuleByID(uid,false) ;
				if(n!=null)
					r++;
			}
		}
		
		this.cleanConns();
		
		if(r>0)
			this.save();
		return r ;
	}
	
	public boolean cleanConns()
	{
		boolean ret = false;
		for(MNNode n:this.id2node.values())
		{
			if(n.cleanOutConns())
				ret = true ;
		}
		return ret ;
	}
	
	public List<MNNode> delNodeByIds(List<String> node_ids) throws Exception
	{
		ArrayList<MNNode> rets = new ArrayList<>() ;
		for(String id:node_ids)
		{
			MNNode n = delNodeById(id,false);
			if(n!=null)
				rets.add(n) ;
		}
		if(rets.size()>0)
		{
			cleanConns() ;
			this.save();
		}
		return rets ;
	}
	
	public MNNode delNodeById(String node_id,boolean bsave) throws Exception
	{
		MNNode n = this.getNodeById(node_id);
		if(n==null)
			return null ;
		
		this.id2node.remove(node_id) ;
		
		if(bsave)
		{
			cleanConns() ;
			this.save();
		}
		return n ;
	}
	
	public MNNode setNodeParamJO(String node_id,JSONObject pmjo,boolean bsave) throws IOException
	{
		MNNode n = this.getNodeById(node_id);
		if(n==null)
			return null ;
		n.setParamJO(pmjo);
		if(bsave)
			this.save();
		return n ;
	}
	
	public MNBase setDetailJO(String node_id,JSONObject detail_jo,boolean node_or_module,boolean bsave) throws IOException
	{
		MNBase n = null ;
		if(node_or_module)
			n = this.getNodeById(node_id);
		else
			n = this.getModuleById(node_id);
		if(n==null)
			return null ;
		n.setDetailJO(detail_jo);
		if(bsave)
			this.save();
		return n ;
	}
	
	/**
	 * 根据前端返回的基本（布局）信息，对自身进行更新并保存
	 * @param jo
	 * @return
	 */
	public boolean updateBasicByJO(JSONObject jo,StringBuilder failedr) throws Exception
	{
		JSONArray jarr = jo.optJSONArray("nodes") ;
		if(jarr!=null)
		{
			for(int i = 0 ; i < jarr.length() ; i ++)
			{
				JSONObject n_jo = jarr.getJSONObject(i) ;
				String id = n_jo.optString("id") ;
				if(Convert.isNullOrEmpty(id))
					continue ;
				MNNode rnn = this.getNodeById(id);
				if(rnn==null)
					continue ;
				if(!rnn.fromJOBasic(n_jo,failedr))
					return  false;
			}
		}
		jarr = jo.optJSONArray("modules") ;
		if(jarr!=null)
		{
			for(int i = 0 ; i < jarr.length() ; i ++)
			{
				JSONObject n_jo = jarr.getJSONObject(i) ;
				String id = n_jo.optString("id") ;
				if(Convert.isNullOrEmpty(id))
					continue ;
				MNModule rnn = this.getModuleById(id);
				if(rnn==null)
					continue ;
				if(!rnn.fromJOBasic(n_jo,failedr))
					return  false;
			}
		}

		this.save();
		return true ;
	}
	
	void save() throws IOException
	{
		this.belongTo.saveNet(this);
	}
	
	public boolean renderOut(Writer out) throws IOException
	{
		JSONObject jo = this.toJO() ;
		jo.write(out) ;
		return true ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("name", this.name) ;
		jo.putOpt("id", this.getId()) ;
		jo.putOpt("title", this.title) ;
		return jo ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("name", this.name) ;
		jo.putOpt("id", this.getId()) ;
		//jo.putOpt("tar_id", this.tarId) ;
		jo.putOpt("title", this.title) ;
		jo.putOpt("desc", this.desc) ;
		//jo.put("up_dt", updateDT.getTime()) ;
		JSONArray jarr = new JSONArray() ;
		for(MNNode n:id2node.values())
		{
			jarr.put(n.toJO()) ;
		}
		jo.put("nodes", jarr) ;
		jarr = new JSONArray() ;
		for(MNModule n:id2module.values())
		{
			jarr.put(n.toJO()) ;
		}
		jo.put("modules", jarr) ;
		return jo ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.id = jo.getString("id") ;
		this.name = jo.getString("name") ;
		//this.tarId = jo.optString("tar_id") ;
		this.title = jo.optString("title",this.title) ;
		this.desc= jo.optString("desc",this.desc) ;
		//long updt = jo.optLong("up_dt",System.currentTimeMillis()) ;
		//updateDT = new Date(updt) ;
		
		JSONArray jarr = jo.optJSONArray("nodes") ;
		if(jarr!=null)
		{
			int len = jarr.length() ;
			for(int i = 0 ; i < len ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				StringBuilder failedr = new StringBuilder() ;
				MNNode n = (MNNode)parseFromJO(this,tmpjo,true,failedr) ;
				if(n==null)
				{
					log.warn(failedr.toString() +" in msg net node "+this.title+"-"+this.id);
					continue ;
				}
				id2node.put(n.getId(), n) ;
			}
		}
		
		jarr = jo.optJSONArray("modules") ;
		if(jarr!=null)
		{
			int len = jarr.length() ;
			for(int i = 0 ; i < len ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				StringBuilder failedr = new StringBuilder() ;
				MNModule n = (MNModule)parseFromJO(this,tmpjo,false,failedr) ;
				if(n==null)
				{
					log.warn(failedr.toString() +" in msg net module "+this.title+"-"+this.id);
					continue ;
				}
				id2module.put(n.getId(), n) ;
			}
		}
		
		return true ;
	}
	
	
	// - 
	

	/**
	 * call by outer to run net in project
	 * @param node_id
	 * @param failedr
	 * @return
	 */
	public boolean RT_triggerNodeStart(String node_id,StringBuilder failedr)
	{
		MNNode n = this.getNodeById(node_id) ;
		if(n==null)
		{
			failedr.append("no node found with id="+node_id) ;
			return false;
		}
		if(!(n instanceof MNNodeStart))
		{
			failedr.append("no start node") ;
			return false;
		}
		
		if(!(n instanceof IMNOnOff))
		{
			failedr.append("no OnOff node") ;
			return false;
		}
		IMNOnOff nstart = (IMNOnOff)n;
		//return RT_triggerNodeStart(nstart,null,failedr) ;
		return nstart.RT_triggerByOnOff(failedr) ;
	}
	
}
