package org.iottree.driver.s7.eth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.iottree.core.conn.ConnPtTcpClient;


/**
 * 
 * @author jason.zhu
 *
 */
public final class S7TcpConn
{
	private static final int ISO_TCP_PORT = 102;
	/**
	 * conn pt
	 */
	private ConnPtTcpClient cpTcp;

	private S7LinkTp linkTp = S7LinkTp.PG;

	byte tsapLocalHI;
	byte tsapLocalLO;
	byte tsapRemoteHI;
	byte tsapRemoteLO;

	private long recvTO = 3000;

	private transient DataInputStream inputS = null;
	private transient DataOutputStream outputS = null;
	
	final byte[] PDU = new byte[2048];
	
	int pduLen = 0;
	
	byte pduType = -1;

	

	public S7TcpConn(ConnPtTcpClient tcpc) // throws Exception
	{
		this.cpTcp = tcpc;

		inputS = new DataInputStream(this.cpTcp.getInputStream());
		outputS = new DataOutputStream(this.cpTcp.getOutputStream());
	}

	public S7TcpConn withLinkTp(S7LinkTp ltp)
	{
		this.linkTp = ltp;
		return this;
	}

	/**
	 * set ISO TSAP params
	 * 
	 * @return
	 */
	public S7TcpConn withTSAP(int loc_tsap, int remote_tsap)
	{
		int loc = loc_tsap & 0x0000FFFF;
		int rrr = remote_tsap & 0x0000FFFF;

		tsapLocalHI = (byte) (loc >> 8);
		tsapLocalLO = (byte) (loc & 0x00FF);
		tsapRemoteHI = (byte) (rrr >> 8);
		tsapRemoteLO = (byte) (rrr & 0x00FF);

		return this;
	}

	public S7TcpConn withRackSlot(int rack, int slot)
	{
		int rtsap = (linkTp.getVal() << 8) + (rack * 0x20) + slot;
		return withTSAP(0x0100, rtsap);
	}

	public S7TcpConn withTimeout(long recv_to)
	{
		recvTO = recv_to;
		return this;
	}

	public ConnPtTcpClient getConnPt()
	{
		return this.cpTcp;
	}

	void recv(byte[] buf, int Start, int Size) throws IOException
	{
		checkStreamLenTimeout(Size, recvTO);
		inputS.read(buf, Start, Size);
	}

	void clearInputStream(long timeout) //throws IOException
	{
		try
		{
			int lastav = inputS.available();
			long lastt = System.currentTimeMillis();
			long curt = lastt;
			while ((curt = System.currentTimeMillis()) - lastt < timeout)
			{
				try
				{
					Thread.sleep(1);
				}
				catch ( Exception e)
				{
				}
	
				int curav = inputS.available();
				if (curav != lastav)
				{
					lastt = curt;
					lastav = curav;
					continue;
				}
			}
	
			if (lastav > 0)
				inputS.skip(lastav);
		}
		catch(IOException e)
		{
			if(S7Msg.log.isDebugEnabled())
				S7Msg.log.error("",e);
			//e.printStackTrace();
		}
	}

	private void checkStreamLenTimeout(int len, long timeout) throws IOException
	{
		long lastt = System.currentTimeMillis();
		int lastlen = inputS.available();
		long curt;
		while ((curt = System.currentTimeMillis()) - lastt < timeout)
		{
			int curlen = inputS.available();
			if (curlen >= len)
				return;

			if (curlen > lastlen)
			{
				lastlen = curlen;
				lastt = curt;
				continue;
			}

			try
			{
				Thread.sleep(1);
			}
			catch ( Exception ee)
			{
			}

			continue;
		}
		throw new IOException("time out");
	}

	void send(byte[] buf, int Len) throws IOException
	{
		outputS.write(buf, 0, Len);
		outputS.flush();
	}

	void send(byte[] buf) throws IOException
	{
		send(buf, buf.length);
	}

	public void close()
	{
		try
		{
			this.cpTcp.close();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		this.close();
	}

	
}
