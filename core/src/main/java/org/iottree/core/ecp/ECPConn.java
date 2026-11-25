package org.iottree.core.ecp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.ecp.util.Jt;
import org.iottree.core.ecp.util.Point;
import org.iottree.core.ecp.util.PtId;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class ECPConn extends ECPBase
{
	ECPNet net ;
	
	private PtId f_jt_ptid = null ;
	
	private PtId t_jt_ptid = null ;
	
	boolean bEnable = true ;
	
	private ArrayList<PtId> mid_pts = null ;
	
//	private ECPJoint from ;
//	
//	private ECPJoint to ;
	
	public ECPConn(ECPNet net,String id)
	{
		super(id);
		this.net = net ;
	}
	
	public ECPNet getNet()
	{
		return this.net ;
	}
	
	public PtId getFromPtJtId()
	{
		return this.f_jt_ptid ;
	}
	
	public String getFromJtID()
	{
		if(this.f_jt_ptid==null)
			return null ;
		return this.f_jt_ptid.id ;
	}
	
	public PtId getToPtJtId()
	{
		return this.t_jt_ptid ;
	}
	
	public String getToJtID()
	{
		if(this.t_jt_ptid==null)
			return null ;
		return this.t_jt_ptid.id ;
	}
	
	public PtId getMidPtById(String mid_id)
	{
		if(Convert.isNullOrEmpty(mid_id))
			return null ;
		if(this.mid_pts==null)
			return null ;
		for(PtId mpt:this.mid_pts)
		{
			if(mid_id.equals(mpt.id))
				return mpt ;
		}
		return null ;
	}
	
	public Jt getFromJt()
	{
		return this.getNet().findJtById(getFromJtID());
	}
	
	public Jt getToJt()
	{
		return this.getNet().findJtById(getToJtID()) ;
	}
	
	private String calcJtId(PtId mid_ptid)
	{
		if(Convert.isNullOrEmpty(mid_ptid.id))
			return null ;
		return Jt.JT_TP_CONN+":"+this.getId()+":"+mid_ptid.id;
	}
	
	public List<ECPConn> getConnsDirect()
	{
		//直接连接的conn
        ArrayList<ECPConn> r= new ArrayList<>() ;
        ECPNet net = this.getNet() ;
        HashSet<String> mid_jtids = new HashSet<>() ;
        if(this.mid_pts!=null)
        {
        	for(PtId pid:this.mid_pts)
        	{
        		String jtid = this.calcJtId(pid) ;
        		if(Convert.isNullOrEmpty(jtid))
        			continue ;
        		mid_jtids.add(jtid) ;
        	}
        }
        for(ECPConn item : net.id2conn.values())
        {
        	if(item==this) continue ;
            String fjtid = item.getFromJtID();
            String tjtid = item.getToJtID();
            if(mid_jtids.contains(fjtid) || mid_jtids.contains(tjtid))
                r.add(item) ;
        }
        return r ;
	}
	
//	public ECPNode getFromNode()
//	{
//		ECPInOut jt = getFromInOut() ;
//		if(jt==null)
//			return null ;
//		return jt.getOwner() ;
//	}
	
//	public ECPInOut getToInOut()
//	{
//		if(Convert.isNullOrEmpty(this.t_io_uid))
//			return null ;
//		return this.getNet().getInOutByUID(t_io_uid) ;
//	}
	
//	public ECPNode getToNode()
//	{
//		ECPInOut jt = getToInOut() ;
//		if(jt==null)
//			return null ;
//		return jt.getOwner() ;
//	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	protected void onAfterLoaded()
	{}

	@Override
	public boolean removeMe()
	{
		return this.getNet().removeConnById(this.getId())!=null ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = super.toJO() ;
		
		ret.put("en", bEnable) ;
		if(this.f_jt_ptid!=null)
			ret.putOpt("f_jt_ptid", this.f_jt_ptid.toJO()) ;
		if(this.t_jt_ptid!=null)
			ret.putOpt("t_jt_ptid", this.t_jt_ptid.toJO()) ;
		
		JSONArray pts_jarr = new JSONArray() ;
		ret.put("mid_pts",pts_jarr) ;
		if(this.mid_pts!=null)
		{
			for(PtId pt:this.mid_pts)
			{
				pts_jarr.put(pt.toJO()) ;
			}
		}
		return ret ;
	}
	
	private boolean fromJOLocal(JSONObject jo)
	{
		JSONObject tmpjo = jo.optJSONObject("f_jt_ptid") ;
		if(tmpjo==null)
			return false;
		this.f_jt_ptid = PtId.parseFrom(tmpjo) ;
		
		tmpjo = jo.optJSONObject("t_jt_ptid") ;
		if(tmpjo==null)
			return false;
		this.t_jt_ptid = PtId.parseFrom(tmpjo) ;
		
		this.bEnable = jo.optBoolean("en",true) ;

		JSONArray pts_jarr = jo.optJSONArray("mid_pts") ;
		if(pts_jarr!=null)
		{
			int n = pts_jarr.length() ;
			ArrayList<PtId> tppts = new ArrayList<>();
			for(int i = 0 ; i < n ; i ++)
			{
				tmpjo = pts_jarr.getJSONObject(i) ;
				PtId pt = PtId.parseFrom(tmpjo) ;
				if(pt==null)
					continue ;
				tppts.add(pt) ;
			}
			this.mid_pts = tppts ;
		}
		return true;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		super.fromJO(jo) ;
		
		fromJOLocal(jo);
		
		return true;
	}
	
	public void updateStructByJO(JSONObject jo)
	{
		fromJOLocal(jo);
	}
}
