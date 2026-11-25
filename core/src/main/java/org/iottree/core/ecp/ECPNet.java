package org.iottree.core.ecp;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.ecp.util.Jt;
import org.iottree.core.ecp.util.JtOfConn;
import org.iottree.core.ecp.util.JtOfInOut;
import org.iottree.core.ecp.util.PtId;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class ECPNet
{
	private ECPNetContainer belongTo ;
	
	String id ;
	
	int x = 0 ;
	
	int y = 0 ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	boolean bEnable = true ;
	
	long updateDT = System.currentTimeMillis() ;
	
	LinkedHashMap<String,ECPNode> id2node = new LinkedHashMap<>() ;
	
	LinkedHashMap<String,ECPConn> id2conn = new LinkedHashMap<>() ;
	
	//LinkedHashMap<String,ECPArrow> id2arrow = new LinkedHashMap<>() ;
	
	//LinkedHashMap<String,ECPJoint> id2joint = new LinkedHashMap<>() ;
	
	
	int id_cc = 0 ;
	
	public ECPNet(ECPNetContainer ecpm,String id)
	{
		this.belongTo = ecpm ;
		this.id = id ;
	}
	
	public ECPNetContainer getBelongTo()
	{
		return this.belongTo ;
	}
	
	public ECPNet asBasic(String name,String title,String desc)
	{
		this.name = name ;
		this.title = title ;
		this.desc = desc ;
		return this ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public String getUID()
	{
		return this.belongTo.getContainerUID()+"-"+this.id ;
	}
	
	public int getX()
	{
		return this.x ;
	}
	
	public int getY()
	{
		return this.y ;
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

	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public Date getUpdateDT()
	{
		return new Date(this.updateDT) ;
	}
	
	public LinkedHashMap<String,ECPNode> getId2Node()
	{
		return this.id2node ;
	}
	
	public LinkedHashMap<String,ECPConn> getId2Conn()
	{
		return this.id2conn ;
	}
	
	public ECPNode getNodeById(String id)
	{
		return this.id2node.get(id) ;
	}
	
	public ECPInOut getInOutByUID(String uid)
	{
		int k = uid.indexOf('-');
		if(k<=0) return null ;
		String nid = uid.substring(0,k) ;
		String io_id = uid.substring(k+1) ;
		ECPNode nd = this.getNodeById(nid) ;
		if(nd==null)
			return null ;
		return nd.getInOutById(io_id) ;
	}
	
	public ECPConn getConnById(String id)
	{
		return this.id2conn.get(id) ;
	}
	
	
	public ECPBase findItemById(String id)
	{
		ECPNode nd = this.id2node.get(id) ;
		if(nd!=null)
			return nd ;

		ECPConn c = this.id2conn.get(id);
		if(c!=null)
			return c ;
		return null ;
	}
	
	public int removeItemByIds(List<String> ids) throws Exception
	{
		int r = 0 ;
		for(String id:ids)
		{
			ECPBase eb = this.findItemById(id) ;
			if(eb==null)
				continue ;
			if(eb.removeMe())
				r ++ ;
		}
		if(r>0)
			this.save();
		return r ;
	}
	
	public Jt findJtById(String jt_id)
	{
		List<String> ss = Convert.splitStrWith(jt_id, ":");
		if(ss==null||ss.size()<2)
			return null ;
		switch(ss.get(0))
		{
		case Jt.JT_TP_IO:
			ECPInOut io = this.getInOutByUID(ss.get(1)) ;
			if(io==null)
				return null ;
			return new JtOfInOut(io);
		case Jt.JT_TP_CONN:
			if(ss.size()<3) return null ;
			
			ECPConn cc = this.getConnById(ss.get(1));
			if(cc==null)
				return null ;
			PtId pid = cc.getMidPtById(ss.get(2));
			if(pid==null)
				return null ;
			return new JtOfConn(cc,pid) ;
		}
		return null ;
	}
	
	void onAfterLoaded()
	{
		for(ECPNode n:this.id2node.values())
		{
			n.onAfterLoaded();
		}
	}

	public ECPNode removeNodeById(String id)
	{
		return this.id2node.remove(id) ;
	}
	
	public ECPConn removeConnById(String id)
	{
		return this.id2conn.remove(id) ;
	}
	
	
	public boolean renderOut(Writer out) throws IOException
	{
		JSONObject jo = this.toJO() ;
		
		// fit for UI
		jo.putOpt("_tp", "_net") ;
		jo.putOpt("tpt", "Net") ;
		jo.putOpt("color", "#FFD700") ;
		jo.putOpt("icon", "\\uf126-90") ;
		//jo.putOpt("show_rt", this.bShowRT) ;
		jo.putOpt("runner",true) ;
		jo.putOpt("pm_ready",true) ;
		jo.putOpt("pm_err","") ;
		
		jo.write(out) ;
		return true ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("id", this.id) ;
		jo.putOpt("name", this.name) ;
		jo.putOpt("title", this.title) ;
		jo.putOpt("enable",this.bEnable) ;
		return jo ;
	}

	public JSONObject toJO()
	{
		JSONObject ret = this.toListJO();
		ret.put("x", this.x);
		ret.put("y", this.y);
		ret.put("id_cc", this.id_cc) ;
		
		JSONArray jarr = new JSONArray() ;
		ret.put("nodes", jarr) ;
		for(ECPNode u:id2node.values())
		{
			jarr.put(u.toJO()) ;
		}
		
		jarr = new JSONArray() ;
		ret.put("conns", jarr) ;
		for(ECPConn aa:id2conn.values())
		{
			jarr.put(aa.toJO()) ;
		}
		
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.name = jo.getString("name") ;
		this.title = jo.optString("title",this.title) ;
		this.desc= jo.optString("desc",this.desc) ;
		this.bEnable = jo.optBoolean("enable",true) ;
		
		StringBuilder failedr = new StringBuilder() ;
		return updateStructByJO(jo,failedr);
	}
	
	public boolean updateStructByJO(JSONObject jo,StringBuilder failedr)
	{
		this.x = jo.optInt("x",0) ;
		this.y = jo.optInt("y",0) ;
		this.id_cc = jo.optInt("id_cc",0) ;
		
		JSONArray jarr = jo.optJSONArray("nodes") ;
		int n = jarr!=null?jarr.length():0;
		LinkedHashMap<String,ECPNode> id2node = new LinkedHashMap<>() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			String id = tmpjo.optString("id") ;
			String tp = tmpjo.optString("tp") ;
			ECPNode u = ECPNode.createByTp(this,tp,id) ;
			if(u==null)
				continue ;
			if(!u.fromJO(tmpjo))
				continue ;
			id2node.put(u.getId(),u) ;
		}
		this.id2node = id2node;
		
		jarr = jo.optJSONArray("conns") ;
		n = jarr!=null?jarr.length():0;
		LinkedHashMap<String,ECPConn> id2conn = new LinkedHashMap<>() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			String id = tmpjo.optString("id") ;
			if(Convert.isNullOrEmpty(id))
				continue ;
			ECPConn aa = new ECPConn(this,id) ;
			if(!aa.fromJO(tmpjo))
				continue ;
			id2conn.put(aa.getId(),aa) ;
		}
		this.id2conn = id2conn;
		
		return true;
	}

//	public boolean updateStructByJO(JSONObject jo,StringBuilder failedr)
//	{
//		this.x = jo.optInt("x",0) ;
//		this.y = jo.optInt("y",0) ;
//		
//		JSONArray jarr = jo.optJSONArray("nodes") ;
//		int n = jarr!=null?jarr.length():0;
//		for(int i = 0 ; i < n ; i ++)
//		{
//			JSONObject tmpjo = jarr.getJSONObject(i) ;
//			String id = tmpjo.optString("id") ;
//			if(Convert.isNullOrEmpty(id))
//				continue ;
//			ECPNode nd = id2node.get(id) ;
//			if(nd==null)
//			{
//				String tp = tmpjo.optString("tp") ;
//				ECPNode u = ECPNode.createByTp(this,tp,id) ;
//				if(u==null)
//					continue ;
//				if(!u.fromJO(tmpjo))
//					continue ;
//				this.id2node.put(u.getId(),u) ;
//			}
//			else
//			{
//				nd.updateStructByJO(tmpjo) ;
//			}
//			
//		}
//		
//		jarr = jo.optJSONArray("arrows") ;
//		n = jarr!=null?jarr.length():0;
//		for(int i = 0 ; i < n ; i ++)
//		{
//			JSONObject tmpjo = jarr.getJSONObject(i) ;
//			String id = tmpjo.optString("id") ;
//			if(Convert.isNullOrEmpty(id))
//				continue ;
//			ECPArrow old_ln = id2arrow.get(id) ;
//			if(old_ln==null)
//			{
//				ECPArrow ln = new ECPArrow(this,id) ;
//				if(!ln.fromJO(tmpjo))
//					continue ;
//				this.id2arrow.put(ln.getId(),ln) ;
//			}
//			else
//			{
//				old_ln.updateStructByJO(tmpjo);
//			}
//		}
//		
//		jarr = jo.optJSONArray("lines") ;
//		n = jarr!=null?jarr.length():0;
//		for(int i = 0 ; i < n ; i ++)
//		{
//			JSONObject tmpjo = jarr.getJSONObject(i) ;
//			String id = tmpjo.optString("id") ;
//			if(Convert.isNullOrEmpty(id))
//				continue ;
//			ECPLine old_ln = id2line.get(id) ;
//			if(old_ln==null)
//			{
//				ECPLine ln = new ECPLine(this,id) ;
//				if(!ln.fromJO(tmpjo))
//					continue ;
//				this.id2line.put(ln.getId(),ln) ;
//			}
//			else
//			{
//				old_ln.updateStructByJO(tmpjo);
//			}
//		}
//		return true;
//	}
	
	public void save() throws Exception
	{
		this.belongTo.saveNet(this);
	}
	
	Thread rtTH = null ;
	boolean rtRun = false;
//	
//	MNCxtPk rtCxtPrj = new MNCxtPk() {
//
//		@Override
//		public String CXT_getUID()
//		{
//			return belongTo.getMsgNetContainerId();
//		}} ;
//	
//	public MNCxtPk RT_getCxtPk()
//	{
//		return rtCxtPrj ;
//	}

	private Runnable rtCxtPrjRunner = new Runnable() {

		@Override
		public void run()
		{
			RT_run() ;
		}
	};
	
	private void RT_run()// throws Exception
	{
		try
		{
//			RT_CXT_load() ;
//			
//			StringBuilder failedr = new StringBuilder() ;
//			for(MNNet net:this.listNets())
//			{
//				if(!net.isEnable())
//					continue ;
//				
//				net.RT_startNetFlow(failedr);
//			}
//		
//			while(rtRun)
//			{
//				Thread.sleep(300);
//				
//				synchronized(this) //cannot be stopped
//				{
//					RT_CXT_save(false);
//				}
//			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
			//RT_stop() ;
			rtTH = null ;
			rtRun=false;
		}
	}
	
	public synchronized void RT_start()
	{
		if(rtRun)
			return ;
		
		rtRun = true ;
		rtTH  =new Thread(rtCxtPrjRunner);
		rtTH.start();
	}
	
	public synchronized void RT_stop()
	{
		if(!rtRun)
			return ;

//		for(MNNet net:this.listNets())
//		{
//			if(!net.isEnable())
//				continue ;
//			net.RT_stopNetFlow();
//		}
		
		rtRun = false;
	}
	
	public boolean RT_isRunning()
	{
		return rtRun ;
	}
}
