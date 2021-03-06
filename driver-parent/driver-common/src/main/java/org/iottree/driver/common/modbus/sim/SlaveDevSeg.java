package org.iottree.driver.common.modbus.sim;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.sim.SimTag;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class SlaveDevSeg
{
	public static abstract class SlaveData
	{
		int startIdx = -1 ;
		
		public final int getStartIdx()
		{
			return this.startIdx ;
		}
		
		public abstract int getDataLen() ;
		
		//public abstract byte[] toModbusBytes();

		
	}

//	public static abstract class SlaveDataWord extends SlaveData
//	{
//		public abstract short[] getInt16Datas();
//	}

	public static class BoolDatas extends SlaveData
	{
		boolean[] usingDatas = null;
		
		public BoolDatas(int len)
		{
			this.usingDatas = new boolean[len] ;
			for(int i = 0 ; i < len ; i ++)
				this.usingDatas[i] = false ;
		}

		public boolean[] getBoolDatas()
		{
			return usingDatas;
		}

//		public void updateUsingData(int regidx, int regnum, int acqidx)
//		{
//			if (datas == null)
//				return;
//
//			if (acqidx >= datas.length)
//				return;
//
//			int dlen = datas.length - acqidx;
//			if (dlen > regnum)
//				dlen = regnum;
//			boolean[] bs = new boolean[dlen];
//			System.arraycopy(datas, acqidx, bs, 0, dlen);
//			usingDatas = bs;
//		}

//		public byte[] toModbusBytes()
//		{
//			return null;
//		}

		@Override
		public int getDataLen()
		{
			if(usingDatas==null)
				return 0 ;
			
			return usingDatas.length;
		}

		/**
		 * provider after regid,before regidx+regnum֮
		 * 
		 * @param regidx
		 * @param regnum
		 * @param acqidx
		 */
		public void setDataBool(int idx, boolean[] bs)
		{
			System.arraycopy(bs, 0, usingDatas, idx, bs.length);
		}
		
		public void getDataBool(int idx,int len,boolean[] buf,int buf_offset)
		{
			System.arraycopy(usingDatas, idx, buf, buf_offset, len);
		}
	}

	public static class Int16Datas extends SlaveData
	{
		short[] usingDatas = null;
		
		public Int16Datas(int len)
		{
			this.usingDatas = new short[len] ;
			for(int i = 0 ; i < len ; i ++)
				this.usingDatas[i] = 0 ;
		}

		public short[] getInt16Datas()
		{
			return usingDatas;
		}

//		public short[] getInt16UsingDatas()
//		{
//			return usingDatas;
//		}

//		public void updateUsingData(int regidx, int regnum, int acqidx)
//		{
//			if (datas == null)
//				return;
//
//			if (acqidx >= datas.length)
//				return;
//
//			int dlen = datas.length - acqidx;
//			if (dlen > regnum)
//				dlen = regnum;
//			short[] bs = new short[dlen];
//			System.arraycopy(datas, acqidx, bs, 0, dlen);
//
//			usingDatas = bs;
//		}

		public byte[] toModbusBytes()
		{
			return null;
		}

		@Override
		public int getDataLen()
		{
			if(usingDatas==null)
				return 0 ;
			
			return usingDatas.length;
		}
	}

//	public static class Int32Datas implements SlaveData, SlaveDataWord
//	{
//		public int[] datas = null;
//
//		short[] usingDatas = null;
//
//		public void updateUsingData(int regidx, int regnum, int acqidx)
//		{
//			if (datas == null)
//				return;
//
//			if (acqidx >= datas.length)
//				return;
//
//			int dlen = datas.length - acqidx;
//			if (dlen > regnum)
//				dlen = regnum;
//			int[] bs = new int[dlen];
//			System.arraycopy(datas, acqidx, bs, 0, dlen);
//
//			short[] r = new short[dlen * 2];
//			for (int i = 0; i < dlen; i++)
//			{
//				r[i * 2] = (short) (0xFFFF & (bs[i] >> 16));
//				r[i * 2 + 1] = (short) (0xFFFF & bs[i]);
//			}
//			usingDatas = r;
//		}
//
//		public short[] getInt16UsingDatas()
//		{
//			return usingDatas;
//		}
//
//		public byte[] toModbusBytes()
//		{
//			return null;
//		}
//	}
//
//	public static class Int64Datas implements SlaveData, SlaveDataWord
//	{
//		public long[] datas = null;
//
//		short[] usingDatas = null;
//
//		public void updateUsingData(int regidx, int regnum, int acqidx)
//		{
//			if (datas == null)
//				return;
//
//			if (acqidx >= datas.length)
//				return;
//
//			int dlen = datas.length - acqidx;
//			if (dlen > regnum)
//				dlen = regnum;
//			long[] bs = new long[dlen];
//			System.arraycopy(datas, acqidx, bs, 0, dlen);
//
//			short[] r = new short[dlen * 4];
//
//			for (int i = 0; i < dlen; i++)
//			{
//				r[i * 2] = (short) (0xFFFF & (bs[i] >> 48));
//				r[i * 2 + 1] = (short) (0xFFFF & (bs[i] >> 32));
//				r[i * 2 + 2] = (short) (0xFFFF & (bs[i] >> 16));
//				r[i * 2 + 3] = (short) (0xFFFF & bs[i]);
//			}
//			usingDatas = r;
//		}
//
//		public short[] getInt16UsingDatas()
//		{
//			return usingDatas;
//		}
//
//		public byte[] toModbusBytes()
//		{
//			return null;
//		}
//	}

	// floatתbyte[]
	static byte[] floatToByte(float v)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		byte[] ret = new byte[4];
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(v);
		bb.get(ret);
		return ret;
	}

	// byte[]תfloat
	static float byteToFloat(byte[] v)
	{
		ByteBuffer bb = ByteBuffer.wrap(v);
		FloatBuffer fb = bb.asFloatBuffer();
		return fb.get();
	}

	// floatתbyte[]
	static byte[] doubleToByte(double v)
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		byte[] ret = new byte[8];
		DoubleBuffer fb = bb.asDoubleBuffer();
		fb.put(v);
		bb.get(ret);
		return ret;
	}

	// byte[]תfloat
	static double byteToDouble(byte[] v)
	{
		ByteBuffer bb = ByteBuffer.wrap(v);
		DoubleBuffer fb = bb.asDoubleBuffer();
		return fb.get();
	}

