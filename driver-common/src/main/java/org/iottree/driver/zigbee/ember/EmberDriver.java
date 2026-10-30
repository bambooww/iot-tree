package org.iottree.driver.zigbee.ember;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.zspec.ZSCoor;
import org.iottree.driver.zigbee.zspec.ZSDriver;

/**
 * EZSP - ASH
 * 
 * @author jason.zhu
 *
 */
public class EmberDriver extends ZSDriver
{
	protected static ILogger log = LoggerManager.getLogger(EmberDriver.class);

	long readTimeout = 3000 ;
	
	private EmberManager mgr ;
	
	public EmberDriver()
	{
		super() ;
		mgr = new EmberManager(this) ;
		log.setCurrentLogLevel(ILogger.LOG_LEVEL_DEBUG);
	}

	public EmberManager getManager()
	{
		return mgr ;
	}
	
	@Override
	public ZSCoor getZSCoor()
	{
		return this.mgr.getCoor() ;
	}

	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	public DevDriver copyMe()
	{
		return new EmberDriver();
	}

	@Override
	public String getName()
	{
		return "zigbee_ember";
	}

	@Override
	public String getTitle()
	{
		return "Zigbee Ember(ZBDongle-E)";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class;
	}
	
	@Override
	public boolean supportDevFinder()
	{
		return true;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		return null;
	}

	@Override
	public DevAddr getSupportAddr()
	{
		return null;
	}
	
	
	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)cp;
		//System.out.println("on conn ready="+cpt) ;
		this.mgr.RT_start();
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		//System.out.println("on conn broken="+cp) ;
		this.mgr.RT_stop();
	}
	
	@Override
	protected void afterDriverRun() throws Exception
	{
		//System.out.println("afterDriverRun") ;
		this.mgr.RT_stop();
	}
	
	private transient boolean RT_bSendRST = false;
	
	public boolean RT_sendReset(boolean b_cancel,StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null || !cpt.isConnReady())
		{
			failedr.append("no connection") ;
			return false;
		}
		
		OutputStream outputo = cpt.getOutputStream() ;
		if(outputo==null)
			return false;
		if(b_cancel)
		{
			outputo.write(0x1A); //CANCEL,ASH规定：在发送 RST 之前发送 CANCEL 字符以清空接收状态机。
			outputo.flush();
			RT_bSendRST = true ;
		}
		ASHFrmRst frm_rst = new ASHFrmRst() ;
		boolean b = RT_sendFrame(frm_rst, failedr);
		
		return b ;
	}
	
	/**
	 * 通过外部接口输出数据帧——自动做转义
	 * 
	 * Flag Byte 7E is sent as 7D 5E
		XON Byte 11 is sent as 7D 31
		XOFF Byte 13 is sent as 7D 33
		Substitute Byte 18 is sent as 7D 38
		Cancel Byte 1A is sent as 7D 3A
		Escape Byte 7D is sent as 7D 5D

	 * 里面需要做转义
	 原始字节	发送时变成	说明
		0x7E	    0x7D 0x5E	Flag Byte 转义
		0x7D	    0x7D 0x5D	Escape Byte 自身转义
		0x11	    0x7D 0x31	XON 转义
		0x13	    0x7D 0x33	XOFF 转义
		0x18     0x7D 0x38   Substitute Byte
		0x1A     0x7D 3A   Cancel Byte
	 * @param outputs
	 * @throws IOException 
	 */
	public synchronized boolean RT_sendFrame(ASHFrame m,StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null || !cpt.isConnReady())
		{
			if(failedr!=null)
				failedr.append("no connection") ;
			return false;
		}
		
		OutputStream conn_out = cpt.getOutputStream() ;
		if(conn_out==null)
		{
			if(failedr!=null)
				failedr.append("no output stream") ;
			return false;
		}
		boolean debug = false;
		if(m instanceof ASHFrmRst)
		{//set seq num 0-7-0
			conn_out.write(0x1A);
		}
		
		byte[] bs = m.pack() ;
		OutputStream outputo = conn_out ;
		ByteArrayOutputStream baos = null ;
		if(debug)
		{
			outputo = baos = new ByteArrayOutputStream() ;
		}
		//reserved bytes and byte stuffing
		int flen = bs.length -1 ;
		for(int i = 0 ; i < flen ; i ++)
		{
			int c = bs[i] & 0xFF ;
			switch(c)
			{
			case 0x7E:
			case 0x7D:
			case 0x11:
			case 0x13:
			case 0x18:
			case 0x1A:
				outputo.write(0x7D); //转义
				outputo.write(c^0x20);
				break ;
			default:
				outputo.write(c);
			}
		}
		outputo.write(ASHFrame.END_FLAG);
		
		if(debug)
		{
			byte[] out_bs = baos.toByteArray() ;
			System.out.println("send>>"+Convert.byteArray2HexStr(out_bs, "-")+"\r\n\t"+m) ;
			conn_out.write(out_bs);
		}
		conn_out.flush();
		return true;
	}
	
	
	@Override
	protected boolean RT_useLoopNoWait()
	{
		return true;
	}
	
