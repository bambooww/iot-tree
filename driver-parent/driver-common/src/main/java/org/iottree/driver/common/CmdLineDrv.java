package org.iottree.driver.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.driver.common.clh.ApcSmartUPS;

public class CmdLineDrv extends DevDriver
{
	CmdLineHandler handler = null ;
	
	long recvTO = 3000 ;
	
	public long getRecvTimeOut()
	{
		return recvTO ;
	}
	
	public CmdLineDrv asHandler(CmdLineHandler h)
	{
		this.handler = h ;
		return this ;
	}
	
	@Override
	protected boolean initDriver(StringBuilder failedr) throws Exception
	{
		if(!super.initDriver(failedr))
			return false;
		
		Object pv = this.getBelongToCh().getPropValue("cmd_line", "recv_to") ;
		if(pv!=null&&pv instanceof Number)
		{
			recvTO = ((Number)pv).longValue() ;
			if(recvTO<=0)
				recvTO = 3000 ;
		}
		
		return handler.init(this,failedr);
	}
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return true;
	}

	@Override
	public DevDriver copyMe()
	{
		CmdLineHandler h = this.handler.copyMe() ;
		CmdLineDrv drv = new CmdLineDrv();
		drv.asHandler(h) ;
		return drv ;
	}

	@Override
	public String getName()
	{
		return handler.getName();
	}

	@Override
	public String getTitle()
	{
		return handler.getTitle();// "Cmd Line Handler";
	}
	
	static List<DevDriver> SUB_DRVS = Arrays.asList(
			new CmdLineDrv().asHandler(new ApcSmartUPS())
			) ;
	
	protected List<DevDriver> supportMultiDrivers()
	{
		return SUB_DRVS ;
	}
	
	public boolean isConnPtToDev()
	{
		return true;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class ;
	}

	@Override
	public boolean supportDevFinder()
	{
		return false;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		return null ;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		PropGroup gp = null;
		
		gp = new PropGroup("cmd_line","Cmd Line Driver");
		//gp.addPropItem(new PropItem("conn_to","Connect Timeout(second)","",PValTP.vt_int,false,null,null,3));
		gp.addPropItem(new PropItem("recv_to","Receive Timeout(millisecond)","",PValTP.vt_int,false,null,null,3000));
		pgs.add(gp) ;
		
		
//		gp = new PropGroup(""+handler.getName(),handler.getTitle());
//		gp.addPropItem(new PropItem("en","Enable auto-demotion on failure","",PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false));
//		gp.addPropItem(new PropItem("dm_tryc","Demote after Retry times","",PValTP.vt_int,false,null,null,3));
//		gp.addPropItem(new PropItem("dm_ms","Demote for millisecond","",PValTP.vt_int,false,null,null,10000));
//		gp.addPropItem(new PropItem("dm_no_req","Discard request during demotion","",PValTP.vt_bool,false,new String[] {"Disabled","Enabled"},new Object[] {false,true},false));
//		pgs.add(gp) ;
		
		return pgs;
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
	
	private Thread recvTh = null ;
	private ConnPtStream connPtS = null ;
	private InputStream connInputS = null ;
	
	private Runnable recvRunner = new Runnable() {

		@Override
		public void run()
		{
			doRecv() ;
		}} ;
	
	private synchronized void startRecv()
	{
		if(recvTh!=null)
			return ;
		recvTh = new Thread(recvRunner) ;
		recvTh.start();
	}
	
	private synchronized void stopRecv()
	{
		if(recvTh==null)
			return ;
		recvTh.interrupt(); 
		recvTh= null ;
	}
	
	@Override
	protected void afterDriverRun() throws Exception
	{
		super.afterDriverRun();
		this.stopRecv();
	}
	
	private void checkReadStart(InputStream inputs,byte[] starts) throws IOException
	{
		if(starts==null||starts.length<=0)
			return ;
		
		int idx = 0 ;
		do
		{
			int c = inputs.read() ;
			int sv = starts[idx] & 0xFF ;
			if(sv==c)
			{
				idx++ ;
			}
			else
			{
				idx = 0 ;
			}
		}
		while(idx<starts.length) ;
	}
	
	private byte[] checkReadEnd(InputStream inputs,byte[] ends,int max_len) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		int idx = 0 ;
		do
		{
			//System.out.println("before read char") ;
			int c = inputs.read() ;
			//System.out.println("read char="+c) ;
			int sv = ends[idx] & 0xFF ;
			if(sv==c)
			{
				idx++ ;
			}
			else
			{
				idx = 0 ;
			}
			baos.write(c);
			if(baos.size()>=max_len)
				throw new IOException("recv length is reach "+max_len+" before end") ;
		}
		while(idx<ends.length) ;
		return baos.toByteArray() ;
	}
	
	
	private void doRecv()
	{
		try
		{
			InputStream inputs = connInputS ;
			String startstr = handler.getBeginStr() ;
			byte[] starts = null ;
			if(startstr!=null)
				starts = startstr.getBytes() ;
			String endstr = handler.getEndStr() ;
			byte[] ends = endstr.getBytes() ;
			int max_len = handler.getRecvMaxLen() ;
			while(recvTh!=null)
			{
				if(inputs.available()<=0)
				{
					try
					{
					Thread.sleep(1);
					}
					catch(Exception e) {}
					continue ;
				}
				checkReadStart(inputs,starts) ;
				
				if(inputs.available()<=0)
				{
					Thread.sleep(1);
					continue ;
				}
				
				byte[] ret = checkReadEnd(inputs,ends,max_len) ;
				handler.RT_onRecved(ret);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(log.isDebugEnabled())
				log.error("CmdLineDrv "+this.getName()+" doRecv Err",e);
		}
		finally
		{
			recvTh = null ;
		}
	}

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev)
	{
		this.connPtS = (ConnPtStream)cp ;
		this.connInputS = this.connPtS.getInputStream() ;

		startRecv() ;
		this.handler.RT_onConned((ConnPtStream)cp);
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev)
	{
		
		stopRecv();
		
		this.handler.RT_onDisconn((ConnPtStream)cp);
		if(this.connPtS==cp)
			this.connPtS = null ;
	}

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		this.handler.RT_runInLoop(this.connPtS);
		return true;
	}

	@Override
	public boolean RT_writeVal(UACh ch,UADev dev,UATag tag, DevAddr da, Object v)
	{
		//super.RT_writeValStr(dev, da, strv)
		
		return false;
	}

	@Override
	public boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags, DevAddr[] da, Object[] v)
	{
		return false;
	}
	
	@Override
	public void RT_fireDrvWarn(String msg)
	{
		super.RT_fireDrvWarn(msg);
		System.out.println("Warn: "+msg) ;
	}
}
