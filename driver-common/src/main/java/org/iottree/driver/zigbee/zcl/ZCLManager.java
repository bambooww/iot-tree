package org.iottree.driver.zigbee.zcl;

import java.util.HashMap;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.zcl.cmd_frm.*;

public class ZCLManager
{
	protected static ILogger log = LoggerManager.getLogger(ZCLManager.class);
	
	private static HashMap<Integer,ZCLFrame> CMD2FRAME = new HashMap<>() ;
	private static void reg_frame(ZCLFrame f)
	{
		int cmd0 = f.getCmdID() ;
		CMD2FRAME.put(cmd0,f) ;
	}
	
	static
	{
		reg_frame(new ReadAttr()) ;
		reg_frame(new ReadAttrRsp()) ;
		reg_frame(new WriteAttr()) ;
		
		reg_frame(new ConfigReportRsp()) ;
		reg_frame(new ReportAttr()) ;
		reg_frame(new DefaultRsp()) ;
	}
	
	static ZCLFrame createNewGenCmdIns(int cmd_id)
	{
		ZCLFrame f = CMD2FRAME.get(cmd_id) ;
		if(f==null)
			return null ;
		try
		{
			return f.getClass().getConstructor().newInstance() ;
		}
		catch(Exception ee)
		{
			if(log.isDebugEnabled())
				log.debug(ee);
			return null ;
		}
	}
	
	ZCLFrame getRegisteredFrame(int cmdid)
	{
		return CMD2FRAME.get(cmdid) ;
	}
	
	/**
	 * state machine to read zcl frame
	 * 
	 * @param inputs
	 * @return
	 */
	public static ZCLFrame unpackedFrame(byte[] bs,StringBuilder failedr) //throws Exception
	{
		int frm_ctrl = bs[0] & 0xFF ;
		boolean b_gen_cmd = ZCLFrame.checkFrameGenCmd(frm_ctrl);
		boolean b_cluster_cmd = ZCLFrame.checkFrameClusterCmd(frm_ctrl);
		boolean has_fac_code = ZCLFrame.checkHasFacSpec(frm_ctrl) ;
		int cmd_id = bs[2] & 0xFF;
		if(has_fac_code)
			cmd_id = bs[4] & 0xFF ;
		ZCLFrame ret = null;
		if(b_gen_cmd)
			ret = createNewGenCmdIns(cmd_id) ;
		else if(b_cluster_cmd)
			ret = new ZCLFrmClusterCmd(cmd_id,true) ; //dir_s2c=true
		else
		{
			failedr.append("unknown frame control") ;
			return null ;
		}
		
		if(ret==null)
		{
			failedr.append("no zcl frame instance created cmdid="+cmd_id) ;
			return null ;
		}
		if(!ret.unpackFrame(bs, 0, failedr))
		{
			failedr.append(" cmdid="+cmd_id) ;
			return null;
		}
		return ret ;
	}
}
