package org.iottree.core.msgnet.util;

import org.iottree.core.msgnet.IMNCxtPk;
import org.iottree.core.msgnet.MNBase;
import org.iottree.core.msgnet.MNCxtPkTP;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNCxtValTP;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONObject;

/**
 * run in node change context rule
 * 
 * @author jason.zhu
 */
public class CxtChgRule
{
	public static enum Action
	{
		set,del,move;//chg,;
		
		public String getTitle()
		{
			Lan lan = Lan.getLangInPk(CxtChgRule.class) ;
			return lan.g("act_"+name()) ;
		}
	}


	
	
	
	
	public static abstract class Pm
	{
		//public abstract Object RT_getVal(MNNet net,MNBase item,MNMsg msg) ;
		
		public abstract JSONObject toJO() ;
		
		public abstract boolean fromJO(JSONObject jo) ;
		
		public abstract boolean isValid(StringBuilder failedr);
		
	}
	
	
	public static class PmSet extends Pm
	{
		MNCxtValSty valSty = MNCxtValSty.vt_str;
		String valStrOrSubN = null ;
		boolean deepCopy=false;
		
		public PmSet(MNCxtValSty vsty,String val_str_subn,boolean deepcopy)
		{
			this.valSty = vsty ;
			this.valStrOrSubN = val_str_subn ;
			this.deepCopy = deepcopy ;
		}
		
		private PmSet()
		{}
		
//		@Override
//		public Object RT_getVal(MNNet net,MNBase item,MNMsg msg)
//		{
//			return RT_getValInCxt(valSty,valStrOrSubN,net,item,msg);
//		}
		

		@Override
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("sor_valsty", valSty.name()) ;
			jo.put("sor_subn", this.valStrOrSubN) ;
			jo.put("sor_deep_cp", this.deepCopy) ;
			return jo;
		}

		@Override
		public boolean fromJO(JSONObject jo)
		{
			this.valSty  = MNCxtValSty.valueOf(jo.getString("sor_valsty")) ;
			this.valStrOrSubN = jo.getString("sor_subn") ;
			this.deepCopy = jo.optBoolean("sor_deep_cp",false) ;
			return true;
		}
		
