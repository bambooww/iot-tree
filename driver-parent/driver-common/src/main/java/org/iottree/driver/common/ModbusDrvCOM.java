package org.iottree.driver.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevDriver;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;

import gnu.io.*;


public class ModbusDrvCOM extends ModbusDrv
{

	@Override
	public String getName()
	{
		return "modbus_com";
	}

	@Override
	public String getTitle()
	{
		return "Modbus COM";
	}
	
	public DevDriver copyMe()
	{
		return new ModbusDrvCOM() ;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh()
	{
		PropGroup r = new PropGroup("comm","Communications");
		
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
	
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		pgs.add(r);
		return pgs ;
	}

	
	ComEndPoint comEP = null ;
	
	protected boolean RT_initDriver(StringBuilder failedr)
	{
		try
		{
			comEP = openCom(failedr) ;
			return comEP!=null;
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage()) ;
			e.printStackTrace();
			return false;
		}
		
	}
	
	ComEndPoint openCom(StringBuilder failedr) throws Exception
	{
		SerialPort sp = null;
		long comid = this.getPropValInt("comm", "comid", 1) ;
		long baud =  this.getPropValInt("comm", "baud", 9600) ;
		int databits = (int)this.getPropValInt("comm", "databits", 8) ;
		//int dtbits = SerialPort.DATABITS_8;
		
		//0-none 1-odd 2-even
		long parity = this.getPropValInt("comm", "parity", 0) ;
		int parityv = SerialPort.PARITY_NONE ;
		if(parity==1)
			parityv = SerialPort.PARITY_ODD ;
		else if(parity==2)
			parityv = SerialPort.PARITY_EVEN ;
		// 1 2
		int stopbits = (int)this.getPropValInt("comm", "stopbits", 1) ;
		//"None","DTR","RTS","RTS DTR","RTS Always"
		long flowctl = this.getPropValInt("comm", "flowctl", 0) ;
		int flowctlv = SerialPort.FLOWCONTROL_NONE;
//		switch(flowctl)
//		{
//		case 1:
//			flowctlv = SerialPort.fl
//		}
		// serP.s
		CommPortIdentifier compid = CommPortIdentifier
				.getPortIdentifier("COM"+comid);
		if (compid == null)
		{
			failedr.append("port COM"+comid+" identifier not found") ;
			return null;
		}

		sp = (SerialPort) compid.open("tbs_ais", 5000);

		sp.notifyOnDataAvailable(true);
		sp.setSerialPortParams((int)baud, databits,stopbits, parityv);
		sp.setFlowControlMode(flowctlv); //TODO

		sp.enableReceiveTimeout(5000);
		// serialPort.setInputBufferSize(5);
		
		return new ComEndPoint(sp) ;
	}
	

	protected void RT_endDriver() throws Exception
	{
		if(comEP!=null)
		{
			comEP.close();
			comEP = null ;
		}
	}

	
	/**
	 * 
	 * @return
	 */
	public boolean isDriverSupportConn()
	{
		return true;
	}
	
	public IConnEndPoint getConnEndPoint()
	{
		return comEP;
	}

	protected void RT_runInLoop() throws Exception
	{
		
	}
	
	
	static class ComEndPoint implements IConnEndPoint
	{
		SerialPort serialPort = null;

		InputStream serInputs = null;
		
		OutputStream serOutputs = null;
		
		public ComEndPoint(SerialPort sp) throws IOException
		{
			serialPort = sp ;
			serInputs = serialPort.getInputStream();
			serOutputs = serialPort.getOutputStream();
		}
		
		@Override
		public void close() throws IOException
		{
			try
			{
				if (serInputs != null)
				{
					try
					{
						serInputs.close();
					}
					catch (Exception ee)
					{
					}
				}

				if (serOutputs != null)
				{
					try
					{
						serOutputs.close();
					}
					catch (Exception ee)
					{
					}
				}

				if (serialPort != null)
				{
					serialPort.close();
				}
			}
			finally
			{
				serialPort = null;
			}
		}

		@Override
		public InputStream getInputStream()
		{
			return serInputs;
		}

		@Override
		public OutputStream getOutputStream()
		{
			return serOutputs;
		}
		
	}

	@Override
	public boolean supportDevFinder()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public List<PropGroup> getPropGroupsForDev()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean RT_runInLoop(StringBuilder failedr) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}
}
