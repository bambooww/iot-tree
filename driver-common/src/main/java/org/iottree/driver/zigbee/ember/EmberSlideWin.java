package org.iottree.driver.zigbee.ember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.iottree.driver.zigbee.ember.ASHFrmData;
import org.iottree.driver.zigbee.ember.util.ASHCounters;

/**
 * 滑动串口。用来支持host可以一次发送多个帧，并通过ack进行向右滑动 Go-Back-N Sliding Window。
 * 
 * ref:
 * https://docs.silabs.com/zigbee/latest/uart-gateway-protocol-reference/05-connected-state
 * 
 * @author jason.zhu
 * @param <T>
 */
public class EmberSlideWin
{
	private static final int WINDOW_SIZE = 3;

	private static final int MAX_SEQ = 8;

	private static final int RETRANSMISSION_TIMEOUT = 3000;

	private static final int MAX_RETRY = 3;

	/**
	 * host本身发送的数据帧序列号
	 */
	private int sendFrmN = -1; // current frame num (for send data)

//	/**
//	 * host收到ncp的对自己发送帧的ack，用来确定ncp接收情况
//	 */
//	private int sendAckN = 0; // cur ack n ,通过recv frame 的frame num+1更新

	/**
	 * host收到ncp的数据帧序列，host发送ack数据基于此+1
	 */
	private int expectedRxSeq = 0; // recv frame data frm num-for send ack frame

//	/**
//	 * 最近接收的几个数据帧
//	 */
//	private LinkedList<Integer> recvedFrms = new LinkedList<>();

	/**
	 * 数据帧滑动窗口队列
	 */
	private Map<Integer, ASHFrmData> sendWindow = new ConcurrentHashMap<>();

	/**
	 * 需要立刻发送的帧队列
	 */
	private LinkedList<ASHFrame> sendingQues = new LinkedList<>();

	// private int winLen ;

	EmberDriver drv;

	EmberManager mgr;
	
	private ASHCounters counters = new ASHCounters() ;

	public EmberSlideWin(EmberManager mgr) // ,int win_len,int que_max_len)
	{
		// if(win_len<=0||win_len>7)
		// throw new IllegalArgumentException("invalid win len") ;
		this.mgr = mgr ;
		this.drv = mgr.driver;
		// this.winLen = win_len ;
	}

	public int getQueLen()
	{
		return this.sendWindow.size();
	}

	private synchronized int get_next_send_frm_n()
	{
		this.sendFrmN++;
		this.sendFrmN = this.sendFrmN % 8;
		return this.sendFrmN;
	}

	public void reset()
	{
		this.sendFrmN = -1;
		this.expectedRxSeq = 0; 
		this.sendWindow = new ConcurrentHashMap<>();
		this.sendingQues = new LinkedList<>() ;
	}

	public synchronized void pushFrmData(ASHFrmData fd)
	{
		int next_f = this.get_next_send_frm_n();
		fd.setFrmNum(next_f);
		fd.setAckNum(this.expectedRxSeq);
		sendWindow.put(next_f, fd);
		queSendingFrm(fd);
		
		fd.rt_retry_c=0 ;
		fd.rt_senddt = System.currentTimeMillis() ;
		this.notifyAll();
	}
	
	private void queSendingAck()
	{
		ASHFrmAck fack = ASHFrmAck.createAck(false, this.expectedRxSeq) ;
		queSendingFrm(fack) ;
	}
	
	private void queSendingNak()
	{
		ASHFrmNak f = ASHFrmNak.createNak(false, this.expectedRxSeq) ;
		queSendingFrm(f) ;
	}
	
	private void queSendingRetransmit(int seq)
	{
		ASHFrmData fd = this.sendWindow.get(seq) ;
		if(fd==null)
			return ;
		//frmNum 不变
		fd.rt_retry_c ++ ;
		fd.rt_senddt = System.currentTimeMillis() ;
		fd.setReTx(true); //reTx=1
		fd.setAckNum(this.expectedRxSeq); //ackNum更新为最新值
		System.out.println( this.sendWindow.size()+" que retrans="+fd) ;
		queSendingFrm(fd) ;
	}

	public synchronized void queSendingFrm(ASHFrame f)
	{
		this.sendingQues.add(f);
		//this.notifyAll();
	}

	private synchronized ASHFrame dequeSendingFrm()
	{
		if (this.sendingQues.size() <= 0)
			return null;
		return this.sendingQues.removeFirst();
	}

	/**
	 * 在外部发送线程，循环调用的函数，用来
	 * 
	 * @return
	 * @throws Exception
	 */
	private void RT_runSendInLoop() throws Exception
	{
		// 发送现有内容
		ASHFrame f = null;
		while ((f = this.dequeSendingFrm()) != null)
		{
			this.drv.RT_sendFrame(f, null);
		}
		
		//滑动窗口超时检测
		checkSendWinTimeout() ;

		EmberManager.sleep(5) ;
		// 判断是否需要重新传
		//this.checkWinDataQue();
	}

