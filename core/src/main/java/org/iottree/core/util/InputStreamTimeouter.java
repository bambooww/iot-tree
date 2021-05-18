package org.iottree.core.util;


import java.io.*;

/**
 * 根据一个输入的Inputstream，根据过期时间读取数据。 其中，里面主函数会被外界的线程快速调用。使得外界一个线程就可以支持很多个这种读取对象
 * 以满足一些数据处理的需要。
 * 
 * @author zzj
 *
 */
public class InputStreamTimeouter
{
	InputStream inputs = null;

	long timeoutMS = 1000;

	int bufLen = 1024;

	private transient int rst = 0;
	private transient ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private transient byte[] readBuf = null;

	private transient long lastDT = -1;

	public InputStreamTimeouter(InputStream inputs, long timeout_ms, int buflen)
	{
		this.inputs = inputs;
		this.timeoutMS = timeout_ms;
		if (timeout_ms <= 0)
			this.timeoutMS = 1000;// 缺省一秒钟
		this.bufLen = buflen;
		if (buflen <= 0)
			this.bufLen = 1024;

		readBuf = new byte[bufLen];
	}

	/**
	 * 被外界线程调用的函数，此函数每次调用间隔，最好不要超过timout_ms 因为超了，可能会引起先后两个数据项混合错误。
	 * 
	 * @return
	 */
	public byte[] readNext() throws Exception
	{
		int len;
		switch (rst)
		{
		case 0:// no data
			len = inputs.available();
			if (len <= 0)
				return null;// no and do nothing

			if (len > bufLen)
				len = bufLen;
			inputs.read(readBuf, 0, len);
			baos.write(readBuf, 0, len);
			lastDT = System.currentTimeMillis();
			rst = 1;
			break;
		case 1:// in reading
			len = inputs.available();
			if (len <= 0)
			{
				// chk time out
				if (System.currentTimeMillis() - lastDT >= timeoutMS)
				{
					byte[] rets = baos.toByteArray();
					rst = 0;
					baos.reset();
					return rets;
				}
				return null;// no and do nothing
			}

			if (len > bufLen)
				len = bufLen;
			inputs.read(readBuf, 0, len);
			baos.write(readBuf, 0, len);
			lastDT = System.currentTimeMillis();
			return null;
		}

		return null;
	}
}
