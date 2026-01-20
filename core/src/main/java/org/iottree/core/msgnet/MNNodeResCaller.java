package org.iottree.core.msgnet;

import java.util.List;
import java.util.stream.Collectors;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public abstract class MNNodeResCaller extends MNNodeRes
{
	private transient JSONObject callInput = null ;
	private transient long callInputDT = -1 ;
	private transient JSONObject callOutput = null ;
	private transient long callOutputDT = -1 ;
	
	
	@Override
	public int getOutNum()
	{
		return 1 ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(getResCaller()==null)
		{
			failedr.append("no caller set or found") ;
			return false;
		}
		return true;
	}

	
	
	/**
	 * call by prev node
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public final JSONObject RT_onResCall(JSONObject input) throws Exception
	{
		callInput = input ;
		callInputDT = System.currentTimeMillis() ;
		
		ResCaller caller = getResCaller() ;
		if(caller!=null)
		{
			try
			{
				callOutput = caller.RT_onResCall(input) ;
				MNMsg m = new MNMsg().asPayload(callOutput) ;
				this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, m));
				return callOutput;
			}
			finally
			{
				callOutputDT = System.currentTimeMillis() ;
			}
		}
		return null ;
	}
	
	public String getCallerName()
	{
		ResCaller rc = getResCaller() ;
		if(rc==null)
			return "" ;
		return rc.getCallerName() ;
	}
	
	public String getCallerTitle()
	{
		ResCaller rc = this.getResCaller() ;
		if(rc==null)
			return "" ;
		return rc.getCallerTitle() ;
	}
	
	public abstract ResCaller getResCaller() ;
	
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		super.RT_renderDiv(divblks);
		
		if(callInput!=null || callOutput!=null)
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div class=\"rt_blk\">Caller") ;
			long cost = callOutputDT-callInputDT ;
			if(callInputDT>0 &&  callOutputDT>0 && cost>0)
				divsb.append(" run cost="+cost+"ms") ;
			
			if(callInput!=null && callInputDT>0)
			{
				divsb.append(Convert.calcDateGapToNow(callInputDT)+ " <button onclick=\"debug_call_msg(\'"+this.getId()+"\','in')\">Input</button>") ;
			}
			if(callOutput!=null && callOutputDT>0)
			{
				divsb.append(Convert.calcDateGapToNow(callOutputDT)+ " <button onclick=\"debug_call_msg(\'"+this.getId()+"\','out')\">Output</button>") ;
			}
			divsb.append("</div>") ;
			divblks.add(new DivBlk("call_inout",divsb.toString())) ;
		}
	}
	

	public JSONObject RT_getCallInput()
	{
		return this.callInput ;
	}
	
	public long RT_getCallInputDT()
	{
		return this.callOutputDT ;
	}
	
	public JSONObject RT_getCallOutput()
	{
		return this.callOutput ;
	}
	
	public long RT_getCallOutputDT()
	{
		return this.callInputDT ;
	}
}