	private void checkSendWinTimeout()
	{
		long now = System.currentTimeMillis();
		ArrayList<ASHFrmData> re_fds = new ArrayList<>() ;
	    for (ASHFrmData entry: sendWindow.values())
	    {
	        if (now - entry.rt_senddt > RETRANSMISSION_TIMEOUT)
	        {
	            if (entry.rt_retry_c >= MAX_RETRY)
	            {
	                disconnect();
	                return;
	            } 
	        }
	    }
	    
	    for(ASHFrmData df:re_fds)
	    {
	    	this.queSendingRetransmit(df.getFrmNum());
	    }
	}
//	private synchronized void checkWinDataQue()
//	{
//		// 检查滑动窗口队列
//		int s = sendWindow.size();
//		if (s <= 0)
//			return;
//		int n = 0;
//		StringBuilder failedr = new StringBuilder();
//		for (; n < winLen && n < s; n++)
//		{
//			ASHFrmData fd = sendWindow.get(n);
//			if (fd.rt_acked)
//				continue;
//			if (fd.rt_sentNum > 0)
//			{// check timeout
//				if (System.currentTimeMillis() - fd.rt_sentDT < 2000)
//					continue;
//			}
//
//			if (this.drv.RT_sendFrame(fd, failedr))
//			{
//				fd.rt_sentDT = System.currentTimeMillis();
//				fd.rt_sentNum++;
//			}
//		}
//
//		this.slideWin();
//		return;
//	}

	/**
	 * 
	 * @param frm
	 * @return
	 * @throws Exception 
	 */
	void RT_onRecvFrm(ASHFrame frm) throws Exception
	{
		if(frm instanceof ASHFrmAck)
		{
			ASHFrmAck fack = (ASHFrmAck)frm ;
			int ack_n = fack.getAckNum() ;
			//this.sendAckN =
			this.on_ack_num(ack_n);
			return ;
		}
		
		if(frm instanceof ASHFrmData)
		{
			ASHFrmData fd = (ASHFrmData)frm ;
			int ack_n = fd.getAckNum() ;
			this.on_ack_num(ack_n);

			if(fd.isReTx())
			{
				on_handle_retransmit(fd) ;
				return ;
			}
			
			int fn = fd.getFrmNum() ;
			if (fn == this.expectedRxSeq)
			{
		        expectedRxSeq = (expectedRxSeq + 1) % MAX_SEQ;
		        queSendingAck();
		     // process app
		        this.on_handle_data(fd) ;

		    } else {
		    	queSendingNak();
		    }
		}
		
		if(frm instanceof ASHFrmNak)
		{// ncp请求重传
			ASHFrmNak fnak = (ASHFrmNak)frm ;
			on_nak_num(fnak.getAckNum()) ;
			return ;
		}
		
		if(frm instanceof ASHFrmRstAck)
		{
			ASHFrmRstAck rstack = (ASHFrmRstAck)frm ;
			expectedRxSeq = 0 ;
			queSendingAck();
			this.mgr.RT_WIN_onFrameRstAck(rstack);
			//this.getCoor().RT_onRstAck(rstack);
			//System.out.println("reset ok ,run state = "+this.hostST+"  "+rstack) ;
			
			return ;
		}
		
		
		if(frm instanceof ASHFrmError)
		{
			on_handle_err();
			return ;
		}
		
		return;
	}

	/**
	 * 当收到一个ack时
	 * 
	 * @param ack_n
	 */
	private synchronized void on_ack_num(int ack_n)
	{
		Iterator<Integer> it = sendWindow.keySet().iterator();
		while (it.hasNext())
		{
			int seq = it.next();
			if (isSeqBefore(seq, ack_n))
			{
				it.remove();
			}
		}
	}
	
	private void on_handle_data(ASHFrmData fd)
	{
		this.mgr.RT_WIN_onFrameData(fd);
	}

	private synchronized void on_nak_num(int ack_n)
	{
		List<Integer> seqs =
	            new ArrayList<>(sendWindow.keySet());

	    Collections.sort(seqs);

	    for (Integer seq : seqs) {

	        if (seq == ack_n || !isSeqBefore(seq, ack_n))
	        {
	            queSendingRetransmit(seq);
	        }
	    }
	}
	
	private synchronized void on_handle_retransmit(ASHFrmData fd)
	{
		if (fd.getFrmNum() == ((expectedRxSeq - 1 + MAX_SEQ) % MAX_SEQ))
		{//已经接收，只需要ack
	        queSendingAck();
	        return;
	    }
	    on_handle_data(fd) ;
	}
	
	/**
	 * 协议同步已经失败
	 */
	private synchronized void on_handle_err()
	{
		this.sendWindow.clear();
		this.sendFrmN = 0 ;
		this.expectedRxSeq = 0 ;
		
		this.mgr.RT_reset(true); // reconnect()
	}
	
	private void disconnect()
	{
		on_handle_err();
	}
	
	private Thread thread = null ;
	
	private Runnable runner = new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				while(thread!=null)
				{
					RT_runSendInLoop();
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
			finally
			{
				thread = null ;
			}
		}
	};
	

	
	public synchronized void RT_start()
	{
		if(this.thread!=null)
			return ;
		
		this.thread = new Thread(runner) ;
		this.thread.start();
	}
	
	public synchronized void RT_stop()
	{
		if(this.thread==null)
			return ;
		try
		{
			this.thread.interrupt(); 
		}
		finally
		{
			this.thread = null ;
		}
	}
	
	public boolean RT_isRunning()
	{
		return this.thread!=null ;
	}
	
	List<Integer> readAndClearCounters()
	{
		List<Integer> r = this.counters.readCouters() ;
		this.counters = new ASHCounters() ;
		return r ;
	}
	/**
	 * 判断seq是否在ack之前
	 * 
	 * @param seq
	 * @param ackNum
	 * @return
	 */
	private static boolean isSeqBefore(int seq, int ack)
	{
		int diff = (ack - seq + MAX_SEQ) % MAX_SEQ;
		return diff > 0 && diff <= WINDOW_SIZE;
	}
}
