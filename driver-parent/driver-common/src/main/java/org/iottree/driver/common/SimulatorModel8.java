package org.iottree.driver.common;

import java.util.HashMap;
import java.util.*;

import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.DevModel;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.MemSeg8;
import org.iottree.core.basic.MemTable;

/**
 * 8bit device model 
 * @author zzj
 *
 */
public class SimulatorModel8 extends DevModel
{
	ArrayList<SimulatorAddr> devAddrs = new ArrayList<>() ;
	
	MemTable<MemSeg8> registerK = new MemTable<>(8,1000) ;
	MemTable<MemSeg8> registerR = new MemTable<>(8,1000) ;
	
	public String getName()
	{
		return "sim_8bits";
	}
	
	public String getTitle()
	{
		return "Simulator8Bits";
	}
	
	public DevModel copyMe()
	{
		return new SimulatorModel8();
	}
	

	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return true;
	}
	

	private Object getVal(MemTable<MemSeg8> mt,DevAddr da)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
			return null ;
		SimulatorAddr maddr = (SimulatorAddr)da ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			return mt.getValBool(maddr.getRegPos(),maddr.getBitPos()) ;
		}
		else if(vt.isNumberVT())
		{
			return mt.getValNumber(vt,maddr.getRegPos()) ;
		}
		return null;
	}
	
	public boolean setVal(MemTable<MemSeg8> mt,DevAddr da,Object v)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
			return false ;
		SimulatorAddr maddr = (SimulatorAddr)da ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			boolean bv = false;
			if(v instanceof Boolean)
				bv = (Boolean)v ;
			else if(v instanceof Number)
				bv = ((Number)v).doubleValue()>0 ;
			else
				return false;
			mt.setValBool(maddr.getRegPos(),maddr.getBitPos(),bv) ;
			return true;
		}
		else if(vt.isNumberVT())
		{
			if(!(v instanceof Number))
				return false;
			mt.setValNumber(vt,maddr.getRegPos(),(Number)v) ;
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean RT_setupModel(List<DevAddr> addrs,StringBuilder failedr)
	{
		if(addrs==null||addrs.size()<=0)
		{
			failedr.append("no device address found!") ;
			return false;
		}
		
		if(addrs!=null)
		{
			for(DevAddr da:addrs)
			{
				devAddrs.add((SimulatorAddr)da) ;
			}
		}
		return true;
	}
	

	@Override
	protected boolean RT_initModel(StringBuilder failedr)
	{
		
		return true;
	}

	@Override
	protected void RT_endModel()
	{
		
	}

	@Override
	protected void RT_runInLoop()
	{
		if(devAddrs==null)
			return ;
		
		for(SimulatorAddr da:devAddrs)
		{
			SimulatorFunc sf = da.getFunc() ;
			if(sf!=null)
			{
				ValTP vt = da.getValTP() ;
				Object v = sf.getValWithRunByRate(vt);
				if(v!=null)
					da.RT_setVal(v);
				continue ;
			}
				
			switch(da.getAddrTp())
			{
			case 'K':
				Object v = getVal(registerK,da) ;
				if(v!=null)
					da.RT_setVal(v);
				break ;
			case 'B':
				v = getVal(registerK,da) ;
				if(v!=null)
					da.RT_setVal(v);
				break ;
			case 'R':
				v = getVal(registerR,da) ;
				if(v!=null)
				{
					ValTP vt = da.getValTP();
					if(vt.isNumberVT())
					{
						if(vt.isNumberFloat())
						{
							double d = ((Number)v).doubleValue() ;
							v = d+0.1 ;
						}
						else
						{
							long d =  ((Number)v).longValue() ;
							v = d + 1 ;
						}
					}
					setVal(registerR,da, v);
					da.RT_setVal(v);
				}
				break ;
			}
		}
	}

	@Override
	public boolean RT_writeVal(DevAddr da, Object v)
	{
		SimulatorAddr simda = (SimulatorAddr)da ;
		switch(simda.getAddrTp())
		{
		case 'K':
			return setVal(registerK,da,v) ;
		case 'B':
			return setVal(registerK,da,v) ;
		case 'R':
			return setVal(registerR,da,v) ;
		}
		return false;
	}

}
