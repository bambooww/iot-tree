package org.iottree.driver.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.*;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.driver.common.modbus.ModbusBlock;
import org.iottree.driver.common.modbus.ModbusCmd;
import org.iottree.driver.common.modbus.ModbusCmdRead;
import org.iottree.driver.common.modbus.ModbusCmdReadBits;
import org.iottree.driver.common.modbus.ModbusCmdReadWords;
import org.iottree.driver.common.modbus.sniffer.SnifferCmd;

/**
 * every UADev in channel may has this object fit for runtime running
 * @author jason.zhu
 */
public class ModbusDevItem //extends DevModel
{
	private transient List<ModbusAddr> maddrs = new ArrayList<>() ;
	
	ModbusBlock mbCoilIn = null ;
	ModbusBlock mbCoilOut = null ;
	ModbusBlock mbRegIn = null ;
	ModbusBlock mbRegHold = null ;
	
	//private transient ModbusCmd writeCmd = null ;
	
	private transient UADev uaDev = null ;
	private transient DevDef devDef = null ;
	
	/**
	 * failed after num successive will make device to be failed 
	 */
	private int failAfterSuccessive = 3 ;
	
	public ModbusDevItem(UADev dev)
	{
		uaDev = dev ;
		devDef=  dev.getDevDef() ;
	}

	public UADev getUADev()
	{
		return uaDev ;
	}
	/**
	 * 
	 * @param addrs
	 * @param failedr
	 * @return
	 */
	boolean init(StringBuilder failedr)
	{
		List<DevAddr> addrs = uaDev.listTagsAddrAll() ;
		if(addrs==null||addrs.size()<=0)
		{
			failedr.append("no access addresses found") ;
			return false;
		}
		List<ModbusAddr> tmpads = new ArrayList<>() ;
		for(DevAddr d:addrs)
		{
			tmpads.add((ModbusAddr)d) ;
		}
		maddrs= tmpads ;
		
		int devid = (int)uaDev.getOrDefaultPropValueLong("modbus_spk", "mdev_addr", 1);
		// System.out.println("Modbus DevItem devid="+devid) ;
		//int devid = Integer.parseInt(uaDev.getId());
		
		failAfterSuccessive = uaDev.getOrDefaultPropValueInt("timing", "failed_tryn", 3);
		
		int blocksize_out_coils = uaDev.getOrDefaultPropValueInt("block_size", "out_coils", -1);
		if(blocksize_out_coils<0 && devDef!=null)
			blocksize_out_coils = devDef.getOrDefaultPropValueInt("block_size", "out_coils", 32);
		if(blocksize_out_coils<=0)
			blocksize_out_coils=32;
		
		int blocksize_in_coils = uaDev.getOrDefaultPropValueInt("block_size", "in_coils", -1);
		if(blocksize_in_coils<0 && devDef!=null)
			blocksize_in_coils = devDef.getOrDefaultPropValueInt("block_size", "in_coils", 32);
		if(blocksize_in_coils<=0)
			blocksize_in_coils=32;
		
		int blocksize_internal_reg = uaDev.getOrDefaultPropValueInt("block_size", "internal_reg", -1);
		if(blocksize_internal_reg<0 && devDef!=null)
			blocksize_internal_reg = devDef.getOrDefaultPropValueInt("block_size", "internal_reg", 32);
		if(blocksize_internal_reg<=0)
			blocksize_internal_reg=32;
		
		int blocksize_holding = uaDev.getOrDefaultPropValueInt("block_size", "holding", -1);
		if(blocksize_holding<0 && devDef!=null)
			blocksize_holding = devDef.getOrDefaultPropValueInt("block_size", "holding", 32);
		if(blocksize_holding<=0)
			blocksize_holding=32;
		
		
//		if(devDef!=null)
//			blocksize = devDef.getOrDefaultPropValueInt("block_size", "out_coils", 32);//uaDev.getPropValueLong("block_size", "out_coils", 32);
//		if(blocksize<=0)
//			blocksize=32;
		
		long reqto = uaDev.getOrDefaultPropValueLong("timing", "req_to", 100) ;;//devDef.getPropValueLong("timing", "req_to", 1000) ;
		long recvto = uaDev.getOrDefaultPropValueLong("timing", "recv_to", 200) ;
		long inter_ms = uaDev.getOrDefaultPropValueLong("timing", "inter_req", 100) ;
		long scan_intv = uaDev.getOrDefaultPropValueLong("timing", "scan_intv", ModbusCmd.SCAN_INTERVER_DEFAULT) ;
		
		//create modbus cmd and address mapping
		List<ModbusAddr> coil_in_addrs = filterAndSortAddrs(ModbusAddr.COIL_INPUT) ;
		List<ModbusAddr> coil_out_addrs = filterAndSortAddrs(ModbusAddr.COIL_OUTPUT) ;
		List<ModbusAddr> reg_input_addrs = filterAndSortAddrs(ModbusAddr.REG_INPUT) ;
		List<ModbusAddr> reg_hold_addrs = filterAndSortAddrs(ModbusAddr.REG_HOLD) ;
		
		if(coil_in_addrs.size()>0)
		{
			ModbusBlock mb = new ModbusBlock(devid,ModbusAddr.COIL_INPUT,coil_in_addrs,
					blocksize_in_coils,scan_intv,failAfterSuccessive);
			mb.setTimingParam(reqto, recvto, inter_ms);
			if(mb.initReadCmds())
				mbCoilIn = mb;
		}
		if(coil_out_addrs.size()>0)
		{
			ModbusBlock mb = new ModbusBlock(devid,ModbusAddr.COIL_OUTPUT,coil_out_addrs,
					blocksize_out_coils,scan_intv,failAfterSuccessive);
			mb.setTimingParam(reqto, recvto, inter_ms);
			if(mb.initReadCmds())
				mbCoilOut = mb;
		}
		
		if(reg_input_addrs.size()>0)
		{
			boolean fwlow32 = uaDev.getOrDefaultPropValueBool("data_encod", "fw_low32", true);
			
			ModbusBlock mb = new ModbusBlock(devid,ModbusAddr.REG_INPUT,reg_input_addrs,
					blocksize_internal_reg,scan_intv,failAfterSuccessive).asFirstWordLowIn32Bit(fwlow32);
			mb.setTimingParam(reqto, recvto, inter_ms);
			if(mb.initReadCmds())
				mbRegIn = mb;
		}
		if(reg_hold_addrs.size()>0)
		{
			boolean fwlow32 = uaDev.getOrDefaultPropValueBool("data_encod", "fw_low32", true);
			ModbusBlock mb = new ModbusBlock(devid,ModbusAddr.REG_HOLD,reg_hold_addrs,
					blocksize_holding,scan_intv,failAfterSuccessive).asFirstWordLowIn32Bit(fwlow32);
			mb.setTimingParam(reqto, recvto, inter_ms);
			if(mb.initReadCmds())
				mbRegHold = mb;
		}
		
		return true;
	}
	
