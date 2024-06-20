package org.iottree.core.msgnet.modules;

import java.util.LinkedList;
import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

/**
 * 内存队列，
 * @author jason.zhu
 *
 */
public class MemQueue_NM extends MNNodeMid
{
	/**
	 * 
	 */
	LinkedList<MNMsg> queMsgList = new LinkedList<>();
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "mem_que_in_multi";
	}
	
	@Override
	public String getColor()
	{
		return "#f0a566";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf141";
	}

	@Override
	public String getTPTitle()
	{
		return g("mem_que_in_multi");
	}

	protected boolean supportCxtVars()
	{
		return false;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;
		divsb.append("<div class=\"rt_blk\">") ;
		divsb.append(" queue length="+this.queMsgList.size()) ;
		divsb.append("") ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk("mq_inmulti",divsb.toString())) ;
		
		super.RT_renderDiv(divblks);
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		MemMultiQueue mmq = (MemMultiQueue)this.getOwnRelatedModule();
		mmq.RT_onQueMsgIn(this,msg) ;
		return null;
	}

}
