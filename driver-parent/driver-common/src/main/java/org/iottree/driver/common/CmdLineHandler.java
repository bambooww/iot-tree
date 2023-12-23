package org.iottree.driver.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import org.iottree.core.ConnException;
import org.iottree.core.ConnPt;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

/**
 * a handler will be one driver
 * 
 * @author jason.zhu
 *
 */
public abstract class CmdLineHandler
{
	public static interface IRecvCallback<T>
	{
		public void onRecved(boolean bsucc, T ret,String error) ;
	}
	
	protected static ILogger log = LoggerManager.getLogger(CmdLineHandler.class) ;
	
	protected CmdLineDrv belongTo  = null ;
	
	public abstract String getName() ;
	
	public abstract String getTitle() ;
	
	public abstract String getDesc() ;
	
	protected String getBeginStr()
	{
		return null ;
	}
	
	protected String getEndStr()
	{
		return "\r\n";
	}
	
	protected abstract CmdLineHandler copyMe() ;
	
	protected abstract int getRecvMaxLen() ;
	
	
	protected boolean init(CmdLineDrv cld,StringBuilder sb) throws Exception
	{
		this.belongTo = cld ;
		
		return true ;
	}
	
	//public abstract void RT_init() throws Exception ;
	
	protected ConnPtStream conn = null ;
	
	protected void RT_onConned(ConnPtStream cpt)// throws Exception
	{
		this.conn = cpt ;
	}
	
	protected void RT_onDisconn(ConnPtStream cpt) //throws Exception
	{
		if(conn==cpt)
			conn = null ;
	}
	
	protected final boolean sendStr(String str) throws UnsupportedEncodingException, IOException
	{
		if(conn==null)
			throw new ConnException("conn is null") ;
		
		OutputStream outputs = this.conn.getOutputStream();
		
		outputs.write(str.getBytes("UTF-8"));
		outputs.flush();
		return true ;
	}
	
	protected final void clearInputBuf() throws IOException
	{
		if(conn==null)
			return;
		
		InputStream inputs = this.conn.getInputStream();
		int n = inputs.available();
		if(n<=0)
			return ;
		inputs.skip(n) ;
	}
	
	private IRecvCallback<String> curSynRet = null ;
	private String recvRet = null ;
	
	protected synchronized boolean sendRecvSyn(String str,IRecvCallback<String> ret) throws Exception
	{
		try
		{
			curSynRet = ret ;
			recvRet = null ;
			clearInputBuf() ;
			if(!this.sendStr(str))
				return false;
			this.wait(this.belongTo.getRecvTimeOut());
			//curSynRet = null ;
			if(recvRet==null)
			{
				ret.onRecved(false, null, "recv time out");
				return false;
			}
			ret.onRecved(true, recvRet, null) ;
			return true ;
		}
		finally
		{
			curSynRet = null ;
		}
	}
	
	synchronized final void RT_onRecved(byte[] bs)
	{
		String ss = new String(bs) ;
		
		if(curSynRet!=null)
		{
			curSynRet = null; //not interfere next recv	
			this.recvRet = ss ;
			this.notifyAll();
			return ;
		}
		
		RT_onRecved(ss) ;
	}
	
	public abstract void RT_runInLoop(ConnPtStream cpt) throws Exception;
	
	public abstract void RT_onRecved(String cmd) ;
}
