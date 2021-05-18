package org.iottree.driver.common.modbus.slave;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal;
import org.w3c.dom.Element;


/**
 * slave��
 * @author jasonzhu
 *
 */
public abstract class MSlaveDataProvider
{
	public static interface SlaveData
	{
		public byte[] toModbusBytes();
		
		/**
		 * �����ȡ��ֵ����provider�����õ�������
		 * 1����regidx֮��regidx+regnum֮ǰ
		 * @param regidx
		 * @param regnum
		 * @param acqidx
		 */
		public void updateUsingData(int regidx,int regnum,int acqidx) ;
	}
	
	public static interface SlaveDataWord
	{
		public short[] getInt16UsingDatas();
	}
	
	public static class BoolDatas implements SlaveData
	{
		public boolean[] datas = null ;
		
		boolean[] usingDatas = null ;
		
		
		public boolean[] getBoolUsingDatas()
		{
			return usingDatas ;
		}
		
		public void updateUsingData(int regidx,int regnum,int acqidx)
		{
			if(datas==null)
				return ;
			
			if(acqidx>=datas.length)
				return ;
			
			int dlen = datas.length-acqidx ;
			if(dlen>regnum)
				dlen = regnum;
			boolean[] bs = new boolean[dlen] ;
			System.arraycopy(datas,acqidx, bs, 0, dlen) ;
			usingDatas = bs ;
		}
		
		public byte[] toModbusBytes()
		{
			return null ;
		}
	}
	
	public static class Int16Datas implements SlaveData,SlaveDataWord
	{
		public short[] datas = null ;
		
		short[] usingDatas = null ;
		
		public short[] getInt16Datas()
		{
			return datas ;
		}
		
		public short[] getInt16UsingDatas()
		{
			return usingDatas ;
		}
		
		public void updateUsingData(int regidx,int regnum,int acqidx)
		{
			if(datas==null)
				return ;
			
			if(acqidx>=datas.length)
				return ;
			
			int dlen = datas.length-acqidx ;
			if(dlen>regnum)
				dlen = regnum;
			short[] bs = new short[dlen] ;
			System.arraycopy(datas,acqidx, bs, 0, dlen) ;
			
			usingDatas = bs ;
		}
		
		public byte[] toModbusBytes()
		{
			return null ;
		}
	}
	
	public static class Int32Datas implements SlaveData,SlaveDataWord
	{
		public int[] datas = null ;
		
		short[] usingDatas = null ;
		
		public void updateUsingData(int regidx,int regnum,int acqidx)
		{
			if(datas==null)
				return ;
			
			if(acqidx>=datas.length)
				return ;
			
			int dlen = datas.length-acqidx ;
			if(dlen>regnum)
				dlen = regnum;
			int[] bs = new int[dlen] ;
			System.arraycopy(datas,acqidx, bs, 0, dlen) ;
			
			short[] r = new short[dlen*2] ;
			for(int i=0;i<dlen;i++)
			{
				r[i*2] = (short)(0xFFFF & (bs[i]>>16)) ;
				r[i*2+1] = (short)(0xFFFF & bs[i]) ;
			}
			usingDatas = r ;
		}
		
		public short[] getInt16UsingDatas()
		{
			return usingDatas ;
		}
		
		public byte[] toModbusBytes()
		{
			return null ;
		}
	}
	
	public static class Int64Datas implements SlaveData,SlaveDataWord
	{
		public long[] datas = null ;
		
		short[] usingDatas = null ;
		
		public void updateUsingData(int regidx,int regnum,int acqidx)
		{
			if(datas==null)
				return ;
			
			if(acqidx>=datas.length)
				return ;
			
			int dlen = datas.length-acqidx ;
			if(dlen>regnum)
				dlen = regnum;
			long[] bs = new long[dlen] ;
			System.arraycopy(datas,acqidx, bs, 0, dlen) ;
			
			short[] r = new short[dlen*4] ;
			
			for(int i=0;i<dlen;i++)
			{
				r[i*2] = (short)(0xFFFF & (bs[i]>>48)) ;
				r[i*2+1] = (short)(0xFFFF & (bs[i]>>32)) ;
				r[i*2+2] = (short)(0xFFFF & (bs[i]>>16)) ;
				r[i*2+3] = (short)(0xFFFF & bs[i]) ;
			}
			usingDatas = r ;
		}
		
		public short[] getInt16UsingDatas()
		{
			return usingDatas ;
		}
		
