package org.iottree.driver.gb.szy;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;

/**
 * 水资源数据帧格式
 * 
 * @author jason.zhu
 *
 */
public class SZYFrame extends SZYMsg
{
	//FC_UP上行帧功能码
	public static final short FC_CMD = 0 ;  //下行发送确认 上行确认
	public static final short FC_RAIN = 1 ; //雨量
	public static final short FC_WATER_LVL = 2 ;
	public static final short FC_FLOW = 3 ;
	public static final short FC_FLOW_RATE = 4 ; //流速
	public static final short FC_GATE_POS = 5 ; //闸位
	public static final short FC_POWER = 6 ; //功率
	public static final short FC_AIR_PRESSURE = 7 ; //气压
	public static final short FC_WIND_SPEED = 8 ; //风速
	public static final short FC_WATER_TEMP = 9 ; //水温
	public static final short FC_WATER_QUALITY = 10 ; //水质
	public static final short FC_SOIL_MOISTURE = 11 ; //土壤含水率
	public static final short FC_EVAPORATION = 12 ; //蒸发量
	public static final short FC_ALARM_STATUE = 13 ; //报警或状态
	public static final short FC_COMPRE = 14 ; //综合参数-统计雨量
	public static final short FC_WATER_PRESSURE = 15 ; //水压
	
	public static enum FC
	{
		cmd(0,"CMD","CMD")
		,rain(1,"RN","RAIN")
		,water_lvl(2,"WL","WATER_LVL")
		,flow(3,"FL","FLOW")
		,flow_rate(4,"FR","FLOW Rate")
		,gate_pos(5,"GP","Gate Pos")
		
		;
		
		private final int val ;
		private final String mk ;
		private final String title ;
		
		FC(int v,String mk,String tt)
		{
			this.val = v ;
			this.mk = mk ;
			this.title = tt ;
		}
		
		public int getValue()
		{
			return val ;
		}
		
		public String getMark()
		{
			return this.mk ;
		}
		
		public String getTitle()
		{
			return this.title ;
		}
		
		public static FC valueOf(int v)
		{
			for(FC f:FC.values())
				if(f.val==v)
					return f ;
			return null ;
		}
		
		public static FC fromMk(String mk)
		{
			for(FC f:FC.values())
				if(f.mk.equals(mk))
					return f ;
			return null ;
		}
	}
	
	public static class UserData
	{
		byte[] userBS = null ;
		
		short afn ;
		
		UserData(byte[] ud)
		{
			this.userBS = ud ;
			this.afn = (short)(ud[0] & 0xFF) ;
		}
	}
	
	//
	public static abstract class UDTerminalUp extends UserData
	{
		DFTermAlertST termAlertST = null ;
		
		byte[] df = null ;
		
		public UDTerminalUp(byte[] ud)
		{
			super(ud) ;
			
			int dlen = ud.length ;
			if(dlen<2)
				throw new IllegalArgumentException("not terminal up data") ;
			
			if((ud[0]&0xFF)!=0xC0)
				throw new IllegalArgumentException("not terminal up data") ;
			
			if(dlen>4)
			{//最后4字节是报警/终端状态
				byte[] bs = new byte[4] ;
				System.arraycopy(ud, dlen-4, bs, 0, 4);
				termAlertST = new DFTermAlertST(bs) ;
			}
			this.df = new byte[dlen-5] ;
			System.arraycopy(ud, 1, this.df, 0, dlen-5);
		}
		
		protected abstract boolean parseDataField() ;  
	}
	
	public static class UDTermUpFlow extends SZYFrame.UDTerminalUp
	{
		List<Float> vals = null ;
		
		public UDTermUpFlow(byte[] ud)
		{
			super(ud) ;
		}
		
		protected boolean parseDataField()
		{
			byte[] bs = this.df ;
			int n = bs.length / 5 ;
			if(n<=0)
				return false;
			vals = new ArrayList<>(n) ;
			for(int i = 0 ; i < n ; i ++)
			{
				float v = transByte5BCDToFloat(bs,i*5) ;
				vals.add(v);
			}
			return true ;
		}
		
		float transByte5BCDToFloat(byte[] bs,int offset)
		{
			StringBuilder sb = new StringBuilder() ;
			int b = bs[offset+4] & 0xFF;
			if((b & 0xF0) == 0xF0)
				sb.append('-') ;
			sb.append(b&0x0F) ;
			
			for(int i = 3 ; i >=0 ; i --)
			{
				b = bs[offset+i] & 0xFF;
				sb.append((b>>4)&0x0F).append(b&0x0F) ;
			}
			
			return Float.parseFloat(sb.toString())/1000 ;
		}
		
		public String toString()
		{
			return "flow "+Convert.combineWith(vals, ' ') ;
		}
	}
	
	short c ; //控制域
	
	boolean dir = true ; //D7 true 1-终端发出上行  false 0 - 中心发出下行
	
	boolean div = false;  //D6
	
	short fcb = 3 ; //D5-D4 帧计数位
	
	FC fc = null ; // D3-D0 功能码
	
	byte[] a = new byte[5] ; //地址域
	
	
	//short afn = 0 ; //应用功能码 AFN   //6.4.1 0xC0  遥测终端自报数据 数据格式表158 ack 159 数据格式表164
	
	
	UserData userData = null ; 
	
	public SZYFrame(byte[] data)
	{
		super(data) ;
	}
	
	public boolean getDir()
	{
		return this.dir ;
	}
	
	public boolean isDiv()
	{
		return this.div ;
	}
	
	public short getFCB()
	{
		return this.fcb ;
	}
	
	public FC getFuncCode()
	{
		return fc ;
	}
	
	public byte[] getAddr()
	{
		return a ;
	}
	
	public String getAddrA1()
	{
		return null ;
	}
	
	public String getAddrA2()
	{
		return null ;
	}

	boolean parseData()
	{
		byte[] bs = this.getData() ;
		if(bs.length<6)
			return false;
		
		this.c = (short)(bs[0] &0xFF);
		this.dir = (this.c & 0x80)>0 ;
		this.div = (this.c & 0x40)>0 ;
		this.fcb = (short)((this.c & 0x30) >> 4) ;
		this.fc =FC.valueOf(this.c & 0x0F) ;
		System.arraycopy(bs, 1, a, 0, 5);
		byte[] ud = new byte[bs.length-6] ;
		System.arraycopy(bs, 6, ud, 0, bs.length-6) ;
		this.userData = parseUserData(ud) ;
		return this.userData!=null;
	}
	
	private UserData parseUserData(byte[] ud)
	{
		UDTermUpFlow ret = null ;
		int afn = ud[0] & 0xFF;
		switch(afn)
		{
		case 0xC0:
			ret = new UDTermUpFlow(ud) ;
			break;
		default:
			return null ;
		}
		
		if(ret.parseDataField())
			return ret ;
		else
			return null ;
	}
	
	public UserData getUserData()
	{
		return this.userData ;
	}
}


