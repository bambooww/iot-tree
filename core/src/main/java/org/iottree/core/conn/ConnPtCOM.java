package org.iottree.core.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.iottree.core.ConnProvider;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

import com.fazecast.jSerialComm.SerialPort;

import gnu.io.CommPortIdentifier;

public class ConnPtCOM extends ConnPtStream
{
	public static String TP = "com";

	public static int[] BAUDS = new int[] { 110, 300, 600, 1200, 2400, 4800, 9600, 19200, 28800, 38400, 56000, 57600,
			115200, 128000, 230400, 256000, 460800, 500000, 512000, 600000, 750000, 921600, 1000000, 1500000, 2000000 };

	public static int[] DATABITS = new int[] { 5, 6, 7, 8 };

	public static int[] STOPBITS = new int[] { 1, 2 };

	public static int[] PARITY = new int[] { 0, 1, 2 };
	public static String[] PARITY_TITLE = new String[] { "None", "Odd", "Even" };
	public static String[] PARITY_NAME = new String[] { "none", "odd", "even" };

	public static int[] FLOWCTL = new int[] { 0, 1, 2, 3, 4 };
	public static String[] FLOWCTL_TITLE = new String[] { "None", "DTR", "RTS", "RTS DTR", "RTS Always" };

	/*
	 * r.addPropItem(new
	 * PropItem("comid","COM ID","Physical port number",PValTP.vt_int,false,null
	 * ,null,1)); r.addPropItem(new PropItem("baud","Baud rate"
	 * ,"Select hardware communication speed(unit: bits / second)",PValTP.vt_int
	 * ,false, new String[]
	 * {"300","600","1200","2400","4800","9600","19200","28800","38400"}, new
	 * Object[] {300,600,1200,2400,4800,9600,19200,28800,38400},19200));
	 * r.addPropItem(new
	 * PropItem("databits","Data bits","",PValTP.vt_int,false,new String[]
	 * {"5","6","7","8"},new Object[] {5,6,7,8},8)); r.addPropItem(new
	 * PropItem("parity","Parity","",PValTP.vt_int,false,new String[]
	 * {"None","Odd","Even"},new Object[] {0,1,2},0)); r.addPropItem(new
	 * PropItem("stopbits","Stop bits","",PValTP.vt_int,false,new String[]
	 * {"1","2"},new Object[] {1,2},1)); r.addPropItem(new
	 * PropItem("flowctl","Flow control","�豸�������������(������·����)",PValTP.
	 * vt_int,false, new String[] {"None","DTR","RTS","RTS DTR","RTS Always"},
	 * new Object[] {0,1,2,3,4},0));
	 */

	String comId = "";

	int baud = 9600;

	int dataBits = 8;// 5 6 7 8

	int parity = 0; // 0 - None 1=Odd 2=Even

	int stopBits = 1; // 1 2

	int flowCtl = 0; // 0=None 1=DTR 2=RTS 3=RTS DTR 4 = RTS Always ;

	InputStream inputS = null;

	OutputStream outputS = null;

	gnu.io.SerialPort serialPortRxTx = null;

	SerialPort serialPort = null;

	public ConnPtCOM()
	{
	}

	public ConnPtCOM(ConnProvider cp, String name, String title, String desc)
	{
		super(cp, name, title, desc);
	}

