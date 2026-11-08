package org.iottree.driver.zigbee.zstack;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zspec.ZSWaiter;

public class MsgWaiter<R extends MsgRecv> extends ZSWaiter<MsgRecv,R>
{
	Msg.CmdTp matchCmdTp = null ;
	
	Msg.SubSys matchSubSys = null ;
	
	Integer cmd1 = null ; // may be ZDOClusterId
	
	Integer target_addr = null ;
	
	String target_ieee = null ;
	
	public MsgWaiter(long to_ms)
	{
		super(to_ms);
	}
	
	public MsgWaiter<R> asMatchCmdTp(Msg.CmdTp cmd_tp)
	{
		this.matchCmdTp = cmd_tp ;
		return this ;
	}
	
	public MsgWaiter<R> asMatchSubSys(Msg.SubSys sub_sys)
	{
		this.matchSubSys = sub_sys ;
		return this ;
	}
	
	public MsgWaiter<R> asMatchCmd1(Integer cmd1)
	{
		this.cmd1 = cmd1 ;
		return this ;
	}
	
	public MsgWaiter<R> asMatchTarget(Object addr)
	{
		if(addr==null) return this;
		if(addr instanceof Integer)
		{
			this.target_addr = (Integer)addr ;
			return this;
		}
		if(addr instanceof String)
		{
			this.target_ieee = (String)addr ;
			if(Convert.isNullOrEmpty(this.target_ieee))
				this.target_addr = null ;
			return this;
		}
		return this ;
	}

	@Override
	public boolean RT_chkRsp(MsgRecv fd, MsgRecv rsp)
	{
		if(this.cmd1!=null && this.cmd1!=rsp.getCmd1())
			return false;
		if(this.matchCmdTp!=null && rsp.getCmdTp()!=this.matchCmdTp)
			return false;
		if(this.matchSubSys!=null && rsp.getSubSys()!=this.matchSubSys)
			return false;
		
		return false;
	}

}
