package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class OutCallRet extends MNNodeEnd
{
	String funcName = null ;
	
	public String getFuncName()
	{
		return this.funcName ;
	}
	
	@Override
	public String getTitle()
	{
		if(Convert.isNotNullEmpty(this.funcName))
			return this.funcName+"()";
		else
			return super.getTitle() ;
	}
	
	@Override
	public String getTP()
	{
		return "_oc_func_ret";
	}

	@Override
	public String getTPTitle()
	{
		return "Func Return";
	}

	@Override
	public String getColor()
	{
		return "#dddddd";
	}

	@Override
	public String getIcon()
	{
		return "PK_call_out";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.funcName))
		{
			failedr.append("no func name set") ;
			return false;
		}
		
		OutCallFunc ocf = findPrevFunc(funcName,this,null) ;
		if(ocf==null)
		{
			failedr.append("no OutCallFunc node prev") ;
			return false;
		}
		return true ;
	}
	
	
	
	private static OutCallFunc findPrevFunc(String func,MNNode curn,List<OutCallFunc> all_cf)
	{
		List<MNConn> pconns = curn.getInConns() ;
		if(pconns==null)
			return null ;
		
		for(MNConn c:pconns)
		{
			MNNode pnode = c.getFromBelongToNode() ;
			if(pnode==null)
				continue ;
			
			if(pnode instanceof OutCallFunc)
			{
				OutCallFunc ocf = (OutCallFunc)pnode ;
				if(all_cf!=null)
					all_cf.add(ocf) ;
				
				if(func!=null && func.equals(ocf.funcName))
					return ocf ;
				continue ;
			}
			
			OutCallFunc ocf = findPrevFunc(func,pnode,all_cf) ;
			if(ocf!=null)
				return ocf ;
		}
		return null ;
	}
	
	public List<OutCallFunc> findAllPrevCallNodes()
	{
		ArrayList<OutCallFunc> rets = new ArrayList<>() ;
		findPrevFunc(null,this,rets) ;
		return rets ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("func", this.funcName) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.funcName = jo.optString("func") ;
	}
	
	// rt
	
	MNMsg retMsg = null ;

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		retMsg = msg ;
		return null;
	}
	
	public void clearReturnMsg()
	{
		retMsg = null ;
	}

	public MNMsg getReturnMsg()
	{
		return retMsg ;
	}
}
