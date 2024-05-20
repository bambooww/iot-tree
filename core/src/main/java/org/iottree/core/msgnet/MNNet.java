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


	static MNNode createFromJO(MNNet net,JSONObject jo,StringBuilder failedr) //throws Exception
	{
		String tp = jo.getString("_tp") ;
		MNNode n = MNManager.getNodeByTP(tp) ;
		if(n==null)
		{
			failedr.append("unknown tp="+tp) ;
			return null ;
		}
		try
		{
			n = (MNNode)n.getClass().getConstructor().newInstance() ;
			n.belongTo = net ;
			if(!n.fromJO(jo))
				failedr.append("node create failed") ;
			return n ;
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage()) ;
			return null ;
		}
	}
	
	public MNNode createNewNodeByTP(String tp,float x,float y)  throws Exception
	{
		MNNode n = MNManager.getNodeByTP(tp) ;
		if(n==null)
			return null ;
		n = (MNNode)n.getClass().getConstructor().newInstance() ;
		n.belongTo = this ;
		//n.id = "n"+nextMaxSubId();
		n.x = x ;
		n.y = y ;
		id2node.put(n.id, n) ;
		
		save() ;
		return n ;
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
	
	public MNNode delNodeByID(String node_id,boolean bsave) throws Exception
	{
		MNNode n = this.getNodeById(node_id) ;
		if(n==null)
			return null ;
		this.id2node.remove(node_id) ;
		if(bsave)
			this.save();
		return n;
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
				MNNode n = delNodeByID(uid,false) ;
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
	
	public MNNode setNodeParamJO(String node_id,JSONObject pmjo,boolean bsave) throws Exception
	{
		MNNode n = this.getNodeById(node_id);
		if(n==null)
			return null ;
		n.setParamJO(pmjo);
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
				MNNode n = createFromJO(this,tmpjo,failedr) ;
				if(n==null)
					continue ;
				id2node.put(n.getId(), n) ;
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
		MNNodeStart nstart = (MNNodeStart)n;
		//return RT_triggerNodeStart(nstart,null,failedr) ;
		return nstart.RT_trigger(null,failedr) ;
	}
	
}