	public void setModbusProtocal(ModbusCmd.Protocol p)
	{
		if(mbCoilIn!=null)
			mbCoilIn.setModbusProtocal(p);
		
		if(mbCoilOut!=null)
			mbCoilOut.setModbusProtocal(p);
		
		if(mbRegIn!=null)
			mbRegIn.setModbusProtocal(p);
		
		if(mbRegHold!=null)
			mbRegHold.setModbusProtocal(p);
	}
	
	private List<ModbusAddr> filterAndSortAddrs(short addrtp)
	{
		ArrayList<ModbusAddr> r = new ArrayList<>() ;
		for(ModbusAddr ma:this.maddrs)
		{
			if(ma.addrTp==addrtp)
				r.add(ma);
		}
		Collections.sort(r);
		return r ;
	}
	
	private transient int errCount = 0 ;
	
	/**
	 * called by driver, run in loop
	 * @param ep
	 */
	boolean doModbusCmd(ConnPtStream ep)  throws Exception
	{
		boolean ret = true ;
		if(mbCoilIn != null)
		{
			if(!mbCoilIn.checkDemotionCanRun())
				return false;
			if(!mbCoilIn.runCmds(ep))
				ret = false;
		}
		if(mbCoilOut != null)
		{
			if(!mbCoilOut.checkDemotionCanRun())
				return false;
			if(!mbCoilOut.runCmds(ep))
				ret= false;
		}
		if(mbRegIn != null)
		{
			if(!mbRegIn.checkDemotionCanRun())
				return false;
			if(!mbRegIn.runCmds(ep))
				ret=false;
		}
		if(mbRegHold != null)
		{
			if(!mbRegHold.checkDemotionCanRun())
				return false;
			if(!mbRegHold.runCmds(ep))
				ret=false;
		}
		
		if(!ret)
		{
			errCount ++ ;
			if(errCount>=this.failAfterSuccessive)
			{
				errCount = this.failAfterSuccessive ;
				return false;//dev do cmd err
			}
		}
		
		return ret ;
	}
	
	void onSnifferCmd(SnifferCmd sc)
	{
		ModbusCmdRead mcr = sc.getFindedCmd();
		byte[] fdd = sc.getFindedData() ;
		if(mcr instanceof ModbusCmdReadBits)
		{
			if(mbCoilIn != null)
			{
				mbCoilIn.onSnifferCmd(sc);
			}
			if(mbCoilOut != null)
			{
				mbCoilOut.onSnifferCmd(sc);
			}
		}
		else if(mcr instanceof ModbusCmdReadWords)
		{
			if(mbRegIn!=null)
				mbRegIn.onSnifferCmd(sc);
			if(mbRegHold!=null)
				mbRegHold.onSnifferCmd(sc);
		}
	}
	/**
	 * outer find connection is broken,it will call this method
	 * so,it can make related tag to show err
	 */
	void doModbusCmdErr()
	{
		if(mbCoilIn != null)
		{
			mbCoilIn.runCmdsErr();
		}
		if(mbCoilOut != null)
		{
			mbCoilOut.runCmdsErr();
		}
		if(mbRegIn != null)
		{
			mbRegIn.runCmdsErr();
		}
		if(mbRegHold != null)
		{
			mbRegHold.runCmdsErr();
		}
	}

	public boolean RT_writeVal(DevAddr da, Object v)
	{
		ModbusAddr ma = (ModbusAddr)da ;
		ModbusBlock mb = null;
		switch(ma.addrTp)
		{
		case ModbusAddr.COIL_OUTPUT:
			mb = mbCoilOut ;
			break ;
		case ModbusAddr.REG_HOLD:
			mb = mbRegHold ;
			break ;
		default:
			return false;
		}
		if(mb==null)
			return false;
		return mb.setWriteCmdAsyn(ma,v) ;
	}

}
