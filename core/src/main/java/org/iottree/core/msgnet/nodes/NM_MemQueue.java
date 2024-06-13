package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.util.RepeatTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 内存消息队列，
 * 
 * @author jason.zhu
 *
 */
public class NM_MemQueue  extends MNNodeMid implements IMNRunner
{
	public static enum Mode
	{
		normal(0), combined_out(1);//,latest_priority(2);

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
			Lan lan = Lan.getLangInPk(NM_MemQueue.class);
			return lan.g(this.name());
		}

		public static Mode valOfInt(int i)
		{
			switch (i)
			{
			case 0:
				return normal;
			case 1:
				return combined_out;
//			case 2:
//				return latest_priority;
			default:
				return null;
			}
		}
	}
	
	public static enum ExceedMaxH
	{
		issue_err(0), discard_old(1),discard_new(2);

		private final int val;

		ExceedMaxH(int v)
		{
			val = v;
		}

		public int getInt()
		{
			return val;
		}

		public String getTitle()
		{
			Lan lan = Lan.getLangInPk(NM_MemQueue.class);
			return lan.g(this.name());
		}

		public static ExceedMaxH valOfInt(int i)
		{
			switch (i)
			{
			case 0:
				return issue_err;
			case 1:
				return discard_old;
			case 2:
				return discard_new;
			default:
				return issue_err;
			}
		}
	}
	
	int queWarnLen = 100 ;
	int queMaxLen = 150 ;
	
	ExceedMaxH exceedMaxH = ExceedMaxH.issue_err ;
	
	Mode mode = Mode.normal ;
	
	int combinedOutMinLen = 1 ;
	
	/**
	 * &lt;=0 will be no limit
	 */
	int combinedOutMaxLen = -1 ;
	
	/**
	 * wait time before combined msg len reach min len,when timeout,msg will be combined and output
	 * 
	 *  &lt;=0 mean will wait until reach min len
	 */
	int combinedOutWaitTO = 3000 ;
	
	
	
	@Override
	public String getColor()
	{
		return "#f0a566";
	}
	
	@Override
	public String getIcon()
	{
		//return "\\uf386";
		return "\\uf141";
	}

	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 2;
	}
	
	@Override
	public String getOutColor(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
			return "red";
		return null ;
	}