//	public static class FloatDatas implements SlaveData, SlaveDataWord
//	{
//		public float[] datas = null;
//
//		short[] usingDatas = null;
//
//		public void updateUsingData(int regidx, int regnum, int acqidx)
//		{
//			if (datas == null)
//				return;
//
//			if (acqidx >= datas.length)
//				return;
//
//			int dlen = datas.length - acqidx;
//			if (dlen > regnum)
//				dlen = regnum;
//			float[] bs = new float[dlen];
//			System.arraycopy(datas, acqidx, bs, 0, dlen);
//
//			short[] r = new short[dlen * 2];
//
//			for (int i = 0; i < dlen; i++)
//			{
//				byte[] fbs = floatToByte(bs[i]);
//				short t;
//				t = (short) (0xFF & (short) fbs[0]);
//				r[i * 2 + 1] = (short) (t << 8);
//				r[i * 2 + 1] |= (short) (0xFF & (short) fbs[1]);
//
//				r[i * 2] = (short) ((0xFF & (short) fbs[2]) << 8);
//				r[i * 2] |= (short) (0xFF & (short) fbs[3]);
//			}
//
//			usingDatas = r;
//		}
//
//		public short[] getInt16UsingDatas()
//		{
//			return usingDatas;
//		}
//
//		public byte[] toModbusBytes()
//		{
//			return null;
//		}
//	}
//
//	public static class DoubleDatas implements SlaveData, SlaveDataWord
//	{
//		public double[] datas = null;
//		short[] usingDatas = null;
//
//		public void updateUsingData(int regidx, int regnum, int acqidx)
//		{
//			if (datas == null)
//				return;
//
//			if (acqidx >= datas.length)
//				return;
//
//			int dlen = datas.length - acqidx;
//			if (dlen > regnum)
//				dlen = regnum;
//			double[] bs = new double[dlen];
//			System.arraycopy(datas, acqidx, bs, 0, dlen);
//
//			short[] r = new short[dlen * 4];
//
//			for (int i = 0; i < dlen; i++)
//			{
//				byte[] fbs = doubleToByte(bs[i]);
//				short t;
//				t = (short) (0xFF & (short) fbs[0]);
//				r[i * 2 + 1] = (short) (t << 8);
//				r[i * 2 + 1] |= (short) (0xFF & (short) fbs[1]);
//
//				r[i * 2] = (short) ((0xFF & (short) fbs[2]) << 8);
//				r[i * 2] |= (short) (0xFF & (short) fbs[3]);
//
//				r[i * 2 + 3] = (short) ((0xFF & (short) fbs[4]) << 8);
//				r[i * 2 + 3] |= (short) (0xFF & (short) fbs[5]);
//
//				r[i * 2 + 2] = (short) ((0xFF & (short) fbs[6]) << 8);
//				r[i * 2 + 2] |= (short) (0xFF & (short) fbs[7]);
//			}
//
//			usingDatas = r;
//		}
//
//		public short[] getInt16UsingDatas()
//		{
//			return usingDatas;
//		}
//
//		public byte[] toModbusBytes()
//		{
//			return null;
//		}
//	}
	
	public static final int MAX_SEG_REG_NUM = 1024 ;
	
	private static LinkedHashMap<Integer,String> fc2titles = null ;
	
	public static LinkedHashMap<Integer,String> listFCs()
	{
		if(fc2titles!=null)
			return fc2titles ;
		
		LinkedHashMap<Integer,String> f2t = new LinkedHashMap<>() ;
		f2t.put(1, "Coil Status(R/W Bool)") ;
		f2t.put(2, "Input Status(R Bool)") ;
		f2t.put(3, "Holding Register(R/W Word)") ;
		f2t.put(4, "Input Register(R Word)") ;
		fc2titles = f2t ;
		return f2t ;
	}
	
	public static String getAddressStr(int fc,int reg)
	{
		if(reg<0||reg>65535)
			throw new IllegalArgumentException("reg must be in 0-65535") ;
		switch(fc)
		{
		case 1:
			return String.format("%06d", reg+1);  
		case 2:
			return "1"+String.format("%05d", reg+1);
		case 3:
			return "4"+String.format("%05d", reg+1);
		case 4:
			return "3"+String.format("%05d", reg+1);
		default:
			return null ;
		}
	}
	
	@data_val
	String id = null ;
	
	@data_val
	int fc = 1 ;

	@data_val(param_name = "reg_idx")
	int regIdx = 0;

	/**
	 * &lt;=MAX_SEG_REG_NUM
	 */
	@data_val(param_name = "reg_num")
	int regNum = 0;

	//@data_val(param_name = "int_ms")
	//long intervalMS = 3000;
	
	@data_obj(obj_c = SlaveTag.class)
	List<SlaveTag> tags = new ArrayList<>();
	
	
	private transient boolean[] boolDatas = null ;
	
	private transient short[] int16Datas = null ;

	public SlaveDevSeg()
	{
		this.id = CompressUUID.createNewId() ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public int getFC()
	{
		return fc ;
	}
	
	public String getFCTitle()
	{
		return listFCs().get(this.fc) ;
	}
	
	public int getRegIdx()
	{
		return regIdx ;
	}
	
	public int getRegNum()
	{
		return regNum ;
	}
	
	public String getAddressStr(int reg)
	{
		return getAddressStr(this.fc,reg) ;
	}
	
	public boolean canWriter()
	{
		return fc==1 || fc==3;
	}
	
	public List<SlaveTag> getSlaveTags()
	{
		return this.tags ;
	}
	
	public SlaveTag getSlaveTag(int regidx)
	{
		if(this.tags==null)
			return null ;
		
		for(SlaveTag tag:this.tags)
		{
			if(tag.getRegIdx()==regidx)
				return tag ;
		}
		return null ;
	}
//	public abstract XmlVal.XmlValType getDataType();
//	
//	protected abstract boolean initSlaveData() ;
//	
	
	public SlaveTag setSlaveTag(int idx,String name)
	{
		SlaveTag st = this.getSlaveTag(idx) ;
		if(st==null)
		{
			st = new SlaveTag(name,idx) ;
			this.tags.add(st) ;
		}
		else
		{
			st.asName(name) ;
		}
		return st ;
	}
	
	public SlaveTag removeSlaveTag(int regidx)
	{
		if(this.tags==null)
			return null ;
		
		for(SlaveTag tag:this.tags)
		{
			if(tag.getRegIdx()==regidx)
			{
				tags.remove(tag) ;
				return tag ;
			}
		}
		return null ;
	}
	
	boolean init()
	{
		switch(this.fc)
		{
		case 1:
		case 2:
			boolDatas = new boolean[regNum] ;
			for(int i = 0 ; i < boolDatas.length ; i ++)
				boolDatas[i] = false;
			return true ;
		case 3:
		case 4:
			int16Datas = new short[regNum] ;
			for(int i = 0 ; i < int16Datas.length ; i ++)
				int16Datas[i] = 0;
			return true ;
		default:
			return false;
		}
	}
	
	public boolean isBoolData()
	{
		return this.fc==1 || this.fc==2 ;
	}
	
	boolean RT_init(StringBuilder failedr)
	{
		switch(this.fc)
		{
		case 1:
		case 2:
			boolDatas = new boolean[regNum] ;
			for(int i = 0 ; i < boolDatas.length ; i ++)
				boolDatas[i] = false;
			return true ;
		case 3:
		case 4:
			int16Datas = new short[regNum] ;
			for(int i = 0 ; i < int16Datas.length ; i ++)
				int16Datas[i] = 0;
			return true ;
		default:
			return false;
		}
	}
	
	public String RT_getDataJsonArrStr()
	{
		StringBuilder sb = new StringBuilder() ;
		switch(this.fc)
		{
		case 1:
		case 2:
			sb.append("[") ;
			for(int i = 0 ; i < boolDatas.length ; i ++)
			{
				if(i==0)
					sb.append(boolDatas[i]) ;
				else
					sb.append(",").append(boolDatas[i]) ;
			}
			sb.append("]") ;
			return sb.toString() ;
		case 3:
		case 4:
			sb.append("[") ;
			for(int i = 0 ; i < int16Datas.length ; i ++)
			{
				if(i==0)
					sb.append(int16Datas[i]) ;
				else
					sb.append(",").append(int16Datas[i]) ;
			}
			sb.append("]") ;
			return sb.toString() ;
		default:
			return null;
		}
	}
//	/**
//	 * 
//	 * @return
//	 */
//	public SlaveData getSlaveData()
//	{
//		switch(this.fc)
//		{
//		case 1:
//		case 2:
//		case 3:
//		case 4:
//		default:
//			return null ;
//		}
//	}
	
	
	public boolean[] getSlaveDataBool()
	{
		return boolDatas ;
	}
	
	public void setSlaveDataBool(int reg,boolean v)
	{
		boolDatas[reg] = v ;
	}
	
	public short[] getSlaveDataInt16()
	{
		return int16Datas ;
	}
	
	public void setSlaveDataInt16(int reg,short v)
	{
		int16Datas[reg] = v ;
	}
	
	public void setSlaveDataStr(int reg,String v)
	{
		if(this.isBoolData())
		{
			boolean bv = "true".equals(v) ||"1".equals(v) ;
			setSlaveDataBool(reg,bv) ;
		}
		else
		{
			short tmpv = (short)Convert.parseToInt32(v, (short)0) ;
			setSlaveDataInt16(reg,tmpv) ;
		}
	}

	public Object getSlaveData(int reg)
	{
		if(this.isBoolData())
		{
			return boolDatas[reg] ;
		}
		else
		{
			return this.int16Datas[reg] ;
		}
	}
	
	public void onMasterWriteBools(int idx,boolean[] datas)
	{
		
	}
	

	public void onMasterWriteInt16s(int idx,short[] datas)
	{
		
	}
	
	
//	void pulseAcquireData()
//	{
//		long ct = System.currentTimeMillis() ;
//		if(ct-lastActDt<intervalMS)
//			return ;
//		
//		SlaveData sd = acquireData(regIdx,regNum) ;
//		sd.updateUsingData(regIdx, regNum, acqIdx);
//		
//		//
//		slaveData = sd ;
//		lastActDt = ct ;
//	}
	
	
//	/**
//	 * call by manager
//	 * 
//	 * implements it to acquire updated data
//	 * key method for modbus slave data provider
//	 * 1, it will be called interval,interval gap is configed
//	 * 2, pay attentation first run and flow running,it can be make more effectioned.
//	 * 3
//	 */
//	protected abstract SlaveData acquireData(int idx,int num) ;
//	
//	protected abstract boolean injectData(SlaveData sd) ;
}
