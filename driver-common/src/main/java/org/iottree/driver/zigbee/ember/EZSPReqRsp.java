package org.iottree.driver.zigbee.ember;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public abstract class EZSPReqRsp
{
	private String uid = IdCreator.newSeqId() ;
	
	protected EZSPCmd reqCmd ;
	
	protected EZSPRsp rspRsp = null;
	
	protected boolean hasFlw ;
	
	protected EZSPRsp flwRsp = null; //follow msg after rsp msg
	
	long reqEndDT = System.currentTimeMillis() ;// call by ZStackManager.RT_synSendMsg
	
	private long toMs ;
	
	protected transient boolean debugOut = false;
	
	protected final EmberManager emberMgr ;
	/**
	 * 
	 * @param req_msg
	 * @param has_follow
	 * @param timeout 115200 256字节，至少需要等待25ms，所以timeout
	 */
	public EZSPReqRsp(EmberManager em)
	{
		this.emberMgr = em ;
	}
	
	public EZSPReqRsp asReq(EZSPCmd req_cmd,boolean has_follow,long timeout)
	{
		this.reqCmd = req_cmd ;
		this.hasFlw = has_follow ;
		this.toMs = timeout>0?timeout:100 ;
		return this ;
	}
	
	public void setDebugOut(boolean b)
	{
		this.debugOut = b ;
	}
	
	public String getUID()
	{
		return this.uid ;
	}
	
	public EZSPReqRsp asTimeout(long timeout)
	{
		this.toMs = timeout>0?timeout:100 ;
		return this;
	}
	
	public EZSPCmd getSReqMsg()
	{
		return this.reqCmd ;
	}
	
	public EZSPRsp getRsp()
	{
		return this.rspRsp ;
	}
	
	public boolean isHasFollow()
	{
		return this.hasFlw ;
	}
	
	public EZSPRsp getFlwRsp()
	{
		return  this.flwRsp ;
	}
	
	private long srspCost = -1 ;
	private long flwCost = -1 ;
	private String errInf = null ;
	
	public String getErrInf()
	{
		return errInf ;
	}
	
	public long getSRspCost()
	{
		return this.srspCost ;
	}
	
	public long getFlwCost()
	{
		return this.flwCost ;
	}
	/**
	 * -1 - error 0 - unknown waiting 1 - end succ
	 * @return
	 */
	int RT_checkEnd()
	{
		if(Convert.isNotNullEmpty(this.errInf))
			return -1 ;
		boolean bget_recv = this.hasFlw?(this.flwRsp!=null):(this.rspRsp!=null) ;
		if(bget_recv)
			return 1 ;
		if(System.currentTimeMillis()-reqEndDT>this.toMs)
		{
			this.errInf = "time out" ;
			return -1 ; //error
		}
		return 0 ; // keep waiting
	}
	
	boolean RT_onRspRecv(EZSPRsp m_recv)
	{
		if(rspRsp==null)
		{
			StringBuilder failedr = new StringBuilder() ;
			if(!RT_chkRsp(m_recv,failedr))
			{
				errInf = failedr.toString() ;
				return false;
			}
			rspRsp = m_recv ;
			srspCost = System.currentTimeMillis() - this.reqEndDT ;
			this.reqEndDT = System.currentTimeMillis() ; //为follow重新计算timeout
			return true;
		}
		
		if(this.hasFlw)
		{
			StringBuilder failedr = new StringBuilder() ;
			if(!RT_chkFlwRsp(m_recv,failedr))
			{
				//errInf = failedr.toString() ;  // may other msg input
				return false;
			}
			
			this.flwRsp = m_recv ;
			flwCost = System.currentTimeMillis() - this.reqEndDT ;
			return true ;
		}
		return false;
	}
	
	protected abstract boolean RT_chkRsp(EZSPRsp m_recv,StringBuilder failedr) ;
	
	protected abstract boolean RT_chkFlwRsp(EZSPRsp m_recv,StringBuilder failedr) ;
	
	protected abstract JSONObject toRetJO() ;
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject().putOpt("timeout", this.toMs).putOpt("srsp_cost", this.srspCost)
				.put("has_flw", this.hasFlw);
		if(isHasFollow())
			r.putOpt("flw_cost", this.flwCost) ;
		r.putOpt("err_inf", this.errInf) ;
		r.putOpt("ret", this.toRetJO()) ;
		return r ;
	}
	
	public JSONObject toDetailJO()
	{
		JSONObject r = toJO();
		JSONObject pkjo = new JSONObject() ;
		r.put("req_rsp", pkjo) ;
		pkjo.put("sreq", this.reqCmd.toString()) ;
		pkjo.put("sreq_pk", this.reqCmd.toEZSPPackOrErr()) ;
		if(rspRsp!=null)
		{
			pkjo.put("srsp", this.rspRsp.toString()) ;
			pkjo.put("srsp_pk", this.rspRsp.toEZSPPackOrErr()) ;
		}
		if(this.hasFlw && this.flwRsp!=null)
		{
			pkjo.put("flw", this.flwRsp.toString()) ;
			pkjo.put("flw_pk", this.flwRsp.toEZSPPackOrErr()) ;
		}
		return r ;
	}
	
	@Override
	public String toString()
	{
		return toJO().toString() ;
	}
}
