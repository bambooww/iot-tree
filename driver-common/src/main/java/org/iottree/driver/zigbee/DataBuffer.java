package org.iottree.driver.zigbee;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;

public class DataBuffer
{
	protected byte[] buf = null ;
	
	protected int pos = 0 ;
	
	protected DataBuffer()
	{}
	
	public DataBuffer(int buflen,int init_pos)
	{
		this.buf = new byte[buflen] ;
		this.pos = init_pos ;
	}
	
	public DataBuffer(byte[] bs,int offset,int len)
	{
		this.buf = new byte[len] ;
		System.arraycopy(bs, offset, buf, 0, len);
	}
	
	public static DataBuffer alloc(int len)
	{
		DataBuffer r = new DataBuffer() ;
		r.buf = new byte[len] ;
		return r ;
	}
	
	public byte[] getBuffer()
	{
		return this.buf ;
	}
	

	public int getBufferLength()
	{
        return this.buf.length;
    }

	
	public int getPosition()
	{
        return this.pos;
    }
	
	public void setPosition(int pos)
	{
        this.pos = pos;
    }
	
	public byte[] getWritten()
	{
		byte[] ret = new byte[this.pos] ;
		System.arraycopy(this.buf, 0, ret, 0, this.pos);
        return ret ;
    }
	
	public boolean hasMore()
	{
        return this.pos < this.buf.length;
    }
	
	/**
     * Check if internal buffer has enough bytes to satisfy: (current position + given count).
     * TODO: move to base `Buffalo` class
     * @param count
     * @returns True if has given more bytes
     */
    public boolean isMoreBy(int count)
    {
        return this.pos + count <= this.buf.length;
    }
	
	public int getLeftLen()
	{
		return buf.length - pos ;
	}
	
	public int readUInt8()
	{
		int r = buf[pos] & 0xFF ;
		pos ++ ;
		return r ;
	}
	
	public int readInt8()
	{
		int r = buf[pos] ;
		pos ++ ;
		return r ;
	}
	
	public void writeUInt8(int v)
	{
		buf[pos] = (byte)v ;
		pos ++ ;
	}
	
	public int readUInt16()
	{
		int r = DataUtil.getUInt16(buf, pos) ;
		pos += 2 ;
		return r ;
	}
	
	public void writeUInt16(int v)
	{
		DataUtil.setUInt16(v, buf, pos);
		pos += 2 ;
	}
	
	public long readUInt32()
	{
		long r = DataUtil.getUInt32(buf, pos) ;
		pos += 4 ;
		return r ;
	}
	
	public void writeUInt32(long v)
	{
		DataUtil.setUInt32(v, buf, pos);
		pos += 4 ;
	}
	
	public String readIEEEAddr()
	{
		String r = DataUtil.transIEEEAddr(buf, pos) ;
		pos += 8 ;
		return r ;
	}
	
	public void writeIEEEAddr(String addr)
	{
		DataUtil.transStr2IEEEAddr(addr, buf, pos) ;
		pos += 8 ;
	}
	
	public String readExtendPanId()
	{
		String r = DataUtil.transPanId(buf, pos) ;
		pos += 8 ;
		return r ;
	}
	
	public void writeExtendPanId(String addr)
	{
		DataUtil.transStr2PanId(addr, buf, pos) ;
		pos += 8 ;
	}
	
	public String readEmberKeyData()
	{
		String r = Convert.byteArray2HexStr(this.buf , pos ,16, "-") ;
		pos += 16 ;
		return r ;
	}
	
	public String readSecManKey()
	{
		return readEmberKeyData();
	}
	
	
	
	public void writeEmberKeyData(String sec_key_16)
	{
		byte[] bs = Convert.hexStr2ByteArray(sec_key_16) ;
		if(bs.length!=16)
			throw new IllegalArgumentException("not 16 bytes len") ;
		System.arraycopy(bs, 0, this.buf, pos, 16);
		pos += 16 ;
	}
	
	public void writeSecManKey(String sec_key_16)
	{
		writeEmberKeyData(sec_key_16) ;
	}
	
	public byte[] readBuf(int len)
	{
		byte[] r = new byte[len] ;
		System.arraycopy(buf, pos, r, 0, len);
		pos += len ;
		return r ;
	}
	
	public byte[] readBufferCopy(int len)
	{
		return this.readBuf(len) ;
	}
	
	public void writeBuf(byte[] bs,int offset,int len)
	{
		System.arraycopy(bs, offset, buf, pos, len);
		pos += len ;
	}
	
	 public void writePayload(byte[] payload)
	 {
		 int len = payload!=null?payload.length:0 ;
	     this.writeUInt8(len);
	     if(len>0)
	    	 System.arraycopy(payload, 0, this.buf, pos, len);
	     this.pos += len ;
	 }

    /**
     * Read a uint8_t for payload length, followed by payload buffer (using post-length position).
     * @returns
     */
    public byte[] readPayload()
    {
        int len = this.readUInt8();
        return this.readBuf(len);
    }
	
	public List<Integer> readListUInt8(int len)
	{
		ArrayList<Integer> r = new ArrayList<>(len) ;
		for(int i = 0 ; i < len ; i ++)
		{
			r.add(buf[pos+i] &0xFF) ;
		}
		pos += len ;
		return r ;
	}
	
	public void writeListUInt8(List<Integer> vs)
	{
		int s = vs.size() ;
		for(int i = 0 ; i < s ; i ++)
		{
			int vi = vs.get(i);
			buf[pos+i] = (byte)vi ;
		}
		pos += s ;
	}
	
	public List<Integer> readListUInt16(int len)
	{
		ArrayList<Integer> r = new ArrayList<>(len) ;
		for(int i = 0 ; i < len ; i ++)
		{
			int vi = DataUtil.getUInt16(buf, pos+i*2) ;
			r.add(vi) ;
		}
		pos += len*2 ;
		return r ;
	}
	
	public void writeListUInt16(List<Integer> vs)
	{
		int s = vs.size() ;
		for(int i = 0 ; i < s ; i ++)
		{
			int vi = vs.get(i);
			DataUtil.setUInt16(vi, buf, pos+i*2);
		}
		pos += s*2 ;
	}
	
	public byte[] readRest()
	{
		int leftn = this.getLeftLen() ;
		byte[] ret = new byte[leftn] ;
		System.arraycopy(this.buf, this.pos, ret, 0, leftn);
		return ret ;
	}
	
	public static byte[] concat(DataBuffer buf,byte[] bs)
	{
        int pos = buf.getPosition() ;
        byte[] msg_bs = new byte[pos+bs.length] ;
        System.arraycopy(buf.getBuffer(), 0, msg_bs, 0, pos);
        System.arraycopy(bs, 0, msg_bs, pos, bs.length);
        return msg_bs ;
	}
}