	public String getConnType()
	{
		return TP;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		xd.setParamValue("comid", comId);
		xd.setParamValue("baud", baud);
		xd.setParamValue("databits", dataBits);
		xd.setParamValue("parity", parity);
		xd.setParamValue("stopbits", stopBits);
		xd.setParamValue("flowctl", flowCtl);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);
		this.comId = xd.getParamValueStr("comid", "");
		this.baud = xd.getParamValueInt32("baud", 9600);
		this.dataBits = xd.getParamValueInt32("databits", 8);
		this.parity = xd.getParamValueInt32("parity", 0);
		this.stopBits = xd.getParamValueInt32("stopbits", 1);
		this.flowCtl = xd.getParamValueInt32("flowctl", 0);
		return r;
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);

		this.comId = jo.getString("comid");
		this.baud = jo.getInt("baud");
		this.dataBits = jo.getInt("databits");
		this.parity = jo.getInt("parity");
		this.stopBits = jo.getInt("stopbits");
		this.flowCtl = jo.getInt("flowctl");
	}

	public String getComId()
	{
		return comId;
	}

	public int getBaud()
	{
		return baud;
	}

	public int getDataBits()
	{
		return dataBits;
	}

	public int getParity()
	{
		return parity;
	}

	public int getStopBits()
	{
		return stopBits;
	}

	public int getFlowCtl()
	{
		return flowCtl;
	}

	@Override
	protected InputStream getInputStreamInner()
	{
		return inputS;
	}

	@Override
	protected OutputStream getOutputStreamInner()
	{
		return outputS;
	}

	public String getStaticTxt()
	{
		return "COM" + this.comId;
	}

	private synchronized boolean connect()
	{
		if (ConnProCOM.usingRxTx())
			return connectRxTx();
		else
			return connectNor();
	}

	private synchronized boolean connectNor()
	{
		if (serialPort != null)
		{
			return true;
		}

		try
		{
			serialPort = SerialPort.getCommPort(comId); // 替换为你的串口
			serialPort.setBaudRate(9600);

			// static final public int ONE_STOP_BIT = 1;
			// static final public int ONE_POINT_FIVE_STOP_BITS = 2;
			// static final public int TWO_STOP_BITS = 3;

			int stopb = SerialPort.ONE_STOP_BIT;
			if (this.stopBits == 2)
				stopb = SerialPort.TWO_STOP_BITS;

			// static final public int NO_PARITY = 0;
			// static final public int ODD_PARITY = 1;
			// static final public int EVEN_PARITY = 2;
			// static final public int MARK_PARITY = 3;
			// static final public int SPACE_PARITY = 4;

			int pari = parity;

			serialPort.setComPortParameters((int) baud, dataBits, stopb, pari);
			// serialPort.notifyOnDataAvailable(true);
			// serialPort.setSerialPortParams(, ,stopBits, parity);

			// public static int[] FLOWCTL = new int[] {0,1,2,3,4};
			// public static String[] FLOWCTL_TITLE = new String[]
			// {"None","DTR","RTS","RTS DTR","RTS Always"};
			//
			// static final public int FLOW_CONTROL_DISABLED = 0x00000000;
			// static final public int FLOW_CONTROL_RTS_ENABLED = 0x00000001;
			// static final public int FLOW_CONTROL_CTS_ENABLED = 0x00000010;
			// static final public int FLOW_CONTROL_DSR_ENABLED = 0x00000100;
			// static final public int FLOW_CONTROL_DTR_ENABLED = 0x00001000;
			// static final public int FLOW_CONTROL_XONXOFF_IN_ENABLED =
			// 0x00010000;
			// static final public int FLOW_CONTROL_XONXOFF_OUT_ENABLED =
			// 0x00100000;

			int flow_ctl = SerialPort.FLOW_CONTROL_DISABLED;
			switch (this.flowCtl)
			{
			case 0:
				flow_ctl = SerialPort.FLOW_CONTROL_DISABLED;
				break;
			case 1:
				flow_ctl = SerialPort.FLOW_CONTROL_DTR_ENABLED;
				break;
			case 2:
				flow_ctl = SerialPort.FLOW_CONTROL_RTS_ENABLED;
				break;
			case 3:
				flow_ctl = SerialPort.FLOW_CONTROL_RTS_ENABLED;
				break;
			}
			serialPort.setFlowControl(flow_ctl);

			// serialPort.enableReceiveTimeout(5000);

			inputS = serialPortRxTx.getInputStream();
			outputS = serialPortRxTx.getOutputStream();
			serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 5000, 5000);
			this.fireConnReady();

			return true;
		}
		catch ( Exception ee)
		{
			// System.out.println("conn to "+this.getStaticTxt()+" err") ;
			disconnect();
			return false;
		}
	}

	private synchronized boolean connectRxTx()
	{
		if (serialPortRxTx != null)
		{
			return true;
		}

		try
		{
			CommPortIdentifier compid = CommPortIdentifier.getPortIdentifier(comId);
			if (compid == null)
			{
				return false;
			}

			serialPortRxTx = (gnu.io.SerialPort) compid.open("cpt_com", 5000);

			serialPortRxTx.notifyOnDataAvailable(true);
			serialPortRxTx.setSerialPortParams((int) baud, dataBits, stopBits, parity);
			serialPortRxTx.setFlowControlMode(flowCtl); // TODO

			serialPortRxTx.enableReceiveTimeout(5000);

			inputS = serialPortRxTx.getInputStream();
			outputS = serialPortRxTx.getOutputStream();

			this.fireConnReady();

			return true;
		}
		catch ( Exception ee)
		{
			// System.out.println("conn to "+this.getStaticTxt()+" err") ;
			disconnect();
			return false;
		}
	}

	synchronized void disconnect() // throws IOException
	{
		try
		{
			try
			{
				if (inputS != null)
					inputS.close();
			}
			catch ( Exception e)
			{
			}

			try
			{
				if (outputS != null)
					outputS.close();
			}
			catch ( Exception e)
			{
			}

			if (ConnProCOM.usingRxTx())
			{
				try
				{
					if (serialPortRxTx != null)
					{
						serialPortRxTx.close();
					}
				}
				catch ( Exception e)
				{
				}
			}
			else
			{
				try
				{
					if (serialPort != null)
					{
						serialPort.closePort();//.close();
					}
				}
				catch ( Exception e)
				{
				}
			}
		}
		finally
		{
			inputS = null;
			outputS = null;
			serialPortRxTx = null;
			serialPort = null ;
		}
	}

	private long lastChk = -1;

	public void RT_checkConn() // throws Exception
	{
		if (System.currentTimeMillis() - lastChk < 5000)
			return;

		try
		{
			connect();
		}
		finally
		{
			lastChk = System.currentTimeMillis();
		}
	}

	public String getDynTxt()
	{
		return "";
	}

	@Override
	public boolean isClosed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnReady()
	{
		return serialPortRxTx != null;
	}

	public String getConnErrInfo()
	{
		if (serialPortRxTx == null)
			return "no connection";
		else
			return null;
	}

	@Override
	public void close() throws IOException
	{
		disconnect();
	}

}
