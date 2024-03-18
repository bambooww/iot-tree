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

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class ConnPtCOM extends ConnPtStream
{
	public static String TP = "com" ;
	
	public static int[] BAUDS = new int[] {110,300,600,1200,2400,4800,9600,19200,28800,38400,56000,57600,115200,
			128000,230400,256000,460800,500000,512000,600000,750000,921600,1000000,1500000,2000000};
	
	public static int[] DATABITS = new int[] {5,6,7,8};
	
	public static int[] STOPBITS = new int[] {1,2};
	
	public static int[] PARITY = new int[] {0,1,2};
	public static String[] PARITY_TITLE = new String[] {"None","Odd","Even"};
	public static String[] PARITY_NAME = new String[] {"none","odd","even"};
	
	public static int[] FLOWCTL = new int[] {0,1,2,3,4};
	public static String[] FLOWCTL_TITLE = new String[] {"None","DTR","RTS","RTS DTR","RTS Always"};
	
	/*
	 r.addPropItem(new PropItem("comid","COM ID","Physical port number",PValTP.vt_int,false,null,null,1));
		r.addPropItem(new PropItem("baud","Baud rate","Select hardware communication speed(unit: bits / second)",PValTP.vt_int,false,
				new String[] {"300","600","1200","2400","4800","9600","19200","28800","38400"},
				new Object[] {300,600,1200,2400,4800,9600,19200,28800,38400},19200));
		r.addPropItem(new PropItem("databits","Data bits","",PValTP.vt_int,false,new String[] {"5","6","7","8"},new Object[] {5,6,7,8},8));
		r.addPropItem(new PropItem("parity","Parity","",PValTP.vt_int,false,new String[] {"None","Odd","Even"},new Object[] {0,1,2},0));
		r.addPropItem(new PropItem("stopbits","Stop bits","",PValTP.vt_int,false,new String[] {"1","2"},new Object[] {1,2},1));
		r.addPropItem(new PropItem("flowctl","Flow control","�豸�������������(������·����)",PValTP.vt_int,false,
				new String[] {"None","DTR","RTS","RTS DTR","RTS Always"},
				new Object[] {0,1,2,3,4},0));
	 */
	
	String comId="" ;
	
	int baud = 9600 ;
	
	int dataBits = 8 ;// 5 6 7 8
	
	int parity = 0 ; // 0 - None 1=Odd 2=Even
	
	int stopBits = 1 ; //1 2
	
	int flowCtl = 0 ; // 0=None 1=DTR 2=RTS 3=RTS DTR  4 = RTS Always ;

	InputStream inputS = null;

	OutputStream outputS = null;
	
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
		this.comId = xd.getParamValueStr("comid","");
		this.baud = xd.getParamValueInt32("baud", 9600);
		this.dataBits =  xd.getParamValueInt32("databits", 8);
		this.parity = xd.getParamValueInt32("parity", 0);
		this.stopBits = xd.getParamValueInt32("stopbits", 1);
		this.flowCtl = xd.getParamValueInt32("flowctl", 0);
		return r;
	}
	
	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);
		
		this.comId = jo.getString("comid");
		this.baud = jo.getInt("baud") ;
		this.dataBits = jo.getInt("databits");
		this.parity = jo.getInt("parity") ;
		this.stopBits = jo.getInt("stopbits");
		this.flowCtl = jo.getInt("flowctl") ;
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
		return dataBits ;
	}
	
	public int getParity()
	{
		return parity ;
	}
	
	public int getStopBits()
	{
		return stopBits ;
	}
	
	public int getFlowCtl()
	{
		return flowCtl ;
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
		if (serialPort != null)
		{
			return true;
		}

		try
		{
			CommPortIdentifier compid = CommPortIdentifier
					.getPortIdentifier(comId);
			if (compid == null)
			{
				return false;
			}

			serialPort = (SerialPort) compid.open("tbs_ais", 5000);

			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams((int)baud, dataBits,stopBits, parity);
			serialPort.setFlowControlMode(flowCtl); //TODO

			serialPort.enableReceiveTimeout(5000);
			
			inputS = serialPort.getInputStream();
			outputS = serialPort.getOutputStream();

			this.fireConnReady();

			return true;
		} catch (Exception ee)
		{
			//System.out.println("conn to "+this.getStaticTxt()+" err") ;
			disconnect();
			return false;
		}
	}

	synchronized void disconnect() //throws IOException
	{
		try
		{
			try
			{
				if (inputS != null)
					inputS.close();
			} catch (Exception e)
			{
			}

			try
			{
				if (outputS != null)
					outputS.close();
			} catch (Exception e)
			{
			}

			try
			{
				if (serialPort != null)
				{
					serialPort.close();
				}
			} catch (Exception e)
			{
			}
		} finally
		{
			inputS = null;
			outputS = null;
			serialPort = null;
		}
	}

	private long lastChk = -1;

	public void RT_checkConn() // throws Exception
	{
		if(System.currentTimeMillis()-lastChk<5000)
			return ;
		
		try
		{
			connect();
		}
		finally
		{
			lastChk = System.currentTimeMillis() ;
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
		return serialPort!=null;
	}
	
	public String getConnErrInfo()
	{
		if(serialPort==null)
			return "no connection" ;
		else
			return null ;
	}

	@Override
	public void close() throws IOException
	{
		disconnect();
	}

	
}
