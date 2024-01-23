package org.iottree.driver.common;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.UAVal.ValTP;

/**
 * simulator address
 * 1)it support integer float bool etc values;
 * 2) it can also support some simulator function like random,ramp sine or user defined
 * 
 * @author zzj
 *
 */
public class SimulatorAddr extends DevAddr
{
	private char tp = 'K';
	private int regpos = -1 ;
	private int bitpos = -1 ;
	
	//private ValTP valTP = null ;
	
//	/**
//	 * simulator function
//	 */
//	private String funcName = null ;
//	
//	/**
//	 * simulator function's parameters
//	 */
//	private List<Object> funcParams = null ;
	
	
	private SimulatorFunc simFunc = null ;
	
	SimulatorAddr()
	{}
	
	public SimulatorAddr(String addr,ValTP vtp,SimulatorFunc func)
	{
		super(addr,vtp) ;
//		this.funcName = func ;
//		this.funcParams = params ;
		
		simFunc = func;
	}
	
	public SimulatorAddr(String addr,ValTP vtp,char tp,int regpos)
	{
		super(addr,vtp) ;
		this.tp = tp ;
		this.bitpos = -1 ;
		this.regpos = regpos ;
		//valTP = vtp ;
	}
	
	public SimulatorAddr(String addr,char tp,int regpos,int bitpos)
	{
		super(addr,ValTP.vt_bool) ;
		this.tp = 'B' ;
		//valTP =  ;
		this.regpos = regpos ;
		this.bitpos = bitpos ;
	}
	
	public char getAddrTp()
	{
		return tp ;
	}
	
	public int getRegPos()
	{
		return regpos ;
	}
	
	public int getBitPos()
	{
		return bitpos ;
	}
	
	public SimulatorFunc getFunc()
	{
		return simFunc ;
	}
	
//	public String getFuncName()
//	{
//		return funcName ;
//	}
//	
//	public List<Object> getFuncParams()
//	{
//		return funcParams ;
//	}
	
//	public ValTP getValTP()
//	{
//		return valTP ;
//	}
	
	@Override
	public DevAddr parseAddr(UADev dev,String str,ValTP vtp, StringBuilder failedr)
	{
		DevAddr r = parseSimFuncAddr(str,vtp, failedr);
		if(r!=null)
		{
			return r;
		}
		return parseSimRegAddr(str,vtp, failedr);
	}
	
	/**
	 * simulator function address parser
	 * @param str
	 * @param vtp
	 * @param failedr
	 * @return
	 */
	private DevAddr parseSimFuncAddr(String str,ValTP vtp, StringBuilder failedr)
	{
		String addr = str ;
		str = str.trim().toLowerCase() ;
		if(!str.endsWith(")"))
		{
			failedr.append("simulator function must like funcname(,,)") ;
			return null ;
		}
		int i = str.indexOf('(') ;
		if(i<=0)
		{
			failedr.append("simulator function must like funcname(,,)") ;
			return null ;
		}
		
		String funcn = str.substring(0,i) ;
		String paramstr = str.substring(i+1,str.length()-1) ;
		
		List<String> ss = Convert.splitStrWith(paramstr, ",，") ;
		ArrayList<Object> pobjs = new ArrayList<>() ;
		
		try
		{
			for(String s:ss)
			{
				int fpt = s.indexOf('.') ;
				if(fpt<0)
					pobjs.add(Long.parseLong(s)) ;
				else
					pobjs.add(Double.parseDouble(s)) ;
			}
			
			SimulatorFunc sf = SimulatorFunc.createFunc(funcn, pobjs, failedr);
			if(sf==null)
				return null ;
			if(!sf.checkValTp(vtp))
			{
				failedr.append("") ;
				return null ;
			}
			return new SimulatorAddr(addr,vtp,sf) ;
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage());
			return null;
		}
	}
	
	private DevAddr parseSimRegAddr(String str,ValTP vtp, StringBuilder failedr)
	{
		String addr = str;
		if(Convert.isNullOrEmpty(str))
		{
			failedr.append("address is empty") ;
			return null ;
		}
		str = str.toUpperCase();
		char tp = str.charAt(0) ;
		switch(tp)
		{
		case 'B':
			break ;
		case 'K':
			break ;
		case 'R':
			break ;
		case 'S':
			break ;
		default:
			failedr.append("unknow address type="+tp) ;
			return null ;
		}
		if(!chkValTp(tp,vtp))
		{
			failedr.append("invalid ValTP for this address"+str) ;
			return null ;
		}
		str = str.substring(1);
		int i = str.indexOf('.') ;
		if(i<0)
		{
			int v = Integer.parseInt(str) ;
			return new SimulatorAddr(addr,vtp,tp,v) ;
		}
		else
		{
			int v = Integer.parseInt(str.substring(0,i)) ;
			int bitv = Integer.parseInt(str.substring(i+1)) ;
			return new SimulatorAddr(addr,tp,v,bitv) ;
		}
	}
	

	@Override
	public boolean isSupportGuessAddr()
	{
		
		return false;
	}

	@Override
	public DevAddr guessAddr(UADev dev,String str)
	{
		
		return null;
	}

	@Override
	public List<String> listAddrHelpers()
	{
		
		return null;
	}
	
	static ValTP[] TPS0  =  new ValTP[] {ValTP.vt_bool};
	static ValTP[] TPS1 = new ValTP[] {ValTP.vt_bool,ValTP.vt_bool,ValTP.vt_byte,ValTP.vt_char,ValTP.vt_int16,ValTP.vt_int32,ValTP.vt_int64,ValTP.vt_float,ValTP.vt_double};
	static ValTP[] TPS2 = new ValTP[] {ValTP.vt_str};
	
	static ValTP[] getSupportedValTPs(char tp)
	{
		switch(tp)
		{
		case 'B':
			return TPS0;
		case 'K':
			return TPS1;
		case 'R':
			return TPS1;
		case 'S':
			return TPS2;
		default:
			return null ;
		}
	}
	
	private static boolean chkValTp(char tp,ValTP vt)
	{
		ValTP[] tps = getSupportedValTPs(tp);
		if(tps==null)
			return false;
		for(ValTP vt0:tps)
			if(vt0==vt)
				return true;
		return false;
	}
	
	@Override
	public ValTP[] getSupportValTPs()
	{
		return getSupportedValTPs(this.tp) ;
	}
	
	

	@Override
	public boolean canRead()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		return true;
	}

}