		public byte[] toModbusBytes()
		{
			return null ;
		}
	}
	
//floatתbyte[]
  static byte[] floatToByte(float v)
  {
          ByteBuffer bb = ByteBuffer.allocate(4);
          byte[] ret = new byte [4];
          FloatBuffer fb = bb.asFloatBuffer();
          fb.put(v);
          bb.get(ret);
          return ret;
  }

//  byte[]תfloat
  static float byteToFloat(byte[] v)
  {
          ByteBuffer bb = ByteBuffer.wrap(v);
          FloatBuffer fb = bb.asFloatBuffer();
          return fb.get();
  }
  
//floatתbyte[]
  static byte[] doubleToByte(double v)
  {
          ByteBuffer bb = ByteBuffer.allocate(8);
          byte[] ret = new byte [8];
          DoubleBuffer fb = bb.asDoubleBuffer();
          fb.put(v);
          bb.get(ret);
          return ret;
  }

//  byte[]תfloat
  static double byteToDouble(byte[] v)
  {
          ByteBuffer bb = ByteBuffer.wrap(v);
          DoubleBuffer fb = bb.asDoubleBuffer();
          return fb.get();
  }
	
	public static class FloatDatas implements SlaveData,SlaveDataWord
	{
		public float[] datas = null ;
		
		short[] usingDatas = null ;
		
		public void updateUsingData(int regidx,int regnum,int acqidx)
		{
			if(datas==null)
				return ;
			
			if(acqidx>=datas.length)
				return ;
			
			int dlen = datas.length-acqidx ;
			if(dlen>regnum)
				dlen = regnum;
			float[] bs = new float[dlen] ;
			System.arraycopy(datas,acqidx, bs, 0, dlen) ;
			
			short[] r = new short[dlen*2] ;
			
			for(int i=0;i<dlen;i++)
			{
				byte[] fbs = floatToByte(bs[i]) ;
				short t ;
				t = (short)(0xFF & (short)fbs[0]) ;
				r[i*2+1] = (short)(t<<8) ;
				r[i*2+1] |= (short)(0xFF & (short)fbs[1]) ;
				
				r[i*2] = (short)((0xFF & (short)fbs[2])<<8) ;
				r[i*2] |= (short)(0xFF & (short)fbs[3]) ;
			}
			
			usingDatas = r ;
		}
		
		public short[] getInt16UsingDatas()
		{
			return usingDatas ;
		}
		
		public byte[] toModbusBytes()
		{
			return null ;
		}
	}
	
	
	public static class DoubleDatas implements SlaveData,SlaveDataWord
	{
		public double[] datas = null ;
		short[] usingDatas = null ;
		
		public void updateUsingData(int regidx,int regnum,int acqidx)
		{
			if(datas==null)
				return ;
			
			if(acqidx>=datas.length)
				return ;
			
			int dlen = datas.length-acqidx ;
			if(dlen>regnum)
				dlen = regnum;
			double[] bs = new double[dlen] ;
			System.arraycopy(datas,acqidx, bs, 0, dlen) ;
			
			short[] r = new short[dlen*4] ;
			
			for(int i=0;i<dlen;i++)
			{
				byte[] fbs = doubleToByte(bs[i]) ;
				short t ;
				t = (short)(0xFF & (short)fbs[0]) ;
				r[i*2+1] = (short)(t<<8) ;
				r[i*2+1] |= (short)(0xFF & (short)fbs[1]) ;
				
				r[i*2] = (short)((0xFF & (short)fbs[2])<<8) ;
				r[i*2] |= (short)(0xFF & (short)fbs[3]) ;
				
				r[i*2+3] = (short)((0xFF & (short)fbs[4])<<8) ;
				r[i*2+3] |= (short)(0xFF & (short)fbs[5]) ;
				
				r[i*2+2] = (short)((0xFF & (short)fbs[6])<<8) ;
				r[i*2+2] |= (short)(0xFF & (short)fbs[7]) ;
			}
			
			usingDatas = r ;
		}
		
		public short[] getInt16UsingDatas()
		{
			return usingDatas ;
		}
		
		public byte[] toModbusBytes()
		{
			return null ;
		}
	}
	
	/**
	 * �豸id
	 */
	short devAddr = 0 ;
	
	int regIdx = 0 ;
	
	int regNum = 0 ;
	
	long intervalMS = 3000 ;
	
	XmlVal.XmlValType valType = null ;
	
	/**
	 * ProviderΪ���ṩ�������ʵ�ַ������Ҫ����ʼ��ַ
	 */
	int acqIdx = 0 ;
	
