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

import javax.script.ScriptException;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.cxt.MNContext;
import org.iottree.core.msgnet.nodes.NS_OnFlowEvt;
import org.iottree.core.msgnet.nodes.NS_OuterTrigger;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.Lan;
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
public class MNNet extends MNCxtPk implements ILang,IMNRunner
{
	static ILogger log = LoggerManager.getLogger(MNNet.class) ;
	
	static final String NET_DIR_PREFIX = "_net_" ;
	static final String NET_JO_FN = "_net_.json" ;
	
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	float x = 0 ;
	float y = 0 ;
	boolean bShowRT = false;
	
	long updateDT = System.currentTimeMillis() ;
	
	// String tarId = null ;
	boolean bEnable = true ;
	
	LinkedHashMap<String,MNNode> id2node = new LinkedHashMap<>() ;
	
	LinkedHashMap<String,MNModule> id2module = new LinkedHashMap<>() ;
	
	MNManager belongTo = null ;
	IMNContainer container = null ;

	public MNNet(MNManager mgr)
	{
		belongTo = mgr ;
		if(mgr!=null)
			this.container = mgr.getBelongTo() ;
		
		this.id = IdCreator.newSeqId() ;
	}
	
	/**
	 * 创建新的定义
	 * @param ref_id
	 * @param title
	 * @return
	 */
	public MNNet(MNManager mgr,String name,String title,String desc)
	{
		belongTo = mgr ;
		this.container = mgr.getBelongTo() ;
		
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
	
	public MNManager getBelongTo()
	{
		return this.belongTo ;
	}
	
	public IMNContainer getContainer()
	{
		return this.container ;
	}
	
	public File getNetFile()
	{
		return this.belongTo.calNetFile(this.id) ;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
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
			return newn ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			failedr.append(e.getMessage()) ;
			return null ;
		}
	}
	
	public MNNode createNewNodeByFullTP(String full_tp,float x,float y,String module_id,String lib_item_id)  throws Exception
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
		
		MNLib.Item libitem = null ;
		if(Convert.isNotNullEmpty(lib_item_id))
		{
			libitem = MNLib.getItemById("n", n.getTPFull(),lib_item_id) ;
			if(libitem==null)
			{
				throw new MNException("no lib item found with id="+lib_item_id) ;
			}
		}
		
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
		
		if(libitem!=null)
		{
			new_n.setParamJO(libitem.getPmJO());
			new_n.title = libitem.getTitle()+" "+calcNextTitleSuffix(libitem.getTitle()) ;
		}
		else
		{
			new_n.title = tpt+" "+calcNextTitleSuffix(tpt) ;
		}
		id2node.put(new_n.id, new_n) ;
		if(m!=null)
		{
			m.nodeIdSet.add(new_n.id) ;
		}
		
