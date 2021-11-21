package org.iottree.driver.common.modbus.sniffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.iottree.driver.common.modbus.ModbusCmd;
import org.iottree.driver.common.modbus.ModbusCmdRead;

/**
 * a modbus line or channel
 * 
 * @author jason.zhu
 *
 */
public class SnifferRTUCh
{
	HashMap<String,SnifferCmd> id2scmd = new HashMap<>() ;
	
	
	private static final int PST_NOR = 0 ;
	
	private static final int PST_REQ = 1 ;
	
	private static final int PST_RESP = 2 ;
	
	private int pst = PST_NOR ;
	
	private SnifferCmd curCmd = null ;
	
	private SnifferBufferFix sniBuf = new SnifferBufferFix(1024) ;
	
	public SnifferRTUCh()
	{
		
	}
	
	/**
	 * state machine to parse modbus data
	 * 
	 * @param bs
	 * @throws Exception 
	 */
	public void onSniffedData(byte[] bs,ISnifferCallback cb) //throws Exception
	{
		if(bs==null||bs.length<=0)
			return ;
		
		//System.out.println("on sniffed data len="+bs.length) ;
		try
		{
			sniBuf.addData(bs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		int buflen ;//= sniBuf.getBufLen() ;
		//System.out.println("  buf len="+buflen) ;
		
		while((buflen = sniBuf.getBufLen())>0)
		{
			//baos.
			switch(pst)
			{
			case PST_NOR:
				do
				{
					if((buflen = sniBuf.getBufLen())<8)
						return ;
					byte[] tmpbs = new byte[8] ;
					sniBuf.peekData(tmpbs, 0, 8) ;
					curCmd = parseReqCmd(tmpbs);
					if(curCmd==null)
					{
						sniBuf.readNextChar() ;
						continue ;
					}
					sniBuf.skipLen(8) ;
					pst = PST_REQ ;
					break ;
				}while(curCmd==null) ;
				break ;
			case PST_REQ:
				//find resp
				if(buflen<3)
				{
					return ;
				}
				byte[] tmpbs = new byte[3] ;
				sniBuf.peekData(tmpbs, 0, 3) ;
				if(tmpbs[0]!=this.curCmd.getDevId()||
						tmpbs[1]!=this.curCmd.getFC())
				{
					pst = PST_NOR ;
					break ;
				}
				
				int resplen = this.curCmd.getRespLen() ;
				if((0xff & tmpbs[2])!=resplen-5)
				{
					pst = PST_NOR ;
					break ;
				}
				
				do
				{
					if(buflen<resplen)
						return ;
					tmpbs = new byte[resplen] ;
					sniBuf.peekData(tmpbs, 0, resplen) ;
					if(curCmd.parseResp(tmpbs))
					{
						if(cb!=null)
							cb.onSnifferCmd(curCmd);
						onSnifferCmdFound(curCmd) ;
						sniBuf.skipLen(resplen) ;
					}
					pst = PST_NOR ;
					break ;
				}while(curCmd==null) ;
			case PST_RESP:
			}
		}
	}
	
	
	private SnifferCmd parseReqCmd(byte[] bs)
	{
		//check fc
		int[] pl = new int[1] ;
		ModbusCmd mc = ModbusCmd.parseRequest(bs,pl) ;
		if(mc==null)
			return null ;
		if(!(mc instanceof ModbusCmdRead))
			return  null;
		
		ModbusCmdRead mcr = (ModbusCmdRead)mc ;
		int resplen = mc.calRespLenRTU() ;
		if(resplen<=0)
			return null ;
		return new SnifferCmd(mcr) ;
	}
	
	private void onSnifferCmdFound(SnifferCmd sc)
	{
		id2scmd.put(sc.getUniqueId(), sc) ;
		//System.out.println(sc.getUniqueId()) ;
	}
	
	public SnifferCmd getSnifferCmd(String id)
	{
		return id2scmd.get(id) ;
	}
}
