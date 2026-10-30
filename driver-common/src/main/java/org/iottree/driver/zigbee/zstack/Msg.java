package org.iottree.driver.zigbee.zstack;

import java.util.Iterator;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public abstract class Msg
{
	public static final int SOF = 0xFE ;
	
	//
	public static final byte CMD0_TYPE_POLL = (byte)0x00 ;
	public static final byte CMD0_TYPE_SREQ = (byte)0x20 ;
	public static final byte CMD0_TYPE_AREQ = (byte)0x40 ;
	public static final byte CMD0_TYPE_SRSP = (byte)0x60 ;
	
	public static final byte CMD0_SUBSYS_00 =0x00 ; // reserved
	public static final byte CMD0_SUBSYS_SYS = (byte)0x01 ; // SYS interface
	public static final byte CMD0_SUBSYS_MAC = (byte)0x02 ; // MAC interface
	public static final byte CMD0_SUBSYS_NWK = (byte)0x03 ; // NWK interface 0x03
	public static final byte CMD0_SUBSYS_AF = (byte)0x04 ; // AF interface 0x04
	public static final byte CMD0_SUBSYS_ZDO = (byte)0x05 ; // ZDO interface 0x05
	public static final byte CMD0_SUBSYS_SAPI = (byte)0x06 ; // SAPI interface 0x06
	public static final byte CMD0_SUBSYS_UTIL = (byte)0x07 ; // UTIL interface 0x07
	public static final byte CMD0_SUBSYS_DEBUG = (byte)0x08 ; // DEBUG interface 0x08
	public static final byte CMD0_SUBSYS_APP_INTERFACE = (byte)0x09 ; // APP interface 0x09
	public static final byte CMD0_SUBSYS_APPCNF = (byte)0x0F; // APP config 0x0F
	public static final byte CMD0_SUBSYS_GP = (byte)0x15 ; // GreenPower 0x15
	
	
	public static final byte CMD1_SYS_PING = 0x01 ;
	public static final byte CMD1_SYS_VERSION = 0x02 ;
	
	//int len ; //1byte 0-250
	
	protected int cmd0 ; // cmd0 bit:7-5 subsystem bit 4-0
	
	//protected byte cmd0_subsys ; // cmd0 bit 4-0
	
	protected int cmd1 ; //1byte
	
	public Msg()
	{}
	
	public Msg(int cmd0,int cmd1)
	{
		this.cmd0 = cmd0 ;
		this.cmd1 = cmd1 ;
	}
	
	public Msg(byte cmd0_type,byte cmd0_sub,int cmd1)
	{
		this.cmd0 = (cmd0_type & 0xE0) + (cmd0_sub&0x1F) ;
		this.cmd1 = cmd1 ;
	}
	
	public Msg asCmd(int cmd0,int cmd1)
	{
		this.cmd0 = cmd0 ;
		this.cmd1 = cmd1 ;
		return this ;
	}
	
	
	public byte getCmd0Type()
	{
		return (byte)(this.cmd0 & 0xE0) ; // 1110 0000
	}
	
	public String getCmd0TypeT()
	{
		switch(getCmd0Type())
		{
		case CMD0_TYPE_POLL:
			return "POLL";
		case CMD0_TYPE_SREQ:
			return "SREQ";
		case CMD0_TYPE_AREQ:
			return "AREQ";
		case CMD0_TYPE_SRSP:
			return "SRSP";
		default:
			return "tp" ;
		}
	}
	
	public byte getCmd0Subsystem()
	{
		return  (byte)(this.cmd0 & 0x1F) ;
	}
	
	public String getCmd0SubsystemT()
	{
		switch(getCmd0Subsystem())
		{
		case CMD0_SUBSYS_00:
			return "reserved" ;
		case CMD0_SUBSYS_SYS:
			return "SYS";
		case CMD0_SUBSYS_MAC:
			return "MAC";
		case CMD0_SUBSYS_NWK:
			return "NWK";
		case CMD0_SUBSYS_AF:
			return "AF";
		case CMD0_SUBSYS_ZDO:
			return "ZDO";
		case CMD0_SUBSYS_SAPI:
			return "SAPI";
		case CMD0_SUBSYS_UTIL:
			return "UTIL";
		case CMD0_SUBSYS_DEBUG:
			return "DEBUG";
		case CMD0_SUBSYS_APP_INTERFACE:
			return "APP_INTERFACE";
		case CMD0_SUBSYS_APPCNF:
			return "APP_CONFIG";
		case CMD0_SUBSYS_GP:
			return "GreenPower";
		default:
			return "sub";
		}
	}
	
	public int getCmd0()
	{
		return this.cmd0;
	}
	
	public int getCmd1()
	{
		return this.cmd1 ;
	}
	
	
	
	public abstract String getTpName() ;
	
	public abstract byte[] getData() ;
	
	
	public byte[] pack()
	{
		byte[] dd = this.getData() ;
		int dlen = dd!=null?dd.length:0 ;
		byte[] ret = new byte[dlen+5] ;
		ret[0] = (byte)SOF;
		ret[1] = (byte)(dlen & 0xFF) ; //len
		ret[2] = (byte)this.cmd0;//cmd0
		ret[3] = (byte)this.cmd1; //cmd1
		if(dlen>0)
			System.arraycopy(dd, 0, ret, 4, dlen);
		ret[dlen+4] = calcFCS(ret,ret.length) ;
		return ret ;
	}
	
	public boolean unpack(byte[] bs,StringBuilder failedr)
	{
		if(bs==null||bs.length<5)
		{
			failedr.append("len<5") ;
			return false;
		}
		if((bs[0] &0xFF)!=SOF)
		{
			failedr.append("SOF err") ;
			return false;
		}
		int len = (int)(bs[1] & 0xFF) ;
		int pk_len = len+5 ;
		if(pk_len>bs.length)
		{
			failedr.append("len err") ;
			return false;
		}
		if(bs[pk_len-1]!=calcFCS(bs,pk_len))
		{
			failedr.append("FCS err") ;
			return false;
		}
		
		this.cmd0 = bs[2] & 0xFF ;
		this.cmd1 = bs[3] & 0xFF ;
		
		if(!checkCmd())
		{
			failedr.append("check cmd err") ;
			return false;
		}
		
		if(len>0)
		{
			byte[] data = new byte[len] ;
			System.arraycopy(bs, 4, data, 0, len);
			
			if(!parseData(data,failedr))
				return false;
		}
		return true;
	}
	
	/**
	 * override by response msg
	 * @return
	 */
	protected boolean checkCmd()
	{
		return true;
	}
	
	protected abstract boolean parseData(byte[] data,StringBuilder failedr);
	
	@Override
	public String toString()
	{
		int dlen = 0 ;
		byte[] d = this.getData() ;
		if(d!=null)
			dlen = d.length ;
		return "["+this.getCmd0TypeT()+" "+this.getCmd0SubsystemT()+"][0x"+String.format("%02X", this.getCmd0())+",0x"+String.format("%02X", this.getCmd1())+"] "+dlen +" ("+this.getTpName()+")";
	}
	
	public String getPackHexStr()
	{
		return Convert.byteArray2HexStr(this.pack(), "-") ;
	}
	
	static byte calcFCS(byte[] bs,int len)
	{
        byte ret = 0;
        for (int i = 1; i < len - 1; i++) {
        	ret = (byte) (ret ^ bs[i]);
        }
        return ret;
    }
	
	protected String transJO2PropStr(JSONObject jo)
	{
		Iterator<String> ks = jo.keys() ;
		String r = "" ;
		while(ks.hasNext())
		{
			String k = ks.next() ;
			r += " "+k+"="+jo.get(k) ;
		}
		return r ;
	}
}
