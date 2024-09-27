package org.iottree.driver.gb.szy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnException;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.driver.common.ModbusDevItem;
import org.iottree.driver.common.modbus.sniffer.SnifferRTUCh;
import org.iottree.core.conn.ConnPtStream;

/**
 * 水资源监测数据传输规约 国家水资源监控能力建设项目标准 SZY206-2016
 * 
 * @author jason.zhu
 *
 */
public class SZY206_2016Driver extends DevDriver implements ILang
{
	SZYListener szyLis = null ;

	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	public DevDriver copyMe()
	{
		return new SZY206_2016Driver();
	}

	@Override
	public String getName()
	{
		return "szy206_2016";
	}

	@Override
	public String getTitle()
	{
		return "SZY206-2016";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class;
	}

	@Override
	public boolean supportDevFinder()
	{
		return false;
	}
	

	static SZYAddr szyAddr = new SZYAddr() ;

	@Override
	public DevAddr getSupportAddr()
	{
		return szyAddr;
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
		return null;
	}
	
	protected  boolean initDriver(StringBuilder failedr) throws Exception
	{
//		Object pv = this.getBelongToCh().getPropValue("modbus_ch", "run_model") ;
//		if(pv!=null&&pv instanceof Number)
//		{
//			bSniffer = ((Number)pv).intValue() == SNIFFER_MODEL;
//		}
//		
//		List<UADev> devs = this.getBelongToCh().getDevs() ;
//		
//		ArrayList<ModbusDevItem> mdis=  new ArrayList<>() ;
//		
//		//create modbus cmds
//		for(UADev dev:devs)
//		{
////			if(dev.getName().equals("enc600"))
////			{
////				System.out.println("enc600") ;
////			}
//			ModbusDevItem mdi = new ModbusDevItem(dev) ;
//			StringBuilder devfr = new StringBuilder() ;
//			if(!mdi.init(devfr))
//				continue ;
//			
//			mdis.add(mdi) ;
//		}
//		
//		modbusDevItems = mdis;
//		if(modbusDevItems.size()<=0)
//		{
//			failedr.append("no modbus cmd inited in driver") ;
//			return false;
//		}
//		
//		if(bSniffer)
//		{
//			snifferCh = new SnifferRTUCh();
//		}
		
		szyLis = new SZYListener() ;
		return true ;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception
	{

	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception
	{

	}

	@Override
	protected boolean RT_runInLoop(UACh ch,UADev dev,StringBuilder failedr) throws Exception
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null)
			return true ;
		//if(true)
		//	throw new Exception("test") ;
		if(!cpt.isConnReady())
			return true ;// do nothing
		
		//if(log.isDebugEnabled())
		//	log.debug("RT_runInLoop conn ready ,run do modbus");
		try
		{
				//System.out.println(" RT_runInLoop in sniffer") ;
				InputStream inputs = cpt.getInputStream();
				int dlen = inputs.available() ;
				if(dlen<=0)
					return true;
				byte[] bs = new byte[dlen] ;
				inputs.read(bs) ;
				szyLis.onRecvedData(bs,(f)->{
					onRecvedFrame(f);
				});
		}
		catch(ConnException se)
		{
			//System.out.println("errdt==="+Convert.toFullYMDHMS(new Date()));
			se.printStackTrace();
			if(log.isDebugEnabled())
				log.debug("RT_runInLoop err", se);
			cpt.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(log.isErrorEnabled())
				log.debug("RT_runInLoop err", e);
		}
		return true;
	}
	
	private void onRecvedFrame(SZYFrame f)
	{
		System.out.println("recv f="+f);
	}

	@Override
	public boolean RT_writeVal(UACh ch, UADev dev, UATag tag, DevAddr da, Object v)
	{
		return false;
	}

	@Override
	public boolean RT_writeVals(UACh ch, UADev dev, UATag[] tags, DevAddr[] da, Object[] v)
	{
		return false;
	}

	// CRC-16-CCITT-FALSE Polynomial: 0x1021
	private static final int POLYNOMIAL = 0x1021;
	private static final int INITIAL_VALUE = 0xFFFF;

	public static int calculateCRC(byte[] data)
	{
		int crc = INITIAL_VALUE;

		for (byte b : data)
		{
			crc ^= (b & 0xFF) << 8;
			for (int i = 0; i < 8; i++)
			{
				if ((crc & 0x8000) != 0)
				{
					crc = (crc << 1) ^ POLYNOMIAL;
				}
				else
				{
					crc <<= 1;
				}
			}
		}

		// Masking to 16 bits
		return crc & 0xFFFF;
	}

	
	/**
	 * 0xE5
	 * 
	 * @author jason.zhu
	 *
	 */
	public class Crc8
	{
		private byte[] table = new byte[256];

		public byte calcCRC(byte[] bytes)
		{
			byte crc = 0;
			if (bytes != null && bytes.length > 0)
			{
				for (byte b : bytes)
				{
					crc = table[crc ^ b];
				}
			}
			return crc;
		}

		public Crc8(byte poly) // 0xE5 ;
		{
			for (int i = 0; i < 256; ++i)
			{
				int temp = i;
				for (int j = 0; j < 8; ++j)
				{
					if ((temp & 0x80) != 0)
					{
						temp = (temp << 1) ^ poly;
					}
					else
					{
						temp <<= 1;
					}
				}
				table[i] = (byte) temp;
			}
		}
	}
}
