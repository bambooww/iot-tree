package org.iottree.core.station;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * platform station cmd
 * 
 * @author jason.zhu
 *
 */
public abstract class PSCmd
{
	protected List<String> params = null ;
	protected byte[] cmdData = null ;
	
	public abstract String getCmd() ;
	
	public List<String> getParams()
	{
		return params ;
	}
	
	public String getParamByIdx(int idx)
	{
		if(this.params==null || idx>=this.params.size())
			return null ;
		return this.params.get(idx) ;
	}
	
	public byte[] getCmdData()
	{
		return this.cmdData ;
	}
	
	protected boolean asParamsAndData(List<String> pms,byte[] data_buf,int offset)
	{
		this.params = pms ;
		if(data_buf!=null&&offset<data_buf.length)
		{
			this.cmdData = new byte[data_buf.length-offset] ;
			System.arraycopy(data_buf, offset, this.cmdData, 0, cmdData.length);
		}
		return true ;
	}
	
	protected PSCmd asParams(List<String> pms)
	{
		this.params = pms ;
		return this ;
	}
	
	protected PSCmd asCmdDataJO(JSONObject jo)
	{
		try
		{
			cmdData = jo.toString().getBytes("utf-8") ;
			return this ;
		}
		catch(Exception eee)
		{
			eee.printStackTrace();
			return null ;
		}
	}
	
	protected PSCmd asCmdData(byte[] bs)
	{
		this.cmdData = bs ;
		return this ;
	}
	
	private JSONObject cmdDataJO=  null ;
	
	public JSONObject getCmdDataJO() throws UnsupportedEncodingException
	{
		if(cmdDataJO!=null)
			return cmdDataJO ;
		if(this.cmdData==null||cmdData.length<=0)
			return null ;
		String txt = new String(this.cmdData,"UTF-8") ;
		return cmdDataJO = new JSONObject(txt) ;
	}
	
	public byte[] packTo()
	{
		try
		{
			ByteArrayOutputStream boas = new ByteArrayOutputStream() ;
			boas.write(this.getCmd().getBytes());
			List<String> ss = this.getParams() ;
			if(ss!=null)
			{
				for(String s:ss)
				{
					boas.write(" ".getBytes());
					boas.write(s.getBytes());
				}
			}
			boas.write("\r\n".getBytes());
			byte[] bs = this.getCmdData() ;
			if(bs!=null)
				boas.write(bs);
			
			return boas.toByteArray() ;
		}
		catch(Exception eee)
		{
			eee.printStackTrace();
			return null ;
		}
	}
	/**
	 * 
	 * @param bs
	 * @return
	 */
	public static PSCmd parseFrom(byte[] bs)
	{
		if(bs==null||bs.length<=0)
			return null ;
		
		int i = 0 ;
		for(i = 0 ; i < bs.length ; i ++)
		{
			if(bs[i]=='\n')
			{
				i ++ ;
				break ;
			}
		}
		
		String ss = new String(bs,0,i).trim() ;
		List<String> sss = Convert.splitStrWith(ss, " ") ;
		String cmd = sss.remove(0) ;
		PSCmd pscmd = null ;
		switch(cmd)
		{
		case PSCmdPrjRtData.CMD:
			pscmd = new PSCmdPrjRtData() ;
			break ;
		case PSCmdPrjStartStop.CMD:
			pscmd = new PSCmdPrjStartStop() ;
			break ;
		case PSCmdStationST.CMD:
			pscmd = new PSCmdStationST();
			break ;
		case PSCmdPlatformST.CMD:
			pscmd = new PSCmdPlatformST() ;
			break ;
		case PSCmdPrjUpdate.CMD:
			pscmd = new PSCmdPrjUpdate() ;
			break ;
		case PSCmdPrjUpTrigger.CMD:
			pscmd = new PSCmdPrjUpTrigger() ;
			break ;
		case PSCmdReboot.CMD:
			pscmd = new PSCmdReboot() ;
			break ;
		case PSCmdDirSyn.CMD:
			pscmd = new PSCmdDirSyn() ;
			break ;
		case PSCmdDirSynAck.CMD:
			pscmd = new PSCmdDirSynAck() ;
			break ;
		case PSCmdPrjSynPM.CMD:
			pscmd = new PSCmdPrjSynPM() ;
			break ;
		default:
			return null ;
		}
		
		boolean b  = pscmd.asParamsAndData(sss,bs,i) ;
		if(b)
			return pscmd ;
		else
			return null ;
	}
	
	
	// RT -
	
	public void RT_onRecvedInPlatform(PlatInsWSServer.SessionItem si,PStation ps) throws Exception
	{}
	
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{}
	
	@Override
	public String toString()
	{
		String ret = getCmd() ;
		if(params!=null&&params.size()>0)
			ret += " "+ Convert.combineStrWith(params, " ") ;
		if(this.cmdData!=null)
			ret += " [data_len="+this.cmdData.length+"]" ;
		return ret ;
	}
}