		public boolean isValid(StringBuilder failedr)
		{
			if(Convert.isNullOrEmpty(this.valStrOrSubN))
			{
				if(null!=valSty.getCxtPkTP())
				{
					failedr.append("sub name or constant val is null or empty");
					return false;
				}
			}
			return true;
		}
	}
	
	public static class PmMove extends Pm
	{
		MNCxtPkTP toPk = MNCxtPkTP.msg ;
		String toSubName = "payload" ;
		
		public PmMove(MNCxtPkTP sor_pk,String sor_subname)
		{
			this.toPk = sor_pk ;
			this.toSubName = sor_subname ;
		}
		
		private PmMove()
		{}
		@Override
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("to_pktp", toPk.name()) ;
			jo.put("to_subn", toSubName) ;
			return jo;
		}

		@Override
		public boolean fromJO(JSONObject jo)
		{
			this.toPk  = MNCxtPkTP.valueOf(jo.getString("to_pktp")) ;
			this.toSubName = jo.getString("to_subn") ;
			return true;
		}
		
		public boolean isValid(StringBuilder failedr)
		{
			if(Convert.isNullOrEmpty(this.toSubName))
			{
				failedr.append("sub name or constant val is null or empty");
				return false;
			}
			return true;
		}
	}
	
	Action act = Action.set ;
	MNCxtPkTP tarPk = MNCxtPkTP.msg ;
	String tarSubName = "payload" ;
	
	Pm pm = null ;
	
	public CxtChgRule()
	{
		
	}
	
	public CxtChgRule asActionTar(Action act,MNCxtPkTP tar_pk,String tar_subname)
	{
		this.act = act ;
		this.tarPk = tar_pk ;
		this.tarSubName = tar_subname ;
		return this ;
	}
	
	public CxtChgRule asSorSet(MNCxtValSty vsty,String val_str_subn,boolean deepcopy)
	{
		this.pm = new PmSet(vsty,val_str_subn,deepcopy);
		return this ;
	}
	
	public CxtChgRule asSorMove(MNCxtPkTP sor_pk,String sor_subname)
	{
		this.pm = new PmMove(sor_pk,sor_subname);
		return this ;
	}
	
	public boolean isValid(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.tarSubName))
		{
			failedr.append("target sub name is empty") ;
			return false;
		}
		if(pm==null && this.act!=Action.del)
		{
			failedr.append("need sor data") ;
			return false;
		}
		if(pm!=null)
		{
			if(!pm.isValid(failedr))
				return false;
		}
		return true ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("act", act.name()) ;
		jo.put("tar_pktp", this.tarPk.name()) ;
		jo.put("tar_subn", this.tarSubName) ;
		if(pm!=null)
			jo.put("pm", pm.toJO()) ;
		return jo ;
	}
	
	public static CxtChgRule fromJO(JSONObject jo,StringBuilder failedr)
	{
		CxtChgRule ret = new CxtChgRule() ;
		String actn = jo.getString("act") ;
		String tar_pkn = jo.getString("tar_pktp") ;
		ret.act = Action.valueOf(actn) ; 
		ret.tarPk = MNCxtPkTP.valueOf(tar_pkn) ;
		ret.tarSubName = jo.optString("tar_subn") ;
		JSONObject pmjo = jo.optJSONObject("pm") ;
		if(pmjo!=null)
		{
			switch(ret.act)
			{
			case set:
				PmSet ss = new PmSet() ;
				ss.fromJO(pmjo) ;
				ret.pm = ss ;
				break ;
			case move:
				PmMove sm = new PmMove() ;
				sm.fromJO(pmjo) ;
				ret.pm = sm ;
				break ;
			default:
				break ;
			}
			
		}
		return ret ;
	}
	
	// rt
	
	private boolean RT_runSet(MNNode node,MNMsg msg,StringBuilder failedr)
	{
		PmSet ss = (PmSet)this.pm ;
		Object val = ss.valSty.RT_getValInCxt(ss.valStrOrSubN,node.getBelongTo(), node, msg) ;
		//if(val==null)
		//	return true ;
		if(val!=null && ss.deepCopy)
		{
			if(val instanceof JSONObject)
			{//TODO
				//val.
			}
		}
		
		return this.tarPk.RT_setValInCxt(this.tarSubName,val,
				node.getBelongTo(),node,msg,failedr) ;
	}
	
	private boolean RT_runDel(MNNode node,MNMsg msg,StringBuilder failedr)
	{
		return this.tarPk.RT_setValInCxt(this.tarSubName,null,
				node.getBelongTo(),node,msg,failedr) ;
	}
	
	private boolean RT_runMove(MNNode node,MNMsg msg,StringBuilder failedr)
	{
		PmMove ss = (PmMove)this.pm ;
		Object val = ss.toPk.RT_getValInCxt(ss.toSubName,node.getBelongTo(),node,msg) ;
		//Object val = ss.RT_getVal(node.getBelongTo(), node, msg) ;
		
		if(!this.tarPk.RT_setValInCxt(this.tarSubName,val,node.getBelongTo(),node,msg,failedr))
			return false;
		if(val==null)
				return true ;
		return ss.toPk.RT_setValInCxt(ss.toSubName,null,node.getBelongTo(),node,msg,failedr) ;
	}
	
	public boolean RT_onMsgInRun(MNNode node,MNMsg msg,StringBuilder failedr)
	{
		switch(act)
		{
		case set:
			return RT_runSet(node,msg,failedr) ;
		case del:
			return RT_runDel(node,msg,failedr) ;
		case move:
			return RT_runMove(node,msg,failedr) ;
		default:
			failedr.append("unknown action") ;
			return false;
		}
	}
}
