package org.iottree.driver.s7.eth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.DevDriver.Model;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.ValChker;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtTcpClient;
import org.iottree.core.util.Convert;
import org.iottree.driver.s7.ppi.PPIDevItem;


public class S7EthDriver extends DevDriver
{
	//S7TcpConn s7conn = null ;
	private HashMap<UADev,S7TcpConn> dev2conn = new HashMap<>() ;
	
	private final S7MsgISOCR msgISOCR =  new S7MsgISOCR();
	
	private HashMap<UADev,S7DevItem> dev2item = new HashMap<>() ;
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return true;
	}

	@Override
	public DevDriver copyMe()
	{
		return new S7EthDriver();
	}

	@Override
	public String getName()
	{
		return "s7_tcpip_eth";
	}

	@Override
	public String getTitle()
	{
		return "Siemens TCP/IP Ethernet";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtTcpClient.class;
	}
	
	
	final private static List<Model> devms = Arrays.asList(
			new Model("s7-200","S7-200") ,
			new Model("s7-300","S7-300") ,
			new Model("s7-400","S7-400") ,
			new Model("s7-1200","S7-1200") ,
			new Model("s7-1500","S7-1500")
			);
	
	@Override
	public List<Model> getDevModels()
	{
		return devms ;
	}

	@Override
	public boolean isConnPtToDev()
	{//every dev has conn and has it's own thread to running
		return true;
	}
	
	@Override
	public boolean supportDevFinder()
	{
		return false;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		PropGroup gp = new PropGroup("s7_comm_pm","S7 Communication Parameters");
		//PropItem pi = new PropItem("link_tp","Link Type","",PValTP.vt_int,false,new String[] {"PC","OP","PG"},new Object[] {false,true},false));
		
		PropItem pi = new PropItem("rack","CPU Rack","",PValTP.vt_int,false,null,null,0) ;
		pi.setValChker(new ValChker<Number>() {
			@Override
			public boolean checkVal(Number v, StringBuilder failedr)
			{
				int vi = v.intValue() ;
				if(vi>=0&&vi<=7)
					return true ;
				failedr.append("CPU Rack must between 0-7") ;
				return false;
			}});
		gp.addPropItem(pi);
		pi = new PropItem("slot","CPU Slot","",PValTP.vt_int,false,null,null,1) ;
		pi.setValChker(new ValChker<Number>() {

			@Override
			public boolean checkVal(Number v, StringBuilder failedr)
			{
				int vi = v.intValue() ;
				if(vi>=1&&vi<=255)
					return true ;
				failedr.append("CPU Slot must between 1-31") ;
				return false;
			}});
		gp.addPropItem(pi);
		pgs.add(gp) ;
		
//		gp = new PropGroup("timing","Timing");
//		//gp.addPropItem(new PropItem("conn_to","Connect Timeout(second)","",PValTP.vt_int,false,null,null,3));
//		//
//		gp.addPropItem(new PropItem("req_to","Request Timeout(millisecond)","",PValTP.vt_int,false,null,null,1000));
//		gp.addPropItem(new PropItem("failed_tryn","Fail after successive times","",PValTP.vt_int,false,null,null,3));
//		gp.addPropItem(new PropItem("recv_to","Receive response timeout(millisecond)","",PValTP.vt_int,false,null,null,200));
//		gp.addPropItem(new PropItem("inter_req","Inter-request millisecond","",PValTP.vt_int,false,null,null,0));
//		pgs.add(gp) ;
		
		return pgs ;
	}
	
	private static S7Addr s7addr = new S7Addr() ;

	@Override
	public DevAddr getSupportAddr()
	{
		return s7addr;
	}
	
	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
		super.initDriver(failedr) ;
		
		List<UADev> devs = this.getBelongToCh().getDevs() ;
		
		HashMap<UADev,S7DevItem> d2i=  new HashMap<>() ;
		
		//create modbus cmds
		for(UADev dev:devs)
		{
			S7DevItem mdi = new S7DevItem(this,dev) ;
			StringBuilder devfr = new StringBuilder() ;
			if(!mdi.init(devfr))
				continue ;
			
			d2i.put(dev,mdi) ;
		}
		
		this.dev2item = d2i;
		if(dev2item.size()<=0)
		{
			failedr.append("no s7 device inited in driver") ;
			return false;
		}

		return true ;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp,UACh ch,UADev dev)
	{
		ConnPtTcpClient tcpc = (ConnPtTcpClient)cp ;
		int rack = dev.getOrDefaultPropValueInt("s7_comm_pm", "rack",0) ;
		int slot = dev.getOrDefaultPropValueInt("s7_comm_pm", "slot",1) ;
		S7TcpConn conn = new S7TcpConn(tcpc).withRackSlot(rack, slot) ;
		try
		{//do iso connection
			msgISOCR.processByConn(conn);
			synchronized(this)
			{
				dev2conn.put(dev, conn);
			}
		}
		catch(Exception exp)
		{
			exp.printStackTrace();
			conn.close();//
		}
	}

	@Override
	protected synchronized void RT_onConnInvalid(ConnPt cp,UACh ch,UADev dev)
	{
		dev2conn.remove(dev);
		try
		{
			ConnPtTcpClient tcpc = (ConnPtTcpClient)cp ;
			tcpc.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected boolean RT_runInLoop(UACh ch,UADev dev,StringBuilder failedr) throws Exception
	{
		S7TcpConn s7conn = null;
		synchronized(this)
		{
			s7conn = dev2conn.get(dev) ;
		}
		
		if(s7conn==null)
			return true ;
		
		S7DevItem ditem = dev2item.get(dev) ;
		if(ditem==null)
			return true ;
		
		ditem.doCmd(s7conn);
	
		
		//byte[] bs = .read(DaveArea.INPUTS, 0, 1, 0);
		
		return true;
	}
	
	private void test(UACh ch,UADev dev,S7TcpConn s7conn) throws S7Exception, IOException
	{
		System.out.println(dev.getName()+" S7 run in loop") ;
		
		byte[] bs = new byte[4] ;
		S7MsgRead.readArea(s7conn, S7MemTp.DB, 200, 0, 4, bs);
		System.out.println("read db data="+Convert.byteArray2HexStr(bs));
		
		bs=  new byte[1] ;
		S7MsgRead.readArea(s7conn, S7MemTp.I,0,1,1,bs) ;
		System.out.println("read IB1 data="+Convert.byteArray2HexStr(bs));
		
		bs=  new byte[1] ;
		S7MsgRead.readArea(s7conn, S7MemTp.Q,0,1,1,bs) ;
		System.out.println("read QB1 data="+Convert.byteArray2HexStr(bs));
	}

	@Override
	public boolean RT_writeVal(UADev dev, DevAddr da, Object v)
	{
		return false;
	}

	@Override
	public boolean RT_writeVals(UADev dev, DevAddr[] da, Object[] v)
	{
		return false;
	}
	
	
//	private static void test1(S7Connector connector)
//	{
//		//connector.read(DaveArea.DI, areaNumber, bytes, offset)
//				//Read from DB100 10 bytes
//				byte[] bs = connector.read(DaveArea.DB, 200, 4, 0);
//
//				S7Serializable cvt = new LongConverter() ;
//				Long data = cvt.extract(Long.class, bs, 0,0) ;
//				System.out.println("read db data="+data);
////				
////				bs[0] = 0x00;
////					
////				//Write to DB100 10 bytes
//				long dt = data+1 ;
//				cvt.insert(dt, bs, 0, 0, 4);
//				connector.write(DaveArea.DB, 200, 0, bs);
//
//				
//	}
//	
//	private static void test2(S7Connector connector)
//	{
//		//connector.read(DaveArea.DI, areaNumber, bytes, offset)
//				//Read from DB100 10 bytes
//				byte[] bs = connector.read(DaveArea.INPUTS, 0, 1, 0);
//
//				//S7Serializable cvt = new LongConverter() ;
//				//Long data = cvt.extract(Long.class, bs, 0,0) ;
//				System.out.println("read input data="+Convert.byteArray2HexStr(bs));
//				
//				bs = connector.read(DaveArea.OUTPUTS, 0, 1, 0);
//
//				//S7Serializable cvt = new LongConverter() ;
//				//Long data = cvt.extract(Long.class, bs, 0,0) ;
//				System.out.println("read out data="+Convert.byteArray2HexStr(bs));
//				
//				bs[0] = (byte)0xFF ;
//				connector.write(DaveArea.OUTPUTS, 0, 0, bs);
//				System.out.println("write output data=FF");
//	}
//	
//	private static void readS7Date() throws IOException
//	{
//		S7Connector connector = S7ConnectorFactory.buildTCPConnector()
//	            .withHost("192.168.18.8")
//	            .withPort(102)
//	            
//	            .withType(1) //optional
//	            .withRack(0) //optional
//	            .withSlot(1) //optional
//	            .build();
//	   
//		try
//		{
//			test1(connector);
//			test2(connector);
//		}
//		finally
//		{
//			//Close connection
//			connector.close();
//		}
//	}
//	
//	
//	public static void main(String[] args) throws Exception
//	{
//		readS7Date();
//	}
}
