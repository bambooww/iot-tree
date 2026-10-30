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
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import gnu.io.CommPortIdentifier;

public class ConnPtCOM extends ConnPtStream
{
	public static String TP = "com";

	public static int[] BAUDS = new int[] { 110, 300, 600, 1200, 2400, 4800, 9600, 19200, 28800, 38400, 56000, 57600,
			115200, 128000,187500, 230400, 256000, 460800, 500000, 512000, 600000, 750000, 921600, 1000000, 1500000, 2000000 };

	public static int[] DATABITS = new int[] { 5, 6, 7, 8 };

	public static int[] STOPBITS = new int[] { 1, 2 };

	public static int[] PARITY = new int[] { 0, 1, 2 };
	public static String[] PARITY_TITLE = new String[] { "None", "Odd", "Even" };
	public static String[] PARITY_NAME = new String[] { "none", "odd", "even" };
	
	public final static int FLOWCTL_NONE = 0 ;
	public final static int FLOWCTL_RTS_CTS = 1 ;
	public final static int FLOWCTL_DTR_DSR = 2 ;
	public final static int FLOWCTL_XON_XOFF = 3 ;

	public static int[] FLOWCTL = new int[] { 0, 1, 2, 3 };
	public static String[] FLOWCTL_TITLE = new String[] { "None", "RTS-CTS", "DTR-DSR", "XON-XOFF"};

	String comId = "";

	int baud = 9600;

	int dataBits = 8;// 5 6 7 8

	int parity = 0; // 0 - None 1=Odd 2=Even

	int stopBits = 1; // 1 2

	int flowCtl = 0; // 0=None 1=DTR 2=RTS 3=RTS DTR 4 = RTS Always ;
	
	/**
	 * do RTS or DTR High/Low/Do nothing after connection
	 */
	int ctrlRTS =0 ; //-1 clear 0- do nothing 1 -set
	int ctrlDTR = 0 ; //-1 clear 0- do nothing 1 -set

	InputStream inputS = null;

	OutputStream outputS = null;

	gnu.io.SerialPort serialPortRxTx = null;

	SerialPort serialPort = null;
	
	private transient long connDT = -1 ;

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
		xd.setParamValue("ctrl_dtr", this.ctrlDTR);
		xd.setParamValue("ctrl_rts", this.ctrlRTS);
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
		this.ctrlDTR= xd.getParamValueInt32("ctrl_dtr", 0); // clear
		this.ctrlRTS= xd.getParamValueInt32("ctrl_rts", 0); //clear
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
		
		this.ctrlDTR = jo.optInt("ctrl_dtr",0);//clear
		this.ctrlRTS = jo.optInt("ctrl_rts",0); //clear
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
	
	/**
	 * ctrl rts low / high /none after connection
	 * @return
	 */
	public int getCtrlRTSLowHigh()
	{
		return this.ctrlRTS;
	}
	
	/**
	 * ctrl dtr low / high /none after connection
	 * @return
	 */
	public int getCtrlDTRLowHigh()
	{
		return this.ctrlDTR;
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
			//serialPort.setBaudRate(9600);

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
			case 1: // RTS-CTS
				flow_ctl = SerialPort.FLOW_CONTROL_RTS_ENABLED | SerialPort.FLOW_CONTROL_CTS_ENABLED;
				break;
			case 2: // DTR-DSR old merdom
				flow_ctl = SerialPort.FLOW_CONTROL_DTR_ENABLED | SerialPort.FLOW_CONTROL_DSR_ENABLED;
				break;
			case 3: // XON-XOFF
				flow_ctl = SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED | SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
				break;
			}
			serialPort.setFlowControl(flow_ctl);

			//serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

			String osName = System.getProperty("os.name").toLowerCase();
	        boolean isWindows = osName.contains("win");

	        // add listener to disconnected
	        serialPort.addDataListener(new SerialPortDataListener() {
	            @Override
	            public int getListeningEvents()
	            {
	                // only lis disconn event
	                return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED|SerialPort.LISTENING_EVENT_CARRIER_DETECT ;
	            }

	            @Override
	            public void serialEvent(SerialPortEvent event)
	            {
	            	int evt = event.getEventType() ;
	                if (evt == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED ||
	                		evt == SerialPort.LISTENING_EVENT_CARRIER_DETECT)
	                {
	                    // find disconn ,release port
	                    serialPort.closePort();
	                    serialPort = null;
	                }
	            }
	        });
	        
			// serialPort.enableReceiveTimeout(5000);

			inputS = serialPort.getInputStream();
			outputS = serialPort.getOutputStream();
			
			serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 5000, 5000);
			
			if(serialPort.openPort())
			{
	            //Thread.sleep(20);
				if(this.ctrlRTS<0)
					serialPort.clearRTS();
				else if(this.ctrlRTS>0)
					serialPort.setRTS();
				
				if(this.ctrlDTR<0)
					serialPort.clearDTR();
				else if(this.ctrlDTR>0)
					serialPort.setDTR() ;
	            
	            //System.out.println("clear_rts="+b_clear_rts+" clear_dtr="+b_clear_dtr);
	            
				this.fireConnReady();
				return true;
			}
			else
			{
				disconnect();
				return false;
			}
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

	public synchronized void disconnect() // throws IOException
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
		if(!this.canRun())
			return ;
		
		if (System.currentTimeMillis() - lastChk < 5000)
			return;

		try
		{
			//System.out.println("com chk conn") ;
			if(connect())
				this.connDT = System.currentTimeMillis() ;
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
		if (ConnProCOM.usingRxTx())
			return serialPortRxTx ==null;
		else
			return serialPort ==null ;
	}

	@Override
	public boolean isConnReady()
	{
		if (ConnProCOM.usingRxTx())
			return serialPortRxTx != null;
		else
			return serialPort!=null ;
	}
	

	@Override
	public long getConnDT()
	{
		return this.connDT;
	}

	public String getConnErrInfo()
	{
		if (!isConnReady())
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