	/**
	 * slave�ڴ��е�����
	 */
	transient SlaveData slaveData = null ;
	
	transient long lastActDt= -1 ;
	/**
	 * ��ʼ��Provider�������ļ���Ӧ��eleҲ�ṩ
	 * �����������������ļ��ڵ��У�����һЩ��Provider����Ҫ���������
	 * 
	 * �̳������أ��˷�������
	 * @param ele
	 */
	protected void init(Element ele)
	{
		devAddr = Convert.parseToInt16(ele.getAttribute("dev"), (short)0) ;
		regIdx = Convert.parseToInt32(ele.getAttribute("reg_idx"), 0) ;
		regNum = Convert.parseToInt32(ele.getAttribute("reg_num"), 0) ;
		
		acqIdx = Convert.parseToInt32(ele.getAttribute("acq_idx"), 0) ;
		
		intervalMS = Convert.parseToInt64(ele.getAttribute("acquire_interval"), 3000) ;
		String strt = ele.getAttribute("type") ;
		valType = XmlVal.StrType2ValType(strt) ;
		if(valType==null)
			throw new IllegalArgumentException("no type in MSlaveDataProvider") ;
	}
	
	public short getDevAddr()
	{
		return devAddr ;
	}
	
	public int getRegIdx()
	{
		return regIdx ;
	}
	
	public int getRegNum()
	{
		return regNum ;
	}
	
	public XmlVal.XmlValType getDataType()
	{
		return valType ;
	}
	
	/**
	 * �������Provider
	 * @return
	 */
	public int getAcqIdx()
	{
		return acqIdx ;
	}
	
	/**
	 * 
	 * @return
	 */
	public SlaveData getSlaveData()
	{
		return slaveData ;
	}
	
	
	//public 
	
	
	void pulseAcquireData()
	{
		long ct = System.currentTimeMillis() ;
		if(ct-lastActDt<intervalMS)
			return ;
		
		SlaveData sd = acquireData(regIdx,regNum) ;
		sd.updateUsingData(regIdx, regNum, acqIdx);
		
		//
		slaveData = sd ;
		lastActDt = ct ;
	}
	/**
	 * �ɹ����ͳһ����
	 * 
	 * �̳���ʵ�ִ˷�������ȡ��Ҫ���µ�����
	 * ��Ӧmodbus slave�����ṩ�߶��ԣ��˷����ǹؼ����ݡ�
	 * 1��Ҫע��˷����ᱻ��ʱ���У����е�ʱ����ͨ�������ļ�����
	 * 2��Ҫע���һ�����кͺ������еĲ��죬����������������Ч�ʣ����ٲ���Ҫ�����ݷ���
	 * 3
	 */
	protected abstract SlaveData acquireData(int idx,int num) ;
}


class TestBitProvider extends MSlaveDataProvider
{
	public XmlVal.XmlValType getDataType()
	{
		return XmlVal.XmlValType.vt_bool ;
	}
	
	
	@Override
	protected SlaveData acquireData(int idx,int num)
	{
		BoolDatas bds = new BoolDatas() ;
		bds.datas = new boolean[num] ;
		boolean bv = false;
		bv = System.currentTimeMillis()%2==0 ;
		for(int i=0;i<num;i++)
			bds.datas[i] = bv ;
		
		return bds;
	}
}


class TestWordProvider extends MSlaveDataProvider
{
	public XmlVal.XmlValType getDataType()
	{
		return XmlVal.XmlValType.vt_int16 ;
	}
	
	
	@Override
	protected SlaveData acquireData(int idx,int num)
	{
		Int16Datas bds = new Int16Datas() ;
		bds.datas = new short[num] ;
		boolean bv = false;
		//bv = System.currentTimeMillis()%num ;
		for(int i=0;i<num;i++)
			bds.datas[i] = (short)(System.currentTimeMillis()%50000) ;
		
		return bds;
	}
	
}

class TestDoubleProvider extends MSlaveDataProvider
{
	public XmlVal.XmlValType getDataType()
	{
		return XmlVal.XmlValType.vt_double ;
	}
	
	
	@Override
	protected SlaveData acquireData(int idx,int num)
	{
		DoubleDatas bds = new DoubleDatas() ;
		bds.datas = new double[num] ;
		boolean bv = false;
		//bv = System.currentTimeMillis()%num ;
		for(int i=0;i<num;i++)
			bds.datas[i] = (double)i+0.1;
		
		return bds;
	}
}