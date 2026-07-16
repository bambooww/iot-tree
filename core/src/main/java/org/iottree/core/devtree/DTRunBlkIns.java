package org.iottree.core.devtree;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;

public class DTRunBlkIns
{
	static Lan lan = Lan.getLangInPk(DTRunBlkIns.class) ;
	
	public static enum Mode
	{
		interval(0), //norma
		
		run_once(1); //can be used for init
		
		private final int val;
		
		Mode(int v)
		{
			val = v;
		}

		public int getInt()
		{
			return val;
		}

		public String getTitle()
		{
			switch(val)
			{
			case 0:
				return lan.g("run_intv");
			case 1:
				return lan.g("run_once");
			default:
				return "" ;
			}
		}

		public static Mode valOfInt(int i)
		{
			switch (i)
			{
			case 1:
				return run_once;
			
			default:
				return interval;
			}
		}
	}
	
	
	DTNode owner = null;
	
	String runBlkUID = null ;
	
	String insName ;
	
	String insTitle ;
	
	String insDesc ;
	
	Mode mode = Mode.interval ;
	
	long minRunIntv = -1 ;

	boolean bEnable = true ;
	
	//ArrayList<DTRunTag> runTags = new ArrayList<>();
	
	private transient DTRunBlk runBlk = null ;
	
	DTRunBlkIns(DTNode owner)
	{
		this.owner = owner ;
	}
	
	DTRunBlkIns(DTNode owner,DTRunBlk runblk,String ins_name,String ins_title)
	{
		this.owner = owner ;
		runBlk = runblk ;
		this.runBlkUID = runblk.getUID();
		this.insName = ins_name ;
		this.insTitle = ins_title ;
	}
	
	DTRunBlkIns(DTNode owner,DTRunBlkIns oth)
	{
		this.owner = owner ;
		this.runBlkUID = oth.runBlkUID;
		runBlk = oth.runBlk ;
		this.insName = oth.insName ;
		this.insTitle = oth.insTitle ;
		this.insDesc = oth.insDesc ;
		this.mode = oth.mode ;
		this.minRunIntv = oth.minRunIntv ;
		this.bEnable = oth.bEnable ;
	}
	
	public DTNode getOwner()
	{
		return this.owner ;
	}
	
	public DTRunBlk getRunBlk()
	{
		if(runBlk!=null)
			return runBlk ;
		return this.runBlk = DTRunBlkManager.getInstance().getRunBlkByUID(this.runBlkUID) ;
	}
	
	public String getInsName()
	{
		return this.insName ;
	}
	
	public String getInsTitle()
	{
		return this.insTitle ;
	}
	
	public String getInsDesc()
	{
		return this.insDesc ;
	}
	
	public DTRunBlkIns asRunMode(Mode m,long min_run_intv)
	{
		this.mode = m;
		this.minRunIntv = min_run_intv ;
		return this ;
	}
	
	
	
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject jo = new JSONObject()
				.put("runblk_uid",this.runBlkUID).put("ins_name", this.insName).putOpt("ins_title",this.insTitle)
				.put("en", this.bEnable).put("m", this.mode.val).put("min_intv", this.minRunIntv)
				.putOpt("ins_desc", this.insDesc);

		if(b_show_detail)
		{
			DTRunBlk rb = getRunBlk() ;
			if(rb!=null)
			{
				jo.put("runblk_uid", rb.getUID()) ;
				jo.put("runblk_t",rb.getTitle()) ;
				jo.put("runblk_tt",rb.getTitleFull()) ;
			}
			jo.put("m_t", this.mode.getTitle()) ;
		}
		return jo ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = this.toJO(true) ;
		return jo ;
	}
	
	public void setBasicByJO(JSONObject jo)
	{
		this.insTitle = jo.optString("ins_title") ;
		this.bEnable = jo.optBoolean("en",true) ;
		this.mode = Mode.valOfInt(jo.optInt("m",0)) ;
		this.minRunIntv = jo.optLong("min_intv",-1) ;
		this.bEnable = jo.optBoolean("en",true) ;
		this.insDesc = jo.optString("ins_desc") ;
	}
	
	public boolean setDetailPMByJO(JSONObject jo,StringBuilder failedr)
	{
		return true ;
	}
	
	public static DTRunBlkIns fromJO(DTNode nd,JSONObject jo)
	{
		DTRunBlkIns ret = new DTRunBlkIns(nd) ;
		ret.insName = jo.optString("ins_name") ;
		ret.runBlkUID = jo.optString("runblk_uid") ;
		if(Convert.isNullOrEmpty(ret.insName) || Convert.isNullOrEmpty(ret.runBlkUID))
		{
			//failedr.append("no ins name or runblk name") ;
			return null ;
		}
		ret.setBasicByJO(jo);
		return ret ;
	}
}
