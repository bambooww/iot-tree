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
import org.iottree.core.UATag;
import org.iottree.core.DevDriver.Model;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.ValChker;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtTcpClient;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;


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
	
	private final static String M_S7_200 = "s7-200";
	private final static String M_S7_300 = "s7-300";
	private final static String M_S7_400 = "s7-400";
	private final static String M_S7_1200 = "s7-1200";
	private final static String M_S7_1500 = "s7-1500";
	
	final private static List<Model> devms = Arrays.asList(
			new Model(M_S7_200,"S7-200") ,
			new Model(M_S7_300,"S7-300") ,
			new Model(M_S7_400,"S7-400") ,
			new Model(M_S7_1200,"S7-1200") ,
			new Model(M_S7_1500,"S7-1500")
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
	
	
	private static int transStr2TSAP(String v,StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(v))
		{
			failedr.append("TSAP cannot be null") ;
			return -1;
		}
		int len = v.length() ;
		if(len!=4)
		{
			failedr.append("TSAP must be hex string with length 4 like 4D57") ;
			return -1;
		}
		
		try
		{
			return Integer.parseInt(v,16) & 0xFFFF ;
		}
		catch(Exception e)
		{
			failedr.append("TSAP must be hex string with length 4 like 4D57") ;
			return -1 ;
		}
		
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		ArrayList<PropGroup> pgs = new ArrayList<>() ;
		
		Lan lan = Lan.getPropLangInPk(this.getClass()) ;
		
		PropGroup gp_nor = new PropGroup("s7_comm_pm",lan);//"S7 Communication Parameters");
		//PropItem pi = new PropItem("link_tp","Link Type","",PValTP.vt_int,false,new String[] {"PC","OP","PG"},new Object[] {false,true},false));
		
		PropItem pi_rack = new PropItem("rack",lan,PValTP.vt_int,false,null,null,0) ; //"CPU Rack",""
		pi_rack.setValChker(new ValChker<Number>() {
			@Override
			public boolean checkVal(Number v, StringBuilder failedr)
			{
				int vi = v.intValue() ;
				if(vi>=0&&vi<=7)
					return true ;
				failedr.append("CPU Rack must between 0-7") ;
				return false;
			}});
		gp_nor.addPropItem(pi_rack);
		PropItem pi_slot = new PropItem("slot",lan,PValTP.vt_int,false,null,null,1) ; //"CPU Slot",""
		pi_slot.setValChker(new ValChker<Number>() {

			@Override
			public boolean checkVal(Number v, StringBuilder failedr)
			{
				int vi = v.intValue() ;
				if(vi>=1&&vi<=255)
					return true ;
				failedr.append("CPU Slot must between 1-31") ;
				return false;
			}});
		gp_nor.addPropItem(pi_slot);
		
		
		ValChker<String> tsapchk = new ValChker<String>() {
			@Override
			public boolean checkVal(String v, StringBuilder failedr)
			{
				int r = transStr2TSAP(v,failedr);
				return r>0;
			}};
		
		PropGroup gp_tsap = new PropGroup("s7_comm_pm",lan);//"S7 Communication Parameters");
		PropItem pi_local_tsap = new PropItem("tsap_local",lan,PValTP.vt_str,false,null,null,"4D57") ; //"Local TSAP (hex)",""
		pi_local_tsap.setValChker(tsapchk);
		gp_tsap.addPropItem(pi_local_tsap);
		PropItem pi_remote_tsap = new PropItem("tsap_remote",lan,PValTP.vt_str,false,null,null,"4D57") ; //"Remote TSAP (hex)",""
		pi_remote_tsap.setValChker(tsapchk);
		gp_tsap.addPropItem(pi_remote_tsap);
		
		switch(d.getDevModel())
		{
		case M_S7_200:
			pgs.add(gp_tsap) ;
			break;
		case M_S7_300:
			pi_slot.setDefaultVal(2);
		case M_S7_400:
		case M_S7_1200:
		case M_S7_1500:
			pgs.add(gp_nor) ;
			break;
		}
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
	
	S7DevItem getDevItem(UADev d)
	{
		return this.dev2item.get(d);
	}

	@Override
	protected void RT_onConnReady(ConnPt cp,UACh ch,UADev dev)
	{
		ConnPtTcpClient tcpc = (ConnPtTcpClient)cp ;
		S7TcpConn conn = new S7TcpConn(tcpc);
		if(M_S7_200.equals(dev.getDevModel()))
		{
			String tsap_l = dev.getOrDefaultPropValueStr("s7_comm_pm", "tsap_local","4D57") ;
			String tsap_r = dev.getOrDefaultPropValueStr("s7_comm_pm", "tsap_remote","4D57") ;
			StringBuilder failedr = new StringBuilder() ;
			int tsap_loc = transStr2TSAP(tsap_l,failedr) ;
			int tsap_rrr = transStr2TSAP(tsap_r,failedr) ;
			conn.withTSAP(tsap_loc, tsap_rrr);
		}
		else
		{
			int rack = dev.getOrDefaultPropValueInt("s7_comm_pm", "rack",0) ;
			int slot = dev.getOrDefaultPropValueInt("s7_comm_pm", "slot",1) ;
			conn.withRackSlot(rack, slot) ;
		}
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
	public boolean RT_writeVal(UACh ch,UADev dev,UATag tag, DevAddr da, Object v)
	{
		S7DevItem mdi = getDevItem(dev) ;
		if(mdi==null)
			return false;
		return mdi.RT_writeVal(da, v) ;
		
	}

	@Override
	public boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags, DevAddr[] da, Object[] v)
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