//	@Override
//	protected void RT_beforeDriverRunLoop() throws Exception
//	{
//		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
//		if(cpt!=null && !cpt.isConnReady())
//		{
//			StringBuilder failedr = new StringBuilder() ;
//			if(!this.RT_sendReset(true,failedr))
//			{
//				if(log.isDebugEnabled())
//					log.debug(failedr.toString());
//			}
//			return ;
//		}
//		this.RT_bStartNow = true; //设定在onConnReady中运行
//		return;
//	}
	
	private transient int respST = 0 ;
	//private transient ASHFrame respFrm = null ;
	private transient int respLen = 0;
	private transient byte[] respBS = new byte[132] ; //128+4

	/**
	 * 接收自动进行stuffing
	 */
	@Override
	protected boolean RT_runInLoopNoWait(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null || !cpt.isConnReady())
		{
			respST = 0 ;
			return true ;
		}
		
		int c = 0 ;
		PushbackInputStream inputs = cpt.getInputStream() ;
		if(inputs==null)
		{
			respST = 0 ;
			return true ;
		}
		
		while(inputs.available()>0)
		{
			c=inputs.read() ;
			
			switch(respST)
			{
			case 0:
				if(c==ASHFrame.END_FLAG)
					continue ;//ignore continues end flag
				if(c==0xFF)
				{//当前不在帧内，按唤醒/待处理信号处理
					continue;
				}
				inputs.unread(c);
//				this.respBS[0] = (byte)c ;
				this.respLen = 0 ;
				this.respST = 1 ;
				break;
			case 1: //帧内解析
				switch(c)
				{
				case ASHFrame.END_FLAG://end
					this.respBS[respLen] = (byte)c ;
					this.respLen ++ ;
					StringBuilder fff = new StringBuilder() ;
					ASHFrame new_frm = ASHFrame.parseFrom(this.mgr,this.respBS,this.respLen,false,fff) ;
					if(new_frm==null)
					{
						mgr.RT_onUnknownResp(fff+" @ "+Convert.byteArray2HexStr(respBS,0,respLen, "-")) ;
					}
					else
					{
						mgr.RT_onRecvFromDriver(new_frm) ;
					}
					this.respST = 0 ;
					break ;
				case 0x7D:
					this.respST = 2 ;
					break;
				case 0x1A: //丢弃当前帧缓存
					this.respLen = 0 ;
					break;
				case 0x18: //丢弃当前帧缓冲,进入“丢弃到下一个 0x7E”状态
					this.respST = 0 ;
					break ;
				case 0x11: //仅做流控，不写入帧缓冲
				case 0x13:
					break ;
				default:
					this.respBS[respLen] = (byte)c ;
					respLen ++ ;
					if(respLen==respBS.length)
					{
						this.respST = 0 ; //超出132，丢弃
					}
					break ;
				}
				break;
			case 2: // 转义
				this.respBS[respLen] = (byte)(c^0x20) ;
				respLen ++ ;
				this.respST = 1 ;
				break ;
			default:
				respST = 0 ;
				break;
			}
		}
		
		sleep(1);
		
		return true;
	}

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		return false;
	}

	@Override
	public boolean RT_writeVal(UACh ch, UADev dev, UATag tag, DevAddr da, Object v)
	{
		return false;
	}

	@Override
	public boolean RT_writeVals(UACh ch, UADev dev, UATag[] tags, DevAddr[] da, Object[] v)
	{
		return false;
	}
}