//	@Override
	public String getTP()
	{
		return "mem_que";
	}

	@Override
	public String getTPTitle()
	{
		return g("mem_que");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.put("mode", this.mode.getInt()) ;
		jo.putOpt("warn_len", this.queWarnLen) ;
		jo.putOpt("max_len", this.queMaxLen) ;
		jo.putOpt("exd_max_h", this.exceedMaxH.getInt()) ;
		jo.putOpt("combined_min_len", this.combinedOutMinLen) ;
		jo.putOpt("combined_max_len", this.combinedOutMaxLen) ;
		jo.putOpt("combined_wait_to", this.combinedOutWaitTO) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.mode = Mode.valOfInt(jo.optInt("mode", 0)) ;
		if(this.mode==null)
			this.mode = Mode.normal ;
		
		this.queWarnLen = jo.optInt("warn_len",100) ;
		this.queMaxLen = jo.optInt("max_len",150) ;
		this.exceedMaxH = ExceedMaxH.valOfInt(jo.optInt("exd_max_h", 0)) ;
		
		this.combinedOutMinLen = jo.optInt("combined_min_len",1) ;
		this.combinedOutMaxLen = jo.optInt("combined_max_len",-1) ;
		this.combinedOutWaitTO = jo.optInt("combined_wait_to",3000) ;

	}
	
	// --------------
	
	private boolean bRun = false;
	
	private Thread procTh = null ;
	/**
	 * 
	 */
	private LinkedList<MNMsg> queMsgList = new LinkedList<>();
	
	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				while(bRun)
				{
					switch(mode)
					{
					case combined_out:
						procModeCombinedOut();
						break ;
//					case latest_priority:
//						procModeLastestPri();
//						break ;
					default:
						procModeNormal();
						break ;
					}
				}
			}
			finally
			{
				procTh = null ;
				bRun = false;
			}
		}
	};
	
	private void procModeNormal()
	{
		MNMsg qi = dequeue() ;
		if(qi==null)
			return ;
		//StringBuilder failedr = new StringBuilder() ;
		//synchronized(this)
		{
			try
			{
				this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, qi));
				remove_first(1) ;
			}
			catch(Throwable tt)
			{
				this.RT_DEBUG_ERR.fire("send_out","procModeNormal err:"+tt.getMessage(), tt);
			}
		}
	}
	
	private void procModeCombinedOut()
	{
		List<MNMsg> ms = dequeue(combinedOutMinLen,combinedOutMaxLen,this.combinedOutWaitTO) ;
		int n ;
		if(ms==null || (n = ms.size())<=0)
			return ;
		
		//synchronized(this)
		{
			JSONArray jarr = new JSONArray() ;
			for(MNMsg m:ms)
			{
				jarr.put(m.getPayload()) ;
			}
			MNMsg newmsg = new MNMsg().asPayload(jarr) ;
			try
			{
				this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, newmsg));
				remove_first(n) ;
			}
			catch(Throwable tt)
			{
				this.RT_DEBUG_ERR.fire("send_out","procModeNormal err:"+tt.getMessage(), tt);
			}
		}
	}
	
	private void procModeLastestPri()
	{
		//throw new RuntimeException("not impl") ;
		this.RT_DEBUG_ERR.fire("send_out","not impl");
	}

	private synchronized void enqueue(MNMsg o)
	{
		queMsgList.addLast(o);		
		notify();
	}
	
	/**
	 * 实现队列查找--能够避免retry的delay不会造成不被处理的情况
	 * @return
	 */
	private MNMsg peekQueue()
	{
		if (queMsgList.isEmpty())
		{
			return null ;
		}
		else
		{
			return queMsgList.getFirst() ;
		}
	}

	private synchronized MNMsg dequeue()
	{
		MNMsg qi = peekQueue();
		if (qi==null)
		{
			try
			{
				wait();
			}
			catch (InterruptedException ie)
			{}
		}
		
		qi = peekQueue();
		if(qi==null)
			return null ;
		return qi ;
	}
	
	private synchronized List<MNMsg> dequeue(int min_num,int max_num,long timeout)
	{
		if(min_num<=0)
			throw new IllegalArgumentException("invalid dequeue num "+min_num) ;
		int len = getQueLen() ;
		
		if(len<min_num)
		{
			long st = System.currentTimeMillis() ;
			do
			{
				try
				{
					wait(1);
				}
				catch (InterruptedException ie)
				{}
				len = getQueLen() ;
				if(timeout>0 && System.currentTimeMillis()-st>=timeout)
				{//timeout
					break ;
				}
			}while(len<min_num) ;
		}
		
		if(len<=0)
			return null ;

		if(max_num>0 && len>max_num)
			len = max_num ;
		
		return this.queMsgList.subList(0, len);
	}
	
	private synchronized void remove_first(int n)
	{
		if(n<=0) return ;
		if(n>queMsgList.size())
			throw new IllegalArgumentException("n is bigger than size") ;
		
		for(int i = 0 ; i < n ; i ++)
			queMsgList.removeFirst();//.remove(msg) ;
	}

	private synchronized void emptyQueue()
	{
		queMsgList.clear();
	}
	
	private int getQueLen()
	{
		return queMsgList.size() ;
	}
	
		
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;
		divsb.append("<div class=\"rt_blk\">"+this.mode.getTitle()) ;
		divsb.append(" queue length="+this.getQueLen()) ;
		divsb.append("") ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk("memque",divsb.toString())) ;
		
		super.RT_renderDiv(divblks);
	}
	
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		int len = this.getQueLen() ;
		if(this.queMaxLen>0 && len>=this.queMaxLen)
		{
			RT_sendExceed(len) ;
			
			switch(exceedMaxH)
			{
			case discard_new:
				this.RT_DEBUG_WARN.fire("discard_new","MaxLen("+queMaxLen+") discard new warn: queue length ="+len,msg.toJO().toString());
				return null; //
			case discard_old:
				this.remove_first(1);
				break ;
			default:
				this.RT_DEBUG_ERR.fire("max_len","MaxLen("+queMaxLen+") error: queue length ="+len);
				//make ahead node to be error
				throw new RuntimeException("MemQueue Node "+this.getTitle()+" queue length="+len) ;
			}
		}
		
		this.enqueue(msg);
		
		if(this.queWarnLen>0 && len>=this.queWarnLen)
		{
			this.RT_DEBUG_WARN.fire("que_len_warn","queue length ="+len);
			RT_sendWarn(len) ;
		}
		else
			this.RT_DEBUG_WARN.clear("que_len_warn");
		return null;//
	}
	
	private transient long RT_lastWarnDT = -1 ;
	
	private transient long RT_lastErrDT = -1 ;
	
	private void RT_sendWarn(int len)
	{
		if(System.currentTimeMillis()-RT_lastWarnDT<5000)
			return;
		
		try
		{
			JSONObject tmpjo = new JSONObject() ;
			tmpjo.put("lvl", "warn") ;
			tmpjo.put("que_len", len) ;
			MNMsg m = new MNMsg().asPayload(tmpjo) ;
			this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(1, m));
		}
		finally
		{
			RT_lastWarnDT = System.currentTimeMillis() ;
		}
	}
	
	private void RT_sendExceed(int len)
	{
		if(System.currentTimeMillis()-RT_lastErrDT<5000)
			return;
		
		try
		{
			JSONObject tmpjo = new JSONObject() ;
			tmpjo.put("lvl", "exceed") ;
			tmpjo.put("que_len", len) ;
			MNMsg m = new MNMsg().asPayload(tmpjo) ;
			this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(1, m));
		}
		finally
		{
			RT_lastErrDT = System.currentTimeMillis() ;
		}
	}

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if (bRun)
			return true;

		bRun = true;
		procTh = new Thread(runner);
		procTh.start();
		return true;
	}

	@Override
	public synchronized void RT_stop()
	{
		Thread th = procTh;
		if (th != null)
			th.interrupt();
		bRun = false;
		procTh = null;
	}

	@Override
	public boolean RT_isRunning()
	{
		return bRun;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
	

	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
			g("warn_or_exceed") ;
		return null ;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		if(idx==0)
			return null;
		if(idx==1)
		{
			if(System.currentTimeMillis()-RT_lastErrDT<3000)
				return "red";
			
			if(System.currentTimeMillis()-RT_lastWarnDT<3000)
				return "yellow";
			return null ;
		}
		return null ;
	}

}