		save() ;
		return new_n ;
	}
	
	public MNNode createNewNodeInModule(MNModule m,MNNode tempn,float x,float y,String lib_item_id,boolean bsave)  throws Exception
	{
		//m.getSupportedNodeByTP(tp) ;
		MNNode n = tempn;
		if(n==null)
			return null ;
		
		MNLib.Item libitem = null ;
		if(Convert.isNotNullEmpty(lib_item_id))
		{
			libitem = MNLib.getItemById("n", n.getTPFull(),lib_item_id) ;
			if(libitem==null)
			{
				throw new MNException("no lib item found with id="+lib_item_id) ;
			}
		}
		
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
		
		if(libitem!=null)
		{
			new_n.setParamJO(libitem.getPmJO());
			new_n.title = libitem.getTitle()+" "+calcNextTitleSuffix(libitem.getTitle()) ;
		}
		else
		{
			new_n.title = tpt+" "+calcNextTitleSuffix(tpt) ;
		}
		id2node.put(new_n.id, new_n) ;
		if(m!=null)
		{
			m.nodeIdSet.add(new_n.id) ;
		}
		
		if(bsave)
			save() ;
		return new_n ;
	}
	
	public MNModule createNewModuleByFullTP(String full_tp,float x,float y,String lib_item_id)  throws Exception
	{
		MNModule m = MNManager.getModuleByFullTP(full_tp) ;
		if(m==null)
		{
			return null ;
		}
		
		MNLib.Item libitem = null ;
		if(Convert.isNotNullEmpty(lib_item_id))
		{
			libitem = MNLib.getItemById("m", m.getTPFull(),lib_item_id) ;
			if(libitem==null)
			{
				throw new MNException("no lib item found with id="+lib_item_id) ;
			}
		}
		
		MNModule new_n = m.createNewIns(this) ;
		
		new_n.x = x ;
		new_n.y = y ;
		String tpt = m.getTPTitle() ;
		if(libitem!=null)
		{
			new_n.setParamJO(libitem.getPmJO());
			new_n.title = libitem.getTitle()+" "+calcNextTitleSuffix(libitem.getTitle()) ;
		}
		else
		{
			new_n.title = tpt+" "+calcNextTitleSuffix(tpt) ;
		}
		
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
	
	private boolean checkItemHasRunning(MNBase item)
	{
		if(item instanceof IMNRunner)
		{
			if(((IMNRunner)item).RT_isRunning())
				return true ;
		}
		
		if(item instanceof MNNode)
		{
			return false;
		}
		
		if(item instanceof MNModule)
		{
			MNModule m = (MNModule)item ;
			List<MNNode> rns = m.getRelatedNodes() ;
			if(rns==null||rns.size()<=0)
				return false;
			for(MNNode rn:rns)
			{
				if(rn instanceof IMNRunner)
				{
					if(((IMNRunner)rn).RT_isRunning())
						return true ;
				}
			}
		}
		return false;
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
			if(checkItemHasRunning(m))
				throw new MNException("cannot del modules or nodes when running") ;
			
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
		
		if(checkItemHasRunning(n))
		{
			if(((IMNRunner)n).RT_isRunning())
				throw new RuntimeException("node ["+n.getTitle()+"] is running") ;
		}
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
	
	public MNBase setDetailJO(String itemid,JSONObject detail_jo,boolean bsave) throws Exception
	{
		MNBase n = this.getItemById(itemid) ;
		if(n==null)
			return null ;
		if(n instanceof IMNRunner)
		{
			IMNRunner run = (IMNRunner)n ;
			if(run.RT_isRunning())
				throw new MNException("node is running,please stop first") ;
		}
		n.setDetailJO(detail_jo);
		
		if(n instanceof MNModule)
			((MNModule)n).checkAfterSetParam();
		
		if(bsave)
			this.save();
		return n ;
	}
	
	public MNCxtPk setCxtDefJO(String itemid,JSONObject cxtdef_jo,boolean bsave) throws IOException
	{
		MNCxtPk def = null ;
		if(Convert.isNotNullEmpty(itemid))
		{
			def = this.getItemById(itemid) ;
			if(def==null)
				return null ;
		}
		else
		{
			def = this ;
		}
		def.CXT_setDefJO(cxtdef_jo);
		if(bsave)
			this.save();
		return def ;
	}
	
	/**
	 * 根据前端返回的基本（布局）信息，对自身进行更新并保存
	 * @param jo
	 * @return
	 */
	public boolean updateBasicByJO(JSONObject jo,StringBuilder failedr) throws Exception
	{
		this.fromJOBasic(jo, failedr) ;
		
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
	
	public void save() throws IOException
	{
		this.belongTo.saveNet(this);
	}
	
	public boolean renderOut(Writer out) throws IOException
	{
		JSONObject jo = this.toJO() ;
		
		// fit for UI
		jo.putOpt("_tp", "__net") ;
		jo.putOpt("tpt", "Flow") ;
		jo.putOpt("color", "#FFD700") ;
		jo.putOpt("icon", "\\uf126-90") ;
		jo.putOpt("show_rt", this.bShowRT) ;
		jo.putOpt("runner",true) ;
		jo.putOpt("pm_ready",true) ;
		jo.putOpt("pm_err","") ;
				
		jo.write(out) ;
		return true ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("name", this.name) ;
		jo.putOpt("id", this.getId()) ;
		jo.putOpt("title", this.title) ;
		jo.putOpt("x", this.x) ;
		jo.putOpt("y", this.y) ;
		jo.put("show_rt", this.bShowRT) ;
		jo.putOpt("enable",this.bEnable) ;

		return jo ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = toListJO();
		//JSONObject jo = toListJO();
		jo.putOpt("desc", this.desc) ;
		
		JSONObject cxtdefjo = this.CXT_getDefJO();
		jo.putOpt("cxt_def", cxtdefjo) ;
		
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
	

	protected boolean fromJOBasic(JSONObject jo,StringBuilder failedr)
	{
		this.x = jo.optFloat("x", this.x) ;
		this.y = jo.optFloat("y", this.y) ;
		this.bShowRT = jo.optBoolean("show_rt",true) ;
		return true ;
	}
	
	
	public boolean fromJO(JSONObject jo)
	{
		//super.fromJO(jo) ;
		this.id = jo.getString("id") ;
		this.name = jo.getString("name") ;
		this.title = jo.optString("title",this.title) ;
		this.desc= jo.optString("desc",this.desc) ;
		this.x = jo.optFloat("x", 0) ;
		this.y = jo.optFloat("y", 0) ;
		this.bEnable = jo.optBoolean("enable",true) ;
		this.bShowRT = jo.optBoolean("show_rt",true) ;
		
		JSONObject cxt_def = jo.optJSONObject("cxt_def") ;
		if(cxt_def!=null)
		{
			this.CXT_setDefJO(cxt_def) ;
		}

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
	
	public JSONObject RT_getNetUpdate(List<String> div_ids)
	{
		JSONObject jo = this.RT_toJO(true) ;//new JSONObject() ;
		jo.put("id", this.getId()) ;
		
		
		JSONObject id2node = new JSONObject() ;
		jo.put("id2node", id2node) ;
		JSONObject id2module = new JSONObject() ;
		jo.put("id2module", id2module) ;
		JSONObject global = new JSONObject() ;
		jo.put("global", global) ;
		
		for(MNNode n:this.id2node.values())
		{
			String nid = n.getId() ;
			boolean outdiv = div_ids!=null && div_ids.contains(nid) ;
			JSONObject tmpjo = n.RT_toJO(outdiv) ;
			id2node.put(nid,tmpjo) ;
		}
		
		for(MNModule n:this.id2module.values())
		{
			String nid = n.getId() ;
			boolean outdiv = div_ids!=null && div_ids.contains(nid) ;
			JSONObject tmpjo = n.RT_toJO(outdiv) ;
			id2module.put(nid,tmpjo) ;
		}
		return jo ;
	}

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
		
		if(!this.bEnable)
		{
			Lan lan = Lan.getLangInPk(MNNet.class) ;
			failedr.append(lan.g("not_enabled")) ;
			return false;
		}
		IMNOnOff nstart = (IMNOnOff)n;
		//return RT_triggerNodeStart(nstart,null,failedr) ;
		return nstart.RT_triggerByOnOff(failedr) ;
	}
	
	
	public boolean RT_startOrStopRunner(String itemid,boolean start_or_stop,StringBuilder failedr)
	{
		MNBase item = this.getItemById(itemid) ;
		if(item==null)
		{
			failedr.append("no item found") ;
			return false;
		}
		if(!(item instanceof IMNRunner))
		{
			failedr.append("not Runner item") ;
			return false;
		}
		IMNRunner rr = (IMNRunner)item ;
		if(start_or_stop)
			return rr.RT_start_main(failedr) ;
		else
		{
			rr.RT_stop();
			return true ;
		}
	}
	
	public void RT_startNetFlow(StringBuilder failedr)
	{
		if(!this.bEnable)
		{
			Lan lan = Lan.getLangInPk(this.getClass()) ;
			failedr.append(lan.g("not_enabled")) ;
			return ;
		}
		
		List<MNNodeStart> ns = getStartNodes() ;
		for(MNNodeStart n:ns)
		{
			if(n instanceof NS_OnFlowEvt)
			{
				((NS_OnFlowEvt)n).RT_fireFlowStart();
			}
		}
		
		for(MNModule m:this.id2module.values())
		{
			
			if(!(m instanceof IMNRunner))
				continue ;
			
//			if(!m.bEnable)
//				continue ;
			
			IMNRunner mnr = (IMNRunner)m ;
			if(!mnr.RT_start_main(failedr))
			{
				failedr.append("\r\n") ;
			}
		}
		
		for(MNNode n:this.id2node.values())
		{
			if(!(n instanceof IMNRunner))
				continue ;
			
//			if(!n.bEnable)
//				continue ;
			
			IMNRunner mnr = (IMNRunner)n ;
			if(!mnr.RT_start_main(failedr))
			{
				failedr.append("\r\n") ;
			}
		}
		
		RT_running = true ;
	}
	
	public void RT_stopNetFlow()
	{
		for(MNNode n:this.id2node.values())
		{
			if(!(n instanceof IMNRunner))
				continue ;
			
			IMNRunner mnr = (IMNRunner)n ;
			mnr.RT_stop();
		}
		
		for(MNModule m:this.id2module.values())
		{
			if(!(m instanceof IMNRunner))
				continue ;
			
			IMNRunner mnr = (IMNRunner)m ;
			mnr.RT_stop();
		}
		
		List<MNNodeStart> ns = getStartNodes() ;
		for(MNNodeStart n:ns)
		{
			if(n instanceof NS_OnFlowEvt)
			{
				((NS_OnFlowEvt)n).RT_fireFlowStop();
			}
		}
		
		RT_running = false ;
	}
	
	public void RT_clean()
	{
		for(MNNode n:this.id2node.values())
		{
			n.RT_clean() ;
		}
		
		for(MNModule m:this.id2module.values())
		{
			m.RT_clean() ;
		}
	}

	JSONObject RT_CXT_toSaveJO()
	{
		JSONObject jo = this.RT_CXT_extractValsForSave() ;
		JSONObject jnodes = new JSONObject() ;
		jo.put("__cxt_items", jnodes) ;
		
		for(MNNode n:this.id2node.values())
		{
			JSONObject tmpjo = n.RT_CXT_extractValsForSave() ;
			jnodes.put(n.getId(),tmpjo) ;
		}
		
		for(MNModule n:this.id2module.values())
		{
			JSONObject tmpjo = n.RT_CXT_extractValsForSave() ;
			jnodes.put(n.getId(),tmpjo) ;
		}
		return jo ;
	}
	
	
	void RT_CXT_fromSavedJO(JSONObject jo)
	{
		if(jo==null)
			return ;
		this.RT_CXT_injectSavedVals(jo);
		JSONObject jnodes = jo.optJSONObject("__cxt_items");
		if(jnodes!=null)
		{
			for(String id:jnodes.keySet())
			{
				MNBase item = this.getItemById(id) ;
				if(item==null)
					continue;
				JSONObject tmpjo=jnodes.getJSONObject(id);
				item.RT_CXT_injectSavedVals(tmpjo);
			}
		}
	}
	
	public boolean RT_CXT_isSelfOrSubDirty()
	{
		if(this.RT_CXT_isDirty())
			return true;
		
		for(MNNode n:this.id2node.values())
		{
			if(n.RT_CXT_isDirty())
				return true ;
		}
		
		for(MNModule n:this.id2module.values())
		{
			if(n.RT_CXT_isDirty())
				return true ;
		}
		
		return false;
	}
	
	void RT_CXT_clearSelfSubDirty()
	{
		this.RT_CXT_clearDirty();
		for(MNNode n:this.id2node.values())
		{
			n.RT_CXT_clearDirty();
		}
		for(MNModule n:this.id2module.values())
		{
			n.RT_CXT_clearDirty();
		}
	}
	
	private boolean RT_running = false;

	@Override
	public boolean RT_start(StringBuilder failedr)
	{
		RT_startNetFlow(failedr) ;
		
		return true;
	}

	@Override
	public void RT_stop()
	{
		this.RT_stopNetFlow();
		
	}

	@Override
	public boolean RT_isRunning()
	{
		return RT_running ;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
	
	public JSONObject RT_toJO(boolean out_rt_div)
	{
		JSONObject jo = new JSONObject() ;
		
		{
			IMNRunner rnr = (IMNRunner)this ;
			jo.put("runner", true) ;
			jo.put("b_running",rnr.RT_isRunning()) ;
			jo.put("runner_en", this.RT_runnerEnabled()) ;
			jo.put("runner_in", this.RT_runnerStartInner()) ;
			StringBuilder rsb = new StringBuilder() ;
			boolean bsusp = rnr.RT_isSuspendedInRun(rsb) ;
			jo.put("suspended", bsusp) ;
			if(bsusp)
				jo.put("suspend_reson", rsb.toString()) ;
		}
		
		if(out_rt_div)
		{
			ArrayList<DivBlk> divblks = new ArrayList<>() ;
			RT_renderDiv(divblks);
			JSONArray tmpjar = new JSONArray() ;
			for(DivBlk db : divblks)
				tmpjar.put(db.toJO()) ;
			jo.put("divs", tmpjar) ;

			//jo.put("div", divsb.toString()) ;
		}
		
		//jo.put("has_warn", this.RT_DEBUG_WARN.hasPrompts()) ;
		//jo.put("has_err", this.RT_DEBUG_ERR.hasPrompts()) ;
		return jo ;
	}

	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		//if(isRunner())
		{
			IMNRunner rnr = (IMNRunner)this ;
			StringBuilder divsb = new StringBuilder() ;
			if(rnr.RT_isRunning())
			{
				StringBuilder ssb = new StringBuilder() ;
				if(rnr.RT_isSuspendedInRun(ssb))
					divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:#dd7924\">Suspended:"+ssb.toString()+"</span><button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',false)\">stop</button></div>") ;
				else
					divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:green\">Running</span><button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',false)\">stop</button></div>") ;
			}
			else
				divsb.append("<div tp='run' class=\"rt_blk\"><span style=\"color:green\">Stopped</span><button onclick=\"rt_item_runner_start_stop('"+this.getId()+"',true)\">start</button></div>") ;
			divblks.add(new DivBlk("net_run",divsb.toString())) ;
		}
//		
//		RT_DEBUG_ERR.renderDiv(divsb);
//		
//		RT_DEBUG_WARN.renderDiv(divsb);
//		
//		RT_DEBUG_INF.renderDiv(divsb);

		CXT_renderVarsDiv(divblks) ;
		
	}
	
	public void RT_triggerByOuter(String triggername,JSONObject payload)
	{
		for(MNNode n:this.id2node.values())
		{
			if(n instanceof NS_OuterTrigger)
			{
				((NS_OuterTrigger)n).RT_triggerByOuter(triggername,payload) ;
			}
		}
	}
	
	// js 
	
	private transient MNContext jsCxt = null ; 
	
	public MNContext RT_JS_getContext() throws ScriptException
	{
		if(jsCxt!=null)
			return jsCxt ;
		
		synchronized(this)
		{
			if(jsCxt!=null)
				return jsCxt ;
			jsCxt = new MNContext(this) ;
			return jsCxt ;
		}
	}
}
