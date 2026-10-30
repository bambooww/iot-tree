package org.iottree.driver.zigbee.zstack;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public abstract class MsgReqRsp
{
	private String uid = IdCreator.newSeqId() ;
	
	protected Msg sreqMsg ;
	
	protected MsgRecv srspMsg = null;
	
	protected boolean hasFollow ;
	
	protected MsgRecv followMsg = null; //follow msg after rsp msg
	
	long reqEndDT = System.currentTimeMillis() ;// call by ZStackManager.RT_synSendMsg
	
	private long toMs ;
	
	protected transient boolean debugOut = false;
	/**
	 * 
	 * @param req_msg
	 * @param has_follow
	 * @param timeout 115200 256字节，至少需要等待25ms，所以timeout
	 */
	public MsgReqRsp()
	{
		
	}
	
	public MsgReqRsp asReq(Msg req_msg,boolean has_follow,long timeout)
	{
		this.sreqMsg = req_msg ;
		this.hasFollow = has_follow ;
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
	
	public MsgReqRsp asTimeout(long timeout)
	{
		this.toMs = timeout>0?timeout:100 ;
		return this;
	}
	
	public Msg getSReqMsg()
	{
		return this.sreqMsg ;
	}
	
	public MsgRecv getSRspMsg()
	{
		return this.srspMsg ;
	}
	
	public boolean isHasFollow()
	{
		return this.hasFollow ;
	}
	
	public MsgRecv getFollowMsg()
	{
		return  this.followMsg ;
	}
	
	private long srspCost = -1 ;
	private long followCost = -1 ;
	private String errInf = null ;
	
	public String getErrInf()
	{
		return errInf ;
	}
	
	public long getSRspCost()
	{
		return this.srspCost ;
	}
	
	public long getFollowCost()
	{
		return this.followCost ;
	}
	/**
	 * -1 - error 0 - unknown waiting 1 - end succ
	 * @return
	 */
	int RT_checkEnd()
	{
		if(Convert.isNotNullEmpty(this.errInf))
			return -1 ;
		boolean bget_recv = this.hasFollow?(this.followMsg!=null):(this.srspMsg!=null) ;
		if(bget_recv)
			return 1 ;
		if(System.currentTimeMillis()-reqEndDT>this.toMs)
		{
			this.errInf = "time out" ;
			return -1 ; //error
		}
		return 0 ; // keep waiting
	}
	
	boolean RT_onMsgRecv(MsgRecv m_recv)
	{
		if(srspMsg==null)
		{
			StringBuilder failedr = new StringBuilder() ;
			if(!RT_chkSRspMsg(m_recv,failedr))
			{
				errInf = failedr.toString() ;
				return false;
			}
			srspMsg = m_recv ;
			srspCost = System.currentTimeMillis() - this.reqEndDT ;
			this.reqEndDT = System.currentTimeMillis() ; //为follow重新计算timeout
			return true;
		}
		
		if(this.hasFollow)
		{
			StringBuilder failedr = new StringBuilder() ;
			if(!RT_chkFollowMsg(m_recv,failedr))
			{
				//errInf = failedr.toString() ;  // may other msg input
				return false;
			}
			
			this.followMsg = m_recv ;
			followCost = System.currentTimeMillis() - this.reqEndDT ;
			return true ;
		}
		return false;
	}
	
	protected abstract boolean RT_chkSRspMsg(MsgRecv m_recv,StringBuilder failedr) ;
	
	protected abstract boolean RT_chkFollowMsg(MsgRecv m_recv,StringBuilder failedr) ;
	
	protected abstract JSONObject toRetJO() ;
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject().putOpt("timeout", this.toMs).putOpt("srsp_cost", this.srspCost)
				.put("has_flw", this.hasFollow);
		if(isHasFollow())
			r.putOpt("flw_cost", this.followCost) ;
		r.putOpt("err_inf", this.errInf) ;
		r.putOpt("ret", this.toRetJO()) ;
		return r ;
	}
	
	public JSONObject toDetailJO()
	{
		JSONObject r = toJO();
		JSONObject pkjo = new JSONObject() ;
		r.put("req_rsp", pkjo) ;
		pkjo.put("sreq", this.sreqMsg.toString()) ;
		pkjo.put("sreq_pk", Convert.byteArray2HexStr(this.sreqMsg.pack(),"-")) ;
		if(srspMsg!=null)
		{
			pkjo.put("srsp", this.srspMsg.toString()) ;
			pkjo.put("srsp_pk", Convert.byteArray2HexStr(this.srspMsg.pack(),"-")) ;
		}
		if(this.hasFollow && this.followMsg!=null)
		{
			pkjo.put("flw", this.followMsg.toString()) ;
			pkjo.put("flw_pk", Convert.byteArray2HexStr(this.followMsg.pack(),"-")) ;
		}
		return r ;
	}
	
	@Override
	public String toString()
	{
		return toJO().toString() ;
	}
}
